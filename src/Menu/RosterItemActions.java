/*
 * RosterItemActions.java
 *
 * Created on 11.12.2005, 19:05
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

package Menu;
import Client.*;
import Colors.ColorTheme;
//#ifndef WMUC
import Conference.ConferenceGroup;
import Conference.InviteForm;
import Conference.MucContact;
import Conference.QueryConfigForm;
import Conference.affiliation.ConferenceQuickPrivelegeModify;
//#ifdef REQUEST_VOICE
//# import Conference.QueryRequestVoice;
//#endif
import Conference.affiliation.Affiliations;
//#endif
//#ifdef SERVICE_DISCOVERY
//# import ServiceDiscovery.*;
//#endif

import images.RosterIcons;

//#if FILE_TRANSFER
//#ifndef NOMMEDIA
//# import io.file.transfer.TransferImage;
//#endif
//# import io.file.transfer.TransferSendFile;
//#endif

import ui.controls.AlertBox;

import xmpp.extensions.IqLast;
import xmpp.extensions.IqPing;
import xmpp.extensions.IqTimeReply;
import xmpp.extensions.IqVersionReply;

import com.alsutton.jabber.datablocks.Presence;

import java.util.Enumeration;
import locale.SR;

import VCard.VCard;
import VCard.VCardEdit;
import VCard.VCardView;


/**
 *
 * @author EvgS
 */
public class RosterItemActions extends Menu {

    Object item;
    
    RosterIcons menuIcons = RosterIcons.getInstance();
    
