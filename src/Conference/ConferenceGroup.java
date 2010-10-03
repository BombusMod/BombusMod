/*
 * ConferenceGroup.java
 *
 * Created on 29.11.2005, 23:11
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

package Conference;

import Client.Group;
import Client.Groups;
import Client.Jid;
import images.RosterIcons;

/**
 *
 * @author EvgS
 */
public class ConferenceGroup extends Group{
    
    /** Creates a new instance of ConferenceGroup */
    public ConferenceGroup(String name, Jid jid) {
	super(name);
        this.jid = jid;
        imageExpandedIndex=RosterIcons.ICON_GCJOIN_INDEX;
        imageCollapsedIndex=RosterIcons.ICON_GCCOLLAPSED_INDEX; 
        this.type=Groups.TYPE_MUC;
    }

    public boolean inRoom = true;
    
    public MucContact selfContact;
    public MucContact confContact;
    
    public Jid jid;
    public String password;
    
    public int getOnlines(){ return (onlines>0)? onlines-1:0; }
    public int getNContacts(){ return (nContacts>0)? nContacts-1:0; }   
    
    
    public long conferenceJoinTime;
}
