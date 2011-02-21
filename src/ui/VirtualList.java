/*
 * VirtualList.java
 *
 * Created on 30.01.2005, 14:46
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
 *
 */

package ui;
import Colors.ColorTheme;

import Fonts.FontCache;
import javax.microedition.lcdui.*;
import Client.*;
import locale.SR;
//#ifdef POPUPS
import ui.controls.PopUp;
//#endif
import ui.controls.Balloon;
import ui.controls.Progress;
import ui.controls.ScrollBar;
import util.StringUtils;
import java.util.Vector;

import ui.controls.CommandsPointer;
import Menu.MenuCommand;

/**
 * Вертикальный список виртуальных элементов.
 * класс реализует управление списком, скроллбар,
 * вызов отрисовки отображаемых на экране элементов.
 * @author Eugene Stahov
 */
public abstract class VirtualList {

    
    /**
     * событие "Курсор выделил элемент"
     * в классе VirtualList вызываемая функция не выполняет действий, необходимо
     * переопределить (override) функцию для реализации необходимых действий
     * @param index индекс выделенного элемента
     */
    public void focusedItem(int index) { }

    /**
     * число элементов виртуального списка
     * эта функция абстрактная, должна быть переопределена при наследовании
     * @return число элементов списка, исключая заголовок
     */
    abstract protected int getItemCount();

    /**
     * элемент виртуального списка
     * эта функция абстрактная, должна быть переопределена при наследовании
     * @param index номер элемента списка. не превосходит значение, возвращённое getItemCount()
     * @return ссылка на элемент с номером index.
     */
    abstract public VirtualElement getItemRef(int index);

    protected int getMainBarBGnd() { return ColorTheme.getColor(ColorTheme.BAR_BGND);} 
    protected int getMainBarBGndBottom() { return ColorTheme.getColor(ColorTheme.BAR_BGND_BOTTOM);}
    
    protected StaticData sd=StaticData.getInstance();

    private int stringHeight;
    
    private int iHeight;
    private int mHeight;
    
//#ifdef GRADIENT
//#     Gradient grIB;
//#     Gradient grMB;
//#endif

    public static int panelsState = 2;

    private static boolean reverse=false;
    private static boolean paintTop=true;
    private static boolean paintBottom=true;

    public static int phoneManufacturer;
    
    public static void changeOrient(int newOrient) {
        panelsState=newOrient;
        switch (panelsState) {
            case 0: paintTop=false; paintBottom=false; reverse=false; break;
            case 1: paintTop=true;  paintBottom=false; reverse=false; break;
            case 2: paintTop=true;  paintBottom=true;  reverse=false; break;
            case 3: paintTop=false; paintBottom=true;  reverse=false; break;
            case 4: paintTop=true;  paintBottom=false; reverse=true;  break;
            case 5: paintTop=true;  paintBottom=true;  reverse=true;  break;
            case 6: paintTop=false; paintBottom=true;  reverse=true;  break;
        }
        // TODO: prevent hide command bar on touch screen device
      /*  if (Config.fullscreen && hasPointerEvents()) {
            paintBottom = !reverse;
            paintTop = reverse;
        }*/
    }
    
//#ifdef POPUPS
    public void setWobble(int type, String contact, String txt) {
        PopUp.getInstance().addPopup(type, contact, txt);
        redraw();
    }
//#endif
    protected int getMainBarRGB() {return ColorTheme.getColor(ColorTheme.BAR_INK);}
    
    protected Config cf=Config.getInstance();

    /**
     * событие "Нажатие кнопки ОК"
     * базовая реализация VirtualList вызывает функцию onSelect для выбранного элемента;
     * необходимо переопределить (override) функцию для реализации желаемых действий
     */
    public void eventOk() {
        try {
            ((VirtualElement)getFocusedObject()).onSelect();
            updateLayout();
            fitCursorByTop();
            redraw();
        } catch (Exception e) {} 
    }
    
    public void eventLongOk() {
//#ifdef TEST
//#         drawTest = true;
//#endif
    }

    public void userKeyPressed(int keyCode){
        switch(keyCode) {
            case 1:
            case VirtualCanvas.KEY_VOL_UP:
                moveCursorHome();
                redraw();
                return;
            case 2:
                keyUp();
                redraw();
                return;
            case 8:
                keyDwn();
                redraw();
                return;
            case 4:
                pageLeft();
                redraw();
                return;
            case 6:
                pageRight();
                redraw();
                return;            
            case 5:
                eventOk();
                return;
            case 7:
            case VirtualCanvas.KEY_VOL_DOWN:
                moveCursorEnd();
                redraw();
                return;
            case VirtualCanvas._KEY_STAR:
//#ifdef POPUPS
                showTimeTrafficInfo();
//#endif
                return;
            case VirtualCanvas._KEY_POUND:
//#ifdef POPUPS
                showInfo();
//#endif
                return;
        }
    }

    public void longKey(int keyCode) {};
//#ifdef MEMORY_USAGE
//#     public static boolean memMonitor;
//#endif
    public static boolean showTimeTraffic = true;
    
    public boolean canBack = true;

    /** метрика экрана */
    int width;
    int height;
    
    private int cursor;

    /**
     * окно приклеено к позиции курсора
     * ПРИКЛЕИВАЕТСЯ:
     *   при нажатии кнопок перемещения курсора
     *   при выборе стилусом элемента списка
     * ОТКЛЕИВАЕТСЯ:
     *   при использовании скролбара
     */
    protected boolean stickyWindow=true;
    
