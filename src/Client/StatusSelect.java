/*
 * SelectStatus.java
 *
 * Created on 27.02.2005, 16:43
 * Copyright (c) 2005-2008, Eugene Stahov (evgs), http://bombus-im.org
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
import ui.controls.TextFieldCombo;
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

    private Vector statusList;
    private int defp;
    private Contact to;

    private Config cf=Config.getInstance();
    private StaticData sd = StaticData.getInstance();
    
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
//#         sd.roster.autoAway=false;
//#         sd.roster.autoXa=false;
//#         sd.roster.messageActivity();
//#endif
        try {
            if (sd.roster.isLoggedIn()) {
                sd.roster.sendDirectPresence(status, to, null);
            } else {
                sd.roster.sendPresence(status, null);
            }
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
        private TextField tfAutoRespondMessage;
        
        private ExtendedStatus status;
        
        private Command cmdOk=new Command(SR.MS_OK,Command.OK,1);
        private Command cmdCancel=new Command(SR.MS_CANCEL,Command.BACK,99);

        private ChoiceGroup autoRespond;
        
        public StatusForm(Display display, ExtendedStatus status){
            this.display=display;
            parentView=display.getCurrent();
            this.status=status;
            
            f=new Form(status.getScreenName());
            
            tfMessage=new TextFieldCombo(SR.MS_MESSAGE, status.getMessage(), 100, 0, "ex_status_list", display);
            f.append(tfMessage);
            
            tfPriority=new NumberField(SR.MS_PRIORITY, status.getPriority(), -128, 128);
            f.append(tfPriority);
            if (status.getImageIndex()<5) {
                tfAutoRespondMessage=new TextFieldEx(SR.MS_AUTORESPOND, status.getAutoRespondMessage(), 100, 0);
                f.append(tfAutoRespondMessage);

                autoRespond=new ChoiceGroup(null, ChoiceGroup.MULTIPLE);
                autoRespond.append(SR.MS_AUTORESPOND, null);
                autoRespond.setSelectedIndex(0, status.getAutoRespond());
                f.append(autoRespond);
            }
            f.addCommand(cmdOk);
            f.addCommand(cmdCancel);
            
            f.setCommandListener(this);
            display.setCurrent(f);
        }
        
        public void commandAction(Command c, Displayable d){
            if (c==cmdOk) {
                if (status.getImageIndex()<5) {
                    boolean flags[]=new boolean[3];

                    autoRespond.getSelectedFlags(flags);
                    status.setAutoRespondMessage(tfAutoRespondMessage.getString());
                    status.setAutoRespond(flags[0]);
                }
                status.setMessage(tfMessage.getString());                    
               
		int priority=tfPriority.getValue();
                status.setPriority(priority);
                

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
