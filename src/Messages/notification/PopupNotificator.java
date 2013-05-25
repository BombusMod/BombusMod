/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Messages.notification;

import ui.VirtualCanvas;
import ui.controls.PopUp;

/**
 *
 * @author Vitaly
 */
public class PopupNotificator implements Notificator {

    public void sendNotify(String title, String text) {
        int popupType = PopUp.TYPE_MESSAGE;
        if (title == null)
            popupType = PopUp.TYPE_SYSTEM;
        PopUp.getInstance().addPopup(popupType, title, text);
        VirtualCanvas.getInstance().repaint();
    }

    public void clear() {
        // TODO: clear popups
    }
    
}
