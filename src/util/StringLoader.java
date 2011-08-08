/*
 * StringLoader.java
 *
 * Created on 25.11.2005, 1:25
 *
 * Copyright (c) 2005-2008, Eugene Stahov (evgs), http://bombus-im.org
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
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Vector;
import io.file.InternalResource;

public class StringLoader {

    int afterEol;

    public Vector[] stringLoader(String resource, int columns) {
    StringBuffer buf = new StringBuffer();
    Vector table[] = new Vector[columns];
    for (int i = 0; i<columns; i++) {
            table[i]=null;
        table[i]=new Vector();
    }

    afterEol=0;
    InputStream in = InternalResource.getResourceAsStream(resource);
        if (in==null) return null;
    try {
        while (true) {
        String line=readLine(in);
        if (line==null)  break;

        if (line.startsWith("//")) continue; // skip all remarks

                if (line.length()==0) continue;

        int indexFrom=0;

        for (int i = 0; i<columns; i++) {
            String cell=null;
            try {
            int indexTo=line.indexOf(0x09, indexFrom);

            if (indexTo<0) indexTo=line.length();
            if (indexFrom<indexTo) cell=line.substring(indexFrom, indexTo);
            indexFrom=indexTo+1;
            } catch (Exception e) {
                        //e.printStackTrace();
                    }

            table[i].addElement( cell );
        }
        }
        in.close();
    } catch (Exception e)    {
            //e.printStackTrace();
        }
        buf=null;
    return table;
    }

    public Vector[] stringLoader(final InputStream in, final int columns) {

        StringBuffer buf = new StringBuffer();
        Vector table[] = new Vector[columns];
        for (int i = 0; i<columns; i++) {
            table[i]=null;
            table[i]=new Vector();
        }

        afterEol=0;
        try {
            while (true) {
            String line=readLine(in);
            if (line==null)  break;

            if (line.startsWith("//")) continue; // skip all remarks

            int indexFrom=0;

            for (int i = 0; i<columns; i++) {
                String cell=null;
                try {
                int indexTo=line.indexOf(0x09, indexFrom);

                if (indexTo<0) indexTo=line.length();
                if (indexFrom<indexTo) cell=line.substring(indexFrom, indexTo);
                indexFrom=indexTo+1;
                } catch (Exception e) { e.printStackTrace(); }

                table[i].addElement( cell );
            }
            }
            in.close();
        } catch (Exception e)    { e.printStackTrace();}
        return table;
    }

    public Hashtable hashtableLoader(String resource) {
    Hashtable hash = new Hashtable();

    afterEol=0;
    InputStream in = InternalResource.getResourceAsStream(resource);
        if (in==null) return null;
    try {
        while (true) {
        String line=readLine(in);
                String key, value;
        if (line==null)  break;

        if (line.startsWith("//")) continue; // skip all remarks

                String cell=null;
                try {
                    int indexTab=line.indexOf(0x09);

                    if (indexTab<=0) continue; // process next line

                    key=line.substring(0, indexTab);
                    value=line.substring(indexTab+1, line.length() );
                    hash.put(key, value);
                } catch (Exception e) { /* e.printStackTrace(); */ }
        }
        in.close();
    } catch (Exception e)    { /* Empty file or not found */}
    return hash;
    }

    public Hashtable hashtableLoaderFromString(String sourc) {
    Hashtable hash = new Hashtable();

    afterEol=0;

        String source=sourc;
    try {
        while (true) {
        String line=readLine(source.substring(afterEol));
                String key, value;
        if (line==null)  break;

        if (line.startsWith("//")) continue; // skip all remarks

                try {
                    int indexTab=line.indexOf(0x09);

                    if (indexTab<=0) continue; // process next line

                    key=line.substring(0, indexTab);
                    value=line.substring(indexTab+1, line.length() );
                    hash.put(key, value);
                } catch (Exception e) {  }
        }
    } catch (Exception e)    {  }

    return hash;
    }

    String readLine(String source) throws IOException {
    StringBuffer buf=new StringBuffer();
        int pos=0;
    try {
            boolean eol=false;
            while (true) {
                int c = getUtfChar(source.substring(pos));
                pos++;
                if (c<0) {
                    eol=true;
                    if (buf.length()==0) return null;
                    break;
                }
                if (c==0xfeff) continue; //skip bom
                if (c==0x0d || c==0x0a) {
                    eol=true;
                    if (c==0x0a) break;
                } else {
                    if (eol) {
                        break;
                    }
                    buf.append((char) c);
                }
            }
        } catch (Exception e)    {  }
        afterEol+=pos;
    return buf.toString();
    }

    int getUtfChar(String source) throws IOException {
    try {
            int chr = source.charAt(0);
            if( chr == 0xff ) return -1; // end of stream

            if (chr<0x80) return chr;
            if (chr<0xc0) throw new IOException("Bad UTF-8 Encoding encountered");
            int chr2= source.charAt(1) &0xff;
            if (chr2==0xff) return -1;
            if (chr2<0x80) throw new IOException("Bad UTF-8 Encoding encountered");

            if (chr<0xe0) {
                // cx, dx
                return ((chr & 0x1f)<<6) | (chr2 &0x3f);
            }
            if (chr<0xf0) {
                // cx, dx
                int chr3= source.charAt(2) &0xff;
                if (chr3==0xff) return -1;
                if (chr3<0x80) throw new IOException("Bad UTF-8 Encoding encountered");
                else return ((chr & 0x0f)<<12) | ((chr2 &0x3f) <<6) | (chr3 &0x3f);
            }

    } catch (Exception e)    {  }
    return -1;
    }

    String readLine(InputStream inputstream) throws IOException {
    StringBuffer buf=new StringBuffer();

    try {
            if (afterEol>0) {
                buf.append(afterEol);
                afterEol=0;
            }
            boolean eol=false;
            while (true) {
                int c = getUtfChar(inputstream);
                if (c<0) {
                    eol=true;
                    if (buf.length()==0) return null;
                    break;
                }
                if (c==0xfeff) continue; //skip bom
                if (c==0x0d || c==0x0a) {
                    eol=true;
                    //inputstream.mark(2);
                    if (c==0x0a) break;
                }
                else {
                    if (eol) {
                        afterEol=c;
                        //inputstream.reset();
                        break;
                    }
                    buf.append((char) c);
                }
            }
        } catch (Exception e)    {  }
    return buf.toString();
    }

    int getUtfChar(InputStream is) throws IOException {
    try {
            int chr = is.read();
            if( chr == 0xff ) return -1; // end of stream

            if (chr<0x80) return chr;
            if (chr<0xc0) throw new IOException("Bad UTF-8 Encoding encountered");
            int chr2= is.read() &0xff;
            if (chr2==0xff) return -1;
            if (chr2<0x80) throw new IOException("Bad UTF-8 Encoding encountered");

            if (chr<0xe0) {
                // cx, dx
                return ((chr & 0x1f)<<6) | (chr2 &0x3f);
            }
            if (chr<0xf0) {
                // cx, dx
                int chr3= is.read() &0xff;
                if (chr3==0xff) return -1;
                if (chr3<0x80) throw new IOException("Bad UTF-8 Encoding encountered");
                else return ((chr & 0x0f)<<12) | ((chr2 &0x3f) <<6) | (chr3 &0x3f);
            }

    } catch (Exception e)    {  }
    return -1;
    }

    public Hashtable hashtableLoader(final InputStream in) {
        Hashtable hash = new Hashtable();

        afterEol=0;
        try {
            while (true) {
            String line=readLine(in);
                String key, value;
            if (line==null)  break;

            if (line.startsWith("//")) continue; // skip all remarks

                String cell=null;
                try {
                    int indexTab=line.indexOf(0x09);

                    if (indexTab<=0) continue; // process next line

                    key=line.substring(0, indexTab);
                    value=line.substring(indexTab+1, line.length() );
                    hash.put(key, value);
                } catch (Exception e) { e.printStackTrace(); }
            }
            in.close();
        } catch (Exception e)    { /* Empty file or not found */}
        return hash;
    }

}
