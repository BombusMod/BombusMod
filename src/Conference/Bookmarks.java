/*
 * Bookmarks.java
 *
 * Created on 18.09.2005, 0:03
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
package Conference;
import Conference.affiliation.Affiliations;
//#ifdef SERVICE_DISCOVERY
import ServiceDiscovery.*;
//#endif
import Menu.MenuCommand;
import locale.SR;
import ui.*;
import java.util.*;
import com.alsutton.jabber.*;
import ui.MainBar;
import ui.controls.AlertBox;
import ui.controls.form.DefForm;

/**
 *
 * @author EvgS
 */
public class Bookmarks extends DefForm {

    private BookmarkItem toAdd;
    private MenuCommand cmdJoin = new MenuCommand(SR.MS_SELECT, MenuCommand.OK, 1);
    private MenuCommand cmdAdvJoin = new MenuCommand(SR.MS_EDIT_JOIN, MenuCommand.SCREEN, 2);
    private MenuCommand cmdNew = new MenuCommand(SR.MS_NEW_BOOKMARK, MenuCommand.SCREEN, 3);
    private MenuCommand cmdDoAutoJoin = new MenuCommand(SR.MS_DO_AUTOJOIN, MenuCommand.SCREEN, 4);
    private MenuCommand cmdConfigure = new MenuCommand(SR.MS_CONFIG_ROOM, MenuCommand.SCREEN, 5);
//#ifdef SERVICE_DISCOVERY
    private MenuCommand cmdDisco = new MenuCommand(SR.MS_DISCO_ROOM, MenuCommand.SCREEN, 6);
//#endif
    private MenuCommand cmdUp = new MenuCommand(SR.MS_MOVE_UP, MenuCommand.SCREEN, 7);
    private MenuCommand cmdDwn = new MenuCommand(SR.MS_MOVE_DOWN, MenuCommand.SCREEN, 8);
    private MenuCommand cmdSort = new MenuCommand(SR.MS_SORT, MenuCommand.SCREEN, 9);
    private MenuCommand cmdSave = new MenuCommand(SR.MS_SAVE_LIST, MenuCommand.SCREEN, 10);
    private MenuCommand cmdRoomOwners = new MenuCommand(SR.MS_OWNERS, MenuCommand.SCREEN, 11);
    private MenuCommand cmdRoomAdmins = new MenuCommand(SR.MS_ADMINS, MenuCommand.SCREEN, 12);
    private MenuCommand cmdRoomMembers = new MenuCommand(SR.MS_MEMBERS, MenuCommand.SCREEN, 13);
    private MenuCommand cmdRoomBanned = new MenuCommand(SR.MS_BANNED, MenuCommand.SCREEN, 14);
    private MenuCommand cmdDel = new MenuCommand(SR.MS_DELETE, MenuCommand.SCREEN, 15);
    JabberStream stream = sd.roster.theStream;

    /** Creates a new instance of Bookmarks
     * @param toAdd
     */
    public Bookmarks(BookmarkItem toAdd) {
        super(null, false);
        loadBookmarks();

        if (getItemCount() == 0 && toAdd == null) {
            new ConferenceForm();
            return;
        }

        this.toAdd = toAdd;

        if (toAdd != null) {
            addBookmark();
        }

        setMainBarItem(new MainBar(2, null, SR.MS_BOOKMARKS + " (" + getItemCount() + ") ", false));//for title updating after "add bookmark"

        enableListWrapping(true);
        show();
    }

    public void commandState() {
        menuCommands.removeAllElements();
        addMenuCommand(cmdJoin);
        addMenuCommand(cmdAdvJoin);
        addMenuCommand(cmdNew);
        addMenuCommand(cmdDoAutoJoin);
        addMenuCommand(cmdUp);
        addMenuCommand(cmdDwn);
        addMenuCommand(cmdSave);
        addMenuCommand(cmdSort);
//#ifdef SERVICE_DISCOVERY
        addMenuCommand(cmdDisco);
//#endif
        addMenuCommand(cmdDel);
        addMenuCommand(cmdRoomOwners);
        addMenuCommand(cmdRoomAdmins);
        addMenuCommand(cmdRoomMembers);
        addMenuCommand(cmdRoomBanned);
        addMenuCommand(cmdConfigure);
    }

    public final void loadBookmarks() {
        loadItemsFrom(sd.roster.bookmarks);
    }

