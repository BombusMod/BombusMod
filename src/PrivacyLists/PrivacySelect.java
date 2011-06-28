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

import Client.StaticData;
import javax.microedition.lcdui.TextField;
import images.RosterIcons;
import Menu.MenuCommand;
import locale.SR;
import ui.*;
import ui.controls.AlertBox;
import java.util.*;
import com.alsutton.jabber.*;
import ui.controls.form.DefForm;

/**
 *
 * @author EvgS
 */
public class PrivacySelect 
        extends DefForm
        implements
        JabberBlockListener,
        MIDPTextBox.TextBoxNotify
{
//#ifdef PLUGINS
//#     public static String plugin = new String("PLUGIN_PRIVACY");
//#endif
    
    private MenuCommand cmdActivate=new MenuCommand (SR.MS_ACTIVATE, MenuCommand.OK, 1);
    private MenuCommand cmdDefault=new MenuCommand (SR.MS_DEFAULT, MenuCommand.SCREEN, 11);
    private MenuCommand cmdNewList=new MenuCommand (SR.MS_NEW_LIST, MenuCommand.SCREEN, 12);
    private MenuCommand cmdDelete=new MenuCommand (SR.MS_DELETE_LIST, MenuCommand.SCREEN, 13);
    //private MenuCommand cmdEdit=new MenuCommand (SR.MS_EDIT_LIST, Command.SCREEN, 14);
    JabberStream stream=StaticData.getInstance().roster.theStream;
    
    /** Creates a new instance of PrivacySelect
     */
    public PrivacySelect() {
        super(null);

        setMainBarItem(new MainBar(2, null, SR.MS_PRIVACY_LISTS, false));

        itemsList.addElement(new PrivacyListItem(new PrivacyList(null)));//none
        
        enableListWrapping(true);
        
        getLists();
    }
    
    public void commandState() {
        menuCommands.removeAllElements();
        addMenuCommand(cmdActivate);
        addMenuCommand(cmdDefault);
        addMenuCommand(cmdNewList);
        addMenuCommand(cmdDelete);        
    }

    private void processIcon(boolean processing){
        getMainBarItem().setElementAt((processing)?(Object)new Integer(RosterIcons.ICON_PROGRESS_INDEX):(Object)null, 0);
        redraw();
    }
   
    protected final void getLists() {
        try {
            Thread.sleep(500);
        } catch (Exception e) { }
        stream.addBlockListener(this);
        processIcon(true);
        PrivacyList.privacyListRq(false, null, "getplists");
    }
    
    public void menuAction(MenuCommand c, VirtualList d) {
        if (c==cmdCancel) {
            destroyView();
            stream.cancelBlockListener(this);
            return;
        }
        if (c==cmdActivate || c==cmdDefault) {
            PrivacyListItem active=((PrivacyListItem)getFocusedObject());
            for (Enumeration e = itemsList.elements(); e.hasMoreElements(); ) {
                PrivacyListItem pl=(PrivacyListItem)e.nextElement();
                boolean state=(pl==active);
                if (c==cmdActivate)
                    pl.list.isActive=state;
                else
                    pl.list.isDefault=state;
            }
            ((PrivacyListItem)getFocusedObject()).list.activate( (c==cmdActivate)? "active":"default" ); 
            getLists();
        }
        
        if (c==cmdDelete) {
            keyClear();
        }
        if (c==cmdNewList)
            addNewElement();
        super.menuAction(c, d);
    }
    
    // MIDPTextBox interface
    public void OkNotify(String listName) {
        if (listName.length()>0)
            new PrivacyModifyList(new PrivacyList(listName), true, this);
    }
    
    public int blockArrived(JabberDataBlock data) {
        try {
            if (data.getTypeAttribute().equals("result"))
                if (data.getAttribute("id").equals("getplists")) {
                data=data.findNamespace("query", "jabber:iq:privacy");
                if (data!=null) {
                    itemsList.removeAllElements();
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
                                itemsList.addElement(new PrivacyListItem(pl));
                            }
                        }
                    } catch (Exception e) {}
                    PrivacyList nullList=new PrivacyList(null);
                    nullList.isActive=activeList.length()==0;
                    nullList.isDefault=defaultList.length()==0;
                    itemsList.addElement(new PrivacyListItem(nullList));//none
                }
                
                processIcon(false);
                
                return JabberBlockListener.NO_MORE_BLOCKS;
                }
        } catch (Exception e) { }
        return JabberBlockListener.BLOCK_REJECTED;
    }

    public void eventOk() {
        PrivacyListItem pl = (PrivacyListItem) getFocusedObject();
        if (pl != null) {
            if (pl.list.name != null)
                new PrivacyModifyList(pl.list, false, this);
        }
    }    
    
    public void addNewElement() {
        new MIDPTextBox(SR.MS_NEW, "", this, TextField.ANY);
    }

    public void cmdDelete() {
        PrivacyListItem pl = (PrivacyListItem) getFocusedObject();
        if ((pl == null) || (pl.list.name == null))
            return;
        pl.list.deleteList();
        getLists();
    }
	
	public void keyClear() {
        String name = getFocusedObject().toString();
        new AlertBox(name, SR.MS_DELETE_LIST + " \"" + name + "\"?") {

            public void yes() {
                cmdDelete();
            }

            public void no() {}
        };
    }    
}
