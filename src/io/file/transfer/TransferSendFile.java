/*
 * TransferSendFile.java
 *
 * Created on 4.11.2006, 0:08
 *
 * Copyright (c) 2005-2007, Eugene Stahov (evgs), http://bombus-im.org
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

package io.file.transfer;

import io.file.browse.Browser;
import io.file.browse.BrowserListener;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;
import locale.SR;
import ui.controls.TextFieldCombo;
import ui.controls.TextFieldEx;

/**
 *
 * @author Evg_S
 */
public class TransferSendFile 
        implements CommandListener, BrowserListener
{
    
    private Display display;
    private Displayable parentView;
    
    Form f;
    TextField fileName;
    /*TextField size;*/
    TextField description;

    private String to;
    
    Command cmdOk=new Command(SR.MS_OK, Command.OK, 1);
    Command cmdBack=new Command(SR.MS_CANCEL, Command.BACK, 99);
    Command cmdPath=new Command(SR.MS_SELECT_FILE, Command.SCREEN, 2);

    /** Creates a new instance of TransferAcceptFile */
    public TransferSendFile(Display display, String recipientJid) {
        this.display=display;
        this.to=recipientJid;
        parentView=display.getCurrent();
        
        f=new Form(SR.MS_SEND_FILE);
        f.append(new StringItem(SR.MS_SEND_FILE_TO, recipientJid));
         
        fileName=new TextFieldCombo(SR.MS_FILE, null, 256, TextField.ANY | TextField.UNEDITABLE, "sendfile", display );
         f.append(fileName);
         
         /*size=new TextField("size", "", 8, TextField.ANY | TextField.UNEDITABLE );
         f.append(size);*/
         
        description=new TextField(SR.MS_DESCRIPTION, "", 128, TextField.ANY );
         f.append(description);

        
        f.addCommand(cmdOk);
        f.addCommand(cmdPath);
        f.addCommand(cmdBack);
        
        f.setCommandListener(this);
        display.setCurrent(f);
    }

    public void BrowserFilePathNotify(String pathSelected) { fileName.setString(pathSelected); }

    public void commandAction(Command c, Displayable d) {
        if (c==cmdPath) { new Browser(fileName.getString(), display, this, false); return; }
        if (c==cmdOk) {
            try {
                TransferTask task=new TransferTask(to, String.valueOf(System.currentTimeMillis()), fileName.getString(), description.getString());
                TransferDispatcher.getInstance().sendFile(task);
                //switch to file transfer manager
                (new io.file.transfer.TransferManager(display)).setParentView(parentView);
                return;
            } catch (Exception e) {}
        }
        
        display.setCurrent(parentView);
    }
  
}
