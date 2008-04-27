/*
 * RenameGroup.java
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
import xmpp.extensions.IqQueryRoster;
import java.util.Enumeration;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextField;
import locale.SR;
import ui.controls.TextFieldCombo;

public class RenameGroup implements CommandListener{

    private Display display;
    private Form f;
    private TextFieldCombo groupName;
    private Group group;
    private Contact contact;
    
    private Command cmdOk=new Command(SR.MS_OK, Command.SCREEN, 1);
    private Command cmdCancel=new Command(SR.MS_CANCEL, Command.BACK, 99);

    StaticData sd=StaticData.getInstance();

    public RenameGroup(Display display, Group group, Contact contact) {
        this.contact=contact;
        this.group=group;
        this.display=display;
        
        f=new Form(SR.MS_NEWGROUP);
        
        groupName=new TextFieldCombo(SR.MS_MOVE, (contact==null)?group.getName():contact.getGroup().getName(), 32, TextField.ANY, "group", display);
        f.append(groupName);
        
        f.addCommand(cmdOk);
        f.addCommand(cmdCancel);
        
        f.setCommandListener(this);
        
        display.setCurrent(f);
    }

    public void commandAction(Command command, Displayable displayable) {
        if (command==cmdOk) {
            if (contact==null)
                sd.roster.theStream.send(new IqQueryRenameGroup (group.getName(), groupName.getString()));
            else
                sd.roster.theStream.send(new IqQueryRoster(contact.getBareJid(), contact.nick, groupName.getString(), null));        
        }
        display.setCurrent(StaticData.getInstance().roster);
    }
    
    class IqQueryRenameGroup extends Iq {
        public IqQueryRenameGroup(String sourceGroup, String destGroup){
            super(null, Iq.TYPE_SET, "addros");

            JabberDataBlock qB = addChildNs("query", "jabber:iq:roster" );

            for (Enumeration e=sd.roster.hContacts.elements(); e.hasMoreElements();){
                Contact cr=(Contact)e.nextElement();
                if (cr.getGroup().getName()==sourceGroup) {
                    JabberDataBlock item= qB.addChild("item",null);
                    item.setAttribute("jid", cr.getBareJid());
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
