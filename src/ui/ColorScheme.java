/*
 * ColorScheme.java
 *
 * Created on 20.02.2005, 21:20
 * Copyright (c) 2006-2007, Daniel Apatin (ad), http://apatin.net.ru
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
 *
 */

package ui;
//#ifdef COLORS
//# import io.NvStorage;
//# import java.io.DataInputStream;
//# import java.io.DataOutputStream;
//# import java.io.IOException;
//#endif

public class ColorScheme {
    private static ColorScheme instance;
    
    public static ColorScheme getInstance(){
	if (instance==null) {
	    instance=new ColorScheme();
//#ifdef COLORS
//# 	    instance.loadFromStorage();
//#endif
	}
	return instance;
    }
    
//#if NICK_COLORS
//#     static int strong(int color) {
//#         if (color==MESSAGE_IN) {
//#             return MESSAGE_IN_S;
//#         } else if (color==MESSAGE_OUT) {
//#             return MESSAGE_OUT_S;
//#         } else if (color==MESSAGE_PRESENCE) {
//#             return MESSAGE_PRESENCE_S;
//#         }
//#         return color;
//#     }
//#endif
    
//#ifdef COLORS
//#     protected void loadFromStorage(){
//# 	try {
//# 	    DataInputStream inputStream=NvStorage.ReadFileRecord("ColorDB", 0);
//#             BALLOON_INK=inputStream.readInt();
//#             BALLOON_BGND=inputStream.readInt();
//#             LIST_BGND=inputStream.readInt();
//#             LIST_BGND_EVEN=inputStream.readInt();
//#             LIST_INK=inputStream.readInt();
//#             MSG_SUBJ=inputStream.readInt();
//#             MSG_HIGHLIGHT=inputStream.readInt();
//#             DISCO_CMD=inputStream.readInt();
//#             BAR_BGND=inputStream.readInt();
//#             BAR_INK=inputStream.readInt();
//#             CONTACT_DEFAULT=inputStream.readInt();
//#             CONTACT_CHAT=inputStream.readInt();
//#             CONTACT_AWAY=inputStream.readInt();
//#             CONTACT_XA=inputStream.readInt();
//#             CONTACT_DND=inputStream.readInt();
//#             GROUP_INK=inputStream.readInt();
//#             BLK_INK=inputStream.readInt();
//#             BLK_BGND=inputStream.readInt();
//#             MESSAGE_IN=inputStream.readInt();
//#             MESSAGE_OUT=inputStream.readInt();
//#             MESSAGE_PRESENCE=inputStream.readInt();
//#             MESSAGE_AUTH=inputStream.readInt();
//#             MESSAGE_HISTORY=inputStream.readInt();
//#             PGS_REMAINED=inputStream.readInt();
//#             PGS_COMPLETE=inputStream.readInt();
//#             inputStream.readInt();//PGS_BORDER=inputStream.readInt();
//#             inputStream.readInt();//PGS_BGND=inputStream.readInt();
//#             HEAP_TOTAL=inputStream.readInt();
//#             HEAP_FREE=inputStream.readInt();
//#             CURSOR_BGND=inputStream.readInt();
//#             SCROLL_BRD=inputStream.readInt();
//#             SCROLL_BAR=inputStream.readInt();
//#             SCROLL_BGND=inputStream.readInt();
//#             CURSOR_OUTLINE=inputStream.readInt();
//#if NICK_COLORS
//#             MESSAGE_IN_S=inputStream.readInt();
//#             MESSAGE_OUT_S=inputStream.readInt();
//#             MESSAGE_PRESENCE_S=inputStream.readInt();
//#else
//#             inputStream.readInt();
//#             inputStream.readInt();
//#             inputStream.readInt();
//#endif
//#             CONTACT_J2J=inputStream.readInt();
//#             BAR_BGND_BOTTOM=inputStream.readInt();
//# 
//# 	    inputStream.close();
//# 	} catch (Exception e) { }
//#     }
//# 
//#     public static void saveToStorage(){
//# 	DataOutputStream outputStream=NvStorage.CreateDataOutputStream();
//# 	try {
//# 	    outputStream.writeInt(BALLOON_INK);
//# 	    outputStream.writeInt(BALLOON_BGND);
//# 	    outputStream.writeInt(LIST_BGND);
//# 	    outputStream.writeInt(LIST_BGND_EVEN);
//# 	    outputStream.writeInt(LIST_INK);
//# 	    outputStream.writeInt(MSG_SUBJ);
//# 	    outputStream.writeInt(MSG_HIGHLIGHT);
//# 	    outputStream.writeInt(DISCO_CMD);
//# 	    outputStream.writeInt(BAR_BGND);
//# 	    outputStream.writeInt(BAR_INK);
//# 	    outputStream.writeInt(CONTACT_DEFAULT);
//# 	    outputStream.writeInt(CONTACT_CHAT);
//# 	    outputStream.writeInt(CONTACT_AWAY);
//# 	    outputStream.writeInt(CONTACT_XA);
//# 	    outputStream.writeInt(CONTACT_DND);
//# 	    outputStream.writeInt(GROUP_INK);
//# 	    outputStream.writeInt(BLK_INK);
//# 	    outputStream.writeInt(BLK_BGND);
//# 	    outputStream.writeInt(MESSAGE_IN);
//# 	    outputStream.writeInt(MESSAGE_OUT);
//# 	    outputStream.writeInt(MESSAGE_PRESENCE);
//# 	    outputStream.writeInt(MESSAGE_AUTH);
//# 	    outputStream.writeInt(MESSAGE_HISTORY);
//# 	    outputStream.writeInt(PGS_REMAINED);
//# 	    outputStream.writeInt(PGS_COMPLETE);
//# 	    outputStream.writeInt(0);//outputStream.writeInt(PGS_BORDER);
//# 	    outputStream.writeInt(0);//outputStream.writeInt(PGS_BGND);
//# 	    outputStream.writeInt(HEAP_TOTAL);
//# 	    outputStream.writeInt(HEAP_FREE);
//# 	    outputStream.writeInt(CURSOR_BGND);
//# 	    outputStream.writeInt(SCROLL_BRD);
//# 	    outputStream.writeInt(SCROLL_BAR);
//# 	    outputStream.writeInt(SCROLL_BGND);
//# 	    outputStream.writeInt(CURSOR_OUTLINE);
//#if NICK_COLORS
//# 	    outputStream.writeInt(MESSAGE_IN_S);
//# 	    outputStream.writeInt(MESSAGE_OUT_S);
//# 	    outputStream.writeInt(MESSAGE_PRESENCE_S);
//#else
//# 	    outputStream.writeInt(0);
//# 	    outputStream.writeInt(0);
//# 	    outputStream.writeInt(0);
//#endif
//# 	    outputStream.writeInt(CONTACT_J2J);
//# 	    outputStream.writeInt(BAR_BGND_BOTTOM);
//# 
//#         } catch (IOException e) { }
//# 	NvStorage.writeFileRecord(outputStream, "ColorDB", 0, true);
//#     }
//#endif
    

