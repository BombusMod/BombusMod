/*
 * ColorTheme.java
 *
 * Created on 22.05.2008, 22:39
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

package Colors;

import java.util.Enumeration;
import java.util.Vector;
//#ifdef COLORS
import io.NvStorage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import midlet.*;
//#endif
/**
 *
 * @author ad
 */
public class ColorTheme {
    private static ColorTheme instance;
    
    /*
purple 0x800080
red 0xff0000
orange 0xffa500
green 0x008000
blue 0x0000ff
     */
    
    public static ColorTheme getInstance(){
	if (instance==null) {
	    instance=new ColorTheme();
            init();
//#ifdef COLORS
	    loadFromStorage();
//#endif
	}
	return instance;
    }
    public static Vector colorsContainer;
    
    private static void init() {
        colorsContainer=new Vector();
        
        colorsContainer.addElement(new ColorItem("BALLOON_INK", 0x4866ad));
        colorsContainer.addElement(new ColorItem("BALLOON_BGND", 0xffffe0));
    
        colorsContainer.addElement(new ColorItem("LIST_BGND", 0xffffff));
        colorsContainer.addElement(new ColorItem("LIST_BGND_EVEN", 0xe8f0f0));
        colorsContainer.addElement(new ColorItem("LIST_INK", 0x000000));
    
        colorsContainer.addElement(new ColorItem("MSG_SUBJ", 0xca0000));
        colorsContainer.addElement(new ColorItem("MSG_HIGHLIGHT", 0x7540b0));
    
        colorsContainer.addElement(new ColorItem("DISCO_CMD", 0x000080));
    
        colorsContainer.addElement(new ColorItem("BAR_BGND", 0xad1010));
        colorsContainer.addElement(new ColorItem("BAR_BGND_BOTTOM", 0x730000));
        colorsContainer.addElement(new ColorItem("BAR_INK", 0xffffff));
    
        colorsContainer.addElement(new ColorItem("CONTACT_DEFAULT", 0x000000));
        colorsContainer.addElement(new ColorItem("CONTACT_CHAT", 0x39358b));
        colorsContainer.addElement(new ColorItem("CONTACT_AWAY", 0x008080));
        colorsContainer.addElement(new ColorItem("CONTACT_XA", 0x535353));
        colorsContainer.addElement(new ColorItem("CONTACT_DND", 0x800000));
        colorsContainer.addElement(new ColorItem("CONTACT_J2J", 0xff0000));
        colorsContainer.addElement(new ColorItem("GROUP_INK", 0x000080));
    
        colorsContainer.addElement(new ColorItem("BLK_INK", 0x000000));
        colorsContainer.addElement(new ColorItem("BLK_BGND", 0xffffff));
    
        colorsContainer.addElement(new ColorItem("MESSAGE_IN", 0x0000b0));
        colorsContainer.addElement(new ColorItem("MESSAGE_OUT", 0xb00000));
        colorsContainer.addElement(new ColorItem("MESSAGE_PRESENCE", 0x006000));
        colorsContainer.addElement(new ColorItem("MESSAGE_AUTH", 0x400040));
        colorsContainer.addElement(new ColorItem("MESSAGE_HISTORY", 0x535353));

        colorsContainer.addElement(new ColorItem("MESSAGE_IN_S", 0x0060ff));
        colorsContainer.addElement(new ColorItem("MESSAGE_OUT_S", 0xff4000));
        colorsContainer.addElement(new ColorItem("MESSAGE_PRESENCE_S", 0x00c040));
    
        colorsContainer.addElement(new ColorItem("PGS_REMAINED", 0xeeeeee));
        colorsContainer.addElement(new ColorItem("PGS_COMPLETE_TOP", 0xffe29d));
        colorsContainer.addElement(new ColorItem("PGS_COMPLETE_BOTTOM", 0xa87824));
        colorsContainer.addElement(new ColorItem("PGS_INK", 0x000000));
    
        colorsContainer.addElement(new ColorItem("HEAP_TOTAL", 0xffffff));
        colorsContainer.addElement(new ColorItem("HEAP_FREE", 0x00007f));
    
        colorsContainer.addElement(new ColorItem("CURSOR_BGND", 0xbbddee));
        colorsContainer.addElement(new ColorItem("CURSOR_OUTLINE", 0x1ea5c5));
    
        colorsContainer.addElement(new ColorItem("SCROLL_BRD", 0x565656));
        colorsContainer.addElement(new ColorItem("SCROLL_BAR", 0x929292));
        colorsContainer.addElement(new ColorItem("SCROLL_BGND", 0x727272));
    
        colorsContainer.addElement(new ColorItem("POPUP_MESSAGE_INK",  0x4866ad));
        colorsContainer.addElement(new ColorItem("POPUP_MESSAGE_BGND",  0xffffe0));
        colorsContainer.addElement(new ColorItem("POPUP_SYSTEM_INK",  0x009900));
        colorsContainer.addElement(new ColorItem("POPUP_SYSTEM_BGND",  0xffffe0));
    
        colorsContainer.addElement(new ColorItem("SECOND_LINE", 0xa0a0a0));
    }
    
