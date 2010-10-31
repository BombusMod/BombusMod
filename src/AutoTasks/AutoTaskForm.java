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
//#if AUTOTASK    
//# import Client.StaticData;
//# import ui.controls.form.DropChoiceBox;
//# import ui.controls.form.NumberInput;
//# import ui.controls.form.SimpleString;
//#endif
import ui.controls.form.DefForm;
import locale.SR;

/**
 *
 * @author ad
 */
public class AutoTaskForm
    extends DefForm {
    
    //private Display display;
    
//#if AUTOTASK    
//#     int hour =0;
//#     int min  =0;
//#     int wait =1;
//# 
//#     private DropChoiceBox taskType;
//#     private DropChoiceBox actionType;
//# 
//#     private SimpleString autoTaskTimeDesc;
//# 
//#     private NumberInput autoTaskDelay;
//# 
//#     private NumberInput autoTaskMin;
//#     private NumberInput autoTaskHour;
//#     
//#     private AutoTask at=StaticData.getInstance().autoTask;
//# 
//#     private int actionIndex;
//#     private int typeIndex;
//#endif
    
    /** Creates a new instance of AutoTaskForm
     */
    public AutoTaskForm() {
//#if !AUTOTASK
        super("");
//#else
//#         super(SR.MS_AUTOTASKS);
//#         
//#         typeIndex=at.taskType;
//#         actionIndex=at.taskAction;
//#         
//#         hour = at.startHour;
//#         min = at.startMin;
//#         wait = at.waitTime/60000;
//#         
//#         taskType=new DropChoiceBox(SR.MS_AUTOTASK_TYPE);
//#         taskType.add(SR.MS_DISABLED);
//#         taskType.add(SR.MS_BY_TIME_);
//#         taskType.add(SR.MS_BY_TIMER_);
//#         taskType.setSelectedIndex(typeIndex);
//# 
//#         actionType=new DropChoiceBox(SR.MS_AUTOTASK_ACTION_TYPE);
//#         actionType.add(SR.MS_AUTOTASK_QUIT_BOMBUSMOD);
//#         actionType.add(SR.MS_AUTOTASK_QUIT_CONFERENCES);
//#         actionType.add(SR.MS_AUTOTASK_LOGOFF);
//#         actionType.add(SR.MS_BREAK_CONECTION);
//#         actionType.setSelectedIndex(actionIndex);
//#         
//#         autoTaskTimeDesc=new SimpleString(SR.MS_AUTOTASK_TIME, true);
//# 
//#         autoTaskHour=new NumberInput(sd.canvas,  SR.MS_AUTOTASK_HOUR, Integer.toString(hour), 0, 23);
//#         autoTaskMin=new NumberInput(sd.canvas,  SR.MS_AUTOTASK_MIN, Integer.toString(min), 0, 59);
//#         autoTaskDelay=new NumberInput(sd.canvas,  SR.MS_AUTOTASK_DELAY, Integer.toString(wait), 1, 600);
//#         
//#         itemsList.addElement(taskType);
//#         itemsList.addElement(actionType);
//#         
//#         update();
//#     }
//# 
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
//#     protected void beginPaint(){
//#         if(typeIndex!=taskType.getSelectedIndex()) {
//#             typeIndex=taskType.getSelectedIndex();       
//#             update();
//#         }
//#     }
//#     
//#     public final void update(){
//#         itemsList.removeElement(autoTaskTimeDesc);
//#         itemsList.removeElement(autoTaskHour);
//#         itemsList.removeElement(autoTaskMin);
//#         itemsList.removeElement(autoTaskDelay);
//#         
//#         if (typeIndex==1) {
//#             itemsList.addElement(autoTaskTimeDesc);
//#             itemsList.addElement(autoTaskHour);
//#             itemsList.addElement(autoTaskMin);
//#         } else if (typeIndex==2) {
//#             itemsList.addElement(autoTaskDelay);
//#         }
//#endif
    }
}
