/*
 * AlertCustomizeForm.java
 *
 * Copyright (c) 2006-2007, Daniel Apatin (ad), http://apatin.net.ru
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

import locale.SR;
import ui.EventNotify;
import java.util.Vector;
import util.StringLoader;
import java.util.Enumeration;
import javax.microedition.lcdui.*;


public class AlertCustomizeForm implements
	CommandListener ,ItemCommandListener
{
    private Display display;
    private Displayable parentView;

    Form f;
    ChoiceGroup notify;
    
    ChoiceGroup MessageFile=new ChoiceGroup(SR.MS_MESSAGE_SOUND, ChoiceGroup.POPUP);
    ChoiceGroup OnlineFile=new ChoiceGroup(SR.MS_ONLINE_SOUND, ChoiceGroup.POPUP);
    ChoiceGroup OfflineFile=new ChoiceGroup(SR.MS_OFFLINE_SOUND, ChoiceGroup.POPUP);
    ChoiceGroup ForYouFile=new ChoiceGroup(SR.MS_MESSAGE_FOR_ME_SOUND, ChoiceGroup.POPUP);
    ChoiceGroup ComposingFile=new ChoiceGroup(SR.MS_COMPOSING_SOUND, ChoiceGroup.POPUP);
    ChoiceGroup ConferenceFile=new ChoiceGroup(SR.MS_CONFERENCE_SOUND, ChoiceGroup.POPUP);
    ChoiceGroup StartUpFile=new ChoiceGroup(SR.MS_STARTUP_SOUND, ChoiceGroup.POPUP);
    ChoiceGroup OutgoingFile=new ChoiceGroup(SR.MS_OUTGOING_SOUND, ChoiceGroup.POPUP);
    ChoiceGroup VIPFile=new ChoiceGroup(SR.MS_VIP_SOUND, ChoiceGroup.POPUP);
    
    Gauge sndVol;
    
    Command cmdOk=new Command(SR.MS_OK,Command.OK,1);
    Command cmdMessageSound=new Command(SR.MS_TEST_SOUND, Command.ITEM,10);
    Command cmdOnlineSound=new Command(SR.MS_TEST_SOUND, Command.ITEM,10);
    Command cmdOfflineSound=new Command(SR.MS_TEST_SOUND, Command.ITEM,10);
    Command cmdForYouSound=new Command(SR.MS_TEST_SOUND, Command.ITEM,10);
    Command cmdComposingSound=new Command(SR.MS_TEST_SOUND, Command.ITEM,10);
    Command cmdConferenceSound=new Command(SR.MS_TEST_SOUND, Command.ITEM,10);
    Command cmdStartUpSound=new Command(SR.MS_TEST_SOUND, Command.ITEM,10);
    Command cmdOutgoingSound=new Command(SR.MS_TEST_SOUND, Command.ITEM,10);
    Command cmdVIPSound=new Command(SR.MS_TEST_SOUND, Command.ITEM,10);
    Command cmdCancel=new Command(SR.MS_CANCEL, Command.BACK,99);
    
    AlertCustomize ac;
    Config cf;
    
    boolean nt[];
    
    Vector files[]=new StringLoader().stringLoader("/sounds/res.txt",3);


    /** Creates a new instance of ConfigForm */
    public AlertCustomizeForm(Display display) {
        this.display=display;
        parentView=display.getCurrent();
        
        ac=AlertCustomize.getInstance();
        cf=Config.getInstance();
       
        f=new Form(SR.MS_NOTICES_OPTIONS);
        
        notify=new ChoiceGroup(SR.MS_SHOW_LAST_APPEARED_CONTACTS, ChoiceGroup.MULTIPLE);
        notify.append(SR.MS_STATUS, null);
        notify.append(SR.MS_BLINKING, null);
        notify.append(SR.MS_SOUND, null);
        
        boolean notifyA[]={
            cf.notifyPicture,
            cf.notifyBlink,
            cf.notifySound
        };
        this.nt=notifyA;
        notify.setSelectedFlags(notifyA);
        
        f.append(notify);
        
	for (Enumeration file=files[2].elements(); file.hasMoreElements(); ) {
            addSoundItem((String)file.nextElement());
	}        
        
        try {
            MessageFile.setSelectedIndex(ac.soundsMsgIndex, true);
        } catch (Exception e) {ac.soundsMsgIndex=0;}
        
	f.append(MessageFile);
	MessageFile.addCommand(cmdMessageSound);
	MessageFile.setItemCommandListener(this);
        

        try {
            OnlineFile.setSelectedIndex(ac.soundOnlineIndex, true);
        } catch (Exception e) {ac.soundOnlineIndex=0;}
	f.append(OnlineFile);
        OnlineFile.addCommand(cmdOnlineSound);
	OnlineFile.setItemCommandListener(this);
        
                 
        try {
            OfflineFile.setSelectedIndex(ac.soundOfflineIndex, true);
        } catch (Exception e) {ac.soundOfflineIndex=0;}
	f.append(OfflineFile);
        OfflineFile.addCommand(cmdOfflineSound);
	OfflineFile.setItemCommandListener(this);
        

        try {
            ForYouFile.setSelectedIndex(ac.soundForYouIndex, true);
        } catch (Exception e) {ac.soundForYouIndex=0;}
	f.append(ForYouFile);
        ForYouFile.addCommand(cmdForYouSound);
	ForYouFile.setItemCommandListener(this);

        
        try {
            ComposingFile.setSelectedIndex(ac.soundComposingIndex, true);
        } catch (Exception e) {ac.soundComposingIndex=0;}
	f.append(ComposingFile);
        ComposingFile.addCommand(cmdComposingSound);
	ComposingFile.setItemCommandListener(this);   
        

        try {
            ConferenceFile.setSelectedIndex(ac.soundConferenceIndex, true);
        } catch (Exception e) {ac.soundConferenceIndex=0;}
	f.append(ConferenceFile);
        ConferenceFile.addCommand(cmdConferenceSound);
	ConferenceFile.setItemCommandListener(this);
        

        try {
            StartUpFile.setSelectedIndex(ac.soundStartUpIndex, true);
        } catch (Exception e) {ac.soundStartUpIndex=0;}
	f.append(StartUpFile);
        StartUpFile.addCommand(cmdStartUpSound);
	StartUpFile.setItemCommandListener(this);   
        

        try {
            OutgoingFile.setSelectedIndex(ac.soundOutgoingIndex, true);
        } catch (Exception e) {ac.soundOutgoingIndex=0;}
	f.append(OutgoingFile);
        OutgoingFile.addCommand(cmdOutgoingSound);
	OutgoingFile.setItemCommandListener(this);

        
        try {
            VIPFile.setSelectedIndex(ac.soundVIPIndex, true);
        } catch (Exception e) {ac.soundVIPIndex=0;}
	f.append(VIPFile);
        VIPFile.addCommand(cmdVIPSound);
	VIPFile.setItemCommandListener(this);
        
        
        sndVol=new Gauge("Sound volume", true, 10,  ac.soundVol/10);
	sndVol.addCommand(cmdMessageSound);
	sndVol.setItemCommandListener(this);
	f.append(sndVol);

        f.addCommand(cmdOk);
        f.addCommand(cmdCancel);
        
        f.setCommandListener(this);
        display.setCurrent(f);
    }
    
    public void commandAction(Command c, Displayable d) {
        if (c==cmdOk) {
	    ac.soundsMsgIndex=MessageFile.getSelectedIndex();
	    ac.soundVol=sndVol.getValue()*10;
            ac.soundOnlineIndex=OnlineFile.getSelectedIndex();
            ac.soundOfflineIndex=OfflineFile.getSelectedIndex();
            ac.soundForYouIndex=ForYouFile.getSelectedIndex();
            ac.soundComposingIndex=ComposingFile.getSelectedIndex();
            ac.soundConferenceIndex=ConferenceFile.getSelectedIndex(); 
            ac.soundStartUpIndex=StartUpFile.getSelectedIndex();
            ac.soundOutgoingIndex=OutgoingFile.getSelectedIndex(); 
            ac.soundVIPIndex=VIPFile.getSelectedIndex(); 

 	    ac.loadSoundName();
 	    ac.loadOnlineSoundName();
 	    ac.loadOfflineSoundName();
 	    ac.loadForYouSoundName();
            ac.loadComposingSoundName();
            ac.loadConferenceSoundName();
            ac.loadStartUpSoundName();
            ac.loadOutgoingSoundName();
            ac.loadVIPSoundName();
           
            ac.saveToStorage();
            
            notify.getSelectedFlags(nt);
            cf.notifyPicture=nt[0];
            cf.notifyBlink=nt[1];
            cf.notifySound=nt[2];
            
            cf.saveToStorage();

            destroyView();
        }
            
        if (c==cmdCancel) destroyView();
    }

    public void commandAction(Command command, Item item) {
 	if (command==cmdMessageSound) {
            PlaySound(MessageFile.getSelectedIndex());
 	}
 	if (command==cmdOnlineSound) {
            PlaySound(OnlineFile.getSelectedIndex());
 	}
 	if (command==cmdOfflineSound) {
            PlaySound(OfflineFile.getSelectedIndex());
 	}
 	if (command==cmdForYouSound) {
            PlaySound(ForYouFile.getSelectedIndex());
 	}
 	if (command==cmdComposingSound) {
            PlaySound(ComposingFile.getSelectedIndex());
 	}
 	if (command==cmdConferenceSound) {
            PlaySound(ConferenceFile.getSelectedIndex());
 	}
 	if (command==cmdStartUpSound) {
            PlaySound(StartUpFile.getSelectedIndex());
 	}
 	if (command==cmdOutgoingSound) {
            PlaySound(OutgoingFile.getSelectedIndex());
 	}
 	if (command==cmdVIPSound) {
            PlaySound(VIPFile.getSelectedIndex());
 	}
    }

    public void destroyView(){
        if (display!=null)   display.setCurrent(parentView);
        ((Canvas)parentView).setFullScreenMode(Config.getInstance().fullscreen);
    }

    private void PlaySound(int sound){
	String soundFile=(String)files[1].elementAt(sound);
	String soundType=(String)files[0].elementAt(sound);
        int soundVol=sndVol.getValue()*10;
	new EventNotify(display, soundType, soundFile,soundVol, 0/*, false*/).startNotify();
    }   
    
    private void addSoundItem(String sound){
	    MessageFile.append(sound, null );
            OnlineFile.append(sound, null );
            OfflineFile.append(sound, null );
            ForYouFile.append(sound, null );
            ComposingFile.append(sound, null );
            ConferenceFile.append(sound, null );
            StartUpFile.append(sound, null );
            OutgoingFile.append(sound, null );
            VIPFile.append(sound, null );
    }  
    
}
