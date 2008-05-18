/*
 * StanzaEdit.java
 *
 * Created on 7 јпрель 2008 г., 16:05
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package Console;

import Client.Config;
import Client.Roster;
import Client.StaticData;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;
import locale.SR;
import ui.VirtualList;
//#ifdef CLIPBOARD
//# import util.ClipBoard;
//#endif

/**
 *
 * @author ad
 */
public class StanzaEdit implements CommandListener, Runnable {

    private Display display;
    private Displayable parentView;

    private String stanza;

    private Command cmdCancel=new Command(SR.MS_CANCEL, Command.BACK,99);
    private Command cmdSend=new Command(SR.MS_SEND, Command.OK,1);
//#ifdef CLIPBOARD
//#     private Command cmdPasteText=new Command(SR.MS_PASTE, Command.SCREEN,10);
//#endif
    private Command cmdPasteIQDisco=new Command("disco#info", Command.SCREEN,11);
    private Command cmdPasteIQVersion=new Command("jabber:iq:version", Command.SCREEN,12);
    private Command cmdPastePresence=new Command("presence", Command.SCREEN,13);
    private Command cmdPasteMessage=new Command("message", Command.SCREEN,14);
//#ifdef CLIPBOARD
//#     private ClipBoard clipboard;
//#endif
    private Config cf;
    
    private TextBox t;
    
    private static final String TEMPLATE_IQ_DISCO="<iq to='???' type='get'>\n<query xmlns='http://jabber.org/protocol/disco#info'/>\n</iq>";
    private static final String TEMPLATE_IQ_VERSION="<iq to='???' type='get'>\n<query xmlns='jabber:iq:version'/>\n</iq>";
    private static final String TEMPLATE_PRESENCE="<presence to='???'>\n<show>???</show>\n<status>???</status>\n</presence>";
    private static final String TEMPLATE_MESSAGE="<message to='???' type='???'>\n<body>???</body>\n</message>";
    
    /** Creates a new instance of MessageEdit */
    public StanzaEdit(Display display, String stanza) {
        this.display=display;
        parentView=display.getCurrent();

        t=new TextBox("", "", 500, TextField.ANY);

        cf=Config.getInstance();
        
        try {
            //expanding buffer as much as possible
            int maxSize=t.setMaxSize(4096); //must not trow

            if (stanza!=null) {
                if (stanza.length()>maxSize)
                    stanza=stanza.substring(0, maxSize-1);
                t.setString(stanza);
            }
         } catch (Exception e) {}

        
        t.addCommand(cmdSend);
        
//#ifdef CLIPBOARD
//#         if (cf.useClipBoard) {
//#             clipboard=ClipBoard.getInstance();
//#             if (!clipboard.isEmpty())
//#                 t.addCommand(cmdPasteText);
//#         }
//#endif

        t.addCommand(cmdPasteIQDisco);
        t.addCommand(cmdPasteIQVersion);
        t.addCommand(cmdPastePresence);
        t.addCommand(cmdPasteMessage);
        
        t.addCommand(cmdCancel);
        t.setCommandListener(this);

        new Thread(this).start() ; // composing
        
        display.setCurrent(t);
    }
    
    public void setParentView(Displayable parentView){
        this.parentView=parentView;
    }
    
    public void commandAction(Command c, Displayable d){
        stanza=t.getString();
        
        if (stanza.length()==0) stanza=null;
        int caretPos=getCaretPos();
//#ifdef CLIPBOARD
//#         if (c==cmdPasteText) { insertText(clipboard.getClipBoard(), getCaretPos()); return; }
//#endif
        if (c==cmdPasteIQDisco) { insertText(TEMPLATE_IQ_DISCO, getCaretPos()); return; }
        if (c==cmdPasteIQVersion) { insertText(TEMPLATE_IQ_VERSION, getCaretPos()); return; }
        if (c==cmdPastePresence) { insertText(TEMPLATE_PRESENCE, getCaretPos()); return; }
        if (c==cmdPasteMessage) { insertText(TEMPLATE_MESSAGE, getCaretPos()); return; }

        if (c==cmdCancel) { 
            stanza=null;
        }

        if (c==cmdSend && stanza==null) return;

        // message/composing sending
        destroyView();
        new Thread(this).start();
        return; 
    }
    
    public void run(){
        Roster r=StaticData.getInstance().roster;
        try {
            if (stanza!=null) {
                stanza=stanza.trim();
                r.theStream.send(stanza);
            }
        } catch (Exception e) { }

        ((VirtualList)parentView).redraw();
        ((VirtualList)parentView).repaint();
    }
    
    public void destroyView(){
        if (display!=null)   display.setCurrent(parentView);
    }

    public int getCaretPos() {     
        int caretPos=t.getCaretPosition();
        // +MOTOROLA STUB
        if (cf.phoneManufacturer==Config.MOTO)
            caretPos=-1;
        
        if (caretPos<0) caretPos=t.getString().length();
        
        return caretPos;
    }

    public void insertText(String s, int caretPos) {
        String src=t.getString();

        StringBuffer sb=new StringBuffer(s);
        
        if (caretPos>0) 
            if (src.charAt(caretPos-1)!=' ')   
                sb.insert(0, ' ');
        
        if (caretPos<src.length())
            if (src.charAt(caretPos)!=' ')
                sb.append(' ');
        
        if (caretPos==src.length()) sb.append(' ');
        
        try {
            int freeSz=t.getMaxSize()-t.size();
            if (freeSz<sb.length()) sb.delete(freeSz, sb.length());
        } catch (Exception e) {}
       
        t.insert(sb.toString(), caretPos);
        sb=null;
    }
}