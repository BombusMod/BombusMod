/*
 * ConfigForm.java
 *
 * Created on 20.05.2008, 22:47
 * Copyright (c) 2006-2008, Daniel Apatin (ad), http://apatin.net.ru
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
import java.util.Vector;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import locale.SR;
import ui.VirtualList;
import ui.controls.form.CheckBox;
import ui.controls.form.DropChoiceBox;
import ui.controls.form.DefForm;
import ui.controls.form.NumberInput;
import ui.controls.form.SimpleString;
import ui.controls.form.SpacerItem;
import util.StringLoader;
import com.alsutton.jabber.datablocks.Presence;
import xmpp.EntityCaps;

public class ConfigForm
        extends DefForm {
    
    private Display display;
    private Displayable parentView;
    
    private CheckBox showOfflineContacts;
    private CheckBox selfContact;
    private CheckBox showTransports;
    private CheckBox ignore;
    private CheckBox collapsedGroups;
    private CheckBox autoFocus;
    private CheckBox showResources;
    private CheckBox useBoldFont;
    private CheckBox rosterStatus;
//#ifdef CLIENTS_ICONS
//#     private CheckBox showClientIcon;
//#endif
    
    private DropChoiceBox subscr;
    private DropChoiceBox nil;
    
//#ifdef SMILES
    private CheckBox smiles;
//#endif
    private CheckBox eventComposing;
    private CheckBox capsState;
    private CheckBox storeConfPresence;
    private CheckBox autoScroll;
    private CheckBox useTabs;
//#ifdef PEP
//#     private CheckBox sndrcvmood;
//#ifdef PEP_TUNE
//#    private CheckBox rcvtune;
//#endif
//#endif
     private CheckBox notifyWhenMessageType;
//#ifdef ANTISPAM
//#    private CheckBox antispam;
//#endif
//#ifdef POPUPS
    private CheckBox popUps;
//#endif
    private CheckBox showBalloons;     
    private CheckBox eventDelivery;
    
//#ifdef DETRANSLIT
//#     private CheckBox autoDetranslit;
//#endif
//#ifdef CLIPBOARD
//#     private CheckBox useClipBoard;
//#endif
    
//#if AUTODELETE
//#         private NumberInput messageCountLimit;
//#endif
    private NumberInput messageLimit;
    
    private CheckBox autoLogin;
    private CheckBox autoJoinConferences;

    private CheckBox fullscreen;
    private CheckBox memMonitor;
    private CheckBox enableVersionOs;
    private CheckBox queryExit;
//#ifdef USER_KEYS
//#    private CheckBox userKeys;
//#endif
//#ifdef NEW_MENU
    private CheckBox newMenu;
//#endif
    private CheckBox lightState;
    private CheckBox popupFromMinimized;

    private NumberInput fieldGmt; 
    private NumberInput fieldLoc;
    
    private DropChoiceBox textWrap;
    private DropChoiceBox langFiles;
//#ifdef AUTOSTATUS
//#     private DropChoiceBox autoAwayType;
//#     private NumberInput fieldAwayDelay; 
//#     private CheckBox awayStatus;
//#endif

    private Vector langs[];

    StaticData sd=StaticData.getInstance();
    
    Config cf;
    
    /** Creates a new instance of ConfigForm */
    public ConfigForm(Display display) {
        super(display, SR.MS_OPTIONS);
        this.display=display;
        parentView=display.getCurrent();

        cf=Config.getInstance();

        itemsList.addElement(new SimpleString(SR.MS_ROSTER_ELEMENTS, true));
        showOfflineContacts = new CheckBox(SR.MS_OFFLINE_CONTACTS, cf.showOfflineContacts); itemsList.addElement(showOfflineContacts);
        selfContact = new CheckBox(SR.MS_SELF_CONTACT, cf.selfContact); itemsList.addElement(selfContact);
        showTransports = new CheckBox(SR.MS_TRANSPORTS, cf.showTransports); itemsList.addElement(showTransports);
        ignore = new CheckBox(SR.MS_IGNORE_LIST, cf.ignore); itemsList.addElement(ignore);
        collapsedGroups = new CheckBox(SR.MS_COLLAPSED_GROUPS, cf.collapsedGroups); itemsList.addElement(collapsedGroups);
        autoFocus = new CheckBox(SR.MS_AUTOFOCUS, cf.autoFocus); itemsList.addElement(autoFocus);
        showResources = new CheckBox(SR.MS_SHOW_RESOURCES, cf.showResources); itemsList.addElement(showResources);
        useBoldFont = new CheckBox(SR.MS_BOLD_FONT, cf.useBoldFont); itemsList.addElement(useBoldFont);
        rosterStatus = new CheckBox(SR.MS_SHOW_STATUSES, cf.rosterStatus); itemsList.addElement(rosterStatus);
//#ifdef CLIENTS_ICONS
//#         showClientIcon = new CheckBox(SR.MS_SHOW_CLIENTS_ICONS, cf.showClientIcon); itemsList.addElement(showClientIcon);
//#endif
        
        itemsList.addElement(new SpacerItem(10));
        itemsList.addElement(new SimpleString(SR.MS_AUTH_NEW, true));
        subscr=new DropChoiceBox(display);
        subscr.append(SR.MS_SUBSCR_AUTO);
        subscr.append(SR.MS_SUBSCR_ASK);
        subscr.append(SR.MS_SUBSCR_DROP);
        subscr.append(SR.MS_SUBSCR_REJECT);
        subscr.setSelectedIndex(cf.autoSubscribe);
        itemsList.addElement(subscr);

        itemsList.addElement(new SpacerItem(10));
        itemsList.addElement(new SimpleString(SR.MS_NOT_IN_LIST, true));
        nil=new DropChoiceBox(display);
        nil.append(SR.MS_NIL_DROP_MP);
        nil.append(SR.MS_NIL_DROP_P);
        nil.append(SR.MS_NIL_ALLOW_ALL);
        nil.setSelectedIndex((cf.notInListDropLevel>NotInListFilter.ALLOW_ALL)? NotInListFilter.ALLOW_ALL: cf.notInListDropLevel);
        itemsList.addElement(nil);

        itemsList.addElement(new SpacerItem(10));
        itemsList.addElement(new SimpleString(SR.MS_MESSAGES, true));
//#ifdef SMILES
            smiles = new CheckBox(SR.MS_SMILES, cf.smiles); itemsList.addElement(smiles);
//#endif
            eventComposing = new CheckBox(SR.MS_COMPOSING_EVENTS, cf.eventComposing); itemsList.addElement(eventComposing);
            capsState = new CheckBox(SR.MS_CAPS_STATE, cf.capsState); itemsList.addElement(capsState);
            storeConfPresence = new CheckBox(SR.MS_STORE_PRESENCE, cf.storeConfPresence); itemsList.addElement(storeConfPresence);
            autoScroll = new CheckBox(SR.MS_AUTOSCROLL, cf.autoScroll); itemsList.addElement(autoScroll);
            useTabs = new CheckBox(SR.MS_EMULATE_TABS, cf.useTabs); itemsList.addElement(useTabs);
//#ifdef PEP
//#             sndrcvmood = new CheckBox(SR.MS_SEND_RECEIVE_USERMOODS, cf.sndrcvmood); itemsList.addElement(sndrcvmood);
//#ifdef PEP_TUNE
//#             rcvtune = new CheckBox(SR.MS_RECEIVE_USERTUNE, cf.rcvtune); itemsList.addElement(rcvtune);
//#endif
//#endif
            notifyWhenMessageType = new CheckBox(SR.MS_RUNNING_MESSAGE, cf.notifyWhenMessageType); itemsList.addElement(notifyWhenMessageType);
//#ifdef ANTISPAM
//#             antispam = new CheckBox(SR.MS_ANTISPAM_CONFERENCE, cf.antispam); itemsList.addElement(antispam);
//#endif
//#ifdef POPUPS
            popUps = new CheckBox(SR.MS_POPUPS, cf.popUps); itemsList.addElement(popUps);
//#endif
            showBalloons = new CheckBox(SR.MS_SHOW_BALLONS, cf.showBalloons); itemsList.addElement(showBalloons);     
            eventDelivery = new CheckBox(SR.MS_DELIVERY, cf.eventDelivery); itemsList.addElement(eventDelivery);
//#ifdef CLIPBOARD
//#             useClipBoard = new CheckBox(SR.MS_CLIPBOARD, cf.useClipBoard); itemsList.addElement(useClipBoard);
//#endif
//#ifdef DETRANSLIT
//#            autoDetranslit = new CheckBox(SR.MS_AUTODETRANSLIT, cf.autoDeTranslit); itemsList.addElement(autoDetranslit);
//#endif

            
//#if AUTODELETE
//#         itemsList.addElement(new SimpleString(SR.MS_MESSAGE_COUNT_LIMIT));
//#         messageCountLimit=new NumberInput(display, Integer.toString(cf.msglistLimit), 10, 1000);
//#         itemsList.addElement(messageCountLimit);
//#endif

        itemsList.addElement(new SpacerItem(10));
        messageLimit=new NumberInput(display, SR.MS_MESSAGE_COLLAPSE_LIMIT, Integer.toString(cf.messageLimit), 200, 1000);
        itemsList.addElement(messageLimit);
        
        itemsList.addElement(new SpacerItem(10));
        itemsList.addElement(new SimpleString(SR.MS_STARTUP_ACTIONS, true));
        autoLogin = new CheckBox(SR.MS_AUTOLOGIN, cf.autoLogin); itemsList.addElement(autoLogin);
        autoJoinConferences = new CheckBox(SR.MS_AUTO_CONFERENCES, cf.autoJoinConferences); itemsList.addElement(autoJoinConferences);
        
        itemsList.addElement(new SpacerItem(10));
        itemsList.addElement(new SimpleString(SR.MS_APPLICATION, true));
        fullscreen = new CheckBox(SR.MS_FULLSCREEN, cf.fullscreen); itemsList.addElement(fullscreen);
        memMonitor = new CheckBox(SR.MS_HEAP_MONITOR, cf.memMonitor); itemsList.addElement(memMonitor);
        enableVersionOs = new CheckBox(SR.MS_SHOW_HARDWARE, cf.enableVersionOs); itemsList.addElement(enableVersionOs);
        queryExit = new CheckBox(SR.MS_CONFIRM_EXIT, cf.queryExit); itemsList.addElement(queryExit);
//#ifdef USER_KEYS
//#         userKeys = new CheckBox(SR.MS_CUSTOM_KEYS, cf.userKeys); itemsList.addElement(userKeys);
//#endif
//#ifdef NEW_MENU
        newMenu = new CheckBox(SR.MS_NEW_MENU, cf.newMenu); itemsList.addElement(newMenu);
//#endif
        lightState = new CheckBox(SR.MS_FLASHLIGHT, cf.lightState); itemsList.addElement(lightState);
        if (cf.allowMinimize) {
            popupFromMinimized = new CheckBox(SR.MS_ENABLE_POPUP, cf.popupFromMinimized);
            itemsList.addElement(popupFromMinimized);
        }
        
        itemsList.addElement(new SpacerItem(10));
        itemsList.addElement(new SimpleString(SR.MS_TIME_SETTINGS, true));
        itemsList.addElement(new SimpleString(SR.MS_GMT_OFFSET, false));
	fieldGmt=new NumberInput(display, null, Integer.toString(cf.gmtOffset), -12, 12); 
        itemsList.addElement(fieldGmt);
        
        itemsList.addElement(new SimpleString(SR.MS_CLOCK_OFFSET, false));
        fieldLoc=new NumberInput(display, null, Integer.toString(cf.locOffset), -12, 12 );
        itemsList.addElement(fieldLoc);

        itemsList.addElement(new SpacerItem(10));
        itemsList.addElement(new SimpleString(SR.MS_TEXTWRAP, true));
        textWrap=new DropChoiceBox(display);
        textWrap.append(SR.MS_TEXTWRAP_CHARACTER);
        textWrap.append(SR.MS_TEXTWRAP_WORD);
	textWrap.setSelectedIndex(cf.textWrap);
	itemsList.addElement(textWrap);
        
        itemsList.addElement(new SpacerItem(10));
        itemsList.addElement(new SimpleString(SR.MS_LANGUAGE, true));
        langFiles=new DropChoiceBox(display);
	langs=new StringLoader().stringLoader("/lang/res.txt",3);
        
        String tempLang=cf.lang;
        if (tempLang==null) { //not detected
            String locale=System.getProperty("microedition.locale");  
            if (locale!=null) {
                tempLang=locale.substring(0, 2).toLowerCase();
            }
        }

	for (int i=0; i<langs[0].size(); i++) {
            String label=(String) langs[2].elementAt(i);
            String langCode=(String) langs[0].elementAt(i);
	    langFiles.append(label);
            if (tempLang.equals(langCode))
                langFiles.setSelectedIndex(i);
        }
        itemsList.addElement(langFiles);

//#ifdef AUTOSTATUS
//#         itemsList.addElement(new SpacerItem(10));
//#         itemsList.addElement(new SimpleString(SR.MS_AWAY_TYPE, true));
//#         autoAwayType=new DropChoiceBox(display);
//#         autoAwayType.append(SR.MS_AWAY_OFF);
//#         autoAwayType.append(SR.MS_AWAY_LOCK);
//#         autoAwayType.append(SR.MS_MESSAGE_LOCK);
//#         autoAwayType.append(SR.MS_IDLE);
//#         autoAwayType.setSelectedIndex(cf.autoAwayType);
//#         itemsList.addElement(autoAwayType);
//# 
//#         fieldAwayDelay=new NumberInput(display, SR.MS_AWAY_PERIOD, Integer.toString(cf.autoAwayDelay), 1, 60);
//#         itemsList.addElement(fieldAwayDelay);
//# 
//#         awayStatus=new CheckBox(SR.MS_AUTOSTATUS_MESSAGE, cf.setAutoStatusMessage);
//#         itemsList.addElement(awayStatus);
//#endif
        
        enableListWrapping(false);
        attachDisplay(display);
    }
    
    public void cmdOk() {
        cf.showOfflineContacts=showOfflineContacts.getValue();
        cf.selfContact=selfContact.getValue();
        cf.showTransports=showTransports.getValue();
        cf.ignore=ignore.getValue();
        cf.collapsedGroups=collapsedGroups.getValue();
        cf.autoFocus=autoFocus.getValue();
        cf.showResources=showResources.getValue();
        cf.useBoldFont=useBoldFont.getValue();
        cf.rosterStatus=rosterStatus.getValue();
//#ifdef CLIENTS_ICONS
//#         cf.showClientIcon=showClientIcon.getValue();
//#endif
        cf.autoSubscribe=subscr.getSelectedIndex();
        
        cf.notInListDropLevel=nil.getSelectedIndex();

//#ifdef SMILES
        cf.smiles=smiles.getValue();
//#endif
        cf.eventComposing=eventComposing.getValue();
        cf.capsState=capsState.getValue();
        cf.storeConfPresence=storeConfPresence.getValue();
        cf.autoScroll=autoScroll.getValue();
        cf.useTabs=useTabs.getValue();
//#ifdef PEP
//#         cf.sndrcvmood=sndrcvmood.getValue();
//#ifdef PEP_TUNE
//#             cf.rcvtune=rcvtune.getValue();
//#endif
//#endif
        cf.notifyWhenMessageType=notifyWhenMessageType.getValue();
//#ifdef ANTISPAM
//#             cf.antispam=antispam.getValue();
//#endif
//#ifdef POPUPS
        cf.popUps=popUps.getValue();
//#endif
        cf.showBalloons=showBalloons.getValue();
        VirtualList.showBalloons=cf.showBalloons;
        cf.eventDelivery=eventDelivery.getValue();
//#ifdef CLIPBOARD
//#         cf.useClipBoard=useClipBoard.getValue();
//#endif
//#ifdef DETRANSLIT
//#         cf.autoDeTranslit=autoDetranslit.getValue();
//#endif

        cf.autoLogin=autoLogin.getValue();
        cf.autoJoinConferences=autoJoinConferences.getValue();

        VirtualList.fullscreen=cf.fullscreen=fullscreen.getValue();
        VirtualList.memMonitor=cf.memMonitor=memMonitor.getValue();
        cf.enableVersionOs=enableVersionOs.getValue();
        cf.queryExit=queryExit.getValue();
//#ifdef USER_KEYS
//#             VirtualList.userKeys=cf.userKeys=userKeys.getValue();
//#endif
//#ifdef NEW_MENU
        cf.newMenu=newMenu.getValue();
//#endif
        cf.lightState=lightState.getValue();
        if (cf.allowMinimize)
            cf.popupFromMinimized=popupFromMinimized.getValue();

        cf.gmtOffset=Integer.parseInt(fieldGmt.getValue());
        cf.locOffset=Integer.parseInt(fieldLoc.getValue());

        cf.textWrap=textWrap.getSelectedIndex();

        cf.lang=(String) langs[0].elementAt( langFiles.getSelectedIndex() );
//#ifdef AUTOSTATUS
//#             cf.setAutoStatusMessage=awayStatus.getValue();
//#             cf.autoAwayDelay=Integer.parseInt(fieldAwayDelay.getValue());
//#             cf.autoAwayType=autoAwayType.getSelectedIndex();
//#endif
        cf.messageLimit=Integer.parseInt(messageLimit.getValue());
//#if AUTODELETE
//#             cf.msglistLimit=Integer.parseInt(messageCountLimit.getValue());
//#endif

        sd.roster.setLight(cf.lightState);   

        sd.roster.setFullScreenMode(cf.fullscreen);

        cf.firstRun=false;

        cf.updateTime();
        cf.saveToStorage();

        String oldVerHash=EntityCaps.calcVerHash();
        EntityCaps.initCaps();
        if (!oldVerHash.equals(EntityCaps.calcVerHash())) 
            sd.roster.sendPresence(Presence.PRESENCE_SAME, null);

        sd.roster.reEnumRoster();
        destroyView();
    }

    public void destroyView(){
        if (display!=null)  
            display.setCurrent(sd.roster);
        ((Canvas)parentView).setFullScreenMode(cf.fullscreen);
    }
}
