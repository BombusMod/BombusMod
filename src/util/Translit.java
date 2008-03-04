/*
 * Translit.java
 *
 * Created on 25.04.2007, 10:30
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

public class Translit {

    static char TranslitChars[][] = new char[106][6];
    static int TranslitCount[] = new int[106];
    static boolean full=false;

    public static final String initTrans = "A.B.V.G.D.E.ZH.Z.I.J.K.L.M.N.O.P.R.S.T.U.F.H.C.CH.SH.SCH.\".Y.'.E.YU.YA.a.b.v.g.d.e.zh.z.i.j.k.l.m.n.o.p.r.s.t.u.f.h.c.ch.sh.sch.\".y.'.e.yu.ya.";

    static void fillarrays()
    {
        TranslitChars[25]="YO".toCharArray(); //YO
        TranslitCount[25]=2;
        TranslitChars[105]="yo".toCharArray(); //yo
        TranslitCount[105]=2;
        
        int ch=40; int p=0; int pos=0;
        while (pos<initTrans.length()) {
           char c=initTrans.charAt(pos); pos++;
           if (c=='.') {
             TranslitCount[ch]=p;
             ch++; p=0;
           } else {
             TranslitChars[ch][p]=c;
             p++;
           }
        }
    }
    
    public static String translit(String s)
    {
        if (!full) 
            fillarrays();
        
        char ac[] = new char[s.length() * 3];
        char ac1[] = s.toCharArray();
        int l = -1;
        for(int i=0; i<s.length(); i++)
        {
            char c = ac1[i];
            if((c >= '\u0410') & (c <= '\u044F') || (c == '\u0401' || c == '\u0451'))
            {
                int k = c - 1000;
                for(int j = 0; j < TranslitCount[k]; j++)
                {
                    l++;
                    ac[l] = TranslitChars[c - 1000][j];
                }

            } else {
                l++;
                ac[l] = ac1[i];
            }
        }

        return new String(ac, 0, l + 1);
    }
}
