/*
 * IconTextList.java
 *
 * Created on 30.01.2005, 18:19
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
import Fonts.FontCache;
import images.RosterIcons;
import javax.microedition.lcdui.*;
import java.util.*;
import Colors.ColorTheme;

abstract public class IconTextElement implements VirtualElement {
    int itemHeight;
    int imageYOfs;
    int fontYOfs;
    
    protected ImageList il;

    private int ilImageSize=0;
    
    ColorTheme ct;
    
    private boolean selectable=true; public boolean isSelectable() { return selectable; }    

//#ifdef SECONDSTRING
//#     private Font getSecondFont() {
//#         return FontCache.getBalloonFont();
//#     }
//#     
//#     private int getSecondFontHeight() {
//#         return FontCache.getBalloonFont().getHeight()-2;
//#     }
//#endif
    
    public int getImageIndex() { return -1; };

    public int getFontIndex() { return 0; }
    
    public Font getFont() {
        return (getFontIndex()==0)?
            FontCache.getRosterNormalFont():
            FontCache.getRosterBoldFont();
    }

    public void drawItem(Graphics g, int ofs, boolean sel){
       g.setFont(getFont());
       
       String str=toString();
       int offset=4;
//#ifdef SECONDSTRING
//#        String secstr="";
//#        if (hasSecondString()) {
//#            secstr=getSecondString();
//#        }
//#endif
       
       if (il!=null) {
            if (getImageIndex()!=-1) {
                offset+=ilImageSize;
                il.drawImage(g, getImageIndex(), 2, imageYOfs);
            }
       }
           
       g.clipRect(offset, 0, g.getClipWidth(), itemHeight);
       
       g.drawString(str, offset-ofs, fontYOfs, Graphics.TOP|Graphics.LEFT);
       
//#ifdef SECONDSTRING
//#        if (hasSecondString()) {
//#            g.setFont(getSecondFont());
//#            g.setColor(ct.getColor(ColorTheme.SECOND_LINE));
//#            g.drawString(secstr, offset-ofs, fontYOfs+getFont().getHeight()-2, Graphics.TOP|Graphics.LEFT);
//#        }
//#endif
    }

    public int getVWidth(){ 
        int wft=getFont().stringWidth(toString());
//#ifdef SECONDSTRING
//#         int wst=0;
//#         if (hasSecondString())
//#             wst=getSecondFont().stringWidth(getSecondString());
//#         return ((wft>wst)?wft:wst)+ilImageSize+4;   
//#else
            return wft+ilImageSize+4;
//#endif
    }
    
    public int getVHeight(){ 
        itemHeight=(ilImageSize>getFont().getHeight())?ilImageSize:getFont().getHeight();
//#if ALCATEL_FONT
//#         fontYOfs=1+((itemHeight-getFont().getHeight())/2);
//#else
        fontYOfs=(itemHeight-getFont().getHeight())/2;
//#endif
//#ifdef SECONDSTRING
//#         if (hasSecondString()){
//#             itemHeight+=getSecondFontHeight();
//#         }
//#endif
        imageYOfs=(itemHeight-ilImageSize)/2;
        return itemHeight;
    }
    public int getColorBGnd(){ return ct.getColor(ColorTheme.LIST_BGND);}
    public int getColor(){ return ct.getColor(ColorTheme.LIST_INK);}

    public void onSelect(){ };
    
    public IconTextElement(ImageList il) {
        super();
        this.il=il;
        ct=ColorTheme.getInstance();
	if (il!=null){
	    ilImageSize=il.getHeight();
	}
    }

    public String getTipString() {
        return null;
    }
    
//#ifdef SECONDSTRING
//#     private boolean hasSecondString() {
//#         try {
//#             String secstr=getSecondString();
//#             if (secstr!=null)
//#                 if (secstr.length()>0)
//#                     return true;
//#         } catch (Exception ex) {}
//#         return false;
//#     }
//# 
//#     public String getSecondString() { 
//#         return null;
//#     }
//#endif
    public int compare(IconTextElement right) { return 0; }
}
