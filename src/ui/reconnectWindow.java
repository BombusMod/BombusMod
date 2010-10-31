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

/**
 *
 * @author ad
 */
public class reconnectWindow implements Runnable {

    private int pos;
    private static int timeout;
    private boolean active;
    private static reconnectWindow instance;

    public static reconnectWindow getInstance() {
        if (instance == null) {
            instance = new reconnectWindow();
            timeout = Config.getInstance().reconnectTime * 4;
        }
        return instance;
    }

    public void startReconnect() {
        if (active)
            return;

        new Thread(this).start();
    }

    public void reconnect() {
        stopReconnect();
        StaticData.getInstance().roster.doReconnect();
    }

    public void stopReconnect() {
        active = false;
        pos = 0;
    }

    public boolean isActive() {
        return active;
    }

    public void run() {
        active = true;
        while (active) {
            try {
                Thread.sleep(250);
            } catch (Exception e) { break; }

            VirtualList.drawReconnect(pos * 4, timeout * 4, Integer.toString(pos / 4));

            pos++;

            if (pos > timeout) {
                reconnect();
                break;
            }
        }
    }
}
