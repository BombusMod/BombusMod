/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;
import Client.Msg;

/**
 *
 * @author Vitaly
 */
public abstract class ClipBoardIO {
    public static ClipBoardIO getInstance() {
//#if android        
//#         return new AndroidClipBoard();
//#else
        return new ClipBoard();
//#endif        
    }
    
    public void set(Msg msg) {
        try {
            StringBuffer clipstr=new StringBuffer(msg.quoteString(true));
            setClipBoard(clipstr.toString());
            clipstr=null;
        } catch (Exception e) {/*no messages*/}
    }
    
    public void append(String str) {
        try {
            StringBuffer clipstr = new StringBuffer(getClipBoard())
            .append("\n\n")
            .append(str);
            setClipBoard(clipstr.toString());
            clipstr = null;
        } catch (Exception e) {/*no messages*/}
    }
    
    public void append(Msg msg) {
        append(msg.quoteString(true));        
    }
    
    public abstract String getClipBoard();
    
    public abstract void setClipBoard(String str);
    
    public abstract boolean isEmpty();
}
