/*
 * HistoryReader.java
 *
 * Created on 18.06.2008, 10:39
 *
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

package History;

import Client.Contact;
import Client.Msg;
//#ifdef MENU_LISTENER
import Menu.Command;
//#else
//# import javax.microedition.lcdui.Command;
//#endif
import Messages.MessageItem;
import Messages.MessageList;
import ui.VirtualElement;
import javax.microedition.lcdui.Display;
import locale.SR;
import ui.MainBar;

/**
 *
 * @author ad
 */
public class HistoryReader extends MessageList {
//#ifdef PLUGINS
//#     public static String plugin = new String("PLUGIN_HISTORY");
//#endif
    
    private HistoryLoader hl;
    static MessageItem MIPrev, MINext, MILarge;

    /** Creates a new instance of HistoryReader
     * @param display
     * @param c 
     */
    public HistoryReader(Display display, Contact c) {
        super();
        MIPrev = new MessageItem(new Msg(Msg.MESSAGE_TYPE_SYSTEM, null, null, "<---"), this, smiles);
        MINext = new MessageItem(new Msg(Msg.MESSAGE_TYPE_SYSTEM, null, null, "--->"), this, smiles);
        MILarge = new MessageItem(new Msg(Msg.MESSAGE_TYPE_SYSTEM, null, null,
                "Size of message is larger then " + hl.BLOCK_SIZE), this, smiles);

        setMainBarItem(new MainBar(c.getName() + ": " + SR.MS_HISTORY));
        addCommands();
        removeCommand(cmdxmlSkin);

        hl = new HistoryLoader(c.bareJid, this, smiles);
        messages = hl.stepBack();

        setCommandListener(this);
        attachDisplay(display);

        moveCursorEnd();
    }

    public void keyPressed(int keyCode) {
        if ((keyCode == KEY_NUM5) || (getGameAction(keyCode) == FIRE)) {
           if (getItemRef(cursor)==MIPrev) {
               messages = hl.stepBack();
               moveCursorEnd();
               return;
           } else if (getItemRef(cursor)==MINext) {
               messages = hl.stepNext();
               moveCursorHome();
               return;
           }
        }
        super.keyPressed(keyCode);
    }

    public int getItemCount() {
        if (messages != null)
           return messages.size();
        else return 0;
    }

    public Msg getMessage(int i) {
        return ((MessageItem) messages.elementAt(i)).msg;
    }
/*
    public VirtualElement getItemRef(int i) {
        return (VirtualElement) messages.elementAt(i);
    }*/
}
