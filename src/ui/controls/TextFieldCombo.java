/*
 * TextFieldCombo.java
 *
 * Created on 9.11.2006, 22:41
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

package ui.controls;

import io.NvStorage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.lcdui.*;
import locale.SR;
import java.io.EOFException;

/**
 *
 * @author Evg_S
 */
public class TextFieldCombo 
        extends TextField
        implements ItemCommandListener, CommandListener
{

    private Command cmdRecent;
    private Command cmdBack;
    private Command cmdSelect;
    private Command cmdClear;
    
    private Display display;
    private Displayable parentView;
    private List list;
    private String label;
    
    private Vector recentList;

    private String id;

    /** Creates a new instance of TextFieldCombo 
           *  if value is null, TextField will be filled using recent list
          */
    public TextFieldCombo(String label, String value, int maxlen, int constraints, String id, Display display) {
        super(label, null, maxlen, constraints);
        
        this.display=display;
        this.label=label;
        this.id="mru-"+id;
        
        loadRecentList();
        try {
          if (value==null) value=(String) recentList.elementAt(0);
          setString(value);
        } catch (Exception e) {/* no history available */}
        
        cmdRecent=new Command(SR.MS_RECENT, Command.ITEM, 2);

        addCommand(cmdRecent);
        setItemCommandListener(this);
    }

    public String getString() {
        String result=super.getString();
        int i=0;
        if (result.length()==0) return result;
        while (i<recentList.size()) {
            if ( result.equals((String)recentList.elementAt(i)) || i>9 ) recentList.removeElementAt(i);
            else i++;
        }
        recentList.insertElementAt(result, 0);
        saveRecentList();
        return result;
    }

    public void commandAction(Command command, Item item) {
        if (recentList.isEmpty()) return;
        parentView=display.getCurrent();
        
        cmdBack=new Command(SR.MS_BACK, Command.BACK, 99);
        cmdSelect=new Command(SR.MS_SELECT, Command.OK, 1);
	cmdClear=new Command(SR.MS_CLEAR, Command.SCREEN,2);
        
        list=new List(label, List.IMPLICIT);
        list.addCommand(cmdBack);
		list.addCommand(cmdClear);
        list.setSelectCommand(cmdSelect);
        
        for (Enumeration e=recentList.elements(); e.hasMoreElements();)
            list.append((String)e.nextElement(), null);
        
        list.setCommandListener(this);
        display.setCurrent(list);
    }

    public void commandAction(Command command, Displayable displayable) {
        display.setCurrent(parentView);
        if (command==cmdSelect) {        
            setString( list.getString(list.getSelectedIndex()));
        }
        if (command==cmdClear) {
            recentList.removeAllElements();
            saveRecentList();
        }
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
    
    public static void setLowerCaseLatin(TextField tf) {
        tf.setInitialInputMode("MIDP_LOWERCASE_LATIN");
    }
}
