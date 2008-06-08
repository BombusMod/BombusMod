/*
 * ExtendedStatus.java
 *
 * Created on 27.02.2005, 17:04
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

package Client;

import images.RosterIcons;
import Colors.ColorTheme;
import ui.IconTextElement;

/**
 *
 * @author Eugene Stahov
 */
public class ExtendedStatus extends IconTextElement{
    
    private String name="";    // status name
    private String status="";
    private int priority;
    int index;
    private String screenName="";
    private boolean autoRespond;
    private String autoRespondMessage = "";
    
    /** Creates a new instance of ExtendedStatus */
    public ExtendedStatus(int index, String name, String showName) {
        super(RosterIcons.getInstance());
        this.index=index;
        this.name=name;
        this.screenName=showName;
    }
    
    //public void onSelect(){}
    public String toString(){ 
        StringBuffer s=new StringBuffer(screenName);
        s.append(" (");
        s.append(priority);
        s.append(") ");
        if (status!=null)
            if (status.length()>0) {
                s.append('"');
                s.append(status);
                s.append('"');
            }
        return s.toString();
    }
    public String getScreenName() { return screenName; }

    public int getImageIndex(){ return index;}

    public String getName() { return name; }
    
    public String getMessage() { return status; }
    public void setMessage(String s) { status=s; }

    public int getPriority() { return priority; }
    public void setPriority(int p) { priority=p; }
    
    public boolean getAutoRespond() { return autoRespond; }
    public void setAutoRespond(boolean state) { autoRespond=state; }
    
    public String getAutoRespondMessage() { return autoRespondMessage; }
    public void setAutoRespondMessage(String s) { autoRespondMessage=s; }
}
