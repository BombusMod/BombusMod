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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import org.microemu.MIDletAccess;
import org.microemu.MIDletBridge;
import org.microemu.MIDletContext;
import org.microemu.MIDletEntry;
import org.microemu.MicroEmulator;
import org.microemu.RecordStoreManager;
import org.microemu.app.launcher.Launcher;
import org.microemu.app.ui.Message;
import org.microemu.app.ui.ResponseInterfaceListener;
import org.microemu.app.ui.StatusBarListener;
import org.microemu.app.util.IOUtils;
import org.microemu.app.util.MIDletSystemProperties;
import org.microemu.app.util.MIDletThread;
import org.microemu.device.Device;
import org.microemu.device.DeviceFactory;
import org.microemu.device.EmulatorContext;
import org.microemu.log.Logger;
import org.microemu.microedition.ImplFactory;
import org.microemu.microedition.ImplementationInitialization;
import org.microemu.microedition.io.ConnectorImpl;
import org.microemu.util.Base64Coder;
import org.microemu.util.JadProperties;

public class Common implements MicroEmulator, CommonInterface {

    public JadProperties jad = new JadProperties();

    protected EmulatorContext emulatorContext;

    private static Common instance;

    private static Launcher launcher;

    private static StatusBarListener statusBarListener = null;

    private JadProperties manifest = new JadProperties();

    private RecordStoreManager recordStoreManager;

    private ResponseInterfaceListener responseInterfaceListener = null;

    public Vector extensions = new Vector();

    private boolean useSystemClassLoader = false;

    private boolean autoTests = false;

    private String propertiesJad = null;

    private String midletClassOrUrl = null;

    private String jadURL = null;
    
    private String midletSuiteName = null;

    private Object destroyNotify = new Object();

    private boolean exitOnMIDletDestroy = false;

    public Common(EmulatorContext context) {
        instance = this;
        this.emulatorContext = context;

        /*
         * Initialize secutity context for implemenations, May be there are better place
         * for this call
         */
        ImplFactory.instance();
        MIDletSystemProperties.initContext();
        // TODO integrate with ImplementationInitialization
        ImplFactory.registerGCF(ImplFactory.DEFAULT, new ConnectorImpl());

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

        String result = jad.getProperty(key);
        if (result == null) {
            result = manifest.getProperty(key);
        }

        return result;
    }

    public InputStream getResourceAsStream(Class origClass, String name) {
        return emulatorContext.getResourceAsStream(origClass, name);
    }

    public void notifyDestroyed(MIDletContext midletContext) {
        Logger.debug("notifyDestroyed");
        notifyImplementationMIDletDestroyed();
        startLauncher(midletContext);
    }

    public void destroyMIDletContext(MIDletContext midletContext) {
        if ((midletContext != null) && (MIDletBridge.getMIDletContext() == midletContext) && !midletContext.isLauncher()) {
            Logger.debug("destroyMIDletContext");
        }
        MIDletThread.contextDestroyed(midletContext);
        synchronized (destroyNotify) {
            destroyNotify.notifyAll();
        }
    }

    public Launcher getLauncher() {
        return launcher;
    }

    public static void dispose() {
        try {
            MIDletAccess midletAccess = MIDletBridge.getMIDletAccess();
            if (midletAccess != null) {
                midletAccess.destroyApp(true);
            }
        } catch (MIDletStateChangeException ex) {
            Logger.error(ex);
        }
        // TODO to be removed when event dispatcher will run input method task
        DeviceFactory.getDevice().getInputMethod().dispose();
    }

    public static boolean isMIDletUrlExtension(String nameString) {
        if (nameString == null) {
            return false;
        }
        // Remove query
        if (nameString.startsWith("http://") || nameString.startsWith("https://")) {
            int s = nameString.lastIndexOf('?');
            if (s != -1) {
                nameString = nameString.substring(0, s);
            }
        }
        int end = nameString.lastIndexOf('.');
        if (end == -1) {
            return false;
        }

        return (nameString.substring(end + 1, nameString.length()).toLowerCase(Locale.ENGLISH).equals("jad") || nameString.substring(end + 1,
                nameString.length()).toLowerCase(Locale.ENGLISH).equals("jar"));
    }

  


