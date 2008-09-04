/*
 * MessageArchive.java
 *
 * Created on 11.12.2005, 2:33
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
 *
 */

package Archive;

import Client.Msg;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Vector;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;

public class MessageArchive {
    
    RecordStore rs;
    Vector indexes;
//#ifdef PLUGINS
//#     public static String plugin = new String("PLUGIN_ARCHIVE");
//#endif
    
    private final static String ARCHIVE="archive";
    private final static String TEMPLATES="ad_templates";

    private int where;
    /** Creates a new instance of MessageArchive */
    public MessageArchive(int where) {
        this.where=where;
	try {
	    rs=RecordStore.openRecordStore((where==1)?ARCHIVE:TEMPLATES, true);
	    int size=rs.getNumRecords();
	    indexes=new Vector(size);
	    RecordEnumeration re=rs.enumerateRecords(null, null, false);
	    
	    while (re.hasNextElement() ){
		indexes.addElement(new Integer(re.nextRecordId()));
	    }
	} catch (Exception e) { }
    }

    public int size(){
	return indexes.size();
    }
    
    private int getRecordId(int index) {
	return ((Integer)indexes.elementAt(index)).intValue();
    }
    public Msg msg(int index){
	try {
	    ByteArrayInputStream bais=new ByteArrayInputStream(
		rs.getRecord(getRecordId(index))
	    );
	    DataInputStream dis=new DataInputStream(bais);
	    Msg msg=new Msg(dis);
            msg.itemCollapsed=true; 
	    dis.close();
	    return msg;
	} catch (Exception e) {}
	return null;
    }
    
    public void delete(int index) {
	try {
	    rs.deleteRecord(getRecordId(index));
	    indexes.removeElementAt(index);
	} catch (Exception e) {}
    }
    
    public void deleteAll() {
	try {
            int i=-1;
            int num=rs.getNumRecords();
            while (true) {
                i=i+1;
                rs.deleteRecord(getRecordId(i));
                
                if (num==i) break;
            }
	} catch (Exception e) {}
	try {
            indexes.removeAllElements();
            RecordStore.deleteRecordStore((where==1)?ARCHIVE:TEMPLATES);
	} catch (Exception e) {}
    }

    public int freeSpace(){
	try {
	    return rs.getSizeAvailable()/1024;
	} catch (Exception e) { }
	return 0;
    }
    
    public void close(){
	try {
	    rs.closeRecordStore();
	} catch (Exception e) { }
	rs=null;
    }

    public static void store(Msg msg, int where) {
	try {
	    ByteArrayOutputStream bout = new ByteArrayOutputStream();
	    DataOutputStream dout = new DataOutputStream( bout );
	    msg.serialize( dout );
	    dout.close();
	    byte b[]=bout.toByteArray();
	    
	    RecordStore rs=RecordStore.openRecordStore((where==1)?ARCHIVE:TEMPLATES, true);
	    rs.addRecord(b, 0, b.length);
            rs.closeRecordStore();
	} catch (Exception e) { e.printStackTrace(); }
    }
}
