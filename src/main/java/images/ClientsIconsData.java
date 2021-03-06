/*
 *
 * Created on 29.08.2008, 0:20
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

package images;

import Client.Contact;
import java.util.Vector;
import util.StringLoader;

/**
 *
 * @author ad
 */
public class ClientsIconsData {
    
    private static Vector clients[]=new Vector[2];

    private static String restxt= "/images/clients.txt";

    private static ClientsIconsData instance;
    public static ClientsIconsData getInstance() {
	if (instance==null){
            try {
                clients[0]=new Vector();
                clients[1]=new Vector();

                clients=new StringLoader().stringLoader(restxt, 2);
            } catch (Exception e) {
            }
            instance=new ClientsIconsData();
        }
	return instance;
    }
    
    private ClientsIconsData() { }

    private static int getClientIDByCaps(String caps) {
        if (clients.length==0) return -1;
        String lcaps=caps.toLowerCase();
        for (int i=0; i<clients[0].size(); i++) {
            String client=((String) clients[0].elementAt(i)).toLowerCase();
            if (client.indexOf(",")>-1) {
                boolean parse = true;
                int pos=0;
                while (parse) {
                    if (pos>-1) {
                        int endpos=client.indexOf(",", pos);
                        String eqStr=(endpos<0)?client.substring(pos):client.substring(pos, endpos);
                        if (lcaps.indexOf(eqStr)>-1) return i;
                        
                        pos=client.indexOf(",", pos+1);
                        if (pos<0) parse=false; else pos=pos+1;
                    } else parse=false;
                }
            } else {
                if (lcaps.indexOf(client)>-1)
                    return i;
            }
	}
        return -1;
    }
    
    public static void processData(Contact c, String data) {
//#ifdef CLIENTS_ICONS
        if (data == null) {
            c.client = -1;
            c.clientName = "";
            return;
        }
        c.client=getClientIDByCaps(data);
        c.clientName=(c.client>-1)?getClientNameByID(c.client):"";
//#endif
    }
    
    private static String getClientNameByID(int id) {
        if (clients.length==0) return "";
        
        return (String) clients[1].elementAt(id);
    }
    
}
