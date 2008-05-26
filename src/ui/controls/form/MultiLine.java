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
import util.strconv;

/**
 *
 * @author ad
 */
public class MultiLine extends IconTextElement {
    
    private Vector lines=null;
    private String text;
    public boolean selectable;
    private Font font;
    
    /**
     * Creates a new instance of MultiLine
     */
    public MultiLine(String text) {
        super(null);
        this.text=text;
        font=FontCache.getMsgFont();
    }
    
    public String getValue() {
        return text;
    }
    
    public int getVHeight(){ return (lines==null)?1:(font.getHeight()*lines.size())+2; }
    
    public void drawItem(Graphics g, int ofs, boolean sel) {
        int width=g.getClipWidth();

        if (lines==null) {
            lines=strconv.parseMessage(text, width-6, -1, false, font);
        }
        if (lines!=null)
            drawAllStrings(g, 2, 0);

        super.drawItem(g, ofs, sel);
    }
    
    private void drawAllStrings(Graphics g, int x, int y) {
        if (lines.size()<1)
            return;

        int fh=font.getHeight();
        g.setFont(font);
	for (int line=0; line<lines.size(); ){
            g.drawString((String) lines.elementAt(line), x, y, Graphics.TOP|Graphics.LEFT);
            line=line+1;
            y += fh;
	}
    }
    
    public boolean isSelectable() { return selectable; }
    
    public Font getFont() { return font; }
}
