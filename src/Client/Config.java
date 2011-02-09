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
    public final int vibraLen = 500;

    public final static int SUBSCR_AUTO = 0;
    public final static int SUBSCR_ASK = 1;
    public final static int SUBSCR_DROP = 2;
    public final static int SUBSCR_REJECT = 3;

    public final static int NOT_DETECTED = 0;
    public final static int NONE = -1;
    public final static int SONYE = 1;
    public final static int NOKIA = 2;
    public final static int SIEMENS = 3;
    public final static int SIEMENS2 = 4;
    public final static int MOTO = 5;
    public final static int MOTOEZX = 6;
    public final static int WINDOWS = 7;
    public final static int INTENT = 8;
    public final static int J2ME = 9;
    public final static int NOKIA_9XXX = 10;
    public final static int SONYE_M600 = 11;
//#if !ZLIB
//#     public final static int XENIUM99=12;
//#endif
    public final static int SAMSUNG = 14;
    public final static int LG = 15;
    public final static int JBED = 16;
    public final static int MICROEMU = 17;
    public final static int WTK = 50;
    public final static int OTHER = 99;

    StaticData sd = StaticData.getInstance();
    private static String platformName;
    public boolean ghostMotor = false;
    //public boolean blFlash=!ghostMotor; //true;
    public boolean muc119 = true;	// before muc 1.19 use muc#owner instead of muc#admin
//#ifdef AUTOSTATUS
//#     public final static int AWAY_OFF = 0;
//#     public final static int AWAY_LOCK = 1;
//#     public final static int AWAY_MESSAGE = 2;
//#     public final static int AWAY_IDLE = 3;
//#     public int autoAwayType = 0;
//#     public int autoAwayDelay = 5; //5 minutes
//#     public boolean useMyStatusMessages = true;
//#endif
//#ifdef HISTORY
//#     public String msgPath = "";
//#     public boolean msgLog = false;
//#     public boolean msgLogPresence = false;
//#     public boolean msgLogConf = false;
//#     public boolean msgLogConfPresence = false;
//#     public boolean lastMessages = false;
//#endif
    public boolean cp1251 = true;
//#ifndef WMUC
    public String defGcRoom = "bombusmod@conference.jabber.ru";
    public boolean storeConfPresence = true;
    public boolean autoJoinConferences = true;
    public int confMessageCount = 20;
//#endif
    // non-volatile values
    public int accountIndex = -1;
    public static boolean fullscreen = true;
    public int def_profile = 0;
//#ifdef SMILES
    public boolean smiles = true;
//#endif
    public boolean showOfflineContacts = false;
    public boolean showTransports = true;
    public boolean selfContact = false;
    public boolean ignore = false;
    public boolean eventComposing = true;
    public boolean autoLogin = true;
    public boolean autoFocus = false;
    public int loginstatus = 0;//loginstatus
    public int gmtOffset;
    public boolean popupFromMinimized = true;
    public boolean memMonitor = false;
    public int rosterFont = 8;
    public int msgFont = 8;
    public int barFont = 8;
    public int baloonFont = 8;
    public String lang;  //not detected (en)
    public boolean capsState = false;
    public int textWrap = 0;
    public int autoSubscribe = SUBSCR_ASK;
    // runtime values
    public boolean allowMinimize = false;
    public int profile = 0;
    public int lastProfile = 0;
    public boolean istreamWaiting;
    public int phoneManufacturer = NOT_DETECTED;
    public int panelsState = 2;
    public boolean lightState = false;
    public boolean autoScroll = true;
//#ifdef POPUPS
    public boolean popUps = true;
//#endif
    public boolean showResources = true;
    public boolean enableVersionOs = true;
    public boolean collapsedGroups = true;
    public int messageLimit = 512;
    public int widthScroll2 = 10;
    public int minItemHeight = 0;
    public boolean widthSystemgc = false;
    public boolean advTouch = true;
    public boolean autoClean = false;
    public boolean eventDelivery = true;
//#ifdef DETRANSLIT
//#     public boolean transliterateFilenames=false;
//#     public boolean autoDeTranslit=false;
//#endif
    public boolean rosterStatus = true;
