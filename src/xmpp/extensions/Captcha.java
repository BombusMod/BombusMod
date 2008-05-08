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
import com.alsutton.jabber.JabberBlockListener;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.datablocks.*;
import javax.microedition.lcdui.Display;

/**
 *
 * @author root
 */
public class Captcha implements JabberBlockListener, XDataForm.NotifyListener{

    private Display display;
    
    private String from;
    private String id;
    
    /** Creates a new instance of Captcha */
    public Captcha(Display display) {
        this.display=display;
    }

    public int blockArrived(JabberDataBlock data) {
        if (data instanceof Message) {
     
            JabberDataBlock challenge=data.findNamespace("challenge", "urn:xmpp:tmp:challenge");
            if (challenge==null) return BLOCK_REJECTED;

            JabberDataBlock xdata=challenge.findNamespace("x","jabber:x:data");

            from=data.getAttribute("from");
            id=data.getAttribute("id");

            new XDataForm(display, xdata, this);

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

    public void XDataFormSubmit(JabberDataBlock form) {
        JabberDataBlock reply=new Iq(from, Iq.TYPE_SET, id);
        reply.addChildNs("challenge", "urn:xmpp:tmp:challenge").addChild(form);
        
        StaticData.getInstance().roster.theStream.send(reply);
    }
    
}
