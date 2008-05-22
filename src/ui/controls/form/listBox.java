/*
 * listBox.java
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
import ui.VirtualElement;
import ui.VirtualList;

/**
 *
 * @author ad
 */
class listBox extends VirtualList implements CommandListener {
    private Command cmdCancel=new Command(SR.MS_CANCEL, Command.BACK,99);
    private Command cmdOk=new Command(SR.MS_OK, Command.OK,1);

    //private Display display;

    private Vector listItems=new Vector();

    private newChoiceBox cb;

    public listBox(Display display, Vector listItems, newChoiceBox cb) {
        super();
        this.listItems=listItems;
        this.cb=cb;
        addCommand(cmdOk);
        addCommand(cmdCancel);
        setCommandListener(this);
        attachDisplay(display);
    }

    public void commandAction(Command c, Displayable d){
        if (c==cmdOk)
            cb.setSelectedIndex(cursor);

        //display.setCurrent(parentView);
    }

    public VirtualElement getItemRef(int index){ 
        return (VirtualElement)listItems.elementAt(index); 
    }
    public int getItemCount() { return listItems.size(); }
}