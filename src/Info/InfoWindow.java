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
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import locale.SR;
import midlet.BombusMod;
import ui.controls.form.DefForm;
import ui.controls.form.LinkString;
import ui.controls.form.MultiLine;
import ui.controls.form.SpacerItem;
//#ifdef CLIPBOARD
//# import util.ClipBoard;
//#endif

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
    
//#ifdef CLIPBOARD
//#     private Command cmdCopy   = new Command(SR.MS_COPY, Command.OK, 1);
//#     private ClipBoard clipboard; 
//#endif
    
    /**
     * Creates a new instance of InfoWindow
     */
    public InfoWindow(Display display) {
        super(display, SR.MS_ABOUT);
        this.display=display;
        parentView=display.getCurrent();

        description=new MultiLine(Version.getName(), Version.getVersionNumber()+"\nMobile Jabber client\n"+Config.getOs()+"\nCopyright (c) 2005-2008, Eugene Stahov (evgs), Daniel Apatin (ad)\nDistributed under GNU Public License (GPL) v2.0", super.superWidth);
        description.selectable=true;
        itemsList.addElement(description);
        itemsList.addElement(new SpacerItem(0));
        
        siteUrl=new LinkString("http://bombusmod.net.ru"){ public void doAction() { try { BombusMod.getInstance().platformRequest("http://bombusmod.net.ru"); } catch (ConnectionNotFoundException ex) { }}};
        itemsList.addElement(siteUrl);
        itemsList.addElement(new SpacerItem(0));
        
        StringBuffer memInfo=new StringBuffer(SR.MS_FREE);
        System.gc();
        memInfo.append(Runtime.getRuntime().freeMemory()>>10)
               .append("\n")
               .append(SR.MS_TOTAL)
               .append(Runtime.getRuntime().totalMemory()>>10);
        memory=new MultiLine(SR.MS_MEMORY, memInfo.toString(), super.superWidth);
        memory.selectable=true;
        itemsList.addElement(memory);
        itemsList.addElement(new SpacerItem(0));
        
        abilities=new MultiLine("Abilities", getAbilities(), super.superWidth);
        abilities.selectable=true;
        itemsList.addElement(abilities);
        
        super.removeCommand(cmdOk);
//#ifdef CLIPBOARD
//#         if (Config.getInstance().useClipBoard) {
//#             clipboard=ClipBoard.getInstance(); 
//#             addCommand(cmdCopy);
//#         }
//#endif

        enableListWrapping(false);
        
        moveCursorTo(getNextSelectableRef(-1));
        attachDisplay(display);
    }
    
    public void commandAction(Command c, Displayable d){
//#ifdef CLIPBOARD
//#         if (c==cmdCopy) {
//#             clipboard.setClipBoard(description.toString()+"\n"+memory.toString()+"\n"+abilities.toString());
//#             destroyView();
//#             return;
//#         }
//#endif
        super.commandAction(c, d);
    }

    private String getAbilities() {
        StringBuffer abilities=new StringBuffer();
        abilities.append("")
//#ifdef COLOR_TUNE
//#         .append("COLOR_TUNE, ")
//#endif
//#ifdef ARCHIVE
        .append("ARCHIVE, ")
//#endif
//#ifdef POPUPS
        .append("POPUPS, ")
//#endif
//#ifdef ELF
//#         .append("ELF, ")
//#endif
//#ifdef NEW_MENU
        .append("NEW_MENU, ")
//#endif
//#ifdef SERVICE_DISCOVERY
        .append("SERVICE_DISCOVERY, ")
//#endif
//#ifdef PRIVACY
        .append("PRIVACY, ")
//#endif
//#ifdef SMILES
        .append("SMILES, ")
//#endif
//#ifdef ANTISPAM
//#         .append("ANTISPAM, ")
//#endif
//#ifdef REQUEST_VOICE
//#         .append("REQUEST_VOICE, ")
//#endif
//#ifdef HISTORY
//#         .append("HISTORY, ")
//#endif
//#ifdef SE_LIGHT
//#         .append("SE_LIGHT, ")
//#endif
//#ifdef TEMPLATES
        .append("TEMPLATES, ")
//#endif
//#ifdef USER_KEYS
//#         .append("USER_KEYS, ")
//#endif
//#ifdef AUTOSTATUS
//#         .append("AUTOSTATUS, ")
//#endif
//#ifdef USE_ROTATOR
        .append("USE_ROTATOR, ")
//#endif
//#ifdef FILE_TRANSFER
        .append("FILE_TRANSFER, ")
//#endif
//#ifdef CHECK_VERSION
//#         .append("CHECK_VERSION, ")
//#endif
//#ifdef DETRANSLIT
//#         .append("DETRANSLIT, ")
//#endif
//#ifdef WMUC
//#         .append("WMUC, ")
//#endif
//#ifdef AUTODELETE
//#         .append("AUTODELETE, ")
//#endif
//#ifdef WSYSTEMGC
//#         .append("WSYSTEMGC, ")
//#endif
//#ifdef NICK_COLORS
        .append("NICK_COLORS, ")
//#endif
//#ifdef IMPORT_EXPORT
//#         .append("IMPORT_EXPORT, ")
//#endif
//#ifdef CHANGE_TRANSPORT
//#         .append("CHANGE_TRANSPORT, ")
//#endif
//#ifdef CONSOLE
//#         .append("CONSOLE, ")
//#endif
//#ifdef CLIPBOARD
//#         .append("CLIPBOARD, ")
//#endif
//#ifdef GRADIENT
//#         .append("GRADIENT, ")
//#endif
//#ifdef PEP
//#         .append("PEP, ")
//#endif
//#ifdef PEP_TUNE
//#         .append("PEP_TUNE, ")
//#endif
//#ifdef CAPTCHA
//#         .append("CAPTCHA, ")
//#endif
//#ifdef STATS
//#         .append("STATS, ")
//#endif
//#ifdef CLIENTS_ICONS
//#         .append("CLIENTS_ICONS, ")
//#endif
        .append("");
        return abilities.toString();
    }
}
