/*
 * File.java
 *
 * Created on 1.11.2006, 20:52
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

package io.file;

import Client.Config;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;

/**
 *
 * @author evgs
 */
public abstract class FileIO {
    protected final static int NOT_DETECTED=0;
    protected final static int NONE=-1;
    protected final static int JSR75=1;
    protected final static int COM_MOTOROLA=2;
    protected final static int COM_SIEMENS=3;
    protected final static int JSR75_SIEMENS=4;
    
    public final static int MAX_NAME_LEN=48;
    
    protected static int fileSystemType;
    
    protected String fileName;
    
    public static FileIO createConnection(String fileName) {
        if (fileSystemType==NOT_DETECTED) {
            fileSystemType=NONE;
            try {
                if (Config.getInstance().phoneManufacturer!=Config.JBED)
                    Class.forName("javax.microedition.io.file.FileConnection");
                fileSystemType=JSR75;
                if (Config.getInstance().phoneManufacturer==Config.SIEMENS) fileSystemType=JSR75_SIEMENS;
            } catch (Exception e) {
                try {
                    Class.forName("com.motorola.io.FileConnection");
                    fileSystemType=COM_MOTOROLA;
                } catch (Exception e2) {
                    try {
                        Class.forName("com.siemens.mp.io.File");
                        fileSystemType=COM_SIEMENS;
                    } catch (Exception e3) {}
                }
            }
            //System.out.println("Detected fs:"+fileSystemType );
        }
        switch (fileSystemType) {
            case JSR75_SIEMENS:
            case JSR75: return new FileJSR75(fileName);
            case COM_MOTOROLA: return new FileComMotorolaIo(fileName);
            case COM_SIEMENS: return new FileSiemens(fileName);
        }
        return null;
    }
    
    public Vector fileList(boolean directoriesOnly) throws IOException{
        if (fileName.length()==0) return rootDirs();
        Vector dir=dirs(directoriesOnly);
        dir.addElement("../");
        return dir;
    }
    
    public byte[] fileRead() {
        InputStream is=null;
        try { is=openInputStream(); } catch (IOException e) {}
        int fileSize = 0;
        try { fileSize =(int)fileSize(); } catch (IOException e) {}
        byte[] b=new byte[fileSize];
        try { is.read(b); is.close(); } catch (IOException e) {}
        try { close(); } catch (IOException e) {}
        return b;
    }
    
    public void fileWrite(byte[] bytes) {
        try { delete(); } catch (IOException ex) { }
        OutputStream os = null;
        try { os = openOutputStream(0); }
            catch (IOException ex) { }
        try { if (os != null) os.write(bytes); }
            catch (IOException ex) { }
        try { if (os != null) { os.close(); os.flush(); } }
            catch (IOException ex) { }
        try { close(); }
            catch (IOException ex) { }
    }
    
    public abstract OutputStream openOutputStream() throws IOException;
   
    public abstract InputStream openInputStream() throws IOException;
    
    public abstract void close() throws IOException;
    
    public abstract void delete() throws IOException;
    
    public abstract void rename(String newName) throws IOException;
    
    public abstract long fileSize() throws IOException;

    protected abstract Vector rootDirs();
    
    protected abstract Vector dirs(boolean directoriesOnly) throws IOException;

    public abstract OutputStream openOutputStream(long pos_eof) throws IOException;
}
