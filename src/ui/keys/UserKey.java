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
import javax.microedition.lcdui.Canvas;

/**
 *
 * @author ad
 */
public class UserKey extends IconTextElement {
    public final static String storage = "keys_db";
    public final static String def_keys = "/userkeys/bombusmod.txt";
    
    private final static int COUNT_KEY_NAMES = 27;
    private int current_index;
    private static int[] someCodes = null;
    private static String[] someNames = null;

    public int command_id = 0;
    public boolean previous_key_long;
    public boolean key_long;
    public int previous_key;
    public int key;
    public boolean two_keys;

    public UserKey() {
        super(RosterIcons.getInstance());
        initSomeKeyNames();
    }

    public UserKey(UserKey u) {
        this();
        command_id = u.command_id;
        previous_key_long = u.previous_key_long;
        key_long = u.key_long;
        previous_key = u.previous_key;
        key = u.key;
        two_keys = u.two_keys;
    }

    private void initSomeKeyNames() { // TODO: подумать как и когда обнулять
        someCodes = new int[COUNT_KEY_NAMES];
        someNames = new String[COUNT_KEY_NAMES];

        current_index = 0;

        // Цифровые клавишы
        addKeyName(Canvas.KEY_NUM0, "(0)");
        addKeyName(Canvas.KEY_NUM1, "(1)");
        addKeyName(Canvas.KEY_NUM2, "(2)");
        addKeyName(Canvas.KEY_NUM3, "(3)");
        addKeyName(Canvas.KEY_NUM4, "(4)");
        addKeyName(Canvas.KEY_NUM5, "(5)");
        addKeyName(Canvas.KEY_NUM6, "(6)");
        addKeyName(Canvas.KEY_NUM7, "(7)");
        addKeyName(Canvas.KEY_NUM8, "(8)");
        addKeyName(Canvas.KEY_NUM9, "(9)");
        addKeyName(Canvas.KEY_STAR, "*");
        addKeyName(Canvas.KEY_POUND, "#");

        // QWERTY-клавиатура
        addKeyName(8, "BackSpace");
        addKeyName(10, "Enter");
        addKeyName(32, "Space");
        
        // Misc
        addKeyName(Config.KEY_BACK, "Back");
        addKeyName(Config.SOFT_LEFT, "Soft_Left");
        addKeyName(Config.SOFT_RIGHT, "Soft_Right");

        // Game Action
        Canvas display = (Canvas) Client.StaticData.getInstance().canvas;
        addKeyName(display.getKeyCode(Canvas.LEFT), "(<)");
        addKeyName(display.getKeyCode(Canvas.RIGHT), "(>)");
        addKeyName(display.getKeyCode(Canvas.UP), "(^)");
        addKeyName(display.getKeyCode(Canvas.DOWN), "(V)");
        addKeyName(display.getKeyCode(Canvas.FIRE), "(o)");
        addKeyName(display.getKeyCode(Canvas.GAME_A), "Game_A");
        addKeyName(display.getKeyCode(Canvas.GAME_B), "Game_B");
        addKeyName(display.getKeyCode(Canvas.GAME_C), "Game_C");
        addKeyName(display.getKeyCode(Canvas.GAME_D), "Game_D");
        display = null;
    }
    
    private void addKeyName(int code, String name) {
        someCodes[current_index] = code;
        someNames[current_index] = name;
        current_index++;
    }

    private static String getKeyName(int code, boolean long_key) {
        String prefix = long_key ? "L" : "";

        for (int i = 0; i < COUNT_KEY_NAMES; i++)
            if (someCodes[i] == code)
                return prefix + someNames[i];

        if (((code > 64) && (code < 91))   // [A-Z]
         || ((code > 96) && (code < 123))) // [a-z]
             return prefix + (char) code;

        return prefix + code;
    }

    public String getPreviousKeyName() {
        return two_keys ? getKeyName(previous_key, previous_key_long) : "OFF";
    }

    public String getLastKeyName() {
        return getKeyName(key, key_long);
    }
/*
    private void setPreviousKey(String name) {
        previous_key_long = isLongKey(name);
        if (previous_key_long)
            name.substring(1);
        previous_key = getKeyCode(name);
    }

    private void setLastKey(String name) {
        key_long = isLongKey(name);
        if (key_long)
            name.substring(1);
        key = getKeyCode(name);
    }
*/
    public static UserKey createFromStrings(String id, String name1, String name2) {
        UserKey u = new UserKey();

        u.command_id = Integer.parseInt(id);

        u.two_keys = !name1.equals("OFF");
        if (u.two_keys) {
            u.previous_key_long = isLongKey(name1);
            if (u.previous_key_long)
                name1 = name1.substring(1);
            u.previous_key = getKeyCode(name1);
        }

        u.key_long = isLongKey(name2);
        if (u.key_long)
            name2 = name2.substring(1);
        u.key = getKeyCode(name2);

        return u;
    }

    private static int getKeyCode(String name) {
        for (int i = 0; i < COUNT_KEY_NAMES; i++)
            if (name.equals(someNames[i]))
                return someCodes[i];

        if (name.length() == 1) {
            char ch = name.charAt(0);
            if (((ch > 64) && (ch < 91))   // [A-Z]
             || ((ch > 96) && (ch < 123))) // [a-z]
                 return (int) ch;
        }

        return Integer.parseInt(name);
    }

    private static boolean isLongKey(String name) {
        if ((name.length() > 1) && (name.charAt(0) == 'L'))
            return true;

        return false;
    }

    public boolean equals(Object ob) {
        if (!(ob instanceof UserKey))
			return false;

        UserKey u = (UserKey) ob;
        boolean result = (key_long == u.key_long)
                && (key == u.key)
                && (two_keys == u.two_keys);
        if (two_keys)
            result = result
                    && (previous_key_long == u.previous_key_long)
                    && (previous_key == u.previous_key);
        return result;
    }
    
    public String toString() {
        StringBuffer s = new StringBuffer();

        if (two_keys)
            s.append(getPreviousKeyName())
             .append(" + ");

        s.append(getLastKeyName());

//        if (command_id > 0)
        s.append("; ")
         .append(UserKeyExec.cmds[command_id]);
        return s.toString();
    }

    public String toLine() {
        return new StringBuffer()
                .append(command_id)
                .append((char) 0x09)
                .append(getPreviousKeyName())
                .append((char) 0x09)
                .append(getLastKeyName())
                .toString();
    }

    public static UserKey createFromDataInputStream(DataInputStream inputStream) throws IOException {
        UserKey u = new UserKey();

        u.command_id = inputStream.readInt();
        u.previous_key_long = inputStream.readBoolean();
        u.key_long = inputStream.readBoolean();
        u.previous_key = inputStream.readInt();
        u.key = inputStream.readInt();
        u.two_keys = inputStream.readBoolean();

        return u;
    }

    public void saveMyToDataOutputStream(DataOutputStream outputStream) {
        try {
            outputStream.writeInt(command_id);
            outputStream.writeBoolean(previous_key_long);
            outputStream.writeBoolean(key_long);
            outputStream.writeInt(previous_key);
            outputStream.writeInt(key);
            outputStream.writeBoolean(two_keys);
        } catch (IOException e) { }
    }

    public int getImageIndex() {
        return 0;
    }
 }
