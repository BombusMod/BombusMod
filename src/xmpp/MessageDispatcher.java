/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xmpp;

import Client.Config;
import Client.Contact;
import Client.Jid;
import Client.Msg;
import Client.NotInListFilter;
import Client.Roster;
import Client.StaticData;
import Conference.ConferenceGroup;
import com.alsutton.jabber.JabberBlockListener;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.datablocks.Message;
import com.alsutton.jabber.datablocks.Presence;
import locale.SR;

/**
 *
 * @author Vitaly
 */
public class MessageDispatcher implements JabberBlockListener {
    
    private Roster roster = StaticData.getInstance().roster;
    private Config cf = Config.getInstance();

    public int blockArrived(JabberDataBlock data) {
        if (data instanceof Message) { // If we've received a message
            //System.out.println(data.toString());
            roster.querysign = false;
            Message message = (Message) data;

            String from = message.getFrom();

            if (roster.myJid.equals(new Jid(from), false)) //Enable forwarding only from self-jids
            {
                from = message.getXFrom();
            }

            String type = message.getTypeAttribute();

            boolean groupchat = false;

            int start_me = -1;
            String name = null;

            if (type != null) {
                if (type.equals("groupchat")) {
                    groupchat = true;
                }
            }

            int mType = Msg.MESSAGE_TYPE_IN;
            //#ifndef WMUC               
            if (groupchat) {
                if (from.equals(roster.groups.getConfGroup(new Jid(from)).jid.bareJid)) {
                    mType = Msg.MESSAGE_TYPE_SYSTEM;
                }
                start_me = 0;
                int rp = from.indexOf('/');
                name = from.substring(rp + 1);
                if (rp > 0) {
                    from = from.substring(0, rp);
                }
            }
//#endif                

            Contact c = roster.getContact(from, (cf.notInListDropLevel != NotInListFilter.DROP_MESSAGES_PRESENCES || groupchat
//#ifndef WMUC
                    || message.getMucInvitation() != null
//#endif
                    ));
            if (c == null) {
                return JabberBlockListener.BLOCK_REJECTED; //not-in-list message dropped
            }
            boolean highlite = false;

            String body = message.getBody().trim();
            String oob = message.getOOB();
            if (oob != null) {
                body += oob;
            }
            if (body.length() == 0) {
                body = null;
            }
            String subj = message.getSubject().trim();
            if (subj.length() == 0) {
                subj = null;
            }

            long tStamp = message.getMessageTime();

            if (groupchat) {
                if (subj != null) { // subject
                    if (body == null) {
                        body = name + " " + SR.MS_HAS_SET_TOPIC_TO + ": " + subj;
                    }
                    if (!subj.equals(c.statusString)) {
                        c.statusString = subj; // adding secondLine to conference
                    } else {
                        return JabberBlockListener.BLOCK_PROCESSED;
                    }
                    subj = null;
                    start_me = -1;
                    highlite = true;
                    mType = Msg.MESSAGE_TYPE_SUBJ;
                }
            } else if (type != null) {
                if (type.equals("error")) {
                    body = SR.MS_ERROR_ + XmppError.findInStanza(message).toString();
                } else if (type.equals("headline")) {
                    mType = Msg.MESSAGE_TYPE_HEADLINE;
                }
            } else {
                type = "chat";
            }
//#ifndef WMUC
            try {
                JabberDataBlock xmlns = message.findNamespace("x", "http://jabber.org/protocol/muc#user");
                JabberDataBlock invite = message.getMucInvitation();
                if (invite != null) {
                    if (message.getTypeAttribute().equals("error")) {
                        ConferenceGroup invConf = (ConferenceGroup) roster.groups.getGroup(from);
                        body = XmppError.decodeStanzaError(message).toString(); /*
                         * "error: invites are forbidden"
                         */
                    } else {
                        String inviteReason = invite.getChildBlockText("reason");
                        String room = from + '/' + StaticData.getInstance().account.getNickName();

                        ConferenceGroup invConf = roster.initMuc(room, xmlns.getChildBlockText("password"));

                        invConf.confContact.commonPresence = false; //FS#761

                        c = invConf.confContact;

                        if (invConf.selfContact.status == Presence.PRESENCE_OFFLINE) {
                            invConf.confContact.status = Presence.PRESENCE_OFFLINE;
                        }

                        if (inviteReason != null) {
                            inviteReason = (inviteReason.length() > 0) ? " (" + inviteReason + ")" : "";
                        }

                        body = invite.getAttribute("from") + SR.MS_IS_INVITING_YOU + from + inviteReason;

                        roster.reEnumRoster();
                    }
                }
            } catch (Exception e) {
            }
//#endif
            if (name == null) {
                name = c.getName();
            }
            // /me
            if (!cf.showNickNames) {
                if (body != null) {
                    //forme=false;
                    if (body.startsWith("/me ")) {
                        start_me = 3;
                    }
                    if (start_me >= 0) {
                        StringBuffer b = new StringBuffer();
                        Msg.appendNick(b, name);
                        if (start_me == 0) {
                            if (!cf.hideTimestamps) {
                                b.insert(0, "<");
                            }
                            b.append("> ");
                        } else {
                            b.insert(0, '*');
                        }
                        b.append(body.substring(start_me));
                        body = b.toString();
                    }

                }
            }
            //boolean compose=false;
            if (type.equals("chat") && roster.myStatus != Presence.PRESENCE_INVISIBLE) {
                if (message.findNamespace("request", Message.NS_RECEIPTS) != null) {
                    roster.sendDeliveryMessage(c, data.getAttribute("id"));
                }
                JabberDataBlock received = message.findNamespace("received", Message.NS_RECEIPTS);
                if (received != null) {
                    c.markDelivered(data.getAttribute("id")); //FIXME: compatibilty with XEP-0184 version 1.0
                    c.markDelivered(received.getAttribute("id")); // XEP-0184 Version 1.1
                }
                if (message.findNamespace("active", Message.NS_CHATSTATES) != null) {
                    c.acceptComposing = true;
                    c.showComposing = false;
//#ifdef RUNNING_MESSAGE
//#                         setTicker(c, "");
//#endif
                }
                if (message.findNamespace("paused", Message.NS_CHATSTATES) != null) {
                    c.acceptComposing = true;
                    c.showComposing = false;
//#ifdef RUNNING_MESSAGE
//#                         setTicker(c, "");
//#endif
                }
                if (message.findNamespace("composing", Message.NS_CHATSTATES) != null) {
                    roster.playNotify(Roster.SOUND_COMPOSING);
                    c.acceptComposing = true;
                    c.showComposing = true;
//#ifdef RUNNING_MESSAGE
//#                         setTicker(c, SR.MS_COMPOSING_NOTIFY);
//#endif
                }
            }
            roster.redraw();

            if (body == null) {
                return JabberBlockListener.BLOCK_REJECTED;
            }

            Msg m = new Msg(mType, from, subj, body);
            if (tStamp != 0) {
                m.dateGmt = tStamp;
            }
//#ifndef WMUC
            if (m.body.indexOf(SR.MS_IS_INVITING_YOU) > -1) {
                m.dateGmt = 0;
            }
            if (groupchat) {
                ConferenceGroup mucGrp = (ConferenceGroup) c.group;
                if (mucGrp.selfContact.getJid().equals(new Jid(message.getFrom()), true)) {
                    m.messageType = Msg.MESSAGE_TYPE_OUT;
                    m.unread = false;
                } else {
                    if (m.dateGmt <= ((ConferenceGroup) c.group).conferenceJoinTime) {
                        m.messageType = Msg.MESSAGE_TYPE_HISTORY;
                    }
                    // highliting messages with myNick substring
                    String myNick = mucGrp.selfContact.getName();
                    String myNick_ = myNick + " ";
                    String _myNick = " " + myNick;
                    if (body.indexOf(myNick) >= 0) {
                        highlite = true;
                        /*
                         * if (body.indexOf("> "+myNick+": ")>-1) highlite=true;
                         * else if (body.indexOf(_myNick+",")>-1) highlite=true;
                         * else if (body.indexOf(": "+myNick+": ")>-1)
                         * highlite=true; else if (body.indexOf(_myNick+" ")>-1)
                         * highlite=true; else if (body.indexOf(", "+myNick)>-1)
                         * highlite=true; else if (body.endsWith(_myNick))
                         * highlite=true; else if (body.indexOf(_myNick+"?")>-1)
                         * highlite=true; else if (body.indexOf(_myNick+"!")>-1)
                         * highlite=true; else if (body.indexOf(_myNick+".")>-1)
                         * highlite=true;
                         */
                    }
                    myNick = null;
                    myNick_ = null;
                    _myNick = null;
                    //TODO: custom highliting dictionary
                }
                m.from = name;
            }
//#endif
            m.highlite = highlite;
            roster.messageStore(c, m);
            return JabberBlockListener.BLOCK_PROCESSED;
        }
        return JabberBlockListener.BLOCK_REJECTED;
    }
    
}
