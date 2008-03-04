/*
 * JabberStreamShutdownException.java
 *
 * Created on 12 Июнь 2007 г., 1:03
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.alsutton.jabber;

import com.alsutton.xmlparser.EndOfXMLException;

/**
 *
 * @author Evg_S
 */
public class JabberStreamShutdownException extends EndOfXMLException {
    
    /** Creates a new instance of JabberStreamShutdownException */
    public JabberStreamShutdownException(String name) {
        super (name);
    }
    
}
