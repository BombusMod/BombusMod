/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import Menu.JuickThingsMenu;
import Menu.MenuCommand;
import Menu.MyMenu;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.datablocks.Message;
import images.RosterIcons;
import java.util.Hashtable;
import java.util.Vector;
import locale.SR;
import ui.VirtualList;
import ui.controls.form.DefForm;

/**
 *
 * @author Vitaly
 */
public class Juick {

    public static String NS_MESSAGE = "http://juick.com/message";

//#ifdef JUICK
//#     static MenuCommand cmdJuickMessageReply=new MenuCommand(SR.MS_JUICK_MESSAGE_REPLY, MenuCommand.SCREEN, 1);
//#     static MenuCommand cmdJuickSendPrivateReply;
//#     public static MenuCommand cmdJuickThings=new MenuCommand(SR.MS_JUICK_THINGS, MenuCommand.SCREEN, 3, RosterIcons.ICON_JUICK);
//#     static MenuCommand cmdJuickMessageDelete=new MenuCommand(SR.MS_JUICK_MESSAGE_DELETE, MenuCommand.SCREEN, 4);
//#     static MenuCommand cmdJuickPostSubscribe;
//#     static MenuCommand cmdJuickPostUnsubscribe=new MenuCommand(SR.MS_JUICK_POST_UNSUBSCRIBE, MenuCommand.SCREEN, 6);
//#     static MenuCommand cmdJuickPostRecommend=new MenuCommand(SR.MS_JUICK_POST_RECOMMEND, MenuCommand.SCREEN, 7);
//#     static MenuCommand cmdJuickPostShow=new MenuCommand(SR.MS_JUICK_POST_SHOW, MenuCommand.SCREEN, 8);
//# 
//#     public static MenuCommand cmdJuickCommands=new MenuCommand(SR.MS_COMMANDS+" Juick", MenuCommand.SCREEN, 15, RosterIcons.ICON_JUICK);
//#     static Vector currentJuickCommands = new Vector();
//# 
//#endif

    public static void commandState(DefForm form) {
	form.addMenuCommand(cmdJuickCommands);
    }

    public static void menuAction(MenuCommand c, ContactMessageList d) {
        Msg body = d.getMessage(d.cursor);
        if (c == cmdJuickMessageReply) {
            juickAction(d, "", body);
        } else if (c == cmdJuickSendPrivateReply) {
            juickAction(d, "PM", body);
        } else if (c == cmdJuickMessageDelete) {
            juickAction(d, "D", body);
        } else if (c == cmdJuickPostSubscribe) {
            juickAction(d, "S", body);
        } else if (c == cmdJuickPostUnsubscribe) {
            juickAction(d, "U", body);
        } else if (c == cmdJuickPostRecommend) {
            juickAction(d, "!", body);
        } else if (c == cmdJuickPostShow) {
            juickAction(d, "+", body);
        } else if (c == cmdJuickCommands) {
            updateJuickCommands(body);
            if (currentJuickCommands.size() > 0)
                new MyMenu(d, SR.MS_COMMANDS, null, currentJuickCommands);
        }
    }

//#ifdef JUICK
//#     public static void updateJuickCommands(Msg msg) {
//# 	currentJuickCommands.removeAllElements();
//# 	String target = getTargetForJuickReply(msg);
//# 
//# 	if (!(target.length() == 0)) {
//# 	    switch (target.charAt(0)) {
//# 		case '#':
//# 		    if (target.indexOf('/') < 0) {
//# 			currentJuickCommands.addElement(cmdJuickPostRecommend);
//# 			currentJuickCommands.addElement(cmdJuickPostShow);
//# 		    }
//# 		    currentJuickCommands.addElement(cmdJuickMessageReply);
//# 		    currentJuickCommands.addElement(cmdJuickMessageDelete);
//# 		    cmdJuickPostSubscribe = new MenuCommand(SR.MS_JUICK_POST_SUBSCRIBE, MenuCommand.SCREEN, 5);
//# 		    currentJuickCommands.addElement(cmdJuickPostSubscribe);
//# 		    currentJuickCommands.addElement(cmdJuickPostUnsubscribe);
//# 		    break;
//# 		case '@':
//# 		    cmdJuickSendPrivateReply = new MenuCommand(SR.MS_JUICK_SEND_PRIVATE_REPLY + " " + target, MenuCommand.SCREEN, 3);
//# 		    currentJuickCommands.addElement(cmdJuickSendPrivateReply);
//# 		    cmdJuickPostSubscribe = new MenuCommand(SR.MS_JUICK_POST_SUBSCRIBE + " " + target, MenuCommand.SCREEN, 5);
//# 		    currentJuickCommands.addElement(cmdJuickPostSubscribe);
//# 		    break;
//# 	    }
//# 	}
//# 
//#     }
//#endif



