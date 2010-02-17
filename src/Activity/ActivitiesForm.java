/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Activity;

import Client.StaticData;
import Mood.PepPublishResult;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.datablocks.Iq;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextField;
import ui.controls.form.DefForm;
import ui.controls.form.DropChoiceBox;
import ui.controls.form.TextInput;

/**
 *
 * @author Vitaly
 */
public class ActivitiesForm extends DefForm {
//#ifdef PLUGINS
//#     public static String plugin = new String("PLUGIN_PEP");
//#endif

    DropChoiceBox activity;
    TextInput ti;
    public ActivitiesForm(Display display, Displayable pView) {
        super(display, pView, "Activity");
        this.display = display;
        activity = new DropChoiceBox(display, "Select activity");
        activity.items = Activities.getInstance().actValue;
        itemsList.addElement(activity);
        ti = new TextInput(display, "Description", "", "pep_act", TextField.ANY);
        itemsList.addElement(ti);
        attachDisplay(display);
    }
    public void cmdOk() {
        publish(activity.getSelectedIndex(), ti.getText());
        display.setCurrent(StaticData.getInstance().roster);
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
            StaticData.getInstance().roster.theStream.addBlockListener(new PepPublishResult(display, sid));
            StaticData.getInstance().roster.theStream.send(setActivity);
        } catch (Exception e) {e.printStackTrace(); }
    }
}


