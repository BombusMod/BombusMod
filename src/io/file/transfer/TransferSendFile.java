/*
 * TransferSendFile.java
 *
 * Created on 26.05.2008, 9:15
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

package io.file.transfer;

import io.file.browse.Browser;
import io.file.browse.BrowserListener;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextField;
import locale.SR;
import ui.controls.form.BoldString;
import ui.controls.form.DefForm;
import ui.controls.form.LinkString;
import ui.controls.form.SimpleString;
import ui.controls.form.TextInput;

public class TransferSendFile
        extends DefForm 
        implements BrowserListener {
    
    private Display display;
    private Displayable parentView;

    private String to;
    
    private LinkString selectFile;
    private TextInput fileName;
    private TextInput description;

    /** Creates a new instance of TransferAcceptFile */
    public TransferSendFile(final Display display, String recipientJid) {
        super(display, SR.MS_SEND_FILE);
        this.display=display;
        this.to=recipientJid;
        parentView=display.getCurrent();
        
        selectFile=new LinkString(SR.MS_SELECT_FILE) { public void doAction() { initBrowser(); } };
        itemsList.addElement(selectFile);

        itemsList.addElement(new BoldString(SR.MS_SEND_FILE_TO));
        itemsList.addElement(new SimpleString(recipientJid));
        
        itemsList.addElement(new BoldString(SR.MS_FILE));
        fileName = new TextInput(display, null, "sendfile", TextField.ANY);
        itemsList.addElement(fileName);
        
        itemsList.addElement(new BoldString(SR.MS_DESCRIPTION));
        description = new TextInput(display, null, null, TextField.ANY);
        itemsList.addElement(description);
        
        moveCursorTo(getNextSelectableRef(-1));
        attachDisplay(display);
    }
    
    public void initBrowser() {
        new Browser(null, display, this, false);
    }

    public void BrowserFilePathNotify(String pathSelected) { fileName.setValue(pathSelected); }

    public void commandAction(Command c, Displayable d) {
        if (c==cmdOk) {
            try {
                TransferTask task=new TransferTask(to, String.valueOf(System.currentTimeMillis()), fileName.getValue(), description.getValue());
                TransferDispatcher.getInstance().sendFile(task);
                //switch to file transfer manager
                (new io.file.transfer.TransferManager(display)).setParentView(parentView);
                return;
            } catch (Exception e) {}
        }
        display.setCurrent(parentView);
    }
  
}
