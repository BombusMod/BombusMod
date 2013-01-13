/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xmpp.login.sasl.mechanisms;

//#if (android)
//# import java.security.MessageDigest;
//#else
import com.ssttr.crypto.MessageDigest;
//#endif
import util.Strconv;
import util.StringUtils;
import xmpp.Jid;
import xmpp.login.sasl.SaslMechanism;

/**
 *
 * @author Vitaly
 */
public class SaslDigestMd5 extends SaslMechanism {

    private String nonce, cnonce, realm, digestUri;

    public String init(Jid bareJid, String password) {
        this.jid = bareJid;
        this.pass = Strconv.unicodeToUTF(password);
        realm = jid.getServer();
        digestUri = "xmpp/" + realm;
        return "";
    }

    public String response(String challenge) throws Exception {
        MessageDigest hUserRealmPass = MessageDigest.getInstance("MD5");
        StringBuffer userRealm = new StringBuffer();
        userRealm.append(Strconv.unicodeToUTF(jid.getNode())).append(":").append(realm).append(":")
                .append(pass);
        hUserRealmPass.update(userRealm.toString().getBytes(), 0, userRealm.length());
        byte[] userRealmDigest = new byte[16];
        hUserRealmPass.digest(userRealmDigest, 0, userRealmDigest.length);

        int nonceIndex = challenge.indexOf("nonce=");
        // first stream - step 2. generating DIGEST-MD5 response due to challenge
        if (nonceIndex >= 0) {
            nonceIndex += 7;
            nonce = challenge.substring(nonceIndex, challenge.indexOf('\"', nonceIndex));
            cnonce = "123456789abcd";

            MessageDigest hA1 = MessageDigest.getInstance("MD5");
            hA1.update(userRealmDigest, 0, userRealmDigest.length);
            hA1.update(":".getBytes(), 0, 1);
            hA1.update(nonce.getBytes(), 0, nonce.length());
            hA1.update(":".getBytes(), 0, 1);
            hA1.update(cnonce.getBytes(), 0, cnonce.length());
            byte[] hA1bits = new byte[16];
            hA1.digest(hA1bits, 0, hA1bits.length);

            MessageDigest hA2 = MessageDigest.getInstance("MD5");
            StringBuffer authenticate = new StringBuffer();
            authenticate.append("AUTHENTICATE:").append(digestUri);
            hA2.update(authenticate.toString().getBytes(), 0, authenticate.length());
            byte[] hA2bits = new byte[16];
            hA2.digest(hA2bits, 0, hA2bits.length);

            MessageDigest hResp = MessageDigest.getInstance("MD5");
            String hA1hex = StringUtils.getDigestHex(hA1bits);
            String hA2hex = StringUtils.getDigestHex(hA2bits);

            hResp.update(hA1hex.getBytes(), 0, hA1hex.length());
            hResp.update(":".getBytes(), 0, 1);
            hResp.update(nonce.getBytes(), 0, nonce.length());
            hResp.update(":00000001:".getBytes(), 0, 10);
            hResp.update(cnonce.getBytes(), 0, cnonce.length());
            hResp.update(":auth:".getBytes(), 0, 6);
            hResp.update(hA2hex.getBytes(), 0, hA2hex.length());
            byte[] hRespBits = new byte[16];
            hResp.digest(hRespBits, 0, hRespBits.length);

            String out = "username=\"" + jid.getNode() + "\",realm=\"" + realm + "\","
                    + "nonce=\"" + nonce + "\",nc=00000001,cnonce=\"" + cnonce + "\","
                    + "qop=auth,digest-uri=\"" + digestUri + "\","
                    + "response=\"" + StringUtils.getDigestHex(hRespBits) + "\",charset=utf-8";
            return out;
        } else {
            // step 3
            return "";
        }
    }

    public String getName() {
        return "DIGEST-MD5";
    }

    public boolean success(String success) {
        return true;
    }
}
