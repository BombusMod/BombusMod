/*
 * SELightTask.java
 *
 * Created on 21.06.2007, 11:23
 * Copyright (c) 2006-2007, Daniel Apatin (ad), http://apatin.net.ru
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

package Client;

import com.nokia.mid.ui.DeviceControl;

public class SELightTask implements Runnable {    
    
    private static SELightTask instance;
    
    public static SELightTask getInstance(){
	if (instance==null) {
	    instance=new SELightTask();
	}
	return instance;
    }
    
    private boolean stop;
    private boolean setLight;
    
    public SELightTask() {
        new Thread(this).start();
    }
    
    public void setLight(boolean setLight){
        this.setLight=setLight;
    }

    public void destroyTask(){
        stop=false;
    }

    public void run() {
        while (!stop) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
                stop=true;
            }
            
            try {
                 if (setLight)
                     DeviceControl.setLights(0, 100);
            } catch (Exception e) {
                setLight=false;
            }
        }
    }

}
