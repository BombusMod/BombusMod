/*
 * EntityCaps.java
 *
 * Created on 17 Июнь 2007 г., 2:58
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package xmpp;

import Client.*;
import Info.Version;
import com.alsutton.jabber.JabberBlockListener;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.datablocks.Iq;
//#if (android)
//# import java.security.MessageDigest;
//#else
import com.ssttr.crypto.MessageDigest;
//#endif
import java.util.Vector;
import util.Strconv;

/**
 *
 * @author Evg_S
 */
public class EntityCaps implements JabberBlockListener {

    /**
     * Creates a new instance of EntityCaps
     */
    public EntityCaps() {
        initCaps();
    }

    public int blockArrived(JabberDataBlock data) {
        if (!(data instanceof Iq)) {
            return BLOCK_REJECTED;
        }
        if (!data.getTypeAttribute().equals("get")) {
            return BLOCK_REJECTED;
        }

        JabberDataBlock query = data.findNamespace("query", "http://jabber.org/protocol/disco#info");
        if (query == null) {
            return BLOCK_REJECTED;
        }
        String node = query.getAttribute("node");

        if (node != null) {
            try {
                if (!node.equals(BOMBUS_NAMESPACE + "#" + calcVerHash())) {
                    return BLOCK_REJECTED;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return BLOCK_REJECTED;
            }
        }

        JabberDataBlock result = new Iq(data.getAttribute("from"), Iq.TYPE_RESULT, data.getAttribute("id"));
        result.addChild(query);

        JabberDataBlock identity = query.addChild("identity", null);
        identity.setAttribute("category", BOMBUS_ID_CATEGORY);
        identity.setAttribute("type", BOMBUS_ID_TYPE);
        identity.setAttribute("name", Version.getName());

        int j = features.size();
        for (int i = 0; i < j; i++) {
            query.addChild("feature", null).setAttribute("var", (String) features.elementAt(i));
        }

        StaticData.getInstance().theStream.send(result);

        return BLOCK_PROCESSED;
    }
    public static String ver = null;

    public static String calcVerHash() throws Exception {
        if (ver != null) {
            return ver;
        }
        int j = features.size();
        if (j < 1) {
            initCaps();
        }

        MessageDigest sha1 = MessageDigest.getInstance("SHA-1");

        //indentity
        StringBuffer identity = new StringBuffer();
        identity.append(BOMBUS_ID_CATEGORY).append("/").append(BOMBUS_ID_TYPE)
                .append("//").append(Version.getName()).append("<");

        for (int i = 0; i < j; i++) {
            identity.append((String) features.elementAt(i));
            identity.append("<");
        }
        sha1.update(identity.toString().getBytes(), 0, identity.length());
        byte[] sha1bits = new byte[20];
        sha1.digest(sha1bits, 0, sha1bits.length);
        ver = Strconv.toBase64(sha1bits, sha1bits.length);

        return ver;
    }

    public static JabberDataBlock presenceEntityCaps() {
        try {
            JabberDataBlock c = new JabberDataBlock("c");
            c.setAttribute("xmlns", "http://jabber.org/protocol/caps");
            c.setAttribute("node", BOMBUS_NAMESPACE);//+'#'+Version.getVersionNumber());
            c.setAttribute("ver", calcVerHash());
            c.setAttribute("hash", "sha-1");
            return c;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    private final static String BOMBUS_NAMESPACE = Version.getUrl() + "/caps";
    private final static String BOMBUS_ID_CATEGORY = "client";
    private final static String BOMBUS_ID_TYPE = "mobile";
    private static Config cf = Config.getInstance();

    public static void initCaps() {
        ver = null;
        features = null;
        features = new Vector();

        //features MUST be sorted
//#ifdef PEP_ACTIVITY
//#         if (cf.rcvactivity) {
//#             features.addElement("http://jabber.org/protocol/activity");
//#             features.addElement("http://jabber.org/protocol/activity+notify");
//#         }
//#endif
//#ifdef FILE_TRANSFER
//#         if (cf.fileTransfer) {
//#             features.addElement("http://jabber.org/protocol/bytestreams");
//#         }
        //#endif        
        if (cf.eventComposing) {
            features.addElement("http://jabber.org/protocol/chatstates"); //xep-0085
        }//#ifdef ADHOC
        if (cf.adhoc) {
            features.addElement("http://jabber.org/protocol/commands"); //xep-0050
        }//#endif
        features.addElement("http://jabber.org/protocol/disco#info");
        features.addElement("http://jabber.org/protocol/disco#items");
//#ifdef PEP_LOCATION
//#         if (cf.rcvloc) {
//#             features.addElement("http://jabber.org/protocol/geoloc");
//#             features.addElement("http://jabber.org/protocol/geoloc+notify");
//#         }
//#endif

//#ifdef FILE_TRANSFER
//#         if (cf.fileTransfer) {
//#             features.addElement("http://jabber.org/protocol/ibb");            
//#         }
        //#endif
//#ifdef PEP
//#         if (cf.sndrcvmood) {
//#             features.addElement("http://jabber.org/protocol/mood");
//#             features.addElement("http://jabber.org/protocol/mood+notify");
//#         }
//#endif
        features.addElement("http://jabber.org/protocol/rosterx"); // XEP-0144
//#ifdef FILE_TRANSFER
//#         if (cf.fileTransfer) {
//#             features.addElement("http://jabber.org/protocol/si");
//#             features.addElement("http://jabber.org/protocol/si/profile/file-transfer");
//#         }
        //#endif
//#ifdef PEP
//#ifdef PEP_TUNE
//#         if (cf.rcvtune) {
//#             features.addElement("http://jabber.org/protocol/tune");
//#             features.addElement("http://jabber.org/protocol/tune+notify");
//#         }
//#endif
//#endif
        features.addElement("jabber:iq:last");
        features.addElement("jabber:iq:privacy");
        features.addElement("jabber:iq:roster");
        features.addElement("jabber:iq:time"); //DEPRECATED
        features.addElement("jabber:iq:version");
        // features.addElement("jabber:x:data"); we didn't support direct data forms
        //"jabber:x:event", //DEPRECATED
        features.addElement("jabber:x:oob");
        features.addElement("urn:xmpp:ping");
        if (cf.eventDelivery) {
            features.addElement("urn:xmpp:receipts"); //xep-0184
        }
        features.addElement("urn:xmpp:time");

        //sort(features);
    }
    private static Vector features = new Vector();
}
