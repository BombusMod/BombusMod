/*
 * Browser.java
 *
 * Created on 26.09.2006, 23:42
 *
 * Copyright (c) 2005-2008, Eugene Stahov (evgs), http://bombus-im.org
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

package io.file.browse;
//#ifdef MENU
//# import Menu.Menu;
//# import Menu.MenuItem;
//#else
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
//#endif

import ui.MainBar;
import images.RosterIcons;
import io.file.FileIO;
import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import locale.SR;
import Colors.ColorTheme;
import ui.IconTextElement;
import ui.VirtualElement;
import ui.VirtualList;

/**
 *
 * @author evgs
 */
public class Browser extends VirtualList
//#ifndef MENU
    implements CommandListener
//#endif
    {
 
    private Vector dir;
//#ifndef MENU
    Command cmdOk=new Command(SR.MS_BROWSE, Command.OK, 1);
    Command cmdSelect=new Command(SR.MS_SELECT, Command.SCREEN, 2);
    Command cmdView=new Command(SR.MS_VIEW, Command.SCREEN, 3);
    Command cmdRoot=new Command(SR.MS_ROOT, Command.SCREEN, 4);
    Command cmdDelete=new Command(SR.MS_DELETE, Command.SCREEN, 5);
    Command cmdBack=new Command(SR.MS_BACK, Command.BACK, 98);
    Command cmdCancel=new Command(SR.MS_CANCEL, Command.EXIT, 99);
//#endif
    private String path;
    private BrowserListener browserListener;

    private boolean getDirectory;
    
    /** Creates a new instance of Browser */
    public Browser(String path, Display display, BrowserListener browserListener, boolean getDirectory) {
        super(display);
        
        this.browserListener=browserListener;
	this.getDirectory=getDirectory;
        this.path="";
		
        setMainBarItem(new MainBar(2, null, null));
//#ifndef MENU
        addCommand(cmdOk);
        
        if (getDirectory) {
            addCommand(cmdSelect);
        } else {
            addCommand(cmdView);
        }
	addCommand(cmdDelete);
        addCommand(cmdRoot);
        addCommand(cmdBack);
        addCommand(cmdCancel);
        setCommandListener(this);
//#endif
        // test for empty path
        if (path==null) path="";
       
        // trim filename
        int l=path.lastIndexOf('/');
        if (l<0) {  path=""; 
        } else path=path.substring(0,l+1);

        chDir(path);
    }
    
    protected int getItemCount() { return dir.size(); }
    
    protected VirtualElement getItemRef(int index) { return (VirtualElement) dir.elementAt(index); }
//#ifndef MENU
    public void commandAction(Command command, Displayable displayable) {
        if (command==cmdBack) {
            if (!chDir("../")) {
                destroyView();
                return;
            }
            redraw();
        }
        if (command==cmdRoot) {
            path="";
            chDir(path);
			return;
        }
        if (command==cmdOk) eventOk();
        if (command==cmdSelect) {
            String f=((FileItem)getFocusedObject()).name;
            if (f.endsWith("/")) {
                if (f.startsWith("../")) f="";
                if (browserListener==null) return;
                destroyView();
                browserListener.BrowserFilePathNotify(path+f);
                return;
            }
            //todo: choose directory here, drop ../
        }
        
        if (command==cmdDelete) {
            fileDelete();
        }

        if (command==cmdView) {
            showFile();
        }
        if (command==cmdCancel) { destroyView(); }
    }
//#endif
    
     private boolean chDir(String relativePath) {
        String focus="";
         if (relativePath.startsWith("/")) {
             path=relativePath;
         } else if (relativePath.startsWith("../")) {
            if (path.length()==0) return false;
            if (path.length()==1) { 
                path="";
            } else {
                int remainderPos=path.lastIndexOf('/', path.length()-2) + 1;
                focus=path.substring(remainderPos);
                path=path.substring(0, 1+path.lastIndexOf('/', path.length()-2));
            }
        } else {
            //if (path.length()==0) path="/";
             path+=relativePath;
        }
        readDirectory(this.path);
        sort(dir);

        for (int i=0; i<dir.size(); i++) {
            if ( ((FileItem)dir.elementAt(i)).name.equals(focus) ) {
                moveCursorTo(i);
                return true;
            }
        }
        moveCursorHome();
         return true;
     }
     
//#ifdef MENU
//#         public void leftCommand() {
//#             new BrowserMenu(display, this);
//#         }
//#         public String getLeftCommand() {
//#             return SR.MS_MENU;
//#         }
//#endif
    
    private void readDirectory(String name) {
        getMainBarItem().setElementAt((path.endsWith("/"))?path.substring(0, path.length()-1):path, 0);
        
        dir=new Vector();
        
        try {
            FileIO f=FileIO.createConnection(name);
            
            Enumeration files=f.fileList(getDirectory).elements();
            
            while (files.hasMoreElements())
                dir.addElement( new FileItem((String) files.nextElement()) );
            
        } catch (Exception ex) {
            dir.addElement( new FileItem("../(Restricted Access)"));
            ex.printStackTrace();
        }
    }
    
    public void fileDelete() {
        String f=((FileItem)getFocusedObject()).name;
        if (f.endsWith("/"))
            return;
        try {
            FileIO fio=FileIO.createConnection(path+f);
            fio.delete();
            fio.close();
            dir.removeElement(getFocusedObject());
            redraw();
        } catch (Exception e) { e.printStackTrace(); }
    }
    
    public void showFile() {
        FileItem fi=(FileItem)getFocusedObject();
        if (fi.getType()<4 && fi.getType()>0)
            new ShowFile(display, path+fi.name, fi.getType());
    }
    
    public void eventOk() {
        String f=((FileItem)getFocusedObject()).name;
        if (!f.endsWith("/")) {
            if (browserListener==null) return;
            destroyView();
            browserListener.BrowserFilePathNotify(path+f);
            return;
        }
        if (!chDir(f)) { 
            destroyView(); 
            return; 
        }
        
        redraw();
    }
    
    
    private class FileItem extends IconTextElement {
        
        public String name;
        private int iconIndex;
        private int type;
        
        public FileItem(String name) {
            super(RosterIcons.getInstance());
            this.name=name;
            //TODO: file icons
            iconIndex=name.endsWith("/")? RosterIcons.ICON_COLLAPSED_INDEX: RosterIcons.ICON_PROFILE_INDEX;
            
            String ext=name.substring(name.lastIndexOf('.')+1).toLowerCase();
            String imgs="png.bmp.jpg.jpeg.gif";
            String snds="wav.mid.amr.wav.mp3.aac";
            String txts="txt.log";
            
            if (txts.indexOf(ext)>=0) {
                iconIndex=RosterIcons.ICON_PRIVACY_ACTIVE;
                type=3;
                return;
            }
            if (imgs.indexOf(ext)>=0) {
                iconIndex=0x57;
                type=2;
                return;
            }
            if (snds.indexOf(ext)>=0) {
                iconIndex=0x33;
                type=1;
                return;
            }
        }
        
        public int getImageIndex() { 
            return iconIndex; 
        }
        
        public String toString() { return (name.endsWith("/"))?name.substring(0, name.length()-1):name; }
        
        public int compare(IconTextElement right){
            FileItem fileItem=(FileItem) right;
            
            int cpi=iconIndex-fileItem.iconIndex;
            if (cpi==0) cpi=name.compareTo(fileItem.name);
            return cpi;
        }
        
        public int getType() {
            return type;
        }
    }
//#ifdef MENU
//#     class BrowserMenu extends Menu {
//#         private Displayable parentView;
//#         
//#         private Browser browser;
//#         
//#         public BrowserMenu (Display display, Browser browser) {
//#             super("", null);
//#             this.browser=browser;
//#             this.parentView=display.getCurrent();
//#     
//#             addItem(SR.MS_BROWSE, 1);
//# 
//#             if (getDirectory) {
//#                 addItem(SR.MS_SELECT, 2);
//#             } else {
//#                 addItem(SR.MS_VIEW, 3);
//#             }
//#             addItem(SR.MS_ROOT, 4);
//#             addItem(SR.MS_DELETE, 5);
//#             addItem(SR.MS_BACK, 6);
//#             addItem(SR.MS_CANCEL, 7);
//# 
//#             attachDisplay(display);
//#         }
//# 
//#         public void eventOk(){
//#             MenuItem me=(MenuItem) getFocusedObject();
//#             if (me==null)  return;
//#             int index=me.index;
//#            
//#             switch (index) {
//#                 case 1:
//#                     browser.eventOk();
//#                     break;
//#                 case 2:
//#                     String f=((FileItem) browser.getFocusedObject()).name;
//#                     if (f.endsWith("/")) {
//#                         if (f.startsWith("../")) f="";
//#                         if (browser.browserListener==null) return;
//#                         browser.destroyView();
//#                         browser.browserListener.BrowserFilePathNotify(path+f);
//#                     }
//#                     //todo: choose directory here, drop ../
//#                     break;
//#                 case 3:
//#                     destroyView();
//#                     browser.showFile();
//#                     return;
//#                 case 4:
//#                     browser.path="";
//#                     browser.chDir("");
//#                     break;
//#                 case 5:
//#                     browser.fileDelete();
//#                     break;
//#                 case 6:
//#                     if (!browser.chDir("../"))
//#                         browser.destroyView();
//#                     else
//#                         browser.redraw();
//#                     break;
//#                 case 7:
//#                     browser.destroyView();
//#                     break;
//#             }
//#             destroyView();
//#         }
//#     }
//#endif
}
