/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xmpp.login.sasl;

import xmpp.login.sasl.mechanisms.SaslPlain;
import xmpp.login.sasl.mechanisms.SaslGoogleToken;
import xmpp.login.sasl.mechanisms.SaslDigestMd5;
import xmpp.login.sasl.mechanisms.SaslScramSha1;
import Client.Config;
import java.util.Vector;
import xmpp.Account;

/**
 *
 * @author Vitaly
 */
public class SaslFactory {
    public final static String NS_SASL = "urn:ietf:params:xml:ns:xmpp-sasl";
    public static SaslMechanism getPreferredMechanism(Account account, Vector availableMechanisms) {
        if (availableMechanisms.contains("X-GOOGLE-TOKEN")) {
            account.isGoogle = true;
            return new SaslGoogleToken();
        }
        if (availableMechanisms.contains("SCRAM-SHA-1")) {
            // prefer another mechanism on legacy devices
            if (Config.getInstance().phoneManufacturer == Config.MICROEMU)
                return new SaslScramSha1();
        }
        if (availableMechanisms.contains("DIGEST-MD5")) {
            return new SaslDigestMd5();
        }
        if (availableMechanisms.contains("PLAIN") && account.plainAuth) {
            return new SaslPlain();
        }
        if (availableMechanisms.contains("SCRAM-SHA-1")) {            
            return new SaslScramSha1();
        }
        return null;
    }
}
