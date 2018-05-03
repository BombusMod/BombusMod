/*
 * Group.java
 *
 * Created on 8.05.2005, 0:36
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
 *
 */
package ui.controls.form;

import images.RosterIcons;
import java.util.*;
import Colors.ColorTheme;
import ui.*;

/**
 *
 * @author Evg_S
 */
public class ItemsGroup {

    public ItemsGroupHeader header;
    public Vector items;
    public boolean collapsed;

    public ItemsGroup(String headerText) {
        header = new ItemsGroupHeader(this, headerText);
        items = new Vector();
    }

    public Vector getItems() {
        Vector temp = new Vector();
        temp.addElement(header);
        if (!collapsed) {
            for (int i = 0; i < items.size(); i++) {
                if (items.elementAt(i) instanceof IconTextElement)
                    temp.addElement(items.elementAt(i));
                // TODO: nested groups
            }
        }
        return temp;
    }

    public class ItemsGroupHeader extends IconTextElement {

        private ItemsGroup group;
        public String name;

        public int imageExpandedIndex = RosterIcons.ICON_EXPANDED_INDEX;
        public int imageCollapsedIndex = RosterIcons.ICON_COLLAPSED_INDEX;

        public ItemsGroupHeader(ItemsGroup group, String name) {
            super(RosterIcons.getInstance());
            this.name = name;
            this.group = group;
        }

        public int getColor() {
            return ColorTheme.getColor(ColorTheme.GROUP_INK);
        }

        public int getImageIndex() {
            return collapsed ? imageCollapsedIndex : imageExpandedIndex;
        }

        public void onSelect() {
            collapsed = !collapsed;
        }

        public ItemsGroup getGroup() {
            return group;
        }

        public String toString() {
            return name;
        }
    }
}
