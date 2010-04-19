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
import Client.StaticData;
import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import locale.SR;
import midlet.BombusMod;
import ui.controls.form.DefForm;
import ui.controls.form.LinkString;
import ui.controls.form.MultiLine;
import ui.controls.form.SpacerItem;
import util.ClipBoard;

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

    StaticData sd=StaticData.getInstance();

//#ifdef CLIPBOARD
//#     private ClipBoard clipboard=ClipBoard.getInstance();
//#endif

    /**
     * Creates a new instance of InfoWindow
     */
    public InfoWindow(Display display, Displayable pView) {
        super(display, pView, SR.MS_ABOUT);
        this.display = display;

        name = new MultiLine(Version.getName(), Version.getVersionNumber() + "\n" + Config.getOs() + "\nMobile Jabber client", super.superWidth);
        name.selectable = true;
        itemsList.addElement(name);

        description = new MultiLine("Copyright (c) 2005-2010", "Eugene Stahov (evgs),\nDaniel Apatin (ad)\n \nDistributed under GNU Public License (GPL) v2.0", super.superWidth);
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

        abilities = new MultiLine("Special thanks", "Advice, aspro, BrennendeR_Komet, 6yp4uk, den_po, disabler, fregl24, G.L.Fire, gimlet, lgs, m, Masy, Muxa, NoNameZ, radiance, Sash, spine, spirtamne, Tasha, TiLan, Totktonada, van, vitalyster, voffk, westsibe, zet. \nWithout you none of this would not have!", super.superWidth);
        abilities.selectable = true;
        itemsList.addElement(abilities);

        itemsList.addElement(new SpacerItem(20));

        StringBuffer memInfo = new StringBuffer(SR.MS_FREE);
//        if (Config.getInstance().widthSystemgc) { _vt
        System.gc();
//        } _vt
        memInfo.append(Runtime.getRuntime().freeMemory() >> 10).append("\n").append(SR.MS_TOTAL).append(Runtime.getRuntime().totalMemory() >> 10);
        memory = new MultiLine(SR.MS_MEMORY, memInfo.toString(), super.superWidth);
        memory.selectable = true;
        itemsList.addElement(memory);
        memInfo = null;

        itemsList.addElement(new SpacerItem(10));

        abilities = new MultiLine("Abilities", getAbilities(), super.superWidth);
        abilities.selectable = true;
        itemsList.addElement(abilities);
//#ifdef CLIPBOARD
//#         if (Config.getInstance().useClipBoard) {
//#             clipboard=ClipBoard.getInstance();
//#         }
//#endif
        attachDisplay(display);
        this.parentView = pView;
    }

