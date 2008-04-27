/*
 * XmppError.java
 *
 * Created on 7 Сентябрь 2007 г., 18:21
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package xmpp;

import com.alsutton.jabber.*;
import java.util.Enumeration;

/**
 *
 * @author evgs
 */
public class XmppError {
    public final static int NONE=0;
    public final static int BAD_REQUEST=1;
    public final static int CONFLICT=2;
    public final static int FEATURE_NOT_IMPLEMENTED=3;
    public final static int FORBIDDEN=4;
    public final static int GONE=5;
    public final static int INTERNAL_SERVER_ERROR=6;
    public final static int ITEM_NOT_FOUND=7;
    public final static int JID_MALFORMED=8;
    public final static int NOT_ACCEPTABLE=9;
    public final static int NOT_ALLOWED=10;
    public final static int NOT_AUTHORIZED=11;
    public final static int PAYMENT_REQUIRED=12;
    public final static int RECIPIENT_UNAVAILEBLE=13;
    public final static int REDIRECT=14;
    public final static int REGISTRATION_REQUIRED=15;
    public final static int REMOTE_SERVER_NOT_FOUND=16;
    public final static int REMOTE_SERVER_TIMEOUT=17;
    public final static int RESOURCE_CONSTRAINT=18;
    public final static int SERVICE_UNAVAILABLE=19;
    public final static int SUBSCRIPTION_REQUIRED=20;
    public final static int UNDEFINED_CONDITION=21;
    public final static int UNEXPECTED_REQUEST=22;
    
    public final static int TYPE_UNDEFINED=0; 
    public final static int TYPE_MODIFY=1; 
    public final static int TYPE_CANCEL=2; 
    public final static int TYPE_AUTH=3; 
    public final static int TYPE_WAIT=4; 
    /** Creates a new instance of XmppError */
    public XmppError(int condition, String text) {
        int errorType=TYPE_UNDEFINED;
        textCondition="undefined-condition";
        switch (condition) {
            case NONE:                      
                errorType=TYPE_UNDEFINED; 
                break;
            case CONFLICT:           
                errorType=TYPE_CANCEL; 
                textCondition="conflict"; 
                break;
            case FEATURE_NOT_IMPLEMENTED:   
                errorType=TYPE_CANCEL; 
                textCondition="feature-not-implemented"; 
                break;
            case FORBIDDEN:  
                errorType=TYPE_AUTH; 
                textCondition="forbidden";
            break;
            case GONE:
                errorType=TYPE_MODIFY; 
                textCondition="gone"; 
            break;
            case INTERNAL_SERVER_ERROR:
                errorType=TYPE_WAIT; 
                textCondition="internal-server-error"; 
                break;
            case ITEM_NOT_FOUND: 
                errorType=TYPE_CANCEL; 
                textCondition="item-not-found";
                break;
            case JID_MALFORMED: 
                errorType=TYPE_MODIFY;
                textCondition="jid-malformed"; 
                break;
            case NOT_ACCEPTABLE:
                errorType=TYPE_MODIFY;
                textCondition="not-acceptable";
                break;
            case NOT_ALLOWED:  
                errorType=TYPE_CANCEL;
                textCondition="not-allowed";
                break;
            case NOT_AUTHORIZED:
                errorType=TYPE_AUTH;
                textCondition="not-authorized";
                break;
            case PAYMENT_REQUIRED:  
                errorType=TYPE_AUTH;
                textCondition="payment-required";
                break;
            case RECIPIENT_UNAVAILEBLE: 
                errorType=TYPE_WAIT;
                textCondition="recipient-unavailable";
                break;
            case REDIRECT:  
                errorType=TYPE_MODIFY; 
                textCondition="redirect";
                break;
            case REGISTRATION_REQUIRED:   
                errorType=TYPE_AUTH;
                textCondition="registration-required";
                break;
            case REMOTE_SERVER_NOT_FOUND:  
                errorType=TYPE_CANCEL;
                textCondition="remote-server-not-found";
                break;
            case REMOTE_SERVER_TIMEOUT:
                errorType=TYPE_WAIT;
                textCondition="remote-server-timeout";
                break;
            case RESOURCE_CONSTRAINT:
                errorType=TYPE_WAIT;
                textCondition="resource-constraint";
                break;
            case SERVICE_UNAVAILABLE:  
                errorType=TYPE_CANCEL;
                textCondition="service-unavailable";
                break;
            case SUBSCRIPTION_REQUIRED:    
                errorType=TYPE_AUTH;
                textCondition="subscription-required";
                break;
            //case UNDEFINED_CONDITION:       errorType=TYPE_UNDEFINED; break;
            //case UNDEFINED_CONDITION:       textCondition="undefined-condition"; break;
            case UNEXPECTED_REQUEST: 
                errorType=TYPE_WAIT;
                textCondition="unexpected-request";
                break;
        }
        errCondition=condition;
        this.text=text;
    }
    
    public JabberDataBlock construct() {
        if (errCondition==NONE) return null;
        JabberDataBlock error=new JabberDataBlock("error", null, null);
        String type=null;
        switch (errorType) {
            case TYPE_MODIFY:    type="modify"; break;
            case TYPE_CANCEL:    type="cancel"; break;
            case TYPE_AUTH:      type="auth"; break;
            case TYPE_WAIT:      type="wait"; break;
            default: /*case TYPE_UNDEFINED:*/ type="cancel"; break;
        }
        error.setAttribute("type", type);

        error.addChildNs(textCondition, "urn:ietf:params:xml:ns:xmpp-stanzas");
        if (text!=null) error.addChildNs("text", "urn:ietf:params:xml:ns:xmpp-stanzas").setText(text);
        
        return error;
    }
    
