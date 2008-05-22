/*
 * ColorUtils.java
 *
 * Created on 27.03.2008, 10:49
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

//#ifdef COLORS
import Client.StaticData;
import io.file.FileIO;
import java.io.InputStream;
import io.NvStorage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import Colors.ColorTheme;
import util.Translit;
//#endif

//#ifndef COLORS
//# import Client.Config;
//# import java.util.Enumeration;
//#endif

import util.StringLoader;
import java.util.Hashtable;
/**
 *
 * @author ad
 */
public class ColorUtils {
    
    /** Creates a new instance of ColorUtils */
    public ColorUtils() { }
    
    private static Hashtable skin;
    private static String skinFile;
    private static int resourceType=1;

    private static ColorTheme ct=ColorTheme.getInstance();
//#ifndef COLORS
//#     public static void loadScheme() {
//#         skinFile = Config.getInstance().schemeFileName();
//#         if (skinFile==null)
//#             return;
//# 
//#         loadSkin(skinFile, 1);
//#     }
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
    
    public static void loadSkin(String skinF, int resourceT){
        skinFile=skinF;
        resourceType=resourceT;
        
        try {
            for (Enumeration r=ct.colorsContainer.elements(); r.hasMoreElements();) {
                ColorItem c=(ColorItem)r.nextElement();
                c.setColor(loadInt(c.getName(), c.getColor()));
            }
//#ifdef COLORS
            ColorScheme.saveToStorage();
//#endif
        } catch (Exception e) { }
        skin=null;
        skinFile=null;
    }
    
    private static int loadInt(String key, int defaultColor) {
        if (skin==null) {
            switch (resourceType) {
//#if FILE_IO && COLORS
                case 0:
                    FileIO f=FileIO.createConnection(skinFile);
                    byte[] b=f.fileRead();
                    if (b!=null) {
                        String str=new String(b, 0, b.length).toString().trim();
                        skin=new StringLoader().hashtableLoaderFromString(str);
                    } else
                    return defaultColor;
                    break;
//#endif
                case 1:
                    skin=new StringLoader().hashtableLoader(skinFile);
                    break;
//#if COLORS
                case 2:
                    skin=new StringLoader().hashtableLoaderFromString(skinFile);
//#endif
            }
        }
        try {
            String value=(String)skin.get(key);
            return getColorInt(value);
        } catch (Exception e) {
            //StaticData.getInstance().roster.errorLog(e.toString());
            return defaultColor;
        }
    }
    
    
//#ifdef COLORS
    private static String loadString(String key) {
        if (skin==null) {
            skin=new StringLoader().hashtableLoader(skinFile);
        }
        return (String)skin.get(key);
    }
    