//#ifdef CLIPBOARD
//#ifdef MENU_LISTENER
//#     public String touchLeftCommand(){ return Config.getInstance().useClipBoard ? SR.MS_COPY : SR.MS_OK; }
//#endif
//# 
//#     public void cmdOk(){
//#         if (Config.getInstance().useClipBoard) {
//#             clipboard.setClipBoard(name.toString()+"\n"+memory.toString()+"\n"+abilities.toString());
//#         }
//#         destroyView();
//#     }
//#endif


    private String getAbilities() {
        Vector abilitiesList=new Vector();
//#ifdef ADHOC
//#ifdef PLUGINS
//#         if (sd.Adhoc)
//#endif
//#             abilitiesList.addElement("ADHOC");
//#endif
//#ifdef ANTISPAM
//#             abilitiesList.addElement("ANTISPAM");
//#endif
//#ifdef ARCHIVE
//#ifdef PLUGINS
//#         if (sd.Archive)
//#endif
            abilitiesList.addElement("ARCHIVE");
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
//#ifdef PLUGINS
//#         if (sd.ChangeTransport)
//#endif
//#             abilitiesList.addElement("CHANGE_TRANSPORT");
//#endif
//#ifdef CHECK_VERSION
//#ifdef PLUGINS
//#         if (sd.Upgrade)
//#endif
//#             abilitiesList.addElement("CHECK_VERSION");
//#endif
//#ifdef CLIENTS_ICONS
//#ifdef PLUGINS
//#         if (sd.ClientsIcons)
//#endif
            abilitiesList.addElement("CLIENTS_ICONS");
//#endif
//#ifdef CLIPBOARD
//#         abilitiesList.addElement("CLIPBOARD");
//#endif
//#ifdef CONSOLE
//#ifdef PLUGINS
//#         if (sd.Console)
//#endif
//#             abilitiesList.addElement("CONSOLE");
//#endif
//#ifdef COLOR_TUNE
//#ifdef PLUGINS
//#         if (sd.Colors)
//#endif
//#             abilitiesList.addElement("COLOR_TUNE");
//#endif
//#ifdef DETRANSLIT
//#         abilitiesList.addElement("DETRANSLIT");
//#endif
//#ifdef ELF
//#         abilitiesList.addElement("ELF");
//#endif
//#ifdef FILE_IO
        abilitiesList.addElement("FILE_IO");
//#endif
//#ifdef FILE_TRANSFER
//#ifdef PLUGINS
//#         if (sd.FileTransfer)
//#endif
            abilitiesList.addElement("FILE_TRANSFER");
//#endif
//#ifdef GRADIENT
//#         abilitiesList.addElement("GRADIENT");
//#endif
//#ifdef HISTORY
//#ifdef PLUGINS
//#         if (sd.History)
//#endif
//#             abilitiesList.addElement("HISTORY");
//#endif
//#ifdef HISTORY_READER
//#ifdef PLUGINS
//#         if (sd.History)
//#endif
//#             abilitiesList.addElement("HISTORY_READER");
//#endif
//#ifdef HTTPCONNECT
//#         abilitiesList.addElement("HTTPCONNECT");
//#endif
//#ifdef HTTPPOLL
//#         abilitiesList.addElement("HTTPPOLL");
//#endif
//#ifdef IMPORT_EXPORT
//#ifdef PLUGINS
//#         if (sd.IE)
//#endif
//#             abilitiesList.addElement("IMPORT_EXPORT");
//#endif
//#ifdef JUICK
//#ifdef PLUGINS
//#         if (sd.Juick)
//#endif
//#         abilitiesList.addElement("JUICK");
//#endif
//#ifdef LAST_MESSAGES
//#ifdef PLUGINS
//#         if (sd.History)
//#endif
//#             abilitiesList.addElement("LAST_MESSAGES");
//#endif
//#ifdef LOGROTATE
//#         abilitiesList.addElement("LOGROTATE");
//#endif
//#ifdef MENU_LISTENER
        abilitiesList.addElement("MENU_LISTENER");
//#endif
//#ifdef NEW_SKIN
//#         abilitiesList.addElement("NEW_SKIN");
//#endif
//#ifdef NICK_COLORS
        abilitiesList.addElement("NICK_COLORS");
//#endif
//#ifdef NON_SASL_AUTH
//#         abilitiesList.addElement("NON_SASL_AUTH");
//#endif
//#ifdef PEP
//#ifdef PLUGINS
//#         if (sd.PEP)
//#endif
//#             abilitiesList.addElement("PEP");
//#endif
//#ifdef PEP_ACTIVITY
//#ifdef PLUGINS
//#         if (sd.PEP)
//#endif
//#             abilitiesList.addElement("PEP_ACTIVITY");
//#endif
//#ifdef PEP_LOCATION
//#ifdef PLUGINS
//#         if (sd.PEP)
//#endif
//#             abilitiesList.addElement("PEP_LOCATION");
//#endif
//#ifdef PEP_TUNE
//#ifdef PLUGINS
//#         if (sd.PEP)
//#endif
//#             abilitiesList.addElement("PEP_TUNE");
//#endif
//#ifdef PLUGINS
//#         abilitiesList.addElement("PLUGINS");
//#endif
//#ifdef POPUPS
        abilitiesList.addElement("POPUPS");
//#endif
//#ifdef REQUEST_VOICE
//#         abilitiesList.addElement("REQUEST_VOICE");
//#endif
//#ifdef RUNNING_MESSAGE
//#         abilitiesList.addElement("RUNNING_MESSAGE");
//#endif
//#ifdef PRIVACY
//#ifdef PLUGINS
//#         if (sd.Privacy)
//#endif
            abilitiesList.addElement("PRIVACY");
//#endif
//#ifdef SASL_XGOOGLETOKEN
//#         abilitiesList.addElement("SASL_XGOOGLETOKEN");
//#endif
//#ifdef SE_LIGHT
//#         abilitiesList.addElement("SE_LIGHT");
//#endif
//#ifdef SERVICE_DISCOVERY
        abilitiesList.addElement("SERVICE_DISCOVERY");
//#endif
//#ifdef SMILES
        abilitiesList.addElement("SMILES");
//#endif
//#ifdef STATS
//#ifdef PLUGINS
//#         if (sd.Stats)
//#endif
//#             abilitiesList.addElement("STATS");
//#endif
//#ifdef TEMPLATES
//#ifdef PLUGINS
//#         if (sd.Archive)
//#endif
//#         abilitiesList.addElement("TEMPLATES");
//#endif
//#ifdef USER_KEYS
//#         abilitiesList.addElement("USER_KEYS");
//#endif
//#ifdef USE_ROTATOR
        abilitiesList.addElement("USE_ROTATOR");
//#endif
//#ifdef WMUC
//#         abilitiesList.addElement("WMUC");
//#endif
//#ifdef ZLIB
        abilitiesList.addElement("ZLIB");
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
