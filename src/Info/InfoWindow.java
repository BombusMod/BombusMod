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
import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.io.ConnectionNotFoundException;
import locale.SR;
import midlet.BombusMod;
import ui.controls.form.DefForm;
import ui.controls.form.LinkString;
import ui.controls.form.MultiLine;
import ui.controls.form.SpacerItem;
import Menu.MenuCommand;
import ui.VirtualList;
import images.RosterIcons;

/**
 *
 * @author ad
 */
public class InfoWindow
        extends DefForm {

    LinkString siteUrl;
    MultiLine description;
    MultiLine name;
    MultiLine memory;
    MultiLine abilities;

//#ifdef CLIPBOARD
//#     MenuCommand cmdCopy      = new MenuCommand(SR.MS_COPY, MenuCommand.OK, 1, RosterIcons.ICON_COPY);
//#     MenuCommand cmdCopyPlus  = new MenuCommand("+ "+SR.MS_COPY, MenuCommand.SCREEN, 2, RosterIcons.ICON_COPYPLUS);
//#endif

    /**
     * Creates a new instance of InfoWindow
     */
    public InfoWindow() {
        super(SR.MS_ABOUT);
        
        name = new MultiLine(Version.getName(), Version.getVersionNumber() + "\n" + Config.getOs() + "\nMobile Jabber client");
        name.selectable = true;
        itemsList.addElement(name);

        description = new MultiLine("Copyright (c) 2005-2011", "Eugene Stahov (evgs),\nDaniel Apatin (ad)\n \nDistributed under GNU Public License (GPL) v2.0");
        description.selectable = true;
        itemsList.addElement(description);

        siteUrl = new LinkString("http://bombusmod.net.ru") {

            public void doAction() {
                try {
                    BombusMod.getInstance().platformRequest("http://bombusmod.net.ru");
                } catch (ConnectionNotFoundException ex) {
                }
            }
        };
        itemsList.addElement(siteUrl);

        itemsList.addElement(new SpacerItem(20));

        abilities = new MultiLine("Special thanks", "Advice, aspro, BrennendeR_Komet, 6yp4uk, den_po, Disabler, fregl24, G.L.Fire, gimlet, lgs, m, MaSy, Muxa, NoNameZ, radiance, Sash, spine, spirtamne, Tasha, TiLan, Totktonada, van, vitalyster, voffk, westsibe, zet. \n \nWithout you none of this would not have been!");
        abilities.selectable = true;
        itemsList.addElement(abilities);

        itemsList.addElement(new SpacerItem(20));

        StringBuffer memInfo = new StringBuffer(SR.MS_FREE);
//        if (Config.getInstance().widthSystemgc) { _vt
        System.gc();
//        } _vt
        memInfo.append(Runtime.getRuntime().freeMemory() >> 10).append("\n").append(SR.MS_TOTAL).append(Runtime.getRuntime().totalMemory() >> 10);
        memory = new MultiLine(SR.MS_MEMORY, memInfo.toString());
        memory.selectable = true;
        itemsList.addElement(memory);        

        itemsList.addElement(new SpacerItem(10));

        abilities = new MultiLine("Abilities", getAbilities());
        abilities.selectable = true;
        itemsList.addElement(abilities);
    }

    public void commandState() {
        menuCommands.removeAllElements();        
//#ifdef CLIPBOARD
//#             if (Config.getInstance().useClipBoard) {                
//#                 addMenuCommand(cmdCopy);
//#                 if (!sd.clipboard.isEmpty())
//#                     addMenuCommand(cmdCopyPlus);
//#             }
//#endif        
    }

    public void menuAction(MenuCommand command, VirtualList displayable) {
//#ifdef CLIPBOARD
//#         if (command == cmdCopy) {
//#             try {
//#                 String str = ((MultiLine) getFocusedObject()).toString();
//#                 if (str == null)
//#                     str = "";
//#                 sd.clipboard.setClipBoard(str);
//#             } catch (Exception e) {}
//#         }
//# 
//#         if (command == cmdCopyPlus) {
//#             try {
//#                 String str = ((MultiLine) getFocusedObject()).toString();
//#                 if (str == null)
//#                     str = "";
//#                 sd.clipboard.append(str);
//#             } catch (Exception e) {}
//#         }
//#endif
        super.menuAction(command, displayable);
    }

    public void cmdOk() {
        destroyView();
    }
    
    private String getAbilities() {
        Vector abilitiesList=new Vector();
//#ifdef ADHOC
//#         abilitiesList.addElement("ADHOC");
//#endif
//#ifdef ANI_SMILES
//#         abilitiesList.addElement("ANI_SMILES");
//#endif
//#ifdef ARCHIVE
//#         abilitiesList.addElement("ARCHIVE");
//#endif
//#ifdef AUTOSTATUS
//#         abilitiesList.addElement("AUTOSTATUS");
//#endif
//#ifdef AUTOTASK
//#         abilitiesList.addElement("AUTOTASK");
//#endif
//#ifdef BACK_IMAGE
//#         abilitiesList.addElement("BACK_IMAGE");
//#endif
//#ifdef CAPTCHA
//#         abilitiesList.addElement("CAPTCHA");
//#endif
//#ifdef CHANGE_TRANSPORT
//#         abilitiesList.addElement("CHANGE_TRANSPORT");
//#endif
//#ifdef CHECK_VERSION
//#         abilitiesList.addElement("CHECK_VERSION");
//#endif
//#ifdef CLIENTS_ICONS
//#         abilitiesList.addElement("CLIENTS_ICONS");
//#endif
//#ifdef CLIPBOARD
//#         abilitiesList.addElement("CLIPBOARD");
//#endif
//#ifdef COLOR_TUNE
//#         abilitiesList.addElement("COLOR_TUNE");
//#endif
//#ifdef CONSOLE
//#         abilitiesList.addElement("CONSOLE");
//#endif
//#ifdef DEBUG
//#         abilitiesList.addElement("DEBUG");
//#endif
//#ifdef DETRANSLIT
//#         abilitiesList.addElement("DETRANSLIT");
//#endif
//#ifdef ELF
//#         abilitiesList.addElement("ELF");
//#endif
//#ifdef FILE_IO
//#         abilitiesList.addElement("FILE_IO");
//#endif
//#ifdef FILE_TRANSFER
//#         abilitiesList.addElement("FILE_TRANSFER");
//#endif
//#ifdef GRADIENT
//#         abilitiesList.addElement("GRADIENT");
//#endif
//#ifdef HISTORY
//#         abilitiesList.addElement("HISTORY");
//#endif
//#ifdef HISTORY_READER
//#         abilitiesList.addElement("HISTORY_READER");
//#endif
//#ifdef HTTPBIND
//#         abilitiesList.addElement("HTTPBIND");
//#endif
//#ifdef HTTPCONNECT
//#         abilitiesList.addElement("HTTPCONNECT");
//#endif
//#ifdef HTTPPOLL
//#         abilitiesList.addElement("HTTPPOLL");
//#endif
//#ifdef IMPORT_EXPORT
//#         abilitiesList.addElement("IMPORT_EXPORT");
//#endif
//#ifdef JUICK
//#         abilitiesList.addElement("JUICK");
//#endif
//#ifdef LANG_DEBUG
//#         abilitiesList.addElement("LANG_DEBUG");
//#endif
//#ifdef LAST_MESSAGES
//#         abilitiesList.addElement("LAST_MESSAGES");
//#endif
//#ifdef LIGHT_CONFIG
//#         abilitiesList.addElement("LIGHT_CONFIG");
//#endif  
//#ifdef LOGROTATE
//#         abilitiesList.addElement("LOGROTATE");
//#endif
//#ifdef MEMORY_USAGE
//#         abilitiesList.addElement("MEMORY_USAGE");
//#endif
//#ifdef MIDP_TICKER
//#         abilitiesList.addElement("MIDP_TICKER");
//#endif
//#ifdef NICK_COLORS
//#         abilitiesList.addElement("NICK_COLORS");
//#endif
//#ifdef NOMMEDIA
//#         abilitiesList.addElement("NOMMEDIA");
//#endif
//#ifdef NON_SASL_AUTH
//#         abilitiesList.addElement("NON_SASL_AUTH");
//#endif
//#ifdef PEP
//#         abilitiesList.addElement("PEP");
//#endif
//#ifdef PEP_ACTIVITY
//#         abilitiesList.addElement("PEP_ACTIVITY");
//#endif
//#ifdef PEP_LOCATION
//#         abilitiesList.addElement("PEP_LOCATION");
//#endif
//#ifdef PEP_TUNE
//#         abilitiesList.addElement("PEP_TUNE");
//#endif
//#ifdef POPUPS
//#         abilitiesList.addElement("POPUPS");
//#endif
//#ifdef PRIVACY
//#         abilitiesList.addElement("PRIVACY");
//#endif
//#ifdef REQUEST_VOICE
//#         abilitiesList.addElement("REQUEST_VOICE");
//#endif
//#ifdef RUNNING_MESSAGE
//#         abilitiesList.addElement("RUNNING_MESSAGE");
//#endif
//#ifdef SASL_XGOOGLETOKEN
//#         abilitiesList.addElement("SASL_XGOOGLETOKEN");
//#endif
//#ifdef SERVICE_DISCOVERY
//#         abilitiesList.addElement("SERVICE_DISCOVERY");
//#endif
//#ifdef SE_LIGHT
//#         abilitiesList.addElement("SE_LIGHT");
//#endif
//#ifdef SMILES
//#         abilitiesList.addElement("SMILES");
//#endif
//#ifdef STATS
//#         abilitiesList.addElement("STATS");
//#endif
//#ifdef SYSTEM_NOTIFY
//#         abilitiesList.addElement("SYSTEM_NOTIFY");
//#endif
//#ifdef TEMPLATES
//#         abilitiesList.addElement("TEMPLATES");
//#endif
//#ifdef TLS        
//#         abilitiesList.addElement("TLS");
//#endif        
//#ifdef USER_KEYS
//#         abilitiesList.addElement("USER_KEYS");
//#endif
//#ifdef USE_ROTATOR
//#         abilitiesList.addElement("USE_ROTATOR");
//#endif
//#ifdef WMUC
//#         abilitiesList.addElement("WMUC");
//#endif
//#ifdef XML_STREAM_DEBUG
//#         abilitiesList.addElement("XML_STREAM_DEBUG");
//#endif
//#ifdef ZLIB
//#         abilitiesList.addElement("ZLIB");
//#endif

        StringBuffer ablist=new StringBuffer();

	for (Enumeration ability=abilitiesList.elements(); ability.hasMoreElements(); ) {
            ablist.append((String)ability.nextElement());
            ablist.append(", ");
	}
        String ab=ablist.toString();
        ablist=null;
        abilitiesList=null;
        return ab.substring(0, ab.length()-2);
    }
}
