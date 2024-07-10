/*
 *  MicroEmulator
 *  Copyright (C) 2002 Bartek Teodorczyk <barteo@barteo.net>
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

package org.microemu.device.j2se;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;

import org.microemu.device.EmulatorContext;
import org.microemu.device.impl.DeviceImpl;
import org.microemu.device.impl.ui.CommandImplUI;
import org.microemu.device.j2se.ui.J2SECanvasUI;
import org.microemu.device.j2se.ui.J2SETextBoxUI;
import org.microemu.device.j2se.ui.J2SETextFieldUI;
import org.microemu.device.ui.CanvasUI;
import org.microemu.device.ui.CommandUI;
import org.microemu.device.ui.EventDispatcher;
import org.microemu.device.ui.TextBoxUI;
import org.microemu.device.ui.TextFieldUI;
import org.microemu.device.ui.UIFactory;

public class J2SEDevice extends DeviceImpl {

	private UIFactory ui = new UIFactory() {
		
		public EventDispatcher createEventDispatcher(Display display) {
			EventDispatcher eventDispatcher = new EventDispatcher();
			Thread thread = new Thread(eventDispatcher, EventDispatcher.EVENT_DISPATCHER_NAME);
			thread.setDaemon(true);
			thread.start();
			
			return eventDispatcher;
		}

		public CommandUI createCommandUI(Command command) {
			return new CommandImplUI(command);
		}

		public CanvasUI createCanvasUI(Canvas canvas) {
			return new J2SECanvasUI(canvas);
		}
		
		public TextBoxUI createTextBoxUI(TextBox textBox) {
			return new J2SETextBoxUI(textBox);
		}

		public TextFieldUI createTextFieldUI(TextField textField) {
			return new J2SETextFieldUI(textField);
		}

	};

	public J2SEDevice(EmulatorContext context) {
		super(context);
	}

	@Override
	public void init() {

	}

	public UIFactory getUIFactory() {
		return ui;
	}

	@Override
	public void minimizeApp() {
		
	}

}
