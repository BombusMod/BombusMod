/**
 *  MicroEmulator
 *  Copyright (C) 2009 Bartek Teodorczyk <barteo@barteo.net>
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
 *  @version $Id: AndroidCommandUI.java 2031 2009-04-29 18:22:42Z barteo $
 */

package org.microemu.android.device.ui;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Image;

import org.microemu.android.MicroEmulatorActivity;
import org.microemu.android.device.AndroidImmutableImage;
import org.microemu.device.ui.CommandUI;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class AndroidCommandUI implements CommandUI {

	private MicroEmulatorActivity activity;
	
	private Command command;
	
	private Drawable drawable;

	public AndroidCommandUI(MicroEmulatorActivity activity, Command command) {
		this.activity = activity;
		this.command = command;
	}

	public Command getCommand() {
		return command;
	}

	public void setImage(Image image) {
		drawable = new BitmapDrawable(((AndroidImmutableImage) image).getBitmap());
	}
	
	public Drawable getDrawable() {
		return drawable;
	}

}
