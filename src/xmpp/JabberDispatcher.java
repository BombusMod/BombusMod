/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xmpp;

import Client.StaticData;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.JabberListener;
import com.alsutton.jabber.datablocks.Presence;
import locale.SR;
import xmpp.login.LoginListener;
import xmpp.login.NonSASLAuth;
import xmpp.login.SASLAuth;

/**
 *
 * @author Vitaly
 */
public class JabberDispatcher implements JabberListener {

    private StaticData sd = StaticData.getInstance();    

    public void beginConversation(LoginListener loginListener) { //todo: verify xmpp version
        // TODO: fixme
        // SplashScreen.getInstance().setExit(this);
        if (sd.theStream.isXmppV1()) {
            sd.theStream.addBlockListener(new SASLAuth(sd.account, loginListener, sd.theStream));
        } 
//#if NON_SASL_AUTH
        else {
            new NonSASLAuth(sd.account, loginListener, sd.theStream);
        }
//#endif
    }

    public void connectionTerminated(Exception e) {
        if (e != null) {
            sd.roster.errorLog("Exception in parser: " + e.getMessage());
            sd.roster.askReconnect(e);
        } else {
            sd.roster.setProgress(SR.MS_DISCONNECTED, 0);
            try {
                sd.roster.sendPresence(Presence.PRESENCE_OFFLINE, null);
            } catch (Exception e2) {
//#if DEBUG
//#                 e2.printStackTrace();
//#endif
            }
        }
        sd.roster.makeRosterOffline();
    }

    public void dispatcherException(Exception e, JabberDataBlock dataBlock) {
        sd.roster.errorLog("JabberDataBlockDispatcher exception\ndataBlock: " + dataBlock.toString());
        if (StaticData.Debug) {
            System.out.println("JabberDataBlockDispatcher exception\ndataBlock: " + dataBlock.toString());
            e.printStackTrace();
        }
    }
}
