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
import images.MenuIcons;
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
import util.StringLoader;
import ui.Time;
import ui.VirtualList;
import io.NvStorage;
//import javax.microedition.rms.*;

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
    public final static int XENIUM99=12;
    public final static int SAMSUNG=14;
    public final static int LG=15;
    public final static int JBED=16;
    public final static int WTK=50;
    public final static int OTHER=99;
    
/*
 PbICb [2277] 
16  ���, 19 :34 
��� ���� ��������� ��� (����� ���������) 
static void SetToMoto() { 
MainForm.KEY_JOYSTICK = -20; 
MainForm.KEY_UP = -1; 
MainForm.KEY_DOWN = -6; 
MainForm.KEY_LEFT = -2; 
MainForm.KEY_RIGHT = -5; 
MainForm.KEY_GREEN = -10; 
MainForm.KEY_LSOFT = -21; 
MainForm.KEY_RSOFT = -22; 
type = \"-=Motorola=-\"; 
} static void SetToNokia() { 
MainForm.KEY_JOYSTICK = -5; 
MainForm.KEY_UP = -1; 
MainForm.KEY_DOWN = -2; 
MainForm.KEY_LEFT = -3; 
MainForm.KEY_RIGHT = -4; 
MainForm.KEY_RED = -11; 
MainForm.KEY_GREEN = -10; 
MainForm.KEY_LSOFT = -6; 
MainForm.KEY_RSOFT = -7; 
type = \"-=Nokia=-\"; 
} static void SetToSamsung() { 
MainForm.KEY_JOYSTICK = -5; 
MainForm.KEY_UP = -1; 
MainForm.KEY_DOWN = -2; 
MainForm.KEY_LEFT = -3; 
MainForm.KEY_RIGHT = -4; 
MainForm.KEY_RED = -23; 
MainForm.KEY_LSOFT = -6; 
MainForm.KEY_RSOFT = -7; 
type = \"-=Samsung=-\"; 
} static void SetToSonyEric() { 
MainForm.KEY_JOYSTICK = -5; 
MainForm.KEY_UP = -1; 
MainForm.KEY_DOWN = -2; 
MainForm.KEY_LEFT = -3; 
MainForm.KEY_RIGHT = -4; 
MainForm.KEY_RED = -8; 
MainForm.KEY_GREEN = -11; 
MainForm.KEY_LSOFT = -6; 
MainForm.KEY_RSOFT = -7; 
type = \"-=Sony Ericsson=-\"; 
} static void SetToSiemens() { 
MainForm.KEY_JOYSTICK = -26; 
MainForm.KEY_UP = -59; 
MainForm.KEY_DOWN = -60; 
MainForm.KEY_LEFT = -61; 
MainForm.KEY_RIGHT = -62; 
MainForm.KEY_RED = -12; 
MainForm.KEY_GREEN = -11; 
MainForm.KEY_LSOFT = -1; 
MainForm.KEY_RSOFT = -4; 
MainForm.KEY_FOTO = -20; 
MainForm.KEY_PLUS = -13; 
MainForm.KEY_MINUS = -14; 
type = \"-=Siemens=-\"; 
}
 */
    
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
//#     public boolean setAutoStatusMessage=true;
//#endif
    
//#ifdef HISTORY
//#      public String msgPath="";
//#      public boolean msgLog=false;
//#      public boolean msgLogPresence=false;
//#      public boolean msgLogConf=false;
//#      public boolean msgLogConfPresence=false;
//#     public boolean lastMessages=false;
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
    public boolean fullscreen=false;
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
    public int locOffset;
    public boolean popupFromMinimized=true;
    public boolean memMonitor=true;
//#ifdef NEW_MENU
    public boolean newMenu=false;
//#endif
    public int font1=0;
    public int font2=0;
    public int font3=0;
    public int font4=0;
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

    public int isbottom=2; //default state both panels show, reverse disabled
    public boolean lightState=false;
    public boolean autoScroll=true;
//#ifdef POPUPS
    public boolean popUps=true;