    public static void processMessage(Message message, Msg msg) {
	JabberDataBlock juick = message.findNamespace("juick", NS_MESSAGE);
	if (juick != null) {
	    msg.isJuickMsg = true;
	    msg.JuickMid = juick.getAttribute("mid");
	    msg.JuickRid = juick.getAttribute("rid");
	    msg.JuickUid = juick.getAttribute("uname");
	}
	getJuickThings(msg);
    }

    public static Vector juickContacts  = new Vector();
    static int indexMainJuickContact  = -1;

    
    public static Contact getMainJuickContact() {
	if (indexMainJuickContact > -1) {
	    return (Contact) juickContacts.elementAt(indexMainJuickContact);
	} else {
	    return null;
	}
    }

    public static boolean isJuickContact(Contact c) {
	return c.jid.equalsViaJ2J("juick@juick.com") || c.jid.equalsViaJ2J("psto@psto.net");
    }

    public static void addJuickContact(Contact c) {
	if (isJuickContact(c)) {
	    juickContacts.addElement(c);
	    // Далее урезаный аналог updateMainJuickContact(). Побыстрее него, работает *только* при добавлении контакта.
	    if (isMainJuickContact(c)) {
		indexMainJuickContact = juickContacts.size() - 1;
	    } else if (indexMainJuickContact < 0) {
		indexMainJuickContact = 0;
	    }
	}
    }

    public static void deleteJuickContact(Contact c) {
	if (juickContacts.removeElement(c)) {
	    updateMainJuickContact();
	}
    }

    public static boolean isMainJuickContact(Contact c) {
	return c.bareJid.equals(JuickConfig.getJuickJID());
    }

