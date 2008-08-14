/*
 * TransferDispatcher.java
 *
 * Created on 28.10.2006, 19:44
 *
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
 */

package io.file.transfer;

import Client.StaticData;
import com.alsutton.jabber.JabberBlockListener;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.datablocks.Iq;
import com.alsutton.jabber.datablocks.Message;
import java.util.Enumeration;
import java.util.Vector;
import util.Strconv;
import xmpp.XmppError;

/**
 *
 * @author Evg_S
 */
public class TransferDispatcher implements JabberBlockListener{

    /** Singleton */
    private static TransferDispatcher instance;
    
    public static TransferDispatcher getInstance() {
        if (instance==null) instance=new TransferDispatcher();
        return instance;
    }
   
    StaticData sd = StaticData.getInstance();
    
    private Vector taskList;
    public Vector getTaskList() { return taskList;  }
    
    /** Creates a new instance of TransferDispatcher */
    private TransferDispatcher() {
        taskList=new Vector();
    }

    public int blockArrived(JabberDataBlock data) {
        if (data instanceof Iq) {
            String id=data.getAttribute("id");
            
            JabberDataBlock si=data.getChildBlock("si");
            if (si!=null) {
                // stream initiating
                String sid=si.getAttribute("id");
                
                JabberDataBlock file=si.getChildBlock("file");
                JabberDataBlock feature=si.getChildBlock("feature");
                
                String type=data.getTypeAttribute();
                if (type.equals("set")) {
                    // sender initiates file sending process
                    TransferTask task=new TransferTask(
                            data.getAttribute("from"),
                            id,   sid,
                            file.getAttribute("name"),
                            file.getChildBlockText("desc"),
                            Integer.parseInt(file.getAttribute("size")),
                            null);
                    
                    synchronized (taskList){ taskList.addElement(task); }
                    
                    eventNotify();
                    sd.roster.addMessageStore(data.getAttribute("from"), file.getAttribute("name")+"\n"+Integer.parseInt(file.getAttribute("size"))+" bytes");
                    sd.roster.playNotify(1000);
                    return BLOCK_PROCESSED;
                }
                if (type.equals("result")) {
                    // our file were accepted
                    TransferTask task=getTransferBySid(id);
                    task.initIBB();
                    
                    eventNotify();
                    return BLOCK_PROCESSED;
                }
            }
            JabberDataBlock open=data.getChildBlock("open");
            if (open!=null) if (open.isJabberNameSpace("http://jabber.org/protocol/ibb")) {
                String sid=open.getAttribute("sid");
                TransferTask task=getTransferBySid(sid);
				
                //verifying block-size
                if (!checkIbbSize(task, id, open.getAttribute("block-size"))) return BLOCK_PROCESSED;
                
                JabberDataBlock accept=new Iq(task.jid, Iq.TYPE_RESULT, id);
                send(accept, true);
                eventNotify();
                return BLOCK_PROCESSED;
            }
            JabberDataBlock close=data.getChildBlock("close");
            if (close!=null) {
                String sid=close.getAttribute("sid");
                TransferTask task=getTransferBySid(sid);
                
                JabberDataBlock done=new Iq(task.jid, Iq.TYPE_RESULT, id);
                send(done, true);
                task.closeFile();
                eventNotify();
                return BLOCK_PROCESSED;
            }
            if (data.getTypeAttribute().equals("result")) {
                TransferTask task=getTransferBySid(id);
                if (task!=null) {
                    task.startTransfer();
                }
            }
            if (data.getTypeAttribute().equals("error")) {
                TransferTask task=getTransferBySid(id);
                if (task!=null) {
                    task.cancel();
                }
            }
        }
        if (data instanceof Message) {
            JabberDataBlock bdata=data.getChildBlock("data");
            if (bdata==null) return BLOCK_REJECTED;
            if (!bdata.isJabberNameSpace("http://jabber.org/protocol/ibb")) return BLOCK_REJECTED;
            String sid=bdata.getAttribute("sid");
            TransferTask task=getTransferBySid(sid);
            
            byte b[]=Strconv.fromBase64(bdata.getText());
//#ifdef DEBUG
//#             System.out.println("data chunk received");
//#endif
            repaintNotify();
            task.writeFile(b);
            
        }
        return BLOCK_REJECTED;
    }


    boolean  checkIbbSize(TransferTask task, String id, String size) {
        try {
            //if (Integer.parseInt(size)<com.alsutton.xmlparser.XMLParser.MAX_BLOCK_SIZE) 
                return true;
        } catch (Exception ex) {}
        
        JabberDataBlock reject=new Iq(task.jid, Iq.TYPE_ERROR, id);
        reject.addChild(new XmppError(XmppError.NOT_ACCEPTABLE, "block-size too long"));
        
        send(reject, true);
        
        //task.state=ERROR;
        task.errMsg="Rejected";
        task.showEvent=true;
        eventNotify();
        return false;
    }
	
    // send shortcut
    void send(JabberDataBlock data, boolean async) {
        //StaticData.getInstance().roster.theStream.send(data);
        try {
            StringBuffer sb=new StringBuffer();
            data.constructXML(sb);
            sd.roster.theStream.sendBuf( sb );
            sb=null;
        } catch (Exception e) {
            //e.printStackTrace();
        }
        
    }

    private TransferTask getTransferBySid(String sid) {
        synchronized (taskList) {
            for (Enumeration e=taskList.elements(); e.hasMoreElements(); ){
                TransferTask task=(TransferTask)e.nextElement();
                if (task.sid.equals(sid)) return task;
            }
        }
        return null;
    }

    void eventNotify() {
        int event=-1;
        synchronized (taskList) {
            for (Enumeration e=taskList.elements(); e.hasMoreElements(); ) {
                TransferTask t=(TransferTask) e.nextElement();
                if (t.showEvent) event=t.getImageIndex();
            }
        }
        Integer icon=(event<0)? null:new Integer(event);
        sd.roster.setEventIcon(icon);
    }

    void repaintNotify() {
        sd.roster.redraw();
    }

    void sendFile(TransferTask task) {
        synchronized (taskList){ taskList.addElement(task); }
        task.sendInit();
    }
}
