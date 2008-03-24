/*
 * ComplexIconTextList.java
 *
 * Created on 6.02.2005, 17:56
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

package ui;
//import ui.ImageList;
import javax.microedition.lcdui.*;
import java.util.*;

/**
 *
 * @author Eugene Stahov
 */
public class ComplexStringList extends VirtualList
{

    protected Vector lines;
    //private ComplexStringDraw cld;
    
    /** Creates a new instance of ComplexIconTextList */
    public ComplexStringList(Display display) {
        super(display);
        cursor=-1;
        //cld=new ComplexStringDraw(il);
    }
    
    public void attachList(Vector list) {
        lines=list;
    }
    
    //public Font getFont() { return f;}
    // overriding base class methods
    protected int getItemCount(){
        if (lines==null) return 0;
        return lines.size();
    }
    
    protected VirtualElement getItemRef(int index){
       return getLine(index); 
    }
    
    protected int getItemHeight(int index){ 
        return getLine(index).getVHeight();
    }
    protected ComplexString cacheUpdate(int index) {return null;}
    
    protected int getItemWidth(int index){ 
        return getLine(index).getVWidth();
    }        

    protected void drawItem(int index, Graphics g, int ofs, boolean selected){
        getLine(index).drawItem(g, ofs, false);
    }

    private ComplexString getLine(int index){
        ComplexString line;
        try {
            line=(ComplexString)lines.elementAt(index);
        } catch (Exception e) { line=null; }
        if (line==null) line=cacheUpdate(index);
        return line;
    }
}
