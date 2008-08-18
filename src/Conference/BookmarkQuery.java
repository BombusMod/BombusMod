/*
 * BookmarkQuery.java
 *
 * Created on 6.11.2006, 22:24
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

package Conference;

import Client.StaticData;
import com.alsutton.jabber.JabberBlockListener;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.datablocks.Iq;
import java.util.Enumeration;
import java.util.Vector;
import Client.Config;
import com.alsutton.jabber.datablocks.Presence;

import util.StringLoader;

/**
 *
 * @author Evg_S
 */
public class BookmarkQuery implements JabberBlockListener{

    public final static boolean SAVE=true;
    public final static boolean LOAD=false;
    
    private StaticData sd = StaticData.getInstance();
    
    private Config cf=Config.getInstance();
    
    /** Creates a new instance of BookmarkQurery */
    public BookmarkQuery(boolean saveBookmarks) {
        
        JabberDataBlock request=new Iq(null, (saveBookmarks)?Iq.TYPE_SET: Iq.TYPE_GET, "getbookmarks");
        JabberDataBlock query=request.addChildNs("query", "jabber:iq:private");

        JabberDataBlock storage=query.addChildNs("storage", "storage:bookmarks");
        if (saveBookmarks) {
            for (Enumeration e=sd.roster.bookmarks.elements(); e.hasMoreElements(); ) {
                storage.addChild( ((BookmarkItem)e.nextElement()).constructBlock() );
            }
        }
        sd.roster.theStream.send(request);
    }
    
    
    public int blockArrived(JabberDataBlock data) {
        try {
            if (!(data instanceof Iq)) 
                return JabberBlockListener.BLOCK_REJECTED;
            if (data.getAttribute("id").equals("getbookmarks")) {
                JabberDataBlock storage=data.findNamespace("query", "jabber:iq:private"). findNamespace("storage", "storage:bookmarks");
                Vector bookmarks=new Vector();
		boolean autojoin=cf.autoJoinConferences && sd.roster.myStatus!=Presence.PRESENCE_INVISIBLE;

                try {
                    for (Enumeration e=storage.getChildBlocks().elements(); e.hasMoreElements(); ){
                        BookmarkItem bm=new BookmarkItem((JabberDataBlock)e.nextElement());
                        bookmarks.addElement(bm);
                        if (bm.autojoin && autojoin) {
                            ConferenceForm.join(bm.desc, bm.jid+'/'+bm.nick, bm.password, cf.confMessageCount);
                        }
                    }
                } catch (Exception e) { } //no any bookmarks

                if (bookmarks.isEmpty()) 
                    loadDefaults(bookmarks);
					
                sd.roster.bookmarks=bookmarks;
                sd.roster.redraw();
                
                return JabberBlockListener.NO_MORE_BLOCKS;
            }
        } catch (Exception e) { }
        return JabberBlockListener.BLOCK_REJECTED;
    }

    private void loadDefaults(Vector bookmarks) {
	Vector defs[]=new StringLoader().stringLoader("/def_bookmarks.txt", 4);
        for (int i=0; i<defs[0].size(); i++) {
            String jid      =(String) defs[0].elementAt(i);
            String nick     =(String) defs[1].elementAt(i);
            String pass     =(String) defs[2].elementAt(i);
            String desc     =(String) defs[3].elementAt(i);
            if (desc==null) desc=jid;
            if (pass==null) pass="";
            if (nick==null) nick=sd.account.getNickName();
            BookmarkItem bm=new BookmarkItem(desc, jid, nick, pass, false);
            bookmarks.addElement(bm);
        }
    }
}
