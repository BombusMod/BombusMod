/*
 * ContactEdit.java
 *
 * Created on 26.05.2008, 10:04
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
//#ifndef WMUC
import Conference.MucContact;
//#endif
import javax.microedition.lcdui.*;
import java.util.*;
import locale.SR;
import ui.controls.TextFieldEx;
import ui.controls.form.BoldString;
import ui.controls.form.CheckBox;
import ui.controls.form.DefForm;
import ui.controls.form.DropChoiceBox;
import ui.controls.form.TextInput;

/**
 *
 * @author Evg_S
 */
public final class ContactEdit
        extends DefForm {
    private Display display;
    public Displayable parentView;
    
    //Form f;
     private TextInput tJid;
     private TextInput tNick;
     private TextInput tGroup;
     //private ChoiceGroup tGrpList;
     private DropChoiceBox tTranspList;
     private CheckBox tAskSubscrCheckBox;
    
    int ngroups;
    
    int grpFIndex;
    
    //Command cmdOk=new Command(SR.MS_ADD, Command.OK, 1);
    //Command cmdSet=new Command(SR.MS_SET, Command.ITEM, 2);
    //Command cmdCancel=new Command(SR.MS_CANCEL,Command.BACK,99);
    
    boolean newContact=true;
    Config cf;

    StaticData sd=StaticData.getInstance();
    //StoreContact sC;
    
    public ContactEdit(Display display, Contact c) {
        super(display, SR.MS_ADD_CONTACT);
        this.display=display;
        parentView=display.getCurrent();
        cf=Config.getInstance();
        
        Vector groups=sd.roster.groups.getRosterGroupNames();

        
        tJid=new TextInput(display, null, null); 

        tNick=new TextInput(display, null, null);
        
        tGroup=new TextInput(display, (c==null)?"":c.getGroup().name, null);

        tTranspList=new DropChoiceBox(display);
        // Transport droplist
        tTranspList.append(sd.account.getServer());
        for (Enumeration e=sd.roster.getHContacts().elements(); e.hasMoreElements(); ){
            Contact ct=(Contact)e.nextElement();
            Jid transpJid=ct.jid;
            if (transpJid.isTransport()) 
                tTranspList.append(transpJid.getBareJid());
        }
        tTranspList.append(SR.MS_OTHER);

        
        tAskSubscrCheckBox=new CheckBox(SR.MS_ASK_SUBSCRIPTION, false);
        
        //tGrpList=new ChoiceGroup(SR.MS_GROUP , ChoiceGroup.POPUP);

        
        try {
            String jid;
//#ifndef WMUC
            if (c instanceof MucContact) {
                jid=Jid.toBareJid( ((MucContact)c).realJid );
            } else {
//#endif
                jid=c.getBareJid();
//#ifndef WMUC
            }
//#endif
            // edit contact
            tJid.setValue(jid);
            tNick.setValue(c.nick);
//#ifndef WMUC
            if (c instanceof MucContact) {
                c=null;
                throw new Exception();
            } 
//#endif
            if (c.getGroupType()!=Groups.TYPE_NOT_IN_LIST  && c.getGroupType()!=Groups.TYPE_SEARCH_RESULT) {
                // edit contact
                getMainBarItem().setElementAt(jid, 0);
                newContact=false;
            } else c=null; // adding not-in-list
        } catch (Exception e) {
            c=null;
        } // if MucContact does not contains realJid
        
        if (c==null){
            itemsList.addElement(new BoldString(SR.MS_USER_JID));
            itemsList.addElement(tJid);
            
            itemsList.addElement(new BoldString(SR.MS_TRANSPORT));
            itemsList.addElement(tTranspList);
        }
        itemsList.addElement(new BoldString(SR.MS_NAME));
        itemsList.addElement(tNick);
        
        itemsList.addElement(new BoldString(SR.MS_GROUP));
        itemsList.addElement(tGroup);

        if (newContact) {
            itemsList.addElement(new BoldString(SR.MS_SUBSCRIPTION));
            itemsList.addElement(tAskSubscrCheckBox);
        }
        
        moveCursorTo(getNextSelectableRef(-1));
        attachDisplay(display);
    }

    public void cmdOk() {
        String jid=tJid.getValue();
        if (jid!=null) {
            boolean ask=tAskSubscrCheckBox.getValue();
            sd.roster.storeContact(jid, tNick.getValue(), tGroup.getValue(), ask);
            destroyView();
        }
    }
 
    public void destroyView(){
        if (display!=null)   display.setCurrent(parentView/*roster*/);
    }

}
