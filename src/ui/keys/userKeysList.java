/*
 * userKeysList.java
 *
 * Created on 14.09.2007, 10:11
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

package ui.keys;

import Client.Config;
import io.NvStorage;
import java.io.DataOutputStream;
import java.util.Vector;
import locale.SR;
import ui.MainBar;
import ui.VirtualElement;
import ui.VirtualList;

//#ifndef MENU_LISTENER
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Command;
//#else
//# import Menu.MenuListener;
//# import Menu.Command;
//# import Menu.MyMenu;
//#endif
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;

public class userKeysList
        extends VirtualList 
        implements
//#ifndef MENU_LISTENER
        CommandListener
//#else
//#         MenuListener
//#endif
    {

    public static String plugin = new String("PLUGIN_USER_KEYS");

    Vector commandsList;
    
    Command cmdOK=new Command(SR.MS_OK, Command.OK,1);
    Command cmdAdd=new Command(SR.MS_ADD_CUSTOM_KEY, Command.SCREEN,3);
    Command cmdEdit=new Command(SR.MS_EDIT,Command.ITEM,3);
    Command cmdDel=new Command(SR.MS_DELETE,Command.ITEM,4);
    Command cmdCancel=new Command(SR.MS_BACK,Command.BACK,99);
    
    private Config cf=Config.getInstance();
    
    /** Creates a new instance of AccountPicker */
    public userKeysList(Display display) {
        super();
        setMainBarItem(new MainBar(SR.MS_CUSTOM_KEYS));
        
        commandsList=userKeyExec.getInstance().commandsList;

        commandState();
        setCommandListener(this);
        
        attachDisplay(display);
    }

    void commandState(){
//#ifdef MENU_LISTENER
//#         menuCommands.removeAllElements();
//#endif
        addCommand(cmdAdd);
        if (commandsList.isEmpty()) {
            removeCommand(cmdEdit);
            removeCommand(cmdDel);
        } else {
            addCommand(cmdEdit);
            addCommand(cmdDel);
        }
        addCommand(cmdOK);
        addCommand(cmdCancel);
    }

    public VirtualElement getItemRef(int Index) { 
        return (VirtualElement)commandsList.elementAt(Index); 
    }
    
    protected int getItemCount() {
        return commandsList.size();
    }
    
    public void commandAction(Command c, Displayable d){
        if (c==cmdCancel) {
            destroyView();
        }
        if (c==cmdOK) {
            rmsUpdate();
            destroyView();    
        }
        if (c==cmdEdit) 
            new userKeyEdit(display, this, this, (userKey)getFocusedObject());
        if (c==cmdAdd)
            new userKeyEdit(display, this, this, null);
        if (c==cmdDel) {
            userKeyExec.getInstance().commandsList.removeElement(getFocusedObject());
            
            rmsUpdate();
            moveCursorHome();
            commandState();
            redraw();
        }
    }
    
    public void eventOk(){
        new userKeyEdit(display, parentView, this, (userKey)getFocusedObject());
    }
    
    void rmsUpdate(){
        DataOutputStream outputStream=NvStorage.CreateDataOutputStream();
        
        for (int i=0;i<commandsList.size();i++) {
            ((userKey)commandsList.elementAt(i)).saveToDataOutputStream(outputStream);
        }
        
        NvStorage.writeFileRecord(outputStream, userKey.storage, 0, true);
    }
    
//#ifdef MENU_LISTENER
//#     public void showMenu() {
//#         commandState();
//#         new MyMenu(display, parentView, this, SR.MS_CUSTOM_KEYS, null, menuCommands);
//#     }
//#endif
}
