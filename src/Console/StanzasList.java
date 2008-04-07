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
    /*
    public void add(Msg msg) {
	try {
	    stanzas.insertElementAt(msg,0);
	} catch (Exception e) {}
    }
    */
    public void add(String msg) {
	try {
	    stanzas.insertElementAt(new Msg(Msg.MESSAGE_TYPE_IN, "local", null, msg.toString()),0);
	} catch (Exception e) {}
    }

    public int size(){
	return stanzas.size();
    }

    void deleteAll() {
        stanzas.removeAllElements();
    }
}
