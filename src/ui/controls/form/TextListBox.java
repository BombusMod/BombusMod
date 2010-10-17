/*
 * TextListBox.java
 *
 * Created on 25 ��� 2008 �., 16:58
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

import ui.MainBar;
import ui.VirtualList;

import locale.SR;

import Menu.MenuCommand;
import java.util.Enumeration;

/**
 *
 * @author ad
 */
public class TextListBox 
        extends DefForm
    {

    private MenuCommand cmdClear=new MenuCommand(SR.MS_CLEAR, MenuCommand.SCREEN, 2);

    private EditBox ti;    

    public TextListBox(EditBox ti) {
        super(null);
        this.ti=ti;
        SimpleString item = null;
        setMainBarItem(new MainBar(SR.MS_SELECT));
        for (Enumeration e = ti.recentList.elements(); e.hasMoreElements();) {
            item = new SimpleString((String)e.nextElement(), false);
            item.selectable = true;
            itemsList.addElement(item);
        }        
    }
    
    public void commandState() {
        super.commandState();
        addMenuCommand(cmdClear);
    }
    
    public void eventOk() {
        if (itemsList.size()>0)
            ti.setValue(itemsList.elementAt(cursor).toString());        
        destroyView();
    }
    public void destroyView() {
        midlet.BombusMod.getInstance().setDisplayable(ti.t);
    }

    public void menuAction(MenuCommand c, VirtualList d){
        if (c==cmdClear) {
            ti.recentList.removeAllElements();
            ti.saveRecentList();
        }        
        super.menuAction(c, d);
    }

    public void cmdOk() {
        eventOk();
    }

    public String touchLeftCommand() {return SR.MS_MENU; }
    public void touchLeftPressed() { showMenu(); }
}
