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
 *  
 *  Contributor(s):
 *    Andres Navarro
 */

package org.microemu.device;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public interface DeviceDisplay 
{
	
	boolean flashBacklight(int duration);
	
	int getWidth();

	int getHeight();

	int getFullWidth();

	int getFullHeight();
	
	boolean isFullScreenMode();

    int numAlphaLevels();

    int numColors();

	void repaint(int x, int y, int width, int height);

	void setScrollDown(boolean state);

	void setScrollUp(boolean state);

	Image createImage(String name) throws IOException;

	Image createImage(byte[] imageData, int imageOffset, int imageLength);

	Image createImage(InputStream is) throws IOException;
}
