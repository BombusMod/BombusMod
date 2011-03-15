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
import images.ClientsIcons;
//#endif
//#if HISTORY
//# import History.HistoryAppend;
//#endif
import javax.microedition.lcdui.Graphics;
import ui.ImageList;
//#ifdef PEP
//# import images.MoodIcons;
//# import PEP.Moods;
//#endif
import images.RosterIcons;
import Colors.ColorTheme;
import Messages.MessageItem;
import VCard.VCard;
import ui.IconTextElement;
import com.alsutton.jabber.datablocks.Presence;
import java.util.Enumeration;
import java.util.Vector;

public class Contact extends IconTextElement{

//#if USE_ROTATOR
    private int isnew=0;
    public void setNewContact() { this.isnew = 8; }
//#endif

//#ifdef PEP    
//#     public int pepMood=-1;
//#     public String pepMoodName=null;
//#     public String pepMoodText=null;
//#ifdef PEP_TUNE
//#     public boolean pepTune;
//#     public String pepTuneText=null;
//#endif
//#ifdef PEP_ACTIVITY
//#     public String activity=null;
//#endif
//#ifdef PEP_LOCATION
//#     public String location = null;
//#endif
//# 
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
    public String bareJid; // for roster/subscription manipulating
    public int status;
    public int priority;
    public Group group;
    public int transport;
    
    public boolean autoresponded=false;
    
    public boolean moveToLatest=false;

    public String presence;
    public String statusString;
    
    public boolean acceptComposing;
    public boolean showComposing=false;
    
    public short deliveryType;
    
    public short incomingState=INC_NONE;
    
    public final static short INC_NONE=0;
    public final static short INC_APPEARING=1;
    public final static short INC_VIEWING=2;
    
    protected short key0;
    protected String key1;

    public byte origin;
    
    public String subscr;
    public int offline_type=Presence.PRESENCE_UNKNOWN;
    public boolean ask_subscribe;

    public final Vector msgs;
    public int activeMessage=-1;
    
    private int newMsgCnt=0;
    private int newHighLitedMsgCnt=0;
    public int unreadType;
    public int lastUnread;
    public int mark = -1;

    public String msgSuspended;
    public String lastSendedMessage;
    
    public VCard vcard;
//#ifdef CLIENTS_ICONS
    public int client=-1;
    public String clientName=null;
//#endif

//#ifdef LOGROTATE
//#     public boolean redraw=false;
//#endif
    private Config cf;

    public String j2j;
    public String lang;
    public String version;
    
//#ifdef FILE_TRANSFER
    public boolean fileQuery;
//#endif

//#ifdef HISTORY
//#ifdef LAST_MESSAGES
//#     private boolean loaded;
//#endif
//#endif
    
    StaticData sd=StaticData.getInstance();
    
    //private Font secondFont; //Issue 88
    //private int secondFontHeight;
    
    private int fontHeight;
    int ilHeight;
    int maxImgHeight;

    private boolean smiles = false;
    
    protected Contact() {
        super(RosterIcons.getInstance());
        cf=Config.getInstance();
//#ifdef SMILES
        smiles  = cf.smiles;
//#endif

        msgs=new Vector();
        
        key1="";

        ilHeight=il.getHeight();
        maxImgHeight=ilHeight;
        
        //secondFont=FontCache.getFont(false, FontCache.baloon);
        //secondFontHeight=secondFont.getHeight();
        fontHeight=getFont().getHeight();
    }

    public Contact(final String Nick, final String sJid, final int Status, String subscr) {
        this();
        nick=Nick; 
        jid= new Jid(sJid);
        status=Status;
        
        bareJid=sJid;
        this.subscr=subscr;
    
        setSortKey((Nick==null)?sJid:Nick);
        
        //calculating transport
        transport=RosterIcons.getInstance().getTransportIndex(jid.getTransport());
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
        clone.transport=RosterIcons.getInstance().getTransportIndex(newjid.getTransport()); //<<<<
//#ifdef PEP
//#         clone.pepMood=pepMood;
//#         clone.pepMoodName=pepMoodName;
//#         clone.pepMoodText=pepMoodText;
//#ifdef PEP_TUNE
//#         clone.pepTune=pepTune;
//#         clone.pepTuneText=pepTuneText;
//#endif
//#ifdef PEP_ACTIVITY
//#         clone.activity=activity;
//#endif
//#ifdef PEP_LOCATION
//#         clone.location=location;
//#endif
//# 
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
        if (j2j!=null) return ColorTheme.getColor(ColorTheme.CONTACT_J2J);

        return getMainColor();
    }
    
