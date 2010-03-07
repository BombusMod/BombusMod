/*
Copyright (c) 2001 Lapo Luchini.
 
Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
 
  1. Redistributions of source code must retain the above copyright notice,
     this list of conditions and the following disclaimer.
 
  2. Redistributions in binary form must reproduce the above copyright
     notice, this list of conditions and the following disclaimer in
     the documentation and/or other materials provided with the distribution.
 
  3. The names of the authors may not be used to endorse or promote products
     derived from this software without specific prior written permission.
 
THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHORS
OR ANY CONTRIBUTORS TO THIS SOFTWARE BE LIABLE FOR ANY DIRECT, INDIRECT,
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
/*
 * This program is based on zlib-1.1.3, so all credit should go authors
 * Jean-loup Gailly(jloup@gzip.org) and Mark Adler(madler@alumni.caltech.edu)
 * and contributors of zlib.
 */

package com.jcraft.jzlib;

import java.io.*;

public final class ZInputStream  extends InputStream {
    
    private InputStream in;
    private ZStream z = new ZStream();
    private final int bufsize = 512;
    private int flush = JZlib.Z_NO_FLUSH;
    private byte[] buf = new byte[bufsize];
    
    public ZInputStream(InputStream in) {
        this(in, false);
    }

    public ZInputStream(InputStream in, boolean nowrap) {
        this.in = in;
        z.inflateInit(nowrap);
        z.next_in = buf;
        z.next_in_index = 0;
        z.avail_in = 0;
    }
    
    public int read() throws IOException {
        byte[] b = new byte[1];
        if(-1 == read(b, 0, 1)) {
            return -1;
        }
        return (b[0] & 0xFF);
    }
    
    private boolean nomoreinput = false;
    
    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }
    private int readBuf(int size) throws IOException {
        return in.read(buf, 0, size);
    }
    public int read(byte[] b, int off, int len) throws IOException {
        if(0 == len) {
            return 0;
        }
        int err;
        z.next_out = b;
        z.next_out_index = off;
        z.avail_out = len;
        do {
            if ((0 == z.avail_in) && !nomoreinput) { // if buffer is empty and more input is avaiable, refill it
                z.next_in_index = 0;
                
                int avail = in.available();
                while (0 == avail) {
                    try { Thread.sleep(70); } catch (Exception e) {};
                    avail = in.available();
                }
                z.avail_in = readBuf(Math.min(avail, buf.length));
                
                
                if(-1 == z.avail_in) {
                    z.avail_in = 0;
                    nomoreinput = true;
                }
            }
            err = z.inflate(flush);
            
            if(nomoreinput && (JZlib.Z_BUF_ERROR == err)) {
                return -1;
            }
            if ((JZlib.Z_OK != err) && (JZlib.Z_STREAM_END != err)) {
                throw new IOException("inflating error #" + err);
            }
            if((nomoreinput || JZlib.Z_STREAM_END == err) && (z.avail_out == len)) {
                return -1;
            }
        } while (z.avail_out == len && JZlib.Z_OK == err);

        return len - z.avail_out;
    }
    
    public long skip(long n) throws IOException {
        return (long)read(new byte[(int)Math.min(512, n)]);
    }
    
    public int getFlushMode() {
        return flush;
    }
    
    public void setFlushMode(int flush) {
        this.flush = flush;
    }
    
    /**
     * We can't count available data size after zlib processing
     * so available() always returns internal buffer size
     */
    public int available() throws IOException {
        return (0 < in.available()) ? bufsize : 0;
    }
    
    
    /**
     * Returns the total number of bytes input so far.
     */
    public long getTotalIn() {
        return z.total_in;
    }
    
    /**
     * Returns the total number of bytes output so far.
     */
    public long getTotalOut() {
        return z.total_out;
    }

    public void close() throws IOException { in.close(); }
    
    public void mark(int readlimit) { in.mark(readlimit); }
    
    public boolean markSupported() { return in.markSupported(); }
    
    public void reset() throws IOException { in.reset(); }

}
