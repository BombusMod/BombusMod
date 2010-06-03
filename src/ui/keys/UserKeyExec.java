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

package ui.keys;

import Client.Config;
import Client.ConfigForm;
import Client.StaticData;
//#ifdef STATS
//# import Statistic.Stats;
//#endif
import Colors.ColorTheme;
//#ifdef CONSOLE
//# import Console.XMLList;
//#endif
//#ifdef PRIVACY
import PrivacyLists.PrivacySelect;
//#endif
//#ifdef SERVICE_DISCOVERY
import ServiceDiscovery.*;
import Statistic.StatsWindow;
//#endif
import java.util.Vector;
import javax.microedition.lcdui.Display;
import locale.SR;
import midlet.BombusMod;
import ui.VirtualList;
//#ifdef JUICK
//# import Client.Contact;
//#endif
import Client.ContactMessageList;
import Client.Roster;
import javax.microedition.lcdui.Displayable;
import io.NvStorage;
import java.io.DataInputStream;
import Messages.MessageList;
import Messages.MessageItem;

public class UserKeyExec {
    StaticData sd = StaticData.getInstance();
    
    private static UserKeyExec instance;
    public static UserKeyExec getInstance() {
        if (instance == null)
            instance = new UserKeyExec();
        return instance;
    }

    private UserKeyExec() {
        init_available_commands();
        init_commands_from_rms();
    }

    public Vector userKeysList;
    public final static UserKeyCommand none_command = new UserKeyCommand(0, SR.MS_NO);
     // 0 - common, 1 - Roster, 2 - ContactMessageList, 3 - none
    public final static Vector[] available_commands = { new Vector(), new Vector(), new Vector() };

    public void init_available_commands() {
        available_commands[0].addElement(new UserKeyCommand(1, SR.MS_OPTIONS));
        available_commands[0].addElement(new UserKeyCommand(2, SR.MS_CLEAN_ALL_MESSAGES));
        available_commands[0].addElement(new UserKeyCommand(3, SR.MS_RECONNECT));
//#ifdef STATS
//#         available_commands[0].addElement(new UserKeyCommand(4, SR.MS_STATS));
//#endif
        available_commands[0].addElement(new UserKeyCommand(5, SR.MS_STATUS_MENU));
        available_commands[0].addElement(new UserKeyCommand(6, SR.MS_FILE_TRANSFERS));
//#ifdef ARCHIVE
        available_commands[0].addElement(new UserKeyCommand(7, SR.MS_ARCHIVE));
//#endif
        available_commands[0].addElement(new UserKeyCommand(8, SR.MS_DISCO));
//#ifdef PRIVACY
        available_commands[0].addElement(new UserKeyCommand(9, SR.MS_PRIVACY_LISTS));
//#endif
//#ifdef USER_KEYS
//#         available_commands[0].addElement(new UserKeyCommand(10, SR.MS_CUSTOM_KEYS));
//#endif
//#ifdef POPUPS
        available_commands[1].addElement(new UserKeyCommand(11, SR.MS_CLEAR_POPUPS));
//#endif
        available_commands[0].addElement(new UserKeyCommand(12, SR.MS_FLASHLIGHT));
        available_commands[0].addElement(new UserKeyCommand(13, SR.MS_ABOUT));
        available_commands[0].addElement(new UserKeyCommand(14, SR.MS_APP_MINIMIZE));
        available_commands[0].addElement(new UserKeyCommand(15, SR.MS_INVERT));
//#ifdef CONSOLE
//#         available_commands[0].addElement(new UserKeyCommand(16, SR.MS_XML_CONSOLE));
//#endif
        available_commands[0].addElement(new UserKeyCommand(17, SR.MS_FULLSCREEN));
//#ifdef JUICK
//#         available_commands[1].addElement(new UserKeyCommand(18, SR.MS_JUICK_FOCUS));
//#endif
        available_commands[1].addElement(new UserKeyCommand(19, SR.MS_HEAP_MONITOR));
//#ifdef SMILES
        available_commands[2].addElement(new UserKeyCommand(20, SR.MS_SMILES_TOGGLE));
//#endif
//#ifdef JUICK
//#         available_commands[2].addElement(new UserKeyCommand(21, SR.MS_COMMANDS + " Juick"));
//#endif
    }

    public static UserKeyCommand get_command_by_id(int command_id, int type) {
        if ((type < 0) || (type > (3-1)))
            return none_command;
        int cmdIndex = available_commands[type].indexOf(new UserKeyCommand(command_id, null));
        if (cmdIndex >= 0) {
            return (UserKeyCommand) available_commands[type].elementAt(cmdIndex);
        }
        return none_command;
    }

    public void init_commands_from_rms() {
        userKeysList = null;
        userKeysList = new Vector();
        
//#ifdef USER_KEYS
//#ifdef PLUGINS
//#         if (sd.UserKeys) {
//#endif
//#             DataInputStream is = NvStorage.ReadFileRecord(UserKey.storage, 0);
//# 
//#             int size = 0;
//#             try {
//#                 size = is.readInt();
//#                 for (int i = 0; i < size; i++)
//#                     userKeysList.addElement(UserKey.createFromDataInputStream(is));
//#             } catch (Exception e) {
//#                 userKeysList = UserKeysList.getDefaultKeysList();
//#                 UserKeysList.rmsUpdate(userKeysList);
//#             }
//#ifdef PLUGINS
//#         } else
//#endif
//#endif
            userKeysList = UserKeysList.getDefaultKeysList();
    }

