/*
 * textInput.java
 *
 * Created on 19.05.2008, 23:01
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
import Colors.ColorTheme;
import images.RosterIcons;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;
import locale.SR;
import ui.IconTextElement;

/**
 *
 * @author ad
 */
public class textInput 
        extends IconTextElement {

    private String text="";
    
    private boolean selectable=true;

    private Display display;
    
    ColorTheme ct;
    
    /** Creates a new instance of textInput */
    public textInput(Display display, String text) {
        super(RosterIcons.getInstance());
        this.display=display;
        this.text=(text==null)?"":text;
        ct=ColorTheme.getInstance();
    }

    protected int getImageIndex() { return -1; }
    
    public String toString() { return (text==null)?"":text; }
    
    public void onSelect(){
        new editBox(display, text, this);
    }
    
    public String getValue() { return (text==null)?"":text; }

    public void setValue(String text) { this.text=text; }
    
    public void drawItem(Graphics g, int ofs, boolean sel) {
        int width=g.getClipWidth();
        int height=g.getClipHeight();

        int oldColor=g.getColor();
        
        g.setColor(ct.getColor(ColorTheme.LIST_BGND));
        g.fillRect(2, 2, width-4, height-4);

        g.setColor((sel)?ct.getColor(ColorTheme.CURSOR_OUTLINE):ct.getColor(ColorTheme.CURSOR_BGND));
        g.drawRoundRect(0, 0, width-1, height-1, 6, 6);
        
        g.setColor(oldColor);
        
        super.drawItem(g, ofs, sel);
    }
    
    class editBox implements CommandListener {
        private Display display;
        private TextBox t;
        private textInput ti;
        
        private Command cmdCancel=new Command(SR.MS_CANCEL, Command.BACK,99);
        private Command cmdOk=new Command(SR.MS_OK, Command.OK,1);

        private Displayable parentView;

        public editBox(Display display, String text, textInput ti) {
            this.display=display;
            parentView=display.getCurrent();
            this.ti=ti;
            t=new TextBox(SR.MS_EDIT, text, 500, TextField.ANY);
            t.addCommand(cmdOk);
            t.addCommand(cmdCancel);
            t.setCommandListener(this);
            t.setConstraints(Config.getInstance().capsState?TextField.INITIAL_CAPS_SENTENCE:TextField.ANY);
            display.setCurrent(t);
        }

        public void commandAction(Command c, Displayable d){
            String text=t.getString();
            if (text.length()==0) text=null;

            if (c==cmdOk)
                ti.setValue(text);

            display.setCurrent(parentView);
        }
    }
    
    public boolean isSelectable() { return selectable; }
}
