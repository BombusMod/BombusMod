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
import images.SmilesIcons;
import locale.SR;
import Colors.ColorTheme;
import ui.*;
import java.util.Vector;
import ui.controls.Balloon;

//#ifndef MENU_LISTENER
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Command;
//#else
//# import Menu.MenuListener;
//# import Menu.Command;
//#endif

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.TextBox;

public class SmilePicker 
        extends VirtualList 
        implements 
//#ifndef MENU_LISTENER
        CommandListener,
//#else
//#         MenuListener,
//#endif
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
    
    Command cmdCancel=new Command(SR.MS_CANCEL,Command.BACK,99);
    Command cmdOk=new Command(SR.MS_SELECT,Command.OK,1);
     
    private Vector smileTable;

    private TextBox t;
 
     /** Creates a new instance of SmilePicker */
    public SmilePicker(Display display, Displayable pView, int caretPos, TextBox tbox) {
         super(display);
         this.caretPos=caretPos;

         this.t=tbox;
         
         il = SmilesIcons.getInstance();
//#ifdef SMILES 
        smileTable=MessageParser.getInstance().getSmileTable();
//#endif
        
        imgCnt=smileTable.size();
        
        realWidth=getWidth()-scrollbar.getScrollWidth();
        
        imgWidth=il.getWidth()+(CURSOR_HOFFSET*2);
        lineHeight = il.getHeight()+(CURSOR_VOFFSET*2);

        xCnt= realWidth / imgWidth;
        
        lines=imgCnt/xCnt;
        xLastCnt=imgCnt-lines*xCnt;
        if (xLastCnt>0) lines++; else xLastCnt=xCnt;

        xBorder=(realWidth-(xCnt*imgWidth))/2;

        commandState();
        this.parentView=pView;
    }
    
    public void commandState() {
//#ifdef MENU_LISTENER
//#         menuCommands.removeAllElements();
//#endif
        addCommand(cmdOk);
        addCommand(cmdCancel);
        
        setCommandListener(this);
    }
    
    int lineIndex;
    
    public int getItemCount(){ return lines; }
    public VirtualElement getItemRef(int index){ lineIndex=index; return this;}

    public int getVWidth(){ return 0; }
    public int getVHeight() { return lineHeight; }
    public int getColor(){ return ColorTheme.getColor(ColorTheme.LIST_INK); }
    public int getColorBGnd(){ return ColorTheme.getColor(ColorTheme.LIST_BGND); }
    public void onSelect(){
        try {
//#ifdef RUNNING_MESSAGE
//#             StaticData.getInstance().roster.me.insert( getTipString() , caretPos);
//#else
            t.insert(getTipString() , caretPos);
//#endif
        } catch (Exception e) { /*e.printStackTrace();*/  }
        destroyView();
    }
    
        
    public void drawItem(Graphics g, int ofs, boolean selected){
        int max=(lineIndex==lines-1)? xLastCnt:xCnt;
        for (int i=0;i<max;i++) {
            il.drawImage(g, lineIndex*xCnt + i, xBorder+(i*imgWidth+CURSOR_HOFFSET), CURSOR_VOFFSET);
        }
    }

    public void drawCursor (Graphics g, int width, int height){
        int x=xBorder+(xCursor*imgWidth);
        g.setColor(getColorBGnd());
        g.fillRect(0,0,width, height);
        g.translate(x,0);
        super.drawCursor(g, imgWidth, lineHeight);
        g.translate(-x,0);
    } 

    protected void drawBalloon(final Graphics g, int balloon, final String text) {
        if (cursor==0) balloon+=lineHeight+Balloon.getHeight();
        int x=xBorder+(xCursor*imgWidth);
        g.translate(x, balloon);
        Balloon.draw(g, text);
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
    
    public void commandAction(Command c, Displayable d){
        if (c==cmdCancel) {
            destroyView();
            return;
        }
        if (c==cmdOk) { eventOk(); }
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

    protected void pointerPressed(int x, int y) { 
        super.pointerPressed(x,y);
        if (x>=xCnt*imgWidth) return;
        xCursor=x/imgWidth;
        setRotator();
        if (cursor!=lines-1) return;
        if (xCursor >= xLastCnt) xCursor=xLastCnt-1;
    }
    
    public void userKeyPressed(int keyCode) {
        switch (keyCode) {
            case KEY_NUM3 :
                super.pageLeft(); keyDwn(); break;
            case KEY_NUM9:
                super.pageRight(); break;
            case KEY_NUM4:
                pageLeft(); break;
            case KEY_NUM6:
                pageRight(); break;
        }
        super.userKeyPressed(keyCode);
    }

    public boolean isSelectable() { return true; }
    
    public boolean handleEvent(int keyCode) { return false; }
    
//#ifdef MENU_LISTENER
//#     public void showMenu(){ eventOk(); }
//#      
//#     public String touchLeftCommand(){ return SR.MS_SELECT; }
//#     public String touchRightCommand(){ return SR.MS_BACK; }
//#endif
}
