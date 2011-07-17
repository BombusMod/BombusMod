/*
 * PrivacyModifyList.java
 *
 * Created on 11.09.2005, 15:51
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
import images.RosterIcons;
import Menu.MenuCommand;
import locale.SR;
import ui.*;
import java.util.*;
import com.alsutton.jabber.*;
import ui.controls.AlertBox;
import ui.controls.form.DefForm;

/**
 *
 * @author EvgS
 */
public class PrivacyModifyList extends DefForm
        implements JabberBlockListener
{
    
    private PrivacyList plist;
    private PrivacySelect pselector;
    
    private MenuCommand cmdAdd=new MenuCommand (SR.MS_ADD_RULE, MenuCommand.SCREEN, 10);
    private MenuCommand cmdDel=new MenuCommand (SR.MS_DELETE_RULE, MenuCommand.SCREEN, 11);
    private MenuCommand cmdEdit=new MenuCommand (SR.MS_EDIT_RULE, MenuCommand.SCREEN, 12);
    private MenuCommand cmdUp=new MenuCommand (SR.MS_MOVE_UP, MenuCommand.SCREEN, 13);
    private MenuCommand cmdDwn=new MenuCommand (SR.MS_MOVE_DOWN, MenuCommand.SCREEN, 14);
    private MenuCommand cmdSave=new MenuCommand (SR.MS_SAVE_LIST, MenuCommand.SCREEN, 16);
    
    JabberStream stream=StaticData.getInstance().roster.theStream;
    
    /** Creates a new instance of PrivacySelect */
    public PrivacyModifyList(PrivacyList privacyList, boolean newList, PrivacySelect privacySelect) {
        super(null);
        setMainBarItem(new MainBar(2, null, privacyList.name, false));

        //commandState();
        plist = privacyList;
        pselector = privacySelect;
        
        if (!newList) {
            processIcon(true);
            stream.addBlockListener(this);
            JabberDataBlock list=new JabberDataBlock("list", null, null);
            list.setAttribute("name", plist.name);
            PrivacyList.privacyListRq(false, list, "getlistitems");
        }
    }

    public void commandState() {
        menuCommands.removeAllElements();
        addMenuCommand(cmdEdit);
        addMenuCommand(cmdAdd);
        addMenuCommand(cmdDel);
        addMenuCommand(cmdUp);
        addMenuCommand(cmdDwn);
        addMenuCommand(cmdSave);
    }
    
    private void processIcon(boolean processing){
        getMainBarItem().setElementAt((processing)?(Object)new Integer(RosterIcons.ICON_PROGRESS_INDEX):(Object)null, 0);
        redraw();
    }

    /*private void getList(){
        processIcon(true);
        stream.addBlockListener(this);
        JabberDataBlock list=new JabberDataBlock("list", null, null);
        list.setAttribute("name", plist.name);
        PrivacyList.privacyListRq(false, list, "getlistitems");
    }*/
    
    protected int getItemCount() { return plist.rules.size(); }
    public VirtualElement getItemRef(int index) {
        if (index >= plist.rules.size()) return null;
        return (VirtualElement) plist.rules.elementAt(index);
    }
    
    public void menuAction(MenuCommand c, VirtualList d) {
        if (c==cmdCancel) {
            stream.cancelBlockListener(this);
            super.cmdCancel();
        }
        if (c==cmdAdd) {
            addNewElement();
        }
        if (c==cmdEdit) {
            eventOk();
        }
        if (c==cmdDel) {
            keyClear();
        }
        if (c==cmdSave) {
            plist.generateList();
            stream.cancelBlockListener(this);
            pselector.getLists();
            //PrivacyList.privacyListRq(false, null, "setplists");
            parentView = pselector;
            destroyView();
        }
        
        if (c==cmdUp) { move(-1); keyUp(); }
        if (c==cmdDwn) { move(+1); keyDwn(); }
        super.menuAction(c, d);
        redraw();
    }
    
    public void move(int offset){
        try {
            int index=cursor;
            PrivacyItem p1=(PrivacyItem)plist.rules.elementAt(index);
            PrivacyItem p2=(PrivacyItem)plist.rules.elementAt(index+offset);
            
            plist.rules.setElementAt(p1, index+offset);
            plist.rules.setElementAt(p2, index);
            
            int tmpOrder=p1.order;
            p1.order=p2.order;
            p2.order=tmpOrder;
            updateView();
        } catch (Exception e) {/* IndexOutOfBounds */}
    }

    public void eventOk(){
        PrivacyItem pitem=(PrivacyItem) getFocusedObject();
        if (pitem!=null) {
            new PrivacyForm(pitem, null);
        }
    }
    
    public int blockArrived(JabberDataBlock data){
        if (data.getTypeAttribute().equals("result"))
            if (data.getAttribute("id").equals("getlistitems")) {
                data=data.findNamespace("query", "jabber:iq:privacy");
                try {
                    data=data.getChildBlock("list");
                    plist.rules=null;
                    plist.rules=new Vector();
                    for (Enumeration e=data.getChildBlocks().elements(); e.hasMoreElements();) {
                        JabberDataBlock item=(JabberDataBlock) e.nextElement();
                        plist.addRule(new PrivacyItem(item));
                    }
                } catch (Exception e) {}
                
                processIcon(false);
                redraw();
                return JabberBlockListener.NO_MORE_BLOCKS;
            } //id, result
        return JabberBlockListener.BLOCK_REJECTED;
    }
    
    private void updateView() {
        itemsList.removeAllElements();
        for (Enumeration e = plist.rules.elements(); e.hasMoreElements();) {
            itemsList.addElement((PrivacyItem)e.nextElement());
        }
        redraw();
    }
    
    public void addNewElement() {
        new PrivacyForm(new PrivacyItem(), plist);
    }

    public void delRule() {
        if (getFocusedObject() != null)
            plist.rules.removeElement(getFocusedObject());
    }

    public void keyClear() {
        String name = getFocusedObject().toString();
        new AlertBox(name, SR.MS_DELETE_RULE + " \"" + name + "\"?") {
            public void yes() {
                delRule();
            }
            public void no() {
            }
        };
    }

    public boolean doUserKeyAction(int command_id) {
        switch (command_id) {
            case 54:
                addNewElement();
                return true;
        }

        return super.doUserKeyAction(command_id);
    }
}
