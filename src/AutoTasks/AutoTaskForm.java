/*
 * AutoTaskForm.java
 *
 * Created on 20 Март 2008 г., 19:52
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package AutoTasks;

import Client.StaticData;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemCommandListener;
import locale.SR;
import ui.Time;
import ui.controls.NumberField;

/**
 *
 * @author ad
 */
public class AutoTaskForm implements
    CommandListener, ItemCommandListener
{
    private Display display;
    private Displayable parentView;

    Form f;

    private ChoiceGroup taskType;
    private ChoiceGroup actionType;

    private NumberField autoTaskDelay;

    private NumberField autoTaskMin;
    private NumberField autoTaskHour;
//#if AUTOTASK
//#     private AutoTask at=StaticData.getInstance().autoTask;
//#endif
    private Command cmdOk=new Command(SR.MS_OK, Command.OK, 1);
    private Command cmdCancel=new Command(SR.MS_CANCEL, Command.BACK, 2);

    
    /** Creates a new instance of AutoTaskForm */
    public AutoTaskForm(Display display) {
        this.display=display;
        parentView=display.getCurrent();

        f=new Form(SR.MS_AUTOTASKS);
//#if AUTOTASK
//#         taskType=new ChoiceGroup(SR.MS_AUTOTASK_TYPE, Choice.POPUP);
//#         taskType.append(SR.MS_DISABLED, null);
//#         taskType.append(SR.MS_BY_TIME_, null);
//#         taskType.append(SR.MS_BY_TIMER_, null);
//#         taskType.setSelectedIndex(at.taskType, true);
//#         f.append(taskType);
//#         
//#         actionType=new ChoiceGroup(SR.MS_AUTOTASK_ACTION_TYPE, Choice.POPUP);
//#         actionType.append(SR.MS_AUTOTASK_QUIT_BOMBUSMOD, null);
//#         actionType.append(SR.MS_AUTOTASK_QUIT_CONFERENCES, null);
//#         actionType.append(SR.MS_AUTOTASK_LOGOFF, null);
//#         actionType.append(SR.MS_BREAK_CONECTION, null);
//#         actionType.setSelectedIndex(at.taskAction, true);
//#         f.append(actionType);
//#         
//#         f.append("\n");
//#         autoTaskDelay=new NumberField(SR.MS_AUTOTASK_DELAY, at.waitTime/60000, 1, 600);
//#         f.append(autoTaskDelay);
//#         
//#         f.append("\n"+SR.MS_AUTOTASK_TIME);
//#         autoTaskHour=new NumberField(SR.MS_AUTOTASK_HOUR, at.startHour, 0, 23);
//#         f.append(autoTaskHour);
//#         autoTaskMin=new NumberField(SR.MS_AUTOTASK_MIN, at.startMin, 0, 59);
//#         f.append(autoTaskMin);
//#endif
        f.addCommand(cmdOk);
        f.addCommand(cmdCancel);
        f.setCommandListener(this);    
        display.setCurrent(f);
    }

    public void commandAction(Command command, Displayable displayable) {
        if (command==cmdOk) {
//#if AUTOTASK
//#             at.taskType=taskType.getSelectedIndex();
//#             at.taskAction=actionType.getSelectedIndex();
//#             //at.initTime=Time.utcTimeMillis();
//#             at.waitTime=autoTaskDelay.getValue()*1000*60;
//#             at.startHour=autoTaskHour.getValue();
//#             at.startMin=autoTaskMin.getValue();
//#             at.initTime=System.currentTimeMillis();
//#             at.startTask();
//#             //at.sleepTime=autoTaskDelay.getValue()*1000;
//#endif
            destroyView();
        } else if (command==cmdCancel) {
            destroyView();
        }
    }

    public void commandAction(Command command, Item item) {
        
    }
    
    public void destroyView(){
        if (display!=null)  
            display.setCurrent(StaticData.getInstance().roster);
    }
    
}
