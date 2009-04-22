/*
 * ArchiveTemplates.java
 *
 * Created on 2.06.2008, 20:21
 *
 * Copyright (c) 2006-2008, Daniel Apatin (ad), http://apatin.net.ru
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

package IE;

import Client.Config;
import Client.Msg;
import Archive.MessageArchive;
import io.file.FileIO;
import java.util.Enumeration;
import java.util.Vector;
import ui.Time;
import util.Strconv;

/**
 *
 * @author ad
 */
public class ArchiveTemplates {
//#ifdef PLUGINS
//#     public static String plugin = new String("PLUGIN_IE");
//#endif
 
    String filePath;

    //private int returnVal=0;

    private final static String start_item="<START_ITEM>";
    private final static String end_item="<END_ITEM>";

    private final static String start_date="<START_DATE>";
    private final static String end_date="<END_DATE>";

    private final static String start_from="<START_FROM>";
    private final static String end_from="<END_FROM>";

    private final static String start_subj="<START_SUBJ>";
    private final static String end_subj="<END_SUBJ>";

    private final static String start_body="<START_BODY>";
    private final static String end_body="<END_BODY>";
    
    private int where;
    
    private Config cf;
    
    MessageArchive archive;
    
    /**
     * Creates a new instance of ArchiveTemplates
     */
    public ArchiveTemplates(int direction, int where, String pathSelected) {
        this.where=where;
        
        cf=Config.getInstance();
        
        archive=new MessageArchive(where);
        
        if (direction==1) {
                exportData(pathSelected);
        } else {
                importArchive(pathSelected);
        }
    }

    public Vector importData(String arhPath) {
        Vector vector=new Vector();
        byte[] bodyMessage;
        String archive="";
        
        FileIO f=FileIO.createConnection(arhPath);
        bodyMessage = f.fileRead();

        if (bodyMessage!=null) {
            if (cf.cp1251) {
                archive=Strconv.convCp1251ToUnicode(new String(bodyMessage, 0, bodyMessage.length));
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
                        vector.insertElementAt(msg, 0);
                    } else
                        break;

                    pos=end_pos+end_item.length();
                }
            } catch (Exception e)	{ 
               //System.out.println(e.toString());
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
            body.append(start_item)
            .append("\r\n")
            .append(start_date)
            .append(m.getDayTime())
            .append(end_date)
            .append("\r\n")
            .append(start_from)
            .append(m.from)
            .append(end_from)
            .append("\r\n")
            .append(start_subj);
            if (m.subject!=null) {
                body.append(m.subject);
            }
            body.append(end_subj)
            .append("\r\n")
            .append(start_body)
            .append(m.body)
            .append(end_body)
            .append("\r\n")
            .append(end_item)
            .append("\r\n\r\n");
        }

        if (cf.cp1251) {
            bodyMessage=Strconv.convUnicodeToCp1251(body.toString()).getBytes();
        } else {
            bodyMessage=body.toString().getBytes();
        }

        FileIO file=FileIO.createConnection(arhPath+((where==1)?"archive_":"template_")+Time.localDate()+".txt");
        file.fileWrite(bodyMessage);

        body=null;
        arhPath=null;
        
        archive.close();
    }
    
    public int getItemCount() {
	return archive.size();
    }
    
    public Msg getMessage(int index) {
	return archive.msg(index);
    }
    
    private void importArchive(String arhPath) {
        Vector history=importData(arhPath);
        
        for (Enumeration messages=history.elements(); messages.hasMoreElements(); )  {
            MessageArchive.store((Msg) messages.nextElement(), where);
        }
        archive.close();
    }
}
