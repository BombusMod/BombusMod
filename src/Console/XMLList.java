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
    implements YesNoAlert.YesNoListener
{
    
    StanzasList stanzas;
    private StaticData sd=StaticData.getInstance();
    Command cmdDeleteAll=new Command(SR.MS_CLEAR_LIST, Command.SCREEN, 10);
    Command cmdNew=new Command(SR.MS_NEW, Command.SCREEN, 5);
    
    /** Creates a new instance of XMLList */
    public XMLList(Display display) {
        super ();
        
        stanzas=StanzasList.getInstance();
        
        setCommandListener(this);
        
	addCommand(cmdBack);
        addCommand(cmdNew);
        addCommand(cmdDeleteAll);
        
        attachDisplay(display);
        
        try {
            focusedItem(0);
        } catch (Exception e) {}
        
	MainBar mainbar=new MainBar("XML_CONSOLE");
        setMainBarItem(mainbar);
    }
    
    protected void beginPaint() {
        getMainBarItem().setElementAt(" ("+getItemCount()+")",1);
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
            //new NewTemplate(display, where); 
        }
	if (m==null) return;

        if (c==cmdDeleteAll) { 
            new YesNoAlert(display, SR.MS_DELETE, SR.MS_SURE_DELETE, this);
            redraw(); 
        }
    }
    
    private void deleteAllMessages() {
        stanzas.deleteAll();
        messages=new Vector();
    }
    
    
    public void keyClear() { 
        if (getItemCount()>0) 
            new YesNoAlert(display, SR.MS_DELETE, SR.MS_SURE_DELETE, this);
    }
    
    public void destroyView(){
	super.destroyView();
    }
    
    public void ActionConfirmed() {
        deleteAllMessages();
    }
}
