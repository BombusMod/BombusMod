/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

//#ifdef SYSTEM_NOTIFY
//# 
//# package Messages.notification;
//# 
//# import Client.Msg;
//# import midlet.BombusMod;
//# import ui.VirtualCanvas;
//# 
//# /**
//#  *
//#  * @author Vitaly
//#  */
//# public final class SEMCNotificator implements Notificator, com.sonyericsson.ui.UIEventListener {
//# 
//#     int eventCode;
//#     public SEMCNotificator() {
//#          com.sonyericsson.ui.UIActivityMenu.getInstance(BombusMod.getInstance()).setEventListener(this);
//#     }
//# 
//#     public void sendNotify(String title, String text) {
//#         if (!VirtualCanvas.getInstance().isShown()) {
//#             StringBuffer out = new StringBuffer(text);
//#             com.sonyericsson.ui.UIActivityMenu.getInstance(BombusMod.getInstance()).addEvent(title, Msg.clearNick(out), null, null);
//#         }
//#     }
//# 
//#     public void eventAction(int i) {
//#         // TODO: handle eventId
//#     }
//# 
//#     public void clear() {
//#     }
//# 
//# }
//# 
//#endif
