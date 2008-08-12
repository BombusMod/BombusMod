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
//#ifndef MENU_LISTENER
import javax.microedition.lcdui.Command;
//#else
//# import Menu.Command;
//#endif
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
    MultiLine name;
    MultiLine memory;
    MultiLine abilities;
    
//#ifdef CLIPBOARD
//#ifndef MENU
//#     public Command cmdOk = new Command(SR.MS_COPY, Command.OK, 1);
//#endif
//#     private ClipBoard clipboard; 
//#endif
    
    /**
     * Creates a new instance of InfoWindow
     */
    public InfoWindow(Display display, Displayable pView) {
        super(display, pView, SR.MS_ABOUT);
        this.display=display;

        name=new MultiLine(Version.getName(), Version.getVersionNumber(), super.superWidth);
        name.selectable=true;
        itemsList.addElement(name);

        description=new MultiLine("Mobile Jabber client", Config.getOs()+"\nCopyright (c) 2005-2008, Eugene Stahov (evgs), Daniel Apatin (ad)\nDistributed under GNU Public License (GPL) v2.0", super.superWidth);
        description.selectable=true;
        itemsList.addElement(description);
        
        siteUrl=new LinkString("http://bombusmod.net.ru"){ public void doAction() { try { BombusMod.getInstance().platformRequest("http://bombusmod.net.ru"); } catch (ConnectionNotFoundException ex) { }}};
        itemsList.addElement(siteUrl);
        
        StringBuffer memInfo=new StringBuffer(SR.MS_FREE);
        System.gc();
        memInfo.append(Runtime.getRuntime().freeMemory()>>10)
               .append("\n")
               .append(SR.MS_TOTAL)
               .append(Runtime.getRuntime().totalMemory()>>10);
        memory=new MultiLine(SR.MS_MEMORY, memInfo.toString(), super.superWidth);
        memory.selectable=true;
        itemsList.addElement(memory);
        
        abilities=new MultiLine("Abilities", getAbilities(), super.superWidth);
        abilities.selectable=true;
        itemsList.addElement(abilities);
//#ifdef CLIPBOARD
//#         if (Config.getInstance().useClipBoard) {
//#             clipboard=ClipBoard.getInstance(); 
//#         }
//#endif
        
//#ifndef MENU
        super.removeCommand(super.cmdOk);
//#ifdef CLIPBOARD
//#         if (Config.getInstance().useClipBoard) {
//#             addCommand(cmdOk);
//#         }
//#endif
//#endif
        enableListWrapping(false);
        
        moveCursorTo(0);
        attachDisplay(display);
        this.parentView=pView;
    }
    public void cmdOk(){
//#ifdef CLIPBOARD
//#         clipboard.setClipBoard(name.toString()+"\n"+memory.toString()+"\n"+abilities.toString());
//#         destroyView();
//#endif
    }
//#ifdef MENU
//#ifdef CLIPBOARD
//#     public String getLeftCommand() { return SR.MS_COPY; }
//#endif
//#     public String getRightCommand() { return SR.MS_BACK; }
//#else
    public void commandAction(Command command, Displayable displayable) {
	if (command==cmdOk) {
	    cmdOk();
	}
        super.commandAction(command, displayable);
    }
//#endif
    
    private String getAbilities() {
        StringBuffer abilities=new StringBuffer();
        abilities.append("")
//#ifdef ADHOC
//#         .append("ADHOC, ")
//#endif
//#ifdef ANTISPAM
//#         .append("ANTISPAM, ")
//#endif
//#ifdef ARCHIVE
        .append("ARCHIVE, ")
//#endif
//#ifdef AUTOSTATUS
//#         .append("AUTOSTATUS, ")
//#endif
//#ifdef CAPTCHA
//#         .append("CAPTCHA, ")
//#endif
//#ifdef CHANGE_TRANSPORT
//#         .append("CHANGE_TRANSPORT, ")
//#endif
//#ifdef CHECK_VERSION
//#         .append("CHECK_VERSION, ")
//#endif
//#ifdef CLIENTS_ICONS
//#         .append("CLIENTS_ICONS, ")
//#endif
//#ifdef CLIPBOARD
//#         .append("CLIPBOARD, ")
//#endif
//#ifdef CONSOLE
//#         .append("CONSOLE, ")
//#endif
//#ifdef COLOR_TUNE
//#         .append("COLOR_TUNE, ")
//#endif
//#ifdef DETRANSLIT
//#         .append("DETRANSLIT, ")
//#endif
//#ifdef ELF
//#         .append("ELF, ")
//#endif
//#ifdef FILE_TRANSFER
        .append("FILE_TRANSFER, ")
//#endif
//#ifdef GRADIENT
//#         .append("GRADIENT, ")
//#endif
//#ifdef HISTORY
//#         .append("HISTORY, ")
//#endif
//#ifdef HISTORY_READER
//#         .append("HISTORY_READER, ")
//#endif
//#ifdef IMPORT_EXPORT
//#         .append("IMPORT_EXPORT, ")
//#endif
//#ifdef LAST_MESSAGES
//#         .append("LAST_MESSAGES, ")
//#endif
//#ifdef LOGROTATE
//#         .append("LOGROTATE, ")
//#endif
//#ifdef MENU_LISTENER
//#         .append("MENU_LISTENER, ")
//#endif
//#ifdef NEW_SKIN
//#         .append("NEW_SKIN, ")
//#endif
//#ifdef NICK_COLORS
        .append("NICK_COLORS, ")
//#endif
//#ifdef PEP
//#         .append("PEP, ")
//#endif
//#ifdef PEP_TUNE
//#         .append("PEP_TUNE, ")
//#endif
//#ifdef POPUPS
        .append("POPUPS, ")
//#endif
//#ifdef REQUEST_VOICE
//#         .append("REQUEST_VOICE, ")
//#endif
//#ifdef PRIVACY
        .append("PRIVACY, ")
//#endif
//#ifdef SE_LIGHT
//#         .append("SE_LIGHT, ")
//#endif
//#ifdef SERVICE_DISCOVERY
        .append("SERVICE_DISCOVERY, ")
//#endif
//#ifdef SMILES
        .append("SMILES, ")
//#endif
//#ifdef STATS
//#         .append("STATS, ")
//#endif
//#ifdef TEMPLATES
        .append("TEMPLATES, ")
//#endif
//#ifdef USER_KEYS
//#         .append("USER_KEYS, ")
//#endif
//#ifdef USE_ROTATOR
        .append("USE_ROTATOR, ")
//#endif
//#ifdef WMUC
//#         .append("WMUC, ")
//#endif
//#ifdef WSYSTEMGC
//#         .append("WSYSTEMGC, ")
//#endif
        .append("");
        return abilities.toString();
    }
}
