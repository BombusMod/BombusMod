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
import ui.VirtualElement;
import ui.controls.form.CheckBox;
import ui.controls.form.DropChoiceBox;
import ui.controls.form.ImageItem;
import ui.controls.form.ItemsGroup;
import ui.controls.form.MultiLine;
import ui.controls.form.PasswordInput;
import ui.controls.form.TextInput;
import util.Strconv;

/**
 *
 * @author Evg_S
 */
public class FormField {

    public String label;
    public String type;
    public String name;
    public Object formItem;
    boolean hidden;
    private boolean required;
    public boolean instructions;
    public Vector optionsList;
    public String body;
    private boolean numericBoolean;
    private boolean registered;
    int formIndex = -1;
    int mediaIndex = -1;
    String mediaUri;
    VirtualElement media;

    /** Creates a new instance of FormField */
    public FormField(JabberDataBlock field) {
        name = field.getTagName();
        label = name;
        body = field.getText();
        if (name.equals("field")) {
            // x:data
            type = field.getTypeAttribute();
            name = field.getAttribute("var");
            label = field.getAttribute("label");
            if (label == null) {
                label = name;
            }
            body = field.getChildBlockText("value");
            if (type == null) {
                media = extractMedia(field);
                formItem = new TextInput(label, body, null, TextField.ANY);
                return;
            }

            required = field.getChildBlock("required") != null;
            if (required) {
                label = label + " *";
            }

            hidden = type.equals("hidden");

            if (type.equals("fixed")) {
                formItem = new MultiLine(label, body);
            } else if (type.equals("boolean")) {
                boolean set = false;
                if (body.equals("1")) {
                    set = true;
                }
                if (body.equals("true")) {
                    set = true;
                }
                numericBoolean = body.length() == 1;
                CheckBox ch = new CheckBox(label, set);
                formItem = ch;
            } else if (type.equals("list-single")) {
                DropChoiceBox ch = new DropChoiceBox(label);

                optionsList = null;
                optionsList = new Vector();
                for (Enumeration e = field.getChildBlocks().elements(); e.hasMoreElements();) {
                    JabberDataBlock option = (JabberDataBlock) e.nextElement();
                    if (option.getTagName().equals("option")) {
                        String value = option.getChildBlockText("value");
                        String label = option.getAttribute("label");
                        if (label == null) {
                            label = value;
                        }
                        optionsList.addElement(value);
                        ch.items.addElement(label);
                        if (body.equals(value)) {
                            int index = ch.items.size() - 1;
                            ((DropChoiceBox) ch).setSelectedIndex(index);
                        }

                    }
                }
                formItem = ch;
            } else if (type.equals("list-multi")) {
                ItemsGroup ch = new ItemsGroup(label);

                optionsList = new Vector();
                for (Enumeration e = field.getChildBlocks().elements(); e.hasMoreElements();) {
                    JabberDataBlock option = (JabberDataBlock) e.nextElement();
                    if (option.getTagName().equals("option")) {
                        String value = option.getChildBlockText("value");
                        String label = option.getAttribute("label");
                        if (label == null) {
                            label = value;
                        }
                        optionsList.addElement(value);
                        boolean check = field.getChildBlockByText(value) != null;
                        ch.items.addElement(new CheckBox(label, check));
                    }
                }
                formItem = ch;
            } else if (type.equals("jid-multi")) {
		StringBuffer jids = new StringBuffer();
		Vector values = field.getChildBlocks();
		if (values != null) {
		    int size = values.size();
		    for (int i = 0; i < size; i++) {
			jids.append(((JabberDataBlock) values.elementAt(i)).getText()).append('\n');
		    }
		}
		formItem = new TextInput(label, jids.toString().trim(), "", TextField.ANY);
	    } // text-single, text-private
            else {
                /* if (body.length()>=200) {
                body=body.substring(0,198);
                }*/
                if (type.equals("text-private")) { // password field
                    formItem = new PasswordInput(label, body);
                } else {
                    formItem = new TextInput(label, body, "", TextField.ANY);
                }
            }
        } else {
            // not x-data
            if (instructions = name.equals("instructions")) {
                formItem = new MultiLine("Instructions", body);
            } else if (name.equals("title")) {
                formItem = new MultiLine(null, body);
            } else if (name.equals("registered")) {
                CheckBox cg = new CheckBox("Remove registration", false);
                formItem = cg;
                registered = true;
            } else {
                formItem = new TextInput(label, body, "", TextField.ANY);
            }
        }

        if (name != null) {
            if (name.equals("key")) {
                hidden = true;
            }
        }
    }

    JabberDataBlock constructJabberDataBlock() {
	if (formItem instanceof MultiLine) return null;
        JabberDataBlock j = null;
        if (formItem instanceof TextInput) {
            if (media == null) {
                String value = ((TextInput) formItem).getValue();
                if (type == null) {
                    j = new JabberDataBlock(null, name, value);
                    return j;
                }
            }
        }
        j = new JabberDataBlock("field", null, null);
        j.setAttribute("var", name);
        if (type != null) {
            j.setAttribute("type", type);
        }

        if (formItem instanceof TextInput) {
	    String value = ((TextInput) formItem).getValue();
	    if (type != null) {
		if (type.equals("jid-multi")) {
		    String jids[] = Strconv.split(value, '\n');
		    for (int i = 0; i < jids.length; i++) {
			j.addChild("value", jids[i]);
		    }
		} else {
		    j.addChild("value", value);
		}
	    } else {
		j.addChild("value", value);
	    }

	}


        if (formItem instanceof CheckBox) {
            if (registered) {
                boolean unregister = ((CheckBox) formItem).getValue();
                if (unregister) {
                    return new JabberDataBlock("remove", null, null);
                }
                return null;
            }

            //only x:data
            if (type.equals("boolean")) {
                boolean set = ((CheckBox) formItem).getValue();
                String result = String.valueOf(set);
                if (numericBoolean) {
                    result = set ? "1" : "0";
                }

                j.addChild("value", result);
            }
        } else if (type != null) {
            if (type.equals("list-multi")) {
                ItemsGroup ch = (ItemsGroup) formItem;
                int count = ch.items.size();
                for (int i = 0; i < count; i++) {
                    if (((CheckBox) ch.items.elementAt(i)).getValue()) {
                        j.addChild("value", (String) optionsList.elementAt(i));
                    }
                }
            } else if (type.equals("list-single")) {
                DropChoiceBox item = (DropChoiceBox) formItem;
                int index = item.getValue();
                j.addChild("value", (String) optionsList.elementAt(index));

            }
        }
        return j;
    }

    private VirtualElement extractMedia(JabberDataBlock field) {
        // XEP-0221
        try {
            JabberDataBlock m = field.findNamespace("media", "urn:xmpp:media-element");
            if (m == null) {
                return null;
            }

            for (Enumeration e = m.getChildBlocks().elements(); e.hasMoreElements();) {
                JabberDataBlock u = (JabberDataBlock) e.nextElement();
                if (u.getTagName().equals("uri")) {
                    if (!u.getTypeAttribute().startsWith("image")) {
                        continue;
                    }
                    mediaUri = u.getText();
                    return new ImageItem(null, "[Loading Image]");
                }
            }

        } catch (Exception e) {
        }
        return null;
    }
}
