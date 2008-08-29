/*
 * ContactMessageList.java
 *
 * Created on 19.02.2005, 23:54
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
import Conference.MucContact;
//#endif
//#ifdef HISTORY
//# import History.HistoryAppend;
//#ifdef LAST_MESSAGES
//# import History.HistoryStorage;
//#endif
//#ifdef HISTORY_READER
//# import History.HistoryReader;
//#endif
//#endif
import Menu.RosterItemActions;
import Messages.MessageList;
import images.RosterIcons;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import locale.SR;
import ui.MainBar;
import java.util.*;
//#ifndef MENU_LISTENER
import javax.microedition.lcdui.Command;
//#else
//# import Menu.Command;
//#endif
//#ifdef CLIPBOARD
//# import util.ClipBoard;
//#endif
//#ifdef ARCHIVE
import Archive.MessageArchive;
//#endif

public class ContactMessageList extends MessageList {
    Contact contact;

    Command cmdSubscribe=new Command(SR.MS_SUBSCRIBE, Command.SCREEN, 1);
    Command cmdUnsubscribed=new Command(SR.MS_DECLINE, Command.SCREEN, 2);
    Command cmdMessage=new Command(SR.MS_NEW_MESSAGE,Command.SCREEN,3);
    Command cmdResume=new Command(SR.MS_RESUME,Command.SCREEN,1);
    Command cmdReply=new Command(SR.MS_REPLY,Command.SCREEN,4);
    Command cmdQuote=new Command(SR.MS_QUOTE,Command.SCREEN,5);
//#ifdef ARCHIVE
    Command cmdArch=new Command(SR.MS_ADD_ARCHIVE,Command.SCREEN,6);
//#endif
    Command cmdPurge=new Command(SR.MS_CLEAR_LIST, Command.SCREEN, 7);
    Command cmdActions=new Command(SR.MS_CONTACT,Command.SCREEN,8);
    Command cmdActive=new Command(SR.MS_ACTIVE_CONTACTS,Command.SCREEN,11);
//#if TEMPLATES
    Command cmdTemplate=new Command(SR.MS_SAVE_TEMPLATE,Command.SCREEN,14);
//#endif
//#ifdef FILE_IO
    Command cmdSaveChat=new Command(SR.MS_SAVE_CHAT, Command.SCREEN, 16);
//#endif
//#ifdef HISTORY
//#ifdef HISTORY_READER
//#          Command cmdReadHistory=new Command("Read history", Command.SCREEN, 17);
//#endif
//# //        if (cf.lastMessages && !contact.isHistoryLoaded()) loadRecentList();
//#endif
//#ifdef CLIPBOARD    
//#     Command cmdSendBuffer=new Command(SR.MS_SEND_BUFFER, Command.SCREEN, 15);
//#endif
    
//#ifdef CLIPBOARD    
//#     private ClipBoard clipboard=ClipBoard.getInstance();
//#endif

    StaticData sd=StaticData.getInstance();
    
    private Config cf;
    
    private boolean composing=true;

    /** Creates a new instance of MessageList */
    public ContactMessageList(Contact contact, Display display) {
        super(display);
        this.contact=contact;
        sd.roster.activeContact=contact;

        cf=Config.getInstance();
//#ifdef CLIPBOARD
//#         if (cf.useClipBoard)
//#             clipboard=ClipBoard.getInstance();
//#endif
        
        MainBar mainbar=new MainBar(contact);
        setMainBarItem(mainbar);
        
        mainbar.addRAlign();
        mainbar.addElement(null);
        mainbar.addElement(null);
        mainbar.addElement(null);
//#ifdef PEP
//#         mainbar.addElement(null);
//#ifdef PEP_TUNE
//#         mainbar.addElement(null);
//#endif
//#endif
//#ifdef CLIENTS_ICONS
//#ifdef PLUGINS
//#         if (sd.ClientsIcons)
//#endif
//#             mainbar.addElement(null);
//#endif
        
        cursor=0;//activate
        commandState();
        setCommandListener(this);
        
        contact.setIncoming(0);
//#ifdef HISTORY
//#ifdef LAST_MESSAGES
//#         if (cf.lastMessages && !contact.isHistoryLoaded()) loadRecentList();
//#endif
//#endif
        if (contact.msgs.size()>0)
            moveCursorTo(contact.firstUnread());
    }
    
    public void commandState(){
//#ifdef MENU_LISTENER
//#         menuCommands.removeAllElements();
//#endif
        if (contact.msgSuspended!=null) 
            addCommand(cmdResume);
        
        if (cmdSubscribe==null) return;
        try {
            Msg msg=(Msg) contact.msgs.elementAt(cursor);
            if (msg.messageType==Msg.MESSAGE_TYPE_AUTH) {
                addCommand(cmdSubscribe);
                addCommand(cmdUnsubscribed);
            }
        } catch (Exception e) {}
        addCommand(cmdMessage);
        if (contact.msgs.size()>0) {
//#ifndef WMUC
            if (contact instanceof MucContact && contact.origin==Contact.ORIGIN_GROUPCHAT) {
                addCommand(cmdReply);
            }
//#endif
            addCommand(cmdQuote);
            addCommand(cmdPurge);
        
//#ifdef CLIPBOARD
//#             if (cf.useClipBoard) {
//#                 addCommand(cmdCopy);
//#                 if (!clipboard.isEmpty()) addCommand(cmdCopyPlus);
//#             }
//#endif
//#ifdef MENU_LISTENER
//#             if (isHasScheme()) 
//#endif
                addCommand(cmdxmlSkin);
//#ifdef MENU_LISTENER
//#             if (isHasUrl()) 
//#endif
                addCommand(cmdUrl);
        }
        
        if (contact.origin!=Contact.ORIGIN_GROUPCHAT)
            addCommand(cmdActions);
    
	addCommand(cmdActive);
        if (contact.msgs.size()>0) {
//#ifdef ARCHIVE
//#ifdef PLUGINS
//#          if (sd.Archive)
//#endif
            addCommand(cmdArch);
//#endif
//#if TEMPLATES
//#ifdef PLUGINS         
//#          if (sd.Archive)
//#endif
            addCommand(cmdTemplate);
//#endif
        }
//#ifdef CLIPBOARD
//#         if (!clipboard.isEmpty() && cf.useClipBoard) {
//#             addCommand(cmdSendBuffer);
//#         }
//#endif
//#ifdef HISTORY
//#         if (cf.saveHistory)
//#             if (cf.msgPath!=null)
//#                 if (!cf.msgPath.equals(""))
//#                     if (contact.msgs.size()>0)
//#                         addCommand(cmdSaveChat);
//#ifdef HISTORY_READER
//#         if (cf.saveHistory && cf.lastMessages)
//#             addCommand(cmdReadHistory);
//#endif
//#endif
        addCommand(cmdBack);
    }
    
    public void showNotify(){
        sd.roster.activeContact=contact;
//#ifdef LOGROTATE
//#         getRedraw(true);
//#endif
        super.showNotify();
//#ifndef MENU_LISTENER
        if (cmdResume==null) return;
        if (contact.msgSuspended==null)
            removeCommand(cmdResume);
        else
            addCommand(cmdResume);
        
        if (cmdSubscribe==null) return;
        try {
            Msg msg=(Msg) contact.msgs.elementAt(cursor); 
            if (msg.messageType==Msg.MESSAGE_TYPE_AUTH) {
                addCommand(cmdSubscribe);
                addCommand(cmdUnsubscribed);
            }
            else {
                removeCommand(cmdSubscribe);
                removeCommand(cmdUnsubscribed);
            }
        } catch (Exception e) {}
//#endif
    }

    protected void beginPaint(){
        markRead(cursor);
        
        if (cursor==(messages.size()-1)) {
            if (contact.moveToLatest) {
                contact.moveToLatest=false;
                moveCursorEnd();
            }
        }
        int num=2;
//#ifdef CLIENTS_ICONS
//#ifdef PLUGINS
//#         if (sd.ClientsIcons)
//#endif
//#             if (contact.client>-1) getMainBarItem().setElementAt(RosterIcons.iconTransparent, num++);
//#endif
        
//#ifdef PEP
//#         if (contact.hasMood()) getMainBarItem().setElementAt(RosterIcons.iconTransparent, num++);
//#ifdef PEP_TUNE
//#         else if (contact.pepTune) getMainBarItem().setElementAt(RosterIcons.iconTransparent, num++);
//#endif
//#endif
        getMainBarItem().setElementAt((contact.vcard==null)?null:RosterIcons.iconHasVcard, num++);
        getMainBarItem().setElementAt(sd.roster.getEventIcon(), num++);
//#ifdef CLIENTS_ICONS
//#ifdef PLUGINS
//#         if (sd.ClientsIcons)
//#endif
//#             if (contact.client<0) getMainBarItem().setElementAt(RosterIcons.iconTransparent, num++);
//#endif
        
//#ifdef PEP
//#         if (!contact.hasMood()) getMainBarItem().setElementAt(RosterIcons.iconTransparent, num++);
//#ifdef PEP_TUNE
//#         if (!contact.pepTune) getMainBarItem().setElementAt(RosterIcons.iconTransparent, num++);
//#endif
//#endif
    }   
    
    public void markRead(int msgIndex) {
	if (msgIndex>=getItemCount()) return;
        if (msgIndex<contact.lastUnread) return;
        /*
        if (cursor==(messages.size()-1)) {
            if (contact.moveToLatest) {
                contact.moveToLatest=false;
                moveCursorEnd();
            }
        }
        */
        sd.roster.countNewMsgs();
//#ifdef LOGROTATE
//#         getRedraw(contact.redraw);
//#endif
    }
