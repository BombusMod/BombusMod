/*
 * StringUtils.java
 *
 * Created on 28 ��� 2008 �., 13:27
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package util;

import java.util.Vector;
import javax.microedition.lcdui.Font;
import ui.Time;

/**
 *
 * @author ad
 */
public class StringUtils {
    
    /** Creates a new instance of StringUtils */
    public StringUtils() { }
    
    private final static String[] badChars= { "?", "\\", "/", "*", ".", "\"", ":", "%", "@", "|", "<", ">", "COM", "LPT", "NULL", "PRINT"};
    
    public static String stringReplace(String aSearch, String aFind, String aReplace) {
    	int pos = aSearch.indexOf(aFind);
    	if (pos != -1) {
            StringBuffer buffer = new StringBuffer();
            int lastPos = 0;
             while (pos != -1) {
                    buffer.append(aSearch.substring(lastPos, pos)).append(aReplace);
                    lastPos = pos + aFind.length();
                    pos = aSearch.indexOf(aFind, lastPos);
            }
            buffer.append( aSearch.substring(lastPos));
            aSearch = buffer.toString();
    	}
    	return aSearch;
    }

    public static String getSizeString(long number) {
        StringBuffer suffix = new StringBuffer();
        
        try {
            if ( number > 1024000 ) {
                String ratio=Long.toString(number/100000);

                int dotpos=ratio.length()-1;

                suffix.append( (dotpos==0)? "0":ratio.substring(0, dotpos))
                      .append('.')
                      .append(ratio.substring(dotpos))
                      
                      .append("mb");
            } else if ( number > 1024 ) {
                String ratio=Long.toString(number/100);

                int dotpos=ratio.length()-1;

                suffix.append( (dotpos==0)? "0":ratio.substring(0, dotpos))
                      .append('.')
                      .append(ratio.substring(dotpos))
                      
                      .append("kb");
            } else {
                suffix.append(number)
                      .append("b");
            }
        } catch (Exception e) { }
        
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
            
            out.append('%').append(hexByteToString((byte)s));
        }
        
        return out.toString();
    }
    
    //private static String wrapSeparators=" .,-=/\\;:+*()[]<>~!@#%^_&";
    
    
    public static Vector parseMessage(String value, int availWidth, Font font) {
        StringBuffer out=new StringBuffer(value);
        int vi = 0;
        while (vi<out.length()) {
            if (out.charAt(vi)<0x03) out.deleteCharAt(vi);
            else vi++;
        }
        value=out.toString();
        
        Vector lines=new Vector();
        char[] valueChars = value.toCharArray();
        int startPos = 0;
        int lastSpacePos = -1;
        int lastSpacePosLength = 0;
        int currentLineWidth = 0;
        for (int i = 0; i < valueChars.length; i++) {
            char c = valueChars[i];
            currentLineWidth += font.charWidth( c );
            if (c == '\n') {
                lines.addElement( new String( valueChars, startPos, i - startPos ) );
                lastSpacePos = -1;
                startPos = i+1;
                currentLineWidth = 0;
                i = startPos;
            } else if (currentLineWidth >= availWidth && i > 0) {
                if ( lastSpacePos == -1 ) {
                    i--;
                    lines.addElement( new String( valueChars, startPos, i - startPos ) );
                    startPos =  i;
                    currentLineWidth = 0;
                } else {
                    currentLineWidth -= lastSpacePosLength;
                    lines.addElement( new String( valueChars, startPos, lastSpacePos - startPos ) );
                    startPos =  lastSpacePos + 1;
                    lastSpacePos = -1;
                }
            } else if (c == ' ' || c == '\t') {
                lastSpacePos = i;
                lastSpacePosLength = currentLineWidth;
            }
        } 
        // last string
        lines.addElement( new String( valueChars, startPos, valueChars.length - startPos ) );

        return lines;
    }
/*
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
                    boolean newline= ( c==0x0d || c==0x0a  );
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
                        if (availHeight>-1)
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
*/    

    public static String toExtendedString(String src){
        src=stringReplace(src,"%dt",Time.dispLocalTime());
        src=stringReplace(src,"%t",Time.localTime());
        return src;
    }
    
    
    public static String hexByteToString(byte b){
        StringBuffer out=new StringBuffer();
        char c = (char) ((b >> 4) & 0xf);
        if (c > 9)   c = (char) ((c - 10) + 'a');
        else  c = (char) (c + '0');
        out.append(c);
        c = (char) (b & 0xf);
        if (c > 9)
            c = (char)((c-10) + 'a');
        else
            c = (char)(c + '0');
        out.append(c);
        
        return out.toString();
    }
}
