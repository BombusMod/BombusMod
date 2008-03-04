/*
 * ArrayLoader.java
 *
 * Created on 24.09.2006, 1:47
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

package util;

import java.io.DataInputStream;
//import java.io.DataOutputStream;
import java.io.InputStream;
//import java.util.Enumeration;

/*
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.io.file.FileSystemRegistry;
*/

/**
 *
 * @author evgs
 */
public class ArrayLoader {
    
    /** Creates a new instance of ArrayLoader */
    public ArrayLoader() {
    }
    public int[] readIntArray(String name) {
        try {
            InputStream in = this.getClass().getResourceAsStream(name);
            DataInputStream is=new DataInputStream(in);
            int len=is.readInt();
            int[] arrayInt=new int[len];
            
            for (int i=0; i<len;i++) {
                arrayInt[i]=is.readInt();
            }
            return arrayInt;
        } catch (Exception ex) { ex.printStackTrace(); }
        
        return null;
    }
    
    public short[] readShortArray(String name) {
        try {
            InputStream in = this.getClass().getResourceAsStream(name);
            DataInputStream is=new DataInputStream(in);
            int len=is.readInt();
            short[] arrayShort=new short[len];
            
            for (int i=0; i<len;i++) {
                arrayShort[i]=is.readShort();
            }
            return arrayShort;
        } catch (Exception ex) { ex.printStackTrace(); }
        
        return null;
    }
    public byte[] readByteArray(String name) {
        try {
            InputStream in = this.getClass().getResourceAsStream(name);
            DataInputStream is=new DataInputStream(in);
            int len=is.readInt();
            byte[] arrayByte=new byte[len];

            is.read(arrayByte, 0, len);
            return arrayByte;
        } catch (Exception ex) { ex.printStackTrace(); }
        
        return null;
    }

/*
    public static void writeIntArray(String name, int[] intArray) {
        try {
            for (Enumeration e=FileSystemRegistry.listRoots(); e.hasMoreElements(); ){
                String root = (String) e.nextElement();
                System.out.println(root);
            }
            FileConnection fc=(FileConnection)Connector.open("file:///root1/" + name);
            if (fc.exists()) return;
            fc.create();
            DataOutputStream os=fc.openDataOutputStream();
            os.writeInt(intArray.length);
            
            for (int i=0; i<intArray.length; i++) os.writeInt(intArray[i]);
            
            os.close();
            fc.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        

    }

    public static void writeShortArray(String name, short[] shortArray) {
        try {
            for (Enumeration e=FileSystemRegistry.listRoots(); e.hasMoreElements(); ){
                String root = (String) e.nextElement();
                System.out.println(root);
            }
            FileConnection fc=(FileConnection)Connector.open("file:///root1/" + name);
            if (fc.exists()) return;
            fc.create();
            DataOutputStream os=fc.openDataOutputStream();
            os.writeInt(shortArray.length);
            
            for (int i=0; i<shortArray.length; i++) os.writeShort(shortArray[i]);
            
            os.close();
            fc.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public static void writeByteArray(String name, byte[] byteArray) {
        try {
            FileConnection fc=(FileConnection)Connector.open("file:///root1/" + name);
            if (fc.exists()) return;
            fc.create();
            DataOutputStream os=fc.openDataOutputStream();
            os.writeInt(byteArray.length);
            
            os.write(byteArray, 0, byteArray.length);
           
            os.close();
            fc.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
*/
}
