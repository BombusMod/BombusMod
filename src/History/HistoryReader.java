/*
 * HistoryReader.java
 *
 * Created on 18.06.2008, 10:39
 *
 * Copyright (c) 2006-2008, Daniel Apatin (ad), http://apatin.net.ru
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

package History;

import Client.Contact;
import Client.Msg;
import Messages.MessageList;
import java.util.Vector;
import javax.microedition.lcdui.Display;
import ui.MainBar;

/**
 *
 * @author ad
 */
public class HistoryReader
    extends MessageList {
    
    public int thisIndex=-1;

    private HistoryLoader hl;
    
    private boolean parsing=false;
    
    private Msg thisMsg=null;
    
    private int listSize=0;
    
    /** Creates a new instance of HistoryReader */
    public HistoryReader(Display display, Contact c) {
        super();

        setMainBarItem(new MainBar("History reader"));
        
        hl = new HistoryLoader(c.getBareJid());
        
        listSize=hl.getSize();
        
        if (listSize>0)
            loadMessage(0);
        else
            return;
        
        setCommandListener(this);
        
	addCommand(cmdBack);
        
        attachDisplay(display);
    }
    
    protected void beginPaint() {
        StringBuffer str = new StringBuffer()
        .append(" (")
        .append(thisIndex+1)
        .append("/")
        .append(listSize)
        .append(")");
        
        getMainBarItem().setElementAt(str.toString(),1);
    }    

    public int getItemCount() {
        return (listSize>0)?1:0;
    }

    public Msg getMessage(int index) {
	return thisMsg;
    }
    
    public void keyPressed(int keyCode) {
        if (parsing) return;
        
        switch (keyCode) {
            case KEY_NUM4:
                loadPrev();
                return;
            case KEY_NUM6:
                loadNext();
                return;
        default:
            try {
                switch (getGameAction(keyCode)){
                    case LEFT:
                        loadPrev();
                        break;
                    case RIGHT:
                        loadNext();
                        break;
                }
            } catch (Exception e) {/* IllegalArgumentException @ getGameAction */}
        }
        super.keyPressed(keyCode);
    }
    
    public void loadNext() {
        loadMessage ((thisIndex<listSize-1)?thisIndex+1:0);
    }
    
    public void loadPrev() {
        loadMessage ((thisIndex>0)?thisIndex-1:listSize-1);
    }
    
    public void loadMessage (final int index) {
        if (thisIndex==index) return;
        
        parsing=true;
        new Thread(new Runnable() {
            public void run() {
                messages=null;
                messages=new Vector();
                thisMsg=hl.getMessage(index);
                thisMsg.itemCollapsed=false;
                thisIndex=index;
                parsing=false;

                repaint();
            }
        }).start();
        //redraw();
    }
}
