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
import Menu.MenuListener;
import Menu.MenuCommand;
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
        MenuListener
    {
    private MenuCommand cmdCancel=new MenuCommand(SR.MS_CANCEL, MenuCommand.BACK,99);
    private MenuCommand cmdOk=new MenuCommand(SR.MS_OK, MenuCommand.OK,1);
    private Vector listItems;

    private DropChoiceBox cb;

    public DropListBox(Vector listItems, DropChoiceBox cb) {
        super();
        this.listItems=listItems;
        this.cb=cb;
        
        setMainBarItem(new MainBar(SR.MS_SELECT));
        
        commandState();
        setMenuListener(this);
        
        moveCursorTo(cb.getSelectedIndex());
        show(parentView);
    }
    
    public void commandState() {
        menuCommands.removeAllElements();
        addMenuCommand(cmdOk);
        addMenuCommand(cmdCancel);
    }
    
    public void eventOk() {
        if (listItems.size()>0)cb.setSelectedIndex(cursor);
        
        destroyView();
    }

    public String touchLeftCommand() { return SR.MS_OK; }
    public void touchLeftPressed(){ eventOk(); }

    public String touchRightCommand() { return SR.MS_CANCEL; }
    public void touchRightPressed(){ destroyView(); }
        
    public VirtualElement getItemRef(int index){ 
        return new ListItem((String) listItems.elementAt(index)); 
    }
    
    public int getItemCount() { return listItems.size(); }

    public void menuAction(MenuCommand command, VirtualList displayable) {
        
    }

}