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
import javax.microedition.lcdui.Displayable;
import locale.SR;
import midlet.BombusMod;
import ui.*;
import java.io.*;
import java.util.*;
import Menu.MenuCommand;
import io.NvStorage;
import ui.controls.AlertBox;
//#ifdef IMPORT_EXPORT
import IE.*;
//#endif
import ui.controls.form.DefForm;


/**
 *
 * @author Eugene Stahov
 */
public class AccountSelect extends DefForm {
    public Vector accountList;
    int activeAccount;
    boolean enableQuit;
    
    MenuCommand cmdLogin=new MenuCommand(SR.MS_SELLOGIN, MenuCommand.OK,1);
    MenuCommand cmdSelect=new MenuCommand(SR.MS_NOLOGIN, MenuCommand.SCREEN,2);
    MenuCommand cmdAdd=new MenuCommand(SR.MS_NEW_ACCOUNT, MenuCommand.SCREEN,3);
    MenuCommand cmdEdit=new MenuCommand(SR.MS_EDIT,MenuCommand.ITEM,3);
    MenuCommand cmdDel=new MenuCommand(SR.MS_DELETE,MenuCommand.ITEM,4);
    MenuCommand cmdConfig=new MenuCommand(SR.MS_OPTIONS,MenuCommand.ITEM,5);
    MenuCommand cmdQuit=new MenuCommand(SR.MS_APP_QUIT,MenuCommand.SCREEN,10);
    
    private Config cf;
    
    /** Creates a new instance of AccountPicker */
    public AccountSelect(boolean enableQuit) {
        super(SR.MS_ACCOUNTS);
        this.enableQuit=enableQuit;
        
        enableListWrapping(true);
        cf=Config.getInstance();        
        
        if (enableQuit) {
            canBack = false;
        }
        accountList=null;
        accountList=new Vector();        
                
        activeAccount=cf.accountIndex;
        loadAccounts();
        show(StaticData.getInstance().roster);
    }
    public final void show(VirtualList pView) {
        super.show(pView);
        if (!accountList.isEmpty()) {
            moveCursorTo(activeAccount);
        } else {
//#ifdef IMPORT_EXPORT
//#ifdef PLUGINS
//#             if (StaticData.getInstance().IE) {
//#endif     
            new IE.Accounts("/def_accounts.txt", 0,  true);
//#ifdef PLUGINS                              
//#             }
//#endif
            loadAccounts();
        if (accountList.isEmpty()) {
//#endif
            new AccountForm(this, null).show(pView);
            return;
//#ifdef IMPORT_EXPORT
        }
//#endif
        }  

        
    }

    public final void loadAccounts() {
        Account a;
        int index=0;
        do {
            a=Account.createFromStorage(index);
            if (a!=null) {
                a.setActive(activeAccount==index);
                accountList.addElement(a);
                index++;
             }
       } while (a!=null);
    }

    public final void commandState(){
        menuCommands.removeAllElements();
        if ((accountList != null) && !accountList.isEmpty()) {
            addMenuCommand(cmdLogin);
            addMenuCommand(cmdSelect);
            
            addMenuCommand(cmdEdit);
            addMenuCommand(cmdDel);
        }
        addMenuCommand(cmdAdd);
        addMenuCommand(cmdConfig);
        if (enableQuit) 
            addMenuCommand(cmdQuit);
    }


    public void touchRightPressed(){
        if (!canBack)
            return;
        destroyView();
    }
    public String touchLeftCommand() { return SR.MS_MENU; }
    public void touchLeftPressed() {
        showMenu();
    }

    public VirtualElement getItemRef(int Index) {
        if (Index > accountList.size())
            Index = accountList.size() - 1;
        return (VirtualElement)accountList.elementAt(Index);
    }
    protected int getItemCount() { return accountList.size();  }

    public void menuAction(MenuCommand c, VirtualList d){
        if (c==cmdQuit) {
            destroyView();
            BombusMod.getInstance().notifyDestroyed();
            return;
        }
        if (c==cmdCancel) {
            destroyView();
        }
        if (c==cmdConfig) new ConfigForm(this);
        if (c==cmdLogin) switchAccount(true);
        if (c==cmdSelect) switchAccount(false);
        if (c==cmdEdit) new AccountForm(this, (Account)getFocusedObject()).show(this);
        if (c==cmdAdd) {
            new AccountForm(this, null).show(this);
        }
        if (c==cmdDel) {
            if (cursor==cf.accountIndex && StaticData.getInstance().roster.isLoggedIn()) return;
            //if (((Account)getFocusedObject()).equals(StaticData.getInstance().account)) return;
            
            new AlertBox(SR.MS_DELETE, getFocusedObject().toString()) {
                public void yes() {
                    delAccount();
                }
                public void no() { }
            };
        }
    }
    

    public void destroyView(){
        if(accountList.size()>0) {
            if (StaticData.getInstance().account==null)
                Account.loadAccount(false, cf.accountIndex);
            midlet.BombusMod.getInstance().setDisplayable(StaticData.getInstance().roster);
        }
    }

    private void delAccount(){
        if (accountList.size()==1) 
            cf.accountIndex=-1;
        else if (cf.accountIndex>cursor) cf.accountIndex--;

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
            canBack = true;
            switchAccount(true);
        }
    }
    
    public void rmsUpdate(){
        DataOutputStream outputStream=NvStorage.CreateDataOutputStream();
        int j=accountList.size();
        for (int i=0;i<j;i++) 
            ((Account)accountList.elementAt(i)).saveToDataOutputStream(outputStream);
        NvStorage.writeFileRecord(outputStream, "accnt_db", 0, true); //Account.storage
    }
    
    protected void keyRepeated(int keyCode) {
        super.keyRepeated(keyCode);
        if (kHold==keyCode) return;
        kHold=keyCode;
        
        if (keyCode==KEY_NUM6) {
            Config.fullscreen=!Config.fullscreen;
            cf.saveToStorage();
            VirtualList.fullscreen=Config.fullscreen;
            StaticData.getInstance().roster.setFullScreenMode(Config.fullscreen);
        }
    }
}
