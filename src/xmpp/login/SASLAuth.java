/*
 * SASLAuth.java
 *
 * Created on 8.07.2006, 23:34
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
package xmpp.login;

import com.alsutton.jabber.JabberBlockListener;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.JabberStream;
import com.alsutton.jabber.datablocks.Iq;
import java.io.IOException;
import locale.SR;
import util.Strconv;
import xmpp.Account;
import xmpp.XmppError;
import xmpp.login.sasl.SaslFactory;
import xmpp.login.sasl.SaslMechanism;

/**
 *
 * @author evgs
 */
public class SASLAuth implements JabberBlockListener {

    private LoginListener listener;
    private Account account;
    private JabberStream stream;
    SaslMechanism selectedMechanism;

    /**
     * Creates a new instance of SASLAuth
     */
    public SASLAuth(Account account, LoginListener listener, JabberStream stream) {
        this.listener = listener;
        this.account = account;
        this.stream = stream;
        //listener.loginMessage(SR.MS_SASL_STREAM);
    }

    public int blockArrived(JabberDataBlock data) {
        //System.out.println(data.toString());        
        if (data.getTagName().equals("features")) {
//#if TLS
//#             JabberDataBlock starttls=data.getChildBlock("starttls");
//#             if (starttls!=null && starttls.isJabberNameSpace("urn:ietf:params:xml:ns:xmpp-tls")) {                
//#                 
//#               /*  if (starttls.getChildBlock("required") != null) {
//#                     listener.loginFailed("TLS required");                    
//#                 }*/ 
//#                 
//#                 JabberDataBlock askTls=new JabberDataBlock("starttls");
//#                 askTls.setNameSpace("urn:ietf:params:xml:ns:xmpp-tls");
//#                 stream.send(askTls);
//#                 StaticData.getInstance().roster.setProgress("TLS negotiation", 39);
//#                 return JabberBlockListener.BLOCK_PROCESSED;    
//#                  //if
//#                 
//#                 /*
//#                  * tls avaiable from server, but user does not want to use it.
//#                  * just ignore this feature and allow auth to take place
//#                  */
//#             }
//#endif            
//#if ZLIB
//#             JabberDataBlock compr = data.getChildBlock("compression");
//#             if (compr != null && account.useCompression()) {
//#                 if (compr.getChildBlockByText("zlib") != null) {
//#                     // negotiating compression
//#                     JabberDataBlock askCompr = new JabberDataBlock("compress");
//#                     askCompr.setNameSpace("http://jabber.org/protocol/compress");
//#                     askCompr.addChild("method", "zlib");
//#                     stream.send(askCompr);
//#                     listener.loginMessage(SR.MS_ZLIB, 43);
//#                     return JabberBlockListener.BLOCK_PROCESSED;
//#                 }
//#             }
//#endif            
            JabberDataBlock mech = data.getChildBlock("mechanisms");            
            if (mech != null) {
                selectedMechanism = SaslFactory
                        .getPreferredMechanism(account, SaslMechanism.parseMechanisms(mech));
                if (selectedMechanism == null) {
                    // no more method found
                    listener.loginFailed("SASL: Unknown mechanisms");
                    return JabberBlockListener.NO_MORE_BLOCKS;
                }
                // first stream - step 1. selecting authentication mechanism
                //common body
                JabberDataBlock auth = new JabberDataBlock("auth");
                auth.setNameSpace(SaslFactory.NS_SASL);
                String initialMessage =
                        selectedMechanism.init(account.JID,
                        Strconv.unicodeToUTF(account.password));
                auth.setAttribute("mechanism",
                        selectedMechanism.getName());
                if (initialMessage != null) {
                    auth.setText(Strconv.toBase64(initialMessage));
                    stream.send(auth);
                    listener.loginMessage(SR.MS_AUTH, 42);
                    return JabberBlockListener.BLOCK_PROCESSED;
                } else {
                    // no more method found
                    listener.loginFailed("SASL: Unknown mechanisms");
                    return JabberBlockListener.NO_MORE_BLOCKS;
                }
            } // second stream - step 1. binding resource
            else if (data.getChildBlock("bind") != null) {
                JabberDataBlock bindIq = new Iq(null, Iq.TYPE_SET, "bind");
                JabberDataBlock bind = bindIq.addChildNs("bind", "urn:ietf:params:xml:ns:xmpp-bind");
                bind.addChild("resource", account.JID.resource);
                stream.send(bindIq);

                listener.loginMessage(SR.MS_RESOURCE_BINDING, 44);

                return JabberBlockListener.BLOCK_PROCESSED;
            }

//#ifdef NON_SASL_AUTH
//#             if (data.findNamespace("auth", "http://jabber.org/features/iq-auth") != null) {
//#                 new NonSASLAuth(account, listener, stream);
//#                 return JabberBlockListener.NO_MORE_BLOCKS;
//#             }
//#endif            

            //fallback if no known authentication methods were found
            listener.loginFailed("No known authentication methods");

            return JabberBlockListener.NO_MORE_BLOCKS;
        } else if (data.getTagName().equals("challenge")) {
            // first stream - step 2,3. reaction to challenges

            String challenge = Strconv.sFromBase64(data.getText());            

            JabberDataBlock resp = new JabberDataBlock("response");
            resp.setNameSpace(SaslFactory.NS_SASL);
            try {
                resp.setText(Strconv.toBase64(selectedMechanism.response(challenge)));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            stream.send(resp);
            return JabberBlockListener.BLOCK_PROCESSED;
        } 
//#if TLS
//#         else if ( data.getTagName().equals("proceed")) {
//#             //todo: investigate why the namespace attribute is not set
//#             try {
//#                 stream.setTls();
//#                 stream.initiateStream();
//#             } catch (Exception ex) { 
//#                 ex.printStackTrace();
//#                 listener.loginFailed("TLS negotiation failed: " + ex.getMessage()); 
//#             } 
//#             return JabberBlockListener.NO_MORE_BLOCKS;
//#         }
//#         else if ( data.getTagName().equals("failure") && data.isJabberNameSpace("urn:ietf:params:xml:ns:xmpp-tls")) {
//#             listener.loginFailed("TLS failed");
//#         }
//#endif                
//#if ZLIB
//#         else if (data.getTagName().equals("compressed")) {
//#             stream.setZlibCompression();
//#             try {
//#                 stream.initiateStream();
//#             } catch (IOException ex) {
//#             }
//#             return JabberBlockListener.NO_MORE_BLOCKS;
//#         } 
//#endif
        else if (data.getTagName().equals("failure")) {
            // first stream - step 4a. not authorized
            listener.loginFailed(XmppError.decodeSaslError(data).toString());
        } else if (data.getTagName().equals("success")) {
            if (!selectedMechanism.success(new String(Strconv.fromBase64(data.getText())))) {
                listener.loginFailed("Server answer not valid");
            }
            // first stream - step 4b. success.
            try {
                stream.initiateStream();
            } catch (IOException ex) {
            }
            return JabberBlockListener.NO_MORE_BLOCKS; // at first stream
        }

        if (data instanceof Iq) {
            if (data.getTypeAttribute().equals("result")) {
                // second stream - step 2. resource binded - opening session
                if (data.getAttribute("id").equals("bind")) {
                    String myJid = data.getChildBlock("bind").getChildBlockText("jid");
                    listener.bindResource(myJid);
                    JabberDataBlock session = new Iq(null, Iq.TYPE_SET, "sess");
                    session.addChildNs("session", "urn:ietf:params:xml:ns:xmpp-session");
                    stream.send(session);
                    listener.loginMessage(SR.MS_SESSION, 45);
                    return JabberBlockListener.BLOCK_PROCESSED;

                    // second stream - step 3. session opened - reporting success login
                } else if (data.getAttribute("id").equals("sess")) {
                    listener.loginSuccess();
                    return JabberBlockListener.NO_MORE_BLOCKS;
                    //return JabberBlockListener.BLOCK_PROCESSED;
                }
            }
        }
        return JabberBlockListener.BLOCK_REJECTED;
    }    
}
