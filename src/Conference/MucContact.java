/*
 * MucContact.java
 *
 * Created on 2.05.2006, 14:05
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

package Conference;

import Client.Contact;
import Client.Roster;
import Client.StaticData;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.datablocks.Presence;
import images.RosterIcons;
import locale.SR;
import Client.Msg;
import util.StringUtils;
import xmpp.XmppError;

/**
 *
 * @author root
 */
public class MucContact extends Contact {
    
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
        
        if (presenceType==Presence.PRESENCE_ERROR) return StringUtils.processError(presence, presenceType, (ConferenceGroup) group, this);
        
        JabberDataBlock item=xmuc.getChildBlock("item");   

        String tempRole=item.getAttribute("role");
        if (tempRole.equals("visitor")) roleCode=ROLE_VISITOR;
   else if (tempRole.equals("participant")) roleCode=ROLE_PARTICIPANT;
   else if (tempRole.equals("moderator")) roleCode=ROLE_MODERATOR;
        
        String tempAffiliation=item.getAttribute("affiliation");
        if (tempAffiliation.equals("owner")) affiliationCode=AFFILIATION_OWNER;
   else if (tempAffiliation.equals("admin")) affiliationCode=AFFILIATION_ADMIN;
   else if (tempAffiliation.equals("member")) affiliationCode=AFFILIATION_MEMBER;
   else if (tempAffiliation.equals("none")) affiliationCode=AFFILIATION_NONE;
        
        boolean roleChanged= !tempRole.equals(role);
        boolean affiliationChanged=!tempAffiliation.equals(affiliation);
        role=tempRole;
        affiliation=tempAffiliation;

        setSortKey(nick);

        switch (roleCode) {
            case ROLE_MODERATOR:
                transport=RosterIcons.ICON_MODERATOR_INDEX;
                key0=GROUP_MODERATOR;
                break;
            case ROLE_VISITOR:
                transport=RosterIcons.getInstance().getTransportIndex("muc#vis");
                key0=GROUP_VISITOR;
                break;
            default:
                transport=(affiliation.equals("member"))? 0: RosterIcons.getInstance().getTransportIndex("muc#vis");
                key0=(affiliation.equals("member"))?GROUP_MEMBER:GROUP_PARTICIPANT;
        }
        
        JabberDataBlock statusBlock=xmuc.getChildBlock("status");
        int statusCode = 0;
        try { 
            statusCode=Integer.parseInt( statusBlock.getAttribute("code") ); 
        } catch (Exception e) { }
        
        StringBuffer b=new StringBuffer();
        appendL(b,nick);
        
        String statusText=presence.getChildBlockText("status");
        String tempRealJid=item.getAttribute("jid");

