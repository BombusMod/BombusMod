/*
 * IqPing.java
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

package xmpp.extensions;

import Client.Msg;
import Client.Roster;
import Client.StaticData;
import com.alsutton.jabber.JabberBlockListener;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.datablocks.*;
import java.util.Enumeration;

public class IqGmail implements JabberBlockListener {

    public IqGmail(){};
    
    public static JabberDataBlock query() {
        JabberDataBlock result=new Iq(null, Iq.TYPE_GET, "mail-request");
        result.addChildNs("query", "google:mail:notify");
        return result;
    }

    public int blockArrived(JabberDataBlock data) {
        if (!(data instanceof Iq)) return BLOCK_REJECTED;
        String type=data.getTypeAttribute();

        if (type.equals("result")) {
            if (data.getAttribute("id").equals("mail-request")) {
                Roster roster=StaticData.getInstance().roster;
                roster.querysign=false;
                
                JabberDataBlock mailbox=data.findNamespace("mailbox", "google:mail:notify");
                for (Enumeration e=mailbox.getChildBlocks().elements(); e.hasMoreElements();) {
                    JabberDataBlock mail=(JabberDataBlock)e.nextElement();

                    String subject=mail.getChildBlock("subject").getText();
                    String body=mail.getChildBlock("snippet").getText();
                    String name=mail.getChildBlock("senders").getChildBlock("sender").getAttribute("name");
                    String address=mail.getChildBlock("senders").getChildBlock("sender").getAttribute("address");

                    Msg m=new Msg(Msg.MESSAGE_TYPE_IN, "local", name+"("+address+")\n"+subject, body);
                    roster.messageStore(roster.selfContact(), m);
                }
                return BLOCK_PROCESSED;
            }
        }
        return BLOCK_REJECTED;
    }
}
