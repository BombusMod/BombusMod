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
//# import javax.microedition.lcdui.CommandListener;
//# import javax.microedition.lcdui.Command;
//#else
import Menu.MenuListener;
import Menu.Command;
import Menu.MyMenu;
//#endif
import java.util.Enumeration;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import util.StringLoader;

public class UserKeysList extends VirtualList implements
//#ifndef MENU_LISTENER
//#         CommandListener
//#else
        MenuListener
//#endif
    {
//#ifdef PLUGINS
//#     public static String plugin = new String("PLUGIN_USER_KEYS");
//#endif

    Vector userKeysList;

//#ifdef USER_KEYS
//#     Command cmdOK = new Command(SR.MS_APPLY, Command.OK, 1);
//#     Command cmdAdd = new Command(SR.MS_ADD_CUSTOM_KEY, Command.SCREEN, 3);
//#     Command cmdEdit = new Command(SR.MS_EDIT, Command.ITEM, 3);
//#     Command cmdDel = new Command(SR.MS_DELETE, Command.ITEM, 4);
//#     Command cmdRestore = new Command(SR.MS_SETDEFAULT, Command.ITEM, 5);
//#     Command cmdCancel = new Command(SR.MS_BACK, Command.BACK, 99);
//#endif

    private Config cf=Config.getInstance();
    
    /** Creates a new instance of AccountPicker */
    public UserKeysList(Display display) {
        super();
//#ifdef USER_KEYS
//#         setMainBarItem(new MainBar(SR.MS_CUSTOM_KEYS));
//#endif
        
        UserKeyExec uexec = UserKeyExec.getInstance();
        uexec.init_commands_from_rms();
        userKeysList = copyVector(uexec.userKeysList);

        commandState();
        setCommandListener(this);
        
        attachDisplay(display);
    }

    private Vector copyVector(Vector v1) {
        int size = v1.size();
        Vector v2 = new Vector(size);
        for (Enumeration e = v1.elements(); e.hasMoreElements();) {
            v2.addElement(new UserKey((UserKey) e.nextElement()));
        }
        return v2;
    }

    public static Vector getDefaultKeysList() {
        Vector defKeysList = new Vector();
        Vector defs[] = new StringLoader().stringLoader("/def_keys.txt", 8);
        for (int i = 0; i < defs[0].size(); i++) {
            int[] commands_id = {
                Integer.parseInt((String) defs[0].elementAt(i)),
                Integer.parseInt((String) defs[1].elementAt(i)),
                Integer.parseInt((String) defs[2].elementAt(i))//,
                //Integer.parseInt((String) defs[3].elementAt(i))
            };
            int previous_key_code = UserKey.get_key_code_by_id(Integer.parseInt((String) defs[4].elementAt(i)));
            int key_code = UserKey.get_key_code_by_id(Integer.parseInt((String) defs[5].elementAt(i)));
            boolean active = ((String) defs[6].elementAt(i)).equals("y");
            boolean two_keys = ((String) defs[7].elementAt(i)).equals("y");
            defKeysList.addElement(new UserKey(commands_id, previous_key_code, key_code, active, two_keys));
        }
        return defKeysList;
    }

    private void restoreDefault() {
        userKeysList = getDefaultKeysList();
    }

    void commandState() {
//#ifdef USER_KEYS
//#ifdef MENU_LISTENER
//#         menuCommands.removeAllElements();
//#endif
//#         addCommand(cmdAdd);
//#         if (userKeysList.isEmpty()) {
//#             removeCommand(cmdEdit);
//#             removeCommand(cmdDel);
//#         } else {
//#             addCommand(cmdEdit);
//#             addCommand(cmdDel);
//#         }
//#         addCommand(cmdRestore);
//#         addCommand(cmdOK);
//#         addCommand(cmdCancel);
//#endif
    }

    public VirtualElement getItemRef(int index) {
        return (VirtualElement) userKeysList.elementAt(index);
    }
    
    protected int getItemCount() {
        return userKeysList.size();
    }
    
    public void commandAction(Command c, Displayable d) {
//#ifdef USER_KEYS
//#         if (c==cmdCancel) {
//#             destroyView();
//#         }
//#         if (c==cmdOK) {
//#             UserKeyExec.getInstance().userKeysList = userKeysList;
//#             rmsUpdate();
//#             destroyView();    
//#         }
//#         if (c==cmdRestore) {
//#             restoreDefault();
//#             moveCursorHome();
//#             commandState();
//#             redraw();
//#         }
//#         if (c==cmdEdit) 
//#             new UserKeyEdit(display, this, (UserKey) getFocusedObject());
//#         if (c==cmdAdd)
//#             new UserKeyEdit(display, this, null);
//#         if (c==cmdDel) {
//#             userKeysList.removeElement(getFocusedObject());
//#             moveCursorHome();
//#             commandState();
//#             redraw();
//#         }
//#endif
    }
    
    public void eventOk() {
        new UserKeyEdit(display, this, (UserKey) getFocusedObject());
    }
    
    public static void rmsUpdate(Vector keysList) {
        DataOutputStream outputStream=NvStorage.CreateDataOutputStream();

        int size = keysList.size();
        try {
            outputStream.writeInt(size);
        } catch (Exception e) { return; }

        for (int i=0;i<size;i++) {
            ((UserKey) keysList.elementAt(i)).saveToDataOutputStream(outputStream);
        }
        
        NvStorage.writeFileRecord(outputStream, UserKey.storage, 0, true);
    }
    
    void rmsUpdate() {
        rmsUpdate(userKeysList);
    }
    
//#if (MENU_LISTENER && USER_KEYS)
//#     public void showMenu() {
//#         commandState();
//#         new MyMenu(display, parentView, this, SR.MS_CUSTOM_KEYS, null, menuCommands);
//#     }
//#endif
}
