/*
 * LastVersion.java
 *
 * Copyright (c) 2006-2007, Daniel Apatin (ad), http://apatin.net.ru
 *
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
 */

package util;

import Info.Version;
import java.io.*;
import javax.microedition.io.*;
import javax.microedition.lcdui.*;
import locale.SR;

public class LastVersion implements CommandListener
{
    
    private Display display;
    private Displayable parentView;
    
    private Form form;
    
    String url = Version.getUrl()+"/news/lastest.php";
    protected Command cmdGo = new Command(SR.MS_GOTO_URL, Command.SCREEN, 1);
    
    public LastVersion(Display display) {
        this.display=display;
        parentView=display.getCurrent();
        
       
        form=new Form(SR.MS_CHECK_UPDATE);
        form.addCommand(new Command(SR.MS_CLOSE, Command.BACK, 99));

        try {
            form.append(getVersion()+"\n");
        } catch (IOException e) {
            form.append("error on request\n");
        }
        
        form.setCommandListener(this);
        display.setCurrent(form);
    }
    
    public void commandAction(Command c, Displayable d) {
        display.setCurrent(parentView);
    }
    
    public String getVersion() throws IOException {
      HttpConnection c = null;
      InputStream is = null;
      OutputStream os = null;
      StringBuffer b = new StringBuffer();
      String result="";
      try {
         c = (HttpConnection)Connector.open(url);
         c.setRequestMethod(HttpConnection.GET);
         c.setRequestProperty("User-Agent","Profile/MIDP-1.0 Confirguration/CLDC-1.0");
         c.setRequestProperty("Content-Language", "en-CA");
         os = c.openOutputStream();
         is = c.openDataInputStream();
         int ch;
         while ((ch = is.read()) != -1) {
            b.append((char) ch);
         }
         result = b.toString();
      } finally {
         if(is!= null) {
            is.close();
         }
         if(os != null) {
            os.close();
         }
         if(c != null) {
            c.close();
         }
      }
      if (result.length()>0) {
          result=strconv.convCp1251ToUnicode(result);
            //int verStart=result.indexOf("start search");
            //int verEnd=result.indexOf("en� search");
            //if (verStart>0 && verEnd>0 ) {
            //    result=result.substring(verStart+16,verEnd-1);
            //}
      }
      b=null;
      return result;
   }
}
