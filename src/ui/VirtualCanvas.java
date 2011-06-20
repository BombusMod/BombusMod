/*
 * NativeCanvas.java
 *
 * Created on 31 Август 2009 г., 19:40
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ui;

import Client.Config;
import Client.StaticData;
import java.util.Timer;
import java.util.TimerTask;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.midlet.MIDlet;
//#ifdef POPUPS
import ui.controls.PopUp;
//#endif
//#ifdef USER_KEYS
//# import ui.keys.UserKeyExec;
//#endif
//#ifdef LIGHT_CONFIG
//# import LightControl.CustomLight;
//#endif

/**
 *
 * @author Vladimir Krukov
 */
public class VirtualCanvas extends Canvas implements CommandListener{

    public static final int _KEY_STAR = 10;
    public static final int _KEY_POUND = 11;
    public static final int KEY_UP = 12;
    public static final int KEY_DOWN = 13;
    public static final int KEY_LEFT = 14;
    public static final int KEY_RIGHT = 15;
    public static final int KEY_FIRE = 16;
    public static final int KEY_BACK = 17;
    public static final int KEY_GREEN = 18;
    public static final int KEY_CLEAR = 19;
    public static final int KEY_SOFT_LEFT = 20;
    public static final int KEY_SOFT_RIGHT = 21;
    public static final int KEY_VOL_UP = 22;
    public static final int KEY_VOL_DOWN = 23;
    public static final int KEY_FLIP_OPEN = 24;
    public static final int KEY_FLIP_CLOSE = 25;

    public static char keyLock = '*';
    public static char keyVibra = '#';
    
    private VirtualList list;
    public VirtualList homeList;

    public Command commandOk;
    public Command commandCancel;

    static VirtualCanvas instance;
    static MIDlet midlet;

    boolean kHold = false;
	
    protected StaticData sd = StaticData.getInstance();
    
    public static VirtualCanvas getInstance() {
        if (instance == null) {
            instance = new VirtualCanvas();
        }        
        return instance;
    }   
    
    private VirtualCanvas() {
        setFullScreenMode(Config.fullscreen);
        if (VirtualList.phoneManufacturer==Config.WINDOWS) {
            setTitle("BombusMod");
        }
        if (hasPointerEvents() && Config.getInstance().swapMenu) {
            // silly china phones
            Config.getInstance().swapMenu = false;
        }
    }
    
    public void setMIDlet(MIDlet midlet) {
        VirtualCanvas.midlet = midlet;
    }
    
    void commandState() {
        // TODO: play with enabled CommandListener for all + filter soft keys
        if (Config.getInstance().phoneManufacturer == Config.NOKIA) {
            if (hasPointerEvents()) {
                return;
            }
            if (Config.fullscreen) {
                setCommandListener(null);
            } else if (list != null) {
                setOk(list.touchLeftCommand());
                setCancel(list.touchRightCommand());
                setCommandListener(instance);
            }
        }
    }
    
    public void show(VirtualList virtualList) {
        KeyRepeatTimer.stop();
        if (virtualList == null)
            virtualList = getList();
        if (isShown()) {
            list = virtualList;
            repaint();
        } else {
            list = virtualList;
            Display.getDisplay(midlet).setCurrent(this);
            repaint();
        }
        commandState();
    }
    
    public VirtualList getList() {
        return list == null ? homeList : list;
    }

    public void setList(VirtualList list) {
        this.list = list;
    }
    
    protected void paint(Graphics graphics) {
        list.width = getWidth();
        list.height = getHeight();
        list.paint(graphics);
    }
    protected final void keyPressed(int keyCode) {
//#ifdef AUTOSTATUS
//#     sd.roster.userActivity();
//#endif
        kHold = false;        
//#ifdef POPUPS
        if (PopUp.getInstance().handlePressed(getKey(keyCode))) {
            list.redraw();
            return;
        }
//#endif
        checkKey(getKey(keyCode));
//#ifdef LIGHT_CONFIG      
//#ifdef PLUGINS                
//#         if (StaticData.getInstance().lightConfig)
//#endif            
//#             CustomLight.keyPressed();
//#endif 
//#ifdef AUTOSTATUS
//#     sd.roster.setAutoAwayTimer();
//#endif
     }

    protected final void keyRepeated(int keyCode){
        // TODO: uncomment to check motorola
        //kHold = true;
        //doKeyAction(getKey(keyCode));
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
        //list.keyReleased(keyCode);
//#ifdef LIGHT_CONFIG      
//#ifdef PLUGINS                
//#         if (StaticData.getInstance().lightConfig)
//#endif            
//#             CustomLight.keyPressed();
//#endif
//#ifdef AUTOSTATUS
//#     sd.roster.userActivity();
//#endif
        KeyRepeatTimer.stop();
//#ifdef POPUPS
        if (PopUp.getInstance().handleReleased(getKey(keyCode))) {
            list.redraw();
            return;
        } else {
	    switch (getKey(keyCode)) {
		case VirtualCanvas._KEY_STAR:
		    list.showTimeTrafficInfo();
                return;
		case VirtualCanvas._KEY_POUND:
		    list.showInfo();
	    }
	}
//#endif

    }

