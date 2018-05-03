/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xmpp.login.sasl;

import com.alsutton.jabber.JabberDataBlock;
import java.util.Enumeration;
import java.util.Vector;
import xmpp.Jid;

/**
 *
 * @author Vitaly
 */
public abstract class SaslMechanism {
    protected Jid jid;
    protected String pass;
    
    public abstract String getName();
    public abstract String init(Jid Jid, String password);
    public abstract String response(String challenge) throws Exception;
    public abstract boolean success(String success);
    
    public static Vector parseMechanisms(JabberDataBlock mechanisms) {
        Vector result = new Vector();
        for (Enumeration e = mechanisms.getChildBlocks().elements(); e.hasMoreElements();) {
            JabberDataBlock mech = (JabberDataBlock)e.nextElement();
            result.addElement(mech.getText());            
        }
        return result;
    }
}
