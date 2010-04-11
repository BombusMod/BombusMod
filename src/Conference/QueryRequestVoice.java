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
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.datablocks.Message;
import javax.microedition.lcdui.Display;

/**
 *
 * @author User
 */
public class QueryRequestVoice {

    private MucContact admin;
    private int action;
    private Display display;

    public QueryRequestVoice(Display display, MucContact admin, int action) {
        this.admin=admin;
        this.action=action;
        this.display=display;
        
        JabberDataBlock msg=new Message(admin.getJid());
        JabberDataBlock x=msg.addChildNs("x", "jabber:x:data"); 
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
