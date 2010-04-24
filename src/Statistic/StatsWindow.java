/*
 * StatsWindow.java
 *
 * Created on 03.10.2008, 19:42
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

package Statistic;

import Client.Config;
import Client.Roster;
import Client.StaticData;
//#ifndef MENU_LISTENER
//# import javax.microedition.lcdui.Command;
//#else
import Menu.Command;
import Menu.MyMenu;
//#endif
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import locale.SR;
import ui.controls.form.DefForm;
import ui.controls.form.MultiLine;
import util.ClipBoard;
import util.StringUtils;

/**
 *
 * @author ad
 */
public class StatsWindow
        extends DefForm {

//#ifdef PLUGINS
//#     public static String plugin = new String("PLUGIN_STATS");
//#endif
    
    Stats st=Stats.getInstance();
    
    public Command cmdClear = new Command(SR.MS_CLEAR, Command.SCREEN, 2);
//#ifdef CLIPBOARD
//#     ClipBoard clipboard  = ClipBoard.getInstance();
//#     Command cmdCopy      = new Command(SR.MS_COPY, Command.SCREEN, 1);
//#     Command cmdCopyPlus  = new Command("+ "+SR.MS_COPY, Command.SCREEN, 2);
//#endif
    
    MultiLine item=null;    

    /**
     * Creates a new instance of StatsWindow
     */
    public StatsWindow(Display display, Displayable pView) {
        super(display, pView, SR.MS_STATS);
        this.display=display;
        item=new MultiLine(SR.MS_ALL, StringUtils.getSizeString(st.getAllTraffic()), super.superWidth); item.selectable=true; itemsList.addElement(item);

        item=new MultiLine(SR.MS_PREVIOUS_, StringUtils.getSizeString(st.getLatest()), super.superWidth); item.selectable=true; itemsList.addElement(item);
        
        item=new MultiLine(SR.MS_CURRENT, StringUtils.getSizeString(Stats.getCurrentTraffic()), super.superWidth); item.selectable=true; itemsList.addElement(item);
//#if ZLIB
        if (StaticData.getInstance().roster.isLoggedIn()) {
            item=new MultiLine(SR.MS_COMPRESSION, StaticData.getInstance().roster.theStream.getStreamStats(), super.superWidth); item.selectable=true; itemsList.addElement(item);
        }

        if (StaticData.getInstance().roster.isLoggedIn()) {
            item=new MultiLine(SR.MS_CONNECTED, StaticData.getInstance().roster.theStream.getConnectionData(), super.superWidth); item.selectable=true; itemsList.addElement(item);
        }
//#endif
        item=new MultiLine(SR.MS_CONN, Integer.toString(st.getSessionsCount()), super.superWidth); item.selectable=true; itemsList.addElement(item);
                
        item=new MultiLine(SR.MS_STARTED, Roster.startTime, super.superWidth); item.selectable=true; itemsList.addElement(item);
        
        
//        removeCommand(cmdOk);
        //setCommandListener(this);
        commandStateTest();
        attachDisplay(display);
        this.parentView=pView;
    }

    public void commandStateTest() {
//#ifdef MENU_LISTENER
        menuCommands.removeAllElements();
//#endif
//#ifdef CLIPBOARD
//#             if (Config.getInstance().useClipBoard) {
//#                 addCommand(cmdCopy);
//#                 if (!clipboard.isEmpty())
//#                     addCommand(cmdCopyPlus);
//#             }
//#endif
        addCommand(cmdClear);
        addCommand(cmdCancel);
    }

//#ifdef MENU_LISTENER
    public String touchLeftCommand(){ return SR.MS_MENU; }
    public void touchLeftPressed(){ cmdOk(); }
    public void cmdOk() { showMenu(); }
//#endif

//#ifdef MENU_LISTENER
    public void showMenu() {
        commandStateTest();
        new MyMenu(display, parentView, this, "", null, menuCommands);
    }
//#endif

    public void commandAction(Command command, Displayable displayable) {
//#ifdef CLIPBOARD
//#         if (command == cmdCopy) {
//#             try {
//#                 String str = ((MultiLine) getFocusedObject()).toString();
//#                 if (str == null)
//#                     str = "";
//#                 clipboard.setClipBoard(str);
//#             } catch (Exception e) {/*no messages*/}
//#         }
//# 
//#         if (command == cmdCopyPlus) {
//#             try {
//#                 String str = ((MultiLine) getFocusedObject()).toString();
//#                 if (str == null)
//#                     str = "";
//#                 str  = clipboard.getClipBoard() + "\n\n" + str;
//# 
//#                 clipboard.setClipBoard(str);
//#             } catch (Exception e) {/*no messages*/}
//#         }
//#endif
        if (command==cmdClear) {
            st.saveToStorage(true);
            cmdCancel();
        } else super.commandAction(command, displayable);
    }
}
