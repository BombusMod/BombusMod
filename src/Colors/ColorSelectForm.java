/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Colors;

import ui.controls.form.ColorSelector;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import ui.controls.form.DefForm;

/**
 *
 * @author Vitaly
 */
public class ColorSelectForm extends DefForm {
    private ColorSelector selector;
    public ColorSelectForm(Display display, Displayable parent, ColorsList list, int color) {
        super(display, parent, list.NAMES[color]);
        parentView = parent;
        selector = new ColorSelector(list, color);
        itemsList.addElement(selector);
        setCommandListener(this);
        attachDisplay(display);
    }
    public void cmdOk() {
        selector.eventOk();
        destroyView();
    }
    public void keyPressed(int key) {
        switch (key) {
            case KEY_NUM2:                
                selector.movePoint(1);
                break;
            case KEY_NUM8:                ;
                selector.movePoint(-1);
                break;
            case KEY_NUM4:
                selector.selectPrev();
                break;
            case KEY_NUM6:
                selector.selectNext();
                break;
            default:
                try {
                    switch (getGameAction(key)){
                        case UP:
                            selector.movePoint(1);
                            break;
                        case DOWN:
                            selector.movePoint(-1);
                            break;
                        case LEFT:
                            selector.selectPrev();
                            break;
                        case RIGHT:
                            selector.selectNext();
                            break;
                        case FIRE:
                            cmdOk();
                            break;
                    }
                } catch (Exception e) {/* IllegalArgumentException @ getGameAction */ }
                repaint();
                serviceRepaints();
        }
    }
}
