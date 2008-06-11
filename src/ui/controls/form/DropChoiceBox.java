/*
 * ChoiceBox.java
 *
 * Created on 20.05.2008, 9:06
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
import java.util.Vector;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Graphics;
import ui.IconTextElement;

/**
 *
 * @author ad
 */
public class DropChoiceBox 
        extends IconTextElement {
    
    public int index=0;
    
    public Vector items=new Vector();
    
    private boolean selectable=true;
    
    private Display display;

    private int colorItem;
    
    /**
     * Creates a new instance of ChoiceBox
     */
    public DropChoiceBox(Display display) {
        super(null);
        this.display=display;
        colorItem=ColorTheme.getInstance().getColor(ColorTheme.CONTROL_ITEM);
    }

    public String toString() {
        if (items.size()<1) return "";//caption;
        return (String) items.elementAt(index);
    }

    public void onSelect(){
        if (items.size()<1) return;
        index=(index+1)%items.size();
    }
    
    public int getValue() { return index; }
    
    public void append(String value) { items.addElement(value); }
    
    public void setSelectedIndex(int index) { 
        if (index>items.size()-1)
            index=0;
        this.index=index;
    }
    
    public int size() { 
        return items.size();
    }
    
    public int getSelectedIndex() { return index; }
    
    public void drawItem(Graphics g, int ofs, boolean sel) {
        int width=g.getClipWidth();
        int height=super.getVHeight();
        int xo=g.getClipX();
        int yo=g.getClipY();

        int oldColor=g.getColor();

        int boxSize=height-1;
        g.setColor(colorItem);
        g.drawRoundRect(0, 0, width-1, boxSize, 6, 6);
        
        g.drawRoundRect(width-boxSize-1, 0, boxSize, boxSize, 6, 6);

        int horCenterTrinangle=width-(boxSize/2);
        int vertCenterTrinangle=height/2;
        int size=boxSize/4;
        g.fillTriangle(horCenterTrinangle-size, vertCenterTrinangle-size, horCenterTrinangle+size, vertCenterTrinangle-size, horCenterTrinangle, vertCenterTrinangle+size);
        
        g.setColor(oldColor);
        
        g.setClip(0, yo, width-height-2, height);
        super.drawItem(g, ofs, sel);
        g.setClip(xo, yo, width, height);
    }

    public boolean handleEvent(int keyCode) {
        if (items.size()<1) return false;
        
         switch (keyCode) {
            case 4:
                index=(index>0)?index-1:items.size()-1;
                return true;
            case 6: 
                index=(index+1)%items.size();
                return true;
             case 5:
                new DropListBox(display, items, this);
         }
        return false;
    }
    
    public boolean isSelectable() { return selectable; }
}
