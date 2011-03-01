/*
 * MainBar.java
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

package ui;

import Client.Config;
import Fonts.FontCache;
import images.RosterIcons;
import javax.microedition.lcdui.Graphics;
//#ifdef BACK_IMAGE
//# import javax.microedition.lcdui.Image;
//#endif

public class MainBar extends ComplexString{

//#ifdef BACK_IMAGE
//#     public static Image bg;
//#endif
    
    public boolean lShift = false;
    public boolean rShift = false;
    
    public MainBar(int size, Object first, Object second, boolean bold) {
        this (size);
        if (first!=null) setElementAt(first,0);
        if (second!=null) setElementAt(second,1);
        
        font = FontCache.getFont(bold, FontCache.bar);
//#ifdef BACK_IMAGE
//#         try {
//#             if (bg==null)
//#                 bg=Image.createImage("/images/panelbg.png");
//#         } catch (Exception e) { }
//#endif        
    }
    
    public MainBar(Object obj) {
        this(1, obj, null, false);
    }
    
    public MainBar(Object obj, boolean bold) {
        this(1, obj, null, bold);
    }
    public MainBar(Object obj, boolean bold, boolean centered) {
        this(1, obj, null, bold);
        this.centered = centered;
    }
    
    public MainBar(int size) {
        super (RosterIcons.getInstance());
        setSize(size);
    }
    public int getVHeight() {
//#ifdef BACK_IMAGE
//#         if (bg != null)
//#             return Math.max(super.getVHeight(), bg.getHeight());
//#         else
//#endif    
       /*     if (centered && Config.getInstance().advTouch)
                return super.getVHeight() << 1;*/
        return Math.max(Config.getInstance().minItemHeight, super.getVHeight());
    }
    public void drawItem(Graphics g, int offset, boolean selected) {
        int xo = g.getClipX();
        int yo = g.getClipY();
        int wo = g.getClipWidth();
        int ho = g.getClipHeight();
//#ifdef BACK_IMAGE
//#         if (bg != null) {
//#             int ofs = 0;
//#             if (getVHeight() > bg.getHeight())
//#                 ofs =  (getVHeight() - bg.getHeight()) >> 1;
//#             for (int i=0; i < g.getClipWidth(); i++)
//#                 g.drawImage(bg, i, ofs , Graphics.TOP|Graphics.LEFT);
//#         }
//#endif        
        g.clipRect((lShift)? 20: 0, 0, g.getClipWidth() - ((rShift)? 20: 0), g.getClipHeight());
        super.drawItem(g, offset, selected);
        //g.setClip(xo, yo, wo, ho);
    }
}
