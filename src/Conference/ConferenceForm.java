/*
 * ConferenceForm.java
 *
 * Created on 24.07.2005, 18:32
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

package Conference;
import Client.StaticData;
import com.alsutton.jabber.JabberDataBlock;
import javax.microedition.lcdui.TextField;
import locale.SR;
import com.alsutton.jabber.datablocks.Presence;
import ui.controls.form.CheckBox;
import ui.controls.form.DefForm;
import ui.controls.form.LinkString;
import ui.controls.form.NumberInput;
import ui.controls.form.PasswordInput;
import ui.controls.form.TextInput;
import Menu.MenuCommand;
import PrivacyLists.QuickPrivacy;
import java.util.Vector;
import ui.VirtualList;

/**
 *
 * @author EvgS
 */
public class ConferenceForm
    extends DefForm {
    
    MenuCommand cmdJoin=new MenuCommand(SR.MS_JOIN, MenuCommand.SCREEN, 1);
    MenuCommand cmdAdd=new MenuCommand(SR.MS_ADD_BOOKMARK, MenuCommand.SCREEN, 5);
    MenuCommand cmdEdit=new MenuCommand(SR.MS_SAVE, MenuCommand.SCREEN, 6);
    private TextInput roomField;
    private TextInput hostField;
    private TextInput nickField;
    private TextInput nameField;
    private PasswordInput passField;
    private NumberInput msgLimitField;
    private CheckBox autoJoin;
    private LinkString linkJoin;
    
    BookmarkItem editConf;

    //private static boolean sndprs=false;
    
    /** Creates a new instance of GroupChatForm */
    public ConferenceForm(String name, String confJid, String password, boolean autojoin) {
        super(SR.MS_JOIN_CONFERENCE);
        int roomEnd=confJid.indexOf('@');
        String room="";
        if (roomEnd>0) room=confJid.substring(0, roomEnd);
        String server;
        String nick = null;
        int serverEnd=confJid.indexOf('/');
        if (serverEnd>0) {
            server=confJid.substring(roomEnd+1,serverEnd);
            nick=confJid.substring(serverEnd+1);
        } else {
            server=confJid.substring(roomEnd+1);
        }
        createForm(name, room, server, nick, password, autojoin);
        room=null;
        server=null;
        nick=null;
    }
    
    /** Creates a new instance of GroupChatForm */
    public ConferenceForm(BookmarkItem join, int cursor) {
        super(SR.MS_JOIN_CONFERENCE);
        if (join==null) return;
        if (join.isUrl) return;
        
        this.editConf=join;
        this.cursor=cursor;

        int roomEnd=join.getJid().indexOf('@');
        String room="";
        if (roomEnd>0) room=join.getJid().substring(0, roomEnd);
        createForm(join.desc, room, join.getJid().substring(roomEnd+1), join.nick, join.password, join.autojoin);
        room=null;
    }
    
    /** Creates a new instance of GroupChatForm */
    public ConferenceForm() {
        super(SR.MS_JOIN_CONFERENCE);
        String room=cf.defGcRoom;
        String server=null;
        // trying to split string like room@server
        int roomE=room.indexOf('@');
        if (roomE>0) {
            server=room.substring(roomE+1);
            room=room.substring(0, roomE);
        }
        // default server
        if (server==null) server="conference."+sd.account.getServer();
        createForm(null, room, server, null, null, false); 
        room=null;
        server=null;
    }
	
    /** Creates a new instance of GroupChatForm */
    public ConferenceForm(String name, String room, String server, String nick, String password, boolean autojoin) {
        super(SR.MS_JOIN_CONFERENCE);
        createForm(name, room, server, nick, password, autojoin);
    }
    
     private void createForm(String name, String room, String server, String nick, final String password, boolean autojoin) {
        roomField=new TextInput(SR.MS_ROOM, room, null, TextField.ANY);//, 64, TextField.ANY);
        itemsList.addElement(roomField);

        hostField=new TextInput(SR.MS_AT_HOST, server, "muc-host", TextField.ANY);//, 64, TextField.ANY, "muc-host", display);
        itemsList.addElement(hostField);
        
        if (nick==null) nick=sd.account.getNickName();
        nickField=new TextInput(SR.MS_NICKNAME, nick, "roomnick", TextField.ANY);//, 32, TextField.ANY, "roomnick", display);
        itemsList.addElement(nickField);

        msgLimitField=new NumberInput( SR.MS_MSG_LIMIT, Integer.toString(cf.confMessageCount), 0, 100);
        itemsList.addElement(msgLimitField);

        nameField=new TextInput(SR.MS_DESCRIPTION, name, null, TextField.ANY);//, 128, TextField.ANY);
        itemsList.addElement(nameField);

        passField=new PasswordInput( SR.MS_PASSWORD, password);//, 32, TextField.ANY | TextField.SENSITIVE );
        itemsList.addElement(passField);

        autoJoin=new CheckBox(SR.MS_AUTOLOGIN, autojoin);
        itemsList.addElement(autoJoin);
        
        linkJoin=new LinkString(SR.MS_JOIN) {
            public void doAction() {
                join(nameField.getValue(), roomField.getValue().trim()+"@"+hostField.getValue().trim()+"/"+nickField.getValue(), passField.getValue(), Integer.parseInt(msgLimitField.getValue()));
                sd.roster.show();
            }
        };
        itemsList.addElement(linkJoin);
        moveCursorTo(getNextSelectableRef(-1));        
    }

    public void menuAction(MenuCommand c, VirtualList d){
        super.menuAction(c, d);
        
        String nick=nickField.getValue();
        String name=nameField.getValue();
        String host=hostField.getValue();
        String room=roomField.getValue();
        String pass=passField.getValue();
        int msgLimit=Integer.parseInt(msgLimitField.getValue());

        boolean autojoin=autoJoin.getValue();
        
        if (nick.length()==0) return;
        if (room.length()==0) return;
        if (host.length()==0) return;
        
        StringBuffer gchat=new StringBuffer(room.trim()).append('@').append(host.trim());
        
        if (name.length()==0) name=gchat.toString();
        
        saveMsgCount(msgLimit);
            
        if (c==cmdEdit) {
            sd.roster.bookmarks.removeElement(editConf);
            sd.roster.bookmarks.insertElementAt(new BookmarkItem(name, gchat.toString(), nick, pass, autojoin), cursor);
            new BookmarkQuery(BookmarkQuery.SAVE);
            destroyView();
        } else if (c==cmdAdd) {
            new Bookmarks(new BookmarkItem(name, gchat.toString(), nick, pass, autojoin));
        } else if (c==cmdJoin) {
//#ifdef PRIVACY            
//#ifdef PLUGINS                        
//#                 if (sd.Privacy) {
//#endif                        
                    if (QuickPrivacy.conferenceList == null)
                        QuickPrivacy.conferenceList = new Vector();
                    QuickPrivacy.conferenceList.addElement(host);
                    new QuickPrivacy().updateQuickPrivacyList();
//#ifdef PLUGINS                        
//#                 }
//#endif
//#endif                                    
            
            try {
                cf.defGcRoom=room+"@"+host;
                cf.saveToStorage();
                gchat.append('/').append(nick);
                join(name, gchat.toString(), pass, msgLimit);
                sd.roster.show();
            } catch (Exception e) { }
        }
        gchat=null;
        nick=null;
        name=null;
        host=null;
        room=null;
        pass=null;
    }
    
    public void commandState(){
        menuCommands.removeAllElements();
        addMenuCommand(cmdJoin);
        addMenuCommand(cmdAdd);
        addMenuCommand(cmdEdit);
        addMenuCommand(cmdCancel);
    }

    public String touchLeftCommand(){ return SR.MS_MENU; }
    
    public void touchLeftPressed(){
        showMenu();
    }

    private void saveMsgCount(int msgLimit) {
        if (cf.confMessageCount!=msgLimit) {
            cf.confMessageCount=msgLimit;
            cf.saveToStorage();
        }
    }

    public static void join(String name, String jid, String pass, int maxStanzas) {
        ConferenceGroup grp = StaticData.getInstance().roster.initMuc(jid, pass);
        grp.desc=name;

        JabberDataBlock x=new JabberDataBlock("x", null, null);
        x.setNameSpace("http://jabber.org/protocol/muc");
        if (pass.length()!=0) {
            x.addChild("password", pass); // adding password to presence
        }
        
        JabberDataBlock history=x.addChild("history", null);
        history.setAttribute("maxstanzas", Integer.toString(maxStanzas));
        history.setAttribute("maxchars","32768");
        try {
            long last=grp.confContact.lastMessageTime;
            long delay= ( grp.conferenceJoinTime - last ) /1000 ;
            if (last!=0) 
                history.setAttribute("seconds",String.valueOf(delay)); // todo: change to since
        } catch (Exception e) {}
        
        int status = StaticData.getInstance().roster.myStatus;
        if (status==Presence.PRESENCE_INVISIBLE) 
            status=Presence.PRESENCE_ONLINE;
        StaticData.getInstance().roster.sendDirectPresence(status, jid, x);

        grp.inRoom=true;

        //sd.roster.reEnumRoster();
    }
}
