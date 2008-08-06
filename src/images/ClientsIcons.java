/*
 * ClientsIcons.java
 *
 * Created on 9.06.2008, 22:45
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

import java.util.Vector;
import ui.ImageList;
import util.StringLoader;

/**
 *
 * @author ad
 */
public class ClientsIcons extends ImageList {
    
    private static Vector clients[]=new Vector[2];
    
    private static String res= "/images/clients.png";
    private static String restxt= "/images/clients.txt";
    
    private final static int CLIENTS_IN_ROW=16;
    private static int cols;

    /** Creates a new instance of RosterIcons */
    private ClientsIcons() {
        super(res, cols, CLIENTS_IN_ROW);
    }
    
    private static ClientsIcons instance;
    public static ClientsIcons getInstance() {
	if (instance==null){
            try {
                clients[0]=new Vector();
                clients[1]=new Vector();

                clients=new StringLoader().stringLoader(restxt, 2);

                cols=ceil(CLIENTS_IN_ROW, clients[0].size());
            } catch (Exception e) {
                System.out.print("Can't load ");
                System.out.println(restxt);
            }
            instance=new ClientsIcons();
        }
	return instance;
    }
    
    private static int ceil(int rows, int count){
        int cols=count/rows;
        if (count>(cols*rows))
            cols++;
        return cols;
    }
    
    public int getClientIDByCaps(String caps) {
        int clientID=-1;
        if (clients.length<1) return clientID;
        
        for (int i=0; i<clients[0].size(); i++) {
            String client=((String) clients[0].elementAt(i)).toLowerCase();
            if (client.indexOf(",")>-1) {
                boolean parse = true;
                int pos=0;
                while (parse) {
                    if (pos>-1) {
                        int endpos=client.indexOf(",", pos);
                        String eqStr=(endpos<0)?client.substring(pos):client.substring(pos, endpos);
                        if (caps.toLowerCase().indexOf(eqStr)>-1) return i;
                        
                        pos=client.indexOf(",", pos+1);
                        if (pos<0) parse=false; else pos=pos+1;
                    } else parse=false;
                }
            } else {
                if (caps.indexOf(client)>-1)
                    return i;
            }
	}
        return clientID;        
    }
    
    public String getClientNameByID(int id) {
        if (clients.length<1) return "";
        
        return (String) clients[1].elementAt(id);
    }
    
}
