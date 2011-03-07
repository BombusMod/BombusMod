/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Messages.notification;

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
//#ifdef NICK_COLORS
            int vi = 0;
            while (vi < out.length()) { // strip nick_colors
                if (out.charAt(vi) < 0x03) {
                    out.deleteCharAt(vi);
                } else {
                    vi++;
                }
            }
//#endif
            UIActivityMenu.getInstance(BombusMod.getInstance()).addEvent(title, out.toString(), null, null);
        }
    }

    public void eventAction(int i) {
        // TODO: handle eventId
    }

}
