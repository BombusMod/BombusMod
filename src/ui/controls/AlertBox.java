/*
 * AlertBox.java
 *
 * Created on 17.05.2008, 14:35
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

package ui.controls;

import Client.Config;
import Client.StaticData;
import Colors.ColorTheme;
import java.util.Vector;
import javax.microedition.lcdui.Canvas;
//#ifndef MENU_LISTENER
//# import javax.microedition.lcdui.CommandListener;
//# import javax.microedition.lcdui.Command;
//#endif
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
//#ifndef WOFFSCREEN
import javax.microedition.lcdui.Image;
//#endif
import locale.SR;
import Fonts.FontCache;
import util.StringUtils;
//#ifdef GRADIENT
//# import ui.Gradient;
//#endif
/**
 *
 * @author ad
 */
public abstract class AlertBox
        extends Canvas
        
//#ifndef MENU_LISTENER
//#         implements CommandListener
//#endif
    {

    protected Display display;
    protected Displayable next;
    
//#ifndef MENU_LISTENER
//#     protected Command cmdOk=new Command(SR.MS_OK, Command.OK, 1);
//#     protected Command cmdCancel=new Command(SR.MS_CANCEL, Command.BACK, 2);
//#endif
    
    public boolean isShowing;

    Font messageFont;
    Font barFont;

    private String left=SR.MS_OK;
    private String right=SR.MS_CANCEL;
    
    boolean init;
    CommandsPointer ar=new CommandsPointer();

    private String mainbar;
    private String text;
    
    private Vector lines=null;
    
    private int topColor=ColorTheme.getColor(ColorTheme.BAR_BGND);
//#ifdef GRADIENT
//#     private int bottomColor=ColorTheme.getColor(ColorTheme.BAR_BGND_BOTTOM);
//#     private Gradient gr=null;
//#     private Gradient gr2=null;
//#endif
    
    private Progress pb;

    int pos=0;
    int steps=1;

    
//#ifndef WOFFSCREEN
    private Image offscreen = null;
//#endif
        
    private int height;
    private int width;    
    
    public AlertBox(String mainbar, String text, Display display, Displayable nextDisplayable) {
        this.display=display;
        
        setFullScreenMode(Config.getInstance().fullscreen);

        messageFont=FontCache.getFont(false, FontCache.msg);
        barFont=FontCache.getFont(false, FontCache.bar);
        
        next=(nextDisplayable==null)? display.getCurrent() : nextDisplayable;

        this.text=text;
        this.mainbar=mainbar;
        isShowing=true;
//#ifndef MENU_LISTENER
//#         addCommand(cmdOk);
//#         addCommand(cmdCancel);
//# 
//#         setCommandListener(this);
//#endif
        display.setCurrent(this);
    }
    
//#ifndef MENU_LISTENER
//#     public void commandAction(Command command, Displayable displayable) {
//#         if (command==cmdOk) {
//#             yes();
//#         } else {
//#             no();
//#         }
//#         destroyView();
//#     }
//#endif

    public void destroyView()	{
        isShowing=false;

        if (display==null) {
            display.setCurrent(StaticData.getInstance().roster);
        } else {
            display.setCurrent(next);
        }
    }
    
    private void getLines(int width) {
        if (lines==null) {
            lines=StringUtils.parseMessage(text, width-4, messageFont);
            text=null;
        }
    }

    protected void paint(Graphics graphics) {
        if (isShowing) {
            Graphics g = graphics;
//#ifndef WOFFSCREEN
            if (offscreen != null) graphics = offscreen.getGraphics();
//#endif
            width=g.getClipWidth();
            height=g.getClipHeight();

            if (!init && hasPointerEvents())
                    ar.init(width, height, getBarFontHeight());
            
            int oldColor=g.getColor();
            
            g.setColor(ColorTheme.getColor(ColorTheme.LIST_BGND));
            g.fillRect(0,0, width, height); //fill back

            int fh=0;
            if (mainbar!=null) {
                fh=getBarFontHeight();
//#ifdef GRADIENT
//#                 if (gr==null) {
//#                     gr=new Gradient(0, 0, width, fh, ColorTheme.getColor(ColorTheme.BAR_BGND), bottomColor, false);
//#                 }
//#                 gr.paint(g);
//#else
            g.setColor(topColor);
            g.fillRect(0, 0, width, fh);
//#endif
                g.setFont(barFont);
                g.setColor(ColorTheme.getColor(ColorTheme.BAR_INK));
                g.drawString(mainbar, width/2, 0, Graphics.TOP|Graphics.HCENTER);
            }


            fh=getBarFontHeight();
//#ifdef GRADIENT
//#             if (gr2==null) {
//#                 gr2=new Gradient(0, height-fh, width, height, topColor, bottomColor, false);
//#             }
//#             gr2.paint(g);
//#else
        g.setColor(topColor);
        g.fillRect(0, height-fh, width, fh);
//#endif
            g.setFont(barFont);
            g.setColor(ColorTheme.getColor(ColorTheme.BAR_INK));
            g.drawString(left, 2, height-fh, Graphics.TOP|Graphics.LEFT);
            g.drawString(right, width-2, height-fh, Graphics.TOP|Graphics.RIGHT);

            getLines(width-4);
            drawAllStrings(g, 2, fh);

            if (pos>0)
                drawProgress (g, width, height-fh);
            
            g.setColor(oldColor);
//#ifndef WOFFSCREEN
            if (g != graphics) g.drawImage(offscreen, 0, 0, Graphics.LEFT | Graphics.TOP);
//#endif
        }
    }
    
    private void drawAllStrings(Graphics g, int x, int y) {
        if (lines==null)
            return;
        if (lines.size()<1)
            return;
        
        g.setFont(messageFont);
        int fh=getFontHeight();
        g.setColor(ColorTheme.getColor(ColorTheme.LIST_INK));

	for (int line=0; line<lines.size(); ){
            g.drawString((String) lines.elementAt(line), x, y, Graphics.TOP|Graphics.LEFT);
            line=line+1;
            y += fh;
	}
    }
    
    private int getBarFontHeight() {
        return barFont.getHeight();
    }
    
    private int getFontHeight() {
        return messageFont.getHeight();
    }
    
    public void drawProgress (Graphics g, int width, int height) {
        int filled=pos*width/steps;

        if (pb==null)
            pb=new Progress(0, height, width);
        Progress.draw(g, filled, Integer.toString(steps-pos));
    }
    
    protected void hideNotify() {
//#ifndef WOFFSCREEN
	offscreen=null;
//#endif
    }
    
    protected void showNotify() {
//#ifndef WOFFSCREEN
	if (!isDoubleBuffered()) offscreen=Image.createImage(width, height);
//#endif
    }
    
    protected void sizeChanged(int w, int h) {
        width=w;
        height=h;
//#ifndef WOFFSCREEN
        if (!isDoubleBuffered()) offscreen=Image.createImage(width, height);
//#endif
    }
    
    protected void keyPressed(int keyCode) { // overriding this method to avoid autorepeat
        if (keyCode==Config.SOFT_LEFT || keyCode==FIRE) {
            yes();
            destroyView();
        } else if (keyCode==Config.SOFT_RIGHT || keyCode==Config.KEY_BACK) {
            no();
            destroyView();
        }
    }
    
    protected void pointerPressed(int x, int y) {
        int act=ar.pointerPressed(x, y);
        if (act==1) {
            yes();
            destroyView();
            return;
        } else if (act==2) {
            no();
            destroyView();
            return;
        }
    }
    
    public abstract void yes();

    public abstract void no();    
}

