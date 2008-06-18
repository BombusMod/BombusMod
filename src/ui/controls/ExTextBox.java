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
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;
import locale.SR;
//#ifdef CLIPBOARD
//# import util.ClipBoard;
//#endif
//#ifdef ARCHIVE
import Archive.ArchiveList;
//#endif

/**
 *
 * @author ad
 */
public class ExTextBox
    extends TextBox {

    private Display display;
    private Displayable parentView;
    
    public String body;
    private String subj;

    private Config cf;
    
//#ifdef CLIPBOARD
//#     private ClipBoard clipboard;
//#endif
    
//#ifdef ARCHIVE
    private Command cmdPaste=new Command(SR.MS_ARCHIVE, Command.SCREEN, 6);    
//#endif
//#if TEMPLATES
    private Command cmdTemplate=new Command(SR.MS_TEMPLATE, Command.SCREEN, 7); 
//#endif  
//#ifdef CLIPBOARD
//#     private Command cmdPasteText=new Command(SR.MS_PASTE, Command.SCREEN, 8);  
//#endif
    
    int maxSize=500;
            
    /** Creates a new instance of UniTextEdit */
    public ExTextBox(Display display, String body, String subj, int type) {
        super(subj, "", 500, type);
        
        this.display=display;
        parentView=display.getCurrent();

        cf=Config.getInstance();

        this.subj=subj;
		
        try {
            //expanding buffer as much as possible
            maxSize=setMaxSize(4096); //must not trow

            setText(body);
         } catch (Exception e) {}


//#ifdef ARCHIVE
        addCommand(cmdPaste);
//#endif
//#ifdef CLIPBOARD
//#         if (cf.useClipBoard) {
//#             clipboard=ClipBoard.getInstance();
//#             if (!clipboard.isEmpty())
//#                 addCommand(cmdPasteText);
//#         }
//#endif
//#if TEMPLATES
        addCommand(cmdTemplate);
//#endif
        setInitialCaps(cf.capsState);
    }
    
    public void setText(String body) {
        if (body!=null) {
            if (body.length()>maxSize)
                body=body.substring(0, maxSize-1);
            setString(body);
        }
    }
    
    
    public void setParentView(Displayable parentView){
        this.parentView=parentView;
    }
    
    public void destroyView(){
        if (display!=null) display.setCurrent(parentView);
    }

    public int getCaretPos() {     
        int caretPos=getCaretPosition();
        // +MOTOROLA STUB
        if (cf.phoneManufacturer==Config.MOTO)
            caretPos=-1;
        if (caretPos<0)
            caretPos=getString().length();
        return caretPos;
    }
    
    public boolean executeCommand(Command c, Displayable displayable) {
        body=getString();
        
        int caretPos=getCaretPos();
		
        if (body.length()==0) body=null;

//#ifdef ARCHIVE
	if (c==cmdPaste) { new ArchiveList(display, caretPos, 1, this); return true; }
//#endif
//#ifdef CLIPBOARD
//#         if (c==cmdPasteText) { insert(clipboard.getClipBoard(), getCaretPos()); return true; }
//#endif
//#if TEMPLATES
        if (c==cmdTemplate) { new ArchiveList(display, caretPos, 2, this); return true; }
//#endif

        return false;
    }
    
    
    public void insert(String s, int caretPos) {
        String src=getString();

        StringBuffer sb=new StringBuffer(s);
        
        if (caretPos>0) 
            if (src.charAt(caretPos-1)!=' ')   
                sb.insert(0, ' ');
        
        if (caretPos<src.length())
            if (src.charAt(caretPos)!=' ')
                sb.append(' ');
        
        if (caretPos==src.length())
            sb.append(' ');
        
        try {
            int freeSz=getMaxSize()-size();
            if (freeSz<sb.length())
                sb.delete(freeSz, sb.length());
        } catch (Exception e) {}
       
        super.insert(sb.toString(), caretPos);
        sb=null;
    }
    
    private void setInitialCaps(boolean state) {
        setConstraints(state?TextField.INITIAL_CAPS_SENTENCE:TextField.ANY);
    }
}