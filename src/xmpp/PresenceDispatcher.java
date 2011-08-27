/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xmpp;

import Client.Config;
import Client.Contact;
import Client.Groups;
import Client.Msg;
import Client.NotInListFilter;
import Client.Roster;
import Client.StaticData;
import Conference.MucContact;
import com.alsutton.jabber.JabberBlockListener;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.datablocks.Presence;
//#ifdef CLIENTS_ICONS
//# import images.ClientsIconsData;
//#endif
import locale.SR;
import ui.VirtualList;

/**
 *
 * @author Vitaly
 */
public class PresenceDispatcher implements JabberBlockListener {
    
    Roster roster = StaticData.getInstance().roster;
    Config cf = Config.getInstance();
        
    public int blockArrived(JabberDataBlock data) {
        if (data instanceof Presence) {  // If we've received a presence
                //System.out.println("presence");
                if (roster.myStatus == Presence.PRESENCE_OFFLINE) {
                    return JabberBlockListener.BLOCK_REJECTED;
                }

                Presence pr = (Presence) data;

                String from = pr.getFrom();
                pr.dispathch();
                int ti = pr.getTypeIndex();

                //PresenceContact(from, ti);
                Msg m = new Msg((ti == Presence.PRESENCE_AUTH || ti == Presence.PRESENCE_AUTH_ASK) ? Msg.MESSAGE_TYPE_AUTH : Msg.MESSAGE_TYPE_PRESENCE, from, null, pr.getPresenceTxt());
//#ifndef WMUC
                JabberDataBlock xmuc = pr.findNamespace("x", "http://jabber.org/protocol/muc#user");
                if (xmuc == null) {
                    xmuc = pr.findNamespace("x", "http://jabber.org/protocol/muc"); //join errors
                }
                if (xmuc != null) {
                    try {
                        MucContact c = roster.mucContact(from);

                        if (pr.getAttribute("ver") != null) {
                            c.version = pr.getAttribute("ver"); // for bombusmod only
                        }
//#ifdef CLIENTS_ICONS
//#                         if (Config.getInstance().showClientIcon) {
//#                             if (pr.hasEntityCaps()) {
//#                                 roster.getClientIcon(c, pr.getEntityNode());
//#                                 String presenceVer = pr.getEntityVer();
//#                                 if (presenceVer != null) {
//#                                     c.version = presenceVer;
//#                                 }
//#                             }
//#                         }
//# 
//#endif
                        String lang = pr.getAttribute("xml:lang");

                        if (lang != null) {
                            c.lang = lang;
                        }
                        lang = null;

                        c.statusString = pr.getStatus();

                        String chatPres = c.processPresence(xmuc, pr);
                        String type = data.getTypeAttribute();

                        if (Config.getInstance().storeConfPresence
                                || chatPres.indexOf(SR.MS_WAS_BANNED) > -1
                                || chatPres.indexOf(SR.MS_WAS_KICKED) > -1
                                || (type != null && type.equals("error"))) {
                            int rp = from.indexOf('/');

                            String name = from.substring(rp + 1);

                            Msg chatPresence = new Msg(Msg.MESSAGE_TYPE_PRESENCE, name, null, chatPres);
                            chatPresence.color = c.getMainColor();
                            roster.messageStore(roster.getContact(from.substring(0, rp), false), chatPresence);
                            name = null;
                        }

                        chatPres = null;

                        roster.messageStore(c, m);

                        c.priority = pr.getPriority();
                        //System.gc();
                        //Thread.sleep(20);
                    } catch (Exception e) {
//#ifdef DEBUG
//#                         e.printStackTrace();
//#endif                        
                    }
                } else {
//#endif
                    Contact c = null;

                    if (ti == Presence.PRESENCE_AUTH_ASK) {
                        //processing subscriptions
                        if (Config.getInstance().autoSubscribe == Config.SUBSCR_DROP) {
                            return JabberBlockListener.BLOCK_REJECTED;
                        }

                        if (Config.getInstance().autoSubscribe == Config.SUBSCR_REJECT) {
//#ifdef DEBUG 
//#                             System.out.print(from);
//#                             System.out.println(": decline subscription");
//#endif
                            roster.sendPresence(from, "unsubscribed", null, false);
                            return JabberBlockListener.BLOCK_PROCESSED;
                        }

                        c = roster.getContact(from, true);

                        if (cf.autoSubscribe != Config.SUBSCR_AUTO) {
                            roster.messageStore(c, m);
                        } else {
                            roster.doSubscribe(c);
                        }

                    } else {
                        // processing presences
                        boolean enNIL = cf.notInListDropLevel > NotInListFilter.DROP_PRESENCES;
                        c = roster.getContact(from, enNIL);

                        if (c == null) {
                            return JabberBlockListener.BLOCK_REJECTED; //drop not-in-list presence
                        }
                        if (pr.getAttribute("ver") != null) {
                            c.version = pr.getAttribute("ver");  // for bombusmod only
                        }
                        if (ti != Presence.PRESENCE_ERROR) {
//#ifdef CLIENTS_ICONS
//#                             if (cf.showClientIcon) {
//#                                 if (ti < Presence.PRESENCE_OFFLINE) {
//#                                     if (pr.hasEntityCaps()) {
//#                                         if (pr.getEntityNode() != null) {
//#                                             ClientsIconsData.processData(c, pr.getEntityNode());
//#                                             if (pr.getEntityVer() != null) {
//#                                                 c.version = pr.getEntityVer();
//#                                             }
//#                                         }
//#                                     } else if (c.jid.hasResource()) {
//#                                         ClientsIconsData.processData(c, c.getResource().substring(1));
//#                                     }
//#                                 }
//#                             }
//#endif
                            JabberDataBlock j2j = pr.findNamespace("x", "j2j:history");
                            if (j2j != null) {
                                if (j2j.getChildBlock("jid") != null) {
                                    c.j2j = j2j.getChildBlock("jid").getAttribute("gateway");
                                }
                            }
                            j2j = null;

                            String lang = pr.getAttribute("xml:lang");
//#if DEBUG
//#                             //System.out.println("xml:lang="+lang); // Very much output!
//#endif
                            c.lang = lang;
                            lang = null;

                            c.statusString = pr.getStatus();
                        }
                        if (ti == Presence.PRESENCE_AUTH) {
                            if (cf.autoSubscribe == Config.SUBSCR_AUTO) {
                                return JabberBlockListener.BLOCK_PROCESSED;
                            }
                        }

                        roster.messageStore(c, m);

                    }

                    c.priority = pr.getPriority();
                    if (ti >= 0) {
                        c.setStatus(ti);
                    }

                    if (c.nick == null && c.status <= Presence.PRESENCE_DND) {
                        JabberDataBlock nick = pr.findNamespace("nick", "http://jabber.org/protocol/nick");
                        if (nick != null) {
                            c.nick = nick.getText();
                            roster.storeContact(from, c.nick, c.group.name, false);
                        } else {
                            nick = pr.getChildBlockChild("nickname"); // PyICQ-t workaround
                            if (nick != null) {
                                c.nick = nick.getText();
                                roster.storeContact(from, c.nick, c.group.name, false);
                            }
                        }
                    }

                    if ((ti == Presence.PRESENCE_ONLINE || ti == Presence.PRESENCE_CHAT) && roster.notifyReady(-111)) {
//#if USE_ROTATOR
//#                         if (cf.notifyBlink) {
//#                             c.setNewContact();
//#                         }
//#endif
                        if (cf.notifyPicture) {
                            if (c.getGroupType() != Groups.TYPE_TRANSP) {
                                c.setIncoming(Contact.INC_APPEARING);
                            }
                        }
                    }
                    if (ti == Presence.PRESENCE_OFFLINE) {
                        c.setIncoming(Contact.INC_NONE);
                        c.showComposing = false;
                    }
                    if (ti >= 0) {
//#ifdef RUNNING_MESSAGE
//#                         if (ti==Presence.PRESENCE_OFFLINE)
//#                             setTicker(c, SR.MS_OFFLINE);
//#                         else if (ti==Presence.PRESENCE_ONLINE)
//#                             setTicker(c, SR.MS_ONLINE);
//#endif
                        if ((ti == Presence.PRESENCE_ONLINE || ti == Presence.PRESENCE_CHAT || ti == Presence.PRESENCE_OFFLINE) && (!c.jid.isTransport()) && (c.getGroupType() != Groups.TYPE_IGNORE)) {
                            roster.playNotify(ti);
                        }
                    }

//#ifndef WMUC
                }
//#endif
                if (cf.autoClean) {
                    roster.cleanAllGroups();
                    VirtualList.sort(roster.hContacts);
                } else {
                    VirtualList.sort(roster.hContacts);
                    roster.reEnumRoster();
                }
                return JabberBlockListener.BLOCK_PROCESSED;
            } // if presence
        
        return BLOCK_REJECTED;
    }
    
}
