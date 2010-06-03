/*
 * PepListener.java
 *
 * Created on 30.04.2008, 21:37
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
package xmpp.extensions;

import PEP.Activities;
import Client.*;
import PEP.Moods;
import com.alsutton.jabber.*;
import com.alsutton.jabber.datablocks.*;
import java.util.*;
import locale.SR;

public class PepListener implements JabberBlockListener {
//#ifdef PLUGINS
//#     public static String plugin = new String("PLUGIN_PEP");
//#endif
    /** Singleton */
    private static PepListener instance;

    public static PepListener getInstance() {
        if (instance == null) {
            instance = new PepListener();
        }
        return instance;
    }
    StaticData sd = StaticData.getInstance();
    Config cf = Config.getInstance();

    /** Creates a new instance of PepListener */
    private PepListener() {
    }

    public void addBlockListener() {
        sd.roster.theStream.addBlockListener(instance);
    }

    public int blockArrived(JabberDataBlock data) {
        if (!(data instanceof Message)) {
            return BLOCK_REJECTED;        //if (!data.getTypeAttribute().equals("headline")) return BLOCK_REJECTED;
        }
        JabberDataBlock event = data.findNamespace("event", "http://jabber.org/protocol/pubsub#event");
        if (event == null) {
            return BLOCK_REJECTED;
        }
        String dtype = data.getTypeAttribute();
        if (event != null && dtype != null && dtype.equals("error")) {
            return BLOCK_PROCESSED;
        }
        String from = data.getAttribute("from");

        String id = null;
        String type = "";
        StringBuffer result = new StringBuffer();
        boolean cancel = false;
//#ifdef PEP_ACTIVITY
//#         boolean hasActivity = false;
//#         if (cf.rcvactivity) {
//#             JabberDataBlock activity = extractEvent(event, "activity", "http://jabber.org/protocol/activity");
//#             if (activity != null) {
//#                 if (activity.getChildBlocks() == null) // user cancel activity publishing
//#                 {
//#                     result = null;
//#                 } else {
//#                     String tag = null;
//#                     String activityText = null;
//#                     //String activityString=null;
//#                     try {
//#                         for (Enumeration e = activity.getChildBlocks().elements(); e.hasMoreElements();) {
//#                             JabberDataBlock child = (JabberDataBlock) e.nextElement();
//#                             tag = child.getTagName();
//#                             if (tag.equals("text")) {
//#                                 continue;
//#                             }
//#                             result.append(Activities.getInstance().getLabel(tag));
//#                             if (child.getChildBlocks() != null) {
//#                                 result.append(": ").append(Activities.getInstance().getLabel(((JabberDataBlock) child.getChildBlocks().elementAt(0)).getTagName()));
//#                             }
//#                             id = activity.getParent().getAttribute("id");
//#                         }
//#                     } catch (Exception ex) {
//#                     }
//# 
//#                     activityText = activity.getChildBlockText("text");
//#                     if (activityText != null) {
//#                         if (activityText.length() > 0) {
//#                             result.append("(").append(activityText).append(")");
//#                         }
//#                     }
//#                 }
//#                 hasActivity = true;
//# 
//#ifdef DEBUG
//#             System.out.println(from+": "+result.toString());
//#endif
//#                 type = SR.MS_USERACTIVITY;
//#             }
//#         }
//#endif
//#ifdef PEP_LOCATION
//#         boolean hasLocation = false;
//#         if (cf.rcvloc) {
//#             JabberDataBlock location = extractEvent(event, "geoloc", "http://jabber.org/protocol/geoloc");
//#             if (location != null) {
//#                 if (location.getChildBlocks() == null) // user cancel location publishing
//#                 {
//#                     result = null;
//#                 } else {
//#                     String tag = null;
//#                     String lat = "", lon = "", text = "";
//# 
//#                     try {
//#                         for (Enumeration e = location.getChildBlocks().elements(); e.hasMoreElements();) {
//#                             JabberDataBlock child = (JabberDataBlock) e.nextElement();
//#                             tag = child.getTagName();
//#                             if (tag.equals("text")) {
//#                                 text = child.getText();
//#                             }
//#                             if (tag.equals("lat")) {
//#                                 lat = child.getText();
//#                             }
//#                             if (tag.equals("lon")) {
//#                                 lon = child.getText();
//#                             }
//#                         }
//#                     } catch (Exception ex) {
//#                     }
//# 
//#                     result.append(text).append(" (").append(lat).append(", ").append(lon).append(")");
//#                 }
//#                 hasLocation = true;
//# 
//#ifdef DEBUG
//#             System.out.println(from+": "+result.toString());
//#endif
//#                 type = SR.MS_USERLOCATION;
//#             }
//#         }
//#endif

//#ifdef PEP_TUNE
//#         boolean hasTune = false;
//#         if (cf.rcvtune) {
//#             JabberDataBlock tune = extractEvent(event, "tune", "http://jabber.org/protocol/tune");
//#             if (tune != null) {
//#                 if (tune.getChildBlocks() == null) // user cancel tune publishing                 
//#                 {
//#                     result = null;
//#                 } else {
//#                     String src = tune.getChildBlockText("source");
//# 
//#                     result.append(tune.getChildBlockText("title")).append(" - ").append(tune.getChildBlockText("artist"));
//#                     if (src.length() > 0) {
//#                         result.append(" (").append(src).append(')');
//#                     }
//#                 }
//#                 hasTune = true;
//# 
//#ifdef DEBUG
//#             System.out.println(from+": "+result.toString());
//#endif
//#                 type = SR.MS_USERTUNE;
//#             }
//#         }
//#endif
        int moodIndex = -1;
        JabberDataBlock mood = null;
        String moodText = null;
        String tag = null;
        boolean hasMood = false;
//#ifdef PEP
//#         if (cf.sndrcvmood) {
//#             mood = extractEvent(event, "mood", "http://jabber.org/protocol/mood");
//# 
//#             if (mood != null) {
//#                 if (mood.getChildBlocks() == null) {
//#                     // user cancel mood publishing
//#                     moodIndex = -1;
//#                     moodText = null;
//#                 } else {
//#                     try {
//#                         for (Enumeration e = mood.getChildBlocks().elements(); e.hasMoreElements();) {
//#                             JabberDataBlock child = (JabberDataBlock) e.nextElement();
//#                             tag = child.getTagName();
//#                             if (tag.equals("text")) {
//#                                 continue;
//#                             }
//#                             moodIndex = Moods.getInstance().getMoodIngex(tag);
//# 
//#                             id = mood.getParent().getAttribute("id");
//#                         }
//#                     } catch (Exception ex) {
//#                         moodIndex = Moods.getInstance().getMoodIngex("-");
//#                     }
//# 
//#                     result.append(Moods.getInstance().getMoodLabel(moodIndex));
//#                     moodText = mood.getChildBlockText("text");
//#                     if (moodText != null) {
//#                         if (moodText.length() > 0) {
//#                             result.append("(").append(moodText).append(")");
//#                         }
//#                     }
//#                 }
//#                 hasMood = true;
//#if DEBUG
//#             System.out.println(from+": "+result.toString());
//#endif
//#                 type = SR.MS_USERMOOD;
//# 
//#             }
//#         }
//#endif
        Msg m = null;
        if (result != null) {
            m = new Msg(Msg.MESSAGE_TYPE_PRESENCE, from, type, result.toString());
        }
        Jid j = new Jid(from);
        Contact c = null;
        c = sd.roster.findContact(j, false);
        if (c.jid.equals(j, false)) {
            if (hasMood) {
//#ifdef PEP
//#                 c.pepMood = moodIndex;
//#                 c.pepMoodName = Moods.getInstance().getMoodLabel(moodIndex);
//#                 c.pepMoodText = moodText;
//#endif

                if (c.getGroupType() == Groups.TYPE_SELF) {
                    if (id != null) {
                        Moods.getInstance().myMoodId = id;
                    }
                    Moods.getInstance().myMoodName = tag;
                    Moods.getInstance().myMoodName = moodText;
                }
            }
//#ifdef PEP_ACTIVITY
//#             if (hasActivity) {
//#                 c.activity = (result != null) ? result.toString() : null;
//#             }
//#endif
//#ifdef PEP_LOCATION
//#             if (hasLocation) {
//#                 c.location = (result != null) ? result.toString() : null;
//#             }
//#endif

//#ifdef PEP_TUNE
//#             if (hasTune) {
//# 
//#                 c.pepTune = (result != null);
//#                 c.pepTuneText = (result != null) ? result.toString() : null;
//#             }
//#endif
            if (m != null) {
                c.addMessage(m);
            }
        }


        sd.roster.redraw();

        return BLOCK_PROCESSED;
    }

    JabberDataBlock extractEvent(JabberDataBlock data, String tagName, String xmlns) {
        JabberDataBlock items = data.getChildBlock("items");
        if (items == null) {
            return null;
        }
        if (!xmlns.equals(items.getAttribute("node"))) {
            return null;
        }
        JabberDataBlock item = items.getChildBlock("item");
        if (item == null) {
            return new JabberDataBlock();
        }
        return item.findNamespace(tagName, xmlns);
    }
}
