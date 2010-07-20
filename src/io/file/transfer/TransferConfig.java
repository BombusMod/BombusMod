/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package io.file.transfer;

import io.NvStorage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 *
 * @author Vitaly
 */
public class TransferConfig {
    private static TransferConfig instance;
    
    public String ftFolder = "";
    public String ftProxy = "socks5.juick.com";
    public int ftProxyPort = 7777;
    
    
    private TransferConfig() {
        readFromStorage();
    }
    public static TransferConfig getInstance(){
	if (instance==null) {
	    instance=new TransferConfig();	    
	}        
	return instance;
    }
    
    private void readFromStorage() {
        DataInputStream inputStream = NvStorage.ReadFileRecord("ft_config", 0);
        try {
            ftFolder = inputStream.readUTF();
            ftProxy = inputStream.readUTF();
            ftProxyPort = inputStream.readInt();
            inputStream.close();
        } catch (Exception e) { /*e.printStackTrace();*/ }
        
    }
    
    public void saveToStorage() {
       DataOutputStream outputStream=NvStorage.CreateDataOutputStream();
         try{
             outputStream.writeUTF(ftFolder);
             outputStream.writeUTF(ftProxy);
             outputStream.writeInt(ftProxyPort);             
         } catch (IOException e) { }
         NvStorage.writeFileRecord(outputStream, "ft_config", 0, true); 
    }
}
