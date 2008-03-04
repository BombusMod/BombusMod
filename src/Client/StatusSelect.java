/*
 * SelectStatus.java
 *
 * Created on 27.02.2005, 16:43
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
 *
 */

package Client;
import java.util.*;
import javax.microedition.lcdui.*;
import locale.SR;
import ui.*;
import ui.MainBar;
import ui.controls.NumberField;
import ui.controls.TextFieldEx;

//import ui.controls.TextFieldCombo;

/**
 *
 * @author Eugene Stahov
 */
public class StatusSelect extends VirtualList implements CommandListener, Runnable{
    
    private Command cmdOk=new Command(SR.MS_SELECT,Command.OK,1);
    private Command cmdEdit=new Command(SR.MS_EDIT,Command.SCREEN,2);
    private Command cmdDef=new Command(SR.MS_SETDEFAULT,Command.OK,3);
    private Command cmdCancel=new Command(SR.MS_CANCEL,Command.BACK,99);
    /** Creates a new instance of SelectStatus */
    private Vector statusList;

    private int defp;

    private Contact to;
    
    private Config cf=Config.getInstance();
     
    public StatusSelect(Display d, Contact to) {
        super();
        statusList=StatusList.getInstance().statusList;
        this.to=to;
        if (to==null) { 
            setMainBarItem(new MainBar(SR.MS_STATUS));
        } else {
            setMainBarItem(new MainBar(to));
        }
        addCommand(cmdOk);
        addCommand(cmdEdit);
        addCommand(cmdDef);
        addCommand(cmdCancel);
        setCommandListener(this);
        
        defp=cf.loginstatus;
        moveCursorTo(defp);
        attachDisplay(d);
    }
    public VirtualElement getItemRef(int Index){
        return (VirtualElement)statusList.elementAt(Index);
    }
    
    public void setParentView(Displayable parentView){
        this.parentView=parentView;
    }
    
    private ExtendedStatus getSel(){ return (ExtendedStatus)getFocusedObject();}
    
    public void commandAction(Command c, Displayable d){
        if (c==cmdOk) eventOk(); 
        if (c==cmdEdit) {
            new StatusForm( display, getSel() );
        }
        
        if (c==cmdDef) {
            cf.loginstatus=cursor;
	    cf.saveToStorage();
            redraw();
        }

        if (c==cmdCancel) destroyView();
    }
    
    public void eventOk(){
        destroyView();
        new Thread(this).start();
    }
    
    public void run(){
        int status=getSel().getImageIndex();
//#ifdef AUTOSTATUS
//#         Roster.autoAway=false;
//#         Roster.autoXa=false;
//#         StaticData.getInstance().roster.messageActivity();
//#endif
        try {
            StaticData.getInstance().roster.sendDirectPresence(status, to, null);
        } catch (Exception e) { }
    }
    
    public int getItemCount(){   return statusList.size(); }
    
    private void save(){
        StatusList.getInstance().saveStatusToStorage();
    }

    class StatusForm implements CommandListener{
        private Display display;
        public Displayable parentView;
        
        private Form f;
        private NumberField tfPriority;
        private TextField tfMessage;
        
        private ExtendedStatus status;
        
        private Command cmdOk=new Command(SR.MS_OK,Command.OK,1);
        private Command cmdCancel=new Command(SR.MS_CANCEL,Command.BACK,99);

        private ChoiceGroup selStatus;
        
        ExtendedStatusList statuslist=new ExtendedStatusList();

        private ChoiceGroup addNewStatusText;
        
        public StatusForm(Display display, ExtendedStatus status){
            this.display=display;
            parentView=display.getCurrent();
            this.status=status;
            
            f=new Form(status.getScreenName());
            
            tfMessage=new TextFieldEx(SR.MS_MESSAGE, status.getMessage(), 100, 0);
            f.append(tfMessage);
            
            tfPriority=new NumberField(SR.MS_PRIORITY, status.getPriority(), -128, 128);
            f.append(tfPriority);
            
            selStatus=new ChoiceGroup(SR.MS_CHOOSE_STATUS, ChoiceGroup.POPUP);
            selStatus.append(status.getMessage(), null );
            for (int i=0;i<statuslist.size();i++) {
                selStatus.append((String)statuslist.msg(i), null );
            }
            selStatus.setSelectedIndex(0, true);
            f.append(selStatus);
            
            addNewStatusText=new ChoiceGroup(null, ChoiceGroup.MULTIPLE);
            addNewStatusText.append(SR.MS_ALL_STATUSES, null);
            addNewStatusText.append(SR.MS_ADD_STATUS, null);
            addNewStatusText.append(SR.MS_REMOVE_STATUS, null);
            f.append(addNewStatusText);
            
            f.addCommand(cmdOk);
            f.addCommand(cmdCancel);
            
            f.setCommandListener(this);
            display.setCurrent(f);
        }
        
        public void commandAction(Command c, Displayable d){
            if (c==cmdOk) {
                boolean flags[]=new boolean[3];
                
                addNewStatusText.getSelectedFlags(flags);
                
                if (selStatus.getSelectedIndex()>0) {
                    if (flags[2]) {
                        status.setMessage(tfMessage.getString());
                    } else {
                        status.setMessage(selStatus.getString(selStatus.getSelectedIndex()));   
                    }
                } else {
                    status.setMessage(tfMessage.getString());                    
                }
               
		int priority=tfPriority.getValue();
                status.setPriority(priority);
                
                if (flags[0]) {
                    for (Enumeration e=StatusList.getInstance().statusList.elements(); e.hasMoreElements();) {
                        ((ExtendedStatus)e.nextElement()).setPriority(priority);
                    }
                }
                if (flags[1]) {
                    statuslist.store(tfMessage.getString());
                }
                if (flags[2]) {
                    statuslist.delete(selStatus.getSelectedIndex()-1);
                }
                save();
            }
            
            destroyView();
        }
        
        private void destroyView(){
            if (display!=null)   
                display.setCurrent(parentView);
        }
    }
}
