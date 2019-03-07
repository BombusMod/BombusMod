package org.bombusmod.ios;

import Client.StaticData;
import org.bombusmod.util.AssetsLoader;
import org.bombusmod.util.ClipBoardIO;
import org.bombusmod.util.EventNotifier;
import org.bombusmod.util.VersionInfo;
import org.microemu.MIDletAccess;
import org.microemu.MIDletBridge;
import org.microemu.app.Common;
import org.microemu.device.DeviceDisplay;
import org.microemu.device.EmulatorContext;
import org.microemu.device.FontManager;
import org.microemu.device.InputMethod;
import org.microemu.iphone.MicroEmulatorDelegate;
import org.microemu.iphone.device.*;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIViewController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import java.io.InputStream;

public class RootViewController extends UIViewController {

    private static final Logger logger = LoggerFactory.getLogger(RootViewController.class);

    public Common common;
    private MIDlet midlet;
    protected EmulatorContext emulatorContext;

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();
        getView().setBackgroundColor(UIColor.white());
        final MicroEmulatorDelegate microEmulator = (MicroEmulatorDelegate)UIApplication.getSharedApplication().getDelegate();
        StaticData sd = StaticData.getInstance();
        sd.setAssetsLoader(new AssetsLoader() {
            @Override
            public InputStream getResourceAsStream(String resource) {
                return getClass().getResourceAsStream(resource);
            }
        });
        sd.setVersionInfo(new VersionInfo() {
            @Override
            public String getVersionNumber() {
                return "0.9.x";
            }
        });
        sd.setEventNotifier(new EventNotifier() {
            @Override
            public void startNotify(String soundMediaType, String soundFileName, int sndVolume, int vibraLength) {
                logger.info("Event: {}", soundFileName);
            }
        });
        emulatorContext = new EmulatorContext() {

            private InputMethod inputMethod = new IPhoneInputMethod();

            private DeviceDisplay deviceDisplay = new IPhoneDeviceDisplay(this, getView());

            private FontManager fontManager = new IPhoneFontManager();

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
                return getClass().getResourceAsStream(name);
            }

            public boolean platformRequest(String url)
            {
                return true;
            }
        };
        common = new Common(emulatorContext);
        common.setRecordStoreManager(new IPhoneRecordStoreManager(microEmulator));
        common.setDevice(new IPhoneDevice(emulatorContext, getView()));
        midlet = common.initMIDlet(false);
        ClipBoardIO.getInstance();
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
            }
        }).start();
    }
}
