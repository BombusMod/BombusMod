/*
 * Config.java
 *
 * Created on 24 январь 2008 г., 20:56
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package IE;

import Client.Config;
import java.util.Vector;

/**
 *
 * @author ad
 */
public class eConfigData {
    
    public eConfigData(String path) {
        Config cf=Config.getInstance();
        Vector array=new Vector();

        array.addElement(new keyValue("accountIndex", Integer.toString(cf.accountIndex)));
        array.addElement(new keyValue("showOfflineContacts", (cf.showOfflineContacts)?"1":"0"));
        array.addElement(new keyValue("fullscreen", (cf.fullscreen)?"1":"0"));
        array.addElement(new keyValue("def_profile", Integer.toString(cf.def_profile)));
        array.addElement(new keyValue("smiles", (cf.smiles)?"1":"0"));
        array.addElement(new keyValue("showTransports", (cf.showTransports)?"1":"0"));
        array.addElement(new keyValue("selfContact", (cf.selfContact)?"1":"0"));
        array.addElement(new keyValue("collapsedGroups", (cf.collapsedGroups)?"1":"0"));
        array.addElement(new keyValue("ignore", (cf.ignore)?"1":"0"));
        array.addElement(new keyValue("eventComposing", (cf.eventComposing)?"1":"0"));
        array.addElement(new keyValue("gmtOffset", Integer.toString(cf.gmtOffset)));
        array.addElement(new keyValue("locOffset", Integer.toString(cf.locOffset)));
        array.addElement(new keyValue("autoLogin", (cf.autoLogin)?"1":"0"));
        array.addElement(new keyValue("autoJoinConferences", (cf.autoJoinConferences)?"1":"0"));
        array.addElement(new keyValue("popupFromMinimized", (cf.popupFromMinimized)?"1":"0"));
        array.addElement(new keyValue("notifyBlink", (cf.notifyBlink)?"1":"0"));
        array.addElement(new keyValue("memMonitor", (cf.memMonitor)?"1":"0"));
        array.addElement(new keyValue("font1", Integer.toString(cf.font1)));
        array.addElement(new keyValue("font2", Integer.toString(cf.font2)));
        array.addElement(new keyValue("autoFocus", (cf.autoFocus)?"1":"0"));
        array.addElement(new keyValue("lang", cf.lang));
        array.addElement(new keyValue("storeConfPresence", (cf.storeConfPresence)?"1":"0"));
        array.addElement(new keyValue("capsState", (cf.capsState)?"1":"0"));
        array.addElement(new keyValue("textWrap", Integer.toString(cf.textWrap)));
        array.addElement(new keyValue("loginstatus", Integer.toString(cf.loginstatus)));
        array.addElement(new keyValue("msgPath", cf.msgPath));
        array.addElement(new keyValue("msgLog", (cf.msgLog)?"1":"0"));
        array.addElement(new keyValue("msgLogPresence", (cf.msgLogPresence)?"1":"0"));
        array.addElement(new keyValue("msgLogConfPresence", (cf.msgLogConfPresence)?"1":"0"));
        array.addElement(new keyValue("msgLogConf", (cf.msgLogConf)?"1":"0"));
        array.addElement(new keyValue("cp1251", (cf.cp1251)?"1":"0"));
        array.addElement(new keyValue("autoAwayDelay", Integer.toString(cf.autoAwayDelay)));
        array.addElement(new keyValue("defGcRoom", cf.defGcRoom));
        array.addElement(new keyValue("altInput", (cf.altInput)?"1":"0"));
        array.addElement(new keyValue("isbottom", Integer.toString(cf.isbottom)));
        array.addElement(new keyValue("confMessageCount", Integer.toString(cf.confMessageCount)));
        array.addElement(new keyValue("newMenu", (cf.newMenu)?"1":"0"));
        array.addElement(new keyValue("lightState", (cf.lightState)?"1":"0"));
        array.addElement(new keyValue("lastMessages", (cf.lastMessages)?"1":"0"));
        array.addElement(new keyValue("notifySound", (cf.notifySound)?"1":"0"));
        array.addElement(new keyValue("setAutoStatusMessage", (cf.setAutoStatusMessage)?"1":"0"));
        array.addElement(new keyValue("autoAwayType", Integer.toString(cf.autoAwayType)));
        array.addElement(new keyValue("autoScroll", (cf.autoScroll)?"1":"0"));
        array.addElement(new keyValue("popUps", (cf.popUps)?"1":"0"));
        array.addElement(new keyValue("showResources", (cf.showResources)?"1":"0"));
        array.addElement(new keyValue("antispam", (cf.antispam)?"1":"0"));
        array.addElement(new keyValue("enableVersionOs", (cf.enableVersionOs)?"1":"0"));
        array.addElement(new keyValue("messageLimit", Integer.toString(cf.messageLimit)));
        array.addElement(new keyValue("eventDelivery", (cf.eventDelivery)?"1":"0"));
        array.addElement(new keyValue("transliterateFilenames", (cf.transliterateFilenames)?"1":"0")); 
        array.addElement(new keyValue("rosterStatus", (cf.rosterStatus)?"1":"0"));
        array.addElement(new keyValue("queryExit", (cf.queryExit)?"1":"0"));
        array.addElement(new keyValue("showBalloons", (cf.showBalloons)?"1":"0"));
        array.addElement(new keyValue("notifyPicture", (cf.notifyPicture)?"1":"0"));
        array.addElement(new keyValue("userKeys", (cf.userKeys)?"1":"0"));
        array.addElement(new keyValue("msglistLimit", Integer.toString(cf.msglistLimit)));
        array.addElement(new keyValue("useTabs", (cf.useTabs)?"1":"0"));  
        array.addElement(new keyValue("autoSubscribe", Integer.toString(cf.autoSubscribe)));
        array.addElement(new keyValue("notInListDropLevel", Integer.toString(cf.notInListDropLevel)));
        array.addElement(new keyValue("useBoldFont", (cf.useBoldFont)?"1":"0"));
        array.addElement(new keyValue("notifyWhenMessageType", (cf.notifyWhenMessageType)?"1":"0"));
        array.addElement(new keyValue("ircLikeStatus", (cf.ircLikeStatus)?"1":"0"));
        array.addElement(new keyValue("sndrcvmood", (cf.sndrcvmood)?"1":"0"));
        array.addElement(new keyValue("scheme", cf.scheme));
        
        new eData(array, path+"config.txt");
        array = null;
    }
    
}
