/*
 * FontCache.java
 *
 * Created on 5.09.2008, 9:54
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

package Fonts;

import Client.Config;

/**
 * Font size cache. Stores pixel sizes directly (no J2ME Font objects).
 * Sizes are set by the platform at startup (BombusModActivity.onResume).
 */
public class FontCache {

    // Default pixel sizes — overridden by platform at startup
    public static int smallFontSize  = 12;
    public static int mediumFontSize = 16;
    public static int largeFontSize  = 20;

    public static int roster=0;
    public static int msg=0;
    public static int bar=0;
    public static int baloon=0;

    public static int getFontHeight(boolean isBold, int size) {
        switch (size) {
            case 0: return smallFontSize;
            case 1: return mediumFontSize;
            case 2: return largeFontSize;
            default: return mediumFontSize;
        }
    }

    /** Approximate string width: char count * avg char width (~60% of height) */
    public static int getStringWidth(String text, boolean isBold, int size) {
        if (text == null) return 0;
        int h = getFontHeight(isBold, size);
        return text.length() * (h * 6 / 10);
    }

    /** Initialize from platform pixel values. Called by BombusModActivity.onResume. */
    public static void initSizes(int small, int medium, int large) {
        smallFontSize = small;
        mediumFontSize = medium;
        largeFontSize = large;
    }
}
