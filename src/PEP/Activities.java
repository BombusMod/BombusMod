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

package PEP;

import Client.Config;
import java.util.Vector;
import util.StringLoader;

/**
 *
 * @author evgs
 */
public class Activities {

    /** Creates a new instance of Moods */
    private Activities() {
       String actFile="/lang/"+Config.getInstance().lang+".activities.txt";
       Vector vAct[]=new StringLoader().stringLoader(actFile, 2);
       
       if (vAct==null) vAct=new StringLoader().stringLoader("/lang/en.activities.txt", 2);
       if (vAct==null) {
           //System.out.println("Cant't load mood names");
           actKey=null;
           actKey=new Vector();
           actValue=null;
           actValue=new Vector();
       } else {
           actKey=vAct[0];
           actValue=vAct[1];
           actKey.insertElementAt("<None>", 0);
           actValue.insertElementAt("<None>", 0);
       }

        actKey.trimToSize();
        actValue.trimToSize();
    }
    
    Vector actKey;
    Vector actValue;
    public String myActName="";
    public String myActText="";
    public String myActId="bactivity";
    private int[] gen =  { 1, 11, 15, 20, 30, 37, 38, 47, 60, 64, 74};

    private static Activities instance;

    public static Activities getInstance() {
        if (instance==null) instance=new Activities();
        return instance;
    }
    
    public int getActIndex(String act) {
        for (int i=0; i<actKey.size(); i++) {
            if (act.equals((String)actKey.elementAt(i))) return i;
        }
        return -1;
    }

    private boolean isGeneral(int index) {
        for(int i=0; i< gen.length; i++) {
            if (index == gen[i]) return true;
        }
        return false;
    }
    private int getGeneralFor(int value) {
        if (value <= 0 || value > getCount()) return 0;
        for(int i = 0; i < gen.length ; i++) {
            int min = gen[i];
            int max = (i==(gen.length-1))? 100: gen[i + 1];
            if ((value > min) && (value < max)) return min;
        }
        return 0;
    }   


    public String[] getActName(int index) {
        String[] names = new String[2];
        if (isGeneral(index)) {
            names[0] = (String)actKey.elementAt(index);
        } else {
            names[0] = (String)actKey.elementAt(getGeneralFor(index));
            names[1] = (String)actKey.elementAt(index);
        }
        return names;
    }
    public int getCount() {
        return actKey.size();
    }
    public String getLabel(String tag) {
        int index = getActIndex(tag);
        return (String)actValue.elementAt(index);
    }
}
