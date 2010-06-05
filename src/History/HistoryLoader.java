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
    private long beginIndex;
    private long endIndex;
    public final long fileSize;

    public final int MAX_READ_BLOCK_SIZE = 4096;
    public final int MAX_LIST_MESSAGES_SIZE = 4096;

    public final static int MESSAGE_MARKER_OUT=1;
    public final static int MESSAGE_MARKER_PRESENCE=2;
    public final static int MESSAGE_MARKER_IN=3;
    public final static int MESSAGE_MARKER_OTHER=0;

    public HistoryLoader(String file) {
       cf=Config.getInstance();
//#ifdef DETRANSLIT
//#         file=(cf.transliterateFilenames)?DeTranslit.translit(file):file;
//#endif
//#ifdef HISTORY
//#        fileName=cf.msgPath+StringUtils.replaceBadChars(file)+".txt";
//#endif
       listMessages = new Vector();
       
       fileSize = getFileSize();
       beginIndex = fileSize-1;
       endIndex = fileSize-1;
       stepBack();
    }

    private long getFileSize() {
        long size = -1;
        FileIO file = FileIO.createConnection(fileName);
        try {
            InputStream is = file.openInputStream();

            try {
                size = file.fileSize();
            } catch (Exception e) { }

            is.close();
            file.close();
            } catch (IOException e1) {
                try {
                    file.close();
                } catch (IOException e2) { }
            }
//#ifdef DEBUG
//#             System.out.println("Size of \""+fileName+"\" is "+size);
//#endif
        return size;
    }

    private byte[] getByteBlock(long pos) {
        FileIO f=FileIO.createConnection(fileName);
        byte[] b = new byte[MAX_READ_BLOCK_SIZE];
        try {
            InputStream is=f.openInputStream();
            is.skip(pos);
            is.read(b);
//#ifdef DEBUG
//#         System.out.println("Readed byte block, begin: "+pos);
//#endif
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

    private String getStrFromBytes(byte[] b, int off, int length) {
        String str = new String(b, off, length).trim();
        if (cf.cp1251) {
            return Strconv.convCp1251ToUnicode(str);
        }
        return str;
    }

    private String getStrFromBytes(byte[] b) {
        return getStrFromBytes(b, 0, b.length);
    }

    private long getCorrectIndex(long pos) {
        if (pos < 0) {
            return 0;
        } else if (pos > (fileSize - MAX_READ_BLOCK_SIZE - 1)) {
            return (fileSize - MAX_READ_BLOCK_SIZE - 1);
        }
        return pos;
    }

    // Получить первое сообщение, начинающееся с позиции >=pos
    private Msg getNextMessage(long pos) {
        String str = "";
        do {
            byte[] b = getByteBlock(pos);
            str += getStrFromBytes(b);
            String current = findBlock(str, "m");
            
            if (current != null) {
                endIndex = pos + findCloseStr(b, 0, true, 'm') + 1;
                return processMessage(
                        findBlock(current, "t"),
                        findBlock(current, "d"),
                        findBlock(current, "f"),
                        findBlock(current, "s"),
                        findBlock(current, "b"));
            }
            pos = getCorrectIndex(pos + MAX_READ_BLOCK_SIZE);
        } while (true);
    }

    // Получить первое с конца сообщение, начинающееся с позиции <=pos
    private Msg getPreviousMessage(long pos) {
        String str = "";
        do {
            pos = getCorrectIndex(pos - MAX_READ_BLOCK_SIZE);
            byte[] b = getByteBlock(pos);
            str = getStrFromBytes(b) + str;
            String current = findLastBlock(str, 'm');
            
            if (current != null) {
                beginIndex = pos + findOpenStr(b, b.length-3, false, 'm') - 1;
                return processMessage(
                        findBlock(current, "t"),
                        findBlock(current, "d"),
                        findBlock(current, "f"),
                        findBlock(current, "s"),
                        findBlock(current, "b"));
            }
        } while (true);
    }

    // Ищет строку <m> в b и возвращает индекс '<' в b
    private int findOpenStr(byte[] b, int i, boolean forward, char ch) {
        while ((i >= 0) && (i < (b.length-2))) {
            if ((b[i] == '<') && (b[i + 1] == ch) && (b[i + 2] == '>'))
                return i;
            i += forward?1:-1;
        }
        return -1;
    }

    // Ищет строку </m> в b и возвращает индекс '<' в b
    private int findCloseStr(byte[] b, int i, boolean forward, char ch) {
        while ((i >= 0) && (i < (b.length-3))) {
            if ((b[i] == '<') && (b[i + 1] == '/') && (b[i + 2] == ch) && (b[i + 3] == '>'))
                return i;
            i += forward?1:-1;
        }
        return -1;
    }

    public void stepNext() {
        listMessages = null;
        listMessages = new Vector();

        int size = 0;
        beginIndex = endIndex;
        do {
            Msg msg = getNextMessage(endIndex);
            size += msg.toString().length();
            listMessages.addElement(msg);
            //endIndex = getNextIndex(endIndex);
        } while (size < MAX_LIST_MESSAGES_SIZE);
    }

    public void stepBack() {
        listMessages = null;
        listMessages = new Vector();

        int size = 0;
        endIndex = beginIndex;
        do {
            Msg msg = getPreviousMessage(beginIndex);
            size += msg.toString().length();
            listMessages.insertElementAt(msg, 0);
        } while (size < MAX_LIST_MESSAGES_SIZE);
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

//#ifdef DEBUG
//#         System.out.println("Processed message: "+msg);
//#endif
        return msg;
    }

    private String findBlock(String source, String needle) {
        int start = source.indexOf("<"+needle+">");
        int end = source.indexOf("</"+needle+">",start);
        if (start<0 || end<0)
            return null;
        return source.substring(start+needle.length()+2, end);
    }
    
    private String findLastBlock(String source, char needle) {
        byte[] chars = source.getBytes();
        int end = findCloseStr(chars, chars.length-4, false, needle);
        int start = findOpenStr(chars, end, false, needle);
        if (start<0 || end<0)
            return null;
        return new String(chars, start+3, end-start-3);
    }
}
