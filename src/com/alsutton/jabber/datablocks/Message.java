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

public class Message extends JabberDataBlock {
  public Message( String to, String message , String subject, boolean groupchat) {
    super();

    setAttribute( "to", to );
    if( message != null )
        setBodyText( message );
    if (subject!=null) 
        setSubject(subject);
    setTypeAttribute((groupchat)?"groupchat":"chat");
  }

  public Message( String to ) {
    super();
    setAttribute( "to", to );
  }

  public Message() {
    this(null);
  }

  public Message( JabberDataBlock _parent, Vector _attributes ) {
    super( _parent, _attributes );
  }

  public final void setBodyText( String text ) {
    addChild( "body", text );
  }

  public final void setSubject( String text ) {
      addChild( "subject", text );
  }

  public String getSubject() {
      return getChildBlockText( "subject" );
  }

  public String getBody() { 
      String body=getChildBlockText( "body" ); 
      
      JabberDataBlock error=getChildBlock("error");
      if (error==null) return body;
      return body+"Error\n"+XmppError.decodeStanzaError(error).toString();
  }
  
  
  public String getOOB() {
      JabberDataBlock oobData=findNamespace("x", "jabber:x:oob");
      if (oobData == null) return null;
      StringBuffer oob=new StringBuffer();
      try {
          oob.append("\n").append(oobData.getChildBlockText("desc"));
          if (oob.length()>1) oob.append(" ");
          oob.append("( ").append(oobData.getChildBlockText("url")).append(" )");
      } catch (Exception ex) { return null; }
  
      return oob.toString();
  }

    public long getMessageTime(){
      try {
          return Time.dateIso8601(
                  findNamespace("x", "jabber:x:delay").getAttribute("stamp")
                  );
      } catch (Exception e) { }
      try {
          return Time.dateIso8601(
                  findNamespace("delay", "urn:xmpp:delay").getAttribute("stamp")
                  );
      } catch (Exception e) { }
      return 0; //0 means no timestamp
   }

    public String getTagName() {
        return "message";
    }

    public String getXFrom() {
	try {
	    // jep-0033 extended stanza addressing from psi
	    JabberDataBlock addresses=getChildBlock("addresses");
	    for (Enumeration e=addresses.getChildBlocks().elements(); e.hasMoreElements(); ) {
		JabberDataBlock adr=(JabberDataBlock) e.nextElement();
		if (adr.getTypeAttribute().equals("ofrom")) {
                    String xfrom = adr.getAttribute("jid");
                    return xfrom.equals("") ? getFrom() : xfrom; // workaround for Tkabber
                }
	    }
	} catch (Exception e) { /* normal case if not forwarded message */ }
	
        return getAttribute("from");
    }
    
    public String getFrom() {
        return getAttribute("from");
    }
//#ifndef WMUC
    public JabberDataBlock getMucInvitation() {
        JabberDataBlock xmlns = findNamespace("x", "http://jabber.org/protocol/muc#user");
        if (xmlns != null) {
            return xmlns.getChildBlock("invite");
        }
        return null;
    }
//#endif
}
