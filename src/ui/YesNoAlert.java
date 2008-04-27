/*
 * YesNoAlert.java
 *
 * Created on 8.05.2005, 23:19
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
 * @author Evg_S
 */
public class YesNoAlert implements CommandListener{
    
    public interface YesNoListener {
        public void ActionConfirmed();
    }
    
     private Display display;
     private Displayable parentView;
    
    private YesNoListener listener;
    
    Form f;

    Command cmdYes=new Command(SR.MS_YES, Command.OK, 1);
    Command cmdNo=new Command(SR.MS_NO, Command.BACK, 99);
	
    /** Creates a new instance of YesNoAlert */
    public YesNoAlert(Display display, String mainbar, String alertText, YesNoListener listener) {
        f=new Form(mainbar);
        f.addCommand(cmdYes);
        f.addCommand(cmdNo);
        f.setCommandListener(this);
		
	this.listener=listener;

        f.append("\n");
        f.append(alertText);
        
        this.display=display;
        this.parentView=display.getCurrent();
        display.setCurrent(f);
        
    }
    public void commandAction(Command c, Displayable d ){
        destroyView();
        if (c==cmdYes) {
            yes();
        } else no();
    }
	
    public void yes() {
        if (listener!=null) listener.ActionConfirmed();
    };

    public void no(){};
    
    public void destroyView(){
        if (display!=null)   display.setCurrent(parentView);
    }

}