        if (statusCode==201) {
            //todo: fix this nasty hack, it will not work if multiple status codes are nested in presence)
            b.setLength(0);
            b.append(SR.MS_NEW_ROOM_CREATED);
        } else if (presenceType==Presence.PRESENCE_OFFLINE) {
            key0=3;
            String reason=item.getChildBlockText("reason");
            switch (statusCode) {
                case 303:
                    b.append(SR.MS_IS_NOW_KNOWN_AS);
                    String chNick=item.getAttribute("nick");
                    appendL(b,chNick);
                    String newJid=from.substring(0, from.indexOf('/')+1)+chNick;
                    jid.setJid(newJid);
                    bareJid=newJid;
                    from=newJid;
                    nick=chNick;
                    break;
                case 301: //ban
                    presenceType=Presence.PRESENCE_ERROR;
                case 307: //kick
                    b.append((statusCode==301)? SR.MS_WAS_BANNED : SR.MS_WAS_KICKED );
//#ifdef POPUPS
                    if (((ConferenceGroup)group).selfContact == this ) {
                        Roster.setWobble(3, null, ((statusCode==301)? SR.MS_WAS_BANNED : SR.MS_WAS_KICKED)+((!reason.equals(""))?"\n"+reason:""));
                    }
//#endif
                    if (!reason.equals(""))
                        b.append("(").append(reason).append(")");

                    testMeOffline();
                    break;
                case 321:
                case 322:
                    b.append((statusCode==321)?SR.MS_HAS_BEEN_UNAFFILIATED_AND_KICKED_FROM_MEMBERS_ONLY_ROOM:SR.MS_HAS_BEEN_KICKED_BECAUSE_ROOM_BECAME_MEMBERS_ONLY);
                    testMeOffline();
                    break;
                default:
                    if (tempRealJid!=null)
                        b.append(" (").append(tempRealJid).append(")");
                    //else if (from!=null)
                    //    b.append(" (").append(from).append(")");

                    b.append(SR.MS_HAS_LEFT_CHANNEL);
                    
                    if (statusText.length()>0)
                        b.append(" (").append(statusText).append(")");

                    testMeOffline();
            } 
        } else {
            if (this.status==Presence.PRESENCE_OFFLINE) {
                if (tempRealJid!=null) {
                    realJid=tempRealJid;  //for moderating purposes
                    b.append(" (").append(tempRealJid).append(')');
                }
                
                b.append(SR.MS_HAS_JOINED_THE_CHANNEL_AS);
                
                if (affiliationCode==AFFILIATION_MEMBER && roleCode==ROLE_PARTICIPANT) {
                    b.append(getAffiliationLocale(affiliationCode));
                } else {
                    b.append(getRoleLocale(roleCode));
                    if (affiliationCode!=AFFILIATION_NONE) {
                        b.append(SR.MS_AND)
                         .append(getAffiliationLocale(affiliationCode));
                    }
                }
                
                if (statusText.length()>0) b.append(" (").append(statusText).append(")");
                
            } else {
                b.append(SR.MS_IS_NOW);
                
                if (roleChanged) b.append(getRoleLocale(roleCode));

                 if (affiliationChanged) {
                    if (roleChanged) b.append(SR.MS_AND);
                    b.append(getAffiliationLocale(affiliationCode));
                }
                if (!roleChanged && !affiliationChanged) b.append(presence.getPresenceTxt());
            }
        }
        
        setStatus(presenceType);
        return b.toString();
    }

    public static String getRoleLocale(int rol) {
        switch (rol) {
            case ROLE_VISITOR: return SR.MS_ROLE_VISITOR;
            case ROLE_PARTICIPANT: return SR.MS_ROLE_PARTICIPANT;
            case ROLE_MODERATOR: return SR.MS_ROLE_MODERATOR;
        }
        return null;
    }
    
    public static String getAffiliationLocale(int aff) {
        switch (aff) {
            case AFFILIATION_NONE: return SR.MS_AFFILIATION_NONE;
            case AFFILIATION_MEMBER: return SR.MS_AFFILIATION_MEMBER;
            case AFFILIATION_ADMIN: return SR.MS_AFFILIATION_ADMIN;
            case AFFILIATION_OWNER: return SR.MS_AFFILIATION_OWNER;
        }
        return null;
    }
    
    private void appendL(StringBuffer sb, String append){
//#if NICK_COLORS
        sb.append((char)1);
//#endif
        sb.append(append);
//#if NICK_COLORS
        sb.append((char)2);
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

    public void testMeOffline(){
         ConferenceGroup gr=(ConferenceGroup)group;
         if ( gr.selfContact == this ) 
            StaticData.getInstance().roster.roomOffline(gr);
    }

    public void addMessage(Msg m) {
        super.addMessage(m);
        switch (m.messageType) {
            case Msg.MESSAGE_TYPE_IN:
            case Msg.MESSAGE_TYPE_OUT:
            case Msg.MESSAGE_TYPE_HISTORY: break;
            default: return;
        }
        lastMessageTime=m.dateGmt;
    }
}
