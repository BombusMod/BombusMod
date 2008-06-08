/*
 * newConfigFonts.java
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
import Client.StaticData;
import javax.microedition.lcdui.*;
import locale.SR;
import ui.controls.form.DropChoiceBox;
import ui.controls.form.DefForm;
import ui.controls.form.SimpleString;

public class ConfigFonts 
        extends DefForm {
    
    private Display display;
    
    private DropChoiceBox font1;
    private DropChoiceBox font2;
    private DropChoiceBox font3;
    private DropChoiceBox font4;
    
    StaticData sd=StaticData.getInstance();
    
    Config cf;
    
    /** Creates a new instance of newConfigFonts */
    public ConfigFonts(Display display) {
        super(display, SR.MS_FONTS_OPTIONS);
        this.display=display;
        parentView=display.getCurrent();
        
        cf=Config.getInstance();
        
        itemsList.addElement(new SimpleString(SR.MS_ROSTER_FONT));
        font1=new DropChoiceBox(display);
        font1.append(SR.MS_FONTSIZE_NORMAL);
        font1.append(SR.MS_FONTSIZE_SMALL);
        font1.append(SR.MS_FONTSIZE_LARGE);
        font1.setSelectedIndex(cf.font1/8);
        itemsList.addElement(font1);
        
        itemsList.addElement(new SimpleString(SR.MS_MESSAGE_FONT));
        font2=new DropChoiceBox(display);
        font2.append(SR.MS_FONTSIZE_NORMAL);
        font2.append(SR.MS_FONTSIZE_SMALL);
        font2.append(SR.MS_FONTSIZE_LARGE);
        font2.setSelectedIndex(cf.font2/8);
        itemsList.addElement(font2);
        
        itemsList.addElement(new SimpleString(SR.MS_BAR_FONT));
        font3=new DropChoiceBox(display);
        font3.append(SR.MS_FONTSIZE_NORMAL);
        font3.append(SR.MS_FONTSIZE_SMALL);
        font3.append(SR.MS_FONTSIZE_LARGE);
        font3.setSelectedIndex(cf.font3/8);
        itemsList.addElement(font3);
        
        itemsList.addElement(new SimpleString(SR.MS_POPUP_FONT));
        font4=new DropChoiceBox(display);
        font4.append(SR.MS_FONTSIZE_NORMAL);
        font4.append(SR.MS_FONTSIZE_SMALL);
        font4.append(SR.MS_FONTSIZE_LARGE);
        font4.setSelectedIndex(cf.font4/8);
        itemsList.addElement(font4);
  
        moveCursorTo(getNextSelectableRef(-1));
        attachDisplay(display);
    }
    
    public void cmdOk() {
        FontCache.rosterFontSize=cf.font1=font1.getValue()*8;
        FontCache.msgFontSize=cf.font2=font2.getValue()*8;
        FontCache.barFontSize=cf.font3=font3.getValue()*8;
        FontCache.balloonFontSize=cf.font4=font4.getValue()*8;
        FontCache.resetCache();

        cf.saveToStorage();
        super.getInfoBarItem().setFont(FontCache.getBarFont());
        super.getInfoBarItem().clearWHCache();
        sd.roster.reEnumRoster();
        destroyView();
    }
}