    private int itemLayoutY[]=new int[1];
    private int listHeight;

    private int list_top;
    private int list_bottom;
    
//#ifdef BACK_IMAGE
//#     public Image img;
//#endif
    
    protected synchronized void updateLayout() {
        int size = getItemCount();
        if (size == 0) {
            listHeight = 0;
            return;
        }
        int layout[]=new int[size+1];
        int y=0;
        for (int index = 0; index < size; index++) {
            VirtualElement item = getItemRef(index);
            if (item != null) {
                y += item.getVHeight();
                layout[index + 1] = y;
            }
        }
        listHeight=y;
        itemLayoutY=layout;
    }
    public int getListHeight() {
        return winHeight;
    }
        
    protected int getElementIndexAt(int yPos) {
        int end = getItemCount() - 1;
        if (end < 0) {
            return -1;
        }
        int begin = 0;
        while (end - begin > 1) {
            int index = (end + begin) >> 1;
            if (itemLayoutY.length <= index) {
                index = itemLayoutY.length - 1;
            }
            if (yPos < itemLayoutY[index]) {
                end = index;
            } else {
                begin = index;
            }
        }
        return (yPos < itemLayoutY[end]) ? begin : end;
    }
    
    public int win_top; // верхняя граница окна относительно списка
    private int winHeight; // отображаемый размер списка
    
    protected int offset; // счётчик автоскроллинга
    
    protected boolean showBalloon;
    
    protected VirtualElement mainbar;
    protected VirtualElement infobar;
    
    private boolean wrapping = true;

    public static int startGPRS=-1;
    public static int offGPRS=0;

    private int itemBorder[]; // TODO: remove

    /** обработка doubleclick */
    private int lastClickX;
    private int lastClickY;
    private int lastClickItem;
    private long lastClickTime;
    private int lastCursor;

    /**
     * Разрешает заворачивание списка в кольцо (перенос курсора через конец списка)
     * по умолчанию установлен true
     * @param wrap будучи переданным true, разрешает перенос курсора через конец списка
     */
    public void enableListWrapping(boolean wrap) { this.wrapping=wrap; }

    /**
     * ссылка на заголовок списка
     * @return объект типа ComplexString
     */
    public ComplexString getMainBarItem() {return (ComplexString)mainbar;}
    public void setMainBarItem(ComplexString mainbar) { this.mainbar=mainbar; }
    
    public ComplexString getInfoBarItem() {return (ComplexString)infobar;}
    public final void setInfoBarItem(ComplexString infobar) { this.infobar=infobar; }

//#ifdef ELF    
//#     private static boolean sie_accu=true;
//#     private static boolean sie_net=true;
//#endif

    /**
     * возвращает ссылку на объект в фокусе.
     * в классе VirtualList возвращает VirtualElement, на который указывает курсор,
     * однако, возможно переопределить функцию при наследовании
     * @return ссылка на объект в фокусе.
     */
    public Object getFocusedObject() { 
        try {
            return getItemRef(cursor);
        } catch (Exception e) { }
        return null;
    }    

    protected VirtualList parentView;

    protected ScrollBar scrollbar;
    
    /** Creates a new instance of VirtualList */
    public VirtualList() {       
       //setFullScreenMode(Config.fullscreen);
      /* if (phoneManufacturer != Config.MICROEMU) {
           width = sd.canvas.getWidth();
           height = sd.canvas.getHeight();
       }*/
       
//#ifdef POPUPS
        PopUp.getInstance();
//#endif
        
        changeOrient(cf.panelsState);

//        setFullScreenMode(fullscreen);

        itemBorder = new int[32]; // TODO: remove

        scrollbar=new ScrollBar();
        scrollbar.setHasPointerEvents(sd.canvas.hasPointerEvents());

        MainBar secondBar=new MainBar("", true, sd.canvas.hasPointerEvents() && cf.advTouch && Config.fullscreen);
        secondBar.addElement(null); //1
        secondBar.addRAlign();
        secondBar.addElement(null); //3
        setInfoBarItem(secondBar);

        stringHeight=FontCache.getFont(false, FontCache.roster).getHeight();
        
//#ifdef BACK_IMAGE
//#         try {
//#             if (img==null)
//#                 img=Image.createImage("/images/bg.png");
//#         } catch (Exception e) { }
//#endif
//#if USE_ROTATOR
        TimerTaskRotate.startRotate(0, this);
//#endif
    }
    
    public void show() {
        parentView = sd.canvas.getList();
        sd.canvas.show(this);
     }

    public void redraw() {
        if (sd.canvas.getList() == this) {
            sd.canvas.repaint();
            return;
         }
     }
      


    /** Вызывается при изменении размера отображаемой области. переопределяет наследуемый метод
     * Canvas.sizeChanged(int width, int heigth). сохраняет новые размеры области рисования.
     * также создаёт новый экранный буфер offscreen, используемый при работе без автоматической
     * двойной буферизации
     * @param w
     * @param h
     */
    protected void sizeChanged(int w, int h) {
        width = w;
        height = h;
        if (messagesWidth == 0)
            messagesWidth = getListWidth();
//#ifdef GRADIENT
//#         iHeight=0;
//#         mHeight=0;
//#endif
        redraw();
    }

    /**
     * начало отрисовки списка.
     * функция вызывается перед отрисовкой списка,
     * перед любыми обращениями к элементам списка.
     *     
     */
    protected void beginPaint() { }

