/*
 * QueryRequestVoice.java
 *
 * Created on 18 Июнь 2007 г., 12:55
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package Conference;

import Client.StaticData;
import ServiceDiscovery.DiscoForm;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.datablocks.Message;
import xmpp.extensions.muc.Conference;

/**
 *
 * @author User
 */
public class QueryRequestVoice {

    private MucContact admin;
    private int action;

    public QueryRequestVoice(MucContact admin, int action) {
        this.admin = admin;
        this.action = action;

        JabberDataBlock msg = new Message(admin.getJid().toString());
        JabberDataBlock x = msg.addChildNs("x", DiscoForm.NS_XDATA);
        x.setTypeAttribute("submit");

        JabberDataBlock fieldType = new JabberDataBlock("field");
        fieldType.setAttribute("var", "FORM_TYPE");

        JabberDataBlock fieldTypeValue = new JabberDataBlock("value");
        fieldTypeValue.setText(Conference.NS_MUC + "#request");

        fieldType.addChild(fieldTypeValue);

        x.addChild(fieldType);


        JabberDataBlock fieldLabel = new JabberDataBlock("field");
        fieldLabel.setAttribute("var", "muc#role");
        fieldLabel.setAttribute("type", "text-single");
        fieldLabel.setAttribute("label", "Requested role");

        JabberDataBlock fieldLabelValue = new JabberDataBlock("value");
        fieldLabelValue.setText("participant");

        fieldLabel.addChild(fieldLabelValue);

        x.addChild(fieldLabel);

        StaticData.getInstance().theStream.send(msg);
    }
}
