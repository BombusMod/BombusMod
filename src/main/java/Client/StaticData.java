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

import com.alsutton.jabber.JabberStream;
import org.bombusmod.util.AssetsLoader;
import org.bombusmod.util.ConnectionService;
import org.bombusmod.util.EventNotifier;
import org.bombusmod.util.VersionInfo;
import xmpp.Account;

/**
 *
 * @author Eugene Stahov
 */
public final class StaticData {
    
    private static StaticData sd;
    
    public Roster roster;
    
    public Account account;
    
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

    public static StaticData getInstance() {
        if (sd == null) {
            sd = new StaticData();
        }
        return sd;
    }
    private JabberStream theStream;
    private ConnectionService service;
    public JabberStream getTheStream() {
        return theStream;
    }
    private AssetsLoader assetsLoader;
    public AssetsLoader getAssetsLoader() {
        return assetsLoader;
    }
    public void setAssetsLoader(AssetsLoader assetsLoader) {
        this.assetsLoader = assetsLoader;
    }
    private VersionInfo versionInfo;
    private EventNotifier eventNotifier;
    public static final boolean Debug = true;
    public static final boolean XmlDebug = true;
    public static final boolean NonSaslAuth = false;

    public VersionInfo getVersionInfo() {
        return versionInfo;
    }

    public void setVersionInfo(VersionInfo versionInfo) {
        this.versionInfo = versionInfo;
    }

    public EventNotifier getEventNotifier() {
        return eventNotifier;
    }

    public void setEventNotifier(EventNotifier eventNotifier) {
        this.eventNotifier = eventNotifier;
    }

    public ConnectionService getService() {
        return service;
    }

    public void setService(ConnectionService service) {
        this.service = service;
    }

    public void setTheStream(JabberStream theStream) {
        this.theStream = theStream;
    }
}
