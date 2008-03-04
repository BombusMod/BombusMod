/*
 * InviteForm.java
 *
 * Created on 15.05.2006, 20:15
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

package Conference;

import Client.Contact;
import Client.StaticData;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.datablocks.Message;
import com.alsutton.jabber.datablocks.Presence;
import java.util.Enumeration;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextField;
import locale.SR;
import ui.controls.TextFieldEx;

/**
 *
 * @author root
 */
public class InviteForm implements CommandListener{
    
    private Display display;
    private Displayable parentView;
    
    Form form;
    TextField reason;
    ChoiceGroup conferenceList;
    
    Contact contact;
    
    Command cmdOk=new Command(SR.MS_OK, Command.OK, 1);
    Command cmdCancel=new Command(SR.MS_CANCEL, Command.BACK, 99);
    /** Creates a new instance of InviteForm */
    public InviteForm(Contact contact, Display display) {
        this.display=display;
        this.contact=contact;
        parentView=display.getCurrent();
        
        form=new Form(SR.MS_INVITE);
        reason=new TextFieldEx(SR.MS_REASON, null, 200, TextField.ANY);
        
        conferenceList=new ChoiceGroup (SR.MS_CONFERENCE, ChoiceGroup.POPUP);
        for (Enumeration c=StaticData.getInstance().roster.getHContacts().elements(); c.hasMoreElements(); ) {
            try {
                MucContact mc=(MucContact)c.nextElement();
                if (mc.origin==Contact.ORIGIN_GROUPCHAT && mc.getStatus()==Presence.PRESENCE_ONLINE)
                    conferenceList.append(mc.getJid(), null);
            } catch (Exception e) {}
        }
        

        form.append(contact.getName());
        form.append("\n");
        form.append(conferenceList);
        form.append(reason);
        
        form.addCommand(cmdOk);
        form.addCommand(cmdCancel);
        form.setCommandListener(this);
        
        display.setCurrent(form);
    }

    public void commandAction(Command c, Displayable d) {
        if (c==cmdOk) {
            String room=conferenceList.getString( conferenceList.getSelectedIndex());
            String rs=reason.getString();
            
            Message inviteMsg=new Message(room);
            JabberDataBlock x=inviteMsg.addChildNs("x", "http://jabber.org/protocol/muc#user");
            JabberDataBlock invite=x.addChild("invite",null);
            String invited=(contact instanceof MucContact)? ((MucContact)contact).realJid : contact.getBareJid();
            
            invite.setAttribute("to", invited);

             invite.addChild("reason",rs);
            StaticData.getInstance().roster.theStream.send(inviteMsg);
            display.setCurrent(StaticData.getInstance().roster);
        }
        else if (c==cmdCancel) { display.setCurrent(parentView); }
    }
    
}
