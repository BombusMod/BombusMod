/*
 * MessageParser.java
 *
 * Created on 6.02.2005, 19:38
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
 *
 */

package Messages;

//#ifdef SMILES
import images.SmilesIcons;
//#endif
import Fonts.FontCache;
import java.io.*;
import java.util.Vector;
import javax.microedition.lcdui.Font;
import Colors.ColorTheme;

import ui.*;
import Client.Msg;
import Client.Config;
import util.Strconv;

public final class MessageParser implements Runnable{
    
    private final static int URL=-2;
    private final static int NOSMILE=-1;

    private Vector smileTable;

    private Leaf root;
    private Leaf emptyRoot;
    
    // Singleton
    private static MessageParser instance=null;
    
    private int width; // window width
//#ifdef SMILES 
    private ImageList smileImages;
//#endif
    private Vector tasks=new Vector();
    
    private Thread thread;
    boolean wordsWrap;
    private static String wrapSeparators=" .,-=/\\;:+*()[]<>~!@#%^_&";
    
    private static String res= "/images/smiles.txt";
    
    public static MessageParser getInstance() {
        if (instance==null) 
            instance=new MessageParser(res);
        return instance;
    }

//#ifdef SMILES 
    public Vector getSmileTable() { return smileTable; }
//#endif
    private class Leaf {
        public int smile=NOSMILE;   // ??? ???????? ? ????
        public String smileChars;     // ??????? ?????????
        public Vector child;

        public Leaf() {
            child=new Vector();
            smileChars=new String();
        }
        
        public Leaf findChild(char c){
            int index=smileChars.indexOf(c);
            return (index==-1)?null:(Leaf)child.elementAt(index);
        }

        private void addChild(char c, Leaf child){
            this.child.addElement(child);
            smileChars=smileChars+c;
        }
    }
  
    private void addSmile(Leaf rootSmile, String smile, int index) {
	Leaf p=rootSmile;
	Leaf p1;
        smile=smile.toLowerCase();
	
	int len=smile.length();
	for (int i=0; i<len; i++) {
	    char c=smile.charAt(i);
	    p1=p.findChild(c);
	    if (p1==null) {
		p1=new Leaf();
		p.addChild((char)c,p1);
	    }
	    p=p1;
	}
	p.smile=index;
    }

    public void parseMsg(MessageItem messageItem,  int width)
    {
        synchronized (tasks) {
            wordsWrap=Config.getInstance().textWrap==1;
            messageItem.msgLines=new Vector();
//#ifdef SMILES
            this.smileImages=SmilesIcons.getInstance();
//#endif
            this.width=width;

            if (tasks.indexOf(messageItem)>=0) return;

            tasks.addElement(messageItem);
            if (thread==null) {
                thread=new Thread(this);
                thread.setPriority(Thread.MAX_PRIORITY);
                thread.start();
            }
        }
        return;
    }
    
    public void run() {
        while(true) {
            MessageItem task=null;
            synchronized (tasks) {
                if (tasks.size()==0) {
                    thread=null;
                    return;
                }
                task=(MessageItem) tasks.lastElement();
            }
            parseMessage(task);
            synchronized (tasks) {
                tasks.removeElement(task);
            }
        }
    }

    private MessageParser(String resource) {
        smileTable=null;
        smileTable=new Vector();
        root=new Leaf();
//#ifdef SMILES
        StringBuffer s=new StringBuffer(10);
        try { // generic errors
            int strnumber=0;
            boolean strhaschars=false;
            boolean endline=false;
            
            InputStream in=this.getClass().getResourceAsStream(resource);
            
            boolean firstSmile=true;
            
            int c;
            while (true) {
                c=in.read();
                if (c<0) break;
                switch (c) {
                    case 0x0d:
                    case 0x0a:
                        if (strhaschars) endline=true; else break;
                    case 0x09:
                        String smile=Strconv.convCp1251ToUnicode(s.toString()).toLowerCase();
                        if (firstSmile) smileTable.addElement(smile);

                        addSmile(root, smile, strnumber);

                        s.setLength(0);
                        firstSmile=false;

                        break;
                    default:
                        s.append((char)c);
                        strhaschars=true;
                }
                if (endline) {
                    endline=strhaschars=false;
                    strnumber++;
                    firstSmile=true;
                }
            }
            s.setLength(0);
            in.close();
            in=null;
        } catch (Exception e) {
            s.setLength(0);
        }
//#endif
        
 	addSmile(root, "http://", URL);
        addSmile(root, "tel:",URL);
        addSmile(root, "ftp://",URL);
        addSmile(root, "https://",URL);
        addSmile(root, "native:",URL);
//#if NICK_COLORS
        addSmile(root, "\01", ComplexString.NICK_ON);
        addSmile(root, "\02", ComplexString.NICK_OFF);
//#endif
        
        emptyRoot=new Leaf();
	addSmile(emptyRoot, "http://", URL);
        addSmile(emptyRoot, "tel:",URL);
        addSmile(emptyRoot, "ftp://",URL);
        addSmile(emptyRoot, "https://",URL);
        addSmile(emptyRoot, "native:",URL);
//#if NICK_COLORS
        addSmile(emptyRoot, "\01", ComplexString.NICK_ON);
        addSmile(emptyRoot, "\02", ComplexString.NICK_OFF);
//#endif
    }
    
