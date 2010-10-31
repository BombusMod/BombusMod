/*
 * Light.java
 *
 * Light-control module.
 *
 * Usage:
 * <CODE>
 * CustomLight.setLightMode(CustomLight.ACTION_NONE);
 * </CODE>
 *
 * @author Vladimir Krukov
 */

package LightControl;

import Client.Config;
import javax.microedition.lcdui.*;
import midlet.BombusMod;
//import javax.microedition.midlet.*;
//import com.motorola.funlight.*;
//import com.siemens.mp.game.*;
//import com.nokia.mid.ui.*;
//import com.samsung.util.*;
import java.util.*;

/**
 * Class for platform-independent light control.
 * 
 * @author Vladimir Krukov
 */
public final class CustomLight extends TimerTask {
//#ifdef PLUGINS
//#     public static String plugin = new String("PLUGIN_LIGHT");
//#endif    
    private static CustomLight instance = new CustomLight(null);
    private Timer timer;
    LightConfig cf = LightConfig.getInstance();
     
    //states
    static final byte IDLE = 0;
    static final byte MESSAGE = 1;
    static final byte KEYPRESSED = 2;
    static final byte ERROR = 3;
    static final byte PRESENCE = 4;
    static final byte CONNECT = 5;
    static final byte BLINK0 = 100;//no light, use this only if previous state is lighting
    static final byte BLINK1 = 101;//light
    static final byte BLINK2 = 102;//no light
    static final byte BLINK3 = 103;//light again

     /* If system windows opened only one state is possible - `keypressed'
     **/

    private byte state = CONNECT;
    private byte previous_state = IDLE;
    private int tick = 10;
    private int previous_tick =0;

    private static final int INTERVAL = 1000;
    
    public static void setLightMode(final byte m) {
        if (null != instance) {
            instance.setMode(m);
        }
    }

    public static void keyPressed() {
        if (null != instance) {
            instance.setMode(KEYPRESSED);
        }
    }
    public static void message() {
        if (null != instance) {
            instance.setMode(MESSAGE);
        }
    }
    public static void presence() {
        if (null != instance) {
            instance.setMode(PRESENCE);
        }
    }
    public static void error() {
        if (null != instance) {
            instance.setMode(ERROR);
        }
    }
    public static void startBlinking() {
        if (null != instance) {
            instance.setMode(BLINK0);
        }
    }


    //returns if current displayable is system one
    private static boolean isSystem() {
        return !(Display.getDisplay(BombusMod.getInstance()).getCurrent() instanceof Canvas);
    }

    //returns number of ticks should be made at a certain state
    private int getMaxTickCount(final byte m) {
        switch (m) {
            case IDLE:
                return 32767;
            case MESSAGE:
                return Math.max(1, cf.light_message_time);
            case KEYPRESSED:
                return Math.max(1, cf.light_keypressed_time);
            case ERROR:
                return Math.max(1, cf.light_error_time);
            case PRESENCE:
                return Math.max(1, cf.light_presence_time);
            case BLINK0:
            case BLINK1:
            case BLINK2:
            case BLINK3:
                return Math.max(1, cf.light_blink_time);
        }
        return 32767;
    }

    //changes mode
    private synchronized void setMode(final byte m) {
        //not controlling light
        if (!cf.light_control) {
            return;
        }
        //do not change light in system windows
        if (isSystem()) {
            return;
        }
        if (m!=KEYPRESSED && m!=CONNECT && m!=IDLE && (state==IDLE || state==KEYPRESSED)) {
            previous_state = state;
            previous_tick = tick;
        }
        
        tick = getMaxTickCount(m);
        //same state as before
        if (m == state) {
            return;
        }
        if (m==BLINK0 && getLightValue(state)<cf.light_blink/2)
            state=BLINK1;
        else
            state = m;
        setLight();
    }

    private synchronized void setLight() {
        setLight(getLightValue(state));
    }

