/*
 * FileSiemens.java
 *
 * Created on 7.11.2006, 23:20
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
//# import com.siemens.mp.io.File;
//# import java.io.IOException;
//# import java.io.InputStream;
//# import java.io.OutputStream;
//# import java.util.Vector;
//# 
//# 
//# public class FileSiemens extends FileIO {
//#     
//#     private File f;
//#     private int fd;
//# 
//#     public FileSiemens(String fileName) {
//#         this.fileName=fileName=fileName.replace('/', '\\');//.substring(1);
//#     }
//#     
//#     public void openFile() throws IOException{
//# 	f = new File();
//#         fd = f.open(fileName);
//#     }
//#     
//#     public void close() throws IOException{
//# 	f.close(fd);
//# 	f = null;
//#     }
//#     
//#     public long fileSize() throws IOException {
//# 	return f.length(fd);
//#     }
//# 
//#     protected Vector rootDirs() {
//#         //System.out.println("Siemens root");
//#         Vector rd = new Vector();
//#         rd.addElement("0:/");
//#         rd.addElement("4:/");
//#         return rd;
//#     }
//# 
//#     protected Vector dirs(boolean directoriesOnly) throws IOException{
//#         String[] directory=File.list(fileName);
//#         Vector rd=new Vector();
//#         
//#         if (directory!=null) 
//#         for (int i = 0; i < File.list(fileName).length; i++) {
//#             if (directory[i].endsWith("/")) { // x75 feature? (excepting s75)
//#                 rd.addElement(directory[i]);
//#             } else if (File.isDirectory(fileName+directory[i])) {
//#                 rd.addElement(directory[i]+"/");
//#             } else {
//#                 rd.addElement(directory[i]);
//#             }
//#         }
//#         return rd;
//#     }
//# 
//#     public OutputStream openOutputStream() throws IOException {
//#         openFile();
//#         return new FileSiemensOutputStream(f, fd, 0);
//#     }
//# 
//#     public InputStream openInputStream() throws IOException {
//#         openFile();
//#         return new FileSiemensInputStream(f, fd);
//#     }
//#     
//#     public OutputStream openOutputStream(long pos_eof) throws IOException {
//#         openFile();
//#         return new FileSiemensOutputStream(f, fd, pos_eof);
//#     }
//#     
//#     public void delete() throws IOException{
//#         File.delete(fileName);
//#     }
//# 
//#     public void rename(String newName) throws IOException {
//#         File.rename(fileName, newName);
//#     }
//# }
//# class FileSiemensInputStream extends InputStream {
//#     private int fileDescriptor;
//#     private File f;
//# 
//#     public FileSiemensInputStream(File f, int fd) {
//#         this.f=f; this.fileDescriptor=fd;
//#     }
//#     
//#     public int read() throws IOException {
//#         byte buf[]=new byte[1];
//#         f.read(fileDescriptor, buf, 0, 1);
//#         return buf[0];
//#     }
//# 
//#     public int read(byte[] b, int off, int len) throws IOException {  return f.read(fileDescriptor, b, off, len); }
//# 
//#     public int read(byte[] b) throws IOException {  return f.read(fileDescriptor, b, 0, b.length);  }
//# }
//# 
//# class FileSiemensOutputStream extends OutputStream {
//#     private int fileDescriptor;
//#     private File f;
//# 
//#     public FileSiemensOutputStream(File f, int fd, long Seek) {
//#         this.f=f;
//#         this.fileDescriptor=fd;
//#         try {
//#             this.f.seek(fd, f.length(fd));
//#         } catch (IOException ex) {
//#         }
//#     }
//#     
//#     public void write(int i) throws IOException {
//#         byte buf[]=new byte[1];
//#         f.write(fileDescriptor, buf, 0, 1);
//#     }
//#     
//#     public void write(byte[] b, int off, int len) throws IOException {  f.write(fileDescriptor, b, off, len); }
//# 
//#     public void write(byte[] b) throws IOException {  f.write(fileDescriptor, b, 0, b.length);  }
//# }
//# 
//#endif
