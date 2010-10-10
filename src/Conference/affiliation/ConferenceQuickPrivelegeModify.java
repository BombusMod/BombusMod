/*
 * ConferenceQuickPrivelegeModify.java
 *
 * Created on 12.11.2006, 19:02
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
package Conference.affiliation;

import Client.StaticData;
import Conference.*;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.datablocks.Iq;
import javax.microedition.lcdui.TextField;
import locale.SR;
import ui.controls.form.DefForm;
import ui.controls.form.MultiLine;
import ui.controls.form.TextInput;

/**
 *
 * @author Evg_S
 */
public class ConferenceQuickPrivelegeModify {

    public final static int KICK = 1;
    public final static int VISITOR = 2;
    public final static int PARTICIPANT = 3;
    public final static int MODERATOR = 4;
    public final static int OUTCAST = 5;
    public final static int NONE = 6;
    public final static int MEMBER = 7;
    public final static int ADMIN = 8;
    public final static int OWNER = 9;
    protected TextInput reason;
    private MucContact victim;
    private int action;
    private String myNick;

    /**
     * Creates a new instance of ConferenceQuickPrivelegeModify
     */
    public ConferenceQuickPrivelegeModify(MucContact victim, int action, String myNick) {

        this.victim = victim;
        this.action = action;
        this.myNick = myNick;

        String okName = SR.MS_OK;

        switch (action) {
            case KICK:
                okName = SR.MS_KICK;
                break;
            case OUTCAST:
                okName = SR.MS_BAN;
                break;
            default:
                setMucMod();
                return;
        } // switch

        new PrivelegeModifyForm(this, victim, okName);
    }

    public final void setMucMod() {
        JabberDataBlock iq = new Iq(victim.jid.getBareJid(), Iq.TYPE_SET, "itemmuc");
        JabberDataBlock query = iq.addChildNs("query", "http://jabber.org/protocol/muc#admin");
        //TODO: separate usecases to muc#owner, muc#admin and muc#moderator
        JabberDataBlock item = new JabberDataBlock("item", null, null);
        query.addChild(item);

        try {
            String rzn = reason.getValue();
            String Nick = "";
            if (rzn.startsWith("!")) {
                rzn = rzn.substring(1);
            } else {
                Nick = (myNick == null) ? myNick : myNick + ": ";
            }
            if (rzn.length() != 0 && myNick != null) {
                item.addChild("reason", Nick + rzn);
            } else {
                item.addChild("reason", Nick);
            }
        } catch (Exception e) {
        }

        switch (action) {
            case KICK:
                item.setAttribute("role", "none");
                item.setAttribute("nick", victim.nick);
                break;

            case OUTCAST:
                item.setAttribute("affiliation", "outcast");
                item.setAttribute("jid", victim.realJid);
                break;

            case PARTICIPANT:
                item.setAttribute("role", "participant");
                item.setAttribute("nick", victim.nick);
                break;

            case VISITOR:
                item.setAttribute("role", "visitor");
                item.setAttribute("nick", victim.nick);
                break;

            case MODERATOR:
                item.setAttribute("role", "moderator");
                item.setAttribute("nick", victim.nick);
                break;

            case MEMBER:
                item.setAttribute("affiliation", "member");
                item.setAttribute("jid", victim.realJid);
                break;

            case NONE:
                item.setAttribute("affiliation", "none");
                item.setAttribute("jid", victim.realJid);
                break;

            case ADMIN:
                item.setAttribute("affiliation", "admin");
                item.setAttribute("jid", victim.realJid);
                break;

            case OWNER:
                item.setAttribute("affiliation", "owner");
                item.setAttribute("jid", victim.realJid);

        }
        //System.out.println(iq);
        StaticData.getInstance().roster.theStream.send(iq);
    }
}

class PrivelegeModifyForm extends DefForm {

    ConferenceQuickPrivelegeModify cq;

    public PrivelegeModifyForm(ConferenceQuickPrivelegeModify cq, MucContact victim, String okName) {
        super(okName);
        this.cq = cq;
        getMainBarItem().setElementAt(okName, 0);

        StringBuffer user = new StringBuffer(victim.nick);
        if (victim.jid != null) {
            user.append(" (").append(victim.realJid).append(")");
        }
        itemsList.addElement(new MultiLine(SR.MS_USER, user.toString(), getListWidth()));
        cq.reason = new TextInput(SR.MS_REASON, "", "reason", TextField.ANY);
        itemsList.addElement(cq.reason);

    }

    public void cmdOk() {
        cq.setMucMod();
        parentView = sd.roster;
        destroyView();
    }
}
