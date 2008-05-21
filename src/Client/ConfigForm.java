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
//#ifndef COLORS
//# import Colors.ColorUtils;
//#endif
import java.util.Vector;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import locale.SR;
import ui.VirtualList;
import ui.controls.form.boldString;
import ui.controls.form.checkBox;
import ui.controls.form.choiceBox;
import ui.controls.form.defForm;
import ui.controls.form.numberInput;
import ui.controls.form.simpleString;
import util.StringLoader;
import com.alsutton.jabber.datablocks.Presence;
import xmpp.EntityCaps;

public class ConfigForm
        extends defForm {
    
    private Display display;
    private Displayable parentView;
    
    private checkBox showOfflineContacts;
    private checkBox selfContact;
    private checkBox showTransports;
    private checkBox ignore;
    private checkBox collapsedGroups;
    private checkBox autoFocus;
    private checkBox showResources;
    private checkBox useBoldFont;
//#ifdef SECONDSTRING
//#     private checkBox rosterStatus;
//#endif
    
    private choiceBox subscr;
    private choiceBox nil;
    
//#ifdef SMILES
    private checkBox smiles;
//#endif
    private checkBox eventComposing;
    private checkBox capsState;
    private checkBox storeConfPresence;
    private checkBox autoScroll;
    private checkBox useTabs;
//#ifdef PEP
//#     private checkBox sndrcvmood;
//#ifdef PEP_TUNE
//#    private checkBox rcvtune;
//#endif
//#endif
     private checkBox notifyWhenMessageType;
//#ifdef ANTISPAM
//#    private checkBox antispam;
//#endif
//#ifdef POPUPS
    private checkBox popUps;
//#endif
    private checkBox showBalloons;     
    private checkBox eventDelivery;
//#ifdef CLIPBOARD
//#     private checkBox useClipBoard;
//#endif
    
//#if AUTODELETE
//#         private numberInput messageCountLimit;
//#endif
    private numberInput messageLimit;
    
    private checkBox autoLogin;
    private checkBox autoJoinConferences;

    private checkBox fullscreen;
    private checkBox memMonitor;
    private checkBox enableVersionOs;
    private checkBox queryExit;
//#ifdef USER_KEYS
//#    private checkBox userKeys;
//#endif
//#ifdef NEW_MENU
    private checkBox newMenu;
//#endif
    private checkBox lightState;
//#ifdef IRC_LIKE
//#    private checkBox ircLikeStatus;
//#endif
    private checkBox popupFromMinimized;

    private numberInput fieldGmt; 
    private numberInput fieldLoc;
    
    private choiceBox textWrap;
    private choiceBox langFiles;
    private choiceBox skinFiles;
//#ifdef AUTOSTATUS
//#     private choiceBox autoAwayType;
//#     private numberInput fieldAwayDelay; 
//#     private checkBox awayStatus;
//#endif
    
    private Vector[] Skinfiles;
    private Vector langs[];

    StaticData sd=StaticData.getInstance();
    
    Config cf;
    
    /** Creates a new instance of ConfigForm */
    public ConfigForm(Display display) {
        super(display, SR.MS_OPTIONS);
        this.display=display;
        parentView=display.getCurrent();

        cf=Config.getInstance();

        itemsList.addElement(new boldString(SR.MS_ROSTER_ELEMENTS));
        showOfflineContacts = new checkBox(SR.MS_OFFLINE_CONTACTS, cf.showOfflineContacts); itemsList.addElement(showOfflineContacts);
        selfContact = new checkBox(SR.MS_SELF_CONTACT, cf.selfContact); itemsList.addElement(selfContact);
        showTransports = new checkBox(SR.MS_TRANSPORTS, cf.showTransports); itemsList.addElement(showTransports);
        ignore = new checkBox(SR.MS_IGNORE_LIST, cf.ignore); itemsList.addElement(ignore);
        collapsedGroups = new checkBox(SR.MS_COLLAPSED_GROUPS, cf.collapsedGroups); itemsList.addElement(collapsedGroups);
        autoFocus = new checkBox(SR.MS_AUTOFOCUS, cf.autoFocus); itemsList.addElement(autoFocus);
        showResources = new checkBox(SR.MS_SHOW_RESOURCES, cf.showResources); itemsList.addElement(showResources);
        useBoldFont = new checkBox(SR.MS_BOLD_FONT, cf.useBoldFont); itemsList.addElement(useBoldFont);
//#ifdef SECONDSTRING
//#         rosterStatus = new checkBox(SR.MS_SECOND_LINE, cf.rosterStatus); itemsList.addElement(rosterStatus);
//#endif

        itemsList.addElement(new boldString(SR.MS_AUTH_NEW));
        subscr=new choiceBox();
        subscr.append(SR.MS_SUBSCR_AUTO);
        subscr.append(SR.MS_SUBSCR_ASK);
        subscr.append(SR.MS_SUBSCR_DROP);
        subscr.append(SR.MS_SUBSCR_REJECT);
        subscr.setSelectedIndex(cf.autoSubscribe);
        itemsList.addElement(subscr);

        itemsList.addElement(new boldString(SR.MS_NOT_IN_LIST));
        nil=new choiceBox();
        nil.append(SR.MS_NIL_DROP_MP);
        nil.append(SR.MS_NIL_DROP_P);
        nil.append(SR.MS_NIL_ALLOW_ALL);
        nil.setSelectedIndex((cf.notInListDropLevel>NotInListFilter.ALLOW_ALL)? NotInListFilter.ALLOW_ALL: cf.notInListDropLevel);
        itemsList.addElement(nil);

        itemsList.addElement(new boldString(SR.MS_MESSAGES));
//#ifdef SMILES
            smiles = new checkBox(SR.MS_SMILES, cf.smiles); itemsList.addElement(smiles);
//#endif
            eventComposing = new checkBox(SR.MS_COMPOSING_EVENTS, cf.eventComposing); itemsList.addElement(eventComposing);
            capsState = new checkBox(SR.MS_CAPS_STATE, cf.capsState); itemsList.addElement(capsState);
            storeConfPresence = new checkBox(SR.MS_STORE_PRESENCE, cf.storeConfPresence); itemsList.addElement(storeConfPresence);
            autoScroll = new checkBox(SR.MS_AUTOSCROLL, cf.autoScroll); itemsList.addElement(autoScroll);
            useTabs = new checkBox(SR.MS_EMULATE_TABS, cf.useTabs); itemsList.addElement(useTabs);
//#ifdef PEP
//#             sndrcvmood = new checkBox(SR.MS_SEND_RECEIVE_USERMOODS, cf.sndrcvmood); itemsList.addElement(sndrcvmood);
//#ifdef PEP_TUNE
//#             rcvtune = new checkBox(SR.MS_RECEIVE_USERTUNE, cf.rcvtune); itemsList.addElement(rcvtune);
//#endif
//#endif
            notifyWhenMessageType = new checkBox(SR.MS_RUNNING_MESSAGE, cf.notifyWhenMessageType); itemsList.addElement(notifyWhenMessageType);
//#ifdef ANTISPAM
//#             antispam = new checkBox(SR.MS_ANTISPAM_CONFERENCE, cf.antispam); itemsList.addElement(antispam);
//#endif
//#ifdef POPUPS
            popUps = new checkBox(SR.MS_POPUPS, cf.popUps); itemsList.addElement(popUps);
//#endif
            showBalloons = new checkBox(SR.MS_SHOW_BALLONS, cf.showBalloons); itemsList.addElement(showBalloons);     
            eventDelivery = new checkBox(SR.MS_DELIVERY, cf.eventDelivery); itemsList.addElement(eventDelivery);
//#ifdef CLIPBOARD
//#             useClipBoard = new checkBox(SR.MS_CLIPBOARD, cf.useClipBoard); itemsList.addElement(useClipBoard);
//#endif

            
//#if AUTODELETE
//#         itemsList.addElement(new simpleString(SR.MS_MESSAGE_COUNT_LIMIT));
//#         messageCountLimit=new numberInput(display, Integer.toString(cf.msglistLimit), 10, 1000);
//#         itemsList.addElement(messageCountLimit);
//#endif

        itemsList.addElement(new simpleString(SR.MS_MESSAGE_COLLAPSE_LIMIT));
        messageLimit=new numberInput(display, Integer.toString(cf.messageLimit), 200, 1000);
        itemsList.addElement(messageLimit);
        
        itemsList.addElement(new boldString(SR.MS_STARTUP_ACTIONS));
        autoLogin = new checkBox(SR.MS_AUTOLOGIN, cf.autoLogin); itemsList.addElement(autoLogin);
        autoJoinConferences = new checkBox(SR.MS_AUTO_CONFERENCES, cf.autoJoinConferences); itemsList.addElement(autoJoinConferences);
        
        itemsList.addElement(new boldString(SR.MS_APPLICATION));
        fullscreen = new checkBox(SR.MS_FULLSCREEN, cf.fullscreen); itemsList.addElement(fullscreen);
        memMonitor = new checkBox(SR.MS_HEAP_MONITOR, cf.memMonitor); itemsList.addElement(memMonitor);
        enableVersionOs = new checkBox(SR.MS_SHOW_HARDWARE, cf.enableVersionOs); itemsList.addElement(enableVersionOs);
        queryExit = new checkBox(SR.MS_CONFIRM_EXIT, cf.queryExit); itemsList.addElement(queryExit);
//#ifdef USER_KEYS
//#         userKeys = new checkBox(SR.MS_CUSTOM_KEYS, cf.userKeys); itemsList.addElement(userKeys);
//#endif
//#ifdef NEW_MENU
        newMenu = new checkBox(SR.MS_NEW_MENU, cf.newMenu); itemsList.addElement(newMenu);
//#endif
        lightState = new checkBox(SR.MS_FLASHLIGHT, cf.lightState); itemsList.addElement(lightState);
//#ifdef IRC_LIKE
//#         ircLikeStatus = new checkBox(SR.MS_IRCLIKESTATUS, cf.ircLikeStatus); itemsList.addElement(ircLikeStatus);
//#endif
        if (cf.allowMinimize) {
            popupFromMinimized = new checkBox(SR.MS_ENABLE_POPUP, cf.popupFromMinimized);
            itemsList.addElement(popupFromMinimized);
        }
        

        itemsList.addElement(new boldString(SR.MS_TIME_SETTINGS));
        itemsList.addElement(new simpleString(SR.MS_GMT_OFFSET));
	fieldGmt=new numberInput(display, Integer.toString(cf.gmtOffset), -12, 12); 
        itemsList.addElement(fieldGmt);
        
        itemsList.addElement(new simpleString(SR.MS_CLOCK_OFFSET));
        fieldLoc=new numberInput(display, Integer.toString(cf.locOffset), -12, 12 );
        itemsList.addElement(fieldLoc);

        try {
            Skinfiles=new StringLoader().stringLoader("/skins/res.txt",2);
            if (Skinfiles[0].size()>0) {
                String tempScheme=(cf.scheme=="")?"default":cf.scheme;
                itemsList.addElement(new boldString(SR.MS_LOAD_SKIN));
                skinFiles=new choiceBox();

                for (int i=0; i<Skinfiles[0].size(); i++) {
                    String schemeName = (String)Skinfiles[1].elementAt(i);
                    skinFiles.append(schemeName);
//#ifndef COLORS
//#                 if (tempScheme.equals(schemeName))
//#                     skinFiles.setSelectedIndex(i);
//#endif
                }
                itemsList.addElement(skinFiles);
            }
        } catch (Exception e) {}

        itemsList.addElement(new boldString(SR.MS_TEXTWRAP));
        textWrap=new choiceBox();
        textWrap.append(SR.MS_TEXTWRAP_CHARACTER);
        textWrap.append(SR.MS_TEXTWRAP_WORD);
	textWrap.setSelectedIndex(cf.textWrap);
	itemsList.addElement(textWrap);
        
        itemsList.addElement(new boldString(SR.MS_LANGUAGE));
        langFiles=new choiceBox();
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
//#         itemsList.addElement(new boldString(SR.MS_AWAY_TYPE));
//#         autoAwayType=new choiceBox();
//#         autoAwayType.append(SR.MS_AWAY_OFF);
//#         autoAwayType.append(SR.MS_AWAY_LOCK);
//#         autoAwayType.append(SR.MS_MESSAGE_LOCK);
//#         autoAwayType.append(SR.MS_IDLE);
//#         autoAwayType.setSelectedIndex(cf.autoAwayType);
//#         itemsList.addElement(autoAwayType);
//#                 
//#         itemsList.addElement(new simpleString(SR.MS_AWAY_PERIOD));
//#         fieldAwayDelay=new numberInput(display, Integer.toString(cf.autoAwayDelay), 1, 60);
//#         itemsList.addElement(fieldAwayDelay);
//# 
//#         awayStatus=new checkBox(SR.MS_AUTOSTATUS_MESSAGE, cf.setAutoStatusMessage);
//#         itemsList.addElement(awayStatus);
//#endif
        
        moveCursorTo(getNextSelectableRef(-1));
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
//#ifdef SECONDSTRING
//#         cf.rosterStatus=rosterStatus.getValue();
//#endif

        cf.autoSubscribe=subscr.getSelectedIndex();

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
//#ifdef IRC_LIKE
//#             cf.ircLikeStatus=ircLikeStatus.getValue();
//#endif
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

//#ifndef COLORS
//#         try {
//#             if (skinFiles.getSelectedIndex()>-1) {
//#                 String tempScheme=(String) Skinfiles[1].elementAt( skinFiles.getSelectedIndex() );
//#                 if (!tempScheme.equals(cf.scheme)) {
//#                     cf.scheme=(String) Skinfiles[1].elementAt( skinFiles.getSelectedIndex() );
//#                     ColorUtils.loadSkin((String)Skinfiles[0].elementAt(skinFiles.getSelectedIndex()), 1);
//#                 }
//#             }
//#         } catch (Exception ex) {}
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
