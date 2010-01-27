/*
 * Roster.java
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

import Account.Account;
import Account.AccountSelect;
import Alerts.AlertCustomize;
import Alerts.AlertProfile;
//#ifndef WMUC
import Conference.BookmarkQuery;
import Conference.Bookmarks;
import Conference.ConferenceGroup;
import Conference.MucContact;
import Conference.affiliation.ConferenceQuickPrivelegeModify;
import Conference.ConferenceForm;
//#endif
import Fonts.FontCache;
import Statistic.Stats;
import images.MenuIcons;

//#ifdef ARCHIVE
import Archive.ArchiveList;
//#endif
import Menu.RosterItemActions;
import Menu.RosterToolsMenu;
import Menu.SieNatMenu;
//#ifdef CLIENTS_ICONS
import images.ClientsIconsData;
//#endif
import images.RosterIcons;

//#ifndef MENU_LISTENER
//# import javax.microedition.lcdui.CommandListener;
//# import javax.microedition.lcdui.Command;
//#else
import Menu.MenuListener;
import Menu.Command;
import Menu.MyMenu;
//#endif

//#if FILE_TRANSFER
import io.file.transfer.TransferDispatcher;
//#endif

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;

import locale.SR;

import login.LoginListener;

//#ifdef NON_SASL_AUTH
//# import login.NonSASLAuth;
//#endif
//#if SASL_XGOOGLETOKEN
//# import login.GoogleTokenAuth;
//#endif
import login.SASLAuth;

import midlet.BombusMod;
import ui.controls.AlertBox;
import util.StringUtils;
import VCard.VCard;
import VCard.VCardEdit;
import VCard.VCardView;
import com.alsutton.jabber.*;
import com.alsutton.jabber.datablocks.*;
import java.util.*;
import ui.*;
import xmpp.EntityCaps;

import xmpp.XmppError;
//#ifdef CAPTCHA
//# import xmpp.extensions.Captcha;
//#endif
import xmpp.extensions.IqQueryRoster;
//#if SASL_XGOOGLETOKEN
//# import xmpp.extensions.IqGmail;
//#endif
import xmpp.extensions.IqLast;
import xmpp.extensions.IqPing;
import xmpp.extensions.IqVersionReply;
import xmpp.extensions.IqTimeReply;
//#ifdef ADHOC
//# import xmpp.extensions.IQCommands;
//#endif

//#ifdef PEP
//# import xmpp.extensions.PepListener;
//#endif

public class Roster
        extends VirtualList
        implements
        JabberListener,
//#ifndef MENU_LISTENER
//#         CommandListener,
//#else
        MenuListener,
//#endif
        Runnable,
        LoginListener
{

    private Command cmdActions;//=new Command(SR.MS_ITEM_ACTIONS, Command.SCREEN, 1);
    private Command cmdStatus=new Command(SR.MS_STATUS_MENU, Command.SCREEN, 4);
    private Command cmdActiveContacts=new Command(SR.MS_ACTIVE_CONTACTS, Command.SCREEN, 3);
    private Command cmdAlert=new Command(SR.MS_ALERT_PROFILE_CMD, Command.SCREEN, 8);
//#ifndef WMUC
    private Command cmdConference=new Command(SR.MS_CONFERENCE, Command.SCREEN, 10);
//#endif
//#ifdef ARCHIVE
    private Command cmdArchive=new Command(SR.MS_ARCHIVE, Command.SCREEN, 10);
//#endif
    private Command cmdAdd=new Command(SR.MS_ADD_CONTACT, Command.SCREEN, 12);
    private Command cmdTools=new Command(SR.MS_TOOLS, Command.SCREEN, 14);    
    private Command cmdAccount=new Command(SR.MS_ACCOUNT_, Command.SCREEN, 15);
    private Command cmdCleanAllMessages=new Command(SR.MS_CLEAN_ALL_MESSAGES, Command.SCREEN, 50);
    private Command cmdInfo=new Command(SR.MS_ABOUT, Command.SCREEN, 80);
    private Command cmdMinimize=new Command(SR.MS_APP_MINIMIZE, Command.SCREEN, 90);
    private Command cmdQuit=new Command(SR.MS_APP_QUIT, Command.SCREEN, 99);

    private Config cf=Config.getInstance();
    private StaticData sd=StaticData.getInstance();
    
    public Contact activeContact = null;
    
    private Jid myJid;

    public JabberStream theStream;
        
    public int messageCount;
    int highliteMessageCount;

    public Object transferIcon;
    
    public Vector hContacts;
    private Vector vContacts;
    private Vector paintVContacts;

    public Groups groups;
    
    public Vector bookmarks;
    
//#ifdef RUNNING_MESSAGE
//#     public static MessageEdit me=null;
//#endif
    
    private StatusList sl;
    public int myStatus=cf.loginstatus;
    private static String myMessage;
    public static int oldStatus=0;
    private static int lastOnlineStatus;
    
    public int currentReconnect=0;
    public boolean doReconnect=false;
    
    public boolean querysign=false;
    
//#ifdef AUTOSTATUS
//#     private AutoStatusTask autostatus;
//#     public static boolean autoAway=false;
//#     public static boolean autoXa=false;
//#endif
    
//#if SASL_XGOOGLETOKEN
//#     private String token;
//#endif
    
    public long lastMessageTime=Time.utcTimeMillis();

    public static String startTime=Time.dispLocalTime();
	
    private static long notifyReadyTime=System.currentTimeMillis();
    private static int blockNotifyEvent=-111;
    
    private int blState=Integer.MAX_VALUE;
        
    private final static int SOUND_FOR_ME=500;
    private final static int SOUND_FOR_CONFERENCE=800;
    private final static int SOUND_MESSAGE=1000;
    private final static int SOUND_CONNECTED=777;
    private final static int SOUND_FOR_VIP=100;
    private final static int SOUND_COMPOSING=888;
    private final static int SOUND_OUTGOING=999;
    
    SplashScreen splash;
    
    public Roster(Display display) {
        super();
        this.display=display;

        splash = SplashScreen.getInstance(display);
         
        sl=StatusList.getInstance();

        setLight(cf.lightState);
        
        MainBar mainbar=new MainBar(4, null, null, false);
        setMainBarItem(mainbar);
        mainbar.addRAlign();
        mainbar.addElement(null);
        mainbar.addElement(null);
        mainbar.addElement(null); //ft

        hContacts=null;
        hContacts=new Vector();
        
        groups=null;
        groups=new Groups();
        
        vContacts=null;
        vContacts=new Vector(); // just for displaying
        
	updateMainBar();

        commandState();
        setCommandListener(this);

        splash.setExit(display, this);
//#ifdef AUTOSTATUS
//#         if (cf.autoAwayType==Config.AWAY_IDLE || cf.autoAwayType==Config.AWAY_MESSAGE)
//#             autostatus=new AutoStatusTask();
//# 
//#         if (myStatus<2)
//#             messageActivity();
//#endif
//#ifdef CLIENTS_ICONS
        ClientsIconsData.getInstance();
//#endif
    }
    
    public void setLight(boolean state) {
        if (phoneManufacturer==Config.SIEMENS || phoneManufacturer==Config.SIEMENS2) {
            try {
                if (state) com.siemens.mp.game.Light.setLightOn();
                else com.siemens.mp.game.Light.setLightOff();  
            } catch( Exception e ) { }
            return;
        }
        if (!state) return;
//#ifdef SE_LIGHT
//#         if (phoneManufacturer==Config.SONYE || phoneManufacturer==Config.NOKIA) {
//#             new KeepLightTask().start();
//#          }
//#endif
    }
    
    public void commandState(){
//#ifdef MENU_LISTENER
        menuCommands.removeAllElements();
//#endif
        int activeType=Command.SCREEN;
        if (phoneManufacturer==Config.NOKIA) activeType=Command.BACK;
        if (phoneManufacturer==Config.INTENT) activeType=Command.BACK;
        if (phoneManufacturer==Config.J2ME) activeType=Command.BACK;

        cmdActions=new Command(SR.MS_ITEM_ACTIONS, activeType, 2);
        
//#ifndef MENU_LISTENER
//#         addCommand(cmdActions);
//#endif

        addCommand(cmdStatus);
        addCommand(cmdActiveContacts);
//#ifndef WMUC
//#ifdef MENU_LISTENER
        if (isLoggedIn())
//#endif
            addCommand(cmdConference);
//#endif
        addCommand(cmdAlert);
//#ifdef ARCHIVE
//#ifdef PLUGINS
//#          if (sd.Archive)
//#endif
            addCommand(cmdArchive);
//#endif
//#ifdef MENU_LISTENER
        if (isLoggedIn())
//#endif
            addCommand(cmdAdd);
        addCommand(cmdAccount);
        addCommand(cmdTools);
        addCommand(cmdInfo);

        if (cf.allowMinimize) 
            addCommand(cmdMinimize);

        addCommand(cmdCleanAllMessages);
        if (phoneManufacturer!=Config.NOKIA_9XXX)
            addCommand(cmdQuit);
        
//#ifdef MENU_LISTENER
        cmdActions.setImg(MenuIcons.ICON_ITEM_ACTIONS);
        cmdStatus.setImg(MenuIcons.ICON_STATUS);

        cmdActiveContacts.setImg(MenuIcons.ICON_CONFERENCE);
        cmdAlert.setImg(MenuIcons.ICON_NOTIFY);
//#ifndef WMUC
        cmdConference.setImg(MenuIcons.ICON_CONFERENCE);
//#endif
//#ifdef ARCHIVE
        cmdArchive.setImg(MenuIcons.ICON_ARCHIVE);
//#endif
        cmdAdd.setImg(MenuIcons.ICON_ADD_CONTACT);
        cmdTools.setImg(MenuIcons.ICON_SETTINGS);
        cmdAccount.setImg(MenuIcons.ICON_VCARD);
        cmdInfo.setImg(MenuIcons.ICON_CHECK_UPD);
        if (cf.allowMinimize)
            cmdMinimize.setImg(MenuIcons.ICON_FILEMAN);
        cmdCleanAllMessages.setImg(MenuIcons.ICON_CLEAN_MESSAGES);
        cmdQuit.setImg(MenuIcons.ICON_BUILD_NEW);
//#endif
    }
    
    public void setProgress(String pgs,int percent){
        if (splash!=null)
            splash.setProgress(pgs, percent);
        setRosterMainBar(pgs);
        redraw();
    }
    
    public void setProgress(int percent){
        if (splash!=null)
            splash.setProgress(percent);
    }
    
    private void setRosterMainBar(String s){
        getMainBarItem().setElementAt(s, 3);
    }
    
    private int rscaler;
    private int rpercent;
    
    public void rosterItemNotify(){
        rscaler++;
        if (rscaler<4) return;
        rscaler=0;
        if (rpercent<100) rpercent++;
        if (splash!=null)
            splash.setProgress(rpercent);
    }

    // establishing connection process
    public void run(){
//#ifdef POPUPS
        //if (cf.firstRun) setWobbler(1, (Contact) null, SR.MS_ENTER_SETTINGS);
//#endif
        setQuerySign(true);
	if (!doReconnect) {
            setProgress(25);
            resetRoster();
        }
        try {
            Account a=sd.account;
//#if SASL_XGOOGLETOKEN
//#             if (a.useGoogleToken()) {
//#                 setProgress(SR.MS_TOKEN, 30);
//#                 token=new GoogleTokenAuth(a).responseXGoogleToken();
//#                 if (token==null) throw new SecurityException("Can't get Google token");
//#             }
//#endif
            setProgress(SR.MS_CONNECT_TO_+a.getServer(), 30);
            
            theStream= a.openJabberStream();
            setProgress(SR.MS_OPENING_STREAM, 40);
            theStream.setJabberListener( this );
            theStream.initiateStream();
        } catch( Exception e ) {
//#ifdef DEBUG
//#             e.printStackTrace();
//#endif
            //SplashScreen.getInstance().close();
            setProgress(SR.MS_FAILED, 100);
            doReconnect=false;
            myStatus=Presence.PRESENCE_OFFLINE;
            setQuerySign(false);
            redraw();
            askReconnect(e);
        }
    }

    public void resetRoster() {
	synchronized (hContacts) {
            hContacts=null;
	    hContacts=new Vector();
            
            groups=null;
	    groups=new Groups();
            
            vContacts=null;
	    vContacts=new Vector(); // just for displaying
            
	    bookmarks=null;
	}
	setMyJid(new Jid(sd.account.getJid()));
	updateContact(sd.account.getNick(), myJid.getBareJid(), SR.MS_SELF_CONTACT, "self", false);
    }
    
    public void systemGC() {
        if (Config.getInstance().widthSystemgc) {
            System.gc();
            try { Thread.sleep(50); } catch (InterruptedException e){}
        }
    }
    
    public void errorLog(String s){
        if (s==null) return;
        
        Msg m=new Msg(Msg.MESSAGE_TYPE_OUT, "local", "Error", s);
        messageStore(selfContact(), m);
    }
    
    public void beginPaint() {
        paintVContacts=vContacts;
    }
    
    public VirtualElement getItemRef(int Index){
        return (VirtualElement)paintVContacts.elementAt(Index);
    }
    
    public int getItemCount(){
        return paintVContacts.size();
    }
    
    public void setEventIcon(Object icon){
        transferIcon=icon;
        getMainBarItem().setElementAt(icon, 7);
        redraw();
    }
    
    public Object getEventIcon() {
        if (transferIcon!=null) return transferIcon;
        return null;
    }
  
    private void updateMainBar(){
        int s=querysign?RosterIcons.ICON_PROGRESS_INDEX:myStatus;
        int profile=cf.profile;
        Object en=(profile>0)? new Integer(profile+RosterIcons.ICON_PROFILE_INDEX+1):null;
        MainBar mainbar=(MainBar) getMainBarItem();

        mainbar.setElementAt((messageCount==0)?null:new Integer(RosterIcons.ICON_MESSAGE_INDEX), 0);
        
        mainbar.setElementAt((messageCount==0)?null:getHeaderString(),1);
        mainbar.setElementAt(new Integer(s), 2);
        mainbar.setElementAt(en, 5);
        
        if (phoneManufacturer==Config.WINDOWS) {
            if (messageCount==0) {
                setTitle("BombusMod");
            } else {
                setTitle("BombusMod "+getHeaderString());
            }
        }
    }

    public String getHeaderString() {
        return ((highliteMessageCount==0)?" ":" "+highliteMessageCount+"/")+messageCount+" ";
    }
    
    boolean countNewMsgs() {
        int m=0;
        int h=0;
        synchronized (hContacts) {
            for (Enumeration e=hContacts.elements();e.hasMoreElements();){
                Contact c=(Contact)e.nextElement();
                m+=c.getNewMsgsCount();
                h+=c.getNewHighliteMsgsCount();
            }
        }
        highliteMessageCount=h;
        messageCount=m;

        updateMainBar();
        return (m>0);
    }
    
    public void cleanupSearch(){
        int index=0;
        synchronized (hContacts) {
            while (index<hContacts.size()) {
                if ( ((Contact) hContacts.elementAt(index)).getGroupType()==Groups.TYPE_SEARCH_RESULT )
                    hContacts.removeElementAt(index);
                else index++;
            }
        }
        reEnumRoster();
    }
    
    public void cmdCleanAllMessages(){
        if (messageCount>0) {
           new AlertBox(SR.MS_UNREAD_MESSAGES+": "+messageCount, SR.MS_SURE_DELETE, display, this) {
                public void yes() { cleanAllMessages(); }
                public void no() { }
            };
        } else {
            cleanAllMessages();
            cleanAllGroups();
        }
    }
    
    public void cleanAllMessages(){
        synchronized (hContacts) {
            for (Enumeration e=hContacts.elements();e.hasMoreElements();) {
                Contact c=(Contact)e.nextElement();
                try {
                    c.purge();
                } catch (Exception ex) { }
            }
        }
        highliteMessageCount=0;
        messageCount=0;
        cleanAllGroups();
        //reEnumRoster();
        redraw();
    }

    public void cleanAllGroups() {
        for (Enumeration e = groups.elements(); e.hasMoreElements();) {
            Group group = (Group)e.nextElement();
            cleanupGroup(group);
        }
        reEnumRoster();
    }
    
    public void cleanupGroup(Group g){
        /*Group g=(Group)getFocusedObject();*/
        if (g==null) return;
        //if (!g.collapsed) return;
