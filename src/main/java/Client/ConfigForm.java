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

import Messages.notification.Notification;
import java.util.Vector;
import locale.SR;
import ui.VirtualList;
import ui.NativeScreenCommand;
import ui.NativeScreenItem;
import ui.NativeScreenModel;
import ui.VirtualListController;
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


    private EntityCaps entityCaps;

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
    private CheckBox autoDetranslit;
//#endif
//#if LOGROTATE
    private NumberInput messageCountLimit;
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
    private CheckBox saveHistory;
//#endif
    private CheckBox adhoc;
    private CheckBox fullscreen;
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
    private DropChoiceBox autoAwayType;
    private NumberInput fieldAwayDelay;
    private CheckBox awayStatus;
//#ifdef RUNNING_MESSAGE
//#      private CheckBox notifyWhenMessageType;
//#endif
    private DropChoiceBox popUps;
    private DropChoiceBox panels;
    private CheckBox drawMenuCommand;
    private CheckBox showNickNames;
    private CheckBox swapSendAndSuspend;
    private Vector langs[];

    /**
     * Creates a new instance of ConfigForm
     */
    public ConfigForm() {
        super(SR.MS_OPTIONS);
        if (!buildNativeModel()) return;
        cf = Config.getInstance();

        itemsList.addElement(new SimpleString(SR.MS_ROSTER_ELEMENTS, true));
        showOfflineContacts = new CheckBox(SR.MS_OFFLINE_CONTACTS, Config.getInstance().showOfflineContacts);
        itemsList.addElement(showOfflineContacts);
        selfContact = new CheckBox(SR.MS_SELF_CONTACT, Config.getInstance().selfContact);
        itemsList.addElement(selfContact);
        showTransports = new CheckBox(SR.MS_TRANSPORTS, Config.getInstance().showTransports);
        itemsList.addElement(showTransports);
        ignore = new CheckBox(SR.MS_IGNORE_LIST, Config.getInstance().ignore);
        itemsList.addElement(ignore);
        collapsedGroups = new CheckBox(SR.MS_COLLAPSED_GROUPS, Config.getInstance().collapsedGroups);
        itemsList.addElement(collapsedGroups);
        autoFocus = new CheckBox(SR.MS_AUTOFOCUS, Config.getInstance().autoFocus);
        itemsList.addElement(autoFocus);
        showResources = new CheckBox(SR.MS_SHOW_RESOURCES, Config.getInstance().showResources);
        itemsList.addElement(showResources);
        useBoldFont = new CheckBox(SR.MS_BOLD_FONT, Config.getInstance().useBoldFont);
        itemsList.addElement(useBoldFont);
        rosterStatus = new CheckBox(SR.MS_SHOW_STATUSES, Config.getInstance().rosterStatus);
        itemsList.addElement(rosterStatus);
//#ifdef CLIENTS_ICONS
        showClientIcon = new CheckBox(SR.MS_SHOW_CLIENTS_ICONS, Config.getInstance().showClientIcon);
        itemsList.addElement(showClientIcon);
//#endif
        autoClean = new CheckBox(SR.MS_AUTOCLEAN_GROUPS, Config.getInstance().autoClean);
        itemsList.addElement(autoClean);

        itemsList.addElement(new SpacerItem(10));
        subscr = new DropChoiceBox(SR.MS_AUTH_NEW);
        subscr.add(SR.MS_SUBSCR_AUTO);
        subscr.add(SR.MS_SUBSCR_ASK);
        subscr.add(SR.MS_SUBSCR_DROP);
        subscr.add(SR.MS_SUBSCR_REJECT);
        subscr.setSelectedIndex(Config.getInstance().autoSubscribe);
        itemsList.addElement(subscr);

        itemsList.addElement(new SpacerItem(10));


        itemsList.addElement(new SpacerItem(10));
        itemsList.addElement(new SimpleString(SR.MS_MESSAGES, true));
//#ifdef SMILES
        smiles = new CheckBox(SR.MS_SMILES, Config.getInstance().smiles);
        itemsList.addElement(smiles);
//#endif
        eventComposing = new CheckBox(SR.MS_COMPOSING_EVENTS, Config.getInstance().eventComposing);
        itemsList.addElement(eventComposing);
        capsState = new CheckBox(SR.MS_CAPS_STATE, Config.getInstance().capsState);
        itemsList.addElement(capsState);
//#ifndef WMUC        
        storeConfPresence = new CheckBox(SR.MS_STORE_PRESENCE, Config.getInstance().storeConfPresence);
        itemsList.addElement(storeConfPresence);
//#endif        
        autoScroll = new CheckBox(SR.MS_AUTOSCROLL, Config.getInstance().autoScroll);
        itemsList.addElement(autoScroll);
//#ifdef RUNNING_MESSAGE
//#         notifyWhenMessageType = new CheckBox(SR.MS_RUNNING_MESSAGE, Config.getInstance().notifyWhenMessageType); itemsList.addElement(notifyWhenMessageType);
//#endif
        popUps = new DropChoiceBox("Notification type");
        popUps.add(SR.MS_DISABLED);
        popUps.add(SR.MS_POPUPS);
        if (Notification.isPlatformSupported())
            popUps.add("System");
        popUps.setSelectedIndex(Config.getInstance().popUps);
        itemsList.addElement(popUps);
        showBalloons = new CheckBox(SR.MS_HIDE_TIMESTAMPS, Config.getInstance().hideTimestamps);
        itemsList.addElement(showBalloons);
        eventDelivery = new CheckBox(SR.MS_DELIVERY, Config.getInstance().eventDelivery);
        itemsList.addElement(eventDelivery);

//#ifdef DETRANSLIT
        autoDetranslit = new CheckBox(SR.MS_AUTODETRANSLIT, Config.getInstance().autoDeTranslit);
        itemsList.addElement(autoDetranslit);
//#endif
        showNickNames = new CheckBox(SR.MS_SHOW_NACKNAMES, Config.getInstance().showNickNames);
        itemsList.addElement(showNickNames);
        swapSendAndSuspend = new CheckBox("swap \"" + SR.MS_SEND + "\" and \"" + SR.MS_SUSPEND + "\" commands", Config.getInstance().swapSendAndSuspend);
        itemsList.addElement(swapSendAndSuspend);

//#if LOGROTATE
        messageCountLimit = new NumberInput(SR.MS_MESSAGE_COUNT_LIMIT, Integer.toString(Config.getInstance().msglistLimit), 3, 1000);
        itemsList.addElement(messageCountLimit);
//#endif

        itemsList.addElement(new SpacerItem(10));
        messageLimit = new NumberInput(SR.MS_MESSAGE_COLLAPSE_LIMIT, Integer.toString(Config.getInstance().messageLimit), 200, 20480);
        itemsList.addElement(messageLimit);

        minItemHeight = new NumberInput(SR.MS_ITEM_HEIGHT, Integer.toString(Config.getInstance().minItemHeight), 0, 100);
        itemsList.addElement(minItemHeight);

        if (VirtualCanvas.getInstance().hasPointerEvents()) {
            widthScroll2 = new NumberInput(SR.MS_MESSAGE_WIDTH_SCROLL_2, Integer.toString(Config.getInstance().widthScroll2), 1, 50);
            itemsList.addElement(widthScroll2);
            advTouch = new CheckBox(SR.MS_SINGLE_CLICK, Config.getInstance().advTouch);
            itemsList.addElement(advTouch);
        }

        itemsList.addElement(new SpacerItem(10));
        itemsList.addElement(new SimpleString(SR.MS_STARTUP_ACTIONS, true));
        autoLogin = new CheckBox(SR.MS_AUTOLOGIN, Config.getInstance().autoLogin);
        itemsList.addElement(autoLogin);
//#ifndef WMUC        
        autoJoinConferences = new CheckBox(SR.MS_AUTO_CONFERENCES, Config.getInstance().autoJoinConferences);
        itemsList.addElement(autoJoinConferences);
//#endif        

        itemsList.addElement(new SpacerItem(10));
        itemsList.addElement(new SimpleString(SR.MS_RECONNECT, true));
        reconnectCount = new NumberInput(SR.MS_RECONNECT_COUNT_RETRY, Integer.toString(Config.getInstance().reconnectCount), 0, 100);
        itemsList.addElement(reconnectCount);
        reconnectTime = new NumberInput(SR.MS_RECONNECT_WAIT, Integer.toString(Config.getInstance().reconnectTime), 1, 60);
        itemsList.addElement(reconnectTime);

        itemsList.addElement(new SpacerItem(10));
        itemsList.addElement(new SimpleString(SR.MS_APPLICATION, true));
        enableVersionOs = new CheckBox(SR.MS_SHOW_HARDWARE, Config.getInstance().enableVersionOs);
        itemsList.addElement(enableVersionOs);
        queryExit = new CheckBox(SR.MS_CONFIRM_EXIT, Config.getInstance().queryExit);
        itemsList.addElement(queryExit);
//#ifdef LIGHT_CONFIG
//#         lightState = new CheckBox(SR.L_CONFIG, Config.getInstance().lightState);
//#         if (phoneManufacturer == Config.SIEMENS || phoneManufacturer == Config.SIEMENS2 || phoneManufacturer == Config.SONYE || phoneManufacturer == Config.NOKIA) {
//#             itemsList.addElement(lightState);
//#         }
//#endif

//#ifdef FILE_TRANSFER
        fileTransfer = new CheckBox(SR.MS_FILE_TRANSFERS, Config.getInstance().fileTransfer);
        itemsList.addElement(fileTransfer);
//#endif
//#ifdef HISTORY
        saveHistory = new CheckBox(SR.MS_HISTORY, Config.getInstance().saveHistory);
        itemsList.addElement(saveHistory);
//#endif
        adhoc = new CheckBox(SR.MS_ADHOC, Config.getInstance().adhoc);
        itemsList.addElement(adhoc);
        if (Config.getInstance().allowMinimize) {
            popupFromMinimized = new CheckBox(SR.MS_ENABLE_POPUP, Config.getInstance().popupFromMinimized);
            itemsList.addElement(popupFromMinimized);
        }
        executeByNum = new CheckBox(SR.MS_EXECUTE_MENU_BY_NUMKEY, Config.getInstance().executeByNum);
        itemsList.addElement(executeByNum);

        itemsList.addElement(new SpacerItem(10));
        itemsList.addElement(new SimpleString(SR.MS_TIME_SETTINGS, true));
        fieldGmt = new NumberInput(SR.MS_GMT_OFFSET, Integer.toString(Config.getInstance().gmtOffset), -12, 12);
        itemsList.addElement(fieldGmt);

        itemsList.addElement(new SpacerItem(10));
        textWrap = new DropChoiceBox(SR.MS_TEXTWRAP);
        textWrap.add(SR.MS_TEXTWRAP_CHARACTER);
        textWrap.add(SR.MS_TEXTWRAP_WORD);
        textWrap.setSelectedIndex(Config.getInstance().textWrap);
        itemsList.addElement(textWrap);

        itemsList.addElement(new SpacerItem(10));
        panels = new DropChoiceBox(SR.MS_PANELS);
        panels.add(SR.MS_NO_BAR + " : " + SR.MS_NO_BAR);
        panels.add(SR.MS_MAIN_BAR + " : " + SR.MS_NO_BAR);
        panels.add(SR.MS_MAIN_BAR + " : " + SR.MS_INFO_BAR);
        panels.add(SR.MS_NO_BAR + " : " + SR.MS_INFO_BAR);
        panels.add(SR.MS_INFO_BAR + " : " + SR.MS_NO_BAR);
        panels.add(SR.MS_INFO_BAR + " : " + SR.MS_MAIN_BAR);
        panels.add(SR.MS_NO_BAR + " : " + SR.MS_MAIN_BAR);
        panels.setSelectedIndex(Config.getInstance().panelsState);
        itemsList.addElement(panels);
        drawMenuCommand = new CheckBox(SR.MS_SHOW_TIME_TRAFFIC, Config.getInstance().showTimeTraffic);
        itemsList.addElement(drawMenuCommand);
        itemsList.addElement(new SpacerItem(10));
        autoAwayType = new DropChoiceBox(SR.MS_AWAY_TYPE);
        autoAwayType.add(SR.MS_AWAY_OFF);
        autoAwayType.add(SR.MS_AWAY_LOCK);
        autoAwayType.add(SR.MS_MESSAGE_LOCK);
        autoAwayType.add(SR.MS_IDLE);
        autoAwayType.setSelectedIndex(Config.autoAwayType);
        itemsList.addElement(autoAwayType);

        fieldAwayDelay = new NumberInput(SR.MS_AWAY_PERIOD, Integer.toString(Config.autoAwayDelay), 1, 60);
        itemsList.addElement(fieldAwayDelay);

        awayStatus = new CheckBox(SR.MS_USE_MY_STATUS_MESSAGES, Config.useMyStatusMessages);
        itemsList.addElement(awayStatus);

        langs = new StringLoader().stringLoader("/lang/res.txt", 3);
        if (langs[0].size() > 1) {
            itemsList.addElement(new SpacerItem(10));
            langFiles = new DropChoiceBox(SR.MS_LANGUAGE);
            String tempLang = Config.getInstance().lang;
            if (tempLang == null) { //not detected
                String locale = System.getProperty("microedition.locale");
                if (locale != null) {
                    tempLang = locale.substring(0, 2).toLowerCase();
                }
            }

            for (int i = 0; i < langs[0].size(); i++) {
                String label = (String) langs[2].elementAt(i);
                String langCode = (String) langs[0].elementAt(i);
                langFiles.add(label);
                if (tempLang.equals(langCode)) {
                    langFiles.setSelectedIndex(i);
                }
            }
            itemsList.addElement(langFiles);
        }
        moveCursorTo(getNextSelectableRef(-1));
    }

    public void cmdOk() {
        Config.getInstance().showOfflineContacts = showOfflineContacts.getValue();
        Config.getInstance().selfContact = selfContact.getValue();
        Config.getInstance().showTransports = showTransports.getValue();
        Config.getInstance().ignore = ignore.getValue();
        Config.getInstance().collapsedGroups = collapsedGroups.getValue();
        Config.getInstance().autoFocus = autoFocus.getValue();
        Config.getInstance().showResources = showResources.getValue();
        Config.getInstance().useBoldFont = useBoldFont.getValue();
        Config.getInstance().rosterStatus = rosterStatus.getValue();
//#ifdef CLIENTS_ICONS
        Config.getInstance().showClientIcon = showClientIcon.getValue();
//#endif
        Config.getInstance().autoSubscribe = subscr.getSelectedIndex();


//#ifdef SMILES
        Config.getInstance().smiles = smiles.getValue();
//#endif
        Config.getInstance().eventComposing = eventComposing.getValue();
        Config.getInstance().capsState = capsState.getValue();
//#ifndef WMUC        
        Config.getInstance().storeConfPresence = storeConfPresence.getValue();
//#endif        
        Config.getInstance().autoScroll = autoScroll.getValue();

//#ifdef RUNNING_MESSAGE
//#         Config.getInstance().notifyWhenMessageType=notifyWhenMessageType.getValue();
//#endif
        Config.getInstance().popUps = popUps.getValue();
        Config.getInstance().hideTimestamps = showBalloons.getValue();
        Config.getInstance().eventDelivery = eventDelivery.getValue();

//#ifdef DETRANSLIT
        Config.getInstance().autoDeTranslit = autoDetranslit.getValue();
//#endif
        Config.getInstance().showNickNames = showNickNames.getValue();
        Config.getInstance().executeByNum = executeByNum.getValue();

        Config.getInstance().autoLogin = autoLogin.getValue();
//#ifndef WMUC        
        Config.getInstance().autoJoinConferences = autoJoinConferences.getValue();
//#endif        

        Config.getInstance().reconnectCount = Integer.parseInt(reconnectCount.getValue());
        Config.getInstance().reconnectTime = Integer.parseInt(reconnectTime.getValue());
//#ifdef FILE_TRANSFER
        Config.getInstance().fileTransfer = fileTransfer.getValue();
//#endif
//#ifdef HISTORY
        Config.getInstance().saveHistory = saveHistory.getValue();
//#endif
        Config.getInstance().adhoc = adhoc.getValue();

        VirtualList.showTimeTraffic = Config.getInstance().showTimeTraffic = drawMenuCommand.getValue();
        Config.getInstance().enableVersionOs = enableVersionOs.getValue();
        Config.getInstance().queryExit = queryExit.getValue();
//#ifdef LIGHT_CONFIG
//#         Config.getInstance().lightState = lightState.getValue();
//#endif
        if (Config.getInstance().allowMinimize) {
            Config.getInstance().popupFromMinimized = popupFromMinimized.getValue();
        }
        Config.getInstance().autoClean = autoClean.getValue();
        if (VirtualCanvas.getInstance().hasPointerEvents()) {
            Config.getInstance().advTouch = advTouch.getValue();
        }

        Config.getInstance().swapSendAndSuspend = swapSendAndSuspend.getValue();

        Config.getInstance().gmtOffset = Integer.parseInt(fieldGmt.getValue());

        Config.getInstance().textWrap = textWrap.getSelectedIndex();

        if (langs[0].size() > 1) {
            Config.getInstance().lang = (String) langs[0].elementAt(langFiles.getSelectedIndex());
        }

        Config.useMyStatusMessages = awayStatus.getValue();
        Config.autoAwayDelay = Integer.parseInt(fieldAwayDelay.getValue());
        Config.autoAwayType = autoAwayType.getSelectedIndex();
        if (autoAwayType.getSelectedIndex() != Config.AWAY_LOCK) {
            if (AutoStatus.getInstance().active()) {
                AutoStatus.getInstance().reset();
            }
        }
        Config.getInstance().messageLimit = Integer.parseInt(messageLimit.getValue());
        if (VirtualCanvas.getInstance().hasPointerEvents()) {
            Config.getInstance().widthScroll2 = Integer.parseInt(widthScroll2.getValue());
        }
        Config.getInstance().minItemHeight = Integer.parseInt(minItemHeight.getValue());

//#if LOGROTATE
        Config.getInstance().msglistLimit = Integer.parseInt(messageCountLimit.getValue());
//#endif
        if (Config.getInstance().panelsState != panels.getSelectedIndex()) {
            Config.getInstance().panelsState = panels.getSelectedIndex();
            VirtualList.changeOrient(Config.getInstance().panelsState);
        }

        //sd.roster.setLight(Config.getInstance().lightState);   TODO: correct for new light control

        VirtualCanvas.getInstance().setFullScreenMode(Config.fullscreen);

        Config.getInstance().firstRun = false;

        Config.getInstance().updateTime();
        Config.getInstance().saveToStorage();
        try {
            String oldVerHash = entityCaps.calcVerHash();
            if (!oldVerHash.equals(entityCaps.calcVerHash())) {
                if (sd.roster.isLoggedIn()) {
                    sd.roster.sendPresence(Presence.PRESENCE_SAME, null);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

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
        if (sd.roster.isLoggedIn()) {
            if ((Config.autoAwayType == Config.AWAY_OFF) || Config.autoAwayType == Config.AWAY_LOCK) {
                AutoStatus.getInstance().stop();
            } else {
                AutoStatus.getInstance().start();
            }
        }
        VirtualListController.getInstance().setModel(null);
        VirtualListController.getInstance().notifyUpdate();
        super.destroyView();
    }

    private boolean buildNativeModel() {
        if (!VirtualListController.getInstance().isActive()) return true;
        NativeScreenModel m = new NativeScreenModel();
        final Client.Config cf = Client.Config.getInstance();
        m.addPar(new NativeScreenItem() {{ setAsCheckBox(SR.MS_OFFLINE_CONTACTS, cf.showOfflineContacts); }});
        m.addPar(new NativeScreenItem() {{ setAsCheckBox(SR.MS_SELF_CONTACT, cf.selfContact); }});
        m.addPar(new NativeScreenItem() {{ setAsCheckBox(SR.MS_TRANSPORTS, cf.showTransports); }});
        m.addPar(new NativeScreenItem() {{ setAsCheckBox(SR.MS_IGNORE_LIST, cf.ignore); }});
        m.addPar(new NativeScreenItem() {{ setAsCheckBox(SR.MS_COLLAPSED_GROUPS, cf.collapsedGroups); }});
        m.addPar(new NativeScreenItem() {{ setAsCheckBox(SR.MS_AUTOFOCUS, cf.autoFocus); }});
        m.addPar(new NativeScreenItem() {{ setAsCheckBox(SR.MS_SHOW_RESOURCES, cf.showResources); }});
        m.addPar(new NativeScreenItem() {{ setAsCheckBox(SR.MS_BOLD_FONT, cf.useBoldFont); }});
        m.addPar(new NativeScreenItem() {{ setAsCheckBox(SR.MS_SHOW_STATUSES, cf.rosterStatus); }});
        m.addPar(new NativeScreenItem() {{ setAsCheckBox(SR.MS_AUTOCLEAN_GROUPS, cf.autoClean); }});
        m.addPar(new NativeScreenItem() {{ setAsCheckBox(SR.MS_COMPOSING_EVENTS, cf.eventComposing); }});
        m.addPar(new NativeScreenItem() {{ setAsCheckBox(SR.MS_AUTOSCROLL, cf.autoScroll); }});
        m.addPar(new NativeScreenItem() {{ setAsCheckBox(SR.MS_AUTO_CONFERENCES, cf.autoJoinConferences); }});
        m.addPar(new NativeScreenItem() {{ setAsCheckBox(SR.MS_AUTOLOGIN, cf.autoLogin); }});
        m.addPar(new NativeScreenItem() {{ setAsCheckBox(SR.MS_FULLSCREEN, cf.fullscreen); }});
        m.addPar(new NativeScreenItem() {{ setAsCheckBox(SR.MS_SHOW_HARDWARE, cf.enableVersionOs); }});
        m.addPar(new NativeScreenItem() {{ setAsCheckBox(SR.MS_ENABLE_POPUP, cf.popupFromMinimized); }});
        m.addPar(new NativeScreenItem() {{ setAsCheckBox(SR.MS_DELIVERY, cf.eventDelivery); }});
        m.addPar(new NativeScreenItem() {{ setAsCheckBox(SR.MS_SINGLE_CLICK, cf.advTouch); }});
        m.addPar(new NativeScreenItem() {{ setAsNumber("reconnectCount", SR.MS_RECONNECT_COUNT_RETRY, cf.reconnectCount); }});
        m.addPar(new NativeScreenItem() {{ setAsNumber("reconnectTime", SR.MS_RECONNECT_WAIT, cf.reconnectTime); }});
        m.addCommand("ok", "OK", NativeScreenCommand.OK, -1);
        final ConfigForm self = this;
        VirtualListController.getInstance().setOnDismiss(new Runnable() { public void run() {
            self.dismissNative();
        }});
        VirtualListController.getInstance().setCaption(SR.MS_OPTIONS);
        VirtualListController.getInstance().setModel(m);
        VirtualListController.getInstance().notifyUpdate();
        return false;
    }
    public void dismissNative() { VirtualListController.getInstance().setModel(null); VirtualListController.getInstance().notifyUpdate(); destroyView(); }
}
