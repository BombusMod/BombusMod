/*
 * XMLList.java
 *
 * Created on 7.04.2008, 13:37
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

package Console;

import Client.Msg;
import Messages.MessageList;
import java.util.Vector;
import Menu.MenuCommand;
import locale.SR;
import ui.VirtualList;
//#ifdef CONSOLE 
//# import ui.MainBar;
//#endif

/**
 *
 * @author ad
 */
public final class XMLList
    extends MessageList {
//#ifdef PLUGINS
//#     public static String plugin = "PLUGIN_CONSOLE";
//#endif
    
    StanzasList stanzas;

    private MenuCommand cmdNew=new MenuCommand(SR.MS_NEW, MenuCommand.OK, 1);
    private MenuCommand cmdEnableDisable=new MenuCommand(SR.MS_ENABLE_DISABLE, MenuCommand.SCREEN, 6);
    private MenuCommand cmdPurge=new MenuCommand(SR.MS_CLEAR_LIST, MenuCommand.SCREEN, 10);
   
   
    /** Creates a new instance of XMLList
     */
    public XMLList() {
        super (new Vector());
                       
        super.smiles = false;       
        stanzas = StanzasList.getInstance();
        moveCursorHome();

//#ifdef CONSOLE        
//# 	MainBar mb=new MainBar(SR.MS_XML_CONSOLE);
//#         setMainBarItem(mb);
//#endif             
    }
    
    public void commandState() {
        menuCommands.removeAllElements();
        addMenuCommand(cmdNew);
        addMenuCommand(cmdEnableDisable);
        addMenuCommand(cmdPurge);
        super.commandState();
    }
 
    protected void beginPaint() {
        StringBuffer str = new StringBuffer(" (")
        .append(getItemCount())
        .append(")");
        
        if (!stanzas.enabled)
            str.append(" - Disabled");
        
        getMainBarItem().setElementAt(str.toString(),1);
    }
    

    public int getItemCount() {
        return stanzas == null ? 0 : stanzas.size();
    }
    
    public Msg getMessage(int index) {        
        return stanzas.msg(index);
    }

    public void stanzaEdit() {
        Msg m = getMessage(cursor);
        new StanzaEdit(this, m == null? "" : m.toString());
    }
    
    public void menuAction(MenuCommand c, VirtualList d) {
        super.menuAction(c, d);

        Msg m = getMessage(cursor);
        if (c == cmdNew) {
            stanzaEdit();
        }
        if (c == cmdEnableDisable) {
            toggle();
        }
        if (m == null) {
            return;
        }

        if (c == cmdPurge) {
            clearReadedMessageList();
        }
    }
    public boolean longKey(int key) {
        switch(key) {
            case 0:
                clearReadedMessageList();
                return true;
        }
        return super.longKey(key);
    }
    public void clearReadedMessageList() {
        try {
            if (cursor+1==StanzasList.getInstance().size()) {
                StanzasList.getInstance().stanzas.removeAllElements();
                messages.removeAllElements();
            }
            else {
                for (int i=0; i<cursor+1; i++)
                    StanzasList.getInstance().stanzas.removeElementAt(0);
            }
            messages.removeAllElements();
            moveCursorHome();
        } catch (Exception e) { }        
        redraw(); 
    }
    public void toggle() {
        StanzasList.getInstance().enabled = !StanzasList.getInstance().enabled;
        redraw();
    }

    public void keyClear() {
        clearReadedMessageList();
    }

    public void keyGreen() {
        toggle();
    }    
}
