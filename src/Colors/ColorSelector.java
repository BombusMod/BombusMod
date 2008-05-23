/*
 * ColorSelector.java
 *
 * Copyright (c) 2006-2008, Daniel Apatin (ad), http://apatin.net.ru
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
 */

package Colors;

import io.NvStorage;
import java.io.DataOutputStream;
import javax.microedition.lcdui.*;
import locale.SR;
import Colors.ColorTheme;
import ui.*;

public class ColorSelector extends Canvas implements Runnable, CommandListener {

    static Font mfont = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_SMALL);
    static int w, h;

    private Display display;
    Displayable parentView;
    Graphics G;
    private ColorTheme ct;

    int cpos;
    String nowcolor;
    int red;
    int green;
    int blue;

    String val;

    int dy;
    int timer;
    boolean exit;

    private int value;
    int paramName;
    int ncolor;

    Command cmdOk = new Command(SR.MS_OK, Command.OK, 1);

    private int color;

    private int py;
    private int ph;

    Command cmdCancel = new Command(SR.MS_CANCEL /*"Back"*/, Command.CANCEL, 99);

    public ColorSelector(Display display, int paramName) {
        super();
        this.display=display;
        parentView=display.getCurrent();
        this.paramName=paramName;
        
        ct=ColorTheme.getInstance();
        this.color=ct.getColor(paramName);
        
        w = getWidth();
        h = getHeight();

        //System.out.println(color+" "+cl.getColorString(color));

        red=ct.getRed(color);
        green=ct.getGreen(color);
        blue=ct.getBlue(color);

        //String s = cl.ColorToString(red, green, blue);

        cpos = 0;

        exit = false;
        (new Thread(this)).start();

        this.addCommand(cmdOk);
        this.addCommand(cmdCancel);

        this.setCommandListener(this);
        display.setCurrent(this);
    }

    protected void paint(Graphics g) {
        py = h - 20;
        ph = h - 50;
        g.setColor(0xffffff);
        g.fillRect(0, 0, w, h);
        g.setFont(mfont);
        String s = ColorTheme.ColorToString(red, green, blue);

        g.setColor(0);
        g.setStrokeStyle(g.SOLID);
        g.drawRect(2, 2, 15, 15);
        g.setColor(red,green,blue);
        g.fillRect(4, 4, 12, 12);
        g.setColor(0x800000);
        g.drawString(s+" "+ColorForm.NAMES[paramName], 20, 5, g.TOP|g.LEFT);


        //draw red
        int pxred = w/3-10;
        int psred = (ph*red)/255;
        g.setColor(0);
        g.setStrokeStyle(g.SOLID);
        g.fillRect(pxred-2, py-ph, 5, ph);
        g.drawString("R", pxred, py+2, g.TOP|g.HCENTER);
        g.setColor(0xff2020);
        g.fillRect(pxred-2, py-psred, 5, psred);
        if (cpos == 0) {
            g.setColor(0);
            g.setStrokeStyle(g.DOTTED);
            g.drawRect(pxred-7, py-ph-5, 15, ph+20);
        }
        
        //draw green
        int pxgreen = w/2;
        int psgreen = (ph*green)/255;
        g.setColor(0);
        g.setStrokeStyle(g.SOLID);
        g.fillRect(pxgreen-2, py-ph, 5, ph);
        g.drawString("G", pxgreen, py+2, g.TOP|g.HCENTER);
        g.setColor(0x00ff00);
        g.fillRect(pxgreen-2, py-psgreen, 5, psgreen);
        if (cpos == 1) {
            g.setColor(0);
            g.setStrokeStyle(g.DOTTED);
            g.drawRect(pxgreen-7, py-ph-5, 15, ph+20);
        }
        
        //draw blue
        int pxblue = w-(w/3-10);
        int psblue = (ph*blue)/255;
        g.setColor(0);
        g.setStrokeStyle(g.SOLID);
        g.fillRect(pxblue-2, py-ph, 5, ph);
        g.drawString("B", pxblue, py+2, g.TOP|g.HCENTER);
        g.setColor(0x4848ff);
        g.fillRect(pxblue-2, py-psblue, 5, psblue);
        if (cpos == 2) {
            g.setColor(0);
            g.setStrokeStyle(g.DOTTED);
            g.drawRect(pxblue-7, py-ph-5, 15, ph+20);
        }
    }
