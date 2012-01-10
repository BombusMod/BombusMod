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

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import ui.IconTextElement;
import ui.VirtualCanvas;

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
	if (img != null) {
        if (!collapsed) {
            g.drawImage(img, screenWidth/2, 0, Graphics.TOP|Graphics.HCENTER);
        }
		resizeImage();
	}
		VirtualCanvas.getInstance().repaint();
        super.drawItem(g, ofs, sel);
    }

    public boolean isSelectable() { return selectable; }
	
	private void resizeImage() {
		try {
			if (img.getWidth()>screenWidth) {
				int newHeight=(img.getHeight() * (screenWidth * 100 / img.getWidth()))/100;
				this.img=scale(img, screenWidth, newHeight);
			}
		} catch(OutOfMemoryError eom){
		} catch (Exception e) {}
	}
    
    public Image scale(Image image, int w, int h) {
        int w0 = image.getWidth();
        int h0 = image.getHeight();
        int[] arrayOld = new int[w0*h0];
        int[] arrayNew = new int[w*h];
        image.getRGB(arrayOld, 0, w0, 0, 0, w0, h0);
        
        int wy=0;
        int wy1=0;
        
        for (int y = 0; y < h; y++) {
             wy=w*y; 
             wy1=w0*(int)(y*h0/h); //thanks evgs :)
             for (int x = 0; x < w; x++) {
                   arrayNew[x+wy] = arrayOld[x*w0/w+wy1];
             }
        }
        arrayOld=null;
        return Image.createRGBImage(arrayNew, w, h, true);
    }
}
