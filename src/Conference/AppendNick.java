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

import com.alsutton.jabber.datablocks.Presence;
import locale.SR;
import ui.*;
import Client.*;
import java.util.*;
import javax.microedition.lcdui.*;
import ui.MainBar;

/**
 *
 * @author EvgS
 */
public class AppendNick         
        extends VirtualList 
        implements CommandListener{

    Vector nicknames;
    int caretPos; 
    
    Command cmdSelect=new Command(SR.MS_APPEND, Command.OK, 1);
    Command cmdCancel=new Command(SR.MS_CANCEL, Command.BACK, 99);

    private TextBox t;
    
    public AppendNick(Display display, Contact to, int caretPos, TextBox t) {
        super(display);
        this.caretPos=caretPos;
        
        this.t=t;
        
        setMainBarItem(new MainBar(SR.MS_SELECT_NICKNAME));
        
        nicknames=null;
        nicknames=new Vector();
        for (Enumeration e=StaticData.getInstance().roster.getHContacts().elements(); e.hasMoreElements(); ) {
            Contact c=(Contact)e.nextElement();
            if (c.inGroup(to.group) && c.origin>Contact.ORIGIN_GROUPCHAT && c.status<Presence.PRESENCE_OFFLINE)
                nicknames.addElement(c);
        }

        addCommand(cmdSelect);
        addCommand(cmdCancel);
        
        setCommandListener(this);
    }
    
    public VirtualElement getItemRef(int Index) { return (VirtualElement)nicknames.elementAt(Index); }
    protected int getItemCount() { return nicknames.size();  }

    public void commandAction(Command c, Displayable d){
        if (c==cmdSelect) eventOk();
        destroyView();
    }
     public void eventOk(){
         try {
             String nick=((Contact)getFocusedObject()).getJid();
             int rp=nick.indexOf('/');
             StringBuffer b=new StringBuffer(nick.substring(rp+1));
             
            if (caretPos==0) b.append(':');
//#ifdef RUNNING_MESSAGE
//#             StaticData.getInstance().roster.me.insert(b.toString(), caretPos);
//#else
            t.insert(b.toString(), caretPos);
//#endif
            b=null;
         } catch (Exception e) {}
    }

}
