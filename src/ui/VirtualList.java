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
import javax.microedition.lcdui.Graphics;
import Client.*;
import locale.SR;
//#ifdef POPUPS
//# import ui.controls.PopUp;
//#endif
import ui.controls.ScrollBar;
import util.StringUtils;
import java.util.Vector;

import ui.controls.CommandsPointer;

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
//#     public void setWobble(int type, String contact, String txt) {
//#         PopUp.getInstance().addPopup(type, contact, txt);
//#         redraw();
//#     }
//#endif
    protected int getMainBarRGB() {return ColorTheme.getColor(ColorTheme.BAR_INK);}
    
    protected Config cf=Config.getInstance();

    /**
     * событие "Нажатие кнопки ОК"
     * базовая реализация VirtualList вызывает функцию onSelect для выбранного элемента;
     * необходимо переопределить (override) функцию для реализации желаемых действий
     */
    public void eventOk() {
        Object o = getFocusedObject();
        if (o != null) {
            ((VirtualElement) o).onSelect();
        }

        if (updateLayout() > 0)
            fitCursorByTop();
        redraw();
    }
    
    public void eventLongOk() {
    }

    public void doKeyAction(int keyCode) {
        switch(keyCode) {
            case VirtualCanvas.KEY_VOL_UP:
            case 1:
                moveCursorHome();
                return;
            case VirtualCanvas.KEY_VOL_DOWN:
            case 7:
                moveCursorEnd();
                return;
            case VirtualCanvas.KEY_UP:
            case 2:
                keyUp();
                return;
            case VirtualCanvas.KEY_DOWN:
            case 8:
                keyDwn();
                return;
            case VirtualCanvas.KEY_LEFT:
            case 4:
                pageLeft();
                return;
            case VirtualCanvas.KEY_RIGHT:
            case 6:
                pageRight();
                return;            
            case VirtualCanvas.KEY_FIRE:
            case 5:
                eventOk();
                return;
            case VirtualCanvas.KEY_SOFT_LEFT:
                doLeftAction();
                return;
            case VirtualCanvas.KEY_SOFT_RIGHT:
                doRightAction();
                return;
            case VirtualCanvas.KEY_CLEAR:
                keyClear();
                return;
            case VirtualCanvas.KEY_GREEN:
                keyGreen();
                return;
            case VirtualCanvas.KEY_BACK:
                if (canBack)
                    cmdCancel();
                return;
            case VirtualCanvas._KEY_STAR:
                showTimeTrafficInfo();
                return;
            case VirtualCanvas._KEY_POUND:
                showInfo();
                return;
        }
    }

    public boolean longKey(int keyCode) {
        return false;
    }

    public boolean doUserKeyAction(int command_id) {
        switch (command_id) {
            case 22:
                moveCursorHome();
                return true;
            case 23:
                moveCursorEnd();
                return true;
            case 24:
                keyDwn();
                return true;
            case 25:
                keyUp();
                return true;
            case 26:
                pageLeft();
                return true;
            case 27:
                pageRight();
                return true;
            case 28:
                if (canBack == true)
                    destroyView();
                return true;
            case 29:
                keyClear();
                return true;
            case 34:
                showInfo();
                return true;
            case 35:
                eventOk();
                return true;
            case 36:
                touchLeftPressed();
                return true;
            case 37:
                touchRightPressed();
                return true;
            case 48:
                pageLeft();
                return true;
	    case 49:
                pageRight();
                return true;
        }

        return false;
    }


