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

import Client.Config;
import Fonts.FontCache;
//#ifdef AUTOSTATUS
//# import Client.ExtendedStatus;
//# import Client.Roster;
//# import Client.StatusList;
//#endif
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

/**
 *
 * @author Eugene Stahov
 */
public final class SplashScreen extends DefForm implements VirtualElement {

    private String capt;
    private int pos = -1;
    // private int width;
    // private int height;
    public Image img;
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
        
        try {
            img = BombusMod.splash;
            if (img == null) {
                img = Image.createImage("/images/splash.png");
            }
        } catch (Exception e) {
        }

        show();
    }

    public SplashScreen(ComplexString status, int exitKey) {
        super(null, false);
        this.status = status;
        this.exitKey = exitKey;
        panelsState = 0;
        status.setElementAt(new Integer(RosterIcons.ICON_KEYBLOCK_INDEX), 6);
        try {
            img = BombusMod.splash;
            if (img == null) {
                img = Image.createImage("/images/splash.png");
            }
        } catch (Exception e) {
        }
        show();
    }

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
        return getListHeight();
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
        int width = g.getClipWidth();
        int height = g.getClipHeight();

        g.setColor(ColorTheme.getColor(ColorTheme.BLK_BGND));
        g.fillRect(0, 0, width, height);

        if (img != null) {
            g.drawImage(img, width / 2, height / 2, Graphics.VCENTER | Graphics.HCENTER);
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
            Progress.draw(g, filled, capt);
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
//#ifdef LIGHT_CONFIG
//#ifdef PLUGINS
//#         if (StaticData.getInstance().lightConfig)
//#endif
//#             CustomLight.keyPressed();
//#endif
       if (key == exitKey) {
            destroyView();
        }
        return true;
    }

    protected final void userkeyPressed(int keyCode) {
        keypressed = keyCode;

        if (pos >= 20) {
            destroyView();
        }
//#ifdef LIGHT_CONFIG      
//#ifdef PLUGINS                
//#         if (StaticData.getInstance().lightConfig)
//#endif            
//#             CustomLight.keyPressed();
//#endif
    }

// ==================================================== //
    public void destroyView() {
        status.setElementAt(null, 6);
        img = null;
//#ifdef AUTOSTATUS
//#         if (Roster.autoAway && cf.autoAwayType == Config.AWAY_LOCK) {
//#             int newStatus = Roster.oldStatus;
//#             ExtendedStatus es = StatusList.getInstance().getStatus(newStatus);
//#             String ms = es.getMessage();
//#             Roster.autoAway = false;
//#             Roster.autoXa = false;
//#             sd.roster.sendPresence(newStatus, ms);
//#         }
//#endif
//        if (cf.widthSystemgc) { _vt
        System.gc();
//        } _vt
        super.destroyView();
    }

    public void touchLeftPressed() {
    }

    public void touchRightPressed() {
    }

    public String touchLeftCommand() {
        return "";
    }

    public String touchRightCommand() {
        return "";
    }
}
