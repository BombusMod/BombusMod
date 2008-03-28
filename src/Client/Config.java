/*
 * Config.java
 *
 * Created on 19.03.2005, 18:37
 * Copyright (c) 2005-2007, Eugene Stahov (evgs), http://bombus-im.org
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
import images.RosterIcons;
import images.SmilesIcons;
import java.io.*;
import java.util.*;
import midlet.BombusMod;
import ui.FontCache;
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
    
    public final int vibraLen=500;

    public final static int AWAY_OFF=0;
    public final static int AWAY_LOCK=1;
    public final static int AWAY_MESSAGE=2;
    public final static int AWAY_IDLE=3;
    
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
    public final static int WTK=50;
    public final static int OTHER=99;
    
    private static String platformName;
    
    public boolean ghostMotor=false;
    //public boolean blFlash=!ghostMotor; //true;
    
    
    public boolean muc119=true;	// before muc 1.19 use muc#owner instead of muc#admin
    
    public char keyLock='*';
    public char keyVibra='#';
    

     public String msgPath="";
     public boolean msgLog=false;
     public boolean msgLogPresence=false;
     public boolean msgLogConf=false;
     public boolean msgLogConfPresence=false;
     public boolean cp1251=true;
    
    public String defGcRoom="bombusmod@conference.jabber.ru";
    
    // non-volatile values
    public int accountIndex=-1;
    public boolean fullscreen=false;
    public int def_profile=0;
    public boolean smiles=true;
    public boolean showOfflineContacts=false;
    public boolean showTransports=true;
    public boolean selfContact=false;
    //public boolean notInList=true;
    public boolean ignore=false;
    public boolean eventComposing=true;
    
    public boolean storeConfPresence=true;      
    
    public boolean autoLogin=true;
    public boolean autoJoinConferences=true;
    
    public boolean autoFocus=false;
    
    public int loginstatus=0;//loginstatus
    
    public int gmtOffset;
    public int locOffset;
    
    public boolean popupFromMinimized=true;
    public boolean memMonitor=true;
    public boolean newMenu=false;
    
    public int font1=0;
    public int font2=0;
    public int font3=0;

    public String lang;  //not detected (en)
    public boolean capsState=false;
    public int textWrap=0;
    public int autoSubscribe=SUBSCR_ASK;
	
    // runtime values
    public boolean allowMinimize=false;
    public boolean allowLightControl=false;
    
    public int profile=0;
    public int lastProfile=0;
    
    public boolean istreamWaiting;

    // Singleton
    private static Config instance;

    public int autoAwayType=0;
    public int autoAwayDelay=5; //5 minutes
    public boolean setAutoStatusMessage=false;
    
    public int confMessageCount=20;

    public boolean altInput=false;

    public int isbottom=2; //default state both panels show, reverse disabled
   
    public boolean lightState=false;
    
    public boolean lastMessages=false;

    public boolean autoScroll=true;

    public boolean popUps=true;

    public boolean showResources=true;
    
    public boolean antispam=false;
    
    public boolean enableVersionOs=true;
    
    public boolean collapsedGroups=true;
    
    public int messageLimit=512;
    
    public boolean eventDelivery=false;
    
    public boolean transliterateFilenames=false;
    
    public boolean rosterStatus=true;
    
    public boolean userMoods=true;

    public boolean queryExit = false;
    
    public int notInListDropLevel=NotInListFilter.ALLOW_ALL; //enable all
    
    public boolean showBalloons = true;
    
    public boolean userKeys = false;

    public int msglistLimit=100;
    
    public boolean useTabs=true;
    
    public int phoneManufacturer=NOT_DETECTED;

    public boolean notifyBlink=true;

    public boolean notifySound=false;

    public boolean notifyPicture=false;

    public boolean useBoldFont=false;
    
    public boolean notifyWhenMessageType = false;
    
    public boolean ircLikeStatus = false;
    
    public boolean sndrcvmood = true;
    
    public String scheme = "";
    
    public static Config getInstance(){
	if (instance==null) {
	    instance=new Config();
	    instance.loadFromStorage();

            FontCache.rosterFontSize=instance.font1;
            FontCache.msgFontSize=instance.font2;
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
                
	if (phoneManufacturer==SONYE) {
            //prefetch images
            RosterIcons.getInstance();
            SmilesIcons.getInstance();
            
			allowMinimize=true;
            greenKeyCode=VirtualList.SE_GREEN;
            if (phoneManufacturer==SONYE_M600) {
                KEY_BACK=-11;
            }
	} else if (phoneManufacturer==NOKIA) {
	    //blFlash=false;
	    greenKeyCode=VirtualList.NOKIA_GREEN;
	} else if (phoneManufacturer==MOTOEZX) {
	    //VirtualList.keyClear=0x1000;
	    VirtualList.keyVolDown=VirtualList.MOTOE680_VOL_DOWN;
	    KEY_BACK=VirtualList.MOTOE680_REALPLAYER;
	} else if (phoneManufacturer==MOTO) {
	    ghostMotor=true;
	    //blFlash=false;
            istreamWaiting=true;
	    greenKeyCode=VirtualList.MOTOROLA_GREEN;
	    //VirtualList.keyClear=0x1000;
	} else if (phoneManufacturer==SIEMENS || phoneManufacturer==SIEMENS2) {
            keyLock='#';
            keyVibra='*';
            allowLightControl=true;
            //blFlash=true;
            KEY_BACK=-4; //keyCode==702
            greenKeyCode=VirtualList.SIEMENS_GREEN;
        } else if (phoneManufacturer==WTK) {
	    greenKeyCode=VirtualList.NOKIA_GREEN;
	}
        
	VirtualList.greenKeyCode=greenKeyCode;
       
        if (phoneManufacturer==XENIUM99) {
            istreamWaiting=false; //is it critical for phillips xenium?
        }
    }
    
    protected void loadFromStorage(){
        DataInputStream inputStream=NvStorage.ReadFileRecord("config", 0);
	try {
	    accountIndex = inputStream.readInt();
	    showOfflineContacts=inputStream.readBoolean();
	    fullscreen=inputStream.readBoolean();
	    def_profile = inputStream.readInt();
	    smiles=inputStream.readBoolean();
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

            msgPath=inputStream.readUTF();
            msgLog=inputStream.readBoolean();
            msgLogPresence=inputStream.readBoolean();
            msgLogConfPresence=inputStream.readBoolean();
            msgLogConf=inputStream.readBoolean();
            cp1251=inputStream.readBoolean();
            
            autoAwayDelay=inputStream.readInt();
        
            defGcRoom=inputStream.readUTF();
            
            altInput=inputStream.readBoolean();
            
            isbottom=inputStream.readInt();
            
            confMessageCount=inputStream.readInt();
            
            newMenu=inputStream.readBoolean();
            
            lightState=inputStream.readBoolean();
			
            notifySound=inputStream.readBoolean();
            
            lastMessages=inputStream.readBoolean();

            setAutoStatusMessage=inputStream.readBoolean();
            
            autoAwayType=inputStream.readInt();
            
            autoScroll=inputStream.readBoolean();
            
            popUps=inputStream.readBoolean();
            
            showResources=inputStream.readBoolean();
            
            antispam=inputStream.readBoolean();
            
            enableVersionOs=inputStream.readBoolean();
            
            messageLimit=inputStream.readInt();
            
            lang=inputStream.readUTF();
            
            eventDelivery=inputStream.readBoolean();
            
            transliterateFilenames=inputStream.readBoolean();
            
            rosterStatus=inputStream.readBoolean();
            
            queryExit=inputStream.readBoolean();
            
            notifyPicture=inputStream.readBoolean();
            
            showBalloons=inputStream.readBoolean();
            
            userKeys=inputStream.readBoolean();
            
            msglistLimit=inputStream.readInt();
            
            useTabs=inputStream.readBoolean();
            
            autoSubscribe=inputStream.readInt();
            
            useBoldFont=inputStream.readBoolean();
            
            notifyWhenMessageType = inputStream.readBoolean();
            ircLikeStatus = inputStream.readBoolean();
            
            sndrcvmood = inputStream.readBoolean();
            
            scheme=inputStream.readUTF();
                    
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
	VirtualList.isbottom=isbottom;
	VirtualList.memMonitor=memMonitor;
        VirtualList.showBalloons=showBalloons;
        VirtualList.userKeys=userKeys;
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
    
    public String schemeFileName(){
        if (scheme=="")
            return null;
        if (scheme.equals("default")) 
            return null;  //default
	Vector files[]=new StringLoader().stringLoader("/skins/res.txt", 2);
        for (int i=0; i<files[0].size(); i++) {
            String schemeName=(String) files[1].elementAt(i);
            if (scheme.equals(schemeName))
        	return (String) files[0].elementAt(i);
        }
        return null;
    }
    
    public void saveToStorage(){
	
	DataOutputStream outputStream=NvStorage.CreateDataOutputStream();
	
	try {
	    outputStream.writeInt(accountIndex);
	    outputStream.writeBoolean(showOfflineContacts);
	    outputStream.writeBoolean(fullscreen);
	    outputStream.writeInt(def_profile);
	    outputStream.writeBoolean(smiles);
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
            
            outputStream.writeInt(notInListDropLevel /*keepAlive*/);
            
            outputStream.writeBoolean(storeConfPresence); 

            outputStream.writeBoolean(capsState); 
	    
	    outputStream.writeInt(textWrap);
            
            outputStream.writeInt(loginstatus);

            outputStream.writeUTF(msgPath);
            outputStream.writeBoolean(msgLog);
            outputStream.writeBoolean(msgLogPresence);
            outputStream.writeBoolean(msgLogConfPresence);
            outputStream.writeBoolean(msgLogConf);
            outputStream.writeBoolean(cp1251);

            
            outputStream.writeInt(autoAwayDelay);
            
            outputStream.writeUTF(defGcRoom);
            
            outputStream.writeBoolean(altInput);
            
            outputStream.writeInt(isbottom);
            
            outputStream.writeInt(confMessageCount);
            
            outputStream.writeBoolean(newMenu);
            
            outputStream.writeBoolean(lightState);
			
            outputStream.writeBoolean(notifySound);
            
            outputStream.writeBoolean(lastMessages);
            
            outputStream.writeBoolean(setAutoStatusMessage);
            
            outputStream.writeInt(autoAwayType);
            
            outputStream.writeBoolean(autoScroll);
            
            outputStream.writeBoolean(popUps);
            
            outputStream.writeBoolean(showResources);
            
            outputStream.writeBoolean(antispam);
            
            outputStream.writeBoolean(enableVersionOs);
            
            outputStream.writeInt(messageLimit);
            
            outputStream.writeUTF(lang);      
            
            outputStream.writeBoolean(eventDelivery);
            
            outputStream.writeBoolean(transliterateFilenames);
            
            outputStream.writeBoolean(rosterStatus);
            
            outputStream.writeBoolean(queryExit);
           
            outputStream.writeBoolean(notifyPicture);
            outputStream.writeBoolean(showBalloons);
            
            outputStream.writeBoolean(userKeys);
            
            outputStream.writeInt(msglistLimit);
            
            outputStream.writeBoolean(useTabs);
            
            outputStream.writeInt(autoSubscribe);
            
            outputStream.writeBoolean(useBoldFont);
            
            outputStream.writeBoolean(notifyWhenMessageType);
            outputStream.writeBoolean(ircLikeStatus);
            
            outputStream.writeBoolean(sndrcvmood);
            
            outputStream.writeUTF(scheme);
            
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
            } else if (platform.startsWith("j2me")) {
                phoneManufacturer=J2ME;
                return;
            } else {
                phoneManufacturer=OTHER;
            }
        }
    }
    
    public static String getPlatformName() {
        if (platformName==null) {
            platformName=System.getProperty("microedition.platform");
            
            String device=System.getProperty("device.model");
            String firmware=System.getProperty("device.software.version");
            
            if (platformName==null) platformName="Motorola";
            
             if (platformName.startsWith("j2me")) {
                if (device!=null) if (device.startsWith("wtk-emulator")) {
                     platformName=device;
                     return platformName;
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
						 return platformName;
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
    
    public final char getCharProperty(final String key, final char defvalue) {
	try { return key.charAt(0); } catch (Exception e) {	}
        return defvalue;
    }
    
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
