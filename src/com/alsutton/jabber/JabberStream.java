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

import Account.Account;
import Client.Config;
import Client.StaticData;
//#ifdef CONSOLE
//# import Client.Msg;
//# import Console.StanzasList;
//#endif
import com.alsutton.jabber.datablocks.Iq;
//#ifdef HTTPBIND
//# import com.alsutton.jabber.datablocks.Presence;
//# import io.HttpBindConnection;
//#endif
import com.alsutton.jabber.datablocks.Message;
import com.alsutton.jabber.datablocks.Presence;
import io.Utf8IOStream;
import java.io.*;
import java.util.*;
//#if android
//# import java.net.Socket;
//#else
import javax.microedition.io.*;
//#endif
import xml.*;
import locale.SR;
import xmpp.XmppError;
import xmpp.extensions.IqPing;

public class JabberStream implements XMLEventListener, Runnable {

    private final Utf8IOStream iostream;
    private final Stack tagStack = new Stack();
    private JabberListener listener;
    private final Vector blockListeners = new Vector();
    private final Vector outgoingPackets = new Vector();
    private String server; // for ping
    public boolean pingSent;
    public boolean loggedIn;
    private boolean xmppV1;
    private String sessionId;
    public Vector outgoingQueries = new Vector();
//#if android
//#     private Socket connection;
//#else
    private StreamConnection connection;
//#endif

    /**
     * Constructor. Connects to the server and sends the jabber welcome message.
     *
     */
    public JabberStream(String server, String host, int port, String proxy) throws IOException {
        this.server = server;

        boolean waiting = Config.getInstance().istreamWaiting;

        if (proxy == null) {
//#if android
//# 	    connection = new Socket(host, port);
//#else                    
            connection = (StreamConnection) Connector.open(host + ":" + port);
//#endif            
        } else {
//#if HTTPCONNECT
//#             connection = io.HttpProxyConnection.open(hostAddr, proxy, StaticData.getInstance().account.getProxyUser(), StaticData.getInstance().account.getProxyPass());
//#elif HTTPPOLL
//#             connection = new io.HttpPollConnection(server, host);
//#elif HTTPBIND
//#             connection = new io.HttpBindConnection(server, host);
//#else            
            throw new IllegalArgumentException("no proxy supported");
//#endif            
        }

        iostream = new Utf8IOStream(connection);
    }

    public void initiateStream() throws IOException {
//#ifdef HTTPBIND
//#         if (connection instanceof HttpBindConnection) {
//#             JabberDataBlock body = new JabberDataBlock("body", null, null);
//#             body.setAttribute("to", server);
//#             body.setNameSpace("http://jabber.org/protocol/httpbind");
//#             body.setAttribute("xmpp:version", "1.0");
//#             body.setAttribute("ver", "1.6");
//#             body.setAttribute("xmlns:xmpp", "urn:xmpp:xbosh");
//#             body.setAttribute("rid", ((HttpBindConnection)connection).nextRid());
//#             if (((HttpBindConnection)connection).sid == null) // first stream
//#             {
//#                 //body.setAttribute("from", Account.);
//#                 body.setAttribute("wait", "60");
//#                 body.setAttribute("hold", "1");                
//#             }
//#             else // stream restarts
//#             {
//#                 body.setAttribute("sid", ((HttpBindConnection)connection).sid);
//#                 body.setAttribute("xmpp:restart", "true");
//#             }
//#             send(body);            
//#         } else {
//#endif
        StringBuffer header = new StringBuffer("<stream:stream to='").append(server).append("' xmlns='jabber:client' xmlns:stream='http://etherx.jabber.org/streams'");
        header.append(" version='1.0'");
        if (SR.MS_XMLLANG != null) {
            header.append(" xml:lang='").append(SR.MS_XMLLANG).append("'");
        }
        header.append('>');
        send(header.toString());
//#ifdef HTTPBIND
//#         }
//#endif
    }

