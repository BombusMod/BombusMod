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
//# import Console.StanzasList;
//#endif
import com.alsutton.jabber.datablocks.Iq;
import com.alsutton.jabber.datablocks.Presence;
import io.HttpBindConnection;
import io.Utf8IOStream;
import java.io.*;
import java.util.*;
import javax.microedition.io.*;
import xml.*;
import locale.SR;
import xmpp.XmppError;
import xmpp.XmppParser;
import xmpp.extensions.IqPing;

public class JabberStream extends XmppParser implements Runnable {
    
    private Utf8IOStream iostream;

    /**
     * The dispatcher thread.
     */
    
    public JabberDataBlockDispatcher dispatcher;
    
    private Vector outgoingPackets = new Vector();

    private String server; // for ping
    
    public boolean pingSent;
	
    public boolean loggedIn;
    
    private boolean xmppV1;
    
    private String sessionId;

    public Vector outgoingQueries = new Vector();

    private StreamConnection connection;
    
    /**
     * Constructor. Connects to the server and sends the jabber welcome message.
     *
     */
    public JabberStream( String server, String hostAddr, String proxy) throws IOException {
        this.server=server;

        boolean waiting=Config.getInstance().istreamWaiting;
                
        if (proxy == null) {
            connection = (StreamConnection) Connector.open(hostAddr);
        } else {
//#if HTTPCONNECT
//#             connection = io.HttpProxyConnection.open(hostAddr, proxy, StaticData.getInstance().account.getProxyUser(), StaticData.getInstance().account.getProxyPass());
//#elif HTTPPOLL
//#             connection = new io.HttpPollConnection(hostAddr, proxy);
//#elif HTTPBIND
//#             connection = new io.HttpBindConnection(hostAddr, proxy);
//#else            
            throw new IllegalArgumentException ("no proxy supported");
//#endif            
        }
        
        iostream = new Utf8IOStream(connection);
        dispatcher = new JabberDataBlockDispatcher(this);        
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
        StringBuffer header=new StringBuffer("<stream:stream to='" ).append( server ).append( "' xmlns='jabber:client' xmlns:stream='http://etherx.jabber.org/streams'" );
        header.append(" version='1.0'");
        if (SR.MS_XMLLANG!=null) {
            header.append(" xml:lang='").append(SR.MS_XMLLANG).append("'");
        }
        header.append( '>' );
        send(header.toString());        
//#ifdef HTTPBIND
//#         }
//#endif
    }
    
     public boolean tagStart(String name, Vector attributes) {
        if (name.equals( "stream:stream" ) ) {
            sessionId = XMLParser.extractAttribute("id", attributes);
            String version=XMLParser.extractAttribute("version", attributes);
            xmppV1 = ("1.0".equals(version));
            
            dispatcher.broadcastBeginConversation();
            return false;
        }
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
        
        return super.tagStart(name, attributes);
    }

    public boolean isXmppV1() { return xmppV1; }
 
    public String getSessionId() { return sessionId; }
     
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
//#                 }
//#             }
//#         }
//#endif

        if (currentBlock == null) {
            if (name.equals( "stream:stream" ) ) {
                dispatcher.halt();
                iostream.close();
                if (!Config.getInstance().oldNokiaS60)
                    iostream=null;
                throw new XMLException("Normal stream shutdown");
            }
            return;
        }
        
        if (currentBlock.getParent() == null) {
            if (currentBlock.getTagName().equals("stream:error")) {
                XmppError xe = XmppError.decodeStreamError(currentBlock);

                dispatcher.halt();
                iostream.close();
                if (!Config.getInstance().oldNokiaS60)
                    iostream=null;
                throw new XMLException("Stream error: "+xe.toString());                
            }

        }
        
