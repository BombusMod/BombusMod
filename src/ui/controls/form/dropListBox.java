/*
 * dropListBox.java
 *
 * Created on 22 Май 2008 г., 16:37
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ui.controls.form;

import java.util.Vector;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
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
public class dropListBox 
        extends VirtualList 
        implements CommandListener {
    
    private Command cmdCancel=new Command(SR.MS_CANCEL, Command.BACK,99);
    private Command cmdOk=new Command(SR.MS_OK, Command.OK,1);

    private Vector listItems;

    private dropChoiceBox cb;

    public dropListBox(Display display, Vector listItems, dropChoiceBox cb) {
        super(display);
        this.listItems=listItems;
        this.cb=cb;
        
        setMainBarItem(new MainBar(SR.MS_SELECT));
        addCommand(cmdOk);
        addCommand(cmdCancel);
        setCommandListener(this);
        moveCursorTo(cb.getSelectedIndex());
    }
    
    public void eventOk() {
        if (listItems.size()>0)
            cb.setSelectedIndex(cursor);
        
        display.setCurrent(parentView);
    }

    public void commandAction(Command c, Displayable d){
        if (c==cmdOk)
            eventOk();
        else if (c==cmdCancel)
            display.setCurrent(parentView);
    }

    public VirtualElement getItemRef(int index){ 
        return new listItem((String) listItems.elementAt(index)); 
    }
    public int getItemCount() { return listItems.size(); }
}