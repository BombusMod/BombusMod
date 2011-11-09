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
import com.ssttr.crypto.SHA1;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.io.Connector;
//#if android
//# import java.net.Socket;
//# import java.io.InputStream;
//# import java.io.OutputStream;
//#else
import javax.microedition.io.StreamConnection;
//#endif
import ui.Time;
import util.StringUtils;

/**
 *
 * @author root
 */
public class DnsSrvResolver {
    
    class SrvRdata {
        String host;
        int port;
        int ttl;
    }
    class TxtRData {
        Hashtable map;
    }
    
    public final static int XMPP_SRV = 1;
    public final static int XMPP_TXT = 2;
    
    private final static String _srv = "_xmpp-client._tcp.";
    private final static String _txt = "_xmppconnect."; // TODO: TXT records
    
    private String server;
    private String resolvedHost;
    private int resolvedPort;
    private long ttl;
    private Config cf;
    
    public DnsSrvResolver() {
        resolvedPort=5222;
    }
    
        private String getSrvRecordName() {
        String srv="srv#"+server;
        if (srv.length()>32) return srv.substring(0, 31);
        return srv;
    }

    private boolean getCachedSrv() {
        DataInputStream inputStream=NvStorage.ReadFileRecord(getSrvRecordName(), 0);
        try {
            resolvedHost=inputStream.readUTF();
            resolvedPort=inputStream.readInt();
            ttl=inputStream.readLong();
            inputStream.close();

            if (ttl>Time.utcTimeMillis()) {
//#ifdef DEBUG
//#                 System.out.println("Srv cache hit");
//#endif
                return true;
            }
//#ifdef DEBUG
//#             System.out.println("Srv cache expired");
//#endif

        } catch (Exception e) {
//#ifdef DEBUG
//#             System.out.println("Srv cache missed");
//#endif         
        }
        return false;
    }

    private void writeSrvCache() {

        DataOutputStream outputStream=NvStorage.CreateDataOutputStream();
        try {
            outputStream.writeUTF(resolvedHost);
            outputStream.writeInt(resolvedPort);
            outputStream.writeLong(ttl);
        } catch (Exception e) {
//#ifdef DEBUG
//#             e.printStackTrace();
//#endif
        }
        NvStorage.writeFileRecord(outputStream, getSrvRecordName(), 0, true);

    }
    
    private boolean askInetSrv(int type) {
       cf=Config.getInstance();
        
        SHA1 shaVer=new SHA1();
        shaVer.init();
        shaVer.updateASCII(Version.getVersionNumber()+server);
        shaVer.finish();

      /*  if (cf.verHash.equals(shaVer.getDigestHex())) {
            resolvedHost=cf.resolvedHost;
            resolvedPort=cf.resolvedPort;
            //System.out.println(resolvedHost+":"+resolvedPort);
            return true;
        }*/
        try {
//#if android
//# 	    Socket sc = new Socket("8.8.8.8", 53);
//# 	    DataInputStream is = new DataInputStream(sc.getInputStream());
//# 	    DataOutputStream os = new DataOutputStream(sc.getOutputStream());
//#else
            StreamConnection sc = (StreamConnection) Connector.open("socket://8.8.8.8:53");
	    DataInputStream is = sc.openDataInputStream();
            DataOutputStream os = sc.openDataOutputStream();              
//#endif
            byte [] message = encode(server, type);
            byte [] data = new byte[2 + message.length];
            System.arraycopy(message, 0, data, 2, message.length);
            StringUtils.putWordBE(data, 0, message.length);
            os.write(data);
            os.flush();
            byte[] responseHeader = new byte[2];
            is.readFully(responseHeader);
            byte[] response = new byte[StringUtils.getWordBE(responseHeader, 0)];
            is.readFully(response);
            Vector res = decode(response);
            if (res.isEmpty() || res.elementAt(0) == null) // Uncorrect response
                return false;
            
            resolvedHost = ((SrvRdata)res.elementAt(0)).host;
            System.out.println("Resloved host: " + resolvedHost);
            resolvedPort = ((SrvRdata)res.elementAt(0)).port;
            
            ttl = ((SrvRdata)res.elementAt(0)).ttl + Time.utcTimeMillis();
            
            cf.verHash=shaVer.getDigestHex();
            cf.resolvedHost = resolvedHost;
            cf.resolvedPort = resolvedPort;
            cf.saveToStorage();
            return true;
      
        } catch (IOException ex) {
//#ifdef DEBUG            
//#             ex.printStackTrace();
//#endif            
        }
        
        return false;
    }    

