/*
 * IqMood.java
 * Copyright (c) 2006-2007, Daniel Apatin (ad), http://apatin.net.ru
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

import com.alsutton.jabber.JabberDataBlock;

public class IqMood extends Iq{
    public IqMood(String to, String id, String moodString, String text) {
        super(to, Iq.TYPE_SET, id);
        
        JabberDataBlock pubsub=addChildNs("pubsub", "http://jabber.org/protocol/pubsub");
        
        JabberDataBlock publish=pubsub.addChild("publish", null);
        publish.setAttribute("node","http://jabber.org/protocol/mood");
        
        JabberDataBlock item=publish.addChild("item", null);

        JabberDataBlock moodItem=item.addChildNs("mood", "http://jabber.org/protocol/mood");
        
        moodItem.addChild(moodString, null);
        
        if (text!=null && text.length()>0)
            moodItem.addChild("text", text);
    }
}