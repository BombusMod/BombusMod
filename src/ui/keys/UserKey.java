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
import ui.IconTextElement;
import Client.Config;
import Client.StaticData;
import javax.microedition.lcdui.Canvas;

/**
 *
 * @author ad
 */
public class UserKey extends IconTextElement {
    public final static String storage="keys_db";

    public int commands_id[] = {0, 0, 0};
    public int previous_key;
    public int key;
    public boolean active   = false;
    public boolean two_keys = true;

    public UserKey(UserKey u) {
        this(u.commands_id, u.previous_key, u.key, u.active, u.two_keys);
    }

    public UserKey(int[] commands_id, int previous_key, int key, boolean active, boolean two_keys) {
        this();
        this.commands_id = commands_id;
        this.previous_key = previous_key;
        this.key = key;
        this.active = active;
        this.two_keys = two_keys;
    }

    public UserKey() {
        super(RosterIcons.getInstance());
    }

    public boolean compareTo (UserKey ob) {
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
            s.append(keyToString(previous_key)).append(" + ");
        s.append(keyToString(key));
        for (int i = 0; i < 3; i++)
            s.append("; ").append(UserKeyExec.get_command_by_id(commands_id[i], i).description);
        return s.toString();
    } 

    public static String keyToString(int key_code) {
        switch(key_code) {
            case Canvas.KEY_NUM0:
                return "[0]";
            case Canvas.KEY_NUM1:
                return "[1]";
            case Canvas.KEY_NUM2:
                return "[2]";
            case Canvas.KEY_NUM3:
                return "[3]";
            case Canvas.KEY_NUM4:
                return "[4]";
            case Canvas.KEY_NUM5:
                return "[5]";
            case Canvas.KEY_NUM6:
                return "[6]";
            case Canvas.KEY_NUM7:
                return "[7]";
            case Canvas.KEY_NUM8:
                return "[8]";
            case Canvas.KEY_NUM9:
                return "[9]";
            case Canvas.KEY_STAR:
                return "[*]";
            case Canvas.KEY_POUND:
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
                } else switch (StaticData.getInstance().canvas.getGameAction(key_code)) {
                        case Canvas.LEFT:
                            return "(<)";
                        case Canvas.RIGHT:
                            return "(>)";
                        case Canvas.UP:
                            return "(^)";
                        case Canvas.DOWN:
                            return "(V)";
                        case Canvas.FIRE:
                            return "(o)";
                        case Canvas.GAME_A:
                            return "[Game \"A\"]";
                        case Canvas.GAME_B:
                            return "[Game \"B\"]";
                        case Canvas.GAME_C:
                            return "[Game \"C\"]";
                        case Canvas.GAME_D:
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

    public static UserKey createFromDataInputStream(DataInputStream inputStream) throws IOException {
        UserKey u = new UserKey();

        for (int i = 0; i < 3; i++)
            u.commands_id[i] = inputStream.readInt();
        inputStream.readInt(); // 4-я команда
        u.previous_key = inputStream.readInt();
        u.key = inputStream.readInt();
        u.active = inputStream.readBoolean();
        u.two_keys = inputStream.readBoolean();

        return u;
    }
    
    public void saveToDataOutputStream(DataOutputStream outputStream) {
        try {
            for (int i = 0; i < 3; i++)
                outputStream.writeInt(commands_id[i]);
            outputStream.writeInt(0); // 4-я команда
            outputStream.writeInt(previous_key);
            outputStream.writeInt(key);	    
	    outputStream.writeBoolean(active);
            outputStream.writeBoolean(two_keys);
        } catch (IOException e) { }
    }

    public int getImageIndex() {return active?0:5;}

    public static int get_key_code_by_id(int id) {
        switch(id) {
            case 0: return Canvas.KEY_NUM0;
            case 1: return Canvas.KEY_NUM1;
            case 2: return Canvas.KEY_NUM2;
            case 3: return Canvas.KEY_NUM3;
            case 4: return Canvas.KEY_NUM4;
            case 5: return Canvas.KEY_NUM5;
            case 6: return Canvas.KEY_NUM6;
            case 7: return Canvas.KEY_NUM7;
            case 8: return Canvas.KEY_NUM8;
            case 9: return Canvas.KEY_NUM9;
            case 10: return Canvas.KEY_STAR;
            default: return -1;
        }
    }
 }
