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
import Messages.MessageItem;
import Messages.MessageList;
import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.lcdui.Canvas;
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
    static MessageItem MIPrev, MINext;

    /** Creates a new instance of HistoryReader
     * @param c 
     */
    public HistoryReader(Contact c) {
        super(new Vector());
        MIPrev = new MessageItem(new Msg(Msg.MESSAGE_TYPE_SYSTEM, null, null, "<---"), smiles);
        MINext = new MessageItem(new Msg(Msg.MESSAGE_TYPE_SYSTEM, null, null, "--->"), smiles);

        setMainBarItem(new MainBar(c.getName() + ": " + SR.MS_HISTORY));        

        hl = new HistoryLoader(c.bareJid, smiles);

        loadFrom(hl.stepEnd());
        moveCursorEnd();
        show();
    }

    public void eventOk() {
        if (getItemRef(getCursor()) == MIPrev) {
            loadFrom(hl.stepBack());
            moveCursorEnd();
            return;
        } else if (getItemRef(getCursor()) == MINext) {
            loadFrom(hl.stepNext());
            moveCursorHome();
            return;
        }
        super.eventOk();
    }

    public final void loadFrom(final Vector items) {
        messages.removeAllElements();
        for (Enumeration e = items.elements(); e.hasMoreElements(); ) {
            messages.addElement((MessageItem)e.nextElement());
        }
    }

    protected boolean key(int key_code, boolean key_long) {
        if (!key_long) {
            switch (key_code) {
                case Canvas.KEY_NUM1:
                    loadFrom(hl.stepBegin());
                    moveCursorHome();
                    return true;
                case Canvas.KEY_NUM7:
                    loadFrom(hl.stepEnd());
                    moveCursorEnd();
                    return true;
            }
        }
     
        return super.key(key_code, key_long);
    }

    public int getItemCount() {
        return messages == null ? 0 : messages.size();
    }

    public Msg getMessage(int i) {
        return ((MessageItem) messages.elementAt(i)).msg;
    }
    public void commandState() {
        super.commandState();
        removeMenuCommand(cmdxmlSkin);
    }
/*
    public VirtualElement getItemRef(int i) {
        return (VirtualElement) messages.elementAt(i);
    }*/
}
