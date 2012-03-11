/*
 * HttpPollingStream.java
 *
 * Created on 22 Ноябрь 2007 г., 19:25
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

//#if HTTPBIND || HTTPPOOL
//# 
//# package io;
//# 
//# import java.io.DataInputStream;
//# import java.io.DataOutputStream;
//# import java.io.IOException;
//# import java.io.InputStream;
//# import java.io.OutputStream;
//# import java.util.Vector;
//# import javax.microedition.io.Connector;
//# import javax.microedition.io.HttpConnection;
//# import javax.microedition.io.StreamConnection;
//# 
//# /**
//#  *
//#  * @author evgs
//#  */
//# public class HttpXmppConnection implements StreamConnection {
//#        
//#     
//#     String host;
//#     String connectionUrl;
//#     String sessionId;
//#     
//#     Vector inStack;
//#     int ps;
//#     
//#     StringBuffer outData;
//#     
//#     InputStream his;
//#     OutputStream hos;
//#     
//#     Vector keys;
//# 
//#     protected boolean opened;
//#     protected String error;
//# 
//#     protected String contentType;    
//# 
//#     public int threadsCount;
//# 
//#     /** Creates a new instance of HttpStream */
//#     public HttpXmppConnection(String host, String connectionUrl) {
//#         this.host=host;
//#         this.connectionUrl = connectionUrl;
//#         outData=new StringBuffer();
//#         inStack=new Vector();        
//# 
//#         his=new HttpXmppInputStream();
//#         hos=new HttpXmppOutputStream();
//# 
//#         ps=0;
//#         threadsCount = 0;
//#         opened=true;
//#     }
//#     
//#     private void httpPostRequest(String postData) throws IOException {        
//#         try {
//#             HttpConnection hc=(HttpConnection)Connector.open(connectionUrl);
//#             hc.setRequestMethod(HttpConnection.POST);
//#             hc.setRequestProperty("Content-Type", contentType );
//#             hc.setRequestProperty("Host", "host");
//#             
//#             String out = wrap(postData);
//#             int outLen=out.length();
//#             //hc.setRequestProperty("Content-Length", String.valueOf(outLen));
//# 
//#             byte bytes[]=new byte[outLen];
//#             for (int i=0; i<outLen; i++) {
//#                 bytes[i]=(byte)out.charAt(i);
//#             }
//#             
//#             OutputStream os=hc.openOutputStream();
//#             os.write(bytes);
//#             os.close();
//#             
//#             int resp=hc.getResponseCode();
//#             
//#             if (resp!=HttpConnection.HTTP_OK) throw new IOException("HTTP Error code"+resp);
//#             InputStream is=hc.openInputStream();            
//#             parseCookies(hc.getHeaderField("Set-Cookie"));
//#             
//#             byte data[];
//#             int inLen=(int)hc.getLength();
//#             
//#             if (inLen<0) {
//#                 throw new Exception ("Content-Length missing"); //TODO:
//#             } else {
//#                 int actual = 0;
//#                 int bytesread = 0 ;
//#                 data = new byte[inLen];
//#                 while ((bytesread != inLen) && (actual != -1)) {
//#                     actual = is.read(data, bytesread, inLen - bytesread);
//#                     bytesread += actual;
//#                 }
//#                 
//#                 if (inLen>0) inStack.addElement(data);
//#             }
//#             is.close();
//#             hc.close();
//#         } catch (Exception e) {
//#             opened=false;
//#             error=e.toString();            
//#         }
//#     }
//# 
//#     protected String wrap(String xmppData) { return xmppData; };
//# 
//#     protected void parseCookies(String cookie) {};
//#     
//#     protected class HttpXmppInputStream extends InputStream {
//# 
//#         public int read() throws IOException {
//#             if (!opened) throw new IOException("Connection closed");
//#             if (inStack.isEmpty()) return -1;
//#             
//#             int llen;
//#             byte[] inb;
//#             do {
//#                 inb=(byte[])inStack.firstElement();
//#                 llen=inb.length - ps;
//#                 if (llen==0) { inStack.removeElementAt(0); ps=0; }
//#                 if (inStack.isEmpty()) return -1;
//#             } while (llen==0);
//#             
//#             return inb[ps++];
//#         }
//# 
//#         public int available() throws IOException {
//#             if (!opened) throw new IOException("Connection closed: "+error);
//#             if (inStack.isEmpty()) return 0;
//#             int avail=((byte[])inStack.firstElement()).length - ps;
//#             if (avail==0) { inStack.removeElementAt(0); ps=0; return available(); }
//#             return avail;
//#         }
//#     }
//# 
//#     protected class HttpXmppOutputStream extends OutputStream implements Runnable {
//#         public void write(int i) throws IOException {
//#             outData.append((char) i);
//#         }
//#         
//#         public void flush() throws IOException {
//# 
//#             if (!opened) {
//#                 throw new IOException("Connection closed");
//#             }
//#             threadsCount++;
//#             new Thread(this).start();
//#         }
//# 
//#         public void run() {
//#             try {
//#                 httpPostRequest(outData.toString());
//#             } catch (IOException ex) {
//#             }
//#             outData = new StringBuffer();
//#             threadsCount--;
//#         }
//#     }
//#     
//#     public InputStream openInputStream() throws IOException {
//#         return his;
//#     }
//# 
//#     public OutputStream openOutputStream() throws IOException {
//#         return hos;
//#     }
//# 
//#     public DataInputStream openDataInputStream() throws IOException {
//#         return new DataInputStream (his);
//#     }
//# 
//#     public DataOutputStream openDataOutputStream() throws IOException {
//#         return new DataOutputStream (hos);
//#     }
//# 
//#     public void close() throws IOException {
//#         opened=false;
//#     }    
//# 
//# }
//# 
//#endif
