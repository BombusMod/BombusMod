/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Account;

import io.NvStorage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import xmpp.Account;

/**
 *
 * @author Vitaly
 */
public class AccountStorage {

    public static Account createFromDataInputStream(DataInputStream inputStream) {
        int version = 0;
        Account a = new Account();
        try {
            version = inputStream.readByte();
            a.userName = inputStream.readUTF();
            a.password = inputStream.readUTF();
            a.server = inputStream.readUTF();
            a.hostAddr = inputStream.readUTF();
            a.port = inputStream.readInt();

            a.nick = inputStream.readUTF();
            a.resource = inputStream.readUTF();

            inputStream.readBoolean(); // was legacy ssl
            a.plainAuth = inputStream.readBoolean();
//#ifndef WMUC            
            a.mucOnly = inputStream.readBoolean();
//#else
//#             inputStream.readBoolean();
//#endif            

//#if HTTPPOLL || HTTPCONNECT || HTTPBIND
//#             a.setEnableProxy(inputStream.readBoolean());
//#             a.proxyHostAddr = inputStream.readUTF();
//#             a.setProxyPort(inputStream.readInt());
//#else
            inputStream.readBoolean();
            inputStream.readUTF();
            inputStream.readInt();
//#endif

            a.compression = inputStream.readBoolean();

            inputStream.readInt(); // was keep-alive type
            a.keepAlivePeriod = inputStream.readInt();

            inputStream.readBoolean(); //firstrun // was dnsresolver
//#ifdef HTTPCONNECT
//#             a.proxyUser = inputStream.readUTF();
//#             a.proxyPass = inputStream.readUTF();
//#else
            inputStream.readUTF();
            inputStream.readUTF();
//#endif
        } catch (IOException e) { /*
             * e.printStackTrace();
             */ }

        return (a.userName == null) ? null : a;
    }

    public static void saveToDataOutputStream(Account account, DataOutputStream outputStream) {

        if (account.hostAddr == null) {
            account.hostAddr = "";
        }
//#if HTTPPOLL || HTTPCONNECT || HTTPBIND
//#         if (account.proxyHostAddr == null) {
//#             account.proxyHostAddr = "";
//#         }
//#endif

        try {
            outputStream.writeByte(7);
            outputStream.writeUTF(account.userName);
            outputStream.writeUTF(account.password);
            outputStream.writeUTF(account.server);
            outputStream.writeUTF(account.hostAddr);
            outputStream.writeInt(account.port);

            outputStream.writeUTF(account.nick);
            outputStream.writeUTF(account.resource);

            outputStream.writeBoolean(false); // was legacy ssl
            outputStream.writeBoolean(account.plainAuth);

            outputStream.writeBoolean(account.mucOnly);

//#if HTTPPOLL || HTTPCONNECT || HTTPBIND
//#             outputStream.writeBoolean(account.enableProxy);
//#             outputStream.writeUTF(account.proxyHostAddr);
//#             outputStream.writeInt(account.proxyPort);
//#else
            outputStream.writeBoolean(false);
            outputStream.writeUTF("");
            outputStream.writeInt(0);
//#endif

            outputStream.writeBoolean(account.compression);

            outputStream.writeInt(0); // was keep-alive type
            outputStream.writeInt(account.keepAlivePeriod);

            outputStream.writeBoolean(true);  //firstrun // dns-resolver
//#ifdef HTTPCONNECT
//#             outputStream.writeUTF(account.proxyUser);
//#             outputStream.writeUTF(account.proxyPass);
//#else
            outputStream.writeUTF("");
            outputStream.writeUTF("");
//#endif

        } catch (IOException e) {
            //e.printStackTrace();
        }
    }
    
    public static Account createFromStorage(int index) {
        Account a = null;
        DataInputStream is = NvStorage.ReadFileRecord("accnt_db", 0); //storage
        if (is == null) {
            return null;
        }
        try {
            do {
                if (is.available() == 0) {
                    a = null;
                    break;
                }
                a = AccountStorage.createFromDataInputStream(is);
                index--;
            } while (index > -1);
            is.close();
        } catch (Exception e) { /*
             * e.printStackTrace();
             */ }
        return a;
    }
}