    public static void updateMainJuickContact() {
	int size = juickContacts.size();
	if (size < 1) {
	    indexMainJuickContact = -1;
	} else if ((size == 1) || (JuickConfig.getJuickJID().length() == 0)) {
	    indexMainJuickContact = 0;
	} else {
	    //indexMainJuickContact = juickContacts.indexOf(new Contact("Juick", juickConfig.getJuickJID(), Presence.PRESENCE_OFFLINE, null));
	    for (int i = 0; i < juickContacts.size(); i++) {
		if (((Contact) juickContacts.elementAt(i)).bareJid.equals(JuickConfig.getJuickJID())) {
		    indexMainJuickContact = i;
		}
	    }
	    if (indexMainJuickContact < 0) {
		JuickConfig.setJuickJID("", false);
		indexMainJuickContact = 0; // Можно сделать это присваивание через рекурсию, но вроде пока не надо.
	    }
	}
    }

//#ifdef JUICK    
//# 
//#     private static void juickContactNotFound() {
//#ifdef POPUPS
//#             StaticData.getInstance().roster.setWobble(ui.controls.PopUp.TYPE_SYSTEM, "Juick", SR.MS_JUICK_CONTACT_NOT_FOUND);
//#endif
//#     }
//# 
//#     public static void getJuickThings(Msg msg) {
//# 	if (msg == null || !msg.things.isEmpty()) return;
//# 	char[] valueChars = msg.body.toCharArray();
//#         int msg_length = valueChars.length;
//# 	Vector things = new Vector();
//#         for (int i = 0; i < msg_length; i++) {
//#             if ((i == 0) || isCharBeforeJuickThing(valueChars[i - 1])) {
//#                 switch (valueChars[i]) {
//#                     case '#':
//#                     case '@':
//#                     case '*':
//#                         char firstSymbol = valueChars[i];
//#                         String thing = "" + firstSymbol;
//#                         while (i < (msg_length - 1) && isCharFromJuickThing(valueChars[++i], firstSymbol)) {
//#                             thing = thing + valueChars[i];
//#                         }
//#                         while (thing.charAt(thing.length() - 1) == '.') {
//#                             thing = thing.substring(0, thing.length() - 1);
//#                         }
//#                         if ((thing.length() > 1) && (things.indexOf(thing) < 0)) {
//#                             if (i < msg_length && ((firstSymbol == '*') && (valueChars[i] == '*'))) {
//#                                 continue;
//#                             }
//#                             things.addElement(thing);
//#                         }
//#                         if (i > 0) {
//#                             i--;
//#                         }
//#                         break;
//#                 }
//#             }
//#         }
//#         msg.things = things;
//#     }
//# 
//#     public static void viewCommands() {
//# 	Hashtable commands = new Hashtable();
//# 	commands.put("Help", "HELP");
//# 	commands.put("Recommended blogs", "@");
//# 	commands.put("Popular messages", "@top+");
//# 	commands.put("My feed", "#");
//# 	commands.put("Second page my feed", "##");
//# 	commands.put("Third page my feed", "###");
//# 	commands.put("Last messages", "#+");
//# 	commands.put("My tags", "*");
//# 	new JuickThingsMenu(commands, getMainJuickContact());
//#     }
//# 
//#     public static boolean isCharBeforeJuickThing(char ch) {
//#         switch(ch) {
//#             case '\u0020': // space
//#             case '\u0009': // tab
//#             case '\u000C': // formfeed
//#             case '\n': // newline
//#             case '\r': // carriage return
//#             case '(':
//#                 return true;
//#         }
//#         return false;
//#     }
//# 
//#     public static boolean isCharFromJuickThing(char ch, char type) {
//#         boolean result = false;
//#         switch(type) {
//#             case '#': // #number
//#                 result = (ch>46) && (ch<58) // '/', [0-9]
//#                         || ((ch>63)&&(ch<91)) // '@', [A-Z]
//#                         || ((ch>96)&&(ch<123)); // [a-z]
//#                 break;
//#             case '@': // @username
//#                 result = ((ch>47)&&(ch<58)) // [0-9]
//#                         || ((ch>63)&&(ch<91)) // '@', [A-Z]
//#                         || ((ch>96)&&(ch<123)) // [a-z]
//#                         || ((ch=='_')||(ch=='|'))
//#                         || ((ch>44)&&(ch<47)); // [-.]
//#                 break;
//#             case '*': // *tag
//#                 result = ((ch>42)&&(ch<58)) // [+,-./], [0-9]
//#                         || ((ch>64)&&(ch<91)) // [A-Z]
//#                         || ((ch>96)&&(ch<123)) // [a-z]
//#                         || ((ch>1039)&&(ch<1104)) || ((ch==1105)||(ch==1025)) // [А-Я], [а-я], 'ё', 'Ё'
//#                         || ((ch=='_')||(ch=='|')||(ch=='?')||(ch=='!')||(ch==39)) // '
//#                         || ((ch>44)&&(ch<47)); // [-.]
//#                 break;
//#         }
//#         return result;
//#     }
//# 
//#     public static String getTargetForJuickReply(Msg msg) {
//# 	String str = msg.body;
//#         if ((str == null) || (str.length() == 0))
//#             return "";
//# 
//# 	if (msg.JuickMid != null) {
//# 	    String mid = msg.JuickMid;
//# 	    if (msg.JuickRid != null)
//# 		mid += ("/" + msg.JuickRid);
//# 	    return "#" + mid;
//# 	}
//# 	if (msg.JuickUid != null) {
//# 	    return "@" + msg.JuickUid;
//# 	}
//# 
//# 
//# 	// dummy parsing for psto.net
//# 
//# 	int lastStrStartIndex = str.lastIndexOf('\n')+1;
//#         if (lastStrStartIndex < 0)
//#             return "";
//#         int numberEndsIndex = str.indexOf(" http://psto.net/", lastStrStartIndex);
//#         
//#         if (numberEndsIndex > 0) {
//#             numberEndsIndex = str.indexOf(' ', lastStrStartIndex);
//#             return str.substring(lastStrStartIndex, numberEndsIndex);
//#         }
//#         return "";
//#     }
//# 
//#     public static void juickAction(VirtualList d, String action, Msg msg) {
//#         if (Juick.getMainJuickContact() == null) {
//#             juickContactNotFound();
//#             return;
//#         }
//#         String target = getTargetForJuickReply(msg);
//#         if ((action.equals("S") || action.equals("U")) && (target.indexOf("/") > 0)) {
//#             target = target.substring(0, target.indexOf("/"));
//#         } else if (action.equals("PM") || action.length() == 0) {
//#             target+=" ";
//#         }
//#         String resultAction = action + " " + target;
//# 
//#         if (action.equals("+") || action.length() == 0) {
//#             resultAction = target+action;
//#         }
//#         try {
//#             Roster.me = new MessageEdit(d, getMainJuickContact(), resultAction, false);
//#             Roster.me.show();
//#         } catch (Exception e) {/*no messages*/}
//#     }
//# 
//#     
//#     public static boolean isJuBoContact(Contact c) {
//#         return c.jid.equalsViaJ2J("jubo@nologin.ru");
//#     }
//# /*
//#     public static boolean noRedirrectToJuickContact(Contact c) {
//#         return (isJuickContact(c)
//#          || c.jid.equalsViaJ2J("implusplus@gmail.com")
//#          || c.jid.equalsViaJ2J("tweet@excla.im")
//#          || c.jid.equalsViaJ2J("twitter@t2p.me")
//#          || c.jid.equalsServerViaJ2J("twitter.tweet.im"));
//#     }
//# 
//#     private static Contact getActualJuickContact(Contact contact) {
//#         if (noRedirrectToJuickContact(contact))
//#             return contact;
//#         else return Juick.getMainJuickContact();
//#     }*/
//#endif



}
