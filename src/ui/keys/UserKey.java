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

//#ifdef USER_KEYS

package ui.keys;

import images.RosterIcons;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import ui.IconTextElement;
import ui.VirtualCanvas;

/**
 *
 * @author ad
 */
public class UserKey extends IconTextElement {
    public final static String storage = "keys_db_r1026";
    public final static String def_keys = "/def_keys.txt";
    
    private final static int COUNT_KEY_NAMES = 33;
    private int current_index;
    private static int[] someCodes = null;
    private static String[] someNames = null;

    public int command_id = 0;
    public boolean modificator = false;
    public int key;

    public UserKey() {
        super(RosterIcons.getInstance());
        initSomeKeyNames();
    }

    public UserKey(UserKey u) {
        this();
        copyFrom(u);
    }

    public final void copyFrom(UserKey u) {
        if (u == null)
            return;
        command_id = u.command_id;
        modificator = u.modificator;
        key = u.key;
    }

    private void initSomeKeyNames() { // TODO: подумать как и когда обнулять
        someCodes = new int[COUNT_KEY_NAMES];
        someNames = new String[COUNT_KEY_NAMES];

        current_index = 0;

        // Цифровые клавишы
        addKeyName(0, "(0)");
        addKeyName(1, "(1)");
        addKeyName(2, "(2)");
        addKeyName(3, "(3)");
        addKeyName(4, "(4)");
        addKeyName(5, "(5)");
        addKeyName(6, "(6)");
        addKeyName(7, "(7)");
        addKeyName(8, "(8)");
        addKeyName(9, "(9)");
        addKeyName(VirtualCanvas._KEY_STAR, "*");
        addKeyName(VirtualCanvas._KEY_POUND, "#");

        // QWERTY-клавиатура
        addKeyName(8, "BackSpace");
        addKeyName(10, "Enter");
        addKeyName(32, "Space");
        
        // Misc
        addKeyName(VirtualCanvas.KEY_BACK, "Back");
        addKeyName(VirtualCanvas.KEY_GREEN, "Green");
        addKeyName(VirtualCanvas.KEY_CLEAR, "Clear");
        addKeyName(VirtualCanvas.KEY_VOL_UP, "Volume_Up");
        addKeyName(VirtualCanvas.KEY_VOL_DOWN, "Volume_Down");
        addKeyName(VirtualCanvas.KEY_FLIP_OPEN, "Flip_Open");
        addKeyName(VirtualCanvas.KEY_FLIP_CLOSE, "Flip_Close");
        addKeyName(VirtualCanvas.KEY_SOFT_LEFT, "Soft_Left");
        addKeyName(VirtualCanvas.KEY_SOFT_RIGHT, "Soft_Right");

        // Game Action
        addKeyName(VirtualCanvas.KEY_LEFT, "(<)");
        addKeyName(VirtualCanvas.KEY_RIGHT, "(>)");
        addKeyName(VirtualCanvas.KEY_UP, "(^)");
        addKeyName(VirtualCanvas.KEY_DOWN, "(V)");
        addKeyName(VirtualCanvas.KEY_FIRE, "(o)");

        VirtualCanvas canvas = VirtualCanvas.getInstance();
        addKeyName(canvas.getKeyCode(VirtualCanvas.GAME_A), "Game_A");
        addKeyName(canvas.getKeyCode(VirtualCanvas.GAME_B), "Game_B");
        addKeyName(canvas.getKeyCode(VirtualCanvas.GAME_C), "Game_C");
        addKeyName(canvas.getKeyCode(VirtualCanvas.GAME_D), "Game_D");
        canvas = null;
    }
    
    private void addKeyName(int code, String name) {
        someCodes[current_index] = code;
        someNames[current_index] = name;
        current_index++;
    }

    public static String getKeyName(int code, boolean modificator) {
        String prefix = modificator ? "M" : "";

        for (int i = 0; i < COUNT_KEY_NAMES; i++)
            if (someCodes[i] == code)
                return prefix + someNames[i];

        if (((code > 64) && (code < 91))   // [A-Z]
         || ((code > 96) && (code < 123))) // [a-z]
             return prefix + (char) code;

        return prefix + code;
    }
/*
    public String getKeyName() {
        return getKeyName(code, modificator);
    }
*/
    public static UserKey createFromStrings(String id, String name) {
        UserKey u = new UserKey();
        u.command_id = Integer.parseInt(id);
        u.modificator = withModificator(name);
        if (u.modificator)
            name = name.substring(1);
        u.key = getKeyCode(name);
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

    private static boolean withModificator(String name) {
        if ((name.length() > 1) && (name.charAt(0) == 'M'))
            return true;
        return false;
    }

/*
    public boolean equals(Object ob) {
        if (!(ob instanceof UserKey))
            return false;

        UserKey u = (UserKey) ob;
        return (modificator == u.modificator) && (key == u.key);
    }
*/
    public boolean equals(int key) {
        return (this.key == key);
    }

    public boolean equals(int key, boolean modificator) {
        return (this.modificator == modificator) && (this.key == key);
    }

    public String toString() {
        if (command_id == -1)
            return getKeyName(key, modificator) + "; " + "Modificator";
        return getKeyName(key, modificator) + "; " + UserKeyExec.cmds[command_id];
    }

    public String toLine() {
        return new StringBuffer()
                .append(command_id)
                .append((char) 0x09)
                .append(getKeyName(key, modificator))
                .toString();
    }

    public static UserKey createFromDataInputStream(DataInputStream inputStream) throws IOException {
        UserKey u = new UserKey();
        u.command_id = inputStream.readInt();
        u.modificator = inputStream.readBoolean();
        u.key = inputStream.readInt();
        return u;
    }

    public void saveMyToDataOutputStream(DataOutputStream outputStream) {
        try {
            outputStream.writeInt(command_id);
            outputStream.writeBoolean(modificator);
            outputStream.writeInt(key);
        } catch (IOException e) { }
    }

    public int getImageIndex() {
        return 0;
    }
}

//#endif
