/*
 Copyright (c) 2000, Al Sutton (al@alsutton.com)
 All rights reserved.
 Redistribution and use in source and binary forms, with or without modification, are permitted
 provided that the following conditions are met:

 1. Redistributions of source code must retain the above copyright notice, this list of conditions
 and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice, this list of
 conditions and the following disclaimer in the documentation and/or other materials provided with
 the distribution.

 Neither the name of Al Sutton nor the names of its contributors may be used to endorse or promote
 products derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE
 LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF
 THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.alsutton.jabber.datablocks;

import com.alsutton.jabber.*;
import java.util.*;
import ui.Time;
import xmpp.XmppError;
//#ifndef WMUC
import xmpp.extensions.muc.Conference;
//#endif
public class Message extends JabberDataBlock {
    
    public static final String NS_CHATSTATES = "http://jabber.org/protocol/chatstates";
    public static final String NS_RECEIPTS = "urn:xmpp:receipts";

    public Message(String to, String message, String subject, boolean groupchat) {
        super("message");

        setAttribute("to", to);
        if (message != null) {
            setBodyText(message);
        }
        if (subject != null) {
            setSubject(subject);
        }
        setTypeAttribute((groupchat) ? "groupchat" : "chat");
    }

    public Message(String to) {
        super("message");
        setAttribute("to", to);
    }

    public Message() {
        super("message");
    }

    public Message(Vector _attributes) {
        super("message", _attributes);
    }

    public final void setBodyText(String text) {
        addChild("body", text);
    }

    public final void setSubject(String text) {
        addChild("subject", text);
    }

    public String getSubject() {
        return getChildBlockText("subject");
    }

    public String getBody() {
        String body = getChildBlockText("body");

        JabberDataBlock error = getChildBlock("error");
        if (error == null) {
            return body;
        }
        return body + "Error\n" + XmppError.decodeStanzaError(error).toString();
    }

    public String getOOB() {
        JabberDataBlock oobData = findNamespace("x", "jabber:x:oob");
        if (oobData == null) {
            return null;
        }
        StringBuffer oob = new StringBuffer();
        try {
            oob.append("\n").append(oobData.getChildBlockText("desc"));
            if (oob.length() > 1) {
                oob.append(" ");
            }
            oob.append("( ").append(oobData.getChildBlockText("url")).append(" )");
        } catch (Exception ex) {
            return null;
        }

        return oob.toString();
    }

    public long getMessageTime() {
        JabberDataBlock delay = findNamespace("x", "jabber:x:delay");
        if (delay == null) {
            delay = findNamespace("delay", "urn:xmpp:delay");
        }
        if (delay != null) {
            return Time.dateIso8601(delay.getAttribute("stamp"));
        }
        return 0; //0 means no timestamp
    }

    public String getXFrom() {
        // jep-0033 extended stanza addressing from psi
        JabberDataBlock addresses = getChildBlock("addresses");
        if (addresses != null) {
            for (Enumeration e = addresses.getChildBlocks().elements(); e.hasMoreElements();) {
                JabberDataBlock adr = (JabberDataBlock) e.nextElement();
                if (adr.getTypeAttribute().equals("ofrom")) {
                    String xfrom = adr.getAttribute("jid");
                    return xfrom.equals("") ? getFrom() : xfrom; // workaround for Tkabber
                }
            }
        }
        return getAttribute("from");
    }

    public String getFrom() {
        return getAttribute("from");
    }
//#ifndef WMUC

    public JabberDataBlock getMucInvitation() {
        JabberDataBlock xmlns = findNamespace("x", Conference.NS_MUC + "#user");
        if (xmlns != null) {
            return xmlns.getChildBlock("invite");
        }
        return null;
    }
//#endif
}