    public static void setColor(int id, int color) {
	((ColorItem)colorsContainer.elementAt(id)).setColor(color);
    }
    
    public static String getName(int id) {
	return ((ColorItem)colorsContainer.elementAt(id)).getName();
    }
    
    public static int getColor(int id) {
	return ((ColorItem)colorsContainer.elementAt(id)).getColor();
    }
    
    public int getColorByName(String name) {
	for (Enumeration r=colorsContainer.elements(); r.hasMoreElements();) {
	    ColorItem c=(ColorItem)r.nextElement();
	    if (c.getName().equals(name)) return c.getColor();
	}
        return 0;
    }
    
    public static void invertSkin(){
        for (Enumeration r=colorsContainer.elements(); r.hasMoreElements();) {
            ColorItem c=(ColorItem)r.nextElement();
            c.setColor(0xFFFFFF-c.getColor());
        }
    }
    
//#if NICK_COLORS
    public static int strong(int color) {
        if (color==getColor(MESSAGE_IN)) {
            return getColor(MESSAGE_IN_S);
        } else if (color==getColor(MESSAGE_OUT)) {
            return getColor(MESSAGE_OUT_S);
        } else if (color==getColor(MESSAGE_PRESENCE)) {
            return getColor(MESSAGE_PRESENCE_S);
        }
        return color;
    }
//#endif
    
    static class ColorItem {
        private String name;
        private int color;
        
        public ColorItem(String name, int color){
            this.name=name;
            this.color=color;
        }
        
        public String getName() { return name; }
        public int getColor() { return color; }
        public void setColor(int color) { this.color=color; }
    }

    public final static int BALLOON_INK             =0;
    public final static int BALLOON_BGND            =1;

    public final static int LIST_BGND               =2;
    public final static int LIST_BGND_EVEN          =3;
    public final static int LIST_INK                =4;

    public final static int MSG_SUBJ                =5;
    public final static int MSG_HIGHLIGHT           =6;

    public final static int DISCO_CMD               =7;

    public final static int BAR_BGND                =8;
    public final static int BAR_BGND_BOTTOM         =9;
    public final static int BAR_INK                 =10;

    public final static int CONTACT_DEFAULT         =11;
    public final static int CONTACT_CHAT            =12;
    public final static int CONTACT_AWAY            =13;
    public final static int CONTACT_XA              =14;
    public final static int CONTACT_DND             =15;
    public final static int CONTACT_J2J             =16;
    public final static int GROUP_INK               =17;

    public final static int BLK_INK                 =18;
    public final static int BLK_BGND                =19;

    public final static int MESSAGE_IN              =20;
    public final static int MESSAGE_OUT             =21;
    public final static int MESSAGE_PRESENCE        =22;
    public final static int MESSAGE_AUTH            =23;
    public final static int MESSAGE_HISTORY         =24;

    public final static int MESSAGE_IN_S            =25;
    public final static int MESSAGE_OUT_S           =26;
    public final static int MESSAGE_PRESENCE_S      =27;

    public final static int PGS_REMAINED            =28;
    public final static int PGS_COMPLETE_TOP        =29;
    public final static int PGS_COMPLETE_BOTTOM     =30;
    public final static int PGS_INK                 =31;

    public final static int HEAP_TOTAL              =32;
    public final static int HEAP_FREE               =33;

    public final static int CURSOR_BGND             =34;
    public final static int CURSOR_OUTLINE          =35;

    public final static int SCROLL_BRD              =36;
    public final static int SCROLL_BAR              =37;
    public final static int SCROLL_BGND             =38;

    public final static int POPUP_MESSAGE_INK       =39;
    public final static int POPUP_MESSAGE_BGND      =40;
    public final static int POPUP_SYSTEM_INK        =41;
    public final static int POPUP_SYSTEM_BGND       =42;

    public final static int SECOND_LINE             =43;
    
    
//#ifdef COLORS
    public static void loadFromStorage(){
	try {
	    DataInputStream inputStream=NvStorage.ReadFileRecord("ColorDB", 0);
            for (Enumeration r=colorsContainer.elements(); r.hasMoreElements();) {
                ColorItem c=(ColorItem)r.nextElement();
                c.setColor(inputStream.readInt());
            }
	    inputStream.close();
	} catch (Exception e) { }
    }

    public static void saveToStorage(){
	DataOutputStream outputStream=NvStorage.CreateDataOutputStream();
	try {
            for (Enumeration r=colorsContainer.elements(); r.hasMoreElements();) {
                ColorItem c=(ColorItem)r.nextElement();
                outputStream.writeInt(c.getColor());
            }
        } catch (IOException e) { }
	NvStorage.writeFileRecord(outputStream, "ColorDB", 0, true);
    }
//#endif
}
