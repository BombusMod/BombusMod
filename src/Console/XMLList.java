/*
 * XMLList.java
 *
 * Created on 7 јпрель 2008 г., 13:37
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package Console;

import Client.Msg;
import Client.StaticData;
import Messages.MessageList;
import java.util.Vector;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import locale.SR;
import ui.MainBar;
import ui.YesNoAlert;

/**
 *
 * @author ad
 */
public class XMLList 
    extends MessageList
{
    
    StanzasList stanzas;
    private StaticData sd=StaticData.getInstance();
    
    private Command cmdNew=new Command(SR.MS_NEW, Command.SCREEN, 5);
    private Command cmdEnableDisable=new Command("Enable/Disable", Command.SCREEN, 6);
    private Command cmdDeleteAll=new Command(SR.MS_CLEAR_LIST, Command.SCREEN, 10);    
    /** Creates a new instance of XMLList */
    public XMLList(Display display) {
        super ();
        
        stanzas=StanzasList.getInstance();
        
        setCommandListener(this);
        
	addCommand(cmdBack);
        addCommand(cmdNew);
        addCommand(cmdEnableDisable);
        addCommand(cmdDeleteAll);

        attachDisplay(display);
        
        try {
            focusedItem(0);
        } catch (Exception e) {}
        
	MainBar mainbar=new MainBar("XML_CONSOLE");
        setMainBarItem(mainbar);
    }
    
    protected void beginPaint() {
        StringBuffer str = new StringBuffer();
        if (!stanzas.enabled)
            str.append(" - Disabled");
        str.append(" (");
        str.append(getItemCount());
        str.append(")");
        
        getMainBarItem().setElementAt(str.toString(),1);
    }
    

    public int getItemCount() {
        return stanzas.size();
    }
    
    public Msg getMessage(int index) {
	return stanzas.msg(index);
    }

    
    public void commandAction(Command c, Displayable d) {
        super.commandAction(c,d);
        
	Msg m=getMessage(cursor);
        if (c==cmdNew) { 
            new StanzaEdit(display, getMessage(cursor).toString()).setParentView(this);
        }
        if (c==cmdEnableDisable) {
            stanzas.enabled=!stanzas.enabled;
            redraw();
        }
	if (m==null) return;

        if (c==cmdDeleteAll) { 
            deleteAllMessages();
        }
    }
    
    private void deleteAllMessages() {
        if (getItemCount()>0) {
            stanzas.deleteAll();
            messages=new Vector();
        }
        redraw(); 
    }
    
    
    public void keyClear() { 
        deleteAllMessages();
    }
    
    public void destroyView(){
	super.destroyView();
    }
}
