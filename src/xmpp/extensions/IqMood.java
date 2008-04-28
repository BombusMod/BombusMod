/*
 * IqMood.java
 * Copyright (c) 2006-2008, Daniel Apatin (ad), http://apatin.net.ru
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

package xmpp.extensions;

import Client.Contact;
import Client.Msg;
import Client.Roster;
import Client.StaticData;
import UserMood.Mood;
import UserMood.MoodList;
import com.alsutton.jabber.JabberBlockListener;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.datablocks.*;
import locale.SR;

public class IqMood implements JabberBlockListener {
    public IqMood(){}
    
    static String MOOD_NS="http://jabber.org/protocol/mood";
    
    public static JabberDataBlock query(String moodString, String text) {
        JabberDataBlock result=new Iq(null, Iq.TYPE_SET, "publish1");
        
        JabberDataBlock pubsub=result.addChildNs("pubsub", "http://jabber.org/protocol/pubsub");

        if (moodString==null) {
            JabberDataBlock retract=pubsub.addChild("retract", null);
            retract.setAttribute("node", MOOD_NS);
            retract.setAttribute("notify", "1");
            retract.addChild("item", null).setAttribute("id","current");
        } else {
            JabberDataBlock publish=pubsub.addChild("publish", null);
            publish.setAttribute("node",MOOD_NS);
            JabberDataBlock item=publish.addChild("item", null);
            item.setAttribute("id","current");
            JabberDataBlock moodItem=item.addChildNs("mood", MOOD_NS);
            moodItem.addChild(moodString, null);
            if (text!=null)
                if (text.length()>0)
                    moodItem.addChild("text", text);
        }
        return result;
    }

    public int blockArrived(JabberDataBlock data) {
        if (!(data instanceof Message)) return BLOCK_REJECTED;
        //System.out.println("iqMessage "+data.toString());
//#if MOOD
//#                 try {
//#                     JabberDataBlock event=data.getChildBlock("event");
//#                     if (event.getAttribute("xmlns").equals("http://jabber.org/protocol/pubsub#event")) {
//#                         JabberDataBlock items=event.getChildBlock("items");
//#                         if (items.getAttribute("node").equals("http://jabber.org/protocol/mood")) {
//#                             Message message = (Message) data;
//#                             String from=message.getFrom();
//#                             Mood mood=null;
//#                             if (items.getChildBlock("retract")==null) {   
//#                                 JabberDataBlock moods=items.getChildBlock("item").getChildBlock("mood");
//# 
//#                                 String userMood="";
//#                                 try {
//#                                     userMood=((JabberDataBlock)moods.getChildBlocks().firstElement()).getTagName();
//#                                     //System.out.println("usermood "+userMood);
//#                                 } catch (Exception e) { /*System.out.println("no usermood");*/ }
//# 
//#                                 String userMoodText="";
//#                                 try {
//#                                     userMoodText=moods.getChildBlock("text").getText();
//#                                 } catch (Exception e) { /*System.out.println("no userMoodText");*/ }
//#                                 mood=MoodList.getInstance().getMood(userMood, userMoodText);
//#                             }
//#                             if (mood!=null) {
//#                                 int index=0;
//#                                 Roster roster=StaticData.getInstance().roster;
//#                                 synchronized (roster.hContacts) {
//#                                     while (index<roster.hContacts.size()) {
//#                                         Contact c=(Contact)roster.hContacts.elementAt(index);
//#                                         if ( c.origin>Contact.ORIGIN_ROSTERRES && c.getStatus()>=Presence.PRESENCE_OFFLINE && c.getNewMsgsCount()==0 )
//#                                             roster.hContacts.removeElementAt(index);
//#                                         else {
//#                                             index++;
//#                                             if (c.getBareJid().equals(from) && c.getStatus()<Presence.PRESENCE_OFFLINE) {
//#                                                 c.setUserMood(mood);
//#                                                 Msg m=new Msg(Msg.MESSAGE_TYPE_HISTORY, from, SR.MS_USER_MOOD, c.getUserMoodLocale()+"\n"+c.getUserMoodText());
//#                                                 roster.messageStore(c, m);
//#                                             }
//#                                         }
//#                                     }
//#                                  }
//#                             }
//#                             return BLOCK_PROCESSED;
//#                         }
//#                     }
//#                 } catch (Exception e) { }
//#endif
        return BLOCK_REJECTED;
    }
}