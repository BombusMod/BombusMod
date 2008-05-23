/*
 * ColorItem.java
 *
 * Created on 23.05.2008, 12:51
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

package Colors;

import images.RosterIcons;
import javax.microedition.lcdui.Graphics;
import ui.IconTextElement;

/**
 *
 * @author ad
 */
public class ColorItem
        extends IconTextElement {
    
    private String name;
    private int color;
    private String locale="";

    public ColorItem(String name, int color){
        super(RosterIcons.getInstance());
        this.name=name;
        this.color=color;
    }

    public String getName() { return name; }
    public int getValue() { return color; }
    public void setColor(int color) { this.color=color; }

    protected int getImageIndex() { return -1; }
    
    public String toString() { return (locale==null)?name:locale; }
    
    public void drawItem(Graphics g, int ofs, boolean sel) {
        int width=g.getClipWidth();
        int height=g.getClipHeight();

        int oldColor=g.getColor();

        g.setColor(color);
        g.fillRect(1, 1, height-2, height-2);

        g.setColor(oldColor);
        
        super.drawItem(g, -height, sel);
    }
    
    public void setLocale(String locale){
        this.locale=locale;
    }
    
    public void onSelect(){
        //state=!state;
    }
    
    public boolean isSelectable() { return true; }
}