    public void paint(Graphics g) {
        mHeight=0;
        iHeight=0;       
        
        
        beginPaint();
//#ifdef POPUPS
        PopUp.getInstance().init(g, width, height);
//#endif
        
        //StaticData.getInstance().screenWidth=width;

               
        
        updateLayout();
        
        setAbsOrg(g, 0,0);
        
        g.setColor(ColorTheme.getColor(ColorTheme.LIST_BGND));
        g.fillRect(0, 0, width, height);
        
//#ifdef BACK_IMAGE
//#         if (img!=null) {
//#             g.drawImage(img, width/2, height/2, Graphics.VCENTER|Graphics.HCENTER);
//#         }
//#endif
        
        if (mainbar!=null)
            mHeight=mainbar.getVHeight(); // nokia fix

        if (infobar!=null) {
            setInfo();
            iHeight=infobar.getVHeight(); // nokia fix
        }
        
        if (paintTop) {
            if (reverse) {
                if (infobar!=null) {
                    iHeight=infobar.getVHeight();                    
                    list_top = iHeight;
                    itemBorder[0] = list_top; // TODO: remove
                     drawInfoPanel(g);
                }
            } else {
                if (mainbar!=null) {                    
                    list_top = mHeight;
                    itemBorder[0] = list_top; // TODO: remove
                    drawMainPanel(g);
                }
            }
        }
        if (paintBottom) {
            if (reverse) {
                if (mainbar!=null) 
                    list_bottom=mHeight;
            } else {
                list_bottom=iHeight; 
            }
        }

        winHeight=height-list_top-list_bottom;

        int count=getItemCount();
        
        boolean scroll=(listHeight>winHeight);

        if (count==0) {
            cursor=(cursor==-1)?-1:0; 
            win_top=0;
        } else if (cursor>=count) {
            cursor=count-1;
            stickyWindow=true;
        }
        if (count>0 && stickyWindow) fitCursorByTop();
        
        int itemMaxWidth=(scroll) ?(width-scrollbar.getScrollWidth()) : (width);

        int itemIndex = win_top > 0 ? getElementIndexAt(win_top) : 0;
        int displayedIndex=0;
        int displayedBottom=list_top;
   
        int baloon=-1;
        int itemYpos;
        try {
            while ((itemYpos=itemLayoutY[itemIndex]-win_top)<winHeight) {
                
                VirtualElement el=getItemRef(itemIndex);
                
                boolean sel=(itemIndex==cursor);
                
                int lh=el.getVHeight();

                setAbsOrg(g, 0, list_top);
                g.setClip(0,0, itemMaxWidth, winHeight);    
                
                g.translate(0, itemYpos);
                
                g.setColor(el.getColorBGnd());

                if (sel) {
                    drawCursor(g, itemMaxWidth, lh); 
                    baloon=g.getTranslateY();
                } else {
//#ifdef BACK_IMAGE
//#                     if (img==null)
//#endif
                        g.fillRect(0,0, itemMaxWidth, lh); //clear field
                }
                g.setColor(el.getColor());
                
                g.clipRect(0, 0, itemMaxWidth, lh);
                el.drawItem(g, (sel)?offset:0, sel);
                
                itemIndex++;
		displayedBottom=list_top+itemYpos+lh;
                itemBorder[++displayedIndex] = displayedBottom; // TODO: remove
            }
        } catch (Exception e) { }

        int clrH=height-displayedBottom;

        if (clrH>0
//#ifdef BACK_IMAGE
//#                 && img==null
//#endif
                ) {
            setAbsOrg(g, 0,displayedBottom);
            g.setClip(0, 0, itemMaxWidth, clrH);
            g.setColor(ColorTheme.getColor(ColorTheme.LIST_BGND));
            g.fillRect(0, 0, itemMaxWidth, clrH);
        }

        if (scroll) {
//#ifdef MEMORY_USAGE
//#         int correct=(memMonitor)?1:0;
//#         setAbsOrg(g, 0, list_top+correct);
//#         g.setClip(0, 0, width, winHeight);
//#
//# 	    scrollbar.setPostion(win_top-correct);
//# 	    scrollbar.setSize(listHeight-correct);
//# 	    scrollbar.setWindowSize(winHeight-correct);
//#else
            setAbsOrg(g, 0, list_top);
            g.setClip(0, 0, width, winHeight);

	    scrollbar.setPostion(win_top);
	    scrollbar.setSize(listHeight);
	    scrollbar.setWindowSize(winHeight);
//#endif
	    
	    scrollbar.draw(g);
        } else scrollbar.setSize(0);

        setAbsClip(g, width, height);
//#ifdef MEMORY_USAGE
//#         drawHeapMonitor(g, list_top); //heap monitor
//#endif
        
        if (showBalloon) {
            if (cf.showBalloons) {
                String text = null;
                try {
                    text = ((VirtualElement) getFocusedObject()).getTipString();
                } catch (Exception e) {
                }
                if (text != null) {
                    drawBalloon(g, baloon, text);
                }
            }
        }

        if (paintBottom) {
            if (reverse) {
                if (mainbar!=null) {
                    setAbsOrg(g, 0, height-mHeight);
                    drawMainPanel(g);
                    if (sd.canvas.hasPointerEvents())
                        CommandsPointer.init(width, height, mHeight);
                }
            } else {
                if (infobar!=null) {
                    setAbsOrg(g, 0, height-iHeight);
                    drawInfoPanel(g);
                    if (sd.canvas.hasPointerEvents())
                        CommandsPointer.init(width, height, iHeight);
                }
            }
            setAbsClip(g, width, height);

            if (sd.roster.messageCount>0) drawEnvelop(g);
            if (System.currentTimeMillis()-sd.getTrafficIn()<2000) drawTraffic(g, false);
            if (System.currentTimeMillis()-sd.getTrafficOut()<2000) drawTraffic(g, true);
        }
        
//#ifdef POPUPS
        setAbsClip(g, width, height);
        drawPopUp(g);
//#endif
        
        if (reconnectWindow.getInstance().isActive()) {
            if (reconnectTimeout>reconnectPos && reconnectPos!=0) {
                int strWidth=g.getFont().stringWidth(SR.MS_RECONNECT);
                int progressWidth=(width/3)*2;
                progressWidth=(strWidth>progressWidth)?strWidth:progressWidth;
                int progressX=(width-progressWidth)/2;
                if (pb==null) pb=new Progress(progressX, height/2, progressWidth);
                int popHeight=Progress.getHeight();
                g.setColor(ColorTheme.getColor(ColorTheme.POPUP_SYSTEM_BGND));
                g.fillRoundRect(progressX-2, (height/2)-(popHeight*2), progressWidth+4, (popHeight*2)+1, 6, 6);
                g.setColor(ColorTheme.getColor(ColorTheme.POPUP_SYSTEM_INK));
                g.drawRoundRect(progressX-2, (height/2)-(popHeight*2), progressWidth+4, (popHeight*2)+1, 6, 6);
                FontCache.drawString(g,SR.MS_RECONNECT, width/2, (height/2)-(popHeight*2), Graphics.TOP | Graphics.HCENTER);
                Progress.draw(g, reconnectPos*progressWidth/reconnectTimeout, reconnectString);
            }
        }    
        
    }

