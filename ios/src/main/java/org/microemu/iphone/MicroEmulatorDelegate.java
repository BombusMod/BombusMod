/**
 *  MicroEmulator
 *  Copyright (C) 2008 Bartek Teodorczyk <barteo@barteo.net>
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
package org.microemu.iphone;

import org.bombusmod.ios.RootViewController;
import org.microemu.MIDletBridge;
import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.uikit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.microedition.midlet.MIDletStateChangeException;

public class MicroEmulatorDelegate extends UIApplicationDelegateAdapter {

	private static final Logger logger = LoggerFactory.getLogger(MicroEmulatorDelegate.class.getSimpleName());

	private static final class ExceptionHandler implements Thread.UncaughtExceptionHandler {
		public void uncaughtException(Thread arg0, Throwable arg1) {
			logger.error("Uncaught exception in thread: " + arg0.getName());
			arg1.printStackTrace();
			System.exit(-1);
		}
	}

	public static void main(String[] args) {
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());
		try (NSAutoreleasePool pool = new NSAutoreleasePool()) {
			UIApplication.main(args, null, MicroEmulatorDelegate.class);
		}
	}

	@Override
	public boolean didFinishLaunching(UIApplication application, UIApplicationLaunchOptions launchOptions) {
		window = new UIWindow(UIScreen.getMainScreen().getBounds());
		window.setRootViewController(new RootViewController());
		window.makeKeyAndVisible();
		return true;
	}
	
	@Override
	public void willEnterForeground(UIApplication application) {
		logger.debug("MicroEmulator.applicationDidResume()");
		try {
			MIDletBridge.getMIDletContext().getMIDletAccess().startApp();
		} catch (MIDletStateChangeException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void didEnterBackground(UIApplication application) {
		logger.debug("MicroEmulator.applicationWillSuspend()");
		MIDletBridge.getMIDletContext().getMIDletAccess().pauseApp();
	}

	private UIWindow window;

	public UIWindow getWindow() {
		return window;
	}

}