/*
 * MenuItem.java
 *
 * Created on 2.04. 2005, 13:22
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
//#ifdef MENU_LISTENER
//# import Client.Config;
//# import Fonts.FontCache;
//# import javax.microedition.lcdui.Graphics;
//#endif
import ui.*;

/**
 *
 * @author Eugene Stahov
 */
public class MenuItem
    extends IconTextElement {

    /** Creates a new instance of MenuItem */
    public int index;
    private String name;
    public int pos;
    private int iconIndex;
    
    public MenuItem(String name, int index, int iconIndex, ImageList il) {
        super(il);
        this.index=index;
	this.name=name;
        this.iconIndex=iconIndex;
    }

    public int getImageIndex() { return iconIndex;  }
    public String toString(){ return name; }
    
//#ifdef MENU_LISTENER
//#     public void drawItem(Graphics g, int ofs, boolean sel) {
//#         if (pos<10 && Config.getInstance().executeByNum) {
//#             int w=g.getClipWidth();
//#             g.setFont(FontCache.getSmallFont());
//#             g.drawString(Integer.toString((pos<9)?pos+1:0), w, 0, Graphics.TOP|Graphics.RIGHT);
//#         }
//#         
//#         super.drawItem(g, ofs, sel);
//#     }
//#endif
}
