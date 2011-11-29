/*
 * IqQueryRoster.java
 *
 * Created on 12.01.2005, 0:17
 *
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
 */
package xmpp;

import Client.Config;
import Client.Groups;
import Client.Jid;
import Client.Roster;
import Client.StaticData;
import com.alsutton.jabber.*;
import com.alsutton.jabber.datablocks.*;
import java.util.Vector;
import locale.SR;
import ui.SplashScreen;
import ui.VirtualCanvas;
import ui.VirtualList;

public class RosterDispatcher implements JabberBlockListener {

    public static final String NS_ROSTER = "jabber:iq:roster";

    public static Iq QueryRoster() {
        Iq result = new Iq(null, Iq.TYPE_GET, "getros");

        result.addChildNs("query", NS_ROSTER);
        return result;        
    }
    Roster roster = StaticData.getInstance().roster;
    Config cf = Config.getInstance();

    /** add to roster*/
    public static Iq QueryRoster(Jid jid, String name, String group, String subscription) {
        Iq result = new Iq(null, Iq.TYPE_SET, "addros");
        JabberDataBlock qB = result.addChildNs("query", NS_ROSTER);
        JabberDataBlock item = qB.addChild("item", null);
        item.setAttribute("jid", jid.bareJid);
        if (name != null) {
            item.setAttribute("name", name);
        }
        if (subscription != null)
        item.setAttribute("subscription", subscription);
        if (group != null) {
            item.addChild("group", group);
        }
        return result;
    }

    public int blockArrived(JabberDataBlock data) {
        if (data instanceof Iq) {
            String from = data.getAttribute("from");
            String type = data.getTypeAttribute();
            String id = data.getAttribute("id");

            if (type.equals("result")) {
                if (id.equals("getros")) {
                    //theStream.enableRosterNotify(false); //voffk

                    if (!processRoster(data)) {
                        return JabberBlockListener.BLOCK_REJECTED;
                    }

                    if (!cf.collapsedGroups) {
                        roster.groups.queryGroupState(true);
                    }

                    roster.setProgress(SR.MS_CONNECTED, 100);
                    roster.reEnumRoster();

                    roster.querysign = roster.doReconnect = false;

                    if (cf.loginstatus == 5) {
                        roster.sendPresence(Presence.PRESENCE_INVISIBLE, null);
                    } else {
                        roster.sendPresence(cf.loginstatus, null);
                    }
                    if (!VirtualCanvas.getInstance().isShown()) {
                        SplashScreen.getInstance().destroyView();
                    } else {
                        VirtualCanvas.getInstance().setList(roster);
                    }

                    return JabberBlockListener.BLOCK_PROCESSED;
                }
            } else if (type.equals("set")) {
                if (processRoster(data)) {
                    StaticData.getInstance().theStream.send(new Iq(from, Iq.TYPE_RESULT, id));
                    roster.reEnumRoster();
                    return JabberBlockListener.BLOCK_PROCESSED;
                }
            }
        }
        return BLOCK_REJECTED;
    }

    boolean processRoster(JabberDataBlock data) {
        JabberDataBlock q = data.findNamespace("query", NS_ROSTER);
        if (q == null) {
            return false;
        }
        int type = 0;

        //verifying from attribute as in RFC3921/7.2
        String from = data.getAttribute("from");
        if (from != null) {
            Jid fromJid = new Jid(from);
            if (fromJid.hasResource()) {
                if (!roster.myJid.equals(fromJid, true)) {
                    return false;
                }
            }
        }

        Vector cont = (q != null) ? q.getChildBlocks() : null;
        q = null;

        if (cont != null) {
            int j = cont.size();
            for (int ii = 0; ii < j; ii++) {
                JabberDataBlock i = (JabberDataBlock) cont.elementAt(ii);
                if (i.getTagName().equals("item")) {
                    String name = i.getAttribute("name");
                    String jid = i.getAttribute("jid");
                    String subscr = i.getAttribute("subscription");
                    boolean ask = (i.getAttribute("ask") != null);

                    String group = i.getChildBlockText("group");
                    if (group.length() == 0) {
                        group = Groups.COMMON_GROUP;
                    }

                    roster.updateContact(name, jid, group, subscr, ask);
                    //sort(hContacts);
                }
            }
        }
        VirtualList.sort(roster.hContacts);
        return true;
    }
}
