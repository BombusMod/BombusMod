/*
 * SmilePicker.java
 *
 * Created on 6.03.2005, 11:50
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
 *
 */

package Client;
import Messages.MessageParser;
import images.AniImageList;
import images.SmilesIcons;
import locale.SR;
import Colors.ColorTheme;
import images.AniIcon;
import ui.*;
import java.util.Vector;


import javax.microedition.lcdui.Graphics;
import ui.controls.form.DefForm;

public class SmilePicker 
        extends DefForm
        implements       
        VirtualElement
{

    private final static int CURSOR_HOFFSET=1;
    private final static int CURSOR_VOFFSET=1;
   
    private int imgCnt;
    private int xCnt;
    private int xLastCnt;
    private int xCursor;
    private int lines;

    private int lineHeight;
    private int imgWidth;
    
    private ImageList il;

    private int caretPos;
    
    private int realWidth=0;
    private int xBorder = 0;
    
    private Vector smileTable;

    private MessageEdit me;
 
    /** Creates a new instance of SmilePicker
     * @param caretPos
     * @param me 
     */
    public SmilePicker(int caretPos, MessageEdit me) {
         super(SR.MS_SELECT);
         this.caretPos=caretPos;

         this.me = me;
         
         il = SmilesIcons.getInstance();
//#ifdef SMILES 
        smileTable=MessageParser.getInstance().getSmileTable();
//#endif
        
        imgCnt=smileTable.size();
        
        realWidth=VirtualCanvas.getInstance().getWidth()-scrollbar.getScrollWidth();
        
        imgWidth=Math.max(il.getWidth()+(CURSOR_HOFFSET*2), cf.minItemHeight);
        lineHeight = il.getHeight()+(CURSOR_VOFFSET*2);

        xCnt= realWidth / imgWidth;
        
        lines=imgCnt/xCnt;
        xLastCnt=imgCnt-lines*xCnt;
        if (xLastCnt>0) lines++; else xLastCnt=xCnt;

        xBorder=(realWidth-(xCnt*imgWidth))/2;
        enableListWrapping(true);
    }
    
    int lineIndex;
    
    public int getItemCount(){ return lines; }
    public VirtualElement getItemRef(int index){ lineIndex=index; return this;}

    public int getVWidth(){ return 0; }
    public int getVHeight() { return Math.max(lineHeight, cf.minItemHeight); }
    public int getColor(){ return ColorTheme.getColor(ColorTheme.LIST_INK); }
    public int getColorBGnd(){ return ColorTheme.getColor(ColorTheme.LIST_BGND); }
    public void onSelect(){
        try {
            me.insert(getTipString() , caretPos);
        } catch (Exception e) { /*e.printStackTrace();*/  }
        destroyView();
    }   
    
    public void destroyView() {
        midlet.BombusMod.getInstance().setDisplayable(me.textbox);
    }

    public void drawItem(Graphics g, int ofs, boolean selected){
        int max=(lineIndex==lines-1)? xLastCnt:xCnt;   
        int x, iwidth;
        AniIcon item;
        for (int i=0;i<max;i++) {
            x = xBorder+(i*imgWidth+CURSOR_HOFFSET);            
            if (il instanceof AniImageList) {          
                item = ((AniImageList)il).iconAt(lineIndex*xCnt + i);
                iwidth = (item == null)? 0 : item.getWidth();
                x += (imgWidth - iwidth) >> 1;                                                       
            }
            il.drawImage(g, lineIndex*xCnt + i, x, CURSOR_VOFFSET);            
        }
    }

    protected synchronized int updateLayout() {
        int res = super.updateLayout();
        getMainBarItem().setElementAt(getTipString(), 0);
        return res;
    }

    public void drawCursor(Graphics g, int width, int height){
        int x=xBorder+(xCursor*imgWidth);

        g.setColor(getColorBGnd());
//#ifdef BACK_IMAGE        
//#         if (img == null)
//#endif
            g.fillRect(0,0,width, height);
        
        g.translate(x,0);
        super.drawCursor(g, imgWidth, lineHeight);
        g.translate(-x,0);
    } 
   
    
    public void pageLeft(){ 
        if (xCursor>0) 
            xCursor--; 
        else {
            if (cursor==0) {
                keyDwn();
                pageLeft();
                return;
            }
            xCursor=xCnt-1;
            keyUp();
            setRotator();
        }
    }
    public void pageRight(){ 
        if ( xCursor < ( (cursor<lines-1)?(xCnt-1):(xLastCnt-1) ) ) {
            xCursor++;
            setRotator();
        } else {
            if (cursor==lines-1) return;
            xCursor=0;
            keyDwn();
        }
    }
    public void keyDwn(){
        super.keyDwn();
        if (cursor!=lines-1)
            return;
        if (xCursor >= xLastCnt)
            xCursor=xLastCnt-1;
    }
   

    public void moveCursorEnd() {
        super.moveCursorEnd();
        xCursor=xLastCnt-1;
    }

    public void moveCursorHome() {
        super.moveCursorHome();
        xCursor=0;
    }

    public String getTipString() {
        return (String) smileTable.elementAt(cursor*xCnt+xCursor);
    }

    private void set_xCursor(int x) {
        if (x >= xCnt*imgWidth)
            return;
        xCursor = x/imgWidth;
        setRotator();
        if (cursor != lines-1)
            return;
        if (xCursor >= xLastCnt)
            xCursor=xLastCnt-1;
    }

    protected void pointerPressed(int x, int y) { 
        super.pointerPressed(x,y);
        set_xCursor(x);
        redraw();
    }

    protected void pointerDragged(int x, int y) { 
        super.pointerDragged(x,y);
        set_xCursor(x);
        redraw();
    }

    public void doKeyAction(int keyCode) {
        switch (keyCode) {
            case 3:
                super.pageLeft();
                keyDwn();
                break;
            case 9:
                super.pageRight();
                keyUp();
                break;            
        }
        super.doKeyAction(keyCode);
    }
    
    public boolean isSelectable() { return true; }
    
    public boolean handleEvent(int keyCode) { return false; }

    // TODO: fix this shit
    public String touchLeftCommand() { return SR.MS_SELECT; }
    public String touchRightCommand() { return cf.swapMenu ? "" : SR.MS_BACK; }

    public void touchLeftPressed() {
        eventOk();
    }
    public void touchRightPressed() {
        if (!cf.swapMenu)
            cmdCancel();
    }

    public void commandState() {}
}
