/*
 * IqVersionReply.java
 *
 * Created on 27.02.2005, 18:31
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
*
 */

package com.alsutton.jabber.datablocks;

import Client.Config;
import Info.Version;
import com.alsutton.jabber.*;

/**
 *
 * @author Eugene Stahov
 */
public class IqVersionReply extends Iq{
    /** Creates a new instance of IqVersionReply */
    public IqVersionReply(JabberDataBlock request) {
        super(request.getAttribute("from"), Iq.TYPE_RESULT, request.getAttribute("id") );
        JabberDataBlock query=addChildNs("query", "jabber:iq:version");

        query.addChild("name", Version.NAME);
        query.addChild("version", Version.getVersionLang());
        if (Config.getInstance().enableVersionOs) {
            query.addChild("os", Config.getOs());
        }
    }
    
    // constructs version request
    public IqVersionReply(String to) {
        super(to, Iq.TYPE_GET, "getver");
        addChildNs("query", "jabber:iq:version");
    }
    
    ///public static boolean 
    private final static String TOPFIELDS []={ "name",  "version",  "os"  }; 

  
    public static String dispatchVersion(JabberDataBlock data) {
        if (!data.isJabberNameSpace("jabber:iq:version")) return "unknown version namespace";
        StringBuffer vc=new StringBuffer();
        //vc.append((char)0x01);
        for (int i=0; i<TOPFIELDS.length; i++){
            String field=data.getChildBlockText(TOPFIELDS[i]);
            if (field.length()>0) {
                vc.append(TOPFIELDS[i]);
                vc.append((char)0xa0);
                vc.append(field);
                vc.append((char)'\n');
            }
        }
        return vc.toString();
    }
}
