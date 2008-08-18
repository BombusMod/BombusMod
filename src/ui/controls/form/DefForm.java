/*
 * DefForm.java
 *
 * Created on 21.05.2008, 9:40
 *
 * Copyright (c) 2006-2008, Daniel Apatin (ad), http://apatin.net.ru
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * You can also redistribute and/or modify this program under the
 * terms of the Psi License, specified in the accompanied COPYING
 * file, as published by the Psi Project; either dated January 1st,
 * 2005, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package ui.controls.form;

//import Client.Config;
import Client.Config;
import java.util.Vector;
//#ifndef MENU_LISTENER
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Command;
//#else
//# import Menu.MenuListener;
//# import Menu.Command;
//# import Menu.MyMenu;
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
public class DefForm 
        extends VirtualList
        implements
//#ifndef MENU_LISTENER
        CommandListener
//#else
//#         MenuListener
//#endif
    {
    
    public Vector menuCommands=new Vector();
    
    public Display display;
    
    public Vector itemsList=new Vector();

    public Command cmdOk = new Command(SR.MS_OK, Command.OK, 1);
    public Command cmdCancel = new Command(SR.MS_BACK, Command.BACK, 99);

    public int superWidth;
    /**
     * Creates a new instance of DefForm
     */
    public DefForm(final Display display, Displayable pView, String caption) {
	this.display=display;
        
	setMainBarItem(new MainBar(caption));
        
        superWidth=super.getWidth();
        
        commandState();
        
	setCommandListener(this);
        
        this.parentView=pView;
    }

    protected int getItemCount() { return itemsList.size(); }

    protected VirtualElement getItemRef(int index) {
        return (VirtualElement)itemsList.elementAt(index);
    }

    public void touchRightPressed(){ cmdCancel(); }
    
    public void commandAction(Command command, Displayable displayable) {
	if (command==cmdCancel) {
	    cmdCancel();
	}
	if (command==cmdOk) {
            cmdOk();
        }
    }

    public void destroyView()	{
	if (display!=null)
            display.setCurrent(parentView);
    }

    public void cmdCancel() {
        destroyView();
    }
    public void cmdOk() { }
    
    public void commandState() {
//#ifdef MENU_LISTENER
//#         menuCommands.removeAllElements();
//#endif
	addCommand(cmdOk);
	addCommand(cmdCancel);
    }
    
//#ifdef MENU_LISTENER
//#     public Command getCommand(int index) {
//#         if (index>menuCommands.size()-1) return null;
//#         return (Command) menuCommands.elementAt(index);
//#     }
//# 
//#     public void addCommand(Command command) {
//#         if (menuCommands.indexOf(command)<0)
//#             menuCommands.addElement(command);
//#     }
//#     public void removeCommand(Command command) {
//#         menuCommands.removeElement(command);        
//#     }
//#     
//#     public void setCommandListener(MenuListener menuListener) { }
//#     
//#     protected void keyPressed(int keyCode) { // overriding this method to avoid autorepeat
//#         if (keyCode==Config.SOFT_LEFT) {
//#             showMenu();
//#             return;
//#         }
//#         if (keyCode==Config.SOFT_RIGHT || keyCode==Config.KEY_BACK) {
//#             cmdCancel();
//#             return;
//#         }
//#         super.keyPressed(keyCode);
//#     }
//#     
//#     public void showMenu() {
//#         commandState();
//#         if (menuCommands.size()==2) {
//#             if (menuCommands.elementAt(0).equals(cmdOk) && menuCommands.elementAt(1).equals(cmdCancel)) {
//#                 cmdOk();
//#                 return;
//#             }
//#         }
//#         new MyMenu(display, parentView, this, "", null, menuCommands);
//#     }
//#     
//#     public String touchLeftCommand(){ return SR.MS_OK; }
//#     
//#     public void touchLeftPressed(){ showMenu(); }
//#endif
}
