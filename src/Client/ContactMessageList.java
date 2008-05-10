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
//# import History.HistoryStorage;
//#endif
import Messages.MessageList;
import images.RosterIcons;
import locale.SR;
import ui.MainBar;
import java.util.*;
import javax.microedition.lcdui.*;
//#ifdef CLIPBOARD
//# import util.ClipBoard;
//#endif
//#ifdef ARCHIVE
import archive.MessageArchive;
//#endif

public class ContactMessageList extends MessageList
{
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
//#ifdef ANTISPAM
//#     Command cmdBlock = new Command(SR.MS_BLOCK_PRIVATE, Command.SCREEN, 22);
//#     Command cmdUnlock = new Command(SR.MS_UNLOCK_PRIVATE, Command.SCREEN, 23);
//#endif
//#ifdef CLIPBOARD    
//#     Command cmdSendBuffer=new Command(SR.MS_SEND_BUFFER, Command.SCREEN, 15);
//#endif
//#ifdef FILE_IO
    Command cmdSaveChat=new Command(SR.MS_SAVE_CHAT, Command.SCREEN, 16);
//#endif
//#ifdef CLIPBOARD
//#     private ClipBoard clipboard;
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
//#ifdef PEP
//#         mainbar.addElement(null);
//#ifdef PEP_TUNE
//#         mainbar.addElement(null);
//#endif
//#endif
        
        cursor=0;//activate
//#ifndef WMUC     
//#ifdef ANTISPAM
//#         if (contact instanceof MucContact && contact.origin!=Contact.ORIGIN_GROUPCHAT && cf.antispam) {
//#             MucContact mc=(MucContact) contact;
//#             if (mc.roleCode!=MucContact.GROUP_MODERATOR && mc.affiliationCode!=MucContact.AFFILIATION_MEMBER) {
//#                 switch (mc.getPrivateState()) {
//#                     case MucContact.PRIVATE_DECLINE:
//#                         addCommand(cmdUnlock);
//#                         break;
//#                     case MucContact.PRIVATE_NONE:
//#                     case MucContact.PRIVATE_REQUEST:
//#                         addCommand(cmdUnlock);
//#                         addCommand(cmdBlock);
//#                         break;
//#                     case MucContact.PRIVATE_ACCEPT:
//#                         addCommand(cmdBlock);
//#                         break;
//#                 }
//#                 
//#             }
//#         }
//#endif
//#endif
        addCommand(cmdMessage);
//#ifndef WMUC
        if (contact instanceof MucContact && contact.origin==Contact.ORIGIN_GROUPCHAT) {
            addCommand(cmdReply);
        }
//#endif
        addCommand(cmdPurge);
        
        if (contact.origin!=Contact.ORIGIN_GROUPCHAT)
            addCommand(cmdActions);
    
	addCommand(cmdActive);
        addCommand(cmdQuote);
//#ifdef ARCHIVE
        addCommand(cmdArch);
//#endif
//#if TEMPLATES
        addCommand(cmdTemplate);
//#endif
//#ifdef FILE_IO
        addCommand(cmdSaveChat);
//#endif
        setCommandListener(this);
        
        contact.setIncoming(0);
//#ifdef HISTORY
//#         if (cf.lastMessages && !contact.isHistoryLoaded())
//#             loadRecentList();
//#endif
        moveCursorTo(contact.firstUnread());
    }

    public void showNotify(){
//#if AUTODELETE
//#         getRedraw(true);
//#endif
        super.showNotify();
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
            } else {
                removeCommand(cmdSubscribe);
                removeCommand(cmdUnsubscribed);
            }
        } catch (Exception e) {}
//#ifdef CLIPBOARD
//#         if (!clipboard.isEmpty() && cf.useClipBoard) {
//#             addCommand(cmdSendBuffer);
//#         }
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
        int num=1;
