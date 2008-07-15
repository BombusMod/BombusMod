/*
 * ClipBoard.java
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
 *
 */

package util;

import Client.Msg;

public class ClipBoard
 {
    
    // Singleton
    private static ClipBoard instance;

    public static ClipBoard getInstance(){
        if (instance==null) {
            instance=new ClipBoard();
        }
        return instance;
    }
    
    private static String clipBoard="";


    public String getClipBoard() {
      return clipBoard;
    }

    public void setClipBoard(String str) {
      clipBoard=(str.length()>4096)?str.substring(0,4095):str;
    }

    public boolean isEmpty() {
        boolean empty = true;
        if (clipBoard!=null)
            if (clipBoard.length()>0)
                empty = false;
        return empty;
    }
    
    public void add(Msg msg) {
        try {
            StringBuffer clipstr=new StringBuffer(msg.quoteString());
            setClipBoard(clipstr.toString());
            clipstr=null;
        } catch (Exception e) {/*no messages*/}
    }
    
    public void append(Msg msg) {
        try {
            StringBuffer clipstr=new StringBuffer(clipBoard)
            .append("\n\n")
            .append(msg.quoteString());
            setClipBoard(clipstr.toString());
            clipstr=null;
        } catch (Exception e) {/*no messages*/}
    }
}
