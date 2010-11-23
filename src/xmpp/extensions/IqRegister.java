/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package xmpp.extensions;

import com.alsutton.jabber.JabberBlockListener;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.datablocks.Iq;
import xmpp.XmppError;

/**
 * Implementation of XEP-0077: In-Band Registration with bits of
 *                   XEP-0158, 4. Extended In-Band Registration
 * @author Vitaly
 */
public class IqRegister implements JabberBlockListener {

    public final static String NS_REGS = "jabber:iq:register";

    public IqRegister(RegistrationListener notifier) {
        this.notifier = notifier;
    }

    private RegistrationListener notifier;

    public int blockArrived(JabberDataBlock data) {
        if (data instanceof Iq) {
            JabberDataBlock query = data.getChildBlock("query");
            if (query.isJabberNameSpace(NS_REGS)) {
                String type = data.getTypeAttribute();
                if (type.equals("result")) {
                    String id = data.getAttribute("id");
                    if (id.startsWith("regac")) {
                        notifier.registrationFormNotify(data);
                    } else {
                        notifier.registrationSuccess();
                    }
                } else if (type.equals("error")) {
                    notifier.registrationFailed(XmppError.findInStanza(data).toString());
                }
                return BLOCK_PROCESSED;
            }
        }
        return BLOCK_REJECTED;
    }

    public interface RegistrationListener {
        public void registrationFormNotify(JabberDataBlock data);
        public void registrationSuccess();
        public void registrationFailed(String errorText);
    };
}
