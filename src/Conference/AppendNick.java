/*
 * AppendNick.java
 *
 * Created on 14.09.2005, 23:32
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

import Client.Contact;
import Client.StaticData;
import Client.MessageEdit;
import locale.SR;
import com.alsutton.jabber.datablocks.Presence;
import java.util.Enumeration;
import java.util.Vector;

import ui.controls.form.DefForm;

/**
 *
 * @author EvgS
 */
public final class AppendNick
        extends DefForm
{

    Vector nicknames;
    int caretPos; 
    
    private MessageEdit me;
    
    public AppendNick(Contact to, int caretPos, MessageEdit me) {
        super(SR.MS_SELECT_NICKNAME);
        this.caretPos=caretPos;
        this.me = me;      
                
        
        for (Enumeration e=StaticData.getInstance().roster.getHContacts().elements(); e.hasMoreElements(); ) {
            Contact c=(Contact)e.nextElement();
            if (c.group==to.group && c.origin>Contact.ORIGIN_GROUPCHAT && c.status<Presence.PRESENCE_OFFLINE)
                itemsList.addElement(c);
        }
        enableListWrapping(true);
    }    
        
    public void eventOk(){
         try {
             String nick=((Contact)getFocusedObject()).getJid();
             int rp=nick.indexOf('/');
             StringBuffer b=new StringBuffer(nick.substring(rp+1));
             
            if (caretPos==0) b.append(':');
            me.insert(b.toString(), caretPos);
            b=null;
         } catch (Exception e) {}
         midlet.BombusMod.getInstance().setDisplayable(me.textbox);
    }
     
    public void cmdOk() { eventOk(); }
    public void cmdCancel() {
        midlet.BombusMod.getInstance().setDisplayable(me.textbox);
    }
     
    public String touchLeftCommand(){ return SR.MS_SELECT; }
    public String touchRightCommand(){ return SR.MS_BACK; }
}
