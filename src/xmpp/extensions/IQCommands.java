/*
 * IQCommands.java
 *
 * Created on 4.07.2008, 14:48
 *
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

import Client.Jid;
import Client.StaticData;
import com.alsutton.jabber.JabberBlockListener;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.datablocks.Iq;
import java.util.Enumeration;
import ui.Time;

/**
 *
 * @author ad
 */
public class IQCommands implements JabberBlockListener {
    
    /**
     * Creates a new instance of IQCommands
     */
    public IQCommands() { }
    

    public int blockArrived(JabberDataBlock data) {
        if (!(data instanceof Iq)) return BLOCK_REJECTED;
        String type=data.getTypeAttribute();
        String from=data.getAttribute("from");
        
        if (from!=null) {
            if (!new Jid(from).getBareJid().equals(StaticData.getInstance().roster.selfContact().getBareJid()))
                return BLOCK_REJECTED;
        } else return BLOCK_REJECTED;

//System.out.println(">>> "+data.toString());
        
        if (type.equals("get")) {
            JabberDataBlock query=data.findNamespace("query", "http://jabber.org/protocol/disco#items");
            JabberDataBlock query2=data.findNamespace("query", "http://jabber.org/protocol/disco#info");
            if (query==null){
                if (query2==null)
                    return BLOCK_REJECTED;
                else query=query2;
            }
            
            if (query.getAttribute("node").equals("http://jabber.org/protocol/commands")) {
                Iq reply=new Iq(data.getAttribute("from"), Iq.TYPE_RESULT, data.getAttribute("id"));
                reply.addChild(query);

                //http://jabber.org/protocol/rc#set-status //4.1 Change Status
                JabberDataBlock status=query.addChild("item", "");
                status.setAttribute("jid", StaticData.getInstance().roster.selfContact().getJid());
                status.setAttribute("node", "http://jabber.org/protocol/rc#set-status");
                status.setAttribute("name", "Set Status");
    /*
                //http://jabber.org/protocol/rc#leave-groupchats //4.5 Leave Groupchats 
                JabberDataBlock leaveChats=query.addChild("item", "");
                leaveChats.setAttribute("jid", StaticData.getInstance().roster.selfContact().getJid());
                leaveChats.setAttribute("node", "http://jabber.org/protocol/rc#leave-groupchats");
                leaveChats.setAttribute("name", "Leave Groupchats");
    */
                StaticData.getInstance().roster.theStream.send(reply);
//System.out.println(">>> "+reply.toString());
                return BLOCK_PROCESSED;
            } else if (query.getAttribute("node").equals("http://jabber.org/protocol/rc#set-status")) {
                processRequest(data);
                return BLOCK_PROCESSED;
            }
        } else if (type.equals("set")) {
            JabberDataBlock command=data.findNamespace("command", "http://jabber.org/protocol/commands");
            if (command==null) return BLOCK_REJECTED;
            
            if (command.getAttribute("node").equals("http://jabber.org/protocol/rc#set-status")) {
                if (command.getAttribute("sessionid")==null) {
                    processRequest(data);
                    return BLOCK_PROCESSED;
                } else if (command.getAttribute("action")!="cancel") {
                    Iq reply=new Iq(data.getAttribute("from"), Iq.TYPE_RESULT, data.getAttribute("id"));
                    JabberDataBlock cmd=reply.addChildNs("command", "http://jabber.org/protocol/commands");
                    cmd.setAttribute("status", "completed");
                    cmd.setAttribute("node", "http://jabber.org/protocol/rc#set-status");
                    cmd.setAttribute("sessionid", command.getAttribute("sessionid"));
                    StaticData.getInstance().roster.theStream.send(reply);
//System.out.println(">>> "+reply.toString());
                    //parsing task
                    JabberDataBlock x=command.findNamespace("x", "jabber:x:data");
                    Enumeration e;

                    String status="";
                    //String priority="";
                    String message="";

                    for (e=x.getChildBlocks().elements(); e.hasMoreElements(); ){
                        JabberDataBlock field=(JabberDataBlock) e.nextElement();
                        if (field.getAttribute("var").equals("status")) {
                            status=field.getChildBlockText("value");
                        } /*else if (field.getAttribute("var").equals("status-priority")) {
                            priority=field.getChildBlockText("value");
                        }*/ else if (field.getAttribute("var").equals("status-message")) {
                            message=field.getChildBlockText("value");
                        }
                    }
                    int newStatus=0;
                    if (status.equals("chat")) newStatus=1;
                    else if (status.equals("away")) newStatus=2;
                    else if (status.equals("xa")) newStatus=3;
                    else if (status.equals("dnd")) newStatus=4;
                    else if (status.equals("invisible")) newStatus=8;
                    else if (status.equals("offline")) newStatus=5;

                    StaticData.getInstance().roster.sendPresence(newStatus, message);

                    return BLOCK_PROCESSED;
                }
            }
        }
        return BLOCK_REJECTED;
    }
    
