/*
 * Bookmarks.java
 *
 * Created on 18.09.2005, 0:03
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

package Conference;
import Client.*;
import Conference.affiliation.Affiliations;
//#ifdef SERVICE_DISCOVERY
//# import ServiceDiscovery.ServiceDiscovery;
//#endif
import javax.microedition.lcdui.*;
import locale.SR;
import ui.*;
import java.util.*;
import com.alsutton.jabber.*;
import ui.MainBar;

/**
 *
 * @author EvgS
 */
public class Bookmarks 
        extends VirtualList 
        implements CommandListener, YesNoAlert.YesNoListener
{   
    private BookmarkItem toAdd;
    
    private Roster roster = StaticData.getInstance().roster;
    
    private Config cf=Config.getInstance();
    
    private Command cmdCancel=new Command (SR.MS_CANCEL, Command.BACK, 99);
    private Command cmdJoin=new Command (SR.MS_SELECT, Command.OK, 1);
    private Command cmdAdvJoin=new Command (SR.MS_EDIT_JOIN, Command.SCREEN, 2);
    private Command cmdDoAutoJoin=new Command(SR.MS_DO_AUTOJOIN, Command.SCREEN, 3);
    private Command cmdNew=new Command (SR.MS_NEW_BOOKMARK, Command.SCREEN, 4);
    private Command cmdConfigure=new Command (SR.MS_CONFIG_ROOM, Command.SCREEN, 5);
//#ifdef SERVICE_DISCOVERY
//#     private Command cmdDisco=new Command (SR.MS_DISCO_ROOM, Command.SCREEN, 6);
//#endif
    private Command cmdUp=new Command (SR.MS_MOVE_UP, Command.SCREEN, 7);
    private Command cmdDwn=new Command (SR.MS_MOVE_DOWN, Command.SCREEN, 8);
    private Command cmdSort=new Command (SR.MS_SORT, Command.SCREEN, 9);
    private Command cmdSave=new Command (SR.MS_SAVE_LIST, Command.SCREEN, 10);

    private Command cmdRoomOwners=new Command (SR.MS_OWNERS, Command.SCREEN, 11);
    private Command cmdRoomAdmins=new Command (SR.MS_ADMINS, Command.SCREEN, 12);
    private Command cmdRoomMembers=new Command (SR.MS_MEMBERS, Command.SCREEN, 13);
    private Command cmdRoomBanned=new Command (SR.MS_BANNED, Command.SCREEN, 14);
    
    private Command cmdDel=new Command (SR.MS_DELETE, Command.SCREEN, 15);

    JabberStream stream=roster.theStream;
    /** Creates a new instance of Bookmarks */
    public Bookmarks(Display display, BookmarkItem toAdd) {
        super ();
        if (getItemCount()==0 && toAdd==null) {
            new ConferenceForm(display);
            return;
        }

        this.toAdd=toAdd;

        if (toAdd!=null) 
            addBookmark();
        
        setMainBarItem(new MainBar(2, null, SR.MS_BOOKMARKS+" ("+getItemCount()+") "));//for title updating after "add bookmark"
        
        addCommand(cmdCancel);
        addCommand(cmdJoin);
        addCommand(cmdDoAutoJoin);
        addCommand(cmdAdvJoin);
	addCommand(cmdNew);
        
        addCommand(cmdUp);
        addCommand(cmdDwn);
        addCommand(cmdSort);
        addCommand(cmdSave);
//#ifdef SERVICE_DISCOVERY
//#         addCommand(cmdDisco);
//#endif
        addCommand(cmdConfigure);
        addCommand(cmdRoomOwners);
        addCommand(cmdRoomAdmins);
        addCommand(cmdRoomMembers);
        addCommand(cmdRoomBanned);
        addCommand(cmdDel);
        setCommandListener(this);
	attachDisplay(display);
    }

    protected int getItemCount() { 
        Vector bookmarks=roster.bookmarks;
        return (bookmarks==null)?0: bookmarks.size(); 
    }
    
    protected VirtualElement getItemRef(int index) { 
        return (VirtualElement) roster.bookmarks.elementAt(index); 
    }
    
    public void loadBookmarks() {
    }

    private void addBookmark() {
        if (toAdd!=null) {
            Vector bm=roster.bookmarks;
            bm.addElement(toAdd);
            //sort(bm);
            saveBookmarks();
        }
    }
    
    public void eventOk(){
        if (getItemCount()==0) 
            return;
        
        BookmarkItem join=(BookmarkItem)getFocusedObject();
        if (join==null) 
            return;
        if (join.isUrl) 
            return;

        ConferenceGroup grp=roster.initMuc(join.getJidNick(), join.password);
        grp.desc=join.desc;
        JabberDataBlock x=new JabberDataBlock("x", null, null);
        x.setNameSpace("http://jabber.org/protocol/muc");
        
        JabberDataBlock history=x.addChild("history", null);
        history.setAttribute("maxstanzas", Integer.toString(cf.confMessageCount));
        history.setAttribute("maxchars","32768");
        try {
            long last=grp.getConference().lastMessageTime;
            long delay= ( grp.conferenceJoinTime - last ) /1000 ;
            if (last!=0) history.setAttribute("seconds",String.valueOf(delay)); // todo: change to since
        } catch (Exception e) {}
        
        roster.sendPresence(join.getJidNick(), null, x, false);
        
        roster.reEnumRoster();
        display.setCurrent(roster);
    }
    
    public void commandAction(Command c, Displayable d){
        if (c==cmdCancel) exitBookmarks();
        if (c==cmdNew) { 
            new ConferenceForm(display);
            return;
        }

	if (getItemCount()==0) return;
        String roomJid=((BookmarkItem)getFocusedObject()).getJid();

        if (c==cmdJoin) eventOk();
        else if (c==cmdAdvJoin) {
            BookmarkItem join=(BookmarkItem)getFocusedObject();
            new ConferenceForm(display, join, cursor);
        }

        else if (c==cmdDel) {
            deleteBookmark();
            setMainBarItem(new MainBar(2, null, SR.MS_BOOKMARKS+" ("+getItemCount()+") "));
            return;
        }
//#ifdef SERVICE_DISCOVERY
//#         else if (c==cmdDisco) new ServiceDiscovery(display, roomJid, null);
//#endif
        else if (c==cmdConfigure) new QueryConfigForm(display, roomJid);
        else if (c==cmdRoomOwners) new Affiliations(display, roomJid, (short)1);  
        else if (c==cmdRoomAdmins) new Affiliations(display, roomJid, (short)2);  
        else if (c==cmdRoomMembers) new Affiliations(display, roomJid, (short)3);  
        else if (c==cmdRoomBanned) new Affiliations(display, roomJid, (short)4);  
        else if (c==cmdSort) sort(roster.bookmarks);
        else if (c==cmdDoAutoJoin) {
            for (Enumeration e=roster.bookmarks.elements(); e.hasMoreElements();) {
                BookmarkItem bm=(BookmarkItem) e.nextElement();
                if (bm.autojoin) 
                    ConferenceForm.join(bm.desc, bm.jid+'/'+bm.nick, bm.password, cf.confMessageCount);
            }
        }
        
        else if (c==cmdSave) saveBookmarks();
        else if (c==cmdUp) { move(-1); keyUp(); }
        else if (c==cmdDwn) { move(+1); keyDwn(); }
        redraw();
    }
    
    private void deleteBookmark(){
        BookmarkItem del=(BookmarkItem)getFocusedObject();
        if (del==null) 
            return;
        if (del.isUrl) 
            return;

        roster.bookmarks.removeElement(del);
        if (getItemCount()<=cursor) 
            moveCursorEnd();
        saveBookmarks();
        redraw();
    }
    
    private void saveBookmarks() {
        new BookmarkQuery(BookmarkQuery.SAVE);
    }

    private void exitBookmarks(){
        display.setCurrent(roster);
    }
    
    public void move(int offset){
        try {
            int index=cursor;
            BookmarkItem p1=(BookmarkItem)getItemRef(index);
            BookmarkItem p2=(BookmarkItem)getItemRef(index+offset);
            
            roster.bookmarks.setElementAt(p1, index+offset);
            roster.bookmarks.setElementAt(p2, index);
        } catch (Exception e) {/* IndexOutOfBounds */}
    }

    public void userKeyPressed(int keyCode) {
        super.userKeyPressed(keyCode);

        switch (keyCode) {
            case KEY_NUM4:
                pageLeft(); break;
            case KEY_NUM6:
                pageRight(); break;
            case keyClear:
                new YesNoAlert(display, SR.MS_DELETE_ASK, ((BookmarkItem)getFocusedObject()).getJid(), this);
                break;
        }
    }
    
    public void ActionConfirmed() {
        deleteBookmark();
    }
}
