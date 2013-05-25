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

import xmpp.Jid;
import xmpp.Account;
import Account.AccountSelect;
import Account.AccountStorage;
import Alerts.AlertCustomize;
import Alerts.AlertProfile;
//#ifndef WMUC
import xmpp.extensions.muc.BookmarkQuery;
import Conference.Bookmarks;
import Conference.ConferenceGroup;
import Conference.MucContact;
import Conference.affiliation.ConferenceQuickPrivelegeModify;
import Conference.ConferenceForm;
import xmpp.extensions.muc.Conference;
//#endif
//#ifdef STATS
//# import Statistic.Stats;
//#endif
//#ifdef ARCHIVE
//# import Archive.ArchiveList;
//#endif
import Menu.RosterItemActions;
import Menu.RosterToolsMenu;
import Menu.SieNatMenu;
//#ifdef CLIENTS_ICONS
//# import images.ClientsIconsData;
//#endif
import images.RosterIcons;

import Menu.MenuCommand;
//#ifdef SYSTEM_NOTIFY
//# import Messages.notification.Notification;
//# import Messages.notification.Notificator;
//#ifdef android
//# import Messages.notification.AndroidNotification;
//#endif
//#endif
//#ifdef PRIVACY
//# import PrivacyLists.QuickPrivacy;
//#endif

//#if FILE_TRANSFER
//# import io.file.transfer.TransferDispatcher;
//#endif

import locale.SR;

import xmpp.login.LoginListener;

import midlet.BombusMod;
import ui.controls.AlertBox;
import util.StringUtils;
import com.alsutton.jabber.*;
import com.alsutton.jabber.datablocks.*;
import java.util.*;

import ui.*;
//#ifdef POPUPS
//# import ui.controls.PopUp;
//#endif
import ui.controls.form.DefForm;
import xmpp.EntityCaps;

import xmpp.XmppError;
//#ifdef CAPTCHA
//# import xmpp.extensions.Captcha;
//#endif

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
import xmpp.extensions.RosterXListener;
import xmpp.extensions.IqVCard;

//#ifdef LIGHT_CONFIG
//# import LightControl.CustomLight;
//#endif
import xmpp.JabberDispatcher;
import xmpp.JidUtils;
import xmpp.PresenceDispatcher;
import xmpp.RosterDispatcher;

