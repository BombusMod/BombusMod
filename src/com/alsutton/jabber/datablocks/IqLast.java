/*
 * IqLast.java
 *
 * Created on 25.07.2006, 19:14
 *
 * Copyright (c) 2005-2007, Eugene Stahov (evgs), http://bombus-im.org
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

package com.alsutton.jabber.datablocks;

import com.alsutton.jabber.JabberDataBlock;
import ui.Time;

/**
 *
 * @author EvgS
 */
public class IqLast extends Iq{
    
    /** Creates a new instance of IqLast */
    public IqLast(JabberDataBlock request, long lastMessageTime) {
        super(request.getAttribute("from"),
              Iq.TYPE_RESULT,
              request.getAttribute("id") );
        JabberDataBlock query=addChildNs("query", "jabber:iq:last");
        long last=(Time.utcTimeMillis()-lastMessageTime)/1000;
        query.setAttribute("seconds", String.valueOf(last));
    }
    
    public IqLast(String to) {
        super(to, Iq.TYPE_GET, "last");
        addChildNs("query", "jabber:iq:last");
    }

    public static String dispatchLast(JabberDataBlock data) {
        if (!data.isJabberNameSpace("jabber:iq:last")) return "unknown last namespace";
        StringBuffer tm=new StringBuffer();
        String field=data.getAttribute("seconds");
        
        if (field!=null) {
            tm.append(Time.secDiffToDate(Integer.parseInt(field)));
        }
        
        //System.out.println(tm.toString());
        return tm.toString();
    }
}
