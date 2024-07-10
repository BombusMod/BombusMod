/**
 *  MicroEmulator
 *  Copyright (C) 2008 Markus Heberling <markus@heberling.net>
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
 *  @version $Id$
 */
package org.microemu.iphone.device;

import org.microemu.device.*;
import org.microemu.device.ui.*;
import org.microemu.iphone.ThreadDispatcher;
import org.microemu.iphone.device.ui.IPhoneCanvasUI;
import org.microemu.iphone.device.ui.IPhoneCommandUI;
import org.microemu.iphone.device.ui.IPhoneTextBoxUI;
import org.robovm.apple.uikit.UIView;

import javax.microedition.lcdui.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class IPhoneDevice implements Device {
	private UIFactory ui = new UIFactory() {

		public EventDispatcher createEventDispatcher(Display display) {
			final EventDispatcher eventDispatcher = new EventDispatcher() {

				@Override
				protected void post(Event event) {
					ThreadDispatcher.dispatchOnMainThread(event, false);
				}

			};

			Thread thread = new Thread(eventDispatcher, EventDispatcher.EVENT_DISPATCHER_NAME);
			thread.setDaemon(true);
			thread.start();

			return eventDispatcher;
		}

		public CanvasUI createCanvasUI(Canvas canvas) {
			return new IPhoneCanvasUI(emulatorContext, parentView, canvas);
		}

		public TextBoxUI createTextBoxUI(TextBox textBox) {
			return new IPhoneTextBoxUI(emulatorContext, parentView, textBox);
		}

		@Override
		public TextFieldUI createTextFieldUI(TextField textField) {
			return null;
		}

		public CommandUI createCommandUI(Command command) {
			return new IPhoneCommandUI(null, command);
		}

	};

	private EmulatorContext emulatorContext;

	private Map systemProperties = new HashMap();

	private Vector softButtons = new Vector();

	private UIView parentView;

	public IPhoneDevice(EmulatorContext emulatorContext, UIView parentView) {
		this.emulatorContext = emulatorContext;
		this.parentView = parentView;
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

	}

	public UIFactory getUIFactory() {
		return ui;
	}

	public boolean hasPointerEvents() {
		return true;
	}

	public boolean hasPointerMotionEvents() {
		return true;
	}

	public boolean hasRepeatEvents() {
		// TODO Auto-generated method stub
		return false;
	}

	public void init() {
		// TODO Auto-generated method stub

	}

	public boolean vibrate(int duration) {
		// TODO Auto-generated method stub
		return false;
	}

}
