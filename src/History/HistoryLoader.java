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
//#ifdef PLUGINS
//#     public static String plugin = new String("PLUGIN_HISTORY");
//#endif

    private String fileName="";
    public Vector listMessages;
    private Vector indexes;
    private int nextIndex = 0;

    public final static int MESSAGE_MARKER_OUT=1;
    public final static int MESSAGE_MARKER_PRESENCE=2;
    public final static int MESSAGE_MARKER_IN=3;
    public final static int MESSAGE_MARKER_OTHER=0;

    public HistoryLoader(String file){
       cf=Config.getInstance();
//#ifdef DETRANSLIT
//#         file=(cf.transliterateFilenames)?DeTranslit.getInstance().translit(file):file;
//#endif
//#ifdef HISTORY
//#        fileName=cf.msgPath+StringUtils.replaceBadChars(file)+".txt";
//#endif
       listMessages = new Vector();
       indexes = new Vector();
    }

    private int processMessage(int pos) {
//#ifdef DEBUG
//#         System.out.println("Called. pos=" + pos);
//#endif
        FileIO f = FileIO.createConnection(fileName);
        Vector lm = new Vector();
        byte[] b = readFile(pos);

        if (b == null) {
//#ifdef DEBUG
//#             System.out.println("Вероятно, EOF.");
//#endif
            return pos;
        }

        String str = getStrFromBytes(b);

        String current = findBlock(str, "m");
        while (current != null) {
            String type = findBlock(current, "t");
            String date = findBlock(current, "d");
            String from = findBlock(current, "f");
            String subj = findBlock(current, "s");
            String body = findBlock(current, "b");
            lm.addElement(processMessage(type, date, from, subj, body));

            str = str.substring(str.indexOf("</m>") + 4);
            current = findBlock(str, "m");
        }

        if ((lm.size() < 1) && (str.indexOf("<m>") > -1)) {
            String largeMessage = "";
            do {
                largeMessage += str;
                pos += 4096;
                b = readFile(pos);
                if (b == null) {
//#ifdef DEBUG
//#                     System.out.println("WARNING! Неожиданный конец лог-файла.");
//#endif
                    return pos; // Или что тут вообще делать?
                    } else {
                    str = getStrFromBytes(b);
                }
            } while (str.indexOf("</m>") < 0);

            largeMessage = findBlock(largeMessage+str, "m");
            String type = findBlock(largeMessage, "t");
            String date = findBlock(largeMessage, "d");
            String from = findBlock(largeMessage, "f");
            String subj = findBlock(largeMessage, "s");
            String body = findBlock(largeMessage, "b");
            lm.addElement(processMessage(type, date, from, subj, body));
        }

        if (lm.size() > 0) {
            pos += getNextMessagePos(b);
            this.listMessages = lm;

        }
        return pos;
    }

    private byte[] readFile(int pos) {
        FileIO f=FileIO.createConnection(fileName);
        byte[] b = new byte[4096];
        try {
            InputStream is=f.openInputStream();
            is.skip(pos);
            is.read(b);
            is.close();

        } catch (IOException e) {
            try { f.close();
                  return null;
                } catch (IOException ex2) {/*No messages*/}
        } catch (Exception e) { return null; }

        try {
            f.close();
        } catch (Exception e) {/*No messages*/}
        return b;
    }

    private String getStrFromBytes(byte[] b) {
        String str = new String(b).trim();
        if (cf.cp1251) {
            str = Strconv.convCp1251ToUnicode(str);
        }
        return str;
    }
    
    private int getNextMessagePos(byte[] b) {
        int ost = 4096;
        while (!(b[ost - 4] == '<' && b[ost - 3] == '/' && b[ost - 2] == 'm' && b[ost - 1] == '>')) {
            ost--;

        }
        return ost;
    }
    
    public void getNext() {
        int oldIndex = nextIndex;
        nextIndex = processMessage(oldIndex);
        if (oldIndex != nextIndex)
            indexes.addElement(new Integer(oldIndex));
    }

    public void getPrev() {
        int size = indexes.size();
        if (size<2)
            return;
        indexes.removeElementAt(size-1);
        nextIndex=processMessage(((Integer)indexes.lastElement()).intValue());
    }

    private Msg processMessage(String marker, String date, String from, String subj, String body) {
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

    private String findBlock(String source, String needle) {
        int start = source.indexOf("<"+needle+">");
        int end = source.indexOf("</"+needle+">");
        if (start<0 || end<0)
            return null;
        return source.substring(start+needle.length()+2, end);
    }
}
