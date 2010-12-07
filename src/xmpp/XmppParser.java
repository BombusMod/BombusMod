/*
 * xmppParser.java
 *
 * Created on 1 �?юнь 2008 г., 20:48
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package xmpp;

import Client.StaticData;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.datablocks.Iq;
import com.alsutton.jabber.datablocks.Message;
import com.alsutton.jabber.datablocks.Presence;
import java.util.Enumeration;
import java.util.Vector;
import xml.XMLEventListener;
import xml.XMLException;

/**
 *
 * @author evgs
 */
public abstract class XmppParser implements XMLEventListener {
    
    /** Creates a new instance of xmppParser */
    public XmppParser() {
    }

    
    /**
     * The method called when a tag is ended in the stream comming from the
     * server.
     *
     * @param name The name of the tag that has just ended.
     */
    
    public void tagEnd(String name) throws XMLException {

        Vector childs = currentBlock.getChildBlocks();
        if (childs != null) {
            childs.trimToSize();
        }

        JabberDataBlock parent = currentBlock.getParent();

        if (parent == null) {
            dispatchXmppStanza(currentBlock);
        } else {
            parent.addChild(currentBlock);
        }
        //dispatcher.broadcastJabberDataBlock( currentBlock );
        //System.out.println(currentBlock.toString());
        currentBlock = parent;
    }

    
    /**
     * Method called when an XML tag is started in the stream comming from the
     * server.
     *
     * @param name Tag name.
     * @param attributes The tags attributes.
     */
    
    public boolean tagStart(String name, Vector attributes) {
        StaticData.getInstance().updateTrafficIn();
        

        if (name.equals( "message" ) )
            currentBlock = new Message(currentBlock, attributes);
        else    if (name.equals("iq") ) 
            currentBlock = new Iq(currentBlock, attributes); 
        else    if (name.equals("presence") ) 
            currentBlock = new Presence(currentBlock, attributes); 
        else    if (name.equals("xml") )
            return false; 
        else    currentBlock = new JabberDataBlock(name, currentBlock, attributes);
        
        if ( name.equals("BINVAL") ){
                return true;
            }
        return false;
    }

    protected abstract void dispatchXmppStanza(JabberDataBlock currentBlock) ;
    /**
     * The current class being constructed.
     */
    
    protected JabberDataBlock currentBlock;

    
    /**
     * Method called when some plain text is encountered in the XML stream
     * comming from the server.
     *
     * @param text The plain text in question
     */
    
    public void plainTextEncountered(String text) {
        if( currentBlock != null ) {
            currentBlock.setText( text );
        }
    }

    
    public void binValueEncountered(byte[] binVaule) {
        if( currentBlock != null ) {
            //currentBlock.addText( text );
            currentBlock.addChild(binVaule);
        }
    }
}
