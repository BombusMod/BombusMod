/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xmpp;

import Client.Contact;
import Client.StaticData;

/**
 *
 * @author Vitaly
 */
public class JidUtils {

    public static boolean equalsViaJ2J(Jid jid, String jid_str) {
        Jid j = new Jid(jid_str);
        String node = jid.getNode();
        String jnode = j.getNode();
        String jserver = j.getServer();

        return jid.equals(j, false)
                || (node.equals(jnode + "%" + jserver))
                || (node.equals(jnode + "\\40" + jserver));
    }

    public static boolean equalsServerViaJ2J(Jid jid, String jserver) {
        String node = jid.getNode();

        return jid.getServer().equals(jserver)
                || (node.endsWith("%" + jserver))
                || (node.endsWith("\\40" + jserver));
    }

    public static boolean isTransport(Jid jid) {
        if (jid.getBare().length() == 0) {
            return false;
        }
        return jid.getBare().indexOf('@') == -1;
    }
    // TODO: move to Roster
    public static boolean belongsToTransport(Jid jid) {
        Jid j = new Jid(jid.getServer());
        Contact tr = StaticData.getInstance().roster.findContact(j, false);
        if (tr != null) {
            return j.equals(tr.jid, false);
        }
        return false;
    }

    public static String getTransport(Jid jid) {
        try {
            int beginIndex = jid.getBare().indexOf('@') + 1;
            int endIndex = jid.getBare().indexOf('.', beginIndex);
            return jid.getBare().substring(beginIndex, endIndex);
        } catch (Exception e) {
            return "-";
        }
    }
}