//#ifdef MEMORY_USAGE
//#     public static boolean memMonitor;
//#endif
    public static boolean showTimeTraffic = true;
    
    public boolean canBack = true;

    /** метрика экрана */
    protected static int width;
    protected static int height;
    
    public int cursor;

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
   
    
    protected synchronized int updateLayout() {
        int size = getItemCount();
        if (size == 0) {
            listHeight = 0;
            return 0;
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
        return itemLayoutY.length;
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
    public int winHeight; // отображаемый размер списка
    
    protected int offset; // счётчик автоскроллинга
    
    protected boolean showBalloon;
    
    protected ComplexString mainbar;
    protected ComplexString infobar;
    
    private boolean wrapping = true;

    public static int startGPRS=-1;
    public static int offGPRS=0;

    /** processing short/long presses and double clicks  */
    private long lastClickTime;
    private int lastCursor;

    /**
     * Разрешает заворачивание списка в кольцо (перенос курсора через конец списка)
     * по умолчанию установлен true
     * @param wrap будучи переданным true, разрешает перенос курсора через конец списка
     */
    public void enableListWrapping(boolean wrap) { this.wrapping=wrap; }
    
        
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
        if (cursor < getItemCount()) {
            return getItemRef(cursor);
        }
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
//#         PopUp.getInstance();
//#endif
        
        changeOrient(cf.panelsState);

//        setFullScreenMode(fullscreen);
     
        scrollbar=new ScrollBar();
        scrollbar.setHasPointerEvents(VirtualCanvas.getInstance().hasPointerEvents());

        infobar = new MainBar("", true, VirtualCanvas.getInstance().hasPointerEvents() && cf.advTouch && Config.fullscreen);
        infobar.addElement(null); //1
        infobar.addRAlign();
        infobar.addElement(null); //3

        stringHeight = FontCache.getFont(false, FontCache.roster).getHeight();        
    }
    
    public void show() {
        parentView = VirtualCanvas.getInstance().getList();
        VirtualCanvas.getInstance().show(this);
     }

    public void redraw() {
        if (VirtualCanvas.getInstance().isShown()) {
            VirtualCanvas.getInstance().repaint();
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
        redraw();
    }

    /**
     * начало отрисовки списка.
     * функция вызывается перед отрисовкой списка,
     * перед любыми обращениями к элементам списка.
     *     
     */
    protected void beginPaint() { }

    public synchronized void paint(Graphics g) {
        if (messagesWidth == 0) {
            messagesWidth = getListWidth();
        }
        beginPaint();
//#ifdef POPUPS
//#        PopUp.getInstance().init(g, width, height);
//#endif

        //StaticData.getInstance().screenWidth=width;



        int count = updateLayout();

        setAbsOrg(g, 0, 0);

        setInfo();
        
        if (paintTop) {
            if (reverse) {
                if (infobar != null) {
                    list_top = infobar.getVHeight();
                    drawInfoPanel(g);
                }
            } else {
                if (mainbar != null) {
                    list_top = mainbar.getVHeight();
                    drawMainPanel(g);
                }
            }
        }
        if (paintBottom) {
            if (reverse) {
                if (mainbar != null) {
                    list_bottom = mainbar.getVHeight();
                }
            } else {
                if (infobar != null) {
                    list_bottom = infobar.getVHeight();
                }
            }
        }

        winHeight = height - list_top - list_bottom;

        boolean scroll = (listHeight > winHeight);

        if (count == 0) {
            cursor = (cursor == -1) ? -1 : 0;
            win_top = 0;
        } else if (cursor >= count) {
            cursor = count - 1;
            stickyWindow = true;
        }
        if (updateLayout() > 0 && stickyWindow) {
            fitCursorByTop();
        }

        int itemMaxWidth = (scroll) ? (width - scrollbar.getScrollWidth()) : (width);

        int itemIndex = win_top > 0 ? getElementIndexAt(win_top) : 0;
        int displayedIndex = 0;
        int displayedBottom = list_top;

        int baloon = -1;
        while (itemIndex < itemLayoutY.length) {
            int itemYpos = itemLayoutY[itemIndex] - win_top;
            if (itemYpos >= winHeight) {
                break;
            }
            VirtualElement el = getItemRef(itemIndex);
            boolean sel = (itemIndex == cursor);
            if (el != null) {
                int lh = el.getVHeight();

                setAbsOrg(g, 0, list_top);
                g.setClip(0, 0, itemMaxWidth, winHeight);

                g.translate(0, itemYpos);

                g.setColor(el.getColorBGnd());

                if (sel) {
                    drawCursor(g, itemMaxWidth, lh);
                    baloon = g.getTranslateY();
                } else {
//#ifdef BACK_IMAGE
//#                 if (VirtualCanvas.getInstance().img == null)
//#endif
                    {
                        g.fillRect(0, 0, itemMaxWidth, lh); //clear field
                    }
                }
                g.setColor(el.getColor());

                g.clipRect(0, 0, itemMaxWidth, lh);
                el.drawItem(g, (sel) ? offset : 0, sel);
                displayedBottom = list_top + itemYpos + lh;
            }
            itemIndex++;

        } // while
        int clrH = height - displayedBottom;

        if (clrH > 0
//#ifdef BACK_IMAGE
//#                                 && VirtualCanvas.getInstance().img==null
//#endif
                ) {
            setAbsOrg(g, 0, displayedBottom);
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
        } else {
            scrollbar.setSize(0);
        }

        setAbsClip(g, width, height);
//#ifdef MEMORY_USAGE
//#         drawHeapMonitor(g, list_top); //heap monitor
//#endif
        

        if (paintBottom) {
            if (reverse) {
                if (mainbar != null) {
                    setAbsOrg(g, 0, height - mainbar.getVHeight());
                    drawMainPanel(g);
                    CommandsPointer.init(width, height, mainbar.getVHeight());
                }
            } else {
                if (infobar != null) {
                    setAbsOrg(g, 0, height - infobar.getVHeight());
                    drawInfoPanel(g);
                    CommandsPointer.init(width, height, infobar.getVHeight());

                }
            }
            setAbsClip(g, width, height);

            if (sd.roster.messageCount > 0) {
                drawEnvelop(g);
            }
            if (System.currentTimeMillis() - sd.getTrafficIn() < 2000) {
                drawTraffic(g, false);
            }
            if (System.currentTimeMillis() - sd.getTrafficOut() < 2000) {
                drawTraffic(g, true);
            }
        }

//#ifdef POPUPS
//#         setAbsClip(g, width, height);
//#         drawPopUp(g);
//#endif        
    }


    protected void drawEnvelop(final Graphics g) {
        g.setColor(getMainBarRGB());
        int wpos= (width/2);
        int hpos= height-13;
        
        g.drawRect(wpos-4,	hpos, 	8, 	6);
        g.drawLine(wpos-3,	hpos+1,	wpos,	hpos+4);
        g.drawLine(wpos,	hpos+4,	wpos+3,	hpos+1);
        g.drawLine(wpos-3,	hpos+5,	wpos-2,	hpos+4);
        g.drawLine(wpos+2,	hpos+4,	wpos+3,	hpos+5);
    }
    
    protected void drawTraffic(final Graphics g, boolean up) {
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
//#     protected void drawPopUp(final Graphics g) {
//#         PopUp.getInstance().paintCustom(g);
//#     }
//#endif
    
    private void setAbsClip(final Graphics g, int w, int h) {
        setAbsOrg(g, 0, 0);
        g.setClip(0,0, w, h);
    }
        
//#ifdef MEMORY_USAGE
//#     protected void drawHeapMonitor(final Graphics g, int y) {
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
//#         ((MainBar)infobar).startColor = getMainBarBGnd();
//#         ((MainBar)infobar).endColor = getMainBarBGndBottom();
//#endif        
        
        ((MainBar)infobar).lShift = (Config.getInstance().phoneManufacturer == Config.NOKIA && reverse && Config.fullscreen);
        ((MainBar)infobar).rShift = (Config.getInstance().phoneManufacturer == Config.SONYE && reverse && Config.fullscreen);
        infobar.drawItem(g, 0, false);

    }
    
    private void drawMainPanel (final Graphics g) {    
        int h=mainbar.getVHeight()+1;
        g.setClip(0,0, width, h);
//#ifdef GRADIENT        
//#         ((MainBar)mainbar).startColor = getMainBarBGndBottom();
//#         ((MainBar)mainbar).endColor = getMainBarBGnd();
//#endif        
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
    
    protected synchronized void fitCursorByTop() {
        if (cursor >= itemLayoutY.length)
            cursor = itemLayoutY.length - 1;
        int top = itemLayoutY[cursor];
        if (top < win_top) {
            win_top = top;
        }

        Object o = getFocusedObject();
        if (o == null) {
            return;
        }

        if (((VirtualElement) o).getVHeight() <= winHeight) {
            if ((cursor + 1) < itemLayoutY.length) {
                int bottom = itemLayoutY[cursor + 1] - winHeight;
                if (bottom > win_top) {
                    win_top = bottom;
                }
            }
        }

        if (top >= win_top + winHeight) {
            win_top = top;
        }
    }
    
    protected void fitCursorByBottom() {
        if ((cursor + 1) < itemLayoutY.length) {
            int bottom = itemLayoutY[cursor + 1] - winHeight;
            if (bottom > win_top) {
                win_top = bottom;
            }
        }

        Object o = getFocusedObject();
        if (o == null) {
            return;
        }

        if (((VirtualElement) o).getVHeight() <= winHeight) {
            int top = itemLayoutY[cursor];
            if (top < win_top) {
                win_top = top;
            }
        }
        if ((cursor + 1) < itemLayoutY.length) {
            if (itemLayoutY[cursor + 1] <= win_top) {
                int bottom = itemLayoutY[cursor + 1] - winHeight;
                win_top = bottom;
            }
        }
    }

/* Currently not used
 * TODO: testing and think about remove it.
 * See pointerReleased(...) method for more information.
    protected void makeCursorMaximallyVisible() {
        if (cursor < itemLayoutY.length - 1) {
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
*/

    public void keyGreen() {}
    public void keyClear() {}

    private int yPointerPos;

    protected void pointerPressed(int x, int y) {
        long clickTime = System.currentTimeMillis();
        lastClickTime = clickTime;
        lastCursor = cursor;

//#ifdef POPUPS
//#         if (PopUp.getInstance().size() > 0) {
//#             return;
//#         }
//#endif

        int act = CommandsPointer.pointerPressed(x, y);
        if (act == 1) {
            doLeftAction();
            stickyWindow = false;
            return;
        } else if (act == 2) {
            doRightAction();
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

        int pos = getElementIndexAt(win_top + y - list_top);
        if (cursor >= 0 && cursor != pos) {
            moveCursorTo(pos);
            stickyWindow = false;
            setRotator();
        }
    }

    boolean itemDragged = false;

    protected void pointerDragged(int x, int y) {
//#ifdef POPUPS
//#         if (PopUp.getInstance().size() > 0) {
//#             return;
//#         }
//#endif

        if ((y < list_top)
        || (y > list_top + winHeight)) {
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
/*
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
*/
    }

    protected void pointerReleased(int x, int y) {
        long clickTime = System.currentTimeMillis();
        long dTime = clickTime - lastClickTime;
        boolean longClick = (dTime > 500 && dTime < 5000);
        boolean shortClick = (dTime <= 200);
        lastClickTime = clickTime;

//#ifdef POPUPS
//#         if ((longClick && PopUp.getInstance().goToMsgList())
//#         || (shortClick && PopUp.getInstance().next())) {
//#             return;
//#         }
//#endif

        if (scrollbar.pointerReleased(x, y, this)
        || (Config.fullscreen && CommandsPointer.pointerPressed(x, y) > 0)
        || (y > list_top + winHeight)) {
            return;
        }

// In my opinion, scrolling without it more comfortable. Totktonada.
/*
        if (cursor != lastCursor) { // Without this condition, scrolling large messages is very interesting :-)
            makeCursorMaximallyVisible();
        }
*/

        if (!itemDragged) {            
            if (longClick) {
                eventLongOk();
            }
            if (shortClick && (cursor == lastCursor || cf.advTouch)) {
                eventOk();
            }
        }
        itemDragged = false;
        lastCursor = cursor;
    }
    
    /**
     * событие "Нажатие кнопки UP"
     * в классе VirtualList функция перемещает курсор на одну позицию вверх.
     * возможно переопределить (override) функцию для реализации необходимых действий
     */
    public void keyUp() {
        if (getItemCount()==0)
            return;
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
        if (getItemCount() == 0) {
            return 0;
        }
        int prevRef = curRef;
        boolean process = true;
        while (process) {
            prevRef--;
            if (prevRef <= 0) {
                if (wrapping) {
                    prevRef = getItemCount();
                } else {
                    prevRef = 0;
                    process = false;
                }
            }
            if (getItemRef(prevRef).isSelectable()) {
                break;
            }            
        }

        return prevRef;
    }

    public int getNextSelectableRef(int curRef) {
        if (getItemCount() == 0) {
            return 0;
        }
        int nextRef = curRef;
        boolean process = true;
        while (process) {
            nextRef++;
            if (nextRef >= getItemCount()) {
                if (wrapping) {
                    nextRef = 0;
                } else {
                    nextRef = curRef;
                    process = false;
                }
            }
            if (getItemRef(nextRef).isSelectable()) {
                break;
            }
        }

        return nextRef;
    }

    private boolean itemPageDown() {
        stickyWindow = false;
        Object o = getFocusedObject();
	if (o == null) {
            return false;
        }

        if (((VirtualElement) o).getVHeight() <= winHeight) {
            stickyWindow = true;
            return false;
        }

        if (!cursorInWindow()) {
            return false;
        }
            
        int remainder = itemLayoutY[cursor + 1] - win_top;
        if (remainder <= winHeight) {
            return false;
        }
        if (remainder <= 2 * winHeight) {
            win_top = remainder - winHeight + win_top + 8;
            return true;
        }

        win_top += winHeight - stringHeight;//-stringHeight;
        return true;
    }
    
    private boolean itemPageUp() {
        stickyWindow=false;
        Object o = getFocusedObject();
	if (o == null) {
            return false;
        }

        if (((VirtualElement) o).getVHeight() <= winHeight) {
            //stickyWindow=true;
            return false;
        }

        if (!cursorInWindow()) {
            return false;
        }
        
        int remainder = win_top - itemLayoutY[cursor];
        if (remainder <= 0) {
            return false;
        }
        if (remainder <= winHeight) {
            win_top = itemLayoutY[cursor];
            return true;
        }

        win_top -= winHeight - stringHeight;//-stringHeight;
        return true;
    }

    public void pageLeft() {
        if (getItemCount()==0)
            return;

        stickyWindow = false;
        win_top -= winHeight;
        if (win_top < 0) {
            win_top = 0;
            /*
            if (!getItemRef(0).isSelectable()) {
	        cursor = getNextSelectableRef(-1);
            } else {
                cursor = 0;
            }
            */
            cursor = getNextSelectableRef(-1);
        }
        if (!cursorInWindow()) {
            cursor = getElementIndexAt(itemLayoutY[cursor] - winHeight);
            Object o = getFocusedObject();
            if (o != null && ((VirtualElement) o).getVHeight() <= winHeight) {
                if (updateLayout() > 0)
                    fitCursorByTop();
            }
        }

        setRotator();
    }

    public void pageRight() {
        if (getItemCount()==0)
            return;

        stickyWindow = false;
        win_top += winHeight;
        int endTop = listHeight - winHeight;
        if (endTop < win_top) {
            win_top = (listHeight < winHeight) ? 0 : endTop;
            int lastItemNum = getItemCount() - 1;
            if (!getItemRef(lastItemNum).isSelectable()) {
                cursor = getPrevSelectableRef(lastItemNum);
            } else {
                cursor = lastItemNum;
            }
        } else if (!cursorInWindow()) {
            cursor = getElementIndexAt(itemLayoutY[cursor] + winHeight);
            Object o = getFocusedObject();
            if (o != null && ((VirtualElement) o).getVHeight() <= winHeight) {
                if (updateLayout() > 0)
                    fitCursorByTop();
            }
        }

        setRotator();
    }
    
    public boolean cursorInWindow() {
        int y1 = itemLayoutY[cursor] - win_top;
        if (y1 >= winHeight) {
            return false;
        }
        if (itemLayoutY.length > cursor) {
            int y2 = itemLayoutY[cursor + 1] - win_top;
            if (y2 >= 0) {
                return true;
            }
        }
        return false;
    }
    
    protected void setRotator() {
//#if (USE_ROTATOR)
//#         if (cursor < getItemCount())
//#             focusedItem(cursor);
//# 
//#         int itemWidth = 0;
//#         if (cursor < getItemCount()) {
//#             VirtualElement item = getItemRef(cursor);
//#             if (item != null) {
//#                 itemWidth = item.getVWidth();
//#                 if (itemWidth >= getListWidth()) {
//#                     itemWidth -= width / 2;
//#                 } else {
//#                     itemWidth = 0;
//#                 }
//#             }
//#         }
//# 
//#         TimerTaskRotate.startRotate(itemWidth, this);
 //#endif
    }
    
    protected void drawCursor (Graphics g, int width, int height) {
//#ifdef BACK_IMAGE
//#         if (VirtualCanvas.getInstance().img == null)
//#endif            
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
    public void destroyView() {
        if (canBack) {
            sd.roster.activeContact = null;
            VirtualCanvas.getInstance().show(parentView);
        }
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
        }
    }
        
    public void setInfo() {
        commandState();        
        if (infobar == null) return;
        if (VirtualCanvas.getInstance().rw != null) {
            if (VirtualCanvas.getInstance().rw.isActive()) {
                infobar.setElementAt(SR.MS_OK, 1);
                infobar.setElementAt(SR.MS_CANCEL, 3);
                return;
            }
        }
        if (Config.getInstance().phoneManufacturer == Config.NOKIA && !Config.fullscreen)
            showTimeTraffic = true;
        infobar.setElementAt((!showTimeTraffic) ? touchLeftCommand() : Time.getTimeWeekDay(), 1);
        infobar.setElementAt((!showTimeTraffic) ? touchRightCommand() : getTraffic(), 3);
    }

    public void showTimeTrafficInfo() {
//#ifdef POPUPS
//#         StringBuffer mem = new StringBuffer();
//#         mem.append(Time.localDate()).append(" ").append(Time.getTimeWeekDay())
//#            .append("\nTraffic: ")
//#            .append(getTraffic())
//#ifdef MEMORY_USAGE
//#            .append("\nFree: ")
//#            .append(Runtime.getRuntime().freeMemory()>>10)
//#            .append(" kb");
//#         if (phoneManufacturer == Config.SONYE)
//#             mem.append("\nTotal: ")
//#                .append(Runtime.getRuntime().totalMemory()>>10)
//#                .append(" kb")
//#endif
//#            ;
//#         setWobble(1, null, mem.toString());
//#endif
    }

    public static String getTraffic() {
        long traffic = StaticData.getInstance().traffic;
        return StringUtils.getSizeString((traffic>0)?traffic*2:0);
    }
        
    public void captionPressed() {};
    public void commandState() {};

    public void doLeftAction() {
        ReconnectWindow rw = VirtualCanvas.getInstance().rw;
        if (rw != null && rw.isActive()) {
            VirtualCanvas.getInstance().reconnectYes();
        } else {
            touchLeftPressed();
        }
    }

    public void doRightAction() {
        ReconnectWindow rw = VirtualCanvas.getInstance().rw;
        if (rw != null && rw.isActive()) {
            VirtualCanvas.getInstance().reconnectNo();
        } else {
            touchRightPressed();
        }
    }
   
    public abstract void touchLeftPressed();   
    public abstract void touchRightPressed();
        

    public abstract String touchLeftCommand();
    public abstract String touchRightCommand();

    public void cmdCancel() {
        if (canBack)
            destroyView();
    }

    public void showInfo() {
//#ifdef POPUPS
//# 	VirtualElement item = (VirtualElement) getFocusedObject();
//# 	if (item != null) {
//# 	    String text = item.getTipString();
//# 	    if (text != null) {
//# 		setWobble(1, null, text);
//# 	    }
//# 	}
//#endif
    }
}
