/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Client;

import Conference.ConferenceGroup;
import Conference.MucContact;
import com.alsutton.jabber.JabberBlockListener;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.JabberListener;
import com.alsutton.jabber.datablocks.Iq;
import com.alsutton.jabber.datablocks.Message;
import com.alsutton.jabber.datablocks.Presence;
import images.ClientsIconsData;
import locale.SR;
//#ifdef NON_SASL_AUTH
//# import login.NonSASLAuth;
//#endif
import login.SASLAuth;
import ui.SplashScreen;
import ui.VirtualList;
import xmpp.XmppError;

/**
 *
 * @author Vitaly
 */
public class RosterListener implements JabberListener {

    Roster roster;

    Config cf = Config.getInstance();
    StaticData sd = StaticData.getInstance();

    public RosterListener (Roster roster) {
	this.roster = roster;
    }

    public void beginConversation() { //todo: verify xmpp version
	SplashScreen.getInstance().setExit(roster);
        if (roster.theStream.isXmppV1())
            roster.theStream.addBlockListener(new SASLAuth(sd.account, roster, roster.theStream));
//#if NON_SASL_AUTH
//#         else new NonSASLAuth(sd.account, roster, roster.theStream);
//#endif
    }

    public int blockArrived( JabberDataBlock data ) {
        try {
            if( data instanceof Iq ) {
                String from = data.getAttribute("from");
                String type = data.getTypeAttribute();
                String id = data.getAttribute("id");

                if ( type.equals( "result" ) ) {
                    if (id.equals("getros")) {
                        //theStream.enableRosterNotify(false); //voffk

                        if (!roster.processRoster(data))
                            return JabberBlockListener.BLOCK_REJECTED;

                        if(!cf.collapsedGroups)
                            roster.groups.queryGroupState(true);

                        roster.setProgress(SR.MS_CONNECTED,100);
                        roster.reEnumRoster();

                        roster.querysign = roster.doReconnect = false;

                        if (cf.loginstatus==5) {
                            roster.sendPresence(Presence.PRESENCE_INVISIBLE, null);
                        } else {
                            roster.sendPresence(cf.loginstatus, null);
                        }
                        if (!sd.canvas.isShown())
                            SplashScreen.getInstance().close(sd.canvas.getList());
                        else
                            sd.canvas.setList(roster);

                        return JabberBlockListener.BLOCK_PROCESSED;
                    }
                } else if (type.equals("set")) {
                    if (roster.processRoster(data)) {
                        roster.theStream.send(new Iq(from, Iq.TYPE_RESULT, id));
                        roster.reEnumRoster();
                        return JabberBlockListener.BLOCK_PROCESSED;
                    }
                }
            } else if( data instanceof Message ) { // If we've received a message
                //System.out.println(data.toString());
                roster.querysign = false;
                Message message = (Message) data;

                String from=message.getFrom();

                if (roster.myJid.equals(new Jid(from), false)) //Enable forwarding only from self-jids
                    from=message.getXFrom();

                String type=message.getTypeAttribute();

                boolean groupchat=false;

                int start_me=-1;
                String name=null;

                if (type!=null)
                    if (type.equals("groupchat"))
                        groupchat=true;

                if (groupchat) {
                    start_me=0;

                    int rp=from.indexOf('/');

                    name=from.substring(rp+1);

                    if (rp>0) from=from.substring(0, rp);
                }

                Contact c = roster.getContact(from, (cf.notInListDropLevel != NotInListFilter.DROP_MESSAGES_PRESENCES || groupchat
//#ifndef WMUC
                        || message.getMucInvitation() != null
//#endif
                        ));
                if (c==null) return JabberBlockListener.BLOCK_REJECTED; //not-in-list message dropped

                boolean highlite=false;

                String body=message.getBody().trim();
                String oob=message.getOOB();
                if (oob!=null) body+=oob;
                if (body.length()==0)
                    body=null;
                String subj=message.getSubject().trim();
                if (subj.length()==0)
                    subj=null;

                long tStamp=message.getMessageTime();

		int mType=Msg.MESSAGE_TYPE_IN;
                if (groupchat) {
                    if (subj!=null) { // subject
                        if (body==null)
                            body=name+" "+SR.MS_HAS_SET_TOPIC_TO+": "+subj;
                        if (!subj.equals(c.statusString)) {
                            c.statusString=subj; // adding secondLine to conference
                        } else {
                            return JabberBlockListener.BLOCK_PROCESSED;
                        }
                        subj=null;
                        start_me=-1;
                        highlite=true;
                        mType=Msg.MESSAGE_TYPE_SUBJ;
                    }
                } else if (type!=null){
                    if (type.equals("error")) {
                        body=SR.MS_ERROR_ + XmppError.findInStanza(message).toString();
                    } else if (type.equals("headline")) {
                        mType=Msg.MESSAGE_TYPE_HEADLINE;
                    }
                } else {
                    type="chat";
                }
//#ifndef WMUC
                 try {
                    JabberDataBlock xmlns=message.findNamespace("x", "http://jabber.org/protocol/muc#user");
                    JabberDataBlock invite=message.getMucInvitation();
                        if (invite!=null) {
                            if (message.getTypeAttribute().equals("error")) {
                                ConferenceGroup invConf=(ConferenceGroup)roster.groups.getGroup(from);
                                body=XmppError.decodeStanzaError(message).toString(); /*"error: invites are forbidden"*/
                            } else {
                                String inviteReason=invite.getChildBlockText("reason");
                                String room=from+'/'+sd.account.getNickName();

                                ConferenceGroup invConf = roster.initMuc(room, xmlns.getChildBlockText("password"));

                                invConf.confContact.commonPresence=false; //FS#761

                                c = invConf.confContact;

                                if (invConf.selfContact.status==Presence.PRESENCE_OFFLINE)
                                    invConf.confContact.status=Presence.PRESENCE_OFFLINE;

                                if (inviteReason!=null)
                                    inviteReason=(inviteReason.length()>0)?" ("+inviteReason+")":"";

                                body=invite.getAttribute("from")+SR.MS_IS_INVITING_YOU+from+inviteReason;

                                roster.reEnumRoster();
                            }
                        }
                } catch (Exception e) {
//#ifdef DEBUG
//#                     e.printStackTrace();
//#endif
                }
//#endif
                if (name==null) name=c.getName();
                // /me
                if (body!=null) {
                    //forme=false;
                    if (body.startsWith("/me ")) start_me=3;
                    if (start_me>=0) {
                        StringBuffer b=new StringBuffer();
//#if NICK_COLORS
                        b.append("\01");
//#endif
                        b.append(name);
//#if NICK_COLORS
                        b.append("\02");
//#endif
                        if (start_me==0) {
                            if (!cf.showBalloons) b.insert(0,"<");
                            b.append("> ");
                        }
                        else
                            b.insert(0,'*');
                        b.append(body.substring(start_me));
                        body=b.toString();
                        b=null;
                    }

                }
                //boolean compose=false;
                if (type.equals("chat") && roster.myStatus!=Presence.PRESENCE_INVISIBLE) {
                    if (message.findNamespace("request", "urn:xmpp:receipts")!=null) {
                        roster.sendDeliveryMessage(c, data.getAttribute("id"));
                    }
                    JabberDataBlock received = message.findNamespace("received", "urn:xmpp:receipts");
                    if (received!=null) {
                         c.markDelivered(data.getAttribute("id")); //FIXME: compatibilty with XEP-0184 version 1.0
                         c.markDelivered(received.getAttribute("id")); // XEP-0184 Version 1.1
                    }
                    if (message.findNamespace("active", "http://jabber.org/protocol/chatstates")!=null) {
                        c.acceptComposing=true;
                        c.showComposing=false;
//#ifdef RUNNING_MESSAGE
//#                         sd.roster.setTicker(c, "");
//#endif
                    }
                    if (message.findNamespace("paused", "http://jabber.org/protocol/chatstates")!=null) {
                        c.acceptComposing=true;
                        c.showComposing=false;
//#ifdef RUNNING_MESSAGE
//#                         sd.roster.setTicker(c, "");
//#endif
                    }
                    if (message.findNamespace("composing", "http://jabber.org/protocol/chatstates")!=null) {
                        roster.playNotify(Roster.SOUND_COMPOSING);
                        c.acceptComposing=true;
                        c.showComposing=true;
//#ifdef RUNNING_MESSAGE
//#                         sd.roster.setTicker(c, SR.MS_COMPOSING_NOTIFY);
//#endif
                    }
                }
                roster.redraw();

                if (body==null)
                    return JabberBlockListener.BLOCK_REJECTED;

                Msg m=new Msg(mType, from, subj, body);
                if (tStamp!=0)
                    m.dateGmt=tStamp;
//#ifndef WMUC
                if (m.body.indexOf(SR.MS_IS_INVITING_YOU)>-1) m.dateGmt=0;
                if (groupchat) {
                    ConferenceGroup mucGrp=(ConferenceGroup)c.group;
                    if (mucGrp.selfContact.getJid().equals(message.getFrom())) {
                        m.messageType = Msg.MESSAGE_TYPE_OUT;
                        m.unread = false;
                    } else {
                        if (m.dateGmt<= ((ConferenceGroup)c.group).conferenceJoinTime)
                            m.messageType=Msg.MESSAGE_TYPE_HISTORY;
                        // highliting messages with myNick substring
	                String myNick=mucGrp.selfContact.getName();
                        String myNick_=myNick+" ";
                        String _myNick=" "+myNick;
			if (body.indexOf(myNick) >= 0) {
			    highlite = true;
/*
                            if (body.indexOf("> "+myNick+": ")>-1)
                                highlite=true;
                            else if (body.indexOf(_myNick+",")>-1)
                                highlite=true;
                            else if (body.indexOf(": "+myNick+": ")>-1)
                                highlite=true;
                            else if (body.indexOf(_myNick+" ")>-1)
                                highlite=true;
                            else if (body.indexOf(", "+myNick)>-1)
                                highlite=true;
                            else if (body.endsWith(_myNick))
                                highlite=true;
                            else if (body.indexOf(_myNick+"?")>-1)
                                highlite=true;
                            else if (body.indexOf(_myNick+"!")>-1)
                                highlite=true;
                            else if (body.indexOf(_myNick+".")>-1)
                                highlite=true;
*/
			}
	                myNick=null; myNick_=null; _myNick=null;
                        //TODO: custom highliting dictionary
                    }
                    m.from=name;
                }
//#endif
//#ifdef JUICK
//# 		Juick.processMessage((Message)data, m);
//#endif
                m.highlite=highlite;
                roster.messageStore(c, m);
                return JabberBlockListener.BLOCK_PROCESSED;
            } else if( data instanceof Presence ) {  // If we've received a presence
                //System.out.println("presence");
                if (roster.myStatus == Presence.PRESENCE_OFFLINE)
                    return JabberBlockListener.BLOCK_REJECTED;

                Presence pr = (Presence) data;

                String from=pr.getFrom();
                pr.dispathch();
                int ti=pr.getTypeIndex();

                //PresenceContact(from, ti);
                Msg m=new Msg( (ti==Presence.PRESENCE_AUTH || ti==Presence.PRESENCE_AUTH_ASK)?Msg.MESSAGE_TYPE_AUTH:Msg.MESSAGE_TYPE_PRESENCE, from, null, pr.getPresenceTxt());
//#ifndef WMUC
                JabberDataBlock xmuc=pr.findNamespace("x", "http://jabber.org/protocol/muc#user");
                if (xmuc==null) xmuc=pr.findNamespace("x", "http://jabber.org/protocol/muc"); //join errors

                if (xmuc!=null) {
                    try {
                        MucContact c = roster.mucContact(from);

                        if (pr.getAttribute("ver")!=null) c.version=pr.getAttribute("ver"); // for bombusmod only
//#ifdef CLIENTS_ICONS
//#ifdef PLUGINS
//#                     if (sd.ClientsIcons)
//#endif
                        if (cf.showClientIcon) {
                            if (pr.hasEntityCaps()) {
                                roster.getClientIcon(c, pr.getEntityNode());
                                String presenceVer = pr.getEntityVer();
                                if (presenceVer != null)
                                    c.version = presenceVer;
                            }
                        }

//#endif
                        String lang=pr.getAttribute("xml:lang");

                        if (lang!=null) c.lang=lang;
                        lang=null;

                        c.statusString=pr.getStatus();

                        String chatPres=c.processPresence(xmuc, pr);
                        String type = data.getTypeAttribute();

                        if (cf.storeConfPresence
                                || chatPres.indexOf(SR.MS_WAS_BANNED)>-1
                                || chatPres.indexOf(SR.MS_WAS_KICKED)>-1
                                || (type != null && type.equals("error"))) {
                            int rp=from.indexOf('/');

                            String name=from.substring(rp+1);

                            Msg chatPresence=new Msg(Msg.MESSAGE_TYPE_PRESENCE, name, null, chatPres );
                            chatPresence.color=c.getMainColor();
                            roster.messageStore(roster.getContact(from.substring(0, rp), false), chatPresence);
                            name=null;
                        }

                        chatPres=null;

                        roster.messageStore(c,m);

                        c.priority=pr.getPriority();
                        //System.gc();
                        //Thread.sleep(20);
                    } catch (Exception e) {
//#ifdef DEBUG
//#                         e.printStackTrace();
//#endif
                    }
                } else {
//#endif
                    Contact c=null;

                     if (ti==Presence.PRESENCE_AUTH_ASK) {
                        //processing subscriptions
                        if (cf.autoSubscribe==Config.SUBSCR_DROP)
                            return JabberBlockListener.BLOCK_REJECTED;

                        if (cf.autoSubscribe==Config.SUBSCR_REJECT) {
//#ifdef DEBUG
//#                             System.out.print(from);
//#                             System.out.println(": decline subscription");
//#endif
                            roster.sendPresence(from, "unsubscribed", null, false);
                            return JabberBlockListener.BLOCK_PROCESSED;
                        }

                        c= roster.getContact(from, true);

                        roster.messageStore(c, m);

                        if (cf.autoSubscribe==Config.SUBSCR_AUTO) {
                             roster.doSubscribe(c);
                             roster.messageStore(c, new Msg(Msg.MESSAGE_TYPE_AUTH, from, null, SR.MS_AUTH_AUTO));
                         }
                    } else {
                        // processing presences
                        boolean enNIL = cf.notInListDropLevel > NotInListFilter.DROP_PRESENCES;
                        c = roster.getContact(from, enNIL);

                        if (c==null) return JabberBlockListener.BLOCK_REJECTED; //drop not-in-list presence

                        if (pr.getAttribute("ver")!=null) c.version=pr.getAttribute("ver");  // for bombusmod only

                        if (pr.getTypeIndex()!=Presence.PRESENCE_ERROR) {
//#ifdef CLIENTS_ICONS
//#ifdef PLUGINS
//#                         if (sd.ClientsIcons)
//#endif
                            if (cf.showClientIcon) if (ti<Presence.PRESENCE_OFFLINE)
                                if (pr.hasEntityCaps()) {
                                    if (pr.getEntityNode()!=null) {
                                        ClientsIconsData.processData(c, pr.getEntityNode());
                                        if (pr.getEntityVer()!=null)
                                            c.version=pr.getEntityVer();
                                    }
                                } else if (c.jid.hasResource()) {
                                    ClientsIconsData.processData(c, c.getResource().substring(1));
                                }
//#endif
                            JabberDataBlock j2j=pr.findNamespace("x", "j2j:history");
                            if (j2j!=null) {
                                if (j2j.getChildBlock("jid")!=null)
                                    c.j2j=j2j.getChildBlock("jid").getAttribute("gateway");
                            }
                            j2j=null;

                            String lang=pr.getAttribute("xml:lang");
//#if DEBUG
//#                             //System.out.println("xml:lang="+lang); // Very much output!
//#endif
                            c.lang=lang; lang=null;

                            c.statusString=pr.getStatus();
                        }

                        roster.messageStore(c, m);
                     }

                    c.priority=pr.getPriority();
                    if (ti>=0)
                        c.setStatus(ti);

                    if (c.nick==null && c.status<=Presence.PRESENCE_DND) {
                        JabberDataBlock nick = pr.findNamespace("nick", "http://jabber.org/protocol/nick");
                        if (nick!=null) c.nick=nick.getText();

                    }

                    if ((ti==Presence.PRESENCE_ONLINE || ti==Presence.PRESENCE_CHAT) && roster.notifyReady(-111)) {
//#if USE_ROTATOR
                        if (cf.notifyBlink)
                            c.setNewContact();
//#endif
                        if (cf.notifyPicture) {
                            if (c.getGroupType()!=Groups.TYPE_TRANSP)
                                c.setIncoming(Contact.INC_APPEARING);
                        }
                    }
                    if (ti==Presence.PRESENCE_OFFLINE)  {
                        c.setIncoming(Contact.INC_NONE);
                        c.showComposing=false;
                    }
                    if (ti>=0) {
//#ifdef RUNNING_MESSAGE
//#                         if (ti==Presence.PRESENCE_OFFLINE)
//#                             sd.roster.setTicker(c, SR.MS_OFFLINE);
//#                         else if (ti==Presence.PRESENCE_ONLINE)
//#                             sd.roster.setTicker(c, SR.MS_ONLINE);
//#endif
                        if ((ti==Presence.PRESENCE_ONLINE || ti==Presence.PRESENCE_CHAT || ti==Presence.PRESENCE_OFFLINE) && (!c.jid.isTransport()) && (c.getGroupType()!=Groups.TYPE_IGNORE))
                            roster.playNotify(ti);
                    }

//#ifndef WMUC
                }
//#endif
                if (cf.autoClean) {
                    roster.cleanAllGroups();
                    VirtualList.sort(roster.hContacts);
                }
                else {
                    VirtualList.sort(roster.hContacts);
                    roster.reEnumRoster();
                }
                return JabberBlockListener.BLOCK_PROCESSED;
            } // if presence
        } catch( Exception e ) {
//#if DEBUG
//#             e.printStackTrace();
//#endif
        }
        return JabberBlockListener.BLOCK_REJECTED;
    }

    public void connectionTerminated(Exception e) {
        if (e != null) {
            roster.errorLog("Exception in parser: " + e.getMessage());
            roster.askReconnect(e);
        } else {
            roster.setProgress(SR.MS_DISCONNECTED, 0);
            try {
                roster.sendPresence(Presence.PRESENCE_OFFLINE, null);
            } catch (Exception e2) {
//#if DEBUG
//#                 e2.printStackTrace();
//#endif
            }
        }
        roster.redraw();
    }

    public void dispatcherException(Exception e, JabberDataBlock dataBlock) {
        roster.errorLog("JabberDataBlockDispatcher exception\ndataBlock: " + dataBlock.toString());
//#ifdef DEBUG
//# 	System.out.println("JabberDataBlockDispatcher exception\ndataBlock: " + dataBlock.toString());
//# 	e.printStackTrace();
//#endif
    }

}
