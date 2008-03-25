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
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Gauge;
import javax.microedition.lcdui.Graphics;
import locale.SR;
import midlet.BombusMod;
import ui.AlertBox;
import ui.FontCache;
import ui.Time;

/**
 *
 * @author ad
 */
public class AutoTask 
        /*extends AlertBox*/
        extends Canvas
        implements Runnable, CommandListener
{
    
    public final static int TASK_TYPE_DISABLED=0;
    public final static int TASK_TYPE_TIME=1;
    public final static int TASK_TYPE_TIMER=2;

    public final static int TASK_ACTION_QUIT=0;
    public final static int TASK_ACTION_CONFERENCE_QUIT=1;
    public final static int TASK_ACTION_LOGOFF=2;
    public final static int TASK_ACTION_RECONNECT=3;
    
    public int taskType=TASK_TYPE_DISABLED;
    public int taskAction=TASK_ACTION_QUIT;
    
    public long initTime=System.currentTimeMillis();
    public int waitTime=3600000;
    public int startHour=0;
    public int startMin=0;
    
    public int SLEEPTIME=5000;

    boolean isRunning;
    
    boolean vibrate;

    private Display display;
    private Displayable parentView=StaticData.getInstance().roster;
    
    private final static int WAITTIME=60;

    private boolean isShowing;
    
    protected Command cmdOk=new Command(SR.MS_OK, Command.OK, 1);
    private Command cmdCancel=new Command(SR.MS_CANCEL, Command.BACK, 2);

    private int value;
    
    Font f=FontCache.getBalloonFont();

    public AutoTask(Display display) {
	super();
        this.display=display;
    }

    public void startTask() {
        isRunning=true;
        if (parentView==null)
            parentView=StaticData.getInstance().roster;
        new Thread(this).start();
    }
    
    public void run() {
        isRunning=true;
        while (isRunning) {
            if (taskType==TASK_TYPE_DISABLED){
                 isRunning=false;
            }
            try {
                Thread.sleep(SLEEPTIME);
            } catch (Exception e) { break; }
            
            if (taskType==TASK_TYPE_TIMER) {
                if ((System.currentTimeMillis()-initTime)>waitTime) {
                     //System.out.println("autotask by Timer Executed");
                     showAlert(taskType);
                     isRunning=false;
                     taskType=TASK_TYPE_DISABLED;
                }
            } else if (taskType==TASK_TYPE_TIME) {
                if (Time.getHour()==startHour && Time.getMin()==startMin ) {
                     //System.out.println("autotask by Time Executed");
                     showAlert(taskType);
                     isRunning=false;
                     taskType=TASK_TYPE_DISABLED;
                }
            } else {
                 isRunning=false;
                 taskType=TASK_TYPE_DISABLED;
            }  
        }
        while (isShowing) {
            try {
                Thread.sleep(1000);
            } catch (Exception e) { break; }
            value+=1;
            if (value>=WAITTIME) {
                if (vibrate) display.vibrate(1000);
                isShowing=false;
                //System.out.println("execute");
                doAction();
                destroyView();
                break;
            }
            repaint();
            if (vibrate) display.vibrate(200);
        }
    }
    
    public void doAction() {
        String caption=SR.MS_AUTOTASKS+": ";
        switch (taskAction) {
            case TASK_ACTION_QUIT:
                BombusMod.getInstance().notifyDestroyed();
                break;
            case TASK_ACTION_CONFERENCE_QUIT:
                caption+=SR.MS_AUTOTASK_QUIT_CONFERENCES;
                StaticData.getInstance().roster.multicastConferencePresence(caption, Presence.PRESENCE_OFFLINE);
                break;
            case TASK_ACTION_LOGOFF:
                caption+=SR.MS_AUTOTASK_LOGOFF;
                StaticData.getInstance().roster.logoff(caption);
                break;
           case TASK_ACTION_RECONNECT:
                caption+=SR.MS_RECONNECT;
                taskType=TASK_TYPE_TIMER;
                initTime=System.currentTimeMillis();
                startTask();
                StaticData.getInstance().roster.connectionTerminated(new Exception(caption));
                break;
        }
    }
    
    public void showAlert(int type) {
        //System.out.println("start alert");

        this.addCommand(cmdOk);
        this.addCommand(cmdCancel);

        this.setCommandListener(this);
        display.setCurrent(this);
        isShowing=true;
    }
    
    public void commandAction(Command command, Displayable displayable) {
        destroyView();
        if (command==cmdOk) {
            if (isShowing) {
                isShowing=false;
                doAction();
            }
        }
        isShowing=false;
    }

    protected void paint(Graphics g) {
        if (isShowing) {
            String caption=SR.MS_AUTOTASKS+": ";
            
            switch (taskAction) {
                case TASK_ACTION_QUIT:
                    caption=SR.MS_AUTOTASK_QUIT_BOMBUSMOD;
                    break;
                case TASK_ACTION_CONFERENCE_QUIT:
                    caption=SR.MS_AUTOTASK_QUIT_CONFERENCES;
                    break;
                case TASK_ACTION_LOGOFF:
                    caption=SR.MS_AUTOTASK_LOGOFF;
                    break;
                case TASK_ACTION_RECONNECT:
                    caption=SR.MS_RECONNECT;
                    break;
            }
            caption+=" - "+(WAITTIME-value);
            
            int width=getWidth();
            int height=getHeight();

            int border=10;
            int y=height/2;
            int xt=(width/2);
            
            int itemWidth=width-(border*2);
            int itemHeight=5;
            
            int filled=(itemWidth*value)/WAITTIME;

            int oldColor=g.getColor();
            g.setColor(0xffffff);
            
            g.fillRect(0,0, width, height); //fill back
            
            g.fillRect(border, y, itemWidth, itemHeight);
            g.setColor(0x668866);
            g.drawRect(border, y, itemWidth, itemHeight);
            g.fillRect(border, y, filled, itemHeight);
            
            int yt=y-f.getHeight();
            g.setColor(0x668866);
            g.setFont(f);
            g.drawString(caption, xt, yt, Graphics.TOP|Graphics.HCENTER);
            
            g.setColor(oldColor);
        }
    }
    
    public void destroyView()	{
        this.removeCommand(cmdOk);
        this.removeCommand(cmdCancel);
        if (parentView!=null)
            display.setCurrent(parentView);
        //parentView=null;
        repaint();
    }
}
