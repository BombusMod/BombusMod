/*
 * IconTextList.java
 *
 * Created on 30.01.2005, 18:19
 *
 * Copyright (c) 2005-2007, Eugene Stahov (evgs), http://bombus-im.org
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
import images.MoodIcons;
import javax.microedition.lcdui.*;
import java.util.*;

abstract public class IconTextElement implements VirtualElement 
{
    
    int itemHeight;
    int imageYOfs;
    int fontYOfs;
    int imgWidth;
    
    ImageList il;
    MoodIcons mi;
    
    int heightFirstLine=0;
    
    abstract protected int getImageIndex();
    
    public int getSecImageIndex() {
        return -1;
    }

    public int getFontIndex() { return 0;}
    
    private Font getFont() {
        return (getFontIndex()==0)?
            FontCache.getRosterNormalFont():
            FontCache.getRosterBoldFont();
    }
    private Font getSmallFont() {
        return FontCache.getBalloonFont();
    }
    
    public void drawItem(Graphics g, int ofs, boolean sel, boolean drawsec){
       g.setFont(getFont());
       
       String str=toString();
       int offset=4+imgWidth;
//#ifdef SECONDSTRING
//#        String secstr=getSecondString();
//# 
//#        if (sel && drawsec && secstr!=null)
//#            itemHeight=heightFirstLine+getSmallFont().getHeight()-1;
//#        else
//#endif
           itemHeight=heightFirstLine;
       
       if (il!=null) 
           il.drawImage(g, getImageIndex(), 2, imageYOfs);
           
       if (getSecImageIndex()>-1) {
           mi.getInstance().drawImage(g, getSecImageIndex(), offset, imageYOfs);
           offset=offset+imgWidth;
       }
           
       g.clipRect(offset, 0, g.getClipWidth(), itemHeight);
       
       g.drawString(str,offset-ofs, fontYOfs, Graphics.TOP|Graphics.LEFT);
       
//#ifdef SECONDSTRING
//#        if (sel && drawsec && secstr!=null) {
//#            g.setFont(getSmallFont());
//#            g.drawString(secstr, offset-ofs, fontYOfs+getFont().getHeight(), Graphics.TOP|Graphics.LEFT);
//#        }
//#endif
    }

    public int getVWidth(){ 
        try {
            int wft=0;
//#ifdef SECONDSTRING
//#             int wst=0;
//#             if (getSecondString()!=null)
//#                 wst=getSmallFont().stringWidth(getSecondString())+imgWidth+2;
//#endif
            wft=getFont().stringWidth(toString());
            
//#ifdef SECONDSTRING
//#             return ((wft>wst)?wft:wst)+imgWidth+4;   
//#else
            return wft+imgWidth+4;
//#endif
        } catch (Exception e) {
            return 0;
        }
    }
    
    public int getVHeight(){ 
        return itemHeight;
    }
    public int getColorBGnd(){ return ColorScheme.LIST_BGND;}

    public void onSelect(){ };
    
    public IconTextElement(ImageList il) {
        super();
        this.il=il;
        int heightFont=FontCache.getRosterNormalFont().getHeight();
        int heightImage=0;
	if (il!=null){
	    heightImage=il.getHeight();
            imgWidth=il.getWidth();
	}
        itemHeight=heightFirstLine=(heightImage>heightFont)?heightImage:heightFont;
        imageYOfs=(itemHeight-heightImage)/2;
        fontYOfs=(itemHeight-heightFont+2)/2;
    }

    public String getTipString() {
        return null;
    }
    
    public int compare(IconTextElement right) { return 0; }
}