        super.tagEnd(name);
    }

    protected void dispatchXmppStanza(JabberDataBlock currentBlock) {
                    dispatcher.broadcastJabberDataBlock( currentBlock );
                }
    
    public void startKeepAliveTask(){
        Account account=StaticData.getInstance().account;
        int keepAliveType = account.getKeepAliveType();
        if (keepAliveType == 0)
            return;
        int keepAlivePeriod;
//#ifdef HTTPBIND
//#         if (connection instanceof HttpBindConnection) {
//#             keepAliveType = 4;
//#             keepAlivePeriod = ((HttpBindConnection)connection).waitPeriod;
//#         } else {
//#endif         
         keepAlivePeriod=account.getKeepAlivePeriod();
//#ifdef HTTPBIND
//#         }
//#endif
        if (keepAlive!=null) { keepAlive.destroyTask(); keepAlive=null; }
        
        keepAlive=new TimerTaskKeepAlive(keepAlivePeriod, keepAliveType);
    }
    
    private boolean connected = false;
    /**
     * The threads run method. Handles the parsing of incomming data in its
     * own thread.
     */
    public void run() {
        try {
            XMLParser parser = new XMLParser( this );
            connected = true;
            
            byte cbuf[] = new byte[512];

            while (connected) {

                while (!outgoingPackets.isEmpty()) {
                    sendPacket((String)outgoingPackets.elementAt(0));
                    outgoingPackets.removeElementAt(0);
                }
                
                int length = iostream.read(cbuf);
                if (length==0) {
                    try { Thread.sleep(100); } catch (Exception e) {} 
                        continue; 
                }
                parser.parse(cbuf, length);
            }
            
            //dispatcher.broadcastTerminatedConnection( null );
        } catch( Exception e ) {            
            //e.printStackTrace();
            dispatcher.broadcastTerminatedConnection(e);
        }
        closeConnection();
    }
    private void closeConnection() {
        if (null != keepAlive) keepAlive.destroyTask();
        
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

            dispatcher.setJabberListener( null );
            //TODO: see FS#528
            try {  Thread.sleep(500); } catch (Exception e) {}
            send("</stream:stream>");
            
            for  (int time=10; 0 < time; --time) {
                if (!dispatcher.isActive()) break;
                try {  Thread.sleep(500); } catch (Exception e) {}
            }
            //connection.close();
        } catch( IOException e ) { }
        dispatcher.halt();
        if (iostream != null) {
        iostream.close();
        if (!Config.getInstance().oldNokiaS60)
            iostream = null; // may hang device
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
//#ifdef DEBUG
//#             ex.printStackTrace();
//#endif
        }
    }
    
    /**
     * Method of sending data to the server.
     *
     * @param The data to send to the server.
     */
    public void sendKeepAlive(int type) throws IOException {
        switch (type) {
//#ifdef HTTPBIND
//#             case 4: // BOSH "keep-alive"
//#                 if (((HttpBindConnection)connection).threadsCount < 2 && loggedIn)
//#                     send("");
//#                 break;
//#endif
            case 3:
                if (pingSent) {
                    dispatcher.broadcastTerminatedConnection(new Exception("Ping Timeout"));
                } else {
                    //System.out.println("Ping myself");
                    ping();
                }
                break;
             case 2:
                send("<iq/>");
                 break;
             case 1:
                send(" ");
         }
     }
    
    private void sendPacket(String data) throws IOException {
        iostream.send(data);
//#ifdef CONSOLE
//#             if (data.equals("</iq") || data.equals(" ") || data.equals("")) addLog("Ping myself", 1);
//#             else addLog(data, 1);
//#endif
    }

    public void send( String data ) throws IOException {
        outgoingPackets.addElement(data);
    }
    
    
    private void sendBuf( StringBuffer data ) throws IOException {
        outgoingPackets.addElement(data.toString());
    }
    
    /**
     * Method of sending a Jabber datablock to the server.
     *
     * @param block The data block to send to the server.
     */
    
    public void send( JabberDataBlock block )  {
        if (block instanceof Iq) {
            String type = block.getTypeAttribute();
            if (type.equals("set") || type.equals("get"))
                outgoingQueries.addElement(block.getAttribute("id"));
        }
        try {
            StringBuffer buf = new StringBuffer();
            block.constructXML(buf);
            sendBuf(buf);
        } catch (Exception e) {
        }
    }
    
//#ifdef CONSOLE
//#     public void addLog (String data, int type) {
//#ifdef PLUGINS
//#             if (StaticData.getInstance().Console)
//#endif
//#         StanzasList.getInstance().add(data, type);
//#     }
//#endif

    /**
     * Set the listener to this stream.
     */
    public void addBlockListener(JabberBlockListener listener) { 
        dispatcher.addBlockListener(listener);
    }

    public void cancelBlockListener(JabberBlockListener listener) { 
        dispatcher.cancelBlockListener(listener);
    }
    
    public void cancelBlockListenerByClass(Class removeClass) {
        dispatcher.cancelBlockListenerByClass(removeClass);
    }
    
    public void setJabberListener( JabberListener listener ) {
        dispatcher.setJabberListener( listener );
    }
    
    private void ping() {
        pingSent=true;
        send(IqPing.query(StaticData.getInstance().account.getServer(), "ping"));
    }

//#if ZLIB
    public void setZlibCompression() {
        iostream.setStreamCompression();
    }

    public String getStreamStats() {
        return iostream.getStreamStats();
    }
    
    public String getConnectionData() {
        return iostream.getConnectionData();
    }
//#endif
//#if TLS
//#     public void setTls() throws IOException {
//#ifdef DEBUG        
//#         System.out.println("iostream setTls");
//#endif        
//#         iostream.setTls();
//#     }
//#endif        

    public long getBytes() {
        return iostream.getBytes();
    }
    
    private TimerTaskKeepAlive keepAlive;

     private class TimerTaskKeepAlive extends TimerTask{
        private Timer t;
        //private int verifyCtr;
        // int period;
        private int type;
        public TimerTaskKeepAlive(int periodSeconds, int type){
            t=new Timer();
            this.type=type;
            //this.period=periodSeconds;
            long periodRun=periodSeconds*1000; // milliseconds
            t.schedule(this, periodRun, periodRun);
        }
        
        public void run() {
            try {                 
                 if (loggedIn) sendKeepAlive(type);
            } catch (Exception e) { 
                if (Client.StaticData.getInstance().roster != null)
                Client.StaticData.getInstance().roster.errorLog("Exception in keep-alive task: "+ e.getMessage());
                dispatcher.broadcastTerminatedConnection(e);
                
            }
        }
	
        public void destroyTask(){
            if (t!=null){
                this.cancel();
                t.cancel();
                t=null;
            }
        }
    }
}



