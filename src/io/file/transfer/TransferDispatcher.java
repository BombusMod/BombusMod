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
import java.util.Enumeration;
import java.util.Vector;
import com.alsutton.jabber.datablocks.Message;
import util.Strconv;
import xmpp.extensions.XDataField;

/**
 *
 * @author Evg_S
 */
public class TransferDispatcher implements JabberBlockListener {
//#ifdef PLUGINS
//#     public static String plugin = new String("PLUGIN_FILE_TRANSFER");
//#endif

    public final static String NS_BYTESTREAMS = "http://jabber.org/protocol/bytestreams";
    public final static String NS_IBB = "http://jabber.org/protocol/ibb";
    
    TransferConfig ft = TransferConfig.getInstance();
    
    /** Singleton */
    private static TransferDispatcher instance;
    private TransferTask proxyTask = null;
    
    public static TransferDispatcher getInstance() {
        if (instance==null) instance=new TransferDispatcher();        
        return instance;
    }
   
    StaticData sd = StaticData.getInstance();
    
    private Vector taskList;
    public Vector getTaskList() { return taskList;  }

    public int getTasksCount() { return taskList.size(); }
    
    /** Creates a new instance of TransferDispatcher */
    private TransferDispatcher() {
        taskList=new Vector();
    }
    
    public void addBlockListener() {
        sd.roster.theStream.addBlockListener(instance);
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
                JabberDataBlock form = feature.getChildBlock("x");
                XDataField field = new XDataField(form.getChildBlock("field"));        
               
                String type=data.getTypeAttribute();
                if (type.equals("set")) {
                    
                    // sender initiates file sending process
                    TransferTask task=new TransferTask(
                            data.getAttribute("from"),
                            id,   sid,
                            file.getAttribute("name"),
                            file.getChildBlockText("desc"),
                            Integer.parseInt(file.getAttribute("size")),
                            field.optionsList);
                    
                    synchronized (taskList){ taskList.addElement(task); }
                    
                    eventNotify();
                    sd.roster.addFileQuery(data.getAttribute("from"), file.getAttribute("name")+"\n"+Integer.parseInt(file.getAttribute("size"))+" bytes");
                    sd.roster.playNotify(1000);
                    return BLOCK_PROCESSED;
                }
                if (type.equals("result")) {
                    // our file were accepted                    
                    TransferTask task=getTransferBySid(id);
                    if (field.body.equals(NS_IBB)) {
                     task.initIBB();
                    } else {
                        task.initBytestreams();
                    }
                    eventNotify();                    
                    return BLOCK_PROCESSED;
                }
            }
// ibb-only
            JabberDataBlock open=data.getChildBlock("open");
            if (open!=null) if (open.isJabberNameSpace(TransferDispatcher.NS_IBB)) {
                String sid=open.getAttribute("sid");
                TransferTask task=getTransferBySid(sid);

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
// //
            
            if (data.getTypeAttribute().equals("result")) {
                TransferTask task = getTransferBySid(id);
                if (task!=null) {
                     switch (task.state) {
                        case (TransferTask.PROXYACTIVATE):
                            boolean success = task.openStreams(ft.ftProxy, ft.ftProxyPort);
                            if (!success) {
                                task.cancel();
                                break;
                            }
                            try {
                            success = task.connectStream();
                            } catch (Exception e) { e.printStackTrace();}
                            if (!success) {
                                task.cancel();
                                break;
                            }
                            task.ProxyActivate();
                            break;
                        case (TransferTask.PROXYOPEN):
                            task.startTransfer();
                            break;
                        default: // ibb
                            task.startTransfer();
                    }
                    return BLOCK_PROCESSED;
                }           
              
            }



            if (data.getTypeAttribute().equals("error")) {
                TransferTask task=getTransferBySid(id);
                if (task!=null) {
                    task.cancel();
                    return BLOCK_PROCESSED;
                }
            }            
        }
// ibb        
        if (data instanceof Message) {
            JabberDataBlock bdata=data.getChildBlock("data");
            if (bdata==null) return BLOCK_REJECTED;
            if (!bdata.isJabberNameSpace(TransferDispatcher.NS_IBB)) return BLOCK_REJECTED;
            String sid=bdata.getAttribute("sid");
            TransferTask task=getTransferBySid(sid);

            byte b[]=Strconv.fromBase64(bdata.getText());
//#ifdef DEBUG
//#             System.out.println("data chunk received");
//#endif
            repaintNotify();
            task.writeFile(b);
            return BLOCK_PROCESSED;

        }
// //        
        return BLOCK_REJECTED;
    }
	
    // send shortcut
    void send(JabberDataBlock data, boolean async) {
        sd.roster.theStream.send(data);
    }

    private TransferTask getTransferBySid(String id) {
        synchronized (taskList) {
            for (Enumeration e=taskList.elements(); e.hasMoreElements(); ){
                TransferTask task=(TransferTask)e.nextElement();
                 if (id.endsWith(task.sid)) return task;
            }
        }
        return null;
    }
    
    public TransferTask getTransferByJid(String jid) {
        synchronized (taskList) {
            for (Enumeration e=taskList.elements(); e.hasMoreElements(); ){
                TransferTask task=(TransferTask)e.nextElement();
                 if (jid.equals(task.jid)) return task;
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
        sd.canvas.repaint();
    }

    void sendFile(TransferTask task) {
        synchronized (taskList){ taskList.addElement(task); }
        task.sendInit();
    }    
}
