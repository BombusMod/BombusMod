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
import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.lcdui.Display;
import locale.SR;
import midlet.BombusMod;
import ui.VirtualList;
//#ifdef JUICK
//# import Client.Contact;
//# import Client.ContactMessageList;
//# import Client.Roster;
//#endif
import javax.microedition.lcdui.Displayable;
import io.NvStorage;
import java.io.DataInputStream;
import java.io.IOException;

public class UserKeyExec {
//#ifdef PLUGINS
//#     public static String plugin = new String("PLUGIN_USER_KEYS");
//#endif

    private static Config cf;
    StaticData sd=StaticData.getInstance();
    
    private static UserKeyExec instance;
    public static UserKeyExec getInstance(){
	if (instance==null) {
	    instance=new UserKeyExec();
            cf=Config.getInstance();
            instance.initCommands();
	}
	return instance;
    }

    public Vector userKeysList;
    
    private void initCommands() {
        userKeysList = null;
        userKeysList = new Vector();

        int storage_version = 1;
        DataInputStream is = NvStorage.ReadFileRecord(UserKey.storage, 0, storage_version);
        if (is==null)
            export_from_old_storage(storage_version);
        is = NvStorage.ReadFileRecord(UserKey.storage, 0, storage_version);
        if (is==null)
            return;

        int size = 0;
        try {
            size = is.readInt();
        } catch (IOException e) { }

        for (int i=0; i<size; i++)
            userKeysList.addElement(UserKey.createFromDataInputStream(is));
    }

    private int get_old_key_code(int key_num) {
        switch(key_num) {
            case 0: return VirtualList.KEY_NUM0;
            case 1: return VirtualList.KEY_NUM1;
            case 2: return VirtualList.KEY_NUM2;
            case 3: return VirtualList.KEY_NUM3;
            case 4: return VirtualList.KEY_NUM4;
            case 5: return VirtualList.KEY_NUM5;
            case 6: return VirtualList.KEY_NUM6;
            case 7: return VirtualList.KEY_NUM7;
            case 8: return VirtualList.KEY_NUM8;
            case 9: return VirtualList.KEY_NUM9;
            case 10: return VirtualList.KEY_STAR;
            default: return -1;
        }
    }

    private void export_from_old_storage(int storage_version) {
        for (int i=(storage_version-1); i>=0; i--)
            switch(i) {
                case 0:
                    DataInputStream is = NvStorage.ReadFileRecord(UserKey.storage, 0);
                    if (is==null)
                        continue;

                    Vector old_user_key_list = new Vector();
                    UserKey u = new UserKey();
                    do {
                        try {
                            u.command_id = is.readInt();
                            u.previous_key = VirtualList.KEY_STAR;
                            u.key = get_old_key_code(is.readInt());
                            u.active = is.readBoolean();
                        } catch (IOException e) { break; }

                        old_user_key_list.addElement(u);
                        } while (true);

                    if (old_user_key_list.size()>0) {
                        UserKeysList.rmsUpdate(old_user_key_list);
                        // Здесь я хотел дропнуть старое хранилище, но потом подумал: пусть живёт.
                        return;
                    }
                    break;
            }
    }

    public boolean commandExecute(Display display, int previous_key_code, int key_code) { //return false if key not executed
        int index_key = userKeysList.indexOf(new UserKey(0, previous_key_code, key_code, true, true));
        if (index_key<0) // Если нет двухкнопочного сочетания, ищем однокнопочное
            index_key = userKeysList.indexOf(new UserKey(0, previous_key_code, key_code, true, false));
        if (index_key<0) // А если нет и его, то тикаем
            return false;
        int command_id = ((UserKey) userKeysList.elementAt(index_key)).command_id;

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
            case 10: //key pound
                new UserKeysList(display);
                break;
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
//#                 if(sd.Juick) {
//#endif
//#                 if (current instanceof ContactMessageList) {
//#                     ContactMessageList current_cml = (ContactMessageList) current;
//#                     current_cml.commandAction(current_cml.cmdJuickCommands, current);
//#                     System.out.println("current instanceof ContactMessageList");
//#                 } else if (current instanceof Roster) {
//#                     Contact jContact = sd.roster.getMainJuickContact();
//#                     if (jContact != null)
//#                         sd.roster.focusToContact(jContact, false);
//#                 } else {
//#ifdef DEBUG
//#                     System.out.println("Current Displayable are not instance of ContactMessageList or Roster.");
//#endif
//#                 }
//#endif
//#ifdef PLUGINS
//#                 }
//#endif
                break;
            case 19:
//            if (cf.widthSystemgc) { _vt
                System.gc();
                try { Thread.sleep(50); } catch (InterruptedException e) { }
//            } _vt
                sd.roster.showHeapInfo();
            break;
            default:
                return false;
        }
        return true;
    }
}
