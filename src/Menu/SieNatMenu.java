/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Menu;

import java.util.Enumeration;
import java.util.Hashtable;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import midlet.BombusMod;

/**
 *
 * @author Vitaly
 */
public class SieNatMenu extends Menu {
    private static Hashtable commandsList = new Hashtable();

    public SieNatMenu(Display display, Displayable parent) {
        super("Native Commands", null);
        commandsList.put("Phonebook", "CNCT_PREFRDBOOK");
        commandsList.put("Records", "RECD_RECORDMENU");
        commandsList.put("Internet", "ELSE_SUFFUNUMNU");
        commandsList.put("Camera", "ELSE_INTRCAMERA");
        commandsList.put("Messages", "MESG_MESSGEMENU");
        commandsList.put("Organizer", "ORGZ_ORGANIZMNU");
        commandsList.put("Extras", "ELSE_EXTRASMENU");
        commandsList.put("My own", "FLSH_MYOWNSTUFF");
        commandsList.put("Setup", "STUP_SETPUPMENU");
        commandsList.put("Card-explorer", "FLSH_FLEXPLORER");
        commandsList.put("My menu", "ELSE_STR_MYMENU");

        this.display = display;
        this.parentView = parent;
        
        int count = commandsList.size();
        int i = 0;
        for (Enumeration e = commandsList.keys(); e.hasMoreElements();) {
         addItem((String)e.nextElement(), i++); 
        }
        attachDisplay(display);
    }

    public void eventOk() {
        destroyView();
        MenuItem me=(MenuItem) getFocusedObject();
        if (me==null) return;        
        try {
            String requestString = "native:" + commandsList.get(me.toString());
            //System.out.println(requestString);
            BombusMod.getInstance().platformRequest(requestString);
        } catch (Exception e) {}

    }

}
