/*
 * IqCheckers.java
 *
 * Created on 23 ������� 2007 �., 15:52
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package xmpp.extensions;

import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.datablocks.*;

/**
 *
 * @author ad
 */
public class IqCheckers extends Iq {
    
    // constructs checkers request
    public IqCheckers(String to) {
        super(to, Iq.TYPE_GET, "checkers");
        addChildNs("query", "checkers").setAttribute("state", "request");
    }
    
    // constructs checkers answer
    public IqCheckers(JabberDataBlock request, boolean answer) {
        super(request.getAttribute("from"), Iq.TYPE_RESULT, "checkers" );
        addChildNs("query", "checkers").setAttribute("state", (answer)?"start":"cancel");
    }
    
    // constructs checkers answer
    public IqCheckers(String to, boolean answer) {
        super(to, Iq.TYPE_RESULT, "checkers" );
        addChildNs("query", "checkers").setAttribute("state", (answer)?"start":"cancel");
    }
    
}
