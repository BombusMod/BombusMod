/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Menu;

import Client.Roster;
import locale.SR;
//import images.MenuIcons;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import Client.MessageEdit;
import Client.Contact;
import java.util.*;

/**
 *
 * @author Totktonada
 */
public class JuickThingsMenu extends Menu {
//#ifdef PLUGINS
//#     public static String plugin = new String("PLUGIN_JUICK");
//#endif
    //MenuIcons menuIcons=MenuIcons.getInstance();
    private Contact contact;
    private Vector things;

    public JuickThingsMenu(Vector things, Display display, Displayable pView, Contact contact) {
//#ifdef JUICK
//#         super(SR.MS_JUICK_THINGS, null); //MenuIcons.getInstance()
//#else
        super("", null);
//#endif
        this.things = things;
        this.contact = contact;

        attachDisplay(display);
        this.parentView = pView;

        int quantity = things.size();
        for(int i=0; i<quantity; i++)
           addItem((String) things.elementAt(i), i); //, menuIcons.ICON_JUICK

    }

    public void eventOk() {
        destroyView();
        MenuItem me = (MenuItem) getFocusedObject();
        if (me == null) {
            return;
        }
        int index = me.index;
        try {
            Roster.me = null; Roster.me = new MessageEdit(display, parentView, contact, things.elementAt(index) + " ");
            Roster.me.show(this);
        } catch (Exception e) {/*no messages*/

        }
    }
}
