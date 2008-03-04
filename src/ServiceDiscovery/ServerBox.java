/*
 * ServerBox.java
 *
 * Created on 8.07.2005, 1:09
 * Copyright (c) 2005-2007, Eugene Stahov (evgs), http://bombus-im.org
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
 *
 */

package ServiceDiscovery;

import javax.microedition.lcdui.*;
import locale.SR;
import ui.controls.TextFieldCombo;
/**
 *
 * @author EvgS
 */
public class ServerBox implements CommandListener {
    
    private Display display;
    private Form f;
    private TextField t;
    
    private ServiceDiscovery sd;
    
    private Command cmdCancel=new Command(SR.MS_CANCEL, Command.BACK,99);
    private Command cmdSend=new Command(SR.MS_DISCOVER, Command.OK /*Command.SCREEN*/,1);
	
	public final static String MRU_ID="disco";
    
    /** Creates a new instance of ServerBox */
    public ServerBox(Display display, String service, ServiceDiscovery sd) {
        this.display=display;
        this.sd=sd;
        
        f=new Form(SR.MS_DISCO);
        f.append("Enter Jabber server address here");
        t=new TextFieldCombo("Address",service,500, TextField.ANY, MRU_ID, display);
        TextFieldCombo.setLowerCaseLatin(t);
        f.append(t);
        f.addCommand(cmdSend);
        f.addCommand(cmdCancel);
        f.setCommandListener(this);
        
        //t.setInitialInputMode("MIDP_LOWERCASE_LATIN");
        display.setCurrent(f);
    }
    
    public void commandAction(Command c, Displayable d){
        String server=t.getString();
        if (server.length()==0) server=null;
        
        /*if (c==cmdCancel) {
            destroyView(); return;
        }*/
        if (c==cmdSend && server!=null) { sd.browse(server, null); }
        
        display.setCurrent(sd);
        return;
    }
}