    public static int BALLOON_INK	=0x4866ad;
    public static int BALLOON_BGND	=0xffffe0;
    public static int LIST_BGND         =0xffffff;
    public static int LIST_BGND_EVEN	=0xf8f0f0;
    public static int LIST_INK          =0x000000;
    public static int MSG_SUBJ          =0xa00000;
    public static int MSG_HIGHLIGHT	=0x904090;
    public static int DISCO_CMD         =0x000080;
    
    public static int BAR_BGND          =0xbb0000;
    public static int BAR_BGND_BOTTOM   =0xa00000;
    public static int BAR_INK           =0xffffff;
    
    public static int CONTACT_DEFAULT	=0x000000;
    public static int CONTACT_CHAT	=0x39358b;
    public static int CONTACT_AWAY	=0x008080;
    public static int CONTACT_XA	=0x535353;
    public static int CONTACT_DND	=0x800000;
    public static int GROUP_INK         =0x000080;
    public static int BLK_INK           =0xffffff;
    public static int BLK_BGND          =0x000000;
    public static int MESSAGE_IN        =0x0000b0;
    public static int MESSAGE_OUT       =0xb00000;
    public static int MESSAGE_PRESENCE  =0x006000;
    public static int MESSAGE_AUTH	=0x400040;
    public static int MESSAGE_HISTORY	=0x535353;
    public static int PGS_REMAINED	=0xffffff;
    public static int PGS_COMPLETE	=0x0000ff;
    //public static int PGS_BORDER	=0x808080;
    //public static int PGS_BGND          =0x000000;
    public static int HEAP_TOTAL	=0xffffff;
    public static int HEAP_FREE         =0x00007f;
    public static int CURSOR_BGND	=0xffb0a6;
    public static int CURSOR_OUTLINE	=0xf5dbdb;
    public static int SCROLL_BRD	=0x950d04;
    public static int SCROLL_BAR	=0xbbbbbb;
    public static int SCROLL_BGND	=0xffffff;
//#if NICK_COLORS
//#     public static int MESSAGE_IN_S       =0x0060ff;
//#     public static int MESSAGE_OUT_S      =0xff4000;
//#     public static int MESSAGE_PRESENCE_S =0x00c040;
//#endif
    public static int CONTACT_J2J        =0xff0000;
}

