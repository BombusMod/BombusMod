/*
 * Config.java
 *
 * Created on 19.03.2005, 18:37
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
import Alerts.AlertProfile;
import images.ActionsIcons;
import images.RosterIcons;
//#ifdef SMILES
import images.SmilesIcons;
//#endif
//#ifdef FILE_IO
import io.file.FileIO;
//#endif
import java.io.*;
import java.util.*;
import midlet.BombusMod;
import Fonts.FontCache;
//#ifdef CLIENTS_ICONS
import images.ClientsIcons;
//#endif
import util.StringLoader;
import ui.Time;
import ui.VirtualList;
import io.NvStorage;

/**
 *
 * @author Eugene Stahov
 */
public class Config {
    // Singleton
    private static Config instance;
    
    public final int vibraLen=500;

    public static int KEY_BACK = -11;
    public static int SOFT_LEFT = -1000;
    public static int SOFT_RIGHT = -1000;
    
    public final static int SUBSCR_AUTO=0;
    public final static int SUBSCR_ASK=1;
    public final static int SUBSCR_DROP=2;
    public final static int SUBSCR_REJECT=3;
    
    public final static int NOT_DETECTED=0;
    public final static int NONE=-1;
    public final static int SONYE=1;
    public final static int NOKIA=2;
    public final static int SIEMENS=3;
    public final static int SIEMENS2=4;
    public final static int MOTO=5;
    public final static int MOTOEZX=6;
    public final static int WINDOWS=7;
    public final static int INTENT=8;
    public final static int J2ME=9;
    public final static int NOKIA_9XXX=10;
    public final static int SONYE_M600=11;
//#if !ZLIB
//#     public final static int XENIUM99=12;
//#endif
    public final static int SAMSUNG=14;
    public final static int LG=15;
    public final static int JBED=16;
    public final static int WTK=50;
    public final static int OTHER=99;

    StaticData sd = StaticData.getInstance();
    
    private static String platformName;
    
    public boolean ghostMotor=false;
    //public boolean blFlash=!ghostMotor; //true;
    
    
    public boolean muc119=true;	// before muc 1.19 use muc#owner instead of muc#admin
    
    public char keyLock='*';
    public char keyVibra='#';
    
//#ifdef AUTOSTATUS
//#     public final static int AWAY_OFF=0;
//#     public final static int AWAY_LOCK=1;
//#     public final static int AWAY_MESSAGE=2;
//#     public final static int AWAY_IDLE=3;
//#     
//#     public int autoAwayType=0;
//#     public int autoAwayDelay=5; //5 minutes
//#     public boolean useMyStatusMessages=true;
//#endif
    
//#ifdef HISTORY
//#      public String msgPath="";
//#      public boolean msgLog=false;
//#      public boolean msgLogPresence=false;
//#      public boolean msgLogConf=false;
//#      public boolean msgLogConfPresence=false;
//#      public boolean lastMessages=false;
//#endif
    public boolean cp1251=true;     
//#ifndef WMUC
    public String defGcRoom="bombusmod@conference.jabber.ru";
    public boolean storeConfPresence=true;   
    public boolean autoJoinConferences=true;
    public int confMessageCount=20;
//#endif
    // non-volatile values
    public int accountIndex=-1;
public static boolean fullscreen=
//#ifdef MENU_LISTENER
            true;
//#else
//#             false;
//#endif    
    public int def_profile=0;
//#ifdef SMILES
    public boolean smiles=true;
//#endif
    public boolean showOfflineContacts=false;
    public boolean showTransports=true;
    public boolean selfContact=false;
    public boolean ignore=false;
    public boolean eventComposing=true;   
    public boolean autoLogin=true;
    public boolean autoFocus=false;
    public int loginstatus=0;//loginstatus
    public int gmtOffset;
    public boolean popupFromMinimized=true;
    public boolean memMonitor=true;

    public int rosterFont=0;
    public int msgFont=0;
    public int barFont=0;
    public int baloonFont=0;
    
