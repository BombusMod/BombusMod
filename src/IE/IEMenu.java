/*
 * IEMenu.java
 *
 * Created on 24.01.2008, 21:55
 *
 * Copyright (c) 2006-2008, Daniel Apatin (ad), http://apatin.net.ru
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * You can also redistribute and/or modify this program under the
 * terms of the Psi License, specified in the accompanied COPYING
 * file, as published by the Psi Project; either dated January 1st,
 * 2005, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
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
                new IE.ConfigData(pathSelected, 0);
                break;
            case 1: //save Config
                new IE.ConfigData(pathSelected, 1);
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
