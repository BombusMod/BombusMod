/*
 * HistoryStorage.java
 *
 * Created on 13.11.2006, 14:49
 * Copyright (c) 2006-2008, Daniel Apatin (ad), http://apatin.net.ru
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

package History;

import Client.Config;
import Client.Msg;
import io.file.FileIO;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;
import util.StringUtils;
//#ifdef DETRANSLIT
//# import util.DeTranslit;
//#endif
import util.Strconv;

public class HistoryStorage {
//#ifdef PLUGINS
//#     public static String plugin = new String("PLUGIN_HISTORY");
//#endif
    
    private String history;
    
    private Config cf;

    public final static int MESSAGE_MARKER_OUT=1;
    public final static int MESSAGE_MARKER_PRESENCE=2;
    public final static int MESSAGE_MARKER_IN=3;
    public final static int MESSAGE_MARKER_OTHER=0;    
    
    public HistoryStorage(String filename) {
        cf=Config.getInstance();
//#ifdef DETRANSLIT
//#        filename=(cf.transliterateFilenames)?DeTranslit.getInstance().translit(filename):filename;
//#endif
       filename=cf.msgPath+StringUtils.replaceBadChars(filename)+".txt";
       this.history = loadHistory(filename);
   }
    
   private String loadHistory(String fileName) {
        byte[] bodyMessage;
        String archive="";
        bodyMessage=readFile(fileName);

        if (bodyMessage!=null) {
            if (cf.cp1251) {
                archive=Strconv.convCp1251ToUnicode(new String(bodyMessage, 0, bodyMessage.length));
            } else {
                archive=new String(bodyMessage, 0, bodyMessage.length);
            }
        }
        return archive;
   }

    public Vector importData() {
        Vector vector=new Vector();
        if (history!=null) {
            try {
                int pos=0; int start_pos=0; int end_pos=0;
                String type=null; String date=null; String from=null; String subj=null; String body=null; String tempstr=null;
                
                while (true) {
                    type=null; date=null; from=null; subj=null; body=null; tempstr=null;
                    start_pos=history.indexOf("<m>",pos);  end_pos=history.indexOf("</m>",start_pos);

                    if (start_pos>-1) {
                        tempstr=history.substring(start_pos+3, end_pos);
                        type=findBlock(tempstr,"t"); date=findBlock(tempstr,"d");  from=findBlock(tempstr,"f");  subj=findBlock(tempstr,"s");  body=findBlock(tempstr,"b");                   
                        
                        if (Integer.parseInt(type)!=MESSAGE_MARKER_PRESENCE) {
                            //System.out.println(type+" ["+date+"]"+from+": "+subj+" "+body+"\r\n");
                            vector.insertElementAt(processMessage (type, date, from, subj, body),0);
                        }
                    } else
                        break;
                    pos=end_pos+4;
                }                
                vector.setSize(5);
            } catch (Exception e) { }
        }
        
        history = null;
        return vector;
    }
    
    private Msg processMessage (String marker, String date, String from, String subj, String body) {
        int msgType=Msg.MESSAGE_TYPE_HISTORY;
        
        int mrk = Integer.parseInt(marker);
        
        switch (mrk) {
            case MESSAGE_MARKER_IN:
                msgType=Msg.MESSAGE_TYPE_IN;
                break;
            case MESSAGE_MARKER_OUT:
                msgType=Msg.MESSAGE_TYPE_OUT;
                break;
            case MESSAGE_MARKER_PRESENCE:
                msgType=Msg.MESSAGE_TYPE_PRESENCE;
                break;
        }
        
        Msg msg=new Msg(msgType,from,subj,body);
        msg.setDayTime(date);
        
        return msg;
    }
    
    private String findBlock(String source, String needle){
        String block = "";
        int start =source.indexOf("<"+needle+">"); int end = source.indexOf("</"+needle+">");
        if (start<0 || end<0)
            return block;
        
        return source.substring(start+3, end);
    }
        
    private byte[] readFile(String arhPath){
        int maxSize=2048; byte[] b = new byte[maxSize]; 
        FileIO f=FileIO.createConnection(arhPath);
        try {
            InputStream is=f.openInputStream(); 
            int fileSize = (int)f.fileSize();
            if (fileSize>maxSize){
                is.skip(fileSize-maxSize);
            }
            is.read(b); is.close(); f.close();
        } catch (IOException e) { try { f.close(); } catch (IOException ex2) { } }

        if (b!=null)
            return b;
        
        return null;
    }    
}
