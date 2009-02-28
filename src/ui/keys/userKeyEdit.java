/*
 * userKeyEdit.java
 *
 * Created on 14.09.2007, 11:01
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

package ui.keys;
import locale.SR;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import ui.controls.form.CheckBox;
import ui.controls.form.DefForm;
import ui.controls.form.DropChoiceBox;

/**
 *
 * @author ad
 */
class userKeyEdit
     extends DefForm {
//#ifdef PLUGINS
//#     public static String plugin = new String("PLUGIN_USER_KEYS");
//#endif

    private final userKeysList keysList;
    
    private Display display;

    private CheckBox active;
    private DropChoiceBox keyDesc;
    private DropChoiceBox keyCode;

    userKey u;
    
    boolean newKey;

    public userKeyEdit(Display display, Displayable pView, userKeysList keysList, userKey u) {

        super(display, pView, (u==null)?
//#ifdef USER_KEYS
//#             SR.MS_ADD_CUSTOM_KEY:
//#else
                "":
//#endif
                (u.toString()));
        
        this.display=display;
        
	this.keysList = keysList;
	
	newKey=(u==null);
	if (newKey) u=new userKey();
	this.u=u;
        
//#ifdef USER_KEYS
//#         active=new CheckBox(SR.MS_ENABLED, u.active);
//#         itemsList.addElement(active);
//# 
//#         keyDesc=new DropChoiceBox(display, SR.MS_KEYS_ACTION);
//#         for (int i=0;i<u.COMMANDS_DESC.length;i++) {
//#             keyDesc.append(u.COMMANDS_DESC[i]);
//#         }
//#         keyDesc.setSelectedIndex(u.commandId);
//#         itemsList.addElement(keyDesc);
//# 
//#         keyCode=new DropChoiceBox(display, SR.MS_KEY);
//#         for (int i=0;i<u.KEYS_NAME.length;i++) {
//#             keyCode.append(u.KEYS_NAME[i]);
//#         }
//#         keyCode.setSelectedIndex((u.key<0)?0:u.key);
//#         itemsList.addElement(keyCode);
//#endif
        
        moveCursorTo(getNextSelectableRef(-1));
        attachDisplay(display);
        this.parentView=pView;
    }
    
    public void cmdOk() {
        u.active=active.getValue();
        u.commandId=keyDesc.getSelectedIndex();
        u.key=keyCode.getSelectedIndex();

        if (newKey) {
            keysList.commandsList.addElement(u);
        }

        keysList.rmsUpdate();
        keysList.commandState();
        destroyView();
    }
    
    public void destroyView()	{
	if (display!=null) display.setCurrent(parentView);
    }
}