//#ifndef WMUC
        if (g instanceof ConferenceGroup) {
            ConferenceGroup cg= (ConferenceGroup) g;

            if (!cg.inRoom) {
                int index=0;
                boolean removeGroup=true;
                synchronized (hContacts) {
                    while (index<hContacts.size()) {
                        Contact contact=(Contact)hContacts.elementAt(index);
                        if (contact.group==g) {
                            if (contact.getNewMsgsCount()==0) {
                                contact.msgs=null;
                                contact=null;
                                hContacts.removeElementAt(index);
                            } else {
                                removeGroup=false;
                                index++;
                            } 
                        } else index++; 
                    }
                    if (removeGroup) {
                        groups.removeGroup(g);
                    } else {
                        return;
                    }
                }
            }// else return;
        }
//#endif
        int index=0;

        synchronized (hContacts) {
            while (index<hContacts.size()) {
                Contact contact=(Contact)hContacts.elementAt(index);
                if (contact.group==g) {
                    if ( contact.origin>Contact.ORIGIN_ROSTERRES
                      && contact.status>=Presence.PRESENCE_OFFLINE
                      && contact.getNewMsgsCount()==0
                      && contact.origin!=Contact.ORIGIN_GROUPCHAT) {
                        
                        contact.msgs=null;
                        contact=null;
                        hContacts.removeElementAt(index);
                    } else {
                        index++;
                    } 
                }
                else index++; 
            }
            if (g.getOnlines()==0 && !(g instanceof ConferenceGroup)) {
                if (g.type==Groups.TYPE_MUC) groups.removeGroup(g);
            }
        }
    }
    
    ReEnumerator reEnumerator=null;
    
    public void reEnumRoster(){
        if (reEnumerator==null) reEnumerator=new ReEnumerator();
        reEnumerator.queueEnum();
        if (Config.getInstance().widthSystemgc) {
            System.gc(); // issue 87 workaround
        }
    }
    
    
    public Vector getHContacts() {return hContacts;}
    
    public void updateContact(String nick, String jid, String grpName, String subscr, boolean ask) {
        // called only on roster read
        int status=Presence.PRESENCE_OFFLINE;
        if (subscr.equals("none")) status=Presence.PRESENCE_UNKNOWN;
        if (ask) status=Presence.PRESENCE_ASK;
        if (subscr.equals("remove")) status=-1;
        
        Jid J=new Jid(jid);
        Contact c=findContact(J,false); // search by bare jid
        if (c==null) {
            c=new Contact(nick, jid, Presence.PRESENCE_OFFLINE, null);
            addContact(c);
        }
        
        boolean firstInstance=true; //FS#712 workaround
        int index=0;
        synchronized (hContacts) {
            for (Enumeration e=hContacts.elements();e.hasMoreElements();) {
                c=(Contact)e.nextElement();
                if (c.jid.equals(J,false)) {
                    Group group= (c.jid.isTransport())? 
                        groups.getGroup(Groups.TYPE_TRANSP) :
                        groups.getGroup(grpName);
                    if (group==null) {
                        group=groups.addGroup(grpName, Groups.TYPE_COMMON);
                    }
                    
                    if (status<0) {
                        hContacts.removeElementAt(index);
                        continue;
                    }
                    
                    c.nick=nick;
                    c.group=group;
                    c.subscr=subscr;
                    c.offline_type=status;
                    c.ask_subscribe=ask;

                    if (c.origin==Contact.ORIGIN_PRESENCE) {
                        if (firstInstance) c.origin=Contact.ORIGIN_ROSTERRES;
                        else c.origin=Contact.ORIGIN_CLONE;
                    }
                    firstInstance=false;

                    if (querysign==true) {
                        if (cf.collapsedGroups) {
                            Group g=c.group;
                            g.collapsed=true; 
                        }
                    }

                    c.setSortKey((nick==null)? jid:nick);
                }
                index++;
            }
        }
        //if (status<0) removeTrash();
    }
    
    private final void removeTrash(){
        int index=0;
        synchronized (hContacts) {
            while (index<hContacts.size()) {
                Contact c=(Contact)hContacts.elementAt(index);
                if (c.offline_type<0) {
                    hContacts.removeElementAt(index);
                } else index++;
            }
            countNewMsgs();
        }
    }
