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

import java.util.Enumeration;
import java.util.Vector;

/**
 *
 * @author ad
 */
public class DeTranslit {
    static Vector translit;
    static boolean filled=false;
    
    private static DeTranslit instance;
    
    public static DeTranslit getInstance(){
	if (instance==null) {
	    instance=new DeTranslit();
	    instance.fill();
	}
	return instance;
    }
    
    /** Creates a new instance of DeTranslit */
    public static String deTranslit(String src) {
        if (translit.size()<1)
            return src;
	for (Enumeration r=translit.elements(); r.hasMoreElements(); ) {
	    TranslitItem tr=(TranslitItem)r.nextElement();
            String temp=
            src=stringReplace(src, tr.getLat(), tr.getCyr());
	}
        return src;
    }
    
    public static String translit(String src) {
        if (translit.size()<1)
            return src;
	for (Enumeration r=translit.elements(); r.hasMoreElements(); ) {
	    TranslitItem tr=(TranslitItem)r.nextElement();
            src=stringReplace(src, tr.getCyr(), tr.getLat());
	}
        return src;
    }
    
    private static void fill() {
        translit=new Vector();
        Vector defs[]=new StringLoader().stringLoader("/translit.txt", 2);
        for (int i=0; i<defs[0].size(); i++) {
            String lat=(String) defs[0].elementAt(i);
            String cyr=(String) defs[1].elementAt(i);
            translit.addElement( new TranslitItem(lat, cyr) );
        }
    }
    
    public static String stringReplace(String aSearch, String aFind, String aReplace) {
    	int pos = aSearch.indexOf(aFind);
    	if (pos != -1) {
            StringBuffer buffer = new StringBuffer();
            int lastPos = 0;
             while (pos != -1) {
                    buffer.append(aSearch.substring(lastPos, pos)).append(aReplace);
                    lastPos = pos + aFind.length();
                    pos = aSearch.indexOf(aFind, lastPos);
            }
            buffer.append( aSearch.substring(lastPos));
            aSearch = buffer.toString();
    	}
    	return aSearch;
    }

    static class TranslitItem {
        private String lat; private String cyr;
        
        public TranslitItem(String lat, String cyr){
            this.lat=lat;
            this.cyr=cyr;
        }
        
        public String getLat() { return lat; }
        public String getCyr() { return cyr; }
    }
}
