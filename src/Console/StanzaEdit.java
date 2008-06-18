/*
 * StanzaEdit.java
 *
 * Created on 7 ������ 2008 �., 16:05
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
import ui.controls.ExTextBox;

/**
 *
 * @author ad
 */
public class StanzaEdit 
        extends ExTextBox
        implements CommandListener, Runnable {

    private Display display;
    private Displayable parentView;

    private String body;

    private Command cmdCancel=new Command(SR.MS_CANCEL, Command.BACK,99);
    private Command cmdSend=new Command(SR.MS_SEND, Command.OK,1);

    private Command cmdPasteIQDisco=new Command("disco#info", Command.SCREEN,11);
    private Command cmdPasteIQVersion=new Command("jabber:iq:version", Command.SCREEN,12);
    private Command cmdPastePresence=new Command("presence", Command.SCREEN,13);
    private Command cmdPasteMessage=new Command("message", Command.SCREEN,14);

    private Config cf;
    
    private static final String TEMPLATE_IQ_DISCO="<iq to='???' type='get'>\n<query xmlns='http://jabber.org/protocol/disco#info'/>\n</iq>";
    private static final String TEMPLATE_IQ_VERSION="<iq to='???' type='get'>\n<query xmlns='jabber:iq:version'/>\n</iq>";
    private static final String TEMPLATE_PRESENCE="<presence to='???'>\n<show>???</show>\n<status>???</status>\n</presence>";
    private static final String TEMPLATE_MESSAGE="<message to='???' type='???'>\n<body>???</body>\n</message>";

    public StanzaEdit(Display display, String body) {
        super(display, body, SR.MS_XML_CONSOLE, TextField.ANY);
        
        this.display=display;
        parentView=display.getCurrent();

        cf=Config.getInstance();
        
        addCommand(cmdSend);

        addCommand(cmdPasteIQDisco);
        addCommand(cmdPasteIQVersion);
        addCommand(cmdPastePresence);
        addCommand(cmdPasteMessage);
        
        addCommand(cmdCancel);
        setCommandListener(this);

        new Thread(this).start() ; // composing
        
        display.setCurrent(this);
    }
    
    public void setParentView(Displayable parentView){
        this.parentView=parentView;
    }
    
    public void commandAction(Command c, Displayable d){
        if (executeCommand(c, d)) return;
        
        body=getString();
        if (body.length()==0) body=null;

        int caretPos=getCaretPos();

        if (c==cmdPasteIQDisco) { insert(TEMPLATE_IQ_DISCO, caretPos); return; }
        if (c==cmdPasteIQVersion) { insert(TEMPLATE_IQ_VERSION, caretPos); return; }
        if (c==cmdPastePresence) { insert(TEMPLATE_PRESENCE, caretPos); return; }
        if (c==cmdPasteMessage) { insert(TEMPLATE_MESSAGE, caretPos); return; }

        if (c==cmdCancel) { 
            body=null;
        }

        if (c==cmdSend && body==null) return;

        // message/composing sending
        destroyView();
        new Thread(this).start();
    }
    
    public void run(){
        Roster r=StaticData.getInstance().roster;
        try {
            if (body!=null) {
                body=body.trim();
                r.theStream.send(body);
            }
        } catch (Exception e) { }

        ((VirtualList)parentView).redraw();
    }
}