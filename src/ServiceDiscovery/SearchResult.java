/*
 * SearchResult.java
 *
 * Created on 10.07.2005, 21:40
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

package ServiceDiscovery;
import java.util.*;
import javax.microedition.lcdui.*;
import locale.SR;
import ui.*;
import com.alsutton.jabber.*;
import com.alsutton.jabber.datablocks.*;
import Client.*;
import ui.MainBar;

/**
 *
 * @author EvgS
 */
public class SearchResult
        extends VirtualList
        implements CommandListener {
    
    StaticData sd=StaticData.getInstance();
    private Command cmdBack=new Command(SR.MS_BACK, Command.BACK, 98);
    private Command cmdAdd=new Command(SR.MS_ADD, Command.SCREEN, 1);
    
    private Vector items;
    boolean xData;
    
    /** Creates a new instance of SearchResult */
    public SearchResult(Display display, JabberDataBlock result) {
        super(display);
        
        String service=result.getAttribute("from");
        
        setMainBarItem(new MainBar(2, null, service));
        
        setCommandListener(this);
        addCommand(cmdBack);
        
        items=new Vector();
        
        JabberDataBlock query=result.getChildBlock("query");
        if (query==null) return;
        
        addCommand(cmdAdd);

        
        JabberDataBlock x=query.getChildBlock("x");
        if (x!=null) { query=x; xData=true; }
        
        sd.roster.cleanupSearch();
        
        for (Enumeration e=query.getChildBlocks().elements(); e.hasMoreElements(); ){
            JabberDataBlock child=(JabberDataBlock) e.nextElement();
	    
            if (child.getTagName().equals("item")) {
                StringBuffer vcard=new StringBuffer();
                String jid="";
		
	        int status=Presence.PRESENCE_OFFLINE;

                // Form vcard=new Form(null);
                if (!xData) { jid=child.getAttribute("jid"); }
                // пїЅпїЅпїЅпїЅ item
                for (Enumeration f=child.getChildBlocks().elements(); f.hasMoreElements(); ){
                    JabberDataBlock field=(JabberDataBlock) f.nextElement();
                    String name;
                    String value;
                    if (xData) {
                        name=field.getAttribute("var");
                        value=field.getChildBlockText("value");
                    } else {
                        name=field.getTagName();
                        value=field.getText();
                    }
                    if (name.equals("jid")) jid=value;
                    if (value.length()>0)
                    {
                        //vcard.append(new StringItem(name,value+"\n"));
                        vcard.append(name)
                             .append((char)0xa0)
                             .append(value)
                             .append((char)'\n');
                    }
		    // status returned by jit
		    if (name.equals("status")) if (!value.equals("offline")) status=Presence.PRESENCE_ONLINE;
                }
                Contact serv=new DiscoContact(null, jid, status);
                serv.setGroup(sd.roster.groups.getGroup(Groups.TYPE_SEARCH_RESULT));
                serv.subscr="search";
                Msg m=new Msg(Msg.MESSAGE_TYPE_IN, jid, "Short info", vcard.toString());
                m.unread=false;
                serv.addMessage(m);
                
                items.addElement(serv);
                sd.roster.addContact(serv);
                vcard=null;
            }
        }
//#ifdef POPUPS
        if (getItemCount()<1)
            VirtualList.setWobble(3, /*(Contact) null,*/ "Not found :(");
//#endif
        sd.roster.reEnumRoster();
    }
    
    public int getItemCount(){ return items.size();}
    public VirtualElement getItemRef(int index) { return (VirtualElement) items.elementAt(index);}

    public void commandAction(Command c, Displayable d){
        if (c==cmdAdd){
            destroyView();
            new ContactEdit(display, (Contact)getFocusedObject());
            return;
        }
        
        if (c==cmdBack) destroyView(); 
    }
    
    public void eventOk(){
        try {
            Contact c=(Contact)getFocusedObject();
            if (c==null) return;
            new ContactMessageList((Contact) getFocusedObject(), display);
        } catch (Exception e) {}
    }
}
