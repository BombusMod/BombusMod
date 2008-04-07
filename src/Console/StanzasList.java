/*
 * StanzasList.java
 *
 * Created on 7 јпрель 2008 г., 13:48
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package Console;

import Client.Msg;
import java.util.Vector;

/**
 *
 * @author ad
 */
public class StanzasList {
    
    Vector stanzas=new Vector();
    
    boolean enabled = false;
    
    private static StanzasList instance;
    
    public static StanzasList getInstance(){
	if (instance==null) {
	    instance=new StanzasList();
	}
	return instance;
    }
    
    public Msg msg(int index){
	try {
            Msg msg=(Msg)stanzas.elementAt(index);
	    return msg;
	} catch (Exception e) {}
	return null;
    }

    public void add(String msg, int type) {
	try {
            if (enabled) {
                Msg stanza=new Msg(type, "local", null, msg.toString());
                stanza.itemCollapsed=true;
                stanzas.addElement(stanza);
            }
	} catch (Exception e) {}
    }

    public int size(){
	return stanzas.size();
    }

    void deleteAll() {
        stanzas.removeAllElements();
    }
}
