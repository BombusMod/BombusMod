/*
 * MessageUrl.java
 *
 * Created on 22.12.2005, 3:01
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
 *
 */

package Messages;

import java.util.Vector;
import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.lcdui.Display;
import midlet.BombusMod;
import Menu.Menu;

/**
 *
 * @author EvgS
 */
public class MessageUrl extends Menu{
    
    private Vector urlList;
    
    /** Creates a new instance of MessageUrl */
    public MessageUrl(Display display, Vector urlList) {
	super("URLs", null);
	this.urlList=urlList;
	
	for (int i=0; i<urlList.size(); i++) { // throws exception
	    addItem((String)urlList.elementAt(i), i);
	}
	attachDisplay(display);
    }
    
    public void eventOk() {
        try {
            BombusMod.getInstance().platformRequest((String)urlList.elementAt(cursor));
        } catch (ConnectionNotFoundException ex) {
            ex.printStackTrace();
        }
	destroyView();
    }
}
