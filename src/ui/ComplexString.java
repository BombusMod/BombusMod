/*
 * ComplexString.java
 *
 * Created on 12.03.2005, 0:35
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

package ui;
import Client.Config;
import Fonts.FontCache;
import java.util.*;
import javax.microedition.lcdui.*;
import Colors.ColorTheme;

/**
 *
 * @author Eugene Stahov
 */
public class ComplexString extends Vector implements VirtualElement {

    //private Vector v;
    public final static int IMAGE     = 0x00000000;
    public final static int COLOR     = 0x01000000;
    public final static int RALIGN    = 0x02000000;
    public final static int UNDERLINE = 0x03000000;
//#if NICK_COLORS
    public final static int NICK_ON   = 0x04000000;
    public final static int NICK_OFF  = 0x05000000;
//#endif

    protected Font font=FontCache.getMsgFont();
    private int height;
    private int width;
    private ImageList imageList;
    private int colorBGnd;
    private int color;
    
    private int heightCorrect;
    
    ColorTheme ct;
    /** Creates a new instance of ComplexString */
    public ComplexString() {
        super();
        ct=ColorTheme.getInstance();
        color=ct.getColor(ColorTheme.LIST_INK);
        colorBGnd=ct.getColor(ColorTheme.LIST_BGND);
        
        if (Config.getInstance().phoneManufacturer==Config.WINDOWS || Config.getInstance().phoneManufacturer==Config.NOKIA)
            heightCorrect=1;
    }

    /** Creates a new instance of ComplexString */
    public ComplexString(ImageList imageList) {
        this();
        this.imageList=imageList;
    }

    private int imgHeight(){
        return (imageList==null)?0:imageList.getHeight();
    }
    private int imgWidth(){
        return (imageList==null)?0:imageList.getWidth();
    }
    
    public int getColor() { return color; }
    public int getColorBGnd() { return colorBGnd; }
    
    public void setColorBGnd(int color){ colorBGnd=color;}
    public void setColor(int color){ this.color=color;}
    
    public void onSelect(){};
    
    public void drawItem(Graphics g, int offset, boolean selected){
        boolean ralign=false;
	boolean underline=false;
        
//#if NICK_COLORS
	boolean nick=false;
//#endif
        
        int w=offset;
        int dw;
        int imageYOfs=(( getVHeight()-imgHeight() )>>1);

        int fontYOfs=(( getVHeight()-font.getHeight() )>>1)+heightCorrect;

        int imgWidth=imgWidth();
        
        g.setFont(font);
        for (int index=0; index<elementCount;index++) {
            Object ob=elementData[index];
            if (ob!=null) {
                
                if (ob instanceof String ){
                    // string element
                    String s=(String) ob;
//#if NICK_COLORS
                    if (nick) {
                        int color=g.getColor();
                        dw=0;
                        int p1=0; 
                        while (p1<s.length()) {
                            int p2=p1;
                            char c1=s.charAt(p1);
                            //processing the same cp
                            while (p2<s.length()) {
                                char c2=s.charAt(p2);
                                if ( (c1&0xff00) != (c2 &0xff00) ) break;
                                p2++;
                            }
                            int vColor=ct.strong(color);
                            g.setColor( (c1>255) ? vColor : color);
                            dw=font.substringWidth(s, p1, p2-p1);
                            if (ralign) w-=dw;
                            g.drawSubstring( s, p1, p2-p1, 
                                    w,fontYOfs,Graphics.LEFT|Graphics.TOP);
                            if (!ralign) w+=dw;
                            p1=p2;
                        }
                        
                        g.setColor(color);
                    } else {
//#endif
                        dw=font.stringWidth(s);
                        if (ralign) w-=dw;
                        g.drawString(s,w,fontYOfs,Graphics.LEFT|Graphics.TOP);
                        if (underline) {
                            int y=getVHeight()-1;
                            g.drawLine(w, y, w+dw, y);
                            underline=false;
                        }
                        if (!ralign) w+=dw;
//#if NICK_COLORS
                    }
//#endif

                } else if ((ob instanceof Integer)) {
                    // image element or color
                    int i=((Integer)ob).intValue();
                    switch (i&0xff000000) {
                        case IMAGE:
                            if (imageList==null) break;
                            if (ralign) w-=imgWidth;
                            imageList.drawImage(g, ((Integer)ob).intValue(), w, imageYOfs);
                            if (!ralign) w+=imgWidth;
                            break;
                        case COLOR:
                            g.setColor(0xFFFFFF&i);
                            break;
                        case RALIGN:
                            ralign=true;
                            w=g.getClipWidth()-1;
			    break;
			case UNDERLINE:
			    underline=true;
			    break;
//#if NICK_COLORS
                        case NICK_ON:
                            nick=true; 
                            break;
                        case NICK_OFF:
                            nick=false;
                            break;
//#endif
                    }
                } /* Integer*/ else if (ob instanceof VirtualElement) { 
                    int clipw=g.getClipWidth(); 
                    int cliph=g.getClipHeight();
                    ((VirtualElement)ob).drawItem(g,0,false);
                    g.setClip(g.getTranslateX(), g.getTranslateY(), clipw, cliph);
                }
            } // if ob!=null
        } // for
        
    }

    public int getVWidth() {
        //g.setColor(0);
        if (width>0) return width;  // cached
        
        int w=0;
        int imgWidth=imgWidth();
        
        for (int index=0; index<elementCount;index++) {
            Object ob=elementData[index];
            if (ob!=null) {
                
                if (ob instanceof String ){
                    // string element
                    w+=font.stringWidth((String)ob);
                } else if ((ob instanceof Integer)&& imageList!=null) {
                    // image element or color
                    int i=(((Integer)ob).intValue());
                    switch (i&0xff000000) {
                        case IMAGE:
                            w+=imgWidth;
                            break;
                    }
                } // Integer
            } // if ob!=null
        } // for
        return width=w;
    }

    public void setElementAt(Object obj, int index) {
        height=width=0; // discarding cached values
        if (index>=elementCount) this.setSize(index+1);
        super.setElementAt(obj, index);
    }
    
    public int getVHeight(){
        if (height!=0) return height;
        for (int i=0;i<elementCount;i++){
            int h=0;
            Object o=elementData[i];
            if (o==null) continue;
            if (o instanceof String) { h=font.getHeight(); } else
            if (o instanceof Integer) {
                int a=((Integer)o).intValue();
                if ((a&0xff000000) == 0) { h=imageList.getHeight(); }
            } else
            if (o instanceof VirtualElement) { h=((VirtualElement)o).getVHeight(); }
            if (h>height) height=h;
        }
        return height;
    }

    public void addElement(Object obj) {
        height=0;
        width=0; // discarding cached values
        super.addElement(obj);
    }

    public void addImage(int imageIndex){ addElement(new Integer(imageIndex)); }
    public void addColor(int colorRGB){ addElement(new Integer(COLOR | colorRGB)); }
    public void addRAlign(){ addElement(new Integer(RALIGN)); }
    public void addUnderline(){ addElement(new Integer(UNDERLINE)); }
    
    public Font getFont() {
        return font;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public String getTipString() { return null; }
    
//#ifdef SECONDSTRING
//#     public String getSecondString() {
//#         return null;
//#     }
//#endif

    public boolean isSelectable() {
        return true;
    }
    
    public void clearWHCache() {
        width=0;
        height=0;
    }

    public boolean handleEvent(int keyCode) { return false; }
}
