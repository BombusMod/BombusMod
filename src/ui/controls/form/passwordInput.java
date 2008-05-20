/*
 * passwordInput.java
 *
 * Created on 20.05.2008, 11:53
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

import javax.microedition.lcdui.Display;

/**
 *
 * @author ad
 */
public class passwordInput
    extends textInput {
    
    /** Creates a new instance of passwordInput */
    public passwordInput(Display display, String text) {
        super(display, text);
    }
    
    public String toString(){
        StringBuffer str=new StringBuffer();
        for (int i=0;i<getValue().length();i++)
            str.append("*");
        
        return str.toString();
    }
}
