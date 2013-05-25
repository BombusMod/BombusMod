/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Messages.notification;

/**
 *
 * @author Vitaly
 */
public interface Notificator {

    public void sendNotify(String title, String text);
    public void clear();
}