/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package PrivacyLists;

import Client.StaticData;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.datablocks.Iq;
import java.util.Enumeration;
import java.util.Vector;

/**
 *
 * @author Vitaly
 */
public class PrivacyList {
    
//#ifdef PLUGINS
//#     public static String plugin = new String("PLUGIN_PRIVACY");
//#endif    

    String name;
    boolean isActive = false;
    boolean isDefault = false;
    Vector rules = new Vector();

    public PrivacyList(String name) {
        this.name = name;
    }

    public void addRule(PrivacyItem rule) {
        int index = 0;
        while (index < rules.size()) {
            if (rule.order <= ((PrivacyItem) rules.elementAt(index)).order) {
                break;
            }
            index++;
        }
        rules.insertElementAt(rule, index);
    }

    public static void privacyListRq(boolean set, JabberDataBlock child, String id) {
        JabberDataBlock pl = new Iq(null, (set) ? Iq.TYPE_SET : Iq.TYPE_GET, id);
        JabberDataBlock qry = pl.addChildNs("query", "jabber:iq:privacy");
        if (child != null) {
            qry.addChild(child);
        }

        System.out.println(pl);
        StaticData.getInstance().roster.theStream.send(pl);
    }
    
    public void generateList() {
        int index = 0;
        JabberDataBlock list0 = listBlock();

        for (Enumeration e = rules.elements(); e.hasMoreElements();) {
            PrivacyItem item = (PrivacyItem) e.nextElement();
            item.order = index++;
            list0.addChild( item.constructBlock() );
        }
        
        privacyListRq(true, list0, "storelst");
    }

    private JabberDataBlock listBlock() {
        JabberDataBlock list0=new JabberDataBlock("list", null, null);
        list0.setAttribute("name", name);
        return list0;
    }
    
    public void deleteList() {
        JabberDataBlock list0=listBlock();
        privacyListRq(true, list0, "storelst");
    }
  
    public void activate(String atr) {
        JabberDataBlock a=new JabberDataBlock(atr, null, null);
        if (atr != null)
            a.setAttribute("name", name);
        privacyListRq(true, a, "plset");
    }
}