/*
    protected void pointerPressed(int x, int y) {
        int r=checkPressed((w/3)-12, x, y);
        int g=checkPressed(w/2-2, x, y);
        int b=checkPressed(w-(w/3-8), x, y);
        if (r>-1) red=r;
        if (g>-1) green=g;
        if (b>-1) blue=b;

        repaint();
    }

    private int checkPressed(int w, int x, int y) {
        if (x>=w && x<(w+5))
            if (y<=py && y>=py-ph) {
                int val=py*(py-y)/255;
                return val;
            }
        return -1;
    }
*/
    protected void keyPressed(int key) {
        switch (key) {
            case KEY_NUM2:
                timer = 7;
                dy = 1;
                movePoint();
                break;
            case KEY_NUM8:
                timer = 7;
                dy = -1;
                movePoint();
                break;
            case KEY_NUM4:
                cpos -= 1; if (cpos < 0) cpos = 2;
                break;
            case KEY_NUM6:
                cpos += 1; if (cpos > 2) cpos = 0;
                break;
            case KEY_NUM5:
                eventOk();
                exit = true;
                destroyView();
                break;
            case KEY_NUM0:
                exit = true;
                display.setCurrent(parentView);
                break;
            default:
                try {
                    switch (getGameAction(key)){
                        case UP:
                            timer = 7;
                            dy = 1;
                            movePoint();
                            break;
                        case DOWN:
                            timer = 7;
                            dy = -1;
                            movePoint();
                            break;
                        case LEFT:
                            cpos -= 1; if (cpos < 0) cpos = 2;
                            break;
                        case RIGHT:
                            cpos += 1; if (cpos > 2) cpos = 0;
                            break;
                        case FIRE:
                            eventOk();
                            exit = true;
                            destroyView();
                            break;

                        default:
                            if (key=='5') {
                                eventOk();
                                exit = true;
                                destroyView();
                                break;
                            }
                    }
                } catch (Exception e) {/* IllegalArgumentException @ getGameAction */}
                repaint();
                serviceRepaints();
        }
    }

    protected void keyReleased(int key) {
            dy = 0;
    }

    public void run() {
        while (! exit) {
            try { Thread.sleep(35); } catch (Exception e) { }
            if (--timer > 0) continue;
            movePoint();
            movePoint();
        }
    }

    public void setValue(int vall) {
        this.value=vall;
        ct.setColor(paramName, value);
//#ifdef COLOR_TUNE
//#         ct.saveToStorage();
//#endif
    }

    private void movePoint() {
        if (dy == 0) return;
        switch (cpos) {
            case 0:
                red=dy+red;
                if (red>255) red=0;
                if (red<0) red=255;
                break;
            case 1:
                green=dy+green;
                if (green>255) green=0;
                if (green<0) green=255;
                break;
            case 2:
                blue=dy+blue;
                if (blue>255) blue=0;
                if (blue<0) blue=255;
                break;
        }
        repaint();
    }

    public void commandAction(Command c, Displayable d) {
        if (c==cmdCancel) {
            exit = true;
            destroyView();
            return;
        }
        if (c==cmdOk) {
            eventOk();
            destroyView();
            return;
        }
    }

    private void eventOk () {
//#if COLOR_TUNE
//#         String val = ColorTheme.ColorToString(red, green, blue);
//# 
//#         int finalColor=ColorTheme.getColorInt(val);
//#         //System.out.println(val);
//# 
//#         setValue(finalColor);
//#endif
        exit = true;
    }

    public void destroyView()	{
        if (display!=null)   display.setCurrent(parentView);
    }
}
