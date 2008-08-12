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
import Messages.MessageList;
import images.RosterIcons;
import java.io.InputStream;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
//#ifndef MENU_LISTENER
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Command;
//#else
//# import Menu.MenuListener;
//# import Menu.Command;
//#endif
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import locale.SR;
import ui.MainBar;

/**
 *
 * @author evgs
 */
public class Upgrade 
        extends MessageList 
        implements Runnable,
//#ifndef MENU_LISTENER
        CommandListener
//#else
//#         MenuListener
//#endif
    {
   
    //private Command cmdBack=new Command(SR.MS_BACK, Command.BACK, 99);
    private final static String VERSION_URL="http://bombusmod.net.ru/checkupdate/check.php";

    Vector news;
    Vector versions[];
    boolean build;
    
    HttpConnection c;
    InputStream is;
            
    private Display display;

    private boolean wait=true;
    private boolean error=false;
    
    /** Creates a new instance of Upgrade */
    public Upgrade(Display display, Displayable pView, boolean build) {
        super ();
        this.display=display;
        this.build=build;
        
        news=new Vector();
        
        setCommandListener(this);
	addCommand(cmdBack);
        
        try {
            focusedItem(0);
        } catch (Exception e) {}
        
	MainBar mainbar=new MainBar(SR.MS_CHECK_UPDATE);
        setMainBarItem(mainbar);
        mainbar.addElement(null);
        mainbar.addRAlign();
        mainbar.addElement(null);

        attachDisplay(display);
        this.parentView=pView;
        
        new Thread(this).start();
    }

    public void run() {
        wait=true;
        clearList();
        String result="";
        StringBuffer b = new StringBuffer();
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
                news.addElement(new Msg(Msg.MESSAGE_TYPE_IN, null, null, name)); 
            }

            if(is!= null) is.close();
            if(c != null) c.close();
        } catch (Exception e) {
            news.addElement(new Msg(Msg.MESSAGE_TYPE_IN, null, null, SR.MS_ERROR));
        }
        wait=false;
        redraw();
    }

    public void commandAction(Command c, Displayable d) {
        super.commandAction(c,d);
        /*try {
            if (BombusMod.getInstance().platformRequest((String) versions[2].elementAt(index))) System.exit(0);
        } catch (Exception e) { e.printStackTrace(); }*/
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
        
        getMainBarItem().setElementAt(str.toString(),1);
        getMainBarItem().setElementAt(pic, 3);
    }

    public void destroyView() {
        super.destroyView();
    }

    public int getItemCount() {
        return news.size();
    }

    public Msg getMessage(int index) {
	return (Msg)news.elementAt(index);
    }
    
    private void clearList() {
        if (getItemCount()>0) {
            news.removeAllElements();
            messages=null;
            messages=new Vector();
        }
        redraw(); 
    }
}
