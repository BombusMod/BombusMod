/*
 * Utf8IOStream.java
 *
 * Created on 18.12.2005, 0:52
 *
 * Copyright (c) 2005-2007, Eugene Stahov (evgs), http://bombus-im.org
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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.*;
//import locale.SR;
import util.strconv;

/**
 *
 * @author EvgS
 */
public class Utf8IOStream implements Runnable{
    
    private StreamConnection connection;
    public InputStream inpStream;
    private OutputStream outStream;

    private boolean iStreamWaiting;

    private int bytesRecv;

    private int bytesSent;

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
//#if !(MIDP1)
        try {
            SocketConnection sc=(SocketConnection)connection;
            sc.setSocketOption(SocketConnection.KEEPALIVE, 1);
        } catch (Exception e) {}
//#endif
	
	inpStream = connection.openInputStream();
	outStream = connection.openOutputStream();	

      length=pbyte=0;
    }
    
    public void send( StringBuffer data ) throws IOException {
	synchronized (outStream) {
            StringBuffer outbuf=strconv.toUTFSb(data);
            int outLen=outbuf.length();
            byte bytes[]=new byte[outLen];
            for (int i=0; i<outLen; i++) {
                bytes[i]=(byte)outbuf.charAt(i);
            }
            
	    outStream.write(bytes);
            bytesSent+=outLen;
	    
//#if OUTSTREAM_FLUSH
	    outStream.flush();
            outbuf=null;
//#endif
            
	}
//#if (XML_STREAM_DEBUG)        
//#         System.out.println(">> "+data);
//#endif
    }
    
    byte cbuf[]=new byte[512];
    int length;
    int pbyte;
    
    private int chRead() throws IOException{
        bytesRecv++;
        if (length>pbyte) {
            return cbuf[pbyte++];
        }

        int avail=inpStream.available();
        
//#if ZLIB
        if (inpStream instanceof ZInputStream) avail=512;
//#endif
        
        while (avail==0) {
//#if !ZLIB
//#             //trying to fix phillips 9@9
//#             if (!Config.getInstance().istreamWaiting) break;
//#endif    
            try { Thread.sleep(100); } catch (Exception e) {}
            avail=inpStream.available();
        }
        
//#if !(XML_STREAM_DEBUG)
	if (avail<2) {
            return inpStream.read() &0xff;
	}
//#else
//#         if (avail<2) {
//# 	  System.out.println(" single-byte");
//#           int ch=inpStream.read();
//# 	  System.out.println("<< "+(char)ch);
//# 	  return ch;
//#         }
//#           
//#           System.out.println(" prebuffering "+avail);
//#endif

	length= inpStream.read(cbuf, 0, (avail<512)?avail:512 );
	pbyte=1;
	
//#if (XML_STREAM_DEBUG)
//# 	System.out.println("<< "+new String(cbuf, 0, length));
//#endif
	return cbuf[0];
    }
    
    public int getNextCharacter()
    throws IOException {
	int chr = chRead() &0xff;
	if( chr == 0xff ) return -1; // end of stream
	
	if (chr<0x80) return chr;
	if (chr<0xc0) throw new IOException("Bad UTF-8 Encoding encountered");
	
        int chr2= chRead() &0xff;
        if (chr2==0xff) return -1;
        if (chr2<0x80) throw new IOException("Bad UTF-8 Encoding encountered");
	
	if (chr<0xe0) {
	    // cx, dx 
	    return ((chr & 0x1f)<<6) | (chr2 &0x3f);
	}

        int chr3= chRead() &0xff;
        if (chr3==0xff) return -1;
        if (chr3<0x80) throw new IOException("Bad UTF-8 Encoding encountered");
        
	if (chr<0xf0) {
	    // cx, dx 
	    return ((chr & 0x0f)<<12) | ((chr2 &0x3f) <<6) | (chr3 &0x3f);
	}
	
	//System.out.print((char)j);
	//return -1;
        
	// chr>=0xf0
        int chr4= chRead() &0xff;
        if (chr3==0xff) return -1;
        if (chr3<0x80) throw new IOException("Bad UTF-8 Encoding encountered");
        
        //return ((chr & 0x07)<<18) | ((chr2 &0x3f) <<12) |((chr3 &0x3f) <<6) | (chr4 &0x3f);
        return '?'; // java char type contains only 16-bit symbols
    }
    
    public void close() {
	try { outStream.close();    }  catch (Exception e) {}
	try { inpStream.close();    }  catch (Exception e) {}
	// Alcatel temporary bugfix - this method hangs
	//try { connection.close();   }  catch (Exception e) {};
	new Thread(this).start();
    }

    public void run() {
	// Alcatel temporary bugfix - this method hangs
	try { connection.close();   }  catch (Exception e) {}
    }

