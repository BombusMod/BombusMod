/*
 * BookmarkItem.java
 *
 * Created on 17.09.2005, 23:21
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
import Client.Config;
import Client.StaticData;
import com.alsutton.jabber.JabberDataBlock;
import images.RosterIcons;
import Colors.Colors;
import ui.*;

/**
 *
 * @author EvgS
 */
public class BookmarkItem extends IconTextElement{
    
    String desc;
    String jid;
    String nick;
    String password;
    boolean autojoin=false;
    boolean isUrl;
    
    private Config cf=Config.getInstance();
    
    public int getImageIndex(){ 
        if (isUrl) return RosterIcons.ICON_PRIVACY_ACTIVE;
        return (autojoin)? RosterIcons.ICON_GCJOIN_INDEX : RosterIcons.ICON_GROUPCHAT_INDEX;
    }

    public String toString(){
        if (desc.length()>0)
            return desc;
        
        return (nick==null)? jid: jid+'/'+nick;
    }
    public String getJidNick() { return jid+'/'+nick; }
    public String getJid() { return jid; }

    public int getColor(){ return Colors.LIST_INK;}
    
    /** Creates a new instance of BookmarkItem */
    public BookmarkItem() {
        super(RosterIcons.getInstance());
    }
    
    public BookmarkItem(JabberDataBlock data) {
        this();
        isUrl=!data.getTagName().equals("conference");
        desc=data.getAttribute("name");
        try {
            String ajoin=data.getAttribute("autojoin").trim();
            autojoin=ajoin.equals("true") || ajoin.equals("1");
        } catch (Exception e) {}
        jid=data.getAttribute((isUrl)?"url":"jid");
        nick=data.getChildBlockText("nick");
        password=data.getChildBlockText("password");
        
        if ((autojoin==true) && (cf.autoJoinConferences==true)) {
            //System.out.println(jid+" autojoin");
            StringBuffer gchat=new StringBuffer();
            gchat.append(jid);
            gchat.append('/');
            gchat.append(nick);
            join(desc, gchat.toString(),password,cf.confMessageCount);
            gchat=null; //for nokia
        }
    }
    
    public BookmarkItem(String desc, String jid, String nick, String password, boolean autojoin){
        this();
        this.desc=desc;
        this.jid=jid;
        this.nick=nick;
        this.password=password;
        this.autojoin=autojoin;
    }
    
    public JabberDataBlock constructBlock() {
        JabberDataBlock data=new JabberDataBlock((isUrl)?"url":"conference", null, null);
        data.setAttribute("name", (desc=="")?jid:desc);
        data.setAttribute((isUrl)?"url":"jid", jid);
        data.setAttribute("autojoin", (autojoin)?"true":"false");
        if (nick!=null) if (nick.length()>0) data.addChild("nick",nick);
        if (password.length()>0) data.addChild("password",password);

        return data;
    }
    
    public static void join(String name, String jid, String pass, int maxStanzas) {
        StaticData sd=StaticData.getInstance();
        
        ConferenceGroup grp=sd.roster.initMuc(jid, pass);
        grp.desc=name;
        JabberDataBlock x=new JabberDataBlock("x", null, null);
        x.setNameSpace("http://jabber.org/protocol/muc");
        if (pass.length()!=0) {
            x.addChild("password", pass);
        }
        
        JabberDataBlock history=x.addChild("history", null);
        history.setAttribute("maxstanzas", String.valueOf(maxStanzas));
        history.setAttribute("maxchars","32768");
        try {
            long last=grp.getConference().lastMessageTime;
            long delay= ( grp.conferenceJoinTime - last ) /1000 ;
            if (last!=0) history.setAttribute("seconds",String.valueOf(delay)); // todo: change to since
        } catch (Exception e) {}

        sd.roster.sendPresence(jid, null, x, false);
        sd.roster.reEnumRoster();
    }

//#ifdef SECONDSTRING
//#         public String getSecondString() { 
//#             return null;
//#         }
//#endif
    
    public int compare(IconTextElement right) {
        String th=(nick==null)? jid: jid+'/'+nick;
        return th.compareTo(right.toString());
    }
            
}
