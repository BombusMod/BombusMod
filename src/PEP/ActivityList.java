/*
 * MoodList.java
 *
 * Created on 1 Май 2008 г., 19:54
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package PEP;

import java.util.Enumeration;
import ui.MIDPTextBox;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.datablocks.Iq;

import javax.microedition.lcdui.Display;
import locale.SR;
import ui.controls.form.DefForm;
import ui.controls.form.SimpleString;

/**
 *
 * @author evgs
 */
public class ActivityList extends DefForm implements MIDPTextBox.TextBoxNotify {
    
    /** Creates a new instance of MoodList */
    
    String acttext;
            
    public ActivityList(Display display) {
        super(
//#ifdef PEP_ACTIVITY
//#                 SR.MS_USERACTIVITY
//#else
                ""
//#endif
                );

        for (Enumeration e = Activities.getInstance().actValue.elements(); e.hasMoreElements();) {
              SimpleString item = new SimpleString((String)e.nextElement(), false);
              item.selectable = true;
              itemsList.addElement(item);
        }
        enableListWrapping(true);
    }

    public void cmdOk() {
        eventOk();
    }

//#ifdef PEP
//#     public void eventOk() {
//#         if (cursor==0) OkNotify(null);
//#             else new MIDPTextBox(SR.MS_USERACTIVITY, acttext, this);
//#     }
//#endif
    
    public void OkNotify(String actText) {
        //String moodName=((MoodItem)getFocusedObject()).getTipString();
        publish(cursor, actText);
        parentView = sd.roster;
        destroyView();        
    }
    
    public void publish(int activity, String text) {
        String sid="publish-activity";
        JabberDataBlock setActivity=new Iq(null, Iq.TYPE_SET, sid);
        JabberDataBlock action=setActivity.addChildNs("pubsub", "http://jabber.org/protocol/pubsub") .addChild("publish", null);
        action.setAttribute("node", "http://jabber.org/protocol/activity");
        JabberDataBlock item=action.addChild("item", null);
        //item.setAttribute("id", Activities.getInstance().myActId);
        JabberDataBlock act=item.addChildNs("activity", "http://jabber.org/protocol/activity");
        if (activity > 0) {
        String[] actName = Activities.getInstance().getActName(activity);
        JabberDataBlock  gen = act.addChild(actName[0], null);
        gen.addChild(actName[1], null);
        act.addChild("text", text);
        }
         
        try {
            //todo: refactor theStream call; send notification to JabberBlockListener if stream was terminated
            sd.theStream.addBlockListener(new PepPublishResult( sid));
            sd.theStream.send(setActivity);
        } catch (Exception e) {
//#ifdef DEBUG            
//#             e.printStackTrace(); 
//#endif            
        }
    }
        
}
