/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.StreamConnection;

/**
 *
 * @author Vitaly
 */
public class SOCKS5Stream {
    
    private StreamConnection connection;
    private InputStream inpStream = null;
    private OutputStream outStream = null;
    
    public SOCKS5Stream(StreamConnection con) throws IOException {
        connection = con;
        inpStream = connection.openInputStream();
        outStream = connection.openOutputStream();
    }
    
    public void send(byte[] data, int ofs, int length) throws IOException {        
        synchronized (outStream) {
            outStream.write(data, ofs, length);
            outStream.flush();
        }
    }
    public void send(byte[] data) throws IOException {        
        send(data, 0, data.length);
    }
    
    public int read(byte[] buf) throws IOException {
        
        int avail = inpStream.available();        
        if (avail==0) return 0;        
        if (avail>buf.length) avail=buf.length;        
        return inpStream.read(buf, 0, avail);
    }
    
    public void close() {
        try {
            inpStream.close(); inpStream = null;
            outStream.close(); outStream = null;
        } catch (IOException e) {
            System.err.println(e);
        }
    }

}
