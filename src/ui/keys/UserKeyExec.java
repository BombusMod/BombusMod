/*
 * userKeyExecute.java
 *
 * Created on 14.09.2007, 13:38
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

//#ifdef USER_KEYS

package ui.keys;

import Client.Config;
import Client.ConfigForm;
import Client.StaticData;
//#ifdef STATS
import Statistic.StatsWindow;
//#endif
import Colors.ColorTheme;
//#ifdef CONSOLE
import Console.XMLList;
//#endif
//#ifdef PRIVACY
import PrivacyLists.PrivacySelect;
//#endif
//#ifdef SERVICE_DISCOVERY
import ServiceDiscovery.*;
//#endif
import java.util.Vector;
import locale.SR;
import midlet.BombusMod;
import ui.VirtualList;
//#ifdef JUICK
import Client.Contact;
//#endif
import Client.ContactMessageList;
import Client.Roster;
import Messages.MessageList;
import Messages.MessageItem;
import Messages.MessageUrl;
import Conference.Bookmarks;
import Client.ActiveContacts;
import Menu.RosterToolsMenu;
import ui.SplashScreen;
import javax.microedition.lcdui.Displayable;
import PrivacyLists.PrivacyModifyList;
import PrivacyLists.PrivacySelect;
import Archive.ArchiveList;
import History.HistoryReader;
import ui.VirtualCanvas;

public class UserKeyExec {

    private static UserKeyExec instance;
    private StaticData sd = StaticData.getInstance();
    private int previousKeyCode;
    public final static String cmds[] = new String[60];
    public KeyScheme keyScheme;

    // true: keyExecute return true;
    // false: keyExecute return false or not called;
    private boolean executed;

    public static UserKeyExec getInstance() {
        if (instance == null)
            instance = new UserKeyExec();
        return instance;
    }

    private UserKeyExec() {
        init_cmds();
        final KeyScheme keysFromStorage = IE.UserKeys.loadFromStorage();
        if (keysFromStorage != null) {
            keyScheme = keysFromStorage;
        } else {
            keyScheme = IE.UserKeys.loadFromFile(UserKey.def_keys, true);
            IE.UserKeys.rmsUpdate(keyScheme);
        }
        executed = false;
    }

    private void init_cmds() {
        cmds[0] = SR.MS_NO;
        cmds[1] = "[Roster] " + SR.MS_OPTIONS;
        cmds[2] = SR.MS_CLEAN_ALL_MESSAGES;
        cmds[3] = SR.MS_RECONNECT;
//#ifdef STATS
        cmds[4] = "[Roster] " + SR.MS_STATS;
//#endif
        cmds[5] = SR.MS_STATUS_MENU;
        cmds[6] = SR.MS_FILE_TRANSFERS;
//#ifdef ARCHIVE
        cmds[7] = SR.MS_ARCHIVE;
//#endif
        cmds[8] = SR.MS_DISCO;
//#ifdef PRIVACY
        cmds[9] = SR.MS_PRIVACY_LISTS;
//#endif
//#ifdef USER_KEYS
        cmds[10] = SR.MS_CUSTOM_KEYS;
//#endif
//#ifdef POPUPS
        cmds[11] = SR.MS_CLEAR_POPUPS;
//#endif
      //  cmds[12] = SR.MS_FLASHLIGHT;
        cmds[13] = SR.MS_ABOUT;
        cmds[14] = SR.MS_APP_MINIMIZE;
        cmds[15] = SR.MS_INVERT;
//#ifdef CONSOLE
        cmds[16] = SR.MS_XML_CONSOLE;
//#endif
        cmds[17] = SR.MS_FULLSCREEN;
//#ifdef JUICK
        cmds[18] = SR.MS_JUICK_FOCUS;
//#endif
        cmds[19] = SR.MS_HEAP_MONITOR;
//#ifdef SMILES
        cmds[20] = SR.MS_SMILES_TOGGLE;
//#endif
//#ifdef JUICK
        cmds[21] = SR.MS_COMMANDS + " Juick";
//#endif
        cmds[22] = "Move cursor home";
        cmds[23] = "Move cursor end";
        cmds[24] = "Move cursor next";
        cmds[25] = "Move cursor previous";
        cmds[26] = "Move cursor left";
        cmds[27] = "Move cursor right";
        cmds[28] = "Go to previous window";
        cmds[29] = "Delete current item";
        cmds[30] = "[Chat] " + "Quote";
        cmds[31] = "[Chat] " + "Active contacts";
        cmds[32] = "[Roster] " + SR.MS_BOOKMARKS;
        cmds[33] = "[Roster] Collapse all group";
        cmds[34] = "Show info";
        cmds[35] = "Action Ok";
        cmds[36] = "Left_Soft std action";
        cmds[37] = "Right_Soft stc action";
        cmds[38] = "[Roster] " + "Show offline contacts";
        cmds[39] = "[Roster] " + SR.MS_TOOLS;
        cmds[40] = "[Roster] " + SR.MS_APP_MINIMIZE;
        cmds[41] = "[Roster + ActiveContacts] " + "Focus to next unreaded";
        cmds[42] = "[Roster] " + "Previous group";
        cmds[43] = "[Roster] " + "Next group";
        cmds[44] = "Block keyboard";
        cmds[45] = "[Roster] " + "Vibra/Sound";
        cmds[46] = "Moto backlight";
        cmds[47] = "[Roster] " + "Active contacts";
        cmds[48] = "[Chat] " + "Previous contact with messages";
        cmds[49] = "[Chat] " + "Next contact with messages";
        cmds[50] = "[Chat + XMLList + ActiveContacts] " + SR.MS_CLEAR_LIST;
        cmds[51] = "[Chat] " + SR.MS_REPLY;
        cmds[52] = "[Chat + Roster + ActiveContacts + XMLList + MessageUrl] " + SR.MS_RESUME;
        cmds[53] = "["+SR.MS_ARCHIVE+"] " + SR.MS_PASTE_BODY;
        cmds[54] = "["+SR.MS_PRIVACY_LISTS+"] " + "Add new item";
        cmds[55] = "["+SR.MS_HISTORY+"] " + "Begin of file";
        cmds[56] = "["+SR.MS_HISTORY+"] " + "End of file";
        cmds[57] = "["+SR.MS_BOOKMARKS+"] " + SR.MS_DISCO_ROOM;
        cmds[58] = "[Roster] " + "Kick from groupchat";
        cmds[59] = "[Chat] " + "focus to next hightlited message";
    }

    public static int getCommandID(String str) {
        for (int i = 0; i < cmds.length; i++) {
            if ((cmds[i] != null) && str.equals(cmds[i]))
                return i;
        }
        return 0;
    }

    public void afterActions(int keyCode) {
        if (executed) {
            previousKeyCode = -1; // Авось, нет клавиши с таким кодом...
        } else {
            previousKeyCode = keyCode;
        }
        executed = false;
    }

    public boolean keyExecute(int key, boolean onlyWithModificator) { // return false if key not executed
        executed = false;

        final VirtualList current = VirtualCanvas.getInstance().getList();
        if (current instanceof UserKeyEdit) {
            if (((UserKeyEdit) current).key(key)) {
                executed = true;
                return executed;
            }
        }

        Vector keysList = keyScheme.getKeysList();
        int modificatorCode = keyScheme.getModificator().key;
        int size = keyScheme.getSize();

        if (previousKeyCode == modificatorCode) {
            for (int i = 0; i < size; i++) {
                UserKey u = ((UserKey) keysList.elementAt(i));
                if (u.equals(key, true))
                    executed = commandExecute(u.command_id) || executed;
            }
        } else if (!onlyWithModificator) {
            for (int i = 0; i < size; i++) {
                UserKey u = ((UserKey) keysList.elementAt(i));
                if (u.equals(key, false))
                    executed = commandExecute(u.command_id) || executed;
            }
        }

        return executed;
    }

    private boolean commandExecute(int command_id) {
        Config cf = Config.getInstance();
        boolean connected = sd.roster.isLoggedIn();
        final VirtualList current = VirtualCanvas.getInstance().getList();

        if (current instanceof SplashScreen) {
            if (command_id == 44) {
                ((SplashScreen) current).destroyView();
                return true;
            }

            return false;
        }

        if (current.doUserKeyAction(command_id)) {
            return true;
        }

        // Common commands
        switch (command_id) {
            case 6: 
//#ifdef FILE_TRANSFER
                new io.file.transfer.TransferManager();
//#endif
                return true;
//#ifdef SERVICE_DISCOVERY
            case 8: 
                if (connected)
                    new ServiceDiscovery(null, null, false);
                return true;
//#endif
//#ifdef PRIVACY
            case 9: 
                if (connected)
                    new PrivacySelect();
                return true;
//#endif
//#ifdef USER_KEYS                
            case 10: //key pound
                new UserKeysList();
                return true;
//#endif
            /*case 12:
                cf.lightState=!cf.lightState;
                sd.roster.setLight(cf.lightState);
                cf.saveToStorage();
                return true;*/
            case 14:
                if (cf.allowMinimize)
                    BombusMod.getInstance().hideApp(true);
                return true;
            case 15:
                ColorTheme.invertSkin();
                return true;
            case 16:
//#ifdef CONSOLE
                    new XMLList();
//#endif
                return true;
            case 17:
                Config.fullscreen = !Config.fullscreen;
                cf.saveToStorage();                
                VirtualCanvas.getInstance().setFullScreenMode(Config.fullscreen);
                return true;

            case 44:
                sd.roster.blockScreen();
                return true;
            case 46:
                sd.roster.changeMotoBacklightState();
                return true;
        }

        return false;
    }
}

//#endif
