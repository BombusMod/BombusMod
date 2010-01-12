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
    private final static String[] items = new String[]{
        "Phonebook", "Records", "Internet", 
        "Camera", "Messages", "Organizer", 
        "Extras", "My own", "Setup", 
        "Card-explorer", "My menu" 
    };
    private final static String[] commands = new String[]{
          "CNCT_PREFRDBOOK"
        , "RECD_RECORDMENU"
        , "ELSE_SUFFUNUMNU"
        , "ELSE_INTRCAMERA"
        , "MESG_MESSGEMENU"
        , "ORGZ_ORGANIZMNU"
        , "ELSE_EXTRASMENU"
        , "FLSH_MYOWNSTUFF"
        , "STUP_SETPUPMENU"
        , "FLSH_FLEXPLORER"
        , "ELSE_STR_MYMENU"
    };

    public SieNatMenu(Display display, Displayable parent) {
        super("Native Commands", null);
        

        this.display = display;
        this.parentView = parent;
        
        int count = commands.length;        
        for (int i = 0; i < count; i++) {
         addItem((String)items[i], i);
        }
        attachDisplay(display);
    }

    public void eventOk() {
        destroyView();
        MenuItem me=(MenuItem) getFocusedObject();
        if (me==null) return;        
        try {
            String requestString = "native:" + commands[me.index];
            //System.out.println(requestString);
            BombusMod.getInstance().platformRequest(requestString);
        } catch (Exception e) {}

    }

}
