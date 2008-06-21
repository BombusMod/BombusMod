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
public class DefForm 
        extends VirtualList
//#ifndef MENU
        implements CommandListener
//#endif
    {
    
    public Display display;
    public Displayable parentView;
    
    public Vector itemsList=new Vector();
//#ifndef MENU
    public Command cmdSelect = new Command(SR.MS_SELECT, Command.OK, 1);
    public Command cmdOk = new Command(SR.MS_OK, Command.SCREEN, 2);
    public Command cmdCancel = new Command(SR.MS_BACK, Command.BACK, 99);
//#endif
    public int superWidth;
    /**
     * Creates a new instance of DefForm
     */
    public DefForm(final Display display, String caption) {
	this.display=display;
	parentView=display.getCurrent();
        
	setMainBarItem(new MainBar(caption));
        
        superWidth=super.getWidth();
//#ifndef MENU
        if (Config.getInstance().phoneManufacturer==Config.NOKIA)
            addCommand(cmdSelect);
        
	addCommand(cmdOk);
	addCommand(cmdCancel);
	setCommandListener(this);
//#endif
    }

    protected int getItemCount() { return itemsList.size(); }

    protected VirtualElement getItemRef(int index) {
        return (VirtualElement)itemsList.elementAt(index);
    }

//#ifndef MENU
    public void touchLeftPressed(){ cmdOk(); }
    public void touchRightPressed(){ cmdCancel(); }
    
    public void commandAction(Command command, Displayable displayable) {
	if (command==cmdCancel) {
	    cmdCancel();
	}
	if (command==cmdSelect) {
            getItemRef(cursor).onSelect();
        }
	if (command==cmdOk) {
            cmdOk();
        }
    }
//#else
//#     public void leftCommand() { cmdOk(); }
//#     public String getLeftCommand() { return SR.MS_OK; }
//#     
//#     public void centerCommand() { getItemRef(cursor).onSelect(); }
//#     //public String getCenterCommand() { return SR.MS_CHANGE; }
//#endif

    public void destroyView()	{
	if (display!=null)
            display.setCurrent(parentView);
    }

    public void cmdCancel() {
        destroyView();
    }
    public void cmdOk() { }
}
