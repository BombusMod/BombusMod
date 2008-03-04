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
//# import Client.StaticData;
//# import java.util.Hashtable;
//# import util.StringLoader;
//#endif

//#if FILE_IO
import io.file.FileIO;
//#endif
//#ifdef COLORS
//# import io.NvStorage;
//# import java.io.DataInputStream;
//# import java.io.DataOutputStream;
//# import java.io.IOException;
//# import java.io.InputStream;
//#endif
//#if TRANSLIT
//# import util.Translit;
//#endif

public class ColorScheme {
    private static ColorScheme instance;

//#ifdef COLORS
//#     private static Hashtable skin;
//#     
//#     private static String skinFile;
//# 
//#     private static int resourceType=1;
//#endif
    
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
    
//#ifdef NEW_YEAR
//#     public static int BALLOON_INK=0x4866ad;
//#     public static int BALLOON_BGND=0xc4dcff;
//#     public static int LIST_BGND=0xf1f9ff;
//#     public static int LIST_BGND_EVEN=0xdfefff;
//#     public static int LIST_INK=0x2144d3;
//#     public static int MSG_SUBJ=0xf20000;
//#     public static int MSG_HIGHLIGHT=0xee28f9;
//#     public static int DISCO_CMD=0x0a0098;
//#     public static int BAR_BGND=0x2b47e8;
//#     public static int BAR_BGND_BOTTOM=0x1f37da;
//#     public static int BAR_INK=0xffffff;
//#     public static int CONTACT_DEFAULT=0x6073ca;
//#     public static int CONTACT_CHAT=0x34a500;
//#     public static int CONTACT_AWAY=0x858d8e;
//#     public static int CONTACT_XA=0x7279a8;
//#     public static int CONTACT_DND=0xb50200;
//#     public static int GROUP_INK=0x243969;
//#     public static int BLK_INK=0x3368df;
//#     public static int BLK_BGND=0xc2d5e6;
//#     public static int MESSAGE_IN=0x5a6360;
//#     public static int MESSAGE_OUT=0x400040;
//#     public static int MESSAGE_PRESENCE=0x94a2ee;
//#     public static int MESSAGE_IN_S=0x5a6360;
//#     public static int MESSAGE_OUT_S=0x400040;
//#     public static int MESSAGE_PRESENCE_S=0x94a2ee;
//#     public static int MESSAGE_AUTH=0x1abe29;
//#     public static int MESSAGE_HISTORY=0x456b85;
//#     public static int PGS_REMAINED=0x39448d;
//#     public static int PGS_COMPLETE=0xffffff;
//#     public static int PGS_BORDER=0x132776;
//#     public static int PGS_BGND=0x254583;
//#     public static int HEAP_TOTAL=0xffffff;
//#     public static int HEAP_FREE=0x2643d7;
//#     public static int CURSOR_BGND=0xc0deff;
//#     public static int CURSOR_OUTLINE=0xbad3f3;
//#     public static int SCROLL_BRD=0x0589b8;
//#     public static int SCROLL_BAR=0xbbbbbb;
//#     public static int SCROLL_BGND=0xdddddd;
//#     public static int CONTACT_J2J=0xff0000;
//#else
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
    public static int PGS_BORDER	=0x808080;
    public static int PGS_BGND          =0x000000;
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
//#endif
    
//#ifdef COLORS
//#     protected void loadFromStorage(){
//# 	try {
//# 	    DataInputStream inputStream=NvStorage.ReadFileRecord("ColorDB", 0);
//#             
//#             BALLOON_INK=inputStream.readInt();
//#             BALLOON_BGND=inputStream.readInt();
//# 
//#             LIST_BGND=inputStream.readInt();
//#             LIST_BGND_EVEN=inputStream.readInt();
//#             LIST_INK=inputStream.readInt();
//#             MSG_SUBJ=inputStream.readInt();
//#             MSG_HIGHLIGHT=inputStream.readInt();
//# 
//#             DISCO_CMD=inputStream.readInt();
//#             
//#             BAR_BGND=inputStream.readInt();
//#             BAR_INK=inputStream.readInt();
//# 
//#             CONTACT_DEFAULT=inputStream.readInt();
//#             CONTACT_CHAT=inputStream.readInt();
//#             CONTACT_AWAY=inputStream.readInt();
//#             CONTACT_XA=inputStream.readInt();
//#             CONTACT_DND=inputStream.readInt();
//# 
//#             GROUP_INK=inputStream.readInt();
//# 
//#             BLK_INK=inputStream.readInt();
//#             BLK_BGND=inputStream.readInt();
//# 
//#             MESSAGE_IN=inputStream.readInt();
//#             MESSAGE_OUT=inputStream.readInt();
//#             MESSAGE_PRESENCE=inputStream.readInt();
//#             MESSAGE_AUTH=inputStream.readInt();
//#             MESSAGE_HISTORY=inputStream.readInt();
//# 
//#             PGS_REMAINED=inputStream.readInt();
//#             PGS_COMPLETE=inputStream.readInt();
//#             PGS_BORDER=inputStream.readInt();
//#             PGS_BGND=inputStream.readInt();
//# 
//#             HEAP_TOTAL=inputStream.readInt();
//#             HEAP_FREE=inputStream.readInt();
//# 
//#             CURSOR_BGND=inputStream.readInt();
//# 
//#             SCROLL_BRD=inputStream.readInt();
//#             SCROLL_BAR=inputStream.readInt();
//#             SCROLL_BGND=inputStream.readInt();
//#             
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
//# 
//#             BAR_BGND_BOTTOM=inputStream.readInt();
//# 
//# 	    inputStream.close();
//# 	} catch (Exception e) { }
//#     }
//# 
//#     protected void saveToStorage(){
//# 	DataOutputStream outputStream=NvStorage.CreateDataOutputStream();
//# 	try {
//# 	    outputStream.writeInt(BALLOON_INK);
//# 	    outputStream.writeInt(BALLOON_BGND);
//#             
//# 	    outputStream.writeInt(LIST_BGND);
//# 	    outputStream.writeInt(LIST_BGND_EVEN);
//# 	    outputStream.writeInt(LIST_INK);
//# 	    outputStream.writeInt(MSG_SUBJ);
//# 	    outputStream.writeInt(MSG_HIGHLIGHT);
//#             
//# 	    outputStream.writeInt(DISCO_CMD);
//#             
//#             outputStream.writeInt(BAR_BGND);
//#             outputStream.writeInt(BAR_INK);
//#             
//# 	    outputStream.writeInt(CONTACT_DEFAULT);
//# 	    outputStream.writeInt(CONTACT_CHAT);
//# 	    outputStream.writeInt(CONTACT_AWAY);
//# 	    outputStream.writeInt(CONTACT_XA);
//# 	    outputStream.writeInt(CONTACT_DND);
//#             
//# 	    outputStream.writeInt(GROUP_INK);
//#             
//# 	    outputStream.writeInt(BLK_INK);
//# 	    outputStream.writeInt(BLK_BGND);
//#             
//# 	    outputStream.writeInt(MESSAGE_IN);
//# 	    outputStream.writeInt(MESSAGE_OUT);
//# 	    outputStream.writeInt(MESSAGE_PRESENCE);
//# 	    outputStream.writeInt(MESSAGE_AUTH);
//# 	    outputStream.writeInt(MESSAGE_HISTORY);
//#             
//# 	    outputStream.writeInt(PGS_REMAINED);
//# 	    outputStream.writeInt(PGS_COMPLETE);
//# 	    outputStream.writeInt(PGS_BORDER);
//# 	    outputStream.writeInt(PGS_BGND);
//#             
//# 	    outputStream.writeInt(HEAP_TOTAL);
//# 	    outputStream.writeInt(HEAP_FREE);
//#             
//# 	    outputStream.writeInt(CURSOR_BGND);
//#             
//# 	    outputStream.writeInt(SCROLL_BRD);
//# 	    outputStream.writeInt(SCROLL_BAR);
//# 	    outputStream.writeInt(SCROLL_BGND);
//#             
//#             outputStream.writeInt(CURSOR_OUTLINE);
//#if NICK_COLORS
//# 	    outputStream.writeInt(MESSAGE_IN_S);
//# 	    outputStream.writeInt(MESSAGE_OUT_S);
//# 	    outputStream.writeInt(MESSAGE_PRESENCE_S);
//#else
//#             outputStream.writeInt(0);
//#             outputStream.writeInt(0);
//#             outputStream.writeInt(0);
//#endif
//# 	    outputStream.writeInt(CONTACT_J2J);
//#    
//# 	    outputStream.writeInt(BAR_BGND_BOTTOM);
//# 
//#         } catch (IOException e) { }
//# 	NvStorage.writeFileRecord(outputStream, "ColorDB", 0, true);
//#     }
//#     
//#     
//#     public void loadSkin(String skinFile, int resourceType){
//#         this.skinFile=skinFile;
//#         this.resourceType=resourceType;
//#         try {
//#             BALLOON_INK=loadInt("BALLOON_INK", BALLOON_INK);
//#             BALLOON_BGND=loadInt("BALLOON_BGND", BALLOON_BGND);
//#             LIST_BGND=loadInt("LIST_BGND", LIST_BGND);
//#             LIST_BGND_EVEN=loadInt("LIST_BGND_EVEN", LIST_BGND_EVEN);
//#             LIST_INK=loadInt("LIST_INK", LIST_INK);
//#             MSG_SUBJ=loadInt("MSG_SUBJ", MSG_SUBJ);
//#             MSG_HIGHLIGHT=loadInt("MSG_HIGHLIGHT", MSG_HIGHLIGHT);
//#             DISCO_CMD=loadInt("DISCO_CMD", DISCO_CMD);
//#             BAR_BGND=loadInt("BAR_BGND", BAR_BGND);
//#             BAR_BGND_BOTTOM=loadInt("BAR_BGND_BOTTOM", BAR_BGND_BOTTOM);
//#             BAR_INK=loadInt("BAR_INK", BAR_INK);
//#             CONTACT_DEFAULT=loadInt("CONTACT_DEFAULT", CONTACT_DEFAULT);
//#             CONTACT_CHAT=loadInt("CONTACT_CHAT", CONTACT_CHAT);
//#             CONTACT_AWAY=loadInt("CONTACT_AWAY", CONTACT_AWAY);
//#             CONTACT_XA=loadInt("CONTACT_XA", CONTACT_XA);
//#             CONTACT_DND=loadInt("CONTACT_DND", CONTACT_DND);
//#             GROUP_INK=loadInt("GROUP_INK", GROUP_INK);
//#             BLK_INK=loadInt("BLK_INK", BLK_INK);
//#             BLK_BGND=loadInt("BLK_BGND", BLK_BGND);
//#             MESSAGE_IN=loadInt("MESSAGE_IN", MESSAGE_IN);
//#             MESSAGE_OUT=loadInt("MESSAGE_OUT", MESSAGE_OUT);
//#             MESSAGE_PRESENCE=loadInt("MESSAGE_PRESENCE", MESSAGE_PRESENCE);
//#if NICK_COLORS
//#             MESSAGE_IN_S=loadInt("MESSAGE_IN_S", MESSAGE_IN_S);
//#             MESSAGE_OUT_S=loadInt("MESSAGE_OUT_S", MESSAGE_OUT_S);
//#             MESSAGE_PRESENCE_S=loadInt("MESSAGE_PRESENCE_S", MESSAGE_PRESENCE_S);
//#endif
//#             MESSAGE_AUTH=loadInt("MESSAGE_AUTH", MESSAGE_AUTH);
//#             MESSAGE_HISTORY=loadInt("MESSAGE_HISTORY", MESSAGE_HISTORY);
//#             PGS_REMAINED=loadInt("PGS_REMAINED", PGS_REMAINED);
//#             PGS_COMPLETE=loadInt("PGS_COMPLETE", PGS_COMPLETE);
//#             PGS_BORDER=loadInt("PGS_BORDER", PGS_BORDER);
//#             PGS_BGND=loadInt("PGS_BGND", PGS_BGND);
//#             HEAP_TOTAL=loadInt("HEAP_TOTAL", HEAP_TOTAL);
//#             HEAP_FREE=loadInt("HEAP_FREE", HEAP_FREE);
//#             CURSOR_BGND=loadInt("CURSOR_BGND", CURSOR_BGND);
//#             CURSOR_OUTLINE=loadInt("CURSOR_OUTLINE", CURSOR_OUTLINE);
//#             SCROLL_BRD=loadInt("SCROLL_BRD", SCROLL_BRD);
//#             SCROLL_BAR=loadInt("SCROLL_BAR", SCROLL_BAR);
//#             SCROLL_BGND=loadInt("SCROLL_BGND", SCROLL_BGND);
//#             CONTACT_J2J=loadInt("CONTACT_J2J", CONTACT_J2J);
//#             saveToStorage();
//#         } catch (Exception e) { }
//#         skin=null;
//#         skinFile=null;
//#     }
//#     
//#     private static int loadInt(String key, int defaultColor) {
//#         if (skin==null) {
//#             //System.out.println(skinFile);
//# 
//#             switch (resourceType) {
//#if FILE_IO
//#                 case 0:
//#                     byte[] b = null;
//#                     int len=0;
//#                     try {
//#                         FileIO f=FileIO.createConnection(skinFile);
//#                         InputStream is=f.openInputStream();
//#                         len=(int)f.fileSize();
//#                         b=new byte[len];
//# 
//#                         is.read(b);
//#                         is.close();
//#                         f.close();
//#                     } catch (Exception e) {}
//#                     if (b!=null) {
//#                         String str=new String(b, 0, len).toString().trim();
//#                         skin=new StringLoader().hashtableLoaderFromString(str);
//#                     } else
//#                     return defaultColor;
//#                     break;
//#endif
//#                 case 1:
//#                     skin=new StringLoader().hashtableLoader(skinFile);
//#                     break;
//#                     
//#                 case 2:
//#                     skin=new StringLoader().hashtableLoaderFromString(skinFile);
//#             }
//#         }
//#         try {
//#             String value=(String)skin.get(key);
//#             return getColorInt(value);
//#         } catch (Exception e) {
//#             //StaticData.getInstance().roster.errorLog(e.toString());
//#             return defaultColor;
//#         }
//#     }
//#     
//#     public static String getSkin(){
//#         StringBuffer body=new StringBuffer();
//#ifdef TRANSLIT
//#         body.append("xmlSkin\t"+Translit.translit(StaticData.getInstance().account.getNickName()));
//#else
//#         body.append("xmlSkin\t"+StaticData.getInstance().account.getNickName());
//#endif
//#         body.append("\r\n");
//#         body.append("BALLOON_INK\t"+getColorString(BALLOON_INK)+"\r\n");
//#         body.append("BALLOON_BGND\t"+getColorString(BALLOON_BGND)+"\r\n");
//#         body.append("LIST_BGND\t"+getColorString(LIST_BGND)+"\r\n");
//#         body.append("LIST_BGND_EVEN\t"+getColorString(LIST_BGND_EVEN)+"\r\n");
//#         body.append("LIST_INK\t"+getColorString(LIST_INK)+"\r\n");
//#         body.append("MSG_SUBJ\t"+getColorString(MSG_SUBJ)+"\r\n");
//#         body.append("MSG_HIGHLIGHT\t"+getColorString(MSG_HIGHLIGHT)+"\r\n");
//#         body.append("DISCO_CMD\t"+getColorString(DISCO_CMD)+"\r\n");
//#         body.append("BAR_BGND\t"+getColorString(BAR_BGND)+"\r\n");
//#         body.append("BAR_BGND_BOTTOM\t"+getColorString(BAR_BGND_BOTTOM)+"\r\n");
//#         body.append("BAR_INK\t"+getColorString(BAR_INK)+"\r\n");
//#         body.append("CONTACT_DEFAULT\t"+getColorString(CONTACT_DEFAULT)+"\r\n");
//#         body.append("CONTACT_CHAT\t"+getColorString(CONTACT_CHAT)+"\r\n");
//#         body.append("CONTACT_AWAY\t"+getColorString(CONTACT_AWAY)+"\r\n");
//#         body.append("CONTACT_XA\t"+getColorString(CONTACT_XA)+"\r\n");
//#         body.append("CONTACT_DND\t"+getColorString(CONTACT_DND)+"\r\n");
//#         body.append("GROUP_INK\t"+getColorString(GROUP_INK)+"\r\n");
//#         body.append("BLK_INK\t"+getColorString(BLK_INK)+"\r\n");
//#         body.append("BLK_BGND\t"+getColorString(BLK_BGND)+"\r\n");
//#         body.append("MESSAGE_IN\t"+getColorString(MESSAGE_IN)+"\r\n");
//#         body.append("MESSAGE_OUT\t"+getColorString(MESSAGE_OUT)+"\r\n");
//#         body.append("MESSAGE_PRESENCE\t"+getColorString(MESSAGE_PRESENCE)+"\r\n");
//#if NICK_COLORS
//#         body.append("MESSAGE_IN_S\t"+getColorString(MESSAGE_IN_S)+"\r\n");
//#         body.append("MESSAGE_OUT_S\t"+getColorString(MESSAGE_OUT_S)+"\r\n");
//#         body.append("MESSAGE_PRESENCE_S\t"+getColorString(MESSAGE_PRESENCE_S)+"\r\n");
//#endif
//#         body.append("MESSAGE_AUTH\t"+getColorString(MESSAGE_AUTH)+"\r\n");
//#         body.append("MESSAGE_HISTORY\t"+getColorString(MESSAGE_HISTORY)+"\r\n");
//#         body.append("PGS_REMAINED\t"+getColorString(PGS_REMAINED)+"\r\n");
//#         body.append("PGS_COMPLETE\t"+getColorString(PGS_COMPLETE)+"\r\n");
//#         body.append("PGS_BORDER\t"+getColorString(PGS_BORDER)+"\r\n");
//#         body.append("PGS_BGND\t"+getColorString(PGS_BGND)+"\r\n");
//#         body.append("HEAP_TOTAL\t"+getColorString(HEAP_TOTAL)+"\r\n");
//#         body.append("HEAP_FREE\t"+getColorString(HEAP_FREE)+"\r\n");
//#         body.append("CURSOR_BGND\t"+getColorString(CURSOR_BGND)+"\r\n");
//#         body.append("CURSOR_OUTLINE\t"+getColorString(CURSOR_OUTLINE)+"\r\n");
//#         body.append("SCROLL_BRD\t"+getColorString(SCROLL_BRD)+"\r\n");
//#         body.append("SCROLL_BAR\t"+getColorString(SCROLL_BAR)+"\r\n");
//#         body.append("SCROLL_BGND\t"+getColorString(SCROLL_BGND)+"\r\n");
//#         body.append("CONTACT_J2J\t"+getColorString(CONTACT_J2J)+"\r\n");
//#         return body.toString();
//#     }
//# 
//#     public static String ColorToString(int cRed, int cGreen, int cBlue) {
//#         StringBuffer color=new StringBuffer(8);
//#         
//#         color.append("0x");
//#         color.append(expandHex(cRed));
//#         color.append(expandHex(cGreen));
//#         color.append(expandHex(cBlue));
//#         
//#         return color.toString();
//#     }
//#     
//#     public static String expandHex(int eVal) {
//#         String rVal=Integer.toHexString(eVal);
//#         if (rVal.length()==1) rVal="0"+rVal;
//#       
//#         return rVal;
//#     }
//#     
//#     public static int getColorInt(int color, int pos) {
//#         String ncolor = getColorString(color);
//# 
//#         switch (pos) {
//#             case 0:
//#                 return Integer.parseInt(ncolor.substring(2,4),16);
//#             case 1:
//#                 return Integer.parseInt(ncolor.substring(4,6),16);
//#             case 2:
//#                 return Integer.parseInt(ncolor.substring(6,8),16);
//#         }
//#         return -1;
//#     }
//#     
//#     public static String getColorString(int color) {
//#         StringBuffer ncolor=new StringBuffer();
//#         
//#         ncolor.append("0x");
//#         
//#         String col=Integer.toHexString(color);
//#         
//#         for (int i=0; i<6-col.length(); i++)
//#             ncolor.append("0");
//#         
//#         ncolor.append(col);
//# 
//#         return ncolor.toString();
//#     }
//#     
//#     public static int getColorInt(String color) { // 0x010000 -> 1
//#         return Integer.parseInt(color.substring(2),16);
//#     }
//#endif
}

