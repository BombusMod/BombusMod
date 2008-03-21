/*
 * AutoTasks.java
 *
 * Created on 20 Март 2008 г., 19:51
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package AutoTasks;

import Client.StaticData;
import com.alsutton.jabber.datablocks.Presence;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Gauge;
import locale.SR;
import midlet.BombusMod;
import ui.AlertBox;
import ui.Time;

/**
 *
 * @author ad
 */
public class AutoTask 
        /*extends AlertBox */
        implements Runnable
{
    
    public final static int TASK_TYPE_DISABLED=0;
    public final static int TASK_TYPE_TIME=1;
    public final static int TASK_TYPE_TIMER=2;

    public final static int TASK_ACTION_QUIT=0;
    public final static int TASK_ACTION_CONFERENCE_QUIT=1;
    public final static int TASK_ACTION_LOGOFF=2;
    
    public int taskType=TASK_TYPE_DISABLED;
    public int taskAction=TASK_ACTION_QUIT;
    
    public long initTime=0;
    public int waitTime=3600000;
    public int startHour=0;
    public int startMin=0;
    
    public int sleepTime=5000;

    //private Gauge timer;
    boolean isRunning;

    private Display display;
    private final static int WAITTIME=60;

    public AutoTask(Display display) {
        this.display=display;
        initTime=System.currentTimeMillis();
        new Thread(this).start();
    }

    public void startTask() {
        isRunning=true;
        new Thread(this).start();
    }
    
    public void run() {
        isRunning=true;
        while (isRunning) {
            if (taskType==TASK_TYPE_DISABLED){
                 //System.out.println("autotask disabled");
                 isRunning=false;
            }
            try {
                Thread.sleep(sleepTime);
            } catch (Exception e) { break; }
            
            if (taskType==TASK_TYPE_TIMER) {
                if ((System.currentTimeMillis()-initTime)>waitTime) {
                     System.out.println("autotask by Timer Executed");
                     doAction();
                     isRunning=false;
                     taskType=TASK_TYPE_DISABLED;
                }
            } else if (taskType==TASK_TYPE_TIME) {
                if (Time.getHour()==startHour && Time.getMin()==startMin ) {
                     System.out.println("autotask by Time Executed");
                     doAction();
                     isRunning=false;
                     taskType=TASK_TYPE_DISABLED;
                }
            } else {
                 //System.out.println("autotask disabled");
                 isRunning=false;
                 taskType=TASK_TYPE_DISABLED;
            }
                
        }
    }
    
    public void doAction() {
        switch (taskAction) {
            case TASK_ACTION_QUIT:
                BombusMod.getInstance().notifyDestroyed();
                break;
            case TASK_ACTION_CONFERENCE_QUIT:
                StaticData.getInstance().roster.multicastConferencePresence("Quit by "+((taskType==TASK_TYPE_TIMER)?"timer":"time")+startHour+":"+startMin, Presence.PRESENCE_OFFLINE);
                break;
            case TASK_ACTION_LOGOFF:
                StaticData.getInstance().roster.logoff();
                break;
        }
    }
}
