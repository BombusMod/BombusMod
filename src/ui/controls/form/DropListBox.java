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
//#ifndef MENU
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
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
//#ifndef MENU
        implements CommandListener
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
//#ifndef MENU
        addCommand(cmdOk);
        addCommand(cmdCancel);
        setCommandListener(this);
//#endif
        moveCursorTo(cb.getSelectedIndex());
    }
    
    public void eventOk() {
        if (listItems.size()>0)
            cb.setSelectedIndex(cursor);
        
        display.setCurrent(parentView);
    }


//#ifndef MENU
    public void commandAction(Command c, Displayable d){
        if (c==cmdOk)
            eventOk();
        else if (c==cmdCancel)
            display.setCurrent(parentView);
    }
//#else
//#     public String getLeftCommand() { return SR.MS_OK; }
//#     public void leftCommand() { eventOk(); }
//#     
//#     public String getRightCommand() { return SR.MS_CANCEL; }
//#endif

    public VirtualElement getItemRef(int index){ 
        return new ListItem((String) listItems.elementAt(index)); 
    }
    
    public int getItemCount() { return listItems.size(); }
}