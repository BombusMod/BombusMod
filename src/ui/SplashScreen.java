/*
 * SplashScreen.java
 *
 * Created on 16.02.2007, 14:23
 *
 * Copyright (c) 2006-2008, Daniel Apatin (ad), http://apatin.net.ru
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
package ui;

import Fonts.FontCache;

import images.RosterIcons;
import midlet.BombusMod;
import Colors.ColorTheme;
//#ifdef LIGHT_CONFIG
//# import Client.StaticData;
//# import LightControl.CustomLight;
//#endif
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import ui.controls.Progress;
import ui.controls.form.DefForm;
//#ifdef AUTOSTATUS
//# import Client.AutoStatus;
//#endif

/**
 *
 * @author Eugene Stahov
 */
public final class SplashScreen extends DefForm implements VirtualElement {

    private String capt;
    private int pos = -1;
    // private int width;
    // private int height;
    public Image splashimg;
    private ComplexString status;
    private static SplashScreen instance;
    public int keypressed = 0;
    private Font clockFont = FontCache.getFont(true, FontCache.bigSize);
    private Progress pb;
    private int exitKey;

    public static SplashScreen getInstance() {
        if (instance == null) {
            instance = new SplashScreen();
        }
        return instance;
    }

    /** Creates a new instance of SplashScreen */
    private SplashScreen() {
        super(null, false);
        infobar = null;
        mainbar = null;
        try {
            splashimg = BombusMod.splash;
            if (splashimg == null) {
                splashimg = Image.createImage("/images/splash.png");
            }
        } catch (Exception e) {
        }

        show();
    }

    public SplashScreen(ComplexString status, int exitKey) {
        super(null, false);
        this.status = status;
        this.exitKey = exitKey;
        infobar = null;
        mainbar = null;
        status.setElementAt(new Integer(RosterIcons.ICON_KEYBLOCK_INDEX), 6);
        try {
            splashimg = BombusMod.splash;
            if (splashimg == null) {
                splashimg = Image.createImage("/images/splash.png");
            }
        } catch (Exception e) {
        }
        show();
    }

    protected void drawTraffic(final Graphics g, boolean up) { }
    protected void drawEnvelop(final Graphics g) { }
//#ifdef MEMORY_USAGE	
//# protected void drawHeapMonitor(final Graphics g, int y) {}
//#endif

    public void commandState() {
        menuCommands.removeAllElements();
    }

    public void setProgress(int progress) {
        pos = progress;
        redraw();
    }

    public void setFailed() {
        setProgress("Failed", 100);
    }

    public void setProgress(String caption, int progress) {
        capt = caption;
//#if DEBUG
//#         System.out.println(capt);
//#endif
        setProgress(progress);
    }

    public int getProgress() {
        return pos;
    }

    public void sizeChanged(int w, int h) {
        redraw();
    }

    public void setExit(VirtualList nextDisplayable) {
        parentView = nextDisplayable;
    }
    
    public int getVHeight() {
        return winHeight;
    }

    public int getVWidth() {
        return 0;
    }

    public int getColorBGnd() {
        return ColorTheme.getColor(ColorTheme.LIST_BGND);
    }

    public int getColor() {
        return ColorTheme.getColor(ColorTheme.LIST_INK);
    }

    public int getItemCount() {
        return 1;
    }

    public VirtualElement getItemRef(int index) {
        return this;
    }

    public void drawItem(Graphics g, int ofs, boolean selected) {
        setAbsOrg(g, 0, 0);
        g.setColor(ColorTheme.getColor(ColorTheme.BLK_BGND));
        g.fillRect(0, 0, width, height);

        if (splashimg != null) {
            g.drawImage(splashimg, width >> 1, height >> 1, Graphics.VCENTER | Graphics.HCENTER);
        }

        if (pos == -1) {
            g.setColor(ColorTheme.getColor(ColorTheme.BLK_INK));
            if (status != null) {
                status.drawItem(g, 0, false);
            }

            g.setFont(clockFont);
            int h = clockFont.getHeight() + 1;

            String time = Time.localTime();
            int tw = clockFont.stringWidth(time);

            FontCache.drawString(g, time, width / 2, height, Graphics.BOTTOM | Graphics.HCENTER);
        } else {
            int filled = pos * width / 100;
            if (pb == null) {
                pb = new Progress(0, height, width);
            }
            pb.draw(g, filled, capt);
        }
    }

    public String getTipString() {
        return null;
    }

    public void onSelect() {
    }

    public boolean isSelectable() {
        return true;
    }

    public boolean handleEvent(int keyCode) {
        return false;
    }

    public void pointerPressed(int x, int y) {
        destroyView();
    }

    public boolean longKey(int key) {
        if (key == exitKey) {
            destroyView();
        }
        return true;
    }

    public final void doKeyAction(int keyCode) {
        keypressed = keyCode;

        if (pos >= 20) {
            destroyView();
        }
    }

   public void destroyView() {
        if (status != null) {
            status.setElementAt(null, 6);
        }
        splashimg = null;
//#ifdef AUTOSTATUS
//#         AutoStatus.getInstance().appUnlocked();
//#endif
        System.gc();
        super.destroyView();
    }
}
