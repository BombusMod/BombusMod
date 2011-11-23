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
import ui.VirtualCanvas;
import xmpp.EntityCaps;

public class ConfigForm
        extends DefForm {  
    
    
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
    private CheckBox showClientIcon;
//#endif
    
    private DropChoiceBox subscr;
    
//#ifdef SMILES
    private CheckBox smiles;
//#endif
    private CheckBox eventComposing;
    private CheckBox capsState;
    private CheckBox storeConfPresence;
    private CheckBox autoScroll;
    private CheckBox useTabs;
    private CheckBox showBalloons;     
    private CheckBox eventDelivery;
    private CheckBox executeByNum;
    
//#ifdef DETRANSLIT
//#     private CheckBox autoDetranslit;
//#endif
//#ifdef CLIPBOARD
//#     private CheckBox useClipBoard;
//#endif
    
//#if LOGROTATE
//#     private NumberInput messageCountLimit;
//#endif
    private NumberInput messageLimit;
    private NumberInput widthScroll2;
    private NumberInput minItemHeight;
    
    private CheckBox autoLogin;
    private CheckBox autoJoinConferences;

    private NumberInput reconnectCount;
    private NumberInput reconnectTime;
//#ifdef FILE_TRANSFER
    private CheckBox fileTransfer;
//#endif
//#ifdef HISTORY
//#     private CheckBox saveHistory;
//#endif
//#ifdef ADHOC
//#     private CheckBox adhoc;
//#endif
    private CheckBox fullscreen;
//#ifdef MEMORY_USAGE
//#     private CheckBox memMonitor;
//#endif
    private CheckBox enableVersionOs;
    private CheckBox queryExit;
    private CheckBox lightState;
    private CheckBox popupFromMinimized;
    private CheckBox widthSystemgc;
    private CheckBox advTouch;
    private CheckBox autoClean;
    private NumberInput fieldGmt; 
    
    private DropChoiceBox textWrap;
    private DropChoiceBox langFiles;
//#ifdef AUTOSTATUS
//#     private DropChoiceBox autoAwayType;
//#     private NumberInput fieldAwayDelay; 
//#     private CheckBox awayStatus;
//#endif
    
//#ifdef RUNNING_MESSAGE
//#      private CheckBox notifyWhenMessageType;
//#endif
//#ifdef POPUPS
    private CheckBox popUps;
//#endif
    

    private DropChoiceBox panels;
    private CheckBox drawMenuCommand;
    
    private CheckBox showNickNames;
    
    private CheckBox swapSendAndSuspend;

    private Vector langs[];

    /** Creates a new instance of ConfigForm
     */
    public ConfigForm() {
        super(SR.MS_OPTIONS);
        
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
        showClientIcon = new CheckBox(SR.MS_SHOW_CLIENTS_ICONS, cf.showClientIcon);
            itemsList.addElement(showClientIcon);
//#endif
        autoClean = new CheckBox(SR.MS_AUTOCLEAN_GROUPS, cf.autoClean);
        itemsList.addElement(autoClean);
        
        itemsList.addElement(new SpacerItem(10));
        subscr=new DropChoiceBox(SR.MS_AUTH_NEW);
        subscr.add(SR.MS_SUBSCR_AUTO);
        subscr.add(SR.MS_SUBSCR_ASK);
        subscr.add(SR.MS_SUBSCR_DROP);
        subscr.add(SR.MS_SUBSCR_REJECT);
        subscr.setSelectedIndex(cf.autoSubscribe);
        itemsList.addElement(subscr);

        itemsList.addElement(new SpacerItem(10));
                

        itemsList.addElement(new SpacerItem(10));
        itemsList.addElement(new SimpleString(SR.MS_MESSAGES, true));
//#ifdef SMILES
        smiles = new CheckBox(SR.MS_SMILES, cf.smiles); itemsList.addElement(smiles);
//#endif
        eventComposing = new CheckBox(SR.MS_COMPOSING_EVENTS, cf.eventComposing); itemsList.addElement(eventComposing);
        capsState = new CheckBox(SR.MS_CAPS_STATE, cf.capsState); itemsList.addElement(capsState);
//#ifndef WMUC        
        storeConfPresence = new CheckBox(SR.MS_STORE_PRESENCE, cf.storeConfPresence); itemsList.addElement(storeConfPresence);
//#endif        
        autoScroll = new CheckBox(SR.MS_AUTOSCROLL, cf.autoScroll); itemsList.addElement(autoScroll);
        useTabs = new CheckBox(SR.MS_EMULATE_TABS, cf.useTabs); itemsList.addElement(useTabs);
//#ifdef RUNNING_MESSAGE
//#         notifyWhenMessageType = new CheckBox(SR.MS_RUNNING_MESSAGE, cf.notifyWhenMessageType); itemsList.addElement(notifyWhenMessageType);
//#endif
//#ifdef POPUPS
        popUps = new CheckBox(SR.MS_POPUPS, cf.popUps); itemsList.addElement(popUps);
//#endif
        showBalloons = new CheckBox(SR.MS_HIDE_TIMESTAMPS, cf.hideTimestamps); itemsList.addElement(showBalloons);
        eventDelivery = new CheckBox(SR.MS_DELIVERY, cf.eventDelivery); itemsList.addElement(eventDelivery);
//#ifdef CLIPBOARD
//#         useClipBoard = new CheckBox(SR.MS_CLIPBOARD, cf.useClipBoard); itemsList.addElement(useClipBoard);
//#endif
//#ifdef DETRANSLIT
//#         autoDetranslit = new CheckBox(SR.MS_AUTODETRANSLIT, cf.autoDeTranslit); itemsList.addElement(autoDetranslit);
//#endif
       showNickNames = new CheckBox(SR.MS_SHOW_NACKNAMES, cf.showNickNames); itemsList.addElement(showNickNames);
       swapSendAndSuspend = new CheckBox("swap \""+SR.MS_SEND+"\" and \""+SR.MS_SUSPEND+"\" commands", cf.swapSendAndSuspend); itemsList.addElement(swapSendAndSuspend);
            
//#if LOGROTATE
//#         messageCountLimit=new NumberInput(sd.canvas, SR.MS_MESSAGE_COUNT_LIMIT, Integer.toString(cf.msglistLimit), 3, 1000);
//#         itemsList.addElement(messageCountLimit);
//#endif

        itemsList.addElement(new SpacerItem(10));
        messageLimit=new NumberInput(sd.canvas,  SR.MS_MESSAGE_COLLAPSE_LIMIT, Integer.toString(cf.messageLimit), 200, 1000);
        itemsList.addElement(messageLimit);

        minItemHeight = new NumberInput(sd.canvas, SR.MS_ITEM_HEIGHT, Integer.toString(cf.minItemHeight), 0, 100);
        itemsList.addElement(minItemHeight);

        if (sd.canvas.hasPointerEvents()) {
            widthScroll2=new NumberInput(sd.canvas,  SR.MS_MESSAGE_WIDTH_SCROLL_2, Integer.toString(cf.widthScroll2), 1, 50);
            itemsList.addElement(widthScroll2);
            advTouch = new CheckBox(SR.MS_SINGLE_CLICK, cf.advTouch);
            itemsList.addElement(advTouch);
        }
        
        itemsList.addElement(new SpacerItem(10));
        itemsList.addElement(new SimpleString(SR.MS_STARTUP_ACTIONS, true));
        autoLogin = new CheckBox(SR.MS_AUTOLOGIN, cf.autoLogin); itemsList.addElement(autoLogin);
//#ifndef WMUC        
        autoJoinConferences = new CheckBox(SR.MS_AUTO_CONFERENCES, cf.autoJoinConferences); itemsList.addElement(autoJoinConferences);
//#endif        
        
        itemsList.addElement(new SpacerItem(10));
        itemsList.addElement(new SimpleString(SR.MS_RECONNECT, true));
	reconnectCount=new NumberInput(sd.canvas,  SR.MS_RECONNECT_COUNT_RETRY, Integer.toString(cf.reconnectCount), 0, 100); itemsList.addElement(reconnectCount);
        reconnectTime=new NumberInput(sd.canvas,  SR.MS_RECONNECT_WAIT, Integer.toString(cf.reconnectTime), 1, 60 ); itemsList.addElement(reconnectTime);
        
        itemsList.addElement(new SpacerItem(10));
        itemsList.addElement(new SimpleString(SR.MS_APPLICATION, true));
        fullscreen = new CheckBox(SR.MS_FULLSCREEN, Config.fullscreen); itemsList.addElement(fullscreen);
//#ifdef MEMORY_USAGE
//#         memMonitor = new CheckBox(SR.MS_HEAP_MONITOR, cf.memMonitor); itemsList.addElement(memMonitor);
//#endif
        enableVersionOs = new CheckBox(SR.MS_SHOW_HARDWARE, cf.enableVersionOs); itemsList.addElement(enableVersionOs);
        queryExit = new CheckBox(SR.MS_CONFIRM_EXIT, cf.queryExit); itemsList.addElement(queryExit);
//#ifdef LIGHT_CONFIG
//#         lightState = new CheckBox(SR.L_CONFIG, cf.lightState);
//#        if (phoneManufacturer==Config.SIEMENS || phoneManufacturer==Config.SIEMENS2 || phoneManufacturer==Config.SONYE || phoneManufacturer==Config.NOKIA) itemsList.addElement(lightState);
//#endif
        
//#ifdef FILE_TRANSFER
        fileTransfer = new CheckBox(SR.MS_FILE_TRANSFERS, cf.fileTransfer); 
            itemsList.addElement(fileTransfer);
//#endif
//#ifdef HISTORY
//#         saveHistory = new CheckBox(SR.MS_HISTORY, cf.saveHistory); 
//#         itemsList.addElement(saveHistory);
//#endif
//#ifdef ADHOC
//#         adhoc = new CheckBox(SR.MS_ADHOC, cf.adhoc); 
//#         itemsList.addElement(adhoc);
//#endif
        if (cf.allowMinimize) {
            popupFromMinimized = new CheckBox(SR.MS_ENABLE_POPUP, cf.popupFromMinimized);
            itemsList.addElement(popupFromMinimized);
        }
        executeByNum = new CheckBox(SR.MS_EXECUTE_MENU_BY_NUMKEY, cf.executeByNum); itemsList.addElement(executeByNum);
        
        itemsList.addElement(new SpacerItem(10));
        itemsList.addElement(new SimpleString(SR.MS_TIME_SETTINGS, true));
	fieldGmt=new NumberInput(sd.canvas,  SR.MS_GMT_OFFSET, Integer.toString(cf.gmtOffset), -12, 12);
        itemsList.addElement(fieldGmt);

        itemsList.addElement(new SpacerItem(10));
        textWrap=new DropChoiceBox(SR.MS_TEXTWRAP);
        textWrap.add(SR.MS_TEXTWRAP_CHARACTER);
        textWrap.add(SR.MS_TEXTWRAP_WORD);
	textWrap.setSelectedIndex(cf.textWrap);
	itemsList.addElement(textWrap);
        
        itemsList.addElement(new SpacerItem(10));
        panels=new DropChoiceBox(SR.MS_PANELS);
        panels.add(SR.MS_NO_BAR+" : "+SR.MS_NO_BAR);
        panels.add(SR.MS_MAIN_BAR+" : "+SR.MS_NO_BAR);
        panels.add(SR.MS_MAIN_BAR+" : "+SR.MS_INFO_BAR);
        panels.add(SR.MS_NO_BAR+" : "+SR.MS_INFO_BAR);
        panels.add(SR.MS_INFO_BAR+" : "+SR.MS_NO_BAR);
        panels.add(SR.MS_INFO_BAR+" : "+SR.MS_MAIN_BAR);
        panels.add(SR.MS_NO_BAR+" : "+SR.MS_MAIN_BAR);
	panels.setSelectedIndex(cf.panelsState);
	itemsList.addElement(panels);
        drawMenuCommand = new CheckBox(SR.MS_SHOW_TIME_TRAFFIC, cf.showTimeTraffic); 
        itemsList.addElement(drawMenuCommand);

//#ifdef AUTOSTATUS
//#         itemsList.addElement(new SpacerItem(10));
//#         autoAwayType=new DropChoiceBox(SR.MS_AWAY_TYPE);
//#         autoAwayType.add(SR.MS_AWAY_OFF);
//#         autoAwayType.add(SR.MS_AWAY_LOCK);
//#         autoAwayType.add(SR.MS_MESSAGE_LOCK);
//#         autoAwayType.add(SR.MS_IDLE);
//#         autoAwayType.setSelectedIndex(cf.autoAwayType);
//#         itemsList.addElement(autoAwayType);
//# 
//#         fieldAwayDelay=new NumberInput(sd.canvas, SR.MS_AWAY_PERIOD, Integer.toString(cf.autoAwayDelay), 1, 60);
//#         itemsList.addElement(fieldAwayDelay);
//# 
//#         awayStatus=new CheckBox(SR.MS_USE_MY_STATUS_MESSAGES, cf.useMyStatusMessages);
//#         itemsList.addElement(awayStatus);
//#endif

	langs=new StringLoader().stringLoader("/lang/res.txt",3);
        if (langs[0].size()>1) {
            itemsList.addElement(new SpacerItem(10));
            langFiles=new DropChoiceBox(SR.MS_LANGUAGE);
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
                langFiles.add(label);
                if (tempLang.equals(langCode))
                    langFiles.setSelectedIndex(i);
            }
            itemsList.addElement(langFiles);
        }
        moveCursorTo(getNextSelectableRef(-1));        
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
        cf.showClientIcon=showClientIcon.getValue();
//#endif
        cf.autoSubscribe=subscr.getSelectedIndex();       
        

//#ifdef SMILES
        cf.smiles=smiles.getValue();
//#endif
        cf.eventComposing=eventComposing.getValue();
        cf.capsState=capsState.getValue();
//#ifndef WMUC        
        cf.storeConfPresence=storeConfPresence.getValue();
//#endif        
        cf.autoScroll=autoScroll.getValue();
        cf.useTabs=useTabs.getValue();
        
//#ifdef RUNNING_MESSAGE
//#         cf.notifyWhenMessageType=notifyWhenMessageType.getValue();
//#endif
//#ifdef POPUPS
        cf.popUps=popUps.getValue();
//#endif
        cf.hideTimestamps=showBalloons.getValue();
        cf.eventDelivery=eventDelivery.getValue();
//#ifdef CLIPBOARD
//#         cf.useClipBoard=useClipBoard.getValue();
//#endif
//#ifdef DETRANSLIT
//#         cf.autoDeTranslit=autoDetranslit.getValue();
//#endif
        cf.showNickNames=showNickNames.getValue();
        cf.executeByNum=executeByNum.getValue();

        cf.autoLogin=autoLogin.getValue();
//#ifndef WMUC        
        cf.autoJoinConferences=autoJoinConferences.getValue();
//#endif        
        
        cf.reconnectCount=Integer.parseInt(reconnectCount.getValue());
        cf.reconnectTime=Integer.parseInt(reconnectTime.getValue());
//#ifdef FILE_TRANSFER
        cf.fileTransfer=fileTransfer.getValue();
//#endif
//#ifdef HISTORY
//#         cf.saveHistory=saveHistory.getValue();
//#endif
//#ifdef ADHOC
//#    cf.adhoc=adhoc.getValue();
//#endif
        
        VirtualList.showTimeTraffic=cf.showTimeTraffic=drawMenuCommand.getValue();
        Config.fullscreen = fullscreen.getValue();
//#ifdef MEMORY_USAGE
//#         VirtualList.memMonitor=cf.memMonitor=memMonitor.getValue();
//#endif
        cf.enableVersionOs=enableVersionOs.getValue();
        cf.queryExit=queryExit.getValue();
//#ifdef LIGHT_CONFIG
//#         cf.lightState=lightState.getValue();
//#endif
        if (cf.allowMinimize)
            cf.popupFromMinimized=popupFromMinimized.getValue();
        cf.autoClean=autoClean.getValue();
        if (VirtualCanvas.getInstance().hasPointerEvents())
            cf.advTouch = advTouch.getValue();

        cf.swapSendAndSuspend=swapSendAndSuspend.getValue();
        
        cf.gmtOffset=Integer.parseInt(fieldGmt.getValue());

        cf.textWrap=textWrap.getSelectedIndex();

        if (langs[0].size()>1) {
            cf.lang=(String) langs[0].elementAt( langFiles.getSelectedIndex() );
        }

//#ifdef AUTOSTATUS
//#             cf.useMyStatusMessages=awayStatus.getValue();
//#             cf.autoAwayDelay=Integer.parseInt(fieldAwayDelay.getValue());
//#             cf.autoAwayType=autoAwayType.getSelectedIndex();
                if (autoAwayType.getSelectedIndex() != Config.AWAY_LOCK) {
                if (AutoStatus.getInstance().active()) {
                    AutoStatus.getInstance().reset();
                }
                }
//#endif
        cf.messageLimit=Integer.parseInt(messageLimit.getValue());
        if (VirtualCanvas.getInstance().hasPointerEvents())
            cf.widthScroll2=Integer.parseInt(widthScroll2.getValue());
        cf.minItemHeight = Integer.parseInt(minItemHeight.getValue());

//#if LOGROTATE
//#             cf.msglistLimit=Integer.parseInt(messageCountLimit.getValue());
//#endif
        if (cf.panelsState!=panels.getSelectedIndex()) {
            cf.panelsState=panels.getSelectedIndex();
            VirtualList.changeOrient(cf.panelsState);
        }       

        //sd.roster.setLight(cf.lightState);   TODO: correct for new light control

        sd.canvas.setFullScreenMode(Config.fullscreen);

        cf.firstRun=false;

        cf.updateTime();
        cf.saveToStorage();

        String oldVerHash=EntityCaps.calcVerHash();
        EntityCaps.initCaps();
        if (!oldVerHash.equals(EntityCaps.calcVerHash()))
            if (sd.roster.isLoggedIn())
                sd.roster.sendPresence(Presence.PRESENCE_SAME, null);

        sd.roster.reEnumRoster();
        destroyView();
    }

    public boolean doUserKeyAction(int command_id) {
        switch (command_id) {
            case 1:
                destroyView();
                return true;
        }

        return super.doUserKeyAction(command_id);
    }

    public void destroyView() {
//#ifdef AUTOSTATUS
        if (sd.roster.isLoggedIn()) {
            if ((Config.getInstance().autoAwayType==Config.AWAY_OFF) || Config.getInstance().autoAwayType == Config.AWAY_LOCK) {
                AutoStatus.getInstance().stop();
            } else {
                AutoStatus.getInstance().start();       
            }
        }
//#endif
        super.destroyView();
    }
}