    public static XmppError findInStanza(JabberDataBlock stanza) {
        return decodeStanzaError(stanza.getChildBlock("error"));
    }
    
    public static XmppError decodeStanzaError(JabberDataBlock error) {
        if (!error.getTagName().equals("error")) throw new IllegalArgumentException();
        return decodeError(error, "urn:ietf:params:xml:ns:xmpp-stanzas");
    }
    
    public static XmppError decodeStreamError(JabberDataBlock error) {
        if (!error.getTagName().equals("stream:error")) throw new IllegalArgumentException();
        return decodeError(error, "urn:ietf:params:xml:ns:xmpp-streams");
    }
        
    private static XmppError decodeError(JabberDataBlock error, String ns) {
        int errCond=NONE;
        String text=null;
        for (Enumeration e=error.getChildBlocks().elements(); e.hasMoreElements();) {
            JabberDataBlock child=(JabberDataBlock) e.nextElement();
            if (!child.isJabberNameSpace(ns)) continue;
            String tag=child.getTagName();
            if (tag.equals("text"))                    text=child.getText();
            if (tag.equals("bad-request"))             errCond=BAD_REQUEST;
            if (tag.equals("conflict"))                errCond=CONFLICT;
            if (tag.equals("feature-not-implemented")) errCond=FEATURE_NOT_IMPLEMENTED;
            if (tag.equals("forbidden"))               errCond=FORBIDDEN;
            if (tag.equals("gone"))                    errCond=GONE;
            if (tag.equals("internal-server-error"))   errCond=INTERNAL_SERVER_ERROR;
            if (tag.equals("item-not-found"))          errCond=ITEM_NOT_FOUND;
            if (tag.equals("jid-malformed"))           errCond=JID_MALFORMED;
            if (tag.equals("not-acceptable"))          errCond=NOT_ACCEPTABLE;
            if (tag.equals("not-allowed"))             errCond=NOT_ALLOWED;
            if (tag.equals("not-authorized"))          errCond=NOT_AUTHORIZED;
            if (tag.equals("payment-required"))        errCond=PAYMENT_REQUIRED;
            if (tag.equals("recipient-unavailable"))   errCond=RECIPIENT_UNAVAILEBLE;
            if (tag.equals("redirect"))                errCond=REDIRECT;
            if (tag.equals("registration-required"))   errCond=REGISTRATION_REQUIRED;
            if (tag.equals("remote-server-not-found")) errCond=REMOTE_SERVER_NOT_FOUND;
            if (tag.equals("remote-server-timeout"))   errCond=REMOTE_SERVER_TIMEOUT;
            if (tag.equals("resource-constraint"))     errCond=RESOURCE_CONSTRAINT;
            if (tag.equals("service-unavailable"))     errCond=SERVICE_UNAVAILABLE;
            if (tag.equals("subscription-required"))   errCond=SUBSCRIPTION_REQUIRED;
            if (tag.equals("undefined-condition"))     errCond=UNDEFINED_CONDITION;
            if (tag.equals("unexpected-request"))      errCond=UNEXPECTED_REQUEST;
        }
        
        if (errCond==NONE) {
            try {
                int code=Integer.parseInt(error.getAttribute("code"));
                switch (code) {
                    case 302: errCond=REDIRECT; break;
                    case 400: errCond=BAD_REQUEST; break;
                    case 401: errCond=NOT_AUTHORIZED; break;
                    case 402: errCond=PAYMENT_REQUIRED; break;
                    case 403: errCond=FORBIDDEN; break;
                    case 404: errCond=ITEM_NOT_FOUND; break;
                    case 405: errCond=NOT_ALLOWED; break;
                    case 406: errCond=NOT_ACCEPTABLE; break;
                    case 407: errCond=REGISTRATION_REQUIRED; break;
                    case 408: errCond=REMOTE_SERVER_TIMEOUT; break;
                    case 409: errCond=CONFLICT; break;
                    case 500: errCond=INTERNAL_SERVER_ERROR; break;
                    case 501: errCond=FEATURE_NOT_IMPLEMENTED; break;
                    case 502: 
                    case 503:
                    case 510: errCond=SERVICE_UNAVAILABLE; break;
                    case 504: errCond=REMOTE_SERVER_TIMEOUT; break;
                    default: errCond=UNDEFINED_CONDITION;
                }
            } catch (Exception e) { errCond=UNDEFINED_CONDITION; };
        }
        
        XmppError xe=new XmppError(errCond, text);
        
        String type=error.getTypeAttribute();
        if (type!=null) {
            if (type.equals("auth")) xe.errorType=TYPE_AUTH;
            if (type.equals("cancel")) xe.errorType=TYPE_CANCEL;
            if (type.equals("modify")) xe.errorType=TYPE_MODIFY;
            if (type.equals("wait")) xe.errorType=TYPE_WAIT;
        }
        
        return xe;
    }
    
    
    public String getText() { return text; };
    public String getName() { return textCondition; /*TODO: remove stub*/}
    
    public String toString() {
        if (text==null) return getName();
        return getName()+": "+getText();
    }

    public int getCondition() { return errCondition; }
    public int getActionType() { return errorType; }
    
    private int errCondition;

    private String textCondition;
    private int errorType;
    private String text;
}