    public static String getSkin(){
        StringBuffer body=new StringBuffer();
//#ifdef TRANSLIT
        body.append("xmlSkin\t"+Translit.translit(StaticData.getInstance().account.getNickName()));
//#else
//#         body.append("xmlSkin\t"+StaticData.getInstance().account.getNickName());
//#endif
        body.append("\r\n");
        body.append("BALLOON_INK\t"+getColorString(cs.BALLOON_INK)+"\r\n");
        body.append("BALLOON_BGND\t"+getColorString(cs.BALLOON_BGND)+"\r\n");
        body.append("LIST_BGND\t"+getColorString(cs.LIST_BGND)+"\r\n");
        body.append("LIST_BGND_EVEN\t"+getColorString(cs.LIST_BGND_EVEN)+"\r\n");
        body.append("LIST_INK\t"+getColorString(cs.LIST_INK)+"\r\n");
        body.append("MSG_SUBJ\t"+getColorString(cs.MSG_SUBJ)+"\r\n");
        body.append("MSG_HIGHLIGHT\t"+getColorString(cs.MSG_HIGHLIGHT)+"\r\n");
        body.append("DISCO_CMD\t"+getColorString(cs.DISCO_CMD)+"\r\n");
        body.append("BAR_BGND\t"+getColorString(cs.BAR_BGND)+"\r\n");
        body.append("BAR_BGND_BOTTOM\t"+getColorString(cs.BAR_BGND_BOTTOM)+"\r\n");
        body.append("BAR_INK\t"+getColorString(cs.BAR_INK)+"\r\n");
        body.append("CONTACT_DEFAULT\t"+getColorString(cs.CONTACT_DEFAULT)+"\r\n");
        body.append("CONTACT_CHAT\t"+getColorString(cs.CONTACT_CHAT)+"\r\n");
        body.append("CONTACT_AWAY\t"+getColorString(cs.CONTACT_AWAY)+"\r\n");
        body.append("CONTACT_XA\t"+getColorString(cs.CONTACT_XA)+"\r\n");
        body.append("CONTACT_DND\t"+getColorString(cs.CONTACT_DND)+"\r\n");
        body.append("GROUP_INK\t"+getColorString(cs.GROUP_INK)+"\r\n");
        body.append("BLK_INK\t"+getColorString(cs.BLK_INK)+"\r\n");
        body.append("BLK_BGND\t"+getColorString(cs.BLK_BGND)+"\r\n");
        body.append("MESSAGE_IN\t"+getColorString(cs.MESSAGE_IN)+"\r\n");
        body.append("MESSAGE_OUT\t"+getColorString(cs.MESSAGE_OUT)+"\r\n");
        body.append("MESSAGE_PRESENCE\t"+getColorString(cs.MESSAGE_PRESENCE)+"\r\n");
        body.append("MESSAGE_IN_S\t"+getColorString(cs.MESSAGE_IN_S)+"\r\n");
        body.append("MESSAGE_OUT_S\t"+getColorString(cs.MESSAGE_OUT_S)+"\r\n");
        body.append("MESSAGE_PRESENCE_S\t"+getColorString(cs.MESSAGE_PRESENCE_S)+"\r\n");
        body.append("MESSAGE_AUTH\t"+getColorString(cs.MESSAGE_AUTH)+"\r\n");
        body.append("MESSAGE_HISTORY\t"+getColorString(cs.MESSAGE_HISTORY)+"\r\n");
        body.append("PGS_REMAINED\t"+getColorString(cs.PGS_REMAINED)+"\r\n");
        body.append("PGS_COMPLETE_TOP\t"+getColorString(cs.PGS_COMPLETE_TOP)+"\r\n");
        body.append("PGS_COMPLETE_BOTTOM\t"+getColorString(cs.PGS_COMPLETE_BOTTOM)+"\r\n");
        body.append("PGS_INK\t"+getColorString(cs.PGS_INK)+"\r\n");
        body.append("HEAP_TOTAL\t"+getColorString(cs.HEAP_TOTAL)+"\r\n");
        body.append("HEAP_FREE\t"+getColorString(cs.HEAP_FREE)+"\r\n");
        body.append("CURSOR_BGND\t"+getColorString(cs.CURSOR_BGND)+"\r\n");
        body.append("CURSOR_OUTLINE\t"+getColorString(cs.CURSOR_OUTLINE)+"\r\n");
        body.append("SCROLL_BRD\t"+getColorString(cs.SCROLL_BRD)+"\r\n");
        body.append("SCROLL_BAR\t"+getColorString(cs.SCROLL_BAR)+"\r\n");
        body.append("SCROLL_BGND\t"+getColorString(cs.SCROLL_BGND)+"\r\n");
        body.append("CONTACT_J2J\t"+getColorString(cs.CONTACT_J2J)+"\r\n");
        body.append("POPUP_MESSAGE_INK\t"+getColorString(cs.POPUP_MESSAGE_INK)+"\r\n");
        body.append("POPUP_MESSAGE_BGND\t"+getColorString(cs.POPUP_MESSAGE_BGND)+"\r\n");
        body.append("POPUP_SYSTEM_INK\t"+getColorString(cs.POPUP_SYSTEM_INK)+"\r\n");
        body.append("POPUP_SYSTEM_BGND\t"+getColorString(cs.POPUP_SYSTEM_BGND)+"\r\n");
        body.append("SECOND_LINE\t"+getColorString(cs.SECOND_LINE)+"\r\n");
        return body.toString();
    }
//#endif
    
    public static String ColorToString(int cRed, int cGreen, int cBlue) {
        StringBuffer color=new StringBuffer(8);
        
        color.append("0x");
        color.append(expandHex(cRed));
        color.append(expandHex(cGreen));
        color.append(expandHex(cBlue));
        
        return color.toString();
    }
    
    public static String expandHex(int eVal) {
        String rVal=Integer.toHexString(eVal);
        if (rVal.length()==1) rVal="0"+rVal;
      
        return rVal;
    }
    
    public static int getColorInt(int color, int pos) {
        String ncolor = getColorString(color);

        switch (pos) {
            case 0:
                return Integer.parseInt(ncolor.substring(2,4),16);
            case 1:
                return Integer.parseInt(ncolor.substring(4,6),16);
            case 2:
                return Integer.parseInt(ncolor.substring(6,8),16);
        }
        return -1;
    }
    
    public static String getColorString(int color) {
        StringBuffer ncolor=new StringBuffer();
        
        ncolor.append("0x");
        
        String col=Integer.toHexString(color);
        
        for (int i=0; i<6-col.length(); i++)
            ncolor.append("0");
        
        ncolor.append(col);

        return ncolor.toString();
    }
    
    public static int getColorInt(String color) {
        return Integer.parseInt(color.substring(2),16);
    }
    
    public static int invertColor(int color){
        return 0xFFFFFF-color;
    }
        
    public static int getRed(int color) {
        return ((color >> 16) & 0xFF);
    }
    public static int getGreen(int color) {
        return ((color >> 8) & 0xFF);
    }
    public static int getBlue(int color) {
        return (color& 0xFF);
    }
}
