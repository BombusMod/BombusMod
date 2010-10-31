/*
 * MIDPTextBox.java
 *
 * Created on 26.03.2005, 20:56
 *
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
 */

package ui;

import javax.microedition.lcdui.*;
import locale.SR;

/**
 *
 * @author  Eugene Stahov
 * @version
 */
public class MIDPTextBox implements CommandListener {
    
    private Displayable parentView = midlet.BombusMod.getInstance().getCurrentDisplayable();
    
    protected Command cmdCancel=new Command(SR.MS_CANCEL, Command.BACK, 99);
    protected Command cmdOK=new Command(SR.MS_OK, Command.OK /*Command.SCREEN*/, 1);
    
    private TextBox t;
    
    private TextBoxNotify tbn;

    /**
     * constructor
     */
    public interface TextBoxNotify {
        void OkNotify(String text_return);
    }
   
    public MIDPTextBox(String mainbar, String text, TextBoxNotify tbn , int constraints) {
        
        t=new TextBox(mainbar, text, 150, constraints);
        
        this.tbn=tbn;
        
        t.addCommand(cmdOK);
        t.addCommand(cmdCancel);
        
        t.setCommandListener(this);
        midlet.BombusMod.getInstance().setDisplayable(t);
    }
    
    /**
     * Called when action should be handled
     * @param command 
     * @param displayable
     */
    public void commandAction(Command command, Displayable displayable) {
        if (command==cmdCancel) { destroyView(); return;}
        if (command==cmdOK) { destroyView(); tbn.OkNotify(t.getString()); return;}
    }

    public void destroyView(){
        midlet.BombusMod.getInstance().setDisplayable(parentView);
    }
}
