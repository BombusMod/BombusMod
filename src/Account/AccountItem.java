/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Account;

import images.RosterIcons;
import ui.IconTextElement;

/**
 *
 * @author Vitaly
 */
public class AccountItem extends IconTextElement {
    
    Account account;
    
    public AccountItem(Account a) {
        super(RosterIcons.getInstance());
        account = a;
    }
    
    public String toString() {
        StringBuffer s = new StringBuffer();
        if (account.getNickName().length() != 0) {
            s.append(account.getNickName());
        } else {
            s.append(account.userName).append('@').append(account.server);
        }
        s.append('/').append(account.resource);
        return s.toString();
    }
    
    public int getImageIndex(){ return account.active ? RosterIcons.ICON_PRESENCE_ONLINE : RosterIcons.ICON_PRESENCE_OFFLINE; }
    
    public String getTipString() { return account.getJid(); }
    
}
