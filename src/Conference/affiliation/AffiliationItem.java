/*
 * AffiliationItem.java
 *
 * Created on 30.10.2005, 11:53
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

package Conference.affiliation;

import com.alsutton.jabber.JabberDataBlock;
import images.RosterIcons;
import Colors.Colors;
import ui.IconTextElement;

/**
 *
 * @author EvgS
 */
public class AffiliationItem extends IconTextElement{
    public final static short AFFILIATION_NONE=0;
    public final static short AFFILIATION_OWNER=1;
    public final static short AFFILIATION_ADMIN=2;
    public final static short AFFILIATION_MEMBER=3;
    public final static short AFFILIATION_OUTCAST=4;
    
    public static String getAffiliationName(short index){
        switch (index) {
            case AFFILIATION_OWNER: return "owner";
            case AFFILIATION_ADMIN: return "admin";
            case AFFILIATION_MEMBER: return "member";
            case AFFILIATION_OUTCAST: return "outcast";
        }
        return "none";
    };
    
    public int getImageIndex(){ 
        switch (affiliation) {
            case AFFILIATION_OWNER: return RosterIcons.ICON_REGISTER_INDEX;
            case AFFILIATION_ADMIN: return RosterIcons.ICON_MODERATOR_INDEX;
            case AFFILIATION_MEMBER: return 0;
            case AFFILIATION_OUTCAST: return RosterIcons.ICON_ERROR_INDEX;
        }
        return RosterIcons.ICON_INVISIBLE_INDEX; 
    }

    public String jid;
    public int affiliation;
    public String reason;
        
    /** Creates a new instance of AffiliationItem */
    public AffiliationItem(String jid, String affiliation) {
        super(RosterIcons.getInstance());
        this.jid=jid;
        for (short index=1; index<5; index++) {
            if (affiliation.equals(getAffiliationName(index))) 
                this.affiliation=index;
        }
	reason="";
    }
    
    public AffiliationItem(JabberDataBlock item) {
        this(item.getAttribute("jid"), item.getAttribute("affiliation"));
        reason=item.getChildBlockText("reason");
        
	if (reason.length()==0) 
            reason=null;
    }
    
    
    public int getColor() { return Colors.LIST_INK; }
    
    public String toString() { return (reason!=null)? jid+" *" : jid; }
    
    public int compare(IconTextElement temp2) {
        return jid.compareTo( ((AffiliationItem)temp2).jid);
    }

    public String getTipString() {
        return reason;
    }	

//#ifdef SECONDSTRING
//#         public String getSecondString() { 
//#             return null;
//#         }
//#endif
}