//#ifndef WMUC
    public MucContact findMucContact(Jid jid) {
        Contact contact=findContact(jid, true);
        try {
            return (MucContact) contact;
        } catch (Exception e) {
            // drop buggy bookmark in roster
            synchronized (hContacts) {
                hContacts.removeElement(contact);
            }
            return null;
        }
    }
    
    public final ConferenceGroup initMuc(String from, String joinPassword){
//#ifdef AUTOSTATUS
//#         if (autoAway) {
//#             ExtendedStatus es=sl.getStatus(oldStatus);
//#             String ms=es.getMessage();
//#             sendPresence(oldStatus, ms);
//#             autoAway=false;
//#             autoXa=false;
//#             myStatus=oldStatus;
//# 
//#             messageActivity();
//#         }
//#endif

        // muc message
        int ri=from.indexOf('@');
        int rp=from.indexOf('/');
        String room=from.substring(0,ri);
        String roomJid=from.substring(0,rp).toLowerCase();

        ConferenceGroup grp=(ConferenceGroup)groups.getGroup(roomJid);

        // creating room
        if (grp==null) // we hasn't joined this room yet
            groups.addGroup(grp=new ConferenceGroup(roomJid, room) );
        grp.password=joinPassword;
        
        MucContact c=findMucContact( new Jid(roomJid) );
        
        if (c==null) {
            c=new MucContact(room, roomJid);
            addContact(c);
        }
		        
        // change nick if already in room
        if (c.status==Presence.PRESENCE_ONLINE) return grp;

        c.setStatus(Presence.PRESENCE_ONLINE);
        
        c.transport=RosterIcons.ICON_GROUPCHAT_INDEX; //FIXME: убрать хардкод
        c.bareJid=from;
        c.origin=Contact.ORIGIN_GROUPCHAT;
        c.commonPresence=true;

        grp.conferenceJoinTime=Time.utcTimeMillis();
        grp.confContact=c;
        c.group=grp;
        
        String nick=from.substring(rp+1);

        // old self-contact
        c=grp.selfContact;
        
        // check for existing entry - it may be our old self-contact
        // or another contact whose nick we pretend
        MucContact foundInRoom = findMucContact( new Jid(from) );
        if (foundInRoom!=null) {
            c=foundInRoom;            //choose found contact instead of old self-contact
        }
 
        // if exists (and online - rudimentary check due to line 500)
        // rename contact
        if (c!=null) if (c.status>=Presence.PRESENCE_OFFLINE) {
            c.nick=nick;
            c.jid.setJid(from);
            c.bareJid=from;
        }
        
        // create self-contact if no any candidates found
        if (c==null) {
            c=new MucContact(nick, from);
            addContact(c);
        }

        grp.selfContact=c;
        c.group=grp;
        c.origin=Contact.ORIGIN_GC_MYSELF;
        
        grp.collapsed=true; //test
               
        sort(hContacts);
        return grp;
    }
    
    public final MucContact mucContact(String from){
        // muc message
        int ri=from.indexOf('@');
        int rp=from.indexOf('/');
        String room=from.substring(0,ri);
        String roomJid=from.substring(0,rp).toLowerCase();

        ConferenceGroup grp=(ConferenceGroup)groups.getGroup(roomJid);

        if (grp==null) return null; // we are not joined this room
        
        MucContact c=findMucContact( new Jid(from) );
        
        if (c==null) {
            c=new MucContact(from.substring(rp+1), from);
            addContact(c);
            c.origin=Contact.ORIGIN_GC_MEMBER;
        }
        
        c.group=grp;
        sort(hContacts);
        return c;
    }
//#endif
    public final Contact getContact(final String jid, boolean createInNIL) {
        Jid J=new Jid(jid);

        Contact c=findContact(J, true); 
        if (c!=null) 
            return c;

        c=findContact(J, false);
        if (c==null) {
            if (!createInNIL) return null;
            c=new Contact(null, jid, Presence.PRESENCE_OFFLINE, "none" ); /*"not-in-list"*/
	    c.bareJid=J.getBareJid();
            c.origin=Contact.ORIGIN_PRESENCE;
            c.group=groups.getGroup(Groups.TYPE_NOT_IN_LIST);
            addContact(c);
        } else {
            if (c.origin==Contact.ORIGIN_ROSTER) {
                c.origin=Contact.ORIGIN_ROSTERRES;
                c.setStatus(Presence.PRESENCE_OFFLINE);
                c.jid=J;
                //System.out.println("add resource");
            } else {
                c=c.clone(J, Presence.PRESENCE_OFFLINE);
                addContact(c);
                //System.out.println("cloned");
            }
        }
        sort(hContacts);
        return c;
    }
    
    public void addContact(Contact c) {
        synchronized (hContacts) { hContacts.addElement(c); }
    }

    public final Contact findContact(final Jid j, final boolean compareResources) {
        synchronized (hContacts) {
            for (Enumeration e=hContacts.elements();e.hasMoreElements();){
                Contact c=(Contact)e.nextElement();
                if (c.jid.equals(j,compareResources)) return c;
            }
        }
        return null;
    }

//#ifdef JUICK
//#         public Vector getJuickContacts() {
//#         Vector juickContacts = new Vector();
//#         synchronized (hContacts) {
//#             for (Enumeration e = hContacts.elements(); e.hasMoreElements();) {
//#                 Contact c = (Contact) e.nextElement();
//#                 if (isJuickContact(c))
//#                     juickContacts.addElement(c.bareJid);
//#             }
//#         }
//#         return juickContacts;
//#     }
//# 
//#     public Contact getMainJuickContact() {
//#         boolean matched;
//#         synchronized (hContacts) {
//#             for (Enumeration e = hContacts.elements(); e.hasMoreElements();) {
//#                 Contact c = (Contact) e.nextElement();
//#                 if (cf.juickJID != null) {
//#                     matched = c.bareJid.equals(cf.juickJID);
//#                 } else {
//#                     matched = isJuickContact(c);
//#                 }
//#                 if (matched)
//#                     return c;
//#             }
//#         }
//#         return null;
//#     }
//# 
//#     public boolean isJuickContact(Contact c) {
//#         return (c.bareJid.equals("juick@juick.com")
//#          || c.bareJid.startsWith("juick%juick.com@"));
//#     }
//#endif

    public void sendPresence(int newStatus, String message) {
        if (newStatus!=Presence.PRESENCE_SAME) 
            myStatus=newStatus;
//#ifdef AUTOSTATUS
//#         messageActivity();
//#endif
	if (message!=null) 
            myMessage=message;
         
        setQuerySign(false);
	
        if (myStatus!=Presence.PRESENCE_OFFLINE) {
             lastOnlineStatus=myStatus;
        }
        
        // reconnect if disconnected
        if (myStatus!=Presence.PRESENCE_OFFLINE && theStream==null ) {
            synchronized (hContacts) {
                doReconnect=(hContacts.size()>1);
            }
            redraw();
            new Thread(this).start();
            return;
        }
        
        blockNotify(-111,13000);

        if (isLoggedIn()) {
            if (myStatus==Presence.PRESENCE_OFFLINE  && !cf.collapsedGroups)
                groups.queryGroupState(false);
            
            // send presence
            ExtendedStatus es= sl.getStatus(myStatus);
            if (message==null)
                myMessage=StringUtils.toExtendedString(es.getMessage());;

            myMessage=StringUtils.toExtendedString(myMessage);
            int myPriority=es.getPriority();

            Presence presence = new Presence(myStatus, myPriority, myMessage, sd.account.getNick());
            
            if (!sd.account.isMucOnly() )
		theStream.send( presence );
//#ifndef WMUC
            multicastConferencePresence(myStatus, myMessage, myPriority);
//#endif
        }
        
        // disconnect
        if (myStatus==Presence.PRESENCE_OFFLINE) {
            try {
                theStream.close(); // sends </stream:stream> and closes socket
            } catch (Exception e) { /*e.printStackTrace();*/ }

            synchronized(hContacts) {
                for (Enumeration e=hContacts.elements(); e.hasMoreElements();){
                    ((Contact)e.nextElement()).setStatus(Presence.PRESENCE_OFFLINE); // keep error & unknown
                 }
            }
            theStream=null;
//#ifdef AUTOSTATUS
//#             autoAway=false;
//#             autoXa=false;
//#endif
            systemGC();
        }
        Contact c=selfContact();
        c.setStatus(myStatus);
        sort(hContacts);
        
        reEnumRoster();
    }
    
    public void sendDirectPresence(int status, String to, JabberDataBlock x) {
        if (to==null) { 
            sendPresence(status, null);
            return;
        }

        ExtendedStatus es= sl.getStatus(status);
        myMessage=es.getMessage();
 
        myMessage=StringUtils.toExtendedString(myMessage);

        Presence presence = new Presence(status, es.getPriority(), myMessage, sd.account.getNick());
        
        presence.setTo(to);
        
        if (x!=null) presence.addChild(x);

        if (theStream!=null) {
            theStream.send( presence );
        }
    }
    
    public void sendDirectPresence(int status, Contact to, JabberDataBlock x) {
        sendDirectPresence(status, (to==null)? null: to.getJid(), x);
        if (to.jid.isTransport()) blockNotify(-111,10000);
//#ifndef WMUC
        if (to instanceof MucContact) ((MucContact)to).commonPresence=false;
//#endif
    }
	
    public boolean isLoggedIn() {
        if (theStream==null) return false;
        return theStream.loggedIn;
    }

    public Contact selfContact() {
	return getContact(myJid.getJid(), false);
    }

//#ifndef WMUC
    public void multicastConferencePresence(int myStatus, String myMessage, int myPriority) {
         //if (!cf.autoJoinConferences) return; //requested to disable
         if (myStatus==Presence.PRESENCE_INVISIBLE) return; //block multicasting presence invisible
         
         synchronized (hContacts) {
             for (Enumeration e=hContacts.elements(); e.hasMoreElements();) {
                Contact c=(Contact) e.nextElement();
                if (c.origin!=Contact.ORIGIN_GROUPCHAT) continue;
                if (!((MucContact)c).commonPresence) continue; // stop if room left manually
                ConferenceGroup confGroup=(ConferenceGroup)c.group;

                if (!confGroup.inRoom) continue; // don`t reenter to leaved rooms

                Contact myself=confGroup.selfContact;

                if (c.status==Presence.PRESENCE_OFFLINE){
                    ConferenceForm.join(confGroup.desc, myself.getJid(), confGroup.password, 20);
                    continue;
                }
                Presence presence = new Presence(myStatus, myPriority, myMessage, null);
                presence.setTo(myself.bareJid);
                theStream.send(presence);
             }
         }
    }