    /** Creates a new instance of RosterItemActions
     * @param item
     * @param action
     */
    public RosterItemActions(Object item) {
        super(item.toString(), RosterIcons.getInstance());

        this.item=item;
        if (!sd.roster.isLoggedIn()) return;
	
        if (item==null) return;
        boolean isContact=( item instanceof Contact );

	if (isContact) {
	    Contact contact=(Contact)item;
	    if (contact.jid.isTransport()) {
		addItem(SR.MS_LOGON,5, RosterIcons.ICON_ON);
		addItem(SR.MS_LOGOFF,6, RosterIcons.ICON_OFF);
                addItem(SR.MS_RESOLVE_NICKNAMES, 7, RosterIcons.ICON_NICK_RESOLVE);
//#if CHANGE_TRANSPORT
//#                     addItem("Change transport", 915, RosterIcons.ICON_COMMAND);
//#endif
	    }
	    addItem(SR.MS_VCARD,1, RosterIcons.ICON_VCARD);
//#ifdef POPUPS
//#             addItem(SR.MS_INFO,86, RosterIcons.ICON_INFO);
//#endif
//#ifdef STATUSES_WINDOW            
//#             addItem("Statuses", 87, RosterIcons.ICON_INFO);
//#endif            
            addItem(SR.MS_CLIENT_INFO,0, RosterIcons.ICON_VERSION);
//#ifdef SERVICE_DISCOVERY
//# 	    addItem(SR.MS_COMMANDS,30, RosterIcons.ICON_COMMAND);
//#endif
//#ifdef CLIPBOARD
//#             if (!sd.clipboard.isEmpty()) {
//#                 addItem(SR.MS_SEND_BUFFER, 914, RosterIcons.ICON_SEND_BUFFER);
//#             }
//#             addItem(SR.MS_COPY_JID, 892, RosterIcons.ICON_COPY_JID);
//# 
//#endif
            addItem(SR.MS_SEND_COLOR_SCHEME, 912, RosterIcons.ICON_SEND_COLORS);
            if (contact.status<Presence.PRESENCE_OFFLINE) {
                addItem(SR.MS_TIME,891, RosterIcons.ICON_TIME);
                addItem(SR.MS_IDLE,889, RosterIcons.ICON_IDLE);
                addItem(SR.MS_PING,893, RosterIcons.ICON_PING);
            }
	    
	    if (contact.getGroupType()!=Groups.TYPE_SELF && contact.getGroupType()!=Groups.TYPE_SEARCH_RESULT && contact.origin<Contact.ORIGIN_GROUPCHAT) {
		if (contact.status<Presence.PRESENCE_OFFLINE) {
                    addItem(SR.MS_ONLINE_TIME,890, RosterIcons.ICON_ONLINE);
                } else {
                    addItem(SR.MS_SEEN,894, RosterIcons.ICON_ONLINE);
                }
                if (!contact.jid.isTransport()) {
                    addItem(SR.MS_EDIT,2, RosterIcons.ICON_RENAME);
                }
		addItem(SR.MS_SUBSCRIPTION,3, RosterIcons.ICON_SUBSCR);
//		addItem(SR.MS_MOVE,1003, menuIcons.ICON_MOVE);
		addItem(SR.MS_DELETE, 4, RosterIcons.ICON_DELETE);
		addItem(SR.MS_DIRECT_PRESENCE,45, RosterIcons.ICON_SET_STATUS);
	    }
	    if (contact.origin==Contact.ORIGIN_GROUPCHAT) 
                return;
//#ifndef WMUC
            boolean onlineConferences=false;
            for (Enumeration cI=sd.roster.hContacts.elements(); cI.hasMoreElements(); ) {
                try {
                    MucContact mcI=(MucContact)cI.nextElement();
                    if (mcI.origin==Contact.ORIGIN_GROUPCHAT && mcI.status==Presence.PRESENCE_ONLINE)
                        onlineConferences=true;
                } catch (Exception e) {}
            }
            
            if (contact instanceof MucContact) {
                MucContact selfContact= ((ConferenceGroup) contact.group).selfContact;
                MucContact mc=(MucContact) contact;
                
                //invite
                if (mc.realJid!=null) {
                    if (onlineConferences)
                        addItem(SR.MS_INVITE,40, RosterIcons.ICON_INVITE);
                }
                //invite
                
                int myAffiliation=selfContact.affiliationCode;
                if (myAffiliation==MucContact.AFFILIATION_OWNER) 
                    myAffiliation++; // allow owner to change owner's affiliation

            
                //addItem(SR.MS_TIME,891); 
                
                if (selfContact.roleCode==MucContact.ROLE_MODERATOR) {
                    if(mc.roleCode<MucContact.ROLE_MODERATOR)
                        addItem(SR.MS_KICK,8, RosterIcons.ICON_KICK);
                    
                    if (myAffiliation>=MucContact.AFFILIATION_ADMIN && mc.affiliationCode<myAffiliation)
                        addItem(SR.MS_BAN,9, RosterIcons.ICON_BAN);
                    
                    if (mc.affiliationCode<MucContact.AFFILIATION_ADMIN) 
                        /* 5.1.1 *** A moderator MUST NOT be able to revoke voice privileges from an admin or owner. */ 
                    if (mc.roleCode==MucContact.ROLE_VISITOR) addItem(SR.MS_GRANT_VOICE,31, RosterIcons.ICON_VOICE);
                    else addItem(SR.MS_REVOKE_VOICE, 32, RosterIcons.ICON_DEVOICE);
                }
//#ifdef REQUEST_VOICE
//# 		if (selfContact.roleCode==MucContact.ROLE_VISITOR) {
//#                     //System.out.println("im visitor");
//#                     if (mc.roleCode==MucContact.ROLE_MODERATOR) {
//#                         //System.out.println(mc.getJid()+" is a moderator");
//#                         addItem(SR.MS_REQUEST_PARTICIPANT_ROLE,39);
//#                     }
//#  		}
//#endif
                if (myAffiliation>=MucContact.AFFILIATION_ADMIN) {
                    // admin use cases
                    
                    //roles
                    if (mc.affiliationCode<MucContact.AFFILIATION_ADMIN) 
                        /* 5.2.1 ** An admin or owner MUST NOT be able to revoke moderation privileges from another admin or owner. */ 
                    if (mc.roleCode==MucContact.ROLE_MODERATOR) 
                        addItem(SR.MS_REVOKE_MODERATOR,31, RosterIcons.ICON_MEMBER);
                    else 
                        addItem(SR.MS_GRANT_MODERATOR,33, RosterIcons.ICON_ADMIN);
                    
                    //affiliations
                    if (mc.affiliationCode<myAffiliation) {
                        if (mc.affiliationCode!=MucContact.AFFILIATION_NONE) 
                            addItem(SR.MS_UNAFFILIATE,36, RosterIcons.ICON_DEMEMBER);
                        /* 5.2.2 */
                        if (mc.affiliationCode!=MucContact.AFFILIATION_MEMBER) 
                            addItem(SR.MS_GRANT_MEMBERSHIP,35, RosterIcons.ICON_MEMBER);
                    }
                    
                    
               //m.addItem(new MenuItem("Set Affiliation",15));
                }
                if (myAffiliation>=MucContact.AFFILIATION_OWNER) {
                    // owner use cases
                    if (mc.affiliationCode!=MucContact.AFFILIATION_ADMIN) 
                        addItem(SR.MS_GRANT_ADMIN,37, RosterIcons.ICON_ADMIN);
                    
                    if (mc.affiliationCode!=MucContact.AFFILIATION_OWNER) 
                        addItem(SR.MS_GRANT_OWNERSHIP,38, RosterIcons.ICON_OWNER);
                }
                if (mc.realJid!=null && mc.status<Presence.PRESENCE_OFFLINE) {
                    
                }
            } else if (contact.getGroupType()!=Groups.TYPE_TRANSP && contact.getGroupType()!=Groups.TYPE_SEARCH_RESULT) {
                // usual contact - invite item check
                 if (onlineConferences) 
                     addItem(SR.MS_INVITE,40, RosterIcons.ICON_INVITE);
            }
//#endif
//#if (FILE_IO && FILE_TRANSFER)
//#             if (!contact.jid.isTransport() && cf.fileTransfer)
//#                 if (contact!=sd.roster.selfContact()) {
//#                         addItem(SR.MS_SEND_FILE, 50, RosterIcons.ICON_SEND_FILE);
//#                 }
//# 
//#endif
//#if FILE_TRANSFER
//#             if (!contact.jid.isTransport() && cf.fileTransfer) {
//#                 if (contact!=sd.roster.selfContact()) {
//#                     String cameraAvailable=System.getProperty("supports.video.capture");
//#                     if (cameraAvailable!=null) if (cameraAvailable.startsWith("true")) {
//#                             addItem(SR.MS_SEND_PHOTO, 51, RosterIcons.ICON_SENDPHOTO);
//#                     }
//#                 }
//#             }
//#endif
        } else {
	    Group group=(Group)item;
	    if (group.type==Groups.TYPE_SEARCH_RESULT)
		addItem(SR.MS_DISCARD,21, RosterIcons.ICON_BAN);
//#ifndef WMUC
        if (group instanceof ConferenceGroup) {
            MucContact self = ((ConferenceGroup) group).selfContact;

            addItem(SR.MS_LEAVE_ROOM, 22, RosterIcons.ICON_LEAVE);
//#ifdef CLIPBOARD
//#             addItem(SR.MS_COPY_JID, 892, RosterIcons.ICON_COPY_JID);
//#             addItem(SR.MS_COPY_TOPIC, 993, RosterIcons.ICON_COPY_TOPIC);
//# 
//#endif

            if (self.status >= Presence.PRESENCE_OFFLINE) {// offline or error
                addItem(SR.MS_REENTER, 23, RosterIcons.ICON_CHANGE_NICK);
            } else {
                addItem(SR.MS_DIRECT_PRESENCE, 46, RosterIcons.ICON_SET_STATUS);
                addItem(SR.MS_CHANGE_NICKNAME, 23, RosterIcons.ICON_CHANGE_NICK);
                if (self.affiliationCode >= MucContact.AFFILIATION_OWNER) {
                    addItem(SR.MS_CONFIG_ROOM, 10, RosterIcons.ICON_CONFIGURE);
                }
                if (self.affiliationCode >= MucContact.AFFILIATION_ADMIN) {
                    addItem(SR.MS_OWNERS, 11, RosterIcons.ICON_OWNERS);
                    addItem(SR.MS_ADMINS, 12, RosterIcons.ICON_ADMINS);
                    addItem(SR.MS_MEMBERS, 13, RosterIcons.ICON_MEMBERS);
                    addItem(SR.MS_BANNED, 14, RosterIcons.ICON_OUTCASTS);
                }
            }
	    } else {
//#endif
                if (    group.type!=Groups.TYPE_IGNORE
                        && group.type!=Groups.TYPE_NOT_IN_LIST
                        && group.type!=Groups.TYPE_SEARCH_RESULT
                        && group.type!=Groups.TYPE_SELF
                        && group.type!=Groups.TYPE_TRANSP)
                {
                    addItem(SR.MS_RENAME,1001, RosterIcons.ICON_RENAME);
                    addItem(SR.MS_DELETE, 1004, RosterIcons.ICON_DELETE);
                }
//#ifndef WMUC
            }
//#endif
 	}
	if (getItemCount()>0) {
            show();            
        }
     }
     
