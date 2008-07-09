/*
 * RosterMenu.java
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
 *
 */

package Menu;

import Client.*;
//#ifdef NEW_SKIN
//# import images.MenuActionsIcons;
//#else
import images.MenuIcons;
//#endif
import javax.microedition.lcdui.Display;
import locale.SR;
import midlet.BombusMod;

public class RosterMenu extends Menu {
    private Object o;
    
    private Config cf;
    private StaticData sd=StaticData.getInstance();
    
//#ifdef NEW_SKIN
//#     MenuActionsIcons menuIcons=MenuActionsIcons.getInstance();
//#else
    MenuIcons menuIcons=MenuIcons.getInstance();
//#endif

    public RosterMenu(Display display, Object o) {
//#ifdef NEW_SKIN
//#         super(SR.MS_MAIN_MENU, MenuActionsIcons.getInstance());
//#else
        super(SR.MS_MAIN_MENU, MenuIcons.getInstance());
//#endif
        
        this.o=o;
        cf=Config.getInstance();
        boolean connected=sd.roster.isLoggedIn();
        
        if (connected) addItem(SR.MS_ITEM_ACTIONS, 0, menuIcons.ICON_ITEM_ACTIONS);
        addItem(SR.MS_STATUS_MENU, 1, menuIcons.ICON_STATUS);
        addItem(SR.MS_ACTIVE_CONTACTS, 2, menuIcons.ICON_CONFERENCE);
        addItem(SR.MS_ALERT_PROFILE_CMD, 4, menuIcons.ICON_NOTIFY);
//#ifndef WMUC
        if (connected) addItem(SR.MS_CONFERENCE, 5, menuIcons.ICON_CONFERENCE);
//#endif
//#ifdef ARCHIVE
        addItem(SR.MS_ARCHIVE, 6, menuIcons.ICON_ARCHIVE);
//#endif
        if (connected) addItem(SR.MS_ADD_CONTACT, 7, menuIcons.ICON_ADD_CONTACT);
        addItem(SR.MS_TOOLS, 8, menuIcons.ICON_SETTINGS);    
        addItem(SR.MS_ACCOUNT_, 9, menuIcons.ICON_VCARD);
        addItem(SR.MS_ABOUT, 10, menuIcons.ICON_CHECK_UPD);
        if (cf.allowMinimize)
            addItem(SR.MS_APP_MINIMIZE, 11, menuIcons.ICON_FILEMAN);
        addItem(SR.MS_CLEAN_ALL_MESSAGES, 12, menuIcons.ICON_CLEAN_MESSAGES);
        addItem(SR.MS_APP_QUIT, 13, menuIcons.ICON_BUILD_NEW);
    
	attachDisplay(display);
    }
    public void eventOk(){
	destroyView();
	MenuItem me=(MenuItem) getFocusedObject();
        
	if (me==null)  return;
        
	int index=me.index;
	switch (index) {
	    case 0: //actions
                sd.roster.cmdActions();
                break;
	    case 1: //status
                sd.roster.cmdStatus();
		break;
            case 2: //active
                sd.roster.cmdActiveContacts();
		break;
            case 4: //alert
                sd.roster.cmdAlert();
		break;
//#ifndef WMUC
            case 5: //conference
                sd.roster.cmdConference();
                break;
//#endif
//#ifdef ARCHIVE
            case 6: //archive
                sd.roster.cmdArchive();
		break;
//#endif
            case 7: {//add contact
                sd.roster.cmdAdd();
                break;
            }
            case 8: //tools
                sd.roster.cmdTools();
		break;
            case 9: //account
                sd.roster.cmdAccount();
		break; 
            case 10: //about
                sd.roster.cmdInfo();
		break; 
            case 11: //minimize
                BombusMod.getInstance().hideApp(true);
		break; 
            case 12: //cleanup All Histories
                sd.roster.cmdCleanAllMessages();
		break; 
	    case 13: {//quit
                sd.roster.cmdQuit();
                return;
	    }
	}
    }
}
