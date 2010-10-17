/*
 * TransferAcceptFile.java
 *
 * Created on 29.10.2006, 1:20
 *
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
 */

package io.file.transfer;

import io.file.FileIO;
import io.file.browse.Browser;
import io.file.browse.BrowserListener;

import javax.microedition.lcdui.TextField;
import locale.SR;
import ui.controls.form.LinkString;
import ui.controls.form.SimpleString;
import ui.controls.form.DefForm;
import ui.controls.form.MultiLine;
import ui.controls.form.TextInput;

/**
 *
 * @author Evg_S
 */
public class TransferAcceptFile
        extends DefForm
        implements BrowserListener {
//#ifdef PLUGINS
//#     public static String plugin = new String("PLUGIN_FILE_TRANSFER");
//#endif
    
    TransferTask t;
    TextInput fileName;
    TextInput path;
    
    LinkString selectFile;
    String ftFolder;
    boolean autoaccept = false;

    /** Creates a new instance of TransferAcceptFile
     * @param transferTask
     */
    public TransferAcceptFile(TransferTask transferTask) {
        super(SR.MS_ACCEPT_FILE, false);
        
        t=transferTask;
        
        // Trimming filename
        String name=t.fileName.trim();
        if (name.length()>FileIO.MAX_NAME_LEN) {
            int extPos=name.lastIndexOf('.');
            int extLen=name.length()-extPos;
            
            if (extLen>FileIO.MAX_NAME_LEN) {
                name=name.substring(0, FileIO.MAX_NAME_LEN-1);
            } else {
                StringBuffer newName=new StringBuffer(name.substring(0, FileIO.MAX_NAME_LEN-extLen-2))
                .append("~")
                .append(name.substring(extPos));
                name=newName.toString();
                newName=null;
            }
        }
        
        fileName=new TextInput(sd.canvas, SR.MS_FILE, name, "", TextField.ANY);
        itemsList.addElement(fileName);
        itemsList.addElement(new SimpleString(SR.MS_FILE_SIZE+" "+String.valueOf(t.fileSize)+" bytes", true));

        path=new TextInput(sd.canvas, SR.MS_SAVE_TO, t.filePath, "recvPath", TextField.ANY);
        itemsList.addElement(path);
        
        selectFile=new LinkString(SR.MS_PATH) { public void doAction() { initBrowser(); } };
        itemsList.addElement(selectFile);
                
        itemsList.addElement(new MultiLine(SR.MS_SENDER, t.jid, sd.roster.getListWidth()));

        itemsList.addElement(new MultiLine(SR.MS_DESCRIPTION, t.description, sd.roster.getListWidth()));
        ftFolder = TransferConfig.getInstance().ftFolder;
        autoaccept = !(ftFolder == null || ftFolder.equals(""));
        if (autoaccept) {
            t.fileName = fileName.getValue().trim();
            t.filePath = ftFolder;
            t.accept();
            destroyView();
        } else {
            show();
        }
    }

    
    public void initBrowser() { new Browser(path.getValue(), this, true); }

    public void BrowserFilePathNotify(String pathSelected) { path.setValue(pathSelected); }
    
    public final void cmdOk() {
        
        t.fileName = fileName.getValue().trim();
        t.filePath = path.getValue();        
        t.accept();
        destroyView();
    }
    
    public void cmdCancel() {
        t.decline();
        destroyView();
    }
}
