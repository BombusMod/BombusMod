/*
 * AccountForm.java
 *
 * Created on 20.05.2008, 13:05
 *
 * Copyright (c) 2006-2008, Daniel Apatin (ad), http://apatin.net.ru
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
 */
package Account;

import xmpp.Account;
import Client.*;
import Info.Version;
import locale.SR;
import ui.SplashScreen;
import ui.controls.AlertBox;
import ui.controls.form.CheckBox;
import ui.controls.form.DefForm;
import ui.controls.form.LinkString;
import ui.controls.form.NumberInput;
import ui.controls.form.PasswordInput;
import ui.controls.form.TextInput;
//#ifdef FILE_IO
//# import io.file.browse.Browser;
//# import io.file.browse.BrowserListener;
//#endif
//#if HTTPCONNECT || HTTPPOLL || HTTPBIND
//# import javax.microedition.lcdui.TextField;
//#endif
import xmpp.Jid;

/**
 *
 * @author ad
 */
public class AccountForm
        extends DefForm
//#ifdef IMPORT_EXPORT
//#         implements BrowserListener
//#endif
{
    private final AccountSelect accountSelect;
    private TextInput userbox;
    private PasswordInput passbox;
    private TextInput ipbox;
    private NumberInput portbox;
    private TextInput resourcebox;
    private TextInput nickbox;
    private CheckBox plainPwdbox;
    private CheckBox compressionBox;
//#ifndef WMUC    
    private CheckBox confOnlybox;
//#endif    
    private LinkString linkRegister;
//#ifdef IMPORT_EXPORT
//#     private LinkString linkImport;
//#endif
    private NumberInput keepAlive;
//#if HTTPPOLL || HTTPCONNECT || HTTPBIND
//#     private TextInput proxyHost;
//#     private TextInput proxyPort;
//#     private TextInput proxyUser;
//#     private TextInput proxyPass;
//#     private CheckBox proxybox;
//#endif
    AccountItem item;
    boolean newaccount;
    boolean showExtended;
    LinkString linkShowExtended;
    LinkString linkSave;
    private boolean doConnect;

    /**
     * Creates a new instance of AccountForm
     *
     * @param accountSelect
     * @param account
     */
    public AccountForm(final AccountSelect accountSelect, AccountItem item) {
        super(null);
        this.accountSelect = accountSelect;
        this.item = item;
        newaccount = (this.item == null);
        if (newaccount) {
            this.item = new AccountItem(new Account());
        }

        mainbar.setElementAt((newaccount) ? SR.MS_NEW_ACCOUNT : (this.item.toString()), 0);

        userbox = new TextInput(SR.MS_JID, newaccount ? 
                "" 
                : this.item.account.JID.getBare(), null);
        itemsList.addElement(userbox);

        passbox = new PasswordInput(SR.MS_PASSWORD, newaccount? "" : this.item.account.password);
        itemsList.addElement(passbox);

        nickbox = new TextInput(SR.MS_NICKNAME, this.item.account.nick, null);
        itemsList.addElement(nickbox);

        linkRegister = new LinkString(SR.MS_REGISTER_ACCOUNT) {

            public void doAction() {
                new AccountRegister(accountSelect);
            }
        };

        if (newaccount) {
            itemsList.addElement(linkRegister);
        }

//#ifdef IMPORT_EXPORT
//#         final BrowserListener listener = this;
//#         linkImport = new LinkString(SR.MS_LOAD_FROM_FILE) {
//# 
//#             public void doAction() {
//#                 new Browser(null, listener, false);
//#             }
//#         };
//#         itemsList.addElement(linkImport);
//#endif

        linkShowExtended = new LinkString(SR.MS_EXTENDED_SETTINGS) {

            public void doAction() {
                showExtended();
            }
        };
        itemsList.addElement(linkShowExtended);

        linkSave = new LinkString(SR.MS_SAVE) {

            public void doAction() {
                cmdOk();
            }
        };
        itemsList.addElement(linkSave);
    }

    public void showExtended() {
        showExtended = true;
        itemsList.removeElement(linkShowExtended);
        itemsList.removeElement(linkSave);

        if (!newaccount) {
            itemsList.addElement(linkRegister);
        }

        ipbox = new TextInput(SR.MS_HOST_IP, item.account.hostAddr, null);
        portbox = new NumberInput(SR.MS_PORT, Integer.toString(item.account.port), 0, 65535);


        plainPwdbox = new CheckBox(SR.MS_PLAIN_PWD, item.account.plainAuth);
        compressionBox = new CheckBox(SR.MS_COMPRESSION, item.account.useCompression());
//#ifndef WMUC        
        confOnlybox = new CheckBox(SR.MS_CONFERENCES_ONLY, item.account.mucOnly);
//#endif        
//#if HTTPCONNECT
//#         proxybox = new CheckBox(/*
//#                  * SR.MS_PROXY_ENABLE
//#                  */"Proxy connect", item.account.isEnableProxy());
//#elif HTTPPOLL        
//#        proxybox = new CheckBox("HTTP Polling", item.account.isEnableProxy());
//#elif HTTPBIND
//#         proxybox = new CheckBox("XMPP BOSH", item.account.isEnableProxy());
//#endif

        itemsList.addElement(plainPwdbox);
        itemsList.addElement(compressionBox);
//#ifndef WMUC        
        itemsList.addElement(confOnlybox);
//#endif        
//#if HTTPCONNECT || HTTPBIND || HTTPPOLL
//#         itemsList.addElement(proxybox);
//#endif

//#ifndef HTTPBIND
        keepAlive = new NumberInput(SR.MS_KEEPALIVE_PERIOD, Integer.toString(item.account.keepAlivePeriod), 10, 2048);//10, 2096        
//#endif
        resourcebox = new TextInput(SR.MS_RESOURCE, newaccount ? Version.NAME : 
                item.account.JID.resource, null);

//#if HTTPCONNECT
//#         proxyHost = new TextInput(/*
//#                  * SR.MS_PROXY_HOST
//#                  */"Proxy name/IP", item.account.proxyHostAddr, null, TextField.URL);
//#         proxyPort = new NumberInput(/*
//#                  * SR.MS_PROXY_PORT
//#                  */"Proxy port", Integer.toString(item.account.getProxyPort()), 0, 65535);
//#         proxyUser = new TextInput(/*
//#                  * SR.MS_PROXY_HOST
//#                  */"Proxy user", item.account.getProxyUser(), null, TextField.URL);
//#         proxyPass = new TextInput(/*
//#                  * SR.MS_PROXY_HOST
//#                  */"Proxy pass", item.account.getProxyPass(), null, TextField.URL);
//#elif HTTPPOLL        
//# 	proxyHost = new TextInput("HTTP Polling URL (http://server.tld:port)", item.account.proxyHostAddr, null, TextField.URL);
//#elif HTTPBIND
//#         proxyHost = new TextInput("BOSH CM (http://server.tld:port)", item.account.proxyHostAddr, null, TextField.URL);
//#endif

        itemsList.addElement(ipbox);
        itemsList.addElement(portbox);
//#ifndef HTTPBIND
        itemsList.addElement(keepAlive);
//#endif
        itemsList.addElement(resourcebox);

//#if HTTPCONNECT
//#         itemsList.addElement(proxyHost);
//#         itemsList.addElement(proxyPort);
//#         itemsList.addElement(proxyUser);
//#         itemsList.addElement(proxyPass);
//#elif HTTPPOLL || HTTPBIND
//#         itemsList.addElement(proxyHost);
//#endif
        itemsList.addElement(linkSave);
    }

    public void cmdOk() {
        String user = userbox.getValue().trim().toLowerCase();
        String pass = passbox.getValue();
        String resource = Version.NAME;
        if (resourcebox != null)
            resource = resourcebox.getValue();
        String server = "";
        int at = user.indexOf('@');
        if (at > -1) {
            server = user.substring(at + 1);
            user = user.substring(0, at);
        }
        if (server.length() == 0 || user.length() == 0 || pass.length() == 0) {
            return;
        }

        item.account.JID = new Jid(user, server, resource);
        item.account.password = pass;
        item.account.nick = nickbox.getValue();

        if (showExtended) {
            String hostname = ipbox.getValue();
            item.account.port = Integer.parseInt(portbox.getValue());
            item.account.hostAddr = hostname;
            item.account.plainAuth = plainPwdbox.getValue();
            item.account.compression = compressionBox.getValue();
//#ifndef WMUC            
            item.account.mucOnly = confOnlybox.getValue();
//#endif            
//#if HTTPCONNECT || HTTPPOLL || HTTPBIND
//#             item.account.setEnableProxy(proxybox.getValue());
//#endif

//#if HTTPPOLL || HTTPCONNECT || HTTPBIND
//#             item.account.proxyHostAddr = proxyHost.getValue();
//#if HTTPCONNECT
//#             item.account.setProxyPort(Integer.parseInt(proxyPort.getValue()));
//# 
//#             item.account.setProxyUser(proxyUser.getValue());
//#             item.account.setProxyPass(proxyPass.getValue());
//#endif
//#endif
//#ifndef HTTPBIND
            item.account.keepAlivePeriod = Integer.parseInt(keepAlive.getValue());
//#endif
        }

        if (newaccount) {
            accountSelect.itemsList.addElement(item);
        }
        accountSelect.rmsUpdate();
        accountSelect.commandState();

        doConnect = true;
        destroyView();
        item = null;
    }

    public void destroyView() {
        if (newaccount && doConnect) {
            new AlertBox(SR.MS_CONNECT_TO, item.account.JID.getBare() + "?") {

                public void yes() {
                    SplashScreen.getInstance().setExit(sd.roster);
                    startLogin(true);
                }

                public void no() {
                    startLogin(false);
                    accountSelect.show();
                }
            };
        } else {
            accountSelect.show();
        }
    }

    private void startLogin(boolean login) {
        Config.getInstance().accountIndex = accountSelect.itemsList.size() - 1;
        sd.roster.loadAccount(login, Config.getInstance().accountIndex);
        SplashScreen.getInstance().destroyView();
    }

//#ifdef IMPORT_EXPORT
//#     public void BrowserFilePathNotify(String pathSelected) {
//#         new IE.Accounts(pathSelected, 0, false);
//#         accountSelect.loadAccounts();
//#         destroyView();
//#     }
//#endif
}
