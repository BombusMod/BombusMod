/*
 * Contact.java
 *
 * Created on 6.01.2005, 19:16
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

package Client;

//#ifndef WMUC
import Conference.MucContact;
//#endif

import History.HistoryStorage;

//#if (FILE_IO && HISTORY)
//# import History.HistoryAppend;
//#endif

//#ifdef MOOD
//# import UserMood.Mood;
//# import UserMood.MoodList;
//# import images.MoodIcons;
//#endif

import images.RosterIcons;
import Colors.Colors;
import ui.Time;
import vcard.VCard;
import java.util.*;
import ui.IconTextElement;
import com.alsutton.jabber.datablocks.Presence;

/**
 * Contact
 * @author Eugene Stahov
 */
public class Contact extends IconTextElement{
    
//#ifdef CHECKERS
//#     private int checkers = -4; //END_GAME_FLAG
//#endif

//#if USE_ROTATOR
//#     private int isnew=0;    
//#     public void setNewContact() {
//#         this.isnew = 10;        
//#     }
//#endif
    
    public int getColor() {
//#if USE_ROTATOR        
//#     if (isnew>0){
//#         isnew--;
//#             return (isnew%2==0)?0xFF0000:0x0000FF;
//#     }
//#endif
        if (j2j!=null)
            return Colors.CONTACT_J2J;
        
        switch (status) {
            case Presence.PRESENCE_CHAT: return Colors.CONTACT_CHAT;
            case Presence.PRESENCE_AWAY: return Colors.CONTACT_AWAY;
            case Presence.PRESENCE_XA: return Colors.CONTACT_XA;
            case Presence.PRESENCE_DND: return Colors.CONTACT_DND;
        }
        return Colors.CONTACT_DEFAULT;
    }
//#ifdef MOOD
//#     public Mood mood;
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
//#ifdef IRC_LIKE
//#     public String addStatus;
//#     public String origNick;
//#endif
    public Jid jid;
    public String bareJid;    // for roster/subscription manipulating
    protected int status;
    public int priority;
    private Group group;
    public int transport;
    
    public boolean autoresponded=false;
    
    public long ping=-1;
    
    public boolean moveToLatest=false;

    public String presence;
    public String statusString=null;
    
    public boolean acceptComposing;
    public boolean showComposing=false;
    
    public short deliveryType;
    
    public short incomingState=0;
    
    public final static short INC_NONE=0;
    public final static short INC_APPEARING=1;
    public final static short INC_VIEWING=2;
    
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
    
    private int newMsgCnt=-1;
    public int unreadType;
    public int lastUnread;
    
    public VCard vcard;
    
    public boolean hasCaps;
    public String capsNode;
    public String capsVer;
    
//#if AUTODELETE
//#     public boolean redraw=false;
//#endif
    
    private Config cf=Config.getInstance();
    private RosterIcons ri = RosterIcons.getInstance();

    private String j2j;
    
    private boolean loaded=false;

    protected Contact (){
        super(RosterIcons.getInstance(), 
//#ifdef MOOD
//#                 MoodIcons.getInstance()
//#else
                null
//#endif
                );
        msgs=new Vector();
        key1="";
    }
    
