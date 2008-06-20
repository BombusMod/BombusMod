/*
 * HistoryLoader.java
 *
 * Created on 18.06.2008, 13:58
 *
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
//#ifdef DETRANSLIT
//# import util.DeTranslit;
//#endif
import util.Strconv;
import util.StringUtils;

/**
 *
 * @author ad
 */
public class HistoryLoader {
    Config cf;

    private String fileName="";

    private Vector fileMap;

    public HistoryLoader(String file){
       cf=Config.getInstance();
//#ifdef DETRANSLIT
//#         file=(cf.transliterateFilenames)?DeTranslit.getInstance().translit(file):file;
//#endif
       fileName=cf.msgPath+StringUtils.replaceBadChars(file)+".txt";
       
       fileMap = getFileMap();
    }
    
    public Msg getMessage(int index) {
        FileIO f=FileIO.createConnection(fileName);
        try {
            InputStream is=f.openInputStream(); 
            String str="";
            
            int blockSize=0;
            
            int pos=((posItem)fileMap.elementAt(index)).getPos();
            if (index<getSize()-2) {
                blockSize=((posItem)fileMap.elementAt(index+1)).getPos()-pos;
            } else {
                blockSize=(int)f.fileSize()-pos;
            }
            is.skip(pos);
            
            byte[] b = new byte[blockSize];
            is.read(b);
            
            is.close();  f.close();
                
            if (b!=null) {
                str = new String(b, 0, blockSize).trim();
                b=null; is.close(); f.close();
                String type=null; String date=null; String from=null; String subj=null; String body=null;
                int start_pos=str.indexOf("<m>",0);
                int end_pos=str.indexOf("</m>",start_pos);

                if (start_pos>-1) {
                    str=str.substring(start_pos+3, end_pos);
                    if (cf.cp1251) {
                        str=Strconv.convCp1251ToUnicode(str);
                    }
                    type=findBlock(str,"t"); date=findBlock(str,"d");  from=findBlock(str,"f");  subj=findBlock(str,"s");  body=findBlock(str,"b");
                    
                    //System.out.println(type+" ["+date+"]"+from+": "+subj+" "+body+"\r\n");
                    return processMessage (type, date, from, subj, body);
                }
            }                
        } catch (IOException e) { try { f.close(); } catch (IOException ex2) { } }
        return null;
    }

    private Msg processMessage (String marker, String date, String from, String subj, String body) {
        int msgType=Msg.MESSAGE_TYPE_HISTORY;

        int mrk = Integer.parseInt(marker);

        switch (mrk) {
            case Msg.MESSAGE_MARKER_IN:
                msgType=Msg.MESSAGE_TYPE_IN;
                break;
            case Msg.MESSAGE_MARKER_OUT:
                msgType=Msg.MESSAGE_TYPE_OUT;
                break;
            case Msg.MESSAGE_MARKER_PRESENCE:
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
    
    public int getSize() {
        return fileMap.size();
    }

    public Vector getFileMap() {
        Vector vector=new Vector();

        FileIO f=FileIO.createConnection(fileName);
        try {
            InputStream is=f.openInputStream(); 
            int fileSize = (int)f.fileSize();

            int pos=0;
            int pos2=0;
            int blockSize=4096;

            byte[] b; 
            String str;
            boolean process=true;
            boolean process2=true;

            while (process) {
                if (pos>0) is.skip(blockSize);
                b = new byte[blockSize];
                is.read(b);
                
                if (b!=null) {
                    str = new String(b, 0, blockSize).trim();

                    pos2=0;
                    process2=true;
                    
                    while (process2) {
                        int mpos=str.indexOf("<m", pos2);

                        if (mpos>-1) {
                            vector.addElement(new posItem(pos+mpos));
                            pos2=mpos+1;
                        } else process2=false;
                    }
                    str=null;
                }
                pos+=blockSize;
                if (pos>fileSize) {
                    process=false;
                }
            }
            b=null;
            is.close(); f.close();
        } catch (IOException e) { try { f.close(); } catch (IOException ex2) { } }

        System.out.println("found messages: "+vector.size());
        return vector;
    }
    
    class posItem {
        int pos=0;
        
        public posItem(int pos) { this.pos=pos; }
        public int getPos() { return pos; }
    }
}
