/*
 * eAccountsData.java
 *
 * Created on 24 январь 2008 г., 23:10
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package IE;

import Client.Config;
import java.util.Vector;

/**
 *
 * @author ad
 */
public class  eAccountsData {
    
    public  eAccountsData(String path) {
        Vector array=new Vector();

        new eData(array, path+"accounts.txt");
        array = null;
    }
    
}
