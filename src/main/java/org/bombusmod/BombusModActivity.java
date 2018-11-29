/**
 * MicroEmulator Copyright (C) 2008 Bartek Teodorczyk <barteo@barteo.net>
 *
 * It is licensed under the following two licenses as alternatives: 1. GNU
 * Lesser General Public License (the "LGPL") version 2.1 or any newer version
 * 2. Apache License (the "AL") Version 2.0
 *
 * You may not use this file except in compliance with at least one of the above
 * two licenses.
 *
 * You may obtain a copy of the LGPL at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.txt
 *
 * You may obtain a copy of the AL at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the LGPL
 * or the AL for the specific language governing permissions and limitations.
 *
 */
package org.bombusmod;

import Client.Contact;
import Client.StaticData;
import android.app.NotificationManager;
import android.content.*;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.Window;
import de.duenndns.ssl.MemorizingTrustManager;
import org.bombusmod.android.service.XmppService;
import org.bombusmod.scrobbler.Receiver;
import org.microemu.DisplayAccess;
import org.microemu.MIDletAccess;
import org.microemu.MIDletBridge;
import org.microemu.android.MicroEmulatorActivity;
import org.microemu.android.device.AndroidDevice;
import org.microemu.android.device.AndroidInputMethod;
import org.microemu.android.device.ui.AndroidCanvasUI;
import org.microemu.android.device.ui.AndroidCommandUI;
import org.microemu.android.device.ui.AndroidDisplayableUI;
import org.microemu.android.util.AndroidLoggerAppender;
import org.microemu.android.util.AndroidRecordStoreManager;
import org.microemu.android.util.AndroidRepaintListener;
import org.microemu.app.Common;
import org.microemu.app.util.MIDletSystemProperties;
import org.microemu.cldc.file.FileSystem;
import org.microemu.device.Device;
import org.microemu.device.DeviceFactory;
import org.microemu.device.ui.CommandUI;
import org.microemu.log.Logger;

