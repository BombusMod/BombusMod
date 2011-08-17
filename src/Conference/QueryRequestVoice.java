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

/**
 *
 * @author User
 */
public class QueryRequestVoice {

    private MucContact admin;
    private int action;
    
    public QueryRequestVoice(MucContact admin, int action) {
        this.admin=admin;
        this.action=action;
        
        JabberDataBlock msg=new Message(admin.getJid().toString());
        JabberDataBlock x=msg.addChildNs("x", DiscoForm.NS_XDATA);
        x.setTypeAttribute("submit");
        
        JabberDataBlock fieldType=new JabberDataBlock("field", null, null);
        fieldType.setAttribute("var","FORM_TYPE");
        
        JabberDataBlock fieldTypeValue=new JabberDataBlock("value", null, null);
        fieldTypeValue.setText("http://jabber.org/protocol/muc#request");
        
        fieldType.addChild(fieldTypeValue);
        
        x.addChild(fieldType);
        
        
        JabberDataBlock fieldLabel=new JabberDataBlock("field", null, null);
        fieldLabel.setAttribute("var","muc#role");
        fieldLabel.setAttribute("type","text-single");
        fieldLabel.setAttribute("label","Requested role");
        
        JabberDataBlock fieldLabelValue=new JabberDataBlock("value", null, null);
        fieldLabelValue.setText("participant");
        
        fieldLabel.addChild(fieldLabelValue);
        
        x.addChild(fieldLabel);
        
        StaticData.getInstance().roster.theStream.send(msg);
    }
}
