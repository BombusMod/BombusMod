/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
//#ifdef SYSTEM_NOTIFY
//# package Messages.notification;
//# 
//# import Client.Config;
//# 
//# /**
//#  *
//#  * @author Vitaly
//#  */
//# public abstract class Notification {
//# 
//#     private static Notificator notifier;
//# 
//#     public static Notificator getNotificator() {
//#         if (notifier == null) {
//#if android
//#             notifier = new AndroidNotification();
//#else            
//#             switch (Config.getInstance().phoneManufacturer) {
//#                 case Config.SONYE:
//#                     if (Config.getInstance().sonyJava >= 750) {
//#                         try {
//#                             notifier = (Notificator) Class
//#                                     .forName("Messages.notification.SEMCNotificator")
//#                                     .newInstance();
//#                         } catch (Exception ex) {
//#                         }
//#                     }
//#                     break;
//#             }
//#endif            
//#         }
//#         return notifier;
//#     }
//# }
//# 
//#endif
