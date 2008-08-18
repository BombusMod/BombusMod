/*
 * SmilesIcons.java
 *
 * Created on 21.05.2008, 22:24
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

package images;

import Messages.MessageParser;
import ui.ImageList;

/**
 *
 * @author EvgS
 */
public class SmilesIcons extends ImageList {
    
    private static String res= "/images/smiles.png";
    private static String restxt= "/images/smiles.txt";
    
    private final static int SMILES_IN_ROW=16;
    private static int cols;
    /** Creates a new instance of SmilesIcons */
    private SmilesIcons() {
	super(res, cols, SMILES_IN_ROW);
    }
    
    private static ImageList instance;
    public static ImageList getInstance() {
	if (instance==null){
//#ifdef SMILES
            try {
                int smilesCount=MessageParser.getInstance().getSmileTable().size();
                cols=ceil(SMILES_IN_ROW, smilesCount);
            } catch (Exception e) {
                System.out.print("Can't load ");
                System.out.println(restxt);
            }
//#endif
            instance=new SmilesIcons();
        }
	return instance;
    }
    
    private static int ceil(int rows, int count){
        int tempCols=count/rows;
        if (count>(tempCols*rows))
            tempCols++;
        return tempCols;
    }
}
