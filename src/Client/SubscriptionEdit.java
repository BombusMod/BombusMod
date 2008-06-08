/*
 * SubscriptionEdit.java
 *
 * Created on 10.05.2005, 19:09
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
 *
 */

package Client;
import javax.microedition.lcdui.*;
import locale.SR;

/**
 *
 * @author Evg_S
 */
public class SubscriptionEdit extends Form implements CommandListener{
    
    private Display display;
      
    Command cmdAskSubscr=new Command(SR.MS_ASK_SUBSCRIPTION, Command.SCREEN, 1);
    Command cmdGrantSubscr=new Command(SR.MS_GRANT_SUBSCRIPTION, Command.SCREEN, 2);
    Command cmdSubscrDel=new Command(SR.MS_SUBSCR_REMOVE, Command.SCREEN, 3);
    //Command cmdUnsubscr=new Command(SR.MS_UNSUBSCRIBE, Command.SCREEN);
    Command cmdBack=new Command(SR.MS_CANCEL, Command.BACK, 99);
    
    Roster r=StaticData.getInstance().roster;
    String to;
    /** Creates a new instance of YesNoAlert */
    public SubscriptionEdit(Display display, Contact c) {
        super(SR.MS_SUBSCRIPTION);
        to=c.getBareJid();
        StringBuffer s=new StringBuffer(c.getNickJid()).append('\n').append("subscr:").append(c.subscr);
        if (c.ask_subscribe) s.append(",ask");
        
//#if !(MIDP1)
        append("\n");
//#endif
        append(s.toString());
        s=null;

        addCommand(cmdGrantSubscr);
        addCommand(cmdAskSubscr);
        addCommand(cmdSubscrDel);
        addCommand(cmdBack);
        
        setCommandListener(this);

        this.display=display;
        display.setCurrent(this);
    }
    public void commandAction(Command c, Displayable d ){
        String presence=null;
        if (c==cmdAskSubscr) { presence="subscribe"; }
        if (c==cmdGrantSubscr) { presence="subscribed"; }
        if (c==cmdSubscrDel) { presence="unsubscribed"; }
        
        if (presence!=null) r.sendPresence(to,presence, null, false);
        
        destroyView();
    }

    public void destroyView(){
        if (display!=null)   display.setCurrent(r);
    }

}
