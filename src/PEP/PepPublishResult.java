/*
 * MoodPublishResult.java
 *
 * Created on 4.05.2008, 1:21
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
 *
 */

//#ifdef PEP

package PEP;

import com.alsutton.jabber.JabberBlockListener;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.datablocks.Iq;
import locale.SR;
import ui.controls.AlertBox;
import xmpp.XmppError;

public class PepPublishResult implements JabberBlockListener {

    private String id;
    
    /** Creates a new instance of MoodPublishResult
     * @param id
     */
    public PepPublishResult(String id) {
        this.id=id;
    }

    public int blockArrived(JabberDataBlock data) {
        if (!(data instanceof Iq)) return BLOCK_REJECTED;
        if (!data.getAttribute("id").equals(id)) return BLOCK_REJECTED;
        
        String type=data.getTypeAttribute();
        if (type.equals("result")) return NO_MORE_BLOCKS;
        
        XmppError e=XmppError.findInStanza(data);
        new AlertBox(SR.MS_ERROR_, SR.MS_PEP_NOT_SUPPORTED+"("+e.toString()+")") {
            public void yes() { }
            public void no() { }
        };
        return NO_MORE_BLOCKS;
    }
}

//#endif
