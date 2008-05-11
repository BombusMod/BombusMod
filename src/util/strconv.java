/*
 * strconv.java
 *
 * Created on 12.01.2005, 1:25
 *
 * Copyright (c) 2005-2008, Eugene Stahov (evgs), http://bombus-im.org
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

/**
 *
 * @author Eugene Stahov
 */
package util;
import Info.Version;
import java.io.ByteArrayOutputStream;
import java.util.Vector;
import javax.microedition.lcdui.Font;
//import java.lang.*;
import ui.Time;

public class strconv {
    
    /** Creates a new instance of strconv */
    private strconv() {
    }
    
    private final static String BOMBUSMOD_REP=Version.getUrl()+"/caps";
    private final static String BOMBUS_NG_REP="http://bombus-im.org/ng";
    private final static String PSI_REP="http://psi-im.org/caps";
    private final static String GOOGLE_REP="http://www.google.com/xmpp/client/caps";
    private final static String MIRANDA_REP="http://miranda-im.org/caps";
    private final static String GAJIM_REP="http://gajim.org/caps";
    private final static String GAIM_REP="http://gaim.sf.net/caps";
    private final static String BOMBUS_REP="http://bombus-im.org/java";
    private final static String KOPETE_REP="http://kopete.kde.org/jabber/caps";
    private final static String PIDGIN_REP="http://pidgin.im/caps";
    private final static String QIP_REP="http://qip.ru/caps";
    private final static String TKABBER_REP="http://tkabber.jabber.ru/";
    
    private final static String[] badChars= { "?", "\\", "/", "*", ".", "\"", ":", "%", "@", "|", "<", ">", "COM", "LPT", "NULL", "PRINT"};
   
    public static final String convCp1251ToUnicode(final String s){
        if (s==null) return null;
        StringBuffer b=new StringBuffer(s.length());
        for (int i=0;i<s.length();i++){
            char ch=s.charAt(i);
            if (ch>0xbf) ch+=0x410-0xc0;
            if (ch==0xa8) ch=0x401;
            if (ch==0xb8) ch=0x451;
            b.append(ch);
            //setCharAt(i, ch);
        }
        return b.toString();
    }
    
    public static final String convUnicodeToCp1251(final String s){
        if (s==null) return null;
        StringBuffer b=new StringBuffer(s.length());
        for (int i=0;i<s.length();i++){
            char ch=s.charAt(i);
            if (ch==0x401) ch=0xa8; //Ё
            if (ch==0x451) ch=0xb8; //ё
            if (ch>0x409) ch+=0xc0-0x410;
            b.append(ch);
            //setCharAt(i, ch);
        }
        return b.toString();
    }
    
    public final static String toBase64( String source) {
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
        
        int len=source.length();
        char[] out = new char[((len+2)/3)*4];
        for (int i=0, index=0; i<len; i+=3, index +=4) {
            boolean trip=false;
            boolean quad=false;
            
            int val = (0xFF & source.charAt(i))<<8;
            if ((i+1) < len) {
                val |= (0xFF & source.charAt(i+1));
                trip = true;
            }
            val <<= 8;
            if ((i+2) < len) {
                val |= (0xFF & source.charAt(i+2));
                quad = true;
            }
            out[index+3] = alphabet.charAt((quad? (val & 0x3F): 64));
            val >>= 6;
            out[index+2] = alphabet.charAt((trip? (val & 0x3F): 64));
            val >>= 6;
            out[index+1] = alphabet.charAt(val & 0x3F);
            val >>= 6;
            out[index+0] = alphabet.charAt(val & 0x3F);
        }
        return new String(out);
    }
    
    public final static String toBase64( byte source[], int len) {
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
        
        if (len<0) len=source.length;
        char[] out = new char[((len+2)/3)*4];
        for (int i=0, index=0; i<len; i+=3, index +=4) {
            boolean trip=false;
            boolean quad=false;
            
            int val = (0xFF & source[i])<<8;
            if ((i+1) < len) {
                val |= (0xFF & source[i+1]);
                trip = true;
            }
            val <<= 8;
            if ((i+2) < len) {
                val |= (0xFF & source[i+2]);
                quad = true;
            }
            out[index+3] = alphabet.charAt((quad? (val & 0x3F): 64));
            val >>= 6;
            out[index+2] = alphabet.charAt((trip? (val & 0x3F): 64));
            val >>= 6;
            out[index+1] = alphabet.charAt(val & 0x3F);
            val >>= 6;
            out[index+0] = alphabet.charAt(val & 0x3F);
        }
        return new String(out);
    }
    
