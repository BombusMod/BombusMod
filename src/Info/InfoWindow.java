/*
 * InfoWindow.java
 *
 * Created on 6.09.2005, 22:21
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

package Info;
import Client.Config;
import Client.Roster;
import javax.microedition.lcdui.*;
import locale.SR;

/**
 *
 * @author EvgS
 */
public class InfoWindow implements CommandListener{

    private Display display;
    private Displayable parentView;
    
    private Form form;

    /** Creates a new instance of InfoWindow */
    public InfoWindow(Display display) {
        this.display=display;
        parentView=display.getCurrent();
        
        form=new Form(SR.MS_ABOUT);
        form.addCommand(new Command(SR.MS_CLOSE, Command.BACK, 99));

        form.append(Version.getNameVersion()+"\nMobile Jabber client\n");
        form.append(Config.getOs());
        form.append("\n");
        form.append("Copyright (c) 2005-2008, Eugene Stahov (evgs), Daniel Apatin (ad)");

        form.append("\n");
        form.append("\n");
        
        StringBuffer memInfo=new StringBuffer(SR.MS_MEMORY);
        memInfo.append("\n");
        memInfo.append(SR.MS_FREE);

        System.gc();
        memInfo.append(Runtime.getRuntime().freeMemory()>>10);
        memInfo.append("\n");
        memInfo.append(SR.MS_TOTAL);
        memInfo.append(Runtime.getRuntime().totalMemory()>>10);
        form.append(memInfo.toString());
        memInfo=null;
        
        form.append("\n");
        StringBuffer abilities=new StringBuffer("Abilities: ");
//#ifdef COLORS
        abilities.append(", COLORS");
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
        form.append(abilities.toString());
        abilities=null;
        
        form.append("\n");
        form.setCommandListener(this);
        display.setCurrent(form);
    }

    public void commandAction(Command c, Displayable d) {
        display.setCurrent(parentView);
    }
}
