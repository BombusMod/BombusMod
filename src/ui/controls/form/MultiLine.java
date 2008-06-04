/*
 * MultiLine.java
 *
 * Created on 25.05.2008, 18:37
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

import Fonts.FontCache;
import java.util.Vector;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import ui.IconTextElement;
import util.StringUtils;

/**
 *
 * @author ad
 */
public class MultiLine extends IconTextElement {
    
    private Vector lines=null;
    private String text;
    
    private String caption;
    
    public boolean selectable;
    
    private Font font;
    private int fontHeight;
    
    private Font captionFont;
    private int captionFontHeight;
    
    private boolean parsed;
    private int itemHeight=0;


    /**
     * Creates a new instance of MultiLine
     */
    public MultiLine(String caption, String text) {
        super(null);
        this.text=text;
        this.caption=caption;
        
        font=FontCache.getMsgFont();
        fontHeight=font.getHeight();
        itemHeight=fontHeight;
        
        if (caption!=null) {
            captionFont=FontCache.getMsgFontBold();
            captionFontHeight=captionFont.getHeight();
            itemHeight=captionFontHeight;
        }
    }
    
    public String getValue() {
        return text;
    }
    
    public String toString() {
        if (caption==null)
            return text;
        
        return caption+"\n"+text;
    }
    
    public int getVHeight(){
        return itemHeight;
    }
    
    public void drawItem(Graphics g, int ofs, boolean sel) {
        int width=g.getClipWidth();
        int height=g.getClipHeight();

        if (lines==null && width>0) {
            lines=StringUtils.parseMessage(text, width-10, font);
            itemHeight=(fontHeight*lines.size())+2;
            if (caption!=null)
                itemHeight+=2+captionFontHeight;
            parsed=true;
        }
        if (!parsed)
            return;
        
        int y=0;
        if (caption!=null) {
            g.setFont(captionFont);
            g.drawString(caption, 2, y, Graphics.TOP|Graphics.LEFT);
            y=captionFontHeight;
        }

        g.setFont(font);

	for (int line=0; line<lines.size(); ){
            g.drawString((String) lines.elementAt(line), 2, y, Graphics.TOP|Graphics.LEFT);
            line=line+1;
            y += fontHeight;
	}
    }

    public boolean isSelectable() { return selectable; }
}
