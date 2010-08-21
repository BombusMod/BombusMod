/*
 * MessageUrl.java
 *
 * Created on 22.12.2005, 3:01
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

package Messages;

import java.util.Vector;
import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.TextField;
import midlet.BombusMod;
import ui.MIDPTextBox;
import ui.MIDPTextBox.TextBoxNotify;
import ui.controls.form.DefForm;
import ui.controls.form.ListItem;
import locale.SR;
import Menu.MenuCommand;
//#ifdef CLIPBOARD
//# import util.ClipBoard;
//# import Client.Msg;
//#endif
import ui.VirtualList;

/**
 *
 * @author EvgS
 */
public class MessageUrl extends DefForm implements TextBoxNotify {

//#ifdef CLIPBOARD
//#     private ClipBoard clipboard=ClipBoard.getInstance();
//# 
//#     protected MenuCommand cmdCopy = new MenuCommand(SR.MS_COPY, MenuCommand.SCREEN, 20);
//#     protected MenuCommand cmdCopyPlus = new MenuCommand("+ "+SR.MS_COPY, MenuCommand.SCREEN, 30);
//#endif
    private Vector urlList;
    MenuCommand cmdGoto=new MenuCommand("Goto", MenuCommand.OK, 2);
    MenuCommand cmdEdit=new MenuCommand("Edit", MenuCommand.SCREEN, 3);

    /** Creates a new instance of MessageUrl
     * @param urlList
     */
    public MessageUrl(Vector urlList) {
	super("URLs");
        this.urlList=urlList;

        commandStateCommon();

	for (int i=0; i<urlList.size(); i++) { // throws exception
	    itemsList.addElement(new ListItem((String)urlList.elementAt(i)));
	}

        setMenuListener(this);

    }
    
    public void menuAction(MenuCommand c, VirtualList d) {
        super.menuAction(c, d);
        if (c==cmdGoto)
            eventOk();
        else if (c==cmdEdit) {
            EditURL();
        }
//#ifdef CLIPBOARD
//#         if (c == cmdCopy)
//#         {
//#             try {
//#                 clipboard.add(new Msg(Msg.MESSAGE_TYPE_IN, "url", null, (String) urlList.elementAt(cursor)));
//#             } catch (Exception e) {/*no messages*/}
//#         }
//# 
//#         if (c==cmdCopyPlus) {
//#             try {
//#                 clipboard.append(new Msg(Msg.MESSAGE_TYPE_IN, "url", null, (String) urlList.elementAt(cursor)));
//#             } catch (Exception e) {/*no messages*/}
//#         }
//#endif
    }
    
    public void eventOk() {
        try {
            BombusMod.getInstance().platformRequest((String)urlList.elementAt(cursor));
        } catch (ConnectionNotFoundException ex) {
            ex.printStackTrace();
        }
	destroyView();
    }
	public void keyPressed(int keyCode) {
		super.keyPressed(keyCode);
		switch (keyCode) {
			case KEY_POUND:                                
                            EditURL();
                    }

	}
    private void EditURL() {
        new MIDPTextBox("Edit URL", (String)urlList.elementAt(cursor), this, TextField.ANY);
    }

    public void OkNotify(String text_return) {
        destroyView();
        Display.getDisplay(BombusMod.getInstance()).setCurrent(parentView);
    }
    
     public void commandState() {
         menuCommands.removeAllElements();
         addMenuCommand(cmdOk);
         addMenuCommand(cmdGoto);
         addMenuCommand(cmdEdit);
         addMenuCommand(cmdCancel);
         commandStateCommon();
     }
     
     public void commandStateCommon() {
//#ifdef CLIPBOARD
//#          if (Client.Config.getInstance().useClipBoard) {
//#              addMenuCommand(cmdCopy);
//#              addMenuCommand(cmdCopyPlus);
//#          }
//#endif
     }
     
    public String touchLeftCommand(){ return SR.MS_MENU; }

    public void touchLeftPressed(){
        showMenu();
    }
    
}
