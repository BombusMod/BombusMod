/*
 * AlertBox.java
 *
 * Copyright (c) 2005-2007, Eugene Stahov (evgs), http://bombus-im.org
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

package ui;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Image;
import locale.SR;

/**
 *
 * @author Evg_S
 */
public class AlertBox implements CommandListener{
    
    protected Display display;
    protected Displayable next;
    protected Alert alert;
    protected Command cmdOk=new Command(SR.MS_OK, Command.OK, 1);
    /** Creates a new instance of AlertBox */
    public AlertBox(String mainbar, String text, Image image, Display display, Displayable nextDisplayable) {
        alert=new Alert(mainbar, text, image, null);
        this.display=display;
        next=(nextDisplayable==null)? display.getCurrent() : nextDisplayable;
        
        alert.setTimeout(15000); //15 seconds
        alert.addCommand(cmdOk);
        alert.setCommandListener(this);
        display.setCurrent(alert);
    }

    public void commandAction(Command command, Displayable displayable) {
        display.setCurrent(next);
    }
}
