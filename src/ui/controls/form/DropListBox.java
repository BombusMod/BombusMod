/*
 * DropListBox.java
 *
 * Created on 22 Май 2008 г., 16:37
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package ui.controls.form;

import java.util.Vector;
import locale.SR;

/**
 *
 * @author ad
 */
public class DropListBox
        extends DefForm {

    private DropChoiceBox cb;

    public DropListBox(Vector listItems, DropChoiceBox cb) {
        super(SR.MS_SELECT);     
        SimpleString item;
        for (int i = 0; i < listItems.size(); i++) {
            item = new SimpleString((String) listItems.elementAt(i), false);
            item.selectable = true;
            itemsList.addElement(item);
        }
        this.cb = cb;
        enableListWrapping(true);
        moveCursorTo(cb.getSelectedIndex());        
    }

    public void eventOk() {
        if (itemsList.size() > 0) {
            cb.setSelectedIndex(cursor);
        }
        destroyView();
    }

    public void cmdOk() {
        eventOk();
    }
}