//#ifdef LOGROTATE
//#     private void getRedraw(boolean redraw) {
//#         if (redraw) {
//#             contact.redraw=false;
//#             messages=null;
//#             messages=new Vector();
//#             redraw();
//#         }
//#     }
//#     private void setRedraw() {
//#         contact.redraw=false;
//#             messages=null;
//#         messages=new Vector();
//#     }
//#endif
    public int getItemCount(){ return contact.msgs.size(); }

    public Msg getMessage(int index) {
        if (index> getItemCount()-1) return null;
        
	Msg msg=(Msg) contact.msgs.elementAt(index); 
	if (msg.unread) contact.resetNewMsgCnt();
	msg.unread=false;
	return msg;
    }
    
    public void focusedItem(int index){ 
        markRead(index); 
    }
    
    public void commandAction(Command c, Displayable d){
        super.commandAction(c,d);
		
        /** login-insensitive commands */
//#ifdef ARCHIVE
        if (c==cmdArch) {
            try {
                MessageArchive.store(getMessage(cursor),1);
            } catch (Exception e) {/*no messages*/}
        }
//#endif
//#if TEMPLATES
        if (c==cmdTemplate) {
            try {
                MessageArchive.store(getMessage(cursor),2);
            } catch (Exception e) {/*no messages*/}
        }
//#endif
        if (c==cmdPurge) {
            if (messages.isEmpty()) return;
            clearReadedMessageList();
        }
//#ifdef HISTORY
//#ifdef HISTORY_READER
//#         if (c==cmdReadHistory) {
//#             new HistoryReader(display, contact);
//#             return;
//#         }
//#endif
//#endif
//#if (FILE_IO && HISTORY)
//#         if (c==cmdSaveChat) saveMessages();
//#endif
        /** login-critical section */
        if (!sd.roster.isLoggedIn()) return;

        if (c==cmdMessage) { 
            contact.msgSuspended=null; 
            keyGreen(); 
        }
        if (c==cmdResume) keyGreen();
        if (c==cmdQuote) Quote();
        if (c==cmdReply) Reply();
        
        if (c==cmdActions) {
//#ifndef WMUC
            if (contact instanceof MucContact) {
                MucContact mc=(MucContact) contact;
                new RosterItemActions(display, this, mc, -1);
            } else {
//#endif
                new RosterItemActions(display, this, contact, -1);
//#ifndef WMUC
            }
//#endif
        }
	if (c==cmdActive) new ActiveContacts(display, this, contact);
        
        if (c==cmdSubscribe) {
            sd.roster.doSubscribe(contact);
        }
		
        if (c==cmdUnsubscribed) {
            sd.roster.sendPresence(contact.bareJid, "unsubscribed", null, false);
        }
//#ifdef CLIPBOARD
//#         if (c==cmdSendBuffer) {
//#             String from=sd.account.toString();
//#             String body=clipboard.getClipBoard();
//#             String subj=null;
//#             
//#             String id=String.valueOf((int) System.currentTimeMillis());
//#             Msg msg=new Msg(Msg.MESSAGE_TYPE_OUT,from,subj,body);
//#             msg.id=id;
//#             
//#             try {
//#                 if (body!=null)
//#                     sd.roster.sendMessage(contact, id, body, subj, null);
//#                 contact.addMessage(new Msg(Msg.MESSAGE_TYPE_OUT,from,subj,"clipboard sended ("+body.length()+"chars)"));
//#             } catch (Exception e) {
//#                 contact.addMessage(new Msg(Msg.MESSAGE_TYPE_OUT,from,subj,"clipboard NOT sended"));
//#             }
//#             redraw();
//#         }
//#endif
    }

    public void clearReadedMessageList() {
        contact.smartPurge(cursor+1);
        messages=null;
        messages=new Vector();
        cursor=0;
        moveCursorHome();
        redraw();
    }
    
    public void eventLongOk(){
        super.eventLongOk();
//#ifndef WMUC
        if (contact instanceof MucContact && contact.origin==Contact.ORIGIN_GROUPCHAT) {
            Reply();
            return;
        }
//#endif
        keyGreen();
    }
    
    public void keyGreen(){
        if (!sd.roster.isLoggedIn()) 
            return;
        
        sd.roster.me=new MessageEdit(display, this, contact, contact.msgSuspended);
        contact.msgSuspended=null;
    }
    
    protected void keyClear(){
        if (!messages.isEmpty())
            clearReadedMessageList();
    }
    
    public void keyRepeated(int keyCode) {
        if (keyCode==KEY_NUM0) 
            clearReadedMessageList();
	else 
            super.keyRepeated(keyCode);
    }  

    public void keyPressed(int keyCode) {
        if (keyCode==KEY_POUND) {
            if (!sd.roster.isLoggedIn())
                return;
//#ifndef WMUC
            if (contact instanceof MucContact && contact.origin==Contact.ORIGIN_GROUPCHAT) {
                Reply();
                return;
            }
//#endif
            keyGreen();
            return;
        } else super.keyPressed(keyCode);
    }

    public void userKeyPressed(int keyCode) {
        switch (keyCode) {
            case KEY_NUM4:
                if (cf.useTabs)
                    sd.roster.searchActiveContact(-1); //previous contact with messages
                else
                    super.pageLeft();
                return;
            case KEY_NUM6:
                if (cf.useTabs)
                    sd.roster.searchActiveContact(1); //next contact with messages
                else
                    super.pageRight();
                return;
            case KEY_NUM3:
                new ActiveContacts(display, this, contact);
                return;        
            case KEY_NUM9:
                if (sd.roster.isLoggedIn()) 
                    Quote();
                return;  
//#ifdef CLIPBOARD
//#             case SIEMENS_VOLUP:
//#             case SIEMENS_CAMERA:
//#                  if (cf.phoneManufacturer==Config.SIEMENS || cf.phoneManufacturer==Config.SIEMENS2) { //copy&copy+
//#                     if (messages.isEmpty()) 
//#                         return;
//#                     try {
//#                         if (clipboard.getClipBoard().length()>0) 
//#                             clipboard.append(getMessage(cursor));
//#                         else
//#                             clipboard.add(getMessage(cursor));
//#                     } catch (Exception e) {/*no messages*/}
//#                     return;
//#                  }
//#                  break;
//#             case SIEMENS_VOLDOWN:
//#             case SIEMENS_MPLAYER:
//#                 if (cf.phoneManufacturer==Config.SIEMENS || cf.phoneManufacturer==Config.SIEMENS2) { //clear clipboard
//#                     clipboard.setClipBoard("");
//#                     return;
//#                 }
//#                 break;
//#endif
        }
    }
    
    public void touchLeftPressed(){
//#ifdef MENU_LISTENER
//#         showMenu();
//#else
        sd.roster.searchActiveContact(-1);
//#endif
    }
    
    private void Reply() {
        try {
            Msg msg=getMessage(cursor);
            
            if (msg==null ||
                msg.messageType == Msg.MESSAGE_TYPE_OUT ||
                msg.messageType == Msg.MESSAGE_TYPE_SUBJ)
                keyGreen();
            else
                sd.roster.me=new MessageEdit(display, this, contact, msg.from+": ");
        } catch (Exception e) {/*no messages*/}
    }
    
    private void Quote() {
        try {
            String msg=new StringBuffer()
                .append((char)0xbb) //
                .append(" ")
                .append(getMessage(cursor).quoteString())
                .append("\n")
                .toString();
            sd.roster.me=new MessageEdit(display, this, contact, msg);
            msg=null;
        } catch (Exception e) {/*no messages*/}
    }
    
