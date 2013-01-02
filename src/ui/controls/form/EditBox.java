/*
 * EditBox.java
 *
 * Created on 25.05.2008, 17:20
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

package ui.controls.form;

import Client.Config;
import Client.StaticData;
import io.NvStorage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;
import locale.SR;
import ui.VirtualCanvas;
import ui.VirtualList;
import ui.controls.ExTextBox;

/**
 *
 * @author ad
 */
public class EditBox implements CommandListener {
    public ExTextBox t;
    private TextInput ti;

    private Command cmdOk=new Command(SR.MS_OK, Command.OK,1);
    private Command cmdRecent=new Command(SR.MS_RECENT, Command.SCREEN, 2);
    private Command cmdCancel=new Command(SR.MS_CANCEL, Command.BACK,99);

    public Vector recentList;

    private VirtualList parentList;
    
    private String caption;
    
    public EditBox(VirtualList parentList, String caption, String text, TextInput ti, int boxType) {
        this.ti = ti;
        this.caption = caption;
        this.parentList = parentList;
        t = new ExTextBox(parentList, text, caption, false);
        t.textbox.setConstraints(boxType);
        t.textbox.addCommand(cmdOk);
        if (ti.id != null) {
            loadRecentList();
            if (recentList.size() > 0) {
                t.textbox.addCommand(cmdRecent);
            }
        }
        t.textbox.addCommand(cmdCancel);
        if (Config.getInstance().capsState) {
            t.textbox.setConstraints(TextField.INITIAL_CAPS_SENTENCE);
        }
        t.show(this);
    }

    public void commandAction(Command c, Displayable d){
        if (t.executeCommand(c, d)) {
            return;
        }
        if (c == cmdRecent) {
            new TextListBox(this);
            return;
        }
        if (c == cmdOk) {
            ti.setValue(t.body);
            if (ti.id != null && t.body != null) {
                int i = 0;
                while (i < recentList.size()) {
                    if (t.body.equals((String) recentList.elementAt(i)) || i > 9) {
                        recentList.removeElementAt(i);
                    } else {
                        i++;
                    }
                }
                recentList.insertElementAt(t.body, 0);
                saveRecentList();
            }
        }

        VirtualCanvas.getInstance().show(parentList);
    }

    private void loadRecentList() {
        recentList=new Vector(10);
        try {
            DataInputStream is=NvStorage.ReadFileRecord(ti.id, 0);

            try { 
                while (true) recentList.addElement(is.readUTF());
            } catch (EOFException e) { is.close(); is=null; }
        } catch (Exception e) { }
    }

    public void saveRecentList() {
        DataOutputStream os=NvStorage.CreateDataOutputStream();
        try {
            for (Enumeration e=recentList.elements(); e.hasMoreElements(); ) {
                String s=(String)e.nextElement();
                os.writeUTF(s);
            }
        } catch (Exception e) { }

        NvStorage.writeFileRecord(os, ti.id, 0, true);
    }    
}
