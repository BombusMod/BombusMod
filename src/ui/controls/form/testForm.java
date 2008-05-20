/*
 * testForm.java
 *
 * Created on 19.05.2008, 22:22
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

package ui.controls.form;

import java.util.Vector;
import javax.microedition.lcdui.Display;
import ui.MainBar;
import ui.VirtualElement;
import ui.VirtualList;

/**
 *
 * @author ad
 */
public class testForm 
        extends VirtualList {
    
    private Vector itemsList=new Vector();
    
    /** Creates a new instance of testForm */
    public testForm(Display display) {
        super();
        //this.display=display;
        setMainBarItem(new MainBar("test form"));
        
        simpleString testSimpleString0=new simpleString("test string");
        itemsList.addElement(testSimpleString0);
        simpleString testSimpleString1=new simpleString("test checkBox");
        itemsList.addElement(testSimpleString1);        
        checkBox testCheckBox1=new checkBox("checkBox1", false);
        itemsList.addElement(testCheckBox1);
        
        spacerItem testSpacerItem1=new spacerItem();
        itemsList.addElement(testSpacerItem1);     
        
        simpleString testSimpleString2=new simpleString("test CheckBoxGroup");
        itemsList.addElement(testSimpleString2);   
        checkBox testCheckBox2=new checkBox("item1", true);
        itemsList.addElement(testCheckBox2);
        checkBox testCheckBox3=new checkBox("item2", true);
        itemsList.addElement(testCheckBox3);
        
        spacerItem testSpacerItem2=new spacerItem();
        itemsList.addElement(testSpacerItem2);  

        simpleString testSimpleString3=new simpleString("test textInput");
        itemsList.addElement(testSimpleString3);
        textInput testTextInput1=new textInput(display, "text of input text");
        itemsList.addElement(testTextInput1);
        
        spacerItem testSpacerItem3=new spacerItem();
        itemsList.addElement(testSpacerItem3);  
        spacerItem testSpacerItem4=new spacerItem();
        itemsList.addElement(testSpacerItem4);
        
        simpleString testSimpleString4=new simpleString("test ChoiceGroup");
        itemsList.addElement(testSimpleString4);
        
        choiceBox testChoiceBox1=new choiceBox(/*, "KeepAlive:"*/);
        testChoiceBox1.append("by socket");
        testChoiceBox1.append("1 byte");
        testChoiceBox1.append("<iq/>");
        testChoiceBox1.append("ping");
        testChoiceBox1.append("a b c d e f g h i j k l m n o p q r s t u v w x y z");
        testChoiceBox1.setSelectedIndex(4);
        itemsList.addElement(testChoiceBox1);

        spacerItem testSpacerItem5=new spacerItem();
        itemsList.addElement(testSpacerItem5);  

        simpleString testSimpleString5=new simpleString("test passwordItem");
        itemsList.addElement(testSimpleString5);
        passwordInput testPasswordInput1=new passwordInput(display, "pass");
        itemsList.addElement(testPasswordInput1);

        spacerItem testSpacerItem6=new spacerItem();
        itemsList.addElement(testSpacerItem6);
        
        moveCursorTo(getNextSelectableRef(-1));
        attachDisplay(display);
    }

    protected int getItemCount() { return itemsList.size(); }

    protected VirtualElement getItemRef(int index) {
        return (VirtualElement)itemsList.elementAt(index);
    }
}