    public static StringBuffer toUTFSb(StringBuffer str) {
        int srcLen = str.length();
        StringBuffer outbuf=new StringBuffer( srcLen );
         for(int i=0; i < srcLen; i++) {
            int c = (int)str.charAt(i);

            if ((c >= 0x20) && (c <= 0x7f)) {
                outbuf.append( (char) c);
                continue;
            } 
            if (((c >= 0x80) && (c <= 0x7ff)) || (c==0)) {
                outbuf.append((char)(0xc0 | (0x1f & (c >> 6))));
                outbuf.append((char)(0x80 | (0x3f & c)));
                continue;
            } 
            if ((c >= 0x800) && (c <= 0xffff)) {
                outbuf.append(((char)(0xe0 | (0x0f & (c >> 12)))));
                outbuf.append((char)(0x80 | (0x3f & (c >>  6))));
                outbuf.append(((char)(0x80 | (0x3f & c))));
                continue;
            } 
            if ((c==0x0d) || (c==0x0a) || (c==0x09)) {
                outbuf.append( (char) c);
                continue;
            }
            if ((c > 0)  && (c < 0x1f)) {
                outbuf.append("&#");
                outbuf.append( (int) c);
                outbuf.append(";");
            }
         }
         return outbuf;
     }
    
    public static byte[] fromBase64(String s) {
        int padding=0;
        int ibuf=1;
        ByteArrayOutputStream baos=new ByteArrayOutputStream(2048);
        for (int i=0; i<s.length(); i++) {
            int nextChar = s.charAt(i);
            //if( nextChar == -1 )
            //    throw new EndOfXMLException();
            int base64=-1;
            if (nextChar>'A'-1 && nextChar<'Z'+1) base64=nextChar-'A';
            else if (nextChar>'a'-1 && nextChar<'z'+1) base64=nextChar+26-'a';
            else if (nextChar>'0'-1 && nextChar<'9'+1) base64=nextChar+52-'0';
            else if (nextChar=='+') base64=62;
            else if (nextChar=='/') base64=63;
            else if (nextChar=='=') {base64=0; padding++;} else if (nextChar=='<') break;
            if (base64>=0) ibuf=(ibuf<<6)+base64;
            if (ibuf>=0x01000000){
                baos.write((ibuf>>16) &0xff);                   //00xx0000 0,1,2 =
                if (padding<2) baos.write((ibuf>>8) &0xff);     //0000xx00 0,1 =
                if (padding==0) baos.write(ibuf &0xff);         //000000xx 0 =
                //len+=3;
                ibuf=1;
            }
        }
        try { baos.close(); } catch (Exception e) {}
        //System.out.println(ibuf);
        //System.out.println(baos.size());
        return baos.toByteArray();
    }
    
    public static String unicodeToUTF(String src) {
        return toUTFSb(new StringBuffer(src)).toString();
    }
    
    public static String toLowerCase(String src){
        StringBuffer dst=new StringBuffer(src);
        int len=dst.length();
        for (int i=0; i<len; i++) {
            char c=dst.charAt(i);
            if (c>'A'-1 && c<'Z'+1) c+='a'-'A';         // default latin chars
            if (c>0x40f && c<0x430) c+=0x430-0x410;     // cyrillic chars
            // TODO: other schemes by request
            dst.setCharAt(i, c);
        }
        return dst.toString();
    }

    public static String toExtendedString(String src){
        src=stringReplace(src,"%dt",Time.dispLocalTime());
        src=stringReplace(src,"%t",Time.localTime());
        return src;
    }
    
    public static String replaceCaps(String src){
        if (src==null)
            return "Unknown";

        if (src.indexOf(BOMBUSMOD_REP)>-1)
            return stringReplace(src,BOMBUSMOD_REP,"BombusMod");
        else if (src.indexOf(BOMBUS_REP)>-1)
            return stringReplace(src,BOMBUS_REP,"Bombus");  
        else if (src.indexOf(BOMBUS_NG_REP)>-1)
            return stringReplace(src,BOMBUS_NG_REP,"Bombus-NG");
        else if (src.indexOf(PSI_REP)>-1)
            return stringReplace(src,PSI_REP,"Psi");
        else if (src.indexOf(MIRANDA_REP)>-1)
            return stringReplace(src,MIRANDA_REP,"Miranda");
        else if (src.indexOf(GAJIM_REP)>-1)
            return stringReplace(src,GAJIM_REP,"Gajim");
        else if (src.indexOf(GOOGLE_REP)>-1)
            return stringReplace(src,GOOGLE_REP,"Google");
        else if (src.indexOf(GAIM_REP)>-1)
            return stringReplace(src,GAIM_REP,"Gaim");
	else if (src.indexOf(KOPETE_REP)>-1)
            return stringReplace(src,KOPETE_REP,"Kopete");  
        else if (src.indexOf(PIDGIN_REP)>-1)
            return stringReplace(src,PIDGIN_REP,"Pidgin");  
        else if (src.indexOf(QIP_REP)>-1)
            return stringReplace(src,QIP_REP,"Qip");  
        else if (src.indexOf(TKABBER_REP)>-1)
            return stringReplace(src,TKABBER_REP,"Tkabber"); 

        return src;
    }
 
