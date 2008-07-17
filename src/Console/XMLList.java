/*
 * XMLList.java
 *
 * Created on 7.04.2008, 13:37
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

package Console;

import Client.Msg;
import Client.StaticData;
import Messages.MessageList;
import java.util.Vector;
//#ifndef MENU_LISTENER
import javax.microedition.lcdui.Command;
//#else
//# import Menu.Command;
//#endif
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import locale.SR;
import ui.MainBar;
//import ui.YesNoAlert;

/**
 *
 * @author ad
 */
public class XMLList 
    extends MessageList {
    
    StanzasList stanzas;
    private StaticData sd=StaticData.getInstance();
    
    private Command cmdNew=new Command(SR.MS_NEW, Command.SCREEN, 5);
    private Command cmdEnableDisable=new Command("Enable/Disable", Command.SCREEN, 6);
    private Command cmdDeleteAll=new Command(SR.MS_CLEAR_LIST, Command.SCREEN, 10);    
    /** Creates a new instance of XMLList */
    public XMLList(Display display) {
        super ();
        
        stanzas=StanzasList.getInstance();
        commandState();
        setCommandListener(this);

        attachDisplay(display);
        
        try {
            focusedItem(0);
        } catch (Exception e) {}
//#ifdef CONSOLE        
//# 	MainBar mainbar=new MainBar(SR.MS_XML_CONSOLE);
//#         setMainBarItem(mainbar);
//#endif
    }
    
    public void commandState() {
//#ifdef MENU_LISTENER
//#         menuCommands.removeAllElements();
//#endif
	addCommand(cmdBack);
        addCommand(cmdNew);
        addCommand(cmdEnableDisable);
        addCommand(cmdDeleteAll);
    }
    
    protected void beginPaint() {
        StringBuffer str = new StringBuffer()
        .append(" (")
        .append(getItemCount())
        .append(")");
        
        if (!stanzas.enabled)
            str.append(" - Disabled");
        
        getMainBarItem().setElementAt(str.toString(),1);
    }
    

    public int getItemCount() {
        return stanzas.size();
    }
    
    public Msg getMessage(int index) {
        Msg msg=new Msg(Msg.MESSAGE_TYPE_OUT,"local",null,null);
        try {
            msg=stanzas.msg(index);
        } catch (Exception e) {}
	return msg;
    }

    public void keyGreen(){
	Msg m=getMessage(cursor);
        String stanza = "";
        try {
            stanza =  m.toString();
        } catch (Exception e) {}
        new StanzaEdit(display, stanza).setParentView(this);
    }
    
    public void commandAction(Command c, Displayable d) {
        super.commandAction(c,d);
        
	Msg m=getMessage(cursor);
        if (c==cmdNew) {
            keyGreen();
        }
        if (c==cmdEnableDisable) {
            stanzas.enabled=!stanzas.enabled;
            redraw();
        }
	if (m==null) return;

        if (c==cmdDeleteAll) { 
            deleteAllMessages();
        }
    }
    
    private void deleteAllMessages() {
        if (getItemCount()>0) {
            stanzas.deleteAll();
            messages=null;
            messages=new Vector();
        }
        redraw(); 
    }

    public void keyClear() { 
        deleteAllMessages();
    }
    
    public void destroyView(){
	super.destroyView();
    }
}
