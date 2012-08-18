/*
 * NonSASLAuth.java
 *
 * Created on 8.06.2006, 22:16
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

import Account.Account;
import com.alsutton.jabber.JabberBlockListener;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.JabberStream;
import com.alsutton.jabber.datablocks.Iq;
//#if (android)
//# import java.security.MessageDigest;
//#else
import com.ssttr.crypto.MessageDigest;
//#endif
import locale.SR;
import util.Strconv;
import util.StringUtils;
import xmpp.XmppError;

/**
 *
 * @author evgs
 */
public class NonSASLAuth implements JabberBlockListener{
    
    private LoginListener listener;
    
    private Account account;

    private JabberStream stream;
    
    /** Creates a new instance of NonSASLAuth */
    public NonSASLAuth(Account account, LoginListener listener, JabberStream stream) {
        this.listener=listener;
        this.account=account;
        this.stream=stream;
        
        stream.addBlockListener(this);
        
        jabberIqAuth(AUTH_GET);
       
        listener.loginMessage(SR.MS_AUTH, 42);
    }

    private final static int AUTH_GET=0;
    private final static int AUTH_PASSWORD=1;
    private final static int AUTH_DIGEST=2;
    
    private void jabberIqAuth(int authType) {
        int type=Iq.TYPE_GET;
        String id="auth-1";
        
        JabberDataBlock query = new JabberDataBlock("query");
        query.setNameSpace( "jabber:iq:auth" );
        query.addChild( "username", account.userName );
        
        switch (authType) {
            case AUTH_DIGEST:
                MessageDigest sha = null;
                try {
                    sha = MessageDigest.getInstance("SHA-1");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                byte[] session = stream.getSessionId().getBytes();
                sha.update(session, 0, session.length);
                byte[] pass = Strconv.unicodeToUTF(account.password).getBytes();
                sha.update(pass, 0, pass.length );
                byte[] sha1 = new byte[20];
                try {
                    sha.digest(sha1, 0, sha1.length);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                query.addChild("digest", StringUtils.getDigestHex(sha1) );

                query.addChild( "resource", account.resource );
                type=Iq.TYPE_SET;
                id="auth-s";
                break;
                
            case AUTH_PASSWORD:
                query.addChild("password", account.password );
                query.addChild( "resource", account.resource );
                type=Iq.TYPE_SET;
                id="auth-s";
                break;
        }

        
        Iq auth=new Iq(account.server, type, id);
        auth.addChild(query);
        
        stream.send(auth);
    }
    public int blockArrived(JabberDataBlock data) {
        try {
            if( data instanceof Iq ) {
                String type = (String) data.getTypeAttribute();
                String id=(String) data.getAttribute("id");
                if ( id.equals("auth-s") ) {
                    if (type.equals( "error" )) {
                        // Authorization error
                        listener.loginFailed( XmppError.findInStanza(data).toString() );
                        
                        return JabberBlockListener.NO_MORE_BLOCKS;
                    } else if (type.equals( "result")) {
                        listener.loginSuccess();
                        return JabberBlockListener.NO_MORE_BLOCKS;
                    }
                }
                if (id.equals("auth-1")) {
                    try {
                        JabberDataBlock query=data.getChildBlock("query");
                        
                        if (query.getChildBlock("digest")!=null) {
                            jabberIqAuth(AUTH_DIGEST);
                            return JabberBlockListener.BLOCK_PROCESSED;
                        } 
                        
                        if (query.getChildBlock("password")!=null) {
                            if (!account.plainAuth) {
                                listener.loginFailed("Plain auth required");
                                return JabberBlockListener.NO_MORE_BLOCKS;
                            }
                            jabberIqAuth(AUTH_PASSWORD);
                            return JabberBlockListener.BLOCK_PROCESSED;
                        } 
                        
                        listener.loginFailed("Unknown mechanism");
                        
                    } catch (Exception e) { listener.loginFailed(e.toString()); }
                    return JabberBlockListener.NO_MORE_BLOCKS;
                }
            }
            
        } catch (Exception e) { }
        return JabberBlockListener.BLOCK_REJECTED;        
    }
    
}
