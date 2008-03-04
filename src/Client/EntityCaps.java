/*
 * EntityCaps.java
 *
 * Created on 17 �?юнь 2007 г., 2:58
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package Client;

import Info.Version;
import com.alsutton.jabber.JabberBlockListener;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.datablocks.Iq;
import com.ssttr.crypto.SHA1;

/**
 *
 * @author Evg_S
 */
public class EntityCaps implements JabberBlockListener{
    
    /** Creates a new instance of EntityCaps */
    public EntityCaps() {}
    public int blockArrived(JabberDataBlock data) {
            if (!(data instanceof Iq)) return BLOCK_REJECTED;
            if (!data.getTypeAttribute().equals("get")) return BLOCK_REJECTED;

            JabberDataBlock query=data.findNamespace("query", "http://jabber.org/protocol/disco#info");
            if (query==null) return BLOCK_REJECTED;
            String node=query.getAttribute("node");

        if (node!=null)
            if (!node.equals(BOMBUS_NAMESPACE+"#"+calcVerHash()))
                return BLOCK_REJECTED;


            JabberDataBlock result=new Iq(data.getAttribute("from"), Iq.TYPE_RESULT, data.getAttribute("id"));
            result.addChild(query);

            JabberDataBlock identity=query.addChild("identity", null);
        identity.setAttribute("category", BOMBUS_ID_CATEGORY);
        identity.setAttribute("type", BOMBUS_ID_TYPE);
        identity.setAttribute("name", Version.NAME);

        for (int i=0; i<features.length; i++) {
            query.addChild("feature", null).setAttribute("var",features[i]);
        }
        
        StaticData.getInstance().roster.theStream.send(result);
        
        return BLOCK_PROCESSED;
    }
	
    public static String ver=null;
    
    public static String calcVerHash() {
        if (ver!=null) return ver;
        
        SHA1 sha1=new SHA1();
        sha1.init();
        
        //indentity
        sha1.update(BOMBUS_ID_CATEGORY+"/"+BOMBUS_ID_TYPE+"//");
        sha1.update(Version.NAME);
        sha1.update("<");
        
        for (int i=0; i<features.length; i++) {
            sha1.update(features[i]);
            sha1.update("<");
        }
        
        sha1.finish();
        ver=sha1.getDigestBase64();
        
        return ver;
    }

    public static JabberDataBlock presenceEntityCaps() {
        JabberDataBlock c=new JabberDataBlock("c", null, null);
        c.setAttribute("xmlns", "http://jabber.org/protocol/caps");
        c.setAttribute("node", BOMBUS_NAMESPACE+'#'+Version.getVersionNumber());
        c.setAttribute("ver", calcVerHash());
        c.setAttribute("hash", "sha-1");
        c.setAttribute("ext", "ep-notify");
        return c;
    }
    
    private final static String BOMBUS_NAMESPACE=Version.getUrl();
    private final static String BOMBUS_ID_CATEGORY="client";
    private final static String BOMBUS_ID_TYPE="mobile";
    
//features MUST be sorted
    private final static String features[]={
        "http://jabber.org/protocol/chatstates", //xep-0085
        "http://jabber.org/protocol/disco#info",
	"http://jabber.org/protocol/ibb",
        "http://www.xmpp.org/extensions/xep-0199.html#ns",
        "http://jabber.org/protocol/muc",
        "http://jabber.org/protocol/si",
        "http://jabber.org/protocol/si/profile/file-transfer",
        "http://jabber.org/protocol/mood+notify",
        "jabber:iq:time", //DEPRECATED
        "jabber:iq:version",
        "jabber:x:data",
        //"jabber:x:event", //DEPRECATED
        "urn:xmpp:ping",
        "urn:xmpp:receipts", //xep-0184
        "urn:xmpp:time"
    };
}
