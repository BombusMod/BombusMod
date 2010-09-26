/*
 * ExTextBox.java
 *
 * Created on 18.06.2008, 9:16
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
 *
 */
package ui.controls;

import Client.Config;
//#ifdef PLUGINS
//# import Client.StaticData;
//#endif
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;
import locale.SR;
//#ifdef CLIPBOARD
//# import util.ClipBoard;
//#endif
//#ifdef ARCHIVE
import Archive.ArchiveList;
import Archive.MessageArchive;
//#endif

/**
 *
 * @author ad
 */
public class ExTextBox {
    
    protected final TextBox textbox = new TextBox("", "", 500, TextField.ANY);
    protected Displayable parentView;    
    
    public String body;
    private String subj;
    protected int caretPos;

    protected Config cf;
    
//#ifdef CLIPBOARD
//#     private ClipBoard clipboard;
//#endif
    
//#ifdef ARCHIVE
    protected Command cmdPaste=new Command(SR.MS_ARCHIVE, Command.SCREEN, 6);
//#endif
//#if TEMPLATES
//#     protected Command cmdTemplate=new Command(SR.MS_TEMPLATE, Command.SCREEN, 7);
//#endif  
//#ifdef CLIPBOARD
//#     protected Command cmdPasteText=new Command(SR.MS_PASTE, Command.SCREEN, 8);
//#endif
    
    int maxSize=500;
            
    /** Creates a new instance of UniTextEdit */
    public ExTextBox(String body, String subj, boolean writespaces) {
        cf = Config.getInstance();
        textbox.setTitle(subj);
		
        try {
            //expanding buffer as much as possible
            maxSize = textbox.setMaxSize(4096); //must not trow
            if (cf.phoneManufacturer != Config.MICROEMU)
                insert(body, 0, writespaces);
            else setText(body);
         } catch (Exception e) {}
        
        commandState();
    }

    public ExTextBox(String body, String subj) {
        this(body, subj, true);
    }

    public void show(Displayable pView, CommandListener listener) {
        setInitialCaps(cf.capsState);
        if (Config.getInstance().phoneManufacturer == Config.SONYE)
            System.gc(); // prevent flickering on Sony Ericcsson C510
        textbox.setCommandListener(listener);
        parentView = pView;
        midlet.BombusMod.getInstance().setDisplayable(textbox);        
    }
        
    public void destroyView() {
        midlet.BombusMod.getInstance().setDisplayable(parentView);
    }   
    
    public final void insert(String s, int caretPos) {
        insert(s, caretPos, true);
    }

    public final void insert(String s, int caretPos, boolean writespaces) {
        if (s == null) return;

        String src = textbox.getString();

        StringBuffer sb = new StringBuffer(s);

        if (writespaces) {
            if (caretPos > 0) {
                if (src.charAt(caretPos - 1) != ' ') {
                    sb.insert(0, ' ');
                }
            }

            if (caretPos < src.length()) {
                if (src.charAt(caretPos) != ' ') {
                    sb.append(' ');
                }
            }

            if (caretPos == src.length()) {
                sb.append(' ');
            }
        }

        try {
            int freeSz = textbox.getMaxSize() - textbox.size();
            if (freeSz < sb.length()) {
                sb.delete(freeSz, sb.length());
            }
        } catch (Exception e) {
        }
        if (cf.phoneManufacturer != Config.MICROEMU)
            textbox.insert(sb.toString(), caretPos);
        else
           setText(src + sb.toString());
        sb = null;        
    }
    public int getCaretPos() {
        int pos = textbox.getCaretPosition();
        // +MOTOROLA STUB
        if (cf.phoneManufacturer == Config.MOTO || cf.phoneManufacturer == Config.MICROEMU)
            pos = -1;
        if (pos < 0)
            pos = textbox.getString().length();
        return pos;
    }
    public final void setText(String body) {
        if (body != null) {
            if (body.length() > maxSize)
                body = body.substring(0, maxSize - 1);
            textbox.setString(body);
        }
    }
    private void setInitialCaps(boolean state) {
        textbox.setConstraints(state?TextField.INITIAL_CAPS_SENTENCE:TextField.ANY);
    }
    
    public void commandState() {
//#ifdef ARCHIVE
//#ifdef PLUGINS
//#         if (StaticData.getInstance().Archive)
//#endif
            textbox.addCommand(cmdPaste);
//#endif
//#ifdef CLIPBOARD
//#         if (cf.useClipBoard) {
//#             clipboard = ClipBoard.getInstance();
//#             if (!clipboard.isEmpty()) {
//#                 textbox.addCommand(cmdPasteText);                
//#             }
//#         }
//#endif
//#if TEMPLATES
//#ifdef PLUGINS
//#         if (StaticData.getInstance().Archive)
//#endif
//#             textbox.addCommand(cmdTemplate);
//#endif        
    }
    
    public boolean executeCommand(Command c, Displayable displayable) { 
        
        body = textbox.getString();
        caretPos = getCaretPos();
        
        if (body.length() == 0)
            body = null;
        
//#ifdef ARCHIVE
	if (c==cmdPaste) { new ArchiveList(caretPos, 1, textbox); return true; }
//#endif
//#ifdef CLIPBOARD
//#         if (c==cmdPasteText) { insert(clipboard.getClipBoard(), getCaretPos()); return true; }
//#endif
//#if TEMPLATES
//#         if (c==cmdTemplate) { new ArchiveList(caretPos, 2, textbox); return true; }
//#endif
        return false;
    }
}
