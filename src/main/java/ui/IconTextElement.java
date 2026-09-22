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
import Colors.ColorTheme;
import Client.Config;

abstract public class IconTextElement implements VirtualElement {
    public Config cf = Config.getInstance();

    int itemHeight;
    int imageYOfs;
    protected int fontYOfs;

    protected ImageList il;
    private int ilImageSize=0;

    public IconTextElement(ImageList il) {
        super();
        this.il=il;
        if (il!=null) ilImageSize=il.getHeight();
    }

    private boolean selectable=true;
    public boolean isSelectable() { return selectable; }

    public boolean handleEvent(int keyCode) { return false; }

    public int getImageIndex() { return -1; };

    public String getImgAlt() { return ""; };

    public int getFontIndex() { return 0; }

    /** Returns font height in pixels for layout calculations */
    public int getFontHeight() {
        return FontCache.getFontHeight((getFontIndex()==0)?false:true, FontCache.roster);
    }

    /** Estimates text width in pixels for layout calculations */
    public int getTextWidth(String text) {
        return FontCache.getStringWidth(text, (getFontIndex()==0)?false:true, FontCache.roster);
    }

    public int getVWidth(){
        return getTextWidth(toString())+ilImageSize+4;
    }

    public int getVHeight() {
        int fontHeight = getFontHeight();
        itemHeight = Math.max(Math.max(ilImageSize, fontHeight), cf.minItemHeight);
        fontYOfs = (itemHeight - fontHeight) >> 1;
        imageYOfs = (itemHeight - ilImageSize) >> 1;
        return itemHeight;
    }

    public int getItemHeight(){
        return itemHeight;
    }

    public int getColorBGnd(){ return ColorTheme.getColor(ColorTheme.LIST_BGND);}
    public int getColor(){ return ColorTheme.getColor(ColorTheme.LIST_INK);}

    public void onSelect(){ };

    public String getTipString() { return null; }

    public int compare(IconTextElement right) { return 0; }
}
