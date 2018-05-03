/*
 * FontCache.java
 *
 * Created on 5.09.2008, 9:54
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

package Fonts;

import Client.Config;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

/**
 *
 * @author ad
 */
public class FontCache {

    private static Font small;
    private static Font smallBold;
    
    private static Font middle;
    private static Font middleBold;
    
    private static Font big;
    private static Font bigBold;
    
    public final static int smallSize=Font.SIZE_SMALL;
    public final static int middleSize=Font.SIZE_MEDIUM;
    public final static int bigSize=Font.SIZE_LARGE;
    
    public final static int plain=Font.STYLE_PLAIN;
    public final static int bold=Font.STYLE_BOLD;
    
    public final static int face=Font.FACE_PROPORTIONAL;

    public static int roster=0;
    public static int msg=0;
    public static int bar=0;
    public static int baloon=0;

    
    private static Font getSmallFont() {
        if (small==null) small=Font.getFont(face, plain, smallSize);
        return small;
    }
    private static Font getSmallBoldFont() {
        if (smallBold==null) smallBold=Font.getFont(face, bold, smallSize);
        return smallBold;
    }
    
    private static Font getMiddleFont() {
        if (middle==null) middle=Font.getFont(face, plain, middleSize);
        return middle;
    }
    private static Font getMiddleBoldFont() {
        if (middleBold==null) middleBold=Font.getFont(face, bold, middleSize);
        return middleBold;
    }
    
    private static Font getBigFont() {
        if (big==null) big=Font.getFont(face, plain, bigSize);
        return big;
    }
    private static Font getBigBoldFont() {
        if (bigBold==null) bigBold=Font.getFont(face, bold, bigSize);
        return bigBold;
    }
    
    public static Font getFont(boolean isBold, int size) {
        switch (size) {
            case smallSize:
                return (isBold || Config.getInstance().forceBoldFont)
                        ? getSmallBoldFont()
                        : getSmallFont();
            case middleSize:
                return (isBold || Config.getInstance().forceBoldFont) 
                        ? getMiddleBoldFont() 
                        : getMiddleFont();
            case bigSize:
                return (isBold || Config.getInstance().forceBoldFont) 
                        ? getBigBoldFont() 
                        : getBigFont();
        }
        return getSmallFont();
    }

    public static void drawString(Graphics g, String text, int x, int y, int anchor) {
        if (Config.getInstance().shadowed) {
            int color = g.getColor();
            g.setColor((color) / 3 > 0x7f ? 0x333333 : 0xcccccc);
            g.drawString(text, x + 1, y + 1, anchor);
            g.setColor(color);
        }        
        g.drawString(text, x, y, anchor);
    }

    //public final static void resetCache() { roster=msg=bar=baloon=0; }
}
