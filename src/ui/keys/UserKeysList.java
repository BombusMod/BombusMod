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

//#ifdef USER_KEYS
import locale.SR;
import Menu.MenuCommand;
import ui.VirtualList;
import ui.controls.form.DefForm;
import java.util.Vector;
import ui.VirtualElement;

public class UserKeysList extends DefForm {

    MenuCommand cmdApply = new MenuCommand(SR.MS_APPLY, MenuCommand.OK, 1);
    MenuCommand cmdAdd = new MenuCommand(SR.MS_ADD, MenuCommand.SCREEN, 2);
    MenuCommand cmdEdit = new MenuCommand(SR.MS_EDIT, MenuCommand.SCREEN, 3);
    MenuCommand cmdDel = new MenuCommand(SR.MS_DELETE, MenuCommand.SCREEN, 4);
    KeyScheme keyScheme;

    public UserKeysList() {
        super(SR.MS_CUSTOM_KEYS);
        enableListWrapping(true);
        keyScheme = new KeyScheme(UserKeyExec.getInstance().keyScheme);
//        redraw(); TODO: Need?
        commandState();
    }

    public VirtualElement getItemRef(int index) {
        if (index >= getItemCount()) {
            return null;
        }
        return (VirtualElement) keyScheme.getFullKeysList().elementAt(index);
    }

    public int getItemCount() {
       return keyScheme.getFullSize();
    }

    public void commandState() {
        menuCommands.removeAllElements();
        addMenuCommand(cmdAdd);
        if (keyScheme.getFullSize() > 0) {
            addMenuCommand(cmdEdit);
            addMenuCommand(cmdDel);
        } else {
            removeMenuCommand(cmdEdit);
            removeMenuCommand(cmdDel);
        }
        addMenuCommand(cmdApply);
    }

    public void cmdOk() {
       UserKeyExec uexec = UserKeyExec.getInstance();
       uexec.keyScheme = keyScheme;
       IE.UserKeys.rmsUpdate(keyScheme);
       destroyView();
    }

    public void menuAction(MenuCommand c, VirtualList d) {
        if (c == cmdEdit) {
            new UserKeyEdit(this, (UserKey) getFocusedObject(), cursor == 0);
        }
        if (c == cmdAdd) {
            new UserKeyEdit(this, null, false);
        }
        if (c == cmdDel && cursor > 0) {
            keyScheme.getKeysList().removeElementAt(cursor);
            moveCursorHome();
            commandState();
            redraw();
        }
        if (c == cmdApply) {
            cmdOk();
        }
        super.menuAction(c, d);
    }

    public void eventOk() {
        new UserKeyEdit(this, (UserKey) getFocusedObject(), cursor == 0);
    }

     public String touchLeftCommand() { return SR.MS_MENU; }
     public void touchLeftPressed() { showMenu(); }

}
//#endif
