/*
 * SubscriptionEdit.java
 *
 * Created on 10.05.2005, 19:09
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
import javax.microedition.lcdui.*;
import locale.SR;
import ui.controls.form.DefForm;
import ui.controls.form.DropChoiceBox;
import ui.controls.form.SimpleString;
import ui.controls.form.SpacerItem;

/**
 *
 * @author Evg_S
 */
public class SubscriptionEdit 
        extends DefForm {
    
    private Display display;
    private Displayable parentView;
    
    private DropChoiceBox action;

    String to;
    /** Creates a new instance of YesNoAlert */
    public SubscriptionEdit(Display display, Contact c) {
        super(display, SR.MS_SUBSCRIPTION);
        
        this.display=display;
        parentView=display.getCurrent();
        
        to=c.getBareJid();

        itemsList.addElement(new SimpleString(to+": "+c.subscr+((c.ask_subscribe)?",ask":""), false));

        itemsList.addElement(new SpacerItem(10));

        action=new DropChoiceBox(display, SR.MS_ACTION);
        action.append(SR.MS_NO);
        action.append(SR.MS_ASK_SUBSCRIPTION);
        action.append(SR.MS_GRANT_SUBSCRIPTION);
        action.append(SR.MS_SUBSCR_REMOVE);
        action.setSelectedIndex(0);
        itemsList.addElement(action);

        moveCursorTo(getNextSelectableRef(-1));
        attachDisplay(display);
    }
    
    public void cmdOk() {
        int actionType=action.getSelectedIndex();
        
        if (actionType>0) {
            String presence=null;
            switch (actionType) {
                case 1:
                    presence="subscribe";
                    break;
                case 2:
                    presence="subscribed";
                    break;
                case 3:
                    presence="unsubscribed";
                    break;
            }

            if (presence!=null) StaticData.getInstance().roster.sendPresence(to, presence, null, false);
        }
        destroyView();
    }

    public void destroyView(){
        if (display!=null)
            display.setCurrent(StaticData.getInstance().roster);
    }

}
