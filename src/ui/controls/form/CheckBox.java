/*
 * CheckBox.java
 *
 * Created on 19.05.2008, 22:16
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
public class CheckBox 
        extends IconTextElement {
    
    private boolean state=false;
    private String text="";
    
    private boolean selectable=true;

    private int colorItem;

    /**
     * Creates a new instance of CheckBox
     */
    public CheckBox(String text, boolean state) {
        super(null);
        this.text=text;
        this.state=state;
        colorItem=ColorTheme.getInstance().getColor(ColorTheme.CONTROL_ITEM);
    }
    
    public String toString() { return text; }

    public void onSelect(){
        state=!state;
    }
    
    public void drawItem(Graphics g, int ofs, boolean sel) {
        int width=g.getClipWidth();
        int height=getItemHeight();
        
        int oldColor=g.getColor();
   
        g.setColor(colorItem); 
        g.drawRect(2, 2, height-5, height-5);

        if (state) {
            g.setColor(colorItem); 
            g.fillRect(4, 4, height-8, height-8);
        }
        
        g.setColor(oldColor);

        g.translate(height,0);
        super.drawItem(g, ofs, sel);
        g.translate(-height,0);
    }    
    
    public boolean getValue() { return state; }
    
    public boolean isSelectable() { return selectable; }
    
    public boolean handleEvent(int keyCode) {
         switch (keyCode) {
            //case 12: // enter
            case 5:
                state=!state;
                return true;
         }
        return false;
    }
}
