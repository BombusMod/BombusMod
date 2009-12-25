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
    
    private JabberDataBlockDispatcher dispatcher;

    //private boolean rosterNotify; //voffk

    private String server; // for ping
    
    public boolean pingSent;
	
    public boolean loggedIn;
    
    private boolean xmppV1;
    
    private String sessionId;
    
    //public void enableRosterNotify(boolean en){ rosterNotify=en; } //voffk
    
    /**
     * Constructor. Connects to the server and sends the jabber welcome message.
     *
     */
    public JabberStream( String server, String hostAddr, String proxy) throws IOException {
        this.server=server;

        boolean waiting=Config.getInstance().istreamWaiting;
        
         StreamConnection connection;
         if (proxy==null) {
             connection = (StreamConnection) Connector.open(hostAddr);
          } else {
//#if HTTPCONNECT
//#             connection = io.HttpProxyConnection.open(hostAddr, proxy);
//#elif HTTPPOLL  
//#             connection = new io.HttpPollingConnection(hostAddr, proxy);
//#else            
            throw new IllegalArgumentException ("no proxy supported");
//#endif            
         }
 
        iostream=new Utf8IOStream(connection);
        dispatcher = new JabberDataBlockDispatcher(this);        
     
        new Thread( this ). start();
    }

    public void initiateStream() throws IOException {
        StringBuffer header=new StringBuffer("<stream:stream to='" ).append( server ).append( "' xmlns='jabber:client' xmlns:stream='http://etherx.jabber.org/streams'" );
        header.append(" version='1.0'");
        if (SR.MS_XMLLANG!=null) {
            header.append(" xml:lang='").append(SR.MS_XMLLANG).append("'");
        }
        header.append( '>' );
        send(header.toString());
        header=null;
    }

    public boolean tagStart(String name, Vector attributes) {
        if (name.equals( "stream:stream" ) ) {
            sessionId = XMLParser.extractAttribute("id", attributes);
            String version=XMLParser.extractAttribute("version", attributes);
            xmppV1 = ("1.0".equals(version));
            
            dispatcher.broadcastBeginConversation();
            return false;
        }
        
        return super.tagStart(name, attributes);
    }

    public boolean isXmppV1() { return xmppV1; }
 
    public String getSessionId() { return sessionId; }
     
    public void tagEnd(String name) throws XMLException {
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
        int keepAliveType=account.getKeepAliveType();
        if (keepAliveType==0) return;
        int keepAlivePeriod=account.getKeepAlivePeriod();

        if (keepAlive!=null) { keepAlive.destroyTask(); keepAlive=null; }
        
        keepAlive=new TimerTaskKeepAlive(keepAlivePeriod, keepAliveType);
    }
    
    /**
     * The threads run method. Handles the parsing of incomming data in its
     * own thread.
     */
    public void run() {
        try {
            XMLParser parser = new XMLParser( this );
            
            byte cbuf[]=new byte[512];
            
            while (true) {
                int length=iostream.read(cbuf);
                
                if (length==0) {
                    try { Thread.sleep(100); } catch (Exception e) {}; 
                    continue; 
                }

                parser.parse(cbuf, length);
            }
            
            //dispatcher.broadcastTerminatedConnection( null );
        } catch( Exception e ) {
            System.out.println("Exception in parser:");
            e.printStackTrace();
            dispatcher.broadcastTerminatedConnection(e);
        };
    }
    
    /**
     * Method to close the connection to the server and tell the listener
     * that the connection has been terminated.
     */
    
    public void close() {
        if (keepAlive!=null) keepAlive.destroyTask();
        
        dispatcher.setJabberListener( null );
        try {
            //TODO: see FS#528
            try {  Thread.sleep(500); } catch (Exception e) {}
             send( "</stream:stream>" );
            int time=10;
            while (dispatcher.isActive()) {
                try {  Thread.sleep(500); } catch (Exception e) {}
                if ((--time)<0) break;
            }
             //connection.close();
        } catch( IOException e ) { }
        dispatcher.halt();
        iostream.close();        
        if (!Config.getInstance().oldNokiaS60) // hangs on second-third reconnect
            iostream=null;
    }
    
    /**
     * Method of sending data to the server.
     *
     * @param The data to send to the server.
     */
    public void sendKeepAlive(int type) throws IOException {
        switch (type){
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
    
    public void send( String data ) throws IOException {
	iostream.send(new StringBuffer(data));
//#ifdef CONSOLE
//#         if (data.equals("</iq") || data.equals(" "))
//#             if (StanzasList.getInstance().enabled) addLog("Ping myself", 1);
//#         else
//#             if (StanzasList.getInstance().enabled) addLog(data, 1);
//#endif
    }
    
    public void sendBuf( StringBuffer data ) throws IOException {
	iostream.send(data);
//#ifdef CONSOLE
//#         if (StanzasList.getInstance().enabled) addLog(data.toString(), 1);
//#endif
    }
    
    /**
     * Method of sending a Jabber datablock to the server.
     *
     * @param block The data block to send to the server.
     */
    
    public void send( JabberDataBlock block )  {
        new SendJabberDataBlock(block).run();
    }
    
//#ifdef CONSOLE
//#     private int canLog=0;
//#     
//#     public void addLog (String data, int type) {
//#ifdef PLUGINS
//#         if (canLog<1) {
//#             if (StaticData.getInstance().Console) {
//#                 canLog=1;
//#             } else {
//#                 canLog=-1;
//#                 return;
//#             }
//#         }
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
                 //System.out.println("Keep-Alive");
                 if (loggedIn)
                     sendKeepAlive(type);
            } catch (Exception e) { 
                dispatcher.broadcastTerminatedConnection(e);
                //e.printStackTrace(); 
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
    
    private class SendJabberDataBlock implements Runnable {
        private JabberDataBlock data;
        public SendJabberDataBlock(JabberDataBlock data) {
            this.data=data;
//            new Thread(this).start();
        }

        public void run(){
            try {
                Thread.sleep(100);
                StringBuffer buf=new StringBuffer();
                data.constructXML(buf);
                sendBuf( buf );
                buf=null;
            } catch (Exception e) { }
        }
    }
}