    protected String saveJar2TmpFile(String jarUrl, boolean reportError) {
        InputStream is = null;
        try {
            URL url = new URL(jad.getJarURL());
            URLConnection conn = url.openConnection();
            if (url.getUserInfo() != null) {
                String userInfo = new String(Base64Coder.encode(url.getUserInfo().getBytes("UTF-8")));
                conn.setRequestProperty("Authorization", "Basic " + userInfo);
            }
            is = conn.getInputStream();
            File tmpDir = null;
            String systemTmpDir = MIDletSystemProperties.getSystemProperty("java.io.tmpdir");
            if (systemTmpDir != null) {
                tmpDir = new File(systemTmpDir, "microemulator-apps-" + MIDletSystemProperties.getSystemProperty("user.name"));
                if ((!tmpDir.exists()) && (!tmpDir.mkdirs())) {
                    tmpDir = null;
                }
            }
            File tmp = File.createTempFile("me2-app-", ".jar", tmpDir);
            tmp.deleteOnExit();
            IOUtils.copyToFile(is, tmp);
            return IOUtils.getCanonicalFileClassLoaderURL(tmp);
        } catch (IOException e) {
            if (reportError) {
                Message.error("Unable to open jar " + jarUrl, e);
            }
            return null;
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    private MIDlet loadMidlet(Class midletClass, MIDletAccess previousMidletAccess) {
        try {
            if (previousMidletAccess != null) {
                previousMidletAccess.destroyApp(true);
            }
        } catch (Throwable e) {
            Message.error("Unable to destroy MIDlet, " + Message.getCauseMessage(e), e);
        }

        MIDletContext context = new MIDletContext();
        MIDletBridge.setThreadMIDletContext(context);
        MIDletBridge.getRecordStoreManager().init(MIDletBridge.getMicroEmulator());
        try {
            MIDlet m;

            final String errorTitle = "Error starting MIDlet";

            try {
                Object object = midletClass.newInstance();
                if (!(object instanceof MIDlet)) {
                    Message.error(errorTitle, "Class " + midletClass.getName() + " should extend MIDlet");
                    return null;
                }
                m = (MIDlet) object;
            } catch (Throwable e) {
                Message.error(errorTitle, "Unable to create MIDlet, " + Message.getCauseMessage(e), e);
                MIDletBridge.destroyMIDletContext(context);
                return null;
            }

            try {
                if (context.getMIDlet() != m) {
                    throw new Error("MIDlet Context corrupted");
                }

                notifyImplementationMIDletStart();
                return m;
            } catch (Throwable e) {
                Message.error(errorTitle, "Unable to start MIDlet, " + Message.getCauseMessage(e), e);
                MIDletBridge.destroyMIDletContext(context);
                return null;
            }

        } finally {
            MIDletBridge.setThreadMIDletContext(null);
        }

    }

    protected void startLauncher(MIDletContext midletContext) {
        if ((midletContext != null) && (midletContext.isLauncher())) {
            return;
        }
        if (midletContext != null) {
            try {
                MIDletAccess previousMidletAccess = midletContext.getMIDletAccess();
                if (previousMidletAccess != null) {
                    previousMidletAccess.destroyApp(true);
                }
            } catch (Throwable e) {
                Logger.error("destroyApp error", e);
            }

            if (exitOnMIDletDestroy) {
                System.exit(0);
            }
        }

        try {
            launcher = new Launcher(this);
            MIDletBridge.getMIDletAccess(launcher).startApp();
        } catch (Throwable e) {
            Message.error("Unable to start launcher MIDlet, " + Message.getCauseMessage(e), e);
            handleStartMidletException(e);
        } finally {
            MIDletBridge.setThreadMIDletContext(null);
        }
    }

    public void setStatusBarListener(StatusBarListener listener) {
        statusBarListener = listener;
    }

    public int checkPermission(String permission) {
        return MIDletSystemProperties.getPermission(permission);
    }

    public boolean platformRequest(final String URL) throws ConnectionNotFoundException {
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


    public void notifyImplementationMIDletStart() {
        for (Iterator iterator = extensions.iterator(); iterator.hasNext();) {
            ImplementationInitialization impl = (ImplementationInitialization) iterator.next();
            impl.notifyMIDletStart();
        }
    }

    public void notifyImplementationMIDletDestroyed() {
        for (Iterator iterator = extensions.iterator(); iterator.hasNext();) {
            ImplementationInitialization impl = (ImplementationInitialization) iterator.next();
            impl.notifyMIDletDestroyed();
        }
    }


    private static JadProperties loadJadProperties(String urlString) throws IOException {
        JadProperties properties = new JadProperties();

        URL url = new URL(urlString);
        if (url.getUserInfo() == null) {
            properties.read(url.openStream());
        } else {
            URLConnection cn = url.openConnection();
            String userInfo = new String(Base64Coder.encode(url.getUserInfo().getBytes("UTF-8")));
            cn.setRequestProperty("Authorization", "Basic " + userInfo);
            properties.read(cn.getInputStream());
        }

        return properties;
    }

    public MIDlet initMIDlet(boolean startMidlet) {
        Class midletClass = null;


        try {
            midletClass = instance.getClass().getClassLoader().loadClass("midlet.BombusMod");
        } catch (Exception e) {
            Message.error("Error", "Unable to find MIDlet class, " + Message.getCauseMessage(e), e);
            return null;
        }

        MIDlet midlet = null;


        if (midletClass != null && propertiesJad != null) {
            try {
                jad = loadJadProperties(propertiesJad);
            } catch (IOException e) {
                Logger.error("Cannot load " + propertiesJad + " URL", e);
            }
        }

        if (midletClass == null) {
            if (launcher == null) {
                try {
                    launcher = new Launcher(this);
                } finally {
                    MIDletBridge.setThreadMIDletContext(null);
                }
            }
            MIDletEntry entry = launcher.getSelectedMidletEntry();
            if (entry != null) {
                midlet = initMIDlet(startMidlet, entry);
            }
        } else {
            midlet = loadMidlet(midletClass, MIDletBridge.getMIDletAccess());
            if (startMidlet) {
                try {
                    MIDletBridge.getMIDletAccess(midlet).startApp();
                } catch (MIDletStateChangeException e) {
                    Logger.error(e);
                }
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
                Logger.error(e);
            }
        }

        return midlet;
    }

    public static String usage() {
        return "[(-d | --device) ({device descriptor} | {device class name}) ] \n" + "[--rms (file | memory)] \n" + "[--id EmulatorID ] \n"
                + "[--impl {JSR implementation class name}]\n" + "[(--classpath|-cp) <JSR CLASSPATH>]\n" + "[(--appclasspath|--appcp) <MIDlet CLASSPATH>]\n"
                + "[--appclass <library class name>]\n" + "[--appclassloader strict|relaxed|delegating|system] \n" + "[-Xautotest:<JAD file url>\n"
                + "[--quit]\n" + "[--logCallLocation true|false]\n" + "[--traceClassLoading\n[--traceSystemClassLoading]\n[--enhanceCatchBlock]\n]"
                + "[--resizableDevice {width} {height}]\n"
                + "(({MIDlet class name} [--propertiesjad {jad file location}]) | {jad file location} | {jar file location})";
    }

}
