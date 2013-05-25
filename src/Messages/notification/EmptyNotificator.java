/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Messages.notification;

/**
 *
 * @author Vitaly
 */
public class EmptyNotificator implements Notificator {

    public void sendNotify(String title, String text) {
        // nothing to do
    }

    public void clear() {
        // nothing to do
    }
    
}
