/*
 * MessageList.java
 *
 * Created on 11.12.2005, 3:02
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
 *
 */

package Messages;

import Client.Config;
import Client.Msg;
import Client.StaticData;
import java.util.Vector;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import locale.SR;
//#ifdef COLORS
import Colors.ColorUtils;
//#endif
import ui.VirtualElement;
import ui.VirtualList;
import Fonts.FontCache;
//#ifdef CLIPBOARD
//# import util.ClipBoard;
//#endif

public abstract class MessageList extends VirtualList implements CommandListener {
    
    private Config cf;
    
    protected Vector messages;
//#ifdef CLIPBOARD
//#     private ClipBoard clipboard;
//#     
//#     protected Command cmdCopy = new Command(SR.MS_COPY, Command.SCREEN, 20);
//#     protected Command cmdCopyPlus = new Command("+ "+SR.MS_COPY, Command.SCREEN, 30);
//#endif
//#ifdef COLORS
    protected Command cmdxmlSkin = new Command(SR.MS_USE_COLOR_SCHEME, Command.SCREEN, 40);
//#endif
    protected Command cmdUrl = new Command(SR.MS_GOTO_URL, Command.SCREEN, 80);
    protected Command cmdBack = new Command(SR.MS_BACK, Command.BACK, 99);
    
    /** Creates a new instance of MessageList */
  
    public MessageList() {
        super();
	messages=new Vector();
        
        cf=Config.getInstance();
        
//#ifdef SMILES
        smiles=cf.smiles;
//#else
//#         smiles=false;
//#endif
//#ifdef CLIPBOARD
//#         if (cf.useClipBoard) {
//#             clipboard=ClipBoard.getInstance();
//#             addCommand(cmdCopy);
//#             addCommand(cmdCopyPlus);
//#         }
//#endif
        enableListWrapping(false);
	
        cursor=0;//activate

//#ifdef COLORS
        addCommand(cmdxmlSkin);
//#endif
        addCommand(cmdUrl);
        addCommand(cmdBack);
    }

    public MessageList(Display display) {
        this();
        attachDisplay(display);
    }
    
    
    public abstract int getItemCount(); // из protected �?делали public

    protected VirtualElement getItemRef(int index) {
	if (messages.size()<getItemCount()) messages.setSize(getItemCount());
	MessageItem mi=(MessageItem) messages.elementAt(index);
	if (mi==null) {
	    mi=new MessageItem(getMessage(index), this, smiles);
            mi.setEven( (index & 1) == 0);
            mi.getColor();
	    messages.setElementAt(mi, index);
	}
        return mi;
    }
    
    public abstract Msg getMessage(int index);
    
    public void markRead(int msgIndex) {}
    
    protected boolean smiles;

    public void removeCommands () {
//#ifdef CLIPBOARD
//#         if (cf.useClipBoard) {
//#             removeCommand(cmdCopy);
//#             removeCommand(cmdCopyPlus);
//#         }
//#endif
//#ifdef COLORS
        removeCommand(cmdxmlSkin);
//#endif
        removeCommand(cmdUrl);
        removeCommand(cmdBack);
    }

    public void commandAction(Command c, Displayable d) {
        if (c==cmdBack) {
            StaticData.getInstance().roster.activeContact=null;
            destroyView();
        }
        if (c==cmdUrl) {
            try {
                Vector urls=((MessageItem) getFocusedObject()).getUrlList();
                new MessageUrl(display, urls); //throws NullPointerException if no urls
            } catch (Exception e) {/* no urls found */}
        }
//#ifdef COLORS
        if (c==cmdxmlSkin) {
            try {
                if (((MessageItem)getFocusedObject()).msg.getBody().startsWith("xmlSkin")) {
                   ColorUtils.loadSkin(((MessageItem)getFocusedObject()).msg.getBody(),2);
                }
            } catch (Exception e){}
        }
//#endif
//#ifdef CLIPBOARD
//#         if (c == cmdCopy)
//#         {
//#             try {
//#                 StringBuffer clipstr=new StringBuffer();
//#                 clipstr.append((((MessageItem)getFocusedObject()).msg.getSubject()==null)?"":((MessageItem)getFocusedObject()).msg.getSubject()+"\n");
//#                 clipstr.append(((MessageItem)getFocusedObject()).msg.quoteString());
//#                 clipboard.setClipBoard(clipstr.toString());
//#                 clipstr=null;
//#             } catch (Exception e) {/*no messages*/}
//#         }
//#         
//#         if (c==cmdCopyPlus) {
//#             try {
//#                 StringBuffer clipstr=new StringBuffer();
//#                 clipstr.append(clipboard.getClipBoard());
//#                 clipstr.append("\n\n");
//#                 clipstr.append((((MessageItem)getFocusedObject()).msg.getSubject()==null)?"":((MessageItem)getFocusedObject()).msg.getSubject()+"\n");
//#                 clipstr.append(((MessageItem)getFocusedObject()).msg.quoteString());
//#                 
//#                 clipboard.setClipBoard(clipstr.toString());
//#                 clipstr=null;
//#             } catch (Exception e) {/*no messages*/}
//#         }
//#endif
    }
//#ifdef SMILES
    protected void keyPressed(int keyCode) { // overriding this method to avoid autorepeat
        if (keyCode=='*') {
            try {
                ((MessageItem)getFocusedObject()).toggleSmiles();
            } catch (Exception e){}
            System.gc();
            return;
        }
        super.keyPressed(keyCode);
    }
//#endif
    public void keyGreen() { eventOk(); }
   
}
