/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package xmpp.extensions;

import com.alsutton.jabber.JabberBlockListener;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.datablocks.Iq;
import java.util.Vector;
import xmpp.XmppError;
import xmpp.extensions.XDataForm.NotifyListener;

/**
 * Implementation of XEP-0077: In-Band Registration with bits of
 *                   XEP-0158, 4. Extended In-Band Registration
 * @author Vitaly
 */
public class IqRegister implements JabberBlockListener, NotifyListener {

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
                    // trying to find jabber:x:data
                    JabberDataBlock xData = query.findNamespace("x", XDataForm.NS_XDATA);
                    if (xData != null) {
                        // modern DataForms registration form
                        new XDataForm(xData, this);
                        
                    } else {
                        // plain In-Band Registration
                        Vector childs = query.getChildBlocks();
                        Vector formItems = new Vector();
                        if (childs != null) {
                            int size = childs.size();
                            String title = null, instructions = null;
                            for (int i = 0; i < size; i++) {
                                JabberDataBlock current = (JabberDataBlock) childs.elementAt(i);
                                String label = current.getTagName();
                                // TODO: check field names with XML Schema
                                if (label.equals("title")) {
                                    title = current.getText();
                                } else if (label.equals("instructions")) {
                                    instructions = current.getText();
                                } else {
                                    formItems.addElement(label);
                                }
                            }
                            if (size > 0) {
                                notifier.registrationFormNotify(title, instructions, formItems);
                            }
                        }
                    }                    
                } else if (type.equals("error")) {
                    notifier.registrationFailed(XmppError.findInStanza(data).toString());
                }
                return BLOCK_PROCESSED;
            }
        }
        return BLOCK_REJECTED;
    }

    public void XDataFormSubmit(JabberDataBlock form) {
    }

    public interface RegistrationListener {
        public void registrationFormNotify(String title, String instructions, Vector registrationFields);
        public void registrationSuccess();
        public void registrationFailed(String errorText);
    };
}
