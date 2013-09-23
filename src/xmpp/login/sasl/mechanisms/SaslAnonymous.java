/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xmpp.login.sasl.mechanisms;

import xmpp.Jid;
import xmpp.login.sasl.SaslMechanism;

/**
 *
 * @author Vitaly
 */
public class SaslAnonymous extends SaslMechanism {

    public String getName() {
        return "ANONYMOUS";
    }

    public String init(Jid Jid, String password) {
        return "";
    }

    public String response(String challenge) throws Exception {
        return "";
    }

    public boolean success(String success) {
        return true;
    }
    
}
