/*
 * Gradient.java
 *
 * Created on 15.05.2008, 19:47
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

package ui;
import javax.microedition.lcdui.Graphics;

public class Gradient {
	private int x1;
	private int x2;

	private int y1;
	private int y2;

	private int redS;
	private int redE;

	private int greenS;
	private int greenE;

	private int blueS;
	private int blueE;

	private boolean vertical;

	public Gradient(int x1, int y1, int x2, int y2, int STARTRGB, int ENDRGB, boolean vertical) {
            this.x1 = x1;
            this.x2 = x2;
            this.y1 = y1;
            this.y2 = y2;
            this.redS = STARTRGB >> 16 & 0xff;
            this.redE = ENDRGB >> 16 & 0xff;
            this.greenS = STARTRGB >> 8 & 0xff;
            this.greenE = ENDRGB >> 8 & 0xff;
            this.blueS = STARTRGB & 0xff;
            this.blueE = ENDRGB & 0xff;
            this.vertical = vertical;
	}
    
	public void paint(Graphics g) {
            if (vertical) {
                paintV(g);
            } else {
                paintH(g);
            }
	}
	
	private void paintV(Graphics g) {
		for(int i2 = x1; i2 <= x2 - 1; i2++) {
                    int gCol[] = GradBackgr(redS, greenS, blueS, redE, greenE, blueE, i2, x1, x2 - 1);
                    g.setColor(gCol[0], gCol[1], gCol[2]);
                    g.drawLine(i2, y1, i2, y2);
		}
	}
    
	private void paintH(Graphics g) {
		for(int i2 = y1; i2 <= y2 - 1; i2++) {
                    int ai[] = GradBackgr(redS, greenS, blueS, redE, greenE, blueE, i2, y1, y2 - 1);
                    g.setColor(ai[0], ai[1], ai[2]);
                    g.drawLine(x1, i2, x2 - 1, i2);
		}
	}
        
            
	public void paintWidth(Graphics g, int width) {
		for(int i2 = y1; i2 <= y2 - 1; i2++) {
                    int ai[] = GradBackgr(redS, greenS, blueS, redE, greenE, blueE, i2, y1, y2 - 1);
                    g.setColor(ai[0], ai[1], ai[2]);
                    g.drawLine(x1, i2, width-1, i2);
		}
	}
	
	public static int[] GradBackgr(int i, int k, int l, int i1, int j1, int k1, int l1, int i2, int j2) {
		return (new int[] { (i1*(l1-i2)+i*(j2-l1))/(j2-i2), (j1*(l1-i2)+k*(j2-l1))/ (j2-i2), (k1*(l1-i2)+l*(j2-l1))/(j2-i2)});
	}
}
