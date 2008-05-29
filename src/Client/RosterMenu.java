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

package Client;

import images.MenuIcons;
import javax.microedition.lcdui.Display;
import locale.SR;
import ui.Menu;
import ui.MenuItem;


public class RosterMenu extends Menu {
    private Object o;
    
    private Config cf;
    private StaticData sd=StaticData.getInstance();

    public RosterMenu(Display display, Object o) {
        super(SR.MS_MAIN_MENU, MenuIcons.getInstance());
        this.o=o;
        cf=Config.getInstance();
        addItem(SR.MS_ITEM_ACTIONS, 0, 0x27);
        addItem(SR.MS_STATUS_MENU, 1, 0x16);
        addItem(SR.MS_ACTIVE_CONTACTS, 2, 0x21);
        addItem(SR.MS_ALERT_PROFILE_CMD, 4, 0x17);
//#ifndef WMUC
        addItem(SR.MS_CONFERENCE, 5, 0x00);
//#endif
//#ifdef ARCHIVE
        addItem(SR.MS_ARCHIVE, 6,0x12);
//#endif
        addItem(SR.MS_ADD_CONTACT, 7, 0x02);
        addItem(SR.MS_TOOLS, 8,0x24);    
        addItem(SR.MS_ACCOUNT_, 9,0x01);
        addItem(SR.MS_ABOUT, 10,0x04);
        addItem(SR.MS_CLEAN_ALL_MESSAGES, 11, 0x00);
        addItem(SR.MS_APP_QUIT, 12,0x22);
    
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
            case 11: //cleanup All Histories
                sd.roster.cmdCleanAllMessages();
		break; 
	    case 12: {//quit
                sd.roster.cmdQuit();
                return;
	    }
	}
    }
}
