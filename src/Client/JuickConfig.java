/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Client;

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import locale.SR;
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
//#ifdef PLUGINS
//#     public static String plugin = new String("PLUGIN_JUICK");
//#endif
    Display display;
    StaticData sd=StaticData.getInstance();
    private DropChoiceBox juickContactsBox = null;
    Records records;

    public JuickConfig(Display display, Displayable pView, String caption) {
        super(display, pView, caption);
        this.display=display;

        readFromStorage();
        addJuickContactsBox();

        attachDisplay(display);
        this.parentView=pView;
    }

    public JuickConfig(Display display, Displayable pView) {
        super(display, pView, null);
        readFromStorage();
    }

    private void addJuickContactsBox() {
//#ifdef JUICK
//#             if (sd.roster.juickContacts.size()>1) {
//#             Vector juickContactsNames = new Vector(sd.roster.juickContacts.size());
//#             for (Enumeration e = sd.roster.juickContacts.elements(); e.hasMoreElements();) {
//#                 Contact c = (Contact) e.nextElement();
//#                 juickContactsNames.addElement(c.getName());
//#             }
//#             juickContactsBox = new DropChoiceBox(display, "Main Juick-contact");
//#             juickContactsBox.items = juickContactsNames;
//#             //if (!account.juickJID.equals(""))
//#             if (sd.roster.indexMainJuickContact > -1)
//#                 juickContactsBox.setSelectedIndex(sd.roster.indexMainJuickContact);
//#             else juickContactsBox.setSelectedIndex(0);
//#             itemsList.addElement(juickContactsBox);
//#         }
//#endif
    }

    private void writeToStorage() {
         DataOutputStream outputStream=NvStorage.CreateDataOutputStream();
         try{
             int size = records.size();
             outputStream.writeInt(size);
             for (int i=0; i<size; i++) {
                  outputStream.writeUTF((String) records.AccountsJIDs.elementAt(i));
                  outputStream.writeUTF((String) records.JuickJIDs.elementAt(i));
             }
         } catch (IOException e) { }
         NvStorage.writeFileRecord(outputStream, "juick_db", 0, true);
     }

    private void readFromStorage() {
        records = new Records();
        DataInputStream inputStream = NvStorage.ReadFileRecord("juick_db", 0);
        try {
            int storedAccounts = inputStream.readInt();
            for (int i=0; i<storedAccounts; i++)
                records.addRecord(inputStream.readUTF(), inputStream.readUTF());
            inputStream.close();
        } catch (Exception e) { /*e.printStackTrace();*/ }
        }

    public void cmdOk() {
//#ifdef JUICK
//#             if (juickContactsBox != null) {
//#                 setJuickJID(((Contact) sd.roster.juickContacts.elementAt(juickContactsBox.getSelectedIndex())).bareJid, true);
//#             } else {
//#                 setJuickJID("", true);
//#             }
//#             sd.roster.updateMainJuickContact();
//#             writeToStorage();
//#             destroyView();
//#endif
    }

    public String getJuickJID () {
        if (records == null)
            readFromStorage();
     return records.getJuickJID(sd.account.getBareJid());
    }

    public void setJuickJID (String JJID, boolean toStorage) {
     records.setJuickJID(sd.account.getBareJid(), JJID);
     if (toStorage)
         writeToStorage();
    }

    private class Records {

        private Vector AccountsJIDs;
        private Vector JuickJIDs;

        private Records() {
            AccountsJIDs=new Vector();
            JuickJIDs=new Vector();
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
            int index=AccountsJIDs.indexOf(AJID);
            if (index>-1) {
                JuickJIDs.setElementAt(JJID, index);
            } else {
                addRecord(AJID, JJID);
            }
        }
    }
}
/*            if ((sd != null) && (sd.roster != null)) {
                if ((!a.juickJID.equals("")) && sd.roster.juickContacts.size()<2) {
                    a.juickJID="";
                }
                //sd.roster.indexMainJuickContact = sd.roster.juickContacts.indexOf(new Client.Contact("Juick", a.juickJID, com.alsutton.jabber.datablocks.Presence.PRESENCE_OFFLINE, null));
            }*/