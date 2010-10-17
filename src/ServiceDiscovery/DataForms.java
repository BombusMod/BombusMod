/*
 * FormField.java
 *
 * Created on 5.06.2005, 20:30
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
package ServiceDiscovery;

import Client.StaticData;
import com.alsutton.jabber.*;
import java.util.*;
import javax.microedition.lcdui.TextField;
import ui.controls.form.CheckBox;
import ui.controls.form.DropChoiceBox;
import ui.controls.form.MultiLine;
import ui.controls.form.SimpleString;
import ui.controls.form.TextInput;

/**
 *
 * @author Evg_S
 */
public class DataForms {

    public static Vector getItems(Vector fields) {
        Vector formItems = new Vector();

        JabberDataBlock currentItem;
        FormField currentField;

        for (int i = 0; i < fields.size(); i++) {
            currentItem = ((JabberDataBlock) fields.elementAt(i));
            currentField = new FormField();
            currentField.name = currentField.label = currentItem.getTagName();
            
            String body = currentItem.getText();
            if (currentField.name.equals("field")) {
                // x:data
                currentField.type = currentItem.getTypeAttribute();
                currentField.name = currentItem.getAttribute("var");
                currentField.label = currentItem.getAttribute("label");
                if (currentField.label == null) {
                    currentField.label = currentField.name;
                }
                body = currentItem.getChildBlockText("value");
                currentField.hidden = currentField.type.equals("hidden");
                if (currentField.type.equals("fixed")) {
                    formItems.addElement(new MultiLine(currentField.label, body, StaticData.getInstance().roster.getListWidth()));
                } else if (currentField.type.equals("boolean")) {
                    boolean set = false;
                    if (body.equals("1")) {
                        set = true;
                    }
                    if (body.equals("true")) {
                        set = true;
                    }

                    CheckBox ch = new CheckBox(currentField.label, set);
                    formItems.addElement(ch);
                    currentField.numericBoolean = body.length() == 1;
                } else if (currentField.type.equals("list-single")) {
                    DropChoiceBox ch = new DropChoiceBox(currentField.label);
                    formItems.addElement(ch);

                    currentField.optionsList = null;
                    currentField.optionsList = new Vector();
                    for (Enumeration e = currentItem.getChildBlocks().elements(); e.hasMoreElements();) {
                        JabberDataBlock option = (JabberDataBlock) e.nextElement();
                        int sel = 0;
                        if (option.getTagName().equals("option")) {
                            String value = option.getChildBlockText("value");
                            String listLabel = option.getAttribute("label");
                            if (listLabel == null) {
                                listLabel = value;
                            }
                            currentField.optionsList.addElement(value);
                            ch.add(listLabel);
                            sel++;
                            if (body.equals(value)) {
                                ch.setSelectedIndex(sel);
                            }
                        }
                    }
                } else if (currentField.type.equals("list-multi")) {
                    formItems.addElement(new SimpleString(currentField.label, true));

                    currentField.optionsList = new Vector();
                    for (Enumeration e = currentItem.getChildBlocks().elements(); e.hasMoreElements();) {
                        JabberDataBlock option = (JabberDataBlock) e.nextElement();
                        int sel = 0;
                        if (option.getTagName().equals("option")) {
                            String value = option.getChildBlockText("value");
                            String lstLabel = option.getAttribute("label");
                            if (lstLabel == null) {
                                lstLabel = value;
                            }
                            currentField.optionsList.addElement(value);
                            sel++;
                            boolean check = currentItem.getChildBlockByText(value) != null;
                            CheckBox ch = new CheckBox(lstLabel, check);
                            formItems.addElement(ch);
                        }
                    }
                } // text-single, text-private
                else {
                    /*if (body.length()>=200) {
                    body=body.substring(0,198);
                    }*/
                    int constrains = (currentField.type.equals("text-private")) ? TextField.PASSWORD : TextField.ANY;
                    formItems.addElement(new TextInput(StaticData.getInstance().canvas, label, body, "", constrains));
                }
            } else {
                // not x-data
                if (currentField.instructions = currentField.name.equals("instructions")) {
                    formItems.addElement(new MultiLine("Instructions", body, StaticData.getInstance().roster.getListWidth()));
                } else if (currentField.name.equals("title")) {
                    formItems.addElement(new MultiLine(null, body, StaticData.getInstance().roster.getListWidth()));
                } else if (currentField.name.equals("registered")) {
                    formItems.addElement(new CheckBox("Remove registration", false));
                    currentField.registered = true;
                } else {
                    formItems.addElement(new TextInput(StaticData.getInstance().canvas, label, body, "", 0));
                }
            }

            if (currentField.name != null) {
                if (currentField.name.equals("key")) {
                    currentField.hidden = true;
                }
            }
        }
        return formItems;
    }

    public static JabberDataBlock constructJabberDataBlock(Vector formItems) {
        JabberDataBlock j = null;
        for (Enumeration e = formItems.elements(); e.hasMoreElements();) {
            Object item = e.nextElement();

            if (item instanceof TextInput) {
                String value = ((TextInput) item).getValue();
                if (type == null) {
                    j = new JabberDataBlock(null, name, value);
                } else {
                    // x:data
                    j = new JabberDataBlock("field", null, null);
                    j.setAttribute("var", name);
                    j.setAttribute("type", type);
                    j.addChild("value", value);
                }
            }
            if (item instanceof CheckBox) {
                if (registered) {
                    boolean unregister = ((CheckBox) item).getValue();
                    if (unregister) {
                        return new JabberDataBlock("remove", null, null);
                    }
                    return null;
                }

                //only x:data
                j = new JabberDataBlock("field", null, null);
                j.setAttribute("var", name);
                j.setAttribute("type", type);
                if (optionsList == null) {
                    boolean set = ((CheckBox) item).getValue();
                    String result = String.valueOf(set);
                    if (numericBoolean) {
                        result = set ? "1" : "0";
                    }
                    j.addChild("value", result);
                } else if (type.equals("list-multi")) {
                    int sel = 0; // skip label
                    for (Enumeration e2 = formItems.elements(); e2.hasMoreElements();) {
                        item = e2.nextElement();
                        if (item instanceof CheckBox && ((CheckBox) item).getValue()) {
                            j.addChild("value", (String) optionsList.elementAt(sel - 1));
                        }
                        sel++;
                    }
                } else /* list-single */ {
                    int index = ((DropChoiceBox) item).getSelectedIndex();
                    if (index >= 0) {
                        j.addChild("value", (String) optionsList.elementAt(index));
                    }
                }
            }
        }
        return j;
    }
       static class FormField {
        String label;
        String type;
        String name;
        boolean hidden;
        //TODO: boolean required;
        boolean instructions;
        Vector optionsList;
        boolean numericBoolean;
        boolean registered;
    }
    
}
