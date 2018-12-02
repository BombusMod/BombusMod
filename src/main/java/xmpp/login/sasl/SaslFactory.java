/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xmpp.login.sasl;

import Client.Config;
import com.alsutton.jabber.JabberStream;
import java.util.Vector;
import xmpp.Account;
import xmpp.login.sasl.mechanisms.SaslAnonymous;
import xmpp.login.sasl.mechanisms.SaslDigestMd5;
import xmpp.login.sasl.mechanisms.SaslPlain;
import xmpp.login.sasl.mechanisms.SaslScramSha1;

/**
 *
 * @author Vitaly
 */
public class SaslFactory {
    public final static String NS_SASL = "urn:ietf:params:xml:ns:xmpp-sasl";
    public static SaslMechanism getPreferredMechanism(Account account, JabberStream stream, Vector availableMechanisms) {
        if (availableMechanisms.contains("SCRAM-SHA-1")) {
            return new SaslScramSha1();
        }
        if (availableMechanisms.contains("DIGEST-MD5")) {
            return new SaslDigestMd5();
        }
        if (availableMechanisms.contains("PLAIN") && (account.plainAuth || stream.isSecured())) {
            return new SaslPlain();
        }
        if (availableMechanisms.contains("ANONYMOUS")) {
            return new SaslAnonymous();
        }
        return null;
    }
}
