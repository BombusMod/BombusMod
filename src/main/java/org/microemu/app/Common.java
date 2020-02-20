/**
 *  MicroEmulator
 *  Copyright (C) 2001-2003 Bartek Teodorczyk <barteo@barteo.net>
 *
 *  It is licensed under the following two licenses as alternatives:
 *    1. GNU Lesser General Public License (the "LGPL") version 2.1 or any newer version
 *    2. Apache License (the "AL") Version 2.0
 *
 *  You may not use this file except in compliance with at least one of
 *  the above two licenses.
 *
 *  You may obtain a copy of the LGPL at
 *      http://www.gnu.org/licenses/old-licenses/lgpl-2.1.txt
 *
 *  You may obtain a copy of the AL at
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the LGPL or the AL for the specific language governing permissions and
 *  limitations.
 *
 *  @version $Id: Common.java 2517 2011-11-10 12:30:37Z barteo@gmail.com $
 */
package org.microemu.app;

import java.io.InputStream;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;

import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import android.util.Log;
import org.bombusmod.BombusModActivity;
import org.microemu.MIDletAccess;
import org.microemu.MIDletBridge;
import org.microemu.MIDletContext;
import org.microemu.MIDletEntry;
import org.microemu.MicroEmulator;
import org.microemu.RecordStoreManager;
import org.microemu.app.ui.ResponseInterfaceListener;
import org.microemu.app.ui.StatusBarListener;
import org.microemu.app.util.MIDletSystemProperties;
import org.microemu.app.util.MIDletThread;
import org.microemu.device.Device;
import org.microemu.device.DeviceFactory;
import org.microemu.device.EmulatorContext;

public class Common implements MicroEmulator, CommonInterface {

    protected EmulatorContext emulatorContext;

    private static Common instance;

    private static StatusBarListener statusBarListener = null;

   private RecordStoreManager recordStoreManager;

    private ResponseInterfaceListener responseInterfaceListener = null;

    public Vector extensions = new Vector();
    
    private String midletSuiteName = null;

    private final Object destroyNotify = new Object();

    public Common(EmulatorContext context) {
        instance = this;
        this.emulatorContext = context;

        MIDletSystemProperties.initContext();

        MIDletBridge.setMicroEmulator(this);
    }

    public RecordStoreManager getRecordStoreManager() {
        return recordStoreManager;
    }

    public void setRecordStoreManager(RecordStoreManager manager) {
        this.recordStoreManager = manager;
    }

    public String getAppProperty(String key) {
        if (key.equals("microedition.platform")) {
            return "MicroEmulator";
        } else if (key.equals("microedition.profiles")) {
            return "MIDP-2.0";
        } else if (key.equals("microedition.configuration")) {
            return "CLDC-1.0";
        } else if (key.equals("microedition.locale")) {
            return Locale.getDefault().getLanguage();
        } else if (key.equals("microedition.encoding")) {
            return System.getProperty("file.encoding");
        }
        return null;
    }

    public InputStream getResourceAsStream(Class origClass, String name) {
        return emulatorContext.getResourceAsStream(origClass, name);
    }

    public void notifyDestroyed(MIDletContext midletContext) {
        Log.d(BombusModActivity.LOG_TAG, "notifyDestroyed");
        startLauncher(midletContext);
    }

    public void destroyMIDletContext(MIDletContext midletContext) {
        if ((midletContext != null) && (MIDletBridge.getMIDletContext() == midletContext)) {
            Log.d(BombusModActivity.LOG_TAG, "destroyMIDletContext");
        }
        MIDletThread.contextDestroyed(midletContext);
        synchronized (destroyNotify) {
            destroyNotify.notifyAll();
        }
    }

    public static void dispose() {
        try {
            MIDletAccess midletAccess = MIDletBridge.getMIDletAccess();
            if (midletAccess != null) {
                midletAccess.destroyApp(true);
            }
        } catch (MIDletStateChangeException ex) {
            Log.e(BombusModActivity.LOG_TAG, "Illegal state", ex);
        }
        // TODO to be removed when event dispatcher will run input method task
        DeviceFactory.getDevice().getInputMethod().dispose();
    }

