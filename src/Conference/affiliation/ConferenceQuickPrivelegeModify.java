/*
 * ConferenceQuickPrivelegeModify.java
 *
 * Created on 12.11.2006, 19:02
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

import Client.StaticData;
import Conference.*;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.datablocks.Iq;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextField;
import locale.SR;
import ui.controls.StringItemEx;
import ui.controls.TextFieldCombo;

/**
 *
 * @author Evg_S
 */
public class ConferenceQuickPrivelegeModify implements CommandListener{
    public final static int KICK=1;
    public final static int VISITOR=2;
    public final static int PARTICIPANT=3;
    public final static int MODERATOR=4;
    
    public final static int OUTCAST=5;
    public final static int NONE=6;
    public final static int MEMBER=7;
    public final static int ADMIN=8;
    public final static int OWNER=9;

    private Display display;
    private Form f;
    private TextFieldCombo reason;
    private MucContact victim;
    
    private Command cmdOk;
    private Command cmdNoReason=new Command(SR.MS_NO_REASON, Command.SCREEN, 2);
    private Command cmdCancel=new Command(SR.MS_CANCEL, Command.BACK, 99);
    
    private int action;

    private String myNick;

    /**
     * Creates a new instance of ConferenceQuickPrivelegeModify
     */
    public ConferenceQuickPrivelegeModify(Display display, MucContact victim, int action, String myNick) {

        this.victim=victim;
        this.action=action;
        this.myNick=myNick;
		
	String okName = SR.MS_OK;
        
        switch (action) {
            case KICK: 
                f=new Form(SR.MS_KICK);
		okName=SR.MS_KICK;
                break;

            case OUTCAST:
                f=new Form(SR.MS_BAN);
		okName=SR.MS_BAN;
                f.append(SR.MS_CONFIRM_BAN);
                break;
            case VISITOR:
                //f=new Form (SR.MS_REVOKE_VOICE); //temporary commented until ejabberd fixes its bug
                okName=SR.MS_REVOKE_VOICE;
                break;
        } // switch
        
        if (f==null) {
            setMucMod();
            return;
        }
        
        this.display=display;
        
        StringBuffer user=new StringBuffer(victim.nick);
        if (victim.jid!=null) {
            user.append(" (");
            user.append(victim.realJid);
            user.append(")");
        }
        f.append(new StringItemEx(SR.MS_USER,user.toString()));
        
        reason=new TextFieldCombo(SR.MS_REASON, "", 256, TextField.ANY, "reason", display);
        f.append(reason);
        
        cmdOk=new Command( okName, Command.SCREEN, 1);
        f.addCommand(cmdOk);
        f.addCommand(cmdNoReason);
        f.addCommand(cmdCancel);
        f.setCommandListener(this);
        user=null;
        display.setCurrent(f);
    }

    public void commandAction(Command command, Displayable displayable) {
        if (command==cmdNoReason) { 
            reason.setString("");
            return;
        }
        
        if (command==cmdOk) setMucMod(); 
        display.setCurrent(StaticData.getInstance().roster);
    }
    
    private void setMucMod(){
        JabberDataBlock iq=new Iq(victim.jid.getBareJid(), Iq.TYPE_SET, "itemmuc");
        JabberDataBlock query=iq.addChildNs("query", "http://jabber.org/protocol/muc#admin"); 
        //TODO: separate usecases to muc#owner, muc#admin and muc#moderator
        JabberDataBlock item=new JabberDataBlock("item", null, null);
        query.addChild(item);

        try {
            String rzn=reason.getString();
            String Nick="";
            if (rzn.startsWith("!")) {
                rzn=rzn.substring(1);
            } else {
                Nick=(myNick==null)?myNick:myNick+": ";                
            }
            if (rzn.length()!=0 && myNick!=null) {
               item.addChild("reason",Nick+rzn);
            } else {
                item.addChild("reason", Nick);
            }
            
            
        } catch (Exception e) {}
        
        switch (action) {
            case KICK: 
                item.setAttribute("role", "none");
                item.setAttribute("nick", victim.nick);
                break;

            case OUTCAST:
                item.setAttribute("affiliation", "outcast");
                item.setAttribute("jid", victim.realJid);
                break;
                
            case PARTICIPANT:
                item.setAttribute("role", "participant");
                item.setAttribute("nick", victim.nick);
                break;
                
            case VISITOR:
                item.setAttribute("role", "visitor");
                item.setAttribute("nick", victim.nick);
                break;
                
            case MODERATOR:
                item.setAttribute("role", "moderator");
                item.setAttribute("nick", victim.nick);
                break;
                
            case MEMBER:
                item.setAttribute("affiliation", "member");
                item.setAttribute("jid", victim.realJid);
                break;
                
            case NONE:
                item.setAttribute("affiliation", "none");
                item.setAttribute("jid", victim.realJid);
                break;
                
            case ADMIN:
                item.setAttribute("affiliation", "admin");
                item.setAttribute("jid", victim.realJid);
                break;
                
            case OWNER:
                item.setAttribute("affiliation", "owner");
                item.setAttribute("jid", victim.realJid);

        }
        //System.out.println(iq);
        StaticData.getInstance().roster.theStream.send(iq);
    }
}
