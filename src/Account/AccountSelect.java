/*
 * AccountSelect.java
 *
 * Created on 19.03.2005, 23:26
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

package Account;
import Client.*;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import locale.SR;
import midlet.BombusMod;
import ui.*;
import java.io.*;
import java.util.*;
//#ifndef MENU_LISTENER
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Command;
//#else
//# import Menu.MenuListener;
//# import Menu.Command;
//# import Menu.MyMenu;
//#endif
import ui.MainBar;
import io.NvStorage;
import ui.controls.AlertBox;


/**
 *
 * @author Eugene Stahov
 */
public class AccountSelect 
    extends VirtualList 
    implements
//#ifndef MENU_LISTENER
        CommandListener
//#else
//#         MenuListener
//#endif
    {

    public Vector accountList;
    int activeAccount;
    boolean enableQuit;
    
    Command cmdLogin=new Command(SR.MS_SELLOGIN, Command.OK,1);
    Command cmdSelect=new Command(SR.MS_NOLOGIN, Command.SCREEN,2);
    Command cmdAdd=new Command(SR.MS_NEW_ACCOUNT, Command.SCREEN,3);
    Command cmdEdit=new Command(SR.MS_EDIT,Command.ITEM,3);
    Command cmdDel=new Command(SR.MS_DELETE,Command.ITEM,4);
    Command cmdConfig=new Command(SR.MS_OPTIONS,Command.ITEM,5);
    Command cmdCancel=new Command(SR.MS_BACK,Command.BACK,99);
    Command cmdQuit=new Command(SR.MS_APP_QUIT,Command.SCREEN,10);
    
    private Config cf;
    
    /** Creates a new instance of AccountPicker */
    public AccountSelect(Display display, boolean enableQuit) {
        super();
        this.enableQuit=enableQuit;
        this.display=display;
        
        cf=Config.getInstance();
        
        setMainBarItem(new MainBar(SR.MS_ACCOUNTS));

        if (enableQuit) {
            VirtualList.canBack=false;
        }
        accountList=null;
        accountList=new Vector();
        Account a;
        
        int index=0;
        activeAccount=cf.accountIndex;
        do {
            a=Account.createFromStorage(index);
            if (a!=null) {
                a.setActive(activeAccount==index);
                accountList.addElement(a);
                index++;
             }
       } while (a!=null);
        
        if (!accountList.isEmpty()) {
            moveCursorTo(activeAccount);
        } else {
            new AccountForm(this, display, null);
            return;
        }
        
        attachDisplay(display);

        commandState();
        setCommandListener(this);
    }
    
    public void commandState(){
//#ifdef MENU_LISTENER
//#         menuCommands.removeAllElements();
//#endif
        if (!accountList.isEmpty()) {
            addCommand(cmdLogin);
            addCommand(cmdSelect);
            
            addCommand(cmdEdit);
            addCommand(cmdDel);
        }
        addCommand(cmdAdd);
        addCommand(cmdConfig);
        
        if (activeAccount>=0 && !enableQuit)
            addCommand(cmdCancel);  // нельз�? выйти без активного аккаунта

        if (enableQuit) 
            addCommand(cmdQuit);
    }

    public VirtualElement getItemRef(int Index) { return (VirtualElement)accountList.elementAt(Index); }
    protected int getItemCount() { return accountList.size();  }

    public void commandAction(Command c, Displayable d){
        if (c==cmdQuit) {
            destroyView();
            BombusMod.getInstance().notifyDestroyed();
            return;
        }
        VirtualList.canBack=true;
        if (c==cmdCancel) {
            destroyView();
        }
        if (c==cmdConfig) new ConfigForm(display);
        if (c==cmdLogin) switchAccount(true);
        if (c==cmdSelect) switchAccount(false);
        if (c==cmdEdit) new AccountForm(this, display,(Account)getFocusedObject());
        if (c==cmdAdd) {
            new AccountForm(this, display, null);
        }
        if (c==cmdDel) {
            if (cursor==cf.accountIndex && StaticData.getInstance().roster.isLoggedIn()) return;
            //if (((Account)getFocusedObject()).equals(StaticData.getInstance().account)) return;
            
            new AlertBox(SR.MS_DELETE, getFocusedObject().toString(), display, this) {
                public void yes() {
                    delAccount();
                }
                public void no() { }
            };
        }
    }
    
    public void touchLeftPressed(){
        eventOk();
    }
    
    public void touchRightPressed(){
        if (!canBack) 
            return;
        destroyView();
    }

    public void destroyView(){
        if(accountList.size()>0) {
            if (StaticData.getInstance().account==null)
                Account.loadAccount(false, cf.accountIndex);
            display.setCurrent(StaticData.getInstance().roster);
        }
    }

    private void delAccount(){
        if (cf.accountIndex>cursor) cf.accountIndex--;
        cf.saveToStorage();

        accountList.removeElement(getFocusedObject());
        rmsUpdate();
        moveCursorHome();
        commandState();
        redraw();
    }
    
    private void switchAccount(boolean login){
        cf.accountIndex=cursor;
        cf.saveToStorage();
        Account.loadAccount(login, cursor);
        destroyView();
    }
    
    public void eventOk(){
        if (getItemCount()>0) {
            VirtualList.canBack=true;
            switchAccount(true);
        }
    }
    
    public void rmsUpdate(){
        DataOutputStream outputStream=NvStorage.CreateDataOutputStream();
        for (int i=0;i<accountList.size();i++) 
            ((Account)accountList.elementAt(i)).saveToDataOutputStream(outputStream);
        NvStorage.writeFileRecord(outputStream, Account.storage, 0, true);
    }
//#ifdef MENU_LISTENER    
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
//#             if (canBack) destroyView();
//#             return;
//#         }
//#         super.keyPressed(keyCode);
//#     }
//#     
//#     public void showMenu() {
//#         commandState();
//#         new MyMenu(display, this, SR.MS_DISCO, null);
//#    }
//#endif
    
    protected void keyRepeated(int keyCode) {
        super.keyRepeated(keyCode);
        if (kHold==keyCode) return;
        kHold=keyCode;
        
        if (keyCode==KEY_NUM6) {
            Config cf=Config.getInstance();
            cf.fullscreen=!cf.fullscreen;
            cf.saveToStorage();
            VirtualList.fullscreen=cf.fullscreen;
            StaticData.getInstance().roster.setFullScreenMode(cf.fullscreen);
        }
    }
}
