/*
 * XDataField.java
 *
 * Created on 6 Май 2008 г., 0:34
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package xmpp.extensions;

import Client.StaticData;
import com.alsutton.jabber.JabberDataBlock;
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

/**
 *
 * @author root
 */
public class XDataField {

    private String name;
    private String label;
    private String type;
    private boolean required;
    public boolean instructions;
    public boolean hidden;
    public String body;
    private boolean registered;
    private boolean numericBoolean;
    public Object formItem;
    int formIndex = -1;
    int mediaIndex = -1;
    String mediaUri;
    VirtualElement media;
    public Vector optionsList = new Vector();

    private boolean multi;

    /** Creates a new instance of XDataField */
    public XDataField(JabberDataBlock field) {

        name = field.getTagName();
        label = name;
        body = field.getText();

        type = field.getTypeAttribute();

        name = field.getAttribute("var");

        label = field.getAttribute("label");
        if (label == null) {
            label = name;
        }

        body = field.getChildBlockText("value");

        required = field.getChildBlock("required") != null;
        if (required) {
            label = label + " *";
        }

        if (type == null) {
            media = extractMedia(field);
            formItem = new TextInput(StaticData.getInstance().canvas, label, body, null, TextField.ANY);
            return;
        }

        hidden = type.equals("hidden");

        if (type.equals("fixed")) {
            MultiLine item = new MultiLine(label, body, StaticData.getInstance().roster.getListWidth());
            item.selectable = true;
            formItem = item;
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
        } else if (type.equals("list-single") || type.equals("list-multi")) {
            
            multi = type.equals("list-multi");

            if (multi) {
                formItem = new ItemsGroup(label);
            } else {
                formItem = new DropChoiceBox(label);
            }
            
            for (Enumeration e = field.getChildBlocks().elements(); e.hasMoreElements();) {
                JabberDataBlock option = (JabberDataBlock) e.nextElement();

                if (option.getTagName().equals("option")) {

                    String opValue = option.getChildBlockText("value");

                    String opLabel = option.getAttribute("label");
                    if (opLabel == null) {
                        opLabel = opValue;
                    }                    
                    boolean set = body.equals(opValue);
                    if (formItem instanceof ItemsGroup) {
                        optionsList.addElement(opValue);
                        ((ItemsGroup)formItem).items.addElement(new CheckBox(opLabel, set));
                    }
                    if (formItem instanceof DropChoiceBox) {
                        ((DropChoiceBox)formItem).items.addElement(opLabel);
                        if (set) {
                            int index = ((DropChoiceBox)formItem).items.size() - 1;
                            ((DropChoiceBox)formItem).setSelectedIndex(index);
                        }

                    }
                }
            }
        } else { // text-single, text-private
            if (body.length() >= 200) {
                body = body.substring(0, 198);
            }
            if (type.equals("text-private")) { // password field
                formItem = new PasswordInput(StaticData.getInstance().canvas, label, body);
            } else {                
                formItem = new TextInput(StaticData.getInstance().canvas, label, body, "", TextField.ANY);
            }
        }



        if (name != null) {
            if (name.equals("key")) {
                hidden = true;
            }
        }
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

    JabberDataBlock constructJabberDataBlock() {

        JabberDataBlock j = new JabberDataBlock("field", null, null);
        j.setAttribute("var", name);
        if (type != null) {
            j.setAttribute("type", type);
        }

        if (formItem instanceof TextInput) {
            String value = ((TextInput) formItem).getValue();

            j.addChild("value", value);
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
                j.addChild("value", (String) item.items.elementAt(index));

            }
        }

        return j;
    }
}