//#endif
    public boolean showResources=true;
//#ifdef ANTISPAM
//#     public boolean antispam=false;
//#endif
    public boolean enableVersionOs=true;
    public boolean collapsedGroups=true;
    public int messageLimit=512;
    public boolean eventDelivery=false;
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
//#endif
    public boolean queryExit = false;
    public int notInListDropLevel=NotInListFilter.ALLOW_ALL; //enable all
    public boolean showBalloons = true;
//#ifdef USER_KEYS
//#     public boolean userKeys = false;
//#endif
//#ifdef AUTODELETE
//#     public int msglistLimit=100;
//#endif
    public boolean useTabs=true;
    public boolean notifyBlink=true;
    public boolean notifySound=false;
    public boolean notifyPicture=false;
    public boolean useBoldFont=false;
    public boolean notifyWhenMessageType = false;
//#ifdef CLIPBOARD
//#     public boolean useClipBoard = true;
//#endif
    public boolean firstRun = true;
    
    public String verHash="";
    public String resolvedHost="";
    public int resolvedPort=0;
    
    public boolean IQNotify=false;
//#ifdef CLIENTS_ICONS
//#     public boolean showClientIcon=true;
//#endif
    
    public int reconnectCount=10;
    public int reconnectTime=15;
    
    public static Config getInstance(){
	if (instance==null) {
	    instance=new Config();
	    instance.loadFromStorage();

            FontCache.rosterFontSize=instance.font1;
            FontCache.msgFontSize=instance.font2;
            
            FontCache.barFontSize=instance.font3;
            FontCache.balloonFontSize=instance.font4;
            FontCache.resetCache();
	}
	return instance;
    }
    
    /** Creates a new instance of Config */
    private Config() {
        getPhoneManufacturer();
        
	int gmtloc=TimeZone.getDefault().getRawOffset()/3600000;
	locOffset=0;
	gmtOffset=gmtloc;
	
	short greenKeyCode=-1000;
        
        switch (phoneManufacturer) {
            case SONYE:
                //prefetch images
                ActionsIcons.getInstance();
                RosterIcons.getInstance();
//#ifdef NEW_MENU
                if (newMenu) MenuIcons.getInstance();
//#endif
//#ifdef SMILES
                if (smiles) SmilesIcons.getInstance();
//#endif
                System.gc();
                try { Thread.sleep(50); } catch (InterruptedException e){}
                
                allowMinimize=true;
                greenKeyCode=-10;
                break;
            case SONYE_M600:
                KEY_BACK=-11;
                break;
            case WTK:
                greenKeyCode=-10;
                break;
            case NOKIA:
                KEY_BACK=VirtualList.NOKIA_PEN;
                greenKeyCode=-10;
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
            case XENIUM99:
                istreamWaiting=false; //is it critical for phillips xenium?
                break;
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
	    locOffset=inputStream.readInt();
	    autoLogin=inputStream.readBoolean();
	    autoJoinConferences=inputStream.readBoolean();
	    popupFromMinimized=inputStream.readBoolean();
	    notifyBlink=inputStream.readBoolean();
	    memMonitor=inputStream.readBoolean();
            font1=inputStream.readInt();
            font2=inputStream.readInt();
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
            isbottom=inputStream.readInt();
            confMessageCount=inputStream.readInt();
//#ifdef NEW_MENU
            newMenu=inputStream.readBoolean();
//#else
//#             inputStream.readBoolean();
//#endif
            lightState=inputStream.readBoolean();
            notifySound=inputStream.readBoolean();
//#ifdef HISTORY
//#             lastMessages=inputStream.readBoolean();
//#else
            inputStream.readBoolean();
//#endif
//#ifdef AUTOSTATUS
//#             setAutoStatusMessage=inputStream.readBoolean();
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
//#ifdef ANTISPAM
//#             antispam=inputStream.readBoolean();
//#else
            inputStream.readBoolean();
//#endif
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
//#ifdef AUTODELETE
//#             msglistLimit=inputStream.readInt();
//#else
            inputStream.readInt();
//#endif
            useTabs=inputStream.readBoolean();
            autoSubscribe=inputStream.readInt();
            useBoldFont=inputStream.readBoolean();
            notifyWhenMessageType = inputStream.readBoolean();
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
            font3=inputStream.readInt();
            font4=inputStream.readInt();
            
            verHash=inputStream.readUTF();
            resolvedHost=inputStream.readUTF();
            resolvedPort=inputStream.readInt();
            
//#ifdef DETRANSLIT
//#             autoDeTranslit=inputStream.readBoolean();
//#else
            inputStream.readBoolean();
//#endif
//#ifdef CLIENTS_ICONS
//#             showClientIcon=inputStream.readBoolean();
//#else
            inputStream.readBoolean();
//#endif
            
            reconnectCount=inputStream.readInt();
            reconnectTime=inputStream.readInt();
            
	    inputStream.close();
	} catch (Exception e) {
            try {
                if (inputStream!=null)
                    inputStream.close();
            } catch (IOException ex) { }
	}
	
	lastProfile=profile=def_profile;
        if (lastProfile==AlertProfile.VIBRA) lastProfile=0;
	updateTime();
	VirtualList.fullscreen=fullscreen;
	VirtualList.memMonitor=memMonitor;
        VirtualList.showBalloons=showBalloons;
//#ifdef USER_KEYS
//#         VirtualList.userKeys=userKeys;
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
	    outputStream.writeInt(locOffset);
	    outputStream.writeBoolean(autoLogin);
	    outputStream.writeBoolean(autoJoinConferences);
            outputStream.writeBoolean(popupFromMinimized);
	    outputStream.writeBoolean(notifyBlink);
	    outputStream.writeBoolean(memMonitor);
            outputStream.writeInt(font1);
            outputStream.writeInt(font2);
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
            outputStream.writeInt(isbottom);
            outputStream.writeInt(confMessageCount);
//#ifdef NEW_MENU
            outputStream.writeBoolean(newMenu);
//#else
//#             outputStream.writeBoolean(false);
//#endif
            outputStream.writeBoolean(lightState);
            outputStream.writeBoolean(notifySound);
//#ifdef HISTORY
//#             outputStream.writeBoolean(lastMessages);
//#else
            outputStream.writeBoolean(false);
//#endif
//#ifdef AUTOSTATUS
//#             outputStream.writeBoolean(setAutoStatusMessage);
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
//#ifdef ANTISPAM
//#             outputStream.writeBoolean(antispam);
//#else
            outputStream.writeBoolean(false);
//#endif
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
//#ifdef AUTODELETE
//#             outputStream.writeInt(msglistLimit);
//#else
            outputStream.writeInt(512);
//#endif
            outputStream.writeBoolean(useTabs);
            outputStream.writeInt(autoSubscribe);
            outputStream.writeBoolean(useBoldFont);
            outputStream.writeBoolean(notifyWhenMessageType);
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
            outputStream.writeInt(font3);
            outputStream.writeInt(font4);
            
            outputStream.writeUTF(verHash);
            outputStream.writeUTF(resolvedHost);
            outputStream.writeInt(resolvedPort);
            
//#ifdef DETRANSLIT
//#             outputStream.writeBoolean(autoDeTranslit);
//#else
            outputStream.writeBoolean(false);
//#endif
//#ifdef CLIENTS_ICONS
//#             outputStream.writeBoolean(showClientIcon);
//#else
            outputStream.writeBoolean(false);
//#endif
            
            outputStream.writeInt(reconnectCount);
            outputStream.writeInt(reconnectTime);
	} catch (Exception e) { }
	
	NvStorage.writeFileRecord(outputStream, "config", 0, true);
    }

    
    public void updateTime(){
	Time.setOffset(gmtOffset, locOffset);
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
            } else if (platform.indexOf("9@9")>-1) {
                phoneManufacturer=XENIUM99;
                return;
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
