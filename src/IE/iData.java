/*
 * importSettings.java
 *
 * Created on 24 январь 2008 г., 19:39
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package IE;

import io.file.FileIO;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

/**
 *
 * @author ad
 */
public class iData {
    final static int SEARCH_KEY     = 1;
    final static int SEARCH_VALUE   = 2;
    final static int SEARCH_BREAK   = 3;   

    private int afterEol;
    private String file;
    private int pos=0;
    
    public  iData(String filename) {
        this.file = filename;
    }

    public Vector iData() {
       String filecontents=loadFile();
       Vector vector=new Vector();
       if (filecontents!=null) {
            try {
                int pos=0; int start_pos=0; int end_pos=0;

                while (true) {
                    String key=null; String value=null; String tempstr=null;
                    start_pos=filecontents.indexOf("<i>",pos); end_pos=filecontents.indexOf("</i>",pos);

                    if (start_pos>-1 && end_pos>-1) {
                        tempstr=filecontents.substring(start_pos+3, end_pos);
                        key=findBlock(tempstr, "k"); 
                        value=findBlock(tempstr, "v"); 
                        vector.addElement(new keyValue(key, value));
                    } else
                        break;

                    pos=end_pos+4;
                }
            } catch (Exception e){ }
        }
        
        filecontents = null;
        return vector;
    }
    
    private String findBlock(String source, String needle){
        int start =source.indexOf("<"+needle+">"); 
        int end = source.indexOf("</"+needle+">");
        if (start<0 || end<0)
            return null;
        
        return source.substring(start+3, end);
    }

   private String loadFile() {
        FileIO f=FileIO.createConnection(file);
        byte[] b = null;
        try {
            InputStream is=f.openInputStream(); 
            b = new byte[(int) f.fileSize()];
            is.read(b); is.close(); f.close();
        } catch (IOException e) { try { f.close(); } catch (IOException ex2) { } }
        return new String(b, 0, b.length);
    }
}
