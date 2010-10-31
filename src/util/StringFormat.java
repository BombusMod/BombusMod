/*
 * StringFormat.java
 *
 * Created on 8.06.2006, 15:00
 *
 * Copyright (c) 2008, Eugene Stahov (evgs), http://bombus-im.org
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

import java.util.Vector;

/**
 *
 * @author evgs
 */
public class StringFormat {

    private String format;
    private Vector args;
    
    /** Creates a new instance of StrungFormat */
    public StringFormat(String format) {
        this.format=format;
        args=new Vector();
    }
    
    public String toString() {
        StringBuffer out = new StringBuffer();
        
        int index=0;
        int len=format.length();
        
        while (index<len) {
            char c=format.charAt(index); 
            index++;

            char c2;
            switch (c) {
                case '%':
                    c2=format.charAt(index);
                    index++;
                    if (c2=='%') {
                        out.append(c);
                        continue;
                    }
                    
                    int ai=c2-'1';
                    if ((ai<0) || (ai>8)) throw new IllegalArgumentException();
                    
                    String s=null;
                    if (ai<args.size()) s=(String) args.elementAt(ai);
                    
                    out.append(s);
                    
                    continue;
                
                default:
                    out.append(c);
            }
        }
        
        return out.toString();
    }
    
    public StringFormat append(String arg) { args.addElement(arg); return this; }
    public StringFormat append(Object obj) { args.addElement(obj.toString()); return this; }
    public StringFormat append(boolean b)  { args.addElement(String.valueOf(b)); return this; }
    public StringFormat append(char c)     { args.addElement(String.valueOf(c)); return this; }
    public StringFormat append(int i)      { args.addElement(String.valueOf(i)); return this; }
    public StringFormat append(long l)     { args.addElement(String.valueOf(l)); return this; }
    
    public StringFormat append(char[] data, int offset, int count) {
        if (count<0) count=data.length-offset;
        args.addElement(String.valueOf(data, offset, count)); 
        return this; 
    }
}
