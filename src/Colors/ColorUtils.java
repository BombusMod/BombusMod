/*
 * ColorUtils.java
 *
 * Created on 27 Март 2008 г., 10:49
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package Colors;

//#ifdef COLORS
//# import Client.StaticData;
//# import io.file.FileIO;
//# import java.io.InputStream;
//# import io.NvStorage;
//# import java.io.DataInputStream;
//# import java.io.DataOutputStream;
//# import java.io.IOException;
//# import Colors.Colors;
//# import util.Translit;
//#endif

//#ifndef COLORS
import Client.Config;
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

    private static Colors cs=Colors.getInstance();
//#ifndef COLORS
    public static void loadScheme() {
        skinFile = Config.getInstance().schemeFileName();
        if (skinFile==null)
            return;

        loadSkin(skinFile, 1);
    }
//#endif
    
    public static void loadSkin(String skinF, int resourceT){
        skinFile=skinF;
        resourceType=resourceT;
        
        try {
            cs.BALLOON_INK=loadInt("BALLOON_INK", cs.BALLOON_INK);
            cs.BALLOON_BGND=loadInt("BALLOON_BGND", cs.BALLOON_BGND);
            cs.LIST_BGND=loadInt("LIST_BGND", cs.LIST_BGND);
            cs.LIST_BGND_EVEN=loadInt("LIST_BGND_EVEN", cs.LIST_BGND_EVEN);
            cs.LIST_INK=loadInt("LIST_INK", cs.LIST_INK);
            cs.MSG_SUBJ=loadInt("MSG_SUBJ", cs.MSG_SUBJ);
            cs.MSG_HIGHLIGHT=loadInt("MSG_HIGHLIGHT", cs.MSG_HIGHLIGHT);
            cs.DISCO_CMD=loadInt("DISCO_CMD", cs.DISCO_CMD);
            cs.BAR_BGND=loadInt("BAR_BGND", cs.BAR_BGND);
            cs.BAR_BGND_BOTTOM=loadInt("BAR_BGND_BOTTOM", cs.BAR_BGND_BOTTOM);
            cs.BAR_INK=loadInt("BAR_INK", cs.BAR_INK);
            cs.CONTACT_DEFAULT=loadInt("CONTACT_DEFAULT", cs.CONTACT_DEFAULT);
            cs.CONTACT_CHAT=loadInt("CONTACT_CHAT", cs.CONTACT_CHAT);
            cs.CONTACT_AWAY=loadInt("CONTACT_AWAY", cs.CONTACT_AWAY);
            cs.CONTACT_XA=loadInt("CONTACT_XA", cs.CONTACT_XA);
            cs.CONTACT_DND=loadInt("CONTACT_DND", cs.CONTACT_DND);
            cs.GROUP_INK=loadInt("GROUP_INK", cs.GROUP_INK);
            cs.BLK_INK=loadInt("BLK_INK", cs.BLK_INK);
            cs.BLK_BGND=loadInt("BLK_BGND", cs.BLK_BGND);
            cs.MESSAGE_IN=loadInt("MESSAGE_IN", cs.MESSAGE_IN);
            cs.MESSAGE_OUT=loadInt("MESSAGE_OUT", cs.MESSAGE_OUT);
            cs.MESSAGE_PRESENCE=loadInt("MESSAGE_PRESENCE", cs.MESSAGE_PRESENCE);
//#if NICK_COLORS
//#             cs.MESSAGE_IN_S=loadInt("MESSAGE_IN_S", cs.MESSAGE_IN_S);
//#             cs.MESSAGE_OUT_S=loadInt("MESSAGE_OUT_S", cs.MESSAGE_OUT_S);
//#             cs.MESSAGE_PRESENCE_S=loadInt("MESSAGE_PRESENCE_S", cs.MESSAGE_PRESENCE_S);
//#endif
            cs.MESSAGE_AUTH=loadInt("MESSAGE_AUTH", cs.MESSAGE_AUTH);
            cs.MESSAGE_HISTORY=loadInt("MESSAGE_HISTORY", cs.MESSAGE_HISTORY);
            cs.PGS_REMAINED=loadInt("PGS_REMAINED", cs.PGS_REMAINED);
            cs.PGS_COMPLETE=loadInt("PGS_COMPLETE", cs.PGS_COMPLETE);
            //cs.PGS_BORDER=loadInt("PGS_BORDER", cs.PGS_BORDER);
            //cs.PGS_BGND=loadInt("PGS_BGND", cs.PGS_BGND);
            cs.HEAP_TOTAL=loadInt("HEAP_TOTAL", cs.HEAP_TOTAL);
            cs.HEAP_FREE=loadInt("HEAP_FREE", cs.HEAP_FREE);
            cs.CURSOR_BGND=loadInt("CURSOR_BGND", cs.CURSOR_BGND);
            cs.CURSOR_OUTLINE=loadInt("CURSOR_OUTLINE", cs.CURSOR_OUTLINE);
            cs.SCROLL_BRD=loadInt("SCROLL_BRD", cs.SCROLL_BRD);
            cs.SCROLL_BAR=loadInt("SCROLL_BAR", cs.SCROLL_BAR);
            cs.SCROLL_BGND=loadInt("SCROLL_BGND", cs.SCROLL_BGND);
            cs.CONTACT_J2J=loadInt("CONTACT_J2J", cs.CONTACT_J2J);
//#ifdef COLORS
//#             ColorScheme.saveToStorage();
//#endif
        } catch (Exception e) { }
        skin=null;
        skinFile=null;
    }
    
    private static int loadInt(String key, int defaultColor) {
        if (skin==null) {
            switch (resourceType) {
//#if FILE_IO && COLORS
//#                 case 0:
//#                     FileIO f=FileIO.createConnection(skinFile);
//#                     byte[] b=f.fileRead();
//#                     if (b!=null) {
//#                         String str=new String(b, 0, b.length).toString().trim();
//#                         skin=new StringLoader().hashtableLoaderFromString(str);
//#                     } else
//#                     return defaultColor;
//#                     break;
//#endif
                case 1:
                    skin=new StringLoader().hashtableLoader(skinFile);
                    break;
//#if COLORS
//#                 case 2:
//#                     skin=new StringLoader().hashtableLoaderFromString(skinFile);
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
//# 
//# /*
//#      public static void saveSkin(String skinF) {
//#         skinFile=skinF;
//#         
//#         StringBuffer body=new StringBuffer();
//#         
//#         body.append("package midlet;\n//#ifdef COLORS\nimport ui.ColorScheme;\n//#endif\npublic class Colors {\n    private static Colors instance;\n    \n    public static Colors getInstance(){\n	if (instance==null) {\n	    instance=new Colors();\n//#ifdef COLORS\n	    ColorScheme.loadFromStorage();\n//#endif\n	}\n	return instance;\n    }\n");
//#         
//#         body.append("public static int BALLOON_INK="+loadString("BALLOON_INK")+";\n");
//#         body.append("public static int BALLOON_BGND="+loadString("BALLOON_BGND")+";\n");
//#         body.append("public static int LIST_BGND="+loadString("LIST_BGND")+";\n");
//#         body.append("public static int LIST_BGND_EVEN="+loadString("LIST_BGND_EVEN")+";\n");
//#         body.append("public static int LIST_INK="+loadString("LIST_INK")+";\n");
//#         body.append("public static int MSG_SUBJ="+loadString("MSG_SUBJ")+";\n");
//#         body.append("public static int MSG_HIGHLIGHT="+loadString("MSG_HIGHLIGHT")+";\n");
//#         body.append("public static int DISCO_CMD="+loadString("DISCO_CMD")+";\n");
//#         body.append("public static int BAR_BGND="+loadString("BAR_BGND")+";\n");
//#         body.append("public static int BAR_BGND_BOTTOM="+loadString("BAR_BGND_BOTTOM")+";\n");
//#         body.append("public static int BAR_INK="+loadString("BAR_INK")+";\n");
//#         body.append("public static int CONTACT_DEFAULT="+loadString("CONTACT_DEFAULT")+";\n");
//#         body.append("public static int CONTACT_CHAT="+loadString("CONTACT_CHAT")+";\n");
//#         body.append("public static int CONTACT_AWAY="+loadString("CONTACT_AWAY")+";\n");
//#         body.append("public static int CONTACT_XA="+loadString("CONTACT_XA")+";\n");
//#         body.append("public static int CONTACT_DND="+loadString("CONTACT_DND")+";\n");
//#         body.append("public static int GROUP_INK="+loadString("GROUP_INK")+";\n");
//#         body.append("public static int BLK_INK="+loadString("BLK_INK")+";\n");
//#         body.append("public static int BLK_BGND="+loadString("BLK_BGND")+";\n");
//#         body.append("public static int MESSAGE_IN="+loadString("MESSAGE_IN")+";\n");
//#         body.append("public static int MESSAGE_OUT="+loadString("MESSAGE_OUT")+";\n");
//#         body.append("public static int MESSAGE_PRESENCE="+loadString("MESSAGE_PRESENCE")+";\n");
//#         body.append("public static int MESSAGE_AUTH="+loadString("MESSAGE_AUTH")+";\n");
//#         body.append("public static int MESSAGE_HISTORY="+loadString("MESSAGE_HISTORY")+";\n");
//#         body.append("public static int PGS_REMAINED="+loadString("PGS_REMAINED")+";\n");
//#         body.append("public static int PGS_COMPLETE="+loadString("PGS_COMPLETE")+";\n");
//#         body.append("public static int HEAP_TOTAL="+loadString("HEAP_TOTAL")+";\n");
//#         body.append("public static int HEAP_FREE="+loadString("HEAP_FREE")+";\n");
//#         body.append("public static int CURSOR_BGND="+loadString("CURSOR_BGND")+";\n");
//#         body.append("public static int CURSOR_OUTLINE="+loadString("CURSOR_OUTLINE")+";\n");
//#         body.append("public static int SCROLL_BRD="+loadString("SCROLL_BRD")+";\n");
//#         body.append("public static int SCROLL_BAR="+loadString("SCROLL_BAR")+";\n");
//#         body.append("public static int SCROLL_BGND="+loadString("SCROLL_BGND")+";\n");
//#         body.append("public static int CONTACT_J2J="+loadString("CONTACT_J2J")+";\n");
//#         
//#         body.append("}\n");
//#         
//#         System.out.println(body.toString());
//# 
//#         skin=null;
//#         skinFile=null;
//#     }
//#      */
//#     private static String loadString(String key) {
//#         if (skin==null) {
//#             skin=new StringLoader().hashtableLoader(skinFile);
//#         }
//#         return (String)skin.get(key);
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
//#         body.append("BALLOON_INK\t"+getColorString(cs.BALLOON_INK)+"\r\n");
//#         body.append("BALLOON_BGND\t"+getColorString(cs.BALLOON_BGND)+"\r\n");
//#         body.append("LIST_BGND\t"+getColorString(cs.LIST_BGND)+"\r\n");
//#         body.append("LIST_BGND_EVEN\t"+getColorString(cs.LIST_BGND_EVEN)+"\r\n");
//#         body.append("LIST_INK\t"+getColorString(cs.LIST_INK)+"\r\n");
//#         body.append("MSG_SUBJ\t"+getColorString(cs.MSG_SUBJ)+"\r\n");
//#         body.append("MSG_HIGHLIGHT\t"+getColorString(cs.MSG_HIGHLIGHT)+"\r\n");
//#         body.append("DISCO_CMD\t"+getColorString(cs.DISCO_CMD)+"\r\n");
//#         body.append("BAR_BGND\t"+getColorString(cs.BAR_BGND)+"\r\n");
//#         body.append("BAR_BGND_BOTTOM\t"+getColorString(cs.BAR_BGND_BOTTOM)+"\r\n");
//#         body.append("BAR_INK\t"+getColorString(cs.BAR_INK)+"\r\n");
//#         body.append("CONTACT_DEFAULT\t"+getColorString(cs.CONTACT_DEFAULT)+"\r\n");
//#         body.append("CONTACT_CHAT\t"+getColorString(cs.CONTACT_CHAT)+"\r\n");
//#         body.append("CONTACT_AWAY\t"+getColorString(cs.CONTACT_AWAY)+"\r\n");
//#         body.append("CONTACT_XA\t"+getColorString(cs.CONTACT_XA)+"\r\n");
//#         body.append("CONTACT_DND\t"+getColorString(cs.CONTACT_DND)+"\r\n");
//#         body.append("GROUP_INK\t"+getColorString(cs.GROUP_INK)+"\r\n");
//#         body.append("BLK_INK\t"+getColorString(cs.BLK_INK)+"\r\n");
//#         body.append("BLK_BGND\t"+getColorString(cs.BLK_BGND)+"\r\n");
//#         body.append("MESSAGE_IN\t"+getColorString(cs.MESSAGE_IN)+"\r\n");
//#         body.append("MESSAGE_OUT\t"+getColorString(cs.MESSAGE_OUT)+"\r\n");
//#         body.append("MESSAGE_PRESENCE\t"+getColorString(cs.MESSAGE_PRESENCE)+"\r\n");
//#if NICK_COLORS
//#         body.append("MESSAGE_IN_S\t"+getColorString(cs.MESSAGE_IN_S)+"\r\n");
//#         body.append("MESSAGE_OUT_S\t"+getColorString(cs.MESSAGE_OUT_S)+"\r\n");
//#         body.append("MESSAGE_PRESENCE_S\t"+getColorString(cs.MESSAGE_PRESENCE_S)+"\r\n");
//#endif
//#         body.append("MESSAGE_AUTH\t"+getColorString(cs.MESSAGE_AUTH)+"\r\n");
//#         body.append("MESSAGE_HISTORY\t"+getColorString(cs.MESSAGE_HISTORY)+"\r\n");
//#         body.append("PGS_REMAINED\t"+getColorString(cs.PGS_REMAINED)+"\r\n");
//#         body.append("PGS_COMPLETE\t"+getColorString(cs.PGS_COMPLETE)+"\r\n");
//#         //body.append("PGS_BORDER\t"+getColorString(cs.PGS_BORDER)+"\r\n");
//#         //body.append("PGS_BGND\t"+getColorString(cs.PGS_BGND)+"\r\n");
//#         body.append("HEAP_TOTAL\t"+getColorString(cs.HEAP_TOTAL)+"\r\n");
//#         body.append("HEAP_FREE\t"+getColorString(cs.HEAP_FREE)+"\r\n");
//#         body.append("CURSOR_BGND\t"+getColorString(cs.CURSOR_BGND)+"\r\n");
//#         body.append("CURSOR_OUTLINE\t"+getColorString(cs.CURSOR_OUTLINE)+"\r\n");
//#         body.append("SCROLL_BRD\t"+getColorString(cs.SCROLL_BRD)+"\r\n");
//#         body.append("SCROLL_BAR\t"+getColorString(cs.SCROLL_BAR)+"\r\n");
//#         body.append("SCROLL_BGND\t"+getColorString(cs.SCROLL_BGND)+"\r\n");
//#         body.append("CONTACT_J2J\t"+getColorString(cs.CONTACT_J2J)+"\r\n");
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
//#endif
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
    
    public static int getColorInt(String color) { // 0x010000 -> 1
        return Integer.parseInt(color.substring(2),16);
    }
}