//#ifdef PEP
//#     public boolean sndrcvmood = false;
//#ifdef PEP_TUNE
//#     public boolean rcvtune = false;
//#endif
//#ifdef PEP_ACTIVITY
//#     public boolean rcvactivity = false;
//#endif
//#ifdef PEP_LOCATION
//#     public boolean rcvloc = false;
//#endif
//#endif
    public boolean queryExit = false;
    public int notInListDropLevel = NotInListFilter.ALLOW_ALL; //enable all
    public boolean showBalloons = false;
//#ifdef LOGROTATE
//#     public int msglistLimit=500;
//#endif
    public boolean useTabs = true;
    public boolean notifyBlink = false;
    public boolean notifySound = false;
    public boolean notifyPicture = false;
    public boolean useBoldFont = false;
    public boolean shadowed = true;
//#ifdef RUNNING_MESSAGE
//#     public boolean notifyWhenMessageType = false;
//#endif
//#ifdef CLIPBOARD
//#     public boolean useClipBoard = true;
//#endif
    public boolean firstRun = true;
    public String verHash = "";
    public String resolvedHost = "";
    public int resolvedPort = 0;
    public boolean IQNotify = false;
//#ifdef CLIENTS_ICONS
    public boolean showClientIcon = true;
//#endif
    public int reconnectCount = 10;
    public int reconnectTime = 15;
    public boolean executeByNum;
    public boolean showNickNames = true;
    public boolean fileTransfer = true;
    public boolean adhoc = false;
    public boolean saveHistory = false;
    public boolean oldSE = false;
    public boolean oldNokiaS60 = false;
    public boolean NokiaS40 = false;
    public boolean showTimeTraffic = false;
    public boolean swapSendAndSuspend = false;
    public boolean useQuickPrivacy = false;

    public static Config getInstance() {
        if (instance == null) {
            instance = new Config();
        }
        return instance;
    }

    /** Creates a new instance of Config */
    private Config() {
        getPhoneManufacturer();
        VirtualList.phoneManufacturer = phoneManufacturer;

        int gmtloc = TimeZone.getDefault().getRawOffset() / 3600000;
        gmtOffset = gmtloc;

        //prefetch images
        RosterIcons.getInstance();
        
//#ifdef SMILES
        if (smiles) {
            SmilesIcons.getInstance();
        }
//#endif
//#ifdef CLIENTS_ICONS
//#ifdef PLUGINS
//#         if (sd.ClientsIcons) 
//#endif            
            if (showClientIcon) {
                ClientsIcons.getInstance();
            }
//#endif

        System.gc();
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
        }

        switch (phoneManufacturer) {
            case SONYE:
                allowMinimize = true;                
                break;
            case NOKIA:
                allowMinimize = !NokiaS40;
                break;
            case MOTO:
                ghostMotor = true;
                istreamWaiting = true;
                break;            
//#if !ZLIB
//#             case XENIUM99:
//#                 istreamWaiting=false; //is it critical for phillips xenium?
//#                 break;
//#endif
        }
        loadFromStorage();

        FontCache.roster = rosterFont;
        FontCache.msg = msgFont;

        FontCache.bar = barFont;
        FontCache.baloon = baloonFont;
    }

    protected final void loadFromStorage() {
//#ifdef DEBUG
//#         System.out.println("LoadFromStorage config");
//#endif
        DataInputStream inputStream = NvStorage.ReadFileRecord("config", 0);
        try {
            accountIndex = inputStream.readInt();
            showOfflineContacts = inputStream.readBoolean();
            fullscreen = inputStream.readBoolean();
            def_profile = inputStream.readInt() % 4;
//#ifdef SMILES
            smiles = inputStream.readBoolean();
//#else
//#             inputStream.readBoolean();
//#endif
            showTransports = inputStream.readBoolean();
            selfContact = inputStream.readBoolean();
            collapsedGroups = inputStream.readBoolean();
            ignore = inputStream.readBoolean();
            eventComposing = inputStream.readBoolean();
            gmtOffset = inputStream.readInt();
            inputStream.readInt(); //locOffset
            autoLogin = inputStream.readBoolean();
//#ifndef WMUC            
            autoJoinConferences = inputStream.readBoolean();
//#else
//#             inputStream.readBoolean();
//#endif            
            popupFromMinimized = inputStream.readBoolean();
            notifyBlink = inputStream.readBoolean();
            memMonitor = inputStream.readBoolean();
            rosterFont = inputStream.readInt();
            msgFont = inputStream.readInt();
            autoFocus = inputStream.readBoolean();
            notInListDropLevel = inputStream.readInt();
//#ifndef WMUC                  
            storeConfPresence = inputStream.readBoolean();
//#else
//#             inputStream.readBoolean();
//#endif                        
            capsState = inputStream.readBoolean();
            textWrap = inputStream.readInt();
            loginstatus = inputStream.readInt();
//#ifdef HISTORY
//#             msgPath = inputStream.readUTF();
//#             msgLog = inputStream.readBoolean();
//#             msgLogPresence = inputStream.readBoolean();
//#             msgLogConfPresence = inputStream.readBoolean();
//#             msgLogConf = inputStream.readBoolean();
//#else
            inputStream.readUTF();
            inputStream.readBoolean();
            inputStream.readBoolean();
            inputStream.readBoolean();
            inputStream.readBoolean();
//#endif
            cp1251 = inputStream.readBoolean();
//#ifdef AUTOSTATUS
//#             autoAwayDelay = inputStream.readInt();
//#else
            inputStream.readInt();
//#endif
//#ifndef WMUC                  
            defGcRoom = inputStream.readUTF();
//#else
//#             inputStream.readUTF();
//#endif                                    
            firstRun = inputStream.readBoolean();
            panelsState = inputStream.readInt();
//#ifndef WMUC                             
            confMessageCount = inputStream.readInt();
//#else
//#             inputStream.readInt();
//#endif                                    
            fileTransfer = inputStream.readBoolean(); //newMenu

            lightState = inputStream.readBoolean();
            notifySound = inputStream.readBoolean();
//#ifdef LAST_MESSAGES
//#             lastMessages=inputStream.readBoolean();
//#else
            inputStream.readBoolean();
//#endif
//#ifdef AUTOSTATUS
//#             useMyStatusMessages = inputStream.readBoolean();
//#             autoAwayType = inputStream.readInt();
//#else
            inputStream.readBoolean();
            inputStream.readInt();
//#endif
            autoScroll = inputStream.readBoolean();
//#ifdef POPUPS
            popUps = inputStream.readBoolean();
//#else
//#             inputStream.readBoolean();
//#endif
            showResources = inputStream.readBoolean();

            saveHistory = inputStream.readBoolean(); //antispam

            enableVersionOs = inputStream.readBoolean();
            messageLimit = inputStream.readInt();
            lang = inputStream.readUTF();
            eventDelivery = inputStream.readBoolean();
//#ifdef DETRANSLIT
//#             transliterateFilenames=inputStream.readBoolean();
//#else
            inputStream.readBoolean();
//#endif

            rosterStatus = inputStream.readBoolean();

            queryExit = inputStream.readBoolean();
            notifyPicture = inputStream.readBoolean();
            showBalloons = inputStream.readBoolean();

            inputStream.readBoolean(); // Здесь был UserKeys
//#ifdef LOGROTATE
//#             msglistLimit=inputStream.readInt();
//#else
            inputStream.readInt();
//#endif
            useTabs = inputStream.readBoolean();
            autoSubscribe = inputStream.readInt();
            useBoldFont = inputStream.readBoolean();
//#ifdef RUNNING_MESSAGE
//#             notifyWhenMessageType = inputStream.readBoolean();
//#else
            inputStream.readBoolean();
//#endif
            IQNotify = inputStream.readBoolean(); //IRC_LIKE
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
            barFont = inputStream.readInt();
            baloonFont = inputStream.readInt();

            verHash = inputStream.readUTF();
            resolvedHost = inputStream.readUTF();
            resolvedPort = inputStream.readInt();

//#ifdef DETRANSLIT
//#             autoDeTranslit=inputStream.readBoolean();
//#else
            inputStream.readBoolean();
//#endif
//#ifdef CLIENTS_ICONS
            showClientIcon = inputStream.readBoolean();
//#else
//#             inputStream.readBoolean();
//#endif

            reconnectCount = inputStream.readInt();
            reconnectTime = inputStream.readInt();

            executeByNum = inputStream.readBoolean();

            showNickNames = inputStream.readBoolean();

            adhoc = inputStream.readBoolean();
//#ifdef PEP_ACTIVITY
//#             rcvactivity = inputStream.readBoolean();
//#else
            inputStream.readBoolean();
//#endif
            shadowed = inputStream.readBoolean(); 

            showTimeTraffic = inputStream.readBoolean();

            swapSendAndSuspend = inputStream.readBoolean();
            widthScroll2 = inputStream.readInt();
            widthSystemgc = inputStream.readBoolean();

            inputStream.readUTF(); // ранее здесь был juickJID

            advTouch = inputStream.readBoolean();
            autoClean = inputStream.readBoolean();
//#ifdef PEP_LOCATION
//#             rcvloc = inputStream.readBoolean();
//#else
            inputStream.readBoolean();
//#endif
//#ifdef PRIVACY
            useQuickPrivacy = inputStream.readBoolean();            
//#else
//#         inputStream.readBoolean();
//#endif
            minItemHeight = inputStream.readInt();

            inputStream.close();
            inputStream = null;
        } catch (IOException e) { // Левые Exception'ы должны обрабатываться не здесь (поэтому ловим только IOException).
            try {
                if (inputStream != null) {
                    inputStream.close();
                    inputStream = null;
                }
            } catch (IOException ex) {
            }
        } catch (NullPointerException e) { /* Сюда мы попадём, если Storage нет */ }

        lastProfile = profile = def_profile;
        if (lastProfile == AlertProfile.VIBRA) {
            lastProfile = 0;
        }
        updateTime();        
        VirtualList.memMonitor = memMonitor;
        VirtualList.panelsState = panelsState;
        VirtualList.showTimeTraffic = showTimeTraffic;

//#ifdef PLUGINS
//#ifdef FILE_TRANSFER
//#         if (!sd.FileTransfer) {
//#             fileTransfer = false;
//#         }
//#endif
//#ifdef PEP
//#         if (!sd.PEP) {
//#             sndrcvmood = false;
//#         }
//#endif
//#ifdef PEP_TUNE
//#         if (!sd.PEP) {
//#             rcvtune = false;
//#         }
//#endif
//#ifdef PEP_ACTIVITY
//#         if (!sd.PEP) {
//#             rcvactivity = false;
//#         }
//#endif
//#ifdef ADHOC
//#         if (!sd.Adhoc) {
//#             adhoc = false;
//#         }
//#endif
//#ifdef CLIENTS_ICONS
//#         if (!sd.ClientsIcons) {
//#             showClientIcon = false;
//#         }
//#endif
//#ifdef HISTORY
//#         if (!sd.History) {
//#             saveHistory = false;
//#         }
//#endif
//#endif
    }

    public String langFileName() {
        if (lang == null) {
            //auto-detecting
            lang = System.getProperty("microedition.locale");
//#ifdef DEBUG
//#             System.out.println(lang);
//#endif
            //We will use only language code from locale
            if (lang == null) {
                lang = "en";
            } else {
                lang = lang.substring(0, 2).toLowerCase();
            }
        }

        if (lang.equals("en")) {
            return null;  //english
        }
        Vector files[] = new StringLoader().stringLoader("/lang/res.txt", 3);
        for (int i = 0; i < files[0].size(); i++) {
            String langCode = (String) files[0].elementAt(i);
            if (lang.equals(langCode)) {
                return (String) files[1].elementAt(i);
            }
        }
        return null; //unknown language ->en
    }

    public void saveToStorage() {
        DataOutputStream outputStream = NvStorage.CreateDataOutputStream();

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
//#ifndef WMUC                             
            outputStream.writeBoolean(autoJoinConferences);
//#else 
//#             outputStream.writeBoolean(false);
//#endif            
            outputStream.writeBoolean(popupFromMinimized);
            outputStream.writeBoolean(notifyBlink);
            outputStream.writeBoolean(memMonitor);
            outputStream.writeInt(rosterFont);
            outputStream.writeInt(msgFont);
            outputStream.writeBoolean(autoFocus);
            outputStream.writeInt(notInListDropLevel);
//#ifndef WMUC            
            outputStream.writeBoolean(storeConfPresence);
//#else 
//#             outputStream.writeBoolean(false);
//#endif                        
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
//#ifndef WMUC            
            outputStream.writeUTF(defGcRoom);
//#else 
//#             outputStream.writeUTF("");
//#endif                        
            outputStream.writeBoolean(firstRun);
            outputStream.writeInt(panelsState);
//#ifndef WMUC            
            outputStream.writeInt(confMessageCount);
//#else 
//#             outputStream.writeInt(0);
//#endif            
            outputStream.writeBoolean(fileTransfer); //newMenu

            outputStream.writeBoolean(lightState);
            outputStream.writeBoolean(notifySound);
//#ifdef LAST_MESSAGES
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

            outputStream.writeBoolean(false); // Здесь был UserKeys

//#ifdef LOGROTATE
//#             outputStream.writeInt(msglistLimit);
//#else
            outputStream.writeInt(500);
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
            outputStream.writeBoolean(shadowed); 

            outputStream.writeBoolean(showTimeTraffic);

            outputStream.writeBoolean(swapSendAndSuspend);
            outputStream.writeInt(widthScroll2);
            outputStream.writeBoolean(widthSystemgc);

            outputStream.writeUTF(""); // ранее здесь был juickJID

            outputStream.writeBoolean(advTouch);
            outputStream.writeBoolean(autoClean);
//#ifdef PEP_LOCATION
//#             outputStream.writeBoolean(rcvloc);
//#else
            outputStream.writeBoolean(false);
//#endif
//#ifdef PRIVACY
            outputStream.writeBoolean(useQuickPrivacy);            
//#else
//#         outputStream.writeBoolean(false);
//#endif             
        outputStream.writeInt(minItemHeight);

        } catch (Exception e) {
        }

        NvStorage.writeFileRecord(outputStream, "config", 0, true);
    }

    public void updateTime() {
        Time.setOffset(gmtOffset);
    }

    private void getPhoneManufacturer() {
        if (phoneManufacturer == NOT_DETECTED) {
            String platform = getPlatformName();
            phoneManufacturer = NONE;

            if (platform.endsWith("(NSG)")) {
                phoneManufacturer = SIEMENS;
                return;
            } else if (platform.startsWith("SIE")) {
                phoneManufacturer = SIEMENS2;
                return;
            } else if (platform.startsWith("Motorola-EZX")) {
                phoneManufacturer = MOTOEZX;
                return;
            } else if (platform.startsWith("Moto")) {
                phoneManufacturer = MOTO;
                return;
            } else if (platform.startsWith("SonyE")) {
                if (platform.startsWith("SonyEricssonM600")) {
                    phoneManufacturer = SONYE_M600;
                    return;
                }
                phoneManufacturer = SONYE;

                String sonyJava = System.getProperty("com.sonyericsson.java.platform");
                oldSE = (sonyJava == null) || sonyJava.startsWith("JP-7"); //JP<=7.x

                return;
//#if !ZLIB
//#             } else if (platform.indexOf("9@9")>-1) {
//#                 phoneManufacturer=XENIUM99;
//#                 return;
//#endif
            } else if (platform.startsWith("Windows")) {
                phoneManufacturer = WINDOWS;
                return;
            } else if (platform.startsWith("Nokia9500")
                    || platform.startsWith("Nokia9300")
                    || platform.startsWith("Nokia9300i")) {
                phoneManufacturer = NOKIA_9XXX;
                return;
            } else if (platform.startsWith("Nokia")) {
                phoneManufacturer = NOKIA;
                int firstDotIndex = platform.indexOf('.');
                if (firstDotIndex != -1) {
                    try {
                        String dir = System.getProperty("fileconn.dir.private");
                        // s40 (6233) does not have this property
                        if (-1 != dir.indexOf("/private/")) {
                            // it is s60 v3 fp1
                            return;
                        }
                    } catch (Exception e) {
                    }
                    if (-1 == platform.indexOf('.', firstDotIndex + 1)) {
                        NokiaS40 = true;
                        return;
                    }
                }
                if (platform.indexOf("java_build_version") == 0) {
                    oldNokiaS60 = true; // buggy S60 3.1 or older
                }
                return;
            } else if (platform.startsWith("Intent")) {
                phoneManufacturer = INTENT;
                return;
            } else if (platform.startsWith("wtk") || platform.endsWith("wtk")) {
                phoneManufacturer = WTK;
                return;
            } else if (platform.startsWith("Samsung")) {
                phoneManufacturer = SAMSUNG;
                return;
            } else if (platform.startsWith("LG")) {
                phoneManufacturer = LG;
                return;
            } else if (platform.startsWith("j2me")) {
                phoneManufacturer = J2ME;
                return;
            } else if (platform.startsWith("Jbed")) {
                phoneManufacturer = JBED;
//#ifdef FILE_IO
                try {
                    FileIO f = FileIO.createConnection("");
                } catch (Exception ex) {
                }
//#endif
                return;
            } else if (platform.indexOf("Android") > 0) {
                phoneManufacturer = MICROEMU;
            } else {
                phoneManufacturer = OTHER;
            }
        }
    }

    public static String getPlatformName() {
        if (platformName == null) {
            platformName = System.getProperty("microedition.platform");

            String sonyJava = System.getProperty("com.sonyericsson.java.platform");
            if (sonyJava != null) {
                platformName = platformName + "/" + sonyJava;
            }

            String device = System.getProperty("device.model");
            String firmware = System.getProperty("device.software.version");

            if (platformName.startsWith("microemu")) {
                platformName = device + "/Android " + firmware;
            }

            //detecting Samsung
            try {
                Class.forName("com.samsung.util.AudioClip");
                platformName = "Samsung-generic";
            } catch (Throwable t0) {
                try {
                    Class.forName("com.samsung.util.Vibration");
                    platformName = "Samsung-generic";
                } catch (Throwable t1) {
                }
            }
            // detecting Sun Java Wireless Client (JavaFX for WM)
            try {
                Class.forName("com.sun.midp.chameleon.MIDPWindow");
                platformName = "JavaFX";
            } catch (Throwable t2) {
                
            }



            if (platformName == null) {
                platformName = "Motorola";
            }

            if (platformName.startsWith("j2me")) {
                if (device != null) {
                    if (device.startsWith("wtk-emulator")) {
                        platformName = device;
                    }
                }
                if (device != null && firmware != null) {
                    platformName = "Motorola"; // buggy v360
                } else {
                    // Motorola EZX phones
                    String hostname = System.getProperty("microedition.hostname");
                    if (hostname != null) {
                        platformName = "Motorola-EZX";
                        if (device != null) {
                            // Motorola EZX ROKR
                            hostname = device;
                        }

                        if (hostname.indexOf("(none)") < 0) {
                            platformName += "/" + hostname;
                        }
                    }
                }
            }
            //else 
            if (platformName.startsWith("Moto")) {
                if (device == null) {
                    device = System.getProperty("funlights.product");
                }
                if (device != null) {
                    platformName = "Motorola-" + device;
                }
                try { // thanks vitalyster
                    Class.forName("com.nokia.mid.ui.DeviceControl");
                    platformName = "Nokia"; // FS #896
                } catch (Throwable ex) {
                }
            }

            if (platformName.indexOf("SIE") > -1) {
                platformName = System.getProperty("microedition.platform") + " (NSG)";
            } else if (System.getProperty("com.siemens.OSVersion") != null) {
                platformName = "SIE-" + System.getProperty("microedition.platform") + "/" + System.getProperty("com.siemens.OSVersion");
            }

            try {
                Class.forName("com.samsung.util.Vibration");
                platformName = "Samsung";
            } catch (Throwable ex) {
            }

            try {
                Class.forName("mmpp.media.MediaPlayer");
                platformName = "LG";
            } catch (Throwable ex) {
                try {
                    Class.forName("mmpp.phone.Phone");
                    platformName = "LG";
                } catch (Throwable ex1) {
                    try {
                        Class.forName("mmpp.lang.MathFP");
                        platformName = "LG";
                    } catch (Throwable ex2) {
                        try {
                            Class.forName("mmpp.media.BackLight");
                            platformName = "LG";
                        } catch (Throwable ex3) {
                        }
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
        if (key != null) {
            String s = BombusMod.getInstance().getAppProperty(key);
            return (s == null) ? defvalue : s;
        } 
        return defvalue;
    }

    public final int getIntProperty(final String key, final int defvalue) {
        try {
            return Integer.parseInt(key);
        } catch (Exception e) {
        }
        return defvalue;
    }

    /*public final char getCharProperty(final String key, final char defvalue) {
    try { return key.charAt(0); } catch (Exception e) {	}
    return defvalue;
    }*/
    public final boolean getBooleanProperty(final String key, final boolean defvalue) {
        try {
            if (key.equals("true")) {
                return true;
            }
            if (key.equals("yes")) {
                return true;
            }
            if (key.equals("1")) {
                return true;
            }
            return false;
        } catch (Exception e) {
        }
        return defvalue;
    }
}
