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

import Fonts.FontCache;
import javax.microedition.lcdui.Graphics;
//#ifdef GRADIENT
//# //import ui.Gradient;
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
    
//#ifdef GRADIENT
//# //    private Gradient gr=null;
//#endif
    
    /** Creates a new instance of progress */
    public Progress(Graphics g, int x, int y, int height, int width) {
        this.g=g;
        this.x=x;
        this.y=y;
        this.height=height;
        this.width=width;
    }
    
    public static void draw(int filled, String text) {
        g.translate(x, y);
        
        g.setColor(0xffffff);
        g.fillRect(0, 0, width, height);
                
        //draw remained
        g.setColor(0xa2a2a2); g.fillRect(0, 0, width, 1);
        g.setColor(0xb3b3b3); g.fillRect(0, 1, width, 1);
        g.setColor(0xc6c6c6); g.fillRect(0, 2, width, 1);
        g.setColor(0xe6e6e6); g.fillRect(0, 3, width, 7);
        g.setColor(0xffffff); g.fillRect(0, 10, width, 2);
        
        // draw filled
        g.setColor(0x89b700); g.fillRect(0, 0, filled, 1);
        g.setColor(0xc5e26f); g.fillRect(0, 1, filled, 2); 
        g.setColor(0xb6d752); g.fillRect(0, 3, filled, 2);
        g.setColor(0x89b700); g.fillRect(0, 5, filled, 3);
        g.setColor(0x99cc00); g.fillRect(0, 8, filled, 1);
        g.setColor(0x89b700); g.fillRect(0, 9, filled, 1);
        g.setColor(0xc6c6c6); g.fillRect(0, 10, filled, 1);
        g.setColor(0xe6e6e6); g.fillRect(0, 11, filled, 1);

        g.setColor(0x000000);
        g.setFont(FontCache.getSmallFont());
        g.drawString(text, width/2, 0, Graphics.TOP|Graphics.HCENTER);
    }
}