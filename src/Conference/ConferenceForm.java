/*
 * ConferenceForm.java
 *
 * Created on 24.07.2005, 18:32
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
import Client.StaticData;
import com.alsutton.jabber.JabberDataBlock;
import locale.SR;
import com.alsutton.jabber.datablocks.Presence;
import ui.controls.form.CheckBox;
import ui.controls.form.DefForm;
import ui.controls.form.LinkString;
import ui.controls.form.NumberInput;
import ui.controls.form.PasswordInput;
import ui.controls.form.TextInput;
import Menu.MenuCommand;
//#ifdef PRIVACY
//# import PrivacyLists.QuickPrivacy;
//# import java.util.Vector;
//#endif
import ui.VirtualList;
import images.RosterIcons;
import xmpp.extensions.muc.Bookmark;
import xmpp.extensions.muc.Conference;

/**
 *
 * @author EvgS
 */
public class ConferenceForm
        extends DefForm {

    MenuCommand cmdJoin = new MenuCommand(SR.MS_JOIN, MenuCommand.SCREEN, 1, RosterIcons.ICON_APPEARING_INDEX);
    MenuCommand cmdAdd = new MenuCommand(SR.MS_ADD_BOOKMARK, MenuCommand.SCREEN, 5, RosterIcons.ICON_NEW);
    MenuCommand cmdEdit = new MenuCommand(SR.MS_SAVE, MenuCommand.SCREEN, 6, RosterIcons.ICON_ARCHIVE);
    private TextInput roomField;
    private TextInput hostField;
    private TextInput nickField;
    private TextInput nameField;
    private PasswordInput passField;
    private NumberInput msgLimitField;
    private CheckBox autoJoin;
    private LinkString linkJoin;
    Bookmark editConf;

    //private static boolean sndprs=false;
    /**
     * Creates a new instance of GroupChatForm
     */
    public ConferenceForm(String name, String confJid, String password, boolean autojoin) {
        super(SR.MS_JOIN_CONFERENCE);
        int roomEnd = confJid.indexOf('@');
        String room = "";
        if (roomEnd > 0) {
            room = confJid.substring(0, roomEnd);
        }
        String server;
        String nick = null;
        int serverEnd = confJid.indexOf('/');
        if (serverEnd > 0) {
            server = confJid.substring(roomEnd + 1, serverEnd);
            nick = confJid.substring(serverEnd + 1);
        } else {
            server = confJid.substring(roomEnd + 1);
        }
        createForm(name, room, server, nick, password, autojoin);
    }

    /**
     * Creates a new instance of GroupChatForm
     */
    public ConferenceForm(Bookmark join, int cursor) {
        super(SR.MS_JOIN_CONFERENCE);
        if (join == null) {
            return;
        }
        if (join.isUrl) {
            return;
        }

        this.editConf = join;
        moveCursorTo(cursor);

        int roomEnd = join.jid.indexOf('@');
        String room = "";
        if (roomEnd > 0) {
            room = join.jid.substring(0, roomEnd);
        }
        createForm(join.name, room, join.jid.substring(roomEnd + 1), join.nick, join.password, join.autojoin);
    }

    /**
     * Creates a new instance of GroupChatForm
     */
    public ConferenceForm() {
        super(SR.MS_JOIN_CONFERENCE);
        String room = cf.defGcRoom;
        String server = null;
        // trying to split string like room@server
        int roomE = room.indexOf('@');
        if (roomE > 0) {
            server = room.substring(roomE + 1);
            room = room.substring(0, roomE);
        }
        // default server
        if (server == null) {
            server = "conference." + sd.account.server;
        }
        createForm(null, room, server, null, null, false);
    }

    /**
     * Creates a new instance of GroupChatForm
     */
    public ConferenceForm(String name, String room, String server, String nick, String password, boolean autojoin) {
        super(SR.MS_JOIN_CONFERENCE);
        createForm(name, room, server, nick, password, autojoin);
    }

    private void createForm(String name, String room, String server, String nick, final String password, boolean autojoin) {
        roomField = new TextInput(SR.MS_ROOM, room, null);
        itemsList.addElement(roomField);

        hostField = new TextInput(SR.MS_AT_HOST, server, "muc-host");
        itemsList.addElement(hostField);

        if (nick == null) {
            nick = sd.account.getNickName();
        }
        nickField = new TextInput(SR.MS_NICKNAME, nick, "roomnick");
        itemsList.addElement(nickField);

        msgLimitField = new NumberInput(SR.MS_MSG_LIMIT, Integer.toString(cf.confMessageCount), 0, 100);
        itemsList.addElement(msgLimitField);

        nameField = new TextInput(SR.MS_DESCRIPTION, name, null);
        itemsList.addElement(nameField);

        passField = new PasswordInput(SR.MS_PASSWORD, password);
        itemsList.addElement(passField);

        autoJoin = new CheckBox(SR.MS_AUTOLOGIN, autojoin);
        itemsList.addElement(autoJoin);

        linkJoin = new LinkString(SR.MS_JOIN) {
            public void doAction() {
                Conference.join(nameField.getValue(),
                        roomField.getValue().trim() + "@" + hostField.getValue().trim()
                        + "/" + nickField.getValue(), passField.getValue(),
                        nickField.getValue(), Integer.parseInt(msgLimitField.getValue()));
                sd.roster.show();
            }
        };
        itemsList.addElement(linkJoin);
        moveCursorTo(getNextSelectableRef(-1));
    }

    public void menuAction(MenuCommand c, VirtualList d) {
        super.menuAction(c, d);

        String nick = nickField.getValue();
        String name = nameField.getValue();
        String host = hostField.getValue();
        String room = roomField.getValue();
        String pass = passField.getValue();
        int msgLimit = Integer.parseInt(msgLimitField.getValue());

        boolean autojoin = autoJoin.getValue();

        if (nick.length() == 0) {
            return;
        }
        if (room.length() == 0) {
            return;
        }
        if (host.length() == 0) {
            return;
        }

        StringBuffer gchat = new StringBuffer(room.trim()).append('@').append(host.trim());

        if (name.length() == 0) {
            name = gchat.toString();
        }

        saveMsgCount(msgLimit);

        if (c == cmdEdit) {
            sd.account.bookmarks.removeElement(editConf);
            Bookmark newItem = new Bookmark(name, gchat.toString(), nick, pass, autojoin);
            BookmarkItem item = new BookmarkItem(newItem);
            if (cursor < sd.account.bookmarks.size()) {
                sd.account.bookmarks.insertElementAt(newItem, cursor);
            } else {
                sd.account.bookmarks.addElement(newItem);
            }
            sd.theStream.addBlockListener(new BookmarkQuery(BookmarkQuery.SAVE));
            destroyView();
        } else if (c == cmdAdd) {
            new Bookmarks(new BookmarkItem(new Bookmark(name, gchat.toString(), nick, pass, autojoin)));
        } else if (c == cmdJoin) {
//#ifdef PRIVACY            
//#             if (!sd.account.isGoogle) {
//#                 if (QuickPrivacy.conferenceList == null) {
//#                     QuickPrivacy.conferenceList = new Vector();
//#                 }
//#                 QuickPrivacy.conferenceList.addElement(host);
//#                 new QuickPrivacy().updateQuickPrivacyList();
//#             }
//#endif                                    

            try {
                cf.defGcRoom = room + "@" + host;
                cf.saveToStorage();
                gchat.append('/').append(nick);
                Conference.join(name, gchat.toString(), pass, nick, msgLimit);
                sd.roster.show();
            } catch (Exception e) {
            }
        }
    }

    public void commandState() {
        menuCommands.removeAllElements();
        addMenuCommand(cmdJoin);
        addMenuCommand(cmdAdd);
        addMenuCommand(cmdEdit);
    }

    private void saveMsgCount(int msgLimit) {
        if (cf.confMessageCount != msgLimit) {
            cf.confMessageCount = msgLimit;
            cf.saveToStorage();
        }
    }
}
