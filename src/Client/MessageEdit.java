/*
 * MessageEdit.java
 *
 * Created on 20.02.2005, 21:20
 * Copyright (c) 2005-2008, Eugene Stahov (evgs), http://bombus-im.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * You can also redistribute and/or modify this program under the
 * terms of the Psi License, specified in the accompanied COPYING
 * file, as published by the Psi Project; either dated January 1st,
 * 2005, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package Client;
//#ifndef WMUC
import Conference.AppendNick;
//#endif
//#ifdef DETRANSLIT
//# import util.DeTranslit;
//#endif
import javax.microedition.lcdui.*;
import locale.SR;
import ui.VirtualList;
import ui.controls.ExTextBox;

/**
 *
 * @author Eugene Stahov
 */
public class MessageEdit 
        extends ExTextBox
        implements CommandListener, Runnable {
    
    Thread thread;
    
    private Display display;
    private Displayable parentView;

    private String body;
    private String subj;
    
    public Contact to;
    
    private boolean composing=true;
    
    StaticData sd = StaticData.getInstance();
    
    private Config cf;
    
//#ifdef DETRANSLIT
//#     private boolean sendInTranslit=false;
//#     private boolean sendInDeTranslit=false;
//#     DeTranslit dt;
//#endif
    
    private Command cmdSend=new Command(SR.MS_SEND, Command.OK, 1);
//#ifdef SMILES
    private Command cmdSmile=new Command(SR.MS_ADD_SMILE, Command.SCREEN,2);
//#endif
    private Command cmdInsNick=new Command(SR.MS_NICKNAMES,Command.SCREEN,3);
    private Command cmdInsMe=new Command(SR.MS_SLASHME, Command.SCREEN, 4); ; // /me
//#ifdef DETRANSLIT
//#     private Command cmdSendInTranslit=new Command(SR.MS_TRANSLIT, Command.SCREEN, 5);
//#     private Command cmdSendInDeTranslit=new Command(SR.MS_DETRANSLIT, Command.SCREEN, 5);
//#endif
    private Command cmdLastMessage=new Command(SR.MS_PREVIOUS, Command.SCREEN, 9);
    private Command cmdSubj=new Command(SR.MS_SET_SUBJECT, Command.SCREEN, 10);
    private Command cmdSuspend=new Command(SR.MS_SUSPEND, Command.BACK,90);
    private Command cmdCancel=new Command(SR.MS_CANCEL, Command.SCREEN,99);

    /** Creates a new instance of MessageEdit */
    public MessageEdit(Display display, Displayable pView, Contact to, String body) {
        super(display, pView, body, to.toString(), TextField.ANY);
        this.to=to;
        this.display=display;

        cf=Config.getInstance();
//#ifdef DETRANSLIT
//#         dt=DeTranslit.getInstance();
//#endif
        
        addCommand(cmdSend);
        addCommand(cmdInsMe);
//#ifdef SMILES
        addCommand(cmdSmile);
//#endif
        if (to.origin>=Contact.ORIGIN_GROUPCHAT)
            addCommand(cmdInsNick);
//#ifdef DETRANSLIT
//#         addCommand(cmdSendInTranslit);
//#         addCommand(cmdSendInDeTranslit);
//#endif
        addCommand(cmdSuspend);
        addCommand(cmdCancel);
        
        if (to.origin==Contact.ORIGIN_GROUPCHAT)
            addCommand(cmdSubj);
        
        if (to.lastSendedMessage!=null)
            addCommand(cmdLastMessage);
                
        setCommandListener(this);

        if (thread==null) (thread=new Thread(this)).start() ; // composing
        
        display.setCurrent(this);
        this.parentView=pView;
    }
    
    public void commandAction(Command c, Displayable d){
        if (executeCommand(c, d)) return;
        
        body=getString();
        if (body.length()==0) body=null;
        
        int caretPos=getCaretPos();

        if (c==cmdInsMe) { insert("/me", 0); return; }
        if (c==cmdLastMessage) { super.setText(to.lastSendedMessage); return; }
//#ifdef SMILES
        if (c==cmdSmile) { new SmilePicker(display, this, caretPos, this); return; }
//#endif
//#ifndef WMUC
        if (c==cmdInsNick) { new AppendNick(display, to, caretPos, this); return; }
//#endif
        if (c==cmdCancel) {
            //runState=4;
            composing=false;
            body=null;
        }
        if (c==cmdSuspend) {
                //runState=4;
                composing=false; 
                to.msgSuspended=body; 
                body=null;
        }
        
        if (c==cmdSend && body==null) return;
//#ifdef DETRANSLIT
//#         if (c==cmdSendInTranslit) {
//#             sendInTranslit=true;
//#         }
//#  
//#         if (c==cmdSendInDeTranslit) {
//#             sendInDeTranslit=true;
//#         }
//#endif
        if (c==cmdSubj) {
            if (body==null) return;
            subj=body;
            body=null; //"/me "+SR.MS_HAS_SET_TOPIC_TO+": "+subj;
        }
        // message/composing sending
        destroyView();
        runState=3;  
    }
    
    /*
     0 - do nothing
     1 - scroll
     2 - send
     3 - send and close
     4 - end cycle
     * 
     */
    int runState=2;

    int strPos=0;
    public void run(){
        while (runState<4) {
            //System.out.println(runState+" "+notifyMessage);
            if (runState==2) { runState=0; send(); }
            if (runState==3) { runState=4; send(); thread=null; ((VirtualList) parentView).redraw(); break; }
            
            if (runState==1) {
                setTitle(notifyMessage.substring(strPos++));
                if ((notifyMessage.length()-strPos)<0) strPos=0;
            }
            try { Thread.sleep(250); } catch (Exception e) { break; }
        }
    }
    
    private void send() {
        String comp=null; // composing event off

        String id=String.valueOf((int) System.currentTimeMillis());
        if (body!=null)
            body=body.trim();
//#ifdef DETRANSLIT
//#         if (sendInTranslit==true) {
//#             if (body!=null)
//#                body=dt.translit(body);
//#             if (subj!=null )
//#                subj=dt.translit(subj);
//#         }
//#         if (sendInDeTranslit==true || cf.autoDeTranslit) {
//#             if (body!=null)
//#                body=dt.deTranslit(body);
//#             if (subj!=null )
//#                subj=dt.deTranslit(subj);
//#         }
//#endif
        if (body!=null || subj!=null ) {
            String from=sd.account.toString();
            Msg msg=new Msg(Msg.MESSAGE_TYPE_OUT,from,subj,body);
            msg.id=id;

            if (to.origin!=Contact.ORIGIN_GROUPCHAT) {
                to.addMessage(msg);
                comp="active"; // composing event in message
            }
        } else if (to.acceptComposing) comp=(composing)? "composing":"paused";
        
        if (!cf.eventComposing) comp=null;
        
        try {
            if (body!=null || subj!=null || comp!=null) {
                to.lastSendedMessage=body;
                sd.roster.sendMessage(to, id, body, subj, comp);
            }
        } catch (Exception e) { }
    }
    
    private String notifyMessage;
    public void setMyTicker(String msg) {
        if (msg!=null && !msg.equals("")) {
            StringBuffer out=new StringBuffer(msg);
            int i=0;
            while (i<out.length()) {
                if (out.charAt(i)<0x03) out.deleteCharAt(i);
                else i++;
            }
            msg=out.toString();
            runState=1;
        } else {
            setTitle(to.toString());
            runState=0;
        }
        notifyMessage=msg;
        strPos=0;
    }
}

