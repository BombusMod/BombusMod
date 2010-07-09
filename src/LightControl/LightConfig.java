/*
 * AlertCustomize.java
 *
 * Copyright (c) 2006-2008, Daniel Apatin (ad), http://apatin.net.ru
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
 *
 */

package LightControl;

import io.NvStorage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class LightConfig {
//#ifdef PLUGINS
//#     public static String plugin = "PLUGIN_LIGHT";
//#endif    

    public boolean light_control=false;
    
    public int light_keypressed_time=10;
    public int light_presence_time=1;
    public int light_message_time=2;
    public int light_error_time=3;
    public int light_blink_time=1;

    public int light_idle=00;
    public int light_presence=10;
    public int light_keypress=15;
    public int light_connect=101;
    public int light_message=101;
    public int light_error=101;
    public int light_blink=101;

    
    // Singleton
    private static LightConfig instance;

    //public boolean flashBackLight;
    
    public static LightConfig getInstance(){
	if (instance==null) {
	    instance=new LightConfig();
	    instance.loadFromStorage();
	}
	return instance;
    }
   
    protected void loadFromStorage(){
        DataInputStream inputStream=NvStorage.ReadFileRecord("LightConfig", 0);
        try {
            light_control=inputStream.readBoolean();
            light_idle=inputStream.readInt();
            light_presence=inputStream.readInt();
            light_presence_time=inputStream.readInt();
            light_message=inputStream.readInt();
            light_message_time=inputStream.readInt();
            light_blink=inputStream.readInt();
            light_blink_time=inputStream.readInt();
            light_keypress=inputStream.readInt();
            light_keypressed_time=inputStream.readInt();
            light_connect=inputStream.readInt();
            light_error=inputStream.readInt();
            light_error_time=inputStream.readInt();
            inputStream.close();
            inputStream=null;
	} catch (Exception e) {
            try {
                if (inputStream!=null) {
                    inputStream.close();
                    inputStream=null;
                }
            } catch (IOException ex) {}
	}
    }
    
    public void saveToStorage(){
	try {
            DataOutputStream outputStream=NvStorage.CreateDataOutputStream();
            
            outputStream.writeBoolean(light_control);
            outputStream.writeInt(light_idle);
            outputStream.writeInt(light_presence);
            outputStream.writeInt(light_presence_time);
            outputStream.writeInt(light_message);
            outputStream.writeInt(light_message_time);
            outputStream.writeInt(light_blink);
            outputStream.writeInt(light_blink_time);
            outputStream.writeInt(light_keypress);
            outputStream.writeInt(light_keypressed_time);
            outputStream.writeInt(light_connect);
            outputStream.writeInt(light_error);
            outputStream.writeInt(light_error_time);

            NvStorage.writeFileRecord(outputStream, "LightConfig", 0, true);
	} catch (IOException e) {
            //e.printStackTrace();
        }
    }
}
