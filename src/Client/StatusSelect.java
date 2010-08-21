/*
 * StatusSelect.java
 *
 * Created on 20.05.2008, 15:47
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

package Client;
import java.util.*;
import javax.microedition.lcdui.TextField;
import locale.SR;
import ui.*;
import ui.MainBar;
import ui.controls.form.CheckBox;
import ui.controls.form.DefForm;
import ui.controls.form.NumberInput;
import ui.controls.form.SimpleString;
import ui.controls.form.SpacerItem;
import ui.controls.form.TextInput;
import Menu.MenuCommand;

/**
 *
 * @author ad
 */
public class StatusSelect
        extends DefForm
        implements Runnable{
    
    private MenuCommand cmdEdit=new MenuCommand(SR.MS_EDIT, MenuCommand.SCREEN,2);
    private MenuCommand cmdDef=new MenuCommand(SR.MS_SETDEFAULT, MenuCommand.OK,3);
    
    private Vector statusList;
    private int defp;
    private Contact to;

    private Config cf;
    private StaticData sd = StaticData.getInstance();
    
    public StatusSelect(Contact to) {
        super(SR.MS_STATUS);
        
        cf=Config.getInstance();
        statusList=StatusList.getInstance().statusList;
        this.to=to;
        if (to!=null) {
             setMainBarItem(new MainBar(to));
        }

        setMenuListener(this);
        
        defp=cf.loginstatus;
        moveCursorTo(defp);
        enableListWrapping(true);
    }
    
    public void commandState() {
        menuCommands.removeAllElements();
        addMenuCommand(cmdOk);
        addMenuCommand(cmdEdit);
        addMenuCommand(cmdDef);
        addMenuCommand(cmdCancel);
    }
    
    public VirtualElement getItemRef(int Index) {
        return (VirtualElement)statusList.elementAt(Index);
    }

    private ExtendedStatus getSel() { return (ExtendedStatus)getFocusedObject(); }
    
    public void menuAction(MenuCommand c, VirtualList d) {
        if (c==cmdOk)
            eventOk();
        if (c==cmdEdit) {
            new StatusForm(getSel());
        }
        
        if (c==cmdDef) {
            cf.loginstatus=cursor;
	    cf.saveToStorage();
            redraw();
        }

        if (c==cmdCancel)
            destroyView();
    }
    
    public void eventOk() {
        destroyView();
        new Thread(this).start();
    }
    
    public void run() {
        int status=getSel().getImageIndex();
//#ifdef AUTOSTATUS
//#         Roster.autoAway=false;
//#         Roster.autoXa=false;
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
    
    public int getItemCount() {   return statusList.size(); }
    
    private void save() {
        StatusList.getInstance().saveStatusToStorage();
    }
    public void touchLeftPressed() {
        showMenu();
    }
    public String touchLeftCommand() {
        return SR.MS_MENU;
    }
    
    
    class StatusForm 
        extends DefForm {
        
        private NumberInput tfPriority;
        private TextInput tfMessage;
        private TextInput tfAutoRespondMessage;
        
        private ExtendedStatus status;

        private CheckBox autoRespond;
        
        public StatusForm(ExtendedStatus status){
            super(SR.MS_STATUS+": "+status.getScreenName());
            this.status=status;
            
            tfMessage = new TextInput(SR.MS_MESSAGE, status.getMessage(), "ex_status_list", TextField.ANY); //, 100, TextField.ANY "ex_status_list"
            itemsList.addElement(tfMessage);

            tfPriority = new NumberInput( SR.MS_PRIORITY, Integer.toString(status.getPriority()), -128, 128); //, 100, TextField.ANY "ex_status_list"
            itemsList.addElement(tfPriority);

            if (status.getImageIndex()<5) {
                itemsList.addElement(new SpacerItem(10));
               
                tfAutoRespondMessage=new TextInput(SR.MS_AUTORESPOND, status.getAutoRespondMessage(), "autorespond", TextField.ANY);//, 100, 0
                itemsList.addElement(tfAutoRespondMessage);
                
                autoRespond = new CheckBox(SR.MS_ENABLE_AUTORESPOND, status.getAutoRespond()); itemsList.addElement(autoRespond);
            }
            
            itemsList.addElement(new SpacerItem(10));
            
            itemsList.addElement(new SimpleString("%t - time", false));
            itemsList.addElement(new SimpleString("%dt - date time", false));
            
            moveCursorTo(getNextSelectableRef(-1));
        }
        
        public void cmdOk() {
            if (status.getImageIndex()<5) {
                status.setAutoRespondMessage(tfAutoRespondMessage.getValue());
                status.setAutoRespond(autoRespond.getValue());
            }
            status.setMessage(tfMessage.getValue());                    
            status.setPriority(Integer.parseInt(tfPriority.getValue()));

            save();

            destroyView();
        }
        
        public void touchLeftPressed(){
            cmdOk();
        }
    }
}
