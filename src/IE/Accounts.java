/*
 * Accounts.java
 *
 * Created on 14.06.2008., 17:12
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

package IE;

import Account.Account;
import io.NvStorage;
import io.file.FileIO;
import java.io.DataOutputStream;
import java.util.Vector;

/**
 *
 * @author ad
 */
public class Accounts {
    
    private final static String userName = "userName"; 
    private final static String server = "server"; 
    private final static String hostAddr = "hostAddr"; 
    private final static String port = "port"; 
    private final static String nick = "nick"; 
    private final static String resource = "resource"; 
    private final static String useSSL = "useSSL"; 
    private final static String plainAuth = "plainAuth"; 
    private final static String mucOnly = "mucOnly"; 
    private final static String compression = "compression"; 
    private final static String keepAliveType = "keepAliveType"; 
    private final static String keepAlivePeriod = "keepAlivePeriod";
    
    Vector accountList;

    private String file;

    /** Creates a new instance of Accounts */
    public Accounts(String path, int direction) {
        accountList=null;
        accountList=new Vector();
        this.file=path;
        
        if (direction==0) {
            importData();
        } else {
            exportData();
        }
        accountList=null;
    }
    
    
    public void importData() {
        String accounts="";
        
        FileIO fileIO=FileIO.createConnection(file);
        byte[] bodyMessage = fileIO.fileRead();

        if (bodyMessage!=null) {
            accounts=new String(bodyMessage, 0, bodyMessage.length);
        }
        if (accounts!=null) {
            try {
                int pos=0;
                int start_pos=0;
                int end_pos=0;
                
                boolean parse=true;

                while (parse) {
                    start_pos=accounts.indexOf("<a>", pos); 
                    end_pos=accounts.indexOf("</a>", pos);
                    
                    if (start_pos>-1 && end_pos>-1) {
                        pos=end_pos+4;
                        String tempstr=accounts.substring(start_pos+3, end_pos);

                        Account account=new Account();
                        account.setUserName(findBlock(tempstr, userName));
                        account.setServer(findBlock(tempstr, server));
                        account.setNick(findBlock(tempstr, nick));
                        
                        account.setPort(Integer.parseInt(findBlock(tempstr, port)));
                        account.setHostAddr(findBlock(tempstr, hostAddr));
                        account.setResource(findBlock(tempstr, resource));
                        account.setUseSSL((findBlock(tempstr, useSSL)=="1")?true:false);
                        account.setPlainAuth((findBlock(tempstr, plainAuth)=="1")?true:false);
                        account.setUseCompression((findBlock(tempstr, compression)=="1")?true:false);
                        account.setMucOnly((findBlock(tempstr, mucOnly)=="1")?true:false);
                        account.setKeepAlivePeriod(Integer.parseInt(findBlock(tempstr, keepAlivePeriod)));
                        account.setKeepAliveType(Integer.parseInt(findBlock(tempstr, keepAliveType)));
                        
                        accountList.addElement(account);
                    } else parse=false;
                }
                rmsUpdate();
            } catch (Exception e) { }
        }

        accounts=null;
        bodyMessage=null;
    }
    
    public void rmsUpdate(){
        DataOutputStream outputStream=NvStorage.CreateDataOutputStream();
        for (int i=0; i<getItemCount(); i++)
            getAccount(i).saveToDataOutputStream(outputStream);
        NvStorage.writeFileRecord(outputStream, Account.storage, 0, true);
    }
    
    
    private String findBlock(String source, String needle){
        String startItem="<"+needle+">";
        int start =source.indexOf(startItem);
        int end = source.indexOf("</"+needle+">");

        if (start>-1 && end>-1 && start!=end) {
            return source.substring(start+startItem.length(), end);
        }

        return "";
    }
    
    private String createBlock(String needle, String value){
        StringBuffer block = new StringBuffer("<")
            .append(needle)
            .append('>');
            if (value!=null)
                block.append(value);
            block.append("</")
            .append(needle)
            .append('>');
        
        return block.toString();
    }
    
    public void exportData() {
        StringBuffer body=new StringBuffer();
        
        getAccounts();
        
        for(int i=0; i<getItemCount(); i++){
            Account a=getAccount(i);
            StringBuffer account = new StringBuffer("<a>");
            account.append(createBlock(userName, a.getUserName()))
                   .append(createBlock(server, a.getServer()))
                   .append(createBlock(hostAddr, a.getHostAddr()))
                   .append(createBlock(port, Integer.toString(a.getPort())))
                   .append(createBlock(nick, a.getNick()))
                   .append(createBlock(resource, a.getResource()))
                   .append(createBlock(useSSL, (a.getUseSSL()?"1":"0")))
                   .append(createBlock(plainAuth, (a.getPlainAuth()?"1":"0")))
                   .append(createBlock(mucOnly, (a.isMucOnly()?"1":"0")))
                   .append(createBlock(compression, (a.useCompression()?"1":"0")))
                   .append(createBlock(keepAliveType, Integer.toString(a.getKeepAliveType())))
                   .append(createBlock(keepAlivePeriod, Integer.toString(a.getKeepAlivePeriod())))
                   .append("</a>\r\n");
            body.append(account);
        }

        byte[] bodyMessage=body.toString().getBytes();

        FileIO fileIO=FileIO.createConnection(file+"accounts.txt");
        fileIO.fileWrite(bodyMessage);

        bodyMessage=null;
        body=null;
    }
    
    private void getAccounts() {
        Account a;
        int index=0;
        do {
            a=Account.createFromStorage(index);
            if (a!=null) {
                accountList.addElement(a);
                index++;
            }
        } while (a!=null);
    }
    
    
    public int getItemCount() {
	return accountList.size();
    }
    
    public Account getAccount(int index) {
	return (Account) accountList.elementAt(index);
    }
}
