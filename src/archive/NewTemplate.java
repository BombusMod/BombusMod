/*
 * NewTemplate.java
 * Copyright (c) 2006-2007, Daniel Apatin (ad), http://apatin.net.ru
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

package archive;

import Client.Msg;
import Client.Roster;
import Client.StaticData;
import javax.microedition.lcdui.*;
import locale.SR;
import ui.controls.TextFieldEx;

public class NewTemplate implements CommandListener
{
    
    private Display display;
    private Displayable parentView;
    Form form;
    TextField templatebox;
    
    private Command cmdOk=new Command(SR.MS_OK, Command.OK, 1);
    private Command cmdCancel = new Command(SR.MS_BACK, Command.BACK, 99);

    private int where;

    public NewTemplate(Display display, int where) {
        this.display=display;
        this.where=where;
        parentView=display.getCurrent();         
        form=new Form(SR.MS_NEW);
        templatebox=new TextFieldEx(SR.MS_NEW, null, 1024, TextField.ANY);
        
        form.append(templatebox);
        form.addCommand(cmdOk);
        form.addCommand(cmdCancel);
	form.setCommandListener(this);        
        display.setCurrent(form);
    }
    
    public void commandAction(Command c, Displayable d){
        if (c==cmdCancel) display.setCurrent(StaticData.getInstance().roster);
	if (c==cmdOk) {
            //final Roster roster=StaticData.getInstance().roster;
	    String reason = templatebox.getString();
            Msg m=new Msg(Msg.MESSAGE_TYPE_OUT, "0", "", reason);
            MessageArchive.store(m, where);
            display.setCurrent(parentView);
	}
    }
}
