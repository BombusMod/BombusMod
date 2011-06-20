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
package Archive;

import Client.Config;
import Client.Msg;
import javax.microedition.lcdui.*;
import locale.SR;
import ui.VirtualList;
import ui.controls.ExTextBox;

/**
 *
 * @author ad
 */
public class archiveEdit
        extends ExTextBox
        implements CommandListener {
//#ifdef PLUGINS
//#     public static String plugin = new String("PLUGIN_ARCHIVE");
//#endif
    private Command cmdCancel;
    private Command cmdOk;
    private Msg msg;
    MessageArchive archive;
    private int where = 1;
    private int pos;
    private ArchiveList al;
    
    public archiveEdit(VirtualList pView, int pos, int where, ArchiveList al) {

        super(pView, null, (pos > -1) ? SR.MS_EDIT : SR.MS_NEW);

        archive = new MessageArchive(where);

        this.where = where;

        this.pos = pos;

        this.al = al;

        if (pos > -1) {
            this.msg = archive.msg(pos);
            body = msg.quoteString();
        }

        setText(body);          
        show(this);
    }

    public void commandState() {

        super.commandState();

        cmdCancel = new Command(SR.MS_CANCEL, Command.BACK, 99);
        cmdOk = new Command(SR.MS_OK, Command.OK /*Command.SCREEN*/, 1);

        textbox.addCommand(cmdOk);
        textbox.addCommand(cmdCancel);

//#ifdef ARCHIVE
        textbox.removeCommand(cmdPaste);
//#endif
//#if TEMPLATES
//#         textbox.removeCommand(cmdTemplate);
//#endif
        if (Config.getInstance().phoneManufacturer == Config.SONYE) 
            if (Config.getPlatformName().indexOf("JP-8.4") > -1)
                System.gc(); // prevent flickering on Sony Ericcsson C510
        textbox.setCommandListener(this);        
        
    }
    
    public void commandAction(Command c, Displayable d){
        //if (executeCommand(c, d)) return;
        
        if (!executeCommand(c, d)) {
		
        if (c == cmdOk) {
                int type = Msg.MESSAGE_TYPE_OUT;
                String from = "";
                String subj = "";
                if (pos > -1) {
                    type = msg.messageType;
                    from = msg.from;
                    subj = msg.subject;
                    archive.delete(pos);
                }
                Msg newmsg = new Msg(type, from, subj, body);

                MessageArchive.store(newmsg, where);
                archive.close();
                al.reFresh();
            }
            destroyView();

        }
    }
}
