/*
 * MucContact.java
 *
 * Created on 2.05.2006, 14:05
 * Copyright (c) 2005-2007, Eugene Stahov (evgs), http://bombus-im.org
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

import Client.Contact;
import Client.StaticData;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.datablocks.Presence;
import images.RosterIcons;
import locale.SR;
import Client.Msg;
import ui.VirtualList;
import com.alsutton.jabber.XmppError;

/**
 *
 * @author root
 */
public class MucContact extends Contact{
    
    public final static short AFFILIATION_OUTCAST=-1;
    public final static short AFFILIATION_NONE=0;
    public final static short AFFILIATION_MEMBER=1;
    public final static short AFFILIATION_ADMIN=2;
    public final static short AFFILIATION_OWNER=3;
    
    public final static short ROLE_VISITOR=-1;
    public final static short ROLE_PARTICIPANT=0;
    public final static short ROLE_MODERATOR=1;

    public final static short GROUP_VISITOR=4;
    public final static short GROUP_MEMBER=3;
    public final static short GROUP_PARTICIPANT=2;
    public final static short GROUP_MODERATOR=1;
    
//#ifdef ANTISPAM
//#     public final static int PRIVATE_DECLINE=-1;
//#     public final static int PRIVATE_NONE=0;
//#     public final static int PRIVATE_REQUEST=2;
//#     public final static int PRIVATE_ACCEPT=3;
//#     
//#     private int privateState;
//#endif

    public String realJid;
    
    public String affiliation;
    public String role;
    
    public short roleCode;
    public short affiliationCode;
    
    public boolean commonPresence=true;
    
    public long lastMessageTime;
    
    /** Creates a new instance of MucContact */
    public MucContact(String nick, String jid) {
        super(nick, jid, Presence.PRESENCE_OFFLINE, "muc");
        offline_type=Presence.PRESENCE_OFFLINE;
    }
    
