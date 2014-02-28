/*
 * Stats.java
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

//#ifdef STATS

package Statistic;

import Client.StaticData;
import io.NvStorage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Stats {

    private long latestTraffic = 0;
    private long traffic = 0;
    private int sessions = 0;

    // Singleton
    private static Stats instance;

    public static Stats getInstance() {
        if (instance == null) {
            instance = new Stats();
            instance.loadFromStorage();
        }
        return instance;
    }

    public long getLatest() {
        return latestTraffic;
    }

    public long getAllTraffic() {
        return traffic + getCurrentTraffic();
    }

    public int getSessionsCount() {
        return sessions;
    }

    private void loadFromStorage() {
        DataInputStream inputStream = NvStorage.ReadFileRecord("stats", 0);
        try {
            traffic = inputStream.readLong();
            latestTraffic = inputStream.readLong();
            sessions = inputStream.readInt();
            inputStream.close();
            inputStream = null;
        } catch (Exception e) {
            try {
                if (inputStream != null) {
                    inputStream.close();
                    inputStream = null;
                }
            } catch (IOException ex) {
            }
        }
        sessions++;
    }

    public void saveToStorage(boolean reset) {
        loadFromStorage();

        long sessionTraffic;
        long allTraffic;

        if (reset) {
            sessionTraffic = 0;
            allTraffic = 0;
            latestTraffic = 0;
            traffic = 0;
            sessions = 0;
        } else {
            sessionTraffic = getCurrentTraffic();
            allTraffic = traffic + sessionTraffic;
        }

        try {
            DataOutputStream outputStream = NvStorage.CreateDataOutputStream();

            outputStream.writeLong(allTraffic);
            outputStream.writeLong(sessionTraffic);
            outputStream.writeInt(sessions);
            NvStorage.writeFileRecord(outputStream, "stats", 0, true);
        } catch (Exception e) {
        }
    }

    public static long getCurrentTraffic() {
        return StaticData.getInstance().traffic * 2;
    }
}

//#endif
