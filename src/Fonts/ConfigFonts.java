/*
 * ConfigFonts.java
 *
 * Created on 20.05.2008, 15:37
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
import locale.SR;
import ui.controls.form.DropChoiceBox;
import ui.controls.form.DefForm;

public class ConfigFonts 
        extends DefForm {
    
    private DropChoiceBox font1;
    private DropChoiceBox font2;
    private DropChoiceBox font3;
    private DropChoiceBox font4;
    
    /** Creates a new instance of ConfigFonts
     */
    public ConfigFonts() {
        super(SR.MS_FONTS_OPTIONS);
        cf=Config.getInstance();

        font1=new DropChoiceBox(SR.MS_ROSTER_FONT);
        font1.append(SR.MS_FONTSIZE_NORMAL);
        font1.append(SR.MS_FONTSIZE_SMALL);
        font1.append(SR.MS_FONTSIZE_LARGE);
        font1.setSelectedIndex(cf.rosterFont/8);
        itemsList.addElement(font1);

        font2=new DropChoiceBox(SR.MS_MESSAGE_FONT);
        font2.append(SR.MS_FONTSIZE_NORMAL);
        font2.append(SR.MS_FONTSIZE_SMALL);
        font2.append(SR.MS_FONTSIZE_LARGE);
        font2.setSelectedIndex(cf.msgFont/8);
        itemsList.addElement(font2);

        font3=new DropChoiceBox(SR.MS_BAR_FONT);
        font3.append(SR.MS_FONTSIZE_NORMAL);
        font3.append(SR.MS_FONTSIZE_SMALL);
        font3.append(SR.MS_FONTSIZE_LARGE);
        font3.setSelectedIndex(cf.barFont/8);
        itemsList.addElement(font3);

        font4=new DropChoiceBox(SR.MS_POPUP_FONT);
        font4.append(SR.MS_FONTSIZE_NORMAL);
        font4.append(SR.MS_FONTSIZE_SMALL);
        font4.append(SR.MS_FONTSIZE_LARGE);
        font4.setSelectedIndex(cf.baloonFont/8);
        itemsList.addElement(font4);
    }
    
    public void cmdOk() {
        FontCache.roster=cf.rosterFont=font1.getValue()*8; //roster
        FontCache.msg=cf.msgFont=font2.getValue()*8; //msg
        FontCache.bar=cf.barFont=font3.getValue()*8; //bar
        FontCache.baloon=cf.baloonFont=font4.getValue()*8; //balloon

        cf.saveToStorage();
        
        super.getInfoBarItem().setFont(FontCache.getFont(true, cf.barFont));
        super.getInfoBarItem().clearWHCache();
        sd.roster.reEnumRoster();
        destroyView();
    }
}