    private static int reconnectPos=0;
    private static int reconnectTimeout=0;
    public static boolean reconnectRedraw=false;
    private static String reconnectString="";
    
    private Progress pb;
    public static void drawReconnect(int pos, int timeout, String reconnect) {
        reconnectPos=pos;
        reconnectTimeout=timeout;
        reconnectRedraw=true;
        reconnectString=reconnect;
    }

    private void drawEnvelop(final Graphics g) {
        g.setColor(getMainBarRGB());
        int wpos= (width/2);
        int hpos= height-13;
        
        g.drawRect(wpos-4,	hpos, 	8, 	6);
        g.drawLine(wpos-3,	hpos+1,	wpos,	hpos+4);
        g.drawLine(wpos,	hpos+4,	wpos+3,	hpos+1);
        g.drawLine(wpos-3,	hpos+5,	wpos-2,	hpos+4);
        g.drawLine(wpos+2,	hpos+4,	wpos+3,	hpos+5);
    }
    
    private void drawTraffic(final Graphics g, boolean up) {
        int pos=(up)?(width/2)+3:(width/2)-3;
        int pos2=(up)?height-4:height-2;
        
        //g.setColor((up)?0xff0000:0x00ff00);
        g.setColor(getMainBarRGB());
        g.drawLine(pos, height-5, pos, height-1);
        g.drawLine(pos-1, pos2, pos+1, pos2);       
        g.fillRect(pos-2, height-3, 1, 1);
        g.fillRect(pos+2, height-3, 1, 1);
    }
    
//#ifdef POPUPS
    protected void drawPopUp(final Graphics g) {
        PopUp.getInstance().paintCustom(g);
    }
//#endif
    
    private void setAbsClip(final Graphics g, int w, int h) {
        setAbsOrg(g, 0, 0);
        g.setClip(0,0, w, h);
    }
    
    protected void drawBalloon(final Graphics g, int balloon, final String text) {
        setAbsOrg(g,0,balloon);
        Balloon.draw(g, text);
    }
//#ifdef MEMORY_USAGE
//#     private void drawHeapMonitor(final Graphics g, int y) {
//#         if (memMonitor) {
//#             int ram=(int)((Runtime.getRuntime().freeMemory()*width)/Runtime.getRuntime().totalMemory());
//#             g.setColor(ColorTheme.getColor(ColorTheme.HEAP_TOTAL));  g.fillRect(0,y,width,1);
//#             g.setColor(ColorTheme.getColor(ColorTheme.HEAP_FREE));  g.fillRect(0,y,ram,1);
//#         }
//#     }
//#endif
    private void drawInfoPanel (final Graphics g) {
        int h=infobar.getVHeight()+1;

        g.setClip(0,0, width, h);
//#ifdef GRADIENT
//#         if (getMainBarBGnd()!=getMainBarBGndBottom() && h > 1) {
//#             if (iHeight!=h) {
//#                 grIB=new Gradient(0, 0, width, h, getMainBarBGnd(), getMainBarBGndBottom(), false);
//#                 iHeight=h;
//#             }
//#             grIB.paint(g);
//#         } else {
//#             g.setColor(getMainBarBGnd());
//#             g.fillRect(0, 0, width, h);
//#         }
//#else
            g.setColor(getMainBarBGnd());
            g.fillRect(0, 0, width, h);
//#endif
        g.setColor(getMainBarRGB());
        ((MainBar)infobar).lShift = (Config.getInstance().phoneManufacturer == Config.NOKIA && reverse && Config.fullscreen);
        ((MainBar)infobar).rShift = (Config.getInstance().phoneManufacturer == Config.SONYE && reverse && Config.fullscreen);
        infobar.drawItem(g, 0, false);

    }
    
