/*
 * ChangeTransport.java
 *
 * Copyright (c) 2006-2007, Daniel Apatin (ad), http://apatin.net.ru
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

import java.util.Enumeration;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import locale.SR;

public class ChangeTransport implements CommandListener{

    private Display display;
    private Form f;
    private ChoiceGroup tTranspList;
    private int transportIndex;
    
    private Command cmdOk=new Command(SR.MS_OK, Command.SCREEN, 1);
    private Command cmdCancel=new Command(SR.MS_CANCEL, Command.BACK, 99);

    Roster roster=StaticData.getInstance().roster;

    public ChangeTransport(Display display, int transportIndex) {
        this.transportIndex=transportIndex;
        this.display=display;
        
        StaticData sd=StaticData.getInstance();
        roster=sd.roster;
        
        f=new Form(SR.MS_NEWGROUP);

        tTranspList=new ChoiceGroup(SR.MS_TRANSPORT, ChoiceGroup.POPUP);
        
        tTranspList.append(sd.account.getServer(), null);
        for (Enumeration e=sd.roster.getHContacts().elements(); e.hasMoreElements(); ){
            Contact ct=(Contact)e.nextElement();
            Jid transpJid=ct.jid;
            if (transpJid.isTransport()) 
                tTranspList.append(transpJid.getBareJid(),null);
        }
        f.append(tTranspList);
        
        f.addCommand(cmdOk);
        f.addCommand(cmdCancel);
        
        f.setCommandListener(this);
        
        display.setCurrent(f);
    }

    public void commandAction(Command command, Displayable displayable) {
        if (command==cmdOk) {
//#if CHANGE_TRANSPORT            
//#             roster.contactChangeTransport(transportIndex, tTranspList.getString(tTranspList.getSelectedIndex()));
//#endif
        }
        display.setCurrent(StaticData.getInstance().roster);
    }
}
