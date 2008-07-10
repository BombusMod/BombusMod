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

package Client;

import io.NvStorage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Stats {

    private long latestTraffic=0;
    private long traffic=0;
    private int sessions=0;

    public static long sessionGPRS=0;
    
//#ifdef ELF    
//#     private static long startGPRS=-1;
//#     private static boolean sie_gprs=true;
//#endif
    
    // Singleton
    private static Stats instance;
    
    public static Stats getInstance(){
	if (instance==null) {
	    instance=new Stats();
            instance.loadFromStorage();
            getGPRS();
	}
	return instance;
    }

    public void save(){
        //loadFromStorage();
        saveToStorage();
    }

    public long getLatest(){
        return latestTraffic;
    }
    
    public long getAllTraffic(){
        return traffic+getCurrentTraffic();
    }
    
    public long getCurrentTraffic(){
        return (sessionGPRS==0)?getGPRS():sessionGPRS;
    }
    
    public int getSessionsCount(){
        return sessions;
    }
    
    private void loadFromStorage(){
        DataInputStream inputStream=NvStorage.ReadFileRecord("stats", 0);
        try {
            traffic=inputStream.readLong();
            latestTraffic=inputStream.readLong();
            sessions=inputStream.readInt();
            inputStream.close();
	} catch (Exception e) {
            try {
                if (inputStream!=null) 
                    inputStream.close();
            } catch (IOException ex) {}
	}
    }
    
    private void saveToStorage(){
        long sessionTraffic=getCurrentTraffic();
        long allTraffic=traffic+sessionTraffic;
        
        sessions++;
        
	try {
            DataOutputStream outputStream=NvStorage.CreateDataOutputStream();
         
            outputStream.writeLong(allTraffic);
            outputStream.writeLong(sessionTraffic);
            outputStream.writeInt(sessions);
            
            NvStorage.writeFileRecord(outputStream, "stats", 0, true);
	} catch (IOException e) { }
    }
    
    public static long getGPRS() {
//#ifdef ELF
//#         if (sie_gprs) {
//#             try {
//#                 int gprscnt=Integer.parseInt(System.getProperty("MPJCGPRS"));
//# 
//#                 if (gprscnt>-1) {
//#                     if (startGPRS<0) {
//#                         startGPRS=gprscnt;
//#                         sessionGPRS=0;
//#                     } else {
//#                         sessionGPRS=gprscnt-startGPRS;
//#                     }
//#                     return sessionGPRS;
//#                 }
//#             } catch (Exception e) { sie_gprs=false; }
//#         }
//#endif
        if (StaticData.getInstance().roster.theStream!=null) {
            sessionGPRS=StaticData.getInstance().traffic;
            return sessionGPRS;
        }
        return 0;
    }   
}