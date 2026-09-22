/*
 * ChoiceBox.java
 *
 * Created on 20.05.2008, 9:06
 *
 * Copyright (c) 2006-2008, Daniel Apatin (ad), http://apatin.net.ru
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
package ui.controls.form;

import Colors.ColorTheme;
import Fonts.FontCache;
import images.RosterIcons;
import java.util.Vector;
import ui.IconTextElement;

/**
 *
 * @author ad
 */
public class DropChoiceBox
        extends IconTextElement {
    
    public int index=0;
    
    public Vector items=new Vector();
    
    private boolean selectable=true;
    
    private int colorItem;
    private int colorBorder;
    private int colorBGnd;
    
    private String caption="";
    
    private Object font;
    private int fontHeight;

    private Object captionFont;
    private int captionFontHeight;

    private int itemHeight=0;
    
    /**
     * Creates a new instance of ChoiceBox
     */
    public DropChoiceBox(String caption) {
        super(RosterIcons.getInstance());
        this.caption=(caption==null)?"":caption;
        
        fontHeight=FontCache.getFontHeight(false, FontCache.roster);
        fontHeight=FontCache.getFontHeight(false, FontCache.roster);
        itemHeight=fontHeight;
        
        if (caption!=null) {
            captionFontHeight=FontCache.getFontHeight(true, FontCache.msg);
            itemHeight+=captionFontHeight;
        }
    }
    
    public int getCaptionLength() {
        if (caption==null) return 0;
        if (caption.equals("")) return 0;
        return FontCache.getStringWidth(caption, true, FontCache.msg);
    }

    public int getTextLength() {
        String text=getTextValue();
        if (text.equals("")) return 0;
        return FontCache.getStringWidth(text, false, FontCache.roster);
    }

    private String getTextValue() {
        if (items.size()<1) return "";
        return (String) items.elementAt(index);
    }

    public String toString() {
        return (getCaptionLength()>getTextLength())?caption:getTextValue();
    }

    public void onSelect() {
        if (items.size()>1)
            new DropListBox(items, this);
        }

    public int getValue() { return index; }
    
    public void add(String value) { items.addElement(value); }
    
    public void setSelectedIndex(int index) { 
        if (index>items.size()-1)
            index=0;
        this.index=index;
    }
    
    public int size() { 
        return items.size();
    }
    
    public int getSelectedIndex() { return index; }
    
    public int getVHeight(){
        return itemHeight;
    }

    public boolean handleEvent(int keyCode) {
        if (items.size()<1) return false;
        
         switch (keyCode) {
             case 5:
                onSelect();
                return true;
         }
        return false;
    }
    
    public boolean isSelectable() { return selectable; }
    
    public void afterSelect() {};
}