    private void addBookmark() {
        if (toAdd != null) {
            itemsList.addElement(toAdd);
            //sort(bm);
            saveBookmarks();
        }
    }

    public void eventOk() {
        if (getItemCount() == 0) {
            return;
        }

        BookmarkItem join = (BookmarkItem) getFocusedObject();
        if (join == null) {
            return;
        }
        if (join.isUrl) {
            return;
        }

        ConferenceForm.join(join.name, join.getJidNick(), join.password, cf.confMessageCount);
        parentView = sd.roster;
        destroyView();
    }

    public void menuAction(MenuCommand c, VirtualList d) {
        if (c == cmdNew) {
            new ConferenceForm();
            return;
        }
        if (c == cmdJoin) {
            eventOk();
        }

        if (getItemCount() == 0) {
            return;
        }
        String roomJid = ((BookmarkItem) getFocusedObject()).getJid();

        if (c == cmdAdvJoin) {
			keyGreen();            
        } else if (c == cmdDel) {
            keyClear();
            setMainBarItem(new MainBar(2, null, SR.MS_BOOKMARKS + " (" + getItemCount() + ") ", false));
            return;
        }
//#ifdef SERVICE_DISCOVERY
        else if (c == cmdDisco) {
            discoCurrent();
        }
//#endif
        else if (c == cmdConfigure) {
            new QueryConfigForm(roomJid);
        } else if (c == cmdRoomOwners) {
            new Affiliations(roomJid, (short) 1);
        } else if (c == cmdRoomAdmins) {
            new Affiliations(roomJid, (short) 2);
        } else if (c == cmdRoomMembers) {
            new Affiliations(roomJid, (short) 3);
        } else if (c == cmdRoomBanned) {
            new Affiliations(roomJid, (short) 4);
        } else if (c == cmdSort) {
            sort(itemsList);
        } else if (c == cmdDoAutoJoin) {
            for (Enumeration e = itemsList.elements(); e.hasMoreElements();) {
                BookmarkItem bm = (BookmarkItem) e.nextElement();
                if (bm.autojoin) {
                    ConferenceForm.join(bm.name, bm.jid + '/' + bm.nick, bm.password, cf.confMessageCount);
                }
            }
            parentView = sd.roster;
            destroyView();
        } else if (c == cmdSave) {
            saveBookmarks();
        } else if (c == cmdUp) {
            move(-1);
            keyUp();
        } else if (c == cmdDwn) {
            move(+1);
            keyDwn();
        }
    }
    
    public void deleteBookmark() {
        BookmarkItem del = (BookmarkItem) getFocusedObject();
        if (del == null || del.isUrl) return;
        
        itemsList.removeElement(getFocusedObject());

        if (getItemCount() <= getCursor()) {
            moveCursorEnd();
        }

        saveBookmarks();
        redraw(); // TODO: need?
    }
	
	public void keyGreen() {
		BookmarkItem join = (BookmarkItem) getFocusedObject();
        new ConferenceForm(join, getCursor());
	}
	
    public void keyClear() {
        new AlertBox(SR.MS_DELETE, ((BookmarkItem) getFocusedObject()).getJid()) {

            public void yes() {
                deleteBookmark();
            }

            public void no() {
            }
        };
    }

    private void saveBookmarks() {
        sd.roster.bookmarks = itemsList;
        sd.roster.theStream.addBlockListener(new BookmarkQuery(BookmarkQuery.SAVE));
    }

    public void move(int offset) {
        try {
            int index = getCursor();
            BookmarkItem p1 = (BookmarkItem) getItemRef(index);
            BookmarkItem p2 = (BookmarkItem) getItemRef(index + offset);

            itemsList.setElementAt(p1, index + offset);
            itemsList.setElementAt(p2, index);
            saveBookmarks();
        } catch (Exception e) {/* IndexOutOfBounds */

        }
    }

//#ifdef SERVICE_DISCOVERY
    public void discoCurrent() {
        new ServiceDiscovery(((BookmarkItem) getFocusedObject()).getJid(), null, false);
    }
//#endif

    public void userKeyPressed(int keyCode) {
        switch (keyCode) {
            case 1:
                moveCursorHome();
                return;
            case 4:
                pageLeft();
                return;
            case 6:
                pageRight();
                return;
            case 7:
                moveCursorEnd();
                return;
            case VirtualCanvas._KEY_POUND:
                discoCurrent();
                return;
        }
        super.userKeyPressed(keyCode);
    }    
}