    public String lang;  //not detected (en)
    public boolean capsState=false;
    public int textWrap=0;
    public int autoSubscribe=SUBSCR_ASK;

    // runtime values
    public boolean allowMinimize=false;
    public int profile=0;
    public int lastProfile=0;
    public boolean istreamWaiting;
    public int phoneManufacturer=NOT_DETECTED;

    public int panelsState=
//#ifdef MENU_LISTENER
            2; //default state both panels show, reverse disabled
//#else
//#             1; // for old menu - show only top panel
//#endif

    public boolean lightState=false;
    public boolean autoScroll=true;
//#ifdef POPUPS
    public boolean popUps=true;
//#endif
    public boolean showResources=true;
    public boolean enableVersionOs=true;
    public boolean collapsedGroups=true;
    public int messageLimit=512;
    public int widthScroll2=10;
    public boolean widthSystemgc=false;
    public boolean advTouch=true;
    public boolean autoClean=false;
    public boolean eventDelivery=true;
//#ifdef DETRANSLIT
//#     public boolean transliterateFilenames=false;
//#     public boolean autoDeTranslit=false;
//#endif
    public boolean rosterStatus=true;
//#ifdef PEP
//#     public boolean sndrcvmood = false;
//#ifdef PEP_TUNE
//#     public boolean rcvtune = false;
//#endif
//#ifdef PEP_ACTIVITY
//#     public boolean rcvactivity = false;
//#endif
//#endif
    public boolean queryExit = false;
    public int notInListDropLevel=NotInListFilter.ALLOW_ALL; //enable all
    public boolean showBalloons = true;
//#ifdef USER_KEYS
//#     public boolean userKeys = false;
//#endif
//#ifdef LOGROTATE
//#     public int msglistLimit=500;
//#endif
    public boolean useTabs=true;
    public boolean notifyBlink=false;
    public boolean notifySound=false;
    public boolean notifyPicture=false;
    public boolean useBoldFont=false;
//#ifdef RUNNING_MESSAGE
//#     public boolean notifyWhenMessageType = false;
//#endif
//#ifdef CLIPBOARD
//#     public boolean useClipBoard = true;
//#endif
    public boolean firstRun = true;
    
    public String verHash="";
    public String resolvedHost="";
    public int resolvedPort=0;
    
    public boolean IQNotify=false;
//#ifdef CLIENTS_ICONS
    public boolean showClientIcon=true;
//#endif

//#ifdef JUICK
//#     public String juickJID=null; // Undefined.
//#endif

    public int reconnectCount=10;
    public int reconnectTime=15;

    public boolean executeByNum;
    public boolean showNickNames;

    public boolean fileTransfer=true;
    public boolean adhoc=false;
    public boolean saveHistory=false;
    
    public boolean oldSE=false;

    public boolean oldNokiaS60 = false;
    public boolean NokiaS40 = false;
    
    public boolean showTimeTraffic=false;
    
    public boolean swapSendAndSuspend=false;
    
    public static Config getInstance(){
	if (instance==null) {
	    instance=new Config();
	    instance.loadFromStorage();

            FontCache.roster=instance.rosterFont;
            FontCache.msg=instance.msgFont;
            
            FontCache.bar=instance.barFont;
            FontCache.baloon=instance.baloonFont;
            //FontCache.resetCache();
	}
        if (instance.firstRun)
            VirtualList.canBack = true; 
	return instance;
    }
    
