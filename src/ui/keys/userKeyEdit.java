/*
 * userKeyEdit.java
 *
 * Created on 14.09.2007, 11:01
 * Copyright (c) 2006-2007, Daniel Apatin (ad), http://apatin.net.ru
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
//#ifdef USER_KEYS
//# import javax.microedition.lcdui.Choice;
//# import javax.microedition.lcdui.ChoiceGroup;
//# import javax.microedition.lcdui.Form;
//# import locale.SR;
//#endif
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;

/**
 *
 * @author User
 */
class userKeyEdit implements CommandListener {
//#ifdef USER_KEYS
//#     private final userKeysList keysList;
//#     
//#     private Display display;
//#     private Displayable parentView;
//#     
//#     private Form f;
//#     private ChoiceGroup active;
//#     private ChoiceGroup keyDesc;
//#     private ChoiceGroup keyCode;
//#     
//#     Command cmdOk = new Command(SR.MS_OK , Command.OK, 1);
//#     Command cmdCancel = new Command(SR.MS_BACK , Command.BACK, 99);
//#     
//#     userKey u;
//#     
//#     boolean newKey;
//#endif
    public userKeyEdit(userKeysList keysList, Display display, userKey u) {
//#ifdef USER_KEYS
//# 	this.keysList = keysList;
//# 	this.display=display;
//# 	parentView=display.getCurrent();
//# 	
//# 	newKey=(u==null);
//# 	if (newKey) u=new userKey();
//# 	this.u=u;
//# 	
//# 	String mainbar = (newKey)?SR.MS_ADD_CUSTOM_KEY:(u.toString());
//# 	f = new Form(mainbar);
//#      
//# 	active = new ChoiceGroup(null, Choice.MULTIPLE);
//# 	active.append(SR.MS_ENABLED,null);
//# 	boolean a[] = {u.getActive()};
//# 	
//# 	active.setSelectedFlags(a);
//#         f.append(active);
//#         
//#         keyDesc=new ChoiceGroup(SR.MS_KEYS_ACTION, ChoiceGroup.POPUP);
//#         for (int i=0;i<userKeyExec.getInstance().COMMANDS_DESC.length;i++) {
//#             keyDesc.append(userKeyExec.getInstance().COMMANDS_DESC[i], null);
//#         }
//#         keyDesc.setSelectedIndex(u.getCommandId(), true);
//#         f.append(keyDesc);
//#         
//#         keyCode=new ChoiceGroup(SR.MS_KEY, ChoiceGroup.POPUP);
//#         for (int i=0;i<userKeyExec.getInstance().KEYS_NAME.length;i++) {
//#             keyCode.append(userKeyExec.getInstance().KEYS_NAME[i], null);
//#         }
//#         keyCode.setSelectedIndex(u.getKey(), true);
//#         f.append(keyCode);
//#         
//# 	f.addCommand(cmdOk);
//# 
//# 	f.addCommand(cmdCancel);
//# 	
//# 	f.setCommandListener(this);
//# 	
//# 	display.setCurrent(f);
//#endif
    }
    
    public void commandAction(Command c, Displayable d) {
//#ifdef USER_KEYS
//# 	if (c==cmdOk) {
//#             boolean a[] = new boolean[1];
//# 	    active.getSelectedFlags(a);
//#             
//#             u.setActive(a[0]);
//#             u.setCommand(keyDesc.getSelectedIndex());
//#             u.setKey(keyCode.getSelectedIndex());
//# 
//#             if (newKey) {
//#                 keysList.commandsList.addElement(u);
//#             }
//#             
//# 	    keysList.rmsUpdate();
//# 	    keysList.commandState();
//# 	}
//#         destroyView();
//#endif
    }
    
    public void destroyView()	{
//#ifdef USER_KEYS
//# 	if (display!=null)   display.setCurrent(parentView);
//#endif
    }
}