    public boolean getSrv(String server, int type){
        this.server=server;

        if (getCachedSrv()) return true;

        if (!askInetSrv(type)) return false;

        writeSrvCache();

        return true;
    }
    
    private byte[] encode(String domain, int type) {
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(os);
            out.writeShort(0x666);
            out.writeByte(0x01); // QR+Opcode+AA+TC+RD = 0000-0001b - RECURSION_DESIRED
            out.writeByte(0x00); // RA+Z+RCode = 0000-0000b
            out.writeShort(0x0001); // queries count
            out.writeShort(0x0000); // answers count
            out.writeShort(0x0000); // authority record count
            out.writeShort(0x0000); // additions count
            StringBuffer domains = new StringBuffer();
            switch (type) {
                case XMPP_SRV:
                    domains.append(_srv);
                    break;
                case XMPP_TXT:
                    domains.append(_txt);
                    break;
            }
            domains.append(domain);
            String[] res = StringUtils.explode(domains.toString(), '.');
            for (int i = 0; i < res.length; ++i) {
                byte[] l = res[i].getBytes();
                out.writeByte(l.length);
                out.write(l);
            }
            out.writeByte(0x00);
            switch(type) {
                case XMPP_SRV:
                    out.writeShort(0x0021); // type: SRV
                    break;
                case XMPP_TXT:
                    out.writeShort(0x0010); // type: TXT
                    break;
            }
            
            out.writeShort(0x0001); // class: Internet
            return os.toByteArray();
        } catch (IOException ex) {
//#ifdef DEBUG            
//#             ex.printStackTrace();
//#endif            
            return null;
        }
    }
    
    private Vector decode (byte[] response) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(response);
            DataInputStream in = new DataInputStream(bais);
            short id = in.readShort(); // id
            short flags = in.readShort(); // flags
            short questions = in.readShort();
            int answers = in.readShort();
            in.readShort();
            in.readShort(); 
            for (int i = 0; i < questions; ++i) {
                while (true) {
                    int length = in.readUnsignedByte();
                    if (0 == length) break;
                    for (int j = 0; j < length; ++j) {
                        in.readUnsignedByte();
                    }
                }
                in.readShort();
                in.readShort();
            }
            Vector res = new Vector();
            for (int i = 0; i < answers; ++i) {
                in.readUnsignedShort(); // ...
                int type = in.readUnsignedShort(); // type
                in.readUnsignedShort(); // class
                int ttl = in.readInt(); // ttl
                int rdlength = in.readUnsignedShort(); // length
                if (type == XMPP_SRV) {
                in.readUnsignedShort();
                in.readUnsignedShort();
                int port = in.readUnsignedShort(); // port
                StringBuffer result = new StringBuffer();
                while (true) {
                    int length = in.readUnsignedByte();
                    if (0 == length) break;
                    for (int j = 0; j < length; ++j) {
                        result.append((char)in.readUnsignedByte());
                    }
                    result.append('.');
                }
                if (443 == port) {
                    port = 5222;
                }
                SrvRdata item = new SrvRdata();
                item.host = result.toString().substring(0, result.length() - 1);
                item.port = port;
                item.ttl = ttl;
                res.addElement(item);
            } else {
                    // TXT
                    StringBuffer result = new StringBuffer();
                    for (int ind = 0; ind < rdlength; ind++) {
                        result.append((char)in.readUnsignedByte());
                    }
                    String rdata = result.toString();
                    SrvRdata item = new SrvRdata(); 
                    item.host = rdata.substring(rdata.indexOf("=") + 1);
                    res.addElement(item);
                }
            } 
            return res;
        } catch (IOException ex) {
//#ifdef DEBUG            
//#             ex.printStackTrace();
//#endif            
            return null;
        }
    }

    public String getHost() { return resolvedHost; }

    public int getPort() { return resolvedPort; }
    
}
