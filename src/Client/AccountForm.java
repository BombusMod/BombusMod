/*
 * newAccountForm.java
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

package Client;

import javax.microedition.lcdui.Display;
import locale.SR;
import ui.SplashScreen;
import ui.controls.AlertBox;
import ui.controls.form.boldString;
import ui.controls.form.checkBox;
import ui.controls.form.choiceBox;
import ui.controls.form.defForm;
import ui.controls.form.numberInput;
import ui.controls.form.passwordInput;
import ui.controls.form.spacerItem;
import ui.controls.form.textInput;

/**
 *
 * @author ad
 */
public class AccountForm 
        extends defForm {

    private final AccountSelect accountSelect;

    private textInput userbox;
    private passwordInput passbox;
    private textInput servbox;
    private textInput ipbox;
    private numberInput portbox;
    private textInput resourcebox;
    private textInput nickbox;
    private checkBox sslbox;
    private checkBox plainPwdbox;
    private checkBox noComprbox;
    private checkBox confOnlybox;
//#if HTTPCONNECT
//#       private checkBox proxybox;
//#elif HTTPPOLL        
//#       private checkBox pollingbox;
//#endif
    private checkBox registerbox;
	
    private numberInput keepAlive;
    private choiceBox keepAliveType;
    
//#if HTTPPOLL || HTTPCONNECT  
//#     private textInput proxyHost;
//#     private textInput proxyPort;
//#endif

    Account account;
    
    boolean newaccount;
    
    /** Creates a new instance of newAccountForm */
    public AccountForm(AccountSelect accountSelect, Display display, Account account) {
        super(display, null);
	this.accountSelect = accountSelect;
        this.display=display;
        
	newaccount=(account==null);
	if (newaccount) account=new Account();
	this.account=account;
	
	String mainbar = (newaccount)?SR.MS_NEW_ACCOUNT:(account.toString());
        getMainBarItem().setElementAt(mainbar, 0);
        
        itemsList.addElement(new boldString(SR.MS_USERNAME));
        userbox = new textInput(display, account.getUserName()); //, 64, TextField.ANY
        itemsList.addElement(userbox);
    
        itemsList.addElement(new boldString(SR.MS_PASSWORD));
	passbox = new passwordInput(display, account.getPassword());//, 64, TextField.PASSWORD
        itemsList.addElement(passbox);
        
        itemsList.addElement(new boldString(SR.MS_SERVER));
        servbox = new textInput(display, account.getServer());//, 64, TextField.ANY
        itemsList.addElement(servbox);
        
        itemsList.addElement(new boldString(SR.MS_HOST_IP));
	ipbox = new textInput(display, account.getHostAddr());//, 64, TextField.ANY
        itemsList.addElement(ipbox);
        
        itemsList.addElement(new boldString(SR.MS_PORT));
        portbox = new numberInput(display, Integer.toString(account.getPort()), 0, 65535);//, 0, 65535
        itemsList.addElement(portbox);
        
        sslbox = new checkBox(SR.MS_SSL, account.getUseSSL()); itemsList.addElement(sslbox);
        plainPwdbox = new checkBox(SR.MS_PLAIN_PWD, account.getPlainAuth()); itemsList.addElement(plainPwdbox);
        noComprbox = new checkBox(SR.MS_NO_COMPRESSION, !account.useCompression()); itemsList.addElement(noComprbox);
        confOnlybox = new checkBox(SR.MS_CONFERENCES_ONLY, account.isMucOnly()); itemsList.addElement(confOnlybox);
//#if HTTPCONNECT
//#        proxybox = new checkBox("proxybox", SR.MS_PROXY_ENABLE, account.isEnableProxy()); itemsList.addElement(proxybox);
//#elif HTTPPOLL        
//#        pollingbox = new checkBox("pollingbox", "HTTP Polling", false); itemsList.addElement(pollingbox);
//#endif
        registerbox = new checkBox(SR.MS_REGISTER_ACCOUNT, false); itemsList.addElement(registerbox);
        
        itemsList.addElement(new boldString(SR.MS_KEEPALIVE));
        keepAliveType=new choiceBox();
        keepAliveType.append("by socket");
        keepAliveType.append("1 byte");
        keepAliveType.append("<iq/>");
        keepAliveType.append("ping");
        keepAliveType.setSelectedIndex(account.keepAliveType);
        itemsList.addElement(keepAliveType);

        itemsList.addElement(new boldString(SR.MS_KEEPALIVE_PERIOD));
        keepAlive = new numberInput(display, Integer.toString(account.keepAlivePeriod), 10, 2048);//10, 2096
        itemsList.addElement(keepAlive);

//#if HTTPPOLL || HTTPCONNECT  
//#     private textInput proxyHost;
//#     private textInput proxyPort;
//#endif
        
        itemsList.addElement(new boldString(SR.MS_RESOURCE));
        resourcebox = new textInput(display, account.getResource());//64, TextField.ANY
        itemsList.addElement(resourcebox);
        
        itemsList.addElement(new boldString(SR.MS_NICKNAME));
        nickbox = new textInput(display, account.getNick());//64, TextField.ANY
        itemsList.addElement(nickbox);

//#if HTTPCONNECT
//# 	itemsList.addElement(new boldString(SR.MS_PROXY_HOST);
//# 	proxyHost = new textInput(display, "proxyHost", account.getProxyHostAddr());//32, TextField.URL
//# 	itemsList.addElement(proxyHost);
//#
//# 	itemsList.addElement(new boldString(SR.MS_PROXY_PORT);
//# 	proxyPort = new textInput(display, "proxyPort", Integer.toString(account.getProxyPort()));//0, 65535
//# 	itemsList.addElement(proxyPort);
//#elif HTTPPOLL        
//# 	itemsList.addElement(new boldString(SR.MS_PROXY_HOST);
//# 	proxyHost = new textInput(display, "proxyHost", account.getProxyHostAddr());//32, TextField.URL
//# 	itemsList.addElement(proxyHost);
//#endif

        itemsList.addElement(new spacerItem());
        
        moveCursorTo(getNextSelectableRef(-1));
        attachDisplay(display);
    }
    
    public void cmdOk() {
        String user = userbox.getValue().trim().toLowerCase();
        String server = servbox.getValue().trim().toLowerCase();
        String pass = passbox.getValue();
        int port=Integer.parseInt(portbox.getValue());

        int at = user.indexOf('@');
        if (at>-1) {
            server=user.substring(at+1);
            user=user.substring(0, at);
        }
        if (server=="" || user=="" || pass=="" || port==0)
            return;
        
        account.setUserName(user);
        account.setServer(server);
        account.setPassword(pass);
        account.setPort(port);
        account.setHostAddr(ipbox.getValue());
        account.setResource(resourcebox.getValue());
        account.setNick(nickbox.getValue());
        account.setUseSSL(sslbox.getValue());
        account.setPlainAuth(plainPwdbox.getValue());
        account.setUseCompression(!noComprbox.getValue());
        account.setMucOnly(confOnlybox.getValue());

//#if HTTPPOLL || HTTPCONNECT            
//#         account.setEnableProxy(proxybox.getValue());
//#         account.setProxyHostAddr(proxyHost.getValue());
//#         account.setProxyPort(proxyPort.getValue());
//#endif

        account.keepAlivePeriod=Integer.parseInt(keepAlive.getValue());
        account.keepAliveType=keepAliveType.getValue();

        if (newaccount) 
            accountSelect.accountList.addElement(account);
        accountSelect.rmsUpdate();
        accountSelect.commandState();

        if (registerbox.getValue())
            new AccountRegister(account, display, parentView); 
        else {
            destroyView();
        }
        account=null;
    }

    public void destroyView(){
        if (newaccount) {
            new AlertBox(SR.MS_CONECT_TO, account.getBareJid()+"?", display, StaticData.getInstance().roster) {
                public void yes() { startLogin();}
                public void no() { if (display!=null) display.setCurrent(parentView); }
            };
        } else if (display!=null)  
            display.setCurrent(parentView);
    }
    
    private void startLogin(){
        Account.loadAccount(true);
        SplashScreen.getInstance().close();
    }
}