//#if android
//# import org.bombusmod.BombusModActivity;
//#endif
public class Roster
        extends DefForm
        implements
        Runnable,
        LoginListener {

    private MenuCommand cmdStatus = new MenuCommand(SR.MS_STATUS_MENU, MenuCommand.SCREEN, 4, RosterIcons.ICON_STATUS);
    private MenuCommand cmdActiveContacts = new MenuCommand(SR.MS_ACTIVE_CONTACTS, MenuCommand.SCREEN, 3, RosterIcons.ICON_ACTIVE);
    private MenuCommand cmdAlert = new MenuCommand(SR.MS_ALERT_PROFILE_CMD, MenuCommand.SCREEN, 8, RosterIcons.ICON_ALERTS);
//#ifndef WMUC
    private MenuCommand cmdConference = new MenuCommand(SR.MS_CONFERENCE, MenuCommand.SCREEN, 10, RosterIcons.ICON_CONFERENCE);
//#endif
//#ifdef ARCHIVE
//#     private MenuCommand cmdArchive = new MenuCommand(SR.MS_ARCHIVE, MenuCommand.SCREEN, 10, RosterIcons.ICON_ARCHIVE);
//#endif
    private MenuCommand cmdAdd = new MenuCommand(SR.MS_ADD_CONTACT, MenuCommand.SCREEN, 12, RosterIcons.ICON_ADD_CONTACT);
    private MenuCommand cmdTools = new MenuCommand(SR.MS_TOOLS, MenuCommand.SCREEN, 14, RosterIcons.ICON_TOOLS);
    private MenuCommand cmdAccount = new MenuCommand(SR.MS_ACCOUNT_, MenuCommand.SCREEN, 15, RosterIcons.ICON_ACCOUNTS);
    private MenuCommand cmdCleanAllMessages = new MenuCommand(SR.MS_CLEAN_ALL_MESSAGES, MenuCommand.SCREEN, 50, RosterIcons.ICON_CLEAN_MESSAGES);
    private MenuCommand cmdInfo = new MenuCommand(SR.MS_ABOUT, MenuCommand.SCREEN, 80, RosterIcons.ICON_ABOUT);
    private MenuCommand cmdMinimize = new MenuCommand(SR.MS_APP_MINIMIZE, MenuCommand.SCREEN, 90, RosterIcons.ICON_MINIMIZE);
    private MenuCommand cmdQuit = new MenuCommand(SR.MS_APP_QUIT, MenuCommand.SCREEN, 99, RosterIcons.ICON_BUILD_NEW);
    public Contact activeContact = null;
    public Jid myJid;
    public int messageCount;
    public int highliteMessageCount;
    public Object transferIcon;
    public final Vector hContacts = new Vector();
    //private Vector vContacts;
    public Groups groups;
    public static MessageEdit me = null;
    private StatusList sl;
    public int myStatus = cf.loginstatus;
    private static String myMessage;
    public static int oldStatus = 0;
    private static int lastOnlineStatus;
    public int currentReconnect = 0;
    public boolean doReconnect = false;
    public boolean querysign = false;
//#ifdef JUICK
//#     public Vector juickContacts = new Vector();
//#     public int indexMainJuickContact = -1; // Т.е. считаем, что жуйкоконтактов нет вообще.
//#endif
    public long lastMessageTime = Time.utcTimeMillis();
    public static String startTime = Time.dispLocalTime();
    private static long notifyReadyTime = System.currentTimeMillis();
    private static int blockNotifyEvent = -111;
    private int blState = Integer.MAX_VALUE;
    public final static int SOUND_FOR_ME = 500;
    public final static int SOUND_FOR_CONFERENCE = 800;
    public final static int SOUND_MESSAGE = 1000;
    public final static int SOUND_CONNECTED = 777;
    public final static int SOUND_FOR_VIP = 100;
    public final static int SOUND_COMPOSING = 888;
    public final static int SOUND_OUTGOING = 999;

    public Roster() {
        super(null, false);

        //splash = SplashScreen.getInstance();

        sl = StatusList.getInstance();

        // setLight(cf.lightState);

        mainbar = new MainBar(4, null, null, false);
        mainbar.addRAlign();
        mainbar.addElement(null);
        mainbar.addElement(null);
        mainbar.addElement(null); //ft

        groups = null;
        groups = new Groups();

        /*
         * vContacts=null; vContacts=new Vector(); // just for displaying
         */
        updateMainBar();

//#ifdef CLIENTS_ICONS
//#         ClientsIconsData.getInstance();
//#endif
        enableListWrapping(true);
    }

    public final void commandState() {
        menuName = SR.MS_MAIN_MENU;
        menuCommands.removeAllElements();
        addMenuCommand(cmdStatus);
        addMenuCommand(cmdActiveContacts);
//#ifndef WMUC

        if (isLoggedIn()) {
            addMenuCommand(cmdConference);
        }
//#endif
        addMenuCommand(cmdAlert);
//#ifdef ARCHIVE
//#         addMenuCommand(cmdArchive);
//#endif
        if (isLoggedIn()) {
            addMenuCommand(cmdAdd);
        }
        addMenuCommand(cmdAccount);
        addMenuCommand(cmdTools);
        addMenuCommand(cmdInfo);

        if (cf.allowMinimize) {
            addMenuCommand(cmdMinimize);
        }

        addMenuCommand(cmdCleanAllMessages);
        if (phoneManufacturer != Config.NOKIA_9XXX) {
            addMenuCommand(cmdQuit);
        }
    }

    public void setProgress(String pgs, int percent) {
        SplashScreen.getInstance().setProgress(pgs, percent);
        setRosterMainBar(pgs);
        redraw();
    }

    public void setProgress(int percent) {
        SplashScreen.getInstance().setProgress(percent);
    }

    private void setRosterMainBar(String s) {
        mainbar.setElementAt(s, 3);
    }
    private int rscaler;
    private int rpercent;

    public void rosterItemNotify() {
        rscaler++;
        if (rscaler < 4) {
            return;
        }
        rscaler = 0;
        if (rpercent < 100) {
            rpercent++;
        }
        SplashScreen.getInstance().setProgress(rpercent);
    }

    // establishing connection process
    public void run() {
//#ifdef POPUPS
//#         //if (cf.firstRun) setWobbler(1, (Contact) null, SR.MS_ENTER_SETTINGS);
//#endif
        setQuerySign(true);
        if (!doReconnect) {
            setProgress(25);
            resetRoster();
        } else {
            makeRosterOffline();
        }
        try {
            Account a = sd.account;
            setProgress(SR.MS_CONNECT_TO_ + a.JID.getServer(), 30);

            sd.theStream = a.openJabberStream();
            new Thread(sd.theStream).start();
            setProgress(SR.MS_OPENING_STREAM, 40);
            sd.theStream.listener = new JabberDispatcher();
            sd.theStream.initiateStream();
        } catch (Exception e) {
            askReconnect(e);
        }
    }

    public void resetRoster() {
        synchronized (hContacts) {
            hContacts.removeAllElements();

//#ifdef JUICK
//#             juickContacts = null;
//#             juickContacts = new Vector();
//#             indexMainJuickContact = -1;
//#endif

            groups = null;
            groups = new Groups();                            
        }
        if (sd.account != null) {
            sd.account.bookmarks.removeAllElements();            
            myJid = sd.account.JID;
            updateContact(sd.account.nick, myJid.getBare(), SR.MS_SELF_CONTACT, "self", false);
        }
    }

    public void errorLog(String s) {
        if (s == null) {
            return;
        }

        Msg m = new Msg(Msg.MESSAGE_TYPE_OUT, "local", "Error", s);
        messageStore(selfContact(), m);
    }

    public void beginPaint() {
        updateMainBar();
    }

    public void show() {
        super.show();
        countNewMsgs();
    }

    public void setEventIcon(Object icon) {
        transferIcon = icon;
        mainbar.setElementAt(icon, 7);
        redraw();
    }

    public Object getEventIcon() {
        if (transferIcon != null) {
            return transferIcon;
        }
        return null;
    }

    private void updateMainBar() {
        int s = querysign ? RosterIcons.ICON_PROGRESS_INDEX : myStatus;
        int profile = cf.profile;
        Object en = (profile > 0) ? new Integer(profile + RosterIcons.ICON_PROFILE_INDEX + 1) : null;

        mainbar.setElementAt((messageCount == 0) ? null : new Integer(RosterIcons.ICON_MESSAGE_INDEX), 0);

        mainbar.setElementAt((messageCount == 0) ? null : getHeaderString(), 1);
        mainbar.setElementAt(new Integer(s), 2);
        mainbar.setElementAt(en, 5);

        if (phoneManufacturer == Config.WINDOWS) {
            if (messageCount == 0) {
                VirtualCanvas.getInstance().setTitle("BombusMod");
            } else {
                VirtualCanvas.getInstance().setTitle("BombusMod " + getHeaderString());
            }
        }
//#ifdef SYSTEM_NOTIFY        
//#         if (highliteMessageCount < 1) {
//#             Notificator n = Notification.getNotificator();
//#             if (n != null) {
//#                 n.clear();
//#             }
//#         }
//#endif        
    }

    public String getHeaderString() {
        return ((highliteMessageCount == 0) ? " " : " " + highliteMessageCount + "/") + messageCount + " ";
    }

    boolean countNewMsgs() {
        int m = 0;
        int h = 0;
        synchronized (hContacts) {
            int j = hContacts.size();
            for (int i = 0; i < j; i++) {
                Contact c = (Contact) hContacts.elementAt(i);
                if (c.origin == Contact.ORIGIN_GROUPCHAT) {
                    h += c.getNewHighliteMsgsCount();
                } else {
                    h += c.getNewMsgsCount();
                }
                m += c.getNewMsgsCount();
            }
        }
        highliteMessageCount = h;
        messageCount = m;
//#ifdef android    
//#         if (highliteMessageCount<1) {
//#             Notification.getNotificator().getNotificationManager().cancel(AndroidNotification.NOTIFY_ID);
//#         } else {
//#             Notification.getNotificator().sendNotify("New messages", String.valueOf(messageCount));
//#         }
//#endif
        if (h == m) {
            h = 0;
        }
        updateMainBar();
        return (m > 0);
    }

    public void cleanupSearch() {
        int index = 0;
        synchronized (hContacts) {
            int j = hContacts.size();
            while (index < j) {
                if (((Contact) hContacts.elementAt(index)).getGroupType() == Groups.TYPE_SEARCH_RESULT) {
                    hContacts.removeElementAt(index);
                    j--;
                } else {
                    index++;
                }
            }
        }
        reEnumRoster();
    }

    public void cmdCleanAllMessages() {
        if (messageCount > 0) {
            new AlertBox(SR.MS_UNREAD_MESSAGES + ": " + messageCount, SR.MS_SURE_DELETE) {

                public void yes() {
                    cleanAllMessages();
                }

                public void no() {
                }
            };
        } else {
            cleanAllMessages();
            cleanAllGroups();
        }
    }

    public void cleanAllMessages() {
        synchronized (hContacts) {
            int j = hContacts.size();
            for (int i = 0; i < j; i++) {
                Contact c = (Contact) hContacts.elementAt(i);
                try {
                    c.purge();
                } catch (Exception ex) {
                }
            }
        }
        highliteMessageCount = 0;
        messageCount = 0;
        cleanAllGroups();
        //reEnumRoster();
        redraw();
    }

    public void cleanAllGroups() {
        for (Enumeration e = groups.elements(); e.hasMoreElements();) {
            Group group = (Group) e.nextElement();
            cleanupGroup(group);
        }
        reEnumRoster();
    }

    public void cleanupGroup(Group g) {
        /*
         * Group g=(Group)getFocusedObject();
         */
        if (g == null) {
            return;
        }
        if (!g.collapsed && !Config.getInstance().autoClean) {
            return;
        }
        Jid groupSelfContact = null;
//#ifndef WMUC
        if (g instanceof ConferenceGroup) {
            ConferenceGroup cg = (ConferenceGroup) g;
            groupSelfContact = cg.selfContact.jid;
            if (!cg.inRoom) {
                int index = 0;
                boolean removeGroup = true;
                synchronized (hContacts) {
                    int j = hContacts.size();
                    while (index < j) {
                        Contact contact = (Contact) hContacts.elementAt(index);
                        if (contact.group.name.equals(g.name)) {
                            if (contact.getNewMsgsCount() == 0) {
                                contact.msgs.removeAllElements();
                                hContacts.removeElementAt(index);
                                j--;
                            } else {
                                removeGroup = false;
                                index++;
                            }
                        } else {
                            index++;
                        }
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
        int index = 0;

        synchronized (hContacts) {
            int j = hContacts.size();
            while (index < j) {
                Contact contact = (Contact) hContacts.elementAt(index);
                if (contact.group.name.equals(g.name)) {
                    if (contact.origin > Contact.ORIGIN_ROSTERRES
                            && contact.status >= Presence.PRESENCE_OFFLINE
                            && !contact.haveChatMessages()
                            && !contact.jid.equals(groupSelfContact, true)
                            && contact.origin != Contact.ORIGIN_GROUPCHAT) {

                        contact.msgs.removeAllElements();
                        hContacts.removeElementAt(index);
                        j--;
                    } else {
                        index++;
                    }
                } else {
                    index++;
                }
            }
//#ifndef WMUC            
            if (g.getOnlines() == 0 && !(g instanceof ConferenceGroup)) {
                if (g.type == Groups.TYPE_MUC) {
                    groups.removeGroup(g);
                }
            }
//#endif            
        }
    }
    ReEnumerator reEnumerator = null;

    public void reEnumRoster() {
        if (reEnumerator == null) {
            reEnumerator = new ReEnumerator();
        }
        reEnumerator.queueEnum();
    }

    public void updateContact(String nick, String jid, String grpName, String subscr, boolean ask) {
        // called only on roster read
        int status = Presence.PRESENCE_OFFLINE;
        if (subscr.equals("none")) {
            status = Presence.PRESENCE_UNKNOWN;
        }
        if (ask) {
            status = Presence.PRESENCE_ASK;
        }
        if (subscr.equals("remove")) {
            status = -1;
        }

        Jid J = new Jid(jid);
        Contact c = findContact(J, false); // search by bare jid
        if (c == null) {
            c = new Contact(nick, jid, Presence.PRESENCE_OFFLINE, null);
            addContact(c);
//#ifdef JUICK
//#             addJuickContact(c);
//#endif
        }

        boolean firstInstance = true; //FS#712 workaround
        int index = 0;
        synchronized (hContacts) {
            int j = hContacts.size();
            for (int i = 0; i < j; i++) {
                c = (Contact) hContacts.elementAt(i);
                if (c.jid.equals(J, false)) {
                    Group group = (JidUtils.isTransport(c.jid))
                            ? groups.getGroup(Groups.TYPE_TRANSP)
                            : groups.getGroup(grpName);
                    if (group == null) {
                        group = groups.addGroup(grpName, Groups.TYPE_COMMON);
                    }

                    if (status < 0) {
                        hContacts.removeElementAt(index);
                        j--;
//#ifdef JUICK
//#                         deleteJuickContact(c);
//#endif
                        continue;
                    }

                    c.group = group;
                    c.subscr = subscr;
                    c.offline_type = status;
                    c.ask_subscribe = ask;

                    if (c.origin == Contact.ORIGIN_PRESENCE) {
                        if (firstInstance) {
                            c.origin = Contact.ORIGIN_ROSTERRES;
                        } else {
                            c.origin = Contact.ORIGIN_CLONE;
                        }
                    }
                    firstInstance = false;

                    if (querysign == true) {
                        if (cf.collapsedGroups) {
                            Group g = c.group;
                            g.collapsed = true;
                        }
                    }

                    c.setSortKey((nick == null) ? jid : nick);
                }
                index++;
            }
        }
        //if (status<0) removeTrash();
    }

    private void removeTrash() {
        int index = 0;
        synchronized (hContacts) {
            int j = hContacts.size();
            while (index < j) {
                Contact c = (Contact) hContacts.elementAt(index);
                if (c.offline_type < 0) {
                    hContacts.removeElementAt(index);
                    j--;
                } else {
                    index++;
                }
            }
            countNewMsgs();
        }
    }
//#ifndef WMUC

    public MucContact findMucContact(Jid jid) {
        Contact contact = findContact(jid, true);
        if (contact instanceof MucContact) {
            return (MucContact) contact;
        } else {
            if (contact != null) {
                // drop buggy bookmark in roster
                synchronized (hContacts) {
                    hContacts.removeElement(contact);
                }
            }
            return null;
        }
    }

    public final ConferenceGroup initMuc(String from, String joinPassword) {

        // muc message
        int ri = from.indexOf('@');
        int rp = from.indexOf('/');
        String room = from.substring(0, ri);
        String roomJid = from.substring(0, rp).toLowerCase();

        ConferenceGroup grp = groups.getConfGroup(new Jid(roomJid));

        // creating room
        if (grp == null) // we hasn't joined this room yet
        {
            groups.addGroup(grp = new ConferenceGroup(room, new Jid(roomJid)));
        }
        grp.password = joinPassword;

        MucContact c = findMucContact(new Jid(roomJid));

        if (c == null) {
            c = new MucContact(room, roomJid);
            addContact(c);
        }

        // change nick if already in room
        if (grp.selfContact != null && grp.selfContact.status < Presence.PRESENCE_OFFLINE) {
            return grp;
        }

        c.setStatus(Presence.PRESENCE_ONLINE);

        c.transport = RosterIcons.ICON_GROUPCHAT_INDEX; //FIXME: убрать хардкод
        c.origin = Contact.ORIGIN_GROUPCHAT;
        c.commonPresence = true;

        grp.conferenceJoinTime = Time.utcTimeMillis();
        grp.confContact = c;
        c.group = grp;

        String nick = from.substring(rp + 1);

        // old self-contact
        c = grp.selfContact;

        // check for existing entry - it may be our old self-contact
        // or another contact whose nick we pretend
        MucContact foundInRoom = findMucContact(new Jid(from));
        if (foundInRoom != null) {
            c = foundInRoom;            //choose found contact instead of old self-contact
        }

        // if exists (and online - rudimentary check due to line 500)
        // rename contact
        if (c != null) {
            if (c.status >= Presence.PRESENCE_OFFLINE) {
                c.nick = nick;
                c.jid = new Jid(from);                
            }
        }

        // create self-contact if no any candidates found
        if (c == null) {
            c = new MucContact(nick, from);
            addContact(c);
        }

        grp.selfContact = c;
        c.group = grp;
        c.origin = Contact.ORIGIN_GC_MYSELF;

        grp.collapsed = cf.collapsedGroups;

        sort(hContacts);
        return grp;
    }

    public final MucContact mucContact(Jid from) {
        // muc message
        String roomJid = from.getBare();

        ConferenceGroup grp = groups.getConfGroup(new Jid(roomJid));

        if (grp == null) {
            return null; // we are not joined this room
        }
        MucContact c = findMucContact(from);

        if (c == null) {
            c = new MucContact(from.resource, from.toString());
            addContact(c);
            c.origin = Contact.ORIGIN_GC_MEMBER;
        }

        c.group = grp;
        sort(hContacts);
        return c;
    }
//#endif

    public final Contact getContact(final String jid, boolean createInNIL) {
        Jid J = new Jid(jid);

        Contact c = findContact(J, true);
        if (c != null) {
            return c;
        }

        c = findContact(J, false);
        if (c == null) {
            if (!createInNIL) {
                return null;
            }
            c = new Contact(null, jid, Presence.PRESENCE_OFFLINE, "none"); /*
             * "not-in-list"
             */
            c.origin = Contact.ORIGIN_PRESENCE;
            c.group = groups.getGroup(Groups.TYPE_NOT_IN_LIST);
            addContact(c);
        } else {
            if (c.origin == Contact.ORIGIN_ROSTER) {
                c.origin = Contact.ORIGIN_ROSTERRES;
                c.setStatus(Presence.PRESENCE_OFFLINE);
                c.jid = J;
                //System.out.println("add resource");
            } else {
                c = c.clone(J, Presence.PRESENCE_OFFLINE);
                addContact(c);
                //System.out.println("cloned");
            }
        }
        sort(hContacts);
        return c;
    }

//#ifdef POPUPS
//#     public void showContactMessageList(String jid) {
//#         sd.roster.getContact(jid, false).getMsgList();
//#     }
//#endif

    public void addContact(Contact c) {
        synchronized (hContacts) {
            hContacts.addElement(c);
        }
    }

    public final Contact findContact(final Jid j, final boolean compareResources) {
        synchronized (hContacts) {
            int j2 = hContacts.size();
            for (int i = 0; i < j2; i++) {
                Contact c = (Contact) hContacts.elementAt(i);
                if (c.jid.equals(j, compareResources)) {
                    return c;
                }
            }
        }
        return null;
    }

    public Contact getFirstContactWithNewHighlite(Contact contact) {
        if (hContacts.isEmpty()) {
            return null;
        }
        if (null == contact) {
            contact = (Contact) hContacts.firstElement();
        }
        for (int index = hContacts.indexOf(contact) + 1; index < hContacts.size(); ++index) {
            Contact c = (Contact) hContacts.elementAt(index);
            if (c.getNewHighliteMsgsCount() > 0 || (c.origin != Contact.ORIGIN_GROUPCHAT && c.hasNewMsgs())) {
                return c;
            }
        }
        for (int index = 0; index < hContacts.size(); ++index) {
            Contact c = (Contact) hContacts.elementAt(index);
            if (c.getNewHighliteMsgsCount() > 0 || (c.origin != Contact.ORIGIN_GROUPCHAT && c.hasNewMsgs())) {
                return c;
            }
        }
        if (contact.getNewHighliteMsgsCount() > 0 || (contact.origin != Contact.ORIGIN_GROUPCHAT && contact.hasNewMsgs())) {
            return contact;
        }
        return null;
    }
//#ifdef JUICK
//# /*
//#      * public Vector getJuickContacts(boolean str) { Vector juickContacts = new
//#      * Vector(); synchronized (hContacts) { for (Enumeration e =
//#      * hContacts.elements(); e.hasMoreElements();) { Contact c = (Contact)
//#      * e.nextElement(); if (isJuickContact(c)) if (str)
//#      * juickContacts.addElement(c.bareJid); else // juickContacts.addElement(c);
//#      * } } return juickContacts; }
//#      */
//# 
//#     public Contact getMainJuickContact() {
//#         if (indexMainJuickContact > -1) {
//#             return (Contact) juickContacts.elementAt(indexMainJuickContact);
//#         } else {
//#             return null;
//#         }
//#     }
//# 
//#     /*
//#      * public void updateMainJuickContact() { System.out.println("1. juickJID:
//#      * "+cf.juickJID); mainJuickContact = null; int index = -1; if
//#      * (!cf.juickJID.equals("")) { index = hContacts.indexOf(new
//#      * Contact("Juick", cf.juickJID, Presence.PRESENCE_OFFLINE, null));
//#      * System.out.println("index: "+index); if (index < 0) { // Если не нашли,
//#      * то считаем, что не указан. cf.juickJID = ""; } } if (index > -1) {
//#      * mainJuickContact = (Contact) hContacts.elementAt(index); } else { Vector
//#      * juickContacts = getJuickContacts(false); if (juickContacts.size() > 0) {
//#      * mainJuickContact = (Contact) juickContacts.elementAt(0); } }
//#      * System.out.println("2. juickJID: "+cf.juickJID); }
//#      */
//#     public boolean isJuickContact(Contact c) {
//#         return JidUtils.equalsViaJ2J(c.jid, "juick@juick.com")
//#                 || JidUtils.equalsViaJ2J(c.jid, "psto@psto.net")
//#                 || JidUtils.equalsViaJ2J(c.jid, "lij.habahaba.im");
//#     }
//# 
//#     public void addJuickContact(Contact c) {
//#         if (isJuickContact(c)) {
//#             juickContacts.addElement(c);
//#             // Далее урезаный аналог updateMainJuickContact(). Побыстрее него, работает *только* при добавлении контакта.
//#             if (isMainJuickContact(c)) {
//#                 indexMainJuickContact = juickContacts.size() - 1;
//#             } else if (indexMainJuickContact < 0) {
//#                 indexMainJuickContact = 0;
//#             }
//#         }
//#     }
//# 
//#     public void deleteJuickContact(Contact c) {
//#         if (juickContacts.removeElement(c)) {
//#             updateMainJuickContact();
//#         }
//#     }
//# 
//#     public boolean isMainJuickContact(Contact c) {
//#         return c.jid.getBare().equals(JuickConfig.getJuickJID());
//#     }
//# 
//#     public void updateMainJuickContact() {
//#         int size = juickContacts.size();
//#         if (size < 1) {
//#             indexMainJuickContact = -1;
//#         } else if ((size == 1) || (JuickConfig.getJuickJID().length() == 0)) {
//#             indexMainJuickContact = 0;
//#         } else {
//#             //indexMainJuickContact = juickContacts.indexOf(new Contact("Juick", juickConfig.getJuickJID(), Presence.PRESENCE_OFFLINE, null));
//#             for (int i = 0; i < juickContacts.size(); i++) {
//#                 if (((Contact) juickContacts.elementAt(i)).jid.getBare().equals(JuickConfig.getJuickJID())) {
//#                     indexMainJuickContact = i;
//#                 }
//#             }
//#             if (indexMainJuickContact < 0) {
//#                 JuickConfig.setJuickJID("", false);
//#                 indexMainJuickContact = 0; // Можно сделать это присваивание через рекурсию, но вроде пока не надо.
//#             }
//#         }
//#     }
//#endif

    public void sendPresence(int newStatus, String message) {
        if (newStatus != Presence.PRESENCE_SAME) {
            myStatus = newStatus;
        }

        if (message != null) {
            myMessage = message;
        }

        setQuerySign(false);

        if (myStatus != Presence.PRESENCE_OFFLINE) {
            lastOnlineStatus = myStatus;
        }

        // reconnect if disconnected
        if (myStatus != Presence.PRESENCE_OFFLINE && !isLoggedIn()) {
            synchronized (hContacts) {
                doReconnect = (hContacts.size() > 1);
            }
            redraw();
            new Thread(this).start();
            return;
        }

        blockNotify(-111, 13000);

        if (isLoggedIn()) {
            if (myStatus == Presence.PRESENCE_OFFLINE && !cf.collapsedGroups) {
                groups.queryGroupState(false);
            }

            // send presence
            ExtendedStatus es = sl.getStatus(myStatus);
            if (message == null) {
                myMessage = StringUtils.toExtendedString(es.getMessage());
            }

            myMessage = StringUtils.toExtendedString(myMessage);
            int myPriority = es.getPriority();

            Presence presence = new Presence(myStatus, myPriority, myMessage, sd.account.nick);

            if (!sd.account.mucOnly) {
                sd.theStream.send(presence);
            }
//#ifndef WMUC
            reEnumerator = null;
            multicastConferencePresence(myStatus, myMessage, myPriority);
//#endif
        }

        // disconnect
        if (myStatus == Presence.PRESENCE_OFFLINE) {
            try {
                sd.theStream.close(); // sends </stream:stream> and closes socket
                sd.theStream.loggedIn = false;
            } catch (Exception e) { /*
                 * e.printStackTrace();
                 */ }
            makeRosterOffline();
        }
        Contact c = selfContact();
        c.setStatus(myStatus);
    }

    public void makeRosterOffline() {
//#ifdef AUTOSTATUS
//#             AutoStatus.getInstance().stop();
//#endif        
        synchronized (hContacts) {
            int j = hContacts.size();
            for (int i = 0; i < j; i++) {
                ((Contact) hContacts.elementAt(i)).setStatus(Presence.PRESENCE_OFFLINE); // keep error & unknown
            }
        }
        sort(hContacts);
        reEnumRoster();
        redraw();
    }

    public void sendDirectPresence(int status, String to, String nick, JabberDataBlock x) {
        if (to == null) {
            sendPresence(status, null);
            return;
        }

        ExtendedStatus es = sl.getStatus(status);
        myMessage = es.getMessage();

        myMessage = StringUtils.toExtendedString(myMessage);

        Presence presence = new Presence(status, es.getPriority(), myMessage,
                nick == null ? sd.account.nick : nick);

        presence.setTo(to);

        if (x != null) {
            presence.addChild(x);
        }

        if (sd.theStream != null) {
            sd.theStream.send(presence);
        }
    }

    public void sendDirectPresence(int status, Contact to, JabberDataBlock x) {
        sendDirectPresence(status, (to == null) ? null : to.getJid().toString(), null, x);
        if (to != null) {
            if (JidUtils.isTransport(to.jid)) {
                blockNotify(-111, 10000);
            }
//#ifndef WMUC
            if (to instanceof MucContact) {
                ((MucContact) to).commonPresence = false;
            }
//#endif
        }
    }

    public boolean isLoggedIn() {
        if (sd.theStream == null) {
            return false;
        }
        return sd.theStream.loggedIn;
    }

    public Contact selfContact() {
        return myJid == null ? null : getContact(myJid.toString(), false);
    }

//#ifndef WMUC
    public void multicastConferencePresence(int myStatus, String myMessage, int myPriority) {
        //if (!cf.autoJoinConferences) return; //requested to disable
        if (myStatus == Presence.PRESENCE_INVISIBLE) {
            return; //block multicasting presence invisible
        }
        synchronized (hContacts) {
            int j = hContacts.size();
            for (int i = 0; i < j; i++) {
                Contact c = (Contact) hContacts.elementAt(i);
                if (c.origin != Contact.ORIGIN_GROUPCHAT) {
                    continue;
                }
                if (!((MucContact) c).commonPresence) {
                    continue; // stop if room left manually
                }
                ConferenceGroup confGroup = (ConferenceGroup) c.group;

                if (!confGroup.inRoom) {
                    continue; // don`t reenter to leaved rooms
                }
                Contact myself = confGroup.selfContact;

                if (c.status == Presence.PRESENCE_OFFLINE) {
                    Conference.join(confGroup.name, myself.getJid().toString(), confGroup.password, myself.nick, 20);
                    continue;
                }
                Presence presence = new Presence(myStatus, myPriority, myMessage, null);
                presence.setTo(myself.jid.getBare());
                sd.theStream.send(presence);
            }
        }
    }
//#endif

    public void sendPresence(String to, String type, JabberDataBlock child, boolean conference) { //voffk: todo: check it!
        JabberDataBlock presence = new Presence(to, type);

        if (child != null) {
            presence.addChild(child);

            ExtendedStatus es = sl.getStatus(myStatus);
            switch (myStatus) {
                case Presence.PRESENCE_CHAT:
                    presence.addChild("show", Presence.PRS_CHAT);
                    break;
                case Presence.PRESENCE_AWAY:
                    presence.addChild("show", Presence.PRS_AWAY);
                    break;
                case Presence.PRESENCE_XA:
                    presence.addChild("show", Presence.PRS_XA);
                    break;
                case Presence.PRESENCE_DND:
                    presence.addChild("show", Presence.PRS_DND);
                    break;
            }
            if (es.getPriority() != 0) {
                presence.addChild("priority", Integer.toString(es.getPriority()));
            }
            if (es.getMessage() != null) {
                presence.addChild("status", StringUtils.toExtendedString(es.getMessage()));
            }
        } else if (conference) {
            ExtendedStatus es = sl.getStatus(Presence.PRESENCE_OFFLINE);
            if (es.getMessage() != null) {
                presence.addChild("status", StringUtils.toExtendedString(es.getMessage()));
            }
        }

        sd.theStream.send(presence);
    }

    public void doSubscribe(Contact c) {
        if (c.subscr == null) {
            c.subscr = "none";
        }
        boolean subscribe =
                c.subscr.startsWith("none")
                || c.subscr.startsWith("from");
        if (c.ask_subscribe) {
            subscribe = false;
        }

        boolean subscribed =
                c.subscr.startsWith("none")
                || c.subscr.startsWith("to");
        //getMessage(cursor).messageType==Msg.MESSAGE_TYPE_AUTH;

        String to = (JidUtils.isTransport(c.jid)) ? c.getJid().toString() : c.jid.getBare();

        if (subscribed) {
            sendPresence(to, "subscribed", null, false);
        }
        if (subscribe) {
            sendPresence(to, "subscribe", null, false);
        }
    }

    public void sendMessage(Contact to, String id, final String body, final String subject, String composingState) {
//#ifdef AUTOSTATUS
//#         AutoStatus.getInstance().userActivity(Config.AWAY_MESSAGE);
//#endif
        try {
//#ifndef WMUC
            boolean groupchat = to.origin == Contact.ORIGIN_GROUPCHAT;
//#else 
//#           boolean groupchat=false;  
//#endif

            Message message = new Message(
                    to.getJid().toString(),
                    body,
                    subject,
                    groupchat);
            message.setAttribute("id", id);
            if (groupchat && body == null && subject == null) {
                return;
            }

            if (composingState != null) {
                message.addChildNs(composingState, Message.NS_CHATSTATES);
            }


            if (!groupchat) {
                if (body != null) {
                    if (cf.eventDelivery) {
                        message.addChildNs("request", Message.NS_RECEIPTS);
                    }
                }
            }

            sd.theStream.send(message);
            lastMessageTime = Time.utcTimeMillis();
            playNotify(SOUND_OUTGOING);
        } catch (Exception e) {
        }
    }

    public void sendDeliveryMessage(Contact c, String id) {
        if (!cf.eventDelivery) {
            return;
        }
        if (myStatus == Presence.PRESENCE_INVISIBLE) {
            return;
        }
        Message message = new Message(c.jid.toString());
        // FIXME: no need to send <received /> to forwarded messages
        //xep-0184
        message.setAttribute("id", id); // FIXME: should be new id by XEP-0184 version 1.1
        message.addChildNs("received", Message.NS_RECEIPTS).setAttribute("id", id);
        sd.theStream.send(message);
    }
    private Vector vCardQueue;

    public void resolveNicknames(String transport) {
        vCardQueue = null;
        vCardQueue = new Vector();
        synchronized (hContacts) {
            int j = hContacts.size();
            for (int i = 0; i < j; i++) {
                Contact k = (Contact) hContacts.elementAt(i);
                if (JidUtils.isTransport(k.jid)) {
                    continue;
                }
                int grpType = k.getGroupType();
                if (k.jid.getServer().equals(transport) && k.nick == null && (grpType == Groups.TYPE_COMMON || grpType == Groups.TYPE_NO_GROUP)) {
                    vCardQueue.addElement(IqVCard.query(k.getJid().toString(), "nickvc" + k.jid.getBare()));
                }
            }
        }
        setQuerySign(true);
        sendVCardReq();

    }

    public void sendVCardReq() {
        querysign = false;
        if (vCardQueue != null) {
            if (!vCardQueue.isEmpty()) {
                JabberDataBlock req = (JabberDataBlock) vCardQueue.lastElement();
                vCardQueue.removeElement(req);
                //System.out.println(k.nick);
                sd.theStream.send(req);
                querysign = true;
            }
        }
        updateMainBar();
    }

//#if CHANGE_TRANSPORT
//#     public void contactChangeTransport(String srcTransport, String dstTransport) { //voffk
//#         setQuerySign(true);
//#         int j = hContacts.size();
//#         for (int i = 0; i < j; i++) {
//#             Contact k = (Contact) hContacts.elementAt(i);
//#             if (JidUtils.isTransport(k.jid)) {
//#                 continue;
//#             }
//#             int grpType = k.getGroupType();
//#             if (k.jid.getServer().equals(srcTransport)
//#                     && (grpType == Groups.TYPE_COMMON || grpType == Groups.TYPE_NO_GROUP
//#                     || grpType == Groups.TYPE_VISIBLE || grpType == Groups.TYPE_VIP
//#                     || grpType == Groups.TYPE_IGNORE)) {
//#                 String jid = k.getJid().toString();
//#                 k.jid = new Jid(StringUtils.stringReplace(jid, srcTransport, dstTransport));
//#                 storeContact(k, true); //new contact addition
//#                 try {
//#                     Thread.sleep(300);
//#                 } catch (Exception ex) {
//#                 }
//#                 deleteContact(k); //old contact deletion
//#             }
//#         }
//#         setQuerySign(false);
//#     }
//#endif

    public void loginFailed(String error) {
        myStatus = Presence.PRESENCE_OFFLINE;
        setProgress(SR.MS_LOGIN_FAILED, 100);

        errorLog(error);

        try {
            sd.theStream.close();
        } catch (Exception e) {
            //e.printStackTrace();
        }

        doReconnect = false;
        setQuerySign(false);
        redraw();
    }

    public void loginSuccess() {
        sd.theStream.addBlockListener(new PresenceDispatcher());
        sd.theStream.addBlockListener(new RosterDispatcher());
        sd.theStream.addBlockListener(new EntityCaps());
        sd.theStream.addBlockListener(new IqVCard());

        sd.theStream.addBlockListener(new IqPing());
        sd.theStream.addBlockListener(new IqVersionReply());
        sd.theStream.startKeepAliveTask(); //enable keep-alive packets

        sd.theStream.loggedIn = true;
        currentReconnect = 0;

        sd.theStream.addBlockListener(new IqLast());
        sd.theStream.addBlockListener(new IqTimeReply());
        sd.theStream.addBlockListener(new RosterXListener());
//#ifdef ADHOC
//#         if (cf.adhoc) {
//#             IQCommands.getInstance().addBlockListener();
//#         }
//#endif

//#ifdef PEP
//#         if (cf.sndrcvmood) {
//#             PepListener.getInstance().addBlockListener();
//#         }
//#endif
//#if SASL_XGOOGLETOKEN
//#         if (StaticData.getInstance().account.isGoogle) {
//#             sd.theStream.addBlockListener(new IqGmail());
//#         }
//#endif
//#if FILE_TRANSFER
//#         if (cf.fileTransfer) // enable File transfers
//#         {
//#             TransferDispatcher.getInstance().addBlockListener();
//#         }
//#endif

//#ifdef CAPTCHA
//#         sd.theStream.addBlockListener(new Captcha());
//#endif

        playNotify(SOUND_CONNECTED);
        if (doReconnect) {
            querysign = doReconnect = false;
            sendPresence(myStatus, null);
            return;
        }
        //
        //theStream.enableRosterNotify(true); //voffk
        rpercent = 50;

        if (sd.account.mucOnly) {
            setProgress(SR.MS_CONNECTED, 100);
            show();
            try {
                reEnumRoster();
            } catch (Exception e) {
            }

            setQuerySign(false);
            doReconnect = false;
//#ifndef WMUC            
            //query bookmarks
            sd.theStream.addBlockListener(new BookmarkQuery(BookmarkQuery.LOAD));
//#endif            
        } else {
            JabberDataBlock qr = RosterDispatcher.QueryRoster();
            setProgress(SR.MS_ROSTER_REQUEST, 49);
            sd.theStream.send(qr);
            show();
        }
//#ifdef AUTOSTATUS
//#         if ((Config.autoAwayType != Config.AWAY_OFF) && Config.autoAwayType != Config.AWAY_LOCK) {
//#             AutoStatus.getInstance().start();
//#         }
//#endif
//#ifndef WMUC
        //query bookmarks
        sd.theStream.addBlockListener(new BookmarkQuery(BookmarkQuery.LOAD));
//#endif
    }
    
    public void loadAccount(boolean launch, int accountIndex) {
        Account a = sd.account = AccountStorage.createFromStorage(accountIndex);
        if (a != null) {
            if (launch) {
                logoff(null);
                resetRoster();
                int loginstatus = Config.getInstance().loginstatus;
                if (loginstatus >= Presence.PRESENCE_OFFLINE) {
                    sendPresence(Presence.PRESENCE_INVISIBLE, null);
                } else {
                    sendPresence(loginstatus, null);
                }
            }
        }
    }

    public void bindResource(String myJid) {
        Contact self = selfContact();
        self.jid = this.myJid = new Jid(myJid);
    }
    
//#ifdef CLIENTS_ICONS
//# 
//#     public void getClientIcon(Contact c, String data) {
//#         ClientsIconsData.processData(c, data);
//#     }
//#endif

//#ifdef POPUPS
//#     boolean showWobbler(Contact c) {
//#         if (!cf.popUps) {
//#             return false;
//#         }
//#         if (activeContact == null) {
//#             return true;
//#         }
//#         return (!c.equals(activeContact));
//#     }
//#endif

//#ifdef FILE_TRANSFER
//#     public void addFileQuery(String from, String message) {
//#         Contact c = getContact(from, true);
//#         c.fileQuery = true;
//#         messageStore(c, new Msg(Msg.MESSAGE_TYPE_FILE_REQ, from, SR.MS_FILE, message));
//#     }
//#endif

    public void messageStore(Contact c, Msg message) {
        if (c == null) {
            return;
        }

        c.addMessage(message);

        boolean autorespond = false;
//#ifdef RUNNING_MESSAGE
//#         if (message.messageType==Msg.MESSAGE_TYPE_IN)
//#             setTicker(c, message.body);
//#endif

        if (countNewMsgs()) {
            reEnumRoster();
        }
        if (cf.autoScroll) {
            VirtualList list = VirtualCanvas.getInstance().getList();
            if (list instanceof ContactMessageList) {
                if (((ContactMessageList) list).contact.compare(c) == 0) {
                    if (c.cml == null) {
                        return;
                    }
                    if (c.cml.on_end || message.messageType == Msg.MESSAGE_TYPE_OUT) {
                        list.moveCursorEnd();
                    }
                }
            }
        }

        //TODO: clear unread flag if not-in-list IS HIDDEN

        if (c.getGroupType() == Groups.TYPE_IGNORE) {
            return;    // no signalling/focus on ignore
        }
//#ifdef POPUPS
//#         if (cf.popUps) {
//#             if (message.messageType == Msg.MESSAGE_TYPE_AUTH && showWobbler(c)) {
//#                 setWobble(2, c.getJid().toString(), message.from + "\n" + message.body);
//#             }
//#         }
//#endif

        if (cf.popupFromMinimized) {
//#ifdef SYSTEM_NOTIFY
//#             Notificator notify = null;
//#if !(android)
//#             if (Config.getInstance().sonyJava >= 750) // prevent NoClassDefFoundError on some phones
//#endif            
//#             {
//#                 notify = Notification.getNotificator();
//#             }
//#endif
            if (AlertCustomize.getInstance().vibrateOnlyHighlited) {
                if (message.highlite) {
//#ifdef SYSTEM_NOTIFY
//#                     if (notify != null) {
//#                         notify.sendNotify(message.from, message.body);
//#                     } else 
//#endif
                    {
                        BombusMod.getInstance().hideApp(false);
                    }
                }
            } else {
//#ifdef SYSTEM_NOTIFY
//#                 if (notify != null) {
//#                     notify.sendNotify(message.from, message.body);
//#                 } else 
//#endif
                {
                    BombusMod.getInstance().hideApp(false);
                }
            }
        }

        if (message.highlite) {
            playNotify(SOUND_FOR_ME);
//#ifdef POPUPS
//#             if (showWobbler(c)) {
//#                 String body = (c.origin == Contact.ORIGIN_GROUPCHAT && cf.showNickNames)
//#                         ? message.from + ":\n" + message.body
//#                         : message.body;
//#                 setWobble(2, c.getJid().toString(), body);
//#             }
//#endif
            autorespond = true;
//#ifdef LIGHT_CONFIG        
//#             CustomLight.message();
//#endif 
            if (cf.autoFocus) {
                focusToContact(c, false);
            }
        } else if (message.messageType == Msg.MESSAGE_TYPE_IN || message.messageType == Msg.MESSAGE_TYPE_HEADLINE) {
            if (c.origin < Contact.ORIGIN_GROUPCHAT) {
//#ifndef WMUC
                if (!(c instanceof MucContact)) 
//#endif
//#ifdef POPUPS
//#                 {
//#                     if (showWobbler(c)) {
//#                         setWobble(2, c.getJid().toString(), c.toString() + ": " + message.body);
//#                         autorespond = true;
//#                     }
//#                 }
//#endif
                if (c.group.type == Groups.TYPE_VIP) {
                    playNotify(SOUND_FOR_VIP);
                    autorespond = true;
//#ifdef LIGHT_CONFIG        
//#                     CustomLight.message();
//#endif 
                    if (cf.autoFocus) {
                        focusToContact(c, false);
                    }
                } else {
                    playNotify(SOUND_MESSAGE);
                    autorespond = true;
//#ifdef LIGHT_CONFIG        
//#                     CustomLight.message();
//#endif 
                    if (cf.autoFocus) {
                        focusToContact(c, false);
                    }
                }
            } 
//#ifndef WMUC
            else {
                if (c.origin != Contact.ORIGIN_GROUPCHAT && c instanceof MucContact) {
                    playNotify(SOUND_MESSAGE); //private message
                    autorespond = true;
//#ifdef LIGHT_CONFIG        
//#                     CustomLight.message();
//#endif 
                    if (cf.autoFocus) {
                        focusToContact(c, false);
                    }
                } else {
                    playNotify(SOUND_FOR_CONFERENCE);
                }
            }
//#endif
        }

        if (c.origin == Contact.ORIGIN_GROUPCHAT || JidUtils.isTransport(c.jid) 
                || c.getGroupType() == Groups.TYPE_TRANSP 
                || c.getGroupType() == Groups.TYPE_SEARCH_RESULT 
                || c.getGroupType() == Groups.TYPE_SELF) {
            autorespond = false;
        }

        if (message.messageType != Msg.MESSAGE_TYPE_IN) {
            autorespond = false;
        }

        if (!c.autoresponded && autorespond) {
            ExtendedStatus es = sl.getStatus(myStatus);
            if (es.getAutoRespond()) {
//#if DEBUG
//#                 System.out.println(SR.MS_AUTORESPOND+" "+c.getJid());
//#endif

                Message autoMessage = new Message(
                        c.getJid().toString(),
                        es.getAutoRespondMessage(),
                        SR.MS_AUTORESPOND,
                        false);
                sd.theStream.send(autoMessage);
                c.autoresponded = true;

                c.addMessage(new Msg(Msg.MESSAGE_TYPE_SYSTEM, "local", SR.MS_AUTORESPOND, ""));
            }
        }
    }

    public void blockNotify(int event, long ms) {
        if (!notifyReady(-111)) {
            return;
        }
        blockNotifyEvent = event;
        notifyReadyTime = System.currentTimeMillis() + ms;
    }

    public boolean notifyReady(int event) {
        if ((blockNotifyEvent == event
                || (blockNotifyEvent == -111 && event <= 7))
                && System.currentTimeMillis() < notifyReadyTime) {
            return false;
        } else {
            return true;
        }
    }

    public void playNotify(int event) {
        if (!notifyReady(event)) {
            return;
        }
//#if DEBUG        
//#         System.out.println("event: "+event);
//#endif
        AlertCustomize ac = AlertCustomize.getInstance();

        int volume = ac.soundVol;
        int vibraLen = cf.vibraLen;
        String type, message;
        //boolean flashBackLight=ac.flashBackLight;

        switch (event) {
            case 0: //online
            case 1: //chat
                if (cf.notifySound) {
                    message = ac.soundOnline;
                    type = ac.soundOnlineType;
                } else {
                    message = null;
                    type = null;
                }
                vibraLen = 0;
                //flashBackLight=false;
                break;
            case 5: //offline
                message = ac.soundOffline;
                type = ac.soundOfflineType;
                vibraLen = 0;
                //flashBackLight=false;
                break;
            case SOUND_FOR_VIP: //VIP
                message = ac.soundVIP;
                type = ac.soundVIPType;
                break;
            case SOUND_MESSAGE: //message
                message = ac.messagesnd;
                type = ac.messageSndType;
                break;
            case SOUND_FOR_CONFERENCE: //conference
                message = ac.soundConference;
                type = ac.soundConferenceType;
                if (ac.vibrateOnlyHighlited) {
                    vibraLen = 0;
                }
                break;
            case SOUND_FOR_ME: //message for you
                message = ac.soundForYou;
                type = ac.soundForYouType;
                break;
            case SOUND_CONNECTED: //startup
                message = ac.soundStartUp;
                type = ac.soundStartUpType;
                vibraLen = 0;
                //flashBackLight=false;
                break;
            case SOUND_COMPOSING: //composing
                message = ac.soundComposing;
                type = ac.soundComposingType;
                vibraLen = 0;
                //flashBackLight=false;
                break;
            case SOUND_OUTGOING: //Outgoing
                message = ac.soundOutgoing;
                type = ac.soundOutgoingType;
                vibraLen = 0;
                //flashBackLight=false;
                break;
            default:
                message = "";
                type = "none";
                vibraLen = 0;
                //flashBackLight=false;
                break;
        }

        int profile = cf.profile;

        EventNotify notify = null;

        switch (profile) {
            //display   fileType   soundName   volume      vibrate
            case AlertProfile.ALL:
                notify = new EventNotify(type, message, volume, vibraLen);
                break;
            case AlertProfile.NONE:
                notify = new EventNotify(null, null, volume, 0);
                break;
            case AlertProfile.VIBRA:
                notify = new EventNotify(null, null, volume, vibraLen);
                break;
            case AlertProfile.SOUND:
                notify = new EventNotify(type, message, volume, 0);
                break;
        }
        if (notify != null) {
            notify.startNotify();
        }
        blockNotify(event, 2000);
    }

    public void focusToContact(final Contact c, boolean force) {
        Group g = c.group;
        if (g.collapsed) {
            g.collapsed = false;
            reEnumerator.queueEnum(c, force);
        }
        int index = itemsList.indexOf(c);
        if (index >= 0) {
            moveCursorTo(index);
        }
    }           

    public void askReconnect(final Exception e) {
        //SplashScreen.getInstance().close();
        try {
            sd.theStream.close(); // sends </stream:stream> and closes socket
        } catch (Exception e1) { /*
             * e1.printStackTrace();
             */ }
        sd.theStream = null;
        setProgress(SR.MS_FAILED, 100);
        doReconnect = false;
        myStatus = Presence.PRESENCE_OFFLINE;
        setQuerySign(false);
        redraw();

        StringBuffer error = new StringBuffer();
        if (e.getClass().getName().indexOf("java.lang.Exception") < 0) {
            error.append(e.getClass().getName());
            error.append('\n');
        }
        if (e.getMessage() != null) {
            error.append(e.getMessage());
        }

        if (currentReconnect >= cf.reconnectCount) {
            errorLog(error.toString());
            return;
        }

        currentReconnect++;

        String topBar = "(" + currentReconnect + "/" + cf.reconnectCount + ") Reconnecting";
        errorLog(topBar + "\n" + error.toString());

        setRotator();
        VirtualCanvas.getInstance().rw = new ReconnectWindow();
        VirtualCanvas.getInstance().rw.startReconnect();
    }

    public void doReconnect() {
        setProgress(SR.MS_DISCONNECTED, 0);

        logoff(null);

        try {
            sendPresence(lastOnlineStatus, null);
        } catch (Exception e2) {
        }
    }

    public void eventOk() {
        super.eventOk();
        if (getFocusedMsgList() == null) {
            cleanupGroup((Group) getFocusedObject());
            reEnumRoster();
        }
    }

    public void eventLongOk() {
        super.eventLongOk();
        Object o = getFocusedObject();
        if ((!(o instanceof Contact)) 
//#ifndef WMUC                
                && (!(o instanceof MucContact))
//#endif
                ) {
            cmdActions();
        } else {
//#ifndef WMUC
//#ifdef POPUPS
//#             showInfo();
//#endif
//#endif
        }
    }

    private ContactMessageList getFocusedMsgList() {
        Object e = getFocusedObject();
        if (e instanceof Contact) {
            return ((Contact) e).getMsgList();
        }

        return null;
    }

    public void messageEditResume() {
        if (!isLoggedIn()) {
            return;
        }

        ContactMessageList pview = getFocusedMsgList();
        if (pview != null) {
            Contact c = (Contact) getFocusedObject();
            me = null;
            me = new MessageEdit(pview, c, c.msgSuspended);
            me.show();
            c.msgSuspended = null;
        }
    }

//#ifndef WMUC
    public void kickFocused() {
        Object focusedObject = getFocusedObject();

        if (!(focusedObject instanceof MucContact)) {
            return;
        }

        MucContact c = (MucContact) focusedObject;

        if (c.origin == Contact.ORIGIN_GROUPCHAT || c.roleCode == MucContact.ROLE_MODERATOR) {
            return;
        }

        ConferenceGroup mucGrp = (ConferenceGroup) c.group;
        if (mucGrp.selfContact.roleCode == MucContact.ROLE_MODERATOR) {
            String myNick = mucGrp.selfContact.getName();
            new ConferenceQuickPrivelegeModify(c, ConferenceQuickPrivelegeModify.KICK, myNick);
        }
    }
//#endif 

    public void collapseAllGroup() {
        for (Enumeration e = groups.elements(); e.hasMoreElements();) {
            Group grp = (Group) e.nextElement();
            grp.collapsed = true;
        }
        reEnumRoster();
    }

    public void moveFocusToGroup(int direction) {
        if (getItemCount() > 0) {
            int newpos = searchGroup(direction);
            if (newpos > -1) {
                moveCursorTo(newpos);
                setRotator();
            }
        }
    }

    public void blockScreen() {
//#ifdef AUTOSTATUS
//#         AutoStatus.getInstance().appLocked();
//#endif
        new SplashScreen(mainbar, VirtualCanvas.keyLock);
    }

    public void toggleVibra() {
        // swap profiles
        int profile = cf.profile;
        cf.profile = (profile == AlertProfile.VIBRA) ? cf.lastProfile : AlertProfile.VIBRA;
        cf.lastProfile = profile;

        updateMainBar();
        redraw(); // Need?
    }

    public void changeMotoBacklightState() {
        if (!cf.ghostMotor) {
            return;
        }

        // backlight management
        blState = (blState == 1) ? Integer.MAX_VALUE : 1;
        midlet.BombusMod.getInstance().getDisplay().flashBacklight(blState);
    }

    public void focusToNextUnreaded() {
        if (getItemCount() == 0) {
            return;
        }
        synchronized (hContacts) {
            int j = hContacts.size();
            for (int i = 0; i < j; i++) {
                Contact c = (Contact) hContacts.elementAt(i);
                c.setIncoming(Contact.INC_NONE);
            }
        }

        redraw();

        if (messageCount == 0) {
            return;
        }

        Object atcursor = getFocusedObject();
        Contact c = (Contact) ((atcursor instanceof Contact) ? atcursor : hContacts.firstElement());
        Enumeration i = hContacts.elements();

        int pass = 0; //
        while (pass < 2) {
            if (!i.hasMoreElements()) {
                i = hContacts.elements();
            }
            Contact p = (Contact) i.nextElement();
            if (pass == 1) {
                if (p.getNewMsgsCount() > 0) {
                    focusToContact(p, true);
                    setRotator();
                    break;
                }
            }
            if (p == c) {
                pass++;
            }
        }
    }

    public void logoff(String mess) {
        if (isLoggedIn()) {
            try {
                if (mess == null) {
                    mess = sl.getStatus(Presence.PRESENCE_OFFLINE).getMessage();
                }
                sendPresence(Presence.PRESENCE_OFFLINE, mess);
                sd.theStream.loggedIn = false;
            } catch (Exception e) {
            }
        }
//#ifdef STATS
//#         Stats.getInstance().saveToStorage(false);
//#endif
    }

    public void quit() {
//#ifdef AUTOSTATUS
//#         AutoStatus.getInstance().stop();
//#endif
        logoff(null);
        try {
            Thread.sleep(250L);
        } catch (InterruptedException ex) {
        }
        BombusMod.getInstance().notifyDestroyed();
    }

    public void menuAction(MenuCommand c, VirtualList d) {
        if (c == cmdMinimize) {
            cmdMinimize();
        } else if (c == cmdActiveContacts) {
            cmdActiveContacts();
        } else if (c == cmdAccount) {
            cmdAccount();
        } else if (c == cmdStatus) {
            cmdStatus();
        } else if (c == cmdAlert) {
            cmdAlert();
        }
//#ifdef ARCHIVE
//#         else if (c == cmdArchive) {
//#             cmdArchive();
//#         }
//#endif
        else if (c == cmdInfo) {
            cmdInfo();
        } else if (c == cmdTools) {
            cmdTools();
        } else if (c == cmdCleanAllMessages) {
            cmdCleanAllMessages();
        } 
//#ifndef WMUC
        else if (c == cmdConference) {
            cmdConference();
        } 
//#endif
        else if (c == cmdQuit) {
            cmdQuit();
        } else if (c == cmdAdd) {
            cmdAdd();
        }
        super.menuAction(c, d);
    }
//menu actions

    public void cmdQuit() {
        if (cf.queryExit) {
            new AlertBox(SR.MS_QUIT_ASK, SR.MS_SURE_QUIT) {

                public void yes() {
                    quit();
                }

                public void no() {
                }
            };
        } else {
            quit();
        }
    }

    public void cmdMinimize() {
//#if android
//#         BombusModActivity.getInstance().minimizeApp();
//#endif        
        if (cf.allowMinimize) {
            BombusMod.getInstance().hideApp(true);
        } else if (phoneManufacturer == Config.SIEMENS2) {
            new SieNatMenu(this);
            /*
             * try { // SIEMENS: MYMENU call. Possible Main Menu for capable
             * phones
             * BombusMod.getInstance().platformRequest("native:ELSE_STR_MYMENU");
             * } catch (Exception e) { }
             */
        } else if (phoneManufacturer == Config.SIEMENS) {
            try {
                // SIEMENS-NSG: MYMENU call. Possible Native Menu for capable phones
                BombusMod.getInstance().platformRequest("native:NAT_MAIN_MENU");
            } catch (Exception e) {
            }
        }
    }

    public void cmdActiveContacts() {
        new ActiveContacts(null);
    }

    public void cmdAccount() {
        new AccountSelect(false).show();
    }

    public void cmdStatus() {
        currentReconnect = 0;
        new StatusSelect(null);
    }

    public void cmdAlert() {
        new AlertProfile();
    }
//#ifdef ARCHIVE
//# 
//#     public void cmdArchive() {
//#         new ArchiveList(-1, 1, null);
//#     }
//#endif

    public void cmdInfo() {
        new Info.InfoWindow();
    }

    public void cmdTools() {
        new RosterToolsMenu();
    }
//#ifdef POPUPS
//# 
//#     public void cmdClearPopups() {
//#         PopUp.getInstance().clear();
//#     }
//#endif
//#ifndef WMUC

    public void cmdConference() {
        if (isLoggedIn()) {
            new Bookmarks(null);
        }
    }
//#endif

    public void cmdActions() {
        if (isLoggedIn()) {
            try {
                new RosterItemActions(getFocusedObject());
            } catch (Exception ex) {
            }
        }
    }

    public void cmdAdd() {
        if (isLoggedIn()) {
            Object o = getFocusedObject();
            Contact cn = null;
            if (o instanceof Contact) {
                cn = (Contact) o;
                if (cn.getGroupType() != Groups.TYPE_NOT_IN_LIST && cn.getGroupType() != Groups.TYPE_SEARCH_RESULT) {
                    cn = null;
                }
            }
//#ifndef WMUC
            if (o instanceof MucContact) {
                cn = (Contact) o;
            }
//#endif
            new ContactEdit(cn);
        }
    }

//#ifndef WMUC
    public void reEnterRoom(Group group) {
        ConferenceGroup confGroup = (ConferenceGroup) group;
        String confJid = confGroup.selfContact.getJid().toString();
        String name = confGroup.name;
        new ConferenceForm(name, confJid, confGroup.password, false);
    }

    public void leaveRoom(Group group) {
        ConferenceGroup confGroup = (ConferenceGroup) group;
        Contact myself = confGroup.selfContact;
        confGroup.confContact.commonPresence = false; //disable reenter after reconnect
        sendPresence(myself.getJid().toString(), "unavailable", null, true);

        confGroup.inRoom = false;
        roomOffline(group);
    }

    public void roomOffline(final Group group) {
        int j = hContacts.size();
        for (int i = 0; i < j; i++) {
            Contact contact = (Contact) hContacts.elementAt(i);
            if (contact.group == group) {
                contact.setStatus(Presence.PRESENCE_OFFLINE);
            }
        }
    }
//#endif    

    private int searchGroup(int direction) {
        int newpos = -1;
        synchronized (itemsList) {
            int size = itemsList.size();
            int pos = cursor;
            int count = size;
            try {
                while (count > 0) {
                    pos += direction;
                    if (pos < 0) {
                        pos = size - 1;
                    }
                    if (pos >= size) {
                        pos = 0;
                    }
                    if (itemsList.elementAt(pos) instanceof Group) {
                        break;
                    }
                }
            } catch (Exception e) {
            }
            newpos = pos;
        }
        return newpos;
    }

    public void searchActiveContact(int direction) {
        Vector activeContacts = new Vector();
        int nowContact = -1, contacts = -1, currentContact = -1;
        synchronized (hContacts) {
            int j = hContacts.size();
            for (int i = 0; i < j; i++) {
                Contact c = (Contact) hContacts.elementAt(i);
                if (c.active()) {
                    activeContacts.addElement(c);
                    contacts = contacts + 1;
                    if (c == activeContact) {
                        nowContact = contacts;
                        currentContact = contacts;
                    }
                }
            }
        }

        int size = activeContacts.size();

        if (size == 0) {
            return;
        }

        try {
            nowContact += direction;
            if (nowContact < 0) {
                nowContact = size - 1;
            }
            if (nowContact >= size) {
                nowContact = 0;
            }

            if (currentContact == nowContact) {
                return;
            }

            Contact c = (Contact) activeContacts.elementAt(nowContact);
            c.getMsgList();
        } catch (Exception e) {
        }
    }

    public void deleteContact(Contact c) {
        synchronized (hContacts) {
            int j = hContacts.size();
            for (int i = 0; i < j; i++) {
                Contact c2 = (Contact) hContacts.elementAt(i);
                if (c.jid.equals(c2.jid, false)) {
                    c2.setStatus(Presence.PRESENCE_TRASH);
                    c2.offline_type = Presence.PRESENCE_TRASH;
                }
            }            

            if (c.getGroupType() == Groups.TYPE_NOT_IN_LIST) {
                hContacts.removeElement(c);
                countNewMsgs();
                reEnumRoster();
            } else {
                sd.theStream.send(RosterDispatcher.QueryRoster(c.jid, null, null, "remove"));
                if (JidUtils.isTransport(c.jid)) {
                    // for buggy transports
                    sendPresence(c.jid.toString(), "unsubscribe", null, false);
                    sendPresence(c.jid.toString(), "unsubscribed", null, false);
                }
                sendPresence(c.jid.getBare(), "unsubscribe", null, false);
                sendPresence(c.jid.getBare(), "unsubscribed", null, false);
                }
            }
            if (JidUtils.isTransport(c.jid)) {
                // double-check for empty jid or our server jid
                if (c.jid.getBare().length() == 0) {
                    return;
                }
                if (c.jid.getBare().equals(myJid.getServer())) {
                    return;
                }
                // automatically remove registration
                JabberDataBlock unreg = new Iq(c.jid.getBare(), Iq.TYPE_SET, "unreg" + System.currentTimeMillis());
                JabberDataBlock query = unreg.addChildNs("query", "jabber:iq:register");
                query.addChild("remove", null);
                sd.theStream.send(unreg);
                // and for buggy transports
                JabberDataBlock unreg2 = new Iq(c.jid.toString(), Iq.TYPE_SET, "unreg" + System.currentTimeMillis());
                JabberDataBlock query2 = unreg2.addChildNs("query", "jabber:iq:register");
                query2.addChild("remove", null);
                sd.theStream.send(unreg2);
            }
        }

    public void setQuerySign(boolean requestState) {
        querysign = requestState;
        updateMainBar();
    }

    public void storeContact(Contact c, boolean askSubscribe) {
        sd.theStream.send(RosterDispatcher.QueryRoster(c.getJid(), c.nick, (c.group == null || c.getGroupType() == Groups.TYPE_NOT_IN_LIST) ? SR.MS_GENERAL : c.group.name, null));
        if (askSubscribe) {
            doSubscribe(c);
        }
    }

    public void loginMessage(String msg, int pos) {
        setProgress(msg, pos);
    }

    public void deleteGroup(Group deleteGroup) {
        synchronized (hContacts) {
            int j = hContacts.size();
            for (int i = 0; i < j; i++) {
                Contact cr = (Contact) hContacts.elementAt(i);
                if (cr.group == deleteGroup) {
                    deleteContact(cr);
                }
            }
        }
    }

    public void destroyView() {
        cmdMinimize();
    }

    public void keyGreen() {
        messageEditResume();
    }

    public void keyClear() {
        if (isLoggedIn()) {
            final Contact c = (Contact) getFocusedObject();
            try {
                boolean isContact = (getFocusedObject() instanceof Contact);
//#ifndef WMUC
                boolean isMucContact = (getFocusedObject() instanceof MucContact);
//#else
//#                 boolean isMucContact=false;
//#endif
                if (isContact && !isMucContact) {
                    new AlertBox(SR.MS_DELETE_ASK, c.getNickJid()) {

                        public void yes() {
                            deleteContact(c);
                        }

                        public void no() {
                        }
                    };
                } 
//#ifndef WMUC
                else if (isContact && isMucContact && c.origin != Contact.ORIGIN_GROUPCHAT) {
                    ConferenceGroup mucGrp = (ConferenceGroup) c.group;
                    if (mucGrp.selfContact.roleCode == MucContact.ROLE_MODERATOR) {
                        if (((MucContact) c).roleCode < MucContact.ROLE_MODERATOR) {
                            String myNick = mucGrp.selfContact.getName();
                            MucContact mc = (MucContact) c;
                            new ConferenceQuickPrivelegeModify(mc, ConferenceQuickPrivelegeModify.KICK, myNick);
                        }
                    }
                }
//#endif
            } catch (Exception e) { /*
                 * NullPointerException
                 */ }
        }
    }

    public void toggleOfflines() {
        cf.showOfflineContacts = !cf.showOfflineContacts;
        sd.roster.reEnumRoster();
    }

    public String selectCommand() {
        return SR.MS_ITEM_ACTIONS;
    }

    public void selectPressed() {
        cmdActions();
    }

    public void doKeyAction(int keyCode) {
        switch (keyCode) {
            case 1:
                if (cf.collapsedGroups) {
                    collapseAllGroup();
                }
                break; // also make super action
            case 3:
                moveFocusToGroup(-1);
                return;
            case 9:
                moveFocusToGroup(1);
                return;
            case 0:
                focusToNextUnreaded();
                return;
            case VirtualCanvas._KEY_POUND:
                changeMotoBacklightState();
                break;
        }
        super.doKeyAction(keyCode);
//#ifdef LIGHT_CONFIG        
//#         CustomLight.keyPressed();
//#endif  
    }

    public boolean longKey(int keyCode) {
        switch (keyCode) {
            case 0:
                toggleOfflines();
                return true;
            case 1:
//#ifndef WMUC                
                if (isLoggedIn()) {
                    new Bookmarks(null);
                }
//#endif                
                return true;
            case 3:
                cmdActiveContacts();
                return true;
            case 6:
                Config.fullscreen = !Config.fullscreen;
                VirtualCanvas.getInstance().setFullScreenMode(Config.fullscreen);
                return true;
            case 4:
                new ConfigForm();
                return true;
            case 7:
                new RosterToolsMenu();
                return true;
            case 9:
                cmdMinimize();
                return true;
            case VirtualCanvas._KEY_STAR:
                if (cf.phoneManufacturer == Config.SIEMENS || cf.phoneManufacturer == Config.SIEMENS2) {
                    toggleVibra();
                } else {
                    blockScreen();
                }
                return true;
            case VirtualCanvas._KEY_POUND:
                if (cf.phoneManufacturer == Config.SIEMENS || cf.phoneManufacturer == Config.SIEMENS2) {
                    blockScreen();
                } else {
                    toggleVibra();
                }
                return true;
        }
//#ifdef LIGHT_CONFIG        
//#         CustomLight.keyPressed();
//#endif 
        return super.longKey(keyCode);
    }

    public boolean doUserKeyAction(int command_id) {
        switch (command_id) {
            case 1:
                new ConfigForm();
                return true;
            case 2:
                cmdCleanAllMessages();
                return true;
            case 3:
                sd.theStream.listener.connectionTerminated(new Exception(SR.MS_SIMULATED_BREAK));
                return true;
//#ifdef POPUPS
//#ifdef STATS
//#             case 4:
//#                 new Statistic.StatsWindow();
//#                 return true;
//#endif
//#endif
            case 5:
                cmdStatus();
                return true;
//#ifdef ARCHIVE
//#             case 7:
//#                 cmdArchive();
//#                 return true;
//#endif
//#ifdef POPUPS
//#             case 11:
//#                 cmdClearPopups();
//#                 return true;
//#endif
            case 13:
                cmdInfo();
                return true;
//#ifdef JUICK
//#             case 18:
//#                 Contact jContact = sd.roster.getMainJuickContact();
//#                 if (jContact != null) {
//#                     focusToContact(jContact, false);
//#                 }
//#                 return true;
//#endif
            case 19:
                System.gc();
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                }
                showTimeTrafficInfo();
                return true;
            case 33:
                if (cf.collapsedGroups) {
                    collapseAllGroup();
                }
                return true;
//#ifndef WMUC
            case 32:
                if (sd.roster.isLoggedIn()) {
                    new Bookmarks(null);
                }
                return true;
//#endif
            case 38:
                cf.showOfflineContacts = !cf.showOfflineContacts;
                reEnumRoster();
                return true;
            case 39:
                new RosterToolsMenu();
                return true;
            case 40:
                cmdMinimize();
                return true;
            case 41:
                focusToNextUnreaded();
                return true;
            case 42:
                moveFocusToGroup(-1);
                return true;
            case 43:
                moveFocusToGroup(1);
                return true;
            case 45:
                toggleVibra();
                return true;
            case 52:
                messageEditResume();
                return true;
            case 47:
                new ActiveContacts(null);
                return true;
//#ifndef WMUC
            case 58:
                kickFocused();
                return true;
//#endif
        }

        return super.doUserKeyAction(command_id);
    }

    public void captionPressed() {
        new ActiveContacts(null);
    }

//#ifdef RUNNING_MESSAGE
//#     void setTicker(Contact c, String message) {
//#         if (cf.notifyWhenMessageType) {
//#             if (me!=null)
//#                 if (me.to==c)
//#                     me.setMyTicker(message);
//#         }
//#     }
//#endif

    /*
     * TODO: debug with collapse group: I get NPE in VirtualList.paint(). public
     * void paint(javax.microedition.lcdui.Graphics g) { try { // NPE and
     * (possibility) ArrayIndexOutOfBoundsException with uncomplete reenum
     * Roster. super.paint(g); } catch (Exception e) { } }
     */
    private class ReEnumerator implements Runnable {

        Thread thread = null;
        int pendingRepaints = 0;
        boolean force;
        Object desiredFocus;

        public synchronized void queueEnum(Object focusTo, boolean force) {
            desiredFocus = focusTo;
            this.force = force;
            queueEnum();
        }

        synchronized public void queueEnum() {
            pendingRepaints++;
            if (thread == null || thread.isAlive() == false) {
                (thread = new Thread(this)).start();
            }
        }

        public synchronized void run() {
//#ifdef PRIVACY
//#             boolean needUpdatePrivacy = false;
//#endif            

            try {
                while (pendingRepaints > 0) {
                    pendingRepaints = 0;

                    int locCursor = cursor;
                    Object focused = (desiredFocus == null) ? getFocusedObject() : desiredFocus;
                    desiredFocus = null;
                    Vector tContacts = new Vector(itemsList.size());

                    groups.resetCounters();

                    synchronized (hContacts) {
                        int j = hContacts.size();
                        for (int i = 0; i < j; i++) {
                            Contact c = (Contact) hContacts.elementAt(i);
                            Group grp = c.group;
                            if (c.group != null) {
                                grp.addContact(c);

//#ifdef PRIVACY
//#                                 if (!sd.account.isGoogle) {
//#                                     if (QuickPrivacy.groupsList == null) {
//#                                         QuickPrivacy.groupsList = new Vector();
//#                                     }
//#                                     if (c.group.type != Groups.TYPE_MUC) {
//#                                         if (!QuickPrivacy.groupsList.contains(c.group.name)) {
//#                                             QuickPrivacy.groupsList.addElement(c.group.name);
//#                                             needUpdatePrivacy = true;
//#                                         }
//#                                     }
//#                                 }
//#endif
                            }
                        }
                    }
//#ifdef PRIVACY                        
//#                     if (!sd.account.isGoogle) {
//#                         if (needUpdatePrivacy && isLoggedIn()) {
//#                             new QuickPrivacy().updateQuickPrivacyList();
//#                         }
//#                     }
//#endif                                                                        
                    // self-contact group
                    Group selfContactGroup = groups.getGroup(Groups.TYPE_SELF);
                    selfContactGroup.visible = (cf.selfContact || selfContactGroup.tonlines > 1 || selfContactGroup.unreadMessages > 0);

                    // hiddens
                    groups.getGroup(Groups.TYPE_IGNORE).visible = cf.ignore;

                    // transports
                    Group transpGroup = groups.getGroup(Groups.TYPE_TRANSP);
                    transpGroup.visible = (cf.showTransports || transpGroup.unreadMessages > 0);

                    // adding groups
                    for (int i = 0; i < groups.getCount(); i++) {
                        groups.addToVector(tContacts, i);
                    }

                    loadItemsFrom(tContacts);

                    //vContacts = tContacts;
                    //tContacts = null;
                    StringBuffer onl = new StringBuffer().append("(").append(groups.getRosterOnline()).append("/").append(groups.getRosterContacts()).append(")");
                    setRosterMainBar(onl.toString());
                    onl = null;

                    if (cursor < 0) {
                        moveCursorTo(0);
                    }

                    if (locCursor == cursor && focused != null) {
                        //  itemsList = vContacts;
                        int c = itemsList.indexOf(focused);
                        if (c >= 0) {
                            moveCursorTo(c);
                        }
                        force = false;
                    }
                    focusedItem(cursor);
                    redraw();
                }
            } catch (Exception e) {
            }
            //thread=null;            
        }
    }
}