//#if ZLIB
    
    private void appendZlibStats(StringBuffer s, long packed, long unpacked, boolean read){
        s.append(packed); s.append(read?"->":"<-"); s.append(unpacked);
        String ratio=Long.toString((10*unpacked)/packed);
        int dotpos=ratio.length()-1;
        
        
        s.append(" (");
        s.append( (dotpos==0)? "0":ratio.substring(0, dotpos));
        s.append('.');
        s.append(ratio.substring(dotpos));
        s.append('x');
        s.append(")");
        
    }

    public String getStreamStats() {
        StringBuffer stats=new StringBuffer();
        try {
            int sent=this.bytesSent;
            int recv=this.bytesRecv;
            if (inpStream instanceof ZInputStream) {
                ZInputStream z = (ZInputStream) inpStream;
                recv+=z.getTotalIn()-z.getTotalOut();
                ZOutputStream zo = (ZOutputStream) outStream;
                sent+=zo.getTotalOut()-zo.getTotalIn();
                stats.append("\nZLib:\nin: "); appendZlibStats(stats, z.getTotalIn(), z.getTotalOut(), true);
                stats.append("\nout: "); appendZlibStats(stats, zo.getTotalOut(), zo.getTotalIn(), false);
            }
            stats.append("\nstream: \nin: "); stats.append(recv);
            stats.append(" out: "); stats.append(sent);
        } catch (Exception e) {
            stats=null;
            return "";
        }
        return stats.toString();
    }

    public int getBytesR() {
        if (inpStream instanceof ZInputStream) {
            ZInputStream z = (ZInputStream) inpStream;
            return (int)z.getTotalIn();
        }
        return bytesRecv;
    }
    
    public int getBytesS() {
        if (inpStream instanceof ZInputStream) {
            ZOutputStream zo = (ZOutputStream) outStream;
            return (int)zo.getTotalOut();
        }
        return bytesSent;
    }
    
    public long getBytes() {
        try {
            if (inpStream instanceof ZInputStream) {
                ZOutputStream zo = (ZOutputStream) outStream;
                ZInputStream z = (ZInputStream) inpStream;
                return (int)zo.getTotalOut()+(int)z.getTotalIn();
            }
            return this.bytesSent+this.bytesRecv;
        } catch (Exception e) { }
        return 0;
    }
    
//#else
//#     
//#      public String getStreamStats() {
//#          StringBuffer stats=new StringBuffer();
//#          try {
//#              int sent=this.bytesSent;
//#              int recv=this.bytesRecv;
//#              stats.append("\nStream: in="); stats.append(recv);
//#              stats.append(" out="); stats.append(sent);
//#          } catch (Exception e) {
//#              stats=null;
//#              return "";
//#          }
//#          return stats.toString();
//#      }
//#      
//#      public long getBytes() {
//#          try {
//#              return this.bytesSent+this.bytesRecv;
//#          } catch (Exception e) { }
//#          return 0;
//#      }
//#      
//#     public int getBytesR() {
//#         return this.bytesRecv;
//#     }
//#     
//#     public int getBytesS() {
//#         return this.bytesSent;
//#     }
//#endif
}
