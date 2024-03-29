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

import Client.StaticData;
import Colors.ColorTheme;
import images.RosterIcons;
import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import Fonts.FontCache;
import ui.VirtualCanvas;
import util.StringUtils;

public class PopUp {
    private static PopUp instance;
    public final static int TYPE_SYSTEM = 1;
    public final static int TYPE_MESSAGE = 2;
    public final static int TYPE_ALERT = 3;

    private final static int COLOR_ALERT_INK = 0xffffff;
    private final static int COLOR_ALERT_BGND = 0xff0000;

    private int popUpHeight, popUpWidth, widthBorder, heightBorder;
    private int border=8;
    private int padding=4;

    private static Font font;

    private int width;
    private int height;

    private Vector popUps;

    private final static int  SCROLLABLE_NONE=-1;
    private final static int  SCROLLABLE_DOWN=0;
    private final static int  SCROLLABLE_BOTH=1;
    private final static int  SCROLLABLE_UP=2;

    private int maxWdth;

    private int startLine;

    public int scrollable=SCROLLABLE_NONE;

    private RosterIcons ri;

    public boolean handled = false;

    synchronized public void addPopup(int type, String contact, String message){
        if (message!=null) {
            //popUps.addElement(new PopUpElement(type, contact, StringUtils.parseMessage(message, width-border-padding, height-border-padding, false, font)));
            font=FontCache.getFont(false, FontCache.baloon); //Issue 88
            popUps.addElement(new PopUpElement(type, contact, StringUtils.parseMessage(message, width-border-padding, font)));
        }
    }

    private PopUp() {
         popUps = new Vector();
         font=FontCache.getFont(false, FontCache.baloon);
         ri=RosterIcons.getInstance();
    }
    public static PopUp getInstance() {
        if (instance == null)
            instance = new PopUp();
        return instance;
    }

    public void init(Graphics g, int width, int height) {
        this.height=height;
        this.width=width;
    }

    public String getContact() {
        if(size()>0)
            return ((PopUpElement) popUps.elementAt(0)).getContact();
        return null;
    }

    public boolean goToMsgList() {
        String c = getContact();
        if (c != null) {
            StaticData.getInstance().roster.showContactMessageList(c);
        }

        return handled = next();
    }

    public boolean next() {
        if(size()>0) {
            popUps.removeElementAt(0);
            scrollable=SCROLLABLE_NONE;
            startLine=0;
            return true;
        }
        return false;
    }

    private void scrollDown() {
        if (scrollable==SCROLLABLE_DOWN || scrollable==SCROLLABLE_BOTH) {
            Vector lines=((PopUpElement)popUps.elementAt(0)).getMessage();
            if (lines.size()<1) return;
            startLine++;
        }
    }

    private void scrollUp() {
        if (scrollable==SCROLLABLE_UP || scrollable==SCROLLABLE_BOTH) {
            Vector lines=((PopUpElement)popUps.elementAt(0)).getMessage();
            if (lines.size()<1) return;
            startLine--;
        }
    }

    public boolean handlePressed(int keyCode) {
        handled = false;
        if (scrollable > -1) {
            switch (keyCode) {
                case 2:
                case 4:
                case VirtualCanvas.KEY_UP:
                case VirtualCanvas.KEY_LEFT:
                    scrollUp();
                    return handled = true;
                case 6:
                case 8:
                case VirtualCanvas.KEY_DOWN:
                case VirtualCanvas.KEY_RIGHT:
                    scrollDown();
                    return handled = true;
            }
        }
        if (keyCode == VirtualCanvas.KEY_GREEN) {
            return goToMsgList();
        }
        return handled = next();
    }
/*
    public boolean handleReleased(int keyCode) {
        if (!handled)
            return next();
        return popUps.isEmpty();
    }
*/

    public void clear() {
        if(size()>0)
            popUps.removeAllElements();
    }

    private void drawAllStrings(Graphics g, int x, int y) {
        Vector lines=((PopUpElement)popUps.elementAt(0)).getMessage();
        if (lines.size()<1) return;

        int fh=getFontHeight();

        int pos=0;

        for (Enumeration stringLine=lines.elements(); stringLine.hasMoreElements(); ) {
            String str=(String)stringLine.nextElement();
            if (pos>=startLine) {
                FontCache.drawString(g,str, x, y, Graphics.TOP|Graphics.LEFT);
                y += fh;
            }
            pos++;
        }
    }

    private int getFontHeight() {
        return font.getHeight();
    }

    private int getHeight() {
        Vector message=((PopUpElement)popUps.elementAt(0)).getMessage();

        return getFontHeight()*(message.size()-startLine);
    }

