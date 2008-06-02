/*
 * Groups.java
 *
+ * Created on 8.05.2005, 0:36
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

import java.util.*;
import locale.SR;

import com.alsutton.jabber.JabberBlockListener;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.datablocks.Iq;

import ui.VirtualList;

/**
 *
 * @author Evg_S
 */
public class Groups implements JabberBlockListener{
    
    Vector groups;
    
    StaticData sd = StaticData.getInstance();

    public final static int TYPE_SELF=1;
    public final static int TYPE_NO_GROUP=2;
    public final static int TYPE_COMMON=3;
    public final static int TYPE_VISIBLE=4;
    public final static int TYPE_MUC=5;
    public final static int TYPE_IGNORE=6;
    public final static int TYPE_NOT_IN_LIST=7;
    public final static int TYPE_SEARCH_RESULT=8;
    public final static int TYPE_TRANSP=9;
    
    public final static String COMMON_GROUP=SR.MS_GENERAL;
    
    private final static String GROUPSTATE_NS="http://bombusmod.net.ru/groups";
    
    public Groups(){
        groups=new Vector();
        addGroup(SR.MS_TRANSPORTS, Groups.TYPE_TRANSP);
        addGroup(SR.MS_SELF_CONTACT, Groups.TYPE_SELF);
        addGroup(SR.MS_SEARCH_RESULTS, Groups.TYPE_SEARCH_RESULT);
        addGroup(SR.MS_NOT_IN_LIST, Groups.TYPE_NOT_IN_LIST);
        addGroup(SR.MS_IGNORE_LIST, Groups.TYPE_IGNORE);
        addGroup(SR.MS_VISIBLE_GROUP, Groups.TYPE_VISIBLE);
        addGroup(Groups.COMMON_GROUP, Groups.TYPE_NO_GROUP);
    }

    private int rosterContacts;
    private int rosterOnline;
    
    public void resetCounters(){
        for (Enumeration e=groups.elements();e.hasMoreElements();){
            Group grp=(Group)e.nextElement();
	    grp.startCount();
        }
	rosterContacts=0;
        rosterOnline=0;
    }
    
    public void addToVector(Vector d, int index){
        Group gr=(Group)groups.elementAt(index);
        if (!gr.visible) return;
        if (gr.contacts.size()>0){
            d.addElement(gr);
            if (!gr.collapsed) for (Enumeration e=gr.contacts.elements();e.hasMoreElements();){
                d.addElement(e.nextElement());
            }
        }
	gr.finishCount();
        
        if (index==Groups.TYPE_SEARCH_RESULT) return;//don't count this contacts
        if (index==Groups.TYPE_NOT_IN_LIST) return;//don't count this contacts
        
	rosterContacts+=gr.getNContacts();
	rosterOnline+=gr.getOnlines();
    }

    public Group getGroup(int type) {
        for (Enumeration e=groups.elements();e.hasMoreElements();){
            Group grp=(Group)e.nextElement();
            if (grp.type==type) return grp;
        }
        return null;
    }
    
    public Enumeration elements(){
        return groups.elements();
    }
    
    public Group getGroup(String name) {
        for (Enumeration e=groups.elements();e.hasMoreElements();){
            Group grp=(Group)e.nextElement();
            if (name.equals(grp.name)) return grp;
        }
        return null;
    }
    
    public Group addGroup(String name, int type) {
        Group ng=new Group(name);
        ng.type=type;
        return addGroup(ng);
    }
    
    public Group addGroup(Group ng) {
        groups.addElement(ng);
        VirtualList.sort(groups);
        return ng;
    }

    public Vector getRosterGroupNames(){
        Vector s=new Vector();
        for (int i=0; i<groups.size(); i++) {
	    Group grp=(Group) groups.elementAt(i);
            if (grp.type<TYPE_NO_GROUP) continue;
            if (grp.type>TYPE_IGNORE) continue;
            s.addElement(grp.name);
        }
        return s;
    }
    public int getCount() {return groups.size();}

    public int getRosterContacts() { return rosterContacts; }
    public int getRosterOnline() { return rosterOnline; }

    void removeGroup(Group g) {
        groups.removeElement(g);
    }

    public int blockArrived(JabberDataBlock data) {
        if (data instanceof Iq) {
            if (data.getTypeAttribute().equals("result")) {
                JabberDataBlock query=data.findNamespace("query", "jabber:iq:private");
                if (query==null) 
                    return BLOCK_REJECTED;
                JabberDataBlock gs=query.findNamespace("gs", GROUPSTATE_NS);
                if (gs==null || gs.getChildBlocks()==null) 
                    return BLOCK_REJECTED;
                for (Enumeration e=gs.getChildBlocks().elements(); e.hasMoreElements();) {
                    JabberDataBlock item=(JabberDataBlock)e.nextElement();
                    String groupName=item.getText();
                    boolean collapsed=item.getAttribute("state").equals("collapsed");
                    Group grp=getGroup(groupName);
                    if (grp==null) 
                        continue;
                    grp.collapsed=collapsed;
                }
                sd.roster.reEnumRoster();
                return NO_MORE_BLOCKS;
            }
        }
        return BLOCK_REJECTED;
    }

    public void queryGroupState(boolean get) {
        if (!sd.roster.isLoggedIn()) 
            return;
        
        JabberDataBlock iq=new Iq(null, (get)? Iq.TYPE_GET : Iq.TYPE_SET, (get)? "queryGS" : "setGS");
        JabberDataBlock query=iq.addChildNs("query", "jabber:iq:private");
        JabberDataBlock gs=query.addChildNs("gs", GROUPSTATE_NS);
        
        if (get) {
            sd.roster.theStream.addBlockListener(this);
        } else {
            for (Enumeration e=groups.elements(); e.hasMoreElements();) {
                Group grp=(Group)e.nextElement();
                if (grp.collapsed) {
                    gs.addChild("item", grp.getName()).setAttribute("state", "collapsed");
                }
            }
        }
        sd.roster.theStream.send(iq);
    }
 }
