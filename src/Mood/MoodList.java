/*
 * MoodList.java
 *
 * Created on 1 Май 2008 г., 19:54
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package Mood;

import javax.microedition.lcdui.TextField;
import ui.MIDPTextBox;
import ui.MainBar;
import Client.StaticData;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.datablocks.Iq;
import java.util.Vector;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import locale.SR;
import ui.VirtualElement;
import ui.VirtualList;

/**
 *
 * @author evgs
 */
public class MoodList extends VirtualList implements CommandListener, MIDPTextBox.TextBoxNotify {
//#ifdef PLUGINS
//#     public static String plugin = new String("PLUGIN_PEP");
//#endif
    
    /** Creates a new instance of MoodList */
    Command cmdBack=new Command(SR.MS_BACK,Command.BACK,99);
    Command cmdOk=new Command(SR.MS_OK,Command.OK,1);

    Vector moods;
    public MoodList(Display display) {
        super();
//#ifdef PEP
//#         setMainBarItem(new MainBar(SR.MS_USERMOOD));
//#endif
        addCommand(cmdBack);
        addCommand(cmdOk);
        setCommandListener(this);
        
        moods=new Vector();
        int count=Moods.getInstance().moodValue.size();
        
        for (int i=0; i<count; i++) {
            moods.addElement(new MoodItem(i));
        }
        
        sort(moods);
        
        attachDisplay(display);
    }

    protected int getItemCount() { return moods.size(); }

    protected VirtualElement getItemRef(int index) { return (VirtualElement)moods.elementAt(index); }

//#ifdef PEP
//#     public void eventOk() {
//#         if (cursor==0) OkNotify(null); 
//#         else new MIDPTextBox(display, SR.MS_USERMOOD, Moods.getInstance().myMoodText, this, TextField.ANY);
//#     }
//#endif
    
    public void OkNotify(String moodText) {
        String moodName=((MoodItem)getFocusedObject()).getTipString();
        publishTune(moodText, moodName);
        destroyView();
        display.setCurrent(StaticData.getInstance().roster);
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
            StaticData.getInstance().roster.theStream.addBlockListener(new MoodPublishResult(display, sid));
            StaticData.getInstance().roster.theStream.send(setMood);
        } catch (Exception e) {e.printStackTrace(); }
    }

    public void commandAction(Command command, Displayable displayable) {
        if (command==cmdBack) destroyView();
        else eventOk();
    }    
}
