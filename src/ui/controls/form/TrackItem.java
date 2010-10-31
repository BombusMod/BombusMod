/*
 * TrackItem.java
 *
 * Created on 26.05.2008, 11:16
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
import javax.microedition.lcdui.Graphics;
import ui.IconTextElement;

/**
 *
 * @author ad
 */
public class TrackItem
        extends IconTextElement {
    
    int value;
    int steps;

    private boolean selectable=true;
    
    /** Creates a new instance of TrackItem */
    public TrackItem(int value, int maxValue) {
        super(null);
        this.value=value;
        this.steps=maxValue+1;
    }
    
    public int getValue() { return value; }
    
    public void drawItem(Graphics g, int ofs, boolean sel) {
        int width=g.getClipWidth();
        int height=g.getClipHeight();
        
        int itemWidth=6;
        int pos=((width-itemWidth)*value)/(steps-1);
        
        int oldColor=g.getColor();
        
        g.setColor(ColorTheme.getColor(ColorTheme.CONTROL_ITEM));
        g.drawLine(4, height/2, width-4, height/2);
        
        g.fillRect(pos, 2, itemWidth, height-4);

        g.setColor(oldColor);
    }  
    
    public void onSelect(){ value=(value+1)%steps; }
    
    public boolean handleEvent(int keyCode) {
         switch (keyCode) {
            case 4:
                value=(value>0)?value-1:steps-1;
                return true;
            case 6: 
                value=(value+1)%steps;
                return true;
         }
        return false;
    }
}
