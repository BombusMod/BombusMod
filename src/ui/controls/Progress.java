/*
 * progress.java
 *
 * Created on 15 Май 2008 г., 19:47
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

    private static Graphics g;
    private static Font font;
    
//#ifdef GRADIENT
//#     private static Gradient gr=null;
//#     private static int bottomColor;
//#endif
    private static int topColor;
    
    private static ColorTheme ct;

    /** Creates a new instance of progress */
    public Progress(Graphics g, int x, int y, int width) {
        this.g=g;
        this.x=x;
        this.y=y;
        this.width=width;
        this.font=FontCache.getSmallFont();
        this.height=font.getHeight();
        ct=ColorTheme.getInstance();
        this.topColor=ct.getColor(ColorTheme.PGS_COMPLETE_TOP);
//#ifdef GRADIENT
//#         this.bottomColor=ct.getColor(ColorTheme.PGS_COMPLETE_BOTTOM);
//#         if (topColor!=bottomColor)
//#             this.gr=new Gradient(0, 1, width, height, topColor, bottomColor, false);
//#endif
    }
    
    public static void draw(int filled, String text) {
        g.translate(x, y-height);
        g.setClip(0,0,width,height);
        
        g.setColor(ct.getColor(ColorTheme.PGS_REMAINED));
        g.fillRect(0, 0, width, height);
//#ifdef GRADIENT
//#         if (topColor!=bottomColor) {
//#             gr.paintWidth(g, filled);
//#         } else {
//#endif
            g.setColor(topColor);
            g.fillRect(0, 1, filled, height);
//#ifdef GRADIENT
//#         }
//#endif
       
        g.setColor(ct.getColor(ColorTheme.PGS_INK));
        g.setFont(font);
        g.drawString(text, width/2, 0, Graphics.TOP|Graphics.HCENTER);
        g.drawLine(x,0,width,0);
        g.drawLine(filled,1,filled,height-1);
    }
}