    public boolean tagStart(String name, Vector attributes) {
//#ifdef HTTPBIND
//#         if (connection instanceof HttpBindConnection) {
//#              if (name.equals("body")) {
//#                  if (((HttpBindConnection) connection).sid == null) {
//#                      xmppV1 = true;
//#                      ((HttpBindConnection) connection).sid = XMLParser.extractAttribute("sid", attributes);
//#                      try {
//#                      ((HttpBindConnection) connection).waitPeriod = Integer.parseInt(XMLParser.extractAttribute("wait", attributes));
//#                      } catch (NumberFormatException e){ // Prosody bug: http://code.google.com/p/lxmppd/issues/detail?id=219
//#                          ((HttpBindConnection) connection).waitPeriod = 30;
//#                      }
//#                  } else {
//#                      // on restart stream
//#                      }
//#                  dispatcher.broadcastBeginConversation();
//#              }
//#          }
//#endif
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

    public void tagEnd(String name) throws XMLException {
//#ifdef HTTPBIND
//#        if (connection instanceof HttpBindConnection) {
//#             if (currentBlock != null) {
//#                 if (currentBlock.isJabberNameSpace("http://jabber.org/protocol/httpbind")) {
//#                     if (loggedIn && ((HttpBindConnection)connection).threadsCount == 0 ) {
//#                         new Thread(keepAlive).start();
//#                     }
//#                     Vector blocks = currentBlock.getChildBlocks();
//#                     if (blocks == null) {
//#                         return;
//#                     }
//#                     for (Enumeration e = blocks.elements(); e.hasMoreElements();) {                                                
//#                         dispatchXmppStanza((JabberDataBlock) e.nextElement());
//#                     }
//#                     sendKeepAlive();                    
//#                 }
//#             }
//#         }
//#endif
        JabberDataBlock in = (JabberDataBlock) tagStack.pop();
        if (tagStack.empty()) {
            dispatchXmppStanza(in);
            if (in.getTagName().equals("stream")) {
                iostream.close();
                throw new XMLException("Normal stream shutdown");
            }
            if (in.getTagName().equals("error")) {
                XmppError xe = XmppError.decodeStreamError(in);
                iostream.close();

                throw new XMLException("Stream error: " + xe.toString());
            }
        } else {
            ((JabberDataBlock) tagStack.peek()).addChild(in);
        }
    }

    protected void dispatchXmppStanza(JabberDataBlock dataBlock) {

        if (dataBlock != null) {
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
                int i = 0;
                int j = blockListeners.size();
                while (i < j) {
                    processResult = ((JabberBlockListener) blockListeners.elementAt(i)).blockArrived(dataBlock);
                    if (processResult == JabberBlockListener.BLOCK_PROCESSED) {
                        break;
                    }
                    if (processResult == JabberBlockListener.NO_MORE_BLOCKS) {
                        j--;
                        blockListeners.removeElementAt(i);
                        break;
                    }
                    i++;
                }
                if (processResult == JabberBlockListener.BLOCK_REJECTED) {
                    if (listener != null) {
                        processResult = listener.blockArrived(dataBlock);
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
//#                 addLog(dataBlock.toString(), Msg.MESSAGE_TYPE_IN);
//#endif
            } catch (Exception e) {
                listener.dispatcherException(e, dataBlock);
            }
        }
    }

    public void startKeepAliveTask() {
        Account account = StaticData.getInstance().account;

        int keepAlivePeriod;
//#ifdef HTTPBIND
//#         if (connection instanceof HttpBindConnection) {
//#             keepAlivePeriod = ((HttpBindConnection)connection).waitPeriod - 3;
//#         } else {
//#endif         
        keepAlivePeriod = account.keepAlivePeriod;
//#ifdef HTTPBIND
//#         }
//#endif
        if (keepAlive != null) {
            keepAlive.destroyTask();
            keepAlive = null;
        }

        keepAlive = new TimerTaskKeepAlive(keepAlivePeriod);
    }
    private boolean connected = false;

    /**
     * The threads run method. Handles the parsing of incomming data in its
     * own thread.
     */
    public void run() {
        try {
            XMLParser parser = new XMLParser(this);
            connected = true;

            byte cbuf[] = new byte[512];

            while (connected) {

                while (!outgoingPackets.isEmpty()) {
                    sendPacket((String) outgoingPackets.elementAt(0));
                    outgoingPackets.removeElementAt(0);
                }

                int length = iostream.read(cbuf);
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
            //e.printStackTrace();
            listener.connectionTerminated(e);
        }
        closeConnection();
    }

    private void closeConnection() {
        if (null != keepAlive) {
            keepAlive.destroyTask();
        }

        try {
//#ifdef HTTPBIND
//#             if (connection instanceof HttpBindConnection) {
//#                 JabberDataBlock body = new JabberDataBlock("body", null, null);
//#                 body.setNameSpace("http://jabber.org/protocol/httpbind");
//#                 body.setAttribute("rid", ((HttpBindConnection) connection).nextRid());
//#                 body.setAttribute("sid", ((HttpBindConnection) connection).sid);
//#                 body.setAttribute("type", "terminate");
//#                 Presence p = new Presence(null, Presence.PRS_OFFLINE);
//#                 p.setNameSpace("jabber:client");
//#                 body.addChild(p);
//#                 send(body);
//#             } else {
//#endif
            send("</stream:stream>");
//#ifdef HTTPBIND
//#             }
//#endif

            setJabberListener(null);
            //TODO: see FS#528
            try {
                Thread.sleep(500);
            } catch (Exception e) {
            }
            send("</stream:stream>");

            //connection.close();
        } catch (IOException e) {
        }
        if (iostream != null) {
            iostream.close();
            /* if (!Config.getInstance().oldNokiaS60)
            iostream = null; // may hang device*/
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
            Thread.sleep(100L);
        } catch (InterruptedException ex) {
        }
    }

    /**
     * Method of sending data to the server.
     *
     * @param The data to send to the server.
     */
    public void sendKeepAlive() {

//#ifdef HTTPBIND
//#         if (connection instanceof HttpBindConnection) {
//#             if (((HttpBindConnection) connection).threadsCount < 2 && loggedIn) {
//#                 try {
//#                     send("");
//#                 } catch (IOException ex) {
//#                     dispatcher.broadcastTerminatedConnection(new Exception("HTTP Exception " + ex.getMessage()));
//#                 }
//#             }
//#         } else {
//#else            
        if (pingSent) {
            listener.connectionTerminated(new Exception("Ping Timeout"));
        } else {
            //System.out.println("Ping myself");
            ping();
        }
//#endif        
//#ifdef HTTPBIND
//#         }
//#endif        
    }

    private void sendPacket(String data) throws IOException {
        iostream.send(data);
//#ifdef CONSOLE
//#         addLog(data, 1);
//#endif
    }

    public void send(String data) throws IOException {
        outgoingPackets.addElement(data);
    }

    private void sendBuf(StringBuffer data) throws IOException {
        outgoingPackets.addElement(data.toString());
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
            sendBuf(buf);
        } catch (Exception e) {
        }
    }

//#ifdef CONSOLE
//#     public void addLog(String data, int type) {
//#         StanzasList.getInstance().add(data, type);
//#     }
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
        blockListeners.addElement(listener);

    }

    public void cancelBlockListener(JabberBlockListener listener) {
        try {
            blockListeners.removeElement(listener);
        } catch (Exception e) {
        }
    }

    public void cancelBlockListenerByClass(Class removeClass) {
        int index = 0;
        int j = blockListeners.size();
        while (index < j) {
            Object list = blockListeners.elementAt(index);
            if (list.getClass().equals(removeClass)) {
                blockListeners.removeElementAt(index);
                j--;
            } else {
                index++;
            }
        }

    }

    public void setJabberListener(JabberListener listener) {
        this.listener = listener;
    }

    /**
     * Method to tell the listener the stream is ready for talking to.
     */
    public void broadcastBeginConversation() {
        if (listener != null) {
            listener.beginConversation();
        }
    }

    private void ping() {
        pingSent = true;
        send(IqPing.query(StaticData.getInstance().account.server, "ping"));
    }

//#if ZLIB
//#     public void setZlibCompression() {
//#         iostream.setStreamCompression();
//#     }
//# 
//#     public String getStreamStats() {
//#         return iostream.getStreamStats();
//#     }
//# 
//#     public String getConnectionData() {
//#         return iostream.getConnectionData();
//#     }
//#endif
//#if TLS
//#     public void setTls() throws IOException {
//#         iostream.setTls();
//#     }
//#endif        

    public long getBytes() {
        return iostream.getBytes();
    }
    private TimerTaskKeepAlive keepAlive;

    public void plainTextEncountered(String text) {
        if (tagStack.peek() != null) {
            ((JabberDataBlock) tagStack.peek()).setText(text);
        }
    }

    public void binValueEncountered(byte[] binvalue) {
        if (tagStack.peek() != null) {
            //currentBlock.addText( text );
            ((JabberDataBlock) tagStack.peek()).addChild(binvalue);
        }
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
}
