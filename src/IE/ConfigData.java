/*
 * ConfigData.java
 *
 * Created on 24.01.2008 , 20:56
 *
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
 */

package IE;

import Alerts.AlertProfile;
import Client.Config;
import Client.NotInListFilter;
import io.file.FileIO;
import java.util.Enumeration;
import java.util.Vector;
import ui.VirtualList;

/**
 *
 * @author ad
 */
public class ConfigData {
//#ifdef PLUGINS
//#     public static String plugin = new String("PLUGIN_IE");
//#endif
    
    private Config cf;
    private Vector array;
    private String file;
    
    public ConfigData(String path, int direction) {
        cf=Config.getInstance();
        array=new Vector();
        this.file=path;
        
        if (direction==0) {
            importData();
        } else {
            exportData();
        }
        array=null;
    }
    
    public void exportData() {
        array.addElement(new keyValue(accountIndex, Integer.toString(cf.accountIndex)));
        array.addElement(new keyValue(showOfflineContacts, (cf.showOfflineContacts)?"1":"0"));
        array.addElement(new keyValue(fullscreen, (cf.fullscreen)?"1":"0"));
        array.addElement(new keyValue(fileTransfer, (cf.fileTransfer)?"1":"0"));
        array.addElement(new keyValue(adhoc, (cf.adhoc)?"1":"0"));
        array.addElement(new keyValue(saveHistory, (cf.saveHistory)?"1":"0"));
        array.addElement(new keyValue(def_profile, Integer.toString(cf.def_profile)));
//#ifdef SMILES
        array.addElement(new keyValue(smiles, (cf.smiles)?"1":"0"));
//#endif
        array.addElement(new keyValue(showTransports, (cf.showTransports)?"1":"0"));
        array.addElement(new keyValue(selfContact, (cf.selfContact)?"1":"0"));
        array.addElement(new keyValue(collapsedGroups, (cf.collapsedGroups)?"1":"0"));
        array.addElement(new keyValue(ignore, (cf.ignore)?"1":"0"));
        array.addElement(new keyValue(eventComposing, (cf.eventComposing)?"1":"0"));
        array.addElement(new keyValue(gmtOffset, Integer.toString(cf.gmtOffset)));
        array.addElement(new keyValue(autoLogin, (cf.autoLogin)?"1":"0"));
//#ifndef WMUC
        array.addElement(new keyValue(autoJoinConferences, (cf.autoJoinConferences)?"1":"0"));
        array.addElement(new keyValue(storeConfPresence, (cf.storeConfPresence)?"1":"0"));
        array.addElement(new keyValue(defGcRoom, cf.defGcRoom));
        array.addElement(new keyValue(confMessageCount, Integer.toString(cf.confMessageCount)));
//#endif
        array.addElement(new keyValue(popupFromMinimized, (cf.popupFromMinimized)?"1":"0"));
        array.addElement(new keyValue(notifyBlink, (cf.notifyBlink)?"1":"0"));
        array.addElement(new keyValue(memMonitor, (cf.memMonitor)?"1":"0"));
        array.addElement(new keyValue(font1, Integer.toString(cf.font1)));
        array.addElement(new keyValue(font2, Integer.toString(cf.font2)));
        array.addElement(new keyValue(font3, Integer.toString(cf.font3)));
        array.addElement(new keyValue(font4, Integer.toString(cf.font4)));
        array.addElement(new keyValue(autoFocus, (cf.autoFocus)?"1":"0"));
        array.addElement(new keyValue(lang, cf.lang));
        array.addElement(new keyValue(capsState, (cf.capsState)?"1":"0"));
        array.addElement(new keyValue(textWrap, Integer.toString(cf.textWrap)));
        array.addElement(new keyValue(loginstatus, Integer.toString(cf.loginstatus)));
//#ifdef HISTORY
//#         array.addElement(new keyValue(lastMessages, (cf.lastMessages)?"1":"0"));
//#         array.addElement(new keyValue(msgPath, cf.msgPath));
//#         array.addElement(new keyValue(msgLog, (cf.msgLog)?"1":"0"));
//#         array.addElement(new keyValue(msgLogPresence, (cf.msgLogPresence)?"1":"0"));
//#         array.addElement(new keyValue(msgLogConfPresence, (cf.msgLogConfPresence)?"1":"0"));
//#         array.addElement(new keyValue(msgLogConf, (cf.msgLogConf)?"1":"0"));
//#endif
        array.addElement(new keyValue(cp1251, (cf.cp1251)?"1":"0"));
        
        array.addElement(new keyValue(panelsState, Integer.toString(cf.panelsState)));
        array.addElement(new keyValue(showTimeTraffic, (cf.showTimeTraffic)?"1":"0"));

        array.addElement(new keyValue(lightState, (cf.lightState)?"1":"0"));
        array.addElement(new keyValue(notifySound, (cf.notifySound)?"1":"0"));
//#ifdef AUTOSTATUS
//#         array.addElement(new keyValue(autoAwayDelay, Integer.toString(cf.autoAwayDelay)));
//#         array.addElement(new keyValue(setAutoStatusMessage, (cf.setAutoStatusMessage)?"1":"0"));
//#         array.addElement(new keyValue(autoAwayType, Integer.toString(cf.autoAwayType)));
//#endif
        array.addElement(new keyValue(autoScroll, (cf.autoScroll)?"1":"0"));
//#ifdef POPUPS
        array.addElement(new keyValue(popUps, (cf.popUps)?"1":"0"));
//#endif
        array.addElement(new keyValue(showResources, (cf.showResources)?"1":"0"));
        array.addElement(new keyValue(enableVersionOs, (cf.enableVersionOs)?"1":"0"));
        array.addElement(new keyValue(messageLimit, Integer.toString(cf.messageLimit)));
        array.addElement(new keyValue(eventDelivery, (cf.eventDelivery)?"1":"0"));
//#ifdef DETRANSLIT
//#         array.addElement(new keyValue(transliterateFilenames, (cf.transliterateFilenames)?"1":"0")); 
//#endif
        array.addElement(new keyValue(rosterStatus, (cf.rosterStatus)?"1":"0"));
        array.addElement(new keyValue(queryExit, (cf.queryExit)?"1":"0"));
        array.addElement(new keyValue(showBalloons, (cf.showBalloons)?"1":"0"));
        array.addElement(new keyValue(notifyPicture, (cf.notifyPicture)?"1":"0"));
//#ifdef USER_KEYS
//#         array.addElement(new keyValue(userKeys, (cf.userKeys)?"1":"0"));
//#endif
//#ifdef LOGROTATE
//#         array.addElement(new keyValue(msglistLimit, Integer.toString(cf.msglistLimit)));
//#endif
        array.addElement(new keyValue(useTabs, (cf.useTabs)?"1":"0"));  
        array.addElement(new keyValue(autoSubscribe, Integer.toString(cf.autoSubscribe)));
        array.addElement(new keyValue(notInListDropLevel, Integer.toString(cf.notInListDropLevel)));
        array.addElement(new keyValue(useBoldFont, (cf.useBoldFont)?"1":"0"));
        array.addElement(new keyValue(notifyWhenMessageType, (cf.notifyWhenMessageType)?"1":"0"));
//#ifdef PEP
//#         array.addElement(new keyValue(sndrcvmood, (cf.sndrcvmood)?"1":"0"));
//#endif
//#ifdef PEP_TUNE
//#         array.addElement(new keyValue(sndrcvtune, (cf.rcvtune)?"1":"0"));
//#endif
//#ifdef PEP_ACTIVITY
//#         array.addElement(new keyValue(rcvactivity, (cf.rcvactivity)?"1":"0"));
//#endif
//#ifdef CLIPBOARD
//#         array.addElement(new keyValue(useClipBoard, (cf.useClipBoard)?"1":"0"));
//#endif

//#ifdef DETRANSLIT
//#         array.addElement(new keyValue(autoDeTranslit, (cf.autoDeTranslit)?"1":"0")); 
//#endif
        array.addElement(new keyValue(IQNotify, (cf.IQNotify)?"1":"0")); 
//#ifdef CLIENTS_ICONS
//#         array.addElement(new keyValue(showClientIcon, (cf.showClientIcon)?"1":"0")); 
//#endif
        
        array.addElement(new keyValue(reconnectCount, Integer.toString(cf.reconnectCount)));
        array.addElement(new keyValue(reconnectTime, Integer.toString(cf.reconnectTime)));
        
        array.addElement(new keyValue(executeByNum, (cf.executeByNum)?"1":"0"));
        array.addElement(new keyValue(showNickNames, (cf.showNickNames)?"1":"0"));
        
        
        StringBuffer body = new StringBuffer();
        body = createArrayString(array);

        FileIO fileOut=FileIO.createConnection(file+"config.txt");
        fileOut.fileWrite(body.toString().getBytes());
    }
    
