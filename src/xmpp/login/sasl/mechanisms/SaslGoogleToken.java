/*
 * GoogleTokenAuth.java
 *
 * Created on 9.08.2008, 18.47
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
package xmpp.login.sasl.mechanisms;

import java.io.InputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import locale.SR;
import util.Strconv;
import util.StringUtils;
import xmpp.Jid;
import xmpp.login.sasl.SaslMechanism;

/**
 *
 * @author root
 */
public class SaslGoogleToken extends SaslMechanism {

    private String readLine(InputStream is) {
        StringBuffer buf = new StringBuffer();
        try {
            while (true) {
                int ch = is.read();
                if (ch == -1 || ch == '\n') {
                    break;
                }
                buf.append((char) ch);
            }
        } catch (Exception e) {
        }
        return buf.toString();
    }

    public String init(Jid jid, String password) {
        this.jid = jid;
        pass = password;
        try {
            String firstUrl = "https://www.google.com:443/accounts/ClientAuth?Email="
                    + StringUtils.urlPrep(Strconv.unicodeToUTF(jid.getBare()))
                    + "&Passwd=" + StringUtils.urlPrep(Strconv.unicodeToUTF(password))
                    + "&PersistentCookie=false&source=googletalk";
            //log.addMessage("Connecting to www.google.com");
            HttpConnection c = (HttpConnection) Connector.open(firstUrl.toString());
            InputStream is = c.openInputStream();


            String sid = readLine(is);
            if (!sid.startsWith("SID=")) {
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
            StringBuffer token = new StringBuffer();
            token.append((char) 0).append(Strconv.unicodeToUTF(jid.getNode())).append((char) 0).append(readLine(is));
            is.close();
            c.close();
            return token.toString();

        } catch (javax.microedition.pki.CertificateException e) {
            throw new SecurityException(e.getMessage());
        } catch (SecurityException e) {
            throw e;
        } catch (Exception e) {
            //listener.loginFailed("Google token error");
        }
        return null;
    }

    public String response(String challenge) {
        return "";
    }

    public String getName() {
        return "X-GOOGLE-TOKEN";
    }

    public boolean success(String success) {
        return true;
    }
}
