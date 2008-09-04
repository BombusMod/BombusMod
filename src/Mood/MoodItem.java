/*
 * MoodItem.java
 *
 * Created on 1.05.2008, 19:43
 *
 * Copyright (c) 2005-2008, Eugene Stahov (evgs), http://bombus-im.org
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

package Mood;

import Colors.ColorTheme;
import images.MoodIcons;
import ui.IconTextElement;

/**
 *
 * @author evgs
 */
public class MoodItem extends IconTextElement {
//#ifdef PLUGINS
//#     public static String plugin = new String("PLUGIN_PEP");
//#endif
    
    private int iconIndex;
    private String name;
    private String label;
    
    /** Creates a new instance of MoodItem */
    public MoodItem(int index) {
        super(MoodIcons.getInstance());
        
        label=Moods.getInstance().getMoodLabel(index);
        name=Moods.getInstance().getMoodName(index);
        iconIndex=index;
    }

    public int getImageIndex() { return iconIndex; }
    public int getColor() { return ColorTheme.getColor(ColorTheme.CONTACT_DEFAULT); }
    public String toString() { return label; }
    public String getTipString() { return name; }

    public int compare(IconTextElement right) {
        return label.compareTo( ((MoodItem)right).label );
    }
}
