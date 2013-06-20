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
import Colors.ColorTheme;
import java.util.Timer;
import java.util.TimerTask;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.midlet.MIDlet;
import ui.controls.PopUp;
//#ifdef USER_KEYS
import ui.keys.UserKeyExec;
//#endif
//#ifdef LIGHT_CONFIG
//# import LightControl.CustomLight;
//#endif
//#ifdef AUTOSTATUS
import Client.AutoStatus;
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

    public static int keyLock = VirtualCanvas._KEY_STAR;
    public static int keyVibra = VirtualCanvas._KEY_POUND;
    
    private VirtualList list;
    public VirtualList homeList;

    public Command commandOk;
    public Command commandCancel;

    static VirtualCanvas instance;
    static MIDlet midlet;

    boolean kHold = false;

    protected StaticData sd = StaticData.getInstance();

    public ReconnectWindow rw;
   

    public static VirtualCanvas getInstance() {
        if (instance == null) {
            instance = new VirtualCanvas();
        }        
        return instance;
    }   
    
    private VirtualCanvas() {
        setFullScreenMode(Config.fullscreen);
    }
    
    public void setMIDlet(MIDlet midlet) {
        VirtualCanvas.midlet = midlet;
    }

    void commandState() {
        // magic to invalidate menu
        setCommandListener(null);
    }
    
    public void show(VirtualList virtualList) {
        KeyRepeatTimer.stop();
        if (virtualList == null)
            virtualList = getList();        
        list = virtualList;     
        Display.getDisplay(midlet).setCurrent(this);
        repaint();
        commandState();
    }
    
    public VirtualList getList() {
        return list == null ? homeList : list;
    }

    public void setList(VirtualList list) {
        this.list = list;
    }

    public void reconnectYes() {
       rw.reconnect();
    }

    public void reconnectNo() {
        rw.stopReconnect();
    }


    protected void paint(Graphics graphics) {
        VirtualList.width = getWidth();
        VirtualList.height = getHeight();
        graphics.setColor(ColorTheme.getColor(ColorTheme.LIST_BGND));
        graphics.fillRect(0, 0, VirtualList.width, VirtualList.height);     
        
        try {
            list.paint(graphics);
        } catch(Exception e) {
            if (sd.roster != null)
                sd.roster.errorLog("list.paint exception: " + e.getClass() + " in " + list.getClass());
            if (StaticData.Debug)
                e.printStackTrace();
        }
        if (rw != null)
            rw.draw(graphics, VirtualList.width, VirtualList.height);
    }

    protected final void keyPressed(int rawKeyCode) {
        int keyCode = getKeyCode(rawKeyCode);
//#ifdef AUTOSTATUS
    AutoStatus.getInstance().userActivity(Config.AWAY_IDLE);
//#endif
        kHold = false;
        if (PopUp.getInstance().handlePressed(keyCode)) {
//#ifdef USER_KEYS
            UserKeyExec.getInstance().keyExecute(keyCode, true);
            UserKeyExec.getInstance().afterActions(keyCode);
//#endif
            repaint();
            return;
        }
        checkKey(keyCode);
        repaint();
//#ifdef LIGHT_CONFIG      
//#             CustomLight.keyPressed();
//#endif 
//#ifdef USER_KEYS
        UserKeyExec.getInstance().afterActions(keyCode);
//#endif
     }

    protected final void keyRepeated(int rawKeyCode){
        int keyCode = getKeyCode(rawKeyCode);
        // TODO: uncomment to check motorola
        //kHold = true;
        //doKeyAction(keyCode);
//#ifdef LIGHT_CONFIG      
//#             CustomLight.keyPressed();
//#endif 
//#ifdef AUTOSTATUS
    AutoStatus.getInstance().userActivity(Config.AWAY_IDLE);
//#endif
    }

    protected final void keyReleased(int rawKeyCode) {
        int keyCode = getKeyCode(rawKeyCode);
//#ifdef LIGHT_CONFIG      
//#             CustomLight.keyPressed();
//#endif
//#ifdef AUTOSTATUS
    AutoStatus.getInstance().userActivity(Config.AWAY_IDLE);
//#endif
        KeyRepeatTimer.stop();
    }

    protected final void pointerPressed(int x, int y) {
//#ifdef AUTOSTATUS
        AutoStatus.getInstance().userActivity(Config.AWAY_IDLE);
//#endif
        try {
            list.pointerPressed(x, y);
        } catch (Exception e) {
            if (sd.roster != null) {
                sd.roster.errorLog("pointerpressed exception: " + e.getMessage());
            }
            if (StaticData.Debug)
                e.printStackTrace();
        }
        repaint();
    }

    protected final void pointerDragged(int x, int y) {
        try {
            list.pointerDragged(x, y);
        } catch (Exception e) {
            if (sd.roster != null) {
                sd.roster.errorLog("pointerdragged exception: " + e.getMessage());
            }
        }
        repaint();
    }

    protected final void pointerReleased(int x, int y) {
        try {
            list.pointerReleased(x, y);
        } catch (Exception e) {
            if (sd.roster != null) {
                sd.roster.errorLog("pointerreleased exception: " + e.getMessage());
            }
        }
        repaint();
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

    public int getKeyCode(int key_code) {
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
        switch (key_code) {
            case -82:
                return KEY_SOFT_RIGHT;
            case -8:
                return KEY_BACK;
            case -10:
                return KEY_GREEN;
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
            case _KEY_POUND:
            case _KEY_STAR:
                KeyRepeatTimer.stop();
                break;
            default:
                KeyRepeatTimer.start(keyCode);
        }
        try {
            doKeyAction(keyCode);
        } catch(Exception e) {
        }
        repaint();
    }
     /**
     * обработка кодов кнопок
     * @param keyCode код нажатой кнопки
     */
    protected void doKeyAction(int keyCode) {
        if (!sendEvent(keyCode)) {
            if (kHold && list.longKey(keyCode)) {
                PopUp.getInstance().handled = true;
                KeyRepeatTimer.stop();
                kHold = false;
            } else {
//#ifdef USER_KEYS
                if (UserKeyExec.getInstance().keyExecute(keyCode, false)) {
                    return;
                }
//#endif                
                list.doKeyAction(keyCode);
            }
        }
    }

    private boolean sendEvent(int key_code) {
        int keyCode = getKeyCode(key_code);
        if ((keyCode > -1) && (list.getFocusedObject() != null)) {
            return ((VirtualElement) list.getFocusedObject()).handleEvent(keyCode);
        }
        return false;
    }
}

