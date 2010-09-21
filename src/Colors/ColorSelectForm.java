/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Colors;

import ui.controls.form.ColorSelector;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import ui.controls.form.DefForm;

/**
 *
 * @author Vitaly
 */
//#ifdef COLOR_TUNE
//# public class ColorSelectForm extends DefForm {
//#     private ColorSelector selector;
//#     public ColorSelectForm( Displayable parent, ColorsList list, int color) {
//#         super(ColorsList.NAMES[color]);
//#         parentView = parent;
//#         selector = new ColorSelector(list, color);
//#         itemsList.addElement(selector);
//#         setMenuListener(this);        
//#     }
//#     public void cmdOk() {
//#         selector.eventOk();
//#         destroyView();
//#     }
//#     public void drawCursor(Graphics g, int width, int height) {
//#         // prevent text selection
//#     }
//# 
//#     protected boolean key(int keyCode, boolean key_long) {
//#         switch (keyCode) {
//#             case KEY_NUM2:
//#                 selector.movePoint(1);
//#                 return true;
//#             case KEY_NUM8:
//#                 selector.movePoint(-1);
//#                 return true;
//#             case KEY_NUM4:
//#                 selector.selectPrev();
//#                 return true;
//#             case KEY_NUM6:
//#                 selector.selectNext();
//#                 return true;
//#             default:
//#                 try {
//#                     switch (getGameAction(keyCode)) {
//#                         case UP:
//#                             selector.movePoint(1);
//#                             return true;
//#                         case DOWN:
//#                             selector.movePoint(-1);
//#                             return true;
//#                         case LEFT:
//#                             selector.selectPrev();
//#                             return true;
//#                         case RIGHT:
//#                             selector.selectNext();
//#                             return true;
//#                         case FIRE:
//#                             cmdOk();
//#                             return true;
//#                     }
//#                 } catch (Exception e) {/* IllegalArgumentException @ getGameAction */ }
//#                 repaint();
//#                 serviceRepaints();
//#         }
//#         
//#         return super.key(keyCode, key_long);
//#     }
//# 
//#     protected void pointerPressed(int x, int y) {
//#         checkBarPointed(x, y, selector.pxred, 0);
//#         checkBarPointed(x, y, selector.pxgreen, 1);
//#         checkBarPointed(x, y, selector.pxblue, 2);
//#         super.pointerPressed(x, y);
//#     }
//#     private boolean checkPointer(int x, int y, int pos) {
//#         if ((x>=pos-5) && (x <= pos+10) && (y >= 0) && y <= (getListHeight() - 50)) {
//#             return true;
//#         }
//#         return false;
//#     }
//#     private void checkBarPointed(int x, int y, int px, int pos) {
//#         if (checkPointer(x, y, px)) {
//#             if (selector.cpos != pos) {
//#                 selector.cpos = pos;
//#             } else {
//#                 if (y < ((getListHeight()-50)/2)) {
//#                 selector.movePoint(1);
//#                 } else {
//#                 selector.movePoint(-1);
//#                 }
//#             }
//#         repaint();
//#         serviceRepaints();
//#         }
//#     }
//#         
//# }
//#endif

