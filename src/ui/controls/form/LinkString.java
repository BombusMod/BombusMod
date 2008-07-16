/*
 * LinkString.java
 *
 * Created on 25.05.2008, 13:24
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

/**
 *
 * @author ad
 */
public abstract class LinkString 
     extends SimpleString {
    
    ColorTheme ct;
    
    /** Creates a new instance of NewClass */
    public LinkString(String text) {
        super(text, true);
        ct=ColorTheme.getInstance();
    }
    
    public int getColor() {
        return ct.getColor(ColorTheme.MSG_HIGHLIGHT);
    }
    
    public void drawItem(Graphics g, int ofs, boolean sel) {
        int width=g.getClipWidth();
        int height=super.getVHeight();

        int oldColor=g.getColor();
        
        int stringWidth=getFont().stringWidth(toString());

        g.setColor(getColor());
        g.drawLine(4, height-1, stringWidth+4, height-1);

        g.setColor(oldColor);

        super.drawItem(g, ofs, sel);
    }
    
    public boolean isSelectable() { return true; }
    
    public void onSelect(){
        doAction();
    }
    
    public abstract void doAction();
    
}
