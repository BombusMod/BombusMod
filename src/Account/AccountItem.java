/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Account;

import xmpp.Account;
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
        return account.JID.toString();
    }
    
    public int getImageIndex(){ return account.active ? RosterIcons.ICON_PRESENCE_ONLINE : RosterIcons.ICON_PRESENCE_OFFLINE; }
    
    public String getTipString() { return account.JID.toString(); }
    
}
