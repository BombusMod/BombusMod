/*
 * ImageList.java
 *
 * Created on 31.01.2005, 0:06
 *
 * Copyright (c) 2005-2008, Eugene Stahov (evgs), http://bombus-im.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * You can also redistribute and/or modify this program under the
 * terms of the Psi License, specified in the accompanied COPYING
 * file, as published by the Psi Project; either dated January 1st,
 * 2005, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

/**
 * @author Eugene Stahov
 */

package ui;

import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Graphics;

public class ImageList {

    public Image skin_png;

    protected Image resImage;
    protected int height;
    protected int width;
    public boolean isLoaded = false;

    protected ImageList() {
    }

    /** Creates a new instance of ImageList */
    public ImageList(String resource, int rows, int columns) {
        try {
            resImage = Image.createImage(resource);
            width = resImage.getWidth() / columns;
            height = (rows == 0) ? width : resImage.getHeight() / rows;
            isLoaded = true;
        } catch (OutOfMemoryError eom) {
        } catch (Exception e) {
        }
    }

    public void drawImage(Graphics g, int index, int x, int y) {
        if (!isLoaded) return;
        int ho = g.getClipHeight();
        int wo = g.getClipWidth();
        int xo = g.getClipX();
        int yo = g.getClipY();

        int iy = y - height * (index >> 4);
        int ix = x - width * (index & 0x0f);
        g.clipRect(x, y, width, height);
        g.drawImage(resImage, ix, iy, Graphics.TOP | Graphics.LEFT);
        g.setClip(xo, yo, wo, ho);
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }
}
