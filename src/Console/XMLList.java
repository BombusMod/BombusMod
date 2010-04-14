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

import Client.Config;
import Client.Msg;
import Client.StaticData;
import Messages.MessageList;
import java.util.Vector;
//#ifndef MENU_LISTENER
//# import javax.microedition.lcdui.Command;
//#else
import Menu.Command;
//#endif
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import locale.SR;
//#ifdef CLIPBOARD   
//# import util.ClipBoard;
//#endif
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
    private StaticData sd=StaticData.getInstance();
    
    private Command cmdNew=new Command(SR.MS_NEW, Command.SCREEN, 5);
    private Command cmdEnableDisable=new Command(SR.MS_ENABLE_DISABLE, Command.SCREEN, 6);
    private Command cmdPurge=new Command(SR.MS_CLEAR_LIST, Command.SCREEN, 10);
    
//#ifdef CLIPBOARD    
//#     private ClipBoard clipboard=ClipBoard.getInstance();
//#endif
    
    /** Creates a new instance of XMLList */
    public XMLList(Display display, Displayable pView) {
        super ();
        
        super.smiles=false;
        
        stanzas=StanzasList.getInstance();
        
        commandState();
        addCommands();
        setCommandListener(this);

        moveCursorHome();

//#ifdef CONSOLE        
//# 	MainBar mainbar=new MainBar(SR.MS_XML_CONSOLE);
//#         setMainBarItem(mainbar);
//#endif
        attachDisplay(display);
        this.parentView=pView;
    }
    
    public void commandState() {
//#ifdef MENU_LISTENER
        menuCommands.removeAllElements();
//#endif
	addCommand(cmdBack);
        addCommand(cmdNew);
//#ifdef CLIPBOARD
//#             if (Config.getInstance().useClipBoard) {
//#                 addCommand(cmdCopy);
//#                 if (!clipboard.isEmpty()) addCommand(cmdCopyPlus);
//#             }
//#endif
        addCommand(cmdEnableDisable);
        addCommand(cmdPurge);
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
        return stanzas.size();
    }
    
    public Msg getMessage(int index) {
        Msg msg=new Msg(Msg.MESSAGE_TYPE_OUT, "local", null, null);
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
        new StanzaEdit(display, this, stanza).setParentView(this);
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

        if (c==cmdPurge) { 
            clearReadedMessageList();
        }
    }
    
    private void clearReadedMessageList() {
        try {
            if (cursor+1==stanzas.size()) {
                stanzas.stanzas.removeAllElements();
                messages=null;
            }
            else {
                for (int i=0; i<cursor+1; i++)
                    stanzas.stanzas.removeElementAt(0);
            }
            messages=new Vector();
        } catch (Exception e) { }
        moveCursorHome();
        redraw(); 
    }
    
    public void keyClear() { 
        clearReadedMessageList();
    }
    
    public void userKeyPressed(int keyCode) {
        if (keyCode=='0')
            clearReadedMessageList();
    }
    
    public void destroyView(){
	super.destroyView();
    }
}
