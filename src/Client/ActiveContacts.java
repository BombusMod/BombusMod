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
import locale.SR;
import ui.MainBar;
import ui.VirtualElement;
import ui.VirtualList;
import ui.controls.form.DefForm;
//#ifdef POPUPS
import ui.controls.PopUp;
//#endif

/**
 *
 * @author EvgS
 */
public class ActiveContacts 
    extends DefForm
{
    
    Vector activeContacts;
    
    StaticData sd = StaticData.getInstance();

    /** Creates a new instance of ActiveContacts
     * @param pView
     * @param current
     */
    public ActiveContacts(VirtualList pView, Contact current) {
	super(SR.MS_ACTIVE_CONTACTS);
	parentView=pView;
        enableListWrapping(true);
        activeContacts=null;
	activeContacts=new Vector();
        //synchronized (sd.roster.getHContacts()) {
            for (Enumeration r=sd.roster.getHContacts().elements(); r.hasMoreElements(); )  {
                Contact c=(Contact)r.nextElement();
                if (c.active()) activeContacts.addElement(c);
            }
        //}

	if (getItemCount()==0) return;
	
        MainBar mb=new MainBar(2, String.valueOf(getItemCount()), " ", false);
        mb.addElement(SR.MS_ACTIVE_CONTACTS);
        setMainBarItem(mb);
	try {
            int focus=activeContacts.indexOf(current);
            moveCursorTo(focus);
        } catch (Exception e) {}

	show(parentView);
    }
    
    protected int getItemCount() { return activeContacts.size(); }
    protected VirtualElement getItemRef(int index) { 
	return (VirtualElement) activeContacts.elementAt(index);
    }
    public void cmdOk() {
        eventOk();
    }
    public void eventOk() {
	Contact c=(Contact)getFocusedObject();
	new ContactMessageList(c);        
        //c.msgSuspended=null; // clear suspended message for selected contact
    }
    

    public void keyPressed(int keyCode) {
        kHold=0;
//#ifdef POPUPS
        PopUp.getInstance().next();
//#endif
	if (keyCode==KEY_NUM3) {
            destroyView();
        } else if (keyCode==KEY_NUM0) {
            if (getItemCount()<1)
                return;

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
        super.destroyView();
    }
    public String touchLeftCommand(){ return SR.MS_SELECT; }    
}
