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
//#     }
//#     public void cmdOk() {
//#         selector.eventOk();
//#         destroyView();
//#     }
//#     public void drawCursor(Graphics g, int width, int height) {
//#         // prevent text selection
//#     };
//#     public void keyPressed(int key) {
//#         switch (key) {
//#             case KEY_NUM2:                
//#                 selector.movePoint(1);
//#                 break;
//#             case KEY_NUM8:                ;
//#                 selector.movePoint(-1);
//#                 break;
//#             case KEY_NUM4:
//#                 selector.selectPrev();
//#                 break;
//#             case KEY_NUM6:
//#                 selector.selectNext();
//#                 break;
//#             default:
//#                 try {
//#                     switch (getGameAction(key)){
//#                         case UP:
//#                             selector.movePoint(1);
//#                             break;
//#                         case DOWN:
//#                             selector.movePoint(-1);
//#                             break;
//#                         case LEFT:
//#                             selector.selectPrev();
//#                             break;
//#                         case RIGHT:
//#                             selector.selectNext();
//#                             break;
//#                         case FIRE:
//#                             cmdOk();
//#                             break;
//#                     }
//#                 } catch (Exception e) {/* IllegalArgumentException @ getGameAction */ }
//#                 repaint();
//#                 serviceRepaints();
//#         }
//#         super.keyPressed(key);
//#     }
//#     protected void keyRepeated(int key) {
//#         try {
//#                     switch (getGameAction(key)){
//#                         case UP:
//#                             selector.movePoint(1);
//#                             break;
//#                         case DOWN:
//#                             selector.movePoint(-1);
//#                             break;
//#                         case LEFT:
//#                             selector.selectPrev();
//#                             break;
//#                         case RIGHT:
//#                             selector.selectNext();
//#                             break;
//#                         case FIRE:
//#                             cmdOk();
//#                             break;
//#                     }
//#                 } catch (Exception e) {/* IllegalArgumentException @ getGameAction */ }
//# 
//#     }
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

