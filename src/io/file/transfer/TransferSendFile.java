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
import javax.microedition.lcdui.TextField;
import locale.SR;
import ui.controls.form.DefForm;
import ui.controls.form.LinkString;
import ui.controls.form.SimpleString;
import ui.controls.form.TextInput;

public class TransferSendFile
        extends DefForm 
        implements BrowserListener {
//#ifdef PLUGINS
//#     public static String plugin = new String("PLUGIN_FILE_TRANSFER");
//#endif
    
    private String to;
    
    private LinkString selectFile;
    private TextInput fileName;
    private TextInput description;

    /** Creates a new instance of TransferAcceptFile
     * @param recipientJid
     */
    public TransferSendFile(String recipientJid) {
        super(SR.MS_SEND_FILE);
        this.to=recipientJid;

        itemsList.addElement(new SimpleString(SR.MS_SEND_FILE_TO, true));
        itemsList.addElement(new SimpleString(recipientJid, false));
        
        fileName = new TextInput(sd.canvas, SR.MS_FILE, null, "sendfile", TextField.ANY);
        itemsList.addElement(fileName);
        
        selectFile=new LinkString(SR.MS_SELECT_FILE) { public void doAction() { initBrowser(); } };
        itemsList.addElement(selectFile);
        
        description = new TextInput(sd.canvas, SR.MS_DESCRIPTION, null, null, TextField.ANY);
        itemsList.addElement(description);
        
        moveCursorTo(2);
    }
    
    public void initBrowser() {
        new Browser(null, this, false);
    }

    public void BrowserFilePathNotify(String pathSelected) { fileName.setValue(pathSelected); redraw(); }

    public void cmdOk() {
        if (fileName.getValue()==null || fileName.getValue().length()==0) return;
        
        try {
            TransferTask task=new TransferTask(to, String.valueOf(System.currentTimeMillis()), fileName.getValue(), description.getValue(), false, null);
            TransferDispatcher.getInstance().sendFile(task);
            //switch to file transfer manager
            new io.file.transfer.TransferManager();
            return;
        } catch (Exception e) {}
    }
}
