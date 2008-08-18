/*
 * HttpPollingStream.java
 *
 * Created on 22 Ноябрь 2007 г., 19:25
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package io;

import com.ssttr.crypto.MessageDigest;
import com.ssttr.crypto.SHA1;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.io.StreamConnection;

/**
 *
 * @author evgs
 */
public class HttpPollingConnection implements StreamConnection {
    
    /** Creates a new instance of HttpPollingStream */
    public HttpPollingConnection(String host, String pollingUrl) {
        this.host=host;
        this.pollingUrl=pollingUrl;
        outData=new StringBuffer();
        inStack=new Vector();
        
        his=new HttpPollInputStream();
        hos=new HttpPollOutputStream();
        
        ps=0;

        opened=true;
    }
    
    String host;
    String pollingUrl;
    String sessionId;
    
    Vector inStack;
    int ps;
    
    StringBuffer outData;
    
    InputStream his;
    OutputStream hos;
    
    Vector keys;

    private boolean opened;
    private String error;
    
    synchronized private void httpPostRequest(String postData) throws IOException {
        try {
            HttpConnection hc=(HttpConnection)Connector.open(pollingUrl);
            hc.setRequestMethod(HttpConnection.POST);
            hc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded" );
            hc.setRequestProperty("Host", "host");
            
            StringBuffer out=new StringBuffer();
            if (sessionId==null) { 
                out.append("0");
                keys=new Vector();
            } else out.append(sessionId);

            do {
                if (keys.size()==0) {
                    initKeys();
                }
                out.append(";").append((String)keys.lastElement());
                keys.removeElementAt(keys.size()-1);
            } while (keys.size()==0);
            
            
            out.append(",").append(postData);
            
            int outLen=out.length();
            //hc.setRequestProperty("Content-Length", String.valueOf(outLen));

            byte bytes[]=new byte[outLen];
            for (int i=0; i<outLen; i++) {
                bytes[i]=(byte)out.charAt(i);
            }
            
            OutputStream os=hc.openOutputStream();
            os.write(bytes);
            os.close();
            
            int resp=hc.getResponseCode();
            
            if (resp!=HttpConnection.HTTP_OK) throw new IOException("HTTP Error code"+resp);
            
            InputStream is=hc.openInputStream();
            
            String cookie=hc.getHeaderField("Set-Cookie");
            int expires=cookie.indexOf(';');
            if (expires>0) cookie=cookie.substring(3, expires);
            
            if (cookie.endsWith(":0")) {
                opened=false;
                error=cookie;
            }
            
            if (sessionId==null) { 
                sessionId=cookie;
            }
            
            byte data[];
            int inLen=(int)hc.getLength();
            
            if (inLen<0) {
                throw new Exception ("Content-Length missing"); //TODO:
            } else {
                int actual = 0;
                int bytesread = 0 ;
                data = new byte[inLen];
                while ((bytesread != inLen) && (actual != -1)) {
                    actual = is.read(data, bytesread, inLen - bytesread);
                    bytesread += actual;
                }
                
                if (inLen>0) inStack.addElement(data);
            }
            is.close();
            hc.close();
        } catch (Exception e) {
            opened=false;
            error=e.toString();
            e.printStackTrace();
        }
    }

    
    private class HttpPollInputStream extends InputStream {

        public int read() throws IOException {
            if (!opened) throw new IOException("Connection closed");
            if (inStack.size()==0) return -1;
            
            int llen;
            byte[] inb;
            do {
                inb=(byte[])inStack.firstElement();
                llen=inb.length - ps;
                if (llen==0) { inStack.removeElementAt(0); ps=0; }
                if (inStack.size()==0) return -1;
            } while (llen==0);
            
            return inb[ps++];
        }

        public int available() throws IOException {
            if (!opened) throw new IOException("Connection closed: "+error);
            if (inStack.size()==0) return 0;
            int avail=((byte[])inStack.firstElement()).length - ps;
            if (avail==0) { inStack.removeElementAt(0); ps=0; return available(); }
            return avail;
        }
    }

    private class HttpPollOutputStream extends OutputStream {
        public void write(int i) throws IOException {
            outData.append((char) i);
        }
        
        public void flush() throws  IOException {
            synchronized (outData) {
                if (!opened) throw new IOException("Connection closed");

                if (outData.length()>0) httpPostRequest(outData.toString());

                outData.setLength(0);
            }
        }
    }
    
    public InputStream openInputStream() throws IOException {
        return his;
    }

    public OutputStream openOutputStream() throws IOException {
        return hos;
    }

    public DataInputStream openDataInputStream() throws IOException {
        return new DataInputStream (his);
    }

    public DataOutputStream openDataOutputStream() throws IOException {
        return new DataOutputStream (hos);
    }

    public void close() throws IOException {
        opened=false;
    }

    private void initKeys() {
        String k0="magick";
        while (keys.size()<6) {
            MessageDigest sha1=new SHA1();
            sha1.init();
            sha1.updateASCII(k0);
            sha1.finish();
            k0=sha1.getDigestBase64();
            
            keys.addElement(k0);
        }
    }

}
