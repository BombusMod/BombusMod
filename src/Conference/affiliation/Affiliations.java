/*
 * AffiliationList.java
 *
 * Created on 30.10.2005, 12:34
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

package Conference.affiliation;

import Client.*;
import com.alsutton.jabber.JabberBlockListener;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.JabberStream;
import com.alsutton.jabber.datablocks.Iq;
import images.RosterIcons;
import java.util.Enumeration;
import locale.SR;
import ui.MainBar;
import ui.controls.form.DefForm;
import Menu.MenuCommand;
//#ifdef CLIPBOARD
//# import util.ClipBoard;
//#endif
import ui.VirtualList;

/**
 *
 * @author EvgS
 */
public class Affiliations 
        extends DefForm
        implements JabberBlockListener
{

    private String id="admin";
    private String namespace="http://jabber.org/protocol/muc#admin";
    private String room;

    private JabberStream stream=StaticData.getInstance().theStream;
    
    private MenuCommand cmdModify = new MenuCommand (SR.MS_MODIFY, MenuCommand.SCREEN, 1, RosterIcons.ICON_RENAME);
    private MenuCommand cmdNew    = new MenuCommand (SR.MS_NEW_JID, MenuCommand.SCREEN, 2, RosterIcons.ICON_REGISTER_INDEX );
//#ifdef CLIPBOARD
//#     private MenuCommand cmdCopy   = new MenuCommand(SR.MS_COPY, MenuCommand.SCREEN, 3, RosterIcons.ICON_COPY);
//#     private ClipBoard clipboard; 
//#endif
    
    /** Creates a new instance of AffiliationList
     * @param room
     * @param affiliationIndex
     */
    public Affiliations(String room, short affiliationIndex) {
        super (AffiliationItem.getAffiliationName(affiliationIndex));
        this.room=room;
        
	//fix for old muc
	switch (affiliationIndex) {
	    case AffiliationItem.AFFILIATION_OWNER:
	    case AffiliationItem.AFFILIATION_ADMIN:
		if (!Config.getInstance().muc119) namespace="http://jabber.org/protocol/muc#owner";
	}
	
        this.id=AffiliationItem.getAffiliationName(affiliationIndex);
        
        mainbar = new MainBar(2, null, " ", false);
        mainbar.addElement(id);
        
        getList();
    }
    
    public final void getList() {
        JabberDataBlock item=new JabberDataBlock("item", null, null);
        item.setAttribute("affiliation", id);
        listRq(false, item, id);
    }
    
    public void menuAction(MenuCommand c, VirtualList d){
        if (c==cmdNew) new AffiliationModify(room, null, "none", "");
        if (c==cmdModify) eventOk();
//#ifdef CLIPBOARD
//#         if (c==cmdCopy) {
//#             try {
//#                 AffiliationItem item=(AffiliationItem)getFocusedObject();
//#                 if (item.jid!=null)
//#                     clipboard.setClipBoard(item.jid);
//#             } catch (Exception e) {/*no messages*/}
//#         }
//#endif     
        
    }
    
    public void destroyView(){
	super.destroyView();
        stream.cancelBlockListener(this);
    }
    
    public void eventOk(){
        try {
            AffiliationItem item=(AffiliationItem)getFocusedObject();
            new AffiliationModify(room, item.jid, 
					AffiliationItem.getAffiliationName( (short)item.affiliation), 
                                        (item.reason==null)? "":item.reason
                    );
        } catch (Exception e) { }
    }
    
    private void processIcon(boolean processing){
        String count=(itemsList==null)? null: String.valueOf(itemsList.size());
        mainbar.setElementAt((processing)?
            (Object)new Integer(RosterIcons.ICON_PROGRESS_INDEX): 
            (Object)count, 0);
        redraw();
    }
    
    public int blockArrived(JabberDataBlock data) {
        try {
            if (data.getAttribute("id").equals(id)) {
                JabberDataBlock query=data.findNamespace("query", namespace);
                itemsList.removeAllElements();
                try {
                    for (Enumeration e=query.getChildBlocks().elements(); e.hasMoreElements(); ){
                        itemsList.addElement(new AffiliationItem((JabberDataBlock)e.nextElement()));
                    }
                } catch (Exception e) { /* no any items */}
                sort(itemsList);

                redraw();
                
                processIcon(false);
                return JabberBlockListener.NO_MORE_BLOCKS;
            }
        } catch (Exception e) { }
        return JabberBlockListener.BLOCK_REJECTED;
    }
    
    public void listRq(boolean set, JabberDataBlock child, String id) {
        
        JabberDataBlock request=new Iq(room, (set)? Iq.TYPE_SET: Iq.TYPE_GET, id);
        JabberDataBlock query=request.addChildNs("query", namespace);
        query.addChild(child);
        
        processIcon(true);
        stream.addBlockListener(this);
        stream.send(request);
    }
    public void commandState(){
        menuCommands.removeAllElements();
        addMenuCommand(cmdModify);
        addMenuCommand(cmdNew);
//#ifdef CLIPBOARD
//#         if (Config.getInstance().useClipBoard) {
//#             clipboard=ClipBoard.getInstance();
//#             addMenuCommand(cmdCopy);
//#         }
//#endif
    }    
}
