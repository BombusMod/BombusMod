/*
 * archiveEdit.java
 *
 * Created on 20.02.2005, 21:20
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
 *
 */

package archive;

import Client.Config;
import Client.Msg;
import Client.StaticData;
import java.util.Vector;
import javax.microedition.lcdui.*;
import locale.SR;
import ui.VirtualList;
//#ifdef CLIPBOARD
//# import util.ClipBoard;
//#endif
/**
 *
 * @author Eugene Stahov
 */
public class archiveEdit implements CommandListener
{
    
    private Display display;
    private Displayable parentView;
    private TextBox t;
    private String body="";

    private Command cmdCancel=new Command(SR.MS_CANCEL, Command.SCREEN,99);
    private Command cmdOk=new Command(SR.MS_OK, Command.OK /*Command.SCREEN*/,1);

    private Command cmdPasteText=new Command(SR.MS_PASTE, Command.SCREEN, 98);  

    private Msg msg;
    
    MessageArchive archive;
//#ifdef CLIPBOARD
//#     private ClipBoard clipboard;
//#endif
    private Config cf;

    private int where=1;

    private int pos;

    private ArchiveList al;
    
    public archiveEdit(Display display, int pos, int where, ArchiveList al) {
        archive=new MessageArchive(where);

        this.where=where;
        this.display=display;
        parentView=display.getCurrent();
        
        this.pos=pos;
        
        this.al=al;
        
        cf=Config.getInstance();
        
	t=new TextBox((pos>-1)?SR.MS_EDIT:SR.MS_NEW, "", 500, TextField.ANY);

        if (pos>-1) {
            this.msg=archive.msg(pos);
            this.body=msg.getBody();
        }
        try {
            //expanding buffer as much as possible
            int maxSize=t.setMaxSize(4096); //must not trow

            if (body!=null) {
                if (body.length()>maxSize)
                    body=body.substring(0, maxSize-1);
                t.setString(body);
            }
         } catch (Exception e) {}

//#ifdef CLIPBOARD
//#         if (Config.getInstance().useClipBoard) {
//#             clipboard=ClipBoard.getInstance();
//#             if (!clipboard.isEmpty())
//#                 t.addCommand(cmdPasteText);
//#         }
//#endif
        
        t.addCommand(cmdOk);

        t.addCommand(cmdCancel);
        t.setCommandListener(this);

        display.setCurrent(t);
    }
    
    public void commandAction(Command c, Displayable d){
        body=t.getString();
		
        int caretPos=t.getCaretPosition();
        if (cf.phoneManufacturer==Config.MOTO)
            caretPos=-1;
        if (caretPos<0) caretPos=body.length();
		
        if (body.length()==0) body=null;
//#ifdef CLIPBOARD
//#         if (c==cmdPasteText) { t.insert(clipboard.getClipBoard(), caretPos); return; }
//#endif
        if (c==cmdOk) {
            int type=Msg.MESSAGE_TYPE_OUT;
            String from="";
            String subj="";
            if (pos>-1) {
                type=msg.messageType;
                from=msg.from;
                subj=msg.subject;
                archive.delete(pos);
            }
            Msg newmsg=new Msg(type, from, subj, body);
            
            archive.store(newmsg, where);
            archive.close();
            
            al.reFresh();
        }
        
        display.setCurrent(parentView);
    }
}