    /** Creates a new instance of Config */
    private Config() {
        getPhoneManufacturer();
        VirtualList.phoneManufacturer=phoneManufacturer;
        
	int gmtloc=TimeZone.getDefault().getRawOffset()/3600000;
	gmtOffset=gmtloc;
	
	short greenKeyCode=-1000;
        
        switch (phoneManufacturer) {
            case SONYE:
                //prefetch images
                RosterIcons.getInstance();
                ActionsIcons.getInstance();
//#ifdef SMILES
                if (smiles) SmilesIcons.getInstance();
//#endif
//#ifdef CLIENTS_ICONS
                if (showClientIcon) ClientsIcons.getInstance();
//#endif


//                if (widthSystemgc) { _vt
                    System.gc();
                    try { Thread.sleep(50); } catch (InterruptedException e){}
//                } _vt
                
                allowMinimize=true;
                greenKeyCode=-10;
                break;
            case SONYE_M600:
                KEY_BACK=-11;
                greenKeyCode=13;
                break;
            case WTK:
                greenKeyCode=-10;
                break;
            case NOKIA:
                KEY_BACK=VirtualList.NOKIA_PEN;
                greenKeyCode=-10;
                allowMinimize=true;
                break;
            case SIEMENS:
            case SIEMENS2:
                keyLock='#';
                keyVibra='*';
                KEY_BACK=-4;
                greenKeyCode=VirtualList.SIEMENS_GREEN;
                break;
            case WINDOWS:
                greenKeyCode=-5;
                VirtualList.keyClear=8;
                break;
            case MOTO:
                ghostMotor=true;
                istreamWaiting=true;
                greenKeyCode=-10;
                break;
            case MOTOEZX:
                VirtualList.keyVolDown=VirtualList.MOTOE680_VOL_DOWN;
                KEY_BACK=VirtualList.MOTOE680_REALPLAYER;
		greenKeyCode=-31;
                break;  
//#if !ZLIB
//#             case XENIUM99:
//#                 istreamWaiting=false; //is it critical for phillips xenium?
//#                 break;
//#endif
        }
	VirtualList.greenKeyCode=greenKeyCode;
    }
    
