/*
 * Progress.java
 *
 * Created on 15.05.2008, 19:47
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

package ui.controls;

import Client.Config;
import Colors.ColorTheme;
import Fonts.FontCache;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
//#ifdef GRADIENT
//# import ui.Gradient;
//#endif

/**
 *
 * @author ad
 */
public class Progress {

    private static int width;
    private static int height;

    private static int y;
    private static int x;

    private static Font font;
    
//#ifdef GRADIENT
//#     private static Gradient gr=null;
//#     private static int bottomColor;
//#endif
    private static int topColor;

    /** Creates a new instance of progress */
    public Progress(int x, int y, int width) {
        Progress.x=x;
        Progress.width=width;
        Progress.font=FontCache.getFont(false, FontCache.bar);
        Progress.height=font.getHeight();
        Progress.y=y-height;
        Progress.topColor=ColorTheme.getColor(ColorTheme.PGS_COMPLETE_TOP);
//#ifdef GRADIENT
//#         Progress.bottomColor=ColorTheme.getColor(ColorTheme.PGS_COMPLETE_BOTTOM);
//#         if (topColor!=bottomColor)
//#             Progress.gr=new Gradient(x, y-height, x+width, y, topColor, bottomColor, false);
//#endif
    }
    
    public static void draw(Graphics g, int filled, String text) {
        g.setColor(ColorTheme.getColor(ColorTheme.PGS_REMAINED));
        g.fillRect(x, y, width, height);
//#ifdef GRADIENT
//#         if (topColor!=bottomColor) {
//#             gr.paintWidth(g, x+filled);
//#         } else {
//#endif
            g.setColor(topColor);
            g.fillRect(x, y+1, filled, height - 1);
//#ifdef GRADIENT
//#         }
//#endif
       
        g.setColor(ColorTheme.getColor(ColorTheme.PGS_INK));
        g.setFont(font);
        g.drawString(text, x+(width/2), y, Graphics.TOP|Graphics.HCENTER);
        g.drawRect(x, y, width-1, height-1);
        //g.drawLine(x,y,width,y);
        g.drawLine(x+filled,y+1,x+filled,y+height-1);
    }
    
    public static int getHeight() {
        return height;
    }
}
