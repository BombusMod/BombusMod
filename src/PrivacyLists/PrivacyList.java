/*
 * PrivacyList.java
 *
 * Created on 26 Август 2005 г., 23:08
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

package PrivacyLists;
import com.alsutton.jabber.datablocks.Iq;
import images.RosterIcons;
import java.util.*;
import ui.*;
import Client.*;
import com.alsutton.jabber.*;

/**
 *
 * @author EvgS
 */
public class PrivacyList extends IconTextElement{
    
    String name;
    boolean isActive;
    boolean isDefault;
    
    Vector rules=new Vector(); 
    
    /** Creates a new instance of PrivacyList */
    public PrivacyList(String name) {
        super(RosterIcons.getInstance());
        this.name=name;
    }
    
    public int getImageIndex() {return (isActive)?
        RosterIcons.ICON_PRIVACY_ACTIVE:
        RosterIcons.ICON_PRIVACY_PASSIVE; }
    
    public String toString() {
        StringBuffer result=new StringBuffer((name==null)? "<none>": name).append(' ');
        if (isDefault) result.append("(default)");
        return result.toString();
    }
    
    
    public void generateList(){
        int index=0;
        
        JabberDataBlock list = listBlock();
        for (Enumeration e=rules.elements(); e.hasMoreElements(); ) {
            
            PrivacyItem item=(PrivacyItem)e.nextElement();
            item.order=index++;
                        
            list.addChild( item.constructBlock() );
        }
        PrivacyList.privacyListRq(true, list, "storelst");
    }

    private JabberDataBlock listBlock() {
        JabberDataBlock list=new JabberDataBlock("list", null, null);
        list.setAttribute("name", name);
        return list;
    }
    
    public void deleteList(){
        JabberDataBlock list=listBlock();
        PrivacyList.privacyListRq(true, list, "storelst");
    }
  
    public void activate (String atr) {
        JabberDataBlock a=new JabberDataBlock(atr, null, null);
        a.setAttribute("name", name);
        privacyListRq(true, a, "plset");
    }
    
    public void addRule(PrivacyItem rule) {
        int index=0;
        while (index<rules.size()) {
            if ( rule.order <= ((PrivacyItem)rules.elementAt(index)).order ) break;
            index++;
        }
        rules.insertElementAt(rule, index);
    }
    
    public final static void privacyListRq(boolean set, JabberDataBlock child, String id){
        JabberDataBlock pl=new Iq(null, (set)? Iq.TYPE_SET: Iq.TYPE_GET, id);
        JabberDataBlock qry=pl.addChildNs("query", "jabber:iq:privacy");
        if (child!=null) qry.addChild(child);
        
        //System.out.println(pl);
        StaticData.getInstance().roster.theStream.send(pl);
    }
}