    private void parseMessage(final MessageItem task) {
        Vector lines=task.msgLines;
        boolean singleLine=task.msg.itemCollapsed;
        
        boolean underline=false;
        
        Leaf smileRoot=(
//#ifdef SMILES
                task.smilesEnabled()
//#else
//#                 false
//#endif
                )? root: emptyRoot;
        
        int state=0;
        if (task.msg.subject==null) state=1;
        while (state<2) {
            if (task.smilesEnabled())
                smileRoot=(state==0)?emptyRoot:root;
            int w=0;
            StringBuffer s=new StringBuffer();
	    int wordWidth=0;
	    int wordStartPos=0;
//#ifdef SMILES
            ComplexString l=new ComplexString(smileImages);
//#else
//#             ComplexString l=new ComplexString();
//#endif
            lines.addElement(l);
            
            Font f=(task.msg.isHighlited() || state==0)? FontCache.getMsgFontBold(): FontCache.getMsgFont();
            l.setFont(f);
            
            String txt=(state==0)? task.msg.subject: task.msg.toString();
            
            int color=(state==0)?
                ColorTheme.getInstance().getColor(ColorTheme.MSG_SUBJ):
                ColorTheme.getInstance().getColor(ColorTheme.LIST_INK);
            l.setColor(color);
           
            if (txt==null) {
                state++;
                continue;
            }
            
            int pos=0;
            while (pos<txt.length()) {
                Leaf smileLeaf=smileRoot;
                int smileIndex=-1;
                int smileStartPos=pos;
                int smileEndPos=pos;
                
                while (pos<txt.length()) {
                    char c=txt.charAt(pos);
                    
                    if (underline) {
                        switch (c) {
                            case ' ':
                            case 0x09:
                            case 0x0d:
                            case 0x0a:
                            case 0xa0:
                            case ')':
                                underline=false;
                                if (wordStartPos!=pos) {
                                    s.append(txt.substring(wordStartPos,pos));
                                    wordStartPos=pos;
				    w+=wordWidth;
                                    wordWidth=0;
                                }
                                if (s.length()>0) {
                                    l.addUnderline();
                                    l.addElement(s.toString());
                                }
                                s.setLength(0);
                        }
                        break;
                    }
                    smileLeaf=smileLeaf.findChild(Character.toLowerCase(c));
                    if (smileLeaf==null) {
                        break;
                    }
                    if (smileLeaf.smile!=-1) {
                        smileIndex=smileLeaf.smile;
                        smileEndPos=pos;
                    }
                    pos++;
                    
                }
                
                if (smileIndex==URL) {
                    if (s.length()>0) l.addElement(s.toString());
                    s.setLength(0);
                    underline=true;
                }
//**************************
//#ifdef SMILES
                if (smileIndex>=0) {
                    if (wordStartPos!=smileStartPos) {
                        s.append(txt.substring(wordStartPos, smileStartPos));
                        w+=wordWidth;
                        wordWidth=0;
                    }
                    if (s.length()>0) {
                        if (underline)
                            l.addUnderline();
                        l.addElement(s.toString());
                    }
                    s.setLength(0);
                    int iw=(smileIndex<0x01000000)? smileImages.getWidth() : 0;
                    if (w+iw>width) {
                        task.notifyRepaint(lines, task.msg, false);
                        l=new ComplexString(smileImages);
                        lines.addElement(l);

                        if (singleLine) return;

                        l.setColor(color);
                        l.setFont(f);
                        w=0;
                    }
                    l.addImage(smileIndex); w+=iw;
                    pos=smileEndPos;
                    wordStartPos=pos+1;
                 } else {
//#endif
                    pos=smileStartPos;
                    char c=txt.charAt(pos);

                    int cw=f.charWidth(c);
                    if (c!=0x20) {
                        boolean newline= ( c==0x0d || c==0x0a );
                        if (wordWidth+cw>width || newline) {
                            s.append(txt.substring(wordStartPos,pos));
                            w+=wordWidth;
                            wordWidth=0;
                            wordStartPos=pos;
                            if (newline) wordStartPos++;
                        }
                        if (w+wordWidth+cw>width || newline) {
                            if (underline) l.addUnderline();
                            l.addElement(s.toString());
                            s.setLength(0); w=0;

                            if (c==0xa0) l.setColor(ColorTheme.getInstance().getColor(ColorTheme.MSG_HIGHLIGHT));
//#ifdef SMILES
                            l=new ComplexString(smileImages);
//#else
//#                             l=new ComplexString();
//#endif
                            lines.addElement(l);
                            task.notifyRepaint(lines, task.msg, false);

                            if (singleLine) return;

                            l.setColor(color);
                            l.setFont(f);
                        }
                    }
//****************************
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
//#ifdef SMILES
                    }
//#endif
                }
		pos++;
            }
 	    if (wordStartPos!=pos)
                s.append(txt.substring(wordStartPos,pos));
            if (s.length()>0) {
                if (underline) l.addUnderline();
                l.addElement(s.toString());
            }
             
            if (l.isEmpty())
                lines.removeElementAt(lines.size()-1);

            task.notifyRepaint(lines, task.msg, true);
            state++;
            s.setLength(0);
        }
    }
    
    public interface MessageParserNotify {
        void notifyRepaint(Vector v, Msg parsedMsg, boolean finalized);
    }
}
