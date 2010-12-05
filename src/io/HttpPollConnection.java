/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package io;

import com.ssttr.crypto.MessageDigest;
import com.ssttr.crypto.SHA1;
import java.util.Vector;

/**
 *
 * @author Vitaly
 */
public class HttpPollConnection extends HttpXmppConnection {

    public HttpPollConnection(String host, String pollingUrl) {
        super(host, pollingUrl);
        contentType = "application/x-www-form-urlencoded";
    }

    protected void parseCookies(String cookie) {
            int expires=cookie.indexOf(';');
            if (expires>0) cookie=cookie.substring(3, expires);

            if (cookie.endsWith(":0")) {
                opened=false;
                error=cookie;
            }

            if (sessionId==null) {
                sessionId=cookie;
            }
    }
    
    protected String wrap(String postData) {
    StringBuffer out = new StringBuffer();
        if (sessionId == null) {
            out.append("0");
            keys = new Vector();
        } else {
            out.append(sessionId);
        }

        do {
            if (keys.isEmpty()) {
                initKeys();
            }
            out.append(";").append((String) keys.lastElement());
            keys.removeElementAt(keys.size() - 1);
        } while (keys.isEmpty());


        out.append(",").append(postData);
        return out.toString();
    }

    private void initKeys() {
        String k0="magick";
        while (keys.size()<6) {
            MessageDigest sha1=new SHA1();
            sha1.init();
            sha1.updateASCII(k0);
            sha1.finish();
            k0=sha1.getDigestBase64();

            keys.addElement(k0);
        }
    }

}
