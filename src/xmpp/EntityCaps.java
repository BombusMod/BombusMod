/*
 * EntityCaps.java
 *
 * Created on 17 �?юнь 2007 г., 2:58
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
import com.ssttr.crypto.SHA1;
import java.util.Vector;

/**
 *
 * @author Evg_S
 */
public class EntityCaps implements JabberBlockListener{
    
    /** Creates a new instance of EntityCaps */
    public EntityCaps() {
        initCaps();
    }
    
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
        identity.setAttribute("name", Version.getName());

        for (int i=0; i<features.size(); i++) {
            query.addChild("feature", null).setAttribute("var", (String) features.elementAt(i));
        }
        
        StaticData.getInstance().roster.theStream.send(result);
        
        return BLOCK_PROCESSED;
    }
	
    public static String ver=null;

    public static String calcVerHash() {
        if (ver!=null) return ver;
        if (features.size()<1)
            initCaps();
        
        SHA1 sha1=new SHA1();
        sha1.init();
        
        //indentity
        sha1.update(BOMBUS_ID_CATEGORY+"/"+BOMBUS_ID_TYPE+"//");
        sha1.update(Version.getName());
        sha1.update("<");
        
        for (int i=0; i<features.size(); i++) {
            sha1.update((String) features.elementAt(i));
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

        return c;
    }
    
    private final static String BOMBUS_NAMESPACE=Version.getUrl()+"/caps";
    private final static String BOMBUS_ID_CATEGORY="client";
    private final static String BOMBUS_ID_TYPE="mobile";
    
    private static Config cf=Config.getInstance();
    
    public static void initCaps() {
        ver=null;
        features=null;
        features=new Vector();
        
        //features MUST be sorted
//#ifdef PEP_ACTIVITY
//#         if (cf.rcvactivity) {
//#             features.addElement("http://jabber.org/protocol/activity");
//#             features.addElement("http://jabber.org/protocol/activity+notify");
//#         }
//#endif
        
        if (cf.eventComposing)
            features.addElement("http://jabber.org/protocol/chatstates"); //xep-0085
//#ifdef ADHOC
//#         if (cf.adhoc)
//#             features.addElement("http://jabber.org/protocol/commands"); //xep-0050
//#endif
        features.addElement("http://jabber.org/protocol/disco#info");
//#ifdef FILE_TRANSFER
        if (cf.fileTransfer) {
            features.addElement("http://jabber.org/protocol/ibb");
        }
 //#endif
//#ifdef PEP
//#          if (cf.sndrcvmood) {
//#             features.addElement("http://jabber.org/protocol/mood");
//#             features.addElement("http://jabber.org/protocol/mood+notify");
//#          }
//#endif
//#ifndef WMUC
        features.addElement("http://jabber.org/protocol/muc");
//#endif
//#ifdef FILE_TRANSFER
        if (cf.fileTransfer) {
            features.addElement("http://jabber.org/protocol/si");
            features.addElement("http://jabber.org/protocol/si/profile/file-transfer");
        }
 //#endif
//#ifdef PEP
//#ifdef PEP_TUNE
//#          if (cf.rcvtune) {
//#               features.addElement("http://jabber.org/protocol/tune");
//#               features.addElement("http://jabber.org/protocol/tune+notify");
//#          }
//#endif
//#ifdef PEP_LOCATION
//#         if (cf.rcvloc) {
//#               features.addElement("http://jabber.org/protocol/geoloc");
//#               features.addElement("http://jabber.org/protocol/geoloc+notify");
//#         }
//#endif
//#endif
        features.addElement("jabber:iq:time"); //DEPRECATED
        features.addElement("jabber:iq:version");
       // features.addElement("jabber:x:data"); we didn't support direct data forms
         //"jabber:x:event", //DEPRECATED
        features.addElement("urn:xmpp:ping");
        if (cf.eventDelivery)
            features.addElement("urn:xmpp:receipts"); //xep-0184
        features.addElement("urn:xmpp:time");

        //sort(features);
    }

    private static Vector features=new Vector();
}