    public StringBuffer createArrayString(Vector array) {
        StringBuffer body = new StringBuffer();
        
        for (Enumeration e=array.elements(); e.hasMoreElements();) {
                keyValue i=(keyValue)e.nextElement();
                body.append("<i><k>")
                .append(i.getKey())
                .append("</k><v>")
                .append(i.getValue())
                .append("</v></i>\r\n");  
        }
        return body;
    }

    private String getValue(String key) {
        for (Enumeration e=array.elements(); e.hasMoreElements();) {
            keyValue i=(keyValue)e.nextElement();
            if (i.getKey().equals(key))
                return i.getValue();
        }
        return null;
    }

    public void importData() {
        array = iData();
        
        cf.accountIndex=cf.getIntProperty(getValue(accountIndex),-1);
        cf.showOfflineContacts=cf.getBooleanProperty(getValue(showOfflineContacts),false);
        cf.fullscreen=cf.getBooleanProperty(getValue(fullscreen),true);
        cf.fileTransfer=cf.getBooleanProperty(getValue(fileTransfer),true);
        cf.adhoc=cf.getBooleanProperty(getValue(adhoc),true);
        cf.saveHistory=cf.getBooleanProperty(getValue(saveHistory),true);
        cf.def_profile = cf.getIntProperty(getValue(def_profile),0);
//#ifdef SMILES
        cf.smiles=cf.getBooleanProperty(getValue(smiles),true);
//#endif
        cf.showTransports=cf.getBooleanProperty(getValue(showTransports),true);
        cf.selfContact=cf.getBooleanProperty(getValue(selfContact),false);
        cf.ignore=cf.getBooleanProperty(getValue(ignore),false);
        cf.collapsedGroups=cf.getBooleanProperty(getValue(collapsedGroups),false);
        cf.eventComposing=cf.getBooleanProperty(getValue(eventComposing),true);
        cf.gmtOffset=cf.getIntProperty(getValue(gmtOffset),0);
        cf.autoLogin=cf.getBooleanProperty(getValue(autoLogin),true);
//#ifndef WMUC
        cf.autoJoinConferences=cf.getBooleanProperty(getValue(autoJoinConferences),true);
        cf.storeConfPresence=cf.getBooleanProperty(getValue(storeConfPresence),true);
        cf.defGcRoom=cf.getStringProperty(getValue(defGcRoom),"bombusmod@conference.jabber.ru");
        cf.confMessageCount=cf.getIntProperty(getValue(confMessageCount),20);
//#endif
        cf.popupFromMinimized=cf.getBooleanProperty(getValue(popupFromMinimized),true);
        cf.memMonitor=cf.getBooleanProperty(getValue(memMonitor),true);
        cf.font1=cf.getIntProperty(getValue(font1),0);
        cf.font2=cf.getIntProperty(getValue(font2),0);
        cf.font3=cf.getIntProperty(getValue(font3),0);
        cf.font4=cf.getIntProperty(getValue(font4),0);
        cf.autoFocus=cf.getBooleanProperty(getValue(autoFocus),false);
        cf.lang=cf.getStringProperty(getValue(lang), null);
        cf.capsState=cf.getBooleanProperty(getValue(capsState),true);
        cf.textWrap=cf.getIntProperty(getValue(textWrap),0);
        cf.loginstatus=cf.getIntProperty(getValue(loginstatus),0);
//#ifdef HISTORY
//#         cf.lastMessages=cf.getBooleanProperty(getValue(lastMessages),false);
//#         cf.msgPath=cf.getStringProperty(getValue(msgPath),"");
//#         cf.msgLog=cf.getBooleanProperty(getValue(msgLog),false);
//#         cf.msgLogPresence=cf.getBooleanProperty(getValue(msgLogPresence),false);
//#         cf.msgLogConfPresence=cf.getBooleanProperty(getValue(msgLogConfPresence),false);
//#         cf.msgLogConf=cf.getBooleanProperty(getValue(msgLogConf),false);
//#endif
        cf.cp1251=cf.getBooleanProperty(getValue(cp1251),true);
        
        cf.panelsState=cf.getIntProperty(getValue(panelsState),2);
        cf.showTimeTraffic=cf.getBooleanProperty(getValue(showTimeTraffic),false);

        cf.lightState=cf.getBooleanProperty(getValue(lightState),true);
//#ifdef AUTOSTATUS
//#         cf.autoAwayDelay=cf.getIntProperty(getValue(autoAwayDelay),5);
//#         cf.setAutoStatusMessage=cf.getBooleanProperty(getValue(setAutoStatusMessage),false);
//#         cf.autoAwayType=cf.getIntProperty(getValue(autoAwayType),0);
//#endif
        cf.autoScroll=cf.getBooleanProperty(getValue(autoScroll),true);
//#ifdef POPUPS
        cf.popUps=cf.getBooleanProperty(getValue(popUps),true);
//#endif
        cf.showResources=cf.getBooleanProperty(getValue(showResources),true);
        cf.enableVersionOs=cf.getBooleanProperty(getValue(enableVersionOs),true);
        cf.messageLimit=cf.getIntProperty(getValue(messageLimit),300);
        cf.eventDelivery=cf.getBooleanProperty(getValue(eventDelivery),true);
//#ifdef DETRANSLIT
//#         cf.transliterateFilenames=cf.getBooleanProperty(getValue(transliterateFilenames),false);
//#endif
        cf.rosterStatus=cf.getBooleanProperty(getValue(rosterStatus),true);
        cf.queryExit=cf.getBooleanProperty(getValue(queryExit),false);
        VirtualList.showBalloons=cf.showBalloons=cf.getBooleanProperty(getValue(showBalloons),false);
//#ifdef USER_KEYS
//#         cf.userKeys=cf.getBooleanProperty(getValue(userKeys),false);
//#endif
//#ifdef LOGROTATE
//#         cf.msglistLimit=cf.getIntProperty(getValue(msglistLimit),100);
//#endif
        cf.useTabs=cf.getBooleanProperty(getValue(useTabs),true);
        cf.autoSubscribe=cf.getIntProperty(getValue(autoSubscribe), Config.SUBSCR_ASK);
        cf.notInListDropLevel=cf.getIntProperty(getValue(notInListDropLevel), NotInListFilter.ALLOW_ALL);
        cf.useBoldFont=cf.getBooleanProperty(getValue(useBoldFont),false);
        cf.notifyWhenMessageType=cf.getBooleanProperty(getValue(notifyWhenMessageType),false);
//#ifdef PEP
//#         cf.sndrcvmood=cf.getBooleanProperty(getValue(sndrcvmood),true);
//#endif
//#ifdef PEP_TUNE
//#         cf.rcvtune=cf.getBooleanProperty(getValue(sndrcvtune),true);
//#endif
//#ifdef PEP_ACTIVITY
//#         cf.rcvactivity=cf.getBooleanProperty(getValue(rcvactivity),true);
//#endif
//#ifdef CLIPBOARD
//#         cf.useClipBoard=cf.getBooleanProperty(getValue(useClipBoard),true);
//#endif        
//#ifdef DETRANSLIT
//#         cf.autoDeTranslit=cf.getBooleanProperty(getValue(autoDeTranslit),false);
//#endif
        cf.IQNotify=cf.getBooleanProperty(getValue(IQNotify),false);
//#ifdef CLIENTS_ICONS
//#         cf.showClientIcon=cf.getBooleanProperty(getValue(showClientIcon),true);
//#endif
        
        cf.reconnectCount=cf.getIntProperty(getValue(reconnectCount), 10);
        cf.reconnectTime=cf.getIntProperty(getValue(reconnectTime), 15);
        
        cf.executeByNum=cf.getBooleanProperty(getValue(executeByNum),false);
        cf.showNickNames=cf.getBooleanProperty(getValue(showNickNames),false);

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
    
    final static int SEARCH_KEY     = 1;
    final static int SEARCH_VALUE   = 2;
    final static int SEARCH_BREAK   = 3;   

    public Vector iData() {
       String filecontents=loadFile();
       Vector vector=new Vector();
       if (filecontents!=null) {
            try {
                int pos=0; int start_pos=0; int end_pos=0;

                while (true) {
                    String key=null; String value=null; String tempstr=null;
                    start_pos=filecontents.indexOf("<i>",pos); end_pos=filecontents.indexOf("</i>",pos);

                    if (start_pos>-1 && end_pos>-1) {
                        tempstr=filecontents.substring(start_pos+3, end_pos);
                        key=findBlock(tempstr, "k"); 
                        value=findBlock(tempstr, "v"); 
                        vector.addElement(new keyValue(key, value));
                    } else
                        break;

                    pos=end_pos+4;
                }
            } catch (Exception e){ }
        }
        
        filecontents = null;
        return vector;
    }
    
    private String findBlock(String source, String needle){
        int start =source.indexOf("<"+needle+">"); 
        int end = source.indexOf("</"+needle+">");
        if (start<0 || end<0)
            return null;
        
        return source.substring(start+3, end);
    }

   private String loadFile() {
        FileIO f=FileIO.createConnection(file);
        byte[] b = f.fileRead();
        return new String(b, 0, b.length);
   }
    

    private final static String accountIndex="accountIndex";
    private final static String showOfflineContacts="showOfflineContacts";
    private final static String fullscreen="fullscreen";
    private final static String fileTransfer="fileTransfer";
    private final static String adhoc="adhoc";
    private final static String saveHistory="saveHistory";
    private final static String def_profile="def_profile";
//#ifdef SMILES
    private final static String smiles="smiles";
//#endif
    private final static String showTransports="showTransports";
    private final static String selfContact="selfContact";
    private final static String collapsedGroups="collapsedGroups";
    private final static String ignore="ignore";
    private final static String eventComposing="eventComposing";
    private final static String gmtOffset="gmtOffset";
    private final static String autoLogin="autoLogin";
//#ifndef WMUC
    private final static String autoJoinConferences="autoJoinConferences";
    private final static String storeConfPresence="storeConfPresence";
    private final static String defGcRoom="defGcRoom";
    private final static String confMessageCount="confMessageCount";
//#endif
    private final static String popupFromMinimized="popupFromMinimized";
    private final static String notifyBlink="notifyBlink";
    private final static String memMonitor="memMonitor";
    private final static String font1="font1";
    private final static String font2="font2";
    private final static String font3="font3";
    private final static String font4="font4";
    private final static String autoFocus="autoFocus";
    private final static String lang="lang";
    private final static String capsState="capsState";
    private final static String textWrap="textWrap";
    private final static String loginstatus="loginstatus";
//#ifdef HISTORY
//#     private final static String lastMessages="lastMessages";
//#     private final static String msgPath="msgPath";
//#     private final static String msgLog="msgLog";
//#     private final static String msgLogPresence="msgLogPresence";
//#     private final static String msgLogConfPresence="msgLogConfPresence";
//#     private final static String msgLogConf="msgLogConf";
//#endif
    private final static String cp1251="cp1251";
    
    private final static String panelsState="panelsState";
    private final static String showTimeTraffic="showTimeTraffic";

    private final static String lightState="lightState";
    private final static String notifySound="notifySound";
//#ifdef AUTOSTATUS
//#     private final static String autoAwayDelay="autoAwayDelay";
//#     private final static String setAutoStatusMessage="setAutoStatusMessage";
//#     private final static String autoAwayType="autoAwayType";
//#endif
    private final static String autoScroll="autoScroll";
//#ifdef POPUPS
    private final static String popUps="popUps";
//#endif
    private final static String showResources="showResources";
    private final static String enableVersionOs="enableVersionOs";
    private final static String messageLimit="messageLimit";
    private final static String eventDelivery="eventDelivery";
//#ifdef DETRANSLIT
//#     private final static String transliterateFilenames="transliterateFilenames"; 
//#     private final static String autoDeTranslit="autoDeTranslit"; 
//#endif
    private final static String rosterStatus="rosterStatus";
    private final static String queryExit="queryExit";
    private final static String showBalloons="showBalloons";
    private final static String notifyPicture="notifyPicture";
//#ifdef USER_KEYS
//#     private final static String userKeys="userKeys";
//#endif
//#ifdef LOGROTATE
//#     private final static String msglistLimit="msglistLimit";
//#endif
    private final static String useTabs="useTabs";
    private final static String autoSubscribe="autoSubscribe";
    private final static String notInListDropLevel="notInListDropLevel";
    private final static String useBoldFont="useBoldFont";
    private final static String notifyWhenMessageType="notifyWhenMessageType";
//#ifdef PEP
//#     private final static String sndrcvmood="sndrcvmood";
//#endif
//#ifdef PEP_TUNE
//#     private final static String sndrcvtune="sndrcvtune";
//#endif
//#ifdef PEP_ACTIVITY
//#     private final static String rcvactivity="rcvactivity";
//#endif
//#ifdef CLIPBOARD
//#     private final static String useClipBoard="useClipBoard";
//#endif
    private final static String IQNotify="IQNotify";
    private final static String showClientIcon="showClientIcon";
    
    private final static String reconnectCount="reconnectCount";
    private final static String reconnectTime="reconnectTime";
    
    private final static String executeByNum="executeByNum";
    
    private final static String showNickNames="showNickNames";
    
    class keyValue {
        String value; String key;

        public keyValue(String key, String value) { this.key=key; this.value=(value==null)?"":value; }

        public String getKey() { return key; }
        public String getValue() { return value; }
    }
}
