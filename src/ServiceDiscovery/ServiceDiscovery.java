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
import Menu.MenuListener;
import Menu.MenuCommand;
import Menu.MyMenu;
import locale.SR;
import Colors.ColorTheme;
import ui.*;
import com.alsutton.jabber.*;
import com.alsutton.jabber.datablocks.*;
import Client.*;
import ui.MainBar;
import io.NvStorage;
import java.io.DataInputStream;
import java.io.EOFException;
import ui.controls.AlertBox;
import xmpp.XmppError;

/**
 *
 * @author Evg_S
 */
public class ServiceDiscovery 
        extends VirtualList
        implements
        MenuListener,
        JabberBlockListener
{
    private final static String NS_ITEMS="http://jabber.org/protocol/disco#items";
    private final static String NS_INFO="http://jabber.org/protocol/disco#info";
    private final static String NS_REGS="jabber:iq:register";
    private final static String NS_SRCH="jabber:iq:search";
    private final static String NS_GATE="jabber:iq:gateway";
    private final static String NS_MUC="http://jabber.org/protocol/muc";
    private final static String NODE_CMDS="http://jabber.org/protocol/commands";

   /* private final static String strCmds="Execute";
    private final int AD_HOC_INDEX=17;*/
    
    private MenuCommand cmdOk=new MenuCommand(SR.MS_BROWSE, MenuCommand.SCREEN, 1);
    private MenuCommand cmdRfsh=new MenuCommand(SR.MS_REFRESH, MenuCommand.SCREEN, 2);
    private MenuCommand cmdFeatures=new MenuCommand(SR.MS_FEATURES, MenuCommand.SCREEN, 3);
    private MenuCommand cmdSrv=new MenuCommand(SR.MS_SERVER, MenuCommand.SCREEN, 10);
    //private MenuCommand cmdAdd=new MenuCommand(SR.MS_ADD_TO_ROSTER, Command.SCREEN, 11); //FS#464 => this string is commented in SR.java'
    private MenuCommand cmdBack=new MenuCommand(SR.MS_BACK, MenuCommand.BACK, 98);
    private MenuCommand cmdCancel=new MenuCommand(SR.MS_CANCEL, MenuCommand.EXIT, 99);

    private StaticData sd=StaticData.getInstance();
    
    private Vector items;
    private Vector stackItems=new Vector();
    
    private Vector features;
    
    private Vector cmds;
    
    private String service;
    private String node;

    private int discoIcon;

    private JabberStream stream;

    
    /** Creates a new instance of ServiceDiscovery */
    public ServiceDiscovery(String service, String node, boolean search) {
        super();

        setMainBarItem(new MainBar(3, null, null, false));
        getMainBarItem().addRAlign();
        getMainBarItem().addElement(null);
        
        stream=sd.roster.theStream;
        stream.cancelBlockListenerByClass(this.getClass());
        stream.addBlockListener(this);
        //sd.roster.discoveryListener=this;
        
        menuCommands.removeAllElements();
        setMenuListener(this);
        addMenuCommand(cmdRfsh);
        addMenuCommand(cmdSrv);
        addMenuCommand(cmdFeatures);
        //addCommand(cmdAdd);
        addMenuCommand(cmdCancel);

        items=new Vector();
        features=new Vector();
        
        this.node=node;
        
        if (service!=null && search) {
            this.service=service;
            requestQuery(NS_SRCH, "discosrch");
        } else if (service!=null) {
            this.service=service;
            requestQuery(NS_INFO, "disco");
        } else {
            this.service=null;
            
            String myServer=sd.account.getServer();
            items.addElement(new DiscoContact(null, myServer, 0));

            try {
                DataInputStream is=NvStorage.ReadFileRecord("disco", 0);
           
                try {
                    while (true) {
                        String recent=is.readUTF();
                        if (myServer.equals(recent)) continue; //only one instance for our service
                        
                        items.addElement(new DiscoContact(null, recent, 0));
                    }
                } catch (EOFException e) { is.close(); }
            } catch (Exception e) {}
            
            //sort(items);
            discoIcon=0; 
            mainbarUpdate(); 
            moveCursorHome();
            redraw();
        }
        show(parentView);
    }
    
    private String discoId(String id) {
        return id+service.hashCode();
    }
    
    public int getItemCount(){ return items.size();}
    public VirtualElement getItemRef(int index) { return (VirtualElement) items.elementAt(index);}
    
    protected void beginPaint(){ getMainBarItem().setElementAt(sd.roster.getEventIcon(), 4); }
    
    
    private void mainbarUpdate(){
        getMainBarItem().setElementAt(new Integer(discoIcon), 0);
        getMainBarItem().setElementAt((service==null)?SR.MS_RECENT:service, 2);
        getMainBarItem().setElementAt(sd.roster.getEventIcon(), 4);
	
	int size=0;
	try { size=items.size(); } catch (Exception e) {}
	String count=null;
        
	removeMenuCommand(cmdOk);
        
	if (size>0) {
	    menuCommands.insertElementAt(cmdOk, 0); 
	    count=" ("+size+") ";
	}
        getMainBarItem().setElementAt(count,1);
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
            
            XmppError xe=XmppError.findInStanza(data);
            
            new AlertBox(data.getAttribute("from"), xe.toString()) {
                public void yes() { };
                public void no() { };
            };

            return JabberBlockListener.BLOCK_PROCESSED;
        }
        JabberDataBlock command1=data.getChildBlock("query");
        JabberDataBlock command2=data.getChildBlock("command");
        if (command1==null) {
            if (command2!=null) {
                command1=command2;
            }
            String node1 = command1.getAttribute("node");
            if ((node1!=null) && (node1.startsWith("http://jabber.org/protocol/rc#")))
                id="discocmd"; //hack
            node1=null;
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
                     //   cmds.addElement(new DiscoCommand(RosterIcons.ICON_AD_HOC, strCmds));
                        requestCommand(NODE_CMDS, "discocmd");
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
                        if (var.equals(NS_REGS)) { cmds1.addElement(new DiscoCommand(RosterIcons.ICON_REGISTER_INDEX, SR.MS_REGISTER)); }
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
//#ifndef NEW_DISCO
            new DiscoForm( data, stream, "discoResult", "query");
//#else
//#             new MyDiscoForm( data, stream, "discoResult", "query");
//#endif
        } else if (id.startsWith("discocmd")) {
            discoIcon=0;
//#ifndef NEW_DISCO
            new DiscoForm( data, stream, "discocmd", "command");
//#else
//#             new MyDiscoForm( data, stream, "discocmd", "command");
//#endif

        } else if (id.startsWith("discosrch")) {
            discoIcon=0;
//#ifndef NEW_DISCO
            new DiscoForm( data, stream, "discoRSearch", "query");
//#else
//#             new MyDiscoForm( data, stream, "discoRSearch", "query");
//#endif          
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
                };
            }
        }
        redraw();
        return JabberBlockListener.BLOCK_PROCESSED;
    }
    
    public void eventOk(){
        super.eventOk();
        Object o= getFocusedObject();
        if (o!=null) 
        if (o instanceof Contact) {
            browse( ((Contact) o).jid.getJid(), null );
        }
        if (o instanceof Node) {
            browse( service, ((Node) o).getNode() );
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
            this.items=items;
            moveCursorHome();
            discoIcon=0; 
            mainbarUpdate(); 
        //}
    }
    
    public void browse(String service, String node){
            State st=new State();
            st.cursor=cursor;
            st.items=items;
            st.service=this.service;
            st.node=this.node;
            st.features=features;
            stackItems.addElement(st);
            
            items=new Vector();
            features=new Vector();
            removeMenuCommand(cmdBack);
            addMenuCommand(cmdBack);
            this.service=service;
            this.node=node;
            requestQuery(NS_INFO,"disco");
    }
    
    public void menuAction(MenuCommand c, VirtualList d){
	if (c==cmdOk) eventOk();
        if (c==cmdBack) exitDiscovery(false);            
        if (c==cmdRfsh) { if (service!=null) requestQuery(NS_INFO, "disco"); }
        if (c==cmdSrv) { new ServerBox(service, this); }
        if (c==cmdFeatures) { new DiscoFeatures( service, features); }
        if (c==cmdCancel) exitDiscovery(true);
    }
    
    private void exitDiscovery(boolean cancel){
        if (cancel || stackItems.isEmpty()) {
            stream.cancelBlockListener(this);
            midlet.BombusMod.getInstance().setDisplayable(parentView);
        } else {
            State st=(State)stackItems.lastElement();
            stackItems.removeElement(st);
            
            service=st.service;
            items=st.items;
            features=st.features;
            discoIcon=0;
            
            mainbarUpdate();
            moveCursorTo(st.cursor);
            redraw();
        }
    }
    
    
    public void destroyView()	{
        exitDiscovery(false);
        super.destroyView();
    }
    
    public void userKeyPressed(int keyCode) {
        super.userKeyPressed(keyCode);
        
        switch (keyCode) {
            case KEY_NUM4:
                pageLeft(); break;
            case KEY_NUM6:
                pageRight(); break;
        }
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
                case RosterIcons.ICON_GCJOIN_INDEX: {
                    int rp=service.indexOf('@');
                    String room=null;
                    String server=service;
                    if (rp>0) {
                        room=service.substring(0,rp);
                        server=service.substring(rp+1);
                    }
                    new ConferenceForm(room, service, null, false);
                    break;
                }
//#endif
                case RosterIcons.ICON_SEARCH_INDEX:
                    requestQuery(NS_SRCH, "discosrch");
                    break;
                case RosterIcons.ICON_REGISTER_INDEX:
                    requestQuery(NS_REGS, "discoreg");
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
    private void exitDiscovery(){
        stream.cancelBlockListener(this);
        destroyView();
    }

    protected boolean key(int keyCode, boolean key_long) {
        if (!key_long) {
            if (keyCode == Config.SOFT_RIGHT || keyCode == Config.KEY_BACK) {
                if (!reconnectWindow.getInstance().isActive()) {
                    exitDiscovery(false);
                    return true;
                }
            }
        }

        return super.key(keyCode, key_long);
    }

    public void showMenu() {
        new MyMenu( this, this, SR.MS_DISCO, null, menuCommands);
    }

}
class State{
    public String service;
    public String node;
    public Vector items;
    public Vector features;
    public int cursor;
}
