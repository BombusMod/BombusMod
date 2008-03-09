/*
 * Mood.java
 * Copyright (c) 2006-2007, Daniel Apatin (ad), http://apatin.net.ru
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

package UserMood;

import images.MoodIcons;
import locale.SR;
import ui.ColorScheme;
import ui.IconTextElement;

public class Mood extends IconTextElement {

    private int id;
    
    private String name;
    private String text;
    private String locale;

    public Mood(int id, String name, String locale, String text) {
        super(MoodIcons.getInstance());
        this.name=name;
        this.locale=locale;
        this.id=id;
        this.text=text;
    }
    
    public String toString(){
        if (SR.MS_XMLLANG=="en" || SR.MS_XMLLANG==null)
            return name;
        return locale;
    }
    
    public int getColor(){ return ColorScheme.LIST_INK;}

   //public int getID() { return id; }
    public String getName() { return name; }
    public String getText() { return text; }
    public String getLocale() { return locale; }
    
//#ifdef SECONDSTRING
//#         public String getSecondString() { 
//#             return null;
//#         }
//#endif

    public int getImageIndex(){ return id;}
}