//#ifdef PEP
//#         if (contact.hasMood()) {
//#             num++;
//#             getMainBarItem().setElementAt(RosterIcons.iconTransparent, num);
//#         }
//#ifdef PEP_TUNE
//#         if (contact.pepTune) {
//#             num++;
//#             getMainBarItem().setElementAt(RosterIcons.iconTransparent, num);
//#         }
//#endif
//#endif
        num++;
        getMainBarItem().setElementAt((contact.vcard==null)?null:RosterIcons.iconHasVcard, num);
        num++;
        getMainBarItem().setElementAt(sd.roster.getEventIcon(), num);
//#ifdef PEP
//#         if (!contact.hasMood()) {
//#             num++;
//#             getMainBarItem().setElementAt(RosterIcons.iconTransparent, num);
//#         }
//#ifdef PEP_TUNE
//#         if (!contact.pepTune) {
//#             num++;
//#             getMainBarItem().setElementAt(RosterIcons.iconTransparent, num);
//#         }
//#endif
//#endif
    }   
    
    public void markRead(int msgIndex) {
	if (msgIndex>=getItemCount()) return;
        if (msgIndex<contact.lastUnread) return;
        
        if (cursor==(messages.size()-1)) {
            if (contact.moveToLatest) {
                contact.moveToLatest=false;
                moveCursorEnd();
            }
        }
        
        sd.roster.countNewMsgs();
//#if AUTODELETE
//#         getRedraw(contact.redraw);
//#     }
//#     
//#     private void getRedraw(boolean redraw) {
//#         if (redraw) {
//#             contact.redraw=false;
//#             messages=new Vector();
//#             redraw();
//#         }
//# 
//#     }
//#     
//#     private void setRedraw() {
//#         contact.redraw=false;
//#         messages=new Vector();
//#endif
    }
    
    public int getItemCount(){ return contact.msgs.size(); }

    public Msg getMessage(int index) { 
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
        if (c==cmdPurge) {
            if (messages.isEmpty()) return;
            clearReadedMessageList();
        }
        
        /** login-critical section */
        if (!sd.roster.isLoggedIn()) return;

        if (c==cmdMessage) { 
            contact.msgSuspended=null; 
            keyGreen(); 
        }
        if (c==cmdResume) { keyGreen(); }
        if (c==cmdQuote) {
            Quote();
        }
        if (c==cmdActions) {
//#ifndef WMUC
            if (contact instanceof MucContact) {
                MucContact mc=(MucContact) contact;
                new RosterItemActions(display, mc, -1);
            } else {
//#endif
                new RosterItemActions(display, contact, -1);
//#ifndef WMUC
            }
//#endif
        }
	
	if (c==cmdActive) {
	    new ActiveContacts(display, contact);
	}
        
        if (c==cmdReply) {
            Reply();
        }
//#if (FILE_IO && HISTORY)
//#         if (c==cmdSaveChat) {
//#             saveMessages();
//#         }
//#endif        
//#if TEMPLATES
        if (c==cmdTemplate) {
            try {
                MessageArchive.store(getMessage(cursor),2);
            } catch (Exception e) {/*no messages*/}
        }
//#endif
        if (c==cmdSubscribe) {
            sd.roster.doSubscribe(contact);
        }
		
        if (c==cmdUnsubscribed) {
            sd.roster.sendPresence(contact.getBareJid(), "unsubscribed", null, false);
        }
//#ifndef WMUC     
//#ifdef ANTISPAM
//#         if (c==cmdUnlock) {
//#             MucContact mc=(MucContact) contact;
//#             mc.setPrivateState(MucContact.PRIVATE_ACCEPT);
//# 
//#             if (!contact.tempMsgs.isEmpty()) {
//#                 for (Enumeration tempMsgs=contact.tempMsgs.elements(); tempMsgs.hasMoreElements(); ) 
//#                 {
//#                     Msg tmpmsg=(Msg) tempMsgs.nextElement();
//#                     contact.addMessage(tmpmsg);
//#                 }
//#                 contact.purgeTemps();
//#             }
//#             redraw();
//#         }
//# 
//#         if (c==cmdBlock) {
//#             MucContact mc=(MucContact) contact;
//#             mc.setPrivateState(MucContact.PRIVATE_DECLINE);
//# 
//#             if (!contact.tempMsgs.isEmpty())
//#                 contact.purgeTemps();
//#             redraw();
//#         }
//#endif
//#endif
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

    private void clearReadedMessageList() {
        contact.smartPurge(cursor+1);
        messages=new Vector();
        
        contact.lastUnread=getItemCount()-1;
        
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
        
        (sd.roster.me=new MessageEdit(display,contact,contact.msgSuspended)).setParentView(this);
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
                new ActiveContacts(display, contact);
                return;        
            case KEY_NUM9:
                Quote();
                return;  
//#ifdef CLIPBOARD
//#             case SIEMENS_VOLUP:
//#             case SIEMENS_CAMERA:
//#                  if (cf.phoneManufacturer==Config.SIEMENS || cf.phoneManufacturer==Config.SIEMENS2) { //copy&copy+
//#                     if (messages.isEmpty()) 
//#                         return;
//#                     try {
//#                         StringBuffer clipstr=new StringBuffer();
//#                         clipstr.append(clipboard.getClipBoard());
//#                         if (clipstr.length()>0)
//#                             clipstr.append("\n\n");
//# 
//#                         clipstr.append((getMessage(cursor).getSubject()==null)?"":getMessage(cursor).getSubject()+"\n");
//#                         clipstr.append(getMessage(cursor).quoteString());
//# 
//#                         clipboard.setClipBoard(clipstr.toString());
//#                         clipstr=null;
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
        sd.roster.searchActiveContact(-1);
    }

    private void Reply() {
        try {
            if (getMessage(cursor).messageType < Msg.MESSAGE_TYPE_PRESENCE/*.MESSAGE_TYPE_HISTORY*/) return;
            if (getMessage(cursor).messageType == Msg.MESSAGE_TYPE_SUBJ) return;

            Msg msg=getMessage(cursor);

            sd.roster.me=new MessageEdit(display,contact,msg.from+": ");
        } catch (Exception e) {/*no messages*/}
    }
    
    private void Quote() {
        try {
            String msg=new StringBuffer()
                .append((char)0xbb) //
                .append(" ")
		.append((getMessage(cursor).getSubject()==null)?"":getMessage(cursor).getSubject()+"\n")
                .append(getMessage(cursor).quoteString())
                .append("\n")
                .toString();
            sd.roster.me=new MessageEdit(display,contact,msg);
            msg=null;
        } catch (Exception e) {/*no messages*/}
    }
    
//#ifdef HISTORY
//#     public void loadRecentList() {
//#         contact.setHistoryLoaded(true);
//#         HistoryStorage hs = new HistoryStorage(contact.getBareJid());
//#         Vector history=hs.importData();
//#         
//#         for (Enumeration messages=history.elements(); messages.hasMoreElements(); )  {
//#             Msg message=(Msg) messages.nextElement();
//#             if (!isMsgExists(message)) {
//#                 message.setHistory(true);
//#                 contact.msgs.insertElementAt(message, 0);
//#             }
//#         }
//#     }
//#     
//# 
//#     private boolean isMsgExists(Msg msg) {
//#          for (Enumeration messages=contact.msgs.elements(); messages.hasMoreElements(); )  {
//#             Msg message=(Msg) messages.nextElement();
//#             if (message.getBody().equals(msg.getBody())) {
//#                 return true;
//#             }
//#          }
//#         return false;
//#     }
//#endif

//#if (FILE_IO && HISTORY)
//#     private void saveMessages() {
//#         if (cf.msgPath==null) {
//#ifdef POPUPS
//#            sd.roster.setWobbler(3, (Contact) null, "Please enter valid path to store log");
//#endif
//#            return;
//#         }
//#         
//#         String histRecord="log_"+contact.getBareJid();
//#          
//#         for (Enumeration messages=contact.msgs.elements(); messages.hasMoreElements(); ) {
//#             Msg message=(Msg) messages.nextElement();
//#             new HistoryAppend(message, false, histRecord);
//#         }
//#     }
//#endif
    public void destroyView(){
        sd.roster.activeContact=null;
        sd.roster.reEnumRoster(); //to reset unread messages icon for this conference in roster
        if (display!=null)
            display.setCurrent(sd.roster);
    }
}
