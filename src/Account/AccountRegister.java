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
import com.alsutton.jabber.*;
import com.alsutton.jabber.datablocks.*;
import java.util.Vector;
import javax.microedition.lcdui.*;
import locale.SR;
import ui.*;
import ui.controls.AlertBox;
import ui.controls.form.DefForm;
import ui.controls.form.DropChoiceBox;
import ui.controls.form.MultiLine;
import ui.controls.form.PasswordInput;
import ui.controls.form.TextInput;
import xmpp.extensions.IqRegister;
import xmpp.extensions.IqRegister.RegistrationListener;
import xmpp.extensions.XDataField;
import xmpp.extensions.XDataForm;

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
    private int state = 0; // server selection

    private Vector regFields;
    
    private boolean xdata = false;

    /** Creates a new instance of AccountRegister
     * @param account
     */
    public AccountRegister(AccountSelect parent) {
        super(SR.MS_REGISTER);
        as = parent;
        splash = SplashScreen.getInstance();
        splash.setProgress(SR.MS_STARTUP, 5);
        //splash.addCommand(cmdCancel);
        //splash.setCommandListener(this);
        serverChoice = new DropChoiceBox("Select server for registration");
        serverChoice.items.addElement("jabber.ru");
        serverChoice.items.addElement("xmpp.ru");
        itemsList.addElement(serverChoice);
    }
    public void cmdOk() {
        state++;
        switch (state) {
            case 1: // registration
                raccount = new Account();
                String serverName = (String) serverChoice.items.elementAt(serverChoice.getSelectedIndex());
                raccount.setServer(serverName);
                StringBuffer caption = new StringBuffer();
                caption.append(SR.MS_REGISTER).append(" ").append(serverName);
                setMainBarItem(new MainBar(caption.toString()));
                itemsList.removeAllElements();
                midlet.BombusMod.getInstance().setDisplayable(splash);
                new Thread(this).start();
                break;
            case 2: // send results
                JabberDataBlock submitForm;
                JabberDataBlock iqreg = new Iq(null, Iq.TYPE_SET, "regsubmit");
                JabberDataBlock query = iqreg.addChild("query", null);
                query.setNameSpace(IqRegister.NS_REGS);
                if (xdata) {
                   /* submitForm = new XDataForm(itemsList);
                    query.addChild(submitForm);*/
                } else {
                    Vector items = construct(itemsList);
                    int size = items.size();
                    for (int i = 0; i < size; i++) {
                        query.addChild(items.elementAt(i));
                    }
                }
                theStream.send(iqreg);
                break;
        }
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
    
    public void connectionTerminated( Exception e ) {}       
    

    public void beginConversation() {
        splash.setProgress(SR.MS_REGISTERING, 60);
        theStream.addBlockListener(new IqRegister(this));
        Iq iqreg = new Iq(null, Iq.TYPE_GET, "regac" + System.currentTimeMillis());
        JabberDataBlock query = iqreg.addChild("query", null);
        query.setNameSpace(IqRegister.NS_REGS);
        theStream.send(iqreg);
    }
    
    public int blockArrived( JabberDataBlock data ) {
        /*//destroyView();
        if (data instanceof Iq) {
            //theStream.close();
            int pgs=100;
            String type=data.getTypeAttribute();
            String mainbar=SR.MS_DONE; 
            if (type.equals("result")) {
                splash.removeCommand(cmdCancel);
                splash.addCommand(cmdOK);
            } else {
                pgs=0;
                mainbar=SR.MS_ERROR_ + XmppError.findInStanza(data).toString();
            }
            splash.setProgress(mainbar,pgs);
            theStream.close();
        }*/
        return JabberBlockListener.BLOCK_PROCESSED;
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

    public void registrationFormNotify(String title, String instructions, Vector registrationFields) {
        int size = registrationFields.size();
            if (title != null) {
                setMainBarItem(new MainBar(title));
            }
            if (instructions != null) {
                MultiLine item = new MultiLine("Instructions", instructions, sd.roster.getListWidth());
                item.selectable = true;
                itemsList.addElement(item);
            }
            for (int i = 0; i < size; i++) {
                Object current = registrationFields.elementAt(i);
                if (current instanceof String) { // plain form
                    if (current.equals("password")) {
                        itemsList.addElement(new PasswordInput(sd.canvas, (String)current, ""));
                    } else {
                        itemsList.addElement(new TextInput(sd.canvas, (String)current, "", "", TextField.ANY));
                    }
                } else if (current instanceof XDataField){
                    itemsList.addElement(((XDataField)current).formItem);
                    xdata = true;
                }
            }
            show();
    }

    public void registrationFailed(String errorText) {
        new AlertBox(SR.MS_ERROR, errorText) {

            public void yes() {
            }

            public void no() {
            }

        };
    }

    public String touchLeftCommand() {
        return SR.MS_SEND;
    }

    public void registrationSuccess() {
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
