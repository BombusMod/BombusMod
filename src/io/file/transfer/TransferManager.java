/*
 * TransferManager.java
 *
 * Created on 28.10.2006, 17:00
 *
 * Copyright (c) 2005-2007, Eugene Stahov (evgs), http://bombus-im.org
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

import ui.MainBar;
import java.util.Vector;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import locale.SR;
import ui.VirtualElement;
import ui.VirtualList;

/**
 *
 * @author Evg_S
 */
public class TransferManager extends VirtualList implements CommandListener{
    
    private Vector taskList;
    
    Command cmdBack=new Command(SR.MS_BACK, Command.BACK, 99);
    Command cmdClrF=new Command(SR.MS_HIDE_FINISHED, Command.SCREEN, 10);
    
    /** Creates a new instance of TransferManager */
    public TransferManager(Display display) {
        super(display);
        
        addCommand(cmdBack);
        addCommand(cmdClrF);
        setCommandListener(this);
        setMainBarItem(new MainBar(2, null, SR.MS_TRANSFERS));
        
        taskList=TransferDispatcher.getInstance().getTaskList();
    }

    protected int getItemCount() { return taskList.size(); }

    protected VirtualElement getItemRef(int index) { return (VirtualElement) taskList.elementAt(index); }

    public void eventOk() {
        TransferTask t=(TransferTask) getFocusedObject();
        if (t!=null)
            if (t.isAcceptWaiting()) new TransferAcceptFile(display, t);
    }

    public void commandAction(Command c, Displayable d) {
        if (c==cmdClrF) {
            synchronized (taskList) {
                int i=0;
                while (i<taskList.size()) {
                    TransferTask task=(TransferTask) taskList.elementAt(i);
                    if (task.isStopped()) taskList.removeElementAt(i);
                    else i++;
                }
            }
            redraw();
        }
        if (c==cmdBack) {
            TransferDispatcher.getInstance().eventNotify();
            destroyView();
        }
        
    }
}
