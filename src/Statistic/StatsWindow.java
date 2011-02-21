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
import Menu.MenuCommand;
import locale.SR;
import ui.VirtualList;
import ui.controls.form.DefForm;
import ui.controls.form.MultiLine;
//#ifdef CLIPBOARD
//# import util.ClipBoard;
//#endif
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
    
    public MenuCommand cmdClear = new MenuCommand(SR.MS_CLEAR, MenuCommand.SCREEN, 2);
//#ifdef CLIPBOARD
//#     ClipBoard clipboard = null;
//#     MenuCommand cmdCopy      = new MenuCommand(SR.MS_COPY, MenuCommand.SCREEN, 1);
//#     MenuCommand cmdCopyPlus  = new MenuCommand("+ "+SR.MS_COPY, MenuCommand.SCREEN, 2);
//#endif
    
    MultiLine item=null;    

    /**
     * Creates a new instance of StatsWindow
     */
    public StatsWindow() {
        super(SR.MS_STATS);
        item=new MultiLine(SR.MS_ALL, StringUtils.getSizeString(st.getAllTraffic()), sd.roster.getListWidth()); item.selectable=true; itemsList.addElement(item);

        item=new MultiLine(SR.MS_PREVIOUS_, StringUtils.getSizeString(st.getLatest()), sd.roster.getListWidth()); item.selectable=true; itemsList.addElement(item);
        
        item=new MultiLine(SR.MS_CURRENT, StringUtils.getSizeString(Stats.getCurrentTraffic()), sd.roster.getListWidth()); item.selectable=true; itemsList.addElement(item);
//#if ZLIB
        if (StaticData.getInstance().roster.isLoggedIn()) {
            item=new MultiLine(SR.MS_COMPRESSION, StaticData.getInstance().roster.theStream.getStreamStats(), sd.roster.getListWidth()); item.selectable=true; itemsList.addElement(item);
        }

        if (StaticData.getInstance().roster.isLoggedIn()) {
            item=new MultiLine(SR.MS_CONNECTED, StaticData.getInstance().roster.theStream.getConnectionData(), sd.roster.getListWidth()); item.selectable=true; itemsList.addElement(item);
        }
//#endif
        item=new MultiLine(SR.MS_CONN, Integer.toString(st.getSessionsCount()), sd.roster.getListWidth()); item.selectable=true; itemsList.addElement(item);
                
        item=new MultiLine(SR.MS_STARTED, Roster.startTime, sd.roster.getListWidth()); item.selectable=true; itemsList.addElement(item);
    }

    public void commandState() {
        menuCommands.removeAllElements();
//#ifdef CLIPBOARD
//#             if (Config.getInstance().useClipBoard) {
//#                 clipboard = ClipBoard.getInstance();
//#                 addMenuCommand(cmdCopy);
//#                 if (!clipboard.isEmpty())
//#                     addMenuCommand(cmdCopyPlus);
//#             }
//#endif        
        addMenuCommand(cmdClear);
    }

    public void menuAction(MenuCommand command, VirtualList displayable) {
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
        }
        super.menuAction(command, displayable);
    }
}
