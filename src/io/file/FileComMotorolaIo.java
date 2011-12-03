/*
 * FileComMotorolaIo.java
 *
 * Created on 1.11.2006, 20:54
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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;
import javax.microedition.io.Connector;

/**
 *
 * @author evgs
 */
class FileComMotorolaIo extends FileIO{

    private com.motorola.io.FileConnection fileConnection;

    /** Creates a new instance of FileComMotorolaIo */
    public FileComMotorolaIo(String fileName) {
        this.fileName=fileName;
    }

    protected void openFile() throws IOException {
        String uri="file:///" + fileName;
        fileConnection = (com.motorola.io.FileConnection) Connector.open(uri);
    }

    public OutputStream openOutputStream() throws IOException {
        if (fileConnection==null) openFile();
        if (!fileConnection.exists()) {
            fileConnection.create();
        } else {
            fileConnection.delete();
            fileConnection.create();
        }
        return fileConnection.openOutputStream();
    }
    
    public OutputStream openOutputStream(long pos_eof) throws IOException {
        if (fileConnection==null) openFile();
        if (!fileConnection.exists()) fileConnection.create();
        return fileConnection.openOutputStream();
    }

    public InputStream openInputStream() throws IOException {
        if (fileConnection==null) openFile();
        return fileConnection.openInputStream(); 
    }

    public void close() throws IOException {
        if (fileConnection!=null) fileConnection.close();
        fileConnection=null;
    }

    public long fileSize() {
        return (fileConnection == null)? 0: fileConnection.fileSize();
    }

    protected Vector rootDirs() {
        String[] roots = com.motorola.io.FileSystemRegistry.listRoots();
        Vector rd=new Vector(roots.length);
        for (int i = 0; i < roots.length; i++)
            rd.addElement(roots[i].substring(1));
        return rd;
    }

    protected Vector dirs(boolean directoriesOnly) throws IOException {
        openFile();
        String[] list = fileConnection.list();
        close();
        
        Vector rd=new Vector(list.length + 1);
        for (int i = 0; i < list.length; i++) {
            if (directoriesOnly & !list[i].endsWith("/")) continue;
            int st=(list[i].startsWith("/")) ? 1 : 0;
            rd.addElement(list[i].substring(st+fileName.length()));
        }
        return rd;
    }

    public void delete() throws IOException{
        fileConnection.delete();
    }

    public void rename(String newName) throws IOException {
        fileConnection.rename(newName);
    }
}

//#endif