//#endif
    public void sendPresence(String to, String type, JabberDataBlock child, boolean conference) { //voffk: todo: check it!
        JabberDataBlock presence=new Presence(to, type);
       
        if (child!=null) {
            presence.addChild(child);

            ExtendedStatus es= sl.getStatus(myStatus);
            switch (myStatus){
                case Presence.PRESENCE_CHAT: presence.addChild("show", Presence.PRS_CHAT);break;
                case Presence.PRESENCE_AWAY: presence.addChild("show", Presence.PRS_AWAY);break;
                case Presence.PRESENCE_XA: presence.addChild("show", Presence.PRS_XA);break;
                case Presence.PRESENCE_DND: presence.addChild("show", Presence.PRS_DND);break;
            }
            if (es.getPriority()!=0) 
                presence.addChild("priority",Integer.toString(es.getPriority()));
            if (es.getMessage()!=null) 
                presence.addChild("status", StringUtils.toExtendedString(es.getMessage()));
        } else if (conference) {
            ExtendedStatus es= sl.getStatus(Presence.PRESENCE_OFFLINE);            
            if (es.getMessage()!=null) 
                presence.addChild("status", StringUtils.toExtendedString(es.getMessage()));
        }
        
        theStream.send(presence);
    }
    
    public void doSubscribe(Contact c) {
        if (c.subscr==null) return;
        boolean subscribe = 
                c.subscr.startsWith("none") || 
                c.subscr.startsWith("from");
        if (c.ask_subscribe) subscribe=false;

        boolean subscribed = 
                c.subscr.startsWith("none") || 
                c.subscr.startsWith("to");
                //getMessage(cursor).messageType==Msg.MESSAGE_TYPE_AUTH;
        
        String to=(c.jid.isTransport())?c.getJid():c.bareJid;
        
        if (subscribed) sendPresence(to,"subscribed", null, false);
        if (subscribe) sendPresence(to,"subscribe", null, false);
    }
    
    public void sendMessage(Contact to, String id, final String body, final String subject , String composingState) {
        try {
//#ifndef WMUC
            boolean groupchat=to.origin==Contact.ORIGIN_GROUPCHAT;
//#else 
//#           boolean groupchat=false;  
//#endif
            
//#ifdef AUTOSTATUS
//#             if (autoAway) {
//#                     ExtendedStatus es=sl.getStatus(oldStatus);
//#                     String ms=es.getMessage();
//#                     sendPresence(oldStatus, ms);
//#                     autoAway=false;
//#                     autoXa=false;
//#                     myStatus=oldStatus;
//#             }
//#endif
            Message message = new Message( 
                    to.getJid(), 
                    body, 
                    subject, 
                    groupchat 
            );
            message.setAttribute("id", id);
            if (groupchat && body==null && subject==null) return;

            if (composingState!=null) 
                message.addChildNs(composingState, "http://jabber.org/protocol/chatstates");


            if (!groupchat) 
                if (body!=null) if (cf.eventDelivery) 
                    message.addChildNs("request", "urn:xmpp:receipts");

            theStream.send( message );
            lastMessageTime=Time.utcTimeMillis();
            playNotify(SOUND_OUTGOING);
        } catch (Exception e) { e.printStackTrace(); }
//#ifdef AUTOSTATUS
//#         messageActivity();
//#endif
    }
    
    private void sendDeliveryMessage(Contact c, String id) {
        if (!cf.eventDelivery) return;
        if (myStatus==Presence.PRESENCE_INVISIBLE) return;
        Message message=new Message(c.jid.getJid());

        //xep-0184
        message.setAttribute("id", id);
        message.addChildNs("received", "urn:xmpp:receipts");
        theStream.send( message );
    }    

    private Vector vCardQueue;
    
    public void resolveNicknames(String transport){
        vCardQueue=null;
	vCardQueue=new Vector();
        synchronized (hContacts) {
            for (Enumeration e=hContacts.elements(); e.hasMoreElements();){
                Contact k=(Contact) e.nextElement();
                if (k.jid.isTransport()) 
                    continue;
                int grpType=k.getGroupType();
                if (k.jid.getServer().equals(transport) && k.nick==null && (grpType==Groups.TYPE_COMMON || grpType==Groups.TYPE_NO_GROUP))
                    vCardQueue.addElement(VCard.getQueryVCard(k.getJid(), "nickvc"+k.bareJid));
            }
        }
	setQuerySign(true);
	sendVCardReq();
	
    }
        
    private void sendVCardReq(){
        querysign=false;
        if (vCardQueue!=null) {
            if (!vCardQueue.isEmpty()) {
                JabberDataBlock req=(JabberDataBlock) vCardQueue.lastElement();
                vCardQueue.removeElement(req);
                //System.out.println(k.nick);
                theStream.send(req);
                querysign=true;
            }
        }
        updateMainBar();
    }
    
//#if CHANGE_TRANSPORT
//#     public void contactChangeTransport(String srcTransport, String dstTransport){ //voffk
//# 	setQuerySign(true);
//#         for (Enumeration e=hContacts.elements(); e.hasMoreElements(); ) {
//# 	    Contact k=(Contact) e.nextElement();
//# 	    if (k.jid.isTransport()) continue;
//#             int grpType=k.getGroupType();
//#             if (k.jid.getServer().equals(srcTransport) &&
//#                     (grpType==Groups.TYPE_COMMON || grpType==Groups.TYPE_NO_GROUP || 
//#                     grpType==Groups.TYPE_VISIBLE || grpType==Groups.TYPE_VIP || 
//#                     grpType==Groups.TYPE_IGNORE)) {
//#                 String jid=k.getJid();
//#                 jid=StringUtils.stringReplace(jid, srcTransport, dstTransport);
//#                 storeContact(jid, k.nick, (!k.group.getName().equals(SR.MS_GENERAL))?(k.group.getName()):"", true); //new contact addition
//#                 try {
//#                     Thread.sleep(300);
//#                 } catch (Exception ex) { }
//#                 deleteContact(k); //old contact deletion
//# 	    }
//# 	}
//# 	setQuerySign(false);
//#     }
//#endif
    
    public void loginFailed(String error){
        myStatus=Presence.PRESENCE_OFFLINE;
        setProgress(SR.MS_LOGIN_FAILED, 100);
        
        errorLog(error);
		
        try {
            theStream.close();
        } catch (Exception e) {
            //e.printStackTrace();
        }
        theStream=null;
        
        systemGC();
        
        doReconnect=false;
        setQuerySign(false);
        redraw();
    }
    
    public void loginSuccess() {
        theStream.addBlockListener(new EntityCaps());

        theStream.addBlockListener(new IqPing());
        theStream.addBlockListener(new IqVersionReply());
        theStream.startKeepAliveTask(); //enable keep-alive packets
        
	theStream.loggedIn=true;
	currentReconnect=0;
        
        theStream.addBlockListener(new IqLast());
        theStream.addBlockListener(new IqTimeReply());
//#ifdef ADHOC
//#         if (cf.adhoc)
//#ifdef PLUGINS
//#             if (sd.Adhoc)
//#endif
//#                 IQCommands.getInstance().addBlockListener();
//#endif

//#ifdef PEP
//#         if (cf.sndrcvmood)
//#ifdef PLUGINS
//#             if (sd.PEP)
//#endif
//#                 PepListener.getInstance().addBlockListener();
//#endif
//#if SASL_XGOOGLETOKEN
//#         if (StaticData.getInstance().account.isGmail())
//#             theStream.addBlockListener(new IqGmail());
//#endif
//#if FILE_TRANSFER
        if (cf.fileTransfer) // enable File transfers
//#ifdef PLUGINS
//#             if (sd.FileTransfer)
//#endif
                TransferDispatcher.getInstance().addBlockListener();
//#endif
     
//#ifdef CAPTCHA
//#         theStream.addBlockListener(new Captcha(display));
//#endif
        
        playNotify(SOUND_CONNECTED);
        if (doReconnect) {
            querysign=doReconnect=false;
            sendPresence(myStatus, null);
            return;
        }
        //
        //theStream.enableRosterNotify(true); //voffk
        rpercent=50;

        if (sd.account.isMucOnly()) {
            setProgress(SR.MS_CONNECTED,100);
            try {
                reEnumRoster();
            } catch (Exception e) { }
			
            setQuerySign(false);
            doReconnect=false;
            if (splash!=null)
                splash.close(); // display.setCurrent(this);
            splash=null;
            
            //query bookmarks
            theStream.addBlockListener(new BookmarkQuery(BookmarkQuery.LOAD));
        } else {
            JabberDataBlock qr=new IqQueryRoster();
            setProgress(SR.MS_ROSTER_REQUEST, 49);
            theStream.send( qr );
        }
//#ifndef WMUC
        //query bookmarks
        if (bookmarks==null)
            theStream.addBlockListener(new BookmarkQuery(BookmarkQuery.LOAD));
//#endif
    }

    public void bindResource(String myJid) {
        Contact self=selfContact();
        self.jid=this.myJid=new Jid(myJid);
    }

    public int blockArrived( JabberDataBlock data ) {
        try {
            if( data instanceof Iq ) {
                String from=data.getAttribute("from");
                String type = (String) data.getTypeAttribute();
                String id=(String) data.getAttribute("id");
                
                if (id!=null) {
                    if (id.startsWith("nickvc")) {
                        if (type.equals("get") || type.equals("set")) return JabberBlockListener.BLOCK_REJECTED;
                        
                        VCard vc=new VCard(data);//.getNickName();
                        String nick=vc.getNickName();
                        
                        Contact c=findContact(new Jid(from), false);
                        
                        String group=(c.getGroupType()==Groups.TYPE_NO_GROUP)? null: c.group.name;
                        if (nick!=null)  storeContact(from,nick,group, false);
                        //updateContact( nick, c.rosterJid, group, c.subscr, c.ask_subscribe);
                        sendVCardReq();
                        return JabberBlockListener.BLOCK_PROCESSED;
                    }
                    
                     if (id.startsWith("getvc")) {
                        if (type.equals("get") || type.equals("set") || type.equals("error") ) return JabberBlockListener.BLOCK_REJECTED;
                        
                        setQuerySign(false);
                        VCard vcard=new VCard(data);
                        String jid=id.substring(5);
                        Contact c=getContact(jid, false); // drop unwanted vcards
                        if (c!=null) {
                            c.vcard=vcard;
                            if (display.getCurrent() instanceof VirtualList) {
                                if (c.getGroupType()==Groups.TYPE_SELF) {
                                    new VCardEdit(display, this, vcard);
                                } else {
                                    new VCardView(display, this, c);
                                }
                            }
                        } else {
                            new VCardView(display, this, c);
                        }
                        return JabberBlockListener.BLOCK_PROCESSED;
                    }
                    
                } // id!=null
                if ( type.equals( "result" ) ) {
                    if (id.equals("getros")){
                        //theStream.enableRosterNotify(false); //voffk

                        processRoster(data);

                        if(!cf.collapsedGroups)
                            groups.queryGroupState(true);

                        setProgress(SR.MS_CONNECTED,100);
                        reEnumRoster();

                        querysign=doReconnect=false;

                        if (cf.loginstatus==5) {
                            sendPresence(Presence.PRESENCE_INVISIBLE, null);    
                        } else {
                            sendPresence(cf.loginstatus, null);
                        }

                        if (splash!=null)
                            splash.close();
                        splash=null;

                        return JabberBlockListener.BLOCK_PROCESSED;
                    }
                } else if (type.equals("set")) {
                    if (processRoster(data)) { 
                        theStream.send(new Iq(from, Iq.TYPE_RESULT, id));
                        reEnumRoster();
                        return JabberBlockListener.BLOCK_PROCESSED;
                    }
                }
            } else if( data instanceof Message ) { // If we've received a message
                //System.out.println(data.toString());
                querysign=false;
                Message message = (Message) data;
                
                String from=message.getFrom();
                
                if (myJid.equals(new Jid(from), false)) //Enable forwarding only from self-jids
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
                
                Contact c=getContact(from, (cf.notInListDropLevel != NotInListFilter.DROP_MESSAGES_PRESENCES || groupchat));
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
                    if (xmlns!=null) {
                        JabberDataBlock invite=xmlns.getChildBlock("invite");
                        if (invite!=null) {
                            if (message.getTypeAttribute().equals("error")) {
                                ConferenceGroup invConf=(ConferenceGroup)groups.getGroup(from);
                                body=XmppError.decodeStanzaError(message).toString(); /*"error: invites are forbidden"*/
                            } else {
                                String inviteReason=invite.getChildBlockText("reason");
                                String room=from+'/'+sd.account.getNickName();
                                
                                ConferenceGroup invConf=initMuc(room, xmlns.getChildBlockText("password"));
                                
                                invConf.confContact.commonPresence=false; //FS#761
                                
                                if (invConf.selfContact.status==Presence.PRESENCE_OFFLINE)
                                    invConf.confContact.status=Presence.PRESENCE_OFFLINE;

                                if (inviteReason!=null)
                                    inviteReason=(inviteReason.length()>0)?" ("+inviteReason+")":"";
                                
                                body=invite.getAttribute("from")+SR.MS_IS_INVITING_YOU+from+inviteReason;
                                
                                reEnumRoster();
                            }
                        }
                    }
                } catch (Exception e) { /*e.printStackTrace();*/ }
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
                if (type.equals("chat") && myStatus!=Presence.PRESENCE_INVISIBLE) {
                    if (message.findNamespace("request", "urn:xmpp:receipts")!=null) {
                        sendDeliveryMessage(c, data.getAttribute("id"));
                    }
                    if (message.findNamespace("received", "urn:xmpp:receipts")!=null) {
                         c.markDelivered(data.getAttribute("id"));
                    }
                    if (message.findNamespace("active", "http://jabber.org/protocol/chatstates")!=null) {
                        c.acceptComposing=true;
                        c.showComposing=false;
//#ifdef RUNNING_MESSAGE
//#                         setTicker(c, "");
//#endif
                    }
                    if (message.findNamespace("paused", "http://jabber.org/protocol/chatstates")!=null) {
                        c.acceptComposing=true;
                        c.showComposing=false;
//#ifdef RUNNING_MESSAGE
//#                         setTicker(c, "");
//#endif
                    }
                    if (message.findNamespace("composing", "http://jabber.org/protocol/chatstates")!=null) {
                        playNotify(SOUND_COMPOSING);
                        c.acceptComposing=true;
                        c.showComposing=true;
//#ifdef RUNNING_MESSAGE
//#                         setTicker(c, SR.MS_COMPOSING_NOTIFY);
//#endif
                    }
                }
                redraw();

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
                        m.messageType=Msg.MESSAGE_TYPE_OUT;
                        m.unread=false;
                    } else {
                        if (m.dateGmt<= ((ConferenceGroup)c.group).conferenceJoinTime)
                            m.messageType=Msg.MESSAGE_TYPE_HISTORY;
                        // highliting messages with myNick substring
	                String myNick=mucGrp.selfContact.getName();
                        String myNick_=myNick+" ";
                        String _myNick=" "+myNick;
			if (body.indexOf(myNick)>-1) {
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
			}
	                myNick=null; myNick_=null; _myNick=null;
                        //TODO: custom highliting dictionary
                    }
                    m.from=name;
                }
