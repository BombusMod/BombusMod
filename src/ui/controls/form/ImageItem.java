/*
 * ImageItem.java
 *
 * Created on 25.05.2008, 19:07
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

import images.Scale;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import ui.IconTextElement;

/**
 *
 * @author ad
 */
public class ImageItem
    extends IconTextElement {
    
    public Image img;
    public boolean collapsed;
    
    public boolean selectable=true;

    private String altText;

    private int screenWidth;
    
    /** Creates a new instance of ImageItem */
    public ImageItem(Image img, String altText) {
        super(null);

        this.img=img;
        this.altText=altText;
    }
    
    public void onSelect() {
        collapsed=!collapsed;
    }
    
    public String toString() {
        if (!collapsed)
            return "";
        
        if (img==null)
            return "[No image]";
        
        StringBuffer im=new StringBuffer(altText);
        if (altText!=null)
            im.append(" ");
        im.append(img.getWidth()).append("x").append(img.getHeight());
        
        return im.toString();            
    }
    
    public int getVHeight(){
        if (collapsed || img==null)
            return super.getVHeight();

        return img.getHeight();
    }
    
    public void drawItem(Graphics g, int ofs, boolean sel) {
        screenWidth=g.getClipWidth();

        if (!collapsed) {
            g.drawImage(img, screenWidth/2, 0, Graphics.TOP|Graphics.HCENTER);
        }
        super.drawItem(g, ofs, sel);
    }

    public boolean isSelectable() { return selectable; }
    
    public boolean handleEvent(int keyCode) {
         switch (keyCode) {
            case 0:
                if (img.getWidth()>screenWidth) {
                    int newHeight=(img.getHeight() * (screenWidth * 100 / img.getWidth()))/100;
                    this.img=new Scale().scale(img, screenWidth, newHeight);
                    return true;
                }
                break;
         }
        return false;
    }
}
