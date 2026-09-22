/*
 * ColorSelector.java
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
//#ifdef COLOR_TUNE
package ui.controls.form;

import Client.Config;
import Client.StaticData;
import Colors.ColorTheme;
import Colors.ColorsList;
import Fonts.FontCache;
import ui.VirtualElement;

public class ColorSelector implements VirtualElement {

    static Object mfont = null; // Font removed
    Object G;
    private int yTranslate;
    public int cpos;
    String nowcolor;
    int red;
    int green;
    int blue;
    String val;
    public int dy;
    private int value;
    int paramName;
    int ncolor;
    private int color;
    private int py;
    private int ph;
    private ColorsList list;
    public int pxred;
    public int pxgreen;
    public int pxblue;

    public ColorSelector(ColorsList list, int paramName) {

        this.list = list;
        this.paramName = paramName;

        this.color = ColorTheme.getColor(paramName);


        red = ColorTheme.getRed(color);
        green = ColorTheme.getGreen(color);
        blue = ColorTheme.getBlue(color);

        //String s = cl.ColorToString(red, green, blue);

        cpos = 0;
    }

    public boolean pointerPressed(int x, int y) {
        y -= yTranslate;
        int itemIndex = getItemIndex(x, y);
        if (cpos == itemIndex) {
            return setColor(getColor(x, y));
        } else {
            if (itemIndex > -1) {
                cpos = itemIndex;
                return true;
            }
        }

        return false;
    }

    public boolean pointerDragged(int x, int y) {
        y -= yTranslate;
        return setColor(getColor(x, y));
    }

    private boolean setColor(int val) {
        if (val > -1) {
            switch (cpos) {
                case 0:
                    red = val;
                    return true;
                case 1:
                    green = val;
                    return true;
                case 2:
                    blue = val;
                    return true;
            }
        }

        return false;
    }

    private int getColor(int x, int y) {
        if (y >= py - ph && y <= py) {
            return (int) (((py - y) * 255) / ph + 1 / 2); // meaning as "return Math.round ((py-y)*255/ph);"
        }
        return -1;
    }

    public int getItemIndex(int x, int y) {
        // red
        int x1 = pxred - 10;
        int x2 = x1 + 20;
        if (x2 > x && x > x1) {
            return 0;
        }
        // green
        x1 = pxgreen - 10;
        x2 = x1 + 20;
        if (x2 > x && x > x1) {
            return 1;
        }
        // blue
        x1 = pxblue - 10;
        x2 = x1 + 20;
        if (x2 > x && x > x1) {
            return 2;
        }

        return -1;
    }

    public boolean handleEvent(int key) {
        return false;
    }

    public void selectNext() {
        cpos += 1;
        if (cpos > 2) {
            cpos = 0;
        }
    }

    public void selectPrev() {
        cpos -= 1;
        if (cpos < 0) {
            cpos = 2;
        }
    }

    public void setValue(int vall) {
        this.value = vall;
        ColorTheme.setColor(paramName, value);
        list.setColor(paramName, value);
        ColorTheme.saveToStorage();
    }

    public void movePoint() {
        if (dy == 0) {
            return;
        }
        switch (cpos) {
            case 0:
                red = dy + red;
                if (red > 255) {
                    red = 0;
                }
                if (red < 0) {
                    red = 255;
                }
                break;
            case 1:
                green = dy + green;
                if (green > 255) {
                    green = 0;
                }
                if (green < 0) {
                    green = 255;
                }
                break;
            case 2:
                blue = dy + blue;
                if (blue > 255) {
                    blue = 0;
                }
                if (blue < 0) {
                    blue = 255;
                }
                break;
        }
    }

    public void movePointAt(int new_dy) {
        dy = new_dy;
        movePoint();
        dy = 0;
    }

    public void eventOk() {
        String cval = ColorTheme.ColorToString(red, green, blue);
        int finalColor = ColorTheme.getColorInt(cval);
        setValue(finalColor);
    }

    public int getVHeight() {
        return StaticData.getInstance().roster.winHeight;
    }

    public int getVWidth() {
        return StaticData.getInstance().roster.getListWidth();
    }

    public int getColorBGnd() {
        return ColorTheme.getColor(ColorTheme.LIST_BGND);
    }

    public int getColor() {
        return ColorTheme.getColor(ColorTheme.LIST_INK);
    }

    public String getTipString() {
        return null;
    }

    public void onSelect() {
    }

    public boolean isSelectable() {
        return false;
    }
}

//#endif