    private void drawMainPanel (final Graphics g) {    
        int h=mainbar.getVHeight()+1;
        g.setClip(0,0, width, h);
//#ifdef GRADIENT
//#         if (getMainBarBGnd()!=getMainBarBGndBottom() && h > 1) {
//#             if (mHeight!=h) {
//#                 grMB=new Gradient(0, 0, width, h, getMainBarBGndBottom(), getMainBarBGnd(), false);
//#                 mHeight=h;
//#             }
//#             grMB.paint(g);
//#         } else {
//#             g.setColor(getMainBarBGnd());
//#             g.fillRect(0, 0, width, h);
//#         }
//#else
        g.setColor(getMainBarBGnd());
        g.fillRect(0, 0, width, h);
//#endif
        g.setColor(getMainBarRGB());
        ((MainBar)mainbar).lShift = (Config.getInstance().phoneManufacturer == Config.NOKIA && !reverse && Config.fullscreen);
        ((MainBar)mainbar).rShift = (Config.getInstance().phoneManufacturer == Config.SONYE && !reverse && Config.fullscreen);
        mainbar.drawItem(g, 0, false);
    }
    

    /**
     * перенос координат (0.0) в абсолютные координаты (x,y)
     * @param g графический контекст отрисовки
     * @param x абсолютная x-координата нового начала координат
     * @param y абсолютная y-координата нового начала координат
     */
    public static void setAbsOrg(Graphics g, int x, int y){
        g.translate(x-g.getTranslateX(), y-g.getTranslateY());
    }

    /** перемещение курсора в начало списка */
    public void moveCursorHome(){
        stickyWindow=true;
        if (cursor>0) cursor=getNextSelectableRef(-1);
        setRotator();
    }

    public void moveCursorEnd(){
        stickyWindow=true;
        int count=getItemCount();
        if (cursor>=0) cursor=(count==0)?0:count-1;
        redraw();
        setRotator();
        
    }

    public void moveCursorTo(int index){
        int count = getItemCount();
        if (count == 0)
            return;
        if (index <= 0)
            index = 0;
        else if (index >= count)
            index = count - 1;
        VirtualElement item = getItemRef(index);
        if (item != null && item.isSelectable())
            cursor = index;
        
        stickyWindow=true;
        redraw();
        //setRotator();
    }
    
    protected void fitCursorByTop(){
        try {
            int top=itemLayoutY[cursor];
            if (top<win_top) win_top=top;   
            if (((VirtualElement)getFocusedObject()).getVHeight()<=winHeight) {
                int bottom=itemLayoutY[cursor+1]-winHeight;
                if (bottom>win_top) win_top=bottom;  
            }
            if (top>=win_top+winHeight) win_top=top; 
        } catch (Exception e) { }
    }
    
    protected void fitCursorByBottom(){
        try {
            int bottom=itemLayoutY[cursor+1]-winHeight;
            if (bottom>win_top) win_top=bottom;
            if (((VirtualElement)getFocusedObject()).getVHeight()<=winHeight) {
                int top=itemLayoutY[cursor];
                if (top<win_top) win_top=top;
            }
            if (itemLayoutY[cursor+1]<=win_top) win_top=bottom;
        } catch (Exception e) {}
    }

    private byte key_long_executed; // 0 - not try, 1 - not executed, 2 - executed.
/*
    protected final void keyRepeated(int keyCode) {
//#ifdef DEBUG
//#         System.out.println("keyRepeated: " + keyCode);
//#endif

        if ((key_long_executed < 1) && UserKeyExec.getInstance().isCurrentKey(keyCode, false)) {
            key_long_executed = (byte) (sendKeyAction(keyCode, true, false) ? 2 : 1);
        }

        if (key_long_executed == 1) {
            sendKeyAction(keyCode, false, true);
        }

//#ifdef LIGHT_CONFIG      
//#ifdef PLUGINS                
//#         if (StaticData.getInstance().lightConfig)
//#endif            
//#             CustomLight.keyPressed();
//#endif
//#ifdef AUTOSTATUS
//#     sd.roster.userActivity();
//#endif
    }

    protected final void keyReleased(int keyCode) {
//#ifdef DEBUG
//#         System.out.println("keyReleased: " + keyCode);
//#endif
//#ifdef AUTOSTATUS
//#     sd.roster.userActivity();
//#endif
    }
*//*
    protected void keyPressed(int keyCode) {
//#ifdef DEBUG
//#         System.out.println("keyPressed: " + keyCode);
//#endif

        key_long_executed = 0;

        UserKeyExec.getInstance().update_current_key(keyCode, false);

        sendKeyAction(keyCode, false, true);*/
		
    protected void keyPressed(int keyCode) {

        // workaround for SE JP6 - enabling vibra in closed state
    /*    if (phoneManufacturer == Config.SONYE) {
            midlet.BombusMod.getInstance().setDisplayable((Displayable) null);
            try {
                 Thread.sleep(300);
            } catch (Exception ex) { }
            sd.canvas.show(this);
        }
*/
    }

    public void keyGreen() {}
    public void keyClear() {}
    

    private int yPointerPos;