//#endif
                m.highlite=highlite;
                messageStore(c, m);
                return JabberBlockListener.BLOCK_PROCESSED;   
            } else if( data instanceof Presence ) {  // If we've received a presence
                //System.out.println("presence");
                if (myStatus==Presence.PRESENCE_OFFLINE) 
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
                        MucContact c = mucContact(from);
                        
                        if (pr.getAttribute("ver")!=null) c.version=pr.getAttribute("ver"); // for bombusmod only
//#ifdef CLIENTS_ICONS
//#ifdef PLUGINS
//#                     if (sd.ClientsIcons)
//#endif
                        if (cf.showClientIcon) {
                            if (pr.hasEntityCaps()) {
                                if (pr.getEntityNode()!=null)
                                    getClientIcon(c, pr.getEntityNode());
                                if (pr.getEntityVer()!=null)
                                    c.version=pr.getEntityVer();
                            }
                        }

//#endif
                        String lang=pr.getAttribute("xml:lang");

                        if (lang!=null) c.lang=lang;
                        lang=null;

                        c.statusString=pr.getStatus();
                        
                        String chatPres=c.processPresence(xmuc, pr);
                        
                        if (cf.storeConfPresence || chatPres.indexOf(SR.MS_WAS_BANNED)>-1 || chatPres.indexOf(SR.MS_WAS_KICKED)>-1) {
                            int rp=from.indexOf('/');

                            String name=from.substring(rp+1);

                            Msg chatPresence=new Msg(Msg.MESSAGE_TYPE_PRESENCE, name, null, chatPres );
                            chatPresence.color=c.getMainColor();
                            messageStore(getContact(from.substring(0, rp), false), chatPresence);
                            name=null;
                        }
                        
                        chatPres=null;
                                
                        messageStore(c,m);

                        c.priority=pr.getPriority();
                        //System.gc();
                        //Thread.sleep(20);
                    } catch (Exception e) { }
                } else {
//#endif
                    Contact c=null;

                     if (ti==Presence.PRESENCE_AUTH_ASK) {
                        //processing subscriptions
                        if (cf.autoSubscribe==Config.SUBSCR_DROP)
                            return JabberBlockListener.BLOCK_REJECTED;
                        
                        if (cf.autoSubscribe==Config.SUBSCR_REJECT) {
//#if DEBUG 
//#                             System.out.print(from);
//#                             System.out.println(": decline subscription");
//#endif
                            sendPresence(from, "unsubscribed", null, false);
                            return JabberBlockListener.BLOCK_PROCESSED;
                        }
                        
                        c=getContact(from, true);
                        
                        messageStore(c, m);

                        if (cf.autoSubscribe==Config.SUBSCR_AUTO) {
                             doSubscribe(c);
                             messageStore(c, new Msg(Msg.MESSAGE_TYPE_AUTH, from, null, SR.MS_AUTH_AUTO));
                         }
                    } else {
                        // processing presences
                        boolean enNIL= cf.notInListDropLevel > NotInListFilter.DROP_PRESENCES;
                        c=getContact(from, enNIL);
                        
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
                      
                        messageStore(c, m);
                     }
                    
                    c.priority=pr.getPriority();
                    if (ti>=0) 
                        c.setStatus(ti);
                    
                    if (c.nick==null && c.status<=Presence.PRESENCE_DND) {
                        JabberDataBlock nick = pr.findNamespace("nick", "http://jabber.org/protocol/nick");
                        if (nick!=null) c.nick=nick.getText();
                        
                    }

                    if ((ti==Presence.PRESENCE_ONLINE || ti==Presence.PRESENCE_CHAT) && notifyReady(-111)) {
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
//#                             setTicker(c, SR.MS_OFFLINE);
//#                         else if (ti==Presence.PRESENCE_ONLINE)
//#                             setTicker(c, SR.MS_ONLINE);
//#endif
                        if ((ti==Presence.PRESENCE_ONLINE || ti==Presence.PRESENCE_CHAT || ti==Presence.PRESENCE_OFFLINE) && (c.getGroupType()!=Groups.TYPE_TRANSP) && (c.getGroupType()!=Groups.TYPE_IGNORE)) 
                            playNotify(ti);
                    }

//#ifndef WMUC
                }
//#endif
                if (cf.autoClean) cleanAllGroups();
		sort(hContacts);
                reEnumRoster();
                return JabberBlockListener.BLOCK_PROCESSED;                
            } // if presence
        } catch(OutOfMemoryError eom){
            System.out.println("error bombusmod\\src\\Client\\Roster.java:12");
        } catch( Exception e ) {
//#if DEBUG
//#             e.printStackTrace();
//#endif
        }
        return JabberBlockListener.BLOCK_REJECTED;
    }
//#ifdef CLIENTS_ICONS
    private void getClientIcon(Contact c, String data) {
        ClientsIconsData.processData(c, data);
    }
//#endif

    boolean  processRoster(JabberDataBlock data){
        JabberDataBlock q=data.findNamespace("query", "jabber:iq:roster");
        if (q==null) return false;
        int type=0;
		
        //verifying from attribute as in RFC3921/7.2
        String from=data.getAttribute("from");
        if (from!=null) {
            Jid fromJid=new Jid(from);
            if (fromJid.hasResource())
                if (!myJid.equals(fromJid, true)) return false;
         }
        
        Vector cont=(q!=null)?q.getChildBlocks():null;
        
        if (cont!=null)
            for (Enumeration e=cont.elements(); e.hasMoreElements();){
                JabberDataBlock i=(JabberDataBlock)e.nextElement();
                if (i.getTagName().equals("item")) {
                    String name=i.getAttribute("name");
                    String jid=i.getAttribute("jid");
                    String subscr=i.getAttribute("subscription");
                    boolean ask= (i.getAttribute("ask")!=null);

                    String group=i.getChildBlockText("group");
                    if (group.length()==0) group=Groups.COMMON_GROUP;

                    updateContact(name,jid,group, subscr, ask);
                    //sort(hContacts);
                }
            }
	sort(hContacts);
        return true;
    }

//#ifdef POPUPS
    boolean showWobbler(Contact c) {
        if (!cf.popUps)
            return false;
        if (activeContact==null)
            return true;
        return(!c.equals(activeContact));
    }
//#endif
    
//#ifdef FILE_TRANSFER
    public void addFileQuery(String from, String message) {
        Contact c=getContact(from, true);
        c.fileQuery=true;
        messageStore(c, new Msg(Msg.MESSAGE_TYPE_SYSTEM, from, SR.MS_FILE, message));
    }
