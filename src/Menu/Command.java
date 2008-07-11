/*
 * Command.java
 *
 * Created on 9.07.2008, 18:25
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

import ui.IconTextElement;

/**
 *
 * @author ad
 */
public class Command extends IconTextElement {
    String name="";
    int pos;
    int map;
    int img;

    public static int OK        = 1;
    public static int SCREEN    = 2;
    public static int BACK      = 3;
    public static int EXIT      = 4;
    public static int CANCEL    = 5;
    public static int ITEM      = 6;

    public Command(String name, int map, int pos) {
        super(null);
        this.name=name;
        this.map=map;
        this.pos=pos;
    }
    
    public void setImg(int img) {
        this.img=img;
    }
    
    public int getImg() {
        return img;
    }   
    
    public String getName() {
        return name;
    }
}
