/*
 * DropListBox.java
 *
 * Created on 22 Май 2008 г., 16:37
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ui.controls.form;

import java.util.Vector;
//#ifndef MENU_LISTENER
//# import javax.microedition.lcdui.CommandListener;
//# import javax.microedition.lcdui.Command;
//#else
import Menu.MenuListener;
import Menu.Command;
//#endif
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import locale.SR;
import ui.MainBar;
import ui.VirtualElement;
import ui.VirtualList;

/**
 *
 * @author ad
 */
public class DropListBox 
        extends VirtualList 
        implements
//#ifndef MENU_LISTENER
//#         CommandListener
//#else
        MenuListener
//#endif
    {
//#ifndef MENU
    private Command cmdCancel=new Command(SR.MS_CANCEL, Command.BACK,99);
    private Command cmdOk=new Command(SR.MS_OK, Command.OK,1);
//#endif
    private Vector listItems;

    private DropChoiceBox cb;

    public DropListBox(Display display, Vector listItems, DropChoiceBox cb) {
        super(display);
        this.listItems=listItems;
        this.cb=cb;
        
        setMainBarItem(new MainBar(SR.MS_SELECT));
        
        commandState();
        setCommandListener(this);
        
        moveCursorTo(cb.getSelectedIndex());
    }
    
    public void commandState() {
//#ifndef MENU_LISTENER
//#         menuCommands.removeAllElements();
//#         addCommand(cmdOk);
//#         addCommand(cmdCancel);
//#endif
    }
    
    public void eventOk() {
        if (listItems.size()>0)cb.setSelectedIndex(cursor);
        
        destroyView();
    }

//#ifdef MENU_LISTENER
    public String touchLeftCommand() { return SR.MS_OK; }
    public void touchLeftPressed(){ eventOk(); }

    public String touchRightCommand() { return SR.MS_CANCEL; }
    public void touchRightPressed(){ destroyView(); }
//#endif
    
    public void destroyView()	{
	if (display!=null)
            display.setCurrent(parentView);
    }

    public VirtualElement getItemRef(int index){ 
        return new ListItem((String) listItems.elementAt(index)); 
    }
    
    public int getItemCount() { return listItems.size(); }

    public void commandAction(Command c, Displayable displayable) {
//#ifndef MENU_LISTENER
//#         if (c==cmdOk) eventOk();
//#         else if (c==cmdCancel) destroyView();
//#endif
    }
}