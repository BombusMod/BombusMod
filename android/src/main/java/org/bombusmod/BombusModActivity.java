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

import static android.Manifest.permission.POST_NOTIFICATIONS;

import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.view.Window;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import org.bombusmod.android.scrobbler.Receiver;
import org.bombusmod.android.service.XmppService;
import org.bombusmod.util.ClipBoardIO;
import org.microemu.DisplayAccess;
import org.microemu.MIDletAccess;
import org.microemu.MIDletBridge;
import org.microemu.android.AndroidConfig;
import org.microemu.android.device.AndroidDevice;
import org.microemu.android.device.AndroidDeviceDisplay;
import org.microemu.android.device.AndroidFontManager;
import org.microemu.android.device.AndroidInputMethod;
import org.microemu.android.device.ui.AndroidCanvasUI;
import org.microemu.android.device.ui.AndroidCommandUI;
import org.microemu.android.device.ui.AndroidDisplayableUI;
import org.microemu.android.util.AndroidRecordStoreManager;
import org.microemu.app.Common;
import org.microemu.device.Device;
import org.microemu.device.DeviceDisplay;
import org.microemu.device.DeviceFactory;
import org.microemu.device.EmulatorContext;
import org.microemu.device.FontManager;
import org.microemu.device.InputMethod;
import org.microemu.device.ui.CommandUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Locale;

import javax.microedition.lcdui.Command;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import Client.Contact;
import Client.StaticData;

public class BombusModActivity extends AppCompatActivity {

    public static final Logger logger = LoggerFactory.getLogger(
            BombusModActivity.class.getSimpleName()
    );
    public static AndroidConfig config = new AndroidConfig();
    public Common common;
    public boolean windowFullscreen;
    protected View contentView;
    protected EmulatorContext emulatorContext;
    private MIDlet midlet;
    private static BombusModActivity instance;
    protected Receiver musicReceiver;

    private boolean serviceBound;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Thread activityThread;

    //audio scrobbler
    IntentFilter scrobblerIntentFilter;

    public static BombusModActivity getInstance() {
        return instance;
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        config.FONT_SIZE_SMALL = getResources().getDimensionPixelSize(R.dimen.small_font_size);
        config.FONT_SIZE_MEDIUM = getResources().getDimensionPixelSize(R.dimen.medium_font_size);
        config.FONT_SIZE_LARGE = getResources().getDimensionPixelSize(R.dimen.large_font_size);

        // Query the activity property android:theme="@android:style/Theme.NoTitleBar.Fullscreen"
        TypedArray ta = getTheme().obtainStyledAttributes(new int[]{android.R.attr.windowFullscreen});
        windowFullscreen = ta.getBoolean(0, false);

        Drawable phoneCallIcon = getResources().getDrawable(android.R.drawable.stat_sys_phone_call);
        int statusBarHeight = 0;
        if (!windowFullscreen) {
            statusBarHeight = phoneCallIcon.getIntrinsicHeight();
        }

        Display display = getWindowManager().getDefaultDisplay();
        final int width = display.getWidth();
        final int height = display.getHeight() - statusBarHeight;

        emulatorContext = new EmulatorContext() {

            private InputMethod inputMethod = new AndroidInputMethod();

            private DeviceDisplay deviceDisplay = new AndroidDeviceDisplay(BombusModActivity.this, this, width, height);

            private FontManager fontManager = new AndroidFontManager(BombusModActivity.this.getResources().getDisplayMetrics());

            public InputMethod getDeviceInputMethod() {
                return inputMethod;
            }

            public DeviceDisplay getDeviceDisplay() {
                return deviceDisplay;
            }

            public FontManager getDeviceFontManager() {
                return fontManager;
            }

            public InputStream getResourceAsStream(Class origClass, String name) {
                try {
                    if (name.startsWith("/")) {
                        return getAssets().open(name.substring(1));
                    } else {
                        Package p = origClass.getPackage();
                        if (p == null) {
                            return getAssets().open(name);
                        } else {
                            String folder = origClass.getPackage().getName().replace('.', '/');
                            return getAssets().open(folder + "/" + name);
                        }
                    }
                } catch (IOException e1) {
                    //Logger.debug(e); // large output with BombusMod
                    return null;
                }
            }

            public boolean platformRequest(String url) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));

