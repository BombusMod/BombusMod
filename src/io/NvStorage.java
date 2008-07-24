/*
 * NvStorage.java
 *
 * Created on 22.03.2005, 22:56
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
import java.io.*;

import javax.microedition.rms.*;

/**
 *
 * @author Eugene Stahov
 */
public class NvStorage {
    
    /**
     * Opens RMS record from named store
     * and returns it as DataInputStream
     */
    static public DataInputStream ReadFileRecord(String name, int index){
        DataInputStream istream=null;
        
        RecordStore recordStore=null;
        try {
            
            recordStore = RecordStore.openRecordStore(name, false);
            byte[] b=recordStore.getRecord(index+1);
            
            if (b.length!=0)         
            istream=new DataInputStream( new ByteArrayInputStream(b) );
            
        } catch (Exception e) { }
        finally { 
            try { recordStore.closeRecordStore(); } catch (Exception e) {} }
        
        return istream;
    }


    private static ByteArrayOutputStream baos;
    /** Creates DataOutputStream based on ByteOutputStream  */
    static public DataOutputStream CreateDataOutputStream(){
        if (baos!=null) return null;
        DataOutputStream ostream=new DataOutputStream( baos=new ByteArrayOutputStream());
        return ostream;
    }
    
    static public boolean writeFileRecord (
            DataOutputStream ostream, 
            String name, int index, 
            boolean rewrite)
    {
        ByteArrayOutputStream lbaos=baos;
        baos=null; // освободим для следующего
        byte[] b=lbaos.toByteArray();
        

        try {
            if (rewrite) RecordStore.deleteRecordStore(name);
        } catch (Exception e) {}

        RecordStore recordStore;
        try {
            recordStore = RecordStore.openRecordStore(name, true);
        } catch (Exception e) { return false;}
        
        try {
            try {
                recordStore.setRecord(index+1, b, 0, b.length);
            } catch (InvalidRecordIDException e) { recordStore.addRecord(b, 0, b.length); }
            recordStore.closeRecordStore();
            ostream.flush();
            ostream.close();
        } catch (Exception e) { 
            //e.printStackTrace(); 
            return false;
        }

        return true;
    }
}
