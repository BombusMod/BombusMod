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
import xmpp.extensions.muc.BookmarkQuery;
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
import images.RosterIcons;
import xmpp.extensions.muc.Bookmark;
import xmpp.extensions.muc.Conference;

/**
 *
 * @author EvgS
 */
public class Bookmarks extends DefForm {

    private BookmarkItem toAdd;
    private MenuCommand cmdJoin = new MenuCommand(SR.MS_SELECT, MenuCommand.OK, 1, RosterIcons.ICON_APPEARING_INDEX);
    private MenuCommand cmdAdvJoin = new MenuCommand(SR.MS_EDIT_JOIN, MenuCommand.SCREEN, 2, RosterIcons.ICON_RENAME);
    private MenuCommand cmdNew = new MenuCommand(SR.MS_NEW_BOOKMARK, MenuCommand.SCREEN, 3, RosterIcons.ICON_NEW);
    private MenuCommand cmdDoAutoJoin = new MenuCommand(SR.MS_DO_AUTOJOIN, MenuCommand.SCREEN, 4, RosterIcons.ICON_PRESENCE_CHAT);
    private MenuCommand cmdConfigure = new MenuCommand(SR.MS_CONFIG_ROOM, MenuCommand.SCREEN, 5, RosterIcons.ICON_CONFIGURE);
//#ifdef SERVICE_DISCOVERY
    private MenuCommand cmdDisco = new MenuCommand(SR.MS_DISCO_ROOM, MenuCommand.SCREEN, 6, RosterIcons.ICON_DISCO);
//#endif
    private MenuCommand cmdUp = new MenuCommand(SR.MS_MOVE_UP, MenuCommand.SCREEN, 7, RosterIcons.ICON_SCROLLABLE_UP);
    private MenuCommand cmdDwn = new MenuCommand(SR.MS_MOVE_DOWN, MenuCommand.SCREEN, 8, RosterIcons.ICON_SCROLLABLE_DOWN);
    private MenuCommand cmdSort = new MenuCommand(SR.MS_SORT, MenuCommand.SCREEN, 9, RosterIcons.ICON_IE);
    private MenuCommand cmdSave = new MenuCommand(SR.MS_SAVE_LIST, MenuCommand.SCREEN, 10, RosterIcons.ICON_ARCHIVE);
    private MenuCommand cmdRoomOwners = new MenuCommand(SR.MS_OWNERS, MenuCommand.SCREEN, 11, RosterIcons.ICON_OWNERS);
    private MenuCommand cmdRoomAdmins = new MenuCommand(SR.MS_ADMINS, MenuCommand.SCREEN, 12, RosterIcons.ICON_ADMINS);
    private MenuCommand cmdRoomMembers = new MenuCommand(SR.MS_MEMBERS, MenuCommand.SCREEN, 13, RosterIcons.ICON_MEMBERS);
    private MenuCommand cmdRoomBanned = new MenuCommand(SR.MS_BANNED, MenuCommand.SCREEN, 14, RosterIcons.ICON_OUTCASTS);
    private MenuCommand cmdDel = new MenuCommand(SR.MS_DELETE, MenuCommand.SCREEN, 15, RosterIcons.ICON_DELETE);
    JabberStream stream = sd.getTheStream();

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

        mainbar = new MainBar(2, null, SR.MS_BOOKMARKS + " (" + getItemCount() + ") ", false);//for title updating after "add bookmark"

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
        itemsList.removeAllElements();
        for (Enumeration e = sd.account.bookmarks.elements(); e.hasMoreElements();) {
            BookmarkItem newItem = new BookmarkItem((Bookmark)e.nextElement());
            itemsList.addElement(newItem);
        }
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
        if (join.bookmark.isUrl) {
            return;
        }

        Conference.join(join.bookmark.name, join.getJidNick(), join.bookmark.password, join.bookmark.nick, cf.confMessageCount);
        parentView = sd.roster;
        destroyView();
    }
    
    public void eventLongOk() {
        if (getItemCount() == 0) {
            return;
        }
        showMenu();
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
            mainbar = new MainBar(2, null, SR.MS_BOOKMARKS + " (" + getItemCount() + ") ", false);
        }
//#ifdef SERVICE_DISCOVERY
        else if (c == cmdDisco) {
            discoCurrent();
        }
//#endif
        else if (c == cmdConfigure) {
            new QueryConfigForm(sd.getTheStream(), roomJid);
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
                if (bm.bookmark.autojoin) {
                    Conference.join(bm.bookmark.name, bm.bookmark.jid + '/' + bm.bookmark.nick, bm.bookmark.password, bm.bookmark.nick, cf.confMessageCount);
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
        if (del == null || del.bookmark.isUrl) return;
        
        itemsList.removeElement(getFocusedObject());

        if (getItemCount() <= cursor) {
            moveCursorEnd();
        }

        saveBookmarks();
        redraw(); // TODO: need?
    }
	
	public void keyGreen() {
		BookmarkItem join = (BookmarkItem) getFocusedObject();
        new ConferenceForm(join.bookmark, cursor);
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
        sd.account.bookmarks.removeAllElements();
        for (Enumeration e = itemsList.elements(); e.hasMoreElements();) {
            sd.account.bookmarks.addElement(((BookmarkItem)e.nextElement()).bookmark);
        }
        sd.getTheStream().addBlockListener(new BookmarkQuery(BookmarkQuery.SAVE));
    }

    public void move(int offset) {
        try {
            int index = cursor;
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

    public void doKeyAction(int keyCode) {
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
//#ifdef SERVICE_DISCOVERY                
                discoCurrent();
//#endif                
                return;
        }
        super.doKeyAction(keyCode);
    }

    public boolean doUserKeyAction(int command_id) {
        switch (command_id) {
//#ifdef SERVICE_DISCOVERY
            case 57:
                discoCurrent();
                return true;
//#endif
        }

        return super.doUserKeyAction(command_id);
    }

}
