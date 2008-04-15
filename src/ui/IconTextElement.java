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
import images.RosterIcons;
import javax.microedition.lcdui.*;
import java.util.*;
import Colors.Colors;

abstract public class IconTextElement implements VirtualElement 
{
    
    int itemHeight;
    int imageYOfs;
    int fontYOfs;
    
    ImageList il;
    ImageList mi;
    
    int heightFirstLine=0;

    private int miImageSize;
    private int ilImageSize;

    private int fontHeight;
    //private int heightSecondFont;
    
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

    public void drawItem(Graphics g, int ofs, boolean sel){
       g.setFont(getFont());
       
       String str=toString();
       int offset=4+ilImageSize;
//#ifdef SECONDSTRING
//#        String secstr="";
//#        if (hasSecondString()) {
//#            secstr=getSecondString();
//#        }
//#endif
       
       if (il!=null)
           drawPic(g, il, getImageIndex(), 2, imageYOfs);

        if (getSecImageIndex()>-1) {
            int secImgY = imageYOfs;
//#ifdef SECONDSTRING
//#             if (hasSecondString()) {
//#                 offset=2;
//#                 secImgY = imageYOfs+il.getHeight();
//#             }
//#endif
            switch (getSecImageIndex()) {
                case 1001:
                    drawPic(g, il, RosterIcons.ICON_COMPOSING_INDEX, offset, secImgY);
                    offset=offset+ilImageSize+2;
                    break;
                case 2001:
                    drawPic(g, il, RosterIcons.ICON_APPEARING_INDEX, offset, secImgY);
                    offset=offset+ilImageSize+2;
                    break;
                case 2002:
                    drawPic(g, il, RosterIcons.ICON_VIEWING_INDEX, offset, secImgY);
                    offset=offset+ilImageSize+2;
                    break;
                case 2003:
                    drawPic(g, il, RosterIcons.ICON_AUTHRQ_INDEX, offset, secImgY);
                    offset=offset+ilImageSize+2;
                    break;
                case 2004:
                    drawPic(g, il, RosterIcons.ICON_MESSAGE_INDEX, offset, secImgY);
                    offset=offset+ilImageSize+2;
                    break;
                default:
                    drawPic(g, mi, getSecImageIndex(), offset, secImgY);
                    offset=offset+miImageSize+2;
            }
            miImageSize=mi.getHeight();
        } else {
            miImageSize=0;
        }
//#ifdef SECONDSTRING
//#        if (hasSecondString()) {
//#            itemHeight=heightFirstLine+((fontHeight>miImageSize)?fontHeight-3:miImageSize);
//#        } else
//#endif
           itemHeight=(heightFirstLine>miImageSize)?heightFirstLine:miImageSize;
           
       g.clipRect(offset, 0, g.getClipWidth(), itemHeight);
       
       g.drawString(str,offset-ofs, fontYOfs, Graphics.TOP|Graphics.LEFT);
       
//#ifdef SECONDSTRING
//#        if (hasSecondString()) {
//#            //g.setFont(getSmallFont());
//#            g.drawString(secstr, offset-ofs, fontYOfs+fontHeight-3, Graphics.TOP|Graphics.LEFT);
//#        }
//#endif
    }
    
    private void drawPic(Graphics g, ImageList i, int iconNum, int x, int y) {
        i.drawImage(g, iconNum, x, y);
    }

    public int getVWidth(){ 
        int wft=getFont().stringWidth(toString());
//#ifdef SECONDSTRING
//#         int wst=0;
//#         if (hasSecondString())
//#             wst=getFont().stringWidth(getSecondString());
//#         return ((wft>wst)?wft:wst)+ilImageSize+4;   
//#else
            return wft+ilImageSize+4;
//#endif
    }
    
    public int getVHeight(){ 
        return itemHeight;
    }
    public int getColorBGnd(){ return Colors.LIST_BGND;}

    public void onSelect(){ };
    
    public IconTextElement(ImageList il, ImageList mi) {
        super();
        this.il=il;
        this.mi=mi;
        fontHeight=FontCache.getRosterNormalFont().getHeight();
        //heightSecondFont=FontCache.getBalloonFont().getHeight();
	if (il!=null){
	    ilImageSize=il.getHeight();
	}
	if (mi!=null){
	    miImageSize=mi.getHeight();
	}
        itemHeight=heightFirstLine=(ilImageSize>fontHeight)?ilImageSize:fontHeight;
        imageYOfs=(itemHeight-ilImageSize)/2;
        fontYOfs=(itemHeight-fontHeight+2)/2;
    }

    public String getTipString() {
        return null;
    }
    
//#ifdef SECONDSTRING
//#     public boolean hasSecondString() {
//#         String secstr=getSecondString();
//#         if (secstr==null)
//#             return false;
//#         return (secstr.length()>0);
//#     }
//#endif
    
    public int compare(IconTextElement right) { return 0; }
}