//#endif
    
    public void messageStore(Contact c, Msg message) {
        if (c==null) return;

        c.addMessage(message);

        boolean autorespond = false;
//#ifdef RUNNING_MESSAGE
//#         if (message.messageType==Msg.MESSAGE_TYPE_IN)
//#             setTicker(c, message.body);
//#endif
        if (Config.getInstance().widthSystemgc) {
                if (cf.ghostMotor) {
                      systemGC();
                }
        }
        if (countNewMsgs()) reEnumRoster();
        
        if (!message.unread) return;
        //TODO: clear unread flag if not-in-list IS HIDDEN

        if (c.getGroupType()==Groups.TYPE_IGNORE) 
            return;    // no signalling/focus on ignore
        
//#ifdef POPUPS
        if (cf.popUps)
            if (message.messageType==Msg.MESSAGE_TYPE_AUTH && showWobbler(c))
                setWobbler(2, c, message.from+"\n"+message.body);
//#endif

	if (cf.popupFromMinimized)
	    BombusMod.getInstance().hideApp(false);
        
        if (cf.autoFocus) 
            focusToContact(c, false);

        if (message.highlite) {
            playNotify(SOUND_FOR_ME);
//#ifdef POPUPS
            if (showWobbler(c))
                setWobbler(2, c, message.body);
//#endif
            autorespond = true;
        } else if (message.messageType==Msg.MESSAGE_TYPE_IN || message.messageType==Msg.MESSAGE_TYPE_HEADLINE) {
            if (c.origin<Contact.ORIGIN_GROUPCHAT) {
//#ifndef WMUC
                if (!(c instanceof MucContact))
//#endif
//#ifdef POPUPS
                    if (showWobbler(c)) {
                        setWobbler(2, c, c.toString()+": "+message.body);
                        autorespond = true;
                    }
//#endif
                if (c.group.type==Groups.TYPE_VIP) {
                    playNotify(SOUND_FOR_VIP);
                    autorespond = true;
                } else {
                    playNotify(SOUND_MESSAGE);
                    autorespond = true;
                }
            }
//#ifndef WMUC
              else {
                if (c.origin!=Contact.ORIGIN_GROUPCHAT && c instanceof MucContact) {
                     playNotify(SOUND_MESSAGE); //private message
                     autorespond = true;
                } else {
                    playNotify(SOUND_FOR_CONFERENCE);
                }
            }
//#endif
        }
        
        if (c.origin==Contact.ORIGIN_GROUPCHAT || c.jid.isTransport() || c.getGroupType()==Groups.TYPE_TRANSP || c.getGroupType()==Groups.TYPE_SEARCH_RESULT || c.getGroupType()==Groups.TYPE_SELF) 
            autorespond=false;
        
        if (message.messageType!=Msg.MESSAGE_TYPE_IN)
            autorespond=false;
        
        if (!c.autoresponded && autorespond) {
            ExtendedStatus es=sl.getStatus(myStatus);
            if (es.getAutoRespond()) {
//#if DEBUG
//#                 System.out.println(SR.MS_AUTORESPOND+" "+c.getJid());
//#endif

                Message autoMessage = new Message( 
                        c.getJid(),
                        es.getAutoRespondMessage(), 
                        SR.MS_AUTORESPOND, 
                        false 
                );
                theStream.send( autoMessage );
                c.autoresponded=true;

                c.addMessage(new Msg(Msg.MESSAGE_TYPE_SYSTEM, "local", SR.MS_AUTORESPOND, ""));
            }
        }
    }

    public void blockNotify(int event, long ms) {
        if (!notifyReady(-111)) return;
        blockNotifyEvent=event;
        notifyReadyTime=System.currentTimeMillis()+ms;
    }

    public boolean notifyReady(int event) {
        if ((blockNotifyEvent==event ||
            (blockNotifyEvent==-111 && event<=7)) &&
           System.currentTimeMillis()<notifyReadyTime) return false;
        else return true;
    }
   
    public void playNotify(int event) {
        if (!notifyReady(event)) return;
//#if DEBUG        
//#         System.out.println("event: "+event);
//#endif
        AlertCustomize ac=AlertCustomize.getInstance();
        
        int volume=ac.soundVol;
        int vibraLen=cf.vibraLen;
        String type, message;
        //boolean flashBackLight=ac.flashBackLight;

        switch (event) {
            case 0: //online
            case 1: //chat
                if (cf.notifySound) {
                    message=ac.soundOnline;
                    type=ac.soundOnlineType;
                } else {
                    message=null; type=null;
                }
                vibraLen=0;
                //flashBackLight=false;
                break;
            case 5: //offline
                message=ac.soundOffline;
                type=ac.soundOfflineType;
                vibraLen=0;
                //flashBackLight=false;
                break;
            case SOUND_FOR_VIP: //VIP
                message=ac.soundVIP;
                type=ac.soundVIPType;
                break;
            case SOUND_MESSAGE: //message
                message=ac.messagesnd;
                type=ac.messageSndType;
                break;
            case SOUND_FOR_CONFERENCE: //conference
                message=ac.soundConference;
                type=ac.soundConferenceType;
                if (ac.vibrateOnlyHighlited)
                    vibraLen=0;
                break;
            case SOUND_FOR_ME: //message for you
                message=ac.soundForYou;
                type=ac.soundForYouType;
                break;
            case SOUND_CONNECTED: //startup
                message=ac.soundStartUp;
                type=ac.soundStartUpType;
                vibraLen=0;
                //flashBackLight=false;
                break;
            case SOUND_COMPOSING: //composing
                message=ac.soundComposing;
                type=ac.soundComposingType;
                vibraLen=0;
                //flashBackLight=false;
                break;
            case SOUND_OUTGOING: //Outgoing
                message=ac.soundOutgoing;
                type=ac.soundOutgoingType;
                vibraLen=0;
                //flashBackLight=false;
                break;
            default:
                message="";
                type="none";
                vibraLen=0;
                //flashBackLight=false;
                break;
        }
       
        int profile=cf.profile;

        EventNotify notify=null;
        
        switch (profile) {
                                                         //display   fileType   soundName   volume      vibrate
            case AlertProfile.ALL:   notify=new EventNotify(display,    type,   message,    volume,     vibraLen); break;
            case AlertProfile.NONE:  notify=new EventNotify(display,    null,   null,       volume,     0); break;
            case AlertProfile.VIBRA: notify=new EventNotify(display,    null,   null,       volume,     vibraLen); break;
            case AlertProfile.SOUND: notify=new EventNotify(display,    type,   message,    volume,     0); break;
        }
        if (notify!=null) notify.startNotify();
        blockNotify(event, 2000);
    }

    private void focusToContact(final Contact c, boolean force) {
	Group g=c.group;
        if (g.collapsed) {
            g.collapsed=false;
            reEnumerator.queueEnum(c, force);
        }
        int index=vContacts.indexOf(c);
        if (index>=0) moveCursorTo(index);
    }

    public void beginConversation() { //todo: verify xmpp version
        if (theStream.isXmppV1())
            new SASLAuth(sd.account, this, theStream)
//#if SASL_XGOOGLETOKEN
//#              .setToken(token)
//#endif
             ;
//#if NON_SASL_AUTH
//#         else new NonSASLAuth(sd.account, this, theStream);
//#endif
    }

    public void connectionTerminated( Exception e ) {
         if( e!=null ) {
            askReconnect(e);
        } else {
            setProgress(SR.MS_DISCONNECTED, 0);
            try {
                sendPresence(Presence.PRESENCE_OFFLINE, null);
            } catch (Exception e2) {
//#if DEBUG
//#                 e2.printStackTrace();
//#endif
            }
         }
        redraw();
    }

    private void askReconnect(final Exception e) {
        StringBuffer error=new StringBuffer();
        if (e.getClass().getName().indexOf("java.lang.Exception")<0) {
            error.append(e.getClass().getName());
            error.append('\n');
        }
        if (e.getMessage()!=null)
            error.append(e.getMessage());
        
        if (e instanceof SecurityException) {
            String errSSL=io.SSLExceptionDecoder.decode(e);
            errorLog(errSSL);
            return;
        }
        if (currentReconnect>=cf.reconnectCount) { errorLog(error.toString()); return; }
        
        currentReconnect++;
        
        String topBar="("+currentReconnect+"/"+cf.reconnectCount+") Reconnecting";
        errorLog(topBar+"\n"+error.toString());

        reconnectWindow.getInstance().startReconnect();
     }
    
     public void doReconnect() {
        setProgress(SR.MS_DISCONNECTED, 0);
        
        logoff(null);
        
        try {
             sendPresence(lastOnlineStatus, null);
        } catch (Exception e2) { }
     }
    
    public void eventOk(){
        super.eventOk();
        if (createMsgList()==null) {
            cleanupGroup((Group)getFocusedObject());
            reEnumRoster();
        }
    }
    
    public void eventLongOk(){
        super.eventLongOk();
//#ifndef WMUC
//#ifdef POPUPS
        showInfo();
//#endif
//#endif
    }

    private Displayable createMsgList(){
        Object e=getFocusedObject();
        if (e instanceof Contact) {
            return new ContactMessageList((Contact)e,display);
        }
        return null;
    }
    
    protected void keyGreen(){
        if (!isLoggedIn()) return;
        Displayable pview=createMsgList();
        if (pview!=null) {
            Contact c=(Contact)getFocusedObject();
//#ifdef RUNNING_MESSAGE
//#             me = new MessageEdit(display, pview, c, c.msgSuspended);
//#else
            new MessageEdit(display, pview, c, c.msgSuspended);
//#endif
            c.msgSuspended=null;
        }
    }
    
    protected void keyClear(){
        if (isLoggedIn()) {
            Contact c=(Contact) getFocusedObject();
            try { 
                boolean isContact=( getFocusedObject() instanceof Contact );
//#ifndef WMUC
                boolean isMucContact=( getFocusedObject() instanceof MucContact );
//#else
//#                 boolean isMucContact=false;
//#endif
                if (isContact && !isMucContact) {
                   new AlertBox(SR.MS_DELETE_ASK, c.getNickJid(), display, this) {
                        public void yes() {
                            deleteContact((Contact)getFocusedObject());
                        }
                        public void no() {}
                    };
                }
//#ifndef WMUC
                else if (isContact && isMucContact && c.origin!=Contact.ORIGIN_GROUPCHAT) {
                    ConferenceGroup mucGrp=(ConferenceGroup)c.group;
                    if (mucGrp.selfContact.roleCode==MucContact.ROLE_MODERATOR) {
                        String myNick=mucGrp.selfContact.getName();
                        MucContact mc=(MucContact) c;
                        new ConferenceQuickPrivelegeModify(display, this, mc, ConferenceQuickPrivelegeModify.KICK,myNick);
                    }
                }
//#endif 
            } catch (Exception e) { /* NullPointerException */ }
        }
    }

    public void keyPressed(int keyCode){
        super.keyPressed(keyCode);
        
        switch (keyCode) {
//#ifdef POPUPS
            case KEY_POUND:
                if (getItemCount()==0)
                    return;
                showInfo();
                return;
//#endif
            case KEY_NUM1:
                if (cf.collapsedGroups) { //collapse all groups
                    for (Enumeration e=groups.elements(); e.hasMoreElements();) {
                        Group grp=(Group)e.nextElement();
                        grp.collapsed=true;
                    }
                    reEnumRoster();
                }
                break;
            case KEY_NUM4:
                super.pageLeft();
                return;
            case KEY_NUM6:
                super.pageRight();
                return;
//#ifdef AUTOSTATUS
//#             case SE_FLIPCLOSE_JP6:
//#             case SIEMENS_FLIPCLOSE:
//#             case MOTOROLA_FLIP:
//#                 if (phoneManufacturer!=Config.SONYE) { //workaround for SE JP6 - enabling vibra in closed state
//#                     display.setCurrent(null);
//#                     try {
//#                         Thread.sleep(300);
//#                     } catch (Exception ex) {}
//#                     display.setCurrent(this);
//#                 }
//#if DEBUG
//#             System.out.println("Flip closed");
//#endif
//#                 if (cf.autoAwayType==Config.AWAY_LOCK)
//#                     if (!autoAway)
//#                         autostatus.setTimeEvent(cf.autoAwayDelay* 60*1000);
//#                 break;
//#endif
            case KEY_NUM0:
                if (getItemCount()==0)
                    return;
                synchronized(hContacts) {
                    for (Enumeration e=hContacts.elements(); e.hasMoreElements();){
                        Contact c=(Contact)e.nextElement();
                        c.setIncoming(Contact.INC_NONE);
                        c=null;
                    }
                }
                redraw();

                systemGC();
                
                if (messageCount==0) return;
                Object atcursor=getFocusedObject();
                
                Contact c=(atcursor instanceof Contact)?(Contact)atcursor:(Contact)hContacts.firstElement();

                Enumeration i=hContacts.elements();

                int pass=0; //
                while (pass<2) {
                    if (!i.hasMoreElements()) i=hContacts.elements();
                    Contact p=(Contact)i.nextElement();
                    if (pass==1) if (p.getNewMsgsCount()>0) { 
                        focusToContact(p, true);
                        setRotator();
                        break; 
                    }
                    if (p==c) pass++;
                }
                break;
            case KEY_NUM3:
                if (getItemCount()==0)
                    return;
                int newpos=searchGroup(-1);
                if (newpos>-1) {
                    moveCursorTo(newpos);
                    setRotator();
                }
                break;
            case KEY_NUM9:
                if (getItemCount()==0)
                    return;
                int newpos2=searchGroup(1);
                if (newpos2>-1) {
                    moveCursorTo(newpos2);
                    setRotator();
                }
                break;
            case KEY_STAR:
                if (cf.ghostMotor) {
                    // backlight management
                    blState=(blState==1)? Integer.MAX_VALUE : 1;
                    display.flashBacklight(blState);
                }
                break;
        }
//#ifdef AUTOSTATUS
//#         userActivity();
//#endif
     }
 
    protected void keyRepeated(int keyCode) {
        super.keyRepeated(keyCode);
        if (kHold==keyCode) return;
        kHold=keyCode;
        
        if (keyCode==cf.keyLock) {
//#ifdef AUTOSTATUS
//#             if (cf.autoAwayType==Config.AWAY_LOCK) {
//#                 if (!autoAway) {
//#                     autoAway=true;
//#                     if (cf.useMyStatusMessages) {
//#                         sendPresence(Presence.PRESENCE_AWAY, null);
//#                     } else {
//#                         sendPresence(Presence.PRESENCE_AWAY, "Auto Status on KeyLock since %t");
//#                     }
//#                 }
//#             }
//#endif
            new SplashScreen(display, getMainBarItem(), cf.keyLock);
            return;
        } else if (keyCode==cf.keyVibra || keyCode==MOTOE680_FMRADIO /* TODO: redefine keyVibra*/) {
            // swap profiles
            int profile=cf.profile;
            cf.profile=(profile==AlertProfile.VIBRA)?cf.lastProfile : AlertProfile.VIBRA;
            cf.lastProfile=profile;
            
            updateMainBar();
            redraw();
            return;
        } else if (keyCode==KEY_NUM0) {
            cf.showOfflineContacts=!cf.showOfflineContacts;
            reEnumRoster();
            return;
        }
//#ifndef WMUC
        else if (keyCode==KEY_NUM1 && isLoggedIn()) new Bookmarks(display, this, null);
//#endif
       	else if (keyCode==KEY_NUM3) new ActiveContacts(display, this, null);
       	else if (keyCode==KEY_NUM4) new ConfigForm(display, this);
        else if (keyCode==KEY_NUM6) {
            Config.fullscreen=!Config.fullscreen;
            cf.saveToStorage();
            VirtualList.fullscreen=Config.fullscreen;
            StaticData.getInstance().roster.setFullScreenMode(Config.fullscreen);
        }
        else if (keyCode==KEY_NUM7)
            new RosterToolsMenu(display, this);
        else if (keyCode==KEY_NUM9) {
            
            
            if (cf.allowMinimize)
                BombusMod.getInstance().hideApp(true);
            else if (phoneManufacturer==Config.SIEMENS2)
              new SieNatMenu(display, this); /*
                 try {
                     //SIEMENS: MYMENU call. Possible Main Menu for capable phones
                      BombusMod.getInstance().platformRequest("native:ELSE_STR_MYMENU");
                 } catch (Exception e) { }  */
            else if (phoneManufacturer==Config.SIEMENS)//SIEMENS-NSG: MYMENU call. Possible Native Menu for capable phones
                 try {
                    BombusMod.getInstance().platformRequest("native:NAT_MAIN_MENU");
                 } catch (Exception e) { }
             
                     }
    }