    public boolean commandExecute(Display display, int previous_key_code, int key_code) { //return false if key not executed
        int[] commands_id = {0, 0, 0};
        int index_key = userKeysList.indexOf(new UserKey(commands_id, previous_key_code, key_code, true, true));
        if (index_key<0) // Если нет двухкнопочного сочетания, ищем однокнопочное
            index_key = userKeysList.indexOf(new UserKey(commands_id, previous_key_code, key_code, true, false));
        if (index_key<0) // А если нет и его, то тикаем
            return false;
        commands_id = ((UserKey) userKeysList.elementAt(index_key)).commands_id;
        boolean executed = false;
        for (int i = 0; i < 3; i++)
            executed = executed || commandExecuteByID(display, commands_id[i], i);
        return executed;
    }

    public boolean commandExecuteByID(Display display, int command_id, int type) {
        Config cf = Config.getInstance();
        boolean connected = sd.roster.isLoggedIn();
        Displayable current = display.getCurrent();

        switch (command_id) {
            case 1: 
                new ConfigForm(display, sd.roster);
                break;
            case 2: 
                sd.roster.cmdCleanAllMessages();
                break;
            case 3: 
                sd.roster.connectionTerminated(new Exception(SR.MS_SIMULATED_BREAK));
                break;
//#ifdef POPUPS
//#ifdef STATS
//#             case 4:
//#ifdef PLUGINS
//#                 if (sd.Stats)
//#endif
//#                     new StatsWindow(display, sd.roster);
//#                 break;
//#endif
//#endif
            case 5:
                sd.roster.cmdStatus();
                break;
            case 6: 
//#if FILE_TRANSFER
                new io.file.transfer.TransferManager(display, sd.roster);
//#endif
                break;
            case 7: 
//#ifdef ARCHIVE
//#ifdef PLUGINS
//#                 if (sd.Archive)
//#endif
                    sd.roster.cmdArchive();
//#endif
                break;
            case 8: 
//#ifdef SERVICE_DISCOVERY
                if (connected) new ServiceDiscovery(display, null, null, false);
//#endif
                break;
            case 9: 
//#ifdef PRIVACY
                if (connected) new PrivacySelect(display, sd.roster);
//#endif
                break;
//#ifdef USER_KEYS                
//#             case 10: //key pound
//#                 new UserKeysList(display);
//#                 break;
//#endif                
            case 11:
//#ifdef POPUPS
                sd.roster.cmdClearPopups();
//#endif
                break;
            case 12:
                cf.lightState=!cf.lightState;
                sd.roster.setLight(cf.lightState);
                cf.saveToStorage();
                break;
            case 13:
                sd.roster.cmdInfo();
                break;
            case 14:
                if (cf.allowMinimize)
                    BombusMod.getInstance().hideApp(true);
                break;
            case 15:
                ColorTheme.invertSkin();
                break;
            case 16:
//#ifdef CONSOLE
//#ifdef PLUGINS
//#                 try {
//#                     Class.forName("Console.XMLList");
//#endif
//#                     new XMLList(display, display.getCurrent());
//#ifdef PLUGINS
//#                 } catch (ClassNotFoundException ignore3) { }
//#endif
//#endif
                break;
            case 17:
                Config.fullscreen=!Config.fullscreen;
                cf.saveToStorage();
                VirtualList.fullscreen=Config.fullscreen;
                sd.roster.setFullScreenMode(Config.fullscreen);
                break;
            case 18:
//#ifdef JUICK
//#ifdef PLUGINS
//#                 if(sd.Juick)
//#endif
//#                 if (current instanceof Roster) {
//#                     Contact jContact = sd.roster.getMainJuickContact();
//#                     if (jContact != null)
//#                         sd.roster.focusToContact(jContact, false);
//#                 } else {
//#                     return false;
//#                 }
//#endif
                break;
            case 19:
//            if (cf.widthSystemgc) { _vt
                System.gc();
                try { Thread.sleep(50); } catch (InterruptedException e) { }
//            } _vt
                if (current instanceof Roster) {
                    sd.roster.showHeapInfo();
                } else {
                    return false;
                }
            break;
            case 20:
//#ifdef SMILES
                if (current instanceof MessageList) {
                    MessageList current_ml = (ContactMessageList) current;
                    ((MessageItem) current_ml.getFocusedObject()).toggleSmiles();
                    current_ml.repaint();
                } else {
                    return false;
                }
//#endif
                break;
            case 21:
//#ifdef JUICK
//#ifdef PLUGINS
//#                 if(sd.Juick)
//#endif
//#                 if (current instanceof ContactMessageList) {
//#                     ContactMessageList current_cml = (ContactMessageList) current;
//#                     current_cml.commandAction(current_cml.cmdJuickCommands, current);
//#                 } else {
//#                     return false;
//#                 }
//#endif
                break;
            default:
                return false;
        }
        return true;
    }
}
