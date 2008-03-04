/*
 * HistoryAppend.java
 *
 * Created on 19.06.2007, 9:24
 * Copyright (c) 2006-2007, Daniel Apatin (ad), http://apatin.net.ru
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
import io.file.FileIO;
import java.io.IOException;
import java.io.OutputStream;
import util.Translit;
import util.strconv;

public class HistoryAppend {
    
    private Config cf=Config.getInstance();
    
    
//#if FILE_IO    
    private int filePos;
    private FileIO file;
    private OutputStream os;
//#endif
    
    public HistoryAppend(StringBuffer body, String filename) {        
       byte[] bodyMessage=(cf.cp1251)?strconv.convUnicodeToCp1251(body.toString()).getBytes():body.toString().getBytes();

//#ifdef TRANSLIT
//#        filename=(cf.transliterateFilenames)?Translit.translit(filename):filename;
//#endif
       
       filename = cf.msgPath+strconv.replaceBadChars(filename)+".txt";
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
        body=null;
        bodyMessage=null;
    }
}
