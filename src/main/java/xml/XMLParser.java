/*
 * XMLParser.java
 *
 * Created on 22.03.2008, 1:02
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

package xml;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.*;
import util.StringUtils;

public class XMLParser {
    private final static int MAX_BIN_DATASIZE=64*1024; //64 KB - experimental
        
    private final static int PLAIN_TEXT=0;
    private final static int TAGNAME=1;
    private final static int ENDTAGNAME=2;
    private final static int ATRNAME=3;
    private final static int ATRVALQS=4;
    private final static int ATRVALQD=5;
    private final static int CDATA=6;
    private final static int BASE64=7;
    private final static int BASE64_INIT=8;

    private int state;
    
    private XMLEventListener eventListener;
    
    private StringBuffer sbuf;
    
    private StringBuffer tagName;
    private Vector attr;
    private String atrName;
    
    ByteArrayOutputStream baos;
    int ibuf;
    int padding;
    /** Creates a new instance of XMLParser */
    
    public XMLParser(XMLEventListener eventListener) {
        this.eventListener=eventListener;
        state=PLAIN_TEXT;
        sbuf=new StringBuffer();
        tagName=new StringBuffer();
    }
 
    public void parse(byte indata[], int size) throws XMLException, UnsupportedEncodingException{
        int dptr=0;
        while (size>0) {
            size--;
            char c=(char)(indata[dptr++] &0xff);
            switch (state) {
                case PLAIN_TEXT: {
                    //parsing plain text
                    if (c=='<') {
                        state=TAGNAME;
                        
                        if (sbuf.length()>0) 
                            eventListener.plainTextEncountered( parsePlainText(sbuf) ); 
                        
                        sbuf=new StringBuffer();
                        tagName=new StringBuffer();
                        attr=null;
                        attr=new Vector();
                        
                        continue;
                    }
                    sbuf.append(c); continue;
                }

        case ATRNAME:
            {
                if (c=='?') continue;
                if (c==' ') continue;
                if (c=='=') continue;
                if (c=='\'') { state=ATRVALQS; atrName=parsePlainText(sbuf); sbuf=new StringBuffer(); continue; }
                if (c=='\"') { state=ATRVALQD; atrName=parsePlainText(sbuf); sbuf=new StringBuffer(); continue; }

                if (c!='>' && c!='/') { 
                    sbuf.append(c);
                    continue;
                } else {
                    state=TAGNAME;
                    sbuf=new StringBuffer();
                }
            }

        case TAGNAME:
            {
                if (c=='?') continue;
                if (c=='/') { 
                    state=ENDTAGNAME; 
                    sbuf=new StringBuffer();
                    if (tagName.length()>0) {
                        String tn= parsePlainText(tagName);
                        eventListener.tagStart(localName(tn, TAGNAME), attr); 
                        sbuf.append(tn);
                    }
                    continue; 
                }
                if (c==' ') { state=ATRNAME; continue; }
                if (c=='>') { 
                    state=PLAIN_TEXT; 
                    if (eventListener.tagStart(localName(parsePlainText(tagName), TAGNAME), attr))
                        state=BASE64_INIT; 
                    continue; 
                }
                tagName.append(c);

                if (c=='[') {
                    if (tagName.toString().equals("![CDATA[")) 
                        state=CDATA;
                    continue;
                }

                continue;
            }

        case CDATA:
            {
                sbuf.append(c);
                if (c=='>') {
                    int e3=sbuf.length()-3;
                    if (e3 < 0) continue;
                    if (sbuf.charAt(e3) != ']') continue;
                    if (sbuf.charAt(e3+1) != ']') continue;
                    //if (sbuf[e3] != '>') continue;
                    sbuf.setLength(e3);
                    state=PLAIN_TEXT;
                    continue;
                }
                continue;
            }
        case ENDTAGNAME:
            {
                if (c==' ') continue;
                if (c=='>') {
                    state=PLAIN_TEXT;
                    eventListener.tagEnd(localName(sbuf.toString(), TAGNAME));
                    sbuf=new StringBuffer();
                    continue;
                }
                sbuf.append(c);
                continue;
            }
            
        case ATRVALQS: 
            {
                if (c=='\'') { 
                    state=ATRNAME; 
                    attr.addElement(localName(atrName, ATRNAME));
                    attr.addElement(parsePlainText(sbuf));
                    sbuf=new StringBuffer(); 
                    continue; 
                }
                sbuf.append(c);
                continue;
            }
        case ATRVALQD: 
            {
                if (c=='\"') { 
                    state=ATRNAME; 
                    attr.addElement(localName(atrName, ATRNAME));
                    attr.addElement(parsePlainText(sbuf));
                    sbuf=new StringBuffer(); 
                    continue; 
                }
                sbuf.append(c);
                continue;
            }
        
        case BASE64_INIT: 
            {
                baos=new ByteArrayOutputStream(MAX_BIN_DATASIZE);
                ibuf=1;
                padding=0;
                state=BASE64;
            }
        case BASE64: 
            {
                int base64=-1;
                if (c > 'A'-1  &&  c < 'Z'+1) base64 =  c - 'A';
                else if (c > 'a'-1  &&  c < 'z'+1) base64 =  c +26-'a';
                else if (c > '0'-1  &&  c < '9'+1) base64 =  c +52-'0';
                else if (c == '+') base64=62;
                else if (c == '/') base64=63;
                else if (c == '=') {base64=0; padding++;}
                
                else if (c == '<') {
                    try { 
                        baos.close();
                        
                        if (baos.size()<MAX_BIN_DATASIZE)
                            eventListener.binValueEncountered( baos.toByteArray() );
                        else
                            eventListener.binValueEncountered( new byte[1] );
                    } catch (Exception ex) { 
                    }
                    
                    baos=null;
                    sbuf=new StringBuffer();
                    tagName=new StringBuffer();
                    state=TAGNAME;
                    continue;
                }
                
                if (base64>=0) ibuf=(ibuf<<6)+base64;
                if (baos.size()<MAX_BIN_DATASIZE) {
                    if (ibuf>=0x01000000){
                        baos.write((ibuf>>16) &0xff);
                        if (padding<2) baos.write((ibuf>>8) &0xff);
                        if (padding==0) baos.write(ibuf &0xff);
                        //len+=3;
                        ibuf=1;
                    }
                    
                }
                
                continue;
            }
        }
    }
        
    };
    
    private String parsePlainText(StringBuffer sb) throws XMLException, UnsupportedEncodingException {
        return StringUtils.unescapeTags(new String(sb.toString().getBytes("ISO8859-1"), "UTF-8"));
    }

    public void pushOutPlainText() throws XMLException, UnsupportedEncodingException {
        if (state==PLAIN_TEXT) {
            if (sbuf.length()>0)
                eventListener.plainTextEncountered( parsePlainText(sbuf) );
            
            sbuf=new StringBuffer();
        }
    }

    public static String extractAttribute(String attributeName, Vector attributes) {
      if (attributes==null) return null;
      int index=0;
      int j=attributes.size();
      while (index<j) {
          if ( ((String)attributes.elementAt(index)).equals(attributeName) )
              return (String)attributes.elementAt(index+1);
          
          index+=2;
      }
      
      return null;
    }
    
    public static String localName(String entity, int type) {
        int delim = entity.indexOf(':');
        if (delim < 0) 
            return entity;
        String localName = entity;
        switch (type) {
            case ATRNAME:
                localName = entity.substring(0, delim); 
                break;
            case TAGNAME:                               
                localName = entity.substring(delim + 1);
                break;                
        }
        return localName;        
    }
}
