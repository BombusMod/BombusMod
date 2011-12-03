/*
 * AccountSelect.java
 *
 * Created on 19.03.2005, 23:26
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
package Account;

import Client.*;
import locale.SR;
import midlet.BombusMod;
import ui.*;
import java.io.*;
import Menu.MenuCommand;
import io.NvStorage;
import ui.controls.AlertBox;
import ui.controls.form.DefForm;
import images.RosterIcons;

/**
 *
 * @author Eugene Stahov
 */
public class AccountSelect extends DefForm {

    int activeAccount;
    boolean enableQuit;
    MenuCommand cmdLogin = new MenuCommand(SR.MS_SELLOGIN, MenuCommand.OK, 1, RosterIcons.ICON_ON);
    MenuCommand cmdSelect = new MenuCommand(SR.MS_DEFAULT, MenuCommand.SCREEN, 2, RosterIcons.ICON_KEYBLOCK_INDEX);
    MenuCommand cmdAdd = new MenuCommand(SR.MS_NEW_ACCOUNT, MenuCommand.SCREEN, 3, RosterIcons.ICON_REGISTER_INDEX);
    MenuCommand cmdEdit = new MenuCommand(SR.MS_EDIT, MenuCommand.ITEM, 3, RosterIcons.ICON_RENAME);
    MenuCommand cmdDel = new MenuCommand(SR.MS_DELETE, MenuCommand.ITEM, 4, RosterIcons.ICON_DELETE);
    MenuCommand cmdConfig = new MenuCommand(SR.MS_OPTIONS, MenuCommand.ITEM, 5, RosterIcons.ICON_CONFIGURE);
    MenuCommand cmdQuit = new MenuCommand(SR.MS_APP_QUIT, MenuCommand.SCREEN, 10, RosterIcons.ICON_CANCEL);

    /** Creates a new instance of AccountPicker */
    public AccountSelect(boolean enableQuit) {
        super(SR.MS_ACCOUNTS, false);
        this.enableQuit = enableQuit;

        enableListWrapping(true);
        cf = Config.getInstance();

        if (enableQuit) {
            canBack = false;
        }

        loadAccounts();
    }

    public final void loadAccounts() {
        Account a;
        int index = 0;
        itemsList.removeAllElements();
        activeAccount = cf.accountIndex;
        do {
            a = Account.createFromStorage(index);
            if (a != null) {
                a.setActive(activeAccount == index);
                itemsList.addElement(new AccountItem(a));
                index++;
            }
        } while (a != null);
        if (!itemsList.isEmpty()) {
            moveCursorTo(activeAccount);
        }
    }

    public final void commandState() {
        menuCommands.removeAllElements();
        if ((itemsList != null) && !itemsList.isEmpty()) {
            addMenuCommand(cmdLogin);
            addMenuCommand(cmdSelect);

            addMenuCommand(cmdEdit);
            addMenuCommand(cmdDel);
        }
        addMenuCommand(cmdAdd);
        addMenuCommand(cmdConfig);
        if (enableQuit) {
            addMenuCommand(cmdQuit);
        }
    }

    public void menuAction(MenuCommand c, VirtualList d) {
        if (c == cmdQuit) {
            destroyView();
            BombusMod.getInstance().notifyDestroyed();
            return;
        }
        if (c == cmdConfig) {
            new ConfigForm();
        }
        if (c == cmdLogin) {
            switchAccount(true);
        }
        if (c == cmdSelect) {
            canBack = true;
            switchAccount(false);
        }
        if (c == cmdEdit) {
            new AccountForm(this, ((AccountItem) getFocusedObject()).account).show();
        }
        if (c == cmdAdd) {
            new AccountForm(this, null).show();
        }
        if (c == cmdDel) {
            if (cursor == cf.accountIndex && StaticData.getInstance().roster.isLoggedIn()) {
                return;
            }
            //if (((Account)getFocusedObject()).equals(StaticData.getInstance().account)) return;

            new AlertBox(SR.MS_DELETE, getFocusedObject().toString()) {

                public void yes() {
                    delAccount();
                }

                public void no() {
                }
            };
        }
        super.menuAction(c, d);
    }

    public void destroyView() {
        if (itemsList.size() > 0) {
            if (StaticData.getInstance().account == null) {
                Account.loadAccount(false, cf.accountIndex);
            }
            parentView = sd.roster;
            super.destroyView();
        }
    }

    private void delAccount() {
        if (itemsList.size() == 1) {
            cf.accountIndex = -1;
        } else if (cf.accountIndex > cursor) {
            cf.accountIndex--;
        }

        cf.saveToStorage();

        itemsList.removeElement(getFocusedObject());
        rmsUpdate();
        moveCursorHome();
        commandState();
        redraw();
    }

    private void switchAccount(boolean login) {
        cf.accountIndex = cursor;
        cf.saveToStorage();
        loadAccounts();
        destroyView();
        Account.loadAccount(login, cursor);
    }

    public void eventOk() {
        if (getItemCount() > 0) {
            canBack = true;
            switchAccount(true);
        }
    }

    public void rmsUpdate() {
        DataOutputStream outputStream = NvStorage.CreateDataOutputStream();
        int j = itemsList.size();
        for (int i = 0; i < j; i++) {
            ((Account) itemsList.elementAt(i)).saveToDataOutputStream(outputStream);
        }
        NvStorage.writeFileRecord(outputStream, "accnt_db", 0, true); //Account.storage
    }
}
