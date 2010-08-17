/*
 * ColorVisualItem.java
 *
 * Created on 23.08.2008, 22:49
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

import javax.microedition.lcdui.Graphics;
import ui.IconTextElement;

/**
 *
 * @author ad
 */
public class ColorVisualItem
        extends IconTextElement {
//#ifdef PLUGINS
//#     public static String plugin = new String("PLUGIN_COLORS");
//#endif
    
    private String name;
    private int color;
    private String locale="";

    public ColorVisualItem(String name, String locale, int color){
        super(null);
        this.name=name;
        this.color=color;
        this.locale=locale;
    }

    //public String getName() { return name; }
    //public int getValue() { return color; }
    public void setColor(int color) { this.color=color; }
    
    public String toString() { return (locale==null)?name:locale; }
    
    public void drawItem(Graphics g, int ofs, boolean sel) {
        int width=g.getClipWidth();
        int height=super.getVHeight();

        int oldColor=g.getColor();

        g.setColor(color);
        g.fillRect(1, 1, height-2, height-2);

        g.setColor(oldColor);

        g.translate(height,0);
        super.drawItem(g, ofs, sel);
        g.translate(-height,0);
        
    }
    
    //public void setLocale(String locale){ this.locale=locale; }
    
    public void onSelect(){
        //state=!state;
    }
    
    public boolean isSelectable() { return true; }
}
