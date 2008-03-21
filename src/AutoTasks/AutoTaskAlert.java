/*
 * AutoTaskAlert.java
 *
 * Created on 20 Март 2008 г., 19:52
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package AutoTasks;

import Client.StaticData;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Gauge;
import locale.SR;
import ui.AlertBox;

/**
 *
 * @author ad
 */
public class AutoTaskAlert extends AlertBox implements Runnable{

    private Gauge timer;
    boolean isRunning;
    private final static int WAITTIME=15;
    

    private Command cmdCancel=new Command(SR.MS_CANCEL, Command.BACK, 2);
    public AutoTaskAlert(String mainbar, String body, Display display) {
        super(mainbar, body, null, display, null);
        alert.setTimeout(Alert.FOREVER);
        
        timer=new Gauge(null, false, WAITTIME, 1);

        alert.setIndicator(timer);
        alert.addCommand(cmdCancel);
        
        new Thread(this).start();
    }

    public void commandAction(Command command, Displayable displayable) {
        if (command==cmdOk) {
            if (isRunning) {
                isRunning=false;
                //do action;
            }
        }
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