     public void eventOk(){
         try {
             MenuItem me=(MenuItem) getFocusedObject();
            destroyView();
            if (me==null) return;
            doAction(me.index);
        } catch (Exception e) { }
    }

    private void doAction(final int index) {
        boolean isContact=( item instanceof Contact );
        Contact c = null;
        Group g = null;
        if (isContact) c=(Contact)item; else g=(Group) item;
        
        String to=null;
        if (isContact) to=(index<3)? c.getJid().toString() : c.bareJid;
            switch (index) {
                case 0: // version
                    sd.roster.setQuerySign(true);
                    sd.theStream.send(IqVersionReply.query(to));
                    break;
                case 86: // info
//#ifdef POPUPS
//#                     sd.roster.showInfo();
//#endif
                    break;
//#ifdef STATUSES_WINDOW                    
//#                 case 87:
//#                     new ContactStatusesList(c);
//#                     return;
//#endif                    
                case 1: // vCard
                    if (c.vcard!=null) {
                        if (c.getGroupType()==Groups.TYPE_SELF)
                            new VCardEdit(c.vcard);
                        else
                            new VCardView(c);
                        return;
                    }                    
                    VCard.request(c.bareJid, c.getJid().toString());
                    break;
                case 2:
                    new ContactEdit(c);
                    return; //break;
                case 3: //subscription
                    new SubscriptionEdit(c);
                    return; //break;
                case 4:
                    new AlertBox(SR.MS_DELETE_ASK, c.getNickJid()) {
                        public void yes() {
                            sd.roster.deleteContact((Contact)item);
                        }
                        public void no() { }
                    };
                    return;
                case 6: // logoff
                    sd.roster.blockNotify(-111,10000); //block sounds to 10 sec
                    Presence presence = new Presence(
                    Presence.PRESENCE_OFFLINE, -1, "", null);
                    presence.setTo(c.getJid().toString());
                    sd.theStream.send( presence );
                    break;
                case 5: // logon
                    sd.roster.blockNotify(-111,10000); //block sounds to 10 sec
                    //querysign=true; displayStatus();
                    Presence presence2 = new Presence(sd.roster.myStatus, 0, "", null);
                    presence2.setTo(c.getJid().toString());
                    sd.theStream.send( presence2 );
                    break;
                case 7: // Nick resolver
                    sd.roster.resolveNicknames(c.bareJid);
                    break;
//#if CHANGE_TRANSPORT
//#                 case 915: // change transport
//#                     new ChangeTransport( c.bareJid);
//#                     return;
//#endif
                case 21:
                    sd.roster.cleanupSearch();
                    break;
//#ifdef SERVICE_DISCOVERY
//#                 case 30:
//#                     new ServiceDiscovery(c.getJid().toString(), "http://jabber.org/protocol/commands", false);
//#                     return;
//#endif
/*                case 1003: 
                    new RenameGroup( null, c);
                    return;
 */
                case 889: //idle
                    sd.roster.setQuerySign(true);
                    sd.theStream.send(IqLast.query(c.getJid().toString(), "idle"));
                    break;
                case 890: //online
                    sd.roster.setQuerySign(true);
                    sd.theStream.send(IqLast.query(c.bareJid, "online_"+c.getResource()));
                    break;
                case 894: //seen
                    sd.roster.setQuerySign(true);
                    sd.theStream.send(IqLast.query(c.bareJid, "seen"));
                    break;
                case 891: //time
                    sd.roster.setQuerySign(true);
                    sd.theStream.send(IqTimeReply.query(c.getJid().toString()));
                    break;
//#ifdef CLIPBOARD
//#ifndef WMUC                    
//#                 case 892: //Copy JID
//#                     String jid = null;
//#                     if (isContact)
//#                         jid = c.getJid().toString();
//#                     else
//#                      jid = ((ConferenceGroup)g).jid.toString();
//# 
//#                     if (jid != null) {
//#                         sd.clipboard.setClipBoard(jid);
//#                     }
//#                     break;
//#                 case 993:
//#                     String topic = ((ConferenceGroup)g).confContact.statusString;
//#                     if (topic != null) {
//#                         sd.clipboard.setClipBoard(topic);
//#                     }
//#                     break;
//#endif                    
//#endif
                case 893: //ping
                    try {
                        sd.roster.setQuerySign(true);
                        //c.setPing();
                        sd.theStream.send(IqPing.query(c.getJid().toString(), null));
                    } catch (Exception e) {/*no messages*/}
                    break;
                case 912: //send color scheme
                    String from=sd.account.toString();
//System.out.println(from);
                    String body=ColorTheme.getSkin();
//System.out.println(body);
                    String id=String.valueOf((int) System.currentTimeMillis());

                    try {
                        sd.roster.sendMessage(c, id, body, null, null);
                        c.addMessage(new Msg(Msg.MESSAGE_TYPE_OUT,from,null,"scheme sended"));
                    } catch (Exception e) {
                        c.addMessage(new Msg(Msg.MESSAGE_TYPE_OUT,from,null,"scheme NOT sended"));
                        //e.printStackTrace();
                    }
                break;
//#ifdef CLIPBOARD
//#                 case 914: //send message from buffer
//#                     String body2 = sd.clipboard.getClipBoard();
//#                     if (body2.length()==0)
//#                         return;
//# 
//#                     String from2=sd.account.toString();
//# 
//#                     String id2=String.valueOf((int) System.currentTimeMillis());
//#                     Msg msg2=new Msg(Msg.MESSAGE_TYPE_OUT,from2,null,body2);
//#                     msg2.id=id2;
//#                     msg2.itemCollapsed=true;
//# 
//#                     try {
//#                         if (body2!=null && body2.length()>0) {
//#                             sd.roster.sendMessage(c, id2, body2, null, null);
//#                             
//#                             if (c.origin<Contact.ORIGIN_GROUPCHAT) c.addMessage(msg2);
//#                         }
//#                     } catch (Exception e) {
//#                         c.addMessage(new Msg(Msg.MESSAGE_TYPE_OUT,from2,null,"clipboard NOT sended"));
//#                     }
//#                     break;
//#endif
//#ifndef WMUC
                case 40: //invite
                    //new InviteForm(c, display);
                    if (c.jid!=null) {
                        new InviteForm(c);
                    } else {
                        MucContact mcJ=(MucContact) c;

                        if (mcJ.realJid!=null) {
                            boolean onlineConferences=false;
                            for (Enumeration cJ=sd.roster.hContacts.elements(); cJ.hasMoreElements(); ) {
                                try {
                                    MucContact mcN=(MucContact)cJ.nextElement();
                                    if (mcN.origin==Contact.ORIGIN_GROUPCHAT && mcN.status==Presence.PRESENCE_ONLINE)
                                        onlineConferences=true;
                                } catch (Exception e) {}
                            }
                            if (onlineConferences) new InviteForm(mcJ);
                        }
                    }
                    return;
//#endif
                case 45: //direct presence
                    new StatusSelect(c);
                    return;
//#if (FILE_IO && FILE_TRANSFER)
//#                 case 50: //send file
//#                     new TransferSendFile(c.getJid());
//#                     return;
//#endif
//#if FILE_TRANSFER
//#ifndef NOMMEDIA
//#                 case 51: //send photo
//#                     new TransferImage(c.getJid());
//#                     return;
//#endif
//#endif
            }
//#ifndef WMUC
            if (c instanceof MucContact || g instanceof ConferenceGroup) {
                MucContact mc=(MucContact) c;
                
                String roomJid="";
                if (g instanceof ConferenceGroup) {
                    roomJid=((ConferenceGroup)g).confContact.getJid().toString();
                }
                
                String myNick="";
                if (c instanceof MucContact) {
                    myNick=((ConferenceGroup)c.group).selfContact.getName();
                }
                
                switch (index) { // muc contact actions
                    case 10: // room config
                        new QueryConfigForm(roomJid);
                        break;
                    case 11: // owners
                    case 12: // admins
                    case 13: // members
                    case 14: // outcasts
                        new Affiliations(roomJid, (short)(index-10));
                        return;
                    case 22:
                        sd.roster.leaveRoom( g );
                        break;
                    case 23:
                        sd.roster.reEnterRoom( g );
                        return; //break;
                    case 46: //conference presence
                        new StatusSelect(((ConferenceGroup)g).confContact);
                        return;
                     case 8: // kick
                        new ConferenceQuickPrivelegeModify(mc, ConferenceQuickPrivelegeModify.KICK,myNick);
                        return;
                     case 9: // ban
                        new ConferenceQuickPrivelegeModify(mc, ConferenceQuickPrivelegeModify.OUTCAST,myNick);
                        return;
                     case 31: //grant voice and revoke moderator
                        new ConferenceQuickPrivelegeModify(mc, ConferenceQuickPrivelegeModify.PARTICIPANT,null); //
                        return;
                     case 32: //revoke voice
                        new ConferenceQuickPrivelegeModify(mc, ConferenceQuickPrivelegeModify.VISITOR,null);
                        return;
                     case 33: //grant moderator
                        new ConferenceQuickPrivelegeModify(mc, ConferenceQuickPrivelegeModify.MODERATOR,null); //
                        return;
                    case 35: //grant membership and revoke admin
                        new ConferenceQuickPrivelegeModify(mc, ConferenceQuickPrivelegeModify.MEMBER,null); //
                        return;
                    case 36: //revoke membership
                        new ConferenceQuickPrivelegeModify(mc, ConferenceQuickPrivelegeModify.NONE,null); //
                         return;
                    case 37: //grant admin and revoke owner
                        new ConferenceQuickPrivelegeModify(mc, ConferenceQuickPrivelegeModify.ADMIN,null); //
                        return;
                    case 38: //grant owner
                        new ConferenceQuickPrivelegeModify(mc, ConferenceQuickPrivelegeModify.OWNER,null); //
                        return;
//#ifdef REQUEST_VOICE
//#                 case 39: //request voice
//#                     new QueryRequestVoice( sd.roster, mc, ConferenceQuickPrivelegeModify.PARTICIPANT);
//#                     return;
//#endif
//#ifdef CLIPBOARD
//#                     case 892: //Copy JID
//#                         try {
//#                             if (mc.realJid!=null)
//#                                 sd.clipboard.setClipBoard(mc.realJid.toString());
//#                         } catch (Exception e) {}
//#                         break;
//#endif
             }
        } else {
//#endif
            Group sg=(Group)item;

            if (       sg.type!=Groups.TYPE_IGNORE 
                    && sg.type!=Groups.TYPE_NOT_IN_LIST
                    && sg.type!=Groups.TYPE_SEARCH_RESULT
                    && sg.type!=Groups.TYPE_SELF
                    && sg.type!=Groups.TYPE_TRANSP)
            {
                switch (index) {
                    case 1001: //rename
                        new RenameGroup(sg/*, null*/);
                        return;
                    case 1004: //delete
                        new AlertBox(SR.MS_DELETE_GROUP_ASK, sg.name) {
                            public void yes() {
                                sd.roster.deleteGroup((Group)item);
                            }
                            public void no() {
                            }
                        };
                        return;
                }
            }
//#ifndef WMUC
         }
//#endif
     }
}
