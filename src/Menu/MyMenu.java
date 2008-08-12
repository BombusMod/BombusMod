/*
 * MyMenu.java
 *
 * Created on 9.07.2008, 18:17
 *
 * Copyright (c) 2005-2008, Eugene Stahov (evgs), http://bombus-im.org
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

package Menu;

import java.util.Enumeration;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import ui.ImageList;

/**
 *
 * @author ad
 */
public class MyMenu extends Menu {
    
    private MenuListener ml;
    private Object o;

    
    /** Creates a new instance of MyMenu */
    public MyMenu(Display display, Displayable parentView, MenuListener menuListener, String caption, ImageList il) {
        super(caption, il);
        this.ml=menuListener;

        this.parentView=parentView;
        
        for (int i=0; i<ml.menuCommands.size(); i++) {
            Command c=(Command)ml.menuCommands.elementAt(i);
            addItem(c.getName(), i, c.getImg());
        }

        attachDisplay(display);
    }
    
    public void eventOk(){
	destroyView();
        MenuItem me=(MenuItem) getFocusedObject();
        
	if (me==null)  return;

        ml.commandAction(getCommand(me.index), parentView);
    }
    
    private Command getCommand(int index) {
        for (Enumeration command=ml.menuCommands.elements(); command.hasMoreElements();) {
            Command cmd =(Command)command.nextElement();
            if (cmd.getName().equals(getFocusedObject().toString()))
                return cmd;
        }
        return null;
    }
}
