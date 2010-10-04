/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Menu;

import midlet.BombusMod;
import ui.VirtualList;

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

    public SieNatMenu(VirtualList parent) {
        super("Native Commands", null);
        

        this.parentView = parent;
        
        int count = commands.length;        
        for (int i = 0; i < count; i++) {
         addItem(items[i], i);
        }
        show(parentView);
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
