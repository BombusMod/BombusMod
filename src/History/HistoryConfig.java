/*
 * HistoryConfig.java
 *
 * Created on 20.05.2008, 19:43
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

package History;

import Client.Config;
import Menu.MenuCommand;
import io.file.browse.Browser;
import io.file.browse.BrowserListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextField;
import locale.SR;
import ui.VirtualList;
import ui.controls.form.CheckBox;
import ui.controls.form.DefForm;
import ui.controls.form.LinkString;
import ui.controls.form.TextInput;

/**
 *
 * @author ad
 */
public class HistoryConfig 
        extends DefForm
        implements BrowserListener {
//#ifdef PLUGINS
//#     public static String plugin = new String("PLUGIN_HISTORY");
//#endif

    MenuCommand cmdPath=new MenuCommand(SR.MS_SELECT_HISTORY_FOLDER, MenuCommand.SCREEN, 2);

    private TextInput historyFolder;
    
    private CheckBox loadHistory;
    private CheckBox saveHistory;
    private CheckBox savePres;
    private CheckBox saveConfHistory;
    private CheckBox saveConfPres;
    private CheckBox win1251;
    private LinkString selectFolder;
//#ifdef DETRANSLIT
//#     private CheckBox translit;
//#endif
    
    Config cf;

    /** Creates a new instance of HistoryConfig */
    public HistoryConfig(Displayable pView) {
        super(SR.MS_HISTORY_OPTIONS);

        cf=Config.getInstance();
//#if HISTORY
//#ifdef LAST_MESSAGES
//#         loadHistory = new CheckBox(SR.MS_LAST_MESSAGES, cf.lastMessages); itemsList.addElement(loadHistory);
//#endif
//#         saveHistory = new CheckBox(SR.MS_SAVE_HISTORY, cf.msgLog); itemsList.addElement(saveHistory);
//#         savePres = new CheckBox(SR.MS_SAVE_PRESENCES, cf.msgLogPresence); itemsList.addElement(savePres);
//#         saveConfHistory = new CheckBox(SR.MS_SAVE_HISTORY_CONF, cf.msgLogConf); itemsList.addElement(saveConfHistory);
//#         saveConfPres = new CheckBox(SR.MS_SAVE_PRESENCES_CONF, cf.msgLogConfPresence); itemsList.addElement(saveConfPres);
//#         win1251 = new CheckBox(SR.MS_1251_CORRECTION, cf.cp1251); itemsList.addElement(win1251);
//#ifdef DETRANSLIT
//#         translit = new CheckBox(SR.MS_1251_TRANSLITERATE_FILENAMES, cf.transliterateFilenames); itemsList.addElement(translit);
//#endif
//# 
//# 	historyFolder = new TextInput(SR.MS_HISTORY_FOLDER, cf.msgPath, null, TextField.ANY); itemsList.addElement(historyFolder);
//#         selectFolder=new LinkString(SR.MS_SELECT_HISTORY_FOLDER) { public void doAction() { selectFolder(); } };
//#         itemsList.addElement(selectFolder);
//#endif
        moveCursorTo(0);        
        
    }

    public void BrowserFilePathNotify(String pathSelected) {
        historyFolder.setValue(pathSelected);
    }

    public void menuAction(MenuCommand command, VirtualList displayable) {
        if (command==cmdPath) {
            selectFolder();
            return;
        }
        super.menuAction(command, displayable);
        destroyView();
    }
    
    public void selectFolder() {
        new Browser(null, this, this, true);
    }

    public void cmdOk() {
//#if HISTORY
//#ifdef LAST_MESSAGES
//#         cf.lastMessages=loadHistory.getValue();
//#endif
//#         cf.msgLog=saveHistory.getValue();
//#         cf.msgLogPresence=savePres.getValue();
//#         cf.msgLogConf=saveConfHistory.getValue();
//#         cf.msgLogConfPresence=saveConfPres.getValue();
//#         cf.cp1251=win1251.getValue();
//#ifdef DETRANSLIT
//#         cf.transliterateFilenames=translit.getValue();
//#endif
//#         cf.msgPath=historyFolder.getValue();
//#endif
        cf.saveToStorage();
    }
    public void commandState() {
        super.commandState();
        addMenuCommand(cmdPath);        
    }
    
    public void touchLeftPressed(){ showMenu(); }

    public String touchLeftCommand() { return SR.MS_MENU; }
}
