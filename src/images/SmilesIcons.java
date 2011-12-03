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
public class SmilesIcons {
    
    private static String res= "/images/smiles.png";
    private static String anitxt= "/smiles/smiles.txt";
    private static String statictxt= "/images/smiles.txt";
    
    private final static int SMILES_IN_ROW=16;
    private static int cols;
    
    private static ImageList instance = null;
    public static ImageList getInstance() {
//#ifdef SMILES
        if (null == instance){

             try {
                 int smilesCount = MessageParser.getInstance().getSmileTable().size();
                 cols = ceil(SMILES_IN_ROW, smilesCount);
             } catch (Exception e) {
//#ifdef DEBUG
//#                  System.out.println("Can't load smiles");
//#endif
             }
//#ifdef ANI_SMILES
            instance = new AniImageList();
            ((AniImageList) instance).load("/smiles");
//#endif

            if (instance == null || instance.getWidth() == 0) {
                instance = new ImageList(res, cols, SMILES_IN_ROW);
            }
        }
//#endif
        return instance;
    }
    
    private static int ceil(int rows, int count){
        int tempCols = count / rows;
        if (count > (tempCols * rows)) tempCols++;
        return tempCols;
    }
}
