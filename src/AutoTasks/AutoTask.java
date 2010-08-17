/*
 * AutoTasks.java
 *
 * Created on 20.03.2008, 19:51
 * Copyright (c) 2006-2008, Daniel Apatin (ad), http://apatin.net.ru
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

package AutoTasks;

import Client.StaticData;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import locale.SR;
import Fonts.FontCache;
import com.alsutton.jabber.datablocks.Presence;
import midlet.BombusMod;
import ui.Time;

/**
 *
 * @author ad
 */
public class AutoTask 
        extends Canvas
        implements Runnable, CommandListener
{
    
    public final int TASK_TYPE_DISABLED=0;
    public final int TASK_TYPE_TIME=1;
    public final int TASK_TYPE_TIMER=2;

    public final int TASK_ACTION_QUIT=0;
    public final int TASK_ACTION_CONFERENCE_QUIT=1;
    public final int TASK_ACTION_LOGOFF=2;
    public final int TASK_ACTION_RECONNECT=3;
    
    public int taskType=TASK_TYPE_DISABLED;
    public int taskAction=TASK_ACTION_QUIT;
    
    public long initTime=System.currentTimeMillis();
    public int waitTime=3600000;
    public int startHour=0;
    public int startMin=0;
    
    public int SLEEPTIME=5000;

    boolean isRunning;
    
    boolean vibrate;
    
    StaticData sd = StaticData.getInstance();

    private Display display;
    private Displayable parentView=sd.roster;
    
    private int WAITTIME=60;

    private boolean isShowing;
    
    protected Command cmdOk=new Command(SR.MS_OK, Command.OK, 1);
    private Command cmdCancel=new Command(SR.MS_CANCEL, Command.BACK, 2);

    private int value;
    
    Font f=FontCache.getFont(false, FontCache.msg);

    private Displayable next;

    public AutoTask(Display display) {
	super();
        this.display=display;
    }

    public void startTask() {
        isRunning=true;
        if (parentView==null)
            parentView=sd.roster;
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
//#ifdef AUTOTASK
        String caption=SR.MS_AUTOTASKS+": ";
        switch (taskAction) {
            case TASK_ACTION_QUIT:
                BombusMod.getInstance().notifyDestroyed();
                break;
//#ifndef WMUC
            case TASK_ACTION_CONFERENCE_QUIT:
                caption+=SR.MS_AUTOTASK_QUIT_CONFERENCES;
                sd.roster.multicastConferencePresence(Presence.PRESENCE_OFFLINE, caption, 0);
                break;
//#endif
            case TASK_ACTION_LOGOFF:
                caption+=SR.MS_AUTOTASK_LOGOFF;
                sd.roster.logoff(caption);
                break;
           case TASK_ACTION_RECONNECT:
                caption+=SR.MS_RECONNECT;
                taskType=TASK_TYPE_TIMER;
                initTime=System.currentTimeMillis();
                startTask();
                sd.roster.connectionTerminated(new Exception(caption));
                break;
        }
//#endif
    }
    
    public void showAlert(int type) {
        //System.out.println("start alert");
        next=display.getCurrent();
        
        this.addCommand(cmdOk);
        this.addCommand(cmdCancel);

        this.setCommandListener(this);

        midlet.BombusMod.getInstance().setDisplayable(this);
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
//#if AUTOTASK
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
//#endif
        }
    }
    
    public void destroyView()	{
        value=0;
        this.removeCommand(cmdOk);
        this.removeCommand(cmdCancel);
        
        if (display==null) {
            midlet.BombusMod.getInstance().setDisplayable(parentView);
        } else {
            midlet.BombusMod.getInstance().setDisplayable(next);
        }
    }
}
