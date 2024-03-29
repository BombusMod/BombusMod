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
 *  @version $Id: AndroidDisplayGraphics.java 2522 2011-11-28 13:44:59Z barteo@gmail.com $
 */

package org.microemu.android.device;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Image;

public class AndroidDisplayGraphics extends javax.microedition.lcdui.Graphics {
	
    public Paint strokePaint = new Paint();
    
    public Paint fillPaint = new Paint();
    
    public AndroidFont androidFont;
    
	private static final DashPathEffect dashPathEffect = new DashPathEffect(new float[] { 5, 5 }, 0);
	
	private Canvas canvas;

	private Rect clip;
	
	private Font font;

	private int canvasInitSave;

	public AndroidDisplayGraphics() {
		strokePaint.setAntiAlias(true);
		strokePaint.setStyle(Paint.Style.STROKE);
		strokePaint.setDither(true);
		fillPaint.setAntiAlias(true);
		fillPaint.setDither(true);
		fillPaint.setStyle(Paint.Style.FILL);
	}
	
    public AndroidDisplayGraphics(Bitmap bitmap) {
        this.canvas = new Canvas(bitmap);
		strokePaint.setAntiAlias(true);
		strokePaint.setDither(true);
		strokePaint.setStyle(Paint.Style.STROKE);
		fillPaint.setAntiAlias(true);
		fillPaint.setDither(true);
		fillPaint.setStyle(Paint.Style.FILL);
        reset(this.canvas);
    }
	
	public final void reset(Canvas canvas) {
	    this.canvas = canvas;
	    if (canvasInitSave > 0) {
			canvas.restoreToCount(canvasInitSave);
		}
	    canvasInitSave = canvas.save();
		clip = this.canvas.getClipBounds();
		setFont(Font.getDefaultFont());
	}
	
	public Canvas getCanvas() {
		return canvas;
	}

	public void clipRect(int x, int y, int width, int height) {
		canvas.clipRect(x, y, x + width, y + height);
		clip = canvas.getClipBounds();
	}

	public void drawImage(Image img, int x, int y, int anchor) {
		int newx = x;
		int newy = y;

		if (anchor == 0) {
			anchor = javax.microedition.lcdui.Graphics.TOP | javax.microedition.lcdui.Graphics.LEFT;
		}

		if ((anchor & javax.microedition.lcdui.Graphics.RIGHT) != 0) {
			newx -= img.getWidth();
		} else if ((anchor & javax.microedition.lcdui.Graphics.HCENTER) != 0) {
			newx -= img.getWidth() / 2;
		}
		if ((anchor & javax.microedition.lcdui.Graphics.BOTTOM) != 0) {
			newy -= img.getHeight();
		} else if ((anchor & javax.microedition.lcdui.Graphics.VCENTER) != 0) {
			newy -= img.getHeight() / 2;
		}

		canvas.drawBitmap(((AndroidImmutableImage) img).getBitmap(), newx, newy, strokePaint);
	}

	public void drawLine(int x1, int y1, int x2, int y2) {
		if (x1 == x2) {
			canvas.drawLine(x1, y1, x2, y2 + 1, strokePaint);
		} else if (y1 == y2) {
			canvas.drawLine(x1, y1, x2 + 1, y2, strokePaint);
		} else {
			canvas.drawLine(x1, y1, x2 + 1, y2 + 1, strokePaint);
		}
	}

	public void drawRect(int x, int y, int width, int height) {
		canvas.drawRect(x, y, x + width, y + height, strokePaint);
	}

	public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
		canvas.drawRoundRect(new RectF(x, y, x + width, y + height), (float) arcWidth, (float) arcHeight, strokePaint);
   }

	public void drawString(String str, int x, int y, int anchor) {
		drawSubstring(str, 0, str.length(), x, y, anchor);
	}

	public void drawSubstring(String str, int offset, int len, int x, int y, int anchor) {
        int newx = x;
        int newy = y;

        if (anchor == 0) {
            anchor = javax.microedition.lcdui.Graphics.TOP | javax.microedition.lcdui.Graphics.LEFT;
        }
        
        if ((anchor & javax.microedition.lcdui.Graphics.TOP) != 0) {
            newy -= androidFont.metrics.ascent;
        } else if ((anchor & javax.microedition.lcdui.Graphics.BOTTOM) != 0) {
            newy -= androidFont.metrics.descent;
        }
        if ((anchor & javax.microedition.lcdui.Graphics.HCENTER) != 0) {
            newx -= androidFont.paint.measureText(str) / 2;
        } else if ((anchor & javax.microedition.lcdui.Graphics.RIGHT) != 0) {
            newx -= androidFont.paint.measureText(str);
        }

        androidFont.paint.setColor(strokePaint.getColor());

        canvas.drawText(str, offset, len + offset, newx, newy, androidFont.paint);
	}

	public void fillRect(int x, int y, int width, int height) {
        canvas.drawRect(x, y, x + width, y + height, fillPaint);
	}

	public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
		canvas.drawRoundRect(new RectF(x, y, x + width, y + height), (float) arcWidth, (float) arcHeight, fillPaint);
    }

	public int getClipHeight() {
		return clip.bottom - clip.top;
	}

	public int getClipWidth() {
		return clip.right - clip.left;
	}

	public int getClipX() {
		return clip.left;
	}

	public int getClipY() {
		return clip.top;
	}

	public int getColor() {
		return strokePaint.getColor();
	}

	public Font getFont() {
		return font;
	}

	public void setClip(int x, int y, int width, int height) {
		if (x == clip.left && x + width == clip.right && y == clip.top && y + height == clip.bottom) {
			return;
		}

        clip.left = x;
        clip.top = y;
        clip.right = x + width;
        clip.bottom = y + height;
		canvas.restore();
		canvas.save();
		canvas.translate(getTranslateX(), getTranslateY());
		canvas.clipRect(clip);
	}

	public void setColor(int RGB) {
		strokePaint.setColor(0xff000000 | RGB);
		fillPaint.setColor(0xff000000 | RGB);
	}

	public void setFont(Font font) {
		this.font = font;
		
        androidFont = AndroidFontManager.getFont(font);

	}
	
	public void setStrokeStyle(int style) {
		if (style != SOLID && style != DOTTED) {
			throw new IllegalArgumentException();
		}

		if (style == SOLID) {
			strokePaint.setPathEffect(null);
			fillPaint.setPathEffect(null);
		} else { // DOTTED
			strokePaint.setPathEffect(dashPathEffect);
			fillPaint.setPathEffect(dashPathEffect);
		}
	}

    public void translate(int x, int y) {
        canvas.translate(x, y);
            
        super.translate(x, y);
            
        clip.left -= x;
        clip.right -= x;
        clip.top -= y;
        clip.bottom -= y;
    }
	
}
