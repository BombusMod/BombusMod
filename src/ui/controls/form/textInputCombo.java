/*
 * textInputCombo.java
 *
 * Created on 22.05.2008, 15:36
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

import Client.Config;
import javax.microedition.lcdui.Display;
/*
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
*/

/**
 *
 * @author ad
 */
public class textInputCombo
    extends textInput
    //implements ItemCommandListener 
    {

    private String id;
/*
    private Vector recentList;
    private Command cmdRecent;
*/
    /** Creates a new instance of textInputCombo */
    public textInputCombo(Display display, String text, String id) {
        super(display, text);
        
        this.id="mru-"+id;
/*      
        loadRecentList();
        try {
          if (text==null) text=(String) recentList.elementAt(0);
          setValue(text);
        } catch (Exception e) { }


        cmdRecent=new Command(SR.MS_RECENT, Command.ITEM, 2);
        this.addCommand(cmdRecent);
*/
    }
/*
    public void commandAction(Command command, Item item) {

    }

    private void saveRecentList() {
        DataOutputStream os=NvStorage.CreateDataOutputStream();
        try {
            for (Enumeration e=recentList.elements(); e.hasMoreElements(); ) {
                String s=(String)e.nextElement();
                os.writeUTF(s);
            }
        } catch (Exception e) { }
        NvStorage.writeFileRecord(os, id, 0, true);
    }
    private void loadRecentList() {
        recentList=new Vector(10);
        try {
            DataInputStream is=NvStorage.ReadFileRecord(id, 0);
            try { 
                while (true) recentList.addElement(is.readUTF());
            } catch (EOFException e) { is.close(); }
        } catch (Exception e) { }
    }
*/
}