    protected final void pointerPressed(int x, int y) {
        list.pointerPressed(x, y);
    }
    protected final void pointerDragged(int x, int y) {
        list.pointerDragged(x, y);
    }
    protected final void pointerReleased(int x, int y) {
        list.pointerReleased(x, y);
    }

    protected void showNotify() {
//#if (USE_ROTATOR)
        TimerTaskRotate.startRotate(-1, list);
//#endif
        setFullScreenMode(Config.fullscreen);
    }

    protected void sizeChanged(int w, int h) {
        if (list != null) {
            list.sizeChanged(w, h);            
        }
    }

    public void commandAction(Command c, Displayable d) {
        if (c == commandOk) 
            list.touchLeftPressed();
        if (c == commandCancel) 
            list.touchRightPressed();
    }

    public final void setOk(String title) {
        if (!Config.fullscreen) {
            if (commandOk != null) {
                removeCommand(commandOk);
            }
            if (title != null) {
                commandOk = new Command(title, Command.OK, 1);
                addCommand(commandOk);
            }

        }
    }
    
    public final void setCancel(String title) {
        if (!Config.fullscreen) {
            if (commandCancel != null) {
                removeCommand(commandCancel);
            }
            if (title != null) {
                commandCancel = new Command(title, Command.BACK, 99);
                addCommand(commandCancel);
            }
        }
    }
        private int getKey(int key_code) {
        switch (key_code) {
            case KEY_NUM0:
                return 0;
            case KEY_NUM1:
                return 1;
            case KEY_NUM2:
                return 2;
            case KEY_NUM3:
                return 3;
            case KEY_NUM4:
                return 4;
            case KEY_NUM5:
                return 5;
            case KEY_NUM6:
                return 6;
            case KEY_NUM7:
                return 7;
            case KEY_NUM8:
                return 8;
            case KEY_NUM9:
                return 9;
            case KEY_STAR:
                return _KEY_STAR;
            case KEY_POUND:
                return _KEY_POUND;
            default:                
                try {
                    String strName = getKeyName(key_code).replace('_', ' ').toUpperCase();
                    if (strName.equals("SOFT 1") || strName.equals("SOFT1")
                        || strName.equals("SOFTKEY 1") || strName.equals("LEFT SOFT")) {
                        return KEY_SOFT_LEFT;
                    }
                    if (strName.equals("SOFT 2") || strName.equals("SOFT2")
                        || strName.equals("SOFTKEY 2") || strName.equals("RIGHT SOFT")) {
                        return KEY_SOFT_RIGHT;
                    }
                    if (strName.indexOf("SOFT") >= 0) {
                        if (strName.indexOf("1") >= 0) {
                            return KEY_SOFT_LEFT;
                        }
                        if (strName.indexOf("2") >= 0) {
                            return KEY_SOFT_RIGHT;
                        }
                    }
                    // from jimm
                    if (strName.equals("ON/OFF") || strName.equals("BACK"))
                        return KEY_BACK;
                    if (strName.equals("CLEAR"))
                        return KEY_CLEAR;
                    if (strName.equals("SEND") || strName.equals("START"))
                        return KEY_GREEN;
                    if (strName.equals("OK") || strName.equals("SELECT")
                        || strName.equals("FIRE") || strName.equals("ENTER")
                         || strName.equals("NAVI-CENTER")) {                        
                        return KEY_FIRE;
                    }
                    if (strName.equals("DOWN") || strName.equals("NAVI-DOWN")
                        || strName.equals("DOWN ARROW"))
                        return KEY_DOWN;
                    if (strName.equals("UP") || strName.equals("NAVI-UP")
                        || strName.equals("UP ARROW"))
                        return KEY_UP;
                    if (strName.equals("LEFT") || strName.equals("NAVI-LEFT")
                        || strName.equals("LEFT ARROW") || strName.equals("SIDEUP"))
                        return KEY_LEFT;
                    if (strName.equals("RIGHT") || strName.equals("NAVI-RIGHT")
                        || strName.equals("RIGHT ARROW") || strName.equals("SIDEDOWN"))
                        return KEY_RIGHT;


                } catch (Exception e) {
                }

        }
        // platform-specific keys (TODO: check is it really needed?)
        switch (Config.getInstance().phoneManufacturer) {
            case Config.SONYE:
                switch (key_code) {
                    case -8:
                        return KEY_CLEAR;
                    case -10:
                        return KEY_GREEN;
                    case -11:
                        return KEY_BACK;
                    case -36:
                        return KEY_VOL_UP;
                    case -37:
                        return KEY_VOL_DOWN;
                    case -30:
                        return KEY_FLIP_OPEN;
                    case -31:
                        return KEY_FLIP_CLOSE;
                }
                break;
            case Config.SONYE_M600:
                switch (key_code) {
                    case 13:
                        return KEY_GREEN;
                    case -11:
                        return KEY_BACK;
                    case 165:
                        return KEY_VOL_UP;
                    case 166:
                        return KEY_VOL_DOWN;
                }
                break;
            case Config.WTK:
                if (key_code == -10) {
                    return KEY_GREEN;
                }
                break;
            case Config.NOKIA:
                switch (key_code) {
                    case -10:
                        return KEY_GREEN;
                    case -8:
                        return KEY_CLEAR;
                    case -63582:
                        return KEY_VOL_UP;
                    case -63583:
                        return KEY_VOL_DOWN;
                }
                break;
            case Config.SIEMENS:
            case Config.SIEMENS2:
                keyLock = '#';
                keyVibra = '*';
                switch (key_code) {
                    case -1:
                        return KEY_SOFT_LEFT;
                    case -4:
                        return KEY_SOFT_RIGHT;
                    case -24:
                        return KEY_FLIP_OPEN;
                    case -22:
                        return KEY_FLIP_CLOSE;
                    case -12:
                        return KEY_BACK;
                    case -11:
                        return KEY_GREEN;
                    case -13:
                        return KEY_VOL_UP;
                    case -14:
                        return KEY_VOL_DOWN;
                }
                break;
            case Config.WINDOWS:
                switch (key_code) {
                    case 40:
                        return KEY_SOFT_LEFT;
                    case 41:
                        return KEY_SOFT_RIGHT;
                    case 8:
                        return KEY_CLEAR;
                    case -5:
                        return KEY_GREEN;
                }
                break;
            case Config.MOTO:
                switch (key_code) {
                    case -200:
                        return KEY_FLIP_OPEN; // and close
                    case -10:
                        return KEY_GREEN;
                }
                break;
            case Config.MOTOEZX:
                switch (key_code) {
                    case -21:
                        return KEY_SOFT_LEFT;
                    case -22:
                        return KEY_SOFT_RIGHT;
                    case -6:
                        return KEY_BACK;
                    case -30:
                        return KEY_VOL_UP;
                    case -31:
                        return KEY_VOL_DOWN;
                }
                break;
            case Config.LG:
                switch (key_code) {
                    case -13:
                        return KEY_VOL_UP;
                    case -14:
                        return KEY_VOL_DOWN;
                    case -16:
                        return KEY_CLEAR;
                    case -10:
                        return KEY_GREEN;
                }
                break;
            case Config.MICROEMU:
                switch (key_code) {
                    case -82:
                        return KEY_SOFT_LEFT;
                    case -8:
                        return KEY_BACK;
                    case -10:
                        return KEY_GREEN;
                }
                break;
        }
        // fallback from jimm
                try {
                switch (getGameAction(key_code)) {
                    case UP:
                        return KEY_UP;
                    case LEFT:
                        return KEY_LEFT;
                    case RIGHT:
                        return KEY_RIGHT;
                    case DOWN:
                        return KEY_DOWN;
                    case FIRE:
                        return KEY_FIRE;
                }
            } catch (Exception e) {
            }
            if (key_code == -6 || key_code == -21 || key_code == 21 || key_code == 105
                    || key_code == -202 || key_code == 113 || key_code == 57345
                    || key_code == 0xFFBD) {
                return KEY_SOFT_LEFT;
            }
            if (key_code == -7 || key_code == -22 || key_code == 22 || key_code == 106
                    || key_code == -203 || key_code == 112 || key_code == 57346
                    || key_code == 0xFFBB) {
                return KEY_SOFT_RIGHT;
            }

            return key_code; // undefined key as is
    }

