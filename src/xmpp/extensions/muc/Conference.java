/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xmpp.extensions.muc;

import Client.StaticData;
import Conference.ConferenceGroup;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.datablocks.Presence;

/**
 *
 * @author Vitaly
 */
public class Conference {
    
    public final static String NS_MUC = "http://jabber.org/protocol/muc";

    public static void join(String name, String jid, String pass, String nick, int maxStanzas) {
        ConferenceGroup grp = StaticData.getInstance().roster.initMuc(jid, pass);
        grp.name = name;

        JabberDataBlock x = new JabberDataBlock("x");
        x.setNameSpace(NS_MUC);
        if (pass.length() != 0) {
            x.addChild("password", pass); // adding password to presence
        }

        JabberDataBlock history = x.addChild("history", null);
        history.setAttribute("maxstanzas", Integer.toString(maxStanzas));
        history.setAttribute("maxchars", "32768");
        try {
            long last = grp.confContact.lastMessageTime;
            long delay = (grp.conferenceJoinTime - last) / 1000;
            if (last != 0) {
                history.setAttribute("seconds", String.valueOf(delay)); // todo: change to since
            }
        } catch (Exception e) {
        }

        int status = StaticData.getInstance().roster.myStatus;
        if (status == Presence.PRESENCE_INVISIBLE) {
            status = Presence.PRESENCE_ONLINE;
        }
        StaticData.getInstance().roster.sendDirectPresence(status, jid, nick, x);

        grp.inRoom = true;

        //sd.roster.reEnumRoster();
    }
}
