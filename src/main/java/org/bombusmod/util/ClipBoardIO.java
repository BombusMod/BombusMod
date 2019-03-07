/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bombusmod.util;
import Client.Msg;
import util.ClipBoardMIDP;

/**
 *
 * @author Vitaly
 */
public abstract class ClipBoardIO {
    public static ClipBoardIO getInstance() {
        try {
            return (ClipBoardIO) Class.forName("org.bombusmod.android.util.AndroidClipboard").newInstance();
        } catch (Exception e) {
            return new ClipBoardMIDP();
        }
    }
    
    public void set(Msg msg) {
        try {
            StringBuffer clipstr=new StringBuffer(msg.quoteString(true));
            setClipBoard(clipstr.toString());
        } catch (Exception e) {/*no messages*/}
    }
    
    public void append(String str) {
        try {
            StringBuffer clipstr = new StringBuffer(getClipBoard())
            .append("\n\n")
            .append(str);
            setClipBoard(clipstr.toString());
        } catch (Exception e) {/*no messages*/}
    }
    
    public void append(Msg msg) {
        append(msg.quoteString(true));        
    }
    
    public abstract String getClipBoard();
    
    public abstract void setClipBoard(CharSequence str);
    
    public abstract boolean isEmpty();
}
