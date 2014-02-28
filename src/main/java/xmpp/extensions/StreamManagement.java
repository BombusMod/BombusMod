/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xmpp.extensions;

import Client.StaticData;
import com.alsutton.jabber.JabberBlockListener;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.JabberStream;
import locale.SR;

/**
 *
 * @author Vitaly
 */
public class StreamManagement implements JabberBlockListener {
    // XEP-0198
    public static final String NS_SM = "urn:xmpp:sm:3";
    JabberStream stream = StaticData.getInstance().getTheStream();
  
    public int blockArrived(JabberDataBlock dataBlock) {

        if (dataBlock.isJabberNameSpace(NS_SM)) {
            if (dataBlock.getTagName().equals("r")) {
                JabberDataBlock ack = new JabberDataBlock("a");
                ack.setNameSpace(NS_SM);
                ack.setAttribute("h", String.valueOf(stream.getStanzasRecv()));
                stream.send(ack);
            }
            if (dataBlock.getTagName().equals("enabled")) {
                stream.setReliable(true);
                if (dataBlock.getAttribute("resume") != null) {
                    stream.setResumptionAllowed(true);
                    stream.setReliableSessionId(dataBlock.getAttribute("id"));
                }
            }
            if (dataBlock.getTagName().equals("resumed")) {
                stream.loggedIn = true;
                StaticData.getInstance().roster.currentReconnect = 0;
                StaticData.getInstance().roster.setQuerySign(false);
                StaticData.getInstance().roster.setProgress(SR.MS_CONNECTED, 100);
                StaticData.getInstance().roster.reEnumRoster();
            }

            // TODO: process <a/>
            return BLOCK_PROCESSED;
        }
        return BLOCK_REJECTED;
    }
    
    public static JabberDataBlock enable() {
        JabberDataBlock enable = new JabberDataBlock("enable");
        enable.setNameSpace(NS_SM);
        enable.setAttribute("resume", "true");
        return enable;
    }
    
}