    public String processPresence(JabberDataBlock xmuc, Presence presence) {
        String from=jid.getJid();
        
        int presenceType=presence.getTypeIndex();
        
         if (presenceType==Presence.PRESENCE_ERROR) {
            XmppError xe=XmppError.findInStanza(presence);
            int errCode=xe.getCondition();

            ConferenceGroup grp=(ConferenceGroup)getGroup();
            if (status>=Presence.PRESENCE_OFFLINE) 
                testMeOffline();
            if (errCode!=XmppError.CONFLICT || status>=Presence.PRESENCE_OFFLINE)
                setStatus(presenceType);

            String errText=xe.getText();
            if (errText!=null) return xe.toString(); // if error description is provided by server
            
            // legacy codes
            switch (errCode) {
                case XmppError.NOT_AUTHORIZED:        return "Password required";
                case XmppError.FORBIDDEN:             return "You are banned in this room";
                case XmppError.ITEM_NOT_FOUND:        return "Room does not exists";
                case XmppError.NOT_ALLOWED:           return "You can't create room on this server";
                case XmppError.NOT_ACCEPTABLE:        return "Reserved roomnick must be used";
                case XmppError.REGISTRATION_REQUIRED: return "This room is members-only";
                case XmppError.CONFLICT:              return "Nickname is already in use by another occupant";
                case XmppError.SERVICE_UNAVAILABLE:   return "Maximum number of users has been reached in this room";
                default: return xe.getName();
            }
         }
        
        JabberDataBlock item=xmuc.getChildBlock("item");   

        String role=item.getAttribute("role");
        if (role.equals("visitor")) roleCode=ROLE_VISITOR;
        if (role.equals("participant")) roleCode=ROLE_PARTICIPANT;
        if (role.equals("moderator")) roleCode=ROLE_MODERATOR;
        
        String affiliation=item.getAttribute("affiliation");
        if (affiliation.equals("owner")) affiliationCode=AFFILIATION_OWNER;
        if (affiliation.equals("admin")) affiliationCode=AFFILIATION_ADMIN;
        if (affiliation.equals("member")) affiliationCode=AFFILIATION_MEMBER;
        if (affiliation.equals("none")) affiliationCode=AFFILIATION_NONE;
        
        boolean roleChanged= !role.equals(this.role);
        boolean affiliationChanged= !affiliation.equals(this.affiliation);
        this.role=role;
        this.affiliation=affiliation;
        
        String chNick=item.getAttribute("nick");

        setSortKey(nick);
        
        switch (roleCode) {
            case ROLE_MODERATOR:
                transport=RosterIcons.ICON_MODERATOR_INDEX;
                key0=GROUP_MODERATOR;
                break;
            case ROLE_VISITOR:
                transport=RosterIcons.getInstance().getTransportIndex("conference_visitors");
                key0=GROUP_VISITOR;
                break;
            default:
                transport=(affiliation.equals("member"))? 0: RosterIcons.getInstance().getTransportIndex("conference_visitors");
                key0=(affiliation.equals("member"))?GROUP_MEMBER:GROUP_PARTICIPANT;
        }

        int rp=from.indexOf('/');
        
        JabberDataBlock statusBlock=xmuc.getChildBlock("status");
        int statusCode;
        try { 
            statusCode=Integer.parseInt( statusBlock.getAttribute("code") ); 
        } catch (Exception e) { statusCode=0; }
        

        StringBuffer b=new StringBuffer();
        appendL(b,nick);
        
        String statusText=presence.getChildBlockText("status");
        
        if (statusCode==201) {
            //todo: fix this nasty hack, it will not work if multiple status codes are nested in presence)
            b.setLength(0);
            b.append(SR.MS_NEW_ROOM_CREATED);
        } else if (presenceType==Presence.PRESENCE_OFFLINE) {
            key0=3;
            String reason=item.getChildBlockText("reason");
            String realJid=item.getAttribute("jid");
            switch (statusCode) {
                
                case 303:
                    b.append(SR.MS_IS_NOW_KNOWN_AS);
                    appendL(b,chNick);
                    String newJid=from.substring(0,rp+1)+chNick;
                    jid.setJid(newJid);
                    bareJid=newJid;
                    from=newJid;
                    nick=chNick;
                    break;
                    
                case 301: //ban
                    presenceType=Presence.PRESENCE_ERROR;
                case 307: //kick
                    if (realJid!=null) {
                        b.append(" (");
                        b.append(realJid);
                        b.append(')');
                    }
                    b.append((statusCode==301)? SR.MS_WAS_BANNED : SR.MS_WAS_KICKED );
//#ifdef POPUPS
//#                     setWobble(nick+((statusCode==301)? SR.MS_WAS_BANNED : SR.MS_WAS_KICKED)+"\n"+reason);
//#endif
                    b.append("(");
                    b.append(reason);
                    b.append(")");
					
                    if (realJid!=null) {
                        b.append(" - ");
                        appendL(b,realJid);
                    }

                    if (reason.indexOf("talks") > -1) toTalks();
                    
                    testMeOffline();
                    break;
                case 321:
                    b.append(SR.MS_HAS_BEEN_UNAFFILIATED_AND_KICKED_FROM_MEMBERS_ONLY_ROOM);
                    testMeOffline();
                    break;
                case 322:
                    b.append(SR.MS_HAS_BEEN_KICKED_BECAUSE_ROOM_BECAME_MEMBERS_ONLY);
                    testMeOffline();
                    break;
                    
                default:
                    if (realJid!=null) {
                        b.append(" (");
                        b.append(realJid);
                        b.append(')');
                    }
                    b.append(SR.MS_HAS_LEFT_CHANNEL);
                    
                if (statusText.length()>0) {
                    b.append(" (");
                    b.append(statusText);
                    b.append(")");
                }

                    testMeOffline();
            } 
                
        } else {
            if (this.status==Presence.PRESENCE_OFFLINE) {
                String realJid=item.getAttribute("jid");
                if (realJid!=null) {
                    this.realJid=realJid;  //for moderating purposes
                    b.append(" (");
                    appendL(b, realJid);
                    b.append(')');
                }
                b.append(SR.MS_HAS_JOINED_THE_CHANNEL_AS);
                switch (roleCode) {
                    case ROLE_PARTICIPANT:
                        if (affiliationCode!=AFFILIATION_MEMBER)
                            b.append(SR.MS_ROLE_PARTICIPANT);
                        break;
                    case ROLE_MODERATOR:
                        b.append(SR.MS_ROLE_MODERATOR);
                        break;
                    case ROLE_VISITOR:
                        b.append(SR.MS_ROLE_VISITOR);
                        break;
                }

                 if (!affiliation.equals("none")) {
                    if (roleCode!=ROLE_PARTICIPANT)
                        b.append(SR.MS_AND);
                    switch (affiliationCode) {
                        case AFFILIATION_NONE:
                            b.append(SR.MS_AFFILIATION_NONE);
                            break;
                        case AFFILIATION_MEMBER:
                            b.append(SR.MS_AFFILIATION_MEMBER);
                            break;
                        case AFFILIATION_ADMIN:
                            b.append(SR.MS_AFFILIATION_ADMIN);
                            break;
                        case AFFILIATION_OWNER:
                            b.append(SR.MS_AFFILIATION_OWNER);
                            break;
                    }
                }
                
                if (statusText.length()>0) {
                    b.append(" (");
                    b.append(statusText);
                    b.append(")");
                }
            } else {
                b.append(SR.MS_IS_NOW);
                if ( roleChanged ) {
                    switch (roleCode) {
                        case ROLE_PARTICIPANT:
                            b.append(SR.MS_ROLE_PARTICIPANT);
                            break;
                        case ROLE_MODERATOR:
                            b.append(SR.MS_ROLE_MODERATOR);
                            break;
                        case ROLE_VISITOR:
                            b.append(SR.MS_ROLE_VISITOR);
                            break;
                    }
                }
                 if (affiliationChanged) {
                    if (roleChanged) b.append(SR.MS_AND);
                        switch (affiliationCode) {
                            case AFFILIATION_NONE:
                                b.append(SR.MS_AFFILIATION_NONE);
                                break;
                            case AFFILIATION_MEMBER:
                                b.append(SR.MS_AFFILIATION_MEMBER);
                                break;
                            case AFFILIATION_ADMIN:
                                b.append(SR.MS_AFFILIATION_ADMIN);
                                break;
                            case AFFILIATION_OWNER:
                                b.append(SR.MS_AFFILIATION_OWNER);
                                break;
                        }
                }
                if (!roleChanged && !affiliationChanged)
                    b.append(presence.getPresenceTxt());
            }
//toon
        }
        
        setStatus(presenceType);
        return b.toString();
    }
    
