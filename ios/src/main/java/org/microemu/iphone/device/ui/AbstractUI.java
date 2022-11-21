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
package org.microemu.iphone.device.ui;

import org.microemu.device.EmulatorContext;
import org.microemu.device.ui.CommandUI;
import org.microemu.device.ui.DisplayableUI;
import org.microemu.iphone.ThreadDispatcher;
import org.robovm.apple.foundation.NSMutableArray;
import org.robovm.apple.foundation.NSObject;
import org.robovm.apple.uikit.UIBarButtonItem;
import org.robovm.apple.uikit.UIBarButtonItemStyle;
import org.robovm.apple.uikit.UIToolbar;
import org.robovm.apple.uikit.UIView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import java.util.Vector;

public abstract class AbstractUI<T extends Displayable> extends NSObject implements DisplayableUI {

	private static final Logger logger = LoggerFactory.getLogger(AbstractUI.class.getSimpleName());

	public static final int NAVIGATION_HEIGHT = 40;

	public static final int TOOLBAR_HEIGHT = 40;

	protected Vector<CommandUI> commands = new Vector<CommandUI>();

	protected CommandListener commandListener;

	protected UIToolbar toolbar;

	protected T displayable;

	protected EmulatorContext emulatorContext;

	protected UIView view;

	protected AbstractUI(EmulatorContext emulatorContext, UIView view, T displayable) {
		super();
		this.emulatorContext = emulatorContext;
		this.view = view;
		this.displayable = displayable;
	}

	public void addCommandUI(CommandUI cmd) {
		commands.add(cmd);
		ThreadDispatcher.dispatchOnMainThread(new Runnable() {
			public void run() {
				updateToolbar();
			}
		}, false);
	}

	protected void updateToolbar() {
		if (toolbar != null) {
			NSMutableArray items = new NSMutableArray(commands.size());
			for (int i = 0; i < commands.size(); i++) {
				CommandUI command = commands.get(i);
				logger.debug(command.getCommand().getLabel());
				UIBarButtonItem item = new UIBarButtonItem(command.getCommand().getLabel(), UIBarButtonItemStyle.Plain);
				final int itemIndex = i;
				item.setOnClickListener(new UIBarButtonItem.OnClickListener() {
					@Override
					public void onClick(UIBarButtonItem uiBarButtonItem) {
						Command command = commands.get(itemIndex).getCommand();
						commandListener.commandAction(command, displayable);
					}
				});
			}
			toolbar.setItems(items);
		}
	}

	public void removeCommandUI(CommandUI cmd) {
		commands.remove(cmd);
		ThreadDispatcher.dispatchOnMainThread(new Runnable() {
			public void run() {
				updateToolbar();
			}
		}, false);
	}

	public void setCommandListener(CommandListener l) {
		commandListener = l;
	}

	class CommandCaller extends NSObject {
		private Command command;

		private CommandCaller(Command command) {
			super();
			this.command = command;
		}


	}
	
	public Vector getCommandsUI() {
		return commands;
	}
}