    protected void pointerPressed(int x, int y) {
        int act = CommandsPointer.pointerPressed(x, y);
        if (act == 1) {
            touchLeftPressed();
            stickyWindow = false;
            return;
        } else if (act == 2) {
            touchRightPressed();
            stickyWindow = false;
            return;
        }
        yPointerPos = y;

        if (scrollbar.pointerPressed(x, y, this)) {
            stickyWindow = false;
            return;
        }
        

        if (y < list_top) {
            captionPressed();
            return;
        }
        if (y > list_top + winHeight) {
            return;
        }
        lastCursor = cursor;
        long clickTime = System.currentTimeMillis();

        int pos = getElementIndexAt(win_top + y - list_top);
        if (cursor >= 0 && cursor != pos) {
            moveCursorTo(pos);
            setRotator();
        }

        if (cursor != lastCursor) {
            if (cursor < itemLayoutY.length - 1) {
                // сделаем элемент максимально видимым
                int il = itemLayoutY[cursor + 1] - winHeight;
                if (il > win_top) {
                    win_top = il;
                }
                il = itemLayoutY[cursor];
                if (il < win_top) {
                    win_top = il;
                }
            }
        }
        
        lastClickTime = clickTime;
        lastClickX = x;
        lastClickY = y;
        lastClickItem = cursor;        

        redraw();
    }

    boolean itemDragged = false;

    protected void pointerDragged(int x, int y) {
        if (y < list_top) {
            return;
        }
        if (y > list_top + winHeight) {
            return;
        }
        if (scrollbar.pointerDragged(x, y, this)) {
            stickyWindow = false;
            return;
        }

        int dy = y - yPointerPos;

        if (Math.abs(dy) < 10) {
            stickyWindow = false;
            return;
        }

        itemDragged = true;

        yPointerPos = y;

        win_top -= dy;

        if (win_top + winHeight > listHeight) {
            win_top = listHeight - winHeight;
        }
        if (win_top < 0) {
            win_top = 0;
        }

        stickyWindow = false;
        if (cursor >= 0) {
            if (getItemCount() != 0) {
                int pos = getElementIndexAt(win_top + y - list_top);
                if ((pos >= 0) && getItemRef(pos).isSelectable()) {
                    cursor = pos;
                }
                setRotator();
            }
        }

        redraw();

    }

    protected void pointerReleased(int x, int y) {
//#ifdef POPUPS
        if (PopUp.getInstance().next()) {
            redraw();
            return;
        }
//#endif

        scrollbar.pointerReleased(x, y, this);

        if (Config.fullscreen) {
            if (CommandsPointer.pointerPressed(x, y) > 0) {
                return;
            }
        }

        if (y > list_top + winHeight) {
            return;
        }
        if (!itemDragged) {            

            long clickTime = System.currentTimeMillis();

            long dTime = clickTime - lastClickTime;
            if (dTime > 500 && dTime < 5000) {
                eventLongOk();
            } else if (dTime <= 200) {
                if (cursor == lastCursor || cf.advTouch) {                    
                    eventOk();
                }
            }
            lastClickTime = clickTime;
            y = 0;
        }
        itemDragged = false;
}
    
    public void reconnectYes() {
        reconnectWindow.getInstance().reconnect();
        //reconnectDraw=false;
        //redraw(); // Need?
    }
    
    public void reconnectNo() {
        reconnectWindow.getInstance().stopReconnect();
        //reconnectDraw=false;
        //redraw(); // Need?
    }

    /**
     * событие "Нажатие кнопки UP"
     * в классе VirtualList функция перемещает курсор на одну позицию вверх.
     * возможно переопределить (override) функцию для реализации необходимых действий
     */
    public void keyUp() {
        if (getItemCount()==0)
            return;
//#ifdef DEBUG
//# 	//System.out.println("keyUp");
//#endif
        if (cursor==0 || (!getItemRef(0).isSelectable() && cursor == 1)) {
            if (wrapping) {
                if (getItemRef(getItemCount()-1).isSelectable())
                    moveCursorEnd();
                else
                    moveCursorTo(getItemCount()-2);
            } else {
                itemPageUp();
            }
            setRotator();
            return;
        }

        if (itemPageUp()) return;
        //stickyWindow=true;
        if (getItemRef(cursor-1).isSelectable())
            cursor--;
        else
            cursor=getPrevSelectableRef(cursor);
        fitCursorByBottom();
        setRotator();
    }

    public void keyDwn() { 
        if (getItemCount()==0)
            return;
//#ifdef DEBUG
//#         //System.out.println("keyDwn");
//#endif
	if (cursor==(getItemCount()-1)) {
            if (wrapping) {
                moveCursorHome();
            } else {
                itemPageDown();
            }
            setRotator();
            return; 
        }
        if (itemPageDown()) {
            return;
        }
        stickyWindow=true;
        if (getItemRef(cursor+1).isSelectable()) {
            cursor++;
        } else {
            cursor=getNextSelectableRef(cursor);
        }
        setRotator();
    }
    
    public int getPrevSelectableRef(int curRef) {
        int prevRef=curRef;
        boolean process=true;
        while (process) {
            prevRef--;
            if (getItemRef(prevRef).isSelectable())
                break;
            if (prevRef==0 && wrapping)
                prevRef=getItemCount();
        }
        
        return prevRef;
    }

    public int getNextSelectableRef(int curRef) {
        if (getItemCount() == 0)
            return 0;
        int nextRef=curRef;
        boolean process=true;
        while (process) {
            nextRef++;
            if (nextRef==getItemCount() && wrapping)
                nextRef=0;
            if (getItemRef(nextRef).isSelectable())
                break;
        }
        
        return nextRef;
    }

