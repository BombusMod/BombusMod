/*
 * ConferenceForm.java
 *
 * Created on 24.07.2005, 18:32
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
import com.alsutton.jabber.JabberDataBlock;
import locale.SR;
import javax.microedition.lcdui.*;
import ui.controls.NumberField;
import ui.controls.TextFieldCombo;
import com.alsutton.jabber.datablocks.Presence;

/**
 *
 * @author EvgS
 */
public class ConferenceForm implements CommandListener{
    
    private Display display;
    private Displayable parentView;
    
    private Config cf=Config.getInstance();
    
    Command cmdJoin=new Command(SR.MS_JOIN, Command.SCREEN, 1);
    Command cmdAdd=new Command(SR.MS_ADD_BOOKMARK, Command.SCREEN, 5);
    Command cmdEdit=new Command(SR.MS_SAVE, Command.SCREEN, 6);
    Command cmdCancel=new Command (SR.MS_CANCEL, Command.BACK, 99);
    
    TextField roomField;
    TextField hostField;
    TextField nickField;
    TextField nameField;
    TextField passField;
    NumberField msgLimitField;
    
    BookmarkItem editConf;
    
    ChoiceGroup AutoJoin;
    boolean aa[];  
    
    private static boolean sndprs=false;
    
    StaticData sd=StaticData.getInstance();

    private int cursor;
    
    /** Creates a new instance of GroupChatForm */
    public ConferenceForm(Display display, String name, String confJid, String password, boolean autojoin) {
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
        createForm(display, name, room, server, nick, password, autojoin);
        room=null;
        server=null;
        nick=null;
    }
    
    /** Creates a new instance of GroupChatForm */
    public ConferenceForm(Display display, BookmarkItem join, int cursor) {
        if (join==null) return;
        if (join.isUrl) return;
        
        this.editConf=join;
        this.cursor=cursor;

        String confJid=join.jid;
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
        createForm(display, name, room, server, nick, password, autojoin);
    }
    
     private void createForm(final Display display, String name, String room, String server, String nick, final String password, boolean autojoin) {
        this.display=display;
        parentView=display.getCurrent();
        
        Form formJoin=new Form(SR.MS_JOIN_CONFERENCE);

        roomField=new TextField(SR.MS_ROOM, room, 64, TextField.ANY);
        formJoin.append(roomField);
        
        hostField=new TextFieldCombo(SR.MS_AT_HOST, server, 64, TextField.ANY, "muc-host", display);
        TextFieldCombo.setLowerCaseLatin(hostField); 
        formJoin.append(hostField);
        
        if (nick==null) nick=sd.account.getNickName();
        nickField=new TextFieldCombo(SR.MS_NICKNAME, nick, 32, TextField.ANY, "roomnick", display);
        formJoin.append(nickField);
        
        msgLimitField=new NumberField(SR.MS_MSG_LIMIT, cf.confMessageCount, 0, 100);
        formJoin.append(msgLimitField);
        
        nameField=new TextField(SR.MS_DESCRIPTION, name, 128, TextField.ANY);
        formJoin.append(nameField);
        
        passField=new TextField(SR.MS_PASSWORD, password, 32, TextField.ANY | TextField.SENSITIVE );
        formJoin.append(passField);

        AutoJoin=new ChoiceGroup(SR.MS_SET, Choice.MULTIPLE);
        AutoJoin.append(SR.MS_AUTOLOGIN, null);
        
        boolean aa[]={
            autojoin,
        };
        this.aa=aa;
        AutoJoin.setSelectedFlags(aa);
        formJoin.append(AutoJoin);
        
        formJoin.addCommand(cmdJoin);
        formJoin.addCommand(cmdAdd);
        formJoin.addCommand(cmdEdit);
        formJoin.addCommand(cmdCancel);
        formJoin.setCommandListener(this);
        
        display.setCurrent(formJoin);
    }
     
    public void commandAction(Command c, Displayable d){
        if (c==cmdCancel) { destroyView(); }
        
        sndprs=true;
        
        String nick=nickField.getString();
        String name=nameField.getString();
        String host=hostField.getString();
        String room=roomField.getString();
        String pass=passField.getString();
        int msgLimit=msgLimitField.getValue();
        
        AutoJoin.getSelectedFlags(aa);
        boolean autojoin=aa[0];
        
        if (nick.length()==0) return;
        if (room.length()==0) return;
        if (host.length()==0) return;
        
        StringBuffer gchat=new StringBuffer(room.trim());
        gchat.append('@');
        gchat.append(host.trim());
        
        if (name.length()==0) name=gchat.toString();
        
        saveMsgCount(msgLimit);
            
        if (c==cmdEdit) {
            StaticData.getInstance().roster.bookmarks.removeElement(editConf);
            StaticData.getInstance().roster.bookmarks.insertElementAt(new BookmarkItem(name, gchat.toString(), nick, pass, autojoin), cursor);
            new BookmarkQuery(BookmarkQuery.SAVE);
            display.setCurrent(sd.roster);
        } else if (c==cmdAdd) {
            new Bookmarks(display, new BookmarkItem(name, gchat.toString(), nick, pass, autojoin));
        } else if (c==cmdJoin) {
            try {
                cf.defGcRoom=room+"@"+host;
                cf.saveToStorage();
                gchat.append('/');
                gchat.append(nick);
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
    
    private void saveMsgCount(int msgLimit) {
        if (cf.confMessageCount!=msgLimit) {
            cf.confMessageCount=msgLimit;
            cf.saveToStorage();
        }
    }
    
    public static void join(String name, String jid, String pass, int maxStanzas) {
        StaticData sd=StaticData.getInstance();
        
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
        
        if (sndprs) {
            //sd.roster.sendPresence(name, null, x, false);
            int status=StaticData.getInstance().roster.myStatus.getImageIndex();
            if (status==Presence.PRESENCE_INVISIBLE) 
                status=Presence.PRESENCE_ONLINE;
            sd.roster.sendDirectPresence(status, jid, x);
            sndprs=false;
        }
        
        sd.roster.reEnumRoster();
    }
    
    public void destroyView(){
        if (parentView!=null) 
            display.setCurrent(parentView);
    }
}
