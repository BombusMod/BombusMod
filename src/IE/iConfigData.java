/*
 * ConfigData.java
 *
 * Created on 24 январь 2008 г., 21:13
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package IE;

import Client.AlertProfile;
import Client.Config;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;
import ui.VirtualList;

/**
 *
 * @author ad
 */
public class iConfigData {
    Vector array = new Vector();

    public iConfigData(String path) {
        Config cf=Config.getInstance();
        iData idata = new iData(path);
        array = idata.iData();
        
        cf.accountIndex=cf.getIntProperty(getValue("accountIndex"),-1);
        cf.showOfflineContacts=cf.getBooleanProperty(getValue("showOfflineContacts"),false);
        cf.fullscreen=cf.getBooleanProperty(getValue("fullscreen"),true);
        cf.def_profile = cf.getIntProperty(getValue("def_profile"),0);
//#ifdef SMILES
//#         cf.smiles=cf.getBooleanProperty(getValue("smiles"),true);
//#endif
        cf.showTransports=cf.getBooleanProperty(getValue("showTransports"),true);
        cf.selfContact=cf.getBooleanProperty(getValue("selfContact"),false);
        cf.ignore=cf.getBooleanProperty(getValue("ignore"),false);
        cf.collapsedGroups=cf.getBooleanProperty(getValue("collapsedGroups"),false);
        cf.eventComposing=cf.getBooleanProperty(getValue("eventComposing"),true);
        cf.gmtOffset=cf.getIntProperty(getValue("gmtOffset"),0);
        cf.locOffset=cf.getIntProperty(getValue("locOffset"),0);
        cf.autoLogin=cf.getBooleanProperty(getValue("autoLogin"),true);
//#ifndef WMUC
        cf.autoJoinConferences=cf.getBooleanProperty(getValue("autoJoinConferences"),true);
        cf.storeConfPresence=cf.getBooleanProperty(getValue("storeConfPresence"),true);
        cf.defGcRoom=cf.getStringProperty(getValue("defGcRoom"),"bombusmod@conference.jabber.ru");
        cf.confMessageCount=cf.getIntProperty(getValue("confMessageCount"),20);
//#endif
        cf.popupFromMinimized=cf.getBooleanProperty(getValue("popupFromMinimized"),true);
        cf.memMonitor=cf.getBooleanProperty(getValue("memMonitor"),true);
        cf.font1=cf.getIntProperty(getValue("font1"),0);
        cf.font2=cf.getIntProperty(getValue("font2"),0);
        cf.autoFocus=cf.getBooleanProperty(getValue("autoFocus"),false);
        cf.lang=cf.getStringProperty(getValue("lang"),"en");
        cf.capsState=cf.getBooleanProperty(getValue("capsState"),true);
        cf.textWrap=cf.getIntProperty(getValue("textWrap"),0);
        cf.loginstatus=cf.getIntProperty(getValue("loginstatus"),0);
//#ifdef HISTORY
//#         cf.lastMessages=cf.getBooleanProperty(getValue("lastMessages"),false);
//#         cf.msgPath=cf.getStringProperty(getValue("msgPath"),"");
//#         cf.msgLog=cf.getBooleanProperty(getValue("msgLog"),false);
//#         cf.msgLogPresence=cf.getBooleanProperty(getValue("msgLogPresence"),false);
//#         cf.msgLogConfPresence=cf.getBooleanProperty(getValue("msgLogConfPresence"),false);
//#         cf.msgLogConf=cf.getBooleanProperty(getValue("msgLogConf"),false);
//#endif
        cf.cp1251=cf.getBooleanProperty(getValue("cp1251"),true);
        cf.isbottom=cf.getIntProperty(getValue("isbottom"),2);
//#ifdef NEW_MENU
//#         cf.newMenu=cf.getBooleanProperty(getValue("newMenu"),false);
//#endif
        cf.lightState=cf.getBooleanProperty(getValue("lightState"),true);
//#ifdef AUTOSTATUS
//#         cf.autoAwayDelay=cf.getIntProperty(getValue("autoAwayDelay"),5);
//#         cf.setAutoStatusMessage=cf.getBooleanProperty(getValue("setAutoStatusMessage"),false);
//#         cf.autoAwayType=cf.getIntProperty(getValue("autoAwayType"),0);
//#endif
        cf.autoScroll=cf.getBooleanProperty(getValue("autoScroll"),true);
//#ifdef POPUPS
//#         cf.popUps=cf.getBooleanProperty(getValue("popUps"),true);
//#endif
        cf.showResources=cf.getBooleanProperty(getValue("showResources"),true);
//#ifdef ANTISPAM
//#         cf.antispam=cf.getBooleanProperty(getValue("antispam"),false);
//#endif
        cf.enableVersionOs=cf.getBooleanProperty(getValue("enableVersionOs"),true);
        cf.messageLimit=cf.getIntProperty(getValue("messageLimit"),300);
        cf.eventDelivery=cf.getBooleanProperty(getValue("eventDelivery"),true);
//#ifdef TRANSLIT
//#         cf.transliterateFilenames=cf.getBooleanProperty(getValue("transliterateFilenames"),false);
//#endif
//#ifdef SECONDSTRING
//#         cf.rosterStatus=cf.getBooleanProperty(getValue("rosterStatus"),true);
//#endif
        cf.queryExit=cf.getBooleanProperty(getValue("queryExit"),false);
        VirtualList.showBalloons=cf.showBalloons=cf.getBooleanProperty(getValue("showBalloons"),false);
//#ifdef USER_KEYS
//#         cf.userKeys=cf.getBooleanProperty(getValue("userKeys"),false);
//#endif
//#ifdef AUTODELETE
//#         cf.msglistLimit=cf.getIntProperty(getValue("msglistLimit"),100);
//#endif
        cf.useTabs=cf.getBooleanProperty(getValue("useTabs"),true);
        cf.autoSubscribe=cf.getIntProperty(getValue("autoSubscribe"), cf.SUBSCR_ASK);
        cf.notInListDropLevel=cf.getIntProperty(getValue("notInListDropLevel"), cf.SUBSCR_ASK);
        cf.useBoldFont=cf.getBooleanProperty(getValue("useBoldFont"),false);
        cf.notifyWhenMessageType=cf.getBooleanProperty(getValue("notifyWhenMessageType"),false);
//#ifdef IRC_LIKE
//#         cf.ircLikeStatus=cf.getBooleanProperty(getValue("ircLikeStatus"),false);
//#endif
//#ifdef MOOD
//#         cf.sndrcvmood=cf.getBooleanProperty(getValue("sndrcvmood"),true);
//#endif
        cf.scheme=cf.getStringProperty(getValue("scheme"),"");
//#ifdef CLIPBOARD
//#         cf.useClipBoard=cf.getBooleanProperty(getValue("useClipBoard"),true);
//#endif
        cf.lastProfile=cf.profile=cf.def_profile;
        if (cf.lastProfile==AlertProfile.VIBRA) 
            cf.lastProfile=0;
        cf.updateTime();

        cf.saveToStorage();

        VirtualList.fullscreen=cf.fullscreen;
        VirtualList.memMonitor=cf.memMonitor;
//#ifdef USER_KEYS
//#         VirtualList.userKeys=cf.userKeys;
//#endif
    }
    
    private String getValue(String key) {
        for (Enumeration e=array.elements(); e.hasMoreElements();) {
            keyValue i=(keyValue)e.nextElement();
            if (i.getKey().equals(key))
                return i.getValue();
        }
        return null;
    }
    
}
