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
//#ifdef GRADIENT
//# package ui;
//# 
//# import Colors.ColorTheme;
//# import javax.microedition.lcdui.Graphics;
//# 
//# public class Gradient {
//# 
//#     public final static int VERTICAL = 0;
//#     public final static int HORIZONTAL = 1;
//#     public final static int MIXED_UP = 2;
//#     public final static int MIXED_DOWN = 3;
//#     public final static int CACHED_VERTICAL = 4;
//#     public final static int CACHED_HORIZONTAL = 5;
//#     public final static int SOLID = 6;
//#     private int x1;
//#     private int x2;
//#     private int y1;
//#     private int y2;
//#     private int alphaS;
//#     private int alphaE;
//#     private int redS;
//#     private int redE;
//#     private int greenS;
//#     private int greenE;
//#     private int blueS;
//#     private int blueE;
//#     private int R;
//#     private int type;
//#     private int[] points = null;
//# 
//#     public Gradient() {
//#     }
//# 
//#     public void update(int x1, int y1,
//#             int x2, int y2,
//#             final int StartRGB, final int EndRGB,
//#             int type,
//#             final int R) {
//#         alphaS = ColorTheme.getAlpha(StartRGB);
//#         alphaE = ColorTheme.getAlpha(EndRGB);
//#         if (alphaS == alphaE && alphaS == 0) {
//#             alphaS = alphaE = 255;
//#         }
//#         redS = ColorTheme.getRed(StartRGB);
//#         redE = ColorTheme.getRed(EndRGB);
//#         greenS = ColorTheme.getGreen(StartRGB);
//#         greenE = ColorTheme.getGreen(EndRGB);
//#         blueS = ColorTheme.getBlue(StartRGB);
//#         blueE = ColorTheme.getBlue(EndRGB);
//#         if (StartRGB == EndRGB && (alphaS == 0 || alphaS == 255)) {
//#             type = SOLID;
//#         }
//# 
//#         int width = this.x2 - this.x1;
//#         int height = this.y2 - this.y1;
//#         boolean changed = (points == null
//#                 || (width != (x2 - x1))
//#                 || (height != (y2 - y1))
//#                 || this.alphaS != alphaS || this.alphaE != alphaE
//#                 || this.redS != redS || this.redE != redE
//#                 || this.greenS != greenS || this.greenE != greenE
//#                 || this.blueS != blueS || this.blueE != blueE
//#                 || this.R != R
//#                 || this.type != type);
//#         if (changed) {
//#             if (y1 < 0) {
//#                 y1 = 0;
//#             }
//#             final int dHeight = VirtualCanvas.getInstance().getHeight();
//#             if (y2 > dHeight + 1) {
//#                 y2 = dHeight;
//#             }
//#             changed = (points == null
//#                     || (width != (x2 - x1))
//#                     || (height != (y2 - y1))
//#                     || this.alphaS != alphaS || this.alphaE != alphaE
//#                     || this.redS != redS || this.redE != redE
//#                     || this.greenS != greenS || this.greenE != greenE
//#                     || this.blueS != blueS || this.blueE != blueE
//#                     || this.R != R
//#                     || this.type != type);
//#         }
//# 
//#         this.x1 = x1;
//#         this.x2 = x2;
//#         this.y1 = y1;
//#         this.y2 = y2;
//#         this.R = R;
//#         this.type = type;
//# 
//#         if (changed) {
//#             if (type == MIXED_UP || type == MIXED_DOWN || type == CACHED_VERTICAL || type == CACHED_HORIZONTAL) {
//#                 if (width != x2 - x1 || height != y2 - y1 || points == null) {
//#                     width = x2 - x1;
//#                     height = y2 - y1;
//#                     points = null;
//# 
//#                     if (width <= 0 || height <= 0) {
//#                         return;
//#                     }
//#                     points = new int[height * width];
//#                 }
//#                 makePoints(width, height, R);
//#             } else {
//#                 points = null;
//#             }
//#         }
//#     }
//# 
//#     public void paint(final Graphics g) {
//#         if (x2 <= x1 || y2 <= y1) {
//#             return;
//#         }
//#         switch (type) {
//#             case SOLID:
//#                 g.setColor(redS, greenS, blueS);
//#                 if (R > 0) {
//#                     g.fillRoundRect(x1, y1, x2 - x1, y2 - y1, R, R);
//#                 } else {
//#                     g.fillRect(x1, y1, x2 - x1, y2 - y1);
//#                 }
//#                 break;
//#             case VERTICAL:
//#                 paintV(g);
//#                 break;
//#             case HORIZONTAL:
//#                 paintH(g);
//#                 break;
//#             case CACHED_VERTICAL:
//#             case CACHED_HORIZONTAL:
//#             case MIXED_UP:
//#             case MIXED_DOWN: {
//#                 final int x = g.getTranslateX();
//#                 final int y = g.getTranslateY();
//#                 g.translate(-x, -y);
//#                 if (points != null) {
//#                     g.drawRGB(points, 0, x2 - x1, x + x1, y + y1, x2 - x1, y2 - y1, true);//(alphaS!=0 || alphaE!=0));
//#                 }
//#                 g.translate(x, y);
//#                 break;
//#             }
//#         }
//#     }
//# 
//#     private int sqrt(final int x) {
//#         final byte iterations = 20;
//#         int a = 7;
//#         for (int i2 = 0; i2 < iterations; i2++) {
//#             if (a == 0) {
//#                 break;
//#             }
//#             a = (a + x / a) >> 1;
//#         }
//#         return a;
//#     }
//# 
//#     private void paintV(final Graphics g) {
//#         for (int i2 = x1; i2 <= x2 - 1; ++i2) {
//#             final int gCol[] = GradBackgr(i2, x1, x2 - 1);
//#             g.setColor(gCol[0], gCol[1], gCol[2]);
//#             g.drawLine(i2, y1, i2, y2);
//#         }
//#     }
//# 
//#     private void paintH(final Graphics g) {
//#         for (int i2 = y1; i2 <= y2 - 1; ++i2) {
//#             final int ai[] = GradBackgr(i2, y1, y2 - 1);
//#             g.setColor(ai[0], ai[1], ai[2]);
//#             g.drawLine(x1, i2, x2 - 1, i2);
//#         }
//#     }
//# 
//#     public void paintWidth(final Graphics g, final int width) {
//#         for (int i2 = y1; i2 <= y2 - 1; ++i2) {
//#             final int ai[] = GradBackgr(i2, y1, y2 - 1);
//#             g.setColor(ai[0], ai[1], ai[2]);
//#             g.drawLine(x1, i2, width - 1, i2);
//#         }
//#     }
//# 
//#     private int[] GradBackgr(final int l1, final int i2, final int j2) {
//#         return new int[]{
//#                     ((redE * (l1 - i2) + redS * (j2 - l1)) / (j2 - i2)),
//#                     ((greenE * (l1 - i2) + greenS * (j2 - l1)) / (j2 - i2)),
//#                     ((blueE * (l1 - i2) + blueS * (j2 - l1)) / (j2 - i2)),
//#                     ((alphaE * (l1 - i2) + alphaS * (j2 - l1)) / (j2 - i2))
//#                 };
//#     }
//# 
//#     private void makePoints(final int width, final int height, final int R) {
//#         if (type == MIXED_DOWN || type == MIXED_UP) {
//#             int a, r, g, b, dist, diff, new_a, new_r, new_g, new_b, color;
//#             int yS, yE, yD;
//# 
//#             final int width2 = width / 2;
//#             final int width3 = width / 3;
//# 
//#             int idx = 0;
//# 
//#             if (type == MIXED_UP) {
//#                 yS = height;
//#                 yE = 0;
//#                 yD = -1;
//#             } else {
//#                 yS = 0;
//#                 yE = height;
//#                 yD = 1;
//#             }
//#             for (int y = yS; y != yE; y += yD) {
//#                 r = y * (redE - redS) / (height - 1) + redS;
//#                 g = y * (greenE - greenS) / (height - 1) + greenS;
//#                 b = y * (blueE - blueS) / (height - 1) + blueS;
//#                 a = y * (alphaE - alphaS) / (height - 1) + alphaS;
//#                 for (int x = width; x > 0; x--) {
//#                     dist = x - width2;
//#                     if (dist < 0) {
//#                         dist = -dist;
//#                     }
//#                     dist = width3 - dist;
//#                     if (dist < 0) {
//#                         dist = 0;
//#                     }
//#                     diff = 96 * dist / width3;
//# 
//#                     new_r = r + diff;
//#                     new_g = g + diff;
//#                     new_b = b + diff;
//#                     new_a = a + diff;
//#                     if (new_a < 0) {
//#                         new_a = 0;
//#                     }
//#                     if (new_a > 255) {
//#                         new_a = 255;
//#                     }
//#                     if (new_r < 0) {
//#                         new_r = 0;
//#                     }
//#                     if (new_r > 255) {
//#                         new_r = 255;
//#                     }
//#                     if (new_g < 0) {
//#                         new_g = 0;
//#                     }
//#                     if (new_g > 255) {
//#                         new_g = 255;
//#                     }
//#                     if (new_b < 0) {
//#                         new_b = 0;
//#                     }
//#                     if (new_b > 255) {
//#                         new_b = 255;
//#                     }
//#                     color = ColorTheme.getColor(new_a, new_r, new_g, new_b);
//#                     points[idx++] = color;
//#                 }
//#             }
//#         } else if (type == CACHED_VERTICAL) {
//#             for (int i = 0; i < width; ++i) {
//#                 final int ai[] = GradBackgr(i, x1, x2 - 1);
//#                 final int color = ColorTheme.getColor(ai[3], ai[0], ai[1], ai[2]);
//#                 int ds = 0;
//#                 if (R > i) {
//#                     ds = (int) (R - sqrt(2 * i * R - i * i));
//#                 }
//#                 if (R > width - i - 1) {
//#                     ds = (int) (R - sqrt(2 * (width - i - 1) * R - (width - i - 1) * (width - i - 1)));
//#                 }
//#                 for (int j = 0; j < height; ++j) {
//#                     if (i < ds || i > height - ds - 1) {
//#                         points[width * j + i] = 0;
//#                     } else {
//#                         points[width * j + i] = color;
//#                     }
//#                 }
//#             }
//#         } else if (type == CACHED_HORIZONTAL) {
//#             for (int j = 0; j < height; ++j) {
//#                 final int ai[] = GradBackgr(j + y1, y1, y2 - 1);
//#                 final int color = ColorTheme.getColor(ai[3], ai[0], ai[1], ai[2]);
//#                 int ds = 0;
//#                 if (R > j) {
//#                     ds = (int) (R - sqrt(2 * j * R - j * j));
//#                 }
//#                 if (R > height - j - 1) {
//#                     ds = (int) (R - sqrt(2 * (height - j - 1) * R - (height - j - 1) * (height - j - 1)));
//#                 }
//#                 for (int i = 0; i < width; ++i) {
//#                     if (i < ds || i > width - ds - 1) {
//#                         points[width * j + i] = 0;
//#                     } else {
//#                         points[width * j + i] = color;
//#                     }
//#                 }
//#             }
//#         }
//#     }
//# }
//#endif
