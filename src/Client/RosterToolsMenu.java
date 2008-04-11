/*
 * RosterToolsMenu.java
 *
 * Created on 11.12.2005, 20:43
 * Copyright (c) 2005-2007, Eugene Stahov (evgs), http://bombus-im.org
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

package Client;
//#ifdef CONSOLE
//# import Console.XMLList;
//#endif
//#ifdef PRIVACY
//# import PrivacyLists.PrivacySelect;
//#endif
//#if AUTOTASK
//# import AutoTasks.AutoTaskForm;
//#endif
//#ifdef SERVICE_DISCOVERY
//# import ServiceDiscovery.ServiceDiscovery;
//#endif
//#if (FILE_IO && HISTORY)
//# import History.HistoryConfig;
//#endif
import UserMood.MoodSelect;
import javax.microedition.lcdui.Display;
import locale.SR;
//#ifdef COLORS
//# import Colors.ColorForm;
//#endif
import ui.Menu;
import ui.MenuItem;
//#ifdef USER_KEYS
//# import ui.keys.userKeysList;
//#endif
import vcard.VCard;
import vcard.vCardForm;
//#ifdef CHECK_VERSION
//# import Info.Upgrade;
//#endif
import Colors.ColorScheme;

public class RosterToolsMenu
        extends Menu {
    
    Config cf=Config.getInstance();
    StaticData sd=StaticData.getInstance();
    /** Creates a new instance of RosterToolsMenu */
    public RosterToolsMenu(Display display) {
        super(SR.MS_TOOLS);
//#ifdef SERVICE_DISCOVERY
//#         addItem(SR.MS_DISCO, 0, 0x13);
//#endif
//#ifdef PRIVACY
//#         addItem(SR.MS_PRIVACY_LISTS, 1, 0x46);
//#endif
//#ifdef MOOD
//#         if (sd.roster.useUserMood && cf.sndrcvmood)
//#             addItem(SR.MS_USER_MOOD, 2, 0x0f16);
//#endif
        addItem(SR.MS_MY_VCARD, 3, 0x0f16);
        addItem(SR.MS_OPTIONS, 4, 0x0f03);
//#if (FILE_IO && HISTORY)
//#         addItem(SR.MS_HISTORY_OPTIONS, 5, 0x0f01);
//#endif
        
//#if (FILE_IO)
        addItem(SR.MS_ROOT,6, 0x0f10);
//#endif
//#if (FILE_IO && FILE_TRANSFER)
//#         addItem(SR.MS_FILE_TRANSFERS, 7, 0x0f34);
//#endif
//#ifdef COLORS
//#         addItem(SR.MS_COLOR_TUNE, 8, 0x0f25);
//#endif
//#if IMPORT_EXPORT
//#         addItem(SR.MS_IMPORT_EXPORT, 9, 0x0f03);
//#endif
        addItem(SR.MS_NOTICES_OPTIONS, 10, 0x0f17);
//#ifdef POPUPS
//#         addItem(SR.MS_STATS, 11, 0x0f30);
//#endif
//#ifdef CHECK_VERSION
//#         addItem(SR.MS_CHECK_UPDATE, 12, 0x46);
//#         if (cf.getStringProperty("Bombus-Upgrade", "123")!="123")
//#             addItem(SR.MS_BUILD_NEW, 13, 0x46);
//#endif
//#ifdef USER_KEYS
//#         if (cf.userKeys)
//#             addItem(SR.MS_CUSTOM_KEYS, 14, 0x0f03);
//#endif
//#if SASL_XGOOGLETOKEN
        if (sd.account.isGmail())
            addItem(SR.MS_CHECK_GOOGLE_MAIL, 15,0x46);
//#endif 
//#if AUTOTASK
//#         addItem(SR.MS_AUTOTASKS, 16, 0x0f03);
//#endif
      
        addItem(SR.MS_INVERT, 17, 0x0f06);
        
        addItem(SR.MS_BREAK_CONECTION, 18, 0x13);
//#ifdef CONSOLE
//#         addItem(SR.MS_XML_CONSOLE, 19, 0x46);
//#endif
        attachDisplay(display);
    }
    public void eventOk(){
        destroyView();
        boolean connected= ( sd.roster.isLoggedIn() );
        MenuItem me=(MenuItem) getFocusedObject();
        if (me==null)  return;
        int index=me.index;
        switch (index) {
//#ifdef SERVICE_DISCOVERY
//#             case 0: // Service Discovery
//#                 if (connected) new ServiceDiscovery(display, null, null);
//#                 break;
//#endif

//#ifdef PRIVACY
//#             case 1: // Privacy Lists
//#                 if (connected) new PrivacySelect(display);
//#                 break;
//#endif
            case 2:
                if (! connected) break;
                new MoodSelect(display);
                return;
            case 3: {
                if (! connected) break;
                Contact c=sd.roster.selfContact();
                if (c.vcard!=null) {
                    new vCardForm(display, c.vcard, true);
                    return;
                }
                VCard.request(c.getBareJid(), c.getJid());
                return;
            }
            case 4:
                new ConfigForm(display);
                return;
//#if (HISTORY)
//#             case 5: //history
//#                 new HistoryConfig(display);
//#                 return;
//#endif

//#if (FILE_IO)
            case 6:
                new io.file.browse.Browser(null, display, null, false);
                return;
//#endif

//#if (FILE_TRANSFER)
//#             case 7:
//#                 new io.file.transfer.TransferManager(display);
//#                 return;
//#endif
                
//#ifdef COLORS
//#             case 8:
//#                 new ColorForm(display);
//#                 return;
//#endif
                
//#if IMPORT_EXPORT
//#             case 9:
//#                 new IE.IEMenu(display);
//#                 return; 
//#endif
            case 10:
                new AlertCustomizeForm(display);
                return;
//#ifdef POPUPS
//#             case 11: //traffic stats
//#                 sd.roster.showStats();
//#                 return;
//#endif
                
//#ifdef CHECK_VERSION
//#             case 12:
//#                 new Upgrade(display, false);
//#                 return;
//#             case 13:
//#                 new Upgrade(display, true);
//#                 return;
//#endif
                
//#ifdef USER_KEYS
//#             case 14:
//#                 new userKeysList(display);
//#                 return;
//#endif
                
//#if SASL_XGOOGLETOKEN
            case 15: //mail check
                sd.roster.sendGmailReq();;
		return; 
//#endif
                
//#if AUTOTASK
//#             case 16:
//#                 new AutoTaskForm(display);
//#                 return;
//#endif
            case 17:
                ColorScheme.invertSkin();
                return;
            case 18:
                sd.roster.connectionTerminated(new Exception(SR.MS_SIMULATED_BREAK));
                return;
//#ifdef CONSOLE
//#             case 19:
//#                 new XMLList(display);
//#                 return;
//#endif
        }
    }
}