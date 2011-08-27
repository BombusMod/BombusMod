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
    
    private String from;
    private String id;

    /** Creates a new instance of Captcha */
    public Captcha() {

    }

    public static String NS_CAPTCHA = "urn:xmpp:captcha";

    public int blockArrived(JabberDataBlock data) {
        if (data instanceof Message) {
     
            JabberDataBlock challenge = data.findNamespace("captcha", NS_CAPTCHA);
            if (challenge==null) return BLOCK_REJECTED;            

            from = data.getAttribute("from");
            id = data.getAttribute("id");

            DiscoForm f = new DiscoForm(null, null, from, challenge, StaticData.getInstance().theStream, id, null);
	    f.fetchMediaElements(data.getChildBlocks());

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
}