    protected void loadFromStorage(){
        DataInputStream inputStream=NvStorage.ReadFileRecord("config", 0);
	try {
	    accountIndex = inputStream.readInt();
	    showOfflineContacts=inputStream.readBoolean();
	    fullscreen=inputStream.readBoolean();
	    def_profile = inputStream.readInt()%4;
//#ifdef SMILES
	    smiles=inputStream.readBoolean();
//#else
//#             inputStream.readBoolean();
//#endif
	    showTransports=inputStream.readBoolean();
	    selfContact=inputStream.readBoolean();
	    collapsedGroups=inputStream.readBoolean();
	    ignore=inputStream.readBoolean();
	    eventComposing=inputStream.readBoolean();
	    gmtOffset=inputStream.readInt();
	    inputStream.readInt(); //locOffset
	    autoLogin=inputStream.readBoolean();
	    autoJoinConferences=inputStream.readBoolean();
	    popupFromMinimized=inputStream.readBoolean();
	    notifyBlink=inputStream.readBoolean();
	    memMonitor=inputStream.readBoolean();
            rosterFont=inputStream.readInt();
            msgFont=inputStream.readInt();
            autoFocus=inputStream.readBoolean();
            notInListDropLevel=inputStream.readInt();
            storeConfPresence=inputStream.readBoolean();
            capsState=inputStream.readBoolean();
	    textWrap=inputStream.readInt();
            loginstatus=inputStream.readInt();
//#ifdef HISTORY
//#             msgPath=inputStream.readUTF();
//#             msgLog=inputStream.readBoolean();
//#             msgLogPresence=inputStream.readBoolean();
//#             msgLogConfPresence=inputStream.readBoolean();
//#             msgLogConf=inputStream.readBoolean();
//#else
            inputStream.readUTF();
            inputStream.readBoolean();
            inputStream.readBoolean();
            inputStream.readBoolean();
            inputStream.readBoolean();
//#endif
            cp1251=inputStream.readBoolean();
//#ifdef AUTOSTATUS
//#             autoAwayDelay=inputStream.readInt();
//#else
            inputStream.readInt();
//#endif
            defGcRoom=inputStream.readUTF();
            firstRun=inputStream.readBoolean();
            panelsState=inputStream.readInt();
            confMessageCount=inputStream.readInt();

            fileTransfer=inputStream.readBoolean(); //newMenu

            lightState=inputStream.readBoolean();
            notifySound=inputStream.readBoolean();
//#ifdef HISTORY
//#             lastMessages=inputStream.readBoolean();
//#else
            inputStream.readBoolean();
//#endif
//#ifdef AUTOSTATUS
//#             useMyStatusMessages=inputStream.readBoolean();
//#             autoAwayType=inputStream.readInt();
//#else
            inputStream.readBoolean();
            inputStream.readInt();
//#endif
            autoScroll=inputStream.readBoolean();
//#ifdef POPUPS
            popUps=inputStream.readBoolean();
//#else
//#             inputStream.readBoolean();
//#endif
            showResources=inputStream.readBoolean();
            
            saveHistory=inputStream.readBoolean(); //antispam

            enableVersionOs=inputStream.readBoolean();
            messageLimit=inputStream.readInt();
            lang=inputStream.readUTF();
            eventDelivery=inputStream.readBoolean();
//#ifdef DETRANSLIT
//#             transliterateFilenames=inputStream.readBoolean();
//#else
            inputStream.readBoolean();
//#endif
            
            rosterStatus=inputStream.readBoolean();
            
            queryExit=inputStream.readBoolean();
            notifyPicture=inputStream.readBoolean();
            showBalloons=inputStream.readBoolean();
//#ifdef USER_KEYS
//#             userKeys=inputStream.readBoolean();
//#else
            inputStream.readBoolean();
//#endif
//#ifdef LOGROTATE
//#             msglistLimit=inputStream.readInt();
//#else
            inputStream.readInt();
//#endif
            useTabs=inputStream.readBoolean();
            autoSubscribe=inputStream.readInt();
            useBoldFont=inputStream.readBoolean();
//#ifdef RUNNING_MESSAGE
//#             notifyWhenMessageType = inputStream.readBoolean();
//#else
            inputStream.readBoolean();
//#endif
            IQNotify=inputStream.readBoolean(); //IRC_LIKE
//#ifdef PEP
//#             sndrcvmood = inputStream.readBoolean();
//#else
            inputStream.readBoolean();
//#endif
            inputStream.readUTF(); //scheme

//#ifdef CLIPBOARD
//#             useClipBoard = inputStream.readBoolean();
//#else
            inputStream.readBoolean();
//#endif
//#ifdef PEP_TUNE
//#             rcvtune = inputStream.readBoolean();
//#else
            inputStream.readBoolean();
//#endif
            barFont=inputStream.readInt();
            baloonFont=inputStream.readInt();
            
            verHash=inputStream.readUTF();
            resolvedHost=inputStream.readUTF();
            resolvedPort=inputStream.readInt();
            
//#ifdef DETRANSLIT
//#             autoDeTranslit=inputStream.readBoolean();
//#else
            inputStream.readBoolean();
//#endif
//#ifdef CLIENTS_ICONS
            showClientIcon=inputStream.readBoolean();
//#else
//#             inputStream.readBoolean();
//#endif
            
            reconnectCount=inputStream.readInt();
            reconnectTime=inputStream.readInt();
            
            executeByNum=inputStream.readBoolean();
            
            showNickNames=inputStream.readBoolean();
            
            adhoc=inputStream.readBoolean();
//#ifdef PEP_ACTIVITY
//#             rcvactivity = inputStream.readBoolean();
//#else
            inputStream.readBoolean();
//#endif
            oldSE=inputStream.readBoolean();
            
            showTimeTraffic=inputStream.readBoolean();
            
            swapSendAndSuspend=inputStream.readBoolean();
            widthScroll2=inputStream.readInt();
            widthSystemgc=inputStream.readBoolean();
//#ifdef JUICK
//#             juickJID=inputStream.readUTF();
//#             if (juickJID.equals("")) juickJID = null;
//#             try {
//#             if (sd.roster.getJuickContacts().size()<2)
//#                 juickJID=null;
//#             } catch (Exception ex) {}
//#else
            inputStream.readUTF();
//#endif
            advTouch = inputStream.readBoolean();
            autoClean = inputStream.readBoolean();
	    inputStream.close();
            inputStream=null;
	} catch (Exception e) {
            try {
                if (inputStream!=null) {
                    inputStream.close();
                    inputStream=null;
                }
            } catch (IOException ex) { }
	}
	
	lastProfile=profile=def_profile;
        if (lastProfile==AlertProfile.VIBRA) lastProfile=0;
	updateTime();
	VirtualList.fullscreen=fullscreen;
	VirtualList.memMonitor=memMonitor;
        VirtualList.showBalloons=showBalloons;
        VirtualList.panelsState=panelsState;
        VirtualList.showTimeTraffic=showTimeTraffic;

//#ifdef USER_KEYS
//#ifdef PLUGINS
//#         if(!sd.UserKeys) userKeys=false;
//#endif
//#         VirtualList.userKeys=userKeys;
//#endif
        
//#ifdef PLUGINS
//#ifdef FILE_TRANSFER
//#         if(!sd.FileTransfer) fileTransfer=false;
//#endif
//#ifdef PEP
//#         if(!sd.PEP) sndrcvmood=false;
//#endif
//#ifdef PEP_TUNE
//#         if(!sd.PEP) rcvtune=false;
//#endif
//#ifdef PEP_ACTIVITY
//#         if (!sd.PEP) rcvactivity=false;
//#endif
//#ifdef ADHOC
//#         if(!sd.Adhoc) adhoc=false;
//#endif
//#ifdef CLIENTS_ICONS
//#         if(!sd.ClientsIcons) showClientIcon=false;
//#endif
//#ifdef HISTORY
//#         if(!sd.History) saveHistory=false;
//#endif
//#endif
    }
    
