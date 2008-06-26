/*
 * MenuContainer.java
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

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import ui.Time;
import ui.VirtualList;

/**
 *
 * @author User
 */
public class MenuContainer {
    
    static Font boldFont = null;
    protected static int boldHeight = 0;
    
    //static Font normalFont = null;
    
    static int scWidth = 0;
    static int scHeight = 0;
    
    protected String leftCommand = "";
    protected String centerCommand = "";
    protected String rightCommand = "";
    
    private boolean initiated=false;

    private boolean hasPointerEvents;
    private int point_y; 
    private int point_x;
    //private int yTranslate;
    
    public MenuContainer() {
        point_y=-1;
        point_x=-1;
    }
/*
    public static String cutStringToWidth(String s, Font font, int width){
        String str = s;
        if(font.stringWidth(str)<width)
            return str;
        int threeDots = font.stringWidth("...");
        while(font.stringWidth(str)>width-threeDots)
            str = str.substring(0, str.length()-2);
        return str+"...";
    }
*/
    public static void drawFooter(Graphics g){
        g.setColor(0x000000);
        g.fillRect(0, scHeight-boldHeight, scWidth, boldHeight);
    }
    
    public static void drawCenterCommand(Graphics g, String val){
        g.setColor(0xffffff);
        g.setFont(getBoldFont());
        int tw = getBoldFont().stringWidth(val);
        g.drawString(val, scWidth/2, scHeight, Graphics.HCENTER | Graphics.BOTTOM);
    }
    
    public static void drawLeftCommand(Graphics g, String val){
        g.setColor(0xffffff);
        g.setFont(getBoldFont());
        int tw = getBoldFont().stringWidth(val);
        g.drawString(val, 2, scHeight, Graphics.LEFT | Graphics.BOTTOM);
    }
    
    public static void drawRightCommand(Graphics g, String val){
        g.setColor(0xffffff);
        g.setFont(getBoldFont());
        int tw = getBoldFont().stringWidth(val);
        g.drawString(val, scWidth-tw-2, scHeight, Graphics.LEFT | Graphics.BOTTOM);
    }
    
    public static Font getBoldFont() {
        return boldFont;
    }
    
    //public static Font getNormalFont() { return normalFont; }
    
    public int getHeight() {
        return boldHeight;
    }

    public String getLeftCommand() {
        return leftCommand;
    }
    
    private int getWidthLeftCommand() {
        if(!leftCommand.equals("")) 
            return getBoldFont().stringWidth(leftCommand);

        return 0;
    }

    public void setLeftCommand(String leftCommand) {
        this.leftCommand = leftCommand;
    }
    
    public String getCenterCommand() {
        return centerCommand;
    }
    
    private int getWidthCenterCommand() {
        if(!centerCommand.equals("")) 
            return getBoldFont().stringWidth(centerCommand);

        return 0;
    }

    public void setCenterCommand(String centerCommand) {
        this.centerCommand = centerCommand;
    }

    public String getRightCommand() {
        return rightCommand;
    }
    
    private int getWidthRightCommand() {
        if(!rightCommand.equals("")) 
            return getBoldFont().stringWidth(rightCommand);
        
        return 0;
    }

    private boolean isClickOnLeftCommand(int x, int y) {
        if (y<scHeight-boldHeight)
            return false;
        
        int cx=getWidthLeftCommand();
        if (cx==0) return false;
        
        if (x>cx) return false;

        return true;
    }
    
    private boolean isClickOnCenterCommand(int x, int y) {
        if (y<scHeight-boldHeight) return false;
        
        int cx=getWidthCenterCommand();
        if (cx==0)
            return false;
        int centerScreen=scWidth/2;
        
        int startX=centerScreen-(cx/2);
        int endX=centerScreen+(cx/2);

        if (x<startX) return false;
        if (x>endX) return false;

        return true;
    }
    
    private boolean isClickOnRightCommand(int x, int y) {
        if (y<scHeight-boldHeight)
            return false;
        
        int cx=getWidthRightCommand();
        if (cx==0) return false;
        
        if (x<(scWidth-cx)) return false;
 
        return true;
    }
    

    public void setRightCommand(String rightCommand) {
        this.rightCommand = rightCommand;
    }
    
    public void paint(Graphics g){
        scWidth = g.getClipWidth();
        scHeight = g.getClipHeight();
        
        if((!leftCommand.equals("")) || (!rightCommand.equals(""))){
            drawFooter(g);
            if(!leftCommand.equals(""))
                drawLeftCommand(g, leftCommand);
            
            if(!centerCommand.equals("")) {
                drawCenterCommand(g, centerCommand);
            } else {
                drawCenterCommand(g, Time.localTime());
            }
            
            if(!rightCommand.equals(""))
                drawRightCommand(g, rightCommand);
        }
    }
    
    public void draw (Graphics g) {
        if (!initiated)
            init(g);
        
        paint(g);        
    }

    public void init(Graphics g) {
        boldFont = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_SMALL);
        boldHeight = boldFont.getHeight();
        
        //normalFont = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL);

        initiated = true;
    }
    
/*           Pointer events              */
    
    public void setHasPointerEvents(boolean hasPointerEvents) {
        this.hasPointerEvents = hasPointerEvents;
    }
    
    public int pointerPressed(int x, int y, VirtualList v) {
        if (isClickOnLeftCommand(x, y)) {
            //System.out.println("isClickOnLeftCommand");
            return 1;
        }
        
        if (isClickOnCenterCommand(x, y)) {
            //System.out.println("isClickOnCenterCommand");
            return 2;
        }
        
        if (isClickOnRightCommand(x, y)) {
            //System.out.println("isClickOnRightCommand");
            return 3;
        }
        
	return 0;
    }
    
    
}