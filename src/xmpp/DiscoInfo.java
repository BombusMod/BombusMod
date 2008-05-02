/*
 * DiscoInfo.java
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

package xmpp;

import Client.*;
import com.alsutton.jabber.JabberBlockListener;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.datablocks.Iq;
import java.util.Enumeration;
import java.util.Vector;
//#ifdef MOOD
//# import xmpp.extensions.IqMood;
//#endif

public class DiscoInfo implements JabberBlockListener{
    StaticData sd = StaticData.getInstance();
    //Vector serverFeatures=new Vector();
    public int blockArrived(JabberDataBlock data) {
        try {
            if (!(data instanceof Iq)) return JabberBlockListener.BLOCK_REJECTED;
            if (data.getAttribute("id").equals("getServerFeatures") && data.getAttribute("type").equals("result")) {
                //System.out.println(data.toString());
                for (Enumeration e=data.findNamespace("query", "http://jabber.org/protocol/disco#info").getChildBlocks().elements(); e.hasMoreElements(); ){
                    JabberDataBlock feature=(JabberDataBlock) e.nextElement();
//#ifdef MOOD
//#                         if (feature.getTagName().equals("identity")) {
//#                         if (feature.getAttribute("category").equals("pubsub"))
//#                             if (feature.getAttribute("type").equals("pep")) {
//#                                 sd.roster.useUserMood=true;
//#                                 sd.roster.theStream.addBlockListener(new IqMood());
//#if DEBUG
//#                                 System.out.println("useUserMood=true");
//#endif
//#                                 break;
//#                             }
//#                     }
//#endif
                }
                return JabberBlockListener.BLOCK_PROCESSED;
            }
        } catch (Exception e) { }
        return JabberBlockListener.BLOCK_REJECTED;
    }
    
    public DiscoInfo() {
        JabberDataBlock request=new Iq(sd.account.getServer(), Iq.TYPE_GET, "getServerFeatures");
        JabberDataBlock query=request.addChildNs("query", "http://jabber.org/protocol/disco#info");
        sd.roster.theStream.send(request);
    }
}