    public String langFileName(){
        if (lang==null) {
            //auto-detecting
            lang=System.getProperty("microedition.locale");
            System.out.println(lang);
            //We will use only language code from locale
            if (lang==null) lang="en"; else lang=lang.substring(0, 2).toLowerCase();
        }
        
        if (lang.equals("en")) return null;  //english
	Vector files[]=new StringLoader().stringLoader("/lang/res.txt", 3);
        for (int i=0; i<files[0].size(); i++) {
            String langCode=(String) files[0].elementAt(i);
            if (lang.equals(langCode))
        	return (String) files[1].elementAt(i);
        }
        return null; //unknown language ->en
    }
    
    public void saveToStorage(){
	DataOutputStream outputStream=NvStorage.CreateDataOutputStream();
	
	try {
	    outputStream.writeInt(accountIndex);
	    outputStream.writeBoolean(showOfflineContacts);
	    outputStream.writeBoolean(fullscreen);
	    outputStream.writeInt(def_profile);
//#ifdef SMILES
	    outputStream.writeBoolean(smiles);
//#else
//#             outputStream.writeBoolean(false);
//#endif
	    outputStream.writeBoolean(showTransports);
	    outputStream.writeBoolean(selfContact);
	    outputStream.writeBoolean(collapsedGroups);
	    outputStream.writeBoolean(ignore);
	    outputStream.writeBoolean(eventComposing);
	    outputStream.writeInt(gmtOffset);
	    outputStream.writeInt(0); //locOffset
	    outputStream.writeBoolean(autoLogin);
	    outputStream.writeBoolean(autoJoinConferences);
            outputStream.writeBoolean(popupFromMinimized);
	    outputStream.writeBoolean(notifyBlink);
	    outputStream.writeBoolean(memMonitor);
            outputStream.writeInt(rosterFont);
            outputStream.writeInt(msgFont);
            outputStream.writeBoolean(autoFocus);
            outputStream.writeInt(notInListDropLevel);
            outputStream.writeBoolean(storeConfPresence); 
            outputStream.writeBoolean(capsState); 
	    outputStream.writeInt(textWrap);
            outputStream.writeInt(loginstatus);
//#ifdef HISTORY
//#             outputStream.writeUTF(msgPath);
//#             outputStream.writeBoolean(msgLog);
//#             outputStream.writeBoolean(msgLogPresence);
//#             outputStream.writeBoolean(msgLogConfPresence);
//#             outputStream.writeBoolean(msgLogConf);
//#else
            outputStream.writeUTF("");
            outputStream.writeBoolean(false);
            outputStream.writeBoolean(false);
            outputStream.writeBoolean(false);
            outputStream.writeBoolean(false);
//#endif
            outputStream.writeBoolean(cp1251);
//#ifdef AUTOSTATUS
//#             outputStream.writeInt(autoAwayDelay);
//#else
            outputStream.writeInt(5);
//#endif
            outputStream.writeUTF(defGcRoom);
            outputStream.writeBoolean(firstRun);
            outputStream.writeInt(panelsState);
            outputStream.writeInt(confMessageCount);

            outputStream.writeBoolean(fileTransfer); //newMenu

            outputStream.writeBoolean(lightState);
            outputStream.writeBoolean(notifySound);
//#ifdef HISTORY
//#             outputStream.writeBoolean(lastMessages);
//#else
            outputStream.writeBoolean(false);
//#endif
//#ifdef AUTOSTATUS
//#             outputStream.writeBoolean(useMyStatusMessages);
//#             outputStream.writeInt(autoAwayType);
//#else
            outputStream.writeBoolean(false);
            outputStream.writeInt(0);
//#endif
            outputStream.writeBoolean(autoScroll);
//#ifdef POPUPS
            outputStream.writeBoolean(popUps);
//#else
//#             outputStream.writeBoolean(false);
//#endif
            outputStream.writeBoolean(showResources);

            outputStream.writeBoolean(saveHistory); //antispam

            outputStream.writeBoolean(enableVersionOs);
            outputStream.writeInt(messageLimit);
            outputStream.writeUTF(lang);      
            outputStream.writeBoolean(eventDelivery);
//#ifdef DETRANSLIT
//#             outputStream.writeBoolean(transliterateFilenames);
//#else
            outputStream.writeBoolean(false);
//#endif

            outputStream.writeBoolean(rosterStatus);

            outputStream.writeBoolean(queryExit);
            outputStream.writeBoolean(notifyPicture);
            outputStream.writeBoolean(showBalloons);
//#ifdef USER_KEYS
//#             outputStream.writeBoolean(userKeys);
//#else
            outputStream.writeBoolean(false);
//#endif
//#ifdef LOGROTATE
//#             outputStream.writeInt(msglistLimit);
//#else
            outputStream.writeInt(512);
//#endif
            outputStream.writeBoolean(useTabs);
            outputStream.writeInt(autoSubscribe);
            outputStream.writeBoolean(useBoldFont);
//#ifdef RUNNING_MESSAGE
//#             outputStream.writeBoolean(notifyWhenMessageType);
//#else
            outputStream.writeBoolean(false);
//#endif
            outputStream.writeBoolean(IQNotify); //IRC_LIKE
//#ifdef PEP
//#             outputStream.writeBoolean(sndrcvmood);
//#else
            outputStream.writeBoolean(false);
//#endif
            outputStream.writeUTF("");//scheme
//#ifdef CLIPBOARD
//#             outputStream.writeBoolean(useClipBoard);
//#else
            outputStream.writeBoolean(false);
//#endif
//#ifdef PEP_TUNE
//#             outputStream.writeBoolean(rcvtune);
//#else
            outputStream.writeBoolean(false);
//#endif
            outputStream.writeInt(barFont);
            outputStream.writeInt(baloonFont);
            
            outputStream.writeUTF(verHash);
            outputStream.writeUTF(resolvedHost);
            outputStream.writeInt(resolvedPort);
            
//#ifdef DETRANSLIT
//#             outputStream.writeBoolean(autoDeTranslit);
//#else
            outputStream.writeBoolean(false);
//#endif
//#ifdef CLIENTS_ICONS
            outputStream.writeBoolean(showClientIcon);
//#else
//#             outputStream.writeBoolean(false);
//#endif
            
            outputStream.writeInt(reconnectCount);
            outputStream.writeInt(reconnectTime);
            
            outputStream.writeBoolean(executeByNum);
            
            outputStream.writeBoolean(showNickNames);
            
            outputStream.writeBoolean(adhoc);
            
//#ifdef PEP_ACTIVITY
//#             outputStream.writeBoolean(rcvactivity);
//#else
            outputStream.writeBoolean(false);
//#endif
            outputStream.writeBoolean(oldSE);
            
            outputStream.writeBoolean(showTimeTraffic);
            
            outputStream.writeBoolean(swapSendAndSuspend);
            outputStream.writeInt(widthScroll2);
            outputStream.writeBoolean(widthSystemgc);
//#ifdef JUICK
//#              if (juickJID == null)
//#                     outputStream.writeUTF("");
//#         else outputStream.writeUTF(juickJID);
//#else
            outputStream.writeUTF("");
//#endif
            outputStream.writeBoolean(advTouch);
            outputStream.writeBoolean(autoClean);
	} catch (Exception e) { }
	
	NvStorage.writeFileRecord(outputStream, "config", 0, true);
    }

    
    public void updateTime(){
	Time.setOffset(gmtOffset);
    }

    
    private final void getPhoneManufacturer() {
        if (phoneManufacturer==NOT_DETECTED) {
            String platform=getPlatformName();
            phoneManufacturer=NONE;

            if (platform.endsWith("(NSG)")) {
                phoneManufacturer=SIEMENS;
                return;
            } else if (platform.startsWith("SIE")) {
                phoneManufacturer=SIEMENS2;
                return;
            } else if (platform.startsWith("Motorola-EZX")) {
                phoneManufacturer=MOTOEZX;
                return;
            } else if (platform.startsWith("Moto")) {
                phoneManufacturer=MOTO;
                return;
            } else if (platform.startsWith("SonyE")) {
                if (platform.startsWith("SonyEricssonM600")) {
                    phoneManufacturer=SONYE_M600;
                    return;
                }
                phoneManufacturer=SONYE;
                return;
//#if !ZLIB
//#             } else if (platform.indexOf("9@9")>-1) {
//#                 phoneManufacturer=XENIUM99;
//#                 return;
//#endif
            } else if (platform.startsWith("Windows")) {
                phoneManufacturer=WINDOWS;
                return;
            } else if (platform.startsWith("Nokia9500") || 
                platform.startsWith("Nokia9300") || 
                platform.startsWith("Nokia9300i")) {
                phoneManufacturer=NOKIA_9XXX;
                return;
            } else if (platform.startsWith("Nokia")) {
                phoneManufacturer=NOKIA;
                int firstDotIndex = platform.indexOf('.');
                 if ((-1 != firstDotIndex) && (-1 == platform.indexOf('.', firstDotIndex + 1))) {
                    // s40
                    NokiaS40 = true;
                    return;
                }
                if (platform.indexOf("java_build_version") == 0)
                     oldNokiaS60 = true; // buggy S60 3.1 or older
                return;
            } else if (platform.startsWith("Intent")) {
                phoneManufacturer=INTENT;
                return;
            } else if (platform.startsWith("wtk") || platform.endsWith("wtk")) {
                phoneManufacturer=WTK;
                return;
            } else if (platform.startsWith("Samsung")) {
                phoneManufacturer=SAMSUNG;
                return;
            } else if (platform.startsWith("LG")) {
                phoneManufacturer=LG;
                return;
            } else if (platform.startsWith("j2me")) {
                phoneManufacturer=J2ME;
                return;
            } else if (platform.startsWith("Jbed")) {
                phoneManufacturer=JBED;
//#ifdef FILE_IO
                try { FileIO f=FileIO.createConnection(""); } catch (Exception ex) { }
//#endif
                return;
            }else {
                phoneManufacturer=OTHER;
            }
        }
    }
    
