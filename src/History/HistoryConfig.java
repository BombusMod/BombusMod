/*
 * HistoryConfig.java
 *
 * Created on 18.06.2007., 15:35
 * Copyright (c) 2006-2007, Daniel Apatin (ad), http://apatin.net.ru
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

package History;

import Client.Config;
import Client.StaticData;
//#if FILE_IO
import io.file.browse.Browser;
import io.file.browse.BrowserListener;
//#endif
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
//#if FILE_IO
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemCommandListener;
import javax.microedition.lcdui.TextField;
import ui.controls.TextFieldEx;
//#endif
import locale.SR;


public class HistoryConfig implements
	CommandListener 
//#if FILE_IO
	,ItemCommandListener
        , BrowserListener
//#endif
{
    private Display display;
    private Displayable parentView;

    Form f;
    ChoiceGroup message;
//#if FILE_IO
    ChoiceGroup history;
    TextField historyFolder;

    Command cmdSetHistFolder=new Command(SR.MS_SELECT_HISTORY_FOLDER, Command.ITEM,11);
//#endif
    
    Command cmdOk=new Command(SR.MS_OK,Command.OK,1);    
    Command cmdCancel=new Command(SR.MS_CANCEL, Command.BACK,99);
    
    Config cf;
    boolean mv[];
    boolean his[];
    
    /** Creates a new instance of ConfigForm */
    public HistoryConfig(Display display) {
        this.display=display;
        parentView=display.getCurrent();
        
        cf=Config.getInstance();
        
        f=new Form(SR.MS_HISTORY_OPTIONS);
        
        message=new ChoiceGroup(SR.MS_MESSAGES, Choice.MULTIPLE);
//#if LAST_MESSAGES
//#         message.append("Last messages", null);
//#endif

        boolean mv[]={
//#if LAST_MESSAGES
//#             cf.lastMessages
//#endif
        };
        this.mv=mv;
        message.setSelectedFlags(mv);
        f.append(message);
        
//#if FILE_IO
        history=new ChoiceGroup(SR.MS_HISTORY, Choice.MULTIPLE); //locale
        history.append(SR.MS_SAVE_HISTORY, null); //locale
        history.append(SR.MS_SAVE_PRESENCES,null);    //locale     
        history.append(SR.MS_SAVE_HISTORY_CONF, null); //locale
        history.append(SR.MS_SAVE_PRESENCES_CONF, null); //locale
        history.append(SR.MS_1251_CORRECTION, null); //locale
//#ifdef TRANSLIT
//#         history.append(SR.MS_1251_TRANSLITERATE_FILENAMES, null); //locale
//#endif
        
        boolean his[]={
            cf.msgLog,
            cf.msgLogPresence,
            cf.msgLogConf,
            cf.msgLogConfPresence,
            cf.cp1251
//#ifdef TRANSLIT
//#             , cf.transliterateFilenames
//#endif
        };
        this.his=his;
        
        history.setSelectedFlags(his);
        f.append(history);
        
        historyFolder=new TextFieldEx(SR.MS_HISTORY_FOLDER, null, 200, TextField.ANY);
        historyFolder.setString(cf.msgPath);
        historyFolder.addCommand(cmdSetHistFolder);
        f.append(historyFolder);
        historyFolder.setItemCommandListener(this);
//#endif

        f.addCommand(cmdOk);
        f.addCommand(cmdCancel);
        
        f.setCommandListener(this);
       
        display.setCurrent(f);
    }
    
    public void commandAction(Command c, Displayable d) {
        if (c==cmdOk) {
            message.getSelectedFlags(mv);
//#if FILE_IO
            history.getSelectedFlags(his);
//#endif

            int mvctr=0;
//#if LAST_MESSAGES
//#             cf.lastMessages=mv[mvctr++];
//#endif
	    
//#if FILE_IO
            cf.msgLog=his[0];
            cf.msgLogPresence=his[1];
            cf.msgLogConf=his[2];
            cf.msgLogConfPresence=his[3];
            cf.cp1251=his[4];
//#ifdef TRANSLIT
//#             cf.transliterateFilenames=his[5];
//#endif
            cf.msgPath=historyFolder.getString();
//#endif             
            
            cf.saveToStorage();
            
            StaticData.getInstance().roster.reEnumRoster();
            destroyView();
        }
        if (c==cmdCancel) destroyView();
    }
    
//#if FILE_IO
    public void commandAction(Command command, Item item) {
        if (command==cmdSetHistFolder) {
            new Browser(null, display, this, true);
        }
    }
//#endif
    
    public void destroyView(){
        if (display!=null)
            display.setCurrent(parentView);
    }

//#if FILE_IO
    public void BrowserFilePathNotify(String pathSelected) {
        historyFolder.setString(pathSelected);
    }
//#endif
}
