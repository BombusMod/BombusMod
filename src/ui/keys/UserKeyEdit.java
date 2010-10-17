/*
 * userKeyEdit.java
 *
 * Created on 14.09.2007, 11:01
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
import java.util.Enumeration;
import locale.SR;
import ui.VirtualCanvas;
import ui.controls.form.CheckBox;
import ui.controls.form.DefForm;
import ui.controls.form.DropChoiceBox;
import ui.controls.form.KeyInput;
import ui.controls.form.SpacerItem;

/**
 *
 * @author ad
 */
class UserKeyEdit extends DefForm {
//#ifdef PLUGINS
//#     public static String plugin = new String("PLUGIN_USER_KEYS");
//#endif

    private final UserKeysList keysList;

    private CheckBox active;
    private CheckBox two_keys_t;
    private DropChoiceBox[] commands_t = {
        new DropChoiceBox("Common"),
        new DropChoiceBox("Roster"),
        new DropChoiceBox("ContactMessageList")
    };
    private KeyInput key_t;

    UserKey u;
    
    boolean newKey;

    public UserKeyEdit(UserKeysList keysList, UserKey u) {
        super((u==null)?
//#ifdef USER_KEYS
//#             SR.MS_ADD_CUSTOM_KEY:
//#else
                "":
//#endif
                (u.toString()));
        
	this.keysList = keysList;
	
	newKey=(u==null);
	if (newKey) u=new UserKey();
	this.u=u;

//#ifdef USER_KEYS
//#         active=new CheckBox(SR.MS_ENABLED, u.active);
//#         itemsList.addElement(active);
//# 
//#         for (int i = 0; i < 3; i++) {
//#             commands_t[i].add(UserKeyExec.none_command.description);
//#             for (Enumeration e = UserKeyExec.available_commands[i].elements(); e.hasMoreElements();) {
//#                 commands_t[i].add(((UserKeyCommand) e.nextElement()).description);
//#             }
//#             commands_t[i].setSelectedIndex(UserKeyExec.available_commands[i].indexOf(new UserKeyCommand(u.commands_id[i], null)) + 1);
//#             itemsList.addElement(commands_t[i]);
//#         }
//# 
//#         two_keys_t = new CheckBox("Two keys", u.two_keys);
//#         itemsList.addElement(two_keys_t);
//# 
//#         itemsList.addElement(new SpacerItem(10));
//# 
//#         key_t = new KeyInput(u, "Press it");
//#         itemsList.addElement(key_t);
//#endif
        
        moveCursorTo(getNextSelectableRef(-1));
        
        parentView = keysList;
    }
    
    public void cmdOk() {
        u.active=active.getValue();
        for (int i = 0; i < 3; i++) {
            int index = commands_t[i].getSelectedIndex() - 1;
            if (index >= 0) {
            u.commands_id[i] = ((UserKeyCommand) UserKeyExec.available_commands[i].elementAt(index)).command_id;
            } else u.commands_id[i] = UserKeyExec.none_command.command_id;
        }

        u.previous_key = key_t.previous_key;
        u.key = key_t.key;
        u.two_keys = two_keys_t.getValue();

        if (newKey) {
            keysList.itemsList.addElement(u);
        }

        //keysList.rmsUpdate();
        keysList.commandState();
        sd.canvas.show(keysList);
    }

    public void eventOk() {
        super.eventOk();
        key_t.two_keys = two_keys_t.getValue();
    }

    public void keyPressed(int keyCode) {
        if (key_t.selected) {
            key_t.keyPressed(keyCode);
            redraw();
            return;
        } else super.keyPressed(keyCode);
    }
}
