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
import PrivacyLists.PrivacySelect;
//#endif
//#if AUTOTASK
//# import AutoTasks.AutoTaskForm;
//#endif
import Fonts.ConfigFonts;
//#ifdef SERVICE_DISCOVERY
import ServiceDiscovery.ServiceDiscovery;
//#endif
//#if HISTORY
//# import History.HistoryConfig;
//#endif
//#ifdef PEP
//# import Mood.MoodList;
//#endif
import VCard.VCard;
import VCard.VCardEdit;
//#ifdef NEW_SKIN
//# import images.MenuActionsIcons;
//#else
import images.MenuIcons;
//#endif
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import locale.SR;
import Colors.ColorConfigForm;
//#ifdef USER_KEYS
//# import ui.keys.userKeysList;
//#endif
//#ifdef CHECK_VERSION
//# import Info.Upgrade;
//#endif
//#if SASL_XGOOGLETOKEN
//# import xmpp.extensions.IqGmail;
//#endif

public class RosterToolsMenu extends Menu {
    Config cf;
    StaticData sd=StaticData.getInstance();
    
//#ifdef NEW_SKIN
//#     MenuActionsIcons menuIcons=MenuActionsIcons.getInstance();
//#else
    MenuIcons menuIcons=MenuIcons.getInstance();
//#endif

    public RosterToolsMenu(Display display, Displayable pView) {
//#ifdef NEW_SKIN
//#         super(SR.MS_TOOLS, MenuActionsIcons.getInstance());
//#else
        super(SR.MS_TOOLS, MenuIcons.getInstance());
//#endif
        cf=Config.getInstance();
        boolean connected=sd.roster.isLoggedIn();
//#ifdef SERVICE_DISCOVERY
        if (connected)
            addItem(SR.MS_DISCO, 0, menuIcons.ICON_DISCO);
//#endif
//#ifdef PRIVACY
        if (connected)
            addItem(SR.MS_PRIVACY_LISTS, 1, menuIcons.ICON_PRIVACY);
//#endif
//#ifdef PEP
//#         if (cf.sndrcvmood && connected)
//#             addItem(SR.MS_USER_MOOD, 2, menuIcons.ICON_MOOD);
//#endif
        if (connected)
            addItem(SR.MS_MY_VCARD, 3, menuIcons.ICON_VCARD);
        addItem(SR.MS_OPTIONS, 4, menuIcons.ICON_SETTINGS);
//#if (HISTORY)
//#         addItem(SR.MS_HISTORY_OPTIONS, 5, menuIcons.ICON_HISTORY);
//#endif
       addItem(SR.MS_FONTS_OPTIONS, 6, menuIcons.ICON_FONTS);
//#if (FILE_IO)
        addItem(SR.MS_FILE_MANAGER, 7, menuIcons.ICON_FILEMAN);
//#endif
//#if (FILE_IO && FILE_TRANSFER)
        if (connected)
            addItem(SR.MS_FILE_TRANSFERS, 8, menuIcons.ICON_FT);
//#endif
        
        addItem(SR.MS_COLOR_TUNE, 9, menuIcons.ICON_COLOR_TUNE);

//#if IMPORT_EXPORT
//#         addItem(SR.MS_IMPORT_EXPORT, 10, menuIcons.ICON_IE);
//#endif
        addItem(SR.MS_NOTICES_OPTIONS, 11, menuIcons.ICON_NOTIFY);
//#ifdef POPUPS
//#ifdef STATS
//#         addItem(SR.MS_STATS, 12, menuIcons.ICON_STAT);
//#endif
//#endif
//#ifdef CHECK_VERSION
//#         addItem(SR.MS_CHECK_UPDATE, 13, menuIcons.ICON_CHECK_UPD);
//#         if (cf.getStringProperty("Bombus-Upgrade", "123")!="123")
//#             addItem(SR.MS_BUILD_NEW, 14, menuIcons.ICON_BUILD_NEW);
//#endif
//#ifdef USER_KEYS
//#         if (cf.userKeys)
//#             addItem(SR.MS_CUSTOM_KEYS, 15, menuIcons.ICON_KEYS);
//#endif
//#if SASL_XGOOGLETOKEN
//#         if (sd.account.isGmail() && connected)
//#             addItem(SR.MS_CHECK_GOOGLE_MAIL, 16, menuIcons.ICON_GMAIL);
//#endif 
//#if AUTOTASK
//#         addItem(SR.MS_AUTOTASKS, 17, menuIcons.ICON_TASKS);
//#endif
//#ifdef CONSOLE
//#         addItem(SR.MS_XML_CONSOLE, 18, menuIcons.ICON_CONCOLE);
//#endif
        addItem(SR.MS_BREAK_CONECTION, 19, menuIcons.ICON_RECONNECT);
        attachDisplay(display);
        this.parentView=pView;
    }
    
    public void eventOk(){
        destroyView();
        boolean connected= ( sd.roster.isLoggedIn() );
        MenuItem me=(MenuItem) getFocusedObject();
        if (me==null)  return;
        int index=me.index;
        switch (index) {
//#ifdef SERVICE_DISCOVERY
            case 0: // Service Discovery
                if (connected) new ServiceDiscovery(display, null, null);
                break;
//#endif
//#ifdef PRIVACY
            case 1: // Privacy Lists
                if (connected) new PrivacySelect(display);
                break;
//#endif
//#ifdef PEP
//#             case 2:
//#                 if (connected)
//#                     new MoodList(display);
//#                 return;
//#endif   
            case 3: {
                if (! connected) break;
                Contact c=sd.roster.selfContact();
                if (c.vcard!=null) {
                    new VCardEdit(display, parentView, c.vcard);
                    return;
                }
                VCard.request(c.getBareJid(), c.getJid());
                return;
            }
            case 4:
                new ConfigForm(display, parentView);
                return;
//#if (HISTORY)
//#             case 5: //history
//#                 new HistoryConfig(display, parentView);
//#                 return;
//#endif
            case 6:
                new ConfigFonts(display, parentView);
                return;
//#if (FILE_IO)
            case 7:
                new io.file.browse.Browser(null, display, sd.roster, null, false);
                return;
//#endif
//#if (FILE_TRANSFER)
            case 8:
                new io.file.transfer.TransferManager(display);
                return;
//#endif
            case 9:
                new ColorConfigForm(display, parentView);
                return;
//#if IMPORT_EXPORT
//#             case 10:
//#                 new IE.IEMenu(display, parentView);
//#                 return; 
//#endif
            case 11:
                new AlertCustomizeForm(display, parentView);
                return;
//#ifdef POPUPS
//#ifdef STATS
//#             case 12: //traffic stats
//#                 sd.roster.showStats();
//#                 return;
//#endif
//#endif
//#ifdef CHECK_VERSION
//#             case 13:
//#                 new Upgrade(display, parentView, false);
//#                 return;
//#             case 14:
//#                 new Upgrade(display, parentView, true);
//#                 return;
//#endif
//#ifdef USER_KEYS
//#             case 15:
//#                 new userKeysList(display);
//#                 return;
//#endif
//#if SASL_XGOOGLETOKEN
//#             case 16: //mail check
//#                 sd.roster.theStream.send(IqGmail.query());
//# 		return; 
//#endif
//#if AUTOTASK
//#             case 17:
//#                 new AutoTaskForm(display, parentView);
//#                 return;
//#endif
//#ifdef CONSOLE
//#             case 18:
//#                 new XMLList(display, parentView);
//#                 return;
//#endif
            case 19:
                sd.roster.connectionTerminated(new Exception(SR.MS_SIMULATED_BREAK));
                return;
        }
    }
}
