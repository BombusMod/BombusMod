/*
 * ContactEdit.java
 *
 * Created on 7.05.2005, 2:15
 * Copyright (c) 2005-2007, Eugene Stahov (evgs), http://bombus-im.org
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
//#ifndef WMUC
import Conference.MucContact;
//#endif
import javax.microedition.lcdui.*;
import java.util.*;
import locale.SR;
import ui.controls.TextFieldEx;

/**
 *
 * @author Evg_S
 */
public final class ContactEdit
        implements CommandListener, ItemStateListener 
//#if (!MIDP1)
        , ItemCommandListener
//#endif
{
    private Display display;
    public Displayable parentView;
    
    Form f;
    TextField tJid;
    TextField tNick;
    TextField tGroup;
    ChoiceGroup tGrpList;
    ChoiceGroup tTranspList;
    ChoiceGroup tAskSubscrCheckBox;
    
    int ngroups;
    
    int grpFIndex;
    
    Command cmdOk=new Command(SR.MS_ADD, Command.OK, 1);
    Command cmdSet=new Command(SR.MS_SET, Command.ITEM, 2);
    Command cmdCancel=new Command(SR.MS_CANCEL,Command.BACK,99);
    
    boolean newContact=true;
    Config cf=Config.getInstance();

    StaticData sd=StaticData.getInstance();
    //StoreContact sC;
    
    public ContactEdit(Display display, Contact c) {
        this.display=display;
        parentView=display.getCurrent();
        
        Vector groups=sd.roster.groups.getRosterGroupNames();
        
        f=new Form(SR.MS_ADD_CONTACT);
        
        tJid=new TextFieldEx(SR.MS_USER_JID, null, 150, TextField.ANY); 
        
        tNick=new TextFieldEx(SR.MS_NAME, null, 32, TextField.ANY); 
        tGroup=new TextFieldEx(SR.MS_GROUP ,null, 32, TextField.ANY);
        
        
        tGrpList=new ChoiceGroup(SR.MS_GROUP , ChoiceGroup.POPUP);
        tTranspList=new ChoiceGroup(SR.MS_TRANSPORT, ChoiceGroup.POPUP);
        
        tAskSubscrCheckBox=new ChoiceGroup(SR.MS_SUBSCRIPTION, ChoiceGroup.MULTIPLE);
        tAskSubscrCheckBox.append(SR.MS_ASK_SUBSCRIPTION, null);
        
        
//#if (!MIDP1)
        //NOKIA FIX
        tGrpList.addCommand(cmdSet);
        tGrpList.setItemCommandListener(this);
        
        tTranspList.addCommand(cmdSet);
        tTranspList.setItemCommandListener(this);
//#endif
        
        // Transport droplist
        tTranspList.append(sd.account.getServer(), null);
        for (Enumeration e=sd.roster.getHContacts().elements(); e.hasMoreElements(); ){
            Contact ct=(Contact)e.nextElement();
            Jid transpJid=ct.jid;
            if (transpJid.isTransport()) 
                tTranspList.append(transpJid.getBareJid(),null);
        }
        tTranspList.append(SR.MS_OTHER,null);
        
        try {
            String jid;
//#ifndef WMUC
            if (c instanceof MucContact) {
                jid=Jid.toBareJid( ((MucContact)c).realJid );
            } else {
//#endif
                jid=c.getBareJid();
//#ifndef WMUC
            }
//#endif
            // edit contact
            tJid.setString(jid);
            tNick.setString(c.nick);
//#ifndef WMUC
            if (c instanceof MucContact) {
                c=null;
                throw new Exception();
            } 
//#endif
            if (c.getGroupType()!=Groups.TYPE_NOT_IN_LIST  && c.getGroupType()!=Groups.TYPE_SEARCH_RESULT) {
                // edit contact
                f.setTitle(jid);
                cmdOk=new Command(SR.MS_UPDATE, Command.OK, 1);
                newContact=false;
            } else c=null; // adding not-in-list
        } catch (Exception e) {c=null;} // if MucContact does not contains realJid
        
        
        int sel=-1;
        ngroups=0;
        String grpName="";
        if (c!=null) grpName=c.getGroup().name;
        
        if (groups!=null) {
            ngroups=groups.size();
            for (int i=0;i<ngroups; i++) {
                String gn=(String)groups.elementAt(i);
                tGrpList.append(gn, null);
                
                if (gn.equals(grpName)) sel=i;
            }
        }
            
        //if (sel==-1) sel=groups.size()-1;
        if (sel<0) sel=0;
        //tGroup.setString(group(sel));
        
        
        if (c==null){
            f.append(tJid);
            f.append(tTranspList);
        }
        updateChoise(tJid.getString(),tTranspList);
        f.append(tNick);
        
        
        //f.append(tGroup);
        
        tGrpList.append(SR.MS_NEWGROUP,null);
        tGrpList.setSelectedIndex(sel, true);
        
        grpFIndex=f.append(tGrpList);
        
        if (newContact) {
            f.append(tAskSubscrCheckBox);
            tAskSubscrCheckBox.setSelectedIndex(0, true);
        }
        
        f.addCommand(cmdOk);
        f.addCommand(cmdCancel);
        
        f.setCommandListener(this);
        f.setItemStateListener(this);
        
        display.setCurrent(f);
    }
    
    //public interface StoreContact {
    //    public void storeContact(String jid, String name, String group, boolean newContact);
    //}

    public void commandAction(Command c, Displayable d) {
        if (c==cmdOk) {
            String jid=getString(tJid);
            if (jid!=null) {
                String name=getString(tNick);
                String group=group(tGrpList.getSelectedIndex());
                if (group==null) group=getString(tGroup);
                
                try {
                    int gSel=tGrpList.getSelectedIndex();
                    if (gSel!=tGrpList.size()-1)  {
                        group=(gSel>0)? tGrpList.getString(gSel) : null; // nokia fix
                    }
                } catch (Exception e) {} // nokia fix
                
                // СЃРѕС…СЂР°РЅРµРЅРёРµ РєРѕРЅС‚Р°РєС‚Р°
                boolean ask[]=new boolean[1];
                tAskSubscrCheckBox.getSelectedFlags(ask);
                sd.roster.storeContact(jid,name,group, ask[0]);
                destroyView();
                return;
            }
        }
        
        if (c==cmdCancel) destroyView();
    }
    
//#if (!MIDP1)
    public void commandAction(Command command, Item item) {
        itemStateChanged(item);
    }
//#endif

    private String getString(TextField t){
        if (t.size()==0) return null;
        String s=t.getString().trim();
        if (s.length()==0) return null;
        return s;
    }
    
    private String group(int index) {
        if (index==0) return null;
        if (index==tGrpList.size()-1) return null;
        return tGrpList.getString(index);
    }
    
    private void updateChoise(String str, ChoiceGroup grp) {
        int sz=grp.size();
        int set=sz-1;
        for (int i=0; i<sz; i++) {
            if (str.equals(grp.getString(i))) {
                set=i;
                break;
            }
        }
        if (grp.getSelectedIndex()!=set) 
            grp.setSelectedIndex(set, true);
    }
    
    public void itemStateChanged(Item item){
        if (item==tGrpList) {
            int index=tGrpList.getSelectedIndex();
            if (index==tGrpList.size()-1) {
                f.set(grpFIndex, tGroup);
            }
            //tGroup.setString(group(index));
        }
        
        //if (item==tGroup) {
        //    updateChoise(tGroup.getString(), tGrpList);
        //}
        
        if (item==tTranspList) {
            int index=tTranspList.getSelectedIndex();
            if (index==tTranspList.size()-1) return;
            
            String transport=tTranspList.getString(index);
            
            String jid=tJid.getString();
            StringBuffer jidBuf=new StringBuffer(jid);
            
            int at=jid.indexOf('@');
            if (at<0) at=tJid.size();
            
            jidBuf.setLength(at);
            jidBuf.append('@');
            jidBuf.append(transport);
            tJid.setString(jidBuf.toString());
            jidBuf=null;
        }
        if (item==tJid) {
            String s1=tJid.getString();
            int at=tJid.getString().indexOf('@');
            try {
                updateChoise(s1.substring(at+1), tTranspList);
            } catch (Exception e) {}
        }
    }
    
    public void destroyView(){
        if (display!=null)   display.setCurrent(parentView/*roster*/);
    }

}
