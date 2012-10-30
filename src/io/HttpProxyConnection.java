/*
 * HttpProxyConnection.java
 *
 * Created on 23 Ноябрь 2007 г., 16:43
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

//#ifdef HTTPCONNECT

package io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import util.Strconv;

/**
 *
 * @author evgs
 */
public class HttpProxyConnection implements StreamConnection {
    
    private StreamConnection conn;
    private InputStream is;
    private OutputStream os;
    /** Creates a new instance of HttpProxyConnection */
    
    private HttpProxyConnection() {}
    
    public static HttpProxyConnection open(String hostAddr, String proxyAddr, String userName, String userPass) throws IOException{
        HttpProxyConnection pconn=new HttpProxyConnection();
        
        pconn.conn = (StreamConnection) Connector.open(proxyAddr);
        pconn.is=pconn.conn.openInputStream();
        pconn.os=pconn.conn.openOutputStream();
        String auth = "Proxy-Authorization: Basic ";
        String req = "";
        if (userName != null && userPass != null) {
            auth += Strconv.toBase64(userName + ":" + userPass);            
            req = "CONNECT " + hostAddr + " HTTP/1.0 \r\nHOST " + hostAddr
                + "\r\n" + auth + "\r\nPragma: no-cache\r\n\r\n";
        } else {
        
        req= "CONNECT " + hostAddr + " HTTP/1.0 \r\nHOST " + hostAddr 
                + "\r\nPragma: no-cache\r\n\r\n";
        }
        pconn.os.write(req.getBytes());
        
        String inpLine=pconn.readLine();
        if (inpLine.indexOf("200",0)<=0) throw new IOException(inpLine);
        while (inpLine.length()>0) {
            inpLine=pconn.readLine();
        }
        
        return pconn;
    }
    
    public String readLine() throws IOException {
	StringBuffer buf=new StringBuffer();
	
	boolean eol=false;
	while (true) {
            
            while (is.available()==0) 
                try { Thread.sleep(100); } catch (Exception e) { }
                
	    int c = is.read();
	    if (c<0) { 
		eol=true;
		if (buf.length()==0) return null;
		break;
	    }
	    if (c==0x0d || c==0x0a) {
		eol=true;
		//inputstream.mark(2);
		if (c==0x0a) break;
	    }
	    else {
		if (eol) {
		    //afterEol=c;
		    //inputstream.reset();
		    break;
		}
		buf.append((char) c);
	    }
	}
	return buf.toString();
    }

    
    public InputStream openInputStream() throws IOException { return is; }
    public OutputStream openOutputStream() throws IOException { return os; }

    public DataInputStream openDataInputStream() throws IOException { 
        return new DataInputStream(is);
    }

    public DataOutputStream openDataOutputStream() throws IOException {
        return new DataOutputStream(os);
    }

    public void close() throws IOException {
        try {
            is.close();
        } catch (Exception e) {}

        try {
            is.close();
        } catch (Exception e) {}

        conn.close();
    }
}

//#endif
