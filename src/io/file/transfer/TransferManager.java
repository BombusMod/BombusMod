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
import ui.MainBar;
import java.util.Vector;
import Client.Config;
//#ifndef MENU_LISTENER
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Command;
//#else
//# import Menu.MenuListener;
//# import Menu.Command;
//# import Menu.MyMenu;
//#endif
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import locale.SR;
import ui.Time;
import ui.VirtualElement;
import ui.VirtualList;

/**
 *
 * @author Evg_S
 */
public class TransferManager
    extends VirtualList
    implements
//#ifndef MENU_LISTENER
        CommandListener
//#else
//#         MenuListener
//#endif
    {
    public static String plugin = new String("PLUGIN_FILE_TRANSFER");
    
    private Vector taskList=new Vector();;
    
    Command cmdBack=new Command(SR.MS_BACK, Command.BACK, 99);
    Command cmdDel=new Command(SR.MS_DECLINE, Command.SCREEN, 10);
    Command cmdClrF=new Command(SR.MS_HIDE_FINISHED, Command.SCREEN, 11);
    Command cmdInfo=new Command(SR.MS_INFO, Command.SCREEN, 12);
    
    /** Creates a new instance of TransferManager */
    public TransferManager(Display display) {
        super(display);

        commandState();
        setCommandListener(this);
        setMainBarItem(new MainBar(2, null, SR.MS_TRANSFERS));
        
        taskList=TransferDispatcher.getInstance().getTaskList();
    }
    
    public void showNotify(){
        super.showNotify();
//#ifndef MENU_LISTENER
        removeCommand(cmdDel);
        removeCommand(cmdClrF);
        removeCommand(cmdInfo);
        commandState();
//#endif
    }
    
    public void commandState(){
//#ifdef MENU_LISTENER
//#         menuCommands.removeAllElements();
//#endif
        addCommand(cmdBack);
        if (getItemCount()>0) {
            addCommand(cmdDel);
            addCommand(cmdClrF);
            addCommand(cmdInfo);
        }
    }

    protected int getItemCount() { return taskList.size(); }

    protected VirtualElement getItemRef(int index) { return (VirtualElement) taskList.elementAt(index); }

    public void eventOk() {
        TransferTask t=(TransferTask) getFocusedObject();
        if (t!=null)
            if (t.isAcceptWaiting()) new TransferAcceptFile(display, this, t);
    }
    
    protected void keyClear() {
        if (getItemCount()>0) {
            synchronized (taskList) {
                TransferTask task=(TransferTask) taskList.elementAt(cursor);
                task.cancel();
                taskList.removeElementAt(cursor);
            }
        }
    }

    public void commandAction(Command c, Displayable d) {
        if (c==cmdClrF) {
            synchronized (taskList) {
                int i=0;
                while (i<taskList.size()) {
                    TransferTask task=(TransferTask) taskList.elementAt(i);
                    if (task.isStopped()) 
                        taskList.removeElementAt(i);
                    else 
                        i++;
                }
            }
            if (getItemCount()<1)
                StaticData.getInstance().roster.setEventIcon(null);
            redraw();
        }
        if (c==cmdDel) keyClear();
        if (c==cmdBack) cmdBack();
        if (c==cmdInfo) cmdInfo();
        
    }
    private void cmdBack() {
        TransferDispatcher.getInstance().eventNotify();
        destroyView();
    }
    
//#ifdef MENU_LISTENER
//#     protected void keyPressed(int keyCode) { // overriding this method to avoid autorepeat
//#         if (keyCode==KEY_POUND) {
//#             cmdInfo();
//#             return;
//#         }
//#         super.keyPressed(keyCode);
//#     }
//# 
//#     public void showMenu() {
//#         commandState();
//#         new MyMenu(display, parentView, this, SR.MS_DISCO, null, menuCommands);
//#     }
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

            VirtualList.setWobble(1, null, info.toString());
        }
    }
}
