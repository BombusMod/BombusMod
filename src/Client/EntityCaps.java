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
import java.util.Vector;

/**
 *
 * @author Evg_S
 */
public class EntityCaps implements JabberBlockListener{
    
    /** Creates a new instance of EntityCaps */
    public EntityCaps() {
        if (features.size()<1)
            fillFeatures();
    }
    
    public int blockArrived(JabberDataBlock data) {
        if (!(data instanceof Iq)) return BLOCK_REJECTED;
        if (!data.getTypeAttribute().equals("get")) return BLOCK_REJECTED;

        JabberDataBlock query=data.findNamespace("query", "http://jabber.org/protocol/disco#info");
        if (query==null) return BLOCK_REJECTED;
        String node=query.getAttribute("node");

        boolean answerMood = false;
        if (node!=null) {
            if (!node.equals(BOMBUS_NAMESPACE+"#"+calcVerHash()))
                return BLOCK_REJECTED;
        }

        JabberDataBlock result=new Iq(data.getAttribute("from"), Iq.TYPE_RESULT, data.getAttribute("id"));
        result.addChild(query);

        JabberDataBlock identity=query.addChild("identity", null);
        identity.setAttribute("category", BOMBUS_ID_CATEGORY);
        identity.setAttribute("type", BOMBUS_ID_TYPE);
        identity.setAttribute("name", Version.getNameVersion());

        for (int i=0; i<features.size(); i++) {
        query.addChild("feature", null).setAttribute("var",(String)features.elementAt(i));
        }
        
        StaticData.getInstance().roster.theStream.send(result);
        
        return BLOCK_PROCESSED;
    }
	
    public static String ver=null;

    //private static boolean useMoods;
    
    public static String calcVerHash() {
        if (ver!=null) return ver;
        
        SHA1 sha1=new SHA1();
        sha1.init();
        
        //indentity
        sha1.update(BOMBUS_ID_CATEGORY+"/"+BOMBUS_ID_TYPE+"//");
        sha1.update(Version.getNameVersion());
        sha1.update("<");
        
        for (int i=0; i<features.size(); i++) {
            sha1.update((String)features.elementAt(i));
            sha1.update("<");
        }
        
        sha1.finish();
        ver=sha1.getDigestBase64();
        
        return ver;
    }

    public static JabberDataBlock presenceEntityCaps() {
        JabberDataBlock c=new JabberDataBlock("c", null, null);
        c.setAttribute("xmlns", "http://jabber.org/protocol/caps");
        c.setAttribute("node", BOMBUS_NAMESPACE);//+'#'+Version.getVersionNumber());
        c.setAttribute("ver", calcVerHash());
        c.setAttribute("hash", "sha-1");
        
        //ext  	A set of nametokens specifying additional feature bundles; this attribute is deprecated (see the Legacy Format section of this document).  	DEPRECATED
        //if (Config.getInstance().sndrcvmood) c.setAttribute("ext", "ep-notify");
        return c;
    }
    
    private final static String BOMBUS_NAMESPACE=Version.getUrl()+"/caps";
    private final static String BOMBUS_ID_CATEGORY="client";
    private final static String BOMBUS_ID_TYPE="mobile";
    
    
    private static final String initFeatures = "http://jabber.org/protocol/chatstates,http://jabber.org/protocol/disco#info,http://jabber.org/protocol/muc,"
//#ifdef FILE_TRANSFER
//#             +"http://jabber.org/protocol/ibb,http://jabber.org/protocol/si,http://jabber.org/protocol/si/profile/file-transfer,"
//#endif
            +"jabber:iq:time,jabber:iq:version,jabber:x:data,urn:xmpp:ping,urn:xmpp:receipts,urn:xmpp:time,";
    
    private static Vector features=new Vector();

    private static void fillFeatures() {
        try {
            int p=0; int pos=0;
            while (pos<initFeatures.length()) {
               p=initFeatures.indexOf(',', pos);
               String feature=initFeatures.substring(pos, p);
               features.addElement((String)feature);
               pos=p+1;
            }
            if (Config.getInstance().sndrcvmood) {
                if (features.indexOf("http://jabber.org/protocol/mood")<0) {
                    features.addElement("http://jabber.org/protocol/mood");
                    features.addElement("http://jabber.org/protocol/mood+notify");
                }        
            }
            sort(features);
        } catch (Exception ex) { }
    }
    
    public final static void sort(Vector sortVector){
        try {
            synchronized (sortVector) {
                int f, i;
                String left, right;
                
                for (f = 1; f < sortVector.size(); f++) {
                    left=(String)sortVector.elementAt(f);
                    right=(String)sortVector.elementAt(f-1);
                    if ( left.compareTo(right) >=0 ) continue;
                    i = f-1;
                    while (i>=0){
                        right=(String)sortVector.elementAt(i);
                        if (right.compareTo(left) <0) break;
                        sortVector.setElementAt(right,i+1);
                        i--;
                    }
                    sortVector.setElementAt(left,i+1);
                }
            }
        } catch (Exception e) { }
    }
}
