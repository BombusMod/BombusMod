/*
 * DeTranslit.java
 *
 * Created on 1.06.2008, 12:58
 *
 * Copyright (c) 2006-2008, Daniel Apatin (ad), http://apatin.net.ru
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

import Client.Config;
import java.util.Vector;

/**
 *
 * @author ad
 */
public class DeTranslit {
//#ifdef PLUGINS
//#     public static String plugin = "PLUGIN_DT";
//#endif
    static Vector translit[];
    static boolean filled=false;
    
    private static DeTranslit instance;
    
    public static DeTranslit getInstance(){
	if (instance==null) {
	    instance=new DeTranslit();
	    DeTranslit.fill();
	}
	return instance;
    }
    
    /** Creates a new instance of DeTranslit
     * @param src
     * @return
     */
    public static String deTranslit(String src) {
        if (translit[0].size()<1) return src;
        if (src==null) return null;
        for (int i=0; i<translit[0].size(); i++) {
            src=StringUtils.stringReplace(src, (String) translit[0].elementAt(i), (String) translit[1].elementAt(i));
	}
        return src;
    }
    
    public static String translit(String src) {
        if (translit[0].size()<1) return src;
        if (src==null) return null;
        for (int i=0; i<translit[0].size(); i++) {
            src=StringUtils.stringReplace(src, (String) translit[1].elementAt(i), (String) translit[0].elementAt(i));
	}
        return src;
    }

//#ifdef DETRANSLIT
    public static String get_actual_filename(String filename) {
        if (Config.getInstance().transliterateFilenames) {
            return translit(filename);
        } else {
            return filename;
        }
    }
//#endif

    private static void fill() {
        translit = null;
        translit = new Vector[2];
        translit[0] = new Vector();
        translit[1] = new Vector();

        Vector defs[] = null;
        defs = new StringLoader().stringLoader("/translit.txt", 2);
        if (defs != null) {
            for (int i = 0; i < defs[0].size(); i++) {
                translit[0].addElement((String) defs[0].elementAt(i));
                translit[1].addElement((String) defs[1].elementAt(i));
            }
        }
    }
/*
    public static String stringReplace(String aSearch, String aFind, String aReplace) {
        String result = aSearch;
        if (result != null && result.length() > 0) {
            int a = 0;
            int b = 0;
            while (true) {
                a = result.indexOf(aFind, b);
                if (a != -1) {
                    result = result.substring(0, a) + aReplace + result.substring(a + aFind.length());
                    b = a + aReplace.length();
                } else
                    break;
            }
        }
        return result;
    }
*/
}
