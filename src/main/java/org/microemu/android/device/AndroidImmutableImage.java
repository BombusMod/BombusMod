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
 *  @version $Id: AndroidImmutableImage.java 1787 2008-10-21 13:00:12Z barteo $
 */

package org.microemu.android.device;

import android.graphics.Bitmap;

public class AndroidImmutableImage extends javax.microedition.lcdui.Image {

	private Bitmap bitmap;
	
	public AndroidImmutableImage(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

    public AndroidImmutableImage(AndroidMutableImage image) {
    	this.bitmap = Bitmap.createBitmap(image.getBitmap());
    }

    public Bitmap getBitmap() {
		return bitmap;
	}

	@Override
	public int getWidth() {
		return bitmap == null? 0: bitmap.getWidth();
	}
	
	@Override
	public int getHeight() {
		return bitmap == null? 0: bitmap.getHeight();
	}

	@Override
    public void getRGB(int []argb, int offset, int scanlength,
            int x, int y, int width, int height) {
        if (width <= 0 || height <= 0)
            return;
        if (x < 0 || y < 0 || x + width > getWidth() || y + height > getHeight())
            throw new IllegalArgumentException("Specified area exceeds bounds of image");
        if ((scanlength < 0? -scanlength:scanlength) < width)
            throw new IllegalArgumentException("abs value of scanlength is less than width");
        if (argb == null)
            throw new NullPointerException("null rgbData");
        if (offset < 0 || offset + width > argb.length)
            throw new ArrayIndexOutOfBoundsException();
        if (scanlength < 0) {
            if (offset + scanlength*(height-1) < 0)
                throw new ArrayIndexOutOfBoundsException();
        } else {
            if (offset + scanlength*(height-1) + width > argb.length)
                throw new ArrayIndexOutOfBoundsException();
        }

        bitmap.getPixels(argb, offset, scanlength, x, y, width, height);

/*        for (int i = 0; i < argb.length; i++) {
		    int a = (argb[i] & 0xFF000000);
		    int b = (argb[i] & 0x00FF0000) >>> 16;
		    int g = (argb[i] & 0x0000FF00) >>> 8;
		    int r = (argb[i] & 0x000000FF);
	
		    argb[i] = a | (r << 16) | (g << 8) | b;
        }*/
	}
	
}
