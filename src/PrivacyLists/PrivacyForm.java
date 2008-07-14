/*
 * PrivacyForm.java
 *
 * Created on 26.05.2008, 15:29
 *
 * Copyright (c) 2006-2008, Daniel Apatin (ad), http://apatin.net.ru
 *
 * Copyright (c) 2006-2008, Daniel Apatin (ad), http://apatin.net.ru
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

package PrivacyLists;
import Client.Contact;
import Client.Group;
import Client.StaticData;
import java.util.Vector;
import javax.microedition.lcdui.*;
import locale.SR;
import ui.controls.form.SimpleString;
import ui.controls.form.CheckBox;
import ui.controls.form.DefForm;
import ui.controls.form.DropChoiceBox;
import ui.controls.form.TextInput;

/**
 *
 * @author EvgS
 */
public class PrivacyForm
        extends DefForm {
    
    private Display display;
    private Displayable parentView;
    
    private PrivacyList targetList;
    private PrivacyItem item;
    
    DropChoiceBox choiceAction;
    DropChoiceBox choiceType;
    DropChoiceBox choiceSubscr;
    
    CheckBox messageStz;
    CheckBox presenceInStz;
    CheckBox presenceOutStz;
    CheckBox iqStz;

    TextInput textValue;
    
    int selectedAction=-1;
    String tValue="";

    /** Creates a new instance of PrivacyForm */
    public PrivacyForm(Display display, PrivacyItem item, PrivacyList plist) {
        super(display, SR.MS_PRIVACY_RULE);
        this.display=display;
        parentView=display.getCurrent();
        
        this.item=item;
        targetList=plist;
        
        update();
        
        moveCursorTo(getNextSelectableRef(-1));
        attachDisplay(display);
    }
    
    private void update() {
        selectedAction=(selectedAction<0)?item.action:choiceAction.getSelectedIndex();
        tValue=(textValue!=null)?textValue.getValue():item.value;
        
        Object rfocus=StaticData.getInstance().roster.getFocusedObject();
        
        itemsList=null;
        itemsList=new Vector();
        
        itemsList.addElement(new SimpleString(SR.MS_PRIVACY_ACTION, true));
        choiceAction=new DropChoiceBox(display);
        for(int i=0; i<PrivacyItem.actions.length; i++){
            choiceAction.append(PrivacyItem.actions[i]);
        }
        choiceAction.setSelectedIndex(selectedAction);
        itemsList.addElement(choiceAction);
        

        itemsList.addElement(new SimpleString(SR.MS_PRIVACY_TYPE, true));
        choiceType=new DropChoiceBox(display);
        for(int i=0; i<PrivacyItem.types.length; i++){
            choiceType.append(PrivacyItem.types[i]);
        }
        choiceType.setSelectedIndex(item.type);
        itemsList.addElement(choiceType);
        
        
        textValue=new TextInput(display, SR.MS_VALUE, tValue, "", TextField.ANY);//64, TextField.ANY);

        switch (selectedAction) {
            case 0: //jid
                if (targetList!=null) {
                    if (rfocus instanceof Contact) {
                        textValue.setValue(((Contact)rfocus).getBareJid());
                    }
                }
                itemsList.addElement(textValue);
                break;
            case 1: //group
                if (targetList!=null)
                    textValue.setValue(((rfocus instanceof Group)?(Group)rfocus:((Contact)rfocus).getGroup()).getName());
                itemsList.addElement(textValue);
                break;
            case 2: //subscription
                itemsList.addElement(new SimpleString(SR.MS_SUBSCRIPTION, true));
                choiceSubscr=new DropChoiceBox(display);
                for(int i=0; i<PrivacyItem.subscrs.length; i++){
                    choiceSubscr.append(PrivacyItem.subscrs[i]);
                }
                for (int i=0; i<PrivacyItem.subscrs.length; i++) {
                    if (item.value.equals(PrivacyItem.subscrs[i])) {
                        choiceSubscr.setSelectedIndex(i);
                        break;
                    }
                }
                itemsList.addElement(choiceSubscr);
                break;
        }

        itemsList.addElement(new SimpleString(SR.MS_STANZAS, true));
        
        messageStz=new CheckBox(PrivacyItem.stanzas[0], item.messageStz); itemsList.addElement(messageStz);
        presenceInStz=new CheckBox(PrivacyItem.stanzas[1], item.presenceInStz); itemsList.addElement(presenceInStz);
        presenceOutStz=new CheckBox(PrivacyItem.stanzas[2], item.presenceOutStz); itemsList.addElement(presenceOutStz);
        iqStz=new CheckBox(PrivacyItem.stanzas[3], item.iqStz); itemsList.addElement(iqStz);
    }

    public void cmdOk() {
        try {
            int type=choiceType.getSelectedIndex();
            String value=textValue.getValue();
            if (type==2) value=PrivacyItem.subscrs[choiceSubscr.getSelectedIndex()];
            if (type!=PrivacyItem.ITEM_ANY) 
            if (value.length()==0) return;

            item.action=choiceAction.getSelectedIndex();
            item.type=type;
            item.value=value;
            
            item.messageStz=messageStz.getValue();
            item.presenceInStz=presenceInStz.getValue();
            item.presenceOutStz=presenceOutStz.getValue();
            item.iqStz=iqStz.getValue();

            if (targetList!=null) {
                if (!targetList.rules.contains(item)) {
                    targetList.addRule(item);
                    item.order=targetList.rules.indexOf(item)*10;
                }
            }
        } catch (Exception e) { }
        destroyView();
    }
}
