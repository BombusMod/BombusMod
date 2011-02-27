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
import Colors.ColorTheme;
import java.util.Vector;
import Menu.MenuCommand;
import images.RosterIcons;
import locale.SR;
import ui.VirtualCanvas;
import ui.VirtualElement;
import ui.VirtualList;
import ui.controls.form.DefForm;
//#ifdef CLIPBOARD
//# import util.ClipBoard;
//#endif

public abstract class MessageList extends DefForm
    {
    
    protected final Vector messages;
//#ifdef CLIPBOARD
//#     private ClipBoard clipboard=ClipBoard.getInstance();
//#     
//#     protected MenuCommand cmdCopy = new MenuCommand(SR.MS_COPY, MenuCommand.SCREEN, 20, RosterIcons.ICON_COPY);
//#     protected MenuCommand cmdCopyPlus = new MenuCommand("+ "+SR.MS_COPY, MenuCommand.SCREEN, 30, RosterIcons.ICON_COPYPLUS);
//#endif
    protected MenuCommand cmdxmlSkin = new MenuCommand(SR.MS_USE_COLOR_SCHEME, MenuCommand.SCREEN, 40, RosterIcons.ICON_USESKIN);

    protected MenuCommand cmdUrl = new MenuCommand(SR.MS_GOTO_URL, MenuCommand.SCREEN, 80, RosterIcons.ICON_GOTOURL);
    protected MenuCommand cmdBack = new MenuCommand(SR.MS_BACK, MenuCommand.BACK, 99, RosterIcons.ICON_BACK);

    
    /** Creates a new instance of MessageList */
  
    public MessageList(Vector messages) {
        super("");
        this.messages = messages;
        cf = Config.getInstance();
        
//#ifdef SMILES
        smiles=cf.smiles;
//#else
//#         smiles=false;
//#endif
        enableListWrapping(false);
	
        moveCursorTo(0);//activate
    }    
    
    
    public abstract int getItemCount();

    public VirtualElement getItemRef(int index) {
        if (messages.size() < getItemCount()) {
            messages.setSize(getItemCount());
        }
        MessageItem mi = (MessageItem) messages.elementAt(index);
        if (mi == null) {
            mi = new MessageItem(getMessage(index), smiles);
            mi.setEven((index & 1) == 0);
            if (mi.msgLines.isEmpty()) {
                mi.parse();
            }
            //mi.getColor();
            messages.setElementAt(mi, index);
        }
        return mi;
    }
    
    protected abstract Msg getMessage(int index);
    
    public void markRead(int msgIndex) {}
    
    protected boolean smiles;

    public void menuAction(MenuCommand c, VirtualList d) {
        if (c == cmdBack) {            
            destroyView();
        }
        if (c == cmdUrl) {
            try {
                Vector urls = ((MessageItem) getFocusedObject()).getUrlList();
                new MessageUrl(urls); //throws NullPointerException if no urls
            } catch (Exception e) {/* no urls found */

            }
        }
        if (c == cmdxmlSkin) {
            try {
                if (((MessageItem) getFocusedObject()).msg.body.indexOf("xmlSkin") > -1) {
                    ColorTheme.loadSkin(((MessageItem) getFocusedObject()).msg.body, 2);
                }
            } catch (Exception e) {
            }
        }

//#ifdef CLIPBOARD
//#         if (c == cmdCopy) {
//#             clipboard.set(((MessageItem) getFocusedObject()).msg);
//#         }
//# 
//#         if (c == cmdCopyPlus) {
//#             clipboard.append(((MessageItem) getFocusedObject()).msg);
//# 
//#         }
//#endif
    }
   
    public void commandState() {         
//#ifdef CLIPBOARD
//#         if (cf.useClipBoard) {
//#             addMenuCommand(cmdCopy);
//#             if (!clipboard.isEmpty())
//#                 addMenuCommand(cmdCopyPlus);
//#         }
//#endif
        if (isHasScheme())
            addMenuCommand(cmdxmlSkin);
        if (isHasUrl())
            addMenuCommand(cmdUrl);
    }

    public boolean isHasScheme() {
        if (getItemCount() < 1) {
            return false;
        }
        String body = ((MessageItem) getFocusedObject()).msg.body;

        if (body.indexOf("xmlSkin") > -1) {
            return true;
        }
        return false;
    }

    public boolean isHasUrl() {
        if (getItemCount() < 1) {
            return false;
        }
        String body = ((MessageItem) getFocusedObject()).msg.body;
        if (body.indexOf("http://") > -1) {
            return true;
        }
        if (body.indexOf("https://") > -1) {
            return true;
        }
        if (body.indexOf("ftp://") > -1) {
            return true;
        }
        if (body.indexOf("tel:") > -1) {
            return true;
        }
        if (body.indexOf("native:") > -1) {
            return true;
        }
        return false;
    }
//#ifdef SMILES
    public void userKeyPressed(int key) {
        switch(key) {
            case VirtualCanvas._KEY_STAR:
                if (getItemCount() == 0) return;
                ((MessageItem)getFocusedObject()).toggleSmiles(this);
                return;
        }
        super.userKeyPressed(key);
    }
//#endif   
}
