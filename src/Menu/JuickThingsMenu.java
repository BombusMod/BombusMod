/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Menu;

import locale.SR;
//import images.MenuIcons;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import Client.MessageEdit;
import Client.Contact;

/**
 *
 * @author Totktonada
 */
public class JuickThingsMenu extends Menu {
    //MenuIcons menuIcons=MenuIcons.getInstance();
    String[] things;
    Display display;
    Displayable parentView;
    Contact contact;

    public JuickThingsMenu(String[] things, int numberOfThing, Display display, Displayable pView, Contact contact) {
        super(SR.MS_JUICK_THINGS, null); //MenuIcons.getInstance()
        this.things = things;
        this.contact = contact;
        this.display = display;
        this.parentView=pView;

        for (int i=0; i<=numberOfThing; i++) {
            if (things[i]!=null) // fix for #(last popular) request
                addItem(things[i], i); //, menuIcons.ICON_JUICK
        }
        attachDisplay(display);
    }

    public void eventOk() {
        destroyView();
        MenuItem me=(MenuItem) getFocusedObject();
        if (me==null) return;
        int index=me.index;
        try {
//#ifdef RUNNING_MESSAGE
//#                 sd.roster.me=new MessageEdit(display, parentView, contact, things[index]+" ");
//#else
        new MessageEdit(display, parentView, contact, things[index]+" ");
//        new MessageEdit(display, this, contact, things[index]+" ");
//#endif
        } catch (Exception e) {/*no messages*/}
    }
}
