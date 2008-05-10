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

package Client;
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
import ServiceDiscovery.ServiceDiscovery;
import ui.controls.vGauge;
//#endif

import xmpp.extensions.IqLast;
import xmpp.extensions.IqPing;
import xmpp.extensions.IqTimeReply;
import xmpp.extensions.IqVersionReply;

import com.alsutton.jabber.datablocks.Presence;
//#if FILE_TRANSFER
import io.file.transfer.TransferSendFile;
//#endif
import java.util.Enumeration;
import javax.microedition.lcdui.Display;
import locale.SR;
//#ifdef COLORS
import Colors.ColorUtils;
//#endif
import ui.Menu;
import ui.MenuItem;
import ui.Time;
//#ifdef CLIPBOARD
//# import util.ClipBoard;
//#endif
import vcard.VCard;
import vcard.vCardForm;

/**
 *
 * @author EvgS
 */
public class RosterItemActions extends Menu {

    Object item;
//#ifdef CLIPBOARD
//#     private ClipBoard clipboard;
//#endif
    private int action;
    
    StaticData sd=StaticData.getInstance();
    
    /** Creates a new instance of RosterItemActions */
    public RosterItemActions(Display display, Object item, int action) {
	super(item.toString());
	this.item=item;
        this.action=action;
        
        if (!sd.roster.isLoggedIn()) return;
	
        if (item==null) return;
        boolean isContact=( item instanceof Contact );
//#ifdef CLIPBOARD
//#         if (Config.getInstance().useClipBoard)
//#             clipboard=ClipBoard.getInstance();
//#endif
	if (isContact) {
	    Contact contact=(Contact)item;
	    if (contact.getGroupType()==Groups.TYPE_TRANSP) {
		addItem(SR.MS_LOGON,5, 0x36);
		addItem(SR.MS_LOGOFF,6, 0x37);
                addItem(SR.MS_RESOLVE_NICKNAMES, 7);
//#if CHANGE_TRANSPORT
//#                 addItem("Change transport", 915);
//#endif
	    }
	    addItem(SR.MS_VCARD,1, 0x0f16);
//#ifdef POPUPS
            addItem(SR.MS_INFO,86, 0x0f04);
//#endif
            addItem(SR.MS_CLIENT_INFO,0, 0x0f04);
//#ifdef SERVICE_DISCOVERY
	    addItem(SR.MS_COMMANDS,30, 0x0f24);
//#endif
//#ifdef CLIPBOARD
//#             if (Config.getInstance().useClipBoard) {
//#                 addItem(SR.MS_SEND_BUFFER,914, 0x0f22);
//#                 if (contact.getGroupType()!=Groups.TYPE_SELF) {
//#                     addItem(SR.MS_COPY_JID,892, 0x0f22);
//#                 }
//#             }
//#endif
//#ifdef COLORS
                addItem("Send current color scheme",912, 0x0f22);
//#endif
            if (contact.status<Presence.PRESENCE_OFFLINE) {
                addItem(SR.MS_TIME,891);
                addItem(SR.MS_IDLE,889);
                addItem(SR.MS_PING,893);
            }
	    
	    if (contact.getGroupType()!=Groups.TYPE_SELF && contact.getGroupType()!=Groups.TYPE_SEARCH_RESULT && contact.origin<Contact.ORIGIN_GROUPCHAT) {
		if (contact.status<Presence.PRESENCE_OFFLINE) {
                    addItem(SR.MS_ONLINE_TIME,890);    
                } else {
                    addItem(SR.MS_SEEN,890); 
                }
                if (contact.getGroupType()!=Groups.TYPE_TRANSP) {
                    addItem(SR.MS_EDIT,2, 0x0f13);
                }
		addItem(SR.MS_SUBSCRIPTION,3, 0x47);
		addItem(SR.MS_MOVE,1003);
		addItem(SR.MS_DELETE, 4, 0x12);
		addItem(SR.MS_DIRECT_PRESENCE,45, 0x01);
	    }
	    if (contact.origin==Contact.ORIGIN_GROUPCHAT) 
                return;
//#ifndef WMUC
            boolean onlineConferences=false;
            for (Enumeration cI=sd.roster.getHContacts().elements(); cI.hasMoreElements(); ) {
                try {
                    MucContact mcI=(MucContact)cI.nextElement();
                    if (mcI.origin==Contact.ORIGIN_GROUPCHAT && mcI.status==Presence.PRESENCE_ONLINE)
                        onlineConferences=true;
                } catch (Exception e) {}
            }
            
            if (contact instanceof MucContact) {
                MucContact selfContact= ((ConferenceGroup) contact.getGroup()).getSelfContact();
                MucContact mc=(MucContact) contact;
                
                //invite
                if (mc.realJid!=null) {
                    if (onlineConferences) addItem(SR.MS_INVITE,40, 0x0f20);
                }
                //invite
                
                int myAffiliation=selfContact.affiliationCode;
                if (myAffiliation==MucContact.AFFILIATION_OWNER) myAffiliation++; // allow owner to change owner's affiliation

            
                //addItem(SR.MS_TIME,891); 
                
                if (selfContact.roleCode==MucContact.ROLE_MODERATOR) {
                    if(mc.roleCode<MucContact.ROLE_MODERATOR)
                        addItem(SR.MS_KICK,8, 0x0f06);
                    
                    if (myAffiliation>=MucContact.AFFILIATION_ADMIN && mc.affiliationCode<myAffiliation)
                        addItem(SR.MS_BAN,9, 0x0f06);
                    
                    if (mc.affiliationCode<MucContact.AFFILIATION_ADMIN) 
                        /* 5.1.1 *** A moderator MUST NOT be able to revoke voice privileges from an admin or owner. */ 
                    if (mc.roleCode==MucContact.ROLE_VISITOR) addItem(SR.MS_GRANT_VOICE,31);
                    else addItem(SR.MS_REVOKE_VOICE, 32);
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
                    if (mc.roleCode==MucContact.ROLE_MODERATOR) addItem(SR.MS_REVOKE_MODERATOR,31);
                    else addItem(SR.MS_GRANT_MODERATOR,33);
                    
                    //affiliations
                    if (mc.affiliationCode<myAffiliation) {
                        if (mc.affiliationCode!=MucContact.AFFILIATION_NONE) addItem(SR.MS_UNAFFILIATE,36);
                        /* 5.2.2 */
                        if (mc.affiliationCode!=MucContact.AFFILIATION_MEMBER) addItem(SR.MS_GRANT_MEMBERSHIP,35);
                    }
                    
                    
               //m.addItem(new MenuItem("Set Affiliation",15));
                }
                if (myAffiliation>=MucContact.AFFILIATION_OWNER) {
                    // owner use cases
                    //if (mc.affiliationCode<=selfContact.affiliationCode) /* 5.2.2 */
                    if (mc.affiliationCode!=MucContact.AFFILIATION_ADMIN) addItem(SR.MS_GRANT_ADMIN,37);
                    //else addItem(SR.MS_REVOKE_ADMIN,35);
                    
                    if (mc.affiliationCode!=MucContact.AFFILIATION_OWNER) addItem(SR.MS_GRANT_OWNERSHIP,38);
                    //else addItem(SR.MS_REVOKE_OWNERSHIP,37);
                }
                if (mc.realJid!=null && mc.getStatus()<Presence.PRESENCE_OFFLINE) {
                    
                }
            } else if (contact.getGroupType()!=Groups.TYPE_TRANSP && contact.getGroupType()!=Groups.TYPE_SEARCH_RESULT) {
                // usual contact - invite item check
                 if (onlineConferences) addItem(SR.MS_INVITE,40);
            }
//#endif
//#if (FILE_IO && FILE_TRANSFER)
            if (contact.getGroupType()!=Groups.TYPE_TRANSP) 
                if (contact!=sd.roster.selfContact())
                    addItem(SR.MS_SEND_FILE, 50, 0x0f34);
            
//#endif
        } else {
	    Group group=(Group)item;
	    if (group.type==Groups.TYPE_SEARCH_RESULT)
		addItem(SR.MS_DISCARD,21);
//#ifndef WMUC
	    if (group instanceof ConferenceGroup) {
		MucContact self=((ConferenceGroup)group).getSelfContact();
                
		addItem(SR.MS_LEAVE_ROOM,22, 0x0f22);
                
                if (self.status>=Presence.PRESENCE_OFFLINE) {// offline or error
		    addItem(SR.MS_REENTER,23, 0x0f21);
                } else {
                    addItem(SR.MS_DIRECT_PRESENCE,46);
                    addItem(SR.MS_CHANGE_NICKNAME,23, 0x0f21);
		    if (self.affiliationCode>=MucContact.AFFILIATION_OWNER) {
			addItem(SR.MS_CONFIG_ROOM,10, 0x0f03);
                    }
		    if (self.affiliationCode>=MucContact.AFFILIATION_ADMIN) {
			addItem(SR.MS_OWNERS,11);
			addItem(SR.MS_ADMINS,12);
			addItem(SR.MS_MEMBERS,13);
			addItem(SR.MS_BANNED,14);
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
                    addItem(SR.MS_RENAME,1001);
                    addItem(SR.MS_DELETE, 1004);
                }
//#ifndef WMUC
            }
//#endif
 	}
	if (getItemCount()>0) {
            if (action<0) 
                attachDisplay(display);
            else try {
                this.display=display; // to invoke dialog Y/N
                doAction(action);
            } catch (Exception e) { 
                //e.printStackTrace();
            }
        }
     }
     
     public void eventOk(){
         try {
             MenuItem me=(MenuItem) getFocusedObject();
            destroyView();
            if (me==null) return;
            int index=action=me.index;
            doAction(index);
        } catch (Exception e) { }
    }

    private void doAction(final int index) {
        boolean isContact=( item instanceof Contact );
        Contact c = null;
        Group g = null;
        if (isContact) c=(Contact)item; else g=(Group) item;
        
        String to=null;
        if (isContact) to=(index<3)? c.getJid() : c.getBareJid();
            switch (index) {
                case 0: // info
                    sd.roster.setQuerySign(true);
                    sd.roster.theStream.send(IqVersionReply.query(to));
                    break;
                case 86: // info
//#ifdef POPUPS
                    sd.roster.showInfo();
//#endif
                    break;
                case 1: // vCard
                    if (c.vcard!=null) {
                        new vCardForm(display, c.vcard, c.getGroupType()==Groups.TYPE_SELF);
                        return;
                    }
                    VCard.request(c.getBareJid(), c.getJid());
                    break;
                case 2:
                    (new ContactEdit(display, c )).parentView=sd.roster;
                    return; //break;
                    
                case 3: //subscription
                    new SubscriptionEdit(display, c);
                    return; //break;
                case 4:
                    new vGauge(SR.MS_DELETE_ASK, c.getNickJid(), 0, display,  sd.roster) {
                        public void yes() {
                            sd.roster.deleteContact((Contact)item);
                        }
                        public void no() { }
                    };
                    return;
                case 6: // logoff
                {
                    sd.roster.blockNotify(-111,10000); //block sounds to 10 sec
                    Presence presence = new Presence(
                    Presence.PRESENCE_OFFLINE, -1, "", null);
                    presence.setTo(c.getJid());
                    sd.roster.theStream.send( presence );
                    break;
                }
                case 5: // logon
                {
                    sd.roster.blockNotify(-111,10000); //block sounds to 10 sec
                    //querysign=true; displayStatus();
                    Presence presence = new Presence(sd.roster.myStatus, 0, "", null);
                    presence.setTo(c.getJid());
                    sd.roster.theStream.send( presence );
                    break;
                }
                case 7: // Nick resolver
                {
                    sd.roster.resolveNicknames(c.getBareJid());
                    break;
                }
//#if CHANGE_TRANSPORT
//#                 case 915: // change transport
//#                 {
//#                     new ChangeTransport(display, c.getBareJid());
//#                     return;
//#                 }
//#endif
                case 21:
                {
                    sd.roster.cleanupSearch();
                    break;
                }
//#ifdef SERVICE_DISCOVERY
                case 30:
                {
                    new ServiceDiscovery(display, c.getJid(), "http://jabber.org/protocol/commands");
                    return;
                }
//#endif
                case 1003: 
                {
                    new RenameGroup(display, null, c);
                    return;
                }
                case 889: //idle
                {
                    sd.roster.setQuerySign(true);
                    sd.roster.theStream.send(IqLast.query(c.getJid()));
                    break;
                }
                case 890: //seen & online
                {
                    sd.roster.setQuerySign(true);
                    sd.roster.theStream.send(IqLast.query(c.getBareJid()));
                    break;
                }
                
                case 891: //time
                {
                    sd.roster.setQuerySign(true);
                    sd.roster.theStream.send(IqTimeReply.query(c.getJid()));
                    break;
                }
//#ifdef CLIPBOARD
//#                 case 892: //Copy JID
//#                 {
//#ifndef WMUC
//#                     if (!(c instanceof MucContact)) {
//#                         //System.out.println("1 c "+c.bareJid);
//#endif
//#                         try {
//#                             if (c.bareJid!=null)
//#                                 clipboard.setClipBoard(c.bareJid);
//#                         } catch (Exception e) {/*no messages*/}
//#ifndef WMUC
//#                     }
//#endif
//#                     break;
//#                 }
//#endif
                case 893: //ping
                {
                    try {
                        sd.roster.setQuerySign(true);
                        //c.setPing();
                        sd.roster.theStream.send(IqPing.query(c.getJid(), "_ping_"+Time.utcTimeMillis()));
                    } catch (Exception e) {/*no messages*/}
                    break;
                }
//#ifdef COLORS
                case 912: //send color scheme
                {
                    String from=sd.account.toString();
                    String body=ColorUtils.getSkin();
                    String subj="";
                    
                    String id=String.valueOf((int) System.currentTimeMillis());
                    
                    Msg msg=new Msg(Msg.MESSAGE_TYPE_OUT,from,subj,body);
                    msg.id=id;
                    
                    try {
                        sd.roster.sendMessage(c, id, body, subj, null);
                        c.addMessage(new Msg(Msg.MESSAGE_TYPE_OUT,from,subj,"scheme sended"));
                    } catch (Exception e) {
                        c.addMessage(new Msg(Msg.MESSAGE_TYPE_OUT,from,subj,"scheme NOT sended"));
                        //e.printStackTrace();
                    }
                    break;
                }
//#endif
//#ifdef CLIPBOARD
//#                 case 914: //send message from buffer
//#                 {
//#                     String body=clipboard.getClipBoard();
//#                     if (body.length()==0)
//#                         return;
//#                     
//#                     String from=sd.account.toString();
//#                     String subj="";
//#                     
//#                     String id=String.valueOf((int) System.currentTimeMillis());
//#                     
//#                     Msg msg=new Msg(Msg.MESSAGE_TYPE_OUT,from,subj,body);
//#                     msg.id=id;
//#                     try {
//#                         sd.roster.sendMessage(c, id, body, subj, null);
//#                         c.addMessage(new Msg(Msg.MESSAGE_TYPE_OUT,from,subj,"message sended from clipboard("+body.length()+"chars)"));
//#                     } catch (Exception e) {
//#                         c.addMessage(new Msg(Msg.MESSAGE_TYPE_OUT,from,subj,"message NOT sended"));
//#                         //e.printStackTrace();
//#                     }
//#                     break;
//#                 }
//#endif
//#ifndef WMUC
                case 40: //invite
                {
                    //new InviteForm(c, display);
                    if (c.jid!=null) {
                        new InviteForm(c, display);                        
                    } else {
                        MucContact mcJ=(MucContact) c;

                        if (mcJ.realJid!=null) {
                            boolean onlineConferences=false;
                            for (Enumeration cJ=sd.roster.getHContacts().elements(); cJ.hasMoreElements(); ) {
                                try {
                                    MucContact mcN=(MucContact)cJ.nextElement();
                                    if (mcN.origin==Contact.ORIGIN_GROUPCHAT && mcN.status==Presence.PRESENCE_ONLINE)
                                        onlineConferences=true;
                                } catch (Exception e) {}
                            }
                            if (onlineConferences) new InviteForm(mcJ, display);
                        }
                    }
                    return;
                }
//#endif
                case 45: //direct presence
                {
                    (new StatusSelect(display, c)).setParentView(sd.roster);
                    return;
                }
                
//#if (FILE_IO && FILE_TRANSFER)
                case 50: //send file
                {
                    new TransferSendFile(display, c.getJid());
                    return;
                }   
//#endif
            }
//#ifndef WMUC
            if (c instanceof MucContact || g instanceof ConferenceGroup) {
                MucContact mc=(MucContact) c;
                
                switch (index) { // muc contact actions
                    case 10: // room config
                    {
                        String roomJid=((ConferenceGroup)g).getConference().getJid();
                        new QueryConfigForm(display, roomJid);
                        break;
                    }
                    case 11: // owners
                    case 12: // admins
                    case 13: // members

                    case 14: // outcasts
                    {
                        String roomJid=((ConferenceGroup)g).getConference().getJid();
                        new Affiliations(display, roomJid, (short)(index-10));
                        return;
                    }

                    case 22:
                    {
                        sd.roster.leaveRoom( g );
                        break;
                    }
                    case 23:
                    {
                        sd.roster.reEnterRoom( g );
                        return; //break;
                    }
                    case 46: //conference presence
                    {
                        new StatusSelect(display, ((ConferenceGroup)g).getConference()).setParentView(sd.roster);
                        return;
                    }
                    
                     case 8: // kick
                     {
                        ConferenceGroup mucGrp=(ConferenceGroup)c.getGroup();
                        String myNick=mucGrp.getSelfContact().getName();
                        new ConferenceQuickPrivelegeModify(display, mc, ConferenceQuickPrivelegeModify.KICK,myNick);
                        return;
                     }
                     case 9: // ban
                     {
                        ConferenceGroup mucGrp=(ConferenceGroup)c.getGroup();
                        String myNick=mucGrp.getSelfContact().getName();
                        new ConferenceQuickPrivelegeModify(display, mc, ConferenceQuickPrivelegeModify.OUTCAST,myNick);
                        return;
                     }
                     case 31: //grant voice and revoke moderator
                     {
                        new ConferenceQuickPrivelegeModify(null, mc, ConferenceQuickPrivelegeModify.PARTICIPANT,null);
                        return;
                     }
                     case 32: //revoke voice
                     {
                        new ConferenceQuickPrivelegeModify(display, mc, ConferenceQuickPrivelegeModify.VISITOR,null);
                        return;
                     }
                     
                     case 33: //grant moderator
                     {
                        new ConferenceQuickPrivelegeModify(null, mc, ConferenceQuickPrivelegeModify.MODERATOR,null);
                        return;
                     }
                    
                case 35: //grant membership and revoke admin
                 {
                    new ConferenceQuickPrivelegeModify(null, mc, ConferenceQuickPrivelegeModify.MEMBER,null);
                     return;
                 }
                 
                case 36: //revoke membership
                 {
                    new ConferenceQuickPrivelegeModify(null, mc, ConferenceQuickPrivelegeModify.NONE,null);
                     return;
                 }
                 
                case 37: //grant admin and revoke owner
                 {
                    new ConferenceQuickPrivelegeModify(null, mc, ConferenceQuickPrivelegeModify.ADMIN,null);
                     return;
                 }
                 
                case 38: //grant owner
                 {
                    new ConferenceQuickPrivelegeModify(null, mc, ConferenceQuickPrivelegeModify.OWNER,null);
                     return;
                 }
//#ifdef REQUEST_VOICE		 
//#                 case 39: //request voice
//#                  {
//#                     new QueryRequestVoice(display, mc, ConferenceQuickPrivelegeModify.PARTICIPANT);
//#                     return;
//#                  }
//#endif
//#ifdef CLIPBOARD
//#                 case 892: //Copy JID
//#                 {
//#                     try {
//#                         if (mc.realJid!=null)
//#                             clipboard.setClipBoard(mc.realJid);
//#                     } catch (Exception e) {}
//#                     break;
//#                  }
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
                    {
                        new RenameGroup(display, sg, null);
                        return;
                    }
                    case 1004: //delete
                    {
                        new vGauge(SR.MS_DELETE_ASK, sg.getName(), 0, display, sd.roster) {
                            public void yes() {
                                sd.roster.deleteGroup((Group)item);
                            }
                            public void no() {
                            }
                        };
                        return;
                    }    
                }
            }
//#ifndef WMUC
         }
//#endif
     }
}