    protected void checkKey(int keyCode) {
        switch (keyCode) {
            case KEY_SOFT_LEFT:
            case KEY_SOFT_RIGHT:
            case KEY_GREEN:
            case KEY_BACK:
            case KEY_FIRE:
            case KEY_CLEAR:
                KeyRepeatTimer.stop();
                break;
            default:
                KeyRepeatTimer.start(keyCode);
        }
        doKeyAction(keyCode);
    }
     /**
     * обработка кодов кнопок
     * @param keyCode код нажатой кнопки
     */
    protected void doKeyAction(int keyCode) {
        switch (keyCode) {
            case KEY_GREEN:
                list.keyGreen();
                return;
            case KEY_SOFT_LEFT:
                list.doLeftAction();
                return;
            case KEY_SOFT_RIGHT:
                list.doRightAction();
                return;
            case KEY_UP:
                list.keyUp();
                list.redraw();
                return;
            case KEY_DOWN:
                list.keyDwn();
                list.redraw();
                return;
            case KEY_LEFT:
                list.pageLeft();
                list.redraw();
                return;
            case KEY_RIGHT:
                list.pageRight();
                list.redraw();
                return;
            case KEY_FIRE:
                list.eventOk();
                return;
            case KEY_CLEAR:
                list.keyClear();
                return;
            case KEY_BACK:
                if (list.canBack)
                    list.cmdCancel();
                return;
        }
        if (!sendEvent(keyCode)) {
//#ifdef USER_KEYS
//#             if (UserKeyExec.getInstance().keyExecute(keyCode, kHold)) {
//#                 return;
//#             }
//#endif
            if (kHold) {
                list.longKey(keyCode);
//#ifdef POPUPS
                PopUp.getInstance().handled = true;
//#endif
                KeyRepeatTimer.stop();
                kHold = false;
            } else {
                list.userKeyPressed(keyCode);
            }
        } else {
            repaint();
        }

    }
    private boolean sendEvent(int key_code) {
        int key = getKey(key_code);
        if ((key > -1) && (list.getFocusedObject() != null)) {
            return ((VirtualElement) list.getFocusedObject()).handleEvent(key);
        }
        return false;
    }
}

