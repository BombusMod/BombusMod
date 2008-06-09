/*
 * Contact.java
 *
 * Created on 6.01.2005, 19:16
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
import Conference.MucContact;
//#endif

import Fonts.FontCache;
//#ifdef CLIENTS_ICONS
//# import images.ClientsIcons;
//#endif
import javax.microedition.lcdui.Font;

//#if HISTORY
//# import History.HistoryAppend;
//#endif

import javax.microedition.lcdui.Graphics;

//#ifdef PEP
//# import images.MoodIcons;
//# import ui.ImageList;
//#endif
import images.RosterIcons;
import Colors.ColorTheme;
import ui.Time;
import VCard.VCard;
import java.util.*;
import ui.IconTextElement;
import com.alsutton.jabber.datablocks.Presence;

public class Contact extends IconTextElement{

//#if USE_ROTATOR
    private int isnew=0;
    
    public void setNewContact() { this.isnew = 10; }
//#endif
    
    private ColorTheme ct;
    
//#ifdef PEP    
//#     public int pepMood=-1;
//#     public String pepMoodName=null;
//#     public String pepMoodText=null;
//#ifdef PEP_TUNE
//#     public boolean pepTune;
//#     public String pepTuneText=null;
//#endif
//#endif
    
    public final static short ORIGIN_ROSTER=0;
    public final static short ORIGIN_ROSTERRES=1;
    public final static short ORIGIN_CLONE=2;
    public final static short ORIGIN_PRESENCE=3;
    public final static short ORIGIN_GROUPCHAT=4;
//#ifndef WMUC
    public final static short ORIGIN_GC_MEMBER=5;
    public final static short ORIGIN_GC_MYSELF=6;
//#endif
    
    public String nick;

    public Jid jid;
    public String bareJid;    // for roster/subscription manipulating
    public int status;
    public int priority;
    private Group group;
    public int transport;
    
    public boolean autoresponded=false;
    
    public boolean moveToLatest=false;

    public String presence;
    public String statusString;
    
    public boolean acceptComposing;
    public boolean showComposing=false;
    
    public short deliveryType;

    public final static short INC_NONE=0;
    public final static short INC_APPEARING=1;
    public final static short INC_VIEWING=2;
    
    public short incomingState=INC_NONE;
    
    public String msgSuspended;
    
    protected short key0;
    protected String key1;

    public byte origin;
    
    public String subscr;
    public int offline_type=Presence.PRESENCE_UNKNOWN;
    public boolean ask_subscribe;
    
    public Vector msgs;
    public int activeMessage=-1;

//#ifdef ANTISPAM
//#     public Vector tempMsgs=new Vector();
//#endif
    
    private int newMsgCnt=0;
    private int newHighLitedMsgCnt=0;
    public int unreadType;
    public int lastUnread;
    
    public VCard vcard;

    private int client=-1;
    
//#if AUTODELETE
//#     public boolean redraw=false;
//#endif
    
    private Config cf;
    private RosterIcons ri = RosterIcons.getInstance();

    private String j2j;
    
    private boolean loaded=false;
    
    private Font secondFont=FontCache.getBalloonFont();
    private int secondFontHeight;
    
    private int fontHeight;

    int ilHeight;

    protected Contact (){
        super(RosterIcons.getInstance());
        cf=Config.getInstance();
        ct=ColorTheme.getInstance();
        msgs=new Vector();
        key1="";

        ilHeight=il.getHeight();
        secondFontHeight=secondFont.getHeight();
        fontHeight=getFont().getHeight();
    }
    
    public int firstUnread(){
        int unreadIndex=0;
        for (Enumeration e=msgs.elements(); e.hasMoreElements();) {
            if (((Msg)e.nextElement()).unread)
                break;
            unreadIndex++;
        }
        return unreadIndex;
    }

    public Contact(final String Nick, final String sJid, final int Status, String subscr) {
        this();
        nick=Nick; jid= new Jid(sJid); status=Status;
        bareJid=sJid;
        this.subscr=subscr;
    
        setSortKey((Nick==null)?sJid:Nick);
        
        //calculating transport
        transport=ri.getTransportIndex(jid.getTransport());
    }
    
    public Contact clone(Jid newjid, final int status) {
        Contact clone=new Contact();
        clone.group=group; 
        clone.jid=newjid; 
        clone.nick=nick;
        clone.key1=key1;
        clone.subscr=subscr;
        clone.offline_type=offline_type;
        clone.origin=ORIGIN_CLONE; 
        clone.status=status; 
        clone.transport=ri.getTransportIndex(newjid.getTransport()); //<<<<
//#ifdef PEP
//#         clone.pepMood=pepMood;
//#         clone.pepMoodName=pepMoodName;
//#         clone.pepMoodText=pepMoodText;
//#ifdef PEP_TUNE
//#         clone.pepTune=pepTune;
//#         clone.pepTuneText=pepTuneText;
//#endif
//#endif
        clone.bareJid=bareJid;
        return clone;
    }
    
    public int getColor() {
//#if USE_ROTATOR        
        if (isnew>0){
            isnew--;
            return (isnew%2==0)?0xFF0000:0x0000FF;
        }
//#endif
        if (j2j!=null)
            return ct.getColor(ColorTheme.CONTACT_J2J);
        
        switch (status) {
            case Presence.PRESENCE_CHAT: return ct.getColor(ColorTheme.CONTACT_CHAT);
            case Presence.PRESENCE_AWAY: return ct.getColor(ColorTheme.CONTACT_AWAY);
            case Presence.PRESENCE_XA: return ct.getColor(ColorTheme.CONTACT_XA);
            case Presence.PRESENCE_DND: return ct.getColor(ColorTheme.CONTACT_DND);
        }
        return ct.getColor(ColorTheme.CONTACT_DEFAULT);
    }

    public int getNewMsgsCount() {
        if (newMsgCnt>0) return newMsgCnt;
        int nm=0;
        if (getGroupType()!=Groups.TYPE_IGNORE) {
            unreadType=Msg.MESSAGE_TYPE_IN;
            for (Enumeration e=msgs.elements(); e.hasMoreElements(); ) {
                Msg m=(Msg)e.nextElement();
                if (m.unread) {
                    nm++;
                    if (m.messageType==Msg.MESSAGE_TYPE_AUTH) 
                        unreadType=m.messageType;
                }
            }
        }
        return newMsgCnt=nm;
    }
    
    public int getNewHighliteMsgsCount() {
        if (newHighLitedMsgCnt>0) return newHighLitedMsgCnt;
        int nm=0;
        if (getGroupType()!=Groups.TYPE_IGNORE) {
            for (Enumeration e=msgs.elements(); e.hasMoreElements(); ) {
                Msg m=(Msg)e.nextElement();
                if (m.unread && m.isHighlited()) { 
                    nm++;
                }
            }
        }
        return newHighLitedMsgCnt=nm;
    }

    public boolean active(){
        return (activeMessage>-1);
    }
    
    public void resetNewMsgCnt() { newMsgCnt=0; newHighLitedMsgCnt=0; }
  
    public void setIncoming (int state) {
        short i=0;
        switch (state){
            case INC_APPEARING:
                i=RosterIcons.ICON_APPEARING_INDEX;
                break;
            case INC_VIEWING:
                i=RosterIcons.ICON_VIEWING_INDEX;
                break;
        }
        incomingState=i;
    }
    
    public int compare(IconTextElement right){
        Contact c=(Contact) right;
        int cmp;
        if ((cmp=key0-c.key0) !=0) return cmp;
        if ((cmp=status-c.status) !=0) return cmp;
        if ((cmp=key1.compareTo(c.key1)) !=0) return cmp;
        if ((cmp=c.priority-priority) !=0) return cmp;
        return c.transport-transport;
    }
    
    public void addMessage(Msg m) {
        boolean first_replace=false;
        if (origin!=ORIGIN_GROUPCHAT) {
            if (m.isPresence()) {
                presence=m.getBody();
                if (msgs.size()==1) 
                    if (((Msg)msgs.firstElement()).isPresence())
                        first_replace=true;
            }
        }
//#if AUTODELETE
//#             else { redraw=deleteOldMessages(); }
//#endif
//#if HISTORY
//#         if (!m.isHistory()) {
//#             if (cf.msgPath!=null && group.type!=Groups.TYPE_TRANSP && group.type!=Groups.TYPE_SEARCH_RESULT) {
//#                 boolean allowLog=false;
//#                 switch (m.messageType) {
//#                     case Msg.MESSAGE_TYPE_PRESENCE:
//#                         if (origin>=ORIGIN_GROUPCHAT) {
//#                             if (cf.msgLogConfPresence)
//#                                 allowLog=true;
//#                         } else  if (cf.msgLogPresence) {
//#                             allowLog=true;
//#                         }
//#                         break;
//#                     case Msg.MESSAGE_TYPE_HISTORY:
//#                         break;
//#                     default:
//#                         if (origin>=ORIGIN_GROUPCHAT && cf.msgLogConf) allowLog=true;
//#                         if (origin<ORIGIN_GROUPCHAT && cf.msgLog) allowLog=true;
//#                 }
//# 
//#ifndef WMUC
//#                 if (origin!=ORIGIN_GROUPCHAT && this instanceof MucContact)
//#                      allowLog=false;
//#endif
//#                 
//#                 if (allowLog) {
//#                     new HistoryAppend(m, cf.lastMessages, getBareJid());
//#                 }
//#             }
//#        }
//#endif
        if (first_replace) {
            msgs.setElementAt(m,0);
            return;
        }

        if (cf.autoScroll)
            moveToLatest=true;
        
        if (m.messageType!=Msg.MESSAGE_TYPE_HISTORY && m.messageType!=Msg.MESSAGE_TYPE_PRESENCE)
            activeMessage=msgs.size()+1;

        msgs.addElement(m);
        
        if (m.unread) {
            lastUnread=msgs.size()-1;
            if (m.messageType>unreadType) unreadType=m.messageType;
            if (newMsgCnt>=0) newMsgCnt++;
        }
    }

    public int getFontIndex(){
        if (cf.showResources)
            return (cf.useBoldFont && status<5)?1:0;

        return active()?1:0;
    }

    public final String getName(){ 
        return (nick==null)?getBareJid():nick; 
    }

    public final String getJid() {
        return jid.getJid();
    }

    public final String getBareJid() {
        return bareJid;
    }

    public String getNickJid() {
        if (nick==null) 
            return bareJid;
        return nick+" <"+bareJid+">";
    }
    
    public final void purge() {
//#ifdef ANTISPAM
//#        try {
//#            purgeTemps();
//#        } catch (Exception e) { }
//#endif
        msgs=new Vector();
        
        activeMessage=-1; //drop activeMessage num
        
        resetNewMsgCnt();
        
        try {
            if (vcard!=null) {
                vcard.clearVCard();
                vcard=null;
            }
        } catch (Exception e) { }
    }
    
//#if AUTODELETE
//#     public final boolean deleteOldMessages() {
//#         int limit=cf.msglistLimit;
//#         if (msgs.size()<limit)
//#             return false;
//#         
//#         int trash = msgs.size()-limit;
//#             for (int i=0; i<trash; i++)
//#                 msgs.removeElementAt(0);
//#         
//#         return true;
//#     }
//#endif
 
    public final void smartPurge(int cursor) {
        try {
            if (cursor==msgs.size() && msgs.size()>0) {
                msgs=new Vector();
                activeMessage=-1; //drop activeMessage num
            } else {
                int cp=-1;
                for (int i=0; i<cursor; i++) {
                    cp++;
                    msgs.removeElementAt(0);
                }
                activeMessage=activeMessage-cp; //drop activeMessage count
            }
        } catch (Exception e) { }
        try {
            if (vcard!=null) {
                vcard.clearVCard();
                vcard=null;
            }
        } catch (Exception e) { }
        
        lastUnread=0;
        resetNewMsgCnt();
    }
    
    public final void setSortKey(String sortKey){
        key1=(sortKey==null)? "": sortKey.toLowerCase();
    }

    public String getTipString() {
        int nm=getNewMsgsCount();
        if (nm!=0) 
            return String.valueOf(nm);
        if (nick!=null) 
            return bareJid;
        return null;
    }

    public Group getGroup() { return group; }
    
    public int getGroupType() {  
        if (group==null) 
            return 0; 
        return group.type;
    }
    
    public boolean inGroup(Group ingroup) { return group==ingroup; }

    public void setGroup(Group group) { this.group = group; }
    
    public String getJ2J() { return j2j; }

    public void setJ2J(String j2j) { this.j2j = j2j; }

    public void setStatus(int status) {
        setIncoming(0);
        this.status = status;
        if (status>=Presence.PRESENCE_OFFLINE) 
            acceptComposing=false;
    }

    public int getStatus() { return status; }
    
    public void setComposing (boolean state) {
        showComposing=state;
    }
   
    void markDelivered(String id) {
        if (id==null) return;
        for (Enumeration e=msgs.elements(); e.hasMoreElements();) {
            Msg m=(Msg)e.nextElement();
            if (m.id!=null)
                if (m.id.equals(id)) 
                    m.delivered=true;
        }
    }
    
//#ifdef ANTISPAM
//#     public void addTempMessage(Msg m) { tempMsgs.addElement(m); }
//# 
//#     public final void purgeTemps() { tempMsgs=new Vector(); }
//#endif
    
    public int getVWidth(){
        String str=(!cf.rosterStatus)?getFirstString():(getFirstLength()>getSecondLength())?getFirstString():getSecondString();
        int wft=getFont().stringWidth(str);
        
        return wft+il.getWidth()+4;
    }
    
    public String toString() {
        return getFirstString();
    }

    public int getSecondLength() {
        if (getSecondString()==null) return 0;
        if (getSecondString()=="") return 0;
        return secondFont.stringWidth(getSecondString());
    }

    public int getFirstLength() {
        if (getFirstString()==null) return 0;
        if (getFirstString()=="") return 0;
        return getFont().stringWidth(getFirstString());
    }
    
    public String getFirstString() {
        if (!cf.showResources)
            return (nick==null)?getJid():nick;
        if (origin>ORIGIN_GROUPCHAT) 
            return nick;
        if (origin==ORIGIN_GROUPCHAT) 
            return getJid();
        return (nick==null)?getJid():nick+jid.getResource(); 
    }
    
    public String getSecondString() {
        if (cf.rosterStatus){
            if (statusString!=null)
                return statusString;
//#if PEP
//#             return getMoodString();
//#endif
        }
        return null;
    }

    public int getImageIndex() {
        if (showComposing==true) 
            return RosterIcons.ICON_COMPOSING_INDEX;
        int st=(status==Presence.PRESENCE_OFFLINE)?offline_type:status;
        if (st<8) st+=transport; 
        return st;
    }

    public int getSecImageIndex() {
//#ifdef ANTISPAM
//#         if (!tempMsgs.isEmpty())
//#             return RosterIcons.ICON_AUTHRQ_INDEX;
//#endif
  
        if (getNewMsgsCount()>0)  {
            switch (unreadType) {
                case Msg.MESSAGE_TYPE_AUTH: return RosterIcons.ICON_AUTHRQ_INDEX;
                default: return RosterIcons.ICON_MESSAGE_INDEX;
            }
        }

        if (incomingState>0) 
            return incomingState;
        return -1;
    }
//#if HISTORY
//#     public boolean isHistoryLoaded () { return loaded; }
//#     
//#     public void setHistoryLoaded (boolean state) { loaded=state; }
//#endif
    
    public void setClient (int client) { this.client=client; }
    
    public int getClient () { return client; }
    
//#ifdef PEP
//#ifdef PEP_TUNE
//#     public void setUserTune (String tune) { pepTuneText=tune; }
//#     
//#     public String getUserTune() { return pepTuneText; }
//#endif
//#     public void setUserMood (String mood) {
//#         pepMoodName=mood;
//#     }
//#     public void setUserMoodText (String mood) {
//#         pepMoodText=mood;
//#     }
//#     
//#     public String getUserMood() {
//#         return pepMoodName;
//#     }
//#     
//#     public String getUserMoodText() {
//#         return pepMoodText;
//#     }
//#     public String getMoodString() {
//#         StringBuffer mood=null;
//#         if (hasMood()) {
//#              mood=new StringBuffer(pepMoodName);
//#              if (pepMoodText!=null) {
//#                 if (pepMoodText.length()>0) {
//#                      mood.append("(")
//#                          .append(pepMoodText)
//#                          .append(")");
//#                 }
//#              }
//#         }
//#         return (mood!=null)?mood.toString():null;
//#     }
//#endif
    
    public int getVHeight(){ 
        int itemVHeight=(ilHeight>fontHeight)?ilHeight:fontHeight;
        if (getSecondString()!=null)
            itemVHeight+=secondFontHeight-3;
        
        return itemVHeight;
    }

    public void drawItem(Graphics g, int ofs, boolean sel) {
        int w=g.getClipWidth();
        int h=getVHeight();
        int xo=g.getClipX();
        int yo=g.getClipY();
        
        int offset=4;
       
        int imgH=(h-ilHeight)/2;
        
        if (getImageIndex()>-1) {
            offset+=ilHeight;
            il.drawImage(g, getImageIndex(), 2, imgH);
        }
//#ifdef CLIENTS_ICONS
//#         if (client>-1) {
//#             ImageList clients=ClientsIcons.getInstance();
//#             w-=clients.getWidth();
//#             clients.drawImage(g, client, w, (h-clients.getHeight())/2);
//#         }
//#endif
//#ifdef PEP
//#         if (hasMood()) {
//#             ImageList moods=MoodIcons.getInstance();
//#             w-=moods.getWidth();
//#             moods.drawImage(g, pepMood, w, (h-moods.getHeight())/2);
//#         }
//#ifdef PEP_TUNE
//#         if (pepTune) {
//#             w-=ilHeight;
//#             il.drawImage(g, RosterIcons.ICON_PROFILE_INDEX+1, w,imgH);
//#         }
//#endif
//#endif
        if (getSecImageIndex()>-1) {
            w-=ilHeight;
            il.drawImage(g, getSecImageIndex(), w,imgH);
        }

        int thisOfs=0;
        
        g.setClip(offset, yo, w-offset, h);

        thisOfs=(getFirstLength()>w)?-ofs+offset:offset;
        g.setFont(getFont());
        g.drawString(getFirstString(), thisOfs, 0, Graphics.TOP|Graphics.LEFT);

        if (getSecondString()!=null) {
            int y=getFont().getHeight()-3;
            thisOfs=(getSecondLength()>w)?-ofs+offset:offset;
            g.setFont(secondFont);
            g.setColor(ct.getColor(ColorTheme.SECOND_LINE));
            g.drawString(getSecondString(), thisOfs, y, Graphics.TOP|Graphics.LEFT);
        }
        g.setClip(xo, yo, w, h);
    }
//#ifdef PEP
//#     boolean hasMood() {
//#         return (pepMood>-1 && pepMood<61);
//#     }
//#endif
}