    public int firstUnread(){
        int unreadIndex=0;
        for (Enumeration e=msgs.elements(); e.hasMoreElements();) {
            if (((Msg)e.nextElement()).unread) break;
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
        //clone.mood=mood;
        clone.transport=ri.getTransportIndex(newjid.getTransport()); //<<<<

        clone.bareJid=bareJid;
        return clone;
    }

    public int getImageIndex() {
        int st=(status==Presence.PRESENCE_OFFLINE)?offline_type:status;
        if (st<8) st+=transport; 
        return st;
    }
    
    public int getNewMsgsCount() {
        if (getGroupType()==Groups.TYPE_IGNORE) return 0;
        if (newMsgCnt>-1) return newMsgCnt;
        int nm=0;
        unreadType=Msg.MESSAGE_TYPE_IN;
        for (Enumeration e=msgs.elements(); e.hasMoreElements(); ) {
            Msg m=(Msg)e.nextElement();
            if (m.unread) { 
                nm++;
                if (m.messageType==Msg.MESSAGE_TYPE_AUTH) 
                    unreadType=m.messageType;
            }
        }
        return newMsgCnt=nm;
    }
    
    public int getNewHighliteMsgsCount() {
        if (getGroupType()==Groups.TYPE_IGNORE) return 0;
        int nm=0;
        for (Enumeration e=msgs.elements(); e.hasMoreElements(); ) {
            Msg m=(Msg)e.nextElement();
            if (m.unread && m.isHighlited()) { 
                nm++;
            }
        }
        return nm;
    }

    public boolean active(){
        if (msgs.size()==0) 
            return false;
        
        if (activeMessage>-1)
            return true;
        return false;
    }
    
    public void resetNewMsgCnt() { newMsgCnt=-1;}
  
    public void setIncoming (int state) {
        short i=0;
        switch (state){
            case INC_APPEARING:
                i=2001;//ICON_APPEARING_INDEX;
                break;
            case INC_VIEWING:
                i=2002;//ICON_VIEWING_INDEX;
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
        if (m.isPresence()) {
            presence=m.getBody();
            if (msgs.size()==1) 
                if ( ((Msg)msgs.firstElement()).isPresence())
                    if (origin!=ORIGIN_GROUPCHAT) 
                       first_replace=true;
        }
//#if AUTODELETE
//#             else { redraw=deleteOldMessages(); }
//#endif
//#if FILE_IO && HISTORY
//# 
//#         if (cf.msgLog && cf.msgPath==null) {
//#ifdef POPUPS
//#             StaticData.getInstance().roster.setWobbler(3, (Contact) null, "Please enter valid path to store log");
//#endif
//#         } else 
//#             if (cf.msgLog && group.type!=Groups.TYPE_TRANSP && group.type!=Groups.TYPE_SEARCH_RESULT)
//#         {
//#             //String histRecord=(nick==null)?getBareJid():nick;
//#             boolean allowLog=false;
//#             switch (m.messageType) {
//#                 case Msg.MESSAGE_TYPE_PRESENCE:
//#                     if (origin>=ORIGIN_GROUPCHAT && cf.msgLogConfPresence) allowLog=true;
//#                     if (origin<ORIGIN_GROUPCHAT && cf.msgLogPresence) allowLog=true;
//#                     break;
//#                 default:
//#                     if (origin>=ORIGIN_GROUPCHAT && cf.msgLogConf) allowLog=true;
//#                     if (origin<ORIGIN_GROUPCHAT) allowLog=true;
//#             }
//#             if (m.isHistory())
//#                 allowLog=false;
//#ifndef WMUC
//#             if (origin!=ORIGIN_GROUPCHAT && this instanceof MucContact)
//#                  allowLog=false;
//#endif
//#             if (allowLog)
//#             {
//#                 //StringBuffer body = createBody(m);
//#                 
//#                 String histRecord=(nick==null)?getBareJid():nick;
//#                 
//#                 new HistoryAppend(m, cf.lastMessages, histRecord);
//#             }
//#        }
//#endif
        if (cf.autoScroll)
            moveToLatest=true;
        
        if (m.messageType!=Msg.MESSAGE_TYPE_HISTORY && m.messageType!=Msg.MESSAGE_TYPE_PRESENCE)
            activeMessage=msgs.size()+1;
                    
        if (first_replace) {
            msgs.setElementAt(m,0);
            return;
        } 
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
    
    public String toString() {
        if (!cf.showResources)
            return (nick==null)?getJid():nick;
        if (origin>ORIGIN_GROUPCHAT) 
            return nick;
        if (origin==ORIGIN_GROUPCHAT) 
            return getJid();
        return (nick==null)?getJid():nick+jid.getResource(); 
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

    public Group getGroup() { 
        return group; 
    }
    
    public int getGroupType() {  
        if (group==null) 
            return 0; 
        return group.type;
    }
    
    public boolean inGroup(Group ingroup) {  
        return group==ingroup;  
    }

    public void setGroup(Group group) { 
        this.group = group; 
    }
    
    public String getJ2J() {  
        return j2j;  
    }

    public void setJ2J(String j2j) { 
        this.j2j = j2j; 
    }

    public void setStatus(int status) {
        setIncoming(0);
        this.status = status;
        if (status>=Presence.PRESENCE_OFFLINE) 
            acceptComposing=false;
    }

    public int getStatus() {
        return status;
    }
    
    public void setPing() {
        this.ping = Time.utcTimeMillis();
    }
    
    public void setComposing (boolean state) {
        showComposing=state;
    }

    public String getPing() {
        if (ping==-1) 
            return "";
        
        String timePing=Long.toString((Time.utcTimeMillis()-ping)/10);
        int dotpos=timePing.length()-2;
        String first = (dotpos==0)? "0":timePing.substring(0, dotpos);
        String second = timePing.substring(dotpos);
        
        StringBuffer s=new StringBuffer(first);
        s.append('.');
        s.append(second);
        s.append(' ');
        s.append(Time.goodWordForm (Integer.parseInt(second), 0));

        this.ping=-1;
        return String.valueOf(s);
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
//#     public void addTempMessage(Msg m) {
//#         tempMsgs.addElement(m);
//#     }
//# 
//#     public final void purgeTemps() {
//#         tempMsgs=new Vector();
//#     }
//#endif
    
//#ifdef SECONDSTRING
//#     public String getSecondString() {
//#         StringBuffer s=new StringBuffer();
//#         if (cf.rosterStatus) {
//#             if (mood!=null) {
//#                 s.append(getUserMoodLocale());
//#                 if (getUserMoodText()!="") {
//#                     s.append(" (");
//#                     s.append(getUserMoodText());
//#                     s.append(")");
//#                 }
//#             } else if (statusString!=null)
//#                 s.append(statusString);
//#             
//#             return (s.toString().length()<1)?null:s.toString();
//#         }
//#         s=null;
//#         return null;
//#     }
//#endif
//#ifdef MOOD
//#     public String getUserMoodLocale() {
//#         return mood.getLocale();
//#     }
//#     
//#     public void setUserMood (Mood mood) {
//#         this.mood=mood;
//#     }
//#     
//#     public String getUserMoodText() {
//#         return mood.getText();
//#     }
//#endif
    public int getSecImageIndex() {
//#ifdef ANTISPAM
//#         if (!tempMsgs.isEmpty())
//#             return 2003;
//#endif
        if (getNewMsgsCount()>0)  {
            switch (unreadType) {
                case Msg.MESSAGE_TYPE_AUTH: return 2003;
                default: return 2004;
            }
        }
        if (showComposing==true) 
            return 1001;

        if (incomingState>0) 
            return incomingState;
//#ifdef MOOD
//#         if (mood!=null)
//#             return mood.getImageIndex();
//#endif
        return -1;
    }
    
    public boolean isHistoryLoaded () {
        return loaded;
    }
    
    public void loadRecentList() {
        loaded=true;
        HistoryStorage hs = new HistoryStorage((nick==null)?getBareJid():nick);
        Vector history=hs.importData();
        
        for (Enumeration messages=history.elements(); messages.hasMoreElements(); )  {
            Msg message=(Msg) messages.nextElement();
            if (!isMsgExists(message)) {
                message.setHistory(true);
                msgs.insertElementAt(message, 0);
            }
        }
    }
    
    private boolean isMsgExists(Msg msg) {
         for (Enumeration messages=msgs.elements(); messages.hasMoreElements(); )  {
            Msg message=(Msg) messages.nextElement();
            if (message.getBody().equals(msg.getBody())) {
                return true;
            }
         }
        return false;
    }

//#ifdef CHECKERS
//#     public void setCheckers(int checkers) {
//#         this.checkers=checkers;
//#     }
//# 
//#     public int getCheckers() {
//#         return checkers;
//#     }
//#endif
}
