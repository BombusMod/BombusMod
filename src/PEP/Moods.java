/*
 * Moods.java
 *
 * Created on 1.05.2008, 16:09
 *
 * Copyright (c) 2005-2007, Eugene Stahov (evgs), http://bombus-im.org
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

//#ifdef PEP

package PEP;

import java.util.Vector;
import util.StringLoader;

/**
 *
 * @author evgs
 */
public class Moods {

    /** Creates a new instance of Moods */
    private Moods() {
       String moodFile="/lang/"+Client.Config.getInstance().lang+".moods.txt";
       Vector vMood[]=new StringLoader().stringLoader(moodFile, 2);
       
       if (vMood==null) vMood=new StringLoader().stringLoader("/lang/en.moods.txt", 2);
       if (vMood==null) {
           //System.out.println("Cant't load mood names");
           moodKey=null;
           moodKey=new Vector();
           moodValue=null;
           moodValue=new Vector();
       } else {
           moodKey=(Vector)vMood[0];
           moodValue=(Vector)vMood[1];
       }

        moodKey.trimToSize();
        moodValue.trimToSize();
    }
    
    Vector moodKey;
    Vector moodValue;
    public String myMoodName="";
    public String myMoodText="";
    public String myMoodId="bmood";
    
    private static Moods instance;

    public static Moods getInstance() {
        if (instance==null) instance=new Moods();
        return instance;
    }
    
    public int getMoodIngex(String mood) {
        for (int i=0; i<moodKey.size(); i++) {
            if (mood.equals((String)moodKey.elementAt(i))) return i;
        }
        return -1;
    }
    
    public String getMoodLabel(int index) { 
        if (index<0) return null;
        return (String)moodValue.elementAt(index); 
    }

    String getMoodName(int index) {
        return (String)moodKey.elementAt(index);
    }
    public int getCount() {
        return moodKey.size();
    }
}

//#endif
