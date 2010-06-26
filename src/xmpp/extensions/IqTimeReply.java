/*
 * IqTimeReply.java
 *
 * Created on 10.09.2005, 23:15
 *
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
 */

package xmpp.extensions;

import Client.Config;
import Client.Contact;
import Client.Msg;
import Client.Roster;
import Client.StaticData;
import com.alsutton.jabber.JabberBlockListener;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.datablocks.*;
import locale.SR;

/**
 *
 * @author EvgS
 */
public class IqTimeReply implements JabberBlockListener{
    
    public IqTimeReply(){};
    
    public static JabberDataBlock query(String to){
        JabberDataBlock result=new Iq(to, Iq.TYPE_GET, "time");
        result.addChildNs("query", "jabber:iq:time");
        return result;
    }
    
    public int blockArrived(JabberDataBlock data) {
        if (!(data instanceof Iq)) return BLOCK_REJECTED;
        String type=data.getTypeAttribute();
        if (type.equals("get")) {
            JabberDataBlock query=data.findNamespace("query", "jabber:iq:time");
            if (query!=null) {
                query.addChild("utc",ui.Time.Xep0082UtcTime());
                query.addChild("display", ui.Time.dispLocalTime());
            } else {
                query=data.findNamespace("time", "urn:xmpp:time");
                if (query==null) return BLOCK_REJECTED;
                query.addChild("utc",ui.Time.utcTime());
                query.addChild("tzo", ui.Time.tzOffset());
            }
            Contact c=StaticData.getInstance().roster.getContact( data.getAttribute("from"), Config.getInstance().IQNotify);
            if (c != null)
                c.setIncoming(Contact.INC_VIEWING);
            Iq reply=new Iq(data.getAttribute("from"), Iq.TYPE_RESULT, data.getAttribute("id"));
            reply.addChild(query);

            StaticData.getInstance().roster.theStream.send(reply);

            return BLOCK_PROCESSED;
        }
        
        if (data.getAttribute("id").equals("time")) {
            Roster roster=StaticData.getInstance().roster;
            Contact c=roster.getContact( data.getAttribute("from"), false);
            //String from=data.getAttribute("from");
            String body=null;

            if (type.equals("error")) {
                body="error";
                roster.querysign=false;
            } else if (type.equals("result")) {
                JabberDataBlock tm=data.getChildBlock("query");
                if (tm!=null) {
                    body=dispatchTime(tm);
                }
                roster.querysign=false;
            }
            if (body!=null) {
                Msg m=new Msg(Msg.MESSAGE_TYPE_SYSTEM, "time", SR.MS_TIME, body);
                roster.messageStore(c, m);
                roster.redraw();
                return BLOCK_PROCESSED;
            }
        }
        return BLOCK_REJECTED;
    }
    
    public static String dispatchTime(JabberDataBlock data) {
        if (!data.isJabberNameSpace("jabber:iq:time")) return "unknown time namespace";
        StringBuffer tm=new StringBuffer();
        String field=data.getChildBlockText("display");
        //System.out.println(field);
        if (field.length()>0) {
                tm.append(field);
        }
        return tm.toString();
    }

}