    public int getMainColor() {
        switch (status) {
            case Presence.PRESENCE_CHAT: return ColorTheme.getColor(ColorTheme.CONTACT_CHAT);
            case Presence.PRESENCE_AWAY: return ColorTheme.getColor(ColorTheme.CONTACT_AWAY);
            case Presence.PRESENCE_XA: return ColorTheme.getColor(ColorTheme.CONTACT_XA);
            case Presence.PRESENCE_DND: return ColorTheme.getColor(ColorTheme.CONTACT_DND);
        }
        return ColorTheme.getColor(ColorTheme.CONTACT_DEFAULT);
    }
    public boolean haveChatMessages() {
        for (Enumeration e = msgs.elements(); e.hasMoreElements();) {
            Msg msg = ((MessageItem)e.nextElement()).msg;
            if (msg.messageType == Msg.MESSAGE_TYPE_IN || msg.messageType == Msg.MESSAGE_TYPE_OUT || msg.messageType == Msg.MESSAGE_TYPE_AUTH)
                return true;
        }
        return false;
    }

    public int getNewMsgsCount() {
        if (msgs.isEmpty()) {
            return 0;
        }
      /*  if (newMsgCnt > 0) {
            return newMsgCnt;
        }*/
        int nm = 0;
        if (getGroupType() != Groups.TYPE_IGNORE) {
            unreadType = Msg.MESSAGE_TYPE_IN;

            for (Enumeration e = msgs.elements(); e.hasMoreElements();) {
                Msg m = ((MessageItem)e.nextElement()).msg;
                if (m.unread) {
                    nm++;
                    if (m.messageType == Msg.MESSAGE_TYPE_AUTH) {
                        unreadType = m.messageType;
                    }
                }
            }
        }
        return newMsgCnt = nm;
    }

    public int getNewHighliteMsgsCount() {
        if (newHighLitedMsgCnt>0) return newHighLitedMsgCnt;
        int nm=0;
        if (getGroupType()!=Groups.TYPE_IGNORE) {
            for (Enumeration e = msgs.elements(); e.hasMoreElements();) {
                Msg m = ((MessageItem)e.nextElement()).msg;
                if (m.unread && m.highlite) { 
                    nm++;
                }
            }
        }
        return newHighLitedMsgCnt=nm;
    }

    public boolean active() {
        if (msgSuspended!=null) return true;
        return (activeMessage>-1);
    }
    
    public void resetNewMsgCnt() { newMsgCnt=0; newHighLitedMsgCnt=0; }
  
    public void setIncoming (int state) {
        if (!cf.IQNotify && state==INC_VIEWING) return;

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
        boolean last_replace = false;
        if (origin != ORIGIN_GROUPCHAT) {
            if (msgs.size() > 0 && m.isPresence()) {
                Object item = msgs.lastElement();
                if (item != null) {
                    if (((MessageItem) item).msg.isPresence()) {
                        last_replace = true;
                    }
                }
            } else {
                if (cf.showNickNames) {
                    StringBuffer who = new StringBuffer();
                    who.append((m.messageType == Msg.MESSAGE_TYPE_OUT) ? sd.account.getNickName() : getName()).append(" (").append(m.getTime()).append(") ");
                    if (m.subject != null) {
                        who.append("\n").append(m.subject);
                    }
                    m.subject = who.toString();
                }
                if (m.body.startsWith("/me ")) {
                    StringBuffer b=new StringBuffer();
//#if NICK_COLORS
                    b.append("\01");
//#endif
                    b.append((m.messageType==Msg.MESSAGE_TYPE_OUT)?sd.account.getNickName():getName());
//#if NICK_COLORS
                    b.append("\02");
//#endif
                    b.insert(0,'*');
                    b.append(m.body.substring(3));
                    m.body=b.toString();
                    b=null;
                }
            }
        } else {
            status=Presence.PRESENCE_ONLINE;
//#ifdef LOGROTATE
//#             redraw=deleteOldMessages();
//#endif
        }
//#if HISTORY
//#ifdef PLUGINS
//#     if(cf.saveHistory)
//#endif
//#         if (!m.history) {
//#             if (!cf.msgPath.equals("") && !jid.isTransport() && group.type!=Groups.TYPE_SEARCH_RESULT) {
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
//#                         HistoryAppend.getInstance().addMessage(m, bareJid);
//#                 }
//#             }
//#        }
//#endif
        if (last_replace) {
            msgs.setElementAt(new MessageItem(m, smiles), msgs.size() - 1);
            return;
        }

        if (cf.autoScroll)
            moveToLatest=true;
        
        if (m.messageType!=Msg.MESSAGE_TYPE_HISTORY && m.messageType!=Msg.MESSAGE_TYPE_PRESENCE)
            activeMessage=msgs.size();

        msgs.addElement(new MessageItem(m, smiles));
        
        if (m.unread || m.messageType == Msg.MESSAGE_TYPE_OUT) {
            lastUnread=msgs.size();
            if (m.messageType>unreadType) unreadType=m.messageType;
            if (newMsgCnt>=0) newMsgCnt++;
            if (m.highlite) if (newHighLitedMsgCnt>=0) newHighLitedMsgCnt++;
        }
        }