    private void appendL(StringBuffer sb, String append){
//#if NICK_COLORS
//#         sb.append((char)1);
//#endif
        sb.append(append);
//#if NICK_COLORS
//#         sb.append((char)2);
//#endif
    }
    
    public String getTipString() {
        StringBuffer tip=new StringBuffer();
        int nm=getNewMsgsCount();
        
        if (nm!=0) tip.append(nm);
        
        if (realJid!=null) {
            if (tip.length()!=0)  tip.append(' ');
            tip.append(realJid);
        }
        
        return (tip.length()==0)? null:tip.toString();
    }
    
    void toTalks(){
        ConferenceGroup group=(ConferenceGroup)getGroup();
        if ( group.getSelfContact() == this ) {
            StaticData sd=StaticData.getInstance();
            sd.roster.confJoin("bombus-talks@conference.jabber.ru/"+sd.account.getNickName());
        }
    }  
    
    void testMeOffline(){
         ConferenceGroup group=(ConferenceGroup)getGroup();
         if ( group.getSelfContact() == this ) 
            StaticData.getInstance().roster.roomOffline(group);
    }

//#ifdef POPUPS
//#     public void setWobble(String reason) {
//#         ConferenceGroup group=(ConferenceGroup)getGroup();
//#         if ( group.getSelfContact() == this ) {
//#             VirtualList.setWobble("!"+reason);
//#         }
//#     }
//#endif

    public void addMessage(Msg m) {
        super.addMessage(m);
        switch (m.messageType) {
            case Msg.MESSAGE_TYPE_IN: break;
            case Msg.MESSAGE_TYPE_OUT: break;
            case Msg.MESSAGE_TYPE_HISTORY: break;
            default: return;
        }
        lastMessageTime=m.dateGmt;
    }
    
//#ifdef ANTISPAM
//#     public void setPrivateState (int state) {
//#         privateState=state;
//#     }
//#     
//#     public int getPrivateState () {
//#         return privateState;
//#     }
//#endif
}