import javax.microedition.lcdui.Command;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class BombusModActivity extends MicroEmulatorActivity {

    public static final String LOG_TAG = "BombusModActivity";
    public Common common;
    private MIDlet midlet;
    private static BombusModActivity instance;
    protected Receiver musicReceiver;

    private SSLContext sslContext;

    private MemorizingTrustManager memorizingTrustManager;

    private XmppService xmppService;

    private boolean serviceBound;

    public static BombusModActivity getInstance() {
        return instance;
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        instance = this;

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);


        Logger.removeAllAppenders();
        Logger.setLocationEnabled(false);
        Logger.addAppender(new AndroidLoggerAppender());

        System.setOut(new PrintStream(new OutputStream() {

            StringBuffer line = new StringBuffer();

            @Override
            public void write(int oneByte) throws IOException {
                if (((char) oneByte) == '\n') {
                    Logger.debug(line.toString());
                    if (line.length() > 0) {
                        line.delete(0, line.length() - 1);
                    }
                } else {
                    line.append((char) oneByte);
                }
            }
        }));

        System.setErr(new PrintStream(new OutputStream() {

            StringBuffer line = new StringBuffer();

            @Override
            public void write(int oneByte) throws IOException {
                if (((char) oneByte) == '\n') {
                    Logger.debug(line.toString());
                    if (line.length() > 0) {
                        line.delete(0, line.length() - 1);
                    }
                } else {
                    line.append((char) oneByte);
                }
            }
        }));

        common = new Common(emulatorContext);
        common.setRecordStoreManager(new AndroidRecordStoreManager(this));
        common.setDevice(new AndroidDevice(emulatorContext, this));

        System.setProperty("microedition.platform", "microemu-android");
        System.setProperty("microedition.configuration", "CLDC-1.1");
        System.setProperty("microedition.profiles", "MIDP-2.0");
        System.setProperty("microedition.locale", Locale.getDefault().toString());
        System.setProperty("device.manufacturer", android.os.Build.BRAND);
        System.setProperty("device.model", android.os.Build.MODEL);
        System.setProperty("device.software.version", android.os.Build.VERSION.RELEASE);


        /*
         * JSR-75
         */
        Map<String, String> properties = new HashMap<String, String>();
        properties.put("fsRoot", "/");
        properties.put("fsSingle", "sdcard");
        FileSystem fs = new FileSystem();
        fs.registerImplementation(properties);
        common.extensions.add(fs);
        MIDletSystemProperties.setPermission("javax.microedition.io.Connector.file.read", 1);
        MIDletSystemProperties.setPermission("javax.microedition.io.Connector.file.write", 1);
        System.setProperty("fileconn.dir.photos", "file:///sdcard/");

        /*
         * BombusModInitialization and Service
         */
        BombusModInitialization init = new BombusModInitialization();
        init.registerImplementation(null);
        common.extensions.add(init);

        initializeExtensions();

        common.setSuiteName("org.BombusMod");
        midlet = common.initMIDlet(false);
        //audio scrobbler
        IntentFilter filter = new IntentFilter();
        //Google Android player
        filter.addAction("com.android.music.playstatechanged");
        filter.addAction("com.android.music.playbackcomplete");
        filter.addAction("com.android.music.metachanged");
        //HTC Music
        filter.addAction("com.htc.music.playstatechanged");
        filter.addAction("com.htc.music.playbackcomplete");
        filter.addAction("com.htc.music.metachanged");
        //MIUI Player
        filter.addAction("com.miui.player.playstatechanged");
        filter.addAction("com.miui.player.playbackcomplete");
        filter.addAction("com.miui.player.metachanged");
        //Real
        filter.addAction("com.real.IMP.playstatechanged");
        filter.addAction("com.real.IMP.playbackcomplete");
        filter.addAction("com.real.IMP.metachanged");
        //SEMC Music Player
        filter.addAction("com.sonyericsson.music.playbackcontrol.ACTION_TRACK_STARTED");
        filter.addAction("com.sonyericsson.music.playbackcontrol.ACTION_PAUSED");
        filter.addAction("com.sonyericsson.music.TRACK_COMPLETED");
        filter.addAction("com.sonyericsson.music.metachanged");
        filter.addAction("com.sonyericsson.music.playbackcomplete");
        filter.addAction("com.sonyericsson.music.playstatechanged");
        //rdio
        filter.addAction("com.rdio.android.metachanged");
        filter.addAction("com.rdio.android.playstatechanged");
        //Samsung Music Player
        filter.addAction("com.samsung.sec.android.MusicPlayer.playstatechanged");
        filter.addAction("com.samsung.sec.android.MusicPlayer.playbackcomplete");
        filter.addAction("com.samsung.sec.android.MusicPlayer.metachanged");
        filter.addAction("com.sec.android.app.music.playstatechanged");
        filter.addAction("com.sec.android.app.music.playbackcomplete");
        filter.addAction("com.sec.android.app.music.metachanged");
        //Winamp
        filter.addAction("com.nullsoft.winamp.playstatechanged");
        //Amazon
        filter.addAction("com.amazon.mp3.playstatechanged");
        //Rhapsody
        filter.addAction("com.rhapsody.playstatechanged");
        //PowerAmp
        filter.addAction("com.maxmpz.audioplayer.playstatechanged");
        //will be added any....
        //scrobblers detect for players (poweramp for example)
        //Last.fm
        filter.addAction("fm.last.android.metachanged");
        filter.addAction("fm.last.android.playbackpaused");
        filter.addAction("fm.last.android.playbackcomplete");
        //A simple last.fm scrobbler
        filter.addAction("com.adam.aslfms.notify.playstatechanged");
        //Scrobble Droid
        filter.addAction("net.jjc1138.android.scrobbler.action.MUSIC_STATUS");
        musicReceiver = new Receiver();
        registerReceiver(musicReceiver, filter);

        try {
            sslContext = SSLContext.getInstance("TLS");
            memorizingTrustManager = new MemorizingTrustManager(instance);
            sslContext.init(null, new X509TrustManager[]{memorizingTrustManager}, new java.security.SecureRandom());
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            Log.w("TLS", e.getMessage());
        }
        Intent intent = new Intent(this, XmppService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        System.out.println("onNewIntent(" + intent.getAction() + ")");
        if ("org.bombusmod.bm-notify".equals(intent.getAction())) {
            Contact c = StaticData.getInstance().roster.getFirstContactWithNewHighlite(null);
            if (c != null) {
                c.getMsgList().show();
                StaticData.getInstance().roster.focusToContact(c, false);
            }
        }
        if ("org.bombusmod.bm-notify.reply".equals(intent.getAction())) {
            Contact c = StaticData.getInstance().roster.getFirstContactWithNewHighlite(null);
            if (c != null) {
                c.getMsgList().Reply();                
            }
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (contentView != null) {
            if (contentView instanceof AndroidRepaintListener) {
                ((AndroidRepaintListener) contentView).onPause();
            }
        }

        if (isFinishing()) {
            NotificationManager mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            mNM.cancelAll();
            Log.i(LOG_TAG, "onPause(); with isFinishing() == true.");
            return;
        }

        Log.i(LOG_TAG, "onPause(); with isFinishing() == false.");

        MIDletAccess ma = MIDletBridge.getMIDletAccess(midlet);
        if (ma != null) {
            ma.pauseApp();
            DisplayAccess da = ma.getDisplayAccess();
            if (da != null) {
                da.hideNotify();
            }
        }
        if (memorizingTrustManager != null) {
            memorizingTrustManager.unbindDisplayActivity(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (memorizingTrustManager != null) {
            memorizingTrustManager.bindDisplayActivity(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(LOG_TAG, "onDestroy();");
        unregisterReceiver(musicReceiver);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(LOG_TAG, "onStop();");
    }

    protected void initializeExtensions() {
    }
    private boolean ignoreBackKeyUp = false;

    @Override
    public void onBackPressed() {
        MIDletAccess ma = MIDletBridge.getMIDletAccess();
        if (ma == null) {
            return;
        }
        final DisplayAccess da = ma.getDisplayAccess();
        if (da == null) {
            return;
        }
        AndroidDisplayableUI ui = (AndroidDisplayableUI) da.getDisplayableUI(da.getCurrent());
        if (ui == null) {
            return;
        }

        List<AndroidCommandUI> commands = ui.getCommandsUI();

        CommandUI cmd = getFirstCommandOfType(commands, Command.BACK);
        if (cmd == null) {
            cmd = getFirstCommandOfType(commands, Command.EXIT);
        }
        if (cmd == null) {
            cmd = getFirstCommandOfType(commands, Command.CANCEL);
        }
        if (cmd == null) {
            return;
        }

        if (ui.getCommandListener() != null) {
            ignoreBackKeyUp = true;

            MIDletBridge.getMIDletAccess().getDisplayAccess().commandAction(cmd.getCommand(), da.getCurrent());
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        MIDletAccess ma = MIDletBridge.getMIDletAccess();
        if (ma == null) {
            return false;
        }
        final DisplayAccess da = ma.getDisplayAccess();
        if (da == null) {
            return false;
        }
        AndroidDisplayableUI ui = (AndroidDisplayableUI) da.getDisplayableUI(da.getCurrent());
        if (ui == null) {
            return false;
        }
        if (ui instanceof AndroidCanvasUI) {
            if (ignoreKey(keyCode)) {
                return false;
            }

            Device device = DeviceFactory.getDevice();
            ((AndroidInputMethod) device.getInputMethod()).buttonPressed(event);

            return true;
        }
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.ECLAIR
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            // Take care of calling this method on earlier versions of 
            // the platform where it doesn't exist. 
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && ignoreBackKeyUp) {
            ignoreBackKeyUp = false;
            return true;
        }
        MIDletAccess ma = MIDletBridge.getMIDletAccess();
        if (ma == null) {
            return false;
        }
        final DisplayAccess da = ma.getDisplayAccess();
        if (da == null) {
            return false;
        }
        AndroidDisplayableUI ui = (AndroidDisplayableUI) da.getDisplayableUI(da.getCurrent());
        if (ui == null) {
            return false;
        }

        if (ui instanceof AndroidCanvasUI) {
            if (ignoreKey(keyCode)) {
                return false;
            }

            Device device = DeviceFactory.getDevice();
            ((AndroidInputMethod) device.getInputMethod()).buttonReleased(event);

            return true;
        }

        return super.onKeyUp(keyCode, event);
    }

    private CommandUI getFirstCommandOfType(List<AndroidCommandUI> commands, int commandType) {
        for (int i = 0; i < commands.size(); i++) {
            CommandUI cmd = commands.get(i);
            if (cmd.getCommand().getCommandType() == commandType) {
                return cmd;
            }
        }

        return null;
    }

    private boolean ignoreKey(int keyCode) {
        switch (keyCode) {
//        case KeyEvent.KEYCODE_MENU:
            case KeyEvent.KEYCODE_VOLUME_DOWN:
            case KeyEvent.KEYCODE_VOLUME_UP:
            case KeyEvent.KEYCODE_HEADSETHOOK:
                return true;
            default:
                return false;
        }
    }
    private final static KeyEvent KEY_RIGHT_DOWN_EVENT = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_RIGHT);
    private final static KeyEvent KEY_RIGHT_UP_EVENT = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_RIGHT);
    private final static KeyEvent KEY_LEFT_DOWN_EVENT = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_LEFT);
    private final static KeyEvent KEY_LEFT_UP_EVENT = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_LEFT);
    private final static KeyEvent KEY_DOWN_DOWN_EVENT = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_DOWN);
    private final static KeyEvent KEY_DOWN_UP_EVENT = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_DOWN);
    private final static KeyEvent KEY_UP_DOWN_EVENT = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_UP);
    private final static KeyEvent KEY_UP_UP_EVENT = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_UP);
    private final static float TRACKBALL_THRESHOLD = 1.0f;
    private float accumulatedTrackballX = 0;
    private float accumulatedTrackballY = 0;

    @Override
    public boolean onTrackballEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            MIDletAccess ma = MIDletBridge.getMIDletAccess();
            if (ma == null) {
                return false;
            }
            final DisplayAccess da = ma.getDisplayAccess();
            if (da == null) {
                return false;
            }
            AndroidDisplayableUI ui = (AndroidDisplayableUI) da.getDisplayableUI(da.getCurrent());
            if (ui instanceof AndroidCanvasUI) {
                float x = event.getX();
                float y = event.getY();
                if ((x > 0 && accumulatedTrackballX < 0) || (x < 0 && accumulatedTrackballX > 0)) {
                    accumulatedTrackballX = 0;
                }
                if ((y > 0 && accumulatedTrackballY < 0) || (y < 0 && accumulatedTrackballY > 0)) {
                    accumulatedTrackballY = 0;
                }
                if (accumulatedTrackballX + x > TRACKBALL_THRESHOLD) {
                    accumulatedTrackballX -= TRACKBALL_THRESHOLD;
                    KEY_RIGHT_DOWN_EVENT.dispatch(this);
                    KEY_RIGHT_UP_EVENT.dispatch(this);
                } else if (accumulatedTrackballX + x < -TRACKBALL_THRESHOLD) {
                    accumulatedTrackballX += TRACKBALL_THRESHOLD;
                    KEY_LEFT_DOWN_EVENT.dispatch(this);
                    KEY_LEFT_UP_EVENT.dispatch(this);
                }
                if (accumulatedTrackballY + y > TRACKBALL_THRESHOLD) {
                    accumulatedTrackballY -= TRACKBALL_THRESHOLD;
                    KEY_DOWN_DOWN_EVENT.dispatch(this);
                    KEY_DOWN_UP_EVENT.dispatch(this);
                } else if (accumulatedTrackballY + y < -TRACKBALL_THRESHOLD) {
                    accumulatedTrackballY += TRACKBALL_THRESHOLD;
                    KEY_UP_DOWN_EVENT.dispatch(this);
                    KEY_UP_UP_EVENT.dispatch(this);
                }
                accumulatedTrackballX += x;
                accumulatedTrackballY += y;

                return true;
            }
        }

        return super.onTrackballEvent(event);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MIDletAccess ma = MIDletBridge.getMIDletAccess();
        if (ma == null) {
            return false;
        }
        final DisplayAccess da = ma.getDisplayAccess();
        if (da == null) {
            return false;
        }
        AndroidDisplayableUI ui = (AndroidDisplayableUI) da.getDisplayableUI(da.getCurrent());
        if (ui == null) {
            return false;
        }

        menu.clear();
        boolean result = false;
        List<AndroidCommandUI> commands = ui.getCommandsUI();
        for (int i = 0; i < commands.size(); i++) {
            result = true;
            AndroidCommandUI cmd = commands.get(i);
            if (cmd.getCommand().getCommandType() == Command.SCREEN) {
                SubMenu item = menu.addSubMenu(Menu.NONE, i + Menu.FIRST, Menu.NONE, cmd.getCommand().getLabel());
                item.setIcon(cmd.getDrawable());
            }
        }

        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        MIDletAccess ma = MIDletBridge.getMIDletAccess();
        if (ma == null) {
            return false;
        }
        final DisplayAccess da = ma.getDisplayAccess();
        if (da == null) {
            return false;
        }
        AndroidDisplayableUI ui = (AndroidDisplayableUI) da.getDisplayableUI(da.getCurrent());
        if (ui == null) {
            return false;
        }

        int commandIndex = item.getItemId() - Menu.FIRST;
        List<AndroidCommandUI> commands = ui.getCommandsUI();
        CommandUI c = commands.get(commandIndex);

        if (c != null) {
            MIDletBridge.getMIDletAccess().getDisplayAccess().commandAction(c.getCommand(), da.getCurrent());
            return true;
        }

        return false;
    }

    public void minimizeApp() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    public SSLContext getSslContext() {
        return sslContext;
    }
    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            XmppService.LocalBinder binder = (XmppService.LocalBinder) service;
            xmppService = binder.getService();
            Log.d("BombusMod", "Service connected");
            serviceBound = true;
            new Thread(new Runnable() {

                public void run() {
                    MIDletAccess ma = MIDletBridge.getMIDletAccess(midlet);
                    if (ma != null) {
                        try {
                            ma.startApp();
                        } catch (MIDletStateChangeException e) {
                            e.printStackTrace();
                        }
                    }

                    if (contentView != null) {
                        if (contentView instanceof AndroidRepaintListener) {
                            ((AndroidRepaintListener) contentView).onResume();
                        }
                        post(new Runnable() {

                            public void run() {
                                contentView.invalidate();
                            }
                        });
                    }
                }
            }).start();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.d("BombusMod", "Service disconnected");
            serviceBound = false;
        }
    };

    public XmppService getXmppService() {
        return xmppService;
    }
}