    public int getFontIndex(){
        if (cf.showResources) return (cf.useBoldFont && status<5)?1:0;

        return active()?1:0;
    }

    public final String getName(){ 
        return (nick==null)?bareJid:nick; 
    }

    public final String getJid() {
        return jid.getJid();
    }

    public String getResource() {
        return jid.getResource();
    }

    public String getNickJid() {
        if (nick==null) return bareJid;
        return nick+" <"+bareJid+">";
    }
    
    public final void purge() {
        msgs.removeAllElements();
        lastSendedMessage=null;
        activeMessage=-1; //drop activeMessage num
        
        resetNewMsgCnt();
        
        clearVCard();
    }
    
    public final void clearVCard() {
        try {
            if (vcard!=null) {
                vcard.clearVCard();
                vcard=null;
            }
        } catch (Exception e) { }
    }
    
//#ifdef LOGROTATE
//#     public final boolean deleteOldMessages() {
//#         int limit=cf.msglistLimit;
//#         if (msgs.size()<limit)
//#             return false;
//# 
//#         int trash = msgs.size()-limit;
//#         for (int i=0; i<trash; i++)
//#             msgs.removeElementAt(0);
//# 
//#         return true;
//#     }
//#endif
    
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

    public int getGroupType() {  
        if (group==null) return 0;
        return group.type;
    }
    
    public void setStatus(int status) {
        setIncoming(0);
        this.status = status;
        if (status>=Presence.PRESENCE_OFFLINE) 
            acceptComposing=false;
    }

    void markDelivered(String id) {
        if (id==null) return;
        for (Enumeration e=msgs.elements(); e.hasMoreElements();) {
            Msg m = ((MessageItem)e.nextElement()).msg;
            if (m.id!=null)
                if (m.id.equals(id)) 
                    m.delivered=true;
        }
    }
    
//#ifdef HISTORY
//#ifdef LAST_MESSAGES
//#     public boolean isHistoryLoaded () { return loaded; }
//# 
//#     public void setHistoryLoaded (boolean state) { loaded=state; }
//#endif
//#endif
    
    public int getVWidth(){
        String str=(!cf.rosterStatus)?getFirstString():(getFirstLength()>getSecondLength())?getFirstString():getSecondString();
        int wft=getFont().stringWidth(str);
        
        return wft+il.getWidth()+4;
    }
    
    public String toString() { return getFirstString(); }

    public int getSecondLength() {
        if (getSecondString()==null) return 0;
        if (getSecondString().length() == 0) return 0;
        return FontCache.getFont(false, FontCache.baloon).stringWidth(getSecondString());
    }

    public int getFirstLength() {
        if (getFirstString()==null) return 0;
        if (getFirstString().length() == 0) return 0;
        return getFont().stringWidth(getFirstString());
    }
    
    public String getFirstString() {
        if (!cf.showResources)
            return (nick==null)?jid.getBareJid():nick;
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
        if (getNewMsgsCount()>0)
            return (unreadType==Msg.MESSAGE_TYPE_AUTH)?RosterIcons.ICON_AUTHRQ_INDEX:RosterIcons.ICON_MESSAGE_INDEX;

        if (incomingState>0)
            return incomingState;
        
        return -1;
    }
    
//#ifdef PEP
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
        int itemVHeight = Math.max(maxImgHeight, fontHeight);
        if (getSecondString() != null)
            itemVHeight += FontCache.getFont(false, FontCache.baloon).getHeight() - 3;
        
