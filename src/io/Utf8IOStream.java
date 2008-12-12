/*
 * Utf8IOStream.java
 *
 * Created on 18.12.2005, 0:52
 *
 * Copyright (c) 2005-2008, Eugene Stahov (evgs), http://bombus-im.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * You can also redistribute and/or modify this program under the
 * terms of the Psi License, specified in the accompanied COPYING
 * file, as published by the Psi Project; either dated January 1st,
 * 2005, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package io;

//#if ZLIB
import com.jcraft.jzlib.JZlib;
import com.jcraft.jzlib.ZInputStream;
import com.jcraft.jzlib.ZOutputStream;
//#endif
import Client.StaticData;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.*;
import Client.Config;
import util.Strconv;

/**
 *
 * @author EvgS
 */
public class Utf8IOStream {
    
    private StreamConnection connection;
    private InputStream inpStream;
    private OutputStream outStream;

    private boolean iStreamWaiting;

    private long bytesRecv;
    private long bytesSent;

//#if (ZLIB)
    public void setStreamCompression(){
        inpStream=new ZInputStream(inpStream);
        outStream=new ZOutputStream(outStream, JZlib.Z_DEFAULT_COMPRESSION);
        ((ZOutputStream)outStream).setFlushMode(JZlib.Z_SYNC_FLUSH);
    }
//#endif
    
    /** Creates a new instance of Utf8IOStream */
    public Utf8IOStream(StreamConnection connection) throws IOException {
	this.connection=connection;
        try {
            SocketConnection sc=(SocketConnection)connection;
            sc.setSocketOption(SocketConnection.KEEPALIVE, 1);
            sc.setSocketOption(SocketConnection.LINGER, 300);
        } catch (Exception e) {}

	inpStream = connection.openInputStream();
	outStream = connection.openOutputStream();	

        length=0;
        pbyte=0;
    }
    
    public void send( StringBuffer data ) throws IOException {
	synchronized (outStream) {
            StaticData.getInstance().updateTrafficOut();
            StringBuffer outbuf=Strconv.toUTFSb(data);
            int outLen=outbuf.length();
            byte bytes[]=new byte[outLen];
            for (int i=0; i<outLen; i++) {
                bytes[i]=(byte)outbuf.charAt(i);
            }
	    outStream.write(bytes);
            setSent(bytesSent+outLen);

	    outStream.flush();
            outbuf=null;
            updateTraffic();
	}
//#if (XML_STREAM_DEBUG)        
//#         System.out.println(">> "+data);
//#endif
    }
    
    byte cbuf[]=new byte[512];
    int length;
    int pbyte;

    public int read(byte buf[]) throws IOException {
        int avail=inpStream.available();

        if (avail==0) 
//#if !ZLIB
//#             //trying to fix phillips 9@9
//#             if (!Config.getInstance().istreamWaiting) avail=1;
//#             else
//#endif            
            return 0;

        if (avail>buf.length) avail=buf.length;
        
        avail=inpStream.read(buf, 0, avail);
//#if (XML_STREAM_DEBUG)
//# 	System.out.println("<< "+new String(buf, 0, avail));
//#endif
        setRecv(bytesRecv+avail);
        updateTraffic();
        return avail;
    }

    private void updateTraffic() {
        StaticData.getInstance().traffic=getBytes();
    }
    
    private void setRecv(long bytes) {
        bytesRecv=bytes;
    }
    
    private void setSent(long bytes) {
        bytesSent=bytes;
    }    
    
    public void close() {
	try { outStream.close(); outStream=null; }  catch (Exception e) {}
	try { inpStream.close(); inpStream=null; }  catch (Exception e) {}
    }

//#if ZLIB
    private void appendZlibStats(StringBuffer s, long packed, long unpacked, boolean read){
        s.append(packed).append(read?"->":"<-").append(unpacked);
        String ratio=Long.toString((10*unpacked)/packed);
        int dotpos=ratio.length()-1;

        s.append(" (").append( (dotpos==0)? "0":ratio.substring(0, dotpos)).append('.').append(ratio.substring(dotpos)).append('x').append(")");
    }

    public String getStreamStats() {
        StringBuffer stats=new StringBuffer();
        try {
            long sent=bytesSent;
            long recv=bytesRecv;
            if (inpStream instanceof ZInputStream) {
                ZInputStream z = (ZInputStream) inpStream;
                recv+=z.getTotalIn()-z.getTotalOut();
                ZOutputStream zo = (ZOutputStream) outStream;
                sent+=zo.getTotalOut()-zo.getTotalIn();
                stats.append("ZLib:\nin: "); appendZlibStats(stats, z.getTotalIn(), z.getTotalOut(), true);
                stats.append("\nout: "); appendZlibStats(stats, zo.getTotalOut(), zo.getTotalIn(), false);
            }
            stats.append("\nin: ")
                 .append(recv)
                 .append("\nout: ")
                 .append(sent);
        } catch (Exception e) {
            stats=null;
            return "";
        }
        return stats.toString();
    }
    
    public String getConnectionData() {
        StringBuffer stats=new StringBuffer();
        try {
            stats.append(((SocketConnection)connection).getLocalAddress())
                 .append(":")
                 .append(((SocketConnection)connection).getLocalPort())
                 .append("->")
                 .append(((SocketConnection)connection).getAddress())
                 .append(":")
                 .append(((SocketConnection)connection).getPort());
        } catch (Exception ex) {
            stats.append("unknown");
        }
        return stats.toString();
    }

    public long getBytes() {
        long startBytes=bytesSent+bytesRecv;
        try {
            if (inpStream instanceof ZInputStream) {
                ZOutputStream zo = (ZOutputStream) outStream;
                ZInputStream z = (ZInputStream) inpStream;
                return (long)zo.getTotalOut()+(long)z.getTotalIn();
            }
            return startBytes;
        } catch (Exception e) { }
        return 0;
    }
//#else
//#     
//#      public String getStreamStats() {
//#          StringBuffer stats=new StringBuffer();
//#          try {
//#              long sent=bytesSent;
//#              long recv=bytesRecv;
//#              stats.append("\nStream: in=").append(recv).append(" out=").append(sent);
//#          } catch (Exception e) {
//#              stats=null;
//#              return "";
//#          }
//#          return stats.toString();
//#      }
//#      
//#      public long getBytes() {
//#          try {
//#              return bytesSent+bytesRecv;
//#          } catch (Exception e) { }
//#          return 0;
//#      }
//#endif
}
