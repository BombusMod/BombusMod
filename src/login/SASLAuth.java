/*
 * SASLAuth.java
 *
 * Created on 8.07.2006, 23:34
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

package login;

import Client.Account;
import com.alsutton.jabber.JabberBlockListener;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.JabberStream;
import com.alsutton.jabber.datablocks.Iq;
import com.ssttr.crypto.MD5;
import java.io.IOException;
import locale.SR;

import util.strconv;
//#if SASL_XGOOGLETOKEN
import java.io.InputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
//#endif

/**
 *
 * @author evgs
 */
public class SASLAuth implements JabberBlockListener{
    
    private LoginListener listener;
    private Account account;
    private JabberStream stream;
    private String sessionId;
    /** Creates a new instance of SASLAuth */
    public SASLAuth(Account account, String sessionId, LoginListener listener, JabberStream stream) {
        this.listener=listener;
        this.account=account;
        this.sessionId=sessionId;
        this.stream=stream;
        if (stream!=null) stream.addBlockListener(this);
        //listener.loginMessage(SR.MS_SASL_STREAM);
    }
    
//#if SASL_XGOOGLETOKEN
    private String token;
    public void setToken(String token) { this.token=token; }
//#endif

    public int blockArrived(JabberDataBlock data) {
        //System.out.println(data.toString());
        if (data.getTagName().equals("stream:features")) {
//#if ZLIB
            JabberDataBlock compr=data.getChildBlock("compression");
            if (compr!=null && account.useCompression()) {
                if (compr.getChildBlockByText("zlib")!=null) {
                    // negotiating compression
                    JabberDataBlock askCompr=new JabberDataBlock("compress", null, null);
                    askCompr.setNameSpace("http://jabber.org/protocol/compress");
                    askCompr.addChild("method", "zlib");
                    stream.send(askCompr);
                    listener.loginMessage(SR.MS_ZLIB);
                    return JabberBlockListener.BLOCK_PROCESSED;
                }
            }
//#endif            
            JabberDataBlock mech=data.getChildBlock("mechanisms");
            if (mech!=null) {
                // first stream - step 1. selecting authentication mechanism
                //common body
                JabberDataBlock auth=new JabberDataBlock("auth", null,null);
                auth.setNameSpace("urn:ietf:params:xml:ns:xmpp-sasl");
                
                // DIGEST-MD5 mechanism
                if (mech.getChildBlockByText("DIGEST-MD5")!=null) {
                    auth.setAttribute("mechanism", "DIGEST-MD5");
                    
                    //System.out.println(auth.toString());
                    
                    stream.send(auth);
                    listener.loginMessage(SR.MS_AUTH);
                    return JabberBlockListener.BLOCK_PROCESSED;
                }
                
//#if SASL_XGOOGLETOKEN
                // X-GOOGLE-TOKEN mechanism
                if (mech.getChildBlockByText("X-GOOGLE-TOKEN")!=null  && token!=null) {
                    auth.setAttribute("mechanism", "X-GOOGLE-TOKEN");
                    auth.setText(token);
                    
                    //System.out.println(auth.toString());
                    
                    stream.send(auth);
                    listener.loginMessage(SR.MS_AUTH);
                    return JabberBlockListener.BLOCK_PROCESSED;
                    
                }
//#endif

                if (mech.getChildBlockByText("PLAIN")!=null) {

                    if (!account.getPlainAuth()) {
                        listener.loginFailed("SASL: Plain auth required");
                        return JabberBlockListener.NO_MORE_BLOCKS;
                    }
                    
                    auth.setAttribute("mechanism", "PLAIN");
                    String plain=
                            strconv.unicodeToUTF(account.getBareJid())
                            +(char)0x00
                            +strconv.unicodeToUTF(account.getUserName())
                            +(char)0x00
                            +strconv.unicodeToUTF(account.getPassword());
                    auth.setText(strconv.toBase64(plain));
                    
                    stream.send(auth);
                    listener.loginMessage(SR.MS_AUTH);
                    return JabberBlockListener.BLOCK_PROCESSED;
                }
                // no more method found
                listener.loginFailed("SASL: Unknown mechanisms");
                return JabberBlockListener.NO_MORE_BLOCKS;
                
            } 
            // second stream - step 1. binding resource
            else if (data.getChildBlock("bind")!=null) {
                JabberDataBlock bindIq=new Iq(null, Iq.TYPE_SET, "bind");
                JabberDataBlock bind=bindIq.addChildNs("bind", "urn:ietf:params:xml:ns:xmpp-bind");
                bind.addChild("resource", account.getResource());
                stream.send(bindIq);

                listener.loginMessage(SR.MS_RESOURCE_BINDING);
                
                return JabberBlockListener.BLOCK_PROCESSED;
            }
            listener.loginFailed("Server does not support SASL"); 
            return JabberBlockListener.NO_MORE_BLOCKS; 
        } else if (data.getTagName().equals("challenge")) {
            // first stream - step 2,3. reaction to challenges
            
            String challenge=decodeBase64(data.getText());
            //System.out.println(challenge);
            
            JabberDataBlock resp=new JabberDataBlock("response", null, null);
            resp.setNameSpace("urn:ietf:params:xml:ns:xmpp-sasl");
            
            int nonceIndex=challenge.indexOf("nonce=");
                // first stream - step 2. generating DIGEST-MD5 response due to challenge
            if (nonceIndex>=0) {
                nonceIndex+=7;
                String nonce=challenge.substring(nonceIndex, challenge.indexOf('\"', nonceIndex));
                String cnonce="123456789abcd";
                
                resp.setText(responseMd5Digest(
                        strconv.unicodeToUTF(account.getUserName()),
                        strconv.unicodeToUTF(account.getPassword()),
                        account.getServer(),
                        "xmpp/"+account.getServer(),
                        nonce,
                        cnonce ));
                //System.out.println(resp.toString());
            }
                // first stream - step 3. sending second empty response due to second challenge
            //if (challenge.startsWith("rspauth")) {}
                
            stream.send(resp);
            return JabberBlockListener.BLOCK_PROCESSED;
        }
//#if ZLIB
        else if ( data.getTagName().equals("compressed")) {
            stream.setZlibCompression();
            try {
                stream.initiateStream();
            } catch (IOException ex) { }
            return JabberBlockListener.NO_MORE_BLOCKS;
        }
        
//#endif
            
        else if ( data.getTagName().equals("failure")) {
            // first stream - step 4a. not authorized
            listener.loginFailed( data.getText()+"failure" );  
        } else if ( data.getTagName().equals("success")) {
            // first stream - step 4b. success.
            try {
                stream.initiateStream();
            } catch (IOException ex) { }
            return JabberBlockListener.NO_MORE_BLOCKS; // at first stream
        }

        if (data instanceof Iq) {
            if (data.getTypeAttribute().equals("result")) {
                // second stream - step 2. resource binded - opening session
                if (data.getAttribute("id").equals("bind")) {
                    String myJid=data.getChildBlock("bind").getChildBlockText("jid");
                    listener.bindResource(myJid);
                    JabberDataBlock session=new Iq(null, Iq.TYPE_SET, "sess");
                    session.addChildNs("session", "urn:ietf:params:xml:ns:xmpp-session");
                    stream.send(session);
                    listener.loginMessage(SR.MS_SESSION);
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
    
    private String decodeBase64(String src)  {
        int len=0;
        int ibuf=1;
        StringBuffer out=new StringBuffer();
        
        for (int i=0; i<src.length(); i++) {
            int nextChar = src.charAt(i);
            int base64=-1;
            if (nextChar>'A'-1 && nextChar<'Z'+1) base64=nextChar-'A';
            else if (nextChar>'a'-1 && nextChar<'z'+1) base64=nextChar+26-'a';
            else if (nextChar>'0'-1 && nextChar<'9'+1) base64=nextChar+52-'0';
            else if (nextChar=='+') base64=62;
            else if (nextChar=='/') base64=63;
            else if (nextChar=='=') {base64=0; len++;} else if (nextChar=='<') break;
            if (base64>=0) ibuf=(ibuf<<6)+base64;
            if (ibuf>=0x01000000){
                out.append( (char)((ibuf>>16) &0xff) );
                if (len<2) out.append( (char)((ibuf>>8) &0xff) );
                if (len==0) out.append( (char)(ibuf &0xff) );
                //len+=3;
                ibuf=1;
            }
        }
        return out.toString();
    }

    /**
     * This routine generates MD5-DIGEST response via SASL specification
     * @param user
     * @param pass
     * @param realm
     * @param digest_uri
     * @param nonce
     * @param cnonce
     * @return
     */
    private String responseMd5Digest(String user, String pass, String realm, String digestUri, String nonce, String cnonce) {

        MD5 hUserRealmPass=new MD5();
        hUserRealmPass.init();
        hUserRealmPass.updateASCII(user);
        hUserRealmPass.update((byte)':');
        hUserRealmPass.updateASCII(realm);
        hUserRealmPass.update((byte)':');
        hUserRealmPass.updateASCII(pass);
        hUserRealmPass.finish();
        
        MD5 hA1=new MD5();
        hA1.init();
        hA1.update(hUserRealmPass.getDigestBits());
        hA1.update((byte)':');
        hA1.updateASCII(nonce);
        hA1.update((byte)':');
        hA1.updateASCII(cnonce);
        hA1.finish();
        
        MD5 hA2=new MD5();
        hA2.init();
        hA2.updateASCII("AUTHENTICATE:");
        hA2.updateASCII(digestUri);
        hA2.finish();
        
        MD5 hResp=new MD5();
        hResp.init();
        hResp.updateASCII(hA1.getDigestHex());
        hResp.update((byte)':');
        hResp.updateASCII(nonce);
        hResp.updateASCII(":00000001:");
        hResp.updateASCII(cnonce);
        hResp.updateASCII(":auth:");
        hResp.updateASCII(hA2.getDigestHex());
        hResp.finish();
        
        String out = "username=\""+user+"\",realm=\""+realm+"\"," +
                "nonce=\""+nonce+"\",nc=00000001,cnonce=\""+cnonce+"\"," +
                "qop=auth,digest-uri=\""+digestUri+"\"," +
                "response=\""+hResp.getDigestHex()+"\",charset=utf-8";
        String resp = strconv.toBase64(out);
        //System.out.println(decodeBase64(resp));
        
        return resp;
    }
    
    
//#if SASL_XGOOGLETOKEN
    private String readLine(InputStream is) {
        StringBuffer buf = new StringBuffer();
        try {
            while(true) {
                int ch = is.read();
                if (ch==-1 || ch == '\n') break;
                buf.append((char)ch);
            }
        } catch (Exception e) {}
        return buf.toString();
    }
    
    /**
     * Generates X-GOOGLE-TOKEN response by communication with http://www.google.com
     * (algorithm from MGTalk/NetworkThread.java)
     * @param userName
     * @param passwd
     * @return
     */
    public String responseXGoogleToken() {
        try {
            String firstUrl = "https://www.google.com:443/accounts/ClientAuth?Email="
                    + strconv.unicodeToUTF(account.getUserName()) + "%40"+ account.getServer()
                    + "&Passwd=" + strconv.unicodeToUTF(account.getPassword()) 
                    + "&PersistentCookie=false&source=googletalk";
            
            //log.addMessage("Connecting to www.google.com");
            HttpConnection c = (HttpConnection) Connector.open(firstUrl.toString());
            InputStream is = c.openInputStream();
            
            
            String sid = readLine(is);
            if(!sid.startsWith("SID=")) {
                throw new SecurityException(SR.MS_LOGIN_FAILED);
            }
            
            String lsid = readLine(is);
            
            String secondUrl = "https://www.google.com:443/accounts/IssueAuthToken?"
                    + sid + "&" + lsid + "&service=mail&Session=true";
            is.close();
            c.close();
            //log.addMessage("Next www.google.com connection");
            c = (HttpConnection) Connector.open(secondUrl);
            is = c.openInputStream();
            //str = readLine(dis);
            String token = "\0"+strconv.unicodeToUTF(account.getUserName())+"\0"+readLine(is);
            is.close();
            c.close();
            return strconv.toBase64(token);
        } catch (javax.microedition.pki.CertificateException e) {
            throw new SecurityException(e.getMessage());
        } catch (SecurityException e) {
            throw e;
        } catch(Exception e) {
            e.printStackTrace();
            listener.loginFailed("Google token error");
        }
        return null;
    }
//#endif
    
}
