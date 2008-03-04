/*
 * SignNumberField.java
 *
 * Created on 10.12.2005, 1:29
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

package ui.controls;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Item;
//#if !MIDP1
import javax.microedition.lcdui.ItemCommandListener;
//#endif
import javax.microedition.lcdui.TextField;
import locale.SR;

/**
 *
 * @author EvgS
 */
public class NumberField extends TextField 
//#if !MIDP1
    implements ItemCommandListener 
//#endif
{
    private int initValue;
    private int minValue;
    private int maxValue;
    private Command sign;
    //private Command clear;
    /** Creates a new instance of SignNumberField */
    public NumberField(String label, int initValue, int minValue, int maxValue) {
	super(label, String.valueOf(initValue), 6, 
	    (minValue<0)?TextField.DECIMAL:NUMERIC );
	this.initValue=initValue;
	this.minValue=minValue;
	this.maxValue=maxValue;
	sign=new Command(SR.MS_CHSIGN, Command.ITEM, 3);
	//clear=new Command(SR.MS_CLEAR, Command.ITEM, 4);
//#if !MIDP1
	if (minValue<0) addCommand(sign);
        //addCommand(clear);
	setItemCommandListener(this);
//#endif
    }
    
    public int getValue() {
	try {
	    int value=Integer.parseInt(getString());
	    if (value>maxValue) return maxValue;
	    if (value<minValue) return minValue;
	    return value;
	} catch (NumberFormatException e) { /* returning initValue */ }
	return initValue;
    }

//#if !MIDP1
    public void commandAction(Command command, Item item) {
	StringBuffer body=new StringBuffer( getString() );
	//if (command==clear) body.setLength(0);
        if (command==sign) {
            if ( body.charAt(0)=='-' ) 
                body.deleteCharAt(0);
            else
                body.insert(0,'-');
            }
	setString(body.toString());
        body=null;
    }
//#endif
}
