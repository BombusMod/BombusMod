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

//#ifdef FILE_IO

package io.file;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;

import Client.StaticData;

/**
 *
 * @author evgs
 */
public abstract class FileIO {
    
    public final static int MAX_NAME_LEN=32768;
    
    protected String fileName;
    
    public static FileIO createConnection(String fileName) {        
        return new FileJDK(fileName);
    }
    
    public List<File> fileList(boolean directoriesOnly) throws IOException{
        if (fileName.length()==0) return rootDirs();
        List<File> dir=dirs(directoriesOnly);
        dir.add(new File("../"));
        return dir;
    }
    
    public byte[] readFile() {
        InputStream is = null;
        try {
            is = openInputStream();
            int fileSize = 0;
            fileSize = (int) fileSize();
            byte[] b = new byte[fileSize];
            is.read(b);
            is.close();
            close();
            return b;
        } catch (IOException ex) {
            if (StaticData.Debug)
                ex.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException ex) {
                if (StaticData.Debug)
                    ex.printStackTrace();
            }
        }
        return null;
    }
    
    public void writeFile(byte[] rawBytes) {
        try {
            OutputStream os = appendOutputStream();
            if (os != null) {
                os.write(rawBytes);
                os.flush();
                os.close();                
            }
            close();
        } catch (IOException ex) {
            if (StaticData.Debug)
                ex.printStackTrace();
        }
    }
    
    public String fileReadUtf() {
        InputStreamReader is = null;
        try {
            is = new InputStreamReader(openInputStream(), "UTF-8");
            int fileSize = (int) fileSize();
            char[] cbuff = new char[fileSize];
            is.read(cbuff);
            is.close();
            close();
            return new String(cbuff);
        } catch (Exception ex) {
            if (StaticData.Debug)
                ex.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (Exception ex) {
                if (StaticData.Debug)
                    ex.printStackTrace();
            }
        }
        return null;
    }
    
    public void fileWriteUtf(String src) {
        try {
            OutputStream os = appendOutputStream();
            if (os != null) {
                os.write(src.getBytes("UTF-8"));
                os.close();
                os.flush();
            }
            close();
        } catch (IOException ex) {
            if (StaticData.Debug)
                ex.printStackTrace();
        }
    }
    
    public abstract OutputStream openOutputStream() throws IOException;
   
    public abstract InputStream openInputStream() throws IOException;
    
    public abstract void close() throws IOException;
    
    public abstract void delete() throws IOException;
    
    public abstract void rename(String newName) throws IOException;
    
    public abstract long fileSize() throws IOException;

    protected abstract List<File> rootDirs();

    protected abstract List<File> dirs(boolean directoriesOnly) throws IOException;

    public abstract OutputStream appendOutputStream() throws IOException;
}

//#endif
