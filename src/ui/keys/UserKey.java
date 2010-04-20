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

import images.RosterIcons;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import locale.SR;
import ui.IconTextElement;
import ui.VirtualList;
import Client.Config;

/**
 *
 * @author ad
 */
public class UserKey extends IconTextElement {
//#ifdef PLUGINS
//#     public static String plugin = new String("PLUGIN_USER_KEYS");
//#endif
    
    public final static String storage="keys_db";

    public int command_id   = 0;
    public int previous_key;
    public int key;
    public boolean active   = false;
    public boolean two_keys = true;

    public UserKey(UserKey u) {
        this(u.command_id, u.previous_key, u.key, u.active, u.two_keys);
    }

    public UserKey(int command_id, int previous_key, int key, boolean active, boolean two_keys) {
        this();
        this.command_id = command_id;
        this.previous_key = previous_key;
        this.key = key;
        this.active = active;
        this.two_keys = two_keys;
    }

    public UserKey() {
        super(RosterIcons.getInstance());
    }

    public boolean equals(Object ob) {
        if (ob instanceof UserKey) {
            UserKey u = (UserKey) ob;
            return ((previous_key == u.previous_key) || (!two_keys))
                    && (key == u.key)
                    && (active == u.active)
                    && (two_keys == u.two_keys);
        } else return false;
    }
    
    public String toString(){
        StringBuffer s = new StringBuffer();
        if (two_keys)
            s.append(keyToString(previous_key)+" + ");
        s.append(keyToString(key));
        s.append("; ");
        s.append(COMMANDS_DESC[command_id]);
        return s.toString();
    } 

    public static String keyToString(int key_code) {
        switch(key_code) {
            case VirtualList.KEY_NUM0:
                return "[0]";
            case VirtualList.KEY_NUM1:
                return "[1]";
            case VirtualList.KEY_NUM2:
                return "[2]";
            case VirtualList.KEY_NUM3:
                return "[3]";
            case VirtualList.KEY_NUM4:
                return "[4]";
            case VirtualList.KEY_NUM5:
                return "[5]";
            case VirtualList.KEY_NUM6:
                return "[6]";
            case VirtualList.KEY_NUM7:
                return "[7]";
            case VirtualList.KEY_NUM8:
                return "[8]";
            case VirtualList.KEY_NUM9:
                return "[9]";
            case VirtualList.KEY_STAR:
                return "[*]";
            case VirtualList.KEY_POUND:
                return "[#]";
            case 32:
                return "[Space]";
            case 10:
                return "[Enter]";
            case 8:
                return "[BackSpace]";
            default:
                if (key_code>=0) {
                    if (((key_code>64)&&(key_code<91))     // [A-Z]
                     || ((key_code>96)&&(key_code<123))) { // [a-z]
                        return "["+(char) key_code+"]";
                    } // Выше - положительные коды, ниже - отрицательные.
                } else switch (Client.StaticData.getInstance().roster.getGameAction(key_code)) {
                        case VirtualList.LEFT:
                            return "(<)";
                        case VirtualList.RIGHT:
                            return "(>)";
                        case VirtualList.UP:
                            return "(^)";
                        case VirtualList.DOWN:
                            return "(V)";
                        case VirtualList.FIRE:
                            return "(o)";
                        case VirtualList.GAME_A:
                            return "[Game \"A\"]";
                        case VirtualList.GAME_B:
                            return "[Game \"B\"]";
                        case VirtualList.GAME_C:
                            return "[Game \"C\"]";
                        case VirtualList.GAME_D:
                            return "[Game \"D\"]";
                    default:
                        if (key_code == Config.KEY_BACK) {
                            return "(Key \"Back\")";
                        } else if (key_code == Config.SOFT_LEFT) {
                            return "(Soft Left)";
                        } else if (key_code == Config.SOFT_RIGHT) {
                            return "(Soft Right)";
                        }
                }
        }
        return "[Code \"" + key_code + "\"]";
    }

    public static UserKey createFromDataInputStream(DataInputStream inputStream) {
        UserKey u=new UserKey();
        try {
            u.command_id  = inputStream.readInt();
            u.previous_key = inputStream.readInt();
            u.key        = inputStream.readInt();
            u.active     = inputStream.readBoolean();
            u.two_keys     = inputStream.readBoolean();
        } catch (IOException e) { return null; }
        return u;
    }
    
    public void saveToDataOutputStream(DataOutputStream outputStream){
        try {
            outputStream.writeInt(command_id);
            outputStream.writeInt(previous_key);
            outputStream.writeInt(key);	    
	    outputStream.writeBoolean(active);
            outputStream.writeBoolean(two_keys);
        } catch (IOException e) { }
    }

    public int getImageIndex() {return active?0:5;}

    public static int get_key_code_by_id(int id) {
        switch(id) {
            case 0: return VirtualList.KEY_NUM0;
            case 1: return VirtualList.KEY_NUM1;
            case 2: return VirtualList.KEY_NUM2;
            case 3: return VirtualList.KEY_NUM3;
            case 4: return VirtualList.KEY_NUM4;
            case 5: return VirtualList.KEY_NUM5;
            case 6: return VirtualList.KEY_NUM6;
            case 7: return VirtualList.KEY_NUM7;
            case 8: return VirtualList.KEY_NUM8;
            case 9: return VirtualList.KEY_NUM9;
            case 10: return VirtualList.KEY_STAR;
            default: return -1;
        }
    }

    public static final String[] COMMANDS_DESC = {
        SR.MS_NO,
        SR.MS_OPTIONS,
        SR.MS_CLEAN_ALL_MESSAGES,
        SR.MS_RECONNECT,
        SR.MS_STATS,
        SR.MS_STATUS_MENU,
        SR.MS_FILE_TRANSFERS,
        SR.MS_ARCHIVE,
        SR.MS_DISCO,
        SR.MS_PRIVACY_LISTS,
//#ifdef USER_KEYS
//#         SR.MS_CUSTOM_KEYS,
//#else
        "No action",
//#endif
        SR.MS_CLEAR_POPUPS,
        SR.MS_FLASHLIGHT,
        SR.MS_ABOUT,
        SR.MS_APP_MINIMIZE,
        SR.MS_INVERT,
        SR.MS_XML_CONSOLE,
        SR.MS_FULLSCREEN,
//#ifdef JUICK
//#         SR.MS_JUICK_FOCUS_COMMANDS,
//#else
        "No action",
//#endif
        SR.MS_HEAP_MONITOR
     };
 }
