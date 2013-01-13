/*
 * HistoryAppend.java
 *
 * Created on 19.06.2007, 9:24
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
//# package History;
//# 
//# import Client.Config;
//# import Client.Msg;
//# import Client.StaticData;
//# import io.file.FileIO;
//# import java.io.IOException;
//# import java.io.OutputStream;
//# import util.Strconv;
//# import util.StringUtils;
//# 
//# public class HistoryAppend {
//# 
//#     /** Singleton */
//#     private static HistoryAppend instance;
//# 
//#     public static HistoryAppend getInstance() {
//#         if (instance == null) {
//#             instance = new HistoryAppend();
//#         }
//#         return instance;
//#     }
//#     StaticData sd = StaticData.getInstance();
//# 
//#     /** Creates a new instance of PepListener */
//#     private HistoryAppend() {
//#     }
//#     private Config cf = Config.getInstance();
//#     private final static String MS = "<m>";
//#     private final static String ME = "</m>";
//#     private final static String TS = "<t>";
//#     private final static String TE = "</t>";
//#     private final static String FS = "<f>";
//#     private final static String FE = "</f>";
//#     private final static String DS = "<d>";
//#     private final static String DE = "</d>";
//#     private final static String SS = "<s>";
//#     private final static String SE = "</s>";
//#     private final static String BS = "<b>";
//#     private final static String BE = "</b>";
//#     private final static String RN = "\r\n";
//#     public final static int MESSAGE_MARKER_OUT = 1;
//#     public final static int MESSAGE_MARKER_PRESENCE = 2;
//#     public final static int MESSAGE_MARKER_IN = 3;
//#     public final static int MESSAGE_MARKER_OTHER = 0;
//#if FILE_IO
//#     //private int filePos;
//#     private FileIO file;
//#     private OutputStream os;
//#endif
//# 
//#     public void addMessage(Msg m, String filename) {
//#         byte[] bodyMessage = getBodyBytes(m);
//#ifdef DETRANSLIT
//#         filename = util.DeTranslit.getInstance().get_actual_filename(filename);
//#endif
//#         filename = cf.msgPath + StringUtils.replaceBadChars(filename) + ".txt";
//# 
//#         file = FileIO.createConnection(filename);        
//#         file.writeFile(bodyMessage);
//#         try {
//#             file.close();
//#         } catch (IOException ex) {
//#             ex.printStackTrace();
//#         }
//#     }
//# 
//#     public void addMessageList(String messages, String filename) {
//#ifdef DETRANSLIT
//#         filename = util.DeTranslit.getInstance().get_actual_filename(filename);
//#endif
//#         filename = cf.msgPath + StringUtils.replaceBadChars(filename) + ".txt";
//# 
//#         file = FileIO.createConnection(filename);
//#         file.fileWriteUtf(messages);
//#         try {
//#             file.close();
//#         } catch (IOException ex) {
//#             ex.printStackTrace();
//#         }
//#     }
//# 
//#     private byte[] getBodyBytes(Msg m) {
//#         String fromName = StaticData.getInstance().account.JID.getNode();
//#         if (m.messageType != Msg.MESSAGE_TYPE_OUT) {
//#             fromName = m.from;
//#         }
//# 
//#         StringBuffer body = new StringBuffer();
//# 
//#         int marker = MESSAGE_MARKER_OTHER;
//#         switch (m.messageType) {
//#             case Msg.MESSAGE_TYPE_IN:
//#                 marker = MESSAGE_MARKER_IN;
//#                 break;
//#             case Msg.MESSAGE_TYPE_PRESENCE:
//#                 marker = MESSAGE_MARKER_PRESENCE;
//#                 break;
//#             case Msg.MESSAGE_TYPE_OUT:
//#                 marker = MESSAGE_MARKER_OUT;
//#         }
//# 
//#         body.append(MS).append(TS).append(marker).append(TE).append(DS)
//#                 .append(m.getDayTime()).append(DE).append(FS)
//#                 .append(StringUtils.escapeTags(fromName)).append(FE);
//#         if (m.subject != null) {
//#             body.append(SS).append(StringUtils.escapeTags(m.subject)).append(SE);
//#         }
//#         body.append(BS).append(StringUtils.escapeTags(m.body)).append(BE).append(ME).append(RN);
//# 
//#         return Strconv.toUTFArray(body.toString());
//#     }
//# }
//# 
//#endif
