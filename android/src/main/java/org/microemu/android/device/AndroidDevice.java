/**
 *  MicroEmulator
 *  Copyright (C) 2008 Bartek Teodorczyk <barteo@barteo.net>
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
 *  @version $Id: AndroidDevice.java 2236 2009-12-07 09:49:31Z barteo@gmail.com $
 */

package org.microemu.android.device;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;

import org.bombusmod.BombusModActivity;
import org.microemu.android.device.ui.AndroidCanvasUI;
import org.microemu.android.device.ui.AndroidCommandUI;
import org.microemu.android.device.ui.AndroidTextBoxUI;
import org.microemu.android.device.ui.AndroidTextFieldUI;
import org.microemu.device.Device;
import org.microemu.device.DeviceDisplay;
import org.microemu.device.EmulatorContext;
import org.microemu.device.FontManager;
import org.microemu.device.InputMethod;
import org.microemu.device.ui.CanvasUI;
import org.microemu.device.ui.CommandUI;
import org.microemu.device.ui.EventDispatcher;
import org.microemu.device.ui.TextBoxUI;
import org.microemu.device.ui.TextFieldUI;
import org.microemu.device.ui.UIFactory;

import android.content.Context;
import android.os.Vibrator;

public class AndroidDevice implements Device {

	private EmulatorContext emulatorContext;
	
	private BombusModActivity activity;
	
	private UIFactory ui = new UIFactory() {

		public EventDispatcher createEventDispatcher(Display display) {
			EventDispatcher eventDispatcher = new EventDispatcher();
			Thread thread = new Thread(eventDispatcher, EventDispatcher.EVENT_DISPATCHER_NAME);
			thread.setDaemon(true);
			thread.start();

			return eventDispatcher;
		}

		public CommandUI createCommandUI(Command command) {
			return new AndroidCommandUI(command);
		}

		public CanvasUI createCanvasUI(Canvas canvas) {
			return new AndroidCanvasUI(activity, canvas);
		}
		
		public TextBoxUI createTextBoxUI(TextBox textBox) {
			return new AndroidTextBoxUI(activity, textBox);
		}

		public TextFieldUI createTextFieldUI(TextField textField) {
			return new AndroidTextFieldUI(activity, textField);
		}

	};
	
	private Map systemProperties = new HashMap();
	
	private Vector softButtons = new Vector();
	
	public AndroidDevice(EmulatorContext emulatorContext, BombusModActivity activity) {
		this.emulatorContext = emulatorContext;
		this.activity = activity;
	}

	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	public Vector getButtons() {
		// TODO Auto-generated method stub
		return null;
	}

	public DeviceDisplay getDeviceDisplay() {
		return emulatorContext.getDeviceDisplay();
	}

	public FontManager getFontManager() {
		return emulatorContext.getDeviceFontManager();
	}

	public InputMethod getInputMethod() {
		return emulatorContext.getDeviceInputMethod();
	}
	
	public UIFactory getUIFactory() {
		return ui;
	}

	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	public Image getNormalImage() {
		// TODO Auto-generated method stub
		return null;
	}

	public Image getOverImage() {
		// TODO Auto-generated method stub
		return null;
	}

	public Image getPressedImage() {
		// TODO Auto-generated method stub
		return null;
	}

	public Vector getSoftButtons() {
		return softButtons;
	}

	public Map getSystemProperties() {
		return systemProperties;
	}

	@Override
	public void minimizeApp() {
		activity.minimizeApp();
	}

	public boolean hasPointerEvents() {
		return true;
	}

	public boolean hasPointerMotionEvents() {
		return true;
	}

	public boolean hasRepeatEvents() {
		return true;
	}

	public void init() {
		// TODO Auto-generated method stub
		
	}

	public boolean vibrate(final int duration) {
		final Vibrator vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator == null)
            return false;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                    vibrator.vibrate(duration);
            }
        });
        return true;
	}

}
