/*
 * DnsSrvResolver.java
 *
 * Created on 24.04.2008, 21:55
 *
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
 */

package io;

import Client.Config;
import Client.StaticData;
import Info.Version;
import com.ssttr.crypto.MD5;
import com.ssttr.crypto.SHA1;
import java.util.Hashtable;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import locale.SR;
import util.strconv;

/**
 *
 * @author root
 */
public class DnsSrvResolver {
    
    private final static String resolverUrl="http://bombusmod.net.ru/srv/";
    
    private String server;
    private String resolvedHost;
    private int resolvedPort;
    private Config cf;
    
    public DnsSrvResolver() {
        resolvedPort=5222;
    }
    
    public boolean getSrv(String server){
        this.server=server;
        cf=Config.getInstance();
        
        SHA1 shaVer=new SHA1();
        shaVer.init();
        shaVer.updateASCII(Version.getVersionNumber()+server);
        shaVer.finish();

        if (cf.verHash.equals(shaVer.getDigestHex())) {
            resolvedHost=cf.resolvedHost;
            resolvedPort=cf.resolvedPort;
            //System.out.println(resolvedHost+":"+resolvedPort);
            return true;
        }

        StringBuffer url=new StringBuffer(resolverUrl);
        url.append("?host=").append(server);
        
        SHA1 sha=new SHA1();
        sha.init();
        sha.updateASCII(StaticData.getInstance().account.getBareJid());
        sha.finish();
        
        url.append("&name=").append(strconv.urlPrep(Version.NAME));
        url.append("&version=").append(strconv.urlPrep(Version.getVersionNumber()));
        url.append("&lang=").append(strconv.urlPrep(SR.MS_IFACELANG));
        url.append("&os=");
        if (Config.getInstance().enableVersionOs)
            url.append(strconv.urlPrep(Config.getOs()));
        url.append("&hash=").append(sha.getDigestHex());

        try {
            HttpConnection c = (HttpConnection) Connector.open(url.toString());

            //System.out.println(url.toString());
            
            if (c.getResponseCode()!=HttpConnection.HTTP_OK) 
                return false;
            
            Hashtable ht=new util.StringLoader().hashtableLoader(c.openInputStream());
            
            c.close();
            
            resolvedHost=(String)ht.get("host");
            resolvedPort=Integer.parseInt((String)ht.get("port"));
            
            //System.out.println(resolvedHost+":"+resolvedPort);
            
            cf.verHash=shaVer.getDigestHex();
            cf.resolvedHost=resolvedHost;
            cf.resolvedPort=resolvedPort;
            cf.saveToStorage();

            return true;
        } catch (Exception e) { 
            e.printStackTrace();
        }

        return false;
    }

    public String getHost() { return resolvedHost; }

    public int getPort() { return resolvedPort; }
    
}
