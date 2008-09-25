/*
 * ScrollBar.java
 *
 * Created on 19.11.2005, 21:26
 *
 * Copyright (c) 2005-2008, Eugene Stahov (evgs), http://bombus-im.org
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

import javax.microedition.lcdui.Graphics;
import Colors.ColorTheme;
//#ifdef GRADIENT
//# //import ui.Gradient;
//#endif
import ui.VirtualList;

/**
 *
 * @author EvgS
 */
public class ScrollBar {
    private static final int WIDTH_SCROLL_1      =4;
    private static final int WIDTH_SCROLL_2      =10;
    
    private int yTranslate;
    
    private int size;
    private int windowSize;
    private int position;
    
    private int scrollerX;
    
    private int drawHeight;
//#ifdef GRADIENT
//#     //private Gradient gr;
//#     //private int prevDrawHeight;
//#endif
    
    private int point_y;    // точка, за которую "держится" указатель
    
    private int scrollerSize;
    private int scrollerPos;
    
    private boolean hasPointerEvents;
    
    private int minimumHeight=3;
    private int scrollWidth=WIDTH_SCROLL_1;
    
    private int colorTop;
    //private int colorBottom;
    private int colorBar;
    private int colorBorder;
    
    /** Creates a new instance of ScrollBar */
    public ScrollBar() {
        point_y=-1;
        colorTop=ColorTheme.getColor(ColorTheme.SCROLL_BGND);
        //colorBottom=0xFFFFFF-colorTop;
        colorBar=ColorTheme.getColor(ColorTheme.SCROLL_BAR);
        colorBorder=ColorTheme.getColor(ColorTheme.SCROLL_BRD);
    }

    public void setWindowSize(int windowSize) {
        this.windowSize = windowSize;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getPostion() {
        return position;
    }

    public void setPostion(int postion) {
        this.position = postion;
    }

    public void setHasPointerEvents(boolean hasPointerEvents) {
        this.hasPointerEvents = hasPointerEvents;
	scrollWidth=(hasPointerEvents)? WIDTH_SCROLL_2: WIDTH_SCROLL_1;
    }

    public int getScrollWidth() {
        return scrollWidth;
    }

    public boolean pointerPressed(int x, int y, VirtualList v) {
	if (size==0) return false;
	if (x<scrollerX) return false; // not in area
	y-=yTranslate;
	if (y<scrollerPos) { 
            // page up
            int pos=position-windowSize;
            if (pos<0) pos=0;
            v.win_top=pos;
            v.repaint(); 
            return true; 
        } 
	if (y>scrollerPos+scrollerSize) { 
            int pos=position+windowSize;
            int listEnd=size-windowSize;
            v.win_top=(pos<listEnd)?pos:listEnd;
            v.repaint(); 
            return true; 
        } // page down
	point_y=y-scrollerPos;
	return true;
    }
    public boolean pointerDragged(int x, int y, VirtualList v) {
	y-=yTranslate;
	if (point_y<0) return false;
	int new_top=y-point_y;
	int new_pos=(new_top*size)/drawHeight;
	if ((position-new_pos)==0) return true;
	if (new_pos<0) new_pos=0;
	if (new_pos+windowSize>size) new_pos=size-windowSize;
	v.win_top=new_pos; v.repaint();
	return true;
    }
    public void pointerReleased(int x, int y, VirtualList v) { point_y=-1; }
    
    public void draw(Graphics g) {
	
	yTranslate=g.getTranslateY();
	
	drawHeight=g.getClipHeight();
	int drawWidth=g.getClipWidth();
	
	scrollerX=drawWidth-scrollWidth;

	g.translate(scrollerX, 0);

        g.setColor(colorTop);
	g.fillRect(1, 0, scrollWidth-2, drawHeight-1);
	
        g.setColor(colorBorder);
        g.drawLine(0, 0, 0, drawHeight-1);
        g.drawLine(scrollWidth-1, 0, scrollWidth-1, drawHeight-1);

	drawHeight-=minimumHeight;
        
	scrollerSize=(drawHeight*windowSize)/size+minimumHeight;
	
	scrollerPos=(drawHeight*position)/size;
        g.setColor(colorBar);
        g.fillRect(1, scrollerPos, scrollWidth-2, scrollerSize);
        
        g.setColor(colorBorder);
        
        g.drawLine(0, scrollerPos, scrollWidth-1, scrollerPos);
        g.drawLine(0, scrollerPos+scrollerSize, scrollWidth-1, scrollerPos+scrollerSize);
    }
}
