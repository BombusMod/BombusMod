/*
 * StringUtils.java
 *
 * Created on 28 ��� 2008 �., 13:27
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package util;
//#ifndef WMUC
import Conference.ConferenceGroup;
import Conference.MucContact;
//#endif
import Client.Msg;
import com.alsutton.jabber.datablocks.Presence;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Vector;
import javax.microedition.lcdui.Font;
import ui.Time;
import xmpp.XmppError;

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
                      
                      .append(" mb");
            } else if ( number > 1024 ) {
                String ratio=Long.toString(number/100);

                int dotpos=ratio.length()-1;

                suffix.append( (dotpos==0)? "0":ratio.substring(0, dotpos))
                      .append('.')
                      .append(ratio.substring(dotpos))
                      
                      .append(" kb");
            } else {
                suffix.append(number)
                      .append(" b");
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

    public static String escapeTags(String src) {
        if (src == null)
            return null;
        src = stringReplace(src, "&", "&amp;");
        src = stringReplace(src, "<", "&lt;");
        src = stringReplace(src, ">", "&gt;");
        return src;
    }
    
    public static String unescapeTags(String src) {
        if (src == null)
            return null;
        src = stringReplace(src, "&gt;", ">");
        src = stringReplace(src, "&lt;", "<");
        src = stringReplace(src, "&amp;", "&");
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

    public static Vector parseMessage(String value, int availWidth, Font font) {
        StringBuffer out=new StringBuffer(value);
        
        value = Msg.clearNick(out);
        
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
    public static String getDigestHex(byte[] digestBits) {
        StringBuffer out = new StringBuffer();
        for (int i = 0; i < digestBits.length; i++) {
            out.append(StringUtils.hexByteToString(digestBits[i]));
        }
        return out.toString();
    }
    // Puts the specified word (val) into the buffer (buf) at position off using big endian byte ordering
    public static void putWordBE(byte[] buf, int off, int val) {
        buf[off]   = (byte) ((val >> 8) & 0x000000FF);
        buf[++off] = (byte) ((val)      & 0x000000FF);
    }   
    
    public static int getWordBE(byte[] buf, int off) {
        int val = (((int) buf[off]) << 8) & 0x0000FF00;
        return val | (((int) buf[++off])) & 0x000000FF;
    }
    
    /* Divide text to array of parts using separator character */
    static public String[] explode(String text, char separator) {
        if (text.equals("")) {
            return new String[0];
        }
        Vector tmp = new Vector();
        int start = 0;
        int end = text.indexOf(separator, start);
        while (end >= start) {
            tmp.addElement(text.substring(start, end));
            start = end + 1;
            end = text.indexOf(separator, start);
        }
        tmp.addElement(text.substring(start));
        String[] result = new String[tmp.size()];
        tmp.copyInto(result);
        return result; 
    }
    
    public static Vector sortVectorOfString(Vector e) {
        Vector v = new Vector();
        for(int count = 0; count < e.size(); count++) {
            String s = (String) e.elementAt(count);
            int i = 0;
            for (i = 0; i < v.size(); i++) {
                int c = s.compareTo((String) v.elementAt(i));
                if (c < 0) {
                    v.insertElementAt(s, i);
                    break;
                } else if (c == 0) {
                    break;
                }
            }
            if (i >= v.size()) {
                v.addElement(s);
            }
        }
        return v;
    }
    
//#ifndef WMUC
    public static String processError(Presence presence, int presenceType, ConferenceGroup group, MucContact muc) {
        XmppError xe=XmppError.findInStanza(presence);
        int errCode=xe.getCondition();

        ConferenceGroup grp=(ConferenceGroup)group;
        if (presenceType>=Presence.PRESENCE_OFFLINE) 
            muc.testMeOffline();
        if (errCode!=XmppError.CONFLICT || presenceType>=Presence.PRESENCE_OFFLINE)
            muc.setStatus(presenceType);

        String errText=xe.getText();
        if (errText!=null) return xe.toString(); // if error description is provided by server

        // legacy codes
        switch (errCode) {
            case XmppError.NOT_AUTHORIZED:        return "Password required";
            case XmppError.FORBIDDEN:             return "You are banned in this room";
            case XmppError.ITEM_NOT_FOUND:        return "Room does not exists";
            case XmppError.NOT_ALLOWED:           return "You can't create room on this server";
            case XmppError.NOT_ACCEPTABLE:        return "Reserved roomnick must be used";
            case XmppError.REGISTRATION_REQUIRED: return "This room is members-only";
            case XmppError.CONFLICT:              return "Nickname is already in use by another occupant";
            case XmppError.SERVICE_UNAVAILABLE:   return "Maximum number of users has been reached in this room";
            default: return xe.getName();
        }
    }
//#endif    
}