                return true;
            }

        };

        activityThread = Thread.currentThread();

        instance = this;

        ColorTheme.getInstance();
        applyBombusTheme();

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        System.setOut(new PrintStream(new OutputStream() {
            StringBuffer line = new StringBuffer();

            @Override
            public void write(int oneByte) {
                if (((char) oneByte) == '\n') {
                    logger.debug(line.toString());
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
                    logger.debug(line.toString());
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

        midlet = common.initMIDlet(false);

        ClipBoardIO.getInstance();
        Intent intent = new Intent(this, XmppService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        if (Build.VERSION.SDK_INT >= 33) {
            if (ContextCompat.checkSelfPermission(this, POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                return;
            }
            ActivityResultLauncher<String> launcher = registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(), isGranted ->
                            logger.info("Notifications permissions granted: {}", isGranted)
            );
            launcher.launch(POST_NOTIFICATIONS);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
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

        if (isFinishing()) {
            NotificationManager mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            mNM.cancelAll();
            logger.info("onPause(); with isFinishing() == true.");
            return;
        }

        logger.info("onPause(); with isFinishing() == false.");

        MIDletAccess ma = MIDletBridge.getMIDletAccess(midlet);
        if (ma != null) {
            ma.pauseApp();
            DisplayAccess da = ma.getDisplayAccess();
            if (da != null) {
                da.hideNotify();
            }
        }
        unregisterReceiver(musicReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        scrobblerIntentFilter = new IntentFilter();
        //Google Android player
        scrobblerIntentFilter.addAction("com.android.music.playstatechanged");
        scrobblerIntentFilter.addAction("com.android.music.playbackcomplete");
        scrobblerIntentFilter.addAction("com.android.music.metachanged");
        //HTC Music
        scrobblerIntentFilter.addAction("com.htc.music.playstatechanged");
        scrobblerIntentFilter.addAction("com.htc.music.playbackcomplete");
        scrobblerIntentFilter.addAction("com.htc.music.metachanged");
        //MIUI Player
        scrobblerIntentFilter.addAction("com.miui.player.playstatechanged");
        scrobblerIntentFilter.addAction("com.miui.player.playbackcomplete");
        scrobblerIntentFilter.addAction("com.miui.player.metachanged");
        //Real
        scrobblerIntentFilter.addAction("com.real.IMP.playstatechanged");
        scrobblerIntentFilter.addAction("com.real.IMP.playbackcomplete");
        scrobblerIntentFilter.addAction("com.real.IMP.metachanged");
        //SEMC Music Player
        scrobblerIntentFilter.addAction("com.sonyericsson.music.playbackcontrol.ACTION_TRACK_STARTED");
        scrobblerIntentFilter.addAction("com.sonyericsson.music.playbackcontrol.ACTION_PAUSED");
        scrobblerIntentFilter.addAction("com.sonyericsson.music.TRACK_COMPLETED");
        scrobblerIntentFilter.addAction("com.sonyericsson.music.metachanged");
        scrobblerIntentFilter.addAction("com.sonyericsson.music.playbackcomplete");
        scrobblerIntentFilter.addAction("com.sonyericsson.music.playstatechanged");
        //rdio
        scrobblerIntentFilter.addAction("com.rdio.android.metachanged");
        scrobblerIntentFilter.addAction("com.rdio.android.playstatechanged");
        //Samsung Music Player
        scrobblerIntentFilter.addAction("com.samsung.sec.android.MusicPlayer.playstatechanged");
        scrobblerIntentFilter.addAction("com.samsung.sec.android.MusicPlayer.playbackcomplete");
        scrobblerIntentFilter.addAction("com.samsung.sec.android.MusicPlayer.metachanged");
        scrobblerIntentFilter.addAction("com.sec.android.app.music.playstatechanged");
        scrobblerIntentFilter.addAction("com.sec.android.app.music.playbackcomplete");
        scrobblerIntentFilter.addAction("com.sec.android.app.music.metachanged");
        //Winamp
        scrobblerIntentFilter.addAction("com.nullsoft.winamp.playstatechanged");
        //Amazon
        scrobblerIntentFilter.addAction("com.amazon.mp3.playstatechanged");
        //Rhapsody
        scrobblerIntentFilter.addAction("com.rhapsody.playstatechanged");
        //PowerAmp
        scrobblerIntentFilter.addAction("com.maxmpz.audioplayer.playstatechanged");
        //will be added any....
        //scrobblers detect for players (poweramp for example)
        //Last.fm
        scrobblerIntentFilter.addAction("fm.last.android.metachanged");
        scrobblerIntentFilter.addAction("fm.last.android.playbackpaused");
        scrobblerIntentFilter.addAction("fm.last.android.playbackcomplete");
        //A simple last.fm scrobbler
        scrobblerIntentFilter.addAction("com.adam.aslfms.notify.playstatechanged");
        //Scrobble Droid
        scrobblerIntentFilter.addAction("net.jjc1138.android.scrobbler.action.MUSIC_STATUS");
        musicReceiver = new Receiver();
        ContextCompat.registerReceiver(this, musicReceiver, scrobblerIntentFilter, ContextCompat.RECEIVER_NOT_EXPORTED);
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
            case KeyEvent.KEYCODE_MENU:
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
        if (ui instanceof AndroidCanvasUI) {
            VirtualList currentList = VirtualCanvas.getInstance().getList();
            if (currentList instanceof DefForm) {
                for (int i = 0; i < ((DefForm) currentList).menuCommands.size(); i++) {
                    result = true;
                    MenuCommand cmd = (MenuCommand) ((DefForm) currentList).menuCommands.get(i);
                    MenuItem item = menu.add(Menu.NONE, i + Menu.FIRST, Menu.NONE, cmd.name);
                    MenuItemCompat.setShowAsAction(item, i == 0 ? MenuItem.SHOW_AS_ACTION_IF_ROOM : MenuItem.SHOW_AS_ACTION_NEVER);
                    //item.setIcon(cmd.getDrawable());                
                }
            }
        } else {
            List<AndroidCommandUI> commands = ui.getCommandsUI();
            for (int i = 0; i < commands.size(); i++) {
                result = true;
                AndroidCommandUI cmd = commands.get(i);
                if (cmd.getCommand().getCommandType() == Command.SCREEN) {
                    MenuItem item = menu.add(Menu.NONE, i + Menu.FIRST, Menu.NONE, cmd.getCommand().getLabel());
                    MenuItemCompat.setShowAsAction(item, MenuItem.SHOW_AS_ACTION_NEVER);
                    item.setIcon(cmd.getDrawable());
                }
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
        if (ui instanceof AndroidCanvasUI) {
            VirtualList currentList = VirtualCanvas.getInstance().getList();
            ((MenuListener)currentList).menuAction(
                    (MenuCommand) ((DefForm) currentList).menuCommands.get(commandIndex), 
                    currentList);
            return true;
        } else {
            List<AndroidCommandUI> commands = ui.getCommandsUI();
            CommandUI c = commands.get(commandIndex);
            if (c != null) {
                MIDletBridge.getMIDletAccess().getDisplayAccess().commandAction(c.getCommand(), da.getCurrent());
                return true;
            }
            return false;
        }
    }
    
    public void applyBombusTheme() {
        ActionBar actionBar = getSupportActionBar();
        int gradientColors[] = {0xff000000 | ColorTheme.getColor(ColorTheme.BAR_BGND), 0xff000000 | ColorTheme.getColor(ColorTheme.BAR_BGND_BOTTOM)};
        GradientDrawable drawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, gradientColors);
        actionBar.setBackgroundDrawable(drawable);
        /*
        int titleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
        if (titleId > 0) {
            TextView barTextView = (TextView) findViewById(titleId);
            barTextView.setTextColor(0xff000000 | ColorTheme.getColor(ColorTheme.BAR_INK));
        }
        */
    }


    public void minimizeApp() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }
    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            XmppService.LocalBinder binder = (XmppService.LocalBinder) service;
            StaticData.getInstance().setService(binder.getService());
            logger.debug("Service connected");
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
            logger.debug("Service disconnected");
            serviceBound = false;
        }
    };

    public void setConfig(AndroidConfig config) {
        BombusModActivity.config = config;
    }

    public EmulatorContext getEmulatorContext() {
        return emulatorContext;
    }

    public boolean post(Runnable r) {
        return handler.post(r);
    }

    public boolean isActivityThread() {
        return (activityThread == Thread.currentThread());
    }

    public View getContentView() {
        return contentView;
    }

    @Override
    public void setContentView(View view) {
        logger.debug("set content view: " + view);
        super.setContentView(view);

        contentView = view;
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        Drawable phoneCallIcon = ContextCompat.getDrawable(
                this, android.R.drawable.stat_sys_phone_call
        );
        int statusBarHeight = 0;
        if (!windowFullscreen) {
            statusBarHeight = phoneCallIcon.getIntrinsicHeight();
        }

        Display display = getWindowManager().getDefaultDisplay();
        AndroidDeviceDisplay deviceDisplay = (AndroidDeviceDisplay) DeviceFactory.getDevice().getDeviceDisplay();
        deviceDisplay.displayRectangleWidth = display.getWidth();
        deviceDisplay.displayRectangleHeight = display.getHeight() - statusBarHeight;
        MIDletAccess ma = MIDletBridge.getMIDletAccess();
        if (ma == null) {
            return;
        }
        DisplayAccess da = ma.getDisplayAccess();
        if (da != null) {
            da.sizeChanged();
            deviceDisplay.repaint(0, 0, deviceDisplay.getFullWidth(), deviceDisplay.getFullHeight());
        }
    }
}
