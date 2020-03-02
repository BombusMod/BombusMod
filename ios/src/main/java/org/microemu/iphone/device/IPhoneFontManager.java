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

import javax.microedition.lcdui.Font;

import org.microemu.device.FontManager;
import org.microemu.iphone.ThreadDispatcher;
import org.robovm.apple.foundation.NSString;
import org.robovm.apple.uikit.UIFont;
import org.robovm.apple.uikit.UIFontTextStyle;

public class IPhoneFontManager implements FontManager {

	private UIFont uifont;
	
	public IPhoneFontManager() {
		ThreadDispatcher.dispatchOnMainThread(new Runnable(){public void run() {
			uifont= UIFont.getPreferredFont(UIFontTextStyle.Body);
		}}, false);
	}
	
	public UIFont getUIFont(Font font){
		return uifont;
	}
	
	public int charWidth(Font f, char ch) {
		return stringWidth(f, String.valueOf(ch));
	}

	public int charsWidth(Font f, char[] ch, int offset, int length) {
		return stringWidth(f, String.valueOf(ch, offset, length));
	}

	public int getBaselinePosition(Font f) {
		return (int)(uifont.getCapHeight());
	}

	public int getHeight(Font f) {
		return (int)(uifont.getLineHeight());
	}

	public void init() {
		// TODO Auto-generated method stub

	}

	public int stringWidth(Font f, String str) {
		NSString nsstring = new NSString(str);
		return (int)nsstring.getSize(getUIFont(f)).getWidth();
	}

	@Override
	public int substringWidth(Font f, String str, int offset, int len) {
		return 0;
	}

}
