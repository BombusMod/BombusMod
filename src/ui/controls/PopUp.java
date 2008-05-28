/*
 * PopUp.java
 *
 * Created on 2.02.2007, 0:19
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

package ui.controls;

import Client.Contact;
import Colors.ColorTheme;
import java.util.Vector;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import Fonts.FontCache;
import util.StringUtils;

public class PopUp {
    private final static int TYPE_SYSTEM = 1;
    private final static int TYPE_MESSAGE = 2;
    private final static int TYPE_ALERT = 3;

    private final static int COLOR_ALERT_INK = 0xffffff;
    private final static int COLOR_ALERT_BGND = 0xff0000;
    
    private int popUpHeight, popUpWidth, widthBorder, heightBorder;
    private int border=8;
    private int padding=4;
    
    private Font font;
    
    private int width;
    private int height;
    
    private Vector popUps;
    
    private ColorTheme ct;

    synchronized public void addPopup(int type, Contact contact, String message){
        if (message!=null)
            //popUps.addElement(new PopUpElement(type, contact, StringUtils.parseMessage(message, width-border-padding, height-border-padding, false, font)));
            popUps.addElement(new PopUpElement(type, contact, StringUtils.parseMessage(message, width-border-padding, font)));
//#ifdef DEBUG
//# //	System.out.println("added message to array = "+message);
//#endif
    }

    public PopUp() {
         popUps = new Vector();
         font=FontCache.getBalloonFont();
         ct=ColorTheme.getInstance();
    }
    
    public void init(Graphics g) {
        height=g.getClipHeight();
        width=g.getClipWidth();
    }
    
    public Contact getContact() {
        if(popUps.size()>0)
            return ((PopUpElement)popUps.elementAt(0)).getContact();
        return null;
    }
    
    public void next() {
        if(popUps.size()>0){
            popUps.removeElementAt(0);
        }
    }
    
    public void clear() {
        if(popUps.size()>0)
            popUps.removeAllElements();
    }

    private void drawAllStrings(Graphics g, int x, int y) {
        Vector lines=((PopUpElement)popUps.elementAt(0)).getMessage();
        if (lines.size()<1) return;
        
        int fh=getFontHeight();

	for (int line=0; line<lines.size(); ) 
	{
            g.drawString((String) lines.elementAt(line), x, y, Graphics.TOP|Graphics.LEFT);
            line=line+1;
            y += fh;
	}
    }
    
    private int getFontHeight() {
        return font.getHeight();
    }

    private int getHeight() {
        Vector message=((PopUpElement)popUps.elementAt(0)).getMessage();
        
        return getFontHeight()*message.size();
    }
    
    private int getStrWidth(String string) {
        return font.stringWidth(string);
    }
    
    private int getMaxWidth() {
        Vector lines=((PopUpElement)popUps.elementAt(0)).getMessage();

        int length=0;
        
        if (lines.size()<1) return length;

	for (int line=0; line<lines.size(); ) 
	{
            String string=(String) lines.elementAt(line);
            length=(length>getStrWidth(string))?length:getStrWidth(string);
            line++;
	}
        return length;
    }
    
    private int getColorInk() {
        int type=((PopUpElement)popUps.elementAt(0)).getType();
        switch (type) {
            case TYPE_SYSTEM:
                return ct.getColor(ColorTheme.POPUP_SYSTEM_INK);
            case TYPE_MESSAGE:
                return ct.getColor(ColorTheme.POPUP_MESSAGE_INK);
        }
        return COLOR_ALERT_INK;
    }
    
    private int getColorBgnd() {
        int type=((PopUpElement)popUps.elementAt(0)).getType();
        switch (type) {
            case TYPE_SYSTEM:
                return ct.getColor(ColorTheme.POPUP_SYSTEM_BGND);
            case TYPE_MESSAGE:
                return ct.getColor(ColorTheme.POPUP_MESSAGE_BGND);
        }
        return COLOR_ALERT_BGND;
    }
    
//paint
    public void paintCustom(Graphics g) {
	if(popUps.size()<1)
	    return;
        
        int strWdth=getMaxWidth();
        
        popUpWidth=(strWdth>(width-border))?width-border:strWdth+padding;
        widthBorder=(strWdth>popUpWidth)?border/2:(width-popUpWidth)/2;

        int stringsHeight=getHeight();

        if (stringsHeight>height) {
            heightBorder=0;
            popUpHeight=height;
        } else {
            popUpHeight=stringsHeight+padding;
            heightBorder=(height-popUpHeight)/2;
        }
     
        g.translate(widthBorder, heightBorder);

        g.setClip(0,0,popUpWidth+1,popUpHeight+1);

        g.setColor(getColorInk());
        
        g.fillRect(1,1,popUpWidth,popUpHeight);                 //shadow
        g.fillRect(0,0,popUpWidth,popUpHeight);                 //border
        
        g.setColor(getColorBgnd());
        g.fillRect(1,1,popUpWidth-2,popUpHeight-2);             //fill
        
        g.setColor(getColorInk());
        g.setFont(font);
        
        drawAllStrings(g, 2,3);
    }
//paint
    
    class PopUpElement {
        private int type;
        private Contact from;
        private Vector message;

        public PopUpElement(int type, Contact from, Vector message) {
            this.from=from;
            this.type=type;
            this.message=message;
        }

        public int getType() { return type; }
        public Vector getMessage() { return message; }
        public Contact getContact() { return from; }
    }
}
 
