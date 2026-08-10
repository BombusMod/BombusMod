/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Client;

import ui.NativeScreenCommand;
import ui.NativeScreenItem;
import ui.NativeScreenModel;
import ui.VirtualListController;
import ui.controls.form.DropChoiceBox;
import ui.controls.form.DefForm;
import io.NvStorage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

/**
 *
 * @author Totktonada
 */
public class JuickConfig extends DefForm {
    private static StaticData sdata = StaticData.getInstance();
    private DropChoiceBox juickContactsBox = null;
    private static Records records = new Records();

    public JuickConfig(String caption) {
        super(caption);
        if (!buildNativeModel(caption)) return;
        if (records.isEmpty()) {
            records.readFromStorage();
        }
        
        addJuickContactsBox();        
    }

    private void addJuickContactsBox() {
//#ifdef JUICK
        if (sdata.roster.juickContacts.size() > 1) {
            Vector juickContactsNames = new Vector(sdata.roster.juickContacts.size());
            for (Enumeration e = sdata.roster.juickContacts.elements(); e.hasMoreElements();) {
                Contact c = (Contact) e.nextElement();
                juickContactsNames.addElement(c.jid.getBare());
            }
            juickContactsBox = new DropChoiceBox("Main Juick-contact");
            juickContactsBox.items = juickContactsNames;
            //if (!account.juickJID.equals(""))
            if (sdata.roster.indexMainJuickContact > -1)
                juickContactsBox.setSelectedIndex(sdata.roster.indexMainJuickContact);
            else juickContactsBox.setSelectedIndex(0);
            itemsList.addElement(juickContactsBox);
        }
//#endif
    }

    public void cmdOk() {
//#ifdef JUICK
            if (juickContactsBox != null) {
                setJuickJID(((Contact) sdata.roster.juickContacts.elementAt(juickContactsBox.getSelectedIndex())).jid.getBare(), true);
            } else {
                setJuickJID("", true);
            }
            sdata.roster.updateMainJuickContact();
            destroyView();
//#endif
    }

    public static String getJuickJID() {
        if (records.isEmpty()) {
            records.readFromStorage();
        }
     return records.getJuickJID(sdata.account.JID.getBare());
    }

    public static void setJuickJID(String JJID, boolean toStorage) {
        if (records.isEmpty()) {
            records.readFromStorage();
        }
        records.setJuickJID(sdata.account.JID.getBare(), JJID);
        if (toStorage) {
            records.writeToStorage();
        }
    }

    private static class Records {
        private Vector AccountsJIDs = null;
        private Vector JuickJIDs = null;

        private boolean isEmpty() {
            return (AccountsJIDs == null) || (AccountsJIDs.size() < 1);
        }

        private Records() {
            AccountsJIDs = new Vector();
            JuickJIDs = new Vector();
        }

        private void addRecord(String AJID, String JJID) {
            AccountsJIDs.addElement(AJID);
            JuickJIDs.addElement(JJID);
        }

        private int size() {
            return JuickJIDs.size();
        }

        private String getJuickJID(String AJID) {
            int index = AccountsJIDs.indexOf(AJID);
            if (index > -1) {
                return (String) JuickJIDs.elementAt(AccountsJIDs.indexOf(AJID));
            } else {
                return "";
            }
        }

        private void setJuickJID(String AJID, String JJID) {
            int index = AccountsJIDs.indexOf(AJID);
            if (index > -1) {
                JuickJIDs.setElementAt(JJID, index);
            } else {
                addRecord(AJID, JJID);
            }
        }

        private void writeToStorage() {
            DataOutputStream outputStream = NvStorage.CreateDataOutputStream();
            try {
                int size = size();
                outputStream.writeInt(size);
                for (int i = 0; i < size; i++) {
                    outputStream.writeUTF((String) AccountsJIDs.elementAt(i));
                    outputStream.writeUTF((String) JuickJIDs.elementAt(i));
                }
            } catch (IOException e) {
            }
            NvStorage.writeFileRecord(outputStream, "juick_db", 0, true);
        }

        private void readFromStorage() {
            DataInputStream inputStream = NvStorage.ReadFileRecord("juick_db", 0);
            try {
                int size = inputStream.readInt();
                for (int i = 0; i < size; i++)
                    addRecord(inputStream.readUTF(), inputStream.readUTF());
                inputStream.close();
            } catch (Exception e) {  }
        }
    }

    private boolean buildNativeModel(String caption) {
        if (!VirtualListController.getInstance().isActive()) return true;
        NativeScreenModel m = new NativeScreenModel();
        m.addCommand("ok", "OK", NativeScreenCommand.OK, -1);
        final JuickConfig self = this;
        VirtualListController.getInstance().setOnDismiss(new Runnable() { public void run() { self.dismissNative(); } });
        VirtualListController.getInstance().setCaption(caption);
        VirtualListController.getInstance().setModel(m);
        return false;
    }
    public void dismissNative() { VirtualListController.getInstance().setModel(null); VirtualListController.getInstance().notifyUpdate(); destroyView(); }
    public void destroyView() { VirtualListController.getInstance().setModel(null); VirtualListController.getInstance().notifyUpdate(); super.destroyView(); }
}
/*            if ((sd != null) && (sd.roster != null)) {
                if ((!a.juickJID.equals("")) && sd.roster.juickContacts.size()<2) {
                    a.juickJID="";
                }
                //sd.roster.indexMainJuickContact = sd.roster.juickContacts.indexOf(new Client.Contact("Juick", a.juickJID, com.alsutton.jabber.datablocks.Presence.PRESENCE_OFFLINE, null));
            }*/