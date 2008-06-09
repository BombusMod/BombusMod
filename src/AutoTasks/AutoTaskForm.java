/*
 * AutoTaskForm.java
 *
 * Created on 20.03.2008, 19:52
 *
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
import java.util.Vector;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import locale.SR;
import ui.controls.form.DropChoiceBox;
import ui.controls.form.DefForm;
import ui.controls.form.NumberInput;
import ui.controls.form.SimpleString;

/**
 *
 * @author ad
 */
public class AutoTaskForm
    extends DefForm {
    
    private Display display;
    private Displayable parentView;
    
    int hour =0;
    int min  =0;
    int wait =1;

    private DropChoiceBox taskType;
    private DropChoiceBox actionType;

    private NumberInput autoTaskDelay;

    private NumberInput autoTaskMin;
    private NumberInput autoTaskHour;
    
//#if AUTOTASK
//#     private AutoTask at=StaticData.getInstance().autoTask;
//#endif

    private int actionIndex;
    private int typeIndex;

    
    /** Creates a new instance of AutoTaskForm */
    public AutoTaskForm(Display display) {
        super(display, SR.MS_AUTOTASKS);
        this.display=display;
        parentView=display.getCurrent();
//#ifdef AUTOTASK
//#         typeIndex=at.taskType;
//#         actionIndex=at.taskAction;
//#         if (autoTaskHour!=null) hour = at.startHour;
//#         if (autoTaskMin!=null) min = at.startMin;
//#         if (autoTaskDelay!=null) wait = at.waitTime/60000;    
//# 
//#         update();
//#         
//# 	addCommand(cmdOk);
//# 	addCommand(cmdCancel);
//# 	setCommandListener(this);
//#         
//#         moveCursorTo(getNextSelectableRef(-1));
//#         attachDisplay(display);
//#endif
    }
//#ifdef AUTOTASK
//#     public void cmdOk() {
//#         at.taskType=taskType.getSelectedIndex();
//#         at.taskAction=actionType.getSelectedIndex();
//#         if (at.taskType==1) {
//#             at.startHour=Integer.parseInt(autoTaskHour.getValue());
//#             at.startMin=Integer.parseInt(autoTaskMin.getValue());
//#         } else if(at.taskType==2) {
//#             at.waitTime=Integer.parseInt(autoTaskDelay.getValue())*1000*60;
//#             at.initTime=System.currentTimeMillis();
//#         }
//#         if (at.taskType!=0)
//#             at.startTask();
//#         destroyView();
//#     }
//# 
//#     public void eventOk(){
//#         super.eventOk();
//#         if (cursor==1) {
//#             typeIndex=taskType.getSelectedIndex();
//#             actionIndex=actionType.getSelectedIndex();
//#             if (autoTaskHour!=null) hour = Integer.parseInt(autoTaskHour.getValue());
//#             if (autoTaskMin!=null) min = Integer.parseInt(autoTaskMin.getValue());
//#             if (autoTaskDelay!=null) wait = Integer.parseInt(autoTaskDelay.getValue());            
//#             update();
//#         }
//#     }
//#     
//#     public void destroyView(){
//#         if (display!=null)  
//#             display.setCurrent(StaticData.getInstance().roster);
//#     }
//# 
//#     public void update(){
//#         itemsList=new Vector();
//#         
//#         itemsList.addElement(new SimpleString(SR.MS_AUTOTASK_TYPE, true));
//#         taskType=new DropChoiceBox(display);
//#         taskType.append(SR.MS_DISABLED);
//#         taskType.append(SR.MS_BY_TIME_);
//#         taskType.append(SR.MS_BY_TIMER_);
//#         taskType.setSelectedIndex(typeIndex);
//#         itemsList.addElement(taskType);
//#         
//#         itemsList.addElement(new SimpleString(SR.MS_AUTOTASK_ACTION_TYPE, true));
//#         actionType=new DropChoiceBox(display);
//#         actionType.append(SR.MS_AUTOTASK_QUIT_BOMBUSMOD);
//#         actionType.append(SR.MS_AUTOTASK_QUIT_CONFERENCES);
//#         actionType.append(SR.MS_AUTOTASK_LOGOFF);
//#         actionType.append(SR.MS_BREAK_CONECTION);
//#         actionType.setSelectedIndex(actionIndex);
//#         itemsList.addElement(actionType);
//#         
//#         if (typeIndex==1) {
//#             itemsList.addElement(new SimpleString(SR.MS_AUTOTASK_TIME, true));
//# 
//#             autoTaskHour=new NumberInput(display, SR.MS_AUTOTASK_HOUR, Integer.toString(hour), 0, 23);
//#             itemsList.addElement(autoTaskHour);
//# 
//#             autoTaskMin=new NumberInput(display, SR.MS_AUTOTASK_MIN, Integer.toString(min), 0, 59);
//#             itemsList.addElement(autoTaskMin);
//#          } else if (typeIndex==2) {
//#             autoTaskDelay=new NumberInput(display, SR.MS_AUTOTASK_DELAY, Integer.toString(wait), 1, 600);
//#             itemsList.addElement(autoTaskDelay);
//#         }
//#     }
//#endif
}
