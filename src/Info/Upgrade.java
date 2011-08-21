/*
 * Upgrade.java
 *
 * Created on 17.07.2007, 0:57
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

package Info;

import Client.Msg;
import Messages.MessageItem;
import Messages.MessageList;
import images.RosterIcons;
import java.io.InputStream;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import locale.SR;
import ui.MainBar;

/**
 *
 * @author evgs
 */
public class Upgrade 
        extends MessageList 
        implements Runnable        
    {
   
    //private MenuCommand cmdBack=new MenuCommand(SR.MS_BACK, Command.BACK, 99, RosterIcons.ICON_RESUME);
    private final static String VERSION_URL="http://bombusmod.net.ru/checkupdate/check.php";

    Vector versions[];
    boolean build;
    
    HttpConnection c;
    InputStream is;
            
    private boolean wait=true;
    private boolean error=false;
    
    /** Creates a new instance of Upgrade
     * @param build
     */
    public Upgrade(boolean build) {
        super (new Vector());
        this.build=build;
        
        try {
            focusedItem(0);
        } catch (Exception e) {}
        
	mainbar = new MainBar(SR.MS_CHECK_UPDATE);
        mainbar.addElement(null);
        mainbar.addRAlign();
        mainbar.addElement(null);        
    }

    public void run() {
        wait=true;
        clearList();
        String vUrl=(build)?Client.Config.getInstance().getStringProperty("Bombus-Upgrade", VERSION_URL):VERSION_URL;
        if (build) {
            vUrl+="?vers=new";
        }
        try {
            c = (HttpConnection) Connector.open(vUrl);
            is = c.openInputStream();
            
            versions=new util.StringLoader().stringLoader(is, 1);
            for (int i=0; i<versions[0].size(); i++) {
                if (versions[0].elementAt(i)==null) continue;
                String name=(String)versions[0].elementAt(i);
                messages.addElement(new MessageItem(new Msg(Msg.MESSAGE_TYPE_IN, null, null, name), false));
            }

            if(is!= null) is.close();
            if(c != null) c.close();
        } catch (Exception e) {
            messages.addElement(new MessageItem(new Msg(Msg.MESSAGE_TYPE_IN, null, null, SR.MS_ERROR), false));
        }
        wait=false;
        redraw();
    }

    protected void beginPaint() {
        StringBuffer str = new StringBuffer();
        Object pic = null;
        if (wait) {
            str.append(" - loading");
            pic = new Integer(RosterIcons.ICON_PROGRESS_INDEX);
        } else if (error) {
            pic = new Integer(RosterIcons.ICON_PRIVACY_BLOCK);
        } else {
            pic = new Integer(RosterIcons.ICON_PRIVACY_ALLOW);
        }
        
        mainbar.setElementAt(str.toString(),1);
        mainbar.setElementAt(pic, 3);
    }
    
    public int getItemCount() {
        return messages.size();
    }

    public Msg getMessage(int index) {
	return ((MessageItem)messages.elementAt(index)).msg;
    }
    
    private void clearList() {
        if (getItemCount()>0) {
            messages.removeAllElements();
        }
        redraw(); 
    }
}
