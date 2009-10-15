/*******************************************************************************
 Jimm - Mobile Messaging - J2ME ICQ clone
 Copyright (C) 2003-05  Jimm Project

 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 ********************************************************************************
 File: src/DrawControls/AniIcon.java
 Version: ###VERSION###  Date: ###DATE###
 Author(s): Vladimir Kryukov
 *******************************************************************************/
/*
 * GifIcon.java
 *
 * Created on 4 Апрель 2008 г., 19:26
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package images;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import ui.ImageList;

/**
 *
 * @author vladimir
 */
public class AniIcon {
    private ImageList images;
    private short[] frames;
    private int[] delays;
    private int currentFrame = 0;
    /** Creates a new instance of GifIcon */

    public Image skin_png;

    protected Image resImage;
    protected int height;
    protected int width;

    public void drawImage(Graphics g, int x, int y) {
        int ho = g.getClipHeight();
        int wo = g.getClipWidth();
        int xo = g.getClipX();
        int yo = g.getClipY();

        int ix = x - width * frames[currentFrame];
        g.clipRect(x, y, width, height);
        g.drawImage(resImage, ix, y, Graphics.TOP|Graphics.LEFT);
        g.setClip(xo, yo, wo, ho);
        painted = true;
    }

    public int getHeight() {return height;}
    public int getWidth() {return width;}
    public AniIcon(String resource, int imageCount, int frameCount) {
        frames = new short[frameCount];
        delays = new int[frameCount];
        try {
            resImage = Image.createImage(resource);
            width = resImage.getWidth() / imageCount;
            height = resImage.getHeight();
        } catch (OutOfMemoryError memory) { 
            System.out.println("Memory error on " + resource);
        } catch (Exception e) { 
            System.out.println("Can't load " + resource);
            e.printStackTrace();
        }
    }
    void addFrame(int num, int iconIndex, int dalay) {
        frames[num] = (short)iconIndex;
        delays[num] = dalay;
    }
    private boolean painted = false;
    private long sleepTime = 0;
    boolean nextFrame(long deltaTime) {
        sleepTime -= deltaTime;
        if (sleepTime <= 0) {
            currentFrame = (currentFrame + 1) % frames.length;
            sleepTime = delays[currentFrame];
            boolean needReepaint = painted;
            painted = false;
            return needReepaint;
        }
        return false;
    }

}
