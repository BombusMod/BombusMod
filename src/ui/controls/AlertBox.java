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

import Client.StaticData;
import Colors.ColorTheme;
import java.util.Vector;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
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
public abstract class AlertBox extends Canvas implements CommandListener {
    
    protected Display display;
    protected Displayable next;
    
    protected Command cmdOk=new Command(SR.MS_OK, Command.OK, 1);
    protected Command cmdCancel=new Command(SR.MS_CANCEL, Command.BACK, 2);
    
    public boolean isShowing;

    Font messageFont;
    Font barFont;

    private String mainbar;
    private String text;
    
    private Vector lines=null;
    
//#ifdef GRADIENT
//#     private Gradient gr=null;
//#endif
    
    private Progress pb;

    int pos=0;
    int steps=1;
    
    ColorTheme ct;
        
    public AlertBox(String mainbar, String text, Display display, Displayable nextDisplayable) {
        this.display=display;
        
        ct=ColorTheme.getInstance();

        messageFont=FontCache.getMsgFont();
        barFont=FontCache.getBarFont();
        
        next=(nextDisplayable==null)? display.getCurrent() : nextDisplayable;

        this.text=text;
        this.mainbar=mainbar;
        isShowing=true;
        
        addCommand(cmdOk);
        addCommand(cmdCancel);

        setCommandListener(this);
        display.setCurrent(this);
    }
    
    public void commandAction(Command command, Displayable displayable) {
        isShowing=false;
        if (command==cmdOk) {
            yes();
        } else {
            no();
        }
        destroyView();
    }
    
    public void destroyView()	{
        isShowing=false;
        removeCommand(cmdOk);
        removeCommand(cmdCancel);

        if (display==null) {
            display.setCurrent(StaticData.getInstance().roster);
        } else {
            display.setCurrent(next);
        }
    }
    
    private void getLines(int width, int height, int fh) {
        if (lines==null) {
            //lines=StringUtils.parseMessage(text, width-4, height-fh-10, false, messageFont);
            lines=StringUtils.parseMessage(text, width-4, messageFont);
            text=null;
        }
    }

    protected void paint(Graphics g) {
        if (isShowing) {
            int width=g.getClipWidth();
            int height=g.getClipHeight();

            int oldColor=g.getColor();
            
            g.setColor(ct.getColor(ColorTheme.LIST_BGND));
            g.fillRect(0,0, width, height); //fill back

            int fh=0;
            if (mainbar!=null) {
                fh=getBarFontHeight();
            
                g.setClip(0,0, width, fh);
//#ifdef GRADIENT
//#                 if (gr==null) {
//#                     gr=new Gradient(0, 0, width, fh, ct.getColor(ColorTheme.BAR_BGND), ct.getColor(ColorTheme.BAR_BGND_BOTTOM), false);
//#                 }
//#                 gr.paint(g);
//#else
            g.setColor(ct.getColor(ColorTheme.BAR_BGND));
            g.fillRect(0, 0, width, fh);
//#endif
                g.setFont(barFont);
                g.setColor(ct.getColor(ColorTheme.BAR_INK));
                g.drawString(mainbar, width/2, 0, Graphics.TOP|Graphics.HCENTER);
            }
            
            g.setClip(0,0, width, height);
            
            getLines(width-4, height-fh-15, fh);
            drawAllStrings(g, 2, fh);
            g.setColor(oldColor);
            
            if (pos>0)
                drawProgress (g, width, height);
        }
    }
    
    private void drawAllStrings(Graphics g, int x, int y) {
        if (lines==null)
            return;
        if (lines.size()<1)
            return;
        
        g.setFont(messageFont);
        int fh=getFontHeight();
        g.setColor(ct.getColor(ColorTheme.LIST_INK));

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
            pb=new Progress(g, 0, height, width);
        pb.draw(filled, Integer.toString(pos));
    }
    
    public abstract void yes();

    public abstract void no();    
}
