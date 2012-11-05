/*
 * BookmarkItem.java
 *
 * Created on 17.09.2005, 23:21
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

import Client.StaticData;
import images.RosterIcons;
import ui.*;
import xmpp.extensions.muc.Bookmark;

/**
 *
 * @author EvgS
 */
public class BookmarkItem extends IconTextElement {

    protected final Bookmark bookmark;
    
    public BookmarkItem(Bookmark bookmark) {
        super(RosterIcons.getInstance());
        this.bookmark = bookmark;
    }
    
    public int getImageIndex() {
        if (bookmark.isUrl) {
            return RosterIcons.ICON_PRIVACY_ACTIVE;
        }
        return (bookmark.autojoin) ? RosterIcons.ICON_GCJOIN_INDEX : RosterIcons.ICON_GROUPCHAT_INDEX;
    }

    public String toString() {
        if (bookmark.name.length() > 0) {
            return bookmark.name;
        }

        return (bookmark.nick == null) ? bookmark.jid : bookmark.jid + '/' + bookmark.nick;
    }

    public String getJidNick() {
        return bookmark.jid + '/' + ((bookmark.nick.length() > 0) ? bookmark.nick : StaticData.getInstance().account.getNickName());
    }

    public String getJid() {
        return bookmark.jid;
    }            

    public int compare(IconTextElement right) {
        String th = (bookmark.nick == null) ? bookmark.jid : bookmark.jid + '/' + bookmark.nick;
        return th.compareTo(right.toString());
    }
}
