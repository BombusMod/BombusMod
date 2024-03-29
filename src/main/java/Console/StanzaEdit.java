/*
 * StanzaEdit.java
 *
 * Created on 7.04.2008, 16:05
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

//#ifdef CONSOLE

package Console;

import com.alsutton.jabber.JabberStream;

import Client.StaticData;
import java.io.IOException;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import locale.SR;
import ui.VirtualList;
import ui.controls.ExTextBox;

/**
 *
 * @author ad
 */
public class StanzaEdit
        extends ExTextBox
        implements CommandListener {
    private Command cmdCancel;
    private Command cmdSend;
    private Command cmdPasteIQDisco;
    private Command cmdPasteIQVersion;
    private Command cmdPastePresence;
    private Command cmdPasteMessage;
    private static final String TEMPLATE_IQ_DISCO = "<iq to='???' id='" + System.currentTimeMillis() + "' type='get'>\n<query xmlns='http://jabber.org/protocol/disco#info'/>\n</iq>";
    private static final String TEMPLATE_IQ_VERSION = "<iq to='???' id='" + System.currentTimeMillis() + "' type='get'>\n<query xmlns='jabber:iq:version'/>\n</iq>";
    private static final String TEMPLATE_PRESENCE = "<presence to='???'>\n<show>???</show>\n<status>???</status>\n</presence>";
    private static final String TEMPLATE_MESSAGE = "<message to='???' type='???'>\n<body>???</body>\n</message>";
    
    public StanzaEdit(VirtualList pView, String body) {
        super(pView, body, SR.MS_XML_CONSOLE);
        show(this);
    }

    public void commandState() {
        super.commandState();
        cmdCancel = new Command(SR.MS_CANCEL, Command.BACK, 99);
        cmdSend = new Command(SR.MS_SEND, Command.OK, 1);

        cmdPasteIQDisco = new Command("disco#info", Command.SCREEN, 11);
        cmdPasteIQVersion = new Command("jabber:iq:version", Command.SCREEN, 12);
        cmdPastePresence = new Command("presence", Command.SCREEN, 13);
        cmdPasteMessage = new Command("message", Command.SCREEN, 14);

        textbox.addCommand(cmdSend);

        textbox.addCommand(cmdPasteIQDisco);
        textbox.addCommand(cmdPasteIQVersion);
        textbox.addCommand(cmdPastePresence);
        textbox.addCommand(cmdPasteMessage);

        textbox.addCommand(cmdCancel);       
        

    }

    public void commandAction(Command c, Displayable d) {

        if (!executeCommand(c, d)) {

            if (c == cmdPasteIQDisco) {
                insert(TEMPLATE_IQ_DISCO, caretPos);
                return;
            }
            if (c == cmdPasteIQVersion) {
                insert(TEMPLATE_IQ_VERSION, caretPos);
                return;
            }
            if (c == cmdPastePresence) {
                insert(TEMPLATE_PRESENCE, caretPos);
                return;
            }
            if (c == cmdPasteMessage) {
                insert(TEMPLATE_MESSAGE, caretPos);
                return;
            }

            if (c == cmdCancel) {
                body = null;
            }

            if (c == cmdSend && body != null && body.length() > 0) {
                try {
                    JabberStream stream = StaticData.getInstance().getTheStream();
                    if (stream != null && stream.loggedIn) {
                        stream.send(body.trim());
                    }
                } catch (IOException ex) {
                    if (StaticData.Debug) {
                        ex.printStackTrace();
                    }
                }
            }
            destroyView();
        }
    }
}

//#endif
