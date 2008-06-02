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

package archive;

import Client.MessageEdit;
import Client.Msg;
import Client.StaticData;
import java.util.Enumeration;
import ui.MainBar;
import Messages.MessageList;
import java.util.Vector;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import locale.SR;
//#if FILE_IO
import io.file.FileIO;
import io.file.browse.Browser;
import io.file.browse.BrowserListener;
import util.strconv;
//#endif
import ui.controls.AlertBox;
import Client.Config;
import ui.Time;

/**
 *
 * @author EvgS
 */
public class ArchiveList 
    extends MessageList {

    Command cmdPaste=new Command(SR.MS_PASTE_BODY, Command.SCREEN, 1);
    Command cmdJid=new Command(SR.MS_PASTE_JID /*"Paste Jid"*/, Command.SCREEN, 2);
    Command cmdSubj=new Command(SR.MS_PASTE_SUBJECT, Command.SCREEN, 3);
    Command cmdEdit=new Command(SR.MS_EDIT, Command.SCREEN, 4);
    Command cmdNew=new Command(SR.MS_NEW, Command.SCREEN, 5);
//#if (FILE_IO)
    Command cmdExport=new Command(SR.MS_EXPORT_TO_FILE, Command.SCREEN, 7);
    Command cmdImport=new Command(SR.MS_IMPORT_TO_FILE, Command.SCREEN, 8);
//#endif
    Command cmdDelete=new Command(SR.MS_DELETE, Command.SCREEN, 9);
    Command cmdDeleteAll=new Command(SR.MS_CLEAR_LIST, Command.SCREEN, 10);

    MessageArchive archive;
   
    private int caretPos;

    private StaticData sd=StaticData.getInstance();

    private Config cf;
    
    private int where=1;

    private MessageEdit t;
    
    /** Creates a new instance of ArchiveList */
    public ArchiveList(Display display, int caretPos, int where, MessageEdit t) {
 	super ();
        this.where=where;
        this.caretPos=caretPos;
        this.t=t;
        cf=Config.getInstance();
        
        archive=new MessageArchive(where);
        
	MainBar mainbar=new MainBar((where==1)?SR.MS_ARCHIVE:SR.MS_TEMPLATE);
	mainbar.addElement(null);
	mainbar.addRAlign();
	mainbar.addElement(null);
	mainbar.addElement(SR.MS_FREE /*"free "*/);
        setMainBarItem(mainbar);
        
        if (getItemCount()>0) {
            addCommand(cmdDelete);
            
            addCommand(cmdEdit);
            addCommand(cmdDeleteAll);
            
            if (t!=null) {
                addCommand(cmdPaste);
                addCommand(cmdJid);
            }
        }
        addCommand(cmdNew);

	addCommand(cmdBack);
	setCommandListener(this);
        
        moveCursorHome();

        attachDisplay(display);
    }

    protected void beginPaint() {
        getMainBarItem().setElementAt(" ("+getItemCount()+")",1);
	getMainBarItem().setElementAt(String.valueOf(getFreeSpace()),3);
    }
    
    public int getItemCount() {
	return archive.size();
    }
    
    public Msg getMessage(int index) {
	return archive.msg(index);
    }

    public void commandAction(Command c, Displayable d) {
        super.commandAction(c,d);
        
	Msg m=getMessage(cursor);
        if (c==cmdNew) { new archiveEdit(display, -1, where, this); }
	if (m==null) return;
        
	if (c==cmdDelete) { keyClear(); }
        if (c==cmdDeleteAll) { deleteAllMessages(); redraw(); }
	if (c==cmdPaste) { pasteData(0); }
	if (c==cmdSubj) { pasteData(1); }
	if (c==cmdJid) { pasteData(2); }
        if (c==cmdEdit) {
            try {
                new archiveEdit(display, cursor, where, this);
            } catch (Exception e) {/*no messages*/}
        }
    }
    
    public void reFresh() {
        archive=new MessageArchive(where);
        messages=new Vector();
    }

    private void deleteMessage() {
        archive.delete(cursor);
        messages=new Vector();
    }
    
    private void deleteAllMessages() {
        new AlertBox(SR.MS_DELETE, SR.MS_SURE_DELETE, display, this) {
            public void yes() {
                archive.deleteAll();
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
	t.insertText(data, caretPos);
	destroyView();
    }
    
    public void keyGreen() { pasteData(0); }
    
    public void keyClear() { 
        if (getItemCount()>0) {
            new AlertBox(SR.MS_DELETE, SR.MS_SURE_DELETE, display, this) {
                public void yes() {
                    deleteMessage();
                }
                public void no() { }
            };
            redraw();
        }
    }
    
    public void focusedItem(int index) {
	if (t==null) return;
	try {
	    if (getMessage(index).subject!=null) {
		addCommand(cmdSubj);
		return;
	    }
	} catch (Exception e) { }
	removeCommand(cmdSubj);
    }
  
    public void destroyView(){
	super.destroyView();
	archive.close();
    }

    private int getFreeSpace() {
        return archive.freeSpace();
    }

}
