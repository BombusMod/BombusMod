/*
 * PrivacyForm.java
 *
 * Created on 11.09.2005, 2:32
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

package PrivacyLists;
import Client.*;
import javax.microedition.lcdui.*;
import locale.SR;
import ui.controls.TextFieldCombo;
import ui.controls.TextFieldEx;

/**
 *
 * @author EvgS
 */
public class PrivacyForm
        implements
        CommandListener,
        ItemStateListener {
    
    private Display display;
    private Displayable parentView;
    private PrivacyItem item;
    
    private PrivacyList targetList;
    
    Form form=new Form(SR.MS_PRIVACY_RULE);
    ChoiceGroup choiceAction=new ChoiceGroup(SR.MS_PRIVACY_ACTION, ChoiceGroup.POPUP, PrivacyItem.actions, null);
    ChoiceGroup choiseType=new ChoiceGroup(SR.MS_PRIVACY_TYPE, ChoiceGroup.POPUP, PrivacyItem.types, null);
    ChoiceGroup choiseStanzas=new ChoiceGroup(SR.MS_STANZAS, ChoiceGroup.MULTIPLE, PrivacyItem.stanzas, null);
    TextField textValue;
    //TextField textOrder;
    ChoiceGroup choiceSubscr=new ChoiceGroup(SR.MS_SUBSCRIPTION, ChoiceGroup.POPUP, PrivacyItem.subscrs, null);
    
    Command cmdCancel=new Command(SR.MS_CANCEL, Command.BACK, 99);
    Command cmdOk=new Command(SR.MS_OK, Command.OK, 1);
    /** Creates a new instance of PrivacyForm */
    public PrivacyForm(Display display, PrivacyItem item, PrivacyList plist) {
        this.display=display;
        parentView=display.getCurrent();
        this.item=item;
        targetList=plist;
        
        textValue=new TextFieldEx(null, item.value, 64, TextField.ANY);
        TextFieldCombo.setLowerCaseLatin(textValue);
        
        form.append(choiceAction);
        choiceAction.setSelectedIndex(item.action, true);
        
        form.append(choiseType);
        choiseType.setSelectedIndex(item.type, true);
        
        form.append(textValue);
        switchType();
        
        form.append(choiseStanzas);
        choiseStanzas.setSelectedFlags(item.stanzasSet);
        
        //form.append("Order: "+item.order);
        
        form.setItemStateListener(this);
        form.setCommandListener(this);
        form.addCommand(cmdOk);
        form.addCommand(cmdCancel);
        display.setCurrent(form);
        
    }
    
    private void switchType() {
        int index=choiseType.getSelectedIndex();
        try {
            Object rfocus=StaticData.getInstance().roster.getFocusedObject();
            switch (index) {
                case 0: //jid
                    if (targetList!=null) if (rfocus instanceof Contact) {
                        textValue.setString(((Contact)rfocus).getBareJid());
                    }
                    form.set(2, textValue);
                    break;
                case 1: //group
                    if (targetList!=null) textValue.setString( ( (rfocus instanceof Group)?
                        (Group)rfocus : 
                        ((Contact)rfocus).getGroup()
                        ).getName());

                    form.set(2, textValue);
                    break;
                case 2: //subscription
                {
                    int i;

                    for (i=0; i<PrivacyItem.subscrs.length; i++)
                    if (item.value.equals(PrivacyItem.subscrs[i])) {
                         choiceSubscr.setSelectedIndex(i, true);
                        break;
                    }
                        choiceSubscr.setSelectedIndex(i, true);
                }
                    form.set(2, choiceSubscr);
                    break;
                    
                case 3:
                    form.set(2, new StringItem(null,"(ANY)"));
            }
            /*if (index==2) {
                form.set(2, choiceSubscr);
            } else {
                textValue.setLabel(PrivacyItem.types[index]);
                form.set(2, textValue);
            }
             */
        } catch (Exception e) {/* При �?мене на �?амого �?еб�? */ }
    }
    
    public void commandAction(Command c, Displayable d) {
        if (c==cmdCancel) { destroyView(); return; }
        if (c==cmdOk) {
            try {
                int type=choiseType.getSelectedIndex();
                String value=textValue.getString();
                if (type==2) value=PrivacyItem.subscrs[choiceSubscr.getSelectedIndex()];
                if (type!=PrivacyItem.ITEM_ANY) 
                if (value.length()==0) return;
                //int order=Integer.parseInt(textOrder.getString());
                
                item.action=choiceAction.getSelectedIndex();
                item.type=type;
                item.value=value;
                //item.order=order;
                choiseStanzas.getSelectedFlags(item.stanzasSet);
                
                if (targetList!=null) 
                    if (!targetList.rules.contains(item)) {
                        targetList.addRule(item);
                        item.order=targetList.rules.indexOf(item)*10;
                    }
                destroyView();
            } catch (Exception e) {
                //e.printStackTrace();
            }
        }
    }
    
    public void itemStateChanged(Item item){
        if (item==choiseType) switchType();
    }
    
    public void destroyView(){
        if (display!=null)   display.setCurrent(parentView);
    }
}
