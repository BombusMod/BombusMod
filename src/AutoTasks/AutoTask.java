/*
 * AutoTasks.java
 *
 * Created on 20 Март 2008 г., 19:51
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package AutoTasks;

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
public class AutoTask 
        /*extends AlertBox */
        /*implements Runnable*/
{
    
    public final static int TASK_TYPE_DISABLED=0;
    public final static int TASK_TYPE_TIME=1;
    public final static int TASK_TYPE_TIMER=2;
    
    //public final static int TASK_ACTION_NONE=-1;
    public final static int TASK_ACTION_QUIT=0;
    public final static int TASK_ACTION_CONFERENCE_QUIT=1;
    public final static int TASK_ACTION_LOGOFF=2;
    
    public int taskType=TASK_TYPE_DISABLED;
    public int taskAction=TASK_ACTION_QUIT;
    
    public long initTime=0;
    public int waitTime=60;
    public int startHour=0;
    public int startMin=0;

    //private Gauge timer;
    //boolean isRunning;
    //private final static int WAITTIME=60;
    
    // Singleton
    private static AutoTask instance;
    
    public static AutoTask getInstance(){
        if (instance==null) {
            instance=new AutoTask();
        }
        return instance;
    }
/*

     private Command cmdCancel=new Command(SR.MS_CANCEL, Command.BACK, 2);
 
     public AutoTask(String mainbar, String body, Display display) {
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
        //display.setCurrent(next);
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
        //commandAction(cmdOk, alert);
    }
*/
}
