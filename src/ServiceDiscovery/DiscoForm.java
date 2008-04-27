/*
 * RegForm.java
 *
 * Created on 5.06.2005, 20:04
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
import com.alsutton.jabber.*;
import com.alsutton.jabber.datablocks.*;
import locale.SR;
//import Client.*;


/**
 *
 * @author Evg_S
 */
public class DiscoForm implements CommandListener{
    
    private Display display;
    private Displayable parentView;
    
    private Vector fields;
    private String xmlns;
    private String service;
    
    private String node;
    private String sessionId;
    
    private String childName;
    
    //private Form form;
    
    private boolean xData;
    
    private Command cmdOk=new Command(SR.MS_SEND, Command.OK /*Command.SCREEN*/, 1);
    private Command cmdCancel=new Command(SR.MS_BACK, Command.BACK, 99);
    
    private String id;
    
    //Roster roster=StaticData.getInstance().roster;
    JabberStream stream;
    
    //private JabberBlockListener listener;
    
    /** Creates a new instance of RegForm */
    public DiscoForm(Display display, JabberDataBlock regform, JabberStream stream, String resultId, String childName) {
        service=regform.getAttribute("from");
        this.childName=childName;
        JabberDataBlock query=regform.getChildBlock(childName);
        xmlns=query.getAttribute("xmlns");
        node=query.getAttribute("node");
        sessionId=query.getAttribute("sessionid");
        JabberDataBlock x=query.getChildBlock("x");
        this.id=resultId;
        //this.listener=listener;
        // todo: обработать ошибку query
        fields=new Vector();
        Form form=new Form(service);

        // for instructions
        
        Vector vFields=(xData=(x!=null))? x.getChildBlocks() : query.getChildBlocks();

	Enumeration e;        
        
        if (vFields!=null) {
            for (e=vFields.elements(); e.hasMoreElements(); ){
                FormField field=new FormField((JabberDataBlock)e.nextElement());
                if (field.instructions) {
                    fields.insertElementAt(field, 0);
                } else { fields.addElement(field); }
            }

            if (x!=null) {
                JabberDataBlock registered=query.getChildBlock("registered");
                if (registered!=null) {
                    FormField unreg=new FormField(registered);
                    fields.addElement(unreg);
                }
            }
            
            for (e=fields.elements(); e.hasMoreElements(); ){
                FormField field=(FormField) e.nextElement();
                if (!field.hidden) form.append(field.formItem);
            }
        }
        
       
        form.setCommandListener(this);
        
        if (childName.equals("command")) {
            if (query.getAttribute("status").equals("completed")) {
                form.append("Complete.");
            } else form.addCommand(cmdOk);
        } else form.addCommand(cmdOk);
        form.addCommand(cmdCancel);
        
        this.display=display;
        this.parentView=display.getCurrent();
        this.stream=stream;
        display.setCurrent(form);
    }
    
    private void sendForm(String id){
        JabberDataBlock req=new Iq(service, Iq.TYPE_SET, id);
        JabberDataBlock qry=req.addChildNs(childName, xmlns);
        //qry.setAttribute("action", "complete");
        qry.setAttribute("node", node);
        qry.setAttribute("sessionid", sessionId);
        
        JabberDataBlock cform=qry;
        if (xData) {
            JabberDataBlock x=qry.addChildNs("x", "jabber:x:data");
            x.setAttribute("type", "submit");
            cform=x;
        }
        
        for (Enumeration e=fields.elements(); e.hasMoreElements(); ) {
            FormField f=(FormField) e.nextElement();
            if (f==null) continue;
            JabberDataBlock ch=f.constructJabberDataBlock();
            if (ch!=null) {
                if (ch.getTagName().equals("remove")) {
                    cform=qry;
                    Vector cb=cform.getChildBlocks();
                    if (cb!=null) cb.removeAllElements();
                    cform.addChild(ch);
                    break;
                }
                cform.addChild(ch);
            }
        }
        
        //System.out.println(req.toString());
        //if (listener!=null) stream.addBlockListener(listener);
        stream.send(req);
    }

    
    public void commandAction(Command c, Displayable d){
        if (c==cmdCancel) destroyView();
        if (c==cmdOk) { 
            sendForm(id);
            destroyView();
        }
    }

    public void destroyView(){
        display.setCurrent(parentView);
    }
}
