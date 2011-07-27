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
//#ifdef STATS
//# import Statistic.Stats;
//#endif
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

import Menu.MenuCommand;
//#ifdef SYSTEM_NOTIFY
//# import Messages.notification.Notification;
//# import Messages.notification.Notificator;
//#endif
//#ifdef PRIVACY
import PrivacyLists.QuickPrivacy;
//#endif

//#if FILE_TRANSFER
import io.file.transfer.TransferDispatcher;
//#endif

import locale.SR;

import login.LoginListener;

//#ifdef NON_SASL_AUTH
//# import login.NonSASLAuth;
//#endif
import login.SASLAuth;

import midlet.BombusMod;
import ui.controls.AlertBox;
import util.StringUtils;
import com.alsutton.jabber.*;
import com.alsutton.jabber.datablocks.*;
import java.util.*;

import ui.*;
//#ifdef POPUPS
import ui.controls.PopUp;
//#endif
import ui.controls.form.DefForm;
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
import xmpp.extensions.RosterXListener;
import xmpp.extensions.IqVCard;

//#ifdef LIGHT_CONFIG
//# import LightControl.CustomLight;
//#endif

public class Roster
        extends DefForm
        implements
        JabberListener,
        Runnable,
        LoginListener {

    private MenuCommand cmdStatus = new MenuCommand(SR.MS_STATUS_MENU, MenuCommand.SCREEN, 4, RosterIcons.ICON_STATUS);
    private MenuCommand cmdActiveContacts = new MenuCommand(SR.MS_ACTIVE_CONTACTS, MenuCommand.SCREEN, 3, RosterIcons.ICON_ACTIVE);
    private MenuCommand cmdAlert = new MenuCommand(SR.MS_ALERT_PROFILE_CMD, MenuCommand.SCREEN, 8, RosterIcons.ICON_ALERTS);
//#ifndef WMUC
    private MenuCommand cmdConference = new MenuCommand(SR.MS_CONFERENCE, MenuCommand.SCREEN, 10, RosterIcons.ICON_CONFERENCE);
//#endif
//#ifdef ARCHIVE
    private MenuCommand cmdArchive = new MenuCommand(SR.MS_ARCHIVE, MenuCommand.SCREEN, 10, RosterIcons.ICON_ARCHIVE);
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
    public JabberStream theStream;
    public int messageCount;
    int highliteMessageCount;
    public Object transferIcon;
    public final Vector hContacts = new Vector();
    //private Vector vContacts;
    public Groups groups;
    public Vector bookmarks;
    public static MessageEdit me = null;
    private StatusList sl;
    public int myStatus = cf.loginstatus;
    private static String myMessage;
    public static int oldStatus = 0;
    private static int lastOnlineStatus;
    public int currentReconnect = 0;
    public boolean doReconnect = false;
    public boolean querysign = false;
//#ifdef AUTOSTATUS
//#     public static boolean autoAway = false;
//#     public static boolean autoXa = false;
//#endif
//#if SASL_XGOOGLETOKEN
//#     private String token;
//#endif
//#ifdef JUICK
//#     public Vector juickContacts = new Vector();
//#     public int indexMainJuickContact = -1; // Т.е. считаем, что жуйкоконтактов нет вообще.
//#endif
    public long lastMessageTime = Time.utcTimeMillis();
    public static String startTime = Time.dispLocalTime();
    private static long notifyReadyTime = System.currentTimeMillis();
    private static int blockNotifyEvent = -111;
    private int blState = Integer.MAX_VALUE;
    private final static int SOUND_FOR_ME = 500;
    private final static int SOUND_FOR_CONFERENCE = 800;
    private final static int SOUND_MESSAGE = 1000;
    private final static int SOUND_CONNECTED = 777;
    private final static int SOUND_FOR_VIP = 100;
    private final static int SOUND_COMPOSING = 888;
    private final static int SOUND_OUTGOING = 999;

    public Roster() {
        super(null, false);

        //splash = SplashScreen.getInstance();

        sl = StatusList.getInstance();

        // setLight(cf.lightState);

        MainBar mb = new MainBar(4, null, null, false);
        setMainBarItem(mb);
        mb.addRAlign();
        mb.addElement(null);
        mb.addElement(null);
        mb.addElement(null); //ft

        groups = null;
        groups = new Groups();

        /*vContacts=null;
        vContacts=new Vector(); // just for displaying
         */
        updateMainBar();

//#ifdef CLIENTS_ICONS
        ClientsIconsData.getInstance();
//#endif
//#ifdef AUTOSTATUS
//#         AutoStatusTask.getInstance();
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
        addMenuCommand(cmdArchive);
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
        getMainBarItem().setElementAt(s, 3);
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
        //if (cf.firstRun) setWobbler(1, (Contact) null, SR.MS_ENTER_SETTINGS);
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
            setProgress(SR.MS_CONNECT_TO_ + a.getServer(), 30);

            theStream = a.openJabberStream();
            new Thread(theStream).start();
            new Thread(theStream.dispatcher).start();
            setProgress(SR.MS_OPENING_STREAM, 40);
            theStream.setJabberListener(this);
            theStream.initiateStream();
        } catch (Exception e) {
//#ifdef DEBUG
//#             e.printStackTrace();
//#endif
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

            bookmarks = null;
            bookmarks = new Vector();
        }
        if (sd.account != null) {
            myJid = new Jid(sd.account.getJid());
            updateContact(sd.account.getNick(), myJid.getBareJid(), SR.MS_SELF_CONTACT, "self", false);
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
//#ifdef AUTOSTATUS
//#         if (cf.autoAwayType == Config.AWAY_IDLE) {
//#             if (!AutoStatusTask.getInstance().isAwayTimerSet()) {
//#                 if (!autoAway) {
//#                     AutoStatusTask.getInstance().setTimeEvent(cf.autoAwayDelay * 60 * 1000);
//#                 }
//#             }
//#         }
//#endif
    }

    public void setEventIcon(Object icon) {
        transferIcon = icon;
        getMainBarItem().setElementAt(icon, 7);
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
        MainBar mb = (MainBar) getMainBarItem();

        mb.setElementAt((messageCount == 0) ? null : new Integer(RosterIcons.ICON_MESSAGE_INDEX), 0);

        mb.setElementAt((messageCount == 0) ? null : getHeaderString(), 1);
        mb.setElementAt(new Integer(s), 2);
        mb.setElementAt(en, 5);

        if (phoneManufacturer == Config.WINDOWS) {
            if (messageCount == 0) {
                sd.canvas.setTitle("BombusMod");
            } else {
                sd.canvas.setTitle("BombusMod " + getHeaderString());
            }
        }
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
                m += c.getNewMsgsCount();
                h += c.getNewHighliteMsgsCount();
            }
        }
        highliteMessageCount = h;
        messageCount = m;

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
        /*Group g=(Group)getFocusedObject();*/
        if (g == null) {
            return;
        }
        if (!g.collapsed && !Config.getInstance().autoClean) {
            return;
        }
        String groupSelfContact = "";
