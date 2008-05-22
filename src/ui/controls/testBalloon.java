/*
 * testBalloon.java
 *
 * Created on 31.03.2008, 14:46
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
import javax.microedition.lcdui.Graphics;

/**
 *
 * @author ad
 */
public class testBalloon {
    public static void draw(Graphics g, int wPop, int hPop, int x, int y, String text) {
        int height=g.getClipHeight();
        int width=g.getClipWidth();
        
        if (x+wPop>width) {
            x=width-wPop-2;
        }
        if (y+hPop>height) {
            y=height-hPop-2;
        }
        
        g.translate(x, y);

        g.setColor(ColorTheme.getInstance().getColor(ColorTheme.BALLOON_BGND));
        g.fillRect(0, 0, wPop, hPop);
        g.setColor(ColorTheme.getInstance().getColor(ColorTheme.BALLOON_INK));
        g.drawRect(0, 0, wPop, hPop);
    }
}
