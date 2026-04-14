/*
 * Account.java
 *
 * Created on 19.03.2005, 21:52
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
package xmpp;

import Client.StaticData;
import com.alsutton.jabber.JabberStream;

import io.DnsSrvResolver;
import java.util.Vector;

public class Account {

    //public final static String storage="accnt_db";
    public Jid JID;
    public String password = "";
    public String hostAddr = "";
    public int port = 5222;
    public boolean active;
    public boolean compression = true;
    public boolean plainAuth;
    public boolean mucOnly;
    public String nick = "";
    private boolean enableProxy;
    public String proxyHostAddr = "";
    private int proxyPort;
    private String proxyUser = "";
    private String proxyPass = "";
    public int keepAlivePeriod = 120;
    private static StaticData sd = StaticData.getInstance();    
    
    public final Vector bookmarks = new Vector();        
           

    public String getNickName() {
        return (nick.length() == 0) ? JID.getNode() : nick;
    }    

    public JabberStream openJabberStream() throws java.io.IOException {
        String proxy = null;
        String host = JID.getServer();
        int tempPort = port;
        boolean resolveHostname = true;
        if (hostAddr != null && hostAddr.length() > 0) {
            host = hostAddr;
            resolveHostname = false;
        }
        if (proxyHostAddr == null || proxyHostAddr.isEmpty()) {
            proxyHostAddr = System.getProperty("http.proxyHost");
            proxyPort = Integer.getInteger("http.proxyPort", 1081);
        }
        if (resolveHostname) {
            DnsSrvResolver dns = new DnsSrvResolver();
            if (dns.getSrv(JID.getServer())) {
                host = dns.getHost();
                tempPort = dns.getPort();
            }
        }

        if (isEnableProxy()) {
            proxy = proxyHostAddr;
        }
        return new JabberStream(JID.getServer(), host, tempPort, proxy, proxyPort);
    }
     public boolean isEnableProxy() {
         return enableProxy;
     }

     public void setEnableProxy(boolean enableProxy) {
         this.enableProxy = enableProxy;
     }

     public int getProxyPort() {
         return proxyPort;
     }

     public void setProxyPort(int proxyPort) {
         this.proxyPort = proxyPort;
     }

     public String getProxyUser() {
         return proxyUser;
     }

     public void setProxyUser(String UserName) {
         this.proxyUser = UserName;
     }

     public String getProxyPass() {
         return proxyPass;
     }

     public void setProxyPass(String Password) {
         this.proxyPass = Password;
     }

    public boolean useCompression() {
        return compression;
    }

    public void setUseCompression(boolean value) {
        this.compression = value;
    }
    public boolean isGoogle = false;

    public void setActive(boolean b) {
        active = b;
    }
}
