/*
 * FilterInputStream.java
 *
 * Created on 30 Июль 2006 г., 20:31
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package com.jcraft.jzlib;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author evgs
 */
public class FilterInputStream extends InputStream{

    protected InputStream in;
    
    /** Creates a new instance of FilterInputStream */
    protected FilterInputStream(InputStream in) { this.in=in; }
    
    public int available() throws IOException {  return in.available(); }
    
    public void close() throws IOException { in.close(); }
    
    public void mark(int readlimit) { in.mark(readlimit); }
    
    public boolean markSupported() { return in.markSupported(); }
    
    public int read() throws IOException { return in.read(); }
    
    public int read(byte[] b) throws IOException { return in.read(b); }
    
    public int read(byte[] b, int off, int len) throws IOException { return in.read(b, off, len); }

    public void reset() throws IOException { in.reset(); }
    
    public long skip(long n) throws IOException { return in.skip(n); }

}
