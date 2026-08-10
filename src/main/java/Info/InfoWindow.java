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
import locale.SR;
import midlet.BombusMod;
import org.bombusmod.util.VersionInfo;
import ui.NativeScreenCommand;
import ui.NativeScreenItem;
import ui.NativeScreenModel;
import ui.VirtualListController;
import ui.controls.form.DefForm;
import ui.controls.form.LinkString;
import ui.controls.form.MultiLine;
import ui.controls.form.SpacerItem;
import Menu.MenuCommand;
import ui.VirtualList;
import images.RosterIcons;

import org.bombusmod.util.ClipBoardIO;

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
    MenuCommand cmdCopy      = new MenuCommand(SR.MS_COPY, MenuCommand.OK, 1, RosterIcons.ICON_COPY);
    MenuCommand cmdCopyPlus  = new MenuCommand("+ "+SR.MS_COPY, MenuCommand.SCREEN, 2, RosterIcons.ICON_COPYPLUS);
//#endif

    /**
     * Creates a new instance of InfoWindow
     */
    public InfoWindow() {
        super(SR.MS_ABOUT);
        if (!buildNativeModel()) {  // native UI active
            VirtualListController.getInstance().notifyUpdate();
            return;
        }

        name = new MultiLine(StaticData.getInstance().getVersionInfo().getName(), StaticData.getInstance().getVersionInfo().getVersionNumber() + "\n" + Config.getOs() + "\nMobile Jabber client");
        name.selectable = true;
        itemsList.addElement(name);

        description = new MultiLine("Copyright (c) 2005-2020", "Eugene Stahov (evgs),\nDaniel Apatin (ad)\n \nDistributed under GNU Public License (GPL) v2.0");
        description.selectable = true;
        itemsList.addElement(description);

        siteUrl = new LinkString(StaticData.getInstance().getVersionInfo().getUrl()) {

            public void doAction() {
                BombusMod.getInstance().platformRequest(StaticData.getInstance().getVersionInfo().getUrl());
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
        addMenuCommand(cmdCopy);
        if (!ClipBoardIO.getInstance().isEmpty()) {
            addMenuCommand(cmdCopyPlus);
        }

//#endif        
    }

    public void menuAction(MenuCommand command, VirtualList displayable) {
//#ifdef CLIPBOARD
        if (command == cmdCopy) {
            try {
                String str = ((MultiLine) getFocusedObject()).toString();
                if (str == null)
                    str = "";
                ClipBoardIO.getInstance().setClipBoard(str);
            } catch (Exception e) {}
        }

        if (command == cmdCopyPlus) {
            try {
                String str = ((MultiLine) getFocusedObject()).toString();
                if (str == null)
                    str = "";
                ClipBoardIO.getInstance().append(str);
            } catch (Exception e) {}
        }
//#endif
        super.menuAction(command, displayable);
    }

    public void cmdOk() {
        destroyView();
    }
    
    private String getAbilities() {
        Vector abilitiesList=new Vector();
//#ifdef ANI_SMILES
        abilitiesList.addElement("ANI_SMILES");
//#endif
//#ifdef ARCHIVE
        abilitiesList.addElement("ARCHIVE");
//#endif
//#ifdef CAPTCHA
        abilitiesList.addElement("CAPTCHA");
//#endif
//#ifdef CLIENTS_ICONS
        abilitiesList.addElement("CLIENTS_ICONS");
//#endif
//#ifdef CLIPBOARD
        abilitiesList.addElement("CLIPBOARD");
//#endif
//#ifdef COLOR_TUNE
        abilitiesList.addElement("COLOR_TUNE");
//#endif
//#ifdef CONSOLE
        abilitiesList.addElement("CONSOLE");
//#endif
        if (StaticData.Debug)
            abilitiesList.addElement("DEBUG");
//#ifdef DETRANSLIT
        abilitiesList.addElement("DETRANSLIT");
//#endif
//#ifdef FILE_IO
        abilitiesList.addElement("FILE_IO");
//#endif
//#ifdef FILE_TRANSFER
        abilitiesList.addElement("FILE_TRANSFER");
//#endif
//#ifdef GRADIENT
        abilitiesList.addElement("GRADIENT");
//#endif
//#ifdef HISTORY
        abilitiesList.addElement("HISTORY");
//#endif
//#ifdef HISTORY_READER
        abilitiesList.addElement("HISTORY_READER");
//#endif
//#ifdef HTTPBIND
//#         abilitiesList.addElement("HTTPBIND");
//#endif
        abilitiesList.addElement("HTTPCONNECT");
//#ifdef HTTPPOLL
//#         abilitiesList.addElement("HTTPPOLL");
//#endif
//#ifdef IMPORT_EXPORT
        abilitiesList.addElement("IMPORT_EXPORT");
//#endif
//#ifdef JUICK
        abilitiesList.addElement("JUICK");
//#endif
//#ifdef LANG_DEBUG
//#         abilitiesList.addElement("LANG_DEBUG");
//#endif
//#ifdef LAST_MESSAGES
        abilitiesList.addElement("LAST_MESSAGES");
//#endif
//#ifdef LIGHT_CONFIG
//#         abilitiesList.addElement("LIGHT_CONFIG");
//#endif  
//#ifdef LOGROTATE
        abilitiesList.addElement("LOGROTATE");
//#endif
//#ifdef MIDP_TICKER
//#         abilitiesList.addElement("MIDP_TICKER");
//#endif
//#ifdef NICK_COLORS
        abilitiesList.addElement("NICK_COLORS");
//#endif
//#ifdef NOMMEDIA
        abilitiesList.addElement("NOMMEDIA");
//#endif
        if (StaticData.NonSaslAuth) {
            abilitiesList.addElement("NON_SASL_AUTH");
        }
//#ifdef PEP
        abilitiesList.addElement("PEP");
//#endif
//#ifdef PEP_ACTIVITY
        abilitiesList.addElement("PEP_ACTIVITY");
//#endif
//#ifdef PEP_LOCATION
        abilitiesList.addElement("PEP_LOCATION");
//#endif
//#ifdef PEP_TUNE
        abilitiesList.addElement("PEP_TUNE");
//#endif
//#ifdef PRIVACY
        abilitiesList.addElement("PRIVACY");
//#endif
//#ifdef REQUEST_VOICE
//#         abilitiesList.addElement("REQUEST_VOICE");
//#endif
//#ifdef RUNNING_MESSAGE
//#         abilitiesList.addElement("RUNNING_MESSAGE");
//#endif
//#ifdef SERVICE_DISCOVERY
        abilitiesList.addElement("SERVICE_DISCOVERY");
//#endif
//#ifdef SE_LIGHT
//#         abilitiesList.addElement("SE_LIGHT");
//#endif
//#ifdef SMILES
        abilitiesList.addElement("SMILES");
//#endif
//#ifdef STATS
        abilitiesList.addElement("STATS");
//#endif
//#ifdef TEMPLATES
        abilitiesList.addElement("TEMPLATES");
//#endif
//#ifdef TLS        
        abilitiesList.addElement("TLS");
//#endif        
//#ifdef USER_KEYS
        abilitiesList.addElement("USER_KEYS");
//#endif
//#ifdef USE_ROTATOR
        abilitiesList.addElement("USE_ROTATOR");
//#endif
//#ifdef WMUC
//#         abilitiesList.addElement("WMUC");
//#endif
        if (StaticData.XmlDebug) {
            abilitiesList.addElement("XML_STREAM_DEBUG");
        }
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

    /** @return true if legacy J2ME items should be built, false if native UI is active */
    private boolean buildNativeModel() {
        if (!VirtualListController.getInstance().isActive()) return true;
        NativeScreenModel model = new NativeScreenModel();
        VersionInfo vi = StaticData.getInstance().getVersionInfo();

        // App name + version
        NativeScreenItem nameItem = model.createNewItem(false);
        nameItem.setAsMultiline(vi.getName() + "\n" + vi.getVersionNumber()
                + "\n" + Config.getOs() + "\nMobile Jabber client");
        model.addPar(nameItem);

        // Copyright
        NativeScreenItem copyItem = model.createNewItem(false);
        copyItem.setAsMultiline("Copyright (c) 2005-2020\nEugene Stahov (evgs),"
                + "\nDaniel Apatin (ad)\n \nDistributed under GNU Public License (GPL) v2.0");
        model.addPar(copyItem);

        // Website link
        NativeScreenItem linkItem = model.createNewItem(true);
        linkItem.setAsLink(vi.getUrl());
        linkItem.description = vi.getUrl();
        model.addPar(linkItem);

        model.addPar(new NativeScreenItem() {{ setAsSpacer(20); }});

        // Memory
        System.gc();
        NativeScreenItem memItem = model.createNewItem(false);
        memItem.setAsMultiline("Memory\nFree: " + (Runtime.getRuntime().freeMemory() >> 10)
                + "K\nTotal: " + (Runtime.getRuntime().totalMemory() >> 10) + "K");
        model.addPar(memItem);

        model.addPar(new NativeScreenItem() {{ setAsSpacer(10); }});

        // Abilities
        NativeScreenItem abItem = model.createNewItem(false);
        abItem.setAsMultiline("Abilities\n" + getAbilities());
        model.addPar(abItem);

        // Commands
        model.addCommand("ok", "OK", NativeScreenCommand.OK, -1);
        model.addCommand("copy", "Copy", NativeScreenCommand.SCREEN, -1);
        model.addCommand("copy_plus", "+ Copy", NativeScreenCommand.SCREEN, -1);

        final InfoWindow self = this;
        VirtualListController.getInstance().setOnDismiss(new Runnable() {
            public void run() { self.dismissNative(); }
        });
        VirtualListController.getInstance().setCaption(SR.MS_ABOUT);
        VirtualListController.getInstance().setModel(model);
        return false; // native UI active, skip J2ME items
    }

    public void show() {
        if (VirtualListController.getInstance().isActive()
                && VirtualListController.getInstance().getModel() != null) {
            VirtualListController.getInstance().notifyUpdate();
            // Dismiss the J2ME screen — native UI is showing instead
            // parentView was saved before this screen pushed
        } else {
            super.show();
        }
    }

    /** Dismiss native UI and return to the legacy CanvasView */
    public void dismissNative() {
        VirtualListController.getInstance().setModel(null);
        VirtualListController.getInstance().notifyUpdate();
        destroyView();
    }

    public void destroyView() {
        VirtualListController.getInstance().setModel(null);
        VirtualListController.getInstance().notifyUpdate();
        super.destroyView();
    }
}