class KeyRepeatTimer extends TimerTask {
        private static Timer timer = new Timer();
        private int key;
       // private int slowlyIterations = 8;


        public static void start(int key) {
            stop();
            timer = new Timer();
            KeyRepeatTimer repeater = new KeyRepeatTimer(key);
            timer.schedule(repeater, 400, 100);
        }
        public static void stop() {
            Timer t = timer;
            if (null != t) {
                t.cancel();
                t = null;
            }
        }

        private KeyRepeatTimer(int keyCode) {
            key = keyCode;            
        }

        public void run() {
         /*   if (0 < slowlyIterations) {
                slowlyIterations--;
                if (0 != slowlyIterations % 2) {
                    return;
                }
            }*/
            if (!VirtualCanvas.getInstance().isShown()) {
                KeyRepeatTimer.stop();
                return;
            }
            VirtualCanvas.getInstance().kHold = true;
            VirtualCanvas.getInstance().doKeyAction(key);
        }
    }


//#if (USE_ROTATOR)    
class TimerTaskRotate extends TimerTask {
    private int scrollLen;
    private int scroll; //wait before scroll * sleep
    private int balloon; // show balloon time

    private boolean scrollline;
    
    private VirtualList attachedList;
    
    private static TimerTaskRotate instance;

    private static Timer timer;

    public TimerTaskRotate() {
        timer = new Timer();
        timer.schedule(this, 250, 250);
    }
    
    public static void startRotate(int max, VirtualList list) {
        //Windows mobile J9 hanging test
        if (Config.getInstance().phoneManufacturer==Config.WINDOWS) {
            list.showBalloon=true;
            list.offset=0;
            return;
        }
        if (instance==null)  {
            instance=new TimerTaskRotate();            
        }
        
        if (max<0) {
            //instance.destroyTask();
            list.offset=0;
            return;
        }
        
        synchronized (instance) {
            list.offset=0;
            instance.scrollLen=max;
            instance.scrollline=(max>0);
            instance.attachedList=list;
            instance.balloon  = 8;
            instance.scroll   = 4;
        }
    }
    
    public void run() {

        synchronized (this) {
            if (scroll == 0) {
                if (instance.scroll()
                        || instance.balloon()) {
                    try {
                        attachedList.redraw();
                    } catch (Exception e) {
                        instance = null;
                    }
                }
            } else {
                scroll--;
            }
            if (VirtualList.reconnectRedraw) {
                VirtualList.reconnectRedraw = false;
                try {
                    attachedList.redraw();
                } catch (Exception e) {
                    instance = null;
                }
            }
        }

    }

    public boolean scroll() {
        synchronized (this) {
            if (scrollline==false || attachedList==null || scrollLen<0)
                return false;
            if (attachedList.offset>=scrollLen) {
                scrollLen=-1; attachedList.offset=0; scrollline = false;
            } else 
                attachedList.offset+=14;

            return true;
        }
    }
    
    public boolean balloon() {
        synchronized (this) {
            if (attachedList==null || balloon<0)
                return false;
            balloon--;
            attachedList.showBalloon=(balloon<8 && balloon>0);
            return true;
        }
    } 
    /*
    public void destroyTask(){
        synchronized (this) { 
            if (attachedList!=null) 
                attachedList.offset=0;
        }
    }*/
}
//#endif


