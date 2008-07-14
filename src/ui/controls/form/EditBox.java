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
import io.NvStorage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;
import locale.SR;
//#ifdef CLIPBOARD
//# import util.ClipBoard;
//#endif

/**
 *
 * @author ad
 */
public class EditBox implements CommandListener {
    private Display display;
    private TextBox t;
    private TextInput ti;

    private Command cmdOk=new Command(SR.MS_OK, Command.OK,1);
    private Command cmdRecent=new Command(SR.MS_RECENT, Command.SCREEN, 2);
    private Command cmdCancel=new Command(SR.MS_CANCEL, Command.BACK,99);

    public Vector recentList;

    private Displayable parentView;

//#ifdef CLIPBOARD
//#     private ClipBoard clipboard;
//#     private Command cmdCopy = new Command(SR.MS_COPY, Command.SCREEN, 3);
//#     private Command cmdCopyPlus = new Command("+ "+SR.MS_COPY, Command.SCREEN, 4);
//#     private Command cmdPasteText=new Command(SR.MS_PASTE, Command.SCREEN, 5);  
//#endif
    public EditBox(Display display, String text, TextInput ti, int boxType) {
        this.display=display;
        parentView=display.getCurrent();
        this.ti=ti;

        t=new TextBox(SR.MS_EDIT, text, 500, boxType);
//#ifdef CLIPBOARD
//#         if (Config.getInstance().useClipBoard) {
//#             clipboard=ClipBoard.getInstance();
//#             t.addCommand(cmdCopy);
//#             if (!clipboard.isEmpty()) {
//#                 t.addCommand(cmdCopyPlus);
//#                 t.addCommand(cmdPasteText);
//#             }
//#         }
//#endif
        t.addCommand(cmdOk);
        if (ti.id!=null) {
            loadRecentList();
            if (recentList.size()>0)
                t.addCommand(cmdRecent);
        }
        t.addCommand(cmdCancel);
        t.setCommandListener(this);
        if (Config.getInstance().capsState)
            t.setConstraints(TextField.INITIAL_CAPS_SENTENCE);
        display.setCurrent(t);
    }

    public void commandAction(Command c, Displayable d){
        String text=t.getString();
        if (text.length()==0) text=null;
        if (c==cmdRecent) {
            new TextListBox(display, this);
            return;
        }
//#ifdef CLIPBOARD
//#         if (c == cmdCopy) {
//#             try {
//#                clipboard.setClipBoard(text);
//#                 if (!clipboard.isEmpty()) {
//#                     t.addCommand(cmdCopyPlus);
//#                 }
//#             } catch (Exception e) {/*no messages*/}
//#             return;
//#         }
//#         if (c==cmdCopyPlus) {
//#             try {
//#                 StringBuffer clipstr=new StringBuffer(clipboard.getClipBoard()).append("\n\n").append(text);
//#                 
//#                 clipboard.setClipBoard(clipstr.toString());
//#                 clipstr=null;
//#             } catch (Exception e) {/*no messages*/}
//#             return;
//#         }
//#         if (c==cmdPasteText) {
//#             t.insert(clipboard.getClipBoard(), getCaretPos()); 
//#             return;
//#         }
//#endif
        if (c==cmdOk) {
            ti.setValue(text);
            if (ti.id!=null && text!=null) {
                int i=0;
                while (i<recentList.size()) {
                    if ( text.equals((String)recentList.elementAt(i)) || i>9 ) recentList.removeElementAt(i);
                    else i++;
                }
                recentList.insertElementAt(text, 0);
                saveRecentList();
           }
        }

        display.setCurrent(parentView);
    }
//#ifdef CLIPBOARD
//#     public int getCaretPos() {     
//#         int caretPos=t.getCaretPosition();
//#         // +MOTOROLA STUB
//#         if (Config.getInstance().phoneManufacturer==Config.MOTO)
//#             caretPos=-1;
//#         if (caretPos<0)
//#             caretPos=t.getString().length();
//#         return caretPos;
//#     }
//#endif

    private void loadRecentList() {
        recentList=new Vector(10);
        try {
            DataInputStream is=NvStorage.ReadFileRecord(ti.id, 0);

            try { 
                while (true) recentList.addElement(is.readUTF());
            } catch (EOFException e) { is.close(); is=null; }
        } catch (Exception e) { }
    }

    private void saveRecentList() {
        DataOutputStream os=NvStorage.CreateDataOutputStream();
        try {
            for (Enumeration e=recentList.elements(); e.hasMoreElements(); ) {
                String s=(String)e.nextElement();
                os.writeUTF(s);
            }
        } catch (Exception e) { }

        NvStorage.writeFileRecord(os, ti.id, 0, true);
    }

    void setValue(String string) {
        t.setString(string);
    }
}
