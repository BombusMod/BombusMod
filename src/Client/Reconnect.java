/*
 * Reconnect.java
 *
 * Created on 14.12.2006, 1:51
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

package Client;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Gauge;
import locale.SR;

/**
 *
 * @author Evg_S
 */
public class Reconnect
    implements Runnable, CommandListener {

    protected Display display;
    protected Displayable next;
    protected Alert alert;
    
    protected Command cmdOk=new Command(SR.MS_OK, Command.OK, 1);
    private Command cmdCancel=new Command(SR.MS_CANCEL, Command.BACK, 2);
    
    private Gauge timer;
    boolean isRunning;
    
    private final static int WAITTIME=15;
    
    /** Creates a new instance of Reconnect */
    public Reconnect(String title, String body, Display display) {
        alert=new Alert(title, body, null, null);
        next=display.getCurrent();
        this.display=display;
        
        //alert.setTimeout(15000); //15 seconds
        alert.addCommand(cmdOk);
        alert.addCommand(cmdCancel);
        alert.setCommandListener(this);
        
        timer=new Gauge(null, false, WAITTIME, 1);
        alert.setIndicator(timer);
        display.setCurrent(alert);
        
        new Thread(this).start();
    }

    public void commandAction(Command command, Displayable displayable) {
        if (command==cmdOk)
            if (isRunning)
                StaticData.getInstance().roster.doReconnect();
        isRunning=false;
        display.setCurrent(next);
    }

    public void run() {
        isRunning=true;
        while (isRunning) {
            try { 
                Thread.sleep(1000);
            } catch (Exception e) { break; }
            int value=timer.getValue()+1;
            timer.setValue(value);
            if (value>=WAITTIME) break;
        }
        commandAction(cmdOk, alert);
    }
}
