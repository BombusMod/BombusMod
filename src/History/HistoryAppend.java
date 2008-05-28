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
import util.Translit;
import util.strconv;

public class HistoryAppend {
    
    private Config cf;
    private boolean convertToWin1251;
    
//#if FILE_IO
    private int filePos;
    private FileIO file;
    private OutputStream os;
//#endif
    
    public HistoryAppend(Msg m, boolean formatted, String filename) {
       cf=Config.getInstance();
       convertToWin1251=cf.cp1251;
       byte[] bodyMessage=createBody(m, formatted).getBytes();

//#ifdef TRANSLIT
       filename=(cf.transliterateFilenames)?Translit.translit(filename):filename;
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
            body.append("[");
            body.append(m.getDayTime());
            body.append("] ");
            body.append(fromName);
            body.append(":\r\n");
            if (m.subject!=null) {
                body.append(m.subject);
                body.append("\r\n");
            }
            body.append(m.getBody());
            body.append("\r\n\r\n");
        } else {
            body.append("<m><t>");
            body.append(marker);
            body.append("</t><d>");
            body.append(m.getDayTime());
            body.append("</d><f>");
            body.append(fromName);
            body.append("</f>");
            if (m.subject!=null) {
                body.append("<s>");
                body.append(m.subject);
                body.append("</s>");
            }
            body.append("<b>");
            body.append(m.getBody());
            body.append("</b></m>\r\n");
        }
        return (convertToWin1251)?strconv.convUnicodeToCp1251(body.toString()):body.toString();
    }
}
