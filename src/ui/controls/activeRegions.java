/*
 * activeRegions.java
 * Copyright (c) 2006-2007, Daniel Apatin (ad), http://apatin.net.ru
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

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import ui.*;
import ui.ColorScheme;
import ui.Time;
import ui.VirtualList;

public class activeRegions {
    static int cHeight = 0;
    static int cWidth = 0;   
    public activeRegions() { }
    
    public void init(int width, int height, int lineHeight) {
        this.cWidth=width/2;
        this.cHeight=height-lineHeight;
    }
    
    public int pointerPressed(int x, int y, VirtualList v) {
        if (x<cWidth && y>cHeight)//System.out.println("isClickOnLeftCommand");
            return 1;        
        if (x>cWidth && y>cHeight)//System.out.println("isClickOnRightCommand");
            return 2;
	return 0;
    }
}

