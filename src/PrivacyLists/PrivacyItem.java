/*
 * PrivacyItem.java
 *
 * Created on 10.09.2005, 21:30
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
import images.RosterIcons;
import locale.SR;
import ui.*;
import com.alsutton.jabber.*;

/**
 *
 * @author EvgS
 */
public class PrivacyItem extends IconTextElement {
//#ifdef PLUGINS
//#     public static String plugin = new String("PLUGIN_PRIVACY");
//#endif
    
    public final static String types[]={"jid", "group", "subscription", "ANY"};
    public final static int ITEM_JID=0;
    public final static int ITEM_GROUP=1;
    public final static int ITEM_SUBSCR=2;
    public final static int ITEM_ANY=3;

    public final static String actions[]={"allow", "deny"};
    public final static int ITEM_ALLOW=0;
    public final static int ITEM_BLOCK=1;

    public final static String stanzas[]={"message", "presence-in", "presence-out", "iq"};
    public final static int STANZA_MSG=0;
    public final static int STANZA_PRESENCE_IN=1;
    public final static int STANZA_PRESENCE_OUT=2;
    public final static int STANZA_IQ=3;
    
    public final static String subscrs[]={"none", "from", "to", "both"};
    
    int type;    //jid|group|subscription|ANY
    String value=new String();
    int action=0;
    int order;
    
    boolean messageStz=false;
    boolean presenceInStz=false;
    boolean presenceOutStz=false;
    boolean iqStz=false;
    
    public int getImageIndex(){
        return action+RosterIcons.ICON_PRIVACY_ALLOW;
    }
    
    public String toString() { return (type==ITEM_ANY)?"ANY":value; }
    
    /** Creates a new instance of PrivacyItem */
    public PrivacyItem() {
        super(RosterIcons.getInstance());
    }
    
    public PrivacyItem(JabberDataBlock item) {
        this();
        String t=item.getTypeAttribute();
        if (t==null) {
            type=ITEM_ANY;
        } else {
            for (type=0; type<2; type++) {
                if (t.equals(types[type])) {
                    break;
                }
            }
        }
        
        value=item.getAttribute("value");
        action=item.getAttribute("action").equals("allow")?0:1;
        order=Integer.parseInt(item.getAttribute("order"));

        messageStz=(item.getChildBlock(stanzas[STANZA_MSG])!=null);
        presenceInStz=(item.getChildBlock(stanzas[STANZA_PRESENCE_IN])!=null);
        presenceOutStz=(item.getChildBlock(stanzas[STANZA_PRESENCE_OUT])!=null);
        iqStz=(item.getChildBlock(stanzas[STANZA_IQ])!=null);
    }
    
    public static PrivacyItem itemIgnoreList(){
        PrivacyItem item=new PrivacyItem();
        item.type=ITEM_GROUP;
        item.value=SR.MS_IGNORE_LIST;
        item.iqStz=true;
        item.presenceOutStz=true;
        return item;
    }
    
    public JabberDataBlock constructBlock() {
        JabberDataBlock item=new JabberDataBlock("item", null, null);
        if (type!=ITEM_ANY) {
            item.setTypeAttribute(types[type]);
            item.setAttribute("value", value);
        }
        item.setAttribute("action", actions[action] );
        item.setAttribute("order", String.valueOf(order));

        if (messageStz) item.addChild(stanzas[STANZA_MSG], null);
        if (presenceInStz) item.addChild(stanzas[STANZA_PRESENCE_IN], null);
        if (presenceOutStz) item.addChild(stanzas[STANZA_PRESENCE_OUT], null);
        if (iqStz) item.addChild(stanzas[STANZA_IQ], null);

        return item;
    }
	
    public String getTipString() {
        StringBuffer tip=new StringBuffer(actions[action]);
        tip.append(" if ").append(types[type]);
        if (type!=ITEM_ANY) {
            tip.append('=').append(value);
        }
        tip.append(' ');
        
        if (messageStz && presenceInStz && presenceOutStz && iqStz) {
            tip.append("all stanzas"); 
        } else if (!messageStz && !presenceInStz && !presenceOutStz && !iqStz) { 
            tip.append("all stanzas"); 
        } else {
            if (messageStz) { tip.append(stanzas[STANZA_MSG]); tip.append(" "); }
            if (presenceInStz) { tip.append(stanzas[STANZA_PRESENCE_IN]); tip.append(" "); }
            if (presenceOutStz) { tip.append(stanzas[STANZA_PRESENCE_OUT]); tip.append(" "); }
            if (iqStz) { tip.append(stanzas[STANZA_IQ]); tip.append(" "); }
        }
        return tip.toString();
    }
}