        return Math.max(itemVHeight, cf.minItemHeight);
    }

    public void drawItem(Graphics g, int ofs, boolean sel) {
        int w=g.getClipWidth();
        int h=getVHeight();
        int xo=g.getClipX();
        int yo=g.getClipY();
        
        int offset= xo + 4;

        int imgH=(h-ilHeight) >> 1;
        
        if (getImageIndex()>-1) {
            offset+=ilHeight;
            il.drawImage(g, getImageIndex(), xo + 2, imgH);
        }
//#ifdef CLIENTS_ICONS
        if (hasClientIcon()) {
            ImageList clients=ClientsIcons.getInstance();
            int clientImgSize=clients.getWidth();
            w-=clientImgSize;
            clients.drawImage(g, client, w, (h-clientImgSize)/2);
            if (maxImgHeight<clientImgSize) maxImgHeight=clientImgSize;
        }
//#endif
//#ifdef PEP
//#         if (hasMood()) {
//#             ImageList moods=MoodIcons.getInstance();
//#             int moodImgSize=moods.getWidth();
//#             w-=moodImgSize;
//#             moods.drawImage(g, pepMood, w, (h-moodImgSize)/2);
//#             if (maxImgHeight<moodImgSize) maxImgHeight=moodImgSize;
//#         }
//#ifdef PEP_TUNE
//#         if (pepTune) {
//#             w-=ilHeight;
//#             il.drawImage(g, RosterIcons.ICON_PROFILE_INDEX+1, w,imgH);
//#         }
//#ifdef PEP_ACTIVITY
//#         if (hasActivity()) {
//#             w-=ilHeight;
//#             il.drawImage(g, RosterIcons.ICON_PROFILE_INDEX, w,imgH);
//#         }
//#endif
//#ifdef PEP_LOCATION
//#         if (hasLocation()) {
//#             w-=ilHeight;
//#             il.drawImage(g, RosterIcons.ICON_PROGRESS_INDEX, w,imgH);
//#         }
//#endif
//# 
//#endif
//#endif
/*         
        if (vcard!=null) {
            w-=ilHeight;
            il.drawImage(g, RosterIcons.ICON_SEARCH_INDEX, w,imgH);
        }
*/ 
//#ifdef FILE_TRANSFER
        if (fileQuery) {
            w-=ilHeight;
            il.drawImage(g, RosterIcons.ICON_PROGRESS_INDEX, w,imgH);
        }
//#endif
        if (getSecImageIndex()>-1) {
            w-=ilHeight;
            il.drawImage(g, getSecImageIndex(), w,imgH);
        }

        int thisOfs=0;
        
        g.setClip(offset, yo, w-offset, h);

        thisOfs=(getFirstLength()>w)?-ofs+offset:offset;
        if ((thisOfs+getFirstLength())<0) thisOfs=offset;
        g.setFont(getFont());

        int thisYOfs  = 0;
        if (getSecondString() == null) {
            thisYOfs = (h - getFont().getHeight()) >> 1;
        }
	FontCache.drawString(g, getFirstString(), thisOfs, thisYOfs, Graphics.TOP|Graphics.LEFT);

        if (getSecondString()!=null) {
            int y=getFont().getHeight()-3;
            thisOfs=(getSecondLength()>w)?-ofs+offset:offset;
            g.setFont(FontCache.getFont(false, FontCache.baloon));
            g.setColor(ColorTheme.getColor(ColorTheme.SECOND_LINE));
            FontCache.drawString(g, getSecondString(), thisOfs, y, Graphics.TOP|Graphics.LEFT);
        }
        g.setClip(xo, yo, w, h);        
    }
    
//#ifdef CLIENTS_ICONS
    boolean hasClientIcon() {
//#ifdef PLUGINS
//#                 if (!sd.ClientsIcons) return false;
//#endif
        return (client>-1);
    }
//#endif
    
//#ifdef PEP
//#     boolean hasMood() {
//#         return (pepMood>-1 && pepMood<Moods.getInstance().getCount());
//#     }
//#ifdef PEP_ACTIVITY
//#     boolean hasActivity() {
//#         if (activity!=null)
//#             if (activity.length()>0)
//#                 return true;
//#         return false;
//#     }
//#endif
//#ifdef PEP_LOCATION
//#     boolean hasLocation() {
//#         return (location != null);
//#     }
//#endif
//# 
//#endif    
}
