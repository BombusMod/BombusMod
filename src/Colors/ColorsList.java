/*
 * ColorsList.java
 *
 * Created on 23.05.2008, 13:10
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

package Colors;

import java.util.Enumeration;

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import locale.SR;
import ui.VirtualElement;
import ui.controls.form.DefForm;

/**
 *
 * @author ad
 */
public class ColorsList extends DefForm
    {
//#ifdef PLUGINS
//#     public static String plugin = new String("PLUGIN_COLORS");
//#endif
    

    public void setColor(int paramName, int value) {
        ((ColorVisualItem)itemsList.elementAt(paramName)).setColor(value);
    }    
    
    /**
     * Creates a new instance of ColorsList
     */
    public ColorsList(Display display, Displayable pView) {
        super(display, pView, SR.MS_COLOR_TUNE);       
        
        int cnt=0;
        itemsList.removeAllElements();
        for (Enumeration r = ColorTheme.colorsContainer.elements(); r.hasMoreElements();) {
            ColorItem c = (ColorItem) r.nextElement();
            //#ifdef COLOR_TUNE
//#             itemsList.addElement(new ColorVisualItem(c.name, NAMES[cnt], c.color));
            //#endif
            cnt++;
        }
        this.display=display;

        setCommandListener(this);
        attachDisplay(display);
        this.parentView=pView;
    }

    private void loadColors() {
        
    }
    
    protected int getItemCount() { return itemsList.size(); }

    protected VirtualElement getItemRef(int index) {
        return (VirtualElement)itemsList.elementAt(index);
    }    
    
    public void cmdOk() {
//#ifdef COLOR_TUNE
//#         new ColorSelectForm(display, this, this, cursor);
//#         
//#endif
    }
//#ifdef MENU_LISTENER
    public String touchLeftCommand() {
        return SR.MS_EDIT;
    }
//#endif

//#ifdef COLOR_TUNE
//#     public static final String[] NAMES = {
//#             SR.MS_BALLOON_INK,
//#             SR.MS_BALLOON_BGND,
//#             SR.MS_LIST_BGND,
//#             SR.MS_LIST_BGND_EVEN,
//#             SR.MS_LIST_INK,
//# 
//#             SR.MS_MSG_SUBJ,
//#             SR.MS_MSG_HIGHLIGHT,
//# 
//#             SR.MS_DISCO_CMD,
//# 
//#             SR.MS_BAR_BGND,
//#             SR.MS_BAR_BGND+" 2",
//#             SR.MS_BAR_INK,
//# 
//#             SR.MS_CONTACT_DEFAULT,
//#             SR.MS_CONTACT_CHAT,
//#             SR.MS_CONTACT_AWAY,
//#             SR.MS_CONTACT_XA,
//#             SR.MS_CONTACT_DND,
//#             SR.MS_CONTACT+" J2J",
//# 
//#             SR.MS_GROUP_INK,
//# 
//#             SR.MS_BLK_INK,
//#             SR.MS_BLK_BGND,
//# 
//#             SR.MS_MESSAGE_IN,
//#             SR.MS_MESSAGE_OUT,
//#             SR.MS_MESSAGE_PRESENCE,
//#             SR.MS_MESSAGE_AUTH,
//#             SR.MS_MESSAGE_HISTORY,
//# 
//#             SR.MS_MESSAGE_IN_S,
//#             SR.MS_MESSAGE_OUT_S,
//#             SR.MS_MESSAGE_PRESENCE_S,
//# 
//#             SR.MS_PGS_REMAINED,
//#             SR.MS_PGS_COMPLETE,
//#             SR.MS_PGS_COMPLETE+" 2",
//#             SR.MS_PGS_INK,
//# 
//#             SR.MS_HEAP_TOTAL,
//#             SR.MS_HEAP_FREE,
//# 
//#             SR.MS_CURSOR_BGND,
//#             SR.MS_CURSOR_OUTLINE,
//# 
//#             SR.MS_SCROLL_BRD,
//#             SR.MS_SCROLL_BAR,
//#             SR.MS_SCROLL_BGND,
//# 
//#             SR.MS_POPUP_MESSAGE,
//#             SR.MS_POPUP_MESSAGE_BGND,
//#             SR.MS_POPUP_SYSTEM,
//#             SR.MS_POPUP_SYSTEM_BGND,
//# 
//#             SR.MS_CONTACT_STATUS,
//# 
//#             SR.MS_CONTROL_ITEM,
//#         };
//#endif
}
