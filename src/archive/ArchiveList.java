/*
 * ArchiveList.java
 *
 * Created on 11.12.2005, 5:24
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

package archive;

import Client.Msg;
import Client.StaticData;
import java.util.Enumeration;
import ui.MainBar;
import Messages.MessageList;
import java.util.Vector;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import locale.SR;
//#if FILE_IO
import io.file.FileIO;
import io.file.browse.Browser;
import io.file.browse.BrowserListener;
import util.strconv;
//#endif
import ui.controls.AlertBox;
import Client.Config;
import ui.Time;

/**
 *
 * @author EvgS
 */
public class ArchiveList 
    extends MessageList
//#if (FILE_IO)
    implements BrowserListener
//#endif
{

    Command cmdPaste=new Command(SR.MS_PASTE_BODY, Command.SCREEN, 1);
    Command cmdJid=new Command(SR.MS_PASTE_JID /*"Paste Jid"*/, Command.SCREEN, 2);
    Command cmdSubj=new Command(SR.MS_PASTE_SUBJECT, Command.SCREEN, 3);
    Command cmdEdit=new Command(SR.MS_EDIT, Command.SCREEN, 4);
    Command cmdNew=new Command(SR.MS_NEW, Command.SCREEN, 5);
//#if (FILE_IO)
    Command cmdExport=new Command(SR.MS_EXPORT_TO_FILE, Command.SCREEN, 7);
    Command cmdImport=new Command(SR.MS_IMPORT_TO_FILE, Command.SCREEN, 8);
//#endif
    Command cmdDelete=new Command(SR.MS_DELETE, Command.SCREEN, 9);
    Command cmdDeleteAll=new Command(SR.MS_CLEAR_LIST, Command.SCREEN, 10);

    MessageArchive archive;
   
    private int caretPos;

    private StaticData sd=StaticData.getInstance();
//#if FILE_IO    
    String filePath;
    
    private int EXPORT=0;
    private int IMPORT=1;

    private int returnVal=0;

    private String start_item="<START_ITEM>";
    private String end_item="<END_ITEM>";

    private String start_date="<START_DATE>";
    private String end_date="<END_DATE>";

    private String start_from="<START_FROM>";
    private String end_from="<END_FROM>";

    private String start_subj="<START_SUBJ>";
    private String end_subj="<END_SUBJ>";

    private String start_body="<START_BODY>";
    private String end_body="<END_BODY>";
//#endif
    private Config cf;
    private int where=1;
    /** Creates a new instance of ArchiveList */
    public ArchiveList(Display display, int caretPos, int where) {
 	super ();
        this.where=where;
        this.caretPos=caretPos;
        cf=Config.getInstance();
        archive=new MessageArchive(where);
        //enableListWrapping(true); //TEST:������� ����� ����� ������
	setCommandListener(this);
	addCommand(cmdBack);
	addCommand(cmdDelete);
//#if (FILE_IO)	
        addCommand(cmdExport);
        addCommand(cmdImport);
//#endif
	addCommand(cmdEdit);
        addCommand(cmdNew);
        addCommand(cmdDeleteAll);
	if (sd.roster.me!=null) {
	    addCommand(cmdPaste);
	    addCommand(cmdJid);
	}
        
        attachDisplay(display);

        try {
            focusedItem(0);
        } catch (Exception e) {}
	
	MainBar mainbar=new MainBar((where==1)?SR.MS_ARCHIVE:SR.MS_TEMPLATE /*"Archive"*/);
	mainbar.addElement(null);
	mainbar.addRAlign();
	mainbar.addElement(null);
	mainbar.addElement(SR.MS_FREE /*"free "*/);
        setMainBarItem(mainbar);
    }

    protected void beginPaint() {
        getMainBarItem().setElementAt(" ("+getItemCount()+")",1);
	getMainBarItem().setElementAt(String.valueOf(getFreeSpace()),3);
    }
    
    public int getItemCount() {
	return archive.size();
    }
    
    public Msg getMessage(int index) {
	return archive.msg(index);
    }

    public void commandAction(Command c, Displayable d) {
        super.commandAction(c,d);
        
	Msg m=getMessage(cursor);
//#if FILE_IO
        if (c==cmdImport) { 
            returnVal=IMPORT;
            new Browser(null, display, this, false);
        }
//#endif
        if (c==cmdNew) { new NewTemplate(display, where); }
	if (m==null) return;
        
	if (c==cmdDelete) { keyClear(); }
        if (c==cmdDeleteAll) { deleteAllMessages(); redraw(); }
	if (c==cmdPaste) { pasteData(0); }
	if (c==cmdSubj) { pasteData(1); }
	if (c==cmdJid) { pasteData(2); }
//#if FILE_IO
        if (c==cmdExport) { 
            returnVal=EXPORT;
            new Browser(null, display, this, true);
        }
//#endif
        if (c==cmdEdit) {
            try {
                new archiveEdit(display, getMessage(cursor), where);
                deleteMessage();
            } catch (Exception e) {/*no messages*/}
        }
    }
	

    private void deleteMessage() {
        archive.delete(cursor);
        messages=new Vector();
    }
    
    private void deleteAllMessages() {
        new AlertBox(SR.MS_DELETE, SR.MS_SURE_DELETE, display, this) {
            public void yes() {
                archive.deleteAll();
                messages=new Vector();
            }
            public void no() { }
        };
    }
    
    private void pasteData(int field) {
	if (sd.roster.me==null) return;
	Msg m=getMessage(cursor);
	if (m==null) return;
	String data;
	switch (field) {
	case 1: 
	    data=m.subject;
	    break;
	case 2: 
	    data=m.from;
	    break;
	default:
	    data=m.quoteString();
	}
	sd.roster.me.insertText(data, caretPos);
	destroyView();
    }
    
    public void keyGreen() { pasteData(0); }
    
    public void keyClear() { 
        if (getItemCount()>0) {
            new AlertBox(SR.MS_DELETE, SR.MS_SURE_DELETE, display, this) {
                public void yes() {
                    deleteMessage();
                }
                public void no() { }
            };
            redraw();
        }
    }
    
    public void focusedItem(int index) {
	if (sd.roster.me==null) return;
	try {
	    if (getMessage(index).subject!=null) {
		addCommand(cmdSubj);
		return;
	    }
	} catch (Exception e) { }
	removeCommand(cmdSubj);
    }

//#if FILE_IO 
    public Vector importData(String arhPath) {
        Vector vector=new Vector();
        byte[] bodyMessage;
        String archive="";
        
        FileIO f=FileIO.createConnection(arhPath);
        bodyMessage = f.fileRead();

        if (bodyMessage!=null) {
            if (cf.cp1251) {
                archive=strconv.convCp1251ToUnicode(new String(bodyMessage, 0, bodyMessage.length));
            } else {
                archive=new String(bodyMessage, 0, bodyMessage.length);
            }
        }
        if (archive!=null) {
            try {
                int pos=0;
                int start_pos=0;
                int end_pos=0;

                while (true) {
                    String date=null; String from=null; String subj=null; String body=null; String tempstr=null;
                    start_pos=archive.indexOf(start_item,pos); end_pos=archive.indexOf(end_item,pos);

                    if (start_pos>-1 && end_pos>-1) {
                        tempstr=archive.substring(start_pos+start_item.length(), end_pos);
                        date=findBlock(tempstr, start_date, end_date); 
                        from=findBlock(tempstr, start_from, end_from); 
                        subj=findBlock(tempstr, start_subj, end_subj);
                        body=findBlock(tempstr, start_body, end_body);
                        //System.out.println("["+date+"]"+from+": "+subj+" "+body+"\r\n");
                        Msg msg = new Msg(Msg.MESSAGE_TYPE_IN,from,subj,body);
                        msg.setDayTime(date);
                        vector.insertElementAt(msg,0);
                    } else
                        break;

                    pos=end_pos+end_item.length();
                }
            } catch (Exception e)	{ 
               System.out.println(e.toString());
            }
        }

        bodyMessage=null;
        arhPath=null;
            
        return vector;
    }
    
    private String findBlock(String source, String _start, String _end){
        String block = "";
        int start =source.indexOf(_start); int end = source.indexOf(_end);
        if (start<0 || end<0)
            return block;
        
        return source.substring(start+_start.length(), end);
    }

    public void exportData(String arhPath) {
        byte[] bodyMessage;
        int items=getItemCount();
        StringBuffer body=new StringBuffer();

        for(int i=0; i<items; i++){
            Msg m=getMessage(i);
            body.append(start_item+"\r\n");
            body.append(start_date);
            body.append(m.getDayTime());
            body.append(end_date+"\r\n");
            body.append(start_from);
            body.append(m.from);
            body.append(end_from+"\r\n");
            body.append(start_subj);
            if (m.subject!=null) {
                body.append(m.subject);
            }
            body.append(end_subj+"\r\n");
            body.append(start_body);
            body.append(m.getBody());
            body.append(end_body+"\r\n");
            body.append(end_item+"\r\n\r\n");
        }

        if (cf.cp1251) {
            bodyMessage=strconv.convUnicodeToCp1251(body.toString()).getBytes();
        } else {
            bodyMessage=body.toString().getBytes();
        }

        FileIO file=FileIO.createConnection(arhPath+((where==1)?"archive_":"template_")+getDate()+".txt");
        file.fileWrite(bodyMessage);

        body=null;
        arhPath=null;
	destroyView();
    }
    
    public void BrowserFilePathNotify(String pathSelected) {
        switch (returnVal) {
            case 0:
                exportData(pathSelected);
                break;
            case 1:
                importArchive(pathSelected);
                break;
        }
        
    }
    
    private void importArchive(String arhPath) {
        Vector history=importData(arhPath);
        
        for (Enumeration messages=history.elements(); messages.hasMoreElements(); )  {
            MessageArchive.store((Msg) messages.nextElement(), where);
        }

        destroyView();
    }
//#endif
    private String getDate() {
        long dateGmt=Time.utcTimeMillis();
        return Time.dayLocalString(dateGmt).trim(); 
    }
  
    public void destroyView(){
	super.destroyView();
	archive.close();
    }

    private int getFreeSpace() {
        return archive.freeSpace();
    }

}
