/*
 * FileJSR75.java
 *
 * Created on 1.11.2006, 20:53
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
//# 
//# package io.file;
//# 
//# import java.io.IOException;
//# import java.io.InputStream;
//# import java.io.OutputStream;
//# import java.util.Enumeration;
//# import java.util.Vector;
//# import javax.microedition.io.Connector;
//# 
//# /**
//#  *
//#  * @author evgs
//#  */
//# class FileJSR75 extends FileIO {
//#     private javax.microedition.io.file.FileConnection fileConnection;
//# 
//#     /** Creates a new instance of FileJSR75 */
//#     public FileJSR75(String fileName) {
//#         this.fileName=fileName;
//#     }
//#     
//#     protected void openFile() throws IOException{
//#         fileConnection = (javax.microedition.io.file.FileConnection) Connector.open("file:///" + fileName, Connector.READ_WRITE);
//#     }
//# 
//#     public OutputStream openOutputStream() throws IOException{
//#         if (fileConnection==null) openFile();
//#         if (!fileConnection.exists()) {
//#             fileConnection.create();
//#         } else {
//#             fileConnection.delete();
//#             fileConnection.create();
//#         }
//#         return fileConnection.openOutputStream();
//#     }
//#     
//#     public void delete() throws IOException{
//#         if (fileConnection==null) openFile();
//#         fileConnection.delete();
//#         fileConnection = null;
//#     }
//# 
//#     public OutputStream appendOutputStream() throws IOException {
//#         if (fileConnection==null) 
//#             openFile();
//#         if (!fileConnection.exists()) 
//#             fileConnection.create();
//#         int size = (int)fileSize();        
//#         return (size > 0) ? fileConnection.openOutputStream(size)
//#                 : fileConnection.openOutputStream();
//#     }
//# 
//#     public InputStream openInputStream() throws IOException{
//#         if (fileConnection==null) openFile();
//#         return fileConnection.openInputStream(); 
//#     }
//# 
//#     public void close() throws IOException{
//#         if (fileConnection!=null) fileConnection.close();
//#         fileConnection=null;
//#     }
//# 
//#     public long fileSize() throws IOException {
//#         return (fileConnection == null)? 0
//#                 : fileConnection.fileSize();
//#     }
//# 
//#     protected Vector rootDirs() {
//#         Vector rd = new Vector();
//#         if (fileSystemType==JSR75_SIEMENS) {
//#             rd.addElement("0:/");
//#             rd.addElement("4:/");
//#             return rd;
//#         }
//#         Enumeration roots = javax.microedition.io.file.FileSystemRegistry.listRoots();
//#         while (roots.hasMoreElements())
//#             rd.addElement(((String) roots.nextElement()));
//#         return rd;
//#     }
//# 
//#     protected Vector dirs(boolean directoriesOnly) throws IOException{
//#         openFile();
//#         Enumeration dirs=fileConnection.list();
//#         close();
//#         Vector rd=new Vector();
//#         while (dirs.hasMoreElements()) {
//#             String item=(String)dirs.nextElement();
//#             if (directoriesOnly) {
//#                 if (item.endsWith("/"))
//#                     rd.addElement(item);
//#             } else {            
//#                 rd.addElement(item);
//#             }
//#         }
//#         return rd;
//#     }
//# 
//#     public void rename(String newName) throws IOException {
//#         fileConnection.rename(newName);
//#     }
//# }
//# 
//#endif
