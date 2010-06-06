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
//#ifndef MENU_LISTENER
//# import javax.microedition.lcdui.Command;
//#else
import Menu.Command;
//#endif
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import locale.SR;
import ui.Time;
//#ifdef POPUPS
import ui.controls.PopUp;
//#endif
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
    
    
    Command cmdDel=new Command(SR.MS_DECLINE, Command.SCREEN, 10);
    Command cmdClrF=new Command(SR.MS_HIDE_FINISHED, Command.SCREEN, 11);
    Command cmdInfo=new Command(SR.MS_INFO, Command.SCREEN, 12);
    Command cmdSettings=new Command("Transfer settings", Command.SCREEN, 12);
    
    /** Creates a new instance of TransferManager */
    public TransferManager(Display display, Displayable pView) {
        super(display, pView, SR.MS_TRANSFERS);        
        itemsList=TransferDispatcher.getInstance().getTaskList();
        // TODO: add classic menu
        setCommandListener(this);
        attachDisplay(display);
        parentView = pView;
    }    
    
//#ifdef MENU_LISTENER
    public void commandState(){
        super.commandState();
        addCommand(cmdSettings);
        if (TransferDispatcher.getInstance().getTasksCount()>0) {
            removeCommand(cmdOk);
        }
        addCommand(cmdDel);
        addCommand(cmdClrF);
        addCommand(cmdInfo);          
    }   
    public String touchLeftCommand(){ return SR.MS_MENU; }
    public void touchLeftPressed(){ 
            showMenu();         
    }
//#endif

    

    public void eventOk() {
        TransferTask t=(TransferTask) getFocusedObject();
        if (t!=null)
            if (t.isAcceptWaiting()) new TransferAcceptFile(display, this, t);
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

    public void commandAction(Command c, Displayable d) {
        super.commandAction(c, d);
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
        if (c==cmdSettings) new TransferSetupForm(display, this);       
    }
    public void cmdOk() {
        TransferDispatcher.getInstance().eventNotify();
        destroyView();
    }
    
//#ifdef MENU_LISTENER
    protected void keyPressed(int keyCode) { // overriding this method to avoid autorepeat
        //kHold=0;
        if (keyCode==KEY_POUND) {
            cmdInfo();
            return;
        }
        super.keyPressed(keyCode);
    }    
//#endif

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
