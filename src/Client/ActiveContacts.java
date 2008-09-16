/*
 * ActiveContacts.java
 *
 * Created on 20.01.2005, 21:20
 * Copyright (c) 2005-2008, Eugene Stahov (evgs), http://bombus-im.org
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
 *
 */
 
package Client;

import java.util.Enumeration;
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
 * @author EvgS
 */
public class ActiveContacts 
    extends VirtualList
    implements
//#ifndef MENU_LISTENER
        CommandListener
//#else
//#         MenuListener
//#endif
{
    
    Vector activeContacts;
    
    StaticData sd = StaticData.getInstance();

    private Command cmdCancel=new Command(SR.MS_BACK, Command.BACK, 99);
    private Command cmdOk=new Command(SR.MS_SELECT, Command.SCREEN, 1);
    
    /** Creates a new instance of ActiveContacts */
    public ActiveContacts(Display display, Displayable pView, Contact current) {
	super();
        activeContacts=null;
	activeContacts=new Vector();
        //synchronized (sd.roster.getHContacts()) {
            for (Enumeration r=sd.roster.getHContacts().elements(); r.hasMoreElements(); )  {
                Contact c=(Contact)r.nextElement();
                if (c.active()) activeContacts.addElement(c);
            }
        //}

	if (getItemCount()==0) return;
	
        MainBar mainbar=new MainBar(2, String.valueOf(getItemCount()), " ");
        mainbar.addElement(SR.MS_ACTIVE_CONTACTS);
        setMainBarItem(mainbar);

	commandState();

	try {
            int focus=activeContacts.indexOf(current);
            moveCursorTo(focus);
        } catch (Exception e) {}

	attachDisplay(display);
        this.parentView=pView;
    }
    
    public void commandState() {
//#ifdef MENU_LISTENER
//#         menuCommands.removeAllElements();
//#endif
        addCommand(cmdOk);
        addCommand(cmdCancel);
    }
    
//#ifdef MENU_LISTENER
//#     public void showMenu() {
//#         commandState();
//#         new MyMenu(display, parentView, this, SR.MS_STATUS, null, menuCommands);
//#     }
//#endif

    protected int getItemCount() { return activeContacts.size(); }
    protected VirtualElement getItemRef(int index) { 
	return (VirtualElement) activeContacts.elementAt(index);
    }

    public void eventOk() {
	Contact c=(Contact)getFocusedObject();
	new ContactMessageList((Contact)c,display).setParentView(sd.roster);
        //c.msgSuspended=null; // clear suspended message for selected contact
    }

    public void commandAction(Command c, Displayable d) {
	if (c==cmdCancel) destroyView();
	if (c==cmdOk) eventOk();
    }

    public void keyPressed(int keyCode) {
//#ifdef POPUPS
        VirtualList.popup.next();
//#endif
	if (keyCode==KEY_NUM3) {
            destroyView();
        } else if (keyCode==KEY_NUM0) {
            if (getItemCount()<1)
                return;
//#ifndef WSYSTEMGC
            System.gc();
//#endif
            Contact c=(Contact)getFocusedObject();

            Enumeration i=activeContacts.elements();
            
            int pass=0; //
            while (pass<2) {
                if (!i.hasMoreElements()) i=activeContacts.elements();
                Contact p=(Contact)i.nextElement();
                if (pass==1) 
                    if (p.getNewMsgsCount()>0) { 
                        focusToContact(p);
                        setRotator();
                        break; 
                    }
                if (p==c) pass++; // полный круг пройден
            }
            return;
        } else super.keyPressed(keyCode);
    }
    
    private void focusToContact(final Contact c) {
        int index=activeContacts.indexOf(c);
        if (index>=0) 
            moveCursorTo(index);
    }
    
    protected void keyGreen(){
        eventOk();
    }
    
    protected void keyClear () {
        Contact c=(Contact)getFocusedObject();
        c.purge();
        activeContacts.removeElementAt(cursor);
        getMainBarItem().setElementAt(String.valueOf(getItemCount()), 0);
    }
    
    public void destroyView(){
        sd.roster.reEnumRoster();
        display.setCurrent(parentView);
    }
}
