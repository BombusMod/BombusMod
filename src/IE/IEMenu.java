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
        addItem("Config restore", 0);
        addItem("Config backup", 1);
        addItem("Archive restore", 2);
        addItem("Archive backup", 3);
        addItem("Templates restore", 4);
        addItem("Templates backup", 5);
        /*
        addItem("Accounts load", 2);
        addItem("Accounts save", 3);
         */
    
	attachDisplay(display);
    }
    public void eventOk(){
	//destroyView();
	MenuItem me=(MenuItem) getFocusedObject();
        
	if (me==null)
            return;
        
	choice=me.index;
        
        if (choice==0)
            new Browser(null, display, this, false);
        
        if (choice==1)
            new Browser(null, display, this, true);
        
        if (choice==2)
            new Browser(null, display, this, false);
        
        if (choice==3)
            new Browser(null, display, this, true);
        
        if (choice==4)
            new Browser(null, display, this, false);
        
        if (choice==5)
            new Browser(null, display, this, true);
    }

    public void BrowserFilePathNotify(String pathSelected) {
        switch (choice) {
            case 0: //load Config
                new IE.iConfigData(pathSelected);
                break;
            case 1: //save Config
                new IE.eConfigData(pathSelected);
                break;
            case 2: //load Archive
                new IE.ArchiveTemplates(0, 1, pathSelected);
                break;
            case 3: //save Archive
                new IE.ArchiveTemplates(1, 1, pathSelected);
                break;
            case 4: //load Templates
                new IE.ArchiveTemplates(0, 0, pathSelected);
                break;
            case 5: //save Templates
                new IE.ArchiveTemplates(1, 0, pathSelected);
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