    private boolean itemPageDown() {
        try {
            stickyWindow=false;
            if (((VirtualElement)getFocusedObject()).getVHeight()<=winHeight) {
                stickyWindow=true;
                return false;
            }

            if (!cursorInWindow()) {
                return false;
            }
            
            int remainder=itemLayoutY[cursor+1]-win_top;
            if (remainder<=winHeight) {
                return false;
            }
            if (remainder<=2*winHeight) {
                win_top=remainder-winHeight+win_top+8;
                return true;
            }
            win_top+=winHeight-stringHeight;//-stringHeight;
            return true;
        } catch (Exception e) {}
        return false;
    }
    
    private boolean itemPageUp() {
        try {
            stickyWindow=false;
            if (((VirtualElement)getFocusedObject()).getVHeight()<=winHeight) {
                //stickyWindow=true;
                return false;
            }

            if (!cursorInWindow()) { return false; }
            
            int remainder=win_top-itemLayoutY[cursor];
            if (remainder<=0) return false;
            if (remainder<=winHeight) {
                win_top=itemLayoutY[cursor];
                return true;
            }
            win_top-=winHeight-stringHeight;//-stringHeight;
            return true;
        } catch (Exception e) {}
        return false;
    }

    public void pageLeft() {
        if (getItemCount()==0)
            return;
//#ifdef DEBUG
//#         //System.out.println("keyLeft");
//#endif
        try {
            stickyWindow=false;
            win_top-=winHeight;
            if (win_top<0) {
                win_top=0;
                //if (!getItemRef(0).isSelectable()) cursor=getNextSelectableRef(-1); else cursor=0;
                cursor=getNextSelectableRef(-1);
            }
            if (!cursorInWindow()) {
                cursor=getElementIndexAt(itemLayoutY[cursor]-winHeight);
                if (((VirtualElement)getFocusedObject()).getVHeight()<=winHeight) 
                    fitCursorByTop();
            }
            setRotator();
        } catch (Exception e) { }
    }

    public void pageRight() {
        if (getItemCount()==0)
            return;
//#ifdef DEBUG
//#         //System.out.println("keyRight");
//#endif
        try {
            stickyWindow=false;
            win_top+=winHeight;
            int endTop=listHeight-winHeight;
            if (endTop<win_top) {
                win_top= (listHeight<winHeight)? 0 : endTop;
                int lastItemNum=getItemCount()-1;
                if (!getItemRef(lastItemNum).isSelectable())
                    cursor=getPrevSelectableRef(lastItemNum);
                else
                    cursor=lastItemNum;
            } else {
                if (!cursorInWindow()) {
                    cursor=getElementIndexAt(itemLayoutY[cursor]+winHeight);
                    if (((VirtualElement)getFocusedObject()).getVHeight()<=winHeight) 
                        fitCursorByTop();
                }
            }
            setRotator();
        } catch (Exception e) {}
    }
    
    public boolean cursorInWindow(){
        try {
            int y1=itemLayoutY[cursor]-win_top;
            int y2=itemLayoutY[cursor+1]-win_top;
            if (y1>=winHeight) return false;
            if (y2>=0) return true;
        } catch (Exception e) { }
        return false;
    }
    
    protected void setRotator() {
//#if (USE_ROTATOR)
        try {
            if (getItemCount() > 0)
                focusedItem(cursor);
            } catch (Exception e) {
//#ifdef DEBUG
//#             System.out.println("setRotator() in VirtialList in one try{} block catch exception:");
//#             System.out.println(e);
//#endif
            }

        int itemWidth = 0;
        try {
            if (cursor >= 0) {
                itemWidth = getItemRef(cursor).getVWidth();
                if (itemWidth >= width - scrollbar.getScrollWidth()) {
                    itemWidth -= width / 2;
                } else {
                    itemWidth = 0;
                }
            }
        } catch (Exception e) {
//#ifdef DEBUG
//#             System.out.println("setRotator() in VirtialList in two try{} block catch exception:");
//#             System.out.println(e);
//#endif
        }


        TimerTaskRotate.startRotate(itemWidth, this);
 //#endif
    }
    
    protected void drawCursor (Graphics g, int width, int height) {
        g.fillRect(0, 0, width, height);
        
        int cursorBGnd=ColorTheme.getColor(ColorTheme.CURSOR_BGND);
        int cursorOutline=ColorTheme.getColor(ColorTheme.CURSOR_OUTLINE);
        
        if (cursorBGnd!=0x010101) {
            g.setColor(ColorTheme.getColor(ColorTheme.CURSOR_BGND));
            g.fillRoundRect(0, 0, width, height, 6, 6);
            //fillSemiTransRect(g, ColorTheme.getColor(ColorTheme.CURSOR_BGND), 200, 1, 1, width-2, height-2);
        }

        if (cursorOutline!=0x010101) {
            g.setColor(cursorOutline);
            g.drawRoundRect(0, 0, width-1, height-1, 6, 6);
        }
    }
/*
    private void fillSemiTransRect(Graphics graph, int color, int alpha, int xPos, int yPos, int rectWidth, int rectHeight) {
        int r1 = ((color & 0xFF0000) >> 16);
        int g1 = ((color & 0x00FF00) >> 8);
        int b1 = (color & 0x0000FF);
        
        int col = (r1 << 16) | (g1 << 8) | (b1) | (alpha << 24);
        
        
        int[] alphaBuffer = new int[rectWidth*rectHeight];
        
        for(int i = 0; i < alphaBuffer.length; i++)
          alphaBuffer[i] = col;
        
        graph.drawRGB(alphaBuffer, 0, rectWidth, xPos, yPos, rectWidth, rectHeight, true);

        alphaBuffer = null;
    }
*/
    

