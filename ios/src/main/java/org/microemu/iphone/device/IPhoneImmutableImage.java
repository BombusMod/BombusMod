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
package org.microemu.iphone.device;

import org.robovm.apple.coregraphics.*;
import org.robovm.apple.foundation.NSData;
import org.robovm.apple.uikit.UIImage;
import org.robovm.rt.bro.Struct;
import org.robovm.rt.bro.ptr.IntPtr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IPhoneImmutableImage extends javax.microedition.lcdui.Image implements IPhoneImage {

	private final static Logger logger = LoggerFactory.getLogger(IPhoneImmutableImage.class.getSimpleName());

	private CGImage bitmap;

	public IPhoneImmutableImage(CGImage bitmap) {
		this.bitmap = bitmap;
		bitmap.retain();
		logger.debug("Retaining bitmap " + this + " " + bitmap);
	}

    public IPhoneImmutableImage(IPhoneMutableImage image) {
		this(image.getBitmap().createCopy(null));
    }
    
    public IPhoneImmutableImage(byte[] imageData, int imageOffset, int imageLength) {
		byte[] offsetImageData=new byte[imageLength];
		System.arraycopy(imageData, imageOffset, offsetImageData, 0, imageLength);
		UIImage uiiImage=new UIImage(new NSData(offsetImageData));
		uiiImage.retain();
		bitmap=uiiImage.getCGImage();
	}

	public IPhoneImmutableImage(int[] argb, int width, int height){
		CGColorSpace colorSpace = CGColorSpace.createDeviceRGB();

		IntPtr data = Struct.allocate(IntPtr.class, argb.length);
		data.set(argb);
		CGBitmapContext imageContext = CGBitmapContext.create(data,
				width, height, 8, width * 4, colorSpace,
				new CGBitmapInfo(CGBitmapInfo.ByteOrder32Little.value() + CGImageAlphaInfo.NoneSkipFirst.value()));

		bitmap = imageContext.toImage();
//
//		Pointer<CGRect> rect=CoreGraphics.CGRectMake(GAME_FIELD_X, GAME_FIELD_Y, GAME_FIELD_WIDTH, GAME_FIELD_HEIGHT);
//
//		// flip upside down
//		CoreGraphics.CGContextTranslateCTM(uiContext, 0, CoreGraphics.CGRectGetMaxY(rect));
//		CoreGraphics.CGContextScaleCTM(uiContext, 1.0f, -1.0f);
//
//		CoreGraphics.CGContextDrawImage(uiContext, rect, image);
		data.free();
//		CoreGraphics.CGContextRelease(imageContext);
//		CoreGraphics.CGImageRelease(image);

	}

    /* (non-Javadoc)
	 * @see org.microemu.iphone.device.IPhoneImage#getBitmap()
	 */
    public CGImage getBitmap() {
		return bitmap;
	}

	@Override
	public int getWidth() {
		return (int)bitmap.getWidth();
	}
	
	@Override
	public int getHeight() {
		return (int) bitmap.getHeight();
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

//        throw new UnsupportedOperationException("Currently not supported on iPhone");
//        bitmap.getPixels(argb, offset, scanlength, x, y, width, height);

/*        for (int i = 0; i < argb.length; i++) {
		    int a = (argb[i] & 0xFF000000);
		    int b = (argb[i] & 0x00FF0000) >>> 16;
		    int g = (argb[i] & 0x0000FF00) >>> 8;
		    int r = (argb[i] & 0x000000FF);
	
		    argb[i] = a | (r << 16) | (g << 8) | b;
        }*/
	}
	
	@Override
	protected void finalize() throws Throwable {
		logger.debug("Releasing bitmap "+this+" "+bitmap);
	   	bitmap.release();
	}
}
