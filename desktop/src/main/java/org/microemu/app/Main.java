/**
 *  MicroEmulator
 *  Copyright (C) 2001 Bartek Teodorczyk <barteo@barteo.net>
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
 */

package org.microemu.app;

import Client.StaticData;
import org.bombusmod.util.AssetsLoader;
import org.bombusmod.util.VersionInfo;
import org.microemu.DisplayAccess;
import org.microemu.MIDletAccess;
import org.microemu.MIDletBridge;
import org.microemu.app.ui.swing.*;
import org.microemu.app.util.FileRecordStoreManager;
import org.microemu.device.*;
import org.microemu.device.impl.Color;
import org.microemu.device.impl.DeviceDisplayImpl;
import org.microemu.device.impl.Rectangle;
import org.microemu.device.impl.SoftButton;
import org.microemu.device.j2se.J2SEDevice;
import org.microemu.device.j2se.J2SEDeviceDisplay;
import org.microemu.device.j2se.J2SEFontManager;
import org.microemu.device.j2se.J2SEInputMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.TimerTask;

public class Main extends JFrame {

	private static Logger logger = LoggerFactory.getLogger("ui");

	private static final long serialVersionUID = 1L;

	protected Common common;

	public static SwingDeviceComponent devicePanel;

	protected EmulatorContext emulatorContext = new EmulatorContext() {

		private InputMethod inputMethod = new J2SEInputMethod();

		private DeviceDisplay deviceDisplay = new J2SEDeviceDisplay(this);

		private FontManager fontManager = new J2SEFontManager();

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
            return MIDletBridge.getCurrentMIDlet().getClass().getResourceAsStream(name);
		}
		
		public boolean platformRequest(final String URL) {
			new Thread(new Runnable() {
				public void run() {
					logger.info("MIDlet requests that the device handle the following URL: " + URL);
				}
			}).start();

			return false;
		}
	};


	private WindowAdapter windowListener = new WindowAdapter() {
		public void windowClosing(WindowEvent ev) {
			//menuExitListener.actionPerformed(null);
		}

		public void windowIconified(WindowEvent ev) {
			MIDletBridge.getMIDletAccess(MIDletBridge.getCurrentMIDlet()).pauseApp();
		}

		public void windowDeiconified(WindowEvent ev) {
			try {
				MIDletBridge.getMIDletAccess(MIDletBridge.getCurrentMIDlet()).startApp();
			} catch (MIDletStateChangeException ex) {
				System.err.println(ex);
			}
		}
	};

	public Main() {
		setTitle("BombusMod");

		addWindowListener(windowListener);

		getContentPane().add(createContents(getContentPane()), "Center");

		StaticData.getInstance().setAssetsLoader(new AssetsLoader() {
			@Override
			public InputStream getResourceAsStream(String resource) {
				return Thread.currentThread().getContextClassLoader().getResourceAsStream(resource.substring(1));
			}
		});
		StaticData.getInstance().setVersionInfo(new VersionInfo() {
			@Override
			public String getVersionNumber() {
				return "X";
			}
		});
		this.common = new Common(emulatorContext);
		common.setRecordStoreManager(new FileRecordStoreManager());
		J2SEDevice device = new J2SEDevice(emulatorContext);
		common.setDevice(device);
		J2SEDeviceDisplay deviceDisplay = (J2SEDeviceDisplay)emulatorContext.getDeviceDisplay();
		deviceDisplay.setBackgroundColor(new Color(0x000000));
		deviceDisplay.setForegroundColor(new Color(0xffffff));
		deviceDisplay.setResizable(true);
		setDeviceSize(deviceDisplay, 640, 480);
		MIDlet midlet = common.initMIDlet(false);
		SwingUtilities.invokeLater(new Runnable() {
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
		});

		updateDevice();
	}

	protected Component createContents(Container parent) {
		devicePanel = new SwingDeviceComponent();
		devicePanel.addKeyListener(devicePanel);
		addKeyListener(devicePanel);

		return devicePanel;
	}
	
	public static void setDeviceSize(DeviceDisplayImpl deviceDisplay, int width, int height) {
	    // move the soft buttons
	    int menuh = 0;
	    Enumeration en = DeviceFactory.getDevice().getSoftButtons().elements();
        while (en.hasMoreElements()) {
            SoftButton button = (SoftButton) en.nextElement();
            Rectangle paintable = button.getPaintable();
            paintable.y = height - paintable.height;
            menuh = paintable.height;
        }
        // resize the display area
        deviceDisplay.setDisplayPaintable(new Rectangle(0, 0, width, height - menuh));
        deviceDisplay.setDisplayRectangle(new Rectangle(0, 0, width, height));
        ((SwingDisplayComponent) devicePanel.getDisplayComponent()).init();
        // update display
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

	protected void updateDevice() {
		devicePanel.init();

		pack();

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);
		devicePanel.requestFocus();
	}

	public static void main(String args[]) {
		List params = new ArrayList();
		StringBuffer debugArgs = new StringBuffer();
		for (int i = 0; i < args.length; i++) {
			params.add(args[i]);
			if (debugArgs.length() != 0) {
				debugArgs.append(", ");
			}
			debugArgs.append("[").append(args[i]).append("]");
		}

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ex) {
			logger.error("UI Error", ex);
		}

		new Main();
	}

}