    public static String getPlatformName() {
        if (platformName==null) {
            platformName=System.getProperty("microedition.platform");
            
            String sonyJava=System.getProperty("com.sonyericsson.java.platform");
            if (sonyJava!=null) platformName=platformName+"/"+sonyJava;
            
            String device=System.getProperty("device.model");
            String firmware=System.getProperty("device.software.version");
                        //detecting Samsung
            try {
                Class.forName("com.samsung.util.AudioClip");
                platformName="Samsung-generic";
            } catch (Throwable t0) {
                try{
                    Class.forName("com.samsung.util.Vibration");
                    platformName="Samsung-generic";
                }catch(Throwable t1){}
            }


            
            if (platformName==null) platformName="Motorola";
            
             if (platformName.startsWith("j2me")) {
                if (device!=null) if (device.startsWith("wtk-emulator")) {
                     platformName=device;
                }
                if (device!=null && firmware!=null)
                    platformName="Motorola"; // buggy v360
		else {
		    // Motorola EZX phones
		    String hostname=System.getProperty("microedition.hostname");
		    if (hostname!=null) {
		        platformName="Motorola-EZX";
		        if (device!=null) {
		    	    // Motorola EZX ROKR
			    hostname=device;
                        }
                     
                        if (hostname.indexOf("(none)")<0)
                         platformName+="/"+hostname;
                    }
		}
             }
 	    //else 
		if (platformName.startsWith("Moto")) {
                if (device==null) device=System.getProperty("funlights.product");
                if (device!=null) platformName="Motorola-"+device;
                try { // thanks vitalyster
                   Class.forName("com.nokia.mid.ui.DeviceControl");
                   platformName="Nokia"; // FS #896
                }
                catch (Throwable ex) {}
            }

            if (platformName.indexOf("SIE") > -1) {
                platformName=System.getProperty("microedition.platform")+" (NSG)";
            } else if (System.getProperty("com.siemens.OSVersion")!=null) {
                platformName="SIE-"+System.getProperty("microedition.platform")+"/"+System.getProperty("com.siemens.OSVersion");
            }

            try {
                Class.forName("com.samsung.util.Vibration");
                platformName="Samsung";
            } catch (Throwable ex) { }
            
            try {
                Class.forName("mmpp.media.MediaPlayer");
                platformName="LG";
            } catch (Throwable ex) {
                try {
                    Class.forName("mmpp.phone.Phone");
                    platformName="LG";
                } catch (Throwable ex1) {
                    try {
                        Class.forName("mmpp.lang.MathFP");
                        platformName="LG";
                    } catch (Throwable ex2) {
                        try {
                            Class.forName("mmpp.media.BackLight");
                            platformName="LG";
                        } catch (Throwable ex3) { }
                    }
                }
            }
        }
        return platformName;
    }

    public static String getOs() {
        return getPlatformName();
    }
    
    public final String getStringProperty(final String key, final String defvalue) {
	try {
	    String s=BombusMod.getInstance().getAppProperty(key);
	    return (s==null)?defvalue:s;
	} catch (Exception e) {	}
        return defvalue;
    }
    
    public final int getIntProperty(final String key, final int defvalue) {
	try { return Integer.parseInt(key); } catch (Exception e) { }
	return defvalue;
    }
    
    /*public final char getCharProperty(final String key, final char defvalue) {
	try { return key.charAt(0); } catch (Exception e) {	}
        return defvalue;
    }*/
    
    public final boolean getBooleanProperty(final String key, final boolean defvalue) {
	try {
	    if (key.equals("true")) return true;
	    if (key.equals("yes")) return true;
	    if (key.equals("1")) return true;
            return false;
	} catch (Exception e) { }
        return defvalue;
    }
    
}
