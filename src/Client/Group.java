/*
 * Group.java
 *
 * Created on 8.05.2005, 0:36
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
 *
 */

package Client;
import com.alsutton.jabber.datablocks.Presence;
import images.RosterIcons;
import java.util.*;
import Colors.Colors;
import ui.*;


/**
 *
 * @author Evg_S
 */
public class Group extends IconTextElement {
    String name;
    protected int type; // group index
    public boolean visible=true;
    protected int nContacts;
    protected int onlines;
    
    public int imageExpandedIndex=RosterIcons.ICON_EXPANDED_INDEX;
    public int imageCollapsedIndex=RosterIcons.ICON_COLLAPSED_INDEX;
    public int imageHasMessageIndex=RosterIcons.ICON_COLLAPSED_INDEX;
    
    
    public Vector contacts;
    
    private Vector tcontacts;
    public int tonlines;
    private int tncontacts;
    public int unreadMessages=0;
    
    protected boolean collapsed;

    public Group(String name /*, String label*/) {
        super(RosterIcons.getInstance(), null);
        this.name=name;
        /*this.label=label;*/
        
    }
    public int getColor(){ return Colors.GROUP_INK; }
    
    public int getImageIndex() {
        return collapsed?
            (unreadMessages>0)?imageHasMessageIndex:imageCollapsedIndex
            :imageExpandedIndex;
    }
    
    public String getName() { return name; }
    
    protected String mainbar(String mainbarStart) {
        StringBuffer mb=new StringBuffer();
        mb.append(mainbarStart);
        mb.append(" (");
        mb.append(getOnlines());
        mb.append("/");
        mb.append(getNContacts());
        mb.append(")");

        return mb.toString();
    }
    
    public String toString(){ return mainbar(name);  }

    public void onSelect(){
        collapsed=!collapsed;
    }

    public void startCount(){
	//int size=(contacts==null)?10:contacts.size();
	tonlines=tncontacts=unreadMessages=0;
	//tcontacts=new Vector(size);
	contacts=new Vector();
    }

    public void addContact(Contact c) {
	tncontacts++;
	boolean online=c.status<Presence.PRESENCE_OFFLINE;
	if (online) {
	    tonlines++;
	}
	//int gindex=c.getGroupIndex();
	// hide offlines whithout new messages
        unreadMessages+=c.getNewMsgsCount();
        
	if (
	online
	|| Config.getInstance().showOfflineContacts
	|| c.getNewMsgsCount()>0
	//|| gindex==Groups.NIL_INDEX
	//|| gindex==Groups.TRANSP_INDEX
	|| type==Groups.TYPE_NOT_IN_LIST
	|| type==Groups.TYPE_TRANSP
	|| type==Groups.TYPE_VISIBLE
	|| c.origin==Contact.ORIGIN_GROUPCHAT
	)
	    contacts.addElement(c);
	//grp.addContact(c);
    }
    void finishCount() {
	//contacts=tcontacts;
        onlines=tonlines;
        nContacts=tncontacts;
        tcontacts=null;
    }

    public int getNContacts() {
        return nContacts;
    }

    public int getOnlines() {
        return onlines;
    }
    
    public int compare(IconTextElement right) {
        if (type<((Group)right).type) return -1;
        if (type>((Group)right).type) return 1;
        return name.toLowerCase().compareTo(((Group)right).name.toLowerCase());
    }

//#ifdef SECONDSTRING
//#         public String getSecondString() { 
//#             return null;
//#         }
//#endif
}
