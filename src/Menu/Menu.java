/*
 * Menu.java
 *
 * Created on 1.05.2005, 20:48
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
import Client.Config;
import java.util.*;
import locale.SR;
import ui.*;

/**
 *
 * @author Evg_S
 */
public class Menu extends VirtualList
{
    Vector menuitems;
    private ImageList il;
    private boolean executeByNum;

    public Menu(Object mainbar, ImageList il) {
        super();
        setMainBarItem(new MainBar(mainbar));
        menuitems=new Vector();
        this.il=il;
        executeByNum=Config.getInstance().executeByNum;
    }
    
    public VirtualElement getItemRef(int index){
        if (index >= menuitems.size()) return null;
        return (VirtualElement)menuitems.elementAt(index); 
    }
    public int getItemCount() { return menuitems.size(); }
    
    public void addItem(MenuItem mi){
        mi.pos=getItemCount();
        menuitems.addElement(mi);
    }
    
    public void addItem(String label, int index, int iconIndex){
        addItem(new MenuItem(label, index, iconIndex, il));
    }
    
    public void addItem(String label, int index){
        addItem(new MenuItem(label, index, -1, il));
    }
    
    public String touchLeftCommand() { return SR.MS_SELECT; }
    public String touchRightCommand() { return cf.swapMenu ? "" : SR.MS_BACK; }

    public void touchLeftPressed() {
        eventOk();
    }
    public void touchRightPressed() {
        if (!cf.swapMenu)
            cmdCancel();
    }
    
    public void commandState() {}
    
    
    public void captionPressed() {}

    public void additionalKey(int keyCode) {
        if (executeByNum && getItemCount() > 0 && keyCode >=0 && keyCode <= 9) {
            executeCommand(keyCode);
            return;
        }
        super.additionalKey(keyCode);
    }

    private void executeCommand(int index) {
        moveCursorTo(index == 0 ? 9 : index - 1);
        eventOk();
    }
}
