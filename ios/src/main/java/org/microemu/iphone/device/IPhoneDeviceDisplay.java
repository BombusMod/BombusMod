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
 *  @version $Id$
 */
package org.microemu.iphone.device;

import org.microemu.DisplayAccess;
import org.microemu.MIDletAccess;
import org.microemu.MIDletBridge;
import org.microemu.MicroEmulator;
import org.microemu.device.DeviceDisplay;
import org.microemu.device.EmulatorContext;
import org.microemu.device.MutableImage;
import org.microemu.device.ui.DisplayableUI;
import org.microemu.iphone.device.ui.IPhoneCanvasUI;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UIView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.microedition.lcdui.Image;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class IPhoneDeviceDisplay implements DeviceDisplay {

	private static final Logger logger = LoggerFactory.getLogger(IPhoneDeviceDisplay.class.getSimpleName());
	
	// TODO change this
	public int displayRectangleWidth;
	
	// TODO change this
	public int displayRectangleHeight;
	
	private EmulatorContext emulatorContext;
	
	public IPhoneDeviceDisplay(EmulatorContext emulatorContext, UIView parentView) {
		this.emulatorContext = emulatorContext;
		displayRectangleWidth = (int)parentView.getFrame().getWidth();
		displayRectangleHeight = (int)parentView.getFrame().getHeight();
	}

	public Image createImage(String name) throws IOException {
		return createImage(emulatorContext.getResourceAsStream(this.getClass(), name));
	}

	public Image createImage(InputStream is) throws IOException {
        byte[] imageData=getStreamAsByteArray(is);
        return createImage(imageData, 0, imageData.length);
	}
	
    /**
     * Returns the contents of the input stream as byte array.
     *
     * @param stream the <code>InputStream</code>
     * @return the stream content as byte array
     */
    private static byte[] getStreamAsByteArray(InputStream stream) throws IOException {
        return getStreamAsByteArray(stream, -1);
    }

    /**
     * Returns the contents of the input stream as byte array.
     *
     * @param stream the <code>InputStream</code>
     * @param length the number of bytes to copy, if length < 0, the number is unlimited
     * @return the stream content as byte array
     */
    private static byte[] getStreamAsByteArray(InputStream stream, int length) throws IOException {
        if (length == 0) return new byte[0];
        boolean checkLength = true;
        if (length < 0) {
            length = Integer.MAX_VALUE;
            checkLength = false;
        }
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        int nextValue = stream.read();
        if (checkLength) length--;
        while (-1 != nextValue && length >= 0) {
            byteStream.write(nextValue);
            nextValue = stream.read();
            if (checkLength) length--;
        }
        return byteStream.toByteArray();
    }
	
	public Image createImage(int width, int height) {
		if (width <= 0 || height <= 0) {
			throw new IllegalArgumentException();
		}

		return new IPhoneMutableImage(width, height);
	}

	public Image createImage(byte[] imageData, int imageOffset, int imageLength) {
		return new IPhoneImmutableImage(imageData, imageOffset, imageLength);
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
		
		return new IPhoneImmutableImage(newrgb, width, height);
	}

	public MutableImage getDisplayImage() {
        throw new UnsupportedOperationException("Currently not supported on iPhone");
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

	@Override
	public boolean flashBacklight(int duration) {
		return false;
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
//        throw new UnsupportedOperationException("Currently not supported on iPhone");
	}

	public void setScrollUp(boolean arg0) {
//        throw new UnsupportedOperationException("Currently not supported on iPhone");
	}

	public void paintDisplayable(int x, int y, int width, int height) {
		MIDletAccess ma = MIDletBridge.getMIDletAccess();
		if (ma == null) {
			return;
		}
		DisplayAccess da = ma.getDisplayAccess();
		if (da == null) {
			return;
		}
		DisplayableUI current = da.getDisplayableUI(da.getCurrent());
		if (current == null) {
			return;
		}

		// TODO
		// g.save(android.graphics.Canvas.CLIP_SAVE_FLAG);
		// TODO
		// if (!(current instanceof Canvas) || ((Canvas) current).getWidth() != displayRectangle.width
		// 		|| ((Canvas) current).getHeight() != displayRectangle.height) {
		// 	g.translate(displayPaintable.x, displayPaintable.y);
		// }
		// TODO
		// Font oldf = g.getFont();
		if (current instanceof IPhoneCanvasUI) {
			UIView view = ((IPhoneCanvasUI) current).getCanvasView();
			logger.debug("Need paint: "+this+" "+view);
			view.setNeedsDisplay();
//			view.setNeedsDisplayInRect$(new CGRect(x,y,width,height));
		} else {
			// TODO extend DisplayableUI interface
			//current.paint();
		}
		// TODO
		// g.setFont(oldf);
		// TODO
		// if (!(current instanceof Canvas) || ((Canvas) current).getWidth() != displayRectangle.width
		//		|| ((Canvas) current).getHeight() != displayRectangle.height) {
		// 	g.translate(-displayPaintable.x, -displayPaintable.y);
		//}
		// TODO
		// g.restore();
	}
	
}
