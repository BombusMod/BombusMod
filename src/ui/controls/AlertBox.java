/*
 * AlertBox.java
 *
 * Created on 17.05.2008, 14:35
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

import javax.microedition.lcdui.Graphics;
import ui.controls.form.DefForm;
import ui.controls.form.MultiLine;
/**
 *
 * @author ad
 */
public abstract class AlertBox extends DefForm {   
    
    
    public AlertBox(String mainbar, String text) {
        super(mainbar);
        MultiLine lines = new MultiLine(null, text);
        lines.selectable = false;
        itemsList.addElement(lines);
        show(parentView);
    }
    
    public void cmdOk() {
        destroyView();
        yes();        
    }
    public void cmdCancel() {
        destroyView();
        no();        
    }
    public void drawCursor(Graphics g, int width, int height) {
        // prevent text selection
    };
    
    public abstract void yes();

    public abstract void no();    
}

