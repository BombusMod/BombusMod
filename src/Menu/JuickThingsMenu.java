/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Menu;

import locale.SR;
//import images.MenuIcons;
import Client.MessageEdit;
import Client.Contact;
import Client.Roster;
import java.util.Vector;
import ui.VirtualList;

/**
 *
 * @author Totktonada
 */
public class JuickThingsMenu extends Menu {
    //MenuIcons menuIcons=MenuIcons.getInstance();
    private Contact contact;
    private Vector things;

    public JuickThingsMenu(VirtualList parent, Vector things, Contact contact) {
//#ifdef JUICK
//#         super(SR.MS_JUICK_THINGS, null); //MenuIcons.getInstance()
//#else
        super("", null);
//#endif
        this.things = things;
        this.contact = contact;
        
        parentView = parent;
        show();

        int quantity = things.size();
        for(int i=0; i<quantity; i++)
           addItem((String) things.elementAt(i), i); //, menuIcons.ICON_JUICK

    }

    public void eventOk() {
        MenuItem me = (MenuItem) getFocusedObject();
        if (me == null) {
            return;
        }
        int index = me.index;

        String body = (String) things.elementAt(index);
        int start = body.indexOf('[');
        int end = body.indexOf(']', start);
        if ((start>=0) || end>start) {
            body = body.substring(start+1, end);
        }

        try {
            Roster.me = null;
            Roster.me = new MessageEdit(parentView, contact, body);
            Roster.me.show();
        } catch (Exception e) {/*no messages*/}
    }
}
