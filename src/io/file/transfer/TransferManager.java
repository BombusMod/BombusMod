/*
 * TransferManager.java
 *
 * Created on 28.10.2006, 17:00
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

package io.file.transfer;

import Client.StaticData;
import Menu.MenuCommand;
import locale.SR;
import ui.Time;
//#ifdef POPUPS
import ui.controls.PopUp;
//#endif
import ui.VirtualList;
import ui.controls.form.DefForm;

/**
 *
 * @author Evg_S
 */
public class TransferManager
    extends DefForm
    {
//#ifdef PLUGINS
//#     public static String plugin = new String("PLUGIN_FILE_TRANSFER");
//#endif    
    
    
    MenuCommand cmdDel=new MenuCommand(SR.MS_DECLINE, MenuCommand.SCREEN, 10);
    MenuCommand cmdClrF=new MenuCommand(SR.MS_HIDE_FINISHED, MenuCommand.SCREEN, 11);
    MenuCommand cmdInfo=new MenuCommand(SR.MS_INFO, MenuCommand.SCREEN, 12);
    MenuCommand cmdSettings=new MenuCommand("Transfer settings", MenuCommand.SCREEN, 12);
    
    /** Creates a new instance of TransferManager
     */
    public TransferManager() {
        super(SR.MS_TRANSFERS);        
        itemsList=TransferDispatcher.getInstance().getTaskList();
        // TODO: add classic menu
        setMenuListener(this);
    }    
    
    public void commandState(){
        super.commandState();
        addMenuCommand(cmdSettings);
        if (TransferDispatcher.getInstance().getTasksCount()>0) {
            removeMenuCommand(cmdOk);
        }
        addMenuCommand(cmdDel);
        addMenuCommand(cmdClrF);
        addMenuCommand(cmdInfo);
    }   
    public String touchLeftCommand(){ return SR.MS_MENU; }
    public void touchLeftPressed(){ 
            showMenu();         
    }

    

    public void eventOk() {
        TransferTask t=(TransferTask) getFocusedObject();
        if (t!=null)
            if (t.isAcceptWaiting()) new TransferAcceptFile(t);
    }
    
    protected void keyClear() {
        if (getItemCount()>0) {
            synchronized (TransferDispatcher.getInstance().getTaskList()) {
                TransferTask task=(TransferTask) TransferDispatcher.getInstance().getTaskList().elementAt(cursor);
                task.cancel();
                TransferDispatcher.getInstance().getTaskList().removeElementAt(cursor);
            }
        }
    }

    public void menuAction(MenuCommand c, VirtualList d) {
        super.menuAction(c, d);
        if (c==cmdClrF) {
            synchronized (TransferDispatcher.getInstance().getTaskList()) {
                int i=0;
                while (i<TransferDispatcher.getInstance().getTaskList().size()) {
                    TransferTask task=(TransferTask)TransferDispatcher.getInstance().getTaskList().elementAt(i);
                    if (task.isStopped()) 
                        TransferDispatcher.getInstance().getTaskList().removeElementAt(i);
                    else 
                        i++;
                }
            }
            if (getItemCount()<1)
                StaticData.getInstance().roster.setEventIcon(null);
            redraw();
        }
        if (c==cmdDel) keyClear();        
        if (c==cmdInfo) cmdInfo();
        if (c==cmdSettings) new TransferConfigForm(this);       
    }
    public void cmdOk() {
        TransferDispatcher.getInstance().eventNotify();
        destroyView();
    }
    
    protected boolean key(int keyCode,  boolean key_long) { // overriding this method to avoid autorepeat
        if (!key_long) {
            switch (keyCode) {
                case KEY_POUND:
                    cmdInfo();
                    return true;
            }
        }
        
        return super.key(keyCode, key_long);
    }    

    private void cmdInfo() {
        if (getItemCount()>0) {
            TransferTask t=(TransferTask) getFocusedObject();
            StringBuffer info=new StringBuffer();
            info.append(t.jid)
                .append("\n")
                .append(t.fileName)
                .append("\n")
                .append(t.fileSize)
                .append(" bytes");
            if (!t.description.equals(""))
                info.append("\n").append(t.description);
            if (t.isStarted() && t.started!=0)
                info.append("\nStarted: ").append(Time.dateTimeLocalString(t.started));
            if (t.isStopped() && t.finished!=0)
                info.append("\nFinished: ").append(Time.dateTimeLocalString(t.finished));
            if (t.errMsg!=null)
                info.append("\nError: ").append(t.errMsg);
//#ifdef POPUPS
            PopUp.getInstance().addPopup(1, null, info.toString());
            redraw();
//#endif
        }
    }
}
