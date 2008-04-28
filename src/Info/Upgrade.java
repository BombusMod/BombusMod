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
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
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
        implements Runnable, CommandListener{
   
    //private Command cmdBack=new Command(SR.MS_BACK, Command.BACK, 99);
    private final static String VERSION_URL="http://bombusmod.net.ru/checkupdate/check.php";

    upgradeItems items;
    Vector versions[];
    boolean build;
    
    HttpConnection c;
    InputStream is;
            
    private Display display;
    private Displayable parentView;

    private boolean wait=true;
    private boolean error=false;
    
    /** Creates a new instance of Upgrade */
    public Upgrade(Display display, boolean build) {
        super ();
        this.display=display;
        this.build=build;
        
        items=upgradeItems.getInstance();
        
        setCommandListener(this);
	addCommand(cmdBack);
        attachDisplay(display);
        
        try {
            focusedItem(0);
        } catch (Exception e) {}
        
	MainBar mainbar=new MainBar(SR.MS_CHECK_UPDATE);
        setMainBarItem(mainbar);
        mainbar.addElement(null);
        mainbar.addRAlign();
        mainbar.addElement(null);
        
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
        } /*else {
            SHA1 sha=new SHA1();
            sha.init();
            sha.update(strconv.unicodeToUTF(StaticData.getInstance().account.getBareJid()) );
            sha.finish();
            
            vUrl+="?name="+Version.NAME;
            vUrl+="&version="+Version.getVersionNumber();
            vUrl+="&lang="+SR.MS_IFACELANG;
            vUrl+="&os="+Config.getOs();
            vUrl+="&hash="+sha.getDigestHex();
        }*/
        try {
            c = (HttpConnection) Connector.open(vUrl);
            is = c.openInputStream();
            
            versions=new util.StringLoader().stringLoader(is, 1);
            for (int i=0; i<versions[0].size(); i++) {
                if (versions[0].elementAt(i)==null) continue;
                String name=(String)versions[0].elementAt(i);
                items.add(new Msg(Msg.MESSAGE_TYPE_IN, "local", null, name)); 
            }

            if(is!= null) is.close();
            if(c != null) c.close();
        } catch (Exception e) {
            items.add(new Msg(Msg.MESSAGE_TYPE_IN, "local", null, "Error on request!"));
        }
        wait=false;
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
        return items.size();
    }

    public Msg getMessage(int index) {
	return items.msg(index);
    }
    
    private void clearList() {
        if (getItemCount()>0) {
            items.clearAll();
            messages=new Vector();
        }
        redraw(); 
    }
}
