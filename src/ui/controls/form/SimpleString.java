/*
 * SimpleString.java
 *
 * Created on 19.05.2008, 22:55
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

import Client.Config;
import Fonts.FontCache;
import javax.microedition.lcdui.Font;
import ui.IconTextElement;

/**
 *
 * @author ad
 */
public class SimpleString 
        extends IconTextElement {

    private String text;
    
    public boolean selectable=false;

    private boolean bold;
    
    private Font font=null;
    
    /**
     * Creates a new instance of SimpleString
     */
    public SimpleString(String text, boolean bold) {
        super(null);
        this.text=text;
        this.bold=bold;
        this.font=FontCache.getFont(bold, FontCache.msg);
    }
    
    public String toString() { return text; }
    
    public boolean isSelectable() { return selectable; }
    
    public Font getFont() { return font; }
}