    private int getStrWidth(String string) {
        return font.stringWidth(string);
    }

    private int getMaxWidth() {
        Vector lines=((PopUpElement)popUps.elementAt(0)).getMessage();

        int length=0;

        if (lines.size()<1) return length;

        for (int line=0; line<lines.size(); ) {
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
                return ColorTheme.getColor(ColorTheme.POPUP_SYSTEM_INK);
            case TYPE_MESSAGE:
                return ColorTheme.getColor(ColorTheme.POPUP_MESSAGE_INK);
        }
        return COLOR_ALERT_INK;
    }

    private int getColorBgnd() {
        int type=((PopUpElement)popUps.elementAt(0)).getType();
        switch (type) {
            case TYPE_SYSTEM:
                return ColorTheme.getColor(ColorTheme.POPUP_SYSTEM_BGND);
            case TYPE_MESSAGE:
                return ColorTheme.getColor(ColorTheme.POPUP_MESSAGE_BGND);
        }
        return COLOR_ALERT_BGND;
    }

//paint
    //private static int[] alphaBuffer = null;

/*    private void fillSemiTransRect(Graphics graph, int color, int alpha, int xPos, int yPos, int rectWidth, int rectHeight) {
        int r1 = ((color & 0xFF0000) >> 16);
        int g1 = ((color & 0x00FF00) >> 8);
        int b1 = (color & 0x0000FF);

        int col = (r1 << 16) | (g1 << 8) | (b1) | (alpha << 24);

        int[] alphaBuffer = new int[rectWidth*rectHeight];

        for(int i = 0; i < alphaBuffer.length; i++)
          alphaBuffer[i] = col;

        //Image img = Image.createImage(rectWidth, rectHeight);

        graph.drawRGB(alphaBuffer, 0, rectWidth, xPos, yPos, rectWidth, rectHeight, true);

        alphaBuffer = null;
    }
*/
    public void paintCustom(Graphics graph) {
        if(size()<1)
            return;
        scrollable=(startLine>0)?SCROLLABLE_UP:SCROLLABLE_NONE;

        maxWdth=getMaxWidth();

        popUpWidth=(maxWdth>(width-border))?width-border:maxWdth+padding;
        widthBorder=(maxWdth>popUpWidth)?border/2:(width-popUpWidth)/2;

        int stringsHeight=getHeight();

        if (stringsHeight>height) {
            scrollable=(startLine>0)?SCROLLABLE_BOTH:SCROLLABLE_DOWN;

            heightBorder=0;
            popUpHeight=height;
        } else {
            popUpHeight=stringsHeight+padding;
            heightBorder=(height-popUpHeight)/2;
        }

        //graph.translate(widthBorder, heightBorder);

        //graph.setClip(0,0,popUpWidth+1,popUpHeight+1);

/*        int alpha = 200;

        if (alpha<255) {
            fillSemiTransRect(graph, getColorBgnd(), alpha, widthBorder+1, heightBorder+1, popUpWidth-1, popUpHeight-1);
        } else {*/

            graph.setColor(getColorBgnd());
            graph.fillRect(widthBorder+1,heightBorder+1,popUpWidth-1,popUpHeight-1);             //fill
        //}

        graph.setColor(getColorInk());
        graph.drawRect(widthBorder,heightBorder,popUpWidth,popUpHeight);                 //border

        graph.setFont(font);
        switch (scrollable) {
            case SCROLLABLE_UP:
                ri.drawImage(graph, RosterIcons.ICON_SCROLLABLE_UP, widthBorder+maxWdth-ri.getWidth(), heightBorder+popUpHeight-ri.getHeight());
                break;
            case SCROLLABLE_BOTH:
                ri.drawImage(graph, RosterIcons.ICON_SCROLLABLE_BOTH, widthBorder+maxWdth-ri.getWidth(), heightBorder+popUpHeight-ri.getHeight());
                break;
            case SCROLLABLE_DOWN:
                ri.drawImage(graph, RosterIcons.ICON_SCROLLABLE_DOWN, widthBorder+maxWdth-ri.getWidth(), heightBorder+popUpHeight-ri.getHeight());
                break;
        }

        drawAllStrings(graph, widthBorder+2, heightBorder+3);

        //graph.translate(-widthBorder, -heightBorder);
        //graph.setClip(0,0,width,height);
    }

    public int size() {
        return popUps.size();
    }
//paint

    static class PopUpElement {
        private int type;
        private String from;
        private Vector message;

        public PopUpElement(int type, String from, Vector message) {
            this.from=from;
            this.type=type;
            this.message=message;
        }

        public int getType() { return type; }
        public Vector getMessage() { return message; }
        public String getContact() { return from; }
    }
}

