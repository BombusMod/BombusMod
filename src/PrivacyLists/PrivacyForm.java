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
import java.util.Vector;
import javax.microedition.lcdui.*;
import locale.SR;
import ui.controls.form.BoldString;
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
    private PrivacyItem item;
    
    private PrivacyList targetList;
    
    DropChoiceBox choiceAction;
    DropChoiceBox choiseType;
    DropChoiceBox choiceSubscr;
    
    CheckBox messageStz;
    CheckBox presenceInStz;
    CheckBox presenceOutStz;
    CheckBox iqStz;

    TextInput textValue;

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
        itemsList=new Vector();
        
        textValue=new TextInput(display, item.value, "", TextField.ANY);//64, TextField.ANY);
        itemsList.addElement(textValue);
        
        itemsList.addElement(new BoldString(SR.MS_PRIVACY_ACTION));
        choiceAction=new DropChoiceBox(display);
        for(int i=0; i<PrivacyItem.actions.length; i++){
            choiceAction.append(PrivacyItem.actions[i]);
        }
        choiceAction.setSelectedIndex(item.action);
        itemsList.addElement(choiceAction);

        itemsList.addElement(new BoldString(SR.MS_PRIVACY_TYPE));
        choiseType=new DropChoiceBox(display);
        for(int i=0; i<PrivacyItem.types.length; i++){
            choiseType.append(PrivacyItem.types[i]);
        }
        choiseType.setSelectedIndex(item.type);
        itemsList.addElement(choiseType);
        
        itemsList.addElement(new BoldString(SR.MS_SUBSCRIPTION));
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
        
        itemsList.addElement(new BoldString(SR.MS_STANZAS));
        messageStz=new CheckBox(PrivacyItem.stanzas[0], PrivacyItem.stanzasSet[0]); itemsList.addElement(messageStz);
        presenceInStz=new CheckBox(PrivacyItem.stanzas[1], PrivacyItem.stanzasSet[1]); itemsList.addElement(presenceInStz);
        presenceOutStz=new CheckBox(PrivacyItem.stanzas[2], PrivacyItem.stanzasSet[2]); itemsList.addElement(presenceOutStz);
        iqStz=new CheckBox(PrivacyItem.stanzas[3], PrivacyItem.stanzasSet[3]); itemsList.addElement(iqStz);
    }

    public void cmdOk() {
        try {
            int type=choiseType.getSelectedIndex();
            String value=textValue.getValue();
            if (type==2) value=PrivacyItem.subscrs[choiceSubscr.getSelectedIndex()];
            if (type!=PrivacyItem.ITEM_ANY) 
            if (value.length()==0) return;

            item.action=choiceAction.getSelectedIndex();
            item.type=type;
            item.value=value;
            
            item.stanzasSet[0]=messageStz.getValue();
            item.stanzasSet[1]=presenceInStz.getValue();
            item.stanzasSet[2]=presenceOutStz.getValue();
            item.stanzasSet[3]=iqStz.getValue();

            if (targetList!=null) {
                if (!targetList.rules.contains(item)) {
                    targetList.addRule(item);
                    item.order=targetList.rules.indexOf(item)*10;
                }
            }
        } catch (Exception e) { }
    }
}
