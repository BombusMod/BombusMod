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
import javax.microedition.lcdui.TextField;
import locale.SR;
import ui.SplashScreen;
import ui.VirtualList;
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
//#if HTTPCONNECT
//#       private CheckBox proxybox;
//#elif HTTPPOLL        
      private CheckBox pollingbox;
//#endif
    private CheckBox registerbox;
    
    private CheckBox dnsResolver;
	
    private NumberInput keepAlive;
    private DropChoiceBox keepAliveType;
    
//#if HTTPPOLL || HTTPCONNECT  
    private TextInput proxyHost;
    private TextInput proxyPort;
    private TextInput proxyUser;
    private TextInput proxyPass;
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
    public AccountForm(AccountSelect accountSelect, Account account) {
        super(null);
	this.accountSelect = accountSelect;
        
	newaccount=(account==null);
	if (newaccount) account=new Account();
	this.account=account;
	
	
        getMainBarItem().setElementAt((newaccount)?SR.MS_NEW_ACCOUNT:(account.toString()), 0);

        userbox = new TextInput(SR.MS_USERNAME, account.getUserName(), null, TextField.ANY); //, 64, TextField.ANY
        itemsList.addElement(userbox);
        
        servbox = new TextInput(SR.MS_SERVER, account.getServer(), null, TextField.ANY);//, 64, TextField.ANY
        itemsList.addElement(servbox);

	passbox = new PasswordInput( SR.MS_PASSWORD, account.getPassword());//, 64, TextField.PASSWORD
        itemsList.addElement(passbox);
        
        nickbox = new TextInput(SR.MS_NICKNAME, account.getNick(), null, TextField.ANY);//64, TextField.ANY
        itemsList.addElement(nickbox);
        
        registerbox = new CheckBox(SR.MS_REGISTER_ACCOUNT, false); 
        
        if (newaccount)
            itemsList.addElement(registerbox);
        
        linkShowExtended = new LinkString(SR.MS_EXTENDED_SETTINGS) { public void doAction() { showExtended(); } };
        itemsList.addElement(linkShowExtended);
        
        linkSave = new LinkString(SR.MS_SAVE) { public void doAction() { cmdOk(); } };
        itemsList.addElement(linkSave);

        show(parentView);
    }
    
    public void showExtended() {
        showExtended=true;
        itemsList.removeElement(linkShowExtended);
        itemsList.removeElement(linkSave);
        
        if (!newaccount)
            itemsList.addElement(registerbox);
        
	ipbox = new TextInput(SR.MS_HOST_IP, account.getHostAddr(), null, TextField.ANY);//, 64, TextField.ANY
        portbox = new NumberInput( SR.MS_PORT, Integer.toString(account.getPort()), 0, 65535);//, 0, 65535
        
                
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
       pollingbox = new CheckBox("HTTP Polling", account.isEnableProxy());
//#endif
        
        itemsList.addElement(dnsResolver);
        itemsList.addElement(sslbox);
        itemsList.addElement(plainPwdbox);
        itemsList.addElement(compressionBox);
//#ifndef WMUC        
        itemsList.addElement(confOnlybox);
//#endif        
//#if HTTPCONNECT
//#        itemsList.addElement(proxybox);
//#elif HTTPPOLL        
       itemsList.addElement(pollingbox);
//#endif

        keepAliveType=new DropChoiceBox(SR.MS_KEEPALIVE);
        keepAliveType.append("by socket");
        keepAliveType.append("1 byte");
        keepAliveType.append("<iq/>");
        keepAliveType.append("ping");
        keepAliveType.setSelectedIndex(account.getKeepAliveType());
        keepAlive = new NumberInput( SR.MS_KEEPALIVE_PERIOD, Integer.toString(account.getKeepAlivePeriod()), 10, 2048);//10, 2096
        itemsList.addElement(keepAliveType);
        
        resourcebox = new TextInput(SR.MS_RESOURCE, account.getResource(), null, TextField.ANY);//64, TextField.ANY
        
//#if HTTPCONNECT
//# 	proxyHost = new TextInput(/*SR.MS_PROXY_HOST*/"Proxy name/IP", account.getProxyHostAddr(), null, TextField.URL);//32, TextField.URL
//# 	proxyPort = new NumberInput( /*SR.MS_PROXY_PORT*/"Proxy port", Integer.toString(account.getProxyPort()), 0, 65535);//, 0, 65535
//#         proxyUser = new TextInput(/*SR.MS_PROXY_HOST*/"Proxy user", account.getProxyUser(), null, TextField.URL);//32, TextField.URL
//#         proxyPass = new TextInput(/*SR.MS_PROXY_HOST*/"Proxy pass", account.getProxyPass(), null, TextField.URL);//32, TextField.URL
//#elif HTTPPOLL        
	proxyHost = new TextInput("HTTP Polling URL (http://server.tld:port)", account.getProxyHostAddr(), null, TextField.URL);//32, TextField.URL
//#endif
        
        itemsList.addElement(ipbox);
        itemsList.addElement(portbox);
        
        itemsList.addElement(keepAlive);
        itemsList.addElement(resourcebox);
        
//#if HTTPCONNECT
//# 	itemsList.addElement(proxyHost);
//# 	itemsList.addElement(proxyPort);
//#         itemsList.addElement(proxyUser);
//#         itemsList.addElement(proxyPass);
//#elif HTTPPOLL        
	itemsList.addElement(proxyHost);
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
        
        boolean registerNew = false;
        
        if (newaccount)
            registerNew=registerbox.getValue();
        
        if (showExtended) {
            registerNew=registerbox.getValue();
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
//#if HTTPCONNECT
//#             account.setEnableProxy(proxybox.getValue());
//#elif HTTPPOLL
            account.setEnableProxy(pollingbox.getValue());
//#endif
            
//#if HTTPPOLL || HTTPCONNECT
            account.setProxyHostAddr(proxyHost.getValue());
//#if HTTPCONNECT
//#             account.setProxyPort(Integer.parseInt(proxyPort.getValue()));
//#
//#             account.setProxyUser(proxyUser.getValue());
//#             account.setProxyPass(proxyPass.getValue());
//#endif
//#endif
        
            account.setKeepAlivePeriod(Integer.parseInt(keepAlive.getValue()));
            account.setKeepAliveType(keepAliveType.getValue());
        }

        if (newaccount) 
            accountSelect.accountList.addElement(account);
        accountSelect.rmsUpdate();
        accountSelect.commandState();

        doConnect=true;
        
        if (registerNew) {
            new AccountRegister(account,  (VirtualList)parentView);
        } else {
            destroyView();
        }
        account=null;
    }

    public void destroyView(){
        if (newaccount && doConnect) {
            new AlertBox(SR.MS_CONNECT_TO, account.getBareJid()+"?") {
                public void yes() { startLogin(true); }
                public void no() { startLogin(false); }
            };
        } else
            accountSelect.show();
    }
    
    private void startLogin(boolean login){
        Config.getInstance().accountIndex=accountSelect.accountList.size()-1;
        Account.loadAccount(login, Config.getInstance().accountIndex);
        SplashScreen.getInstance().close();
    }
    
    protected void keyRepeated(int keyCode) {
        super.keyRepeated(keyCode);
        if (kHold==keyCode) return;
        kHold=keyCode;
        
        if (keyCode==KEY_NUM6) {
            Config cf=Config.getInstance();
            Config.fullscreen=!Config.fullscreen;
            cf.saveToStorage();
            VirtualList.fullscreen=Config.fullscreen;
            StaticData.getInstance().roster.setFullScreenMode(Config.fullscreen);
        }
    }
}
