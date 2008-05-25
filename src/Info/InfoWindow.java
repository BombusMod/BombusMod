/*
 * InfoWindow.java
 *
 * Created on 25.05.2008, 19:29
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

package Info;

import Client.Config;
import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.lcdui.Display;
import locale.SR;
import midlet.BombusMod;
import ui.controls.form.BoldString;
import ui.controls.form.DefForm;
import ui.controls.form.LinkString;
import ui.controls.form.MultiLine;
import ui.controls.form.SpacerItem;

/**
 *
 * @author ad
 */
public class InfoWindow
        extends DefForm {
    
    LinkString siteUrl;
    MultiLine description;
    MultiLine memory;
    MultiLine abilities;
    
    /**
     * Creates a new instance of InfoWindow
     */
    public InfoWindow(Display display) {
        super(display, SR.MS_ABOUT);
        this.display=display;
        parentView=display.getCurrent();
        
        description=new MultiLine(Version.getNameVersion()+"\nMobile Jabber client\n"+Config.getOs()+"\nCopyright (c) 2005-2008, Eugene Stahov (evgs), Daniel Apatin (ad)");
        description.selectable=true;
        itemsList.addElement(description);
        itemsList.addElement(new SpacerItem(0));
        
        siteUrl=new LinkString("http://bombusmod.net.ru"){ public void doAction() { try { BombusMod.getInstance().platformRequest("http://bombusmod.net.ru"); } catch (ConnectionNotFoundException ex) { }}};
        itemsList.addElement(siteUrl);
        itemsList.addElement(new SpacerItem(0));
        
        StringBuffer memInfo=new StringBuffer(SR.MS_MEMORY);
        memInfo.append("\n");
        memInfo.append(SR.MS_FREE);
        System.gc();
        memInfo.append(Runtime.getRuntime().freeMemory()>>10);
        memInfo.append("\n");
        memInfo.append(SR.MS_TOTAL);
        memInfo.append(Runtime.getRuntime().totalMemory()>>10);
        memory=new MultiLine(memInfo.toString());
        memory.selectable=true;
        itemsList.addElement(memory);
        itemsList.addElement(new SpacerItem(0));
        
        abilities=new MultiLine(getAbilities());
        abilities.selectable=true;
        itemsList.addElement(abilities);

        //moveCursorTo(getNextSelectableRef(-1));
        attachDisplay(display);
    }

    private String getAbilities() {
        StringBuffer abilities=new StringBuffer("Abilities: ");
//#ifdef COLOR_TUNE
//#         abilities.append(", COLOR_TUNE");
//#endif
//#ifdef ARCHIVE
        abilities.append(", ARCHIVE");
//#endif
//#ifdef POPUPS
        abilities.append(", POPUPS");
//#endif
//#ifdef ELF
//#         abilities.append(", ELF");
//#endif
//#ifdef NEW_MENU
        abilities.append(", NEW_MENU");
//#endif
//#ifdef SERVICE_DISCOVERY
        abilities.append(", SERVICE_DISCOVERY");
//#endif
//#ifdef PRIVACY
        abilities.append(", PRIVACY");
//#endif
//#ifdef SMILES
        abilities.append(", SMILES");
//#endif
//#ifdef ANTISPAM
//#         abilities.append(", ANTISPAM");
//#endif
//#ifdef REQUEST_VOICE
//#         abilities.append(", REQUEST_VOICE");
//#endif
//#ifdef HISTORY
//#         abilities.append(", HISTORY");
//#endif
//#ifdef SE_LIGHT
//#         abilities.append(", SE_LIGHT");
//#endif
//#ifdef TEMPLATES
        abilities.append(", TEMPLATES");
//#endif
//#ifdef USER_KEYS
//#         abilities.append(", USER_KEYS");
//#endif
//#ifdef AUTOSTATUS
//#         abilities.append(", AUTOSTATUS");
//#endif
//#ifdef USE_ROTATOR
        abilities.append(", USE_ROTATOR");
//#endif
//#ifdef FILE_TRANSFER
        abilities.append(", FILE_TRANSFER");
//#endif
//#ifdef LAST_MESSAGES
//#         abilities.append(", LAST_MESSAGES");
//#endif
//#ifdef CHECK_VERSION
//#         abilities.append(", CHECK_VERSION");
//#endif
//#ifdef TRANSLIT
        abilities.append(", TRANSLIT");
//#endif
//#ifdef WMUC
//#         abilities.append(", WMUC");
//#endif
//#ifdef AUTODELETE
//#         abilities.append(", AUTODELETE");
//#endif
//#ifdef WSYSTEMGC
//#         abilities.append(", WSYSTEMGC");
//#endif
//#ifdef NICK_COLORS
        abilities.append(", NICK_COLORS");
//#endif
//#ifdef IMPORT_EXPORT
//#         abilities.append(", IMPORT_EXPORT");
//#endif
//#ifdef SECONDSTRING
//#         abilities.append(", SECONDSTRING");
//#endif
//#ifdef CHANGE_TRANSPORT
//#         abilities.append(", CHANGE_TRANSPORT");
//#endif
//#ifdef CONSOLE
//#         abilities.append(", CONSOLE");
//#endif
//#ifdef CLIPBOARD
//#         abilities.append(", CLIPBOARD");
//#endif
//#ifdef GRADIENT
//#         abilities.append(", GRADIENT");
//#endif
//#ifdef PEP
//#         abilities.append(", PEP");
//#endif
//#ifdef PEP_TUNE
//#         abilities.append(", PEP_TUNE");
//#endif
//#ifdef CAPTCHA
//#         abilities.append(", CAPTCHA");
//#endif
//#ifdef STATS
//#         abilities.append(", STATS");
//#endif
        return abilities.toString();
    }
}
