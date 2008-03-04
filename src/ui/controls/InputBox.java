/*
 * InputBox.java
 *
 * Created on 26.04.2007, 15:14
 *
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
 */

package ui.controls;

import Client.Config;
//import Info.Phone;
import java.util.Vector;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import ui.ColorScheme;
//import ui.FontCache;

import java.util.Timer;
import java.util.TimerTask;
//import ui.VirtualList;

/**
 *
 * @author adsky
 */
public class InputBox {
    private int height, width, boxHeight;
    
    private Font font=Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_SMALL);
    
    private static String wrapSeparators=" .,-=/\\;:+*()[]<>~!@#%^_&";
    
    private static String [][] siemensChars =  {
            {".",",","?","!","'","\"","0","+","-","(",")","@","/",":","_","~","#","$","%","^","&","*","№"}, //0
            {" ","\n","1"}, //1
            {"\u0430","\u0431","\u0432","\u0433","2","a","b","c","\u0410","\u0411","\u0412","\u0413","A","B","C"}, //2
            {"\u0434","\u0435","\u0451","\u0436","\u0437","3","d","e","f","\u0414","\u0415","\u0401","\u0416","\u0417","D","E","F"}, //3
            {"\u0438","\u0439","\u043A","\u043B","4","g","h","i","\u0418","\u0419","\u041A","\u041B","G","H","I"}, //4
            {"\u043C","\u043D","\u043E","5","j","k","l","\u041C","\u041D","\u041E","J","K","L"}, //5
            {"\u043F","\u0440","\u0441","6","m","n","o","\u041F","\u0420","\u0421","M","N","O"}, //6
            {"\u0442","\u0443","\u0444","\u0445","7","p","q","r","s","\u0422","\u0423","\u0424","\u0425","P","Q","R","S"}, //7
            {"\u0446","\u0447","\u0448","\u0449","\u044A","8","t","u","v","\u0426","\u0427","\u0428","\u0429","\u042A","T","U","V"}, //8
            {"\u044B","\u044C","\u044D","\u044E","\u044F","9","w","x","y","z","\u042B","\u042C","\u042D","\u042E","\u042F","W","X","Y","Z"}, //9
    };
    
    private static String [][] genericChars =  {
            {".",",","?","!","'","\"","0","+","-","(",")","@","/",":","_","~","#","$","%","^","&","*","№"}, //0
            {" ","\n","1"}, //1
            {"\u0430","\u0431","\u0432","\u0433","2","a","b","c","\u0410","\u0411","\u0412","\u0413","A","B","C"}, //2
            {"\u0434","\u0435","\u0451","\u0436","\u0437","3","d","e","f","\u0414","\u0415","\u0401","\u0416","\u0417","D","E","F"}, //3
            {"\u0438","\u0439","\u043A","\u043B","4","g","h","i","\u0418","\u0419","\u041A","\u041B","G","H","I"}, //4
            {"\u043C","\u043D","\u043E","\u043F","5","j","k","l","\u041C","\u041D","\u041E","\u041F","J","K","L"}, //5
            {"\u0440","\u0441","\u0442","\u0443","6","m","n","o","\u0420","\u0421","\u0422","\u0423","M","N","O"}, //6
            {"\u0444","\u0445","\u0446","\u0447","7","p","q","r","s","\u0424","\u0425","\u0426","\u0427","P","Q","R","S"}, //7
            {"\u0448","\u0449","\u044A","\u044B","8","t","u","v","\u0428","\u0429","\u042A","\u042B","T","U","V"}, //8
            {"\u044C","\u044D","\u044E","\u044F","9","w","x","y","z","\u042C","\u042D","\u042E","\u042F","W","X","Y","Z"}, //9
    };

    private Vector strings;
    
    public String text="";
    
    public int key;

    private Timer timer;

    private boolean openedChar=false;

    private int lastkey;

    private int keycount=0;

    private int charsnum=0;
    
    public Config cf=Config.getInstance();

    public InputBox() { }
    
    public void draw(Graphics g, int width, int height) {
        this.height=height;
        this.width=width+10;
        
        boxHeight=getHeight();
        
        if (boxHeight<0)
            return;

        g.setClip(0, height-boxHeight, width, boxHeight);
        g.translate(0, height-boxHeight);
        
        g.setColor(ColorScheme.BALLOON_INK);
        g.fillRect(0,0,width,boxHeight);
        
        g.setColor(ColorScheme.BALLOON_BGND);
        g.fillRect(1,1,width-2,boxHeight-2);
        
        g.setColor(ColorScheme.BALLOON_INK);
        g.setFont(font);
        drawStrings(g);
    }

    private Vector parseMessage(int stringWidth) {
        Vector lines=new Vector();
        int state=0;
        
        while (state<1) {
            int w=0;
            StringBuffer s=new StringBuffer();
	    int wordWidth=0;
	    int wordStartPos=0;

            if (text==null) {
                state++;
                continue;
            }
            
            int pos=0;
            while (pos<text.length()) {
                char c=text.charAt(pos);

                int cw=font.charWidth(c);
                if (c!=0x20) {
                    boolean newline= ( c==0x0d || c==0x0a /*|| c==0xa0*/ );
                    if (wordWidth+cw>stringWidth || newline) {
                        s.append(text.substring(wordStartPos,pos));
                        w+=wordWidth;
                        wordWidth=0;
                        wordStartPos=pos;
                        if (newline) wordStartPos++;
                    }
                    if (w+wordWidth+cw>stringWidth || newline) {
                        lines.addElement(s.toString());
                        s.setLength(0); w=0;
                    }
                }
                if (c==0x09) c=0x20;

                if (c>0x1f) wordWidth+=cw;

                if (wrapSeparators.indexOf(c)>=0) {
                    if (pos>wordStartPos) 
                        s.append(text.substring(wordStartPos,pos));
                    if (c>0x1f) s.append(c);
                    w+=wordWidth;
                    wordStartPos=pos+1;
                    wordWidth=0;
                }
                
                pos++;
            }
	    if (wordStartPos!=pos)
		s.append(text.substring(wordStartPos,pos));
            if (s.length()>0) {
                lines.addElement(s.toString());
            }
            
            if (lines.isEmpty()) lines.removeElementAt(lines.size()-1);
            state++;
            
            s=null;
        }
        return lines;
    }
    
    private void drawStrings(Graphics g) {
        int y=1;
        if (strings.size()<1) return;

	for (int line=0; line<strings.size(); ) 
	{
            g.drawString((String) strings.elementAt(line), 2, y, Graphics.TOP|Graphics.LEFT);
            line=line+1;
            y += getFontHeight();
	}
    }
    
    private int getFontHeight() {
        int result=font.getHeight();
        return result;
    }
    
    public int getHeight() {
        strings=parseMessage(width-14);
        if (strings.isEmpty())
            return 0;
        return getFontHeight()*strings.size();
    }
    
    
    
    // ----------------------KeyHandler------------------------------//
    public String getText() {
        return text;
    }

    public void sendKey(int key) {
        if (lastkey==key) {
            if (openedChar) {
                keycount++;                
            } else {
                keycount=0;    
            }
        } else {
            openedChar=false;
            this.lastkey=key;
            keycount=0;
        }
        switch (key) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 0: {
                getString(key);
                break;
            }
            case -1: {
                text=(text.length()>1)?text.substring(0,text.length()-1):"";
                break;
            }
            case -10000: {
                text="";
            }
            //this.text=text;
        }
        startTimer();
    }
    
    public String getString( int key ) {
        if (cf.allowLightControl) {
            charsnum=(keycount>siemensChars[key].length)? 0 : keycount;
            text=(openedChar)? text.substring(0,text.length()-1)+siemensChars[key][charsnum] : text+siemensChars[key][charsnum];
        } else {
            charsnum=(keycount>genericChars[key].length)? 0 : keycount;
            text=(openedChar)? text.substring(0,text.length()-1)+genericChars[key][charsnum] : text+genericChars[key][charsnum];
        }
        return text;
    }
    
    public void startTimer() {
        stopTimer();
        timer = new Timer();
        timer.schedule(new RemindTask(), 1000);
        openedChar=true;
    }
    
    public void stopTimer() {
        if (timer!=null){
            timer.cancel();
            timer=null;
        }
        openedChar=false;
    }

    class RemindTask extends TimerTask {
        public void run() {
            stopTimer();
        }
   }
}
