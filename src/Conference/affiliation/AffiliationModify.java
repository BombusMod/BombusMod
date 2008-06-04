/*
 * AffiliationModify.java
 *
 * Created on 30.10.2005, 15:32
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

import Client.StaticData;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.JabberStream;
import com.alsutton.jabber.datablocks.Iq;
import javax.microedition.lcdui.*;
import locale.SR;
import ui.controls.AlertBox;
import ui.controls.form.BoldString;
import ui.controls.form.DefForm;
import ui.controls.form.DropChoiceBox;
import ui.controls.form.TextInput;

/**
 *
 * @author EvgS
 */
public class AffiliationModify
        extends DefForm {
    
    private Display display;
    private Displayable parentView;

    private TextInput jidItem;
    private DropChoiceBox affiliationItem;
    private TextInput reasonItem;
    
    private String room;
    private int recentAffiliation;
    
    /** Creates a new instance of AffiliationModify */
    public AffiliationModify(Display display, String room, String jid, String affiliation, String reason) {
        super(display, SR.MS_AFFILIATION);
        
        this.display=display;
        parentView=display.getCurrent();

        this.room=room;

        jidItem=new TextInput(display, SR.MS_JID, jid, null, TextField.ANY);
        itemsList.addElement(jidItem);

        itemsList.addElement(new BoldString(SR.MS_SET_AFFILIATION));
        affiliationItem=new DropChoiceBox(display);
        for (short index=0; index<=AffiliationItem.AFFILIATION_OUTCAST; index++) {
            String name=AffiliationItem.getAffiliationName(index);
            affiliationItem.append(name);
            if (affiliation.equals(name)) recentAffiliation=index;
        }
        affiliationItem.setSelectedIndex(recentAffiliation);
        itemsList.addElement(affiliationItem);

	reasonItem=new TextInput(display, SR.MS_REASON, reason, "reason", TextField.ANY);
	itemsList.addElement(reasonItem);

        attachDisplay(display);
    }
    
    
    private void modify(){
        JabberStream stream=StaticData.getInstance().roster.theStream;
        
        JabberDataBlock request=new Iq(room, Iq.TYPE_SET, "admin_modify");
        JabberDataBlock query=request.addChildNs("query", "http://jabber.org/protocol/muc#admin");
        JabberDataBlock child=query.addChild("item", null);
        child.setAttribute("jid", jidItem.getValue());
        child.setAttribute("affiliation", AffiliationItem.getAffiliationName((short)affiliationItem.getSelectedIndex()));
		
        String rs=reasonItem.getValue();
        if (rs!="") child.addChild("reason", rs);        

        stream.send(request);
        try {
            Thread.sleep(300);
        } catch (Exception ex) {}
        
        try {
            Affiliations a=(Affiliations) parentView;
            a.getList();
        } catch (Exception e) {}
        destroyView();
    }
    
    public void cmdOk() {
        if (jidItem.getValue()=="") return;
        if (recentAffiliation==AffiliationItem.AFFILIATION_OWNER) {
            StringBuffer warn=new StringBuffer(SR.MS_ARE_YOU_SURE_WANT_TO_DISCARD /*"Are You sure want to discard "*/);
            warn.append(jidItem.getValue());
            warn.append(SR.MS_FROM_OWNER_TO/*" from OWNER to "*/);
            warn.append(AffiliationItem.getAffiliationName((short)affiliationItem.getSelectedIndex()));
            new AlertBox(SR.MS_MODIFY_AFFILIATION, warn.toString(), display, null) {
                    public void yes() {
                        modify();
                        destroyView();
                    }
                    public void no() {}
            };
            warn=null;
        } else modify();
    }

	
    public void ActionConfirmed() {
        modify();
	destroyView();
    }

}
