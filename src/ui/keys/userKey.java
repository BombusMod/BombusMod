/*
 * userKey.java
 *
 * Created on 14.09.2007, 10:42
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

package ui.keys;

import Client.StaticData;
import images.RosterIcons;
import io.NvStorage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import ui.IconTextElement;

/**
 *
 * @author User
 */
public class userKey extends IconTextElement {
    public final static String storage="keys_db";
            
    private int    commandId = 0;
    private int    key       = -1;
    public boolean active    = false;

    public userKey() {
        super(RosterIcons.getInstance());
    }
    
    public static userKey loadUserKey(int index){
	StaticData sd=StaticData.getInstance();
	userKey u=userKey.createFromStorage(index);
        return u;
    }
    
    public String toString(){
        StringBuffer s=new StringBuffer("");
        s.append("(* + ");
        s.append(userKeyExec.getKeyDesc(key));
        s.append(") ");
        s.append(getDesc());
        
        return s.toString();
    }
    
    public String getDesc(){
        return userKeyExec.getDesc(commandId);
    }
    
    public static userKey createFromStorage(int index) {
        userKey u=null;
        DataInputStream is=NvStorage.ReadFileRecord(storage, 0);
        if (is==null) return null;
        try {
            do {
                if (is.available()==0) {u=null; break;}
                u=createFromDataInputStream(is);
                index--;
            } while (index>-1);
            is.close();
        } catch (Exception e) { /*e.printStackTrace();*/ }
        return u;
    }    
    
    public static userKey createFromDataInputStream(DataInputStream inputStream){
        userKey u=new userKey();
        try {
            u.commandId  = inputStream.readInt();
            u.key        = inputStream.readInt();
            u.active     = inputStream.readBoolean();
        } catch (IOException e) { /*e.printStackTrace();*/ }
            
        return (u.key==-1)?null:u;
    }
    
    public void saveToDataOutputStream(DataOutputStream outputStream){
        try {
            outputStream.writeInt(commandId);
            outputStream.writeInt(key);	    
	    outputStream.writeBoolean(active);	    
        } catch (IOException e) { }
        
    }
    
    
    public int getCommandId() { return commandId; }
    
    public int getKey(){ return (key<0)?0:key; }
    
    public boolean getActive () { return active; }
    
    
    
    public void setCommand(int descId) { this.commandId = descId; }

    public void setKey(int key) { this.key = key; }

    public void setActive(boolean active) { this.active = active; }
    
    protected int getImageIndex() {return active?0:5;}
    
    public void onSelect(){};
    
    public String getTipString() { return null; }
}

