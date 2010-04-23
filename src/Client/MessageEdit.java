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
//#ifdef CLIPBOARD
//# import util.ClipBoard;
//#endif
//#ifdef ARCHIVE
import Archive.ArchiveList;
//#endif
//#ifdef RUNNING_MESSAGE
//# import ui.VirtualList;
//#endif
/**
 *
 * @author Eugene Stahov
 */
public final class MessageEdit
        implements CommandListener
//#ifdef RUNNING_MESSAGE
//#         , Runnable {
//#     Thread thread;
//#else
    {
//#endif
    private Display display;
    private Displayable parentView;
    
    private String subj;
    
    public Contact to;
    
    private boolean composing=true;
    
    StaticData sd = StaticData.getInstance();
    
    private Config cf;
    public String body;

//#ifdef DETRANSLIT
//#     private boolean sendInTranslit=false;
//#     private boolean sendInDeTranslit=false;
//#endif
//#ifdef CLIPBOARD
//#     private ClipBoard clipboard;
//#endif

//#ifdef ARCHIVE
    private Command cmdPaste=new Command(SR.MS_ARCHIVE, Command.ITEM, 6);
//#endif
//#if TEMPLATES
//#     private Command cmdTemplate=new Command(SR.MS_TEMPLATE, Command.ITEM, 7);
//#endif
//#ifdef CLIPBOARD
//#     private Command cmdPasteText=new Command(SR.MS_PASTE, Command.ITEM, 8);
//#endif
    
    private Command cmdSend;//=new Command(SR.MS_SEND, Command.OK, 1);
    private Command cmdSendAndStepBack = new Command(SR.MS_SEND+" & "+SR.MS_STEP_BACK, Command.ITEM, 80);

//#ifdef SMILES
    private Command cmdSmile=new Command(SR.MS_ADD_SMILE, Command.ITEM,2);
//#endif
    private Command cmdInsNick=new Command(SR.MS_NICKNAMES,Command.ITEM,3);
    private Command cmdInsMe=new Command(SR.MS_SLASHME, Command.ITEM, 4); ; // /me
//#ifdef DETRANSLIT
//#     private Command cmdSendInTranslit=new Command(SR.MS_TRANSLIT, Command.ITEM, 5);
//#     private Command cmdSendInDeTranslit=new Command(SR.MS_DETRANSLIT, Command.ITEM, 5);
//#endif
    private Command cmdLastMessage=new Command(SR.MS_PREVIOUS, Command.ITEM, 9);
    private Command cmdSubj=new Command(SR.MS_SET_SUBJECT, Command.ITEM, 10);
    private Command cmdSuspend;//=new Command(SR.MS_SUSPEND, Command.BACK,90);
    private Command cmdCancel=new Command(SR.MS_CANCEL, Command.ITEM,99);
    private final TextBox t;
    int maxSize = 500;
//#ifdef RUNNING_MESSAGE
//#     Ticker ticker = new Ticker("");
//#endif
    /** Creates a new instance of MessageEdit */
    public MessageEdit(Display display, Displayable pView, Contact to, String body) {
        t = new TextBox(to.toString(), "", 500, TextField.ANY);
        try {
            //expanding buffer as much as possible
            maxSize = t.setMaxSize(4096); //must not trow

         } catch (Exception e) {}

        insert(body, 0, false); // workaround for Nokia S40
        this.to=to;
        this.display=display;

        cf=Config.getInstance();
//#ifdef DETRANSLIT
//#         DeTranslit.getInstance();
//#endif

        if (!cf.swapSendAndSuspend) {
            cmdSuspend=new Command(SR.MS_SUSPEND, Command.BACK, 90);
            cmdSend=new Command(SR.MS_SEND, Command.OK, 1);
        } else {
            cmdSuspend=new Command(SR.MS_SUSPEND, Command.OK, 1);
            cmdSend=new Command(SR.MS_SEND, Command.BACK, 90);
        }

//#ifdef ARCHIVE
//#ifdef PLUGINS
//#         if (StaticData.getInstance().Archive)
//#endif
            t.addCommand(cmdPaste);
//#endif
//#ifdef CLIPBOARD
//#         if (cf.useClipBoard) {
//#             clipboard=ClipBoard.getInstance();
//#             if (!clipboard.isEmpty()) {
//#                 t.addCommand(cmdPasteText);
//#             }
//#         }
//#endif
//#if TEMPLATES
//#ifdef PLUGINS
//#         if (StaticData.getInstance().Archive)
//#endif
//#             t.addCommand(cmdTemplate);
//#endif

        t.addCommand(cmdSend);

        boolean viewSendAndBackCmd = true;
        if (pView instanceof ContactMessageList)
            viewSendAndBackCmd = !((ContactMessageList) pView).contact.equals(to);
        if (viewSendAndBackCmd) {
            t.addCommand(cmdSendAndStepBack);
        }

        t.addCommand(cmdInsMe);
//#ifdef SMILES
        t.addCommand(cmdSmile);
//#endif
        if (to.origin>=Contact.ORIGIN_GROUPCHAT)
            t.addCommand(cmdInsNick);
//#ifdef DETRANSLIT
//#         t.addCommand(cmdSendInTranslit);
//#         t.addCommand(cmdSendInDeTranslit);
//#endif
        t.addCommand(cmdSuspend);
        t.addCommand(cmdCancel);
        
        if (to.origin==Contact.ORIGIN_GROUPCHAT)
            t.addCommand(cmdSubj);
        
        if (to.lastSendedMessage!=null)
            t.addCommand(cmdLastMessage);        
//#ifdef RUNNING_MESSAGE
//#ifdef MIDP_TICKER
//#         if (cf.notifyWhenMessageType) {
//#             t.setTicker(ticker);
//#         } else {
//#             t.setTicker(null);
//#         }
//#endif
//#         if (thread==null) (thread=new Thread(this)).start() ; // composing
//#else
        send() ; // composing
//#endif
        setInitialCaps(cf.capsState);
        if (Config.getInstance().phoneManufacturer == Config.SONYE) System.gc(); // prevent flickering on Sony Ericcsson C510
        t.setCommandListener(this);
        display.setCurrent(t);
        this.parentView = pView;
    }

    public void commandAction(Command c, Displayable d){
        
        body = t.getString();

        int caretPos=getCaretPos();

        if (body.length()==0) body=null;

//#ifdef ARCHIVE
	if (c==cmdPaste) { new ArchiveList(display, t, caretPos, 1, t); return; }
//#endif
//#ifdef CLIPBOARD
//#         if (c==cmdPasteText) { insert(clipboard.getClipBoard(), getCaretPos()); return; }
//#endif
//#if TEMPLATES
//#         if (c==cmdTemplate) { new ArchiveList(display, t, caretPos, 2, t); return; }
//#endif
        

        if (c==cmdInsMe) { insert("/me", 0); return; }
        if (c==cmdLastMessage) { setText(to.lastSendedMessage); return; }
//#ifdef SMILES
        if (c==cmdSmile) { new SmilePicker(display, t, caretPos, this); return; }
//#endif
//#ifndef WMUC
        if (c==cmdInsNick) { new AppendNick(display, t, to, caretPos, this); return; }
//#endif
        if (c==cmdCancel) {
            composing=false;
            body = null;
        }
        if (c==cmdSuspend) {
                composing = false;
                if (body != null) {
                    to.msgSuspended = body.trim();
                    body = null;
                }
        }
        
        if (c==cmdSend && body==null) {
            composing = false;
        }
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
        if (c == cmdSend && !((parentView instanceof ContactMessageList) && ((ContactMessageList) parentView).equals(to)))
            parentView = new ContactMessageList(to, display);
        display.setCurrent(parentView);

//#ifdef RUNNING_MESSAGE
//#         runState=3;
//#else
        send();
//#endif
    }
//#ifdef RUNNING_MESSAGE
//#     /*
//#      0 - do nothing
//#      1 - scroll
//#      2 - send
//#      3 - send and close
//#      4 - end cycle
//#      *
//#      */
//#     int runState=2;
//#
//#     int strPos=0;
//#     public void run(){
//#         while (runState<4) {
//#             //System.out.println(runState+" "+notifyMessage);
//#             if (runState==2) { runState=0; send(); }
//#             if (runState==3) {
//#                 runState=4;
//#                 send();
//#                 thread=null;
//#                 ((VirtualList) parentView).redraw();
//#                 break;
//#             }
//#             if (runState==1) {
//#ifdef MIDP_TICKER
//#                 if (cf.notifyWhenMessageType) {
//#                 if (notifyMessage != null)
//#                     ticker.setString(notifyMessage);
//#                     runState = 4;
//#                 }
//#else
//#                 if (cf.notifyWhenMessageType) {
//#                  t.setTitle(notifyMessage.substring(strPos++));
//#                 if ((notifyMessage.length()-strPos)<0) strPos=0;
//#             }
//#endif
//#             }
//#             try { Thread.sleep(250); } catch (Exception e) { break; }
//#         }
//#     }
//#
//#     private String notifyMessage;
//#     public void setMyTicker(String msg) {
//#         if (msg!=null && !msg.equals("")) {
//#             StringBuffer out=new StringBuffer(msg);
//#             int i=0;
//#             while (i<out.length()) {
//#                 if (out.charAt(i)<0x03) out.deleteCharAt(i);
//#                 else i++;
//#             }
//#             msg=out.toString();
//#             runState=1;
//#         } else {
//#ifdef MIDP_TICKER
//#             if (cf.notifyWhenMessageType) {
//#                 ticker.setString("");
//#             }
//#else
//#             if (cf.notifyWhenMessageType) {
//#                 t.setTitle(to.toString());
//#             }
//#endif
//#             runState=0;
//#         }
//#         notifyMessage=msg;
//#         strPos=0;
//#     }
//#endif

    private void send() {
        String comp = null; // composing event off

        String id = String.valueOf((int) System.currentTimeMillis());

        if (body != null)
            body = body.trim();

         if (body!=null || subj!=null ) {
//#ifdef DETRANSLIT
//#              if (sendInTranslit == true) {
//#                  if (body != null)
//#                      body = DeTranslit.translit(body);
//#                  if (subj != null)
//#                      subj = DeTranslit.translit(subj);
//#              }
//#              if (sendInDeTranslit == true || cf.autoDeTranslit) {
//#                  if (body != null)
//#                      body = DeTranslit.deTranslit(body);
//#                  if (subj != null)
//#                      subj = DeTranslit.deTranslit(subj);
//#              }
//#endif
             String from = sd.account.toString();
             Msg msg = new Msg(Msg.MESSAGE_TYPE_OUT, from, subj, body);
             msg.id = id;

             if (to.origin != Contact.ORIGIN_GROUPCHAT) {
                 to.addMessage(msg);
                 comp = "active"; // composing event in message
             }
         } else if (to.acceptComposing)
             comp = (composing) ? "composing" : "paused";

        if (!cf.eventComposing)
            comp = null;

         try {
             if (body!=null || subj!=null || comp!=null) {
                 to.lastSendedMessage=body;
                 sd.roster.sendMessage(to, id, body, subj, comp);
             }
         } catch (Exception e) { }
     }

/* Пролистывание команд по страницам, для SE C510
    private void addCommand(Command cmd) {
       Commands.addElement(cmd);
       // А вообще здесь insert надо, в соответствие с приоритетом.
    }

    private int getCommandPages() {
        int count_pages = ((int) (Commands.size()/8))+1;
        if ((Commands.size() % 8) < 3)
            count_pages--;
        return count_pages;
    }

    private void nextPage() {
    }
*/
    public void insert(String s, int caretPos) {
        insert(s, caretPos, true);
    }

    public void insert(String s, int caretPos, boolean writespaces) {
        if (s == null) return;

        String src = t.getString();

        StringBuffer sb = new StringBuffer(s);

        if (writespaces) {
            if (caretPos > 0) {
                if (src.charAt(caretPos - 1) != ' ') {
                    sb.insert(0, ' ');
                }
            }

            if (caretPos < src.length()) {
                if (src.charAt(caretPos) != ' ') {
                    sb.append(' ');
                }
            }

            if (caretPos == src.length()) {
                sb.append(' ');
            }
        }

        try {
            int freeSz = t.getMaxSize() - t.size();
            if (freeSz < sb.length()) {
                sb.delete(freeSz, sb.length());
            }
        } catch (Exception e) {
        }

        t.insert(sb.toString(), caretPos);
        sb = null;        
    }
    public int getCaretPos() {
        int caretPos = t.getCaretPosition();
        // +MOTOROLA STUB
        if (cf.phoneManufacturer==Config.MOTO)
            caretPos=-1;
        if (caretPos<0)
            caretPos = t.getString().length();
        return caretPos;
    }
    public void setText(String body) {
        if (body!=null) {
            if (body.length()>maxSize)
                body=body.substring(0, maxSize-1);
            t.setString(body);
        }
    }
    private void setInitialCaps(boolean state) {
        t.setConstraints(state?TextField.INITIAL_CAPS_SENTENCE:TextField.ANY);
    }
}

