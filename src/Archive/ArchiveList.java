/*
 * ArchiveList.java
 *
 * Created on 11.12.2005, 5:24
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

package Archive;

import Client.Msg;
import javax.microedition.lcdui.TextBox;
import ui.MainBar;
import Messages.MessageList;
import java.util.Vector;
import Menu.MenuCommand;
import locale.SR;
import ui.VirtualList;
import ui.controls.AlertBox;

/**
 *
 * @author EvgS
 */
public class ArchiveList 
    extends MessageList {
//#ifdef PLUGINS
//#     public static String plugin = new String("PLUGIN_ARCHIVE");
//#endif
    
    MenuCommand cmdPaste=new MenuCommand(SR.MS_PASTE_BODY, MenuCommand.SCREEN, 1);
    MenuCommand cmdJid=new MenuCommand(SR.MS_PASTE_JID /*"Paste Jid"*/, MenuCommand.SCREEN, 2);
    MenuCommand cmdSubj=new MenuCommand(SR.MS_PASTE_SUBJECT, MenuCommand.SCREEN, 3);
    MenuCommand cmdEdit=new MenuCommand(SR.MS_EDIT, MenuCommand.SCREEN, 4);
    MenuCommand cmdNew=new MenuCommand(SR.MS_NEW, MenuCommand.SCREEN, 5);
    MenuCommand cmdDelete=new MenuCommand(SR.MS_DELETE, MenuCommand.SCREEN, 9);
    MenuCommand cmdDeleteAll=new MenuCommand(SR.MS_DELETE_ALL, MenuCommand.SCREEN, 10);

    MessageArchive archive;
   
    private int caretPos;
    
    private int where=1;

    private TextBox t;
    
    /** Creates a new instance of ArchiveList
     * @param caretPos
     * @param where
     * @param t
     */
    public ArchiveList(int caretPos, int where, TextBox t) {
 	super ();
        this.where = where;
        this.caretPos = caretPos;
        this.t = t;     
        archive = new MessageArchive(where);                
	MainBar mb=new MainBar((where==1)?SR.MS_ARCHIVE:SR.MS_TEMPLATE);
	mb.addElement(null);
	mb.addRAlign();
	mb.addElement(null);
	mb.addElement(SR.MS_FREE /*"free "*/);
        setMainBarItem(mb);      
        show();
    }

    public final void commandState() {
        super.commandState();
        
        if (getItemCount()>0) {
            if (t!=null) {
                addMenuCommand(cmdPaste);
                addMenuCommand(cmdJid);
                addMenuCommand(cmdSubj);
            }
            addMenuCommand(cmdEdit);
            
            addMenuCommand(cmdDelete);
            addMenuCommand(cmdDeleteAll);
        }
        addMenuCommand(cmdNew);
    }

    protected void beginPaint() {
        getMainBarItem().setElementAt(" ("+getItemCount()+")",1);
	getMainBarItem().setElementAt(String.valueOf(getFreeSpace()),3);
    }
    
    public int getItemCount() {
	return archive.size();
    }
    
    protected Msg getMessage(int index) {
	return archive.msg(index);
    }

    public void menuAction(MenuCommand c, VirtualList d) {
        super.menuAction(c,d);
        
	Msg m=getMessage(cursor);
        if (c==cmdNew) { new archiveEdit(this, -1, where, this); }
	if (m==null) return;
        
	if (c==cmdDelete) { keyClear(); }
        if (c==cmdDeleteAll) { deleteAllMessages(); redraw(); }
	if (c==cmdPaste) { pasteData(0); }
	if (c==cmdSubj) { pasteData(1); }
	if (c==cmdJid) { pasteData(2); }
        if (c==cmdEdit) {
            try {
                new archiveEdit(this, cursor, where, this);
            } catch (Exception e) {/*no messages*/}
        }
    }
    
    public void reFresh() {
        archive=new MessageArchive(where);
        messages=null;
        messages=new Vector();
    }

    private void deleteMessage() {
        archive.delete(cursor);
        messages=null;
        messages=new Vector();
        if (cursor>0)
            cursor--;
        setRotator();
        redraw();
    }
    
    private void deleteAllMessages() {
        new AlertBox(SR.MS_ACTION, SR.MS_DELETE_ALL+"?") {
            public void yes() {
                archive.deleteAll();
                messages=null;
                messages=new Vector();
            }
            public void no() { }
        };
    }
    
    private void pasteData(int field) {
	if (t==null) return;
	Msg m=getMessage(cursor);
	if (m==null) return;
	String data;
	switch (field) {
	case 1: 
	    data=m.subject;
	    break;
	case 2: 
	    data=m.from;
	    break;
	default:
	    data=m.quoteString();
	}
	t.insert(data, caretPos);
	destroyView();
    }
    
    public void keyGreen() { pasteData(0); }
    
    public void keyClear() { 
        if (getItemCount()>0) {
            new AlertBox(SR.MS_DELETE, SR.MS_SURE_DELETE) {
                public void yes() {
                    deleteMessage();
                }
                public void no() { }
            };
            redraw();
        }
    }

    public void destroyView(){
        archive.close();
        if (t != null)
            midlet.BombusMod.getInstance().setDisplayable(t);
        else 
            super.destroyView();
    }

    private int getFreeSpace() {
        return archive.freeSpace();
    }
}
