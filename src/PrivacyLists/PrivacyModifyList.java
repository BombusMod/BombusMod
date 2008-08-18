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

import Client.Config;
import Client.StaticData;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import ui.MainBar;
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
public class PrivacyModifyList 
        extends VirtualList 
        implements
//#ifndef MENU_LISTENER
        CommandListener,
//#else
//#         MenuListener,
//#endif
        JabberBlockListener
{
    private PrivacyList plist;
    
    private Command cmdCancel=new Command (SR.MS_CANCEL, Command.BACK, 99);
    private Command cmdAdd=new Command (SR.MS_ADD_RULE, Command.SCREEN, 10);
    private Command cmdDel=new Command (SR.MS_DELETE_RULE, Command.SCREEN, 11);
    private Command cmdEdit=new Command (SR.MS_EDIT_RULE, Command.SCREEN, 12);
    private Command cmdUp=new Command (SR.MS_MOVE_UP, Command.SCREEN, 13);
    private Command cmdDwn=new Command (SR.MS_MOVE_DOWN, Command.SCREEN, 14);
    private Command cmdSave=new Command (SR.MS_SAVE_LIST, Command.SCREEN, 16);
    
    JabberStream stream=StaticData.getInstance().roster.theStream;
    
    /** Creates a new instance of PrivacySelect */
    public PrivacyModifyList(Display display, Displayable pView, PrivacyList privacyList) {
        super(display);
        setMainBarItem(new MainBar(2, null, SR.MS_PRIVACY_LISTS));

        commandState();

        plist=privacyList;
        getList();
        this.parentView=pView;
    }

    public void commandState() {
//#ifdef MENU_LISTENER
//#         menuCommands.removeAllElements();
//#endif
        addCommand(cmdCancel);
        addCommand(cmdEdit);
        addCommand(cmdAdd);
        addCommand(cmdDel);
        addCommand(cmdUp);
        addCommand(cmdDwn);
        addCommand(cmdSave);
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
    
    private void processIcon(boolean processing){
        getMainBarItem().setElementAt((processing)?(Object)new Integer(RosterIcons.ICON_PROGRESS_INDEX):(Object)null, 0);
        redraw();
    }

    private void getList(){
        processIcon(true);
        stream.addBlockListener(this);
        JabberDataBlock list=new JabberDataBlock("list", null, null);
        list.setAttribute("name", plist.name);
        PrivacyList.privacyListRq(false, list, "getlistitems");
    }
    
    protected int getItemCount() { return plist.rules.size(); }
    protected VirtualElement getItemRef(int index) { return (VirtualElement) plist.rules.elementAt(index); }
    
    public void commandAction(Command c, Displayable d) {
        if (c==cmdCancel) {
            stream.cancelBlockListener(this);
            destroyView();
        }
        if (c==cmdAdd) {
            new PrivacyForm(display, this, new PrivacyItem(), plist);
        }
        if (c==cmdEdit) eventOk();
        if (c==cmdDel) {
            Object del=getFocusedObject();
            if (del!=null) plist.rules.removeElement(del);
        }
        if (c==cmdSave) {
            plist.generateList();
            stream.cancelBlockListener(this);
            PrivacyList.privacyListRq(false, null, "setplists");
            destroyView();
        }
        
        if (c==cmdUp) { move(-1); keyUp(); }
        if (c==cmdDwn) { move(+1); keyDwn(); }
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
            
        } catch (Exception e) {/* IndexOutOfBounds */}
    }

    public void eventOk(){
        PrivacyItem pitem=(PrivacyItem) getFocusedObject();
        if (pitem!=null) {
            new PrivacyForm(display, this, pitem, null);
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
                return JabberBlockListener.NO_MORE_BLOCKS;
            } //id, result
        return JabberBlockListener.BLOCK_REJECTED;
    }

}
