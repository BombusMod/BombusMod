/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Messages.notification;

import Client.Msg;
import com.sonyericsson.ui.UIActivityMenu;
import midlet.BombusMod;
import ui.VirtualCanvas;

/**
 *
 * @author Vitaly
 */
public class SEMCNotificator implements Notificator, com.sonyericsson.ui.UIEventListener {

    int eventCode;
    public SEMCNotificator() {
         UIActivityMenu.getInstance(BombusMod.getInstance()).setEventListener(this);
    }

    public void sendNotify(String title, String text) {
        if (!VirtualCanvas.getInstance().isShown()) {
            StringBuffer out = new StringBuffer(text);
            UIActivityMenu.getInstance(BombusMod.getInstance()).addEvent(title, Msg.clearNick(out), null, null);
        }
    }

    public void eventAction(int i) {
        // TODO: handle eventId
    }

}
