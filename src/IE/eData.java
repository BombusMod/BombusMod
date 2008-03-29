/*
 * exportData.java
 *
 * Created on 24 январь 2008 г., 20:15
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package IE;

import io.file.FileIO;
import java.util.Enumeration;
import java.util.Vector;

/**
 *
 * @author ad
 */
public class eData {
    public eData(Vector array, String fileName) {
        StringBuffer body = new StringBuffer();
        body = createArrayString(array);
        saveData(body, fileName);
    }
    
    public StringBuffer createArrayString(Vector array) {
        StringBuffer body = new StringBuffer();
        
        for (Enumeration e=array.elements(); e.hasMoreElements();) {
                keyValue i=(keyValue)e.nextElement();
                body.append("<i><k>");
                body.append(i.getKey());
                body.append("</k><v>");
                body.append(i.getValue());
                body.append("</v></i>\r\n");  
        }
        return body;
    }
    
    public void saveData(StringBuffer body, String fileName) {
       FileIO file=FileIO.createConnection(fileName);
       file.fileWrite(body.toString().getBytes());
    }
    
}