    private void processRequest(JabberDataBlock data) {
        Iq reply=new Iq(data.getAttribute("from"), Iq.TYPE_RESULT, data.getAttribute("id"));

        JabberDataBlock cmd=reply.addChildNs("command", "http://jabber.org/protocol/commands");
        cmd.setAttribute("status", "executing");
        cmd.setAttribute("node", "http://jabber.org/protocol/rc#set-status");
        cmd.setAttribute("sessionid", "set-status:"+Time.utcTime());

        JabberDataBlock x=cmd.addChildNs("x", "jabber:x:data");
        x.setTypeAttribute("form");
        x.addChild("title", "Change Status");
        x.addChild("instructions", "Choose the status and status message");

        JabberDataBlock fieldHidden=x.addChild("field", "");
        fieldHidden.setTypeAttribute("hidden");
        fieldHidden.setAttribute("var", "FORM_TYPE");
        fieldHidden.addChild("value", "http://jabber.org/protocol/rc");

        JabberDataBlock fieldStatus=x.addChild("field", "");
        fieldStatus.setTypeAttribute("list-single");
        fieldStatus.setAttribute("var", "status");
        fieldStatus.setAttribute("label", "Status");
        fieldStatus.addChild("value", "online");
        fieldStatus.addChild("required", "");

        JabberDataBlock labelOnline=fieldStatus.addChild("option", "");
        labelOnline.setAttribute("label", "Online");
        labelOnline.addChild("value", "online");

        JabberDataBlock labelChat=fieldStatus.addChild("option", "");
        labelChat.setAttribute("label", "Chat");
        labelChat.addChild("value", "chat");

        JabberDataBlock labelAway=fieldStatus.addChild("option", "");
        labelAway.setAttribute("label", "Away");
        labelAway.addChild("value", "away");

        JabberDataBlock labelExtendedAway=fieldStatus.addChild("option", "");
        labelExtendedAway.setAttribute("label", "Extended Away");
        labelExtendedAway.addChild("value", "xa");

        JabberDataBlock labelDoNotDisturb=fieldStatus.addChild("option", "");
        labelDoNotDisturb.setAttribute("label", "Do Not Disturb");
        labelDoNotDisturb.addChild("value", "dnd");

        JabberDataBlock labelInvisible=fieldStatus.addChild("option", "");
        labelInvisible.setAttribute("label", "Invisible");
        labelInvisible.addChild("value", "invisible");

        JabberDataBlock labelOffline=fieldStatus.addChild("option", "");
        labelOffline.setAttribute("label", "Offline");
        labelOffline.addChild("value", "offline");
/*
        JabberDataBlock fieldPriority=x.addChild("field", "");
        fieldPriority.setTypeAttribute("text-single");
        fieldPriority.setAttribute("var", "status-priority");
        fieldPriority.setAttribute("label", "Priority");
        fieldPriority.addChild("value", "0");
*/
        JabberDataBlock fieldMessage=x.addChild("field", "");
        fieldMessage.setTypeAttribute("text-multi");
        fieldMessage.setAttribute("var", "status-message");
        fieldMessage.setAttribute("label", "Message");

        StaticData.getInstance().roster.theStream.send(reply);
//System.out.println(">>> "+reply.toString());
    }
}
