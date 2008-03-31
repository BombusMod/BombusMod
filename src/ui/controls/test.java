/*
 * test.java
 *
 * Created on 31 Март 2008 г., 14:46
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ui.controls;

import Colors.Colors;
import javax.microedition.lcdui.Graphics;

/**
 *
 * @author ad
 */
public class test {
    public static void draw(Graphics g, int wPop, int hPop, int x, int y, String text) {
        int height=g.getClipHeight();
        int width=g.getClipWidth();
        
        if (x+wPop>width) {
            x=width-wPop-2;
        }
        if (y+hPop>height) {
            y=height-hPop-2;
        }
        
        g.translate(x, y);

        g.setColor(Colors.BALLOON_BGND);
        g.fillRect(0, 0, wPop, hPop);
        g.setColor(Colors.BALLOON_INK);
        g.drawRect(0, 0, wPop, hPop);
    }
}
