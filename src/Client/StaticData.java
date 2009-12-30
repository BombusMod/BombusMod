/*
 * StaticData.java
 *
 * Created on 20.02.2005, 17:10
 * Copyright (c) 2005-2008, Eugene Stahov (evgs), http://bombus-im.org
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
 *
 */

package Client;

import Account.Account;
//#ifdef AUTOTASK
//# import AutoTasks.AutoTask;
//#endif
/**
 *
 * @author Eugene Stahov
 */
public class StaticData {
    
    private static StaticData sd;
    
    public Roster roster;
    
    public Account account;

//#ifdef AUTOTASK
//#     public AutoTask autoTask;
//#endif
    
//#ifdef FILE_IO
    public String previousPath="";
//#endif
    
    //public int screenWidth;
    
    public long traffic = 0;

    private long trafficOut;
    private long trafficIn;

    public void updateTrafficIn() { trafficIn=System.currentTimeMillis(); }
    public long getTrafficIn() { return trafficIn; }
    public void updateTrafficOut() { trafficOut=System.currentTimeMillis(); }
    public long getTrafficOut() { return trafficOut; }
    
    /** Creates a new instance of StaticData */
    private StaticData() { }
    
    public static StaticData getInstance(){
        if (sd==null) 
            sd=new StaticData();
        return sd;
    }

//#ifdef PLUGINS
//#     public boolean Archive=true;
//#     public boolean ChangeTransport=true;
//#     public boolean Console=true;
//#     public boolean FileTransfer=true;
//#     public boolean History=true;
//#     public boolean ImageTransfer=true;
//#     public boolean PEP=true;
//#     public boolean Privacy=true;
//#     public boolean IE=true;
//#     public boolean Colors=true;
//#     public boolean Adhoc=true;
//#     public boolean Stats=true;
//#     public boolean ClientsIcons=true;
//#     public boolean UserKeys=true;
//#     public boolean Upgrade=true;
//#     public boolean Juick=true;
//#endif
}
