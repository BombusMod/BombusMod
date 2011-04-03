/*
 * MessageItem.java
 *
 * Created on 21.01.2006, 23:17
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
 *
 */

package Messages;

import Client.Config;
import Client.Msg;
import Client.StaticData;
import images.RosterIcons;
import java.util.Vector;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import Colors.ColorTheme;
import ui.ComplexString;
import Fonts.FontCache;
import ui.VirtualElement;
import ui.VirtualList;
import ui.controls.form.ListItem;

public class MessageItem implements VirtualElement {//, MessageParser.MessageParserNotify {
    
    public Msg msg;
    public final Vector msgLines;
    private boolean even;
    private boolean smiles;
    private boolean partialParse=false;
    
    private int itemHeight = -1;
    //private Font font;
    
    /** Creates a new instance of MessageItem */
    public MessageItem(Msg msg, boolean showSmiles) {
        msgLines = new Vector();
        this.msg=msg;
        this.smiles=showSmiles;
        //this.font=FontCache.getFont(false, FontCache.msg);
        partialParse = msg.itemCollapsed;
        parse();
    }

    public int getVHeight() { 
        if (msg==null) return 0;
        if (itemHeight<0) {
            itemHeight=getFont().getHeight();
        }
        if (msg.delivered) {
            int rh=RosterIcons.getInstance().getHeight();
            Math.max(rh, Math.max(itemHeight, Config.getInstance().minItemHeight));
        }
        return Math.max(itemHeight, Config.getInstance().minItemHeight);
    }
    
    public Font getFont() {
        return FontCache.getFont(false, FontCache.msg);
    }
    
    public int getVWidth() { return 0; }
    
    public int getColorBGnd() {
        return ColorTheme.getColor(even ? ColorTheme.LIST_BGND_EVEN : ColorTheme.LIST_BGND);
    }
    public final void parse() {
        MessageParser.getInstance().parseMsg(this, VirtualList.messagesWidth);
        updateHeight();
    }
    public int getColor() { return msg.getColor(); }
    
    public void drawItem(Graphics g, int ofs, boolean selected) {
        int xorg=g.getTranslateX();
        int yorg=g.getTranslateY();
        int iconOffset = 2;
        g.translate(2,0);
        if (msgLines.isEmpty()) {
            parse();
        }
        if (msgLines.isEmpty()) {
            return;
        }
        //int y=0;
        int lineCount = msg.itemCollapsed ? 1 : msgLines.size();
        int messageHeight = 0;
        for (int i = 0; i < lineCount; i++) {
            messageHeight += ((ComplexString) msgLines.elementAt(i)).getVHeight();
        }
        if (messageHeight < Config.getInstance().minItemHeight) {
            g.translate(g.getTranslateX(), (Config.getInstance().minItemHeight - messageHeight) >> 1);
        }
        for (int index = 0; index < lineCount; ++index) {
            ComplexString line = (ComplexString)msgLines.elementAt(index);
            if (line.isEmpty()) break;
            int h=line.getVHeight();
            int cy=g.getClipY();
            //clipping
            if (cy <= h && cy+g.getClipHeight()>0 ) {
                if (msg.itemCollapsed && (msgLines.size()>1)) {
                    RosterIcons.getInstance().drawImage(g, 
                            RosterIcons.ICON_MSGCOLLAPSED_INDEX,
                            0,
                            (getVHeight()
                            - RosterIcons.getInstance().getHeight()) >> 1  - g.getTranslateY());
                    g.translate(8,0);
                    iconOffset = 2 + RosterIcons.getInstance().getWidth() >> 1;
                } 
                line.drawItem(g, iconOffset, selected);
            }
            g.translate(g.getTranslateX(), h);
        }
        
        g.translate(xorg-g.getTranslateX(), yorg-g.getTranslateY());

        if (msg.delivered) {
            int right=g.getClipX()+g.getClipWidth();
            RosterIcons.getInstance().drawImage(
                    g, RosterIcons.ICON_DELIVERED_INDEX, 
                    right-RosterIcons.getInstance().getWidth()-3, 0 
            );
        }
    }
    
    public void onSelect() {
        msg.itemCollapsed = !msg.itemCollapsed;
        if (partialParse) {
            partialParse=false;
            parse();
        }
        updateHeight();
        StaticData.getInstance().canvas.repaint();
    }
    
    private void updateHeight() {
        int height = 0;
        int size = msg.itemCollapsed ? Math.min(msgLines.size(), 1) : msgLines.size();
        for (int i = 0; i < size; ++i) {
            ComplexString line=(ComplexString) msgLines.elementAt(i);
            height += line.getVHeight();
        }
        itemHeight = height;
    }

    public Vector getUrlList() { 
        Vector urlList=new Vector();
        addUrls(msg.body, "http://", urlList);
        addUrls(msg.body, "https://", urlList);
        addUrls(msg.body, "tel:", urlList);
        addUrls(msg.body, "ftp://", urlList);
        addUrls(msg.body, "native:", urlList);
        return (urlList.isEmpty())? null: urlList;
    }
    
    private void addUrls(String text, String addString, Vector urlList) {
        int pos=0;
        int len=text.length();
        while (pos<len) {
            int head=text.indexOf(addString, pos);
            if (head>=0) {
                pos=head;
                
                while (pos<len) {
                    char c=text.charAt(pos);
                    if (c==' ' || c==0x09 || c==0x0d || c==0x0a || c==0xa0 || c==')' )  
                        break;
                    pos++;
                }
                urlList.addElement(new ListItem(text.substring(head, pos)));
                
            } else break;
        }
    }
    
    public void setEven(boolean even) {
        this.even = even;
    }

    public String getTipString() {
        return msg.getTime();
    }
//#ifdef SMILES
    public void toggleSmiles(VirtualList view) {
        smiles = !smiles;
        parse();
        view.redraw();
    }
    
    final boolean smilesEnabled() { return smiles; }
//#endif

    public boolean isSelectable() { return true; }

    public boolean handleEvent(int keyCode) { return false; }
}
