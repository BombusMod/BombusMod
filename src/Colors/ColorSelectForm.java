/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Colors;

import ui.controls.form.ColorSelector;
import javax.microedition.lcdui.Graphics;
import ui.VirtualList;
import ui.controls.form.DefForm;

/**
 *
 * @author Vitaly
 */
//#ifdef COLOR_TUNE
//# public final class ColorSelectForm extends DefForm {
//# // implements Runnable {
//# 
//#     private ColorSelector selector;
//#     
//#     boolean move_point;
//# 
//#     public ColorSelectForm(VirtualList parent, ColorsList list, int color) {
//#         super(ColorsList.NAMES[color]);
//#         parentView = parent;
//#         selector = new ColorSelector(list, color);
//#         itemsList.addElement(selector);
//#     }
//# 
//#     public void cmdOk() {
//#         selector.eventOk();
//#         destroyView();
//#     }
//# 
//#     public void destroyView() {
//#         move_point = false;
//#         super.destroyView();
//#     }
//# 
//#     public void drawCursor(Graphics g, int width, int height) {
//#         // prevent text selection
//#     }
//# 
//#     public void keyUp() {
//#         selector.movePointAt(1);
//#     }
//# 
//#     public void keyDwn() {
//#         selector.movePointAt(-1);
//#     }
//# 
//#     public void pageLeft() {
//#         selector.selectPrev();
//#     }
//# 
//#     public void pageRight() {
//#         selector.selectNext();
//#     }
//# 
//#     protected void pointerPressed(int x, int y) {
//#         if (selector.pointerPressed(x, y)) {
//#             redraw();
//#             return;
//#         }
//#         super.pointerPressed(x, y);
//#     }
//# 
//#     protected void pointerDragged(int x, int y) {
//#         if (selector.pointerDragged(x, y)) {
//#             redraw();
//#             return;
//#         }
//#         super.pointerDragged(x, y);
//#     }
//# 
//# /*
//#     public void run() {
//#         while (move_point) {
//#             try {
//#                 Thread.sleep(35);
//#             } catch (Exception e) { }
//# 
//#             selector.movePoint();
//#             redraw();
//#         }
//#     }
//# 
//#     protected void pointerPressed(int x, int y) {
//#         int action = selector.pointerPressed(x, y);
//#         if (action >= 0) {
//#             switch (action) {
//#                 case 1:
//#                 case 2:
//#                 case 3:
//#                     selector.dy = 1;
//#                     move_point = true;
//#                     new Thread(this).start();
//#                     break;
//#                 case 4:
//#                 case 5:
//#                 case 6:
//#                     selector.cpos = action - 4;
//#                     break;
//#             }
//#         }
//#         super.pointerPressed(x, y);
//#     }
//# 
//#     public void pointerReleased(int x, int y) {
//#         selector.dy = 0;
//#         move_point = false;
//#     }
//# */
//# }
//#endif
