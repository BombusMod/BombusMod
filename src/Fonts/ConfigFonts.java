/*
 * ConfigFonts.java
 *
 * Created on 11.05.2008, 01:37
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
import Fonts.FontCache;

public class ConfigFonts implements CommandListener {
    private Display display;
    private Displayable parentView;

    Form f;
    
    ChoiceGroup font1;
    ChoiceGroup font2;
    ChoiceGroup font3;
    ChoiceGroup font4;
    
    Command cmdOk=new Command(SR.MS_OK,Command.OK,1);
    Command cmdCancel=new Command(SR.MS_CANCEL, Command.BACK,99);
    
    StaticData sd=StaticData.getInstance();
    
    Config cf;
    
    /** Creates a new instance of ConfigForm */
    public ConfigFonts(Display display) {
        this.display=display;
        parentView=display.getCurrent();
        
        cf=Config.getInstance();;

        f=new Form(SR.MS_FONTS_OPTIONS);

        String fnts[]={SR.MS_FONTSIZE_NORMAL, SR.MS_FONTSIZE_SMALL, SR.MS_FONTSIZE_LARGE};
        font1=new ChoiceGroup(SR.MS_ROSTER_FONT, ChoiceGroup.POPUP, fnts, null);
        font2=new ChoiceGroup(SR.MS_MESSAGE_FONT, ChoiceGroup.POPUP, fnts, null);
        font3=new ChoiceGroup(SR.MS_BAR_FONT, ChoiceGroup.POPUP, fnts, null);
        font4=new ChoiceGroup(SR.MS_POPUP_FONT, ChoiceGroup.POPUP, fnts, null);
        font1.setSelectedIndex(cf.font1/8, true);
        font2.setSelectedIndex(cf.font2/8, true);
        font3.setSelectedIndex(cf.font3/8, true);
        font4.setSelectedIndex(cf.font4/8, true);
        
        f.append(font1);
        f.append(font2);
        f.append(font3);
        f.append(font4);
        
        f.addCommand(cmdOk);
        f.addCommand(cmdCancel);    
        f.setCommandListener(this);    
        display.setCurrent(f);
    }
    
    public void commandAction(Command c, Displayable d) {
        if (c==cmdOk) {
            FontCache.rosterFontSize=cf.font1=font1.getSelectedIndex()*8;
            FontCache.msgFontSize=cf.font2=font2.getSelectedIndex()*8;

            FontCache.barFontSize=cf.font3=font3.getSelectedIndex()*8;
            FontCache.balloonFontSize=cf.font4=font4.getSelectedIndex()*8;
            FontCache.resetCache();

            cf.saveToStorage();

            sd.roster.reEnumRoster();
        }
        destroyView();
    }
    
    public void destroyView(){
        if (display!=null)
            display.setCurrent(sd.roster);
    }
}
