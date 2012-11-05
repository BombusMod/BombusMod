/*
 * QueryConfigForm.java
 *
 * Created on 11.10.2005, 0:35
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
package Conference;

import Client.StaticData;
import ServiceDiscovery.DiscoForm;
import com.alsutton.jabber.JabberBlockListener;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.datablocks.Iq;
import com.alsutton.jabber.JabberStream;
import xmpp.extensions.muc.Conference;

/**
 *
 * @author EvgS
 */
public class QueryConfigForm implements JabberBlockListener {

    private final static String OWNER_XMLNS = Conference.NS_MUC + "#owner";

    public String roomJid;

    /** Creates a new instance of QueryConfigForm */
    public QueryConfigForm(String roomJid) {
        this.roomJid = roomJid;
        JabberDataBlock getform = new Iq(roomJid, Iq.TYPE_GET, "confform");
        getform.addChildNs("query", OWNER_XMLNS);

        JabberStream stream = StaticData.getInstance().theStream;
        stream.addBlockListener(this);
        stream.send(getform);
        StaticData.getInstance().roster.setQuerySign(true);

    }

    public int blockArrived(JabberDataBlock data) {
        JabberDataBlock query = data.findNamespace("query", OWNER_XMLNS);
        if (query != null) {
            StaticData.getInstance().roster.setQuerySign(false);
            if (data.getTypeAttribute().equals("result")) {
                new DiscoForm(null, null, null, data, StaticData.getInstance().theStream, "setform", "query");
            }
            return JabberBlockListener.NO_MORE_BLOCKS;
        }
        return JabberBlockListener.BLOCK_REJECTED;
    }

    public void XDataFormSubmit(JabberDataBlock form) {
        JabberDataBlock setform = new Iq(roomJid, Iq.TYPE_SET, "setform");
        JabberDataBlock query = setform.addChildNs("query", OWNER_XMLNS);
        query.addChild(form);
        //System.out.println("Destination form: " + form.toString());
        StaticData.getInstance().theStream.send(setform);
    }
}
