/*
 * IEMenu.java
 *
 * Created on 24 ������ 2008 �., 21:55
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package IE;

import io.file.browse.Browser;
import io.file.browse.BrowserListener;
import javax.microedition.lcdui.Display;
import ui.Menu;
import ui.MenuItem;

/**
 *
 * @author ad
 */
public class IEMenu 
        extends Menu
        implements BrowserListener
{
    private int choice = -1;
    
    public IEMenu(Display display) {
        super("Import/Export", null);
        addItem("Config load", 0);
        addItem("Config save", 1);
        /*
        addItem("Accounts load", 2);
        addItem("Accounts save", 3);
         */
        addItem("Exit", 99);
    
	attachDisplay(display);
    }
    public void eventOk(){
	//destroyView();
	MenuItem me=(MenuItem) getFocusedObject();
        
	if (me==null)
            return;
        
	int index=me.index;
        if (index==99) {
            destroyView();
            return;
        }
        choice = index;
        
        if (choice==0)
            new Browser(null, display, this, false);
        
        if (choice==1)
            new Browser(null, display, this, true);
        
        
        /*
	switch (index) {
	    case 0: //actions
                choice = index;
                break;
	    case 1: //status

		break;
            case 2: //active

		break;
             case 3: //user mood

                 break;
            case 4: //alert

		break;
            case 5: //conference
                
                break;
            case 6: //archive

		break;
            case 7: //add contact

                break;
	}
         */
    }

    public void BrowserFilePathNotify(String pathSelected) {
        switch (choice) {
            case 0: //load Config
                new IE.iConfigData(pathSelected);
                break;
            case 1: //save Config
                new IE.eConfigData(pathSelected);
                break;
            /*
            case 2: //load Accounts
                new IE.iAccountsData(pathSelected);
                break;
            case 3: //save Accounts
                new IE.eAccountsData(pathSelected);
                break;
             */
        }
    }
}
