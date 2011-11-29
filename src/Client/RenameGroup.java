/*
 * newRenameGroup.java
 *
 * Created on 20.05.2008, 15:26
 *
 * Copyright (c) 2006-2008, Daniel Apatin (ad), http://apatin.net.ru
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

package Client;

import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.datablocks.Iq;
import java.util.Enumeration;
import javax.microedition.lcdui.TextField;
import locale.SR;
import ui.controls.form.DefForm;
import ui.controls.form.SpacerItem;
import ui.controls.form.TextInput;

/**
 *
 * @author ad
 */
public class RenameGroup 
        extends DefForm {
    
    private Group group;
    //private Contact contact;
    
    private TextInput groupName;
    
    /** Creates a new instance of newRenameGroup
     * @param group
     */
    public RenameGroup(Group group/*, Contact contact*/) {
        super(SR.MS_RENAME);
        //this.contact=contact;
        this.group=group;
        
        groupName = new TextInput(null, /*(contact==null)?*/group.name/*:contact.getGroup().getName()*/, "groups", TextField.ANY); // 32, TextField.ANY
        itemsList.addElement(groupName);
        
        itemsList.addElement(new SpacerItem(0));
        
        moveCursorTo(getNextSelectableRef(-1));
    }

    public void  cmdOk() {
        //if (contact==null)
            sd.theStream.send(new IqQueryRenameGroup (group.name, groupName.getValue()));
        /*else
            sd.roster.theStream.send(new IqQueryRoster(contact.getBareJid(), contact.nick, groupName.getValue(), null)); */

        destroyView();
    }    
    
    
    class IqQueryRenameGroup extends Iq {
        public IqQueryRenameGroup(String sourceGroup, String destGroup){
            super(null, Iq.TYPE_SET, "addros");

            JabberDataBlock qB = addChildNs("query", "jabber:iq:roster" );

            for (Enumeration e=sd.roster.hContacts.elements(); e.hasMoreElements();){
                Contact cr=(Contact)e.nextElement();
                if (cr.group.name.equals(sourceGroup)) {
                    JabberDataBlock item= qB.addChild("item",null);
                    item.setAttribute("jid", cr.bareJid);
                    item.setAttribute("name", cr.nick);
                    item.setAttribute("subscription", null);
                    if (destGroup!=null) {
                        item.addChild("group",destGroup);
                    }
                }
            }
        }
    }
}
