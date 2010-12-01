/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Colors;

import javax.microedition.lcdui.Canvas;
import ui.controls.form.ColorSelector;
import javax.microedition.lcdui.Graphics;
import ui.VirtualList;
import ui.controls.form.DefForm;

/**
 *
 * @author Vitaly
 */
//#ifdef COLOR_TUNE
//# public final class ColorSelectForm extends DefForm implements Runnable {
//# 
//#     private ColorSelector selector;
//#     
//#     boolean move_point;
//# 
//#     public ColorSelectForm( VirtualList parent, ColorsList list, int color) {
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
//#     };
//# 
//#     public boolean key(int keyCode, boolean key_long) {
//#         if (key_long)
//#             return super.key(keyCode, key_long);
//# 
//#         switch (keyCode) {
//#             case Canvas.KEY_NUM2:
//#                 selector.movePointAt(1);
//#                 return true;
//#             case Canvas.KEY_NUM8:
//#                 selector.movePointAt(-1);
//#                 return true;
//#             case Canvas.KEY_NUM4:
//#                 selector.selectPrev();
//#                 return true;
//#             case Canvas.KEY_NUM6:
//#                 selector.selectNext();
//#                 return true;
//#             default:
//#                 try {
//#                     switch (sd.canvas.getGameAction(keyCode)){
//#                         case Canvas.UP:
//#                             selector.movePointAt(1);
//#                             return true;
//#                         case Canvas.DOWN:
//#                             selector.movePointAt(-1);
//#                             return true;
//#                         case Canvas.LEFT:
//#                             selector.selectPrev();
//#                             return true;
//#                         case Canvas.RIGHT:
//#                             selector.selectNext();
//#                             return true;
//#                         case Canvas.FIRE:
//#                             cmdOk();
//#                             return true;
//#                     }
//#                 } catch (Exception e) { /* IllegalArgumentException @ getGameAction */ }
//#         }
//# 
//#         return super.key(keyCode, key_long);
//#     }
//# 
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
//# }
//#endif
