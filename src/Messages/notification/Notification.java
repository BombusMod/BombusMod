/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Messages.notification;

import Client.Config;

/**
 *
 * @author Vitaly
 */
public abstract class Notification {
    
    public static final int NOTIFICATOR_TYPE_NONE = 0;
    public static final int NOTIFICATOR_TYPE_POPUP = 1;
    public static final int NOTIFICATOR_TYPE_PLATFORM = 2;

    private static Notificator notifier;
    
    private static int lastUsedNotifier = -1;
    
    public static boolean isPlatformSupported() {
        return Config.getInstance().phoneManufacturer == Config.MICROEMU
                || (Config.getInstance().phoneManufacturer == Config.SONYE
                    && Config.getInstance().sonyJava >= 750);
    }

    public static Notificator getNotificator() {
        int newNotifierType = Config.getInstance().popUps;
        if (notifier == null || newNotifierType != lastUsedNotifier) {
            switch (Config.getInstance().popUps) {
                case NOTIFICATOR_TYPE_NONE:
                    notifier = new EmptyNotificator();
                    break;
                case NOTIFICATOR_TYPE_POPUP:
                    notifier = new PopupNotificator();
                    break;
                case NOTIFICATOR_TYPE_PLATFORM:
                    switch (Config.getInstance().phoneManufacturer) {                        
                        case Config.MICROEMU:
//#if android                    
//#                             notifier = new AndroidNotificator();
//#endif                    
                            break;
                        case Config.SONYE:
                            if (isPlatformSupported()) {
                                try {
                                    notifier = (Notificator) Class
                                            .forName("Messages.notification.SEMCNotificator")
                                            .newInstance();
                                } catch (Exception ex) {
                                }
                            } else {
                                fallbackToPopups();
                            }
                            break;
                        default:
                            fallbackToPopups();
                            break;
                    }
                    break;
                default:
                    break;
            }
            lastUsedNotifier = newNotifierType;
        }
        return notifier;
    }
    
    private static void fallbackToPopups() {
        notifier = new PopupNotificator();
        Config.getInstance().popUps = Notification.NOTIFICATOR_TYPE_POPUP;                                                        
    }
}