    public void run() {
        if (!cf.light_control) {
            return;
        }
        setLight();
        if (state==IDLE || isSystem())
            return;
        if (0 != tick) {
            tick--;
            return;
        }

        switch (state) {
            case MESSAGE:
            case PRESENCE:
            case BLINK3:
            case ERROR:
                state = previous_state;
                tick = previous_tick;
                break;
            case KEYPRESSED:
            case CONNECT:
                state = IDLE;
                break;
            case BLINK0:
                state = BLINK1;
                tick = getMaxTickCount(state);
                break;
            case BLINK1:
                state = BLINK2;
                tick = getMaxTickCount(state);
                break;
            case BLINK2:
                state = BLINK3;
                tick = getMaxTickCount(state);
                break;
            default:
                state = IDLE;
                break;
        }
    }

    private void setLight(int value) {
        if ((value > 100) || (value < 0)) {
            return;
        }
        if ((value > 0) && (Config.getInstance().phoneManufacturer == Config.NOKIA)) {
            setLight(0);
        }
        try {
            switch (Config.getInstance().phoneManufacturer) {                
                case Config.MOTO:
                case Config.MOTOEZX:
                    /*int curBrightness = value * 255 / 100;
                    int c = curBrightness + (curBrightness << 8) + (curBrightness << 16);
                    // 1 - Display
                    // 2 - Keypad
                    // 3 - Sideband

                    FunLight.getRegion(1).setColor(c);
                    FunLight.getRegion(1).getControl();
//                    if (value > 0) {
//                        FunLight.getRegion(1).getControl();
//                    } else {
//                        FunLight.getRegion(1).releaseControl();
//                        Jimm.getJimm().getDisplay().flashBacklight(0x00000000);
//                    }
                    break;#*/
                case Config.SIEMENS:
                case Config.SIEMENS2:    
                    if (value > 0) {
                        com.siemens.mp.game.Light.setLightOn();
                    } else {
                        com.siemens.mp.game.Light.setLightOff();
                    }
                    break;
                case Config.NOKIA:
                case Config.SONYE:    
                    com.nokia.mid.ui.DeviceControl.setLights(0, value);
                    break;

//                case LIGHT_SAMSUNG:
//                    if (value > 0) {
//                        LCDLight.on(0x7FFFFFFF);
//                    } else {
//                        LCDLight.off();
//                    }
//                    break;
                default:
                    if (value > 0) {
                        BombusMod.getInstance().getDisplay().flashBacklight(0x7FFFFFFF);
                    } else {
                        BombusMod.getInstance().getDisplay().flashBacklight(0x00000000);
                    }
                    break;
            }
        } catch (Exception e) {
        }
    }       
   
    
    private int getLightValue(byte state) {
        switch (state) {
            case IDLE:
                return cf.light_idle;
            case PRESENCE:
                return cf.light_presence;
            case KEYPRESSED:
                return cf.light_keypress;
            case CONNECT:
                return cf.light_connect;
            case MESSAGE:
                return cf.light_message;
            case ERROR:
                return cf.light_error;
            case BLINK0:
                return 0;
            case BLINK1:
                return cf.light_blink;
            case BLINK2:
                return 0;
            case BLINK3:
                return cf.light_blink;
        }
        return 100;
    }

    /** Creates a new instance of Light */
    private CustomLight(Timer timer) {
        this.timer = timer;
        if (null != timer) {
            timer.scheduleAtFixedRate(this, 0, INTERVAL);
        }
    }
    public static void setLight(boolean on) {
        final boolean worked = (null != instance.timer);
        if (worked) {
            if (!on) {
                instance.timer.cancel();
                instance.setLight(0);
                instance = new CustomLight(null);
            }
        } else {
            if (on) {
                instance = new CustomLight(new Timer());
            }
        }
    }
    /* TODO: remove old light
    public final void setLight(boolean state) {
//#ifndef NOLEGACY
        if (phoneManufacturer==Config.SIEMENS || phoneManufacturer==Config.SIEMENS2) {
            try {
                if (state) com.siemens.mp.game.Light.setLightOn();
                else com.siemens.mp.game.Light.setLightOff();  
            } catch( Exception e ) { }
            return;
        }
//#endif
        if (!state) return;
//#ifdef SE_LIGHT
//#         if (phoneManufacturer==Config.SONYE || phoneManufacturer==Config.NOKIA) {
//#             new KeepLightTask().start();
//#          }
//#endif
    } */
}