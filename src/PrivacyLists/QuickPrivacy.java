/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

//#ifdef PRIVACY
//# 
//# package PrivacyLists;
//# 
//# import Client.NotInListFilter;
//# import Client.StaticData;
//# import com.alsutton.jabber.JabberBlockListener;
//# import com.alsutton.jabber.JabberDataBlock;
//# import java.util.Vector;
//# import locale.SR;
//# import ui.controls.form.CheckBox;
//# import ui.controls.form.DefForm;
//# import ui.controls.form.DropChoiceBox;
//# import ui.controls.form.LinkString;
//# import ui.controls.form.SpacerItem;
//# 
//# /**
//#  *
//#  * @author Vitaly
//#  */
//# public class QuickPrivacy extends DefForm implements JabberBlockListener {
//# 
//#     public static final String LIST_QUICKPRIVACY = "bm-quickprivacy";
//#     private DropChoiceBox nil;
//#     private CheckBox usePrivacy;
//#     public static Vector conferenceList;
//#     public static Vector groupsList;
//# 
//#     public QuickPrivacy() {
//#         super("Privacy", false);
//#         usePrivacy = new CheckBox("Enable privacy settings", cf.useQuickPrivacy);
//#         itemsList.addElement(usePrivacy);
//#         itemsList.addElement(new SpacerItem(10));
//#         nil = new DropChoiceBox(SR.MS_NOT_IN_LIST);
//#         nil.add(SR.MS_NIL_DROP_MP);
//#         nil.add(SR.MS_NIL_DROP_P);
//#         nil.add(SR.MS_NIL_ALLOW_ALL);
//#         nil.setSelectedIndex((cf.notInListDropLevel > NotInListFilter.ALLOW_ALL) ? NotInListFilter.ALLOW_ALL : cf.notInListDropLevel);
//#         itemsList.addElement(nil);
//#         LinkString ed = new LinkString(SR.MS_PRIVACY_LISTS) {
//# 
//#             public void doAction() {
//#                 new PrivacySelect();
//#             }
//#         };
//#         itemsList.addElement(ed);
//#     }
//# 
//#     public void cmdOk() {
//#         cf.useQuickPrivacy = usePrivacy.getValue();
//#         cf.notInListDropLevel = nil.getSelectedIndex();
//#         if (cf.useQuickPrivacy) {
//#             updateQuickPrivacyList();
//#         } else {
//#             new PrivacyList(null).activate("active");
//#             new PrivacyList(null).activate("default");
//#         }
//#         cf.saveToStorage();
//#         destroyView();
//#     }
//# 
//#     public int blockArrived(JabberDataBlock data) {
//#         if (!cf.useQuickPrivacy)
//#             return BLOCK_REJECTED;
//# 
//#         try {
//#             if (data.getTypeAttribute().equals("result")) {
//#                 if (data.getAttribute("id").equals("quicklst")) {
//#                     if (cf.useQuickPrivacy) {
//#                         new PrivacyList(LIST_QUICKPRIVACY).activate("active");
//#                         new PrivacyList(LIST_QUICKPRIVACY).activate("default");
//#                     }
//#                     return JabberBlockListener.NO_MORE_BLOCKS;
//#                 }
//#             }
//#         } catch (Exception e) {
//#         }
//#         return BLOCK_REJECTED;
//#     }
//# 
//#     /*  
//#     item  action="deny" type="subscription" value="none" order="666"
//#     item action="allow" type="group" value="{$group}" order="100"
//#     item action="allow" type="jid" value="{$conference}" order="10"
//#     item action="allow" type=jid" value="{$self}" order="0" 
//#      */
//#     public void updateQuickPrivacyList() {
//#         if (!cf.useQuickPrivacy)
//#             return;
//#         JabberDataBlock qList = new JabberDataBlock("list");
//#         qList.setAttribute("name", LIST_QUICKPRIVACY);
//#         PrivacyItem item0 = new PrivacyItem();
//#         item0.type = PrivacyItem.ITEM_SUBSCR;
//#         item0.value = "none";
//#         item0.action = PrivacyItem.ITEM_BLOCK;
//#         item0.order = 666;
//#         qList.addChild(item0.constructBlock());
//#         PrivacyItem item3 = new PrivacyItem();
//#         item3.action = PrivacyItem.ITEM_ALLOW;
//#         item3.type = PrivacyItem.ITEM_JID;
//#         item3.value = StaticData.getInstance().roster.myJid.bareJid;
//#         item3.order = 0;
//#         qList.addChild(item3.constructBlock());
//#         if (conferenceList != null) {
//#             for (int i = 0; i < conferenceList.size(); i++) {
//#                 PrivacyItem item4 = new PrivacyItem();
//#                 item4.action = PrivacyItem.ITEM_ALLOW;
//#                 item4.order = 10 + i;
//#                 item4.type = PrivacyItem.ITEM_JID;
//#                 item4.value = (String) conferenceList.elementAt(i);
//#                 qList.addChild(item4.constructBlock());
//#             }
//#         }
//# 
//#         if (groupsList != null) {
//#             int groupsSize = groupsList.size();
//# 
//#             for (int i = 0; i < groupsSize; i++) {
//#                 PrivacyItem item1 = new PrivacyItem();
//#                 item1.action = PrivacyItem.ITEM_ALLOW;
//#                 item1.order = 100 + i;
//#                 item1.type = PrivacyItem.ITEM_GROUP;
//#                 item1.value = (String) groupsList.elementAt(i);
//#                 qList.addChild(item1.constructBlock());
//#             }
//#         }
//#         JabberDataBlock item2 = PrivacyItem.itemIgnoreList().constructBlock();
//#         qList.addChild(item2);
//#         StaticData.getInstance().theStream.addBlockListener(this);
//#         PrivacyList.privacyListRq(true, qList, "quicklst");
//#     }
//# }
//# 
//#endif
