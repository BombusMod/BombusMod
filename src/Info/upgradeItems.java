/*
 * upgradeItems.java
 *
 * Created on 15 јпрель 2008 г., 11:00
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package Info;

import Client.Msg;
import java.util.Vector;

/**
 *
 * @author ad
 */
public class upgradeItems {
    
    Vector items=new Vector();
    
    private static upgradeItems instance;
    
    public static upgradeItems getInstance(){
	if (instance==null) {
	    instance=new upgradeItems();
	}
	return instance;
    }
    
    public int size(){
	return items.size();
    }
    
    public void add(Msg msg) {
        items.addElement(msg);
    }
    
    public void clearAll() {
	items.removeAllElements();
    }
    
    public Msg msg(int index){
	try {
            Msg msg=(Msg)items.elementAt(index);
	    return msg;
	} catch (Exception e) {}
	return null;
    }
}