    /**
     * отсоединение от менеджера дисплея текущего виртуального списка,
     * присоединение к менеджеру предыдущего Displayable
     */
    public void destroyView(){
        sd.roster.activeContact=null; 
        sd.canvas.show(parentView);       
    }

    public int getListWidth() {
        return width-scrollbar.getScrollWidth()-2;
    }
    public static int messagesWidth = 0;


    public static void sort(Vector sortVector){
        try {
            synchronized (sortVector) {
                int f, i;
                IconTextElement left, right;
                
                int j=sortVector.size();
                for (f = 1; f < j; f++) {
                    left=(IconTextElement)sortVector.elementAt(f);
                    right=(IconTextElement)sortVector.elementAt(f-1);
                    if ( left.compare(right) >=0 ) continue;
                    i = f-1;
                    while (i>=0){
                        right=(IconTextElement)sortVector.elementAt(i);
                        if (right.compare(left) <0) break;
                        sortVector.setElementAt(right,i+1);
                        i--;
                    }
                    sortVector.setElementAt(left,i+1);
                }
            }
        } catch (Exception e) {
//#ifdef DEBUG            
//#             e.printStackTrace(); /* ClassCastException */
//#endif            
        }
    }
    
    public int getCursor() {
        return cursor;
    }
    
    public void setInfo() {
        if (reconnectWindow.getInstance().isActive()) {
            getInfoBarItem().setElementAt(SR.MS_OK, 1);
            getInfoBarItem().setElementAt(SR.MS_CANCEL, 3);
            return;
        }
        if (Config.getInstance().phoneManufacturer == Config.NOKIA && !Config.fullscreen)
            showTimeTraffic = true;
        getInfoBarItem().setElementAt((!showTimeTraffic) ? touchLeftCommand() : Time.getTimeWeekDay(), 1);
        getInfoBarItem().setElementAt((!showTimeTraffic) ? touchRightCommand() : getTraffic(), 3);
    }

    public void showTimeTrafficInfo() {
//#ifdef POPUPS
        StringBuffer mem = new StringBuffer();
        mem.append(Time.localDate()).append(" ").append(Time.getTimeWeekDay())
           .append("\nTraffic: ")
           .append(getTraffic())
//#ifdef MEMORY_USAGE
//#            .append("\nFree: ")
//#            .append(Runtime.getRuntime().freeMemory()>>10)
//#            .append(" kb");
//#         if (phoneManufacturer == Config.SONYE)
//#             mem.append("\nTotal: ")
//#                .append(Runtime.getRuntime().totalMemory()>>10)
//#                .append(" kb")
//#endif
           ;
        setWobble(1, null, mem.toString());
//#endif
    }

    public static String getTraffic() {
        long traffic = StaticData.getInstance().traffic;
        return StringUtils.getSizeString((traffic>0)?traffic*2:0);
    }
    
    public final Vector menuCommands = new Vector();

    public void addMenuCommand(MenuCommand command) {
        if (menuCommands.indexOf(command) < 0)
            menuCommands.addElement(command);
    }

    public void removeMenuCommand(MenuCommand command) {
        menuCommands.removeElement(command);
    }

    protected void showMenu() { }

    public void touchLeftPressed() {
       if (reconnectWindow.getInstance().isActive())
           reconnectYes();
        else /*if (isHasMenu())
           showMenu();
        else if (menuCommands.size() > 0 && this instanceof Menu.MenuListener)
            ((Menu.MenuListener) this).menuAction((MenuCommand) menuCommands.elementAt(0), this);
        else*/
            cmdOk();
    }

    public void touchRightPressed() {
        if (reconnectWindow.getInstance().isActive())
            reconnectNo();
         else /*{
            int size = menuCommands.size();
            if (size > 1 && this instanceof Menu.MenuListener)
                ((Menu.MenuListener) this).menuAction((MenuCommand) menuCommands.elementAt(size - 1), this);
            else
                cmdCancel();
		 }*/
            cmdCancel();
    }

    public void captionPressed() { }

    public String touchLeftCommand() {
/*        if (isHasMenu())
		    return SR.MS_MENU;
        else if (menuCommands.size() > 0)
            return ((MenuCommand) menuCommands.elementAt(0)).getName();

        return SR.MS_OK;
*/
        return SR.MS_MENU;
    }

    public String touchRightCommand() {
/*        int size = menuCommands.size();
        if (size > 1)
            return ((MenuCommand) menuCommands.elementAt(size - 1)).getName();
        else*/ if (canBack)
            return SR.MS_BACK;

        return "";
    }

    public void cmdCancel() {
        if (canBack)
            destroyView();
    }


    public void cmdOk() { showMenu(); }
//    public void cmdOk() { }


    public void showInfo() {
//#ifdef POPUPS
        if (!cf.popUps)
            return;

        try {
            String text = ((VirtualElement) getFocusedObject()).getTipString();
            if (text != null) {
                setWobble(1, null, text);
            }
        } catch (Exception e) { }
//#endif
    }
}
