/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package xmpp.login.sasl.mechanisms;

import util.Strconv;
import xmpp.Jid;
import xmpp.login.sasl.SaslMechanism;

/**
 *
 * @author Vitaly
 */
public class SaslPlain extends SaslMechanism {

    public String getName() {
        return "PLAIN";
    }

    public String init(Jid Jid, String password) {
        this.jid = Jid;
        pass = password;
        return Strconv.unicodeToUTF(jid.bareJid)
                + (char) 0x00
                + Strconv.unicodeToUTF(jid.getNode())
                + (char) 0x00
                + Strconv.unicodeToUTF(password);
    }

    public String response(String challenge) throws Exception {
        return "";
    }

    public boolean success(String success) {
        return true;
    }

}
