/*
 * AlertCustomizeForm.java
 *
 * Created on 26.05.2008, 13:12
 *
 * Copyright (c) 2006-2008, Daniel Apatin (ad), http://apatin.net.ru
 *
 * Copyright (c) 2006-2008, Daniel Apatin (ad), http://apatin.net.ru
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
import java.util.Vector;
import ui.controls.form.BoldString;
import ui.controls.form.CheckBox;
import ui.controls.form.DefForm;
import ui.controls.form.DropChoiceBox;
import ui.controls.form.TrackItem;
import util.StringLoader;
import java.util.Enumeration;
import javax.microedition.lcdui.*;


public class AlertCustomizeForm 
        extends DefForm {
    private Display display;
    private Displayable parentView;

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
    
    private TrackItem sndVol;
    
    AlertCustomize ac;
    Config cf;
    Vector files[];
    Vector fileNames;


    /** Creates a new instance of ConfigForm */
    public AlertCustomizeForm(Display display) {
        super(display, SR.MS_NOTICES_OPTIONS);
        this.display=display;
        parentView=display.getCurrent();
        
        ac=AlertCustomize.getInstance();
        cf=Config.getInstance();
        
        files=new StringLoader().stringLoader("/sounds/res.txt",3);
        fileNames=new Vector();
	for (Enumeration file=files[2].elements(); file.hasMoreElements(); ) {
            fileNames.addElement((String)file.nextElement());
	}
        
        itemsList.addElement(new BoldString(SR.MS_MESSAGE_SOUND));
        MessageFile=new DropChoiceBox(display); MessageFile.items=fileNames; 
        MessageFile.setSelectedIndex(ac.soundsMsgIndex); itemsList.addElement(MessageFile);
        
        itemsList.addElement(new BoldString(SR.MS_ONLINE_SOUND));
        OnlineFile=new DropChoiceBox(display); OnlineFile.items=fileNames; 
        OnlineFile.setSelectedIndex(ac.soundOnlineIndex); itemsList.addElement(OnlineFile);
        
        itemsList.addElement(new BoldString(SR.MS_OFFLINE_SOUND));
        OfflineFile=new DropChoiceBox(display); OfflineFile.items=fileNames; 
        OfflineFile.setSelectedIndex(ac.soundOfflineIndex); itemsList.addElement(OfflineFile);
        
        itemsList.addElement(new BoldString(SR.MS_MESSAGE_FOR_ME_SOUND));
        ForYouFile=new DropChoiceBox(display); ForYouFile.items=fileNames; 
        ForYouFile.setSelectedIndex(ac.soundForYouIndex); itemsList.addElement(ForYouFile);
        
        itemsList.addElement(new BoldString(SR.MS_COMPOSING_SOUND));
        ComposingFile=new DropChoiceBox(display); ComposingFile.items=fileNames; 
        ComposingFile.setSelectedIndex(ac.soundComposingIndex); itemsList.addElement(ComposingFile);
        
        itemsList.addElement(new BoldString(SR.MS_CONFERENCE_SOUND));
        ConferenceFile=new DropChoiceBox(display); ConferenceFile.items=fileNames; 
        ConferenceFile.setSelectedIndex(ac.soundConferenceIndex); itemsList.addElement(ConferenceFile);
        
        itemsList.addElement(new BoldString(SR.MS_STARTUP_SOUND));
        StartUpFile=new DropChoiceBox(display); StartUpFile.items=fileNames; 
        StartUpFile.setSelectedIndex(ac.soundStartUpIndex); itemsList.addElement(StartUpFile);
        
        itemsList.addElement(new BoldString(SR.MS_OUTGOING_SOUND));
        OutgoingFile=new DropChoiceBox(display); OutgoingFile.items=fileNames; 
        OutgoingFile.setSelectedIndex(ac.soundOutgoingIndex); itemsList.addElement(OutgoingFile);
        
        itemsList.addElement(new BoldString(SR.MS_VIP_SOUND));
        VIPFile=new DropChoiceBox(display); VIPFile.items=fileNames; 
        VIPFile.setSelectedIndex(ac.soundVIPIndex); itemsList.addElement(VIPFile);

        itemsList.addElement(new BoldString(SR.MS_SHOW_LAST_APPEARED_CONTACTS));
        statusBox=new CheckBox(SR.MS_STATUS, cf.notifyPicture); itemsList.addElement(statusBox);
        blinkBox=new CheckBox(SR.MS_BLINKING, cf.notifyBlink); itemsList.addElement(blinkBox);
        soundBox=new CheckBox(SR.MS_SOUND, cf.notifySound); itemsList.addElement(soundBox);
        
        itemsList.addElement(new BoldString("Sound volume"));
        sndVol=new TrackItem(ac.soundVol/10, 10);
        itemsList.addElement(sndVol);

        moveCursorTo(getNextSelectableRef(-1));
        attachDisplay(display);
    }
    
    public void cmdOk() {
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

        cf.notifyPicture=statusBox.getValue();
        cf.notifyBlink=blinkBox.getValue();
        cf.notifySound=soundBox.getValue();

        cf.saveToStorage();

        destroyView();
    }
}
