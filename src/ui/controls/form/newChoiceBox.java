/*
 * newChoiceBox.java
 *
 * Created on 22.05.2008, 15:51
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

import Colors.ColorTheme;
import images.RosterIcons;
import java.util.Vector;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Graphics;
import ui.IconTextElement;

/**
 *
 * @author ad
 */

public class newChoiceBox 
        extends IconTextElement {
    
    private int index=0;
    
    private Vector items;
    
    private boolean selectable=true;

    private Display display;
    
    private ColorTheme ct;
    
    /** Creates a new instance of choiceBox */
    public newChoiceBox(Display diplay) {
        super(RosterIcons.getInstance());
        this.display=display;
        items=new Vector();
        ct=ColorTheme.getInstance();
    }

    protected int getImageIndex() { return -1; }
    
    public String toString() {
        if (items.size()<1) return "";//caption;
        return (String) items.elementAt(index);
    }

    public void onSelect(){
        if (items.size()<1) return;
        new listBox(display, items, this);
        //index=(index+1)%items.size();
    }
    
    public int getValue() { return index; }
    
    public void append(String value) { items.addElement(value); }
    
    public void setSelectedIndex(int index) { this.index=index; }
    
    public int getSelectedIndex() { return index; }
    
    public void drawItem(Graphics g, int ofs, boolean sel) {
        int width=g.getClipWidth();
        int height=g.getClipHeight();

        int oldColor=g.getColor();

        g.setColor((sel)?ct.getColor(ColorTheme.CURSOR_BGND):ct.getColor(ColorTheme.LIST_BGND));
        g.fillRect(2, 2, width-4, height-4);
        
        g.setColor((sel)?ct.getColor(ColorTheme.CURSOR_OUTLINE):ct.getColor(ColorTheme.CURSOR_BGND));
        g.drawRect(0, 0, width-1, height-1);
        
        g.setColor(oldColor);

        RosterIcons.getInstance().drawImage(
                g, 0x26, g.getClipX()+g.getClipWidth()-RosterIcons.getInstance().getWidth(), 0 
        );
        super.drawItem(g, ofs, sel);
    }
    
    public boolean isSelectable() { return selectable; }
}