//#ifdef AUTOSTATUS
//#     private void userActivity() {
//#         if (autostatus==null) return;
//# 
//#         if (cf.autoAwayType==Config.AWAY_IDLE) {
//#             if (!autoAway) {
//#                 autostatus.setTimeEvent(cf.autoAwayDelay* 60*1000);
//#                 return;
//#             }
//#         } else {
//#             return;
//#         }
//#         autostatus.setTimeEvent(0);
//#         setAutoStatus(Presence.PRESENCE_ONLINE);
//#     }
//# 
//#     public void messageActivity() {
//#         if (autostatus==null) return;
//# 
//#         if (cf.autoAwayType==Config.AWAY_MESSAGE) {
//#              //System.out.println("messageActivity "+myStatus.getImageIndex());
//#              if (myStatus<2)
//#                 autostatus.setTimeEvent(cf.autoAwayDelay* 60*1000);
//#              else if (!autoAway)
//#                 autostatus.setTimeEvent(0);
//#         }
//#     }
//#endif

//#ifdef POPUPS
    public void showInfo() {
        if (getFocusedObject()==null)
            return;

        try {
            VirtualList.popup.next();
            if (getFocusedObject() instanceof Group
//#ifndef WMUC
                    || getFocusedObject() instanceof ConferenceGroup
//#endif
                    )
                return;
            setWobbler(1, (Contact) null, null);
        } catch (Exception e) { }
    }

    public void setWobbler(int type, Contact contact, String info) {
        if (info==null) {
            StringBuffer mess=new StringBuffer();
            boolean isContact=(getFocusedObject() instanceof Contact);
            Contact cntact=(Contact)getFocusedObject();
//#ifndef WMUC
            boolean isMucContact=(getFocusedObject() instanceof MucContact);
            if (isMucContact) {
                MucContact mucContact=(MucContact)getFocusedObject();

                if (mucContact.origin!=Contact.ORIGIN_GROUPCHAT){
                    mess.append((mucContact.realJid==null)?"":"jid: "+mucContact.realJid+"\n");

                    if (mucContact.affiliationCode>MucContact.AFFILIATION_NONE)
                        mess.append(MucContact.getAffiliationLocale(mucContact.affiliationCode));

                    if (!(mucContact.roleCode==MucContact.ROLE_PARTICIPANT && mucContact.affiliationCode==MucContact.AFFILIATION_MEMBER)) {
                        if (mucContact.affiliationCode>MucContact.AFFILIATION_NONE)
                            mess.append(SR.MS_AND);
                        mess.append(MucContact.getRoleLocale(mucContact.roleCode));
                    }
                }
            } else {
//#endif
                mess.append("jid: ")
                    .append(cntact.bareJid)
                    .append(cntact.jid.getResource())
                    .append("\n")
                    .append(SR.MS_SUBSCRIPTION)
                    .append(": ")
                    .append(cntact.subscr);
//#ifdef PEP
//#                 if (cntact.hasMood()) {
//#                     mess.append("\n")
//#                         .append(SR.MS_USERMOOD)
//#                         .append(": ")
//#                         .append(cntact.getMoodString());
//#                 }
//#ifdef PEP_ACTIVITY
//#                 if (cntact.hasActivity()) {
//#                     mess.append("\n").append(SR.MS_USERACTIVITY).append(": ").append(cntact.activity);
//#                 }
//#endif
//#ifdef PEP_TUNE
//#                 if (cntact.pepTune) {
//#                     mess.append("\n").append(SR.MS_USERTUNE);
//#                     if (cntact.pepTuneText!="") {
//#                         mess.append(": ").append(cntact.pepTuneText);
//#                     }
//#                 }
//#endif
//#endif
//#ifndef WMUC
            }
//#endif
            if (cntact.origin!=Contact.ORIGIN_GROUPCHAT){
                mess.append((cntact.j2j!=null)?"\nJ2J: "+cntact.j2j:"");
//#ifdef CLIENTS_ICONS
//#ifdef PLUGINS
//#                 if (cf.showClientIcon)
//#endif
                    if (cntact.client>-1) {
                        mess.append("\n")
                            .append(SR.MS_USE)
                            .append(": ")
                            .append(cntact.clientName);
                    }
//#endif
                if (cntact.version!=null) {
                    mess.append("\n")
                        .append(SR.MS_VERSION)
                        .append(": ")
                        .append(cntact.version);
                }
                if (cntact.lang!=null) {
                    mess.append("\n")
                        .append(SR.MS_LANGUAGE)
                        .append(": ")
                        .append(cntact.lang);
                }
            }

            if (cntact.statusString!=null) {
                if (cntact.origin!=Contact.ORIGIN_GROUPCHAT){
                    mess.append("\n")
                        .append(SR.MS_STATUS)
                        .append(": ");
                }
                mess.append(cntact.statusString);

            if (cntact.priority!=0) {
                    mess.append(" [")
                        .append(cntact.priority)
                        .append("]");
                }
            }

            VirtualList.setWobble(1, null, mess.toString());
            mess=null;
        } else {
            VirtualList.setWobble(type, contact.getJid(), info);
        }

        redraw();
    }
//#endif
    
    public void logoff(String mess){
        if (isLoggedIn()) {
            try {
                if (mess==null) mess=sl.getStatus(Presence.PRESENCE_OFFLINE).getMessage();
                sendPresence(Presence.PRESENCE_OFFLINE, mess);
            } catch (Exception e) { }
        }
//#ifdef STATS
//#ifdef PLUGINS
//#         if (sd.Stats)
//#endif
//#             Stats.getInstance().saveToStorage(false);
//#endif
    }

    public void quit() {
//#ifdef AUTOSTATUS
//#         if (cf.autoAwayType!=Config.AWAY_OFF) {
//#             try {
//#                 autostatus.destroyTask();
//#             } catch (Exception ex) {}
//#         }
//#endif
        destroyView();
        logoff(null);

        BombusMod.getInstance().notifyDestroyed();
    }
//#ifndef MENU
    public void commandAction(Command c, Displayable d){
//#ifdef AUTOSTATUS
//#         userActivity();
//#endif
        if (c==cmdActions) { cmdActions(); }
        else if (c==cmdMinimize) { cmdMinimize();  }
        else if (c==cmdActiveContacts) { cmdActiveContacts(); }
        else if (c==cmdAccount){ cmdAccount(); }
        else if (c==cmdStatus) { cmdStatus(); }
        else if (c==cmdAlert) { cmdAlert(); }
//#ifdef ARCHIVE
	else if (c==cmdArchive) { cmdArchive(); }
//#endif
        else if (c==cmdInfo) { cmdInfo(); }
        else if (c==cmdTools) { cmdTools(); }
        else if (c==cmdCleanAllMessages) { cmdCleanAllMessages(); }     
//#ifndef WMUC
        else if (c==cmdConference) { cmdConference(); }
//#endif
        else if (c==cmdQuit) { cmdQuit(); }
        else if (c==cmdAdd) { cmdAdd(); }
    }
//#endif
//menu actions
    public void cmdQuit() { 
        if (cf.queryExit) {
            new AlertBox(SR.MS_QUIT_ASK, SR.MS_SURE_QUIT, display, null) {
                public void yes() { quit(); }
                public void no() { }
            };
        } else {
            quit();
        }
    }

    public void cmdMinimize() { BombusMod.getInstance().hideApp(true);  }
    public void cmdActiveContacts() { new ActiveContacts(display, this, null); }
    public void cmdAccount(){ new AccountSelect(display, this, false); }
    public void cmdStatus() { currentReconnect=0; new StatusSelect(display, this, null); }
    public void cmdAlert() { new AlertProfile(display, this); }
//#ifdef ARCHIVE
    public void cmdArchive() { new ArchiveList(display, this, -1, 1, null); }
