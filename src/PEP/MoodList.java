/*
 * MoodList.java
 *
 * Created on 1 Май 2008 г., 19:54
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package PEP;

import javax.microedition.lcdui.TextField;
import ui.MIDPTextBox;
import Client.StaticData;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.datablocks.Iq;

import locale.SR;
import ui.VirtualCanvas;
import ui.controls.form.DefForm;

/**
 *
 * @author evgs
 */
public class MoodList extends DefForm implements MIDPTextBox.TextBoxNotify {
//#ifdef PLUGINS
//#     public static String plugin = "PLUGIN_PEP";
//#endif
    
    
    String moodName;
    
    /** Creates a new instance of MoodList */

    public MoodList() {
        super(
//#ifdef PEP
//#                 SR.MS_USERMOOD
//#else
                ""
//#endif
                );

        int count=Moods.getInstance().moodValue.size();
        
        for (int i=0; i<count; i++) {
            itemsList.addElement(new MoodItem(i));
        }
        
        sort(itemsList);
        enableListWrapping(true);
    }

    public void cmdOk() {
        eventOk();
    }

//#ifdef PEP
//#     public void eventOk() {
//#         moodName = ((MoodItem)getFocusedObject()).getTipString();
//#         if (cursor==0) OkNotify(null); 
//#         else 
//#             new MIDPTextBox(SR.MS_USERMOOD, Moods.getInstance().myMoodText, this, TextField.ANY);
//#     }
//#endif
    
    public void OkNotify(String moodText) {        
        publishTune(moodText, moodName);
        parentView = sd.roster;
        destroyView();        
    }
    
    private void publishTune(final String moodText, final String moodName) {
        String sid="publish-mood";
        JabberDataBlock setMood=new Iq(null, Iq.TYPE_SET, sid);
        JabberDataBlock action=setMood.addChildNs("pubsub", "http://jabber.org/protocol/pubsub") .addChild( (moodText!=null)?"publish":"retract", null);
        action.setAttribute("node", "http://jabber.org/protocol/mood");
        JabberDataBlock item=action.addChild("item", null);
        item.setAttribute("id", Moods.getInstance().myMoodId);

        if (moodText!=null) {
            JabberDataBlock mood=item.addChildNs("mood", "http://jabber.org/protocol/mood");
         
            mood.addChild(moodName, null);
            mood.addChild("text",moodText);
        } else {
            item.addChild("retract", null);
            action.setAttribute("notify","1");
        }
        try {
            //todo: refactor theStream call; send notification to JabberBlockListener if stream was terminated
            StaticData.getInstance().roster.theStream.addBlockListener(new PepPublishResult( sid));
            StaticData.getInstance().roster.theStream.send(setMood);
        } catch (Exception e) {
//#ifdef DEBUG            
//#             e.printStackTrace(); 
//#endif            
        }
    }
        
}
