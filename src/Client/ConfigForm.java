/*
 * ConfigForm.java
 *
 * Created on 2.05.2005, 18:19
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
import Colors.ColorUtils;
import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.lcdui.*;
import locale.SR;
import ui.controls.NumberField;
import util.StringLoader;
import ui.*;
import com.alsutton.jabber.datablocks.Presence;
import xmpp.EntityCaps;

public class ConfigForm implements
	CommandListener, ItemCommandListener
{
    private Display display;
    private Displayable parentView;

    Form f;
    ChoiceGroup roster;
    ChoiceGroup message;
    ChoiceGroup subscr;
    
    ChoiceGroup nil;
    
    NumberField MessageLimit;
    NumberField MessageCountLimit;
    
    ChoiceGroup startup;
    ChoiceGroup application;

    ChoiceGroup lang;
    
    ChoiceGroup font1;
    ChoiceGroup font2;
    
    ChoiceGroup textWrap;
    
    //NumberField keepAlive;
    NumberField fieldLoc;
    NumberField fieldGmt;
//#ifdef AUTOSTATUS
//#     ChoiceGroup awayStatus;
//# 
//#     NumberField fieldAwatDelay;
//#     ChoiceGroup autoAwayType;
//#endif
    Command cmdOk=new Command(SR.MS_OK,Command.OK,1);

    ChoiceGroup SkinFile;
    private Vector[] Skinfiles;
    
//#ifdef COLORS
    Command cmdLoadSkin=new Command(SR.MS_LOAD_SKIN, Command.ITEM,15);
//#endif
    Command cmdCancel=new Command(SR.MS_CANCEL, Command.BACK,99);
    
    StaticData sd=StaticData.getInstance();
    
    Config cf=Config.getInstance();;
    boolean ra[];
    boolean mv[];
    boolean ap[];
    boolean su[];
//#ifdef AUTOSTATUS
//#     boolean aa[];
//#endif
    boolean lc[];
    boolean se[];
    Vector files[];
    Vector langs[];
    
    /** Creates a new instance of ConfigForm */
    public ConfigForm(Display display) {
        this.display=display;
        parentView=display.getCurrent();

        f=new Form(SR.MS_OPTIONS);
        roster=new ChoiceGroup(SR.MS_ROSTER_ELEMENTS, Choice.MULTIPLE);
        roster.append(SR.MS_OFFLINE_CONTACTS, null);
        roster.append(SR.MS_SELF_CONTACT, null);
        roster.append(SR.MS_TRANSPORTS, null);
        roster.append(SR.MS_IGNORE_LIST, null);
        roster.append(SR.MS_COLLAPSED_GROUPS, null);
        roster.append(SR.MS_AUTOFOCUS,null);
        roster.append(SR.MS_SHOW_RESOURCES,null);
        roster.append(SR.MS_BOLD_FONT,null);
//#ifdef SECONDSTRING
//#         roster.append(SR.MS_SHOW_STATUSES,null);
//#endif

        boolean rosterA[]={
            cf.showOfflineContacts,
            cf.selfContact,
            cf.showTransports, 
            cf.ignore, 
            cf.collapsedGroups,
            cf.autoFocus,
            cf.showResources,
            cf.useBoldFont
//#ifdef SECONDSTRING
//#             ,cf.rosterStatus
//#endif
        };
        this.ra=rosterA;
        roster.setSelectedFlags(rosterA);
    
        subscr=new ChoiceGroup(SR.MS_AUTH_NEW, Choice.POPUP);
        subscr.append(SR.MS_SUBSCR_AUTO, null);
        subscr.append(SR.MS_SUBSCR_ASK, null);
        subscr.append(SR.MS_SUBSCR_DROP, null);
        subscr.append(SR.MS_SUBSCR_REJECT, null);
        subscr.setSelectedIndex(cf.autoSubscribe, true);

        nil=new ChoiceGroup(SR.MS_NOT_IN_LIST, ChoiceGroup.POPUP);
        nil.append(SR.MS_NIL_DROP_MP, null);
        nil.append(SR.MS_NIL_DROP_P, null);
        nil.append(SR.MS_NIL_ALLOW_ALL, null);
        nil.setSelectedIndex((cf.notInListDropLevel>NotInListFilter.ALLOW_ALL)? NotInListFilter.ALLOW_ALL: cf.notInListDropLevel, true);

        message=new ChoiceGroup(SR.MS_MESSAGES, Choice.MULTIPLE);
//#ifdef SMILES
        message.append(SR.MS_SMILES, null);
//#endif
        message.append(SR.MS_COMPOSING_EVENTS, null);
        message.append(SR.MS_CAPS_STATE, null);
        message.append(SR.MS_STORE_PRESENCE,null);
        message.append(SR.MS_AUTOSCROLL, null);
        message.append(SR.MS_EMULATE_TABS, null);
//#ifdef PEP
//#         message.append(SR.MS_SEND_RECEIVE_USERMOODS, null);
//#ifdef PEP_TUNE
//#         message.append(SR.MS_RECEIVE_USERTUNE, null);
//#endif
//#endif
        message.append(SR.MS_RUNNING_MESSAGE, null);
//#ifdef ANTISPAM
//#         message.append(SR.MS_ANTISPAM_CONFERENCE, null);
//#endif
//#ifdef POPUPS
        message.append(SR.MS_POPUPS, null);
//#endif
        message.append(SR.MS_SHOW_BALLONS, null);
        message.append(SR.MS_DELIVERY, null);
//#ifdef CLIPBOARD
//#         message.append(SR.MS_CLIPBOARD, null);
//#endif
        boolean messageV[]={
//#ifdef SMILES
            cf.smiles,
//#endif
            cf.eventComposing,
            cf.capsState,
            cf.storeConfPresence,
            cf.autoScroll,
            cf.useTabs,
//#ifdef PEP
//#             cf.sndrcvmood,
//#ifdef PEP_TUNE
//#             cf.rcvtune,
//#endif
//#endif
            cf.notifyWhenMessageType
//#ifdef ANTISPAM
//#             ,cf.antispam
//#endif
//#ifdef POPUPS
            ,cf.popUps
//#endif
            ,cf.showBalloons       
            ,cf.eventDelivery
//#ifdef CLIPBOARD
//#             ,cf.useClipBoard
//#endif
        };
        this.mv=messageV;

        message.setSelectedFlags(messageV);
//#if AUTODELETE
//#         MessageCountLimit=new NumberField(SR.MS_MESSAGE_COUNT_LIMIT, cf.msglistLimit, 10, 1000);
//#endif
        MessageLimit=new NumberField(SR.MS_MESSAGE_COLLAPSE_LIMIT, cf.messageLimit, 200, 1000);

	startup=new ChoiceGroup(SR.MS_STARTUP_ACTIONS, Choice.MULTIPLE);
        startup.append(SR.MS_AUTOLOGIN, null);
        startup.append(SR.MS_AUTO_CONFERENCES,null);
        su=new boolean[2];
        su[0]=cf.autoLogin;
        su[1]=cf.autoJoinConferences;
        startup.setSelectedFlags(su);
        
        application=new ChoiceGroup(SR.MS_APPLICATION, Choice.MULTIPLE);
        application.append(SR.MS_FULLSCREEN,null);
        application.append(SR.MS_HEAP_MONITOR,null);
        application.append(SR.MS_SHOW_HARDWARE,null);
        application.append(SR.MS_CONFIRM_EXIT,null);
//#ifdef USER_KEYS
//#         application.append(SR.MS_CUSTOM_KEYS,null);
//#endif
//#ifdef NEW_MENU
        application.append(SR.MS_NEW_MENU,null);
//#endif
        application.append(SR.MS_FLASHLIGHT,null);
//#ifdef IRC_LIKE
//#         application.append(SR.MS_IRCLIKESTATUS,null);
//#endif
	if (cf.allowMinimize)
            application.append(SR.MS_ENABLE_POPUP,null);
       
        boolean appP[]={
            cf.fullscreen,
            cf.memMonitor,
            cf.enableVersionOs,
            cf.queryExit,
//#ifdef USER_KEYS
//#             cf.userKeys,
//#endif
//#ifdef NEW_MENU
            cf.newMenu,
//#endif
            cf.lightState,
//#ifdef IRC_LIKE
//#             cf.ircLikeStatus,
//#endif
            cf.popupFromMinimized
        };

        this.ap=appP;
        
        application.setSelectedFlags(appP);

	fieldGmt=new NumberField(SR.MS_GMT_OFFSET, cf.gmtOffset, -12, 12); 
        fieldLoc=new NumberField(SR.MS_CLOCK_OFFSET, cf.locOffset, -12, 12 );

        String fnts[]={SR.MS_FONTSIZE_NORMAL, SR.MS_FONTSIZE_SMALL, SR.MS_FONTSIZE_LARGE};
        font1=new ChoiceGroup(SR.MS_ROSTER_FONT, ChoiceGroup.POPUP, fnts, null);
        font2=new ChoiceGroup(SR.MS_MESSAGE_FONT, ChoiceGroup.POPUP, fnts, null);
        font1.setSelectedIndex(cf.font1/8, true);
        font2.setSelectedIndex(cf.font2/8, true);

        f.append(roster);
        
        try {
            String tempScheme=(cf.scheme=="")?"default":cf.scheme;
           
            SkinFile=new ChoiceGroup(SR.MS_LOAD_SKIN, ChoiceGroup.POPUP);
            Skinfiles=new StringLoader().stringLoader("/skins/res.txt",2);
            for (int i=0; i<Skinfiles[0].size(); i++) {
                String schemeName = (String)Skinfiles[1].elementAt(i);
                SkinFile.append(schemeName, null);
//#ifndef COLORS
//#                 if (tempScheme.equals(schemeName))
//#                     SkinFile.setSelectedIndex(i, true);
//#endif
            }
//#ifdef COLORS
            if (Skinfiles.length>0) {
                SkinFile.setItemCommandListener(this);
                SkinFile.addCommand(cmdLoadSkin);
            }
//#endif 
            f.append(SkinFile);
        } catch (Exception e) {}
        
        f.append(subscr);
        
        f.append(nil);
        
        f.append(font1);

        f.append(message);
        f.append(MessageLimit);
//#if AUTODELETE
//#         f.append(MessageCountLimit);
//#endif
        f.append(font2);
        
	String textWraps[]={SR.MS_TEXTWRAP_CHARACTER, SR.MS_TEXTWRAP_WORD};
	textWrap=new ChoiceGroup(SR.MS_TEXTWRAP, ChoiceGroup.POPUP, textWraps,null);
	textWrap.setSelectedIndex(cf.textWrap, true);
	f.append(textWrap);
        
//******************** lang
        lang=new ChoiceGroup(SR.MS_LANGUAGE, ChoiceGroup.POPUP);
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
	    lang.append( label, null );
            if (tempLang.equals(langCode))
                lang.setSelectedIndex(i, true);
        }
