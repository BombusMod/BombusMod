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
import javax.microedition.lcdui.Displayable;
import ui.MIDPTextBox;
import ui.MIDPTextBox.TextBoxNotify;
import ui.controls.form.DefForm;
import ui.controls.form.ListItem;
import locale.SR;
//#ifndef MENU_LISTENER
//# import javax.microedition.lcdui.Command;
//#else
import Menu.Command;
//#endif
//#ifdef CLIPBOARD
//# import util.ClipBoard;
//# import Client.Msg;
//#endif

/**
 *
 * @author EvgS
 */
public class MessageUrl extends DefForm implements TextBoxNotify {

//#ifdef CLIPBOARD
//#     private ClipBoard clipboard=ClipBoard.getInstance();
//# 
//#     protected Command cmdCopy = new Command(SR.MS_COPY, Command.SCREEN, 20);
//#     protected Command cmdCopyPlus = new Command("+ "+SR.MS_COPY, Command.SCREEN, 30);
//#endif
    private Vector urlList;
    Command cmdGoto=new Command("Goto", Command.OK, 1);
    Command cmdEdit=new Command("Edit", Command.SCREEN, 2);

    /** Creates a new instance of MessageUrl */
    public MessageUrl(Display display, Displayable pView, Vector urlList) {
	super(display, pView, "URLs");
        this.display = display;        
	this.urlList=urlList;

	for (int i=0; i<urlList.size(); i++) { // throws exception
	    itemsList.addElement(new ListItem((String)urlList.elementAt(i)));
	}

        commandStateForThis();
        setCommandListener(this);
	attachDisplay(display);
        this.parentView = pView;
    }
    
    public void commandAction(Command c, Displayable d) {
        super.commandAction(c, d);
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
        new MIDPTextBox(display, this, "Edit URL", (String)urlList.elementAt(cursor), this, TextField.ANY);
    }

    public void OkNotify(String text_return) {
        destroyView();
        Display.getDisplay(BombusMod.getInstance()).setCurrent(parentView);
    }

     public void commandState() {
//#ifdef MENU_LISTENER
        menuCommands.removeAllElements();
//#else
//#          super.commandState();
//#          addCommand(cmdCancel);
//#          removeCommand(cmdOk);
//#endif
     }

     public void commandStateForThis() {
        addCommand(cmdGoto);
        addCommand(cmdEdit);

//#ifdef CLIPBOARD
//#         if (Client.Config.getInstance().useClipBoard) {
//#             addCommand(cmdCopy);
//#             addCommand(cmdCopyPlus);
//#         }
//#endif
     }

//#ifdef MENU_LISTENER
    public String touchLeftCommand(){ return SR.MS_MENU; }

    public void touchLeftPressed(){
        showMenu();
    }
//#endif
    
}
