/*
 * TestForm.java
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

import Colors.ColorsList;
import java.util.Vector;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.TextField;
import locale.SR;
import ui.MainBar;
import ui.VirtualElement;
import ui.VirtualList;

/**
 *
 * @author ad
 */
public class TestForm 
        extends DefForm {
    
    private Vector itemsList=new Vector();
    
    /**
     * Creates a new instance of TestForm
     */
    public TestForm(final Display display) {
        super(display, "test form");
        this.display=display;
        
        DropChoiceBox testNewChoiceBox1=new DropChoiceBox(display);
        testNewChoiceBox1.append("by socket");
        testNewChoiceBox1.append("1 byte");
        testNewChoiceBox1.append("<iq/>");
        testNewChoiceBox1.append("ping");
        testNewChoiceBox1.append("a b c d e f g h i j k l m n o p q r s t u v w x y z 0 1 2 3 4 5 6 7 8 9");
        testNewChoiceBox1.setSelectedIndex(4);
        itemsList.addElement(testNewChoiceBox1);
        
        TrackItem testTrackItem1=new TrackItem(3, 10);
        itemsList.addElement(testTrackItem1);
        
        TextInput testInputCombo1=new TextInput(display, "remember box", "text", "test", TextField.ANY);
        itemsList.addElement(testInputCombo1);
        
        MultiLine testMultiLine1=new MultiLine("test", "multi line text\nyo yo yo\n yep!", super.superWidth);
        itemsList.addElement(testMultiLine1);
        
        ImageItem testImageItem1=new ImageFileItem("/images/splash.png", "BM Splash");
        itemsList.addElement(testImageItem1);

        LinkString testLinkString0=new LinkString(SR.MS_COLOR_TUNE) { public void doAction() { new ColorsList(display); } };
        itemsList.addElement(testLinkString0);
        
        SimpleString testBoldString0=new SimpleString("test bold string", true);
        itemsList.addElement(testBoldString0);
        SimpleString testSimpleString0=new SimpleString("test string", false);
        itemsList.addElement(testSimpleString0);
        SimpleString testSimpleString1=new SimpleString("test checkBox", false);
        itemsList.addElement(testSimpleString1);        
        CheckBox testCheckBox1=new CheckBox("checkBox1", false);
        itemsList.addElement(testCheckBox1);
        
        SpacerItem testSpacerItem1=new SpacerItem(0);
        itemsList.addElement(testSpacerItem1);     
        
        SimpleString testSimpleString2=new SimpleString("test CheckBoxGroup", false);
        itemsList.addElement(testSimpleString2);   
        CheckBox testCheckBox2=new CheckBox("item1", true);
        itemsList.addElement(testCheckBox2);
        CheckBox testCheckBox3=new CheckBox("item2 a b c d e f g h i j k l m n o p q r s t u v w x y z 0 1 2 3 4 5 6 7 8 9", true);
        itemsList.addElement(testCheckBox3);
        
        SpacerItem testSpacerItem2=new SpacerItem(0);
        itemsList.addElement(testSpacerItem2);  

        TextInput testTextInput1=new TextInput(display, "test textInput", "text of input text", null, TextField.ANY);
        itemsList.addElement(testTextInput1);
        
        SpacerItem testSpacerItem3=new SpacerItem(0);
        itemsList.addElement(testSpacerItem3);  
        SpacerItem testSpacerItem4=new SpacerItem(0);
        itemsList.addElement(testSpacerItem4);
        
        SimpleString testSimpleString4=new SimpleString("test ChoiceGroup", false);
        itemsList.addElement(testSimpleString4);
        
        DropChoiceBox testChoiceBox1=new DropChoiceBox(display);
        testChoiceBox1.append("by socket");
        testChoiceBox1.append("1 byte");
        testChoiceBox1.append("<iq/>");
        testChoiceBox1.append("ping");
        testChoiceBox1.append("a b c d e f g h i j k l m n o p q r s t u v w x y z 0 1 2 3 4 5 6 7 8 9");
        testChoiceBox1.setSelectedIndex(4);
        itemsList.addElement(testChoiceBox1);

        SpacerItem testSpacerItem5=new SpacerItem(0);
        itemsList.addElement(testSpacerItem5);  

        PasswordInput testPasswordInput1=new PasswordInput(display, "test passwordItem", "pass");
        itemsList.addElement(testPasswordInput1);

        SpacerItem testSpacerItem6=new SpacerItem(0);
        itemsList.addElement(testSpacerItem6);
        
        moveCursorTo(getNextSelectableRef(-1));
        attachDisplay(display);
    }

    protected int getItemCount() { return itemsList.size(); }

    protected VirtualElement getItemRef(int index) {
        return (VirtualElement)itemsList.elementAt(index);
    }
}