//#ifndef WMUC
        if (g instanceof ConferenceGroup) {
            ConferenceGroup cg = (ConferenceGroup) g;
            groupSelfContact = cg.selfContact.bareJid;
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
                                contact = null;
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
                            && !contact.bareJid.equals(groupSelfContact)
                            && contact.origin != Contact.ORIGIN_GROUPCHAT) {

                        contact.msgs.removeAllElements();
                        contact = null;
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
                    Group group = (c.jid.isTransport())
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

                    c.nick = nick;
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
        c.bareJid = from;
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
                c.jid.setJid(from);
                c.bareJid = from;
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

    public final MucContact mucContact(String from) {
        // muc message
        int ri = from.indexOf('@');
        int rp = from.indexOf('/');
        String room = from.substring(0, ri);
        String roomJid = from.substring(0, rp).toLowerCase();

        ConferenceGroup grp = groups.getConfGroup(new Jid(roomJid));

        if (grp == null) {
            return null; // we are not joined this room
        }
        MucContact c = findMucContact(new Jid(from));

        if (c == null) {
            c = new MucContact(from.substring(rp + 1), from);
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
            c = new Contact(null, jid, Presence.PRESENCE_OFFLINE, "none"); /*"not-in-list"*/
            c.bareJid = J.getBareJid();
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
    public void showContactMessageList(String jid) {
        new ContactMessageList(sd.roster.getContact(jid, false));
    }
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

//#ifdef JUICK
//# /*        public Vector getJuickContacts(boolean str) {
//#     Vector juickContacts = new Vector();
//#     synchronized (hContacts) {
//#     for (Enumeration e = hContacts.elements(); e.hasMoreElements();) {
//#     Contact c = (Contact) e.nextElement();
//#     if (isJuickContact(c))
//#     if (str)
//#     juickContacts.addElement(c.bareJid);
//#     else juickContacts.addElement(c);
//#     }
//#     }
//#     return juickContacts;
//#     }*/
//#     public Contact getMainJuickContact() {
//#         if (indexMainJuickContact > -1) {
//#             return (Contact) juickContacts.elementAt(indexMainJuickContact);
//#         } else {
//#             return null;
//#         }
//#     }
//# 
//#     /*    public void updateMainJuickContact() {
//#     System.out.println("1. juickJID: "+cf.juickJID);
//#     mainJuickContact = null;
//#     int index = -1;
//#     if (!cf.juickJID.equals("")) {
//#     index = hContacts.indexOf(new Contact("Juick", cf.juickJID, Presence.PRESENCE_OFFLINE, null));
//#     System.out.println("index: "+index);
//#     if (index < 0) { // Если не нашли, то считаем, что не указан.
//#     cf.juickJID = "";
//#     }
//#     }
//#     if (index > -1) {
//#     mainJuickContact = (Contact) hContacts.elementAt(index);
//#     } else {
//#     Vector juickContacts = getJuickContacts(false);
//#     if (juickContacts.size() > 0) {
//#     mainJuickContact = (Contact) juickContacts.elementAt(0);
//#     }
//#     }
//#     System.out.println("2. juickJID: "+cf.juickJID);
//#     }*/
//#     public boolean isJuickContact(Contact c) {
//#         return c.jid.equalsViaJ2J("juick@juick.com") || c.jid.equalsViaJ2J("psto@psto.net");
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
//#         return c.bareJid.equals(JuickConfig.getJuickJID());
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
//#                 if (((Contact) juickContacts.elementAt(i)).bareJid.equals(JuickConfig.getJuickJID())) {
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
//#ifdef AUTOSTATUS
//#         messageActivity();
//#endif
        if (message != null) {
            myMessage = message;
        }

        setQuerySign(false);

        if (myStatus != Presence.PRESENCE_OFFLINE) {
            lastOnlineStatus = myStatus;
        }

        // reconnect if disconnected
        if (myStatus != Presence.PRESENCE_OFFLINE && theStream == null) {
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

            Presence presence = new Presence(myStatus, myPriority, myMessage, sd.account.getNick());

            if (!sd.account.isMucOnly()) {
                theStream.send(presence);
            }
//#ifndef WMUC
            reEnumerator = null;
            multicastConferencePresence(myStatus, myMessage, myPriority);
//#endif
        }

        // disconnect
        if (myStatus == Presence.PRESENCE_OFFLINE) {
            try {
                theStream.close(); // sends </stream:stream> and closes socket
            } catch (Exception e) { /*e.printStackTrace();*/ }
            makeRosterOffline();
            theStream = null;
//#ifdef AUTOSTATUS
//#             autoAway = false;
//#             autoXa = false;
//#endif            
        }

        Contact c = selfContact();
        c.setStatus(myStatus);
    }

    private void makeRosterOffline() {
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

    public void sendDirectPresence(int status, String to, JabberDataBlock x) {
        if (to == null) {
            sendPresence(status, null);
            return;
        }

        ExtendedStatus es = sl.getStatus(status);
        myMessage = es.getMessage();

        myMessage = StringUtils.toExtendedString(myMessage);

        Presence presence = new Presence(status, es.getPriority(), myMessage, sd.account.getNick());

        presence.setTo(to);

        if (x != null) {
            presence.addChild(x);
        }

        if (theStream != null) {
            theStream.send(presence);
        }
    }

    public void sendDirectPresence(int status, Contact to, JabberDataBlock x) {
        sendDirectPresence(status, (to == null) ? null : to.getJid(), x);
        if (to != null) {
            if (to.jid.isTransport()) {
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
        if (theStream == null) {
            return false;
        }
        return theStream.loggedIn;
    }

    public Contact selfContact() {
        return myJid == null ? null : getContact(myJid.getJid(), false);
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
                    ConferenceForm.join(confGroup.name, myself.getJid(), confGroup.password, 20);
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

        theStream.send(presence);
    }

    public void doSubscribe(Contact c) {
        if (c.subscr == null) {
            return;
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

        String to = (c.jid.isTransport()) ? c.getJid() : c.bareJid;

        if (subscribed) {
            sendPresence(to, "subscribed", null, false);
        }
        if (subscribe) {
            sendPresence(to, "subscribe", null, false);
        }
    }

    public void sendMessage(Contact to, String id, final String body, final String subject, String composingState) {
        try {
//#ifndef WMUC
            boolean groupchat = to.origin == Contact.ORIGIN_GROUPCHAT;
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
                    groupchat);
            message.setAttribute("id", id);
            if (groupchat && body == null && subject == null) {
                return;
            }

            if (composingState != null) {
                message.addChildNs(composingState, "http://jabber.org/protocol/chatstates");
            }


            if (!groupchat) {
                if (body != null) {
                    if (cf.eventDelivery) {
                        message.addChildNs("request", "urn:xmpp:receipts");
                    }
                }
            }

            theStream.send(message);
            lastMessageTime = Time.utcTimeMillis();
            playNotify(SOUND_OUTGOING);
        } catch (Exception e) {
//#ifdef DEBUG            
//#             e.printStackTrace(); 
//#endif            
        }
//#ifdef AUTOSTATUS
//#         messageActivity();
//#endif
    }

    private void sendDeliveryMessage(Contact c, String id) {
        if (!cf.eventDelivery) {
            return;
        }
        if (myStatus == Presence.PRESENCE_INVISIBLE) {
            return;
        }
        Message message = new Message(c.jid.getJid());
        // FIXME: no need to send <received /> to forwarded messages
        //xep-0184
        message.setAttribute("id", id); // FIXME: should be new id by XEP-0184 version 1.1
        message.addChildNs("received", "urn:xmpp:receipts").setAttribute("id", id);
        theStream.send(message);
    }
    private Vector vCardQueue;

    public void resolveNicknames(String transport) {
        vCardQueue = null;
        vCardQueue = new Vector();
        synchronized (hContacts) {
            int j = hContacts.size();
            for (int i = 0; i < j; i++) {
                Contact k = (Contact) hContacts.elementAt(i);
                if (k.jid.isTransport()) {
                    continue;
                }
                int grpType = k.getGroupType();
                if (k.jid.getServer().equals(transport) && k.nick == null && (grpType == Groups.TYPE_COMMON || grpType == Groups.TYPE_NO_GROUP)) {
                    vCardQueue.addElement(IqVCard.query(k.getJid(), "nickvc" + k.bareJid));
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
                theStream.send(req);
                querysign = true;
            }
        }
        updateMainBar();
    }

//#if CHANGE_TRANSPORT
//#     public void contactChangeTransport(String srcTransport, String dstTransport){ //voffk
//# 	setQuerySign(true);
//#         int j=hContacts.size();
//#         for (int i=0; i<j; i++) {
//# 	    Contact k=(Contact) hContacts.elementAt(i);
//# 	    if (k.jid.isTransport()) continue;
//#             int grpType=k.getGroupType();
//#             if (k.jid.getServer().equals(srcTransport) &&
//#                     (grpType==Groups.TYPE_COMMON || grpType==Groups.TYPE_NO_GROUP ||
//#                     grpType==Groups.TYPE_VISIBLE || grpType==Groups.TYPE_VIP ||
//#                     grpType==Groups.TYPE_IGNORE)) {
//#                 String jid=k.getJid();
//#                 jid=StringUtils.stringReplace(jid, srcTransport, dstTransport);
//#                 storeContact(jid, k.nick, (!k.group.name.equals(SR.MS_GENERAL))?(k.group.name):"", true); //new contact addition
//#                 try {
//#                     Thread.sleep(300);
//#                 } catch (Exception ex) { }
//#                 deleteContact(k); //old contact deletion
//# 	    }
//# 	}
//# 	setQuerySign(false);
//#     }
//#endif
    public void loginFailed(String error) {
        myStatus = Presence.PRESENCE_OFFLINE;
        setProgress(SR.MS_LOGIN_FAILED, 100);

        errorLog(error);

        try {
            theStream.close();
        } catch (Exception e) {
            //e.printStackTrace();
        }
        theStream = null;

        doReconnect = false;
        setQuerySign(false);
        redraw();
    }

    public void loginSuccess() {
//#ifdef AUTOSTATUS
//#         if (myStatus < 2) {
//#             messageActivity();
//#         }
//#endif

        theStream.addBlockListener(new EntityCaps());
        theStream.addBlockListener(new IqVCard());

        theStream.addBlockListener(new IqPing());
        theStream.addBlockListener(new IqVersionReply());
        theStream.startKeepAliveTask(); //enable keep-alive packets

        theStream.loggedIn = true;
        currentReconnect = 0;

        theStream.addBlockListener(new IqLast());
        theStream.addBlockListener(new IqTimeReply());
        theStream.addBlockListener(new RosterXListener());
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
//#             theStream.addBlockListener(new IqGmail());
//#         }
//#endif
//#if FILE_TRANSFER
        if (cf.fileTransfer) // enable File transfers
        {
            TransferDispatcher.getInstance().addBlockListener();
        }
//#endif

//#ifdef CAPTCHA
//#         theStream.addBlockListener(new Captcha());
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

        if (sd.account.isMucOnly()) {
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
            theStream.addBlockListener(new BookmarkQuery(BookmarkQuery.LOAD));
//#endif            
        } else {
            JabberDataBlock qr = new IqQueryRoster();
            setProgress(SR.MS_ROSTER_REQUEST, 49);
            theStream.send(qr);
            show();
        }
//#ifndef WMUC
        //query bookmarks
        theStream.addBlockListener(new BookmarkQuery(BookmarkQuery.LOAD));
//#endif

    }

    public void bindResource(String myJid) {
        Contact self = selfContact();
        self.jid = this.myJid = new Jid(myJid);
    }

    public int blockArrived(JabberDataBlock data) {
        try {
            if (data instanceof Iq) {
                String from = data.getAttribute("from");
                String type = data.getTypeAttribute();
                String id = data.getAttribute("id");

                if (type.equals("result")) {
                    if (id.equals("getros")) {
                        //theStream.enableRosterNotify(false); //voffk

                        if (!processRoster(data)) {
                            return JabberBlockListener.BLOCK_REJECTED;
                        }

                        if (!cf.collapsedGroups) {
                            groups.queryGroupState(true);
                        }

                        setProgress(SR.MS_CONNECTED, 100);
                        reEnumRoster();

                        querysign = doReconnect = false;

                        if (cf.loginstatus == 5) {
                            sendPresence(Presence.PRESENCE_INVISIBLE, null);
                        } else {
                            sendPresence(cf.loginstatus, null);
                        }
                        if (!sd.canvas.isShown()) {
                            SplashScreen.getInstance().destroyView();
                        } else {
                            sd.canvas.setList(this);
                        }

                        return JabberBlockListener.BLOCK_PROCESSED;
                    }
                } else if (type.equals("set")) {
                    if (processRoster(data)) {
                        theStream.send(new Iq(from, Iq.TYPE_RESULT, id));
                        reEnumRoster();
                        return JabberBlockListener.BLOCK_PROCESSED;
                    }
                }
            } else if (data instanceof Message) { // If we've received a message
                //System.out.println(data.toString());
                querysign = false;
                Message message = (Message) data;

                String from = message.getFrom();

                if (myJid.equals(new Jid(from), false)) //Enable forwarding only from self-jids
                {
                    from = message.getXFrom();
                }

                String type = message.getTypeAttribute();

                boolean groupchat = false;

                int start_me = -1;
                String name = null;

                if (type != null) {
                    if (type.equals("groupchat")) {
                        groupchat = true;
                    }
                }

                if (groupchat) {
                    start_me = 0;

                    int rp = from.indexOf('/');

                    name = from.substring(rp + 1);

                    if (rp > 0) {
                        from = from.substring(0, rp);
                    }
                }

                Contact c = getContact(from, (cf.notInListDropLevel != NotInListFilter.DROP_MESSAGES_PRESENCES || groupchat
//#ifndef WMUC
                        || message.getMucInvitation() != null 
//#endif
                        ));
                if (c == null) {
                    return JabberBlockListener.BLOCK_REJECTED; //not-in-list message dropped
                }
                boolean highlite = false;

                String body = message.getBody().trim();
                String oob = message.getOOB();
                if (oob != null) {
                    body += oob;
                }
                if (body.length() == 0) {
                    body = null;
                }
                String subj = message.getSubject().trim();
                if (subj.length() == 0) {
                    subj = null;
                }

                long tStamp = message.getMessageTime();

                int mType = Msg.MESSAGE_TYPE_IN;
                if (groupchat) {
                    if (subj != null) { // subject
                        if (body == null) {
                            body = name + " " + SR.MS_HAS_SET_TOPIC_TO + ": " + subj;
                        }
                        if (!subj.equals(c.statusString)) {
                            c.statusString = subj; // adding secondLine to conference
                        } else {
                            return JabberBlockListener.BLOCK_PROCESSED;
                        }
                        subj = null;
                        start_me = -1;
                        highlite = true;
                        mType = Msg.MESSAGE_TYPE_SUBJ;
                    }
                } else if (type != null) {
                    if (type.equals("error")) {
                        body = SR.MS_ERROR_ + XmppError.findInStanza(message).toString();
                    } else if (type.equals("headline")) {
                        mType = Msg.MESSAGE_TYPE_HEADLINE;
                    }
                } else {
                    type = "chat";
                }
//#ifndef WMUC
                try {
                    JabberDataBlock xmlns = message.findNamespace("x", "http://jabber.org/protocol/muc#user");
                    JabberDataBlock invite = message.getMucInvitation();
                    if (invite != null) {
                        if (message.getTypeAttribute().equals("error")) {
                            ConferenceGroup invConf = (ConferenceGroup) groups.getGroup(from);
                            body = XmppError.decodeStanzaError(message).toString(); /*"error: invites are forbidden"*/
                        } else {
                            String inviteReason = invite.getChildBlockText("reason");
                            String room = from + '/' + sd.account.getNickName();

                            ConferenceGroup invConf = initMuc(room, xmlns.getChildBlockText("password"));

                            invConf.confContact.commonPresence = false; //FS#761

                            c = invConf.confContact;

                            if (invConf.selfContact.status == Presence.PRESENCE_OFFLINE) {
                                invConf.confContact.status = Presence.PRESENCE_OFFLINE;
                            }

                            if (inviteReason != null) {
                                inviteReason = (inviteReason.length() > 0) ? " (" + inviteReason + ")" : "";
                            }

                            body = invite.getAttribute("from") + SR.MS_IS_INVITING_YOU + from + inviteReason;

                            reEnumRoster();
                        }
                    }
                } catch (Exception e) {
//#ifdef DEBUG                    
//#                     e.printStackTrace();
//#endif                    
                }
//#endif
                if (name == null) {
                    name = c.getName();
                }
                // /me
                if (!cf.showNickNames) {
                    if (body != null) {
                        //forme=false;
                        if (body.startsWith("/me ")) {
                            start_me = 3;
                        }
                        if (start_me >= 0) {
                            StringBuffer b = new StringBuffer();
//#if NICK_COLORS
                            b.append("\01");
//#endif
                            b.append(name);
//#if NICK_COLORS
                            b.append("\02");
//#endif
                            if (start_me == 0) {
                                if (!cf.hideTimestamps) {
                                    b.insert(0, "<");
                                }
                                b.append("> ");
                            } else {
                                b.insert(0, '*');
                            }
                            b.append(body.substring(start_me));
                            body = b.toString();
                            b = null;
                        }

                    }
                }
                //boolean compose=false;
                if (type.equals("chat") && myStatus != Presence.PRESENCE_INVISIBLE) {
                    if (message.findNamespace("request", "urn:xmpp:receipts") != null) {
                        sendDeliveryMessage(c, data.getAttribute("id"));
                    }
                    JabberDataBlock received = message.findNamespace("received", "urn:xmpp:receipts");
                    if (received != null) {
                        c.markDelivered(data.getAttribute("id")); //FIXME: compatibilty with XEP-0184 version 1.0
                        c.markDelivered(received.getAttribute("id")); // XEP-0184 Version 1.1
                    }
                    if (message.findNamespace("active", "http://jabber.org/protocol/chatstates") != null) {
                        c.acceptComposing = true;
                        c.showComposing = false;
//#ifdef RUNNING_MESSAGE
//#                         setTicker(c, "");
//#endif
                    }
                    if (message.findNamespace("paused", "http://jabber.org/protocol/chatstates") != null) {
                        c.acceptComposing = true;
                        c.showComposing = false;
//#ifdef RUNNING_MESSAGE
//#                         setTicker(c, "");
//#endif
                    }
                    if (message.findNamespace("composing", "http://jabber.org/protocol/chatstates") != null) {
                        playNotify(SOUND_COMPOSING);
                        c.acceptComposing = true;
                        c.showComposing = true;
//#ifdef RUNNING_MESSAGE
//#                         setTicker(c, SR.MS_COMPOSING_NOTIFY);
//#endif
                    }
                }
                redraw();

                if (body == null) {
                    return JabberBlockListener.BLOCK_REJECTED;
                }

                Msg m = new Msg(mType, from, subj, body);
                if (tStamp != 0) {
                    m.dateGmt = tStamp;
                }
//#ifndef WMUC
                if (m.body.indexOf(SR.MS_IS_INVITING_YOU) > -1) {
                    m.dateGmt = 0;
                }
                if (groupchat) {
                    ConferenceGroup mucGrp = (ConferenceGroup) c.group;
                    if (mucGrp.selfContact.getJid().equals(message.getFrom())) {
                        m.messageType = Msg.MESSAGE_TYPE_OUT;
                        m.unread = false;
                    } else {
                        if (m.dateGmt <= ((ConferenceGroup) c.group).conferenceJoinTime) {
                            m.messageType = Msg.MESSAGE_TYPE_HISTORY;
                        }
                        // highliting messages with myNick substring
                        String myNick = mucGrp.selfContact.getName();
                        String myNick_ = myNick + " ";
                        String _myNick = " " + myNick;
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
                        myNick = null;
                        myNick_ = null;
                        _myNick = null;
                        //TODO: custom highliting dictionary
                    }
                    m.from = name;
                }
//#endif
                m.highlite = highlite;
                messageStore(c, m);
                return JabberBlockListener.BLOCK_PROCESSED;
            } else if (data instanceof Presence) {  // If we've received a presence
                //System.out.println("presence");
                if (myStatus == Presence.PRESENCE_OFFLINE) {
                    return JabberBlockListener.BLOCK_REJECTED;
                }

                Presence pr = (Presence) data;

                String from = pr.getFrom();
                pr.dispathch();
                int ti = pr.getTypeIndex();

                //PresenceContact(from, ti);
                Msg m = new Msg((ti == Presence.PRESENCE_AUTH || ti == Presence.PRESENCE_AUTH_ASK) ? Msg.MESSAGE_TYPE_AUTH : Msg.MESSAGE_TYPE_PRESENCE, from, null, pr.getPresenceTxt());
//#ifndef WMUC
                JabberDataBlock xmuc = pr.findNamespace("x", "http://jabber.org/protocol/muc#user");
                if (xmuc == null) {
                    xmuc = pr.findNamespace("x", "http://jabber.org/protocol/muc"); //join errors
                }
                if (xmuc != null) {
                    try {
                        MucContact c = mucContact(from);

                        if (pr.getAttribute("ver") != null) {
                            c.version = pr.getAttribute("ver"); // for bombusmod only
                        }
//#ifdef CLIENTS_ICONS
                        if (cf.showClientIcon) {
                            if (pr.hasEntityCaps()) {
                                getClientIcon(c, pr.getEntityNode());
                                String presenceVer = pr.getEntityVer();
                                if (presenceVer != null) {
                                    c.version = presenceVer;
                                }
                            }
                        }

//#endif
                        String lang = pr.getAttribute("xml:lang");

                        if (lang != null) {
                            c.lang = lang;
                        }
                        lang = null;

                        c.statusString = pr.getStatus();

                        String chatPres = c.processPresence(xmuc, pr);
                        String type = data.getTypeAttribute();

                        if (cf.storeConfPresence
                                || chatPres.indexOf(SR.MS_WAS_BANNED) > -1
                                || chatPres.indexOf(SR.MS_WAS_KICKED) > -1
                                || (type != null && type.equals("error"))) {
                            int rp = from.indexOf('/');

                            String name = from.substring(rp + 1);

                            Msg chatPresence = new Msg(Msg.MESSAGE_TYPE_PRESENCE, name, null, chatPres);
                            chatPresence.color = c.getMainColor();
                            messageStore(getContact(from.substring(0, rp), false), chatPresence);
                            name = null;
                        }

                        chatPres = null;

                        messageStore(c, m);

                        c.priority = pr.getPriority();
                        //System.gc();
                        //Thread.sleep(20);
                    } catch (Exception e) {
//#ifdef DEBUG
//#                         e.printStackTrace();
//#endif                        
                    }
                } else {
//#endif
                    Contact c = null;

                    if (ti == Presence.PRESENCE_AUTH_ASK) {
                        //processing subscriptions
                        if (cf.autoSubscribe == Config.SUBSCR_DROP) {
                            return JabberBlockListener.BLOCK_REJECTED;
                        }

                        if (cf.autoSubscribe == Config.SUBSCR_REJECT) {
//#ifdef DEBUG 
//#                             System.out.print(from);
//#                             System.out.println(": decline subscription");
//#endif
                            sendPresence(from, "unsubscribed", null, false);
                            return JabberBlockListener.BLOCK_PROCESSED;
                        }

                        c = getContact(from, true);

                        if (cf.autoSubscribe != Config.SUBSCR_AUTO) {
                            messageStore(c, m);
                        } else {
                            doSubscribe(c);
                        }

                    } else {
                        // processing presences
                        boolean enNIL = cf.notInListDropLevel > NotInListFilter.DROP_PRESENCES;
                        c = getContact(from, enNIL);

                        if (c == null) {
                            return JabberBlockListener.BLOCK_REJECTED; //drop not-in-list presence
                        }
                        if (pr.getAttribute("ver") != null) {
                            c.version = pr.getAttribute("ver");  // for bombusmod only
                        }
                        if (ti != Presence.PRESENCE_ERROR) {
//#ifdef CLIENTS_ICONS
                            if (cf.showClientIcon) {
                                if (ti < Presence.PRESENCE_OFFLINE) {
                                    if (pr.hasEntityCaps()) {
                                        if (pr.getEntityNode() != null) {
                                            ClientsIconsData.processData(c, pr.getEntityNode());
                                            if (pr.getEntityVer() != null) {
                                                c.version = pr.getEntityVer();
                                            }
                                        }
                                    } else if (c.jid.hasResource()) {
                                        ClientsIconsData.processData(c, c.getResource().substring(1));
                                    }
                                }
                            }
//#endif
                            JabberDataBlock j2j = pr.findNamespace("x", "j2j:history");
                            if (j2j != null) {
                                if (j2j.getChildBlock("jid") != null) {
                                    c.j2j = j2j.getChildBlock("jid").getAttribute("gateway");
                                }
                            }
                            j2j = null;

                            String lang = pr.getAttribute("xml:lang");
//#if DEBUG
//#                             //System.out.println("xml:lang="+lang); // Very much output!
//#endif
                            c.lang = lang;
                            lang = null;

                            c.statusString = pr.getStatus();
                        }
                        if (ti == Presence.PRESENCE_AUTH) {
                            if (cf.autoSubscribe == Config.SUBSCR_AUTO) {
                                return JabberBlockListener.BLOCK_PROCESSED;
                            }
                        }

                        messageStore(c, m);

                    }

                    c.priority = pr.getPriority();
                    if (ti >= 0) {
                        c.setStatus(ti);
                    }

                    if (c.nick == null && c.status <= Presence.PRESENCE_DND) {
                        JabberDataBlock nick = pr.findNamespace("nick", "http://jabber.org/protocol/nick");
                        if (nick != null) {
                            c.nick = nick.getText();
                            storeContact(from, c.nick, c.group.name, false);
                        } else {
                            nick = pr.getChildBlockChild("nickname"); // PyICQ-t workaround
                            if (nick != null) {
                                c.nick = nick.getText();
                                storeContact(from, c.nick, c.group.name, false);
                            }
                        }
                    }

                    if ((ti == Presence.PRESENCE_ONLINE || ti == Presence.PRESENCE_CHAT) && notifyReady(-111)) {
//#if USE_ROTATOR
                        if (cf.notifyBlink) {
                            c.setNewContact();
                        }
//#endif
                        if (cf.notifyPicture) {
                            if (c.getGroupType() != Groups.TYPE_TRANSP) {
                                c.setIncoming(Contact.INC_APPEARING);
                            }
                        }
                    }
                    if (ti == Presence.PRESENCE_OFFLINE) {
                        c.setIncoming(Contact.INC_NONE);
                        c.showComposing = false;
                    }
                    if (ti >= 0) {
//#ifdef RUNNING_MESSAGE
//#                         if (ti==Presence.PRESENCE_OFFLINE)
//#                             setTicker(c, SR.MS_OFFLINE);
//#                         else if (ti==Presence.PRESENCE_ONLINE)
//#                             setTicker(c, SR.MS_ONLINE);
//#endif
                        if ((ti == Presence.PRESENCE_ONLINE || ti == Presence.PRESENCE_CHAT || ti == Presence.PRESENCE_OFFLINE) && (!c.jid.isTransport()) && (c.getGroupType() != Groups.TYPE_IGNORE)) {
                            playNotify(ti);
                        }
                    }

//#ifndef WMUC
                }
//#endif
                if (cf.autoClean) {
                    cleanAllGroups();
                    sort(hContacts);
                } else {
                    sort(hContacts);
                    reEnumRoster();
                }
                return JabberBlockListener.BLOCK_PROCESSED;
            } // if presence
        } catch (Exception e) {
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

    boolean processRoster(JabberDataBlock data) {
        JabberDataBlock q = data.findNamespace("query", "jabber:iq:roster");
        if (q == null) {
            return false;
        }
        int type = 0;

        //verifying from attribute as in RFC3921/7.2
        String from = data.getAttribute("from");
        if (from != null) {
            Jid fromJid = new Jid(from);
            if (fromJid.hasResource()) {
                if (!myJid.equals(fromJid, true)) {
                    return false;
                }
            }
        }

        Vector cont = (q != null) ? q.getChildBlocks() : null;
        q = null;

        if (cont != null) {
            int j = cont.size();
            for (int ii = 0; ii < j; ii++) {
                JabberDataBlock i = (JabberDataBlock) cont.elementAt(ii);
                if (i.getTagName().equals("item")) {
                    String name = i.getAttribute("name");
                    String jid = i.getAttribute("jid");
                    String subscr = i.getAttribute("subscription");
                    boolean ask = (i.getAttribute("ask") != null);

                    String group = i.getChildBlockText("group");
                    if (group.length() == 0) {
                        group = Groups.COMMON_GROUP;
                    }

                    updateContact(name, jid, group, subscr, ask);
                    //sort(hContacts);
                }
            }
        }
        sort(hContacts);
        return true;
    }

//#ifdef POPUPS
    boolean showWobbler(Contact c) {
        if (!cf.popUps) {
            return false;
        }
        if (activeContact == null) {
            return true;
        }
        return (!c.equals(activeContact));
    }
//#endif

//#ifdef FILE_TRANSFER
    public void addFileQuery(String from, String message) {
        Contact c = getContact(from, true);
        c.fileQuery = true;
        messageStore(c, new Msg(Msg.MESSAGE_TYPE_FILE_REQ, from, SR.MS_FILE, message));
    }
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
        if (message.messageType == Msg.MESSAGE_TYPE_OUT) {
            if (cf.autoScroll) {
                VirtualList list = sd.canvas.getList();
                if (list instanceof ContactMessageList) {
                    if (((ContactMessageList) list).contact.compare(c) == 0) {
                        list.moveCursorEnd();
                    }
                }
            }
        }

        if (!message.unread) {
            return;
        }
        //TODO: clear unread flag if not-in-list IS HIDDEN

        if (c.getGroupType() == Groups.TYPE_IGNORE) {
            return;    // no signalling/focus on ignore
        }
//#ifdef POPUPS
        if (cf.popUps) {
            if (message.messageType == Msg.MESSAGE_TYPE_AUTH && showWobbler(c)) {
                setWobble(2, c.getJid(), message.from + "\n" + message.body);
            }
        }
//#endif

        if (cf.popupFromMinimized) {
//#ifdef SYSTEM_NOTIFY
//#             Notificator notify = Notification.getNotificator();
//#endif
            if (AlertCustomize.getInstance().vibrateOnlyHighlited) {
                if (message.highlite) {
//#ifdef SYSTEM_NOTIFY
//#                     if (notify != null)
//#                         notify.sendNotify(message.from, message.body);
//#                     else
//#endif
                    BombusMod.getInstance().hideApp(false);
                }
            } else {
//#ifdef SYSTEM_NOTIFY
//#                 if (notify != null)
//#                     notify.sendNotify(message.from, message.body);
//#                 else
//#endif
                BombusMod.getInstance().hideApp(false);
            }
        }

        if (message.highlite) {
            playNotify(SOUND_FOR_ME);
//#ifdef POPUPS
            if (showWobbler(c)) {
                setWobble(2, c.getJid(), message.body);
            }
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
                {
                    if (showWobbler(c)) {
                        setWobble(2, c.getJid(), c.toString() + ": " + message.body);
                        autorespond = true;
                    }
                }
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
            } //#ifndef WMUC
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

        if (c.origin == Contact.ORIGIN_GROUPCHAT || c.jid.isTransport() || c.getGroupType() == Groups.TYPE_TRANSP || c.getGroupType() == Groups.TYPE_SEARCH_RESULT || c.getGroupType() == Groups.TYPE_SELF) {
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
                        c.getJid(),
                        es.getAutoRespondMessage(),
                        SR.MS_AUTORESPOND,
                        false);
                theStream.send(autoMessage);
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

    public void beginConversation() { //todo: verify xmpp version
        SplashScreen.getInstance().setExit(this);
        if (theStream.isXmppV1()) {
            theStream.addBlockListener(new SASLAuth(sd.account, this, theStream));
        }
//#if NON_SASL_AUTH
//#         else new NonSASLAuth(sd.account, this, theStream);
//#endif
    }

    public void connectionTerminated(Exception e) {
        if (e != null) {
            errorLog("Exception in parser: " + e.getMessage());
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
        makeRosterOffline();
    }

    public void dispatcherException(Exception e, JabberDataBlock dataBlock) {
        errorLog("JabberDataBlockDispatcher exception\ndataBlock: " + dataBlock.toString());
//#ifdef DEBUG
//# 	System.out.println("JabberDataBlockDispatcher exception\ndataBlock: " + dataBlock.toString());
//# 	e.printStackTrace();
//#endif
    }

    private void askReconnect(final Exception e) {
        //SplashScreen.getInstance().close();
        try {
            theStream.close(); // sends </stream:stream> and closes socket
        } catch (Exception e1) { /*e1.printStackTrace();*/ }
        theStream = null;
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

        if (e instanceof SecurityException) {
            String errSSL = io.SSLExceptionDecoder.decode(e);
            errorLog(errSSL);
            return;
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
        if (createMsgList() == null) {
            cleanupGroup((Group) getFocusedObject());
            reEnumRoster();
        }
    }

    public void eventLongOk() {
        super.eventLongOk();
//#ifndef WMUC
//#ifdef POPUPS
        showInfo();
//#endif
//#endif
    }

    private VirtualList createMsgList() {
        Object e = getFocusedObject();
        if (e instanceof Contact) {
            return new ContactMessageList((Contact) e);
        }
        return null;
    }

    public void messageEditResume() {
        if (!isLoggedIn()) {
            return;
        }

        VirtualList pview = createMsgList();
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
        if (!cf.collapsedGroups) {
            return;
        }

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
//#     if (cf.autoAwayType == Config.AWAY_LOCK) {
//#         if (!autoAway) {
//#         autoAway = true;
//#         if (cf.useMyStatusMessages)
//#             sendPresence(Presence.PRESENCE_AWAY, null);
//#         else
//#             sendPresence(Presence.PRESENCE_AWAY, "Auto Status on KeyLock since %t");
//#         }
//#     }
//#endif
        new SplashScreen(getMainBarItem(), VirtualCanvas.keyLock);
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
                c = null;
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

//#ifdef AUTOSTATUS
//#     public void setAutoAwayTimer() {
//#         if (cf.autoAwayType == Config.AWAY_LOCK) {
//#             if (!autoAway) {
//#                 AutoStatusTask.getInstance().setTimeEvent(cf.autoAwayDelay * 60 * 1000);
//#             }
//#         }
//#     }
//# 
//#     public void userActivity() {
//#         if (cf.autoAwayType == Config.AWAY_IDLE) {
//#             if (!autoAway) {
//#                 AutoStatusTask.getInstance().setTimeEvent(cf.autoAwayDelay * 60 * 1000);
//#                 return;
//#             }
//#         } else {
//#             return;
//#         }
//#         AutoStatusTask.getInstance().setTimeEvent(0);
//#         setAutoStatus(Presence.PRESENCE_ONLINE);
//#     }
//# 
//#     public final void messageActivity() {
//# 
//#         if (cf.autoAwayType == Config.AWAY_MESSAGE) {
//#             //System.out.println("messageActivity "+myStatus.getImageIndex());
//#             if (myStatus < 2) {
//#                 AutoStatusTask.getInstance().setTimeEvent(cf.autoAwayDelay * 60 * 1000);
//#             } else if (!autoAway) {
//#                 AutoStatusTask.getInstance().setTimeEvent(0);
//#             }
//#         }
//#     }
//#endif
    public void logoff(String mess) {
        if (isLoggedIn()) {
            try {
                if (mess == null) {
                    mess = sl.getStatus(Presence.PRESENCE_OFFLINE).getMessage();
                }
                sendPresence(Presence.PRESENCE_OFFLINE, mess);
            } catch (Exception e) {
            }
        }
//#ifdef STATS
//#         Stats.getInstance().saveToStorage(false);
//#endif
    }

    public void quit() {
        logoff(null);
        try {
            Thread.sleep(250L);
        } catch (InterruptedException ex) {
//#ifdef DEBUG            
//#             ex.printStackTrace();
//#endif            
        }
        BombusMod.getInstance().notifyDestroyed();
    }

    public void menuAction(MenuCommand c, VirtualList d) {
//#ifdef AUTOSTATUS
//#         userActivity();
//#endif
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
        } //#ifdef ARCHIVE
        else if (c == cmdArchive) {
            cmdArchive();
        } //#endif
        else if (c == cmdInfo) {
            cmdInfo();
        } else if (c == cmdTools) {
            cmdTools();
        } else if (c == cmdCleanAllMessages) {
            cmdCleanAllMessages();
        } //#ifndef WMUC
        else if (c == cmdConference) {
            cmdConference();
        } //#endif
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
        if (cf.allowMinimize) {
            BombusMod.getInstance().hideApp(true);
        } else if (phoneManufacturer == Config.SIEMENS2) {
            new SieNatMenu(this);
            /*
            try {
            // SIEMENS: MYMENU call. Possible Main Menu for capable phones
            BombusMod.getInstance().platformRequest("native:ELSE_STR_MYMENU");
            } catch (Exception e) { }
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

    public void cmdArchive() {
        new ArchiveList(-1, 1, null);
    }
//#endif

    public void cmdInfo() {
        new Info.InfoWindow();
    }

    public void cmdTools() {
        new RosterToolsMenu();
    }
//#ifdef POPUPS

    public void cmdClearPopups() {
        PopUp.getInstance().clear();
    }
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
        String confJid = confGroup.selfContact.getJid();
        String name = confGroup.name;
        new ConferenceForm(name, confJid, confGroup.password, false);
    }

    public void leaveRoom(Group group) {
        ConferenceGroup confGroup = (ConferenceGroup) group;
        Contact myself = confGroup.selfContact;
        confGroup.confContact.commonPresence = false; //disable reenter after reconnect
        sendPresence(myself.getJid(), "unavailable", null, true);

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
            new ContactMessageList(c);
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
            if (c.jid.isTransport()) {
                // double-check for empty jid or our server jid
                if (c.bareJid.length() == 0) {
                    return;
                }
                if (c.bareJid.equals(myJid.getServer())) {
                    return;
                }
                // automatically remove registration
                JabberDataBlock unreg = new Iq(c.bareJid, Iq.TYPE_SET, "unreg" + System.currentTimeMillis());
                JabberDataBlock query = unreg.addChildNs("query", "jabber:iq:register");
                query.addChild("remove", null);
                theStream.send(unreg);
            }

            if (c.getGroupType() == Groups.TYPE_NOT_IN_LIST) {
                hContacts.removeElement(c);
                countNewMsgs();
                reEnumRoster();
            } else {
                theStream.send(new IqQueryRoster(c.bareJid, null, null, "remove"));

                sendPresence(c.bareJid, "unsubscribe", null, false);
                sendPresence(c.bareJid, "unsubscribed", null, false);
            }
        }
    }

    public void setQuerySign(boolean requestState) {
        querysign = requestState;
        updateMainBar();
    }

    public void storeContact(String jid, String name, String group, boolean askSubscribe) {
        theStream.send(new IqQueryRoster(jid, name, group, null));
        if (askSubscribe) {
            theStream.send(new Presence(jid, "subscribe"));
        }
    }

    public void loginMessage(String msg, int pos) {
        setProgress(msg, pos);
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
//#ifdef AUTOSTATUS
//#         if (cf.autoAwayType==Config.AWAY_IDLE)
//#             AutoStatusTask.getInstance().setTimeEvent(0);
//#endif
    }

    public void keyGreen() {
        if (!isLoggedIn()) {
            return;
        }
        VirtualList pview = createMsgList();
        if (pview != null) {
            Contact c = (Contact) getFocusedObject();
            Roster.me = null;
            Roster.me = new MessageEdit(pview, c, c.msgSuspended);
            Roster.me.show();
            c.msgSuspended = null;
        }
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
            } catch (Exception e) { /* NullPointerException */ }
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
                collapseAllGroup();
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
                if (isLoggedIn()) {
                    new Bookmarks(null);
                }
                return true;
            case 3:
                cmdActiveContacts();
                return true;
            case 6:
                Config.fullscreen = !Config.fullscreen;
                sd.canvas.setFullScreenMode(Config.fullscreen);
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
                connectionTerminated(new Exception(SR.MS_SIMULATED_BREAK));
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
            case 7:
                cmdArchive();
                return true;
//#endif
//#ifdef POPUPS
            case 11:
                cmdClearPopups();
                return true;
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
                collapseAllGroup();
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

    /* TODO: debug with collapse group: I get NPE in VirtualList.paint().
    public void paint(javax.microedition.lcdui.Graphics g) {
    try { // NPE and (possibility) ArrayIndexOutOfBoundsException with uncomplete reenum Roster.
    super.paint(g);
    } catch (Exception e) {
    //#ifdef DEBUG
    //#             e.printStackTrace();
    //#endif
    }
    }
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
            boolean needUpdatePrivacy = false;
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
                                if (!sd.account.isGoogle) {
                                    if (QuickPrivacy.groupsList == null) {
                                        QuickPrivacy.groupsList = new Vector();
                                    }
                                    if (c.group.type != Groups.TYPE_MUC) {
                                        if (!QuickPrivacy.groupsList.contains(c.group.name)) {
                                            QuickPrivacy.groupsList.addElement(c.group.name);
                                            needUpdatePrivacy = true;
                                        }
                                    }
                                }
//#endif
                            }
                        }
                    }
//#ifdef PRIVACY                        
                    if (!sd.account.isGoogle) {
                        if (needUpdatePrivacy && isLoggedIn()) {
                            new QuickPrivacy().updateQuickPrivacyList();
                        }
                    }
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
//#ifdef DEBUG
//#                 //e.printStackTrace();
//#endif
            }
            //thread=null;            
        }
    }
}