//#ifdef HISTORY
//#ifdef LAST_MESSAGES
//#     public void loadRecentList() {
//#         contact.setHistoryLoaded(true);
//#         HistoryStorage hs = new HistoryStorage(contact.bareJid);
//#         Vector history=hs.importData();
//# 
//#         for (Enumeration messages2=history.elements(); messages2.hasMoreElements(); )  {
//#             Msg message=(Msg) messages2.nextElement();
//#             if (!isMsgExists(message)) {
//#                 message.setHistory(true);
//#                 contact.msgs.insertElementAt(message, 0);
//#             }
//#             message=null;
//#         }
//#         history=null;
//#     }
//# 
//#     private boolean isMsgExists(Msg msg) {
//#          for (Enumeration messages=contact.msgs.elements(); messages.hasMoreElements(); )  {
//#             Msg message=(Msg) messages.nextElement();
//#             if (message.getBody().equals(msg.getBody())) {
//#                 return true;
//#             }
//#             message=null;
//#          }
//#         return false;
//#     }
//#endif
//# 
//#     private void saveMessages() {
//#         StringBuffer histRecord=new StringBuffer("chatlog_");
//#ifndef WMUC
//#         if (contact instanceof MucContact) {
//#             if (contact.origin>=Contact.ORIGIN_GROUPCHAT) {
//#                 histRecord.append(contact.bareJid);
//#             } else {
//#                 String nick=contact.getJid();
//#                 int rp=nick.indexOf('/');
//#                 histRecord.append(nick.substring(rp+1)).append("_").append(nick.substring(0, rp));
//#                 nick=null;
//#             }
//#         } else {
//#endif
//#             histRecord.append(contact.bareJid);
//#ifndef WMUC
//#         }
//#endif
//#         StringBuffer messageList=new StringBuffer();
//#         for (Enumeration cmessages=contact.msgs.elements(); cmessages.hasMoreElements(); ) {
//#             Msg message=(Msg) cmessages.nextElement();
//#             messageList.append(message.quoteString());
//#             messageList.append("\n\n");
//#         }
//#         HistoryAppend.getInstance().addMessageList(messageList.toString(), histRecord.toString());
//#         messageList=null;
//#         histRecord=null;
//#     }
//#endif

    public void destroyView(){
        sd.roster.activeContact=null;
        sd.roster.reEnumRoster(); //to reset unread messages icon for this conference in roster
        if (display!=null)
            display.setCurrent(sd.roster);
    }
    
//#ifdef MENU_LISTENER
//#     //public Vector menuCommands=new Vector();
//# 
//#     public void showMenu() {
//#          commandState();
//#          super.showMenu();
//#     }
//#     
//#     public boolean isHasScheme() {
//#         if (contact.msgs.size()<1) {
//#             return false;
//#         }
//#         String body=((Msg) contact.msgs.elementAt(cursor)).getBody();
//#         
//#         if (body.indexOf("xmlSkin")>-1) return true;
//#         return false;
//#     }
//# 
//#     public boolean isHasUrl() {
//#         if (contact.msgs.size()<1) {
//#             return false;
//#         }
//#         String body=((Msg) contact.msgs.elementAt(cursor)).getBody();
//#         if (body.indexOf("http://")>-1) return true;
//#         if (body.indexOf("https://")>-1) return true;
//#         if (body.indexOf("ftp://")>-1) return true;
//#         if (body.indexOf("tel:")>-1) return true;
//#         if (body.indexOf("native:")>-1) return true;
//#         return false;
//#     }
//#endif
}
