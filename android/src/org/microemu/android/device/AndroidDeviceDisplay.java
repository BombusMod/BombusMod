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
 *  @version $Id: AndroidDeviceDisplay.java 2496 2011-05-07 11:27:52Z barteo@gmail.com $
 */

package org.microemu.android.device;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import org.microemu.MIDletBridge;
import org.microemu.android.device.ui.AndroidCanvasUI;
import org.microemu.android.device.ui.AndroidCanvasUI.CanvasView;
import org.microemu.app.ui.DisplayRepaintListener;
import org.microemu.device.DeviceDisplay;
import org.microemu.device.EmulatorContext;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.PowerManager;

public class AndroidDeviceDisplay implements DeviceDisplay {
	
	private Activity activity;
    
	private EmulatorContext context;
	
	// TODO change this
	public int displayRectangleWidth;
	
	// TODO change this
	public int displayRectangleHeight;
	
    private ArrayList<DisplayRepaintListener> displayRepaintListeners = new ArrayList<DisplayRepaintListener>();
    	
	private Rect rectangle = new Rect();
	
	public AndroidDeviceDisplay(Activity activity, EmulatorContext context, int width, int height) {
		this.activity = activity;
		this.context = context;
        this.displayRectangleWidth = width;
        this.displayRectangleHeight = height;
	}

	public Image createImage(String name) throws IOException {
		Object midlet = MIDletBridge.getCurrentMIDlet();
		if (midlet == null) {
			midlet = getClass();
		}
		InputStream is = context.getResourceAsStream(midlet.getClass(), name);
		if (is == null) {
			throw new IOException(name + " could not be found.");
		}

		return createImage(is);
	}

	public Image createImage(Image source) {
		if (source.isMutable()) {
			return new AndroidImmutableImage((AndroidMutableImage) source);
		} else {
			return source;
		}
	}

	public Image createImage(InputStream is) throws IOException {
		byte[] imageBytes = new byte[1024];
		int num;
		ByteArrayOutputStream ba = new ByteArrayOutputStream();
		while ((num = is.read(imageBytes)) != -1) {
			ba.write(imageBytes, 0, num);
		}

		byte[] bytes = ba.toByteArray();
		return new AndroidImmutableImage(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
	}

	public Image createImage(int width, int height, boolean withAlpha, int fillColor) {
		if (width <= 0 || height <= 0) {
			throw new IllegalArgumentException();
		}

		return new AndroidMutableImage(width, height, withAlpha, fillColor);
	}

	public Image createImage(byte[] imageData, int imageOffset, int imageLength) {
		return new AndroidImmutableImage(BitmapFactory.decodeByteArray(imageData, imageOffset, imageLength));
	}

	public Image createRGBImage(int[] rgb, int width, int height, boolean processAlpha) {
		if (rgb == null)
			throw new NullPointerException();
		if (width <= 0 || height <= 0)
			throw new IllegalArgumentException();
		
		// TODO processAlpha is not handled natively, check whether we need to create copy of rgb
		int[] newrgb = rgb;
		if (!processAlpha) {
			newrgb = new int[rgb.length];
			for (int i = 0; i < rgb.length; i++) {
				newrgb[i] = (0x00ffffff & rgb[i]) | 0xff000000;
			}
		}
		return new AndroidImmutableImage(Bitmap.createBitmap(newrgb, width, height, Bitmap.Config.ARGB_8888));
	}

    private Timer flashBackLightTimer = null;
    
    public boolean flashBacklight(int duration) {
    	if (flashBackLightTimer == null) {
    		flashBackLightTimer = new Timer();
    	}
    	
		PowerManager pm = (PowerManager) activity.getSystemService(Context.POWER_SERVICE);
		final PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "");
		wakeLock.acquire();
		
		flashBackLightTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				wakeLock.release();
			}
			
		}, duration);
		
    	return true;
    }

	public int getFullHeight() {
		return displayRectangleHeight;
	}

	public int getFullWidth() {
		return displayRectangleWidth;
	}

	public int getHeight() {
		// TODO Auto-generated method stub
		return displayRectangleHeight;
	}

	public int getWidth() {
		// TODO Auto-generated method stub
		return displayRectangleWidth;
	}

	public boolean isColor() {
		return true;
	}

	public boolean isFullScreenMode() {
		// TODO Auto-generated method stub
		return false;
	}

	public int numAlphaLevels() {
		return 256;
	}

	public int numColors() {
		return 65536;
	}

	public void repaint(int x, int y, int width, int height) {
		paintDisplayable(x, y, width, height);
	}

	public void setScrollDown(boolean arg0) {
		// TODO Auto-generated method stub

	}

	public void setScrollUp(boolean arg0) {
		// TODO Auto-generated method stub

	}
	
	public void addDisplayRepaintListener(DisplayRepaintListener listener) {
	    displayRepaintListeners.add(listener);
	}

    public void removeDisplayRepaintListener(DisplayRepaintListener listener) {
        displayRepaintListeners.remove(listener);
    }

	public void paintDisplayable(int x, int y, int width, int height) {
        rectangle.left = x;
        rectangle.top = y;
        rectangle.right = x + width;
        rectangle.bottom = y + height;
        for (int i = 0; i < displayRepaintListeners.size(); i++) {
            DisplayRepaintListener l = displayRepaintListeners.get(i);
            if (l != null) {
                l.repaintInvoked(rectangle);    
            }
        }
	}
	
}
