/*
 * MoodList.java
 *
 * Created on 1 Май 2008 г., 19:54
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package Mood;

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
public class MoodList extends VirtualList implements CommandListener{
    
    /** Creates a new instance of MoodList */
    Command cmdBack=new Command(SR.MS_BACK,Command.BACK,99);
    Command cmdOk=new Command(SR.MS_OK,Command.OK,1);

    Vector moods;
    public MoodList(Display display) {
        super();
        setMainBarItem(new MainBar(SR.MS_USER_MOOD));
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

    public void eventOk() {
        String moodName=((MoodItem)getFocusedObject()).getTipString();
        JabberDataBlock setMood=new Iq(null, Iq.TYPE_SET, "publish-mood");
        JabberDataBlock node=setMood.addChildNs("pubsub", "http://jabber.org/protocol/pubsub")
          .addChild("publish", null);
        node.setAttribute("node", "http://jabber.org/protocol/mood");
        JabberDataBlock mood=node.addChild("item", null)
          .addChildNs("mood", "http://jabber.org/protocol/mood");
        //mood.setAttribute("id","current");
        mood.addChild(moodName, null);
        mood.addChild("text","");
        
        StaticData.getInstance().roster.theStream.send(setMood);
        destroyView();
    }

    public void commandAction(Command command, Displayable displayable) {
        if (command==cmdBack) destroyView();
        eventOk();
    }
    
}
