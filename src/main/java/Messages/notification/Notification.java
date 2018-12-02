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
        return true;
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
                    notifier = new AndroidNotificator();
                    break;
                default:
                    break;
            }
            lastUsedNotifier = newNotifierType;
        }
        return notifier;
    }
}
