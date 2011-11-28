/*
 * reconnectWindow.java
 *
 *
 * Created on 05.09.2008, 14:08
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
import Client.StaticData;
import ui.controls.Progress;
import Fonts.FontCache;
import javax.microedition.lcdui.*;
import locale.SR;
import Colors.ColorTheme;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author ad
 */
public class ReconnectWindow extends TimerTask {

    private int pos;
    private static int timeout;
    private boolean active;
    private String reconnectString = "";
    private Progress pb;
    private Timer t;

    public ReconnectWindow() {
        timeout = Config.getInstance().reconnectTime * 4;
    }

    public void draw(Graphics g, int width, int height) {
        int reconnectPos = pos * 4;
        int reconnectTimeout = timeout * 4;

        if (!isActive()) {
            return;
        }
        if (!(reconnectTimeout > reconnectPos && reconnectPos != 0)) {
            return;
        }

        int strWidth = g.getFont().stringWidth(SR.MS_RECONNECT);
        int progressWidth = (width / 3) * 2;
        progressWidth = (strWidth > progressWidth) ? strWidth : progressWidth;
        int progressX = (width - progressWidth) / 2;
        if (pb == null) {
            pb = new Progress(progressX, height / 2, progressWidth);
        }
        int popHeight = pb.getHeight();
        g.setColor(ColorTheme.getColor(ColorTheme.POPUP_SYSTEM_BGND));
        g.fillRoundRect(progressX - 2, (height / 2) - (popHeight * 2), progressWidth + 4, (popHeight * 2) + 1, 6, 6);
        g.setColor(ColorTheme.getColor(ColorTheme.POPUP_SYSTEM_INK));
        g.drawRoundRect(progressX - 2, (height / 2) - (popHeight * 2), progressWidth + 4, (popHeight * 2) + 1, 6, 6);
        FontCache.drawString(g, SR.MS_RECONNECT, width / 2, (height / 2) - (popHeight * 2), Graphics.TOP | Graphics.HCENTER);
        pb.draw(g, reconnectPos * progressWidth / reconnectTimeout, reconnectString);
    }

    private void redraw() {
        VirtualCanvas.getInstance().repaint();
    }

    public void startReconnect() {
        if (active) {
            return;
        }
        stopReconnect();
        active = true;
        t = new Timer();
        t.schedule(this, 0, 250);
    }

    public void reconnect() {
        stopReconnect();
        StaticData.getInstance().roster.doReconnect();
    }

    public void stopReconnect() {
        active = false;
        if (t != null)
            t.cancel();
        t = null;
        pos = 0;
        redraw();
    }

    public boolean isActive() {
        return active;
    }

    public void run() {
        reconnectString = Integer.toString(pos / 4);
        redraw();
        if (++pos >= timeout) {
            reconnect();
        }
    }
}
