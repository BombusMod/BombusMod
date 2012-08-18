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
package login;

import Account.Account;
import com.alsutton.jabber.JabberBlockListener;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.JabberStream;
import com.alsutton.jabber.datablocks.Iq;
import com.ssttr.crypto.MD5;
import java.io.IOException;
import locale.SR;
import util.Strconv;
import xmpp.XmppError;

/**
 *
 * @author evgs
 */
public class SASLAuth implements JabberBlockListener {

    private LoginListener listener;
    private Account account;
    private SASL_ScramSha1 scramSHA1;
    private JabberStream stream;

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
                // first stream - step 1. selecting authentication mechanism
                //common body
                JabberDataBlock auth = new JabberDataBlock("auth");
                auth.setNameSpace("urn:ietf:params:xml:ns:xmpp-sasl");

                if (mech.getChildBlockByText("SCRAM-SHA-1") != null && !account.plainAuth) {
                    auth.setAttribute("mechanism", "SCRAM-SHA-1");
                    scramSHA1 = new SASL_ScramSha1();
                    auth.setText(Strconv.toBase64(scramSHA1.init(account.getBareJid(), Strconv.unicodeToUTF(account.password))));
                    stream.send(auth);
                    listener.loginMessage(SR.MS_AUTH, 42);
                    return JabberBlockListener.BLOCK_PROCESSED;
                }
                // DIGEST-MD5 mechanism
                if (mech.getChildBlockByText("DIGEST-MD5") != null) {
                    auth.setAttribute("mechanism", "DIGEST-MD5");

                    //System.out.println(auth.toString());

                    stream.send(auth);
                    listener.loginMessage(SR.MS_AUTH, 42);
                    return JabberBlockListener.BLOCK_PROCESSED;
                }
//#ifdef SASL_XGOOGLETOKEN
//#                 // X-GOOGLE-TOKEN mechanism
//#                 if (mech.getChildBlockByText("X-GOOGLE-TOKEN") != null) {
//# 
//#                     listener.loginMessage(SR.MS_TOKEN, 40);
//#                     String token;
//#                     try {
//#                         token = new GoogleTokenAuth(account).responseXGoogleToken();
//#                     } catch (Exception e) {
//#                         listener.loginFailed("Can't get Google token: " + e.getMessage());
//#                         return JabberBlockListener.NO_MORE_BLOCKS;
//#                     }
//#                     if (token == null) {
//#                         listener.loginFailed("Can't get Google token");
//#                         return JabberBlockListener.NO_MORE_BLOCKS;
//#                     }
//#                     account.isGoogle = true;
//#                     auth.setAttribute("mechanism", "X-GOOGLE-TOKEN");
//#                     auth.setText(token);
//# 
//#                     //System.out.println(auth.toString());
//# 
//#                     stream.send(auth);
//#                     listener.loginMessage(SR.MS_AUTH, 42);
//#                     return JabberBlockListener.BLOCK_PROCESSED;
//# 
//#                 }
//#endif

                if (mech.getChildBlockByText("PLAIN") != null) {

                    if (!account.plainAuth) {
                        listener.loginFailed("SASL: Plain auth required");
                        return JabberBlockListener.NO_MORE_BLOCKS;
                    }

                    auth.setAttribute("mechanism", "PLAIN");
                    String plain =
                            Strconv.unicodeToUTF(account.getBareJid())
                            + (char) 0x00
                            + Strconv.unicodeToUTF(account.userName)
                            + (char) 0x00
                            + Strconv.unicodeToUTF(account.password);
                    auth.setText(Strconv.toBase64(plain));

                    stream.send(auth);
                    listener.loginMessage(SR.MS_AUTH, 42);
                    return JabberBlockListener.BLOCK_PROCESSED;
                }
                // no more method found
                listener.loginFailed("SASL: Unknown mechanisms");
                return JabberBlockListener.NO_MORE_BLOCKS;

            } // second stream - step 1. binding resource
            else if (data.getChildBlock("bind") != null) {
                JabberDataBlock bindIq = new Iq(null, Iq.TYPE_SET, "bind");
                JabberDataBlock bind = bindIq.addChildNs("bind", "urn:ietf:params:xml:ns:xmpp-bind");
                bind.addChild("resource", account.resource);
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
            //System.out.println(challenge);

            JabberDataBlock resp = new JabberDataBlock("response");
            resp.setNameSpace("urn:ietf:params:xml:ns:xmpp-sasl");

            if (scramSHA1 != null) {
                resp.setText(Strconv.toBase64(scramSHA1.response(challenge)));
            } else {
                int nonceIndex = challenge.indexOf("nonce=");
                // first stream - step 2. generating DIGEST-MD5 response due to challenge
                if (nonceIndex >= 0) {
                    nonceIndex += 7;
                    String nonce = challenge.substring(nonceIndex, challenge.indexOf('\"', nonceIndex));
                    String cnonce = "123456789abcd";
                    resp.setText(responseMd5Digest(
                            Strconv.unicodeToUTF(account.userName),
                            Strconv.unicodeToUTF(account.password),
                            account.server,
                            "xmpp/" + account.server,
                            nonce,
                            cnonce));
                }
                //System.out.println(resp.toString());
            }
            // first stream - step 3. sending second empty response due to second challenge
            //if (challenge.startsWith("rspauth")) {}

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
            if (scramSHA1 != null) {
                if (!scramSHA1.success(new String(Strconv.fromBase64(data.getText())))) {
                    listener.loginFailed("Server answer not valid");
                }
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

    /**
     * This routine generates MD5-DIGEST response via SASL specification
     *
     * @param user
     * @param pass
     * @param realm
     * @param digest_uri
     * @param nonce
     * @param cnonce
     * @return
     */
    private String responseMd5Digest(String user, String pass, String realm, String digestUri, String nonce, String cnonce) {

        MD5 hUserRealmPass = new MD5();
        hUserRealmPass.init();
        hUserRealmPass.updateASCII(user);
        hUserRealmPass.update((byte) ':');
        hUserRealmPass.updateASCII(realm);
        hUserRealmPass.update((byte) ':');
        hUserRealmPass.updateASCII(pass);
        hUserRealmPass.finish();

        MD5 hA1 = new MD5();
        hA1.init();
        hA1.update(hUserRealmPass.getDigestBits());
        hA1.update((byte) ':');
        hA1.updateASCII(nonce);
        hA1.update((byte) ':');
        hA1.updateASCII(cnonce);
        hA1.finish();

        MD5 hA2 = new MD5();
        hA2.init();
        hA2.updateASCII("AUTHENTICATE:");
        hA2.updateASCII(digestUri);
        hA2.finish();

        MD5 hResp = new MD5();
        hResp.init();
        hResp.updateASCII(hA1.getDigestHex());
        hResp.update((byte) ':');
        hResp.updateASCII(nonce);
        hResp.updateASCII(":00000001:");
        hResp.updateASCII(cnonce);
        hResp.updateASCII(":auth:");
        hResp.updateASCII(hA2.getDigestHex());
        hResp.finish();

        String out = "username=\"" + user + "\",realm=\"" + realm + "\","
                + "nonce=\"" + nonce + "\",nc=00000001,cnonce=\"" + cnonce + "\","
                + "qop=auth,digest-uri=\"" + digestUri + "\","
                + "response=\"" + hResp.getDigestHex() + "\",charset=utf-8";
        String resp = Strconv.toBase64(out);
        //System.out.println(decodeBase64(resp));
        return resp;
    }
}
