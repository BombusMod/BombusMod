/*
Copyright (c) 2000,2001 Al Sutton (al@alsutton.com)
All rights reserved.
Redistribution and use in source and binary forms, with or without modification, are permitted
provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this list of conditions
and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice, this list of
conditions and the following disclaimer in the documentation and/or other materials provided with
the distribution.

Neither the name of Al Sutton nor the names of its contributors may be used to endorse or promote
products derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS ``AS IS'' AND ANY EXPRESS OR
IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE
LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF
THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.alsutton.jabber;

import Client.Config;
import Client.Msg;
import Client.StaticData;
import Console.StanzasList;
import com.alsutton.jabber.datablocks.Iq;
import com.alsutton.jabber.datablocks.Message;
import com.alsutton.jabber.datablocks.Presence;
import io.tls.TlsIO;
import locale.SR;
import xml.XMLEventListener;
import xml.XMLException;
import xml.XMLParser;
import xmpp.Account;
import xmpp.XmppError;
import xmpp.extensions.IqPing;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

public class JabberStream implements XMLEventListener, Runnable {

    private final Stack tagStack = new Stack();
    public JabberListener listener;
    private final List<JabberBlockListener> blockListeners = new CopyOnWriteArrayList<>();
    private final Vector outgoingPackets = new Vector();
    private String server; // for ping
    public boolean pingSent;
    public boolean loggedIn;
    private boolean xmppV1;
    private String sessionId;
    private boolean secured;
    public Vector outgoingQueries = new Vector();
    private Socket connection;
    private long stanzasRecv = 0;
    private long stanzasSent = 0;
    private boolean managementSupported;
    private boolean reliable;
    private boolean resumptionAllowed;
    private String reliableSessionId;

    public void connect(String server, String host, int port, String proxy) throws IOException {
        this.server = server;

        boolean waiting = Config.getInstance().istreamWaiting;

        if (proxy == null) {
            connection = new Socket(host, port);
        } else {
            throw new IllegalArgumentException("no proxy supported");
        }
        try {
            connection.setKeepAlive(true);
            connection.setSoLinger(true, 300);
        } catch (Exception e) {
            e.printStackTrace();
        }

        inpStream = connection.getInputStream();
        outStream = connection.getOutputStream();

        length = 0;
        pbyte = 0;
    }

    public void initiateStream() throws IOException {
        StringBuffer header = new StringBuffer("<stream:stream to='").append(server).append("' xmlns='jabber:client' xmlns:stream='http://etherx.jabber.org/streams'");
        header.append(" version='1.0'");
        if (SR.MS_XMLLANG != null) {
            header.append(" xml:lang='").append(SR.MS_XMLLANG).append("'");
        }
        header.append('>');
        send(header.toString());
    }

    public boolean tagStart(String name, Vector attributes) {
        JabberDataBlock in;
        StaticData.getInstance().updateTrafficIn();

        if (name.equals("message")) {
            in = new Message(attributes);
        } else if (name.equals("iq")) {
            in = new Iq(attributes);
        } else if (name.equals("presence")) {
            in = new Presence(attributes);
        } else if (name.equals("xml")) {
            return false;
        } else {
            in = new JabberDataBlock(name, attributes);
        }

        if (name.equals("stream")) {
            sessionId = XMLParser.extractAttribute("id", attributes);
            String version = XMLParser.extractAttribute("version", attributes);
            xmppV1 = ("1.0".equals(version));

            broadcastBeginConversation();
            return false;
        }
        tagStack.push(in);

        return (name.equals("BINVAL"));
    }

    public boolean isXmppV1() {
        return xmppV1;
    }

    public String getSessionId() {
        return sessionId;
    }

    public boolean isSecured() {
        return secured;
    }

    public void tagEnd(String name) {
        if (name.equals("stream")) {
            if (listener != null) {
                listener.connectionTerminated(new XMLException("Normal stream shutdown"));
            }
            return;
        }
        JabberDataBlock in = (JabberDataBlock) tagStack.pop();
        if (tagStack.empty()) {
            dispatchXmppStanza(in);
            if (name.equals("error")) {
                XmppError xe = XmppError.decodeStreamError(in);
                if (listener != null) {
                    listener.connectionTerminated(new XMLException("Stream error: " + xe.toString()));
                }
            }
        } else {
            ((JabberDataBlock) tagStack.peek()).addChild(in);
        }
    }

    protected void dispatchXmppStanza(JabberDataBlock dataBlock) {

        if (dataBlock != null) {
            if (isReliable() && dataBlock.getAttribute("xmlns") == null // jabber:client
                    && !dataBlock.getTagName().equals("features")) {
                incStanzasRecv();
            }
            try {
                if (dataBlock instanceof Iq) {
                    // verify id attribute
                    if (dataBlock.getAttribute("id") == null) {
                        dataBlock.setAttribute("id", "666");
                    }
                    // verify is it our query
                    String type = dataBlock.getTypeAttribute();
                    if (type.equals("result") || type.equals("error")) {
                        String id = dataBlock.getAttribute("id");
                        if (outgoingQueries.indexOf(id) >= 0) {
                            outgoingQueries.removeElement(id);
                        } else {
                            return; // ignore bad iq result/error
                        }
                    }
                }

                int processResult = JabberBlockListener.BLOCK_REJECTED;
                for (JabberBlockListener blockListener : blockListeners) {
                    processResult = blockListener.blockArrived(dataBlock);
                    if (processResult == JabberBlockListener.BLOCK_PROCESSED) {
                        break;
                    }
                    if (processResult == JabberBlockListener.NO_MORE_BLOCKS) {
                        blockListeners.remove(blockListener);
                        break;
                    }
                }

                if (processResult == JabberBlockListener.BLOCK_REJECTED) {
                    if (!(dataBlock instanceof Iq)) {
                        return;
                    }

                    String type = dataBlock.getTypeAttribute();
                    if (type.equals("get") || type.equals("set")) {
                        dataBlock.setAttribute("to", dataBlock.getAttribute("from"));
                        dataBlock.setAttribute("from", null);
                        dataBlock.setTypeAttribute("error");
                        dataBlock.addChild(new XmppError(XmppError.FEATURE_NOT_IMPLEMENTED, null).construct());
                        send(dataBlock);
                    }
                    //TODO: reject iq stansas where type =="get" | "set"
                }
//#ifdef CONSOLE
                addLog(dataBlock.toString(), Msg.MESSAGE_TYPE_IN);
//#endif
            } catch (Exception e) {
                listener.dispatcherException(e, dataBlock);
            }
        }
    }
    private boolean connected = false;

    public void flush() throws IOException {
        while (!outgoingPackets.isEmpty()) {
            sendPacket((String) outgoingPackets.elementAt(0));
            outgoingPackets.removeElementAt(0);
        }
    }

    /**
     * The threads run method. Handles the parsing of incomming data in its
     * own thread.
     */
    public void run() {
        try {
            XMLParser parser = new XMLParser(this);
            connected = true;

            byte cbuf[] = new byte[32768];

            while (connected) {

                flush();

                int length = read(cbuf);
                if (length == 0) {
                    try {
                        Thread.sleep(100);
                    } catch (Exception e) {
                    }
                    continue;
                }
                parser.parse(cbuf, length);
            }

            //dispatcher.broadcastTerminatedConnection( null );
        } catch (Exception e) {
            if (StaticData.Debug) {
                e.printStackTrace();
            }
            if (listener != null) {
                listener.connectionTerminated(e);
            }
        }
        closeConnection();
    }

    private void closeConnection() {
        try {
            send("</stream:stream>");
            //TODO: see FS#528
            try {
                Thread.sleep(500);
            } catch (Exception e) {
            }
            send("</stream:stream>");

            //connection.close();
        } catch (IOException e) {
        }
    }

    /**
     * Method to close the connection to the server and tell the listener
     * that the connection has been terminated.
     */
    public void close() {
        connected = false;
        // wait to finish parser
        try {
            flush();
            Thread.sleep(100L);
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (InterruptedException ex) {
        }
        try {
            outStream.close();
            outStream = null;
        } catch (Exception e) {
        }
        try {
            inpStream.close();
            inpStream = null;
        } catch (Exception e) {
        }
    }

    /**
     * Method of sending data to the server.
     *
     * @param The data to send to the server.
     */
    public void sendKeepAlive() {
        if (pingSent) {
            if (listener != null) {
                listener.connectionTerminated(new Exception("Ping Timeout"));
            }
        } else {
            //System.out.println("Ping myself");
            ping();
        }
    }

    public void send(String data) throws IOException {
        sendPacket(data);
    }
    /**
     * Method of sending a Jabber datablock to the server.
     *
     * @param block The data block to send to the server.
     */
    public void send(JabberDataBlock block) {
        if (block instanceof Iq) {
            String type = block.getTypeAttribute();
            if (type.equals("set") || type.equals("get")) {
                outgoingQueries.addElement(block.getAttribute("id"));
            }
        }
        try {
            StringBuffer buf = new StringBuffer();
            block.constructXML(buf);
            send(buf.toString());
        } catch (Exception e) {
        }
    }

    //#ifdef CONSOLE
    public void addLog(String data, int type) {
        StanzasList.getInstance().add(data, type);
    }
//#endif

    /**
     * Set the listener to this stream.
     */
    public void addBlockListener(JabberBlockListener listener) {
        if (listener == null) {
            return;
        }
        if (blockListeners.indexOf(listener) > 0) {
            return;
        }
        blockListeners.add(listener);

    }

    public void cancelBlockListener(JabberBlockListener listener) {
        try {
            blockListeners.remove(listener);
        } catch (Exception e) {
        }
    }

    public void cancelBlockListenerByClass(Class removeClass) {
        int index = 0;
        int j = blockListeners.size();
        while (index < j) {
            Object list = blockListeners.get(index);
            if (list.getClass().equals(removeClass)) {
                blockListeners.remove(index);
                j--;
            } else {
                index++;
            }
        }

    }

    /**
     * Method to tell the listener the stream is ready for talking to.
     */
    public void broadcastBeginConversation() {
        if (listener != null) {
            listener.beginConversation(StaticData.getInstance().roster);
        }
    }

    private void ping() {
        pingSent = true;
        send(IqPing.query(StaticData.getInstance().account.JID.getServer(), "ping"));
    }

    public void plainTextEncountered(String text) {
        if (!tagStack.isEmpty()) {
            ((JabberDataBlock) tagStack.peek()).setText(text);
        }
    }

    public void binValueEncountered(byte[] binvalue) {
        if (!tagStack.isEmpty()) {
            //currentBlock.addText( text );
            ((JabberDataBlock) tagStack.peek()).addChild(binvalue);
        }
    }

    /**
     * @return the reliable
     */
    public boolean isReliable() {
        return reliable;
    }

    /**
     * @param reliable the reliable to set
     */
    public void setReliable(boolean reliable) {
        this.reliable = reliable;
    }

    /**
     * @return the stanzasRecv
     */
    public long getStanzasRecv() {
        return stanzasRecv;
    }

    /**
     * @param stanzasRecv the stanzasRecv to set
     */
    public void incStanzasRecv() {
        setStanzasRecv(getStanzasRecv() + 1);
    }

    /**
     * @return the stanzasSent
     */
    public long getStanzasSent() {
        return stanzasSent;
    }

    /**
     * @return the managementSupported
     */
    public boolean isManagementSupported() {
        return managementSupported;
    }

    /**
     * @param managementSupported the managementSupported to set
     */
    public void setManagementSupported(boolean managementSupported) {
        this.managementSupported = managementSupported;
    }

    /**
     * @return the resumptionAllowed
     */
    public boolean isResumptionAllowed() {
        return resumptionAllowed;
    }

    /**
     * @param resumptionAllowed the resumptionAllowed to set
     */
    public void setResumptionAllowed(boolean resumptionAllowed) {
        this.resumptionAllowed = resumptionAllowed;
    }

    /**
     * @return the reliableSessionId
     */
    public String getReliableSessionId() {
        return reliableSessionId;
    }

    /**
     * @param reliableSessionId the reliableSessionId to set
     */
    public void setReliableSessionId(String reliableSessionId) {
        this.reliableSessionId = reliableSessionId;
    }

    /**
     * @param stanzasRecv the stanzasRecv to set
     */
    public void setStanzasRecv(long stanzasRecv) {
        this.stanzasRecv = stanzasRecv;
    }

    /**
     * @param stanzasSent the stanzasSent to set
     */
    public void setStanzasSent(long stanzasSent) {
        this.stanzasSent = stanzasSent;
    }

    private class TimerTaskKeepAlive extends TimerTask {

        private Timer t;

        public TimerTaskKeepAlive(int periodSeconds) {
            t = new Timer();
            //this.period=periodSeconds;
            long periodRun = periodSeconds * 1000; // milliseconds
            t.schedule(this, periodRun, periodRun);
        }

        public void run() {

            if (loggedIn) {
                sendKeepAlive();
            }

        }

        public void destroyTask() {
            if (t != null) {
                this.cancel();
                t.cancel();
                t = null;
            }
        }
    }

    private InputStream inpStream;
    private OutputStream outStream;

    private boolean iStreamWaiting;

    private long bytesRecv;
    private long bytesSent;

    public boolean tlsExclusive = false;
    TlsIO tlsHandler;

    public void setTls() throws Exception {
        tlsExclusive = true;
        tlsHandler = TlsIO.create(connection, inpStream, outStream,
                StaticData.getInstance().account.JID.getServer());
        inpStream = tlsHandler.getTlsInputStream();
        outStream = tlsHandler.getTlsOutputStream();
        tlsExclusive = false;
        secured = true;
        length = pbyte = 0;
    }

    public void sendPacket(String data) throws IOException {
        synchronized (outStream) {

            StaticData.getInstance().updateTrafficOut();
            outStream.write(data.getBytes("UTF-8"));
            setSent(bytesSent + data.length());

            outStream.flush();
            updateTraffic();
        }
        System.out.println(">> " + data);
        addLog(data, 1);
    }

    byte cbuf[] = new byte[512];
    int length;
    int pbyte;

    public int read(byte buf[]) throws IOException {
        if (tlsExclusive)
            return 0;
        int avail = inpStream.read(buf, 0, buf.length);
        if (avail > 0)
            System.out.println("<< " + new String(buf, 0, avail, "UTF-8"));
        setRecv(bytesRecv + avail);
        updateTraffic();
        return avail;
    }

    private void updateTraffic() {
        StaticData.getInstance().traffic = getBytes();
    }

    private void setRecv(long bytes) {
        bytesRecv = bytes;
    }

    private void setSent(long bytes) {
        bytesSent = bytes;
    }


    public String getStreamStats() {
        StringBuffer stats = new StringBuffer();
        try {
            long sent = bytesSent;
            long recv = bytesRecv;
            stats.append("\nStream: in=").append(recv).append(" out=").append(sent);
        } catch (Exception e) {
            stats = null;
            return "";
        }
        return stats.toString();
    }

    public long getBytes() {
        try {
            return bytesSent + bytesRecv;
        } catch (Exception e) {
        }
        return 0;
    }

}
