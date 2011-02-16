/*
 * IqGetVCard.java
 *
 * Created on 4.05.2005, 22:48
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

package xmpp.extensions;

import Client.Contact;
import Client.Groups;
import Client.Jid;
import Client.StaticData;
import VCard.VCard;
import VCard.VCardEdit;
import VCard.VCardView;
import com.alsutton.jabber.JabberBlockListener;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.datablocks.*;
import locale.SR;
import ui.VirtualList;
import ui.controls.AlertBox;
import xmpp.XmppError;

/**
 * Class representing the iq message block
 */

public class IqVCard implements JabberBlockListener
{
    public static JabberDataBlock query(String to, String id ) {
        Iq result = new Iq (to, Iq.TYPE_GET, id );
        result.addChildNs("vCard", VCard.NS_VCARD );
        return result;
    }

    public int blockArrived(JabberDataBlock data) {
        if( data instanceof Iq ) {
                String from = data.getAttribute("from");
                String type = data.getTypeAttribute();
                String id = data.getAttribute("id");
                    if (id.startsWith("nickvc")) {
                        if (type.equals("get") || type.equals("set")) return JabberBlockListener.BLOCK_REJECTED;
                        
                        VCard vc=new VCard(data);//.getNickName();
                        String nick=vc.getNickName();

                        Contact c=StaticData.getInstance().roster.findContact(new Jid(from), false);

                        String group=(c.getGroupType()==Groups.TYPE_NO_GROUP)? null: c.group.name;
                        if (nick!=null)  
                            StaticData.getInstance().roster.storeContact(from,nick,group, false);
                        //updateContact( nick, c.rosterJid, group, c.subscr, c.ask_subscribe);
                        StaticData.getInstance().roster.sendVCardReq();
                        return JabberBlockListener.BLOCK_PROCESSED;
                    }
                if (id.startsWith("getvc")) {
                        int index = id.indexOf(data.getAttribute("from"));
                        String matchedjid = id.substring(index, id.length());
                        String vcardFrom = data.getAttribute("from");
                        if (!(vcardFrom.equals(matchedjid) || vcardFrom.equals(new Jid(matchedjid).getBareJid())))
                            return JabberBlockListener.BLOCK_REJECTED;
                         if (type.equals("error")) {
                             StaticData.getInstance().roster.setQuerySign(false);
                            AlertBox alertBox = new AlertBox(SR.MS_ERROR, XmppError.findInStanza(data).toString()) {

                                                   public void yes() {
                                                       destroyView();
                                                   }

                                                   public void no() {
                                                       destroyView();
                                                   }
                                               };
                             return JabberBlockListener.BLOCK_PROCESSED;
                         }
                        if (type.equals("get") || type.equals("set") ) return JabberBlockListener.BLOCK_REJECTED;

                        StaticData.getInstance().roster.setQuerySign(false);
                        VCard vcard=new VCard(data);
                        String jid=id.substring(5);
                        Contact c=StaticData.getInstance().roster.getContact(jid, false); // drop unwanted vcards
                        if (c!=null) {
                            c.vcard=vcard;
                            if (StaticData.getInstance().canvas.getList() instanceof VirtualList) {
//                                if (c.getGroupType()==Groups.TYPE_SELF) { // Not able to edit VCard if self contact in roster
                                if (c.getJid().equals(StaticData.getInstance().roster.myJid.getJid())) {
                                    new VCardEdit(vcard);
                                } else {
                                    new VCardView(c);
                                }
                            }
                        } else {
                            new VCardView(c);
                        }
                        return JabberBlockListener.BLOCK_PROCESSED;
                    }
        }
        return BLOCK_REJECTED;
    }    
}
