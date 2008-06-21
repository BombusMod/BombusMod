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
import java.util.*;
import javax.microedition.lcdui.*;
import locale.SR;
import ui.*;

/**
 *
 * @author Evg_S
 */
public class Menu extends VirtualList
//#ifndef MENU
        implements CommandListener
//#endif
{
    Vector menuitems;
//#ifndef MENU
    Command cmdBack=new Command(SR.MS_BACK,Command.BACK,99);
    Command cmdOk=new Command(SR.MS_OK,Command.OK,1);
//#endif
    private ImageList il;
    
    public Menu(String mainbar, ImageList il) {
        super();
        setMainBarItem(new MainBar(mainbar));
        menuitems=new Vector();
        this.il=il;
//#ifndef MENU
        addCommand(cmdBack);
        addCommand(cmdOk);
        setCommandListener(this);
//#endif
    }
    
    public VirtualElement getItemRef(int index){ 
        return (VirtualElement)menuitems.elementAt(index); 
    }
    public int getItemCount() { return menuitems.size(); }
    
    public void addItem(MenuItem mi){
        menuitems.addElement(mi);
    }
    
    public void addItem(String label, int index, int iconIndex){
        addItem(new MenuItem(label, index, iconIndex, il));
    }
    
    public void addItem(String label, int index){
        addItem(new MenuItem(label, index, -1, il));
    }
//#ifndef MENU
    public void commandAction(Command c, Displayable d) {
        if (c==cmdBack) destroyView();
	if (c==cmdOk) eventOk();
    }
//#else 
//#     public void leftCommand() { eventOk(); }
//#     public String getLeftCommand() { return SR.MS_OK; }
//#endif
}
