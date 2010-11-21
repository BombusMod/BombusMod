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
//#     boolean exit;
//#     int timer;
//# 
//#     public ColorSelectForm( VirtualList parent, ColorsList list, int color) {
//#         super(ColorsList.NAMES[color]);
//#         parentView = parent;
//#         selector = new ColorSelector(list, color);
//#         itemsList.addElement(selector);
//#         new Thread(this).start();
//#     }
//#     public void cmdOk() {
//#         exit = true;
//#         selector.eventOk();
//#         destroyView();
//#     }
//# 
//#     public void destroyView() {
//#         exit = true;
//#         super.destroyView();
//#     }
//#     public void drawCursor(Graphics g, int width, int height) {
//#         // prevent text selection
//#     };
//#     public void keyPressed(int key) {
//#         switch (key) {
//#             case Canvas.KEY_NUM2:
//#                 selector.dy = 1;
//#                 timer = 7;
//#                 selector.movePoint();
//#                 break;
//#             case Canvas.KEY_NUM8:
//#                 selector.dy = -1;
//#                 timer = 7;
//#                 selector.movePoint();
//#                 break;
//#             case Canvas.KEY_NUM4:
//#                 selector.selectPrev();
//#                 break;
//#             case Canvas.KEY_NUM6:
//#                 selector.selectNext();
//#                 break;
//#             default:
//#                 try {
//#                     switch (sd.canvas.getGameAction(key)){
//#                         case Canvas.UP:
//#                             selector.dy = 1;
//#                             timer = 7;
//#                             selector.movePoint();
//#                             break;
//#                         case Canvas.DOWN:
//#                             selector.dy = -1;
//#                             timer = 7;
//#                             selector.movePoint();
//#                             break;
//#                         case Canvas.LEFT:
//#                             selector.selectPrev();
//#                             break;
//#                         case Canvas.RIGHT:
//#                             selector.selectNext();
//#                             break;
//#                         case Canvas.FIRE:
//#                             cmdOk();
//#                             break;
//#                     }
//#                 } catch (Exception e) {/* IllegalArgumentException @ getGameAction */ }
//#                 redraw();
//#                 sd.canvas.serviceRepaints();
//#         }
//#         super.keyPressed(key);
//#     }
//# 
//#     protected void keyReleased(int key) {
//#             selector.dy = 0;
//#     }
//#     
//#     public void run() {
//#         while (! exit) {
//#             try { Thread.sleep(35); } catch (Exception e) { }
//#             if (--timer > 0) continue;
//#             selector.movePoint();
//#             selector.movePoint();
//#         }
//#     }
//#     protected void pointerPressed(int x, int y) {
//#         int action = -1;
//#         if ((action = selector.pointerPressed(x, y)) >= 0) {
//#             switch (action) {
//#                 case 1:
//#                 case 2:
//#                 case 3:
//#                     keyPressed(Canvas.KEY_NUM2);
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
//#     public void pointerReleased(int x, int y) {
//#         selector.dy = 0;
//#     }
//#         
//# }
//#endif