class KeyRepeatTimer extends TimerTask {
        private static Timer timer = new Timer();
        private int key;
        private int slowlyIterations = 8;


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
            if (0 < slowlyIterations) {
                slowlyIterations--;
                if (0 != slowlyIterations % 2) {
                    return;
                }
            }
            if (!VirtualCanvas.getInstance().isShown()) {
                KeyRepeatTimer.stop();
                return;
            }
            VirtualCanvas.getInstance().kHold = true;
            try {
                VirtualCanvas.getInstance().doKeyAction(key);
            } catch(Exception e) {
            }
            VirtualCanvas.getInstance().repaint();
        }
    }


//#if (USE_ROTATOR)    
class TimerTaskRotate extends TimerTask {

    private int scrollLen;
    private int scroll; //wait before scroll * sleep
    private boolean scrollline;
    private VirtualList attachedList;
    private static TimerTaskRotate instance;
    private static Timer timer;

    public TimerTaskRotate() {
        timer = new Timer();
        timer.schedule(this, 250, 250);
    }

    public static void startRotate(int max, VirtualList list) {

        if (instance == null) {
            instance = new TimerTaskRotate();
        }

        if (max < 0) {
            //instance.destroyTask();
            list.offset = 0;
            return;
        }

        synchronized (instance) {
            list.offset = 0;
            instance.scrollLen = max;
            instance.scrollline = (max > 0);
            instance.attachedList = list;
            instance.scroll = 1;
        }
    }

    public void run() {


        if (scroll == 0) {
            if (instance.scroll()) {
                if (attachedList != null) {
                    attachedList.redraw();
                }
            } else {
                timer.cancel();
                timer = null;
                instance = null;
            }
        } else {
            scroll--;
        }
        /* TODO: remove
        if (VirtualList.reconnectRedraw) {
        VirtualList.reconnectRedraw = false;
        try {
        attachedList.redraw();
        } catch (Exception e) {
        instance = null;
        }
        }
         */

    }

    public boolean scroll() {
        synchronized (this) {
            if (scrollline == false || attachedList == null || scrollLen < 0) {
                return false;
            }
            if (attachedList.offset >= scrollLen) {
                scrollLen = -1;
                attachedList.offset = 0;
                scrollline = false;
            } else {
                attachedList.offset += 14;
            }

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
