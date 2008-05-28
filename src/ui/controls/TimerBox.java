/*
 * TimerBox.java
 *
 * Created on 17.05.2008, 14:37
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

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;

/**
 *
 * @author ad
 */
public abstract class TimerBox extends AlertBox implements Runnable {
    
    private int timeout;

    public TimerBox(String mainbar, String text, int timeout, Display display, Displayable nextDisplayable) {
        super(mainbar, text, display, nextDisplayable);
        
        this.timeout=timeout;
        super.isShowing=true;
        super.steps=timeout;
        new Thread(this).start();
    }
    
    public void run() {
        while (super.isShowing) {
            try {
                Thread.sleep(1000);
            } catch (Exception e) { break; }

            super.pos+=1;
            
            if (super.pos>=timeout) {
                break;
            }
            repaint();
        }
        yes();
        super.destroyView();
    }

    public abstract void yes();

    public abstract void no();
}