//#endif
    public void cmdInfo() { new Info.InfoWindow(display, this); }
    public void cmdTools() { new RosterToolsMenu(display, this); }
//#ifdef POPUPS
    public void cmdClearPopups() { VirtualList.popup.clear(); }
//#endif
//#ifndef WMUC
   public void cmdConference() { if (isLoggedIn()) new Bookmarks(display, this, null); }
//#endif
   public void cmdActions() {
       if (isLoggedIn()) {
           try {
                new RosterItemActions(display, this, getFocusedObject(), -1);
           } catch (Exception ex) {}
       }
   }
   
   public void cmdAdd() {
       if (isLoggedIn()) {
            Object o=getFocusedObject();
            Contact cn=null;
            if (o instanceof Contact) {
                cn=(Contact)o;
                if (cn.getGroupType()!=Groups.TYPE_NOT_IN_LIST && cn.getGroupType()!=Groups.TYPE_SEARCH_RESULT)
                    cn=null;
            }
//#ifndef WMUC
            if (o instanceof MucContact)
                cn=(Contact)o;
//#endif
            new ContactEdit(display, this, cn);
       }
   }

//#ifndef WMUC
    public void reEnterRoom(Group group) {
	ConferenceGroup confGroup=(ConferenceGroup)group;
        String confJid=confGroup.selfContact.getJid();
        String name=confGroup.desc;
	new ConferenceForm(display, this, name, confJid, confGroup.password, false);
    }
    
    public void leaveRoom(Group group){
	ConferenceGroup confGroup=(ConferenceGroup)group;
	Contact myself=confGroup.selfContact;
	confGroup.confContact.commonPresence=false; //disable reenter after reconnect
        sendPresence(myself.getJid(), "unavailable", null, true);
        
        confGroup.inRoom=false;
	roomOffline(group);
    }
    
    public void roomOffline(final Group group) {
         for (Enumeration e=hContacts.elements(); e.hasMoreElements();) {
            Contact contact=(Contact)e.nextElement();
            if (contact.group==group) {
                contact.setStatus(Presence.PRESENCE_OFFLINE);
            }
         }
    }
//#endif
    protected void showNotify() { 
        super.showNotify(); 
        countNewMsgs(); 
//#ifdef AUTOSTATUS
//#         if (cf.autoAwayType==Config.AWAY_IDLE) {
//#             if (autostatus == null)  // Issue 107
//#                 autostatus = new AutoStatusTask();
//#             if (!autostatus.isAwayTimerSet())
//#                 if (!autoAway)
//#                     autostatus.setTimeEvent(cf.autoAwayDelay* 60*1000);
//#         }
//#endif
    }
    
    protected void hideNotify() {
        super.hideNotify();
//#ifdef AUTOSTATUS
//#         if (cf.autoAwayType==Config.AWAY_IDLE)
//#             if (kHold==0)
//#                 autostatus.setTimeEvent(0);
//#endif
    }
    
    private int searchGroup(int direction){
        int newpos=-1;
	synchronized (vContacts) {
	    int size=vContacts.size();
	    int pos=cursor;
	    int count=size;
	    try {
		while (count>0) {
		    pos+=direction;
		    if (pos<0) pos=size-1;
		    if (pos>=size) pos=0;
		    if (vContacts.elementAt(pos) instanceof Group) break;
		}
	    } catch (Exception e) { }
            newpos=pos;
	}
        return newpos;
    }
    
    public void searchActiveContact(int direction){
	Vector activeContacts=new Vector();
        int nowContact = -1, contacts=-1, currentContact=-1;
        synchronized (hContacts) {
            for (Enumeration r=hContacts.elements(); r.hasMoreElements(); ){
                Contact c=(Contact)r.nextElement();
                if (c.active()) {
                    activeContacts.addElement(c);
                    contacts=contacts+1;
                    if (c==activeContact) {
                        nowContact=contacts;
                        currentContact=contacts;
                    }
                }
            }
        }
        
        int size=activeContacts.size();
        
	if (size==0) return;

        try {
            nowContact+=direction;
            if (nowContact<0) nowContact=size-1;
            if (nowContact>=size) nowContact=0;
            
            if (currentContact==nowContact) return;
            
            Contact c=(Contact)activeContacts.elementAt(nowContact);
            new ContactMessageList((Contact)c,display);
        } catch (Exception e) { }
    }

    public void deleteContact(Contact c) {
        synchronized (hContacts) {
            for (Enumeration e=hContacts.elements();e.hasMoreElements();) {
                Contact c2=(Contact)e. nextElement();
                if (c.jid.equals(c2. jid,false)) {
                    c2.setStatus(Presence.PRESENCE_TRASH);
                    c2.offline_type=Presence.PRESENCE_TRASH;
                }
            }

            if (c.getGroupType()==Groups.TYPE_NOT_IN_LIST) {
                hContacts.removeElement(c);
                countNewMsgs();
                reEnumRoster();
            } else {
                theStream.send(new IqQueryRoster(c.bareJid,null,null,"remove"));
                
                sendPresence(c.bareJid, "unsubscribe", null, false);
                sendPresence(c.bareJid, "unsubscribed", null, false);
            }
        }
    }

    public void setQuerySign(boolean requestState) {
        querysign=requestState;
        updateMainBar();
    }

    public void storeContact(String jid, String name, String group, boolean askSubscribe){
        theStream.send(new IqQueryRoster(jid, name, group, null));
        if (askSubscribe) theStream.send(new Presence(jid,"subscribe"));
    }

    public void loginMessage(String msg, int pos) {
        setProgress(msg, pos);
    }
	
    public void setMyJid(Jid myJid) {
        this.myJid = myJid;
    }
    
//#ifdef AUTOSTATUS
//#     public void setAutoAway() {
//#         if (!autoAway) {
//#             oldStatus=myStatus;
//#             if (myStatus==0 || myStatus==1) {
//#                 autoAway=true;
//#                 if (cf.useMyStatusMessages) {
//#                     sendPresence(Presence.PRESENCE_AWAY, null);
//#                 } else {
//#                     sendPresence(Presence.PRESENCE_AWAY, SR.MS_AUTO_AWAY);
//#                 }
//#             }
//#         }
//#     }
//# 
//#     public void setAutoXa() {
//#         if (autoAway && !autoXa) {
//#             autoXa=true;
//#             if (cf.useMyStatusMessages) {
//#                 sendPresence(Presence.PRESENCE_XA, null);
//#             } else {
//#                 sendPresence(Presence.PRESENCE_XA, SR.MS_AUTO_XA);
//#             }
//#         }
//#     }
//# 
//#     public void setAutoStatus(int status) {
//#         if (!isLoggedIn())
//#             return;
//#         if (status==Presence.PRESENCE_ONLINE && autoAway) {
//#             autoAway=false;
//#             autoXa=false;
//#             sendPresence(Presence.PRESENCE_ONLINE, null);
//#             return;
//#         }
//#         if (status!=Presence.PRESENCE_ONLINE && myStatus==Presence.PRESENCE_ONLINE && !autoAway) {
//#             autoAway=true;
//#             if (cf.useMyStatusMessages) {
//#                 sendPresence(Presence.PRESENCE_AWAY, null);
//#             } else {
//#                 sendPresence(Presence.PRESENCE_AWAY, "Auto Status on KeyLock since %t");
//#             }
//#         }
//#     }
//#endif

    public void deleteGroup(Group deleteGroup) {
        synchronized (hContacts) {
            for (Enumeration e=hContacts.elements(); e.hasMoreElements();){
                Contact cr=(Contact)e.nextElement();
                if (cr.group==deleteGroup)
                    deleteContact(cr);                
            }
        }
    }

    
//#ifdef MENU_LISTENER
    public void showMenu() {
        commandState();
        new MyMenu(display, parentView, this, SR.MS_MAIN_MENU, MenuIcons.getInstance(), menuCommands);
    }

    public String touchRightCommand(){ return (cf.oldSE)?SR.MS_MENU:SR.MS_ACTION; }
    public String touchLeftCommand(){ return (cf.oldSE)?SR.MS_ACTION:SR.MS_MENU; }

    public void touchRightPressed(){ if (cf.oldSE) showMenu(); else cmdActions(); }
    public void touchLeftPressed(){ if (cf.oldSE) cmdActions(); else showMenu(); }
    public void captionPressed() {new ActiveContacts(display, this, null);}

//#endif
    
//#ifdef RUNNING_MESSAGE
//#     void setTicker(Contact c, String message) {
//#         if (cf.notifyWhenMessageType) {
//#             if (me!=null)
//#                 if (me.to==c)
//#                     me.setMyTicker(message);
//#         }
//#     }
//#endif
    

    private class ReEnumerator implements Runnable{
        //Thread thread;
        int pendingRepaints=0;
	boolean force;
	
	Object desiredFocus;
        
        public void queueEnum(Object focusTo, boolean force) {
	    desiredFocus=focusTo;
	    this.force=force;
	    queueEnum();
        }
	
        synchronized public void queueEnum() {
            pendingRepaints++;
            //if (thread==null) (thread=new Thread(this)).start();
            new Thread(this).start();
        }
        
        public void run(){
            //try {
                while (pendingRepaints>0) {
                    pendingRepaints=0;
                    
                    int locCursor=cursor;
                    Object focused=(desiredFocus==null)?getFocusedObject():desiredFocus;
		    desiredFocus=null;
                    Vector tContacts=new Vector(vContacts.size());

                    groups.resetCounters();
                    
                    synchronized (hContacts) {
                        for (Enumeration e=hContacts.elements(); e.hasMoreElements();){
                            Contact c=(Contact)e.nextElement();
                            Group grp=c.group;
			    grp.addContact(c);
                        }
                    }                
                    // self-contact group
                    Group selfContactGroup=groups.getGroup(Groups.TYPE_SELF);
                    selfContactGroup.visible=(cf.selfContact || selfContactGroup.tonlines>1 || selfContactGroup.unreadMessages>0 );
                    
                    // hiddens
                    groups.getGroup(Groups.TYPE_IGNORE).visible= cf.ignore ;
                    
                    // transports
                    Group transpGroup=groups.getGroup(Groups.TYPE_TRANSP);
                    transpGroup.visible= (cf.showTransports || transpGroup.unreadMessages>0);
                    
                    // adding groups
                    for (int i=0; i<groups.getCount(); i++)
                        groups.addToVector(tContacts,i);
                    
                    vContacts=tContacts;
                    tContacts=null;
                    StringBuffer onl=new StringBuffer()
                    .append("(")
                    .append(groups.getRosterOnline())
                    .append("/")
                    .append(groups.getRosterContacts())
                    .append(")");
                    setRosterMainBar(onl.toString());
                    onl=null;
                    
                    if (cursor<0) cursor=0;

                    if ( locCursor==cursor && focused!=null ) {
                        int c=vContacts.indexOf(focused);
                        if (c>=0) moveCursorTo(c);
			force=false;
                    }
                    focusedItem(cursor);
                    redraw();
                }
            //} catch (Exception e) {
//#ifdef DEBUG
//#                 //e.printStackTrace();
//#endif
            //}
            //thread=null;
        }
    }
}
