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
import Client.*;
import com.alsutton.jabber.JabberDataBlock;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextField;
import locale.SR;
import com.alsutton.jabber.datablocks.Presence;
import ui.controls.form.SimpleString;
import ui.controls.form.CheckBox;
import ui.controls.form.DefForm;
import ui.controls.form.NumberInput;
import ui.controls.form.PasswordInput;
import ui.controls.form.TextInput;
//#ifndef MENU_LISTENER
import javax.microedition.lcdui.Command;
//#else
//# import Menu.Command;
//#endif

/**
 *
 * @author EvgS
 */
public class ConferenceForm
    extends DefForm {
    
    private Display display;
    private Displayable parentView;
    
    private Config cf=Config.getInstance();
//#ifndef MENU
    Command cmdJoin=new Command(SR.MS_JOIN, Command.SCREEN, 1);
    Command cmdAdd=new Command(SR.MS_ADD_BOOKMARK, Command.SCREEN, 5);
    Command cmdEdit=new Command(SR.MS_SAVE, Command.SCREEN, 6);
//#endif
    private TextInput roomField;
    private TextInput hostField;
    private TextInput nickField;
    private TextInput nameField;
    private PasswordInput passField;
    private NumberInput msgLimitField;
    private CheckBox autoJoin;
    
    BookmarkItem editConf;

    //private static boolean sndprs=false;
    
    private static StaticData sd=StaticData.getInstance();

    private int cursor;
    
    /** Creates a new instance of GroupChatForm */
    public ConferenceForm(Display display, String name, String confJid, String password, boolean autojoin) {
        super(display, SR.MS_JOIN_CONFERENCE);
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
        createForm(display, name, room, server, nick, password, autojoin);
        room=null;
        server=null;
        nick=null;
    }
    
    /** Creates a new instance of GroupChatForm */
    public ConferenceForm(Display display, BookmarkItem join, int cursor) {
        super(display, SR.MS_JOIN_CONFERENCE);
        if (join==null) return;
        if (join.isUrl) return;
        
        this.editConf=join;
        this.cursor=cursor;

        String confJid=join.getJidNick();
        int roomEnd=confJid.indexOf('@');
        String room="";
        if (roomEnd>0) room=confJid.substring(0, roomEnd);
        String server;
        String nick=null;
        int serverEnd=confJid.indexOf('/');
        if (serverEnd>0) {
            server=confJid.substring(roomEnd+1,serverEnd);
            nick=confJid.substring(serverEnd+1);
        } else {
            server=confJid.substring(roomEnd+1);
        }
        createForm(display, join.desc, room, server, nick, join.password, join.autojoin);
        confJid=null;
        room=null;
        server=null;
        nick=null;
    }
    
    /** Creates a new instance of GroupChatForm */
    public ConferenceForm(Display display) {
        super(display, SR.MS_JOIN_CONFERENCE);
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
        createForm(display, null, room, server, null, null, false); 
        room=null;
        server=null;
    }
	
    /** Creates a new instance of GroupChatForm */
    public ConferenceForm(Display display, String name, String room, String server, String nick, String password, boolean autojoin) {
        super(display, SR.MS_JOIN_CONFERENCE);
        createForm(display, name, room, server, nick, password, autojoin);
    }
    
     private void createForm(final Display display, String name, String room, String server, String nick, final String password, boolean autojoin) {
        this.display=display;
        parentView=display.getCurrent();

        roomField=new TextInput(display, SR.MS_ROOM, room, null, TextField.ANY);//, 64, TextField.ANY);
        itemsList.addElement(roomField);

        hostField=new TextInput(display, SR.MS_AT_HOST, server, "muc-host", TextField.ANY);//, 64, TextField.ANY, "muc-host", display);
        itemsList.addElement(hostField);
        
        if (nick==null) nick=sd.account.getNickName();
        nickField=new TextInput(display, SR.MS_NICKNAME, nick, "roomnick", TextField.ANY);//, 32, TextField.ANY, "roomnick", display);
        itemsList.addElement(nickField);

        msgLimitField=new NumberInput(display, SR.MS_MSG_LIMIT, Integer.toString(cf.confMessageCount), 0, 100);
        itemsList.addElement(msgLimitField);

        nameField=new TextInput(display, SR.MS_DESCRIPTION, name, null, TextField.ANY);//, 128, TextField.ANY);
        itemsList.addElement(nameField);

        passField=new PasswordInput(display, SR.MS_PASSWORD, password);//, 32, TextField.ANY | TextField.SENSITIVE );
        itemsList.addElement(passField);

        autoJoin=new CheckBox(SR.MS_AUTOLOGIN, autojoin);
        itemsList.addElement(autoJoin);
        
        commandState();
        
	setCommandListener(this);

        moveCursorTo(getNextSelectableRef(-1));
        attachDisplay(display);
    }

    public void commandAction(Command c, Displayable d){
        super.commandAction(c, d);
        
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
            display.setCurrent(sd.roster);
        } else if (c==cmdAdd) {
            new Bookmarks(display, new BookmarkItem(name, gchat.toString(), nick, pass, autojoin));
        } else if (c==cmdJoin) {
            try {
                cf.defGcRoom=room+"@"+host;
                cf.saveToStorage();
                gchat.append('/').append(nick);
                join(name, gchat.toString(),pass, msgLimit);
                display.setCurrent(sd.roster);
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
//#ifdef MENU_LISTENER
//#         menuCommands.removeAllElements();
//#endif
        addCommand(cmdJoin);
        addCommand(cmdAdd);
        addCommand(cmdEdit);
//#ifndef MENU_LISTENER
        addCommand(cmdCancel);
//#endif
        removeCommand(cmdOk);
    }
    private void saveMsgCount(int msgLimit) {
        if (cf.confMessageCount!=msgLimit) {
            cf.confMessageCount=msgLimit;
            cf.saveToStorage();
        }
    }
    
//#ifdef MENU
//#         public void leftCommand() {
//#             try {
//#                 String nick=nickField.getValue();
//#                 String name=nameField.getValue();
//#                 String host=hostField.getValue();
//#                 String room=roomField.getValue();
//#                 String pass=passField.getValue();
//#                 int msgLimit=Integer.parseInt(msgLimitField.getValue());
//# 
//#                 boolean autojoin=autoJoin.getValue();
//# 
//#                 if (nick.length()==0) return;
//#                 if (room.length()==0) return;
//#                 if (host.length()==0) return;
//# 
//#                 StringBuffer gchat=new StringBuffer(room.trim()).append('@').append(host.trim());
//# 
//#                 if (name.length()==0) name=gchat.toString();
//# 
//#                 saveMsgCount(msgLimit);
//#         
//#                 cf.defGcRoom=room+"@"+host;
//#                 cf.saveToStorage();
//#                 gchat.append('/').append(nick);
//#                 join(name, gchat.toString(),pass, msgLimit);
//#                 display.setCurrent(sd.roster);
//#             } catch (Exception e) { }
//#         }
//#endif
    
    public static void join(String name, String jid, String pass, int maxStanzas) {
        ConferenceGroup grp=sd.roster.initMuc(jid, pass);
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
            long last=grp.getConference().lastMessageTime;
            long delay= ( grp.conferenceJoinTime - last ) /1000 ;
            if (last!=0) 
                history.setAttribute("seconds",String.valueOf(delay)); // todo: change to since
        } catch (Exception e) {}
        
        //if (sndprs) {
            //sd.roster.sendPresence(name, null, x, false);
            int status=sd.roster.myStatus;
            if (status==Presence.PRESENCE_INVISIBLE) 
                status=Presence.PRESENCE_ONLINE;
            sd.roster.sendDirectPresence(status, jid, x);
            //sndprs=false;
        //}
        grp.inRoom=true;
        sd.roster.reEnumRoster();
    }
}
