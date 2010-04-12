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
import ui.MainBar;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import com.alsutton.jabber.datablocks.Presence;
import java.util.Enumeration;
import java.util.Vector;
import ui.VirtualElement;
import ui.VirtualList;

//#ifndef MENU_LISTENER
//# import javax.microedition.lcdui.CommandListener;
//# import javax.microedition.lcdui.Command;
//#else
import Menu.MenuListener;
import Menu.Command;
//#endif

/**
 *
 * @author EvgS
 */
public final class AppendNick
        extends VirtualList 
        implements
//#ifndef MENU_LISTENER
//#         CommandListener
//#else
        MenuListener
//#endif
{

    Vector nicknames;
    int caretPos; 
    
    Command cmdOk=new Command(SR.MS_APPEND, Command.OK, 1);
    Command cmdCancel=new Command(SR.MS_CANCEL, Command.BACK, 99);

    private MessageEdit me;
    
    public AppendNick(Display display, Displayable pView, Contact to, int caretPos, MessageEdit me) {
        super(display);
        this.caretPos=caretPos;
        
        this.me = me;
        
        setMainBarItem(new MainBar(SR.MS_SELECT_NICKNAME));
        
        nicknames=null;
        nicknames=new Vector();
        for (Enumeration e=StaticData.getInstance().roster.getHContacts().elements(); e.hasMoreElements(); ) {
            Contact c=(Contact)e.nextElement();
            if (c.group==to.group && c.origin>Contact.ORIGIN_GROUPCHAT && c.status<Presence.PRESENCE_OFFLINE)
                nicknames.addElement(c);
        }
        commandState();

        this.parentView=pView;
    }
    
    public void commandState() {
//#ifdef MENU_LISTENER
        menuCommands.removeAllElements();
//#endif
        addCommand(cmdOk);
        addCommand(cmdCancel);
        
        setCommandListener(this);
    }
    
    public VirtualElement getItemRef(int Index) { return (VirtualElement)nicknames.elementAt(Index); }
    protected int getItemCount() { return nicknames.size();  }

    public void commandAction(Command c, Displayable d){
        if (c==cmdOk)
            eventOk();
        else
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
            me.insert(b.toString(), caretPos);
//#endif
            b=null;
         } catch (Exception e) {}
         destroyView();
    }
     
//#ifdef MENU_LISTENER
    public void showMenu(){ eventOk(); }
     
    public String touchLeftCommand(){ return SR.MS_SELECT; }
    public String touchRightCommand(){ return SR.MS_BACK; }
//#endif
}
