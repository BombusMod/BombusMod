/*
 * Captcha.java
 *
 * Created on 6 Май 2008 г., 1:46
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package xmpp.extensions;

import Client.StaticData;
import ServiceDiscovery.DiscoForm;
import com.alsutton.jabber.JabberBlockListener;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.datablocks.*;

/**
 *
 * @author root
 */
public class Captcha implements JabberBlockListener{

    private String from = null;
    private String id = null;
    
    /** Creates a new instance of Captcha */
    public Captcha() {
        
    }

    public int blockArrived(JabberDataBlock data) {
        if (data instanceof Message) {
     
            JabberDataBlock challenge = null;
            challenge = data.findNamespace("captcha", "urn:xmpp:captcha");
            if (challenge==null) return BLOCK_REJECTED;

            JabberDataBlock xdata = null;
            xdata = challenge.findNamespace("x", DiscoForm.NS_XDATA);

            from=data.getAttribute("from");
            id=data.getAttribute("id");

            new DiscoForm(null, null, data, StaticData.getInstance().roster.theStream, id, "x").fetchMediaElements(data.getChildBlocks());

            return BLOCK_PROCESSED;
        }
        
        if (data instanceof Iq) {
            if (!data.getAttribute("id").equals(id)) return BLOCK_REJECTED;
            
            //TODO: error handling
            //if ()
            return BLOCK_PROCESSED;
        }
        
        return BLOCK_REJECTED;
    }
/*
    public void XDataFormSubmit(JabberDataBlock form) {
        JabberDataBlock reply=new Iq(from, Iq.TYPE_SET, id);
        reply.addChildNs("captcha", "urn:xmpp:captcha").addChild(form);
        
        StaticData.getInstance().roster.theStream.send(reply);
    }*/
    
}
