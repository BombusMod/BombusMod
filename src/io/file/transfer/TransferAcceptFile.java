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
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;
import locale.SR;
import ui.controls.TextFieldCombo;

/**
 *
 * @author Evg_S
 */
public class TransferAcceptFile 
        implements CommandListener, BrowserListener
{
    private Display display;
    private Displayable parentView;
    
    Form f;
    TransferTask t;
    TextField fileName;
    TextField path;
    
    Command cmdOk=new Command(SR.MS_OK, Command.OK, 1);
    Command cmdDecline=new Command(SR.MS_CANCEL, Command.CANCEL, 90);
    Command cmdBack=new Command(SR.MS_BACK, Command.BACK, 99);
    Command cmdPath=new Command(SR.MS_PATH, Command.SCREEN, 2);
    /** Creates a new instance of TransferAcceptFile */
    public TransferAcceptFile(Display display, TransferTask transferTask) {
        this.display=display;
        parentView=display.getCurrent();
        t=transferTask;
        
        // Trimming filename
        String name=t.fileName.trim();
        if (name.length()>FileIO.MAX_NAME_LEN) {
            int extPos=name.lastIndexOf('.');
            int extLen=name.length()-extPos;
            
            if (extLen>FileIO.MAX_NAME_LEN) {
                name=name.substring(0, FileIO.MAX_NAME_LEN-1);
            } else {
                StringBuffer newName=new StringBuffer();
                newName.append(name.substring(0, FileIO.MAX_NAME_LEN-extLen-2));
                newName.append("~");
                newName.append(name.substring(extPos));
                name=newName.toString();
                newName=null;
            }
        }
        
        f=new Form(SR.MS_ACCEPT_FILE);
        fileName=new TextField(SR.MS_FILE, name, FileIO.MAX_NAME_LEN, TextField.ANY);
        path=new TextFieldCombo(SR.MS_SAVE_TO, t.filePath, 200, TextField.ANY, "recvPath", display);
         
        f.append(new StringItem(SR.MS_SENDER, t.jid));
        f.append(fileName);
        f.append(new StringItem(SR.MS_FILE_SIZE, String.valueOf(t.fileSize)));
        f.append(path);
        f.append(new StringItem(SR.MS_DESCRIPTION, t.description));
        
        f.addCommand(cmdOk);
        f.addCommand(cmdPath);
        f.addCommand(cmdDecline);
        f.addCommand(cmdBack);
        
        f.setCommandListener(this);
        display.setCurrent(f);
    }

    public void BrowserFilePathNotify(String pathSelected) { path.setString(pathSelected); }

    public void commandAction(Command c, Displayable d) {
        if (c==cmdDecline) { t.decline(); }
        if (c==cmdPath) { new Browser(path.getString(), display, this, true); return; }
        if (c==cmdOk) {
            t.fileName=fileName.getString().trim();
            t.filePath=path.getString();
            t.accept();
        }
        
        display.setCurrent(parentView);
    }
}
