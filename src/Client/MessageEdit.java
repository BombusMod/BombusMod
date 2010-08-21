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
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextField;
import javax.microedition.lcdui.Ticker;
import ui.controls.ExTextBox;

//#ifdef DETRANSLIT
//# import util.DeTranslit;
//#endif
import locale.SR;
import ui.VirtualCanvas;
/**
 *
 * @author Eugene Stahov
 */
public final class MessageEdit
        extends ExTextBox implements CommandListener 
//#ifdef RUNNING_MESSAGE
//#         , Runnable {
//#     Thread thread;
//#else
    {
//#endif    
    private String subj;
    public Contact to;
    private boolean composing = true;
    StaticData sd = StaticData.getInstance();
//#ifdef DETRANSLIT
//#     private boolean sendInTranslit = false;
//#     private boolean sendInDeTranslit = false;
//#endif
    private Command cmdSend;//=new Command(SR.MS_SEND, Command.OK, 1);
    // private Command cmdSendAndStepBack = new Command(SR.MS_SEND+" & "+SR.MS_STEP_BACK, Command.SCREEN, 80);

//#ifdef SMILES
    private Command cmdSmile;
//#endif
//#ifndef WMUC    
    private Command cmdInsNick;
//#endif    
    private Command cmdInsMe; // /me
//#ifdef DETRANSLIT
//#     private Command cmdSendInTranslit;
//#     private Command cmdSendInDeTranslit;
//#endif
    private Command cmdLastMessage;
    private Command cmdSubj;
    private Command cmdSuspend;//=new Command(SR.MS_SUSPEND, Command.BACK,90);
    private Command cmdCancel;
//#ifdef MIDP_TICKER
//#     Ticker ticker = new Ticker("");
//#endif
    /** Creates a new instance of MessageEdit */
    public MessageEdit(Contact to, String body) {
        super(body, to.toString());

        this.to = to;
        
        if (to != null) {
//#ifndef WMUC            
            if (to.origin >= Contact.ORIGIN_GROUPCHAT) 
                textbox.addCommand(cmdInsNick);
            
            if (to.origin == Contact.ORIGIN_GROUPCHAT) 
                textbox.addCommand(cmdSubj);
//#endif        
            
            if (to.lastSendedMessage != null) {
                textbox.addCommand(cmdLastMessage);
            }
        }

//#ifdef DETRANSLIT
//#ifdef PLUGINS
//#        if (sd.DeTranslit)
//#endif
//#             DeTranslit.getInstance();
//#endif

    }

    public void show(Displayable pView) {
//#ifdef RUNNING_MESSAGE
//#ifdef MIDP_TICKER
//#         if (cf.notifyWhenMessageType) {
//#             textbox.setTicker(ticker);
//#         } else {
//#             textbox.setTicker(null);
//#         }
//#endif
//#         if (thread == null) 
//#             (thread = new Thread(this)).start(); // composing
//#else
        send() ; // composing
//#endif
        super.show(pView, this);
    }

    public void commandState() {

        super.commandState();

//#ifdef SMILES
        cmdSmile = new Command(SR.MS_ADD_SMILE, Command.SCREEN, 2);
//#endif
//#ifndef WMUC        
        cmdInsNick = new Command(SR.MS_NICKNAMES, Command.SCREEN, 3);
//#endif        
        cmdInsMe = new Command(SR.MS_SLASHME, Command.SCREEN, 4);  // /me
//#ifdef DETRANSLIT
//#         cmdSendInTranslit = new Command(SR.MS_TRANSLIT, Command.SCREEN, 5);
//#         cmdSendInDeTranslit = new Command(SR.MS_DETRANSLIT, Command.SCREEN, 5);
//#endif
        cmdLastMessage = new Command(SR.MS_PREVIOUS, Command.SCREEN, 9);
        cmdSubj = new Command(SR.MS_SET_SUBJECT, Command.SCREEN, 10);
        cmdCancel = new Command(SR.MS_CANCEL, Command.SCREEN, 99);


        if (!Config.getInstance().swapSendAndSuspend) {
            cmdSuspend = new Command(SR.MS_SUSPEND, Command.BACK, 90);
            cmdSend = new Command(SR.MS_SEND, Command.OK, 1);
        } else {
            cmdSuspend = new Command(SR.MS_SUSPEND, Command.OK, 1);
            cmdSend = new Command(SR.MS_SEND, Command.BACK, 90);
        }


        /*   boolean viewSendAndBackCmd = true;
        if (parentView instanceof ContactMessageList)
        viewSendAndBackCmd = !((ContactMessageList) parentView).contact.equals(to);
        if (viewSendAndBackCmd) {
        textbox.addCommand(cmdSendAndStepBack);
        }
         */
        textbox.addCommand(cmdInsMe);
//#ifdef SMILES
        textbox.addCommand(cmdSmile);
//#endif
//#ifdef DETRANSLIT
//#         textbox.addCommand(cmdSendInTranslit);
//#         textbox.addCommand(cmdSendInDeTranslit);
//#endif
        textbox.addCommand(cmdSuspend);
        textbox.addCommand(cmdCancel);        

        textbox.addCommand(cmdSend);
    }

    public void commandAction(Command c, Displayable d) {

        if (!executeCommand(c, d)) {

            if (c == cmdInsMe) {
                insert("/me", 0);
                return;
            }
            if (c == cmdLastMessage) {
                setText(to.lastSendedMessage);
                return;
            }
//#ifdef SMILES
            if (c == cmdSmile) {
                new SmilePicker(caretPos, this);
                return;
            }
//#endif
//#ifndef WMUC
            if (c == cmdInsNick) {
                new AppendNick(to, caretPos, this);
                return;
            }
//#endif
            if (c == cmdCancel) {
                composing = false;
                body = null;
            }
            if (c == cmdSuspend) {
                composing = false;
                if (body != null) {
                    to.msgSuspended = body.trim();
                    body = null;
                }
            }

            if (c == cmdSend && body == null) {
                composing = false;
            }
//#ifdef DETRANSLIT
//#             if (c == cmdSendInTranslit) {
//#                 sendInTranslit = true;
//#             }
//# 
//#             if (c == cmdSendInDeTranslit) {
//#                 sendInDeTranslit = true;
//#             }
//#endif
            if (c == cmdSubj) {
                if (body == null) {
                    return;
                }
                subj = body;
                body = null; //"/me "+SR.MS_HAS_SET_TOPIC_TO+": "+subj;
            }
            // message/composing sending
            if (c == cmdSend && !(parentView instanceof ContactMessageList))
                parentView = new ContactMessageList(to);
         midlet.BombusMod.getInstance().setDisplayable(parentView);
         if (parentView instanceof ContactMessageList) {             
                ((ContactMessageList)parentView).forceScrolling();
                VirtualCanvas.nativeCanvas.repaint();
            }
//#ifdef RUNNING_MESSAGE
//#             runState = 3;
//#else
        send();
//#endif
        }
    }
//#ifdef RUNNING_MESSAGE
//#     /*
//#     0 - do nothing
//#     1 - scroll
//#     2 - send
//#     3 - send and close
//#     4 - end cycle
//#      *
//#      */
//#     int runState = 2;
//#     int strPos = 0;
//# 
//#     public void run() {
//#         while (runState < 4) {
//#             //System.out.println(runState+" "+notifyMessage);
//#             if (runState == 2) {
//#                 runState = 0;
//#                 send();
//#             }
//#             if (runState == 3) {
//#                 runState = 4;
//#                 send();
//#                 thread = null;
//#                 ((ui.VirtualList) parentView).redraw();
//#                 break;
//#             }
//#             if (runState == 1) {
//#ifdef MIDP_TICKER
//#                 if (cf.notifyWhenMessageType) {
//#                 if (notifyMessage != null)
//#                     ticker.setString(notifyMessage);
//#                     runState = 4;
//#                 }
//#else
//#                 if (cf.notifyWhenMessageType) {
//#                     textbox.setTitle(notifyMessage.substring(strPos++));
//#                     if ((notifyMessage.length() - strPos) < 0) {
//#                         strPos = 0;
//#                     }
//#                 }
//#endif
//#             }
//#             try {
//#                 Thread.sleep(250);
//#             } catch (Exception e) {
//#                 break;
//#             }
//#         }
//#     }
//#     private String notifyMessage;
//# 
//#     public void setMyTicker(String msg) {
//#         if (msg != null && !msg.equals("")) {
//#             StringBuffer out = new StringBuffer(msg);
//#             int i = 0;
//#             while (i < out.length()) {
//#                 if (out.charAt(i) < 0x03) {
//#                     out.deleteCharAt(i);
//#                 } else {
//#                     i++;
//#                 }
//#             }
//#             msg = out.toString();
//#             runState = 1;
//#         } else {
//#ifdef MIDP_TICKER
//#             if (cf.notifyWhenMessageType) {
//#                 ticker.setString("");
//#             }
//#else
//#             if (cf.notifyWhenMessageType) {
//#                 textbox.setTitle(to.toString());
//#             }
//#endif
//#             runState = 0;
//#         }
//#         notifyMessage = msg;
//#         strPos = 0;
//#     }
//#endif
    private void send() {
        String comp = null; // composing event off

        String id = String.valueOf((int) System.currentTimeMillis());

        if (body != null) {
            body = body.trim();
        }
        if (body != null || subj != null) {
//#ifdef DETRANSLIT
//#             if (sendInTranslit == true) {
//#                 if (body != null) {
//#                     body = DeTranslit.translit(body);
//#                 }
//#                 if (subj != null) {
//#                     subj = DeTranslit.translit(subj);
//#                 }
//#             }
//#             if (sendInDeTranslit == true || cf.autoDeTranslit) {
//#                 if (body != null) {
//#                     body = DeTranslit.deTranslit(body);
//#                 }
//#                 if (subj != null) {
//#                     subj = DeTranslit.deTranslit(subj);
//#                 }
//#             }
//#endif
            String from = sd.account.toString();
            Msg msg = new Msg(Msg.MESSAGE_TYPE_OUT, from, subj, body);
            msg.id = id;

            if (to.origin != Contact.ORIGIN_GROUPCHAT) {
                to.addMessage(msg);
                comp = "active"; // composing event in message
            }
        } else if (to.acceptComposing) {
            comp = (composing) ? "composing" : "paused";
        }
        if (!cf.eventComposing) {
            comp = null;
        }
        try {
            if (body != null || subj != null || comp != null) {
                to.lastSendedMessage = body;
                sd.roster.sendMessage(to, id, body, subj, comp);
            }
        } catch (Exception e) {
            sd.roster.errorLog(e.getMessage());
        }
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
}

