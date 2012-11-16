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

//#ifdef HISTORY
//# 
//# package History;
//# 
//# import Client.Config;
//# import io.file.FileIO;
//# import java.util.Vector;
//# import util.StringUtils;
//# 
//# public class HistoryStorage {
//#     
//#     private String history;
//#     
//#     private Config cf;
//# 
//#     public final static int MESSAGE_MARKER_OUT=1;
//#     public final static int MESSAGE_MARKER_PRESENCE=2;
//#     public final static int MESSAGE_MARKER_IN=3;
//#     public final static int MESSAGE_MARKER_OTHER=0;    
//#     
//#     public HistoryStorage(String filename) {
//#         cf=Config.getInstance();
//# // TODO: check this fork (for set filename).
//#ifdef DETRANSLIT
//#        filename = util.DeTranslit.getInstance().get_actual_filename(filename);
//#endif
//#        filename=cf.msgPath+StringUtils.replaceBadChars(filename)+".txt";
//# 
//#        this.history = loadHistory(filename);
//#    }
//#     
//#    private String loadHistory(String fileName) {        
//#         return readFile(fileName);
//#    }
//# 
//#     public Vector importData() {
//#         Vector vector=new Vector();
//#         if (history!=null) {
//#             try {
//#                 int pos=0; int start_pos=0; int end_pos=0;
//#                 String type=null; String date=null; String from=null; String subj=null; String body=null; String tempstr=null;
//#                 
//#                 while (true) {
//#                     type=null; date=null; from=null; subj=null; body=null; tempstr=null;
//#                     start_pos=history.indexOf("<m>",pos);  end_pos=history.indexOf("</m>",start_pos);
//# 
//#                     if (start_pos>-1) {
//#                         tempstr=history.substring(start_pos+3, end_pos);
//#                         type=findBlock(tempstr,"t"); date=findBlock(tempstr,"d");  from=findBlock(tempstr,"f");  subj=findBlock(tempstr,"s");  body=findBlock(tempstr,"b");                   
//#                         
//#                         if (Integer.parseInt(type)!=MESSAGE_MARKER_PRESENCE) {
//#                             //System.out.println(type+" ["+date+"]"+from+": "+subj+" "+body+"\r\n");
//#                             vector.insertElementAt(HistoryLoader.processMessage(type, date, from, subj, body), 0);
//#                         }
//#                     } else
//#                         break;
//#                     pos=end_pos+4;
//#                 }                
//#                 if(vector.size()>5)
//#                    vector.setSize(5);
//#             } catch (Exception e) { }
//#         }
//#         
//#         history = null;
//#         return vector;
//#     }
//#     
//#     private String findBlock(String source, String needle){
//#         String block = "";
//#         int start =source.indexOf("<"+needle+">");
//#         int end = source.indexOf("</"+needle+">");
//#         if (start<0 || end<0)
//#             return block;
//#         return source.substring(start+3, end);
//#     }
//#         
//#     private String readFile(String arhPath){
//#         FileIO f = FileIO.createConnection(arhPath);
//#         return f.fileReadUtf();
//#     }    
//# }
//# 
//#endif
