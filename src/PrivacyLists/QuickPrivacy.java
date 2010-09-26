/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package PrivacyLists;

import Client.NotInListFilter;
import Client.StaticData;
import com.alsutton.jabber.JabberBlockListener;
import com.alsutton.jabber.JabberDataBlock;
import java.util.Vector;
import locale.SR;
import ui.controls.form.DefForm;
import ui.controls.form.DropChoiceBox;
import ui.controls.form.LinkString;

/**
 *
 * @author Vitaly
 */
public class QuickPrivacy extends DefForm implements JabberBlockListener, Runnable {
    
//#ifdef PLUGINS
//#     public static String plugin = new String("PLUGIN_PRIVACY");
//#endif
    
    
    public static final String LIST_QUICKPRIVACY = "bm-quickprivacy";
    
    private DropChoiceBox nil;
    
    public static Vector conferenceList;
    
    public QuickPrivacy() {
        super("Privacy", false);
        nil = new DropChoiceBox(SR.MS_NOT_IN_LIST);
        nil.append(SR.MS_NIL_DROP_MP);
        nil.append(SR.MS_NIL_DROP_P);
        nil.append(SR.MS_NIL_ALLOW_ALL);
        nil.setSelectedIndex((cf.notInListDropLevel > NotInListFilter.ALLOW_ALL)? NotInListFilter.ALLOW_ALL: cf.notInListDropLevel);
        itemsList.addElement(nil);
        LinkString ed = new LinkString(SR.MS_PRIVACY_LISTS) {

            public void doAction() {
                new PrivacySelect();
            }
        };
        itemsList.addElement(ed);        
    }
    
    public void cmdOk() {
        cf.notInListDropLevel = nil.getSelectedIndex();
        updateQuickPrivacyList();
        destroyView();
    }

    public int blockArrived(JabberDataBlock data) {   
         try {
            if (data.getTypeAttribute().equals("result"))
                if (data.getAttribute("id").equals("quicklst")) {
                    new PrivacyList(LIST_QUICKPRIVACY).activate("active");
                    new PrivacyList(LIST_QUICKPRIVACY).activate("default");                
                    return JabberBlockListener.NO_MORE_BLOCKS;
                }
        } catch (Exception e) { }
        return BLOCK_REJECTED;
    }

    public void run() {
    }
    
    /*  
        item action="deny" order="0"
        item action="allow" type="subscription" value="both" order="10"
        item  action="allow" type="subscription" value="from" order=20"
        item action="allow" type="jid" value="{$conference}" order="30"
        item action="allow" type=jid" value="{$self}" order="40" 
    */
    
    public void updateQuickPrivacyList(){
        JabberDataBlock qList=new JabberDataBlock("list", null, null);
        qList.setAttribute("name", LIST_QUICKPRIVACY);
        PrivacyItem item0 = new PrivacyItem();
        item0.type = PrivacyItem.ITEM_ANY;
        item0.action = PrivacyItem.ITEM_BLOCK;
        item0.order = 666;
        qList.addChild(item0.constructBlock());
        PrivacyItem item1 = new PrivacyItem();
        item1.action = PrivacyItem.ITEM_ALLOW;
        item1.order = 20; 
        item1.type = PrivacyItem.ITEM_SUBSCR;
        item1.value = "both";
        qList.addChild(item1.constructBlock());
        PrivacyItem item2 = new PrivacyItem();
        item2.action = PrivacyItem.ITEM_ALLOW;
        item2.type = PrivacyItem.ITEM_SUBSCR;
        item2.value = "from";
        item2.order = 10;
        qList.addChild(item2.constructBlock());
        PrivacyItem item3 = new PrivacyItem();
        item3.action = PrivacyItem.ITEM_ALLOW;
        item3.type = PrivacyItem.ITEM_JID;
        item3.value = StaticData.getInstance().roster.myJid.getBareJid();
        item3.order = 0;
        qList.addChild(item3.constructBlock());
        for (int i = 0; i < conferenceList.size(); i++) {
            PrivacyItem item4 = new PrivacyItem();
            item4.action = PrivacyItem.ITEM_ALLOW;
            item4.order = 21 + i;
            item4.type = PrivacyItem.ITEM_JID;
            item4.value = (String)conferenceList.elementAt(i);
            qList.addChild(item4.constructBlock());
        }        
        StaticData.getInstance().roster.theStream.addBlockListener(this);
        PrivacyList.privacyListRq(true, qList, "quicklst");
    }

}
