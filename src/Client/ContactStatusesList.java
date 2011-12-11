/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import Messages.MessageItem;
import Messages.MessageList;
import ui.MainBar;

/**
 *
 * @author Vitaly
 */
//#ifdef STATUSES_WINDOW
//# public class ContactStatusesList extends MessageList {
//# 
//#     private final Contact contact;
//#     public ContactStatusesList(Contact c) {
//#         super(c.statuses);
//#         contact = c;
//#         mainbar = new MainBar(contact);
//#     }
//#     public int getItemCount() {
//#         return contact == null ? 0 :contact.statuses.size(); 
//#     }
//# 
//#     protected Msg getMessage(int index) {
//#         if (index >= getItemCount()) {
//#             return null;
//#         }
//# 
//#         Msg msg = ((MessageItem) contact.statuses.elementAt(index)).msg;
//#         if (msg.unread) {
//#             contact.resetNewMsgCnt();
//#         }
//#         msg.unread = false;
//#         return msg;
//#     }
//#     
//# }
//#endif