    private MIDlet loadMidlet(Class midletClass, MIDletAccess previousMidletAccess) {
        try {
            if (previousMidletAccess != null) {
                previousMidletAccess.destroyApp(true);
            }
        } catch (Throwable e) {
            Log.e(BombusModActivity.LOG_TAG,"Unable to destroy MIDlet", e);
        }

        MIDletContext context = new MIDletContext();
        MIDletBridge.setThreadMIDletContext(context);
        MIDletBridge.getRecordStoreManager().init(MIDletBridge.getMicroEmulator());
        try {
            MIDlet m;

            final String errorTitle = "Error starting MIDlet";

            try {
                Object object = midletClass.newInstance();
                m = (MIDlet) object;
            } catch (Throwable e) {
                Log.e(BombusModActivity.LOG_TAG,"Unable to create MIDlet", e);
                MIDletBridge.destroyMIDletContext(context);
                return null;
            }

            try {
                if (context.getMIDlet() != m) {
                    throw new Error("MIDlet Context corrupted");
                }

                return m;
            } catch (Throwable e) {
                Log.d(BombusModActivity.LOG_TAG, "Unable to start MIDlet", e);
                MIDletBridge.destroyMIDletContext(context);
                return null;
            }

        } finally {
            MIDletBridge.setThreadMIDletContext(null);
        }

    }

    protected void startLauncher(MIDletContext midletContext) {
       if (midletContext != null) {
            try {
                MIDletAccess previousMidletAccess = midletContext.getMIDletAccess();
                if (previousMidletAccess != null) {
                    previousMidletAccess.destroyApp(true);
                }
            } catch (Throwable e) {
                Log.e(BombusModActivity.LOG_TAG, "destroyApp error", e);
            }

            System.exit(0);            
        }        
    }

    public void setStatusBarListener(StatusBarListener listener) {
        statusBarListener = listener;
    }

    public int checkPermission(String permission) {
        return MIDletSystemProperties.getPermission(permission);
    }

    public boolean platformRequest(final String URL) {
        return emulatorContext.platformRequest(URL);
    }

    public void setResponseInterfaceListener(ResponseInterfaceListener listener) {
        responseInterfaceListener = listener;
    }

    protected void handleStartMidletException(Throwable e) {

    }
    
    public String getSuiteName() {
    	return midletSuiteName;
    }
    
    public void setSuiteName(String name) {
    	midletSuiteName = name;
    }

    public Device getDevice() {
        return DeviceFactory.getDevice();
    }

    public void setDevice(Device device) {
        MIDletSystemProperties.setDevice(device);
        DeviceFactory.setDevice(device);
    }

    private static Common getInstance() {
        return instance;
    }

    public static void setStatusBar(String text) {
        if (statusBarListener != null) {
            statusBarListener.statusBarChanged(text);
        }
    }

    private void setResponseInterface(boolean state) {
        if (responseInterfaceListener != null) {
            responseInterfaceListener.stateChanged(state);
        }
    }

    public MIDlet initMIDlet(boolean startMidlet) {
        Class midletClass = null;


        try {
            midletClass = instance.getClass().getClassLoader().loadClass("midlet.BombusMod");
        } catch (Exception e) {
            Log.d(BombusModActivity.LOG_TAG, "Unable to find MIDlet class", e);
            return null;
        }

        MIDlet midlet = null;


        midlet = loadMidlet(midletClass, MIDletBridge.getMIDletAccess());
        if (startMidlet) {
            try {
                MIDletBridge.getMIDletAccess(midlet).startApp();
            } catch (MIDletStateChangeException e) {
                Log.e(BombusModActivity.LOG_TAG, "Illegal state", e);
            }
        }

        if (midlet == null) {
            startLauncher(MIDletBridge.getMIDletContext());
        }


        return midlet;
    }

    public MIDlet initMIDlet(boolean startMidlet, MIDletEntry entry) {
        MIDlet midlet = loadMidlet(entry.getMIDletClass(), MIDletBridge.getMIDletAccess());
        if (startMidlet) {
            try {
                MIDletBridge.getMIDletAccess(midlet).startApp();
            } catch (MIDletStateChangeException e) {
                Log.e(BombusModActivity.LOG_TAG, "Illegal state", e);
            }
        }

        return midlet;
    }
}
