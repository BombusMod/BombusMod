/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package xmpp.extensions;

import Client.Contact;
import Client.Group;
import Client.StaticData;
import com.alsutton.jabber.JabberBlockListener;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.datablocks.Presence;
import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import ui.controls.form.DefForm;
import ui.controls.form.MultiLine;

/**
 *
 * @author Vitaly
 */
public class RosterXListener implements JabberBlockListener {

    public RosterXListener() {};

    Vector items;    
    String sender;

    public int blockArrived(JabberDataBlock data) {
        if (data instanceof Presence)
            return BLOCK_REJECTED;
        JabberDataBlock x=data.findNamespace("x", "http://jabber.org/protocol/rosterx");
        if (x == null) return BLOCK_REJECTED;        
        Vector newcontacts = null;
        newcontacts = new Vector();
        sender = data.getAttribute("from");
        JabberDataBlock item;
        String action;
        Contact newcontact;
        for (Enumeration e=x.getChildBlocks().elements(); e.hasMoreElements();)  {
            item = null;
            item = (JabberDataBlock)e.nextElement();
            if (item != null) {
                action = null;
                action = item.getAttribute("action");
                if (action != null && action.equals("add")) {
                    // add contact to list of new contacts
                    newcontact = null;
                    newcontact = new Contact(item.getAttribute("name"), item.getAttribute("jid"), Presence.PRESENCE_OFFLINE, null);
                    newcontact.group = new Group(item.getChildBlockText("group"));
                    newcontacts.addElement(newcontact);
                } 
            }
        }
        if (!newcontacts.isEmpty())
            new RosterAddForm(midlet.BombusMod.getInstance().getDisplay(), StaticData.getInstance().roster, sender, newcontacts);

        return BLOCK_PROCESSED;
    }

}

class RosterAddForm extends DefForm {

    Vector contacts;

    public RosterAddForm (Display display, Displayable pView, String sender, Vector items) {
        super(display, pView, sender);
        contacts = items;
        itemsList.addElement(new MultiLine("Add contacts", "Add " + contacts.size() + " contacts to your roster?", super.getWidth()));
        attachDisplay(display);
        parentView = pView;
    }
    public void cmdOk() {
        Contact c;
        for (Enumeration e = contacts.elements(); e.hasMoreElements();) {
            c = (Contact)e.nextElement();
            StaticData.getInstance().roster.storeContact(c.bareJid.toString(), c.nick, (c.group.getName() == null) ? "" : c.group.getName(), true);
        }
        destroyView();
    }
}
