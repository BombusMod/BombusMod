/*
 * PrivacySelect.java
 *
 * Created on 26.08.2005, 23:04
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
 *
 */

package PrivacyLists;

import Client.Config;
import Client.StaticData;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextField;
import images.RosterIcons;
//#ifndef MENU_LISTENER
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Command;
//#else
//# import Menu.MenuListener;
//# import Menu.Command;
//# import Menu.MyMenu;
//#endif
import locale.SR;
import ui.*;
import java.util.*;
import com.alsutton.jabber.*;

/**
 *
 * @author EvgS
 */
public class PrivacySelect 
        extends VirtualList 
        implements
//#ifndef MENU_LISTENER
        CommandListener,
//#else
//#         MenuListener,
//#endif
        JabberBlockListener,
        MIDPTextBox.TextBoxNotify
{
    public static String plugin = new String("PLUGIN_PRIVACY");
    
    private Vector list=new Vector();
    
    private Command cmdCancel=new Command (SR.MS_BACK, Command.BACK, 99);
    private Command cmdActivate=new Command (SR.MS_ACTIVATE, Command.SCREEN, 10);
    private Command cmdDefault=new Command (SR.MS_SETDEFAULT, Command.SCREEN, 11);
    private Command cmdNewList=new Command (SR.MS_NEW_LIST, Command.SCREEN, 12);
    private Command cmdDelete=new Command (SR.MS_DELETE_LIST, Command.SCREEN, 13);
    //private Command cmdEdit=new Command (SR.MS_EDIT_LIST, Command.SCREEN, 14);
    private Command cmdIL=new Command (SR.MS_MK_ILIST, Command.SCREEN, 16);
    
    JabberStream stream=StaticData.getInstance().roster.theStream;
    
    /** Creates a new instance of PrivacySelect */
    public PrivacySelect(Display display, Displayable pView) {
        super(display);
        
        setMainBarItem(new MainBar(2, null, SR.MS_PRIVACY_LISTS));

        list.addElement(new PrivacyList(null));//none
        
        commandState();
        
        getLists();
        this.parentView=pView;
    }
    
    public void commandState() {
//#ifdef MENU_LISTENER
//#         menuCommands.removeAllElements();
//#endif
        addCommand(cmdActivate);
        addCommand(cmdDefault);
        addCommand(cmdCancel);
        addCommand(cmdNewList);
        addCommand(cmdDelete);
        addCommand(cmdIL);
    }

    private void processIcon(boolean processing){
        getMainBarItem().setElementAt((processing)?(Object)new Integer(RosterIcons.ICON_PROGRESS_INDEX):(Object)null, 0);
        redraw();
    }
   
    private void getLists(){
        stream.addBlockListener(this);
        processIcon(true);
        PrivacyList.privacyListRq(false, null, "getplists");
    }
    
    protected int getItemCount() { return list.size(); }
    protected VirtualElement getItemRef(int index) { return (VirtualElement) list.elementAt(index); }
    public void commandAction(Command c, Displayable d) {
        if (c==cmdCancel) {
            destroyView();
            stream.cancelBlockListener(this);
        }
        if (c==cmdActivate || c==cmdDefault) {
            PrivacyList active=((PrivacyList)getFocusedObject());
            for (Enumeration e=list.elements(); e.hasMoreElements(); ) {
                PrivacyList pl=(PrivacyList)e.nextElement();
                boolean state=(pl==active);
                if (c==cmdActivate) pl.isActive=state; else pl.isDefault=state;
            }
            ((PrivacyList)getFocusedObject()).activate( (c==cmdActivate)? "active":"default" ); 
            getLists();
        }
        if (c==cmdIL) {
            generateIgnoreList();
            getLists();
        }
        if (c==cmdDelete) {
            PrivacyList pl=(PrivacyList) getFocusedObject();
            if (pl!=null) {
                if (pl.name!=null) pl.deleteList();
                getLists();
            }
        }
        if (c==cmdNewList)
            new MIDPTextBox(display, SR.MS_NEW, "", this, TextField.ANY);
    }
    
//#ifdef MENU_LISTENER
//#     public Vector menuCommands=new Vector();
//#     
//#     public Command getCommand(int index) {
//#         if (index>menuCommands.size()-1) return null;
//#         return (Command) menuCommands.elementAt(index);
//#     }
//# 
//#     public void touchLeftPressed(){
//#         showMenu();
//#     }
//# 
//#     public void addCommand(Command command) {
//#         if (menuCommands.indexOf(command)<0)
//#             menuCommands.addElement(command);
//#     }
//#     public void removeCommand(Command command) {
//#         menuCommands.removeElement(command);        
//#     }
//#     
//#     public void setCommandListener(MenuListener menuListener) { }
//#     
//#     protected void keyPressed(int keyCode) { // overriding this method to avoid autorepeat
//#         if (keyCode==Config.SOFT_LEFT) {
//#             showMenu();
//#             return;
//#         }
//#         if (keyCode==Config.SOFT_RIGHT || keyCode==Config.KEY_BACK) {
//#             destroyView();
//#             return;
//#         }
//#         super.keyPressed(keyCode);
//#     }
//# 
//#     public void showMenu() {
//#         commandState();
//#         new MyMenu(display, parentView, this, SR.MS_STATUS, null, menuCommands);
//#     }
//#endif
    
    // MIDPTextBox interface
    public void OkNotify(String listName) {
        if (listName.length()>0)
            new PrivacyModifyList(display, this, new PrivacyList(listName));
    }
    
    public int blockArrived(JabberDataBlock data){
        try {
            if (data.getTypeAttribute().equals("result"))
                if (data.getAttribute("id").equals("getplists")) {
                data=data.findNamespace("query", "jabber:iq:privacy");
                if (data!=null) {
                    list=null;
                    list=new Vector();
                    String activeList="";
                    String defaultList="";
                    try {
                        for (Enumeration e=data.getChildBlocks().elements(); e.hasMoreElements();) {
                            JabberDataBlock pe=(JabberDataBlock) e.nextElement();
                            String tag=pe.getTagName();
                            String name=pe.getAttribute("name");
                            if (tag.equals("active")) activeList=name;
                            if (tag.equals("default")) defaultList=name;
                            if (tag.equals("list")) {
                                PrivacyList pl=new PrivacyList(name);
                                pl.isActive=(name.equals(activeList));
                                pl.isDefault=(name.equals(defaultList));
                                list.addElement(pl);
                            }
                        }
                    } catch (Exception e) {}
                    PrivacyList nullList=new PrivacyList(null);
                    nullList.isActive=activeList.length()==0;
                    nullList.isDefault=defaultList.length()==0;
                    list.addElement(nullList);//none
                }
                
                processIcon(false);
                
                return JabberBlockListener.NO_MORE_BLOCKS;
                }
        } catch (Exception e) { }
        return JabberBlockListener.BLOCK_REJECTED;
    }

    public void eventOk(){
        PrivacyList pl=(PrivacyList) getFocusedObject();
        if (pl!=null) {
            if (pl.name!=null) new PrivacyModifyList(display, this, pl);
        }
    }
    private void generateIgnoreList(){
        JabberDataBlock ignoreList=new JabberDataBlock("list", null, null);
        ignoreList.setAttribute("name", SR.MS_IGNORE_LIST);
        JabberDataBlock item=PrivacyItem.itemIgnoreList().constructBlock();
        ignoreList.addChild(item);
        PrivacyList.privacyListRq(true, ignoreList, "ignlst");
    }
}