    public static String stringReplace(String aSearch, String aFind, String aReplace) {
        String result = aSearch;
        if (result != null && result.length() > 0) {
            int a = 0;
            int b = 0;
            while (true) {
                a = result.indexOf(aFind, b);
                if (a != -1) {
                    result = result.substring(0, a) + aReplace + result.substring(a + aFind.length());
                    b = a + aReplace.length();
                } else
                    break;
            }
        }
        return result;
    }

    public static String getSizeString(long number) {
        StringBuffer suffix = new StringBuffer();
        
        try {
            if ( number > 1024000 )
            {
                String ratio=Long.toString(number/100000);

                int dotpos=ratio.length()-1;

                suffix.append( (dotpos==0)? "0":ratio.substring(0, dotpos));
                suffix.append('.');
                suffix.append(ratio.substring(dotpos));

                suffix.append("mb");
            }
            else if ( number > 1024 )
            {
                String ratio=Long.toString(number/100);

                int dotpos=ratio.length()-1;

                suffix.append( (dotpos==0)? "0":ratio.substring(0, dotpos));
                suffix.append('.');
                suffix.append(ratio.substring(dotpos));

                suffix.append("kb");
            }
            else
            {
                suffix.append(number);
                suffix.append("b");
            }
        } catch (Exception e) {
            suffix.append("error");
        }
        
        return suffix.toString();
    }
    
    public static String replaceBadChars (String src) {
        for (int i=0; i<badChars.length;i++) {
            src=stringReplace(src,badChars[i],"_");
        }
        return src;
    }
    
    public static String urlPrep(String src){
        String mask=" #$%&/:;<=>?@[\\]^'{|}";
        StringBuffer out=new StringBuffer();
        
        for (int i=0; i<src.length(); i++) {
            char s=src.charAt(i);
            
            if (mask.indexOf(s)<0) {  out.append(s); continue;  }
            
            out.append('%');
            
            char c = (char) ((s >> 4) & 0xf);
            if (c > 9)   c = (char) ((c - 10) + 'a');
            else  c = (char) (c + '0');
            out.append(c);
            
            c = (char) (s & 0xf);
            if (c > 9)
                c = (char)((c-10) + 'a');
            else
                c = (char)(c + '0');
            out.append(c);
            
        }
        
        return out.toString();
    }
    
    private static String wrapSeparators=" .,-=/\\;:+*()[]<>~!@#%^_&";
    
    public static Vector parseMessage(String str, int availWidth, int availHeight, boolean wordsWrap, Font font) {
        Vector lines=new Vector();
        int state=0;
        String txt=str;
        int fontHeight=font.getHeight()+1;
        
        while (state<1) {
            int w=0;
            StringBuffer s=new StringBuffer();
	    int wordWidth=0;
	    int wordStartPos=0;

            if (txt==null) {
                state++;
                continue;
            }
            
            int pos=0;
            while (pos<txt.length()) {
                char c=txt.charAt(pos);

                int cw=font.charWidth(c);
                if (c!=0x20) {
                    boolean newline= ( c==0x0d || c==0x0a /*|| c==0xa0*/ );
                    if (wordWidth+cw>availWidth || newline) {
                        s.append(txt.substring(wordStartPos,pos));
                        w+=wordWidth;
                        wordWidth=0;
                        wordStartPos=pos;
                        if (newline) 
                            wordStartPos++;
                    }
                    if (w+wordWidth+cw>availWidth || newline) {
                        lines.addElement(s.toString()); //lastest string in l
                        s.setLength(0); w=0;
                        if (fontHeight*lines.size()>availHeight){
                            return lines; //stop when linesHeight>height
                        }
                    }
                }
                if (c==0x09) 
                    c=0x20;

                if (c>0x1f) 
                    wordWidth+=cw;

                if (wrapSeparators.indexOf(c)>=0 || !wordsWrap) {
                    if (pos>wordStartPos) 
                        s.append(txt.substring(wordStartPos,pos));
                    if (c>0x1f) s.append(c);
                    w+=wordWidth;
                    wordStartPos=pos+1;
                    wordWidth=0;
                }
                
                pos++;
            }
	    if (wordStartPos!=pos)
		s.append(txt.substring(wordStartPos,pos));
            if (s.length()>0) {
                lines.addElement(s.toString());
            }
            
            if (lines.isEmpty()) 
                lines.removeElementAt(lines.size()-1);  //lastest string
            
            state++;
            s=null;
        }
        return lines;
    }
}