//******************** lang
        f.append(startup);

	f.append(application);

	//f.append(keepAlive);
	
        f.append(SR.MS_TIME_SETTINGS);
        f.append("\n");
        
        f.append(fieldGmt);
        f.append(fieldLoc);      
        f.append(lang);
//#ifdef AUTOSTATUS
//#         autoAwayType=new ChoiceGroup(SR.MS_AWAY_TYPE, Choice.POPUP);
//#         autoAwayType.append(SR.MS_AWAY_OFF, null);
//#         autoAwayType.append(SR.MS_AWAY_LOCK, null);
//#         autoAwayType.append(SR.MS_MESSAGE_LOCK, null);
//#         autoAwayType.append(SR.MS_IDLE, null);
//#         autoAwayType.setSelectedIndex(cf.autoAwayType, true);
//# 
//#         fieldAwatDelay=new NumberField(SR.MS_AWAY_PERIOD, cf.autoAwayDelay, 1, 60);
//# 
//#         awayStatus=new ChoiceGroup(SR.MS_SET, Choice.MULTIPLE);
//#         awayStatus.append(SR.MS_AUTOSTATUS_MESSAGE, null);
//# 
//#         boolean autoAway[]={
//#             cf.setAutoStatusMessage
//#         };
//# 
//#         this.aa=autoAway;
//#         awayStatus.setSelectedFlags(autoAway);
//#         f.append(awayStatus);
//# 
//#         f.append(autoAwayType);
//#         f.append(fieldAwatDelay);
//#endif

        f.addCommand(cmdOk);
        f.addCommand(cmdCancel);       
        f.setCommandListener(this);    
        display.setCurrent(f);
    }
    
    public void commandAction(Command c, Displayable d) {
        if (c==cmdOk) {
            roster.getSelectedFlags(ra);
            message.getSelectedFlags(mv);
            application.getSelectedFlags(ap);
	    startup.getSelectedFlags(su);
//#ifdef AUTOSTATUS
//#             awayStatus.getSelectedFlags(aa);
//#endif
            
            cf.notInListDropLevel=nil.getSelectedIndex();
            
            cf.showOfflineContacts=ra[0];
            cf.selfContact=ra[1];
            cf.showTransports=ra[2];
            cf.ignore=ra[3];
            cf.collapsedGroups=ra[4];
            cf.autoFocus=ra[5];
            cf.showResources=ra[6];
            cf.useBoldFont=ra[7];
//#ifdef SECONDSTRING
//#             cf.rosterStatus=ra[8];
//#endif
            
            cf.autoSubscribe=subscr.getSelectedIndex();

            int mvctr=0;
//#ifdef SMILES
            cf.smiles=mv[mvctr++];
//#endif
            cf.eventComposing=mv[mvctr++];
            cf.capsState=mv[mvctr++];
            cf.storeConfPresence=mv[mvctr++];
            cf.autoScroll=mv[mvctr++];
            cf.useTabs=mv[mvctr++];
//#ifdef PEP
//#             cf.sndrcvmood=mv[mvctr++];
//#ifdef PEP_TUNE
//#             cf.rcvtune=mv[mvctr++];
//#endif
//#endif
            cf.notifyWhenMessageType=mv[mvctr++];
//#ifdef ANTISPAM
//#             cf.antispam=mv[mvctr++];
//#endif
//#ifdef POPUPS
            cf.popUps=mv[mvctr++];
//#endif
            cf.showBalloons=mv[mvctr++];
            VirtualList.showBalloons=cf.showBalloons;
            cf.eventDelivery=mv[mvctr++];
//#ifdef CLIPBOARD
//#             cf.useClipBoard=mv[mvctr++];
//#endif
            
	    cf.autoLogin=su[0];
	    cf.autoJoinConferences=su[1];
            
	    int apctr=0;
            VirtualList.fullscreen=cf.fullscreen=ap[apctr++];
	    VirtualList.memMonitor=cf.memMonitor=ap[apctr++];
            cf.enableVersionOs=ap[apctr++];
            cf.queryExit=ap[apctr++];
//#ifdef USER_KEYS
//#             VirtualList.userKeys=cf.userKeys=ap[apctr++];
//#endif
//#ifdef NEW_MENU
            cf.newMenu=ap[apctr++];
//#endif
            cf.lightState=ap[apctr++];
//#ifdef IRC_LIKE
//#             cf.ircLikeStatus=ap[apctr++];
//#endif
            //cf.blFlash=ap[apctr++];
            if (cf.allowMinimize)
                cf.popupFromMinimized=ap[apctr++];
            
	    cf.gmtOffset=fieldGmt.getValue();
	    cf.locOffset=fieldLoc.getValue();
            
            FontCache.rosterFontSize=cf.font1=font1.getSelectedIndex()*8;
            FontCache.msgFontSize=cf.font2=font2.getSelectedIndex()*8;
            FontCache.resetCache();
	    
	    cf.textWrap=textWrap.getSelectedIndex();

            cf.lang=(String) langs[0].elementAt( lang.getSelectedIndex() );
//#ifdef AUTOSTATUS
//#             cf.setAutoStatusMessage=aa[0];
//#             cf.autoAwayDelay=fieldAwatDelay.getValue();
//#             cf.autoAwayType=autoAwayType.getSelectedIndex();
//#endif
            cf.messageLimit=MessageLimit.getValue();
//#if AUTODELETE
//#             cf.msglistLimit=MessageCountLimit.getValue();
//#endif

//#ifndef COLORS
//#             if (SkinFile.getSelectedIndex()>-1) {
//#                 String tempScheme=(String) Skinfiles[1].elementAt( SkinFile.getSelectedIndex() );
//#                 if (!tempScheme.equals(cf.scheme)) {
//#                     cf.scheme=(String) Skinfiles[1].elementAt( SkinFile.getSelectedIndex() );
//#                     ColorUtils.loadSkin((String)Skinfiles[0].elementAt(SkinFile.getSelectedIndex()), 1);
//#                 }
//#             }
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
        }
        destroyView();
    }

    public void commandAction(Command command, Item item) {
//#ifdef COLORS
	if (command==cmdLoadSkin) {
            int skinfl=SkinFile.getSelectedIndex();
            String skinFile=(String)Skinfiles[0].elementAt(skinfl);
            //ColorUtils.saveSkin(skinFile);
            ColorUtils.loadSkin(skinFile, 1);
	}
//#endif
    }
    
    public void destroyView(){
        if (display!=null)  
            display.setCurrent(sd.roster);
        ((Canvas)parentView).setFullScreenMode(cf.fullscreen);
    }
}
