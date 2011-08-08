/*
 * Jimm - Mobile Messaging - J2ME ICQ clone
 * Copyright (C) 2003-05  Jimm Project
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * GifImageList.java
 *
 * Created on 4 Апрель 2008 г., 18:55
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package images;

import java.io.InputStream;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import midlet.BombusMod;
import ui.ImageList;
import io.file.InternalResource;

/**
 *
 * @author vladimir
 */
public class AniImageList extends ImageList implements Runnable {

    private AniIcon[] icons;
    private Thread thread;
    public AniImageList() {
    }
    //! Return image by index
    public AniIcon iconAt(int index) { //!< Index of requested image in the list
        if (index < size() && index >= 0) {
            return icons[index];
        }
        return null;
    }
    public int size() {
        return icons != null ? icons.length : 0;
    }

    public void drawImage(Graphics g, int index, int x, int y) {
        if (0 <= index && index < icons.length) {
            //g.drawRect(x, y+(height-icons[index].getHeight())/2, icons[index].getWidth(), icons[index].getHeight());
            icons[index].drawImage(g, x, y+(height-icons[index].getHeight())/2);
        } else {
        }
    }
    private String getAnimationFile(String resName, int i) {
        return resName + "/" + (i + 1) + ".png";
    }

    public void load(String resName) {
        try {
            InputStream is = InternalResource.getResourceAsStream(resName + "/animate.bin");
            int smileCount = is.read();

            icons = new AniIcon[smileCount];
            for (int smileNum = 0; smileNum < smileCount; smileNum++) {
                int imageCount = is.read();
                int frameCount = is.read();
                AniIcon icon = new AniIcon(getAnimationFile(resName, smileNum), imageCount, frameCount);
                boolean loaded = (0 < icon.getWidth());
                if (!loaded) {
                    width = height = 0;
                    return;
                }
                for (int frameNum = 0; frameNum < frameCount; frameNum++) {
                    int iconIndex = is.read();
                    int delay = is.read() * WAIT_TIME;
                    icon.addFrame(frameNum, iconIndex, delay);
                }
                icons[smileNum] = icon;
                width = Math.max(width, icon.getWidth());
                height = Math.max(height, icon.getHeight());
            }
        } catch (Exception e) {
        }
        if (size() > 0) {
            thread = new Thread(this);
            thread.start();
        }
    }

    private static final int WAIT_TIME = 100;
    public void run() {
        long time = System.currentTimeMillis();
        while (true) {
            try {
                Thread.sleep(WAIT_TIME);
            } catch (Exception e) {
            }
            long newTime = System.currentTimeMillis();
            boolean animationWorked = true;
            if (animationWorked) {
                boolean update = false;
                for (int i = 0; i < size(); i++) {
                    if (null != icons[i]) {
                        update |= icons[i].nextFrame(newTime - time);
                    }
                }
                if (update) {
                    Displayable d = BombusMod.getInstance().getDisplay().getCurrent();
                    if (d instanceof Canvas) {
                        ((Canvas)d).repaint();
                    }

                }
            }
            time = newTime;
        }
    }
}
