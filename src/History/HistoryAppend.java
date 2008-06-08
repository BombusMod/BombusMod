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

package History;

import Client.Config;
import Client.Msg;
import Client.StaticData;
import io.file.FileIO;
import java.io.IOException;
import java.io.OutputStream;
import util.StringUtils;
//#ifdef DETRANSLIT
//# import util.DeTranslit;
//#endif
import util.Strconv;

public class HistoryAppend {
    
    private Config cf;
    private boolean convertToWin1251;
    
    private final static String MS="<m>";
    private final static String ME="</m>";
    
    private final static String TS="<t>";
    private final static String TE="</t>";
    
    private final static String FS="<f>";
    private final static String FE="</f>";
    
    private final static String DS="<d>";
    private final static String DE="</d>";
    
    private final static String SS="<s>";
    private final static String SE="</s>";
    
    private final static String BS="<b>";
    private final static String BE="</b>";
    
    private final static String RN="\r\n";
    
//#if FILE_IO
    private int filePos;
    private FileIO file;
    private OutputStream os;
//#endif
    
    public HistoryAppend(Msg m, boolean formatted, String filename) {
       cf=Config.getInstance();
       convertToWin1251=cf.cp1251;
       byte[] bodyMessage=createBody(m, formatted).getBytes();

//#ifdef DETRANSLIT
//#        filename=(cf.transliterateFilenames)?DeTranslit.getInstance().translit(filename):filename;
//#endif
       
       filename = cf.msgPath+StringUtils.replaceBadChars(filename)+".txt";
       file=FileIO.createConnection(filename);
        try {
            os = file.openOutputStream(0);
            try {
                os.write(bodyMessage);
                filePos+=bodyMessage.length;
            } catch (IOException ex) { }
            os.close();
            os.flush();
            file.close();
        } catch (IOException ex) {
            try {
                file.close();
            } catch (IOException ex2) { }
        }
        filename=null;
        bodyMessage=null;
    }
    
    
    private String createBody(Msg m, boolean formatted) {
        String fromName=StaticData.getInstance().account.getUserName();
        if (m.messageType!=Msg.MESSAGE_TYPE_OUT)
            fromName=m.from;

        StringBuffer body=new StringBuffer();
        
        int marker=Msg.MESSAGE_MARKER_OTHER;
        switch (m.messageType){
            case Msg.MESSAGE_TYPE_IN:
                marker=Msg.MESSAGE_MARKER_IN;
                break;
            case Msg.MESSAGE_TYPE_PRESENCE:
                marker=Msg.MESSAGE_MARKER_PRESENCE;
                break;
           case Msg.MESSAGE_MARKER_OUT:
                marker=Msg.MESSAGE_MARKER_OUT;
        }
        if (!formatted) {
            body.append("[")
                .append(m.getDayTime())
                .append("] ")
                .append(fromName)
                .append(":")
                .append(RN);
            if (m.subject!=null) {
                body.append(m.subject)
                    .append(RN);
            }
            body.append(m.getBody())
                .append(RN)
                .append(RN);
        } else {
            body.append(MS)
                .append(TS)
                .append(marker)
                .append(TE)
                .append(DS)
                .append(m.getDayTime())
                .append(DE)
                .append(FS)
                .append(fromName)
                .append(FE);
            if (m.subject!=null) {
                body.append(SS)
                    .append(m.subject)
                    .append(SE);
            }
            body.append(BS)
                .append(m.getBody())
                .append(BE)
                .append(ME)
                .append(RN);
        }
        return (convertToWin1251)?Strconv.convUnicodeToCp1251(body.toString()):body.toString();
    }
}
