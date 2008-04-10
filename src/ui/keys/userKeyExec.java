/*
 * userKeyExecute.java
 *
 * Created on 14.09.2007, 13:38
 * Copyright (c) 2006-2007, Daniel Apatin (ad), http://apatin.net.ru
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
import Client.Roster;
import Client.StaticData;
//#ifdef CONSOLE
//# import Console.XMLList;
//#endif
//#ifdef PRIVACY
//# import PrivacyLists.PrivacySelect;
//#endif
//#ifdef SERVICE_DISCOVERY
//# import ServiceDiscovery.ServiceDiscovery;
//#endif
import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.lcdui.Display;
import locale.SR;
import midlet.BombusMod;
import Colors.ColorScheme;

public class userKeyExec {
    
    private static userKeyExec instance;
    static Config cf=Config.getInstance();
    
    public static userKeyExec getInstance(){
	if (instance==null) {
	    instance=new userKeyExec();
            instance.initCommands();
	}
	return instance;
    }

    private Display display;

    public Vector commandsList;
    
    private void initCommands() {
        commandsList=new Vector();
        
        userKey u;

        int index=0;
        do {
            u=userKey.createFromStorage(index);
            if (u!=null) {
                commandsList.addElement(u);
                index++;
             }
       } while (u!=null);
    }

    private int getCommandByKey(int key) {
        int commandNum = -1;
         for (Enumeration commands=commandsList.elements(); commands.hasMoreElements(); ) 
         {
            userKey userKeyItem=(userKey) commands.nextElement();
            if (userKeyItem.getKey()==key) {
                if (userKeyItem.getActive()) {
                    commandNum=userKeyItem.getCommandId();
                }
            }
            
         }
        return commandNum;
    }
    
    public void commandExecute(Display display, int command) { //return false if key not executed
        this.display=display;

        int commandId=getCommandByKey(command);
        
        boolean connected= ( StaticData.getInstance().roster.isLoggedIn() );

        Roster roster=StaticData.getInstance().roster;
        switch (commandId) {
            case -1: // ky-ky?
                break;
            case 0:
                // do nothing
                break;
            case 1: 
                new ConfigForm(display);
                break;
            case 2: 
                roster.cleanupAllHistories();
                break;
            case 3: 
                roster.connectionTerminated(new Exception(SR.MS_SIMULATED_BREAK));
                break;
            case 4: 
                roster.showStats();
                break;
            case 5:
                roster.cmdStatus();
                break;
            case 6: 
//#if FILE_TRANSFER
//#                 new io.file.transfer.TransferManager(display);
//#endif
                break;
            case 7: 
//#ifdef ARCHIVE
//#                 roster.cmdArchive();
//#endif
                break;
            case 8: 
//#ifdef SERVICE_DISCOVERY
//#                 if (connected) new ServiceDiscovery(display, null, null);
//#endif
                break;
            case 9: 
//#ifdef PRIVACY
//#                 if (connected) new PrivacySelect(display);
//#endif
                break;
            case 10: //key pound
                new userKeysList(display);
                break;
            case 11:
//#ifdef POPUPS
//#                 roster.cmdClearPopups();
//#endif
                break;
            case 12:
                cf.lightState=!cf.lightState;
                roster.setLight(cf.lightState);
                cf.saveToStorage();
                break;
            case 13:
                new Info.InfoWindow(display);
                break;
            case 14:
                if (cf.allowMinimize)
                    BombusMod.getInstance().hideApp(true);
                break;
            case 15:
                ColorScheme.invertSkin();
                break;
//#ifdef CONSOLE
//#             case 16:
//#                 new XMLList(display);
//#                 break;
//#endif
        }
    } 


    static String getDesc(int descId) {
        return COMMANDS_DESC[descId];
    }
    
    static String getKeyDesc(int commandId) {
        return KEYS_NAME[commandId];
    }

    public static final String[] COMMANDS_DESC = {
            SR.MS_NO,
            SR.MS_OPTIONS,
            SR.MS_CLEAN_ALL_MESSAGES,
            SR.MS_RECONNECT,
            SR.MS_STATS,
            SR.MS_STATUS_MENU,
//#if FILE_TRANSFER
//#             SR.MS_FILE_TRANSFERS,
//#endif
//#ifdef ARCHIVE
//#             SR.MS_ARCHIVE,
//#endif
//#ifdef SERVICE_DISCOVERY
//#             SR.MS_DISCO,
//#endif
//#ifdef PRIVACY
//#             SR.MS_PRIVACY_LISTS,
//#endif
            SR.MS_CUSTOM_KEYS,
//#ifdef POPUPS
//#             SR.MS_CLEAR_POPUPS,
//#endif
            SR.MS_FLASHLIGHT,
            SR.MS_ABOUT,
            SR.MS_APP_MINIMIZE,
            SR.MS_INVERT
//#ifdef CONSOLE
//#             , SR.MS_XML_CONSOLE
//#endif
    };
    
    public static final String[] KEYS_NAME = {
            "0",
            "1",
            "2",
            "3",
            "4",
            "5",
            "6",
            "7",
            "8",
            "9",
            "#"
    };
}
