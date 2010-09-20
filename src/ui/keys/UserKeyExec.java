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
//# import Statistic.StatsWindow;
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
//#endif
import java.util.Vector;
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
import io.file.FileIO;
import java.io.IOException;
import util.StringLoader;
import java.io.InputStream;
import java.io.DataOutputStream;

public class UserKeyExec {

    private static UserKeyExec instance;
    StaticData sd = StaticData.getInstance();
    public final static String cmds[] = new String[22];
    public Vector keysList;
    public UserKey current_key;

    public static UserKeyExec getInstance() {
        if (instance == null)
            instance = new UserKeyExec();
        return instance;
    }

    private UserKeyExec() {
        init_cmds();
        if(!loadFromStorage())
            loadDefault();
        current_key = new UserKey();
    }

    private void init_cmds() {
        cmds[0] = SR.MS_NO;
        cmds[1] = SR.MS_OPTIONS;
        cmds[2] = SR.MS_CLEAN_ALL_MESSAGES;
        cmds[3] = SR.MS_RECONNECT;
//#ifdef STATS
//#         cmds[4] = SR.MS_STATS;
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
//#         cmds[10] = SR.MS_CUSTOM_KEYS;
//#endif
//#ifdef POPUPS
        cmds[11] = SR.MS_CLEAR_POPUPS;
//#endif
      //  cmds[12] = SR.MS_FLASHLIGHT;
        cmds[13] = SR.MS_ABOUT;
        cmds[14] = SR.MS_APP_MINIMIZE;
        cmds[15] = SR.MS_INVERT;
//#ifdef CONSOLE
//#         cmds[16] = SR.MS_XML_CONSOLE;
//#endif
        cmds[17] = SR.MS_FULLSCREEN;
//#ifdef JUICK
//#         cmds[18] = SR.MS_JUICK_FOCUS;
//#endif
        cmds[19] = SR.MS_HEAP_MONITOR;
//#ifdef SMILES
        cmds[20] = SR.MS_SMILES_TOGGLE;
//#endif
//#ifdef JUICK
//#         cmds[21] = SR.MS_COMMANDS + " Juick";
//#endif
    }

    public void update_current_key(int key, boolean key_long) {
        current_key.previous_key_long = current_key.key_long;
        current_key.key_long = key_long;

        current_key.previous_key = current_key.key;
        current_key.key = key;
    }

    public static int getCommandID(String str) {
        for (int i = 0; i < cmds.length; i++) {
            if ((cmds[i] != null) && str.equals(cmds[i]))
                return i;
        }
        return 0;
    }

    public boolean loadFromStorage() {
        keysList = null;
        keysList = new Vector();
        
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
//#                     keysList.addElement(UserKey.createFromDataInputStream(is));
//#                 return true;
//#             } catch (Exception e) { return false; }
//#ifdef PLUGINS
//#            }
//#endif
//#endif
//        return false;
    }

    public void loadFromInputStream(String file, boolean fs) {
        keysList = null;
        keysList = new Vector();

        Vector[] table = null;
        if (fs) {
            FileIO f = FileIO.createConnection(file);
            try {
                InputStream in = f.openInputStream();
                table = new StringLoader().stringLoader(in, 3);
                f.close();
            } catch (IOException e) { e.printStackTrace(); }
        } else table = new StringLoader().stringLoader(file, 3);

        for (int i = 0; i < table[0].size(); i++) {
            keysList.addElement(UserKey.createFromStrings(
                    (String) table[0].elementAt(i),
                    (String) table[1].elementAt(i),
                    (String) table[2].elementAt(i)));
        }

        rmsUpdate();
    }

    private void loadDefault() {
        loadFromInputStream(UserKey.def_keys, false);
    }

    public void writeToFile(String directory) {
        FileIO file = FileIO.createConnection(directory + "userkeys.txt");

        StringBuffer keyScheme = new StringBuffer("//UserKeys");
        for (int i = 0; i < keysList.size(); i++) {
            keyScheme.append("\n")
                     .append(((UserKey) keysList.elementAt(i)).toLine());
            }

        file.fileWrite(keyScheme.toString().getBytes());
    }

    public void rmsUpdate() {
        DataOutputStream outputStream = NvStorage.CreateDataOutputStream();

        int size = keysList.size();
        try {
            outputStream.writeInt(size);
        } catch (Exception e) { return; }

        for (int i = 0; i < size; i++) {
            ((UserKey) keysList.elementAt(i)).saveMyToDataOutputStream(outputStream);
        }

        NvStorage.writeFileRecord(outputStream, UserKey.storage, 0, true);
    }

    public boolean keyExecute(int key, boolean key_long) { // return false if key not executed
        update_current_key(key, key_long);
        boolean executed = false;

        current_key.two_keys = true;
        for (int i = 0; i < keysList.size(); i++) {
            UserKey u = ((UserKey) keysList.elementAt(i));
            if (current_key.equals(u))
                executed = commandExecute(u.command_id) || executed;
        }

        if (executed)
            return true;

        current_key.two_keys = false;
        for (int i = 0; i < keysList.size(); i++) {
            UserKey u = ((UserKey) keysList.elementAt(i));
            if (current_key.equals(u))
                executed = commandExecute(u.command_id) || executed;
        }

        return executed;
    }

    private boolean commandExecute(int command_id) {
        Config cf = Config.getInstance();
        boolean connected = sd.roster.isLoggedIn();
        Displayable current = midlet.BombusMod.getInstance().getCurrentDisplayable();

        switch (command_id) {
            case 1: 
                new ConfigForm();
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
//#                     new StatsWindow();
//#                 break;
//#endif
//#endif
            case 5:
                sd.roster.cmdStatus();
                break;
            case 6: 
//#if FILE_TRANSFER
                new io.file.transfer.TransferManager();
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
                if (connected) new ServiceDiscovery( null, null, false);
//#endif
                break;
            case 9: 
//#ifdef PRIVACY
                if (connected) new PrivacySelect();
//#endif
                break;
//#ifdef USER_KEYS                
//#             case 10: //key pound
//#                 new UserKeysList();
//#                 break;
//#endif                
            case 11:
//#ifdef POPUPS
                sd.roster.cmdClearPopups();
//#endif
                break;
            /*case 12:
                cf.lightState=!cf.lightState;
                sd.roster.setLight(cf.lightState);
                cf.saveToStorage();
                break;*/
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
//#                     new XMLList();
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
                    ((MessageItem)((MessageList)current).getFocusedObject()).toggleSmiles();
                    ((MessageList)current).redraw();
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
//#                     current_cml.menuAction(current_cml.cmdJuickCommands, (VirtualList)current);
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
