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

import Menu.MenuCommand;
import java.util.Enumeration;
import ui.MainBar;
import ui.VirtualList;
import ui.controls.form.DefForm;

public class UserKeysList extends DefForm {

//#ifdef PLUGINS
//#     public static String plugin = new String("PLUGIN_USER_KEYS");
//#endif
    
//#ifdef USER_KEYS
//#     MenuCommand cmdApply = new MenuCommand(SR.MS_APPLY, MenuCommand.OK, 1);
//#     MenuCommand cmdAdd = new MenuCommand(SR.MS_ADD_CUSTOM_KEY, MenuCommand.SCREEN, 2);
//#     MenuCommand cmdEdit = new MenuCommand(SR.MS_EDIT, MenuCommand.SCREEN, 3);
//#     MenuCommand cmdDel = new MenuCommand(SR.MS_DELETE, MenuCommand.SCREEN, 4);
//#endif

    private Config cf = Config.getInstance();
    
    /** Creates a new instance of AccountPicker */
    public UserKeysList() {
        super(null);
//#ifdef USER_KEYS
//#         setMainBarItem(new MainBar(SR.MS_CUSTOM_KEYS));
//#endif
        
        UserKeyExec uexec = UserKeyExec.getInstance();
        itemsList = copyVector(uexec.keysList);
        
    }

    private Vector copyVector(Vector v1) {
        int size = v1.size();
        Vector v2 = new Vector(size);
        for (Enumeration e = v1.elements(); e.hasMoreElements();) {
            v2.addElement(new UserKey((UserKey) e.nextElement()));
        }
        return v2;
    }

    public void commandState() {
//#ifdef USER_KEYS
//#       menuCommands.removeAllElements();
//#         addMenuCommand(cmdAdd);
//#         if (itemsList.isEmpty()) {
//#             removeMenuCommand(cmdEdit);
//#             removeMenuCommand(cmdDel);
//#         } else {
//#             addMenuCommand(cmdEdit);
//#             addMenuCommand(cmdDel);
//#         }
//#         addMenuCommand(cmdApply);
//#endif
        }
    
    public void cmdOk() {
       UserKeyExec.getInstance().keysList = itemsList;
       UserKeyExec.getInstance().rmsUpdate();
       destroyView();
    }

    public void menuAction(MenuCommand c, VirtualList d) {
//#ifdef USER_KEYS
//#         if (c==cmdEdit) 
//#             new UserKeyEdit( this, (UserKey) getFocusedObject());
//#         if (c==cmdAdd)
//#             new UserKeyEdit( this, null);
//#         if (c==cmdDel) {
//#             itemsList.removeElement(getFocusedObject());
//#             moveCursorHome();
//#             commandState();
//#             redraw();
//#         }
//#         if (c==cmdApply) {
//#             cmdOk();
//#         }
//#endif
        super.menuAction(c, d);
    }
    
    public void eventOk() {
//#ifdef USER_KEYS
//#         new UserKeyEdit( this, (UserKey) getFocusedObject());
//#endif
    }
    
     public String touchLeftCommand() {return SR.MS_MENU;}
     public void touchLeftPressed() { showMenu(); }

}
