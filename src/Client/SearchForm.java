/*
 * SearchForm.java
 *
 * Created on 03.10.2008, 19:34
 * Copyright (c) 2006-2008, Daniel Apatin (ad), http://apatin.net.ru
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

package Client;

import io.NvStorage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextField;
import locale.SR;
import ui.MIDPTextBox;
import ui.MainBar;
import ui.VirtualElement;
import ui.controls.form.ListItem;
import util.StringLoader;

//#ifdef SERVICE_DISCOVERY
import ServiceDiscovery.*;
//#endif
//#ifndef MENU_LISTENER
//# import javax.microedition.lcdui.Command;
//#else
import Menu.Command;
//#endif
import ui.controls.form.DefForm;

/**
 *
 * @author ad
 */
public class SearchForm
        extends DefForm
        implements MIDPTextBox.TextBoxNotify
    { 
    
    private Command cmdAddServer = new Command(SR.MS_ADD, Command.SCREEN, 2);
    private Command cmdDel=new Command (SR.MS_DELETE, Command.SCREEN, 3);
    
    Vector servers = new Vector();
    
    /**
     * Creates a new instance of SearchForm
     */
    public SearchForm(Display display, Displayable pView) {
        super(display, pView, SR.MS_SEARCH);
        this.display=display;
        loadRecentList();

        if (getItemCount()<1) loadDefaults();

        updateMainBar();
//#ifndef MENU_LISTENER
//#         addCommand(cmdOk);
//#         addCommand(cmdAddServer);
//#         addCommand(cmdDel);
//#         addCommand(cmdCancel);
//#endif
        setCommandListener(this);
        attachDisplay(display);
        this.parentView=pView;
    }
    
    private void updateMainBar() {
        setMainBarItem(new MainBar(2, null, SR.MS_USERS_SEARCH+" ("+getItemCount()+") ", false));
    }

//#ifdef MENU_LISTENER
    public void commandState() {
        menuCommands.removeAllElements();
        addCommand(cmdAddServer);
        addCommand(cmdDel);
    }

    public String touchLeftCommand(){ return SR.MS_MENU; }
    public void touchLeftPressed(){
        showMenu();
    }
//#endif
    
    public void commandAction(Command c, Displayable d) {
        super.commandAction(c, d);     
     if (c==cmdAddServer) {
            new MIDPTextBox(display, this, SR.MS_SERVER, null, this, TextField.ANY);
	} else if (c==cmdDel) {
            delServer();
        }
    }
    
    public void OkNotify(String server) {
        addServer(server);
    }    
    
    private void loadDefaults() {
	Vector defs[]=new StringLoader().stringLoader("/def_search.txt", 1);
        for (int i=0; i<defs[0].size(); i++) {
            String server   =(String) defs[0].elementAt(i);
            servers.addElement(server);
        }
        defs=null;
    }
    
    private void loadRecentList() {
        servers=new Vector();
        try {
            DataInputStream is=NvStorage.ReadFileRecord("search_servers", 0);

            try { 
                while (true) servers.addElement(is.readUTF());
            } catch (EOFException e) { is.close(); is=null; }
        } catch (Exception e) { }
    }
    
    public void saveRecentList() {
        DataOutputStream os=NvStorage.CreateDataOutputStream();
        try {
            for (Enumeration e=servers.elements(); e.hasMoreElements(); ) {
                String s=(String)e.nextElement();
                os.writeUTF(s);
            }
        } catch (Exception e) { }

        NvStorage.writeFileRecord(os, "search_servers", 0, true);
    }
    
    private void addServer(String server) {
        servers.addElement(server);
        saveRecentList();
        
        updateMainBar();
    }
    
    private void delServer(){
        servers.removeElementAt(cursor);
        if (getItemCount()<=cursor) moveCursorEnd();
        
        saveRecentList();
        
        updateMainBar();
        
        redraw();
    }

    public void cmdOk() {
        eventOk();
    }

    public void eventOk(){
//#ifdef SERVICE_DISCOVERY
        if (getItemCount()==0)
            return;

        ListItem join=(ListItem)getFocusedObject();
        new ServiceDiscovery(display, join.toString(), null, true);
//#endif
    }
    
    protected int getItemCount() {
        return (servers==null)?0:servers.size();
    }

    protected VirtualElement getItemRef(int index) {
        return new ListItem((String) servers.elementAt(index)); 
    }   
}

