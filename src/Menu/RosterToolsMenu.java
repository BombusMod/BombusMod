/*
 * RosterToolsMenu.java
 *
 * Created on 11.12.2005, 20:43
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

package Menu;
//#ifdef CONSOLE
//# import Console.XMLList;
//#endif
import Alerts.AlertCustomizeForm;
import Client.*;
//#ifdef PRIVACY
//# import PrivacyLists.QuickPrivacy;
//#endif
//#if AUTOTASK
//# import AutoTasks.AutoTaskForm;
//#endif
import Fonts.ConfigFonts;
//#ifdef SERVICE_DISCOVERY
//# import ServiceDiscovery.*;
//#endif
//#if HISTORY
//# import History.HistoryConfig;
//#endif
//#ifdef PEP
//# import PEP.PepForm;
//#endif
//#ifdef STATS
//# import Statistic.StatsWindow;
//#endif
import VCard.VCard;
import VCard.VCardEdit;
import locale.SR;
import Colors.ColorConfigForm;
import images.RosterIcons;

//import ui.reconnectWindow;

//#ifdef USER_KEYS
//# import ui.keys.UserKeysList;
//#endif

//#if SASL_XGOOGLETOKEN
//# import xmpp.extensions.IqGmail;
//#endif
//#ifdef LIGHT_CONFIG
//# import LightControl.LightConfigForm;
//#endif

public class RosterToolsMenu extends Menu {

    RosterIcons Icons=RosterIcons.getInstance();

    public RosterToolsMenu() {
        super(SR.MS_TOOLS, RosterIcons.getInstance());

        cf=Config.getInstance();
        boolean connected=sd.roster.isLoggedIn();
//#ifdef SERVICE_DISCOVERY
//#         if (connected)
//#             addItem(SR.MS_DISCO, 0, RosterIcons.ICON_DISCO);
//#endif
//#ifdef PRIVACY
//#         if (connected)
//#             if (!sd.account.isGoogle)
//#                 addItem(SR.MS_PRIVACY_LISTS, 1, RosterIcons.ICON_PRIVACY);
//#endif
//#ifdef PEP
//#         if (connected)
//#                 addItem(SR.MS_PEP, 2, RosterIcons.ICON_MOOD);
//#endif
        if (connected)
            addItem(SR.MS_MY_VCARD, 3, RosterIcons.ICON_VCARD);
        addItem(SR.MS_OPTIONS, 4, RosterIcons.ICON_SETTINGS);
                
//#if (HISTORY)
//#         if (cf.saveHistory)
//#             addItem(SR.MS_HISTORY_OPTIONS, 6, RosterIcons.ICON_HISTORY);
//#endif
       addItem(SR.MS_FONTS_OPTIONS, 7, RosterIcons.ICON_FONTS);
//#if (FILE_IO)
//#         addItem(SR.MS_FILE_MANAGER, 8, RosterIcons.ICON_FILEMAN);
//#endif
//#if (FILE_IO && FILE_TRANSFER)
//#         if (connected && cf.fileTransfer)
//#             addItem(SR.MS_FILE_TRANSFERS, 9, RosterIcons.ICON_FT);
//#endif
        
        addItem(SR.MS_COLOR_TUNE, 10, RosterIcons.ICON_COLOR_TUNE);

//#if IMPORT_EXPORT
//#             addItem(SR.MS_IMPORT_EXPORT, 11, RosterIcons.ICON_IE);
//#endif
        addItem(SR.MS_NOTICES_OPTIONS, 12, RosterIcons.ICON_NOTIFY);
//#ifdef STATS
//#             addItem(SR.MS_STATS, 13, RosterIcons.ICON_STAT);
//#endif
//#ifdef CHECK_VERSION
//#             addItem(SR.MS_CHECK_UPDATE, 14, RosterIcons.ICON_CHECK_UPD);
//#             if (!cf.getStringProperty("Bombus-Upgrade", "123").equals("123"))
//#                 addItem(SR.MS_BUILD_NEW, 15, RosterIcons.ICON_BUILD_NEW);
//#endif
//#ifdef USER_KEYS
//#             addItem(SR.MS_CUSTOM_KEYS, 16, RosterIcons.ICON_KEYS);
//#endif
//#if SASL_XGOOGLETOKEN
//#         if (sd.account != null && sd.account.isGoogle && connected)
//#             addItem(SR.MS_CHECK_GOOGLE_MAIL, 17, RosterIcons.ICON_GMAIL);
//#endif 
//#if AUTOTASK
//#         addItem(SR.MS_AUTOTASKS, 18, RosterIcons.ICON_TASKS);
//#endif
//#ifdef CONSOLE
//#             addItem(SR.MS_XML_CONSOLE, 19, RosterIcons.ICON_CONSOLE);
//#endif
//#ifdef JUICK
//#             if (sd.roster.juickContacts.size() > 1) {
//#                 addItem("Tools for Juick.Com", 20, RosterIcons.ICON_JUICK);
//#             }
//#endif
//#ifdef LIGHT_CONFIG
//#             if (cf.lightState)
//#                 addItem(SR.L_CONFIG, 21, RosterIcons.ICON_SETTINGS);
//#endif        

        addItem(SR.MS_BREAK_CONECTION, 22, RosterIcons.ICON_RECONNECT);
        show();
    }
    
    public void eventOk(){
        //destroyView();
        boolean connected= ( sd.roster.isLoggedIn() );
        MenuItem me=(MenuItem) getFocusedObject();
        if (me==null)  return;
        int index=me.index;
        switch (index) {
//#ifdef SERVICE_DISCOVERY
//#             case 0: // Service Discovery
//#                 if (connected) new ServiceDiscovery(null, null, false);
//#                 break;
//#endif
//#ifdef PRIVACY
//#             case 1: // Privacy Lists
//#                 if (connected) new QuickPrivacy().show();
//#                 break;
//#endif
//#ifdef PEP
//#             case 2:
//#                 if (connected)
//#                     new PepForm();
//#                 return;
//#endif   
            case 3: {
                if (! connected) break;
                Contact c=sd.roster.selfContact();
                if (c.vcard!=null) {
                    new VCardEdit(c.vcard);
                    return;
                }
                VCard.request(c.bareJid, c.getJid().toString());
                return;
            }
            case 4:
                new ConfigForm();
                return;            
//#if (HISTORY)
//#             case 6: //history
//#                 new HistoryConfig();
//#                 return;
//#endif
            case 7:
                new ConfigFonts();
                return;
//#if (FILE_IO)
//#             case 8:
//#                 new io.file.browse.Browser(null, null, false);
//#                 return;
//#endif
//#if (FILE_TRANSFER)
//#             case 9:                
//#                 new io.file.transfer.TransferManager();
//#                 return;
//#endif
            case 10:
                new ColorConfigForm();
                return;
//#if IMPORT_EXPORT
//#             case 11:
//#                 new IE.IEMenu();
//#                 return;
//#endif
            case 12:
                new AlertCustomizeForm();
                return;
//#ifdef STATS
//#             case 13: //traffic stats
//#                 new StatsWindow();
//#                 return;
//#endif
//#ifdef CHECK_VERSION
//#             case 14:
//#                 Info.Upgrade up = new Info.Upgrade(false);
//#                 new Thread(up).start();
//#                 return;
//#             case 15:
//#                 Info.Upgrade up2 = new Info.Upgrade(true);
//#                 new Thread(up2).start();
//#                 return;
//#endif
//#ifdef USER_KEYS
//#             case 16:
//#                 new UserKeysList();
//#                 return;
//#endif
//#if SASL_XGOOGLETOKEN
//#             case 17: //mail check
//#                 destroyView();
//#                 sd.roster.theStream.send(IqGmail.query());
//# 		return;
//#endif
//#if AUTOTASK
//#             case 18:
//#                 new AutoTaskForm();
//#                 return;
//#endif
//#ifdef CONSOLE
//#             case 19:
//#                 new XMLList();
//#                 return;
//#endif
//#ifdef JUICK
//#             case 20:
//#                 new JuickConfig(me.toString());
//#                 return;
//#endif
            case 22:
                sd.roster.errorLog(SR.MS_SIMULATED_BREAK);
                //reconnectWindow.getInstance().startReconnect();
                sd.roster.doReconnect();//connectionTerminated(new Exception(SR.MS_SIMULATED_BREAK));
                destroyView();
                return;
            case 21:
//#ifdef LIGHT_CONFIG
//#                 new LightConfigForm();
//#endif                
                return;            
        }
    }
}
