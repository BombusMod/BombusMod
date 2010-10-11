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

import Client.StaticData;
import Menu.MenuCommand;
import Menu.MyMenu;

import ui.MainBar;
import images.RosterIcons;
import io.file.FileIO;
import java.util.Enumeration;

import locale.SR;
import ui.IconTextElement;
import ui.VirtualList;
import ui.controls.form.DefForm;

/**
 *
 * @author evgs
 */
public class Browser extends DefForm {
 
    MenuCommand cmdBrowse=new MenuCommand(SR.MS_BROWSE, MenuCommand.OK, 1);
    MenuCommand cmdSelect=new MenuCommand(SR.MS_SELECT, MenuCommand.SCREEN, 2);
    MenuCommand cmdView=new MenuCommand(SR.MS_VIEW, MenuCommand.SCREEN, 3);
    MenuCommand cmdRoot=new MenuCommand(SR.MS_ROOT, MenuCommand.SCREEN, 4);
    MenuCommand cmdDelete=new MenuCommand(SR.MS_DELETE, MenuCommand.SCREEN, 5);
    MenuCommand cmdExit=new MenuCommand(SR.MS_CANCEL, MenuCommand.EXIT, 99);

    private String path;
    private BrowserListener browserListener;

    private boolean getDirectory;
    
    /** Creates a new instance of Browser */
    public Browser(String path, BrowserListener browserListener, boolean getDirectory) {
        super(null);
        
        this.browserListener=browserListener;
	this.getDirectory=getDirectory;
//#ifdef FILE_IO
        this.path=(path==null)?StaticData.getInstance().previousPath:path;
//#endif

        // test for empty path
        if (path==null) path="";

        setMainBarItem(new MainBar(2, null, null, false));
        
        menuCommands.removeAllElements();
        
        addMenuCommand(cmdBrowse);
        
        if (getDirectory) {
            addMenuCommand(cmdSelect);
        } else {
            addMenuCommand(cmdView);
        }
	addMenuCommand(cmdDelete);
        addMenuCommand(cmdRoot);
        addMenuCommand(cmdExit);
        addMenuCommand(cmdCancel);
        
        // trim filename
        int l=path.lastIndexOf('/');
        if (l<0)
            path=""; 
        else
            path=path.substring(0,l+1);

        chDir(path);      
        
    }
    
    public void cmdCancel() {
        if (!chDir("../")) {
            destroyView();            
        } else {
            redraw();
        }
    }

    public void menuAction(MenuCommand command, VirtualList displayable) {
        if (command==cmdCancel) cmdCancel();

        if (command==cmdRoot) {
            path="";
            chDir(path);
            return;
        }
        if (command==cmdBrowse) eventOk();
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
        if (command==cmdExit) { destroyView(); }
    }
    
    public void destroyView(){
//#ifdef FILE_IO
        StaticData.getInstance().previousPath=path;
//#endif
        super.destroyView();
    }

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
        sort(itemsList);

        for (int i=0; i<itemsList.size(); i++) {
            if ( ((FileItem)itemsList.elementAt(i)).name.equals(focus) ) {
                moveCursorTo(i);
                return true;
            }
        }
        moveCursorHome();
        return true;
     }
    
    private void readDirectory(String name) {
        getMainBarItem().setElementAt((path.endsWith("/"))?path.substring(0, path.length()-1):path, 0);
        
        itemsList.removeAllElements();
        try {
            FileIO f=FileIO.createConnection(name);
            
            Enumeration files=f.fileList(getDirectory).elements();
            
            while (files.hasMoreElements())
                itemsList.addElement( new FileItem((String) files.nextElement()) );
            
        } catch (Exception ex) {
            itemsList.addElement( new FileItem("../(Restricted Access)"));
//#ifdef DEBUG            
//#             ex.printStackTrace();
//#endif            
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
            itemsList.removeElement(getFocusedObject());
            redraw();
        } catch (Exception e) {
//#ifdef DEBUG            
//#             e.printStackTrace(); 
//#endif            
        }
    }
    
    public void showFile() {
        FileItem fi=(FileItem)getFocusedObject();
        if (fi.getType()<4 && fi.getType()>0)
            new ShowFile( path+fi.name, fi.getType());
    }
    
    public void eventOk() {
        String f=((FileItem)getFocusedObject()).name;
        if (!f.endsWith("/")) {
            if (browserListener==null) {
                showFile();
                return;
            }
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
    public void showMenu() {
        new MyMenu(this, SR.MS_DISCO, null, menuCommands);
    }   
    
    public void touchLeftPressed() {
        showMenu();
    }
    
    public String touchLeftCommand() {
        return SR.MS_MENU;
    }
    
}
