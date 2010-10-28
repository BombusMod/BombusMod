/*
 * AccountRegister.java
 *
 * Created on 24.04.2005, 2:36
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

package Account;
import ServiceDiscovery.DiscoForm;
import com.alsutton.jabber.*;
import com.alsutton.jabber.datablocks.*;
import java.util.Vector;
import locale.SR;
import ui.*;
import ui.controls.form.DefForm;
import ui.controls.form.DropChoiceBox;
import ui.controls.form.TextInput;
import xmpp.extensions.IqRegister;
import xmpp.extensions.IqRegister.RegistrationListener;

/**
 *
 * @author Evg_S
 */
public class AccountRegister
        extends DefForm
        implements         
            JabberListener,
            RegistrationListener,
            Runnable
{
    private Account raccount;
    private JabberStream theStream ;
    private SplashScreen splash;
    private AccountSelect as;

    private DropChoiceBox serverChoice;

    
    /** Creates a new instance of AccountRegister
     * @param account
     */
    public AccountRegister(AccountSelect parent) {
        super(SR.MS_REGISTER);
        as = parent;
        splash = SplashScreen.getInstance();
        splash.setExit(as);
        splash.setProgress(SR.MS_STARTUP, 5);
        //splash.addCommand(cmdCancel);
        //splash.setCommandListener(this);
        serverChoice = new DropChoiceBox("Select server for registration");
        serverChoice.items.addElement("jabber.ru");
        serverChoice.items.addElement("xmpp.ru");
        itemsList.addElement(serverChoice);
    }
    public void cmdOk() {
        raccount = new Account();
        String serverName = (String) serverChoice.items.elementAt(serverChoice.getSelectedIndex());
        raccount.setServer(serverName);
        StringBuffer caption = new StringBuffer();
        caption.append(SR.MS_REGISTER).append(" ").append(serverName);
        setMainBarItem(new MainBar(caption.toString()));
        itemsList.removeAllElements();
        midlet.BombusMod.getInstance().setDisplayable(splash);
        new Thread(this).start();
    }
    public void run() {
        try {
            splash.setProgress(SR.MS_CONNECT_TO_ + raccount.getServer(), 30);
            //give a chance another thread to finish ui
            Thread.sleep(500);
            theStream = raccount.openJabberStream();
            new Thread(theStream).start();
            new Thread(theStream.dispatcher).start();

            theStream.setJabberListener(this);
            theStream.initiateStream();
        } catch (Exception e) {
            splash.setFailed();
        }

    }
    
    public void connectionTerminated( Exception e) {}
    

    public void beginConversation() {
        splash.setProgress(SR.MS_REGISTERING, 60);
        theStream.addBlockListener(new IqRegister(this));
        Iq iqreg = new Iq(null, Iq.TYPE_GET, "regac" + System.currentTimeMillis());
        JabberDataBlock query = iqreg.addChild("query", null);
        query.setNameSpace(IqRegister.NS_REGS);
        theStream.send(iqreg);
    }
    
    public int blockArrived( JabberDataBlock data ) {                
        return JabberBlockListener.BLOCK_REJECTED;
    }
    
    /*public void commandAction(Command c, Displayable d) {
        splash.setCommandListener(null);
        splash.removeCommand(cmdCancel);
        try {
            theStream.close();
        } catch (Exception e) { 
            //e.printStackTrace();
        }
        as.rmsUpdate();
        splash.close(as);        
    }*/

    public void registrationFormNotify(JabberDataBlock data) {
        new DiscoForm(data, theStream, "register"+System.currentTimeMillis(), "query").fetchMediaElements(data.getChildBlock("query").getChildBlocks());
    }

    public void registrationFailed(String errorText) {
        splash.setProgress(errorText, 100);
        midlet.BombusMod.getInstance().setDisplayable(splash);
        theStream.close();
    }

    public String touchLeftCommand() {
        return SR.MS_SEND;
    }

    public void registrationSuccess(String user, String pass) {
        as.rmsUpdate();
        String success = SR.MS_DONE;
        splash.setProgress(success, 100);
        midlet.BombusMod.getInstance().setDisplayable(splash);
        theStream.close();
    }

    public Vector construct(Vector items) {
        Vector result = new Vector();
        int size = items.size();
        for (int i = 0; i < size; i++) {
            Object current = items.elementAt(i);
            if (current instanceof TextInput) {
                JabberDataBlock res = new JabberDataBlock(null, ((TextInput)current).caption, ((TextInput)current).getValue());
                result.addElement(res);
            }
        }
        return result;
    }

    public void dispatcherException(Exception e, JabberDataBlock data) {
    }
}
