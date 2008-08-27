/*
 * InviteForm.java
 *
 * Created on 26.05.2008, 09:37
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

package Conference;

import Client.Contact;
import Client.StaticData;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.datablocks.Message;
import com.alsutton.jabber.datablocks.Presence;
import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextField;
import locale.SR;
import ui.controls.form.SimpleString;
import ui.controls.form.DefForm;
import ui.controls.form.DropChoiceBox;
import ui.controls.form.TextInput;

public class InviteForm
        extends DefForm {
    
    private Display display;

    Vector conferences=new Vector();
    
    TextInput reason;
    DropChoiceBox conferenceList;
    Contact contact;
    
    /** Creates a new instance of InviteForm */
    public InviteForm(Display display, Displayable pView, Contact contact) {
        super(display, pView, SR.MS_INVITE);
        this.display=display;
        this.contact=contact;
        
        itemsList.addElement(new SimpleString(contact.getName(), true));
        conferenceList=new DropChoiceBox(display, SR.MS_CONFERENCE);
        for (Enumeration c=StaticData.getInstance().roster.getHContacts().elements(); c.hasMoreElements(); ) {
            try {
                MucContact mc=(MucContact)c.nextElement();
                if (mc.origin==Contact.ORIGIN_GROUPCHAT && mc.getStatus()==Presence.PRESENCE_ONLINE) {
                    conferenceList.append(mc.getJid());
                    conferences.addElement(mc.getJid());
                }
            } catch (Exception e) {}
        }
        itemsList.addElement(conferenceList);
        
        reason=new TextInput(display, SR.MS_REASON, null, "", TextField.ANY); //200
        itemsList.addElement(reason);
        
        moveCursorTo(getNextSelectableRef(-1));
        attachDisplay(display);
        this.parentView=pView;
    }

    public void cmdOk() {
        String room=(String) conferences.elementAt(conferenceList.getSelectedIndex());
        String rs=reason.getValue();

        Message inviteMsg=new Message(room);
        JabberDataBlock x=inviteMsg.addChildNs("x", "http://jabber.org/protocol/muc#user");
        JabberDataBlock invite=x.addChild("invite",null);
        String invited=(contact instanceof MucContact)? ((MucContact)contact).realJid : contact.getBareJid();

        invite.setAttribute("to", invited);

        invite.addChild("reason",rs);
        StaticData.getInstance().roster.theStream.send(inviteMsg);
        display.setCurrent(StaticData.getInstance().roster);
    }
}
