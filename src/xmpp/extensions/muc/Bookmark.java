/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xmpp.extensions.muc;

import com.alsutton.jabber.JabberDataBlock;

/**
 *
 * @author Vitaly
 */
public class Bookmark {

    public String name;
    public String jid;
    public String nick;
    public String password;
    public boolean autojoin = false;
    public boolean isUrl;

    public Bookmark(JabberDataBlock data) {
        isUrl = !data.getTagName().equals("conference");
        name = data.getAttribute("name");
        try {
            String ajoin = data.getAttribute("autojoin").trim();
            autojoin = ajoin.equals("true") || ajoin.equals("1");
        } catch (Exception e) {
        }
        jid = data.getAttribute((isUrl) ? "url" : "jid");
        nick = data.getChildBlockText("nick");
        password = data.getChildBlockText("password");
    }

    public Bookmark(String name, String jid, String nick, String password, boolean autojoin) {
        this.name = name;
        this.jid = jid;
        this.nick = nick;
        this.password = password;
        this.autojoin = autojoin;
    }
    
    public JabberDataBlock constructBlock() {
        JabberDataBlock data = new JabberDataBlock((isUrl) ? "url" : "conference");
        data.setAttribute("name", (name.equals("")) ? jid : name);
        data.setAttribute((isUrl) ? "url" : "jid", jid);
        data.setAttribute("autojoin", (autojoin) ? "true" : "false");
        if (nick != null) {
            if (nick.length() > 0) {
                data.addChild("nick", nick);
            }
        }
        if (password.length() > 0) {
            data.addChild("password", password);
        }
        return data;
    }
}
