/*
 * StatusList.java
 *
 * Created on 3.12.2005, 17:33
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

package Client;

import com.alsutton.jabber.datablocks.Presence;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Enumeration;
import java.util.Vector;
import locale.SR;
import io.NvStorage;

/**
 *
 * @author EvgS
 */
public class StatusList {
    
    // Singleton implementation
    private static StatusList instance;
    public static StatusList getInstance() {
	if (instance==null) instance=new StatusList();
	return instance;
    }

    public Vector statusList;

    /** Creates a new instance of StatusList */
    private StatusList() {
        statusList=new Vector(7);
        
        try {
	    DataInputStream inputStream=NvStorage.ReadFileRecord("status", 0);
	    
	    createFromStream(Presence.PRESENCE_ONLINE, Presence.PRS_ONLINE, inputStream);
	    createFromStream(Presence.PRESENCE_CHAT, Presence.PRS_CHAT, inputStream);
	    createFromStream(Presence.PRESENCE_AWAY, Presence.PRS_AWAY, inputStream);
 	    createFromStream(Presence.PRESENCE_XA, Presence.PRS_XA, inputStream);
 	    createFromStream(Presence.PRESENCE_DND, Presence.PRS_DND, inputStream);
 	    createFromStream(Presence.PRESENCE_INVISIBLE, Presence.PRS_INVISIBLE, inputStream);
	    createFromStream(Presence.PRESENCE_OFFLINE, Presence.PRS_OFFLINE, inputStream);
	    
	    inputStream.close();
            inputStream=null;
        } catch (Exception e) { 
            //e.printStackTrace(); 
        }

    }
    
    private void createFromStream(int presenceIndex, String presenceName, DataInputStream dataInputStream) {
	ExtendedStatus status=new ExtendedStatus(presenceIndex, presenceName, (String) SR.getPresence(presenceName));
        try {
            int priority=dataInputStream.readInt();
	    status.setPriority((priority>128)?128:priority);
            status.setMessage(dataInputStream.readUTF());
            status.setAutoRespond(dataInputStream.readBoolean());
            status.setAutoRespondMessage(dataInputStream.readUTF());
        } catch (Exception e) { /*on stream errors*/ }
	statusList.addElement(status);
    }
    
    public void saveStatusToStorage(){
        try {
            DataOutputStream outputStream=NvStorage.CreateDataOutputStream();
            
            int j=statusList.size();
            for (int i=0;i<j;i++) {
                ExtendedStatus e=(ExtendedStatus)statusList.elementAt(i);
                outputStream.writeInt(e.getPriority());
                outputStream.writeUTF(e.getMessage());
                outputStream.writeBoolean(e.getAutoRespond());
                outputStream.writeUTF(e.getAutoRespondMessage());
            }
            NvStorage.writeFileRecord(outputStream, "status", 0, true);            
        } catch (Exception e) { 
            //e.printStackTrace(); 
        }
    }
    
    public ExtendedStatus getStatus(final int status) {
	ExtendedStatus es=null;
	for (Enumeration e=statusList.elements(); e.hasMoreElements(); ){
	    es=(ExtendedStatus)e.nextElement();
	    if (status==es.getImageIndex()) break;
	}
	return es;
    }
}
