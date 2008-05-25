/*
 * TextListBox.java
 *
 * Created on 25 Май 2008 г., 16:58
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

import java.util.Vector;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import locale.SR;
import ui.MainBar;
import ui.VirtualElement;
import ui.VirtualList;

import io.NvStorage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemCommandListener;
import locale.SR;

/**
 *
 * @author ad
 */
public class TextListBox 
        extends VirtualList 
        implements CommandListener {
    
    private Command cmdCancel=new Command(SR.MS_CANCEL, Command.BACK,99);
    private Command cmdOk=new Command(SR.MS_OK, Command.OK,1);

    private Vector recentList;

    private EditBox ti;

    public TextListBox(Display display, EditBox ti) {
        super(display);
        this.ti=ti;
        this.recentList=ti.recentList;
        setMainBarItem(new MainBar(SR.MS_SELECT));
        addCommand(cmdOk);
        addCommand(cmdCancel);
        setCommandListener(this);
    }
    
    public void eventOk() {
        if (recentList.size()>0)
            ti.setValue((String) recentList.elementAt(cursor));
        
        display.setCurrent(parentView);
    }

    public void commandAction(Command c, Displayable d){
        if (c==cmdOk)
            eventOk();
        else if (c==cmdCancel)
            display.setCurrent(parentView);
    }

    public VirtualElement getItemRef(int index){ 
        return new ListItem((String) recentList.elementAt(index)); 
    }
    public int getItemCount() { return recentList.size(); }
}
