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

import Client.*;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.TextField;
import locale.SR;
import ui.SplashScreen;
import ui.controls.AlertBox;
import ui.controls.form.CheckBox;
import ui.controls.form.DropChoiceBox;
import ui.controls.form.DefForm;
import ui.controls.form.LinkString;
import ui.controls.form.NumberInput;
import ui.controls.form.PasswordInput;
import ui.controls.form.TextInput;

/**
 *
 * @author ad
 */
public class AccountForm 
        extends DefForm {

    private final AccountSelect accountSelect;

    private TextInput userbox;
    private PasswordInput passbox;
    private TextInput servbox;
    private TextInput ipbox;
    private NumberInput portbox;
    private TextInput resourcebox;
    private TextInput nickbox;
    private CheckBox sslbox;
    private CheckBox plainPwdbox;
    private CheckBox compressionBox;
//#ifndef WMUC    
    private CheckBox confOnlybox;
//#endif    

    private LinkString linkRegister;
    
    private CheckBox dnsResolver;
	
    private NumberInput keepAlive;
    private DropChoiceBox keepAliveType;
    
//#if HTTPPOLL || HTTPCONNECT || HTTPBIND
//#     private TextInput proxyHost;
//#     private TextInput proxyPort;
//#     private TextInput proxyUser;
//#     private TextInput proxyPass;
//#     private CheckBox proxybox;
//#endif

    Account account;
    
    boolean newaccount;
    
    boolean showExtended;
    
    LinkString linkShowExtended;
    LinkString linkSave;

    private boolean doConnect;
    
    /** Creates a new instance of AccountForm
     * @param accountSelect
     * @param account
     */
    public AccountForm(final AccountSelect accountSelect, Account acc) {
        super(null);
	this.accountSelect = accountSelect;
        account=acc;
	newaccount=(account==null);
        if (newaccount)
            this.account=new Account();
	
        getMainBarItem().setElementAt((newaccount)?SR.MS_NEW_ACCOUNT:(account.toString()), 0);

        userbox = new TextInput(sd.canvas, SR.MS_USERNAME, account.getUserName(), null, TextField.ANY); //, 64, TextField.ANY
        itemsList.addElement(userbox);
        
        servbox = new TextInput(sd.canvas, SR.MS_SERVER, account.getServer(), null, TextField.ANY);//, 64, TextField.ANY
        itemsList.addElement(servbox);

	passbox = new PasswordInput(sd.canvas,  SR.MS_PASSWORD, account.getPassword());//, 64, TextField.PASSWORD
        itemsList.addElement(passbox);
        
        nickbox = new TextInput(sd.canvas, SR.MS_NICKNAME, account.getNick(), null, TextField.ANY);//64, TextField.ANY
        itemsList.addElement(nickbox);
        
        linkRegister = new LinkString(SR.MS_REGISTER_ACCOUNT) {

            public void doAction() {
                new AccountRegister(accountSelect);
            }
        };
        
        if (newaccount)
            itemsList.addElement(linkRegister);
        
        linkShowExtended = new LinkString(SR.MS_EXTENDED_SETTINGS) { public void doAction() { showExtended(); } };
        itemsList.addElement(linkShowExtended);
        
        linkSave = new LinkString(SR.MS_SAVE) { public void doAction() { cmdOk(); } };
        itemsList.addElement(linkSave);        
    }
    
    public void showExtended() {
        showExtended=true;
        itemsList.removeElement(linkShowExtended);
        itemsList.removeElement(linkSave);
        
        if (!newaccount)
            itemsList.addElement(linkRegister);
        
        ipbox = new TextInput(sd.canvas, SR.MS_HOST_IP, account.getHostAddr(), null, TextField.ANY);//, 64, TextField.ANY
        portbox = new NumberInput(sd.canvas,  SR.MS_PORT, Integer.toString(account.getPort()), 0, 65535);//, 0, 65535
        
                
        dnsResolver = new CheckBox(SR.MS_USE_DNS_SRV_RESOLVER, account.getDnsResolver()); 
        sslbox = new CheckBox(SR.MS_SSL, account.getUseSSL());
        plainPwdbox = new CheckBox(SR.MS_PLAIN_PWD, account.getPlainAuth());
        compressionBox = new CheckBox(SR.MS_COMPRESSION, account.useCompression());
//#ifndef WMUC        
        confOnlybox = new CheckBox(SR.MS_CONFERENCES_ONLY, account.isMucOnly());
//#endif        
//#if HTTPCONNECT
//#        proxybox = new CheckBox(/*SR.MS_PROXY_ENABLE*/"Proxy connect", account.isEnableProxy());
//#elif HTTPPOLL        
//#        proxybox = new CheckBox("HTTP Polling", account.isEnableProxy());
//#elif HTTPBIND
//#         proxybox = new CheckBox("XMPP BOSH", account.isEnableProxy());
//#endif
        
        itemsList.addElement(dnsResolver);
        itemsList.addElement(sslbox);
        itemsList.addElement(plainPwdbox);
        itemsList.addElement(compressionBox);
//#ifndef WMUC        
        itemsList.addElement(confOnlybox);
//#endif        
//#if HTTPCONNECT || HTTPBIND || HTTPPOLL
//#         itemsList.addElement(proxybox);
//#endif

//#ifndef HTTPBIND
        // TODO: make available when "XMPP BOSH" is unchecked
        keepAliveType=new DropChoiceBox(SR.MS_KEEPALIVE);
        keepAliveType.add("by socket");
        keepAliveType.add("1 byte");
        keepAliveType.add("<iq/>");
        keepAliveType.add("ping");
        keepAliveType.setSelectedIndex(account.getKeepAliveType());
        keepAlive = new NumberInput(sd.canvas,  SR.MS_KEEPALIVE_PERIOD, Integer.toString(account.getKeepAlivePeriod()), 10, 2048);//10, 2096
        itemsList.addElement(keepAliveType);
//#endif
        resourcebox = new TextInput(sd.canvas, SR.MS_RESOURCE, account.getResource(), null, TextField.ANY);//64, TextField.ANY
        
//#if HTTPCONNECT
//# 	proxyHost = new TextInput(sd.canvas,/*SR.MS_PROXY_HOST*/"Proxy name/IP", account.getProxyHostAddr(), null, TextField.URL);//32, TextField.URL
//# 	proxyPort = new NumberInput(sd.canvas, /*SR.MS_PROXY_PORT*/"Proxy port", Integer.toString(account.getProxyPort()), 0, 65535);//, 0, 65535
//#         proxyUser = new TextInput(sd.canvas,/*SR.MS_PROXY_HOST*/"Proxy user", account.getProxyUser(), null, TextField.URL);//32, TextField.URL
//#         proxyPass = new TextInput(sd.canvas,/*SR.MS_PROXY_HOST*/"Proxy pass", account.getProxyPass(), null, TextField.URL);//32, TextField.URL
//#elif HTTPPOLL        
//# 	proxyHost = new TextInput(sd.canvas, "HTTP Polling URL (http://server.tld:port)", account.getProxyHostAddr(), null, TextField.URL);//32, TextField.URL
//#elif HTTPBIND
//#         proxyHost = new TextInput(sd.canvas, "BOSH CM (http://server.tld:port)", account.getProxyHostAddr(), null, TextField.URL);//32, TextField.URL
//#endif
        
        itemsList.addElement(ipbox);
        itemsList.addElement(portbox);
//#ifndef HTTPBIND
        itemsList.addElement(keepAlive);
//#endif
        itemsList.addElement(resourcebox);
        
//#if HTTPCONNECT
//# 	itemsList.addElement(proxyHost);
//# 	itemsList.addElement(proxyPort);
//#         itemsList.addElement(proxyUser);
//#         itemsList.addElement(proxyPass);
//#elif HTTPPOLL || HTTPBIND
//#         itemsList.addElement(proxyHost);
//#endif
        itemsList.addElement(linkSave);
    }
    
    public void cmdOk() {
        String user = userbox.getValue().trim().toLowerCase();
        String server = servbox.getValue().trim().toLowerCase();
        String pass = passbox.getValue();

        int at = user.indexOf('@');
        if (at>-1) {
            server=user.substring(at+1);
            user=user.substring(0, at);
        }
        if (server.length()==0 || user.length()==0 || pass.length()==0)
            return;
        
        account.setUserName(user);
        account.setServer(server);
        account.setPassword(pass);
        account.setNick(nickbox.getValue());
        
        if (showExtended) {
            account.setDnsResolver(dnsResolver.getValue());
            account.setPort(Integer.parseInt(portbox.getValue()));
            account.setHostAddr(ipbox.getValue());
            account.setResource(resourcebox.getValue());
            account.setUseSSL(sslbox.getValue());
            account.setPlainAuth(plainPwdbox.getValue());
            account.setUseCompression(compressionBox.getValue());
//#ifndef WMUC            
            account.setMucOnly(confOnlybox.getValue());
//#endif            
//#if HTTPCONNECT || HTTPPOLL || HTTPBIND
//#             account.setEnableProxy(proxybox.getValue());
//#endif
            
//#if HTTPPOLL || HTTPCONNECT || HTTPBIND
//#             account.setProxyHostAddr(proxyHost.getValue());
//#if HTTPCONNECT
//#             account.setProxyPort(Integer.parseInt(proxyPort.getValue()));
//#
//#             account.setProxyUser(proxyUser.getValue());
//#             account.setProxyPass(proxyPass.getValue());
//#endif
//#endif
//#ifndef HTTPBIND
            account.setKeepAlivePeriod(Integer.parseInt(keepAlive.getValue()));
            account.setKeepAliveType(keepAliveType.getValue());
//#endif
        }

        if (newaccount) 
            accountSelect.itemsList.addElement(account);
        accountSelect.rmsUpdate();
        accountSelect.commandState();

        doConnect=true;        
        destroyView();
        account=null;
    }

    public void destroyView(){
        if (newaccount && doConnect) {
            new AlertBox(SR.MS_CONNECT_TO, account.getBareJid()+"?") {
                public void yes() {
                    SplashScreen.getInstance().setExit(sd.roster);
                    startLogin(true);
                }
                public void no() { 
                    startLogin(false);
                    accountSelect.show();
                }
            };            
        } else
            accountSelect.show();
    }
    
    private void startLogin(boolean login){
        Config.getInstance().accountIndex=accountSelect.itemsList.size()-1;
        Account.loadAccount(login, Config.getInstance().accountIndex);
        SplashScreen.getInstance().close();
    }
    
    protected boolean key(int keyCode, boolean key_long) {
        if (key_long) {
            switch (keyCode) {
                case Canvas.KEY_NUM6:
                    Config.fullscreen = !Config.fullscreen;
                    cf.saveToStorage();
                    sd.canvas.setFullScreenMode(Config.fullscreen);
                    return true;
            }
        }

        return super.key(keyCode, key_long);
    }
}
