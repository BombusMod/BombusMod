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
//#ifdef GRADIENT
import ui.Gradient;
//#endif

/**
 *
 * @author ad
 */
public class Progress {

    private int width;
    private int height;

    private int y;
    private int x;

    private Object font;
    
//#ifdef GRADIENT
    private Gradient gr=null;
    private int bottomColor;
//#endif
    private int topColor;

    /** Creates a new instance of progress */
    public Progress(int x, int y, int width) {
        this.x=x;
        this.width=width;
        this.font=FontCache.getFontHeight(false, FontCache.bar);
        this.height=FontCache.getFontHeight(false, FontCache.bar);
        this.y=y-height;
        this.topColor=ColorTheme.getColor(ColorTheme.PGS_COMPLETE_TOP);
//#ifdef GRADIENT
        this.bottomColor=ColorTheme.getColor(ColorTheme.PGS_COMPLETE_BOTTOM);
        if (topColor!=bottomColor)
            this.gr=new Gradient(x, y-height, x+width, y, topColor, bottomColor, false);
//#endif
    }
    
    
    public int getHeight() {
        return height;
    }
}
