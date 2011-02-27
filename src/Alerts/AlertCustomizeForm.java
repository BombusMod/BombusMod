/*
 * AlertCustomizeForm.java
 *
 * Created on 26.05.2008, 13:12
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
 *
 */

package Alerts;

import Client.*;
import locale.SR;
import java.util.Vector;
import ui.EventNotify;
import ui.controls.form.SimpleString;
import ui.controls.form.CheckBox;
import ui.controls.form.DefForm;
import ui.controls.form.DropChoiceBox;
import ui.controls.form.SpacerItem;
import ui.controls.form.TrackItem;
import util.StringLoader;
import java.util.Enumeration;
import Menu.MenuCommand;
import ui.VirtualList;


public class AlertCustomizeForm
        extends DefForm {
    
    
    private CheckBox statusBox;
    private CheckBox blinkBox;
    private CheckBox soundBox;
    
    private DropChoiceBox MessageFile;
    private DropChoiceBox OnlineFile;
    private DropChoiceBox OfflineFile;
    private DropChoiceBox ForYouFile;
    private DropChoiceBox ComposingFile;
    private DropChoiceBox ConferenceFile;
    private DropChoiceBox StartUpFile;
    private DropChoiceBox OutgoingFile;
    private DropChoiceBox VIPFile;
    
    private CheckBox vibrateOnlyHighlited;
    //private CheckBox flashBackLight;
    
    private CheckBox IQNotify;
    
    private TrackItem sndVol;
    
    AlertCustomize ac;
    Vector files[];
    Vector fileNames;

    MenuCommand cmdSave=new MenuCommand(SR.MS_SAVE, MenuCommand.OK, 1);
    MenuCommand cmdTest=new MenuCommand(SR.MS_TEST_SOUND, MenuCommand.SCREEN, 2);

    /** Creates a new instance of ConfigForm
     */
    public AlertCustomizeForm() {
        super(SR.MS_NOTICES_OPTIONS);
        
        ac=AlertCustomize.getInstance();
        cf=Config.getInstance();
        
        files=new StringLoader().stringLoader("/sounds/res.txt",3);
        fileNames=null;
        fileNames=new Vector();
	for (Enumeration file=files[2].elements(); file.hasMoreElements(); ) {
            fileNames.addElement((String)file.nextElement());
	}

        MessageFile=new DropChoiceBox(SR.MS_MESSAGE_SOUND); MessageFile.items=fileNames; 
        MessageFile.setSelectedIndex(ac.soundsMsgIndex); itemsList.addElement(MessageFile);

        OnlineFile=new DropChoiceBox(SR.MS_ONLINE_SOUND); OnlineFile.items=fileNames; 
        OnlineFile.setSelectedIndex(ac.soundOnlineIndex); itemsList.addElement(OnlineFile);

        OfflineFile=new DropChoiceBox(SR.MS_OFFLINE_SOUND); OfflineFile.items=fileNames; 
        OfflineFile.setSelectedIndex(ac.soundOfflineIndex); itemsList.addElement(OfflineFile);

        ForYouFile=new DropChoiceBox(SR.MS_MESSAGE_FOR_ME_SOUND); ForYouFile.items=fileNames; 
        ForYouFile.setSelectedIndex(ac.soundForYouIndex); itemsList.addElement(ForYouFile);

        ComposingFile=new DropChoiceBox(SR.MS_COMPOSING_SOUND); ComposingFile.items=fileNames; 
        ComposingFile.setSelectedIndex(ac.soundComposingIndex); itemsList.addElement(ComposingFile);

        ConferenceFile=new DropChoiceBox(SR.MS_CONFERENCE_SOUND); ConferenceFile.items=fileNames; 
        ConferenceFile.setSelectedIndex(ac.soundConferenceIndex); itemsList.addElement(ConferenceFile);

        StartUpFile=new DropChoiceBox(SR.MS_STARTUP_SOUND); StartUpFile.items=fileNames; 
        StartUpFile.setSelectedIndex(ac.soundStartUpIndex); itemsList.addElement(StartUpFile);

        OutgoingFile=new DropChoiceBox(SR.MS_OUTGOING_SOUND); OutgoingFile.items=fileNames; 
        OutgoingFile.setSelectedIndex(ac.soundOutgoingIndex); itemsList.addElement(OutgoingFile);

        VIPFile=new DropChoiceBox(SR.MS_VIP_SOUND); VIPFile.items=fileNames; 
        VIPFile.setSelectedIndex(ac.soundVIPIndex); itemsList.addElement(VIPFile);

        itemsList.addElement(new SimpleString(SR.MS_SHOW_LAST_APPEARED_CONTACTS, true));
        statusBox=new CheckBox(SR.MS_STATUS, cf.notifyPicture); itemsList.addElement(statusBox);
        blinkBox=new CheckBox(SR.MS_BLINKING, cf.notifyBlink); itemsList.addElement(blinkBox);
        soundBox=new CheckBox(SR.MS_SOUND, cf.notifySound); itemsList.addElement(soundBox);
        
        itemsList.addElement(new SpacerItem(10));
        vibrateOnlyHighlited=new CheckBox(SR.MS_VIBRATE_ONLY_HIGHLITED, ac.vibrateOnlyHighlited); itemsList.addElement(vibrateOnlyHighlited);

        //itemsList.addElement(new SpacerItem(10));
        //flashBackLight=new CheckBox(SR.MS_FLASH_BACKLIGHT, ac.flashBackLight); itemsList.addElement(flashBackLight);
        
        itemsList.addElement(new SimpleString(SR.MS_SOUND_VOLUME, true));
        sndVol=new TrackItem(ac.soundVol/10, 10);
        itemsList.addElement(sndVol);
        
        itemsList.addElement(new SpacerItem(10));
        IQNotify=new CheckBox(SR.MS_SHOW_IQ_REQUESTS, cf.IQNotify); itemsList.addElement(IQNotify);        
    }
    
    public void cmdSave() {
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
        
        ac.vibrateOnlyHighlited=vibrateOnlyHighlited.getValue();
        //ac.flashBackLight=flashBackLight.getValue();
        
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

        cf.notifyPicture=statusBox.getValue();
        cf.notifyBlink=blinkBox.getValue();
        cf.notifySound=soundBox.getValue();
        
        cf.IQNotify=IQNotify.getValue();

        cf.saveToStorage();

        destroyView();
    }
    
    public void menuAction(MenuCommand c, VirtualList d) {
        super.menuAction(c, d);
        if (c==cmdTest)
            PlaySound();
        else if (c==cmdSave) {
            cmdSave();
        }        
    }
    
    private int playable() {
        if (cursor<9) return cursor;
        return -1;
    }
    
    private void PlaySound(){
        int sound=playable();
        if (sound<0) return;
        
        int selectedSound=((DropChoiceBox)itemsList.elementAt(sound)).getSelectedIndex();
        
        String soundFile=(String)files[1].elementAt(selectedSound);
        String soundType=(String)files[0].elementAt(selectedSound);
        int soundVol=sndVol.getValue()*10;
//#ifdef DEBUG
//#         System.out.println(cursor+": "+sound+" "+soundFile+" "+soundType+" "+soundVol);
//#endif
        new EventNotify( soundType, soundFile, soundVol, 0).startNotify();
    }

    public void commandState(){
        menuCommands.removeAllElements();        
        if (playable()>-1)
            addMenuCommand(cmdTest);
        addMenuCommand(cmdSave);
    }
}
