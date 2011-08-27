/*
 * ServiceDiscovery.java
 *
 * Created on 4.06.2005, 21:12
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
//#ifndef WMUC
import Conference.ConferenceForm;
//#endif
import images.RosterIcons;
import java.util.*;
import Menu.MenuCommand;
import locale.SR;
import Colors.ColorTheme;
import ui.*;
import com.alsutton.jabber.*;
import com.alsutton.jabber.datablocks.*;
import Client.*;
import ui.MainBar;
import ui.controls.AlertBox;
import ui.controls.form.DefForm;
import xmpp.XmppError;
import xmpp.extensions.IqRegister;

/**
 *
 * @author Evg_S
 */
public class ServiceDiscovery 
        extends DefForm
        implements        
        JabberBlockListener, ServerBox.ServiceNotify
{
    public final static String NS_ITEMS="http://jabber.org/protocol/disco#items";
    public final static String NS_INFO="http://jabber.org/protocol/disco#info";
    private final static String NS_SRCH="jabber:iq:search";
    private final static String NS_GATE="jabber:iq:gateway";
    private final static String NS_MUC="http://jabber.org/protocol/muc";
    public final static String NS_CMDS="http://jabber.org/protocol/commands";

    /*private final static String strCmds="Execute";
    private final int AD_HOC_INDEX=17;*/
    
    private MenuCommand cmdBrowse=new MenuCommand(SR.MS_BROWSE, MenuCommand.OK, 1, RosterIcons.ICON_PROGRESS_INDEX);
    private MenuCommand cmdRfsh=new MenuCommand(SR.MS_REFRESH, MenuCommand.SCREEN, 2, RosterIcons.ICON_FT);
    private MenuCommand cmdFeatures=new MenuCommand(SR.MS_FEATURES, MenuCommand.SCREEN, 3, RosterIcons.ICON_VCARD);
    private MenuCommand cmdSrv=new MenuCommand(SR.MS_SERVER, MenuCommand.SCREEN, 10, RosterIcons.ICON_ON);
    //private MenuCommand cmdAdd=new MenuCommand(SR.MS_ADD_TO_ROSTER, Command.SCREEN, 11, RosterIcons.ICON_NEW); //FS#464 => this string is commented in SR.java'
    private MenuCommand cmdBack=new MenuCommand(SR.MS_BACK, MenuCommand.BACK, 98, RosterIcons.ICON_BACK);    

    private Stack stackItems=new Stack();
    
    private Vector features = new Vector();
    
    private Vector cmds;
    
    private String service;
    private String node;

    private int discoIcon;

    private JabberStream stream;


    /** Creates a new instance of ServiceDiscovery */
    public ServiceDiscovery(String service, String node, boolean search) {
        super(null);

        mainbar = new MainBar(3, null, null, false);
        mainbar.addRAlign();
        mainbar.addElement(null);
        
        stream=sd.theStream;
        stream.cancelBlockListenerByClass(this.getClass());
        stream.addBlockListener(this);
        //sd.roster.discoveryListener=this;        
        
        this.node=node;
        
        enableListWrapping(true);
        
        if (service != null && search) {
            this.service=service;
            requestQuery(NS_SRCH, "discosrch");
        } else if (service != null) {            
            browse(service, node, true);
        } else {            
            browse(sd.account.getServer(), null, true);
        }        
    }
    
    private String discoId(String id) {
        return id+service.hashCode();
    }
    
    protected void beginPaint(){ mainbar.setElementAt(sd.roster.getEventIcon(), 4); }
    
    
    private void mainbarUpdate(){
        mainbar.setElementAt(new Integer(discoIcon), 0);
        mainbar.setElementAt((service==null)?SR.MS_RECENT:service, 2);
        mainbar.setElementAt(sd.roster.getEventIcon(), 4);
	
	int size = itemsList.size();
	String count=null;
        
	removeMenuCommand(cmdBrowse);
        
	if (size > 0) {
	    menuCommands.insertElementAt(cmdBrowse, 0); 
	    count=" ("+size+") ";
	}
        mainbar.setElementAt(count,1);
    }
    
    private void requestQuery(String namespace, String id){
        discoIcon=RosterIcons.ICON_PROGRESS_INDEX; 
        mainbarUpdate(); 
        redraw();
        
        JabberDataBlock req=new Iq(service, Iq.TYPE_GET, discoId(id));
        JabberDataBlock qry=req.addChildNs("query", namespace);
        qry.setAttribute("node", node);

        //stream.addBlockListener(this);
        //System.out.println(">> "+req.toString());
        stream.send(req);
    }
    
    private void requestCommand(String namespace, String id){
        discoIcon=RosterIcons.ICON_PROGRESS_INDEX; 
        mainbarUpdate(); 
        redraw();
        
        JabberDataBlock req=new Iq(service, Iq.TYPE_SET, id);
        JabberDataBlock qry=req.addChildNs("command", namespace);
        qry.setAttribute("node", node);
        qry.setAttribute("action", "execute");	

        //stream.addBlockListener(this);
        //System.out.println(req.toString());
        stream.send(req);
    }
    
    public int blockArrived(JabberDataBlock data) {
        if (!(data instanceof Iq)) return JabberBlockListener.BLOCK_REJECTED;
        String id=data.getAttribute("id");
        if (!id.startsWith("disco")) return JabberBlockListener.BLOCK_REJECTED;
        
        if (data.getTypeAttribute().equals("error")) {
            //System.out.println(data.toString());
            discoIcon=RosterIcons.ICON_ERROR_INDEX;
            mainbarUpdate();
            //redraw();
            
            /*XmppError xe=XmppError.findInStanza(data);
            
            /*new AlertBox(data.getAttribute("from"), xe.toString()) {
                public void yes() { };
                public void no() { };           
                public void destroyView() {exitDiscovery(false); super.destroyView();}
            };*/

        //    return JabberBlockListener.BLOCK_PROCESSED;
        }
        if (!data.getTypeAttribute().equals("result")) {
            JabberDataBlock command1 = data.getChildBlock("query");
            JabberDataBlock command2 = data.getChildBlock("command");
            if (command1 == null) {
                if (command2 != null) {
                    command1 = command2;
                }
                String node1 = command1.getAttribute("node");
                if ((node1 != null) && (node1.startsWith("http://jabber.org/protocol/rc#"))) {
                    id = "discocmd"; //hack
                }
                node1 = null;
            }
        }

        JabberDataBlock query=data.getChildBlock((id.equals("discocmd"))?"command":"query");
        Vector childs=query.getChildBlocks();
        //System.out.println(id);

        if (id.equals(discoId("disco2"))) {
            Vector items1=new Vector();
            if (childs!=null)
            for (Enumeration e=childs.elements(); e.hasMoreElements(); ){
                JabberDataBlock i=(JabberDataBlock)e.nextElement();
                if (i.getTagName().equals("item")){
                    String name=i.getAttribute("name");
                    String jid=i.getAttribute("jid");
                    String node1=i.getAttribute("node");
                    if (name == null) { // workaround for M-Link (jabber.org) and maybe others
                        int resourcePos=jid.indexOf('/');
                        if (resourcePos>-1)
                            name = jid.substring(resourcePos + 1, jid.length());
                    }
                    Object serv=null;
                    if (node1==null) {
                        int resourcePos=jid.indexOf('/');
                        if (resourcePos>-1)
                            jid=jid.substring(0, resourcePos);
                        serv=new DiscoContact(name, jid, 0);
                    } else {
                        serv=new Node(name, node1);
                    }
                    items1.addElement(serv);
                }
            }
            
            showResults(items1);
        } else if (id.equals(discoId("disco"))) {
            Vector cmds1=new Vector();
            boolean showPartialResults=false;
            boolean loadItems=true;
            boolean client=false;
            if (childs!=null) {
                JabberDataBlock identity=query.getChildBlock("identity");
                if (identity!=null) {
                    String category=identity.getAttribute("category");
                    String type=identity.getTypeAttribute();
                    if (category.equals("automation") && type.equals("command-node"))  {
                      //  cmds1.addElement(new DiscoCommand(RosterIcons.ICON_AD_HOC, strCmds));
                        requestCommand(NS_CMDS, "discocmd");
                    }
                    if (category.equals("conference")) {
                        cmds1.addElement(new DiscoCommand(RosterIcons.ICON_GCJOIN_INDEX, SR.MS_JOIN_CONFERENCE));
                        if (service.indexOf('@')<=0) {
                            loadItems=false;
                            showPartialResults=true;
                            cmds1.addElement(new DiscoCommand(RosterIcons.ICON_ROOMLIST, SR.MS_LOAD_ROOMLIST));
                        }
                    }
                 }
                for (Enumeration e=childs.elements(); e.hasMoreElements();) {
                    JabberDataBlock i=(JabberDataBlock)e.nextElement();
                    if (i.getTagName().equals("feature")) {
                        String var=i.getAttribute("var");
                        features.addElement(var);
                        //if (var.equals(NS_MUC)) { cmds.addElement(new DiscoCommand(RosterIcons.ICON_GCJOIN_INDEX, strJoin)); }
                        if (var.equals(NS_SRCH)) { cmds1.addElement(new DiscoCommand(RosterIcons.ICON_SEARCH_INDEX, SR.MS_SEARCH)); }
                        if (var.equals(IqRegister.NS_REGS)) { cmds1.addElement(new DiscoCommand(RosterIcons.ICON_REGISTER_INDEX, SR.MS_REGISTER)); }
                        if (var.equals(NS_GATE)) { showPartialResults=true; }
                        //if (var.equals(NODE_CMDS)) { cmds.addElement(new DiscoCommand(AD_HOC_INDEX,strCmds)); }
                    }
                }
             }
            /*if (data.getAttribute("from").equals(service)) */ { //FIXME!!!
                this.cmds=cmds1;
                if (loadItems) requestQuery(NS_ITEMS, "disco2");
                if (showPartialResults) showResults(new Vector());
            }
        } else if (id.startsWith ("discoreg")) {
            discoIcon=0;
            new DiscoForm(this, null, null, data, stream, "discoResult", "query").fetchMediaElements(query.getChildBlocks());
        } else if (id.startsWith("discocmd")) {
            discoIcon=0;
	    new DiscoForm(this, null, null, data, stream, "discocmd", "command");

        } else if (id.startsWith("discosrch")) {
            discoIcon=0;	    
	    new DiscoForm(this, null, null, data, stream, "discoRSearch", "query");
        } else if (id.startsWith("discoR")) {
            String text=SR.MS_DONE;
            String mb=data.getTypeAttribute();
            if (mb.equals("error")) {
                text=XmppError.findInStanza(data).toString();
            }
            if (text.equals(SR.MS_DONE) && id.endsWith("Search") ) {
                new SearchResult( data);
            } else {
                new AlertBox(mb, text) {
                    public void yes() { }
                    public void no() { }   
                    public void destroyView() {exitDiscovery(false); super.destroyView();}
                };
            }
        }
        redraw();
        return JabberBlockListener.BLOCK_PROCESSED;
    }

    public void eventOk() {
        super.eventOk();
        Object o = getFocusedObject();
        if (o != null) {
            if (o instanceof Contact) {
                browse(((Contact) o).jid.toString(), null, false);
            }
			if (o instanceof Node) {
                browse(service, ((Node) o).getNode(), false);
            }
        }
    }

    private void showResults(final Vector items) {
        try {
            sort(items);
        } catch (Exception e) { 
            //e.printStackTrace(); 
        }
        
        /*if (data.getAttribute("from").equals(service)) - jid hashed in id attribute*/ //{
            for (Enumeration e=cmds.elements(); e.hasMoreElements();) 
                items.insertElementAt(e.nextElement(),0);
            loadItemsFrom(items);
            moveCursorHome();
            discoIcon=0; 
            mainbarUpdate(); 
        //}
    }
    State st=new State();
    
    public final void browse(String service, String node, boolean start) {
	if (!start) {
	    if (node == null || !node.equals(NS_CMDS)) {
		pushState();
	    }
	}
	    
	itemsList.removeAllElements();
	features.removeAllElements();
	this.service = service;
	this.node = node;
	requestQuery(NS_INFO, "disco");
    }

    void pushState() {
	st.cursor = cursor;
	st.items = new Vector();
	int size = itemsList.size();
	for (int i = 0; i < size; i++) {
	    st.items.addElement(itemsList.elementAt(i));
	}
	st.service = this.service;
	st.node = this.node;
	st.features = features;
	stackItems.push(st);
    }
    void popState() {
	st = (State) stackItems.pop();
	service = st.service;
	node = st.node;
	features = st.features;	
	itemsList.removeAllElements();
	int size = st.items.size();
	for (int i = 0; i < size; i++) {
	    itemsList.addElement(st.items.elementAt(i));
	}
	moveCursorTo(st.cursor);
    }
    
    public void menuAction(MenuCommand c, VirtualList d){
	if (c==cmdBrowse) eventOk();
        if (c==cmdBack) exitDiscovery(false);            
        if (c==cmdRfsh) { if (service!=null) requestQuery(NS_INFO, "disco"); }
        if (c==cmdSrv) { new ServerBox(service, this); }
        if (c==cmdFeatures) { new DiscoFeatures( service, features); }
        if (c==cmdCancel) exitDiscovery(true);
    }
    
    public void exitDiscovery(boolean cancel) {
	if (cancel || stackItems.empty()) {
	    stream.cancelBlockListener(this);
	    super.destroyView();
	} else {
	    popState();
	    discoIcon = 0;
	    //requestQuery(NS_INFO,"disco");
	    mainbarUpdate();
	    moveCursorTo(st.cursor);
	    redraw();
	}
    }

    
    public void destroyView()	{
        exitDiscovery(false);        
    }
    
    public void OkNotify(String selectedServer) {
        browse(selectedServer, null, false);
    }

    private class DiscoCommand extends IconTextElement {
        String name;
        int index;
        int icon;
        
        public DiscoCommand(int icon, String name) {
            super(RosterIcons.getInstance());
            this.icon=icon; this.name=name;
        }
        public int getColor(){ return ColorTheme.getColor(ColorTheme.DISCO_CMD); }
        public int getImageIndex() { return icon; }
        public String toString(){ return name; }
        public void onSelect(){
            switch (icon) {
//#ifndef WMUC
                case RosterIcons.ICON_GCJOIN_INDEX: 
                    int rp=service.indexOf('@');
                    String room = null; 
                    if (rp > 0) {
                        room = service.substring(0, rp);                        
                    }
                    new ConferenceForm(room, service, null, false);
                    break;                
//#endif
                case RosterIcons.ICON_SEARCH_INDEX:
                    requestQuery(NS_SRCH, "discosrch");
                    break;
                case RosterIcons.ICON_REGISTER_INDEX:
                    requestQuery(IqRegister.NS_REGS, "discoreg");
                    break;
                case RosterIcons.ICON_ROOMLIST:
                    requestQuery(NS_ITEMS, "disco2");
                    break;
               /* case RosterIcons.ICON_AD_HOC:
                    requestCommand(NODE_CMDS, "discocmd");*/
                default:
            }
        }
    }
    

    /*public void showMenu() {
        new MyMenu( this, this, SR.MS_DISCO, null, menuCommands);
    }*/
    
    public void commandState() {
        menuCommands.removeAllElements();
        addMenuCommand(cmdBrowse);
        addMenuCommand(cmdRfsh);
        addMenuCommand(cmdSrv);
        addMenuCommand(cmdFeatures);
        //addCommand(cmdAdd);
        addMenuCommand(cmdCancel);        
    }
   

}
class State{
    public String service;
    public String node;
    public Vector items;
    public Vector features;
    public int cursor;
}
