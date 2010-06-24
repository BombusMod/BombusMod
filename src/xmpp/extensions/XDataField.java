/*
 * XDataField.java
 *
 * Created on 6 Май 2008 г., 0:34
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package xmpp.extensions;

import com.alsutton.jabber.JabberDataBlock;
import java.util.*;
import javax.microedition.lcdui.*;
import util.Strconv;

/**
 *
 * @author root
 */
public class XDataField {

    private String name;        
    private String label;        
    private String type;
    public String value;
    private boolean required;   
    boolean hidden;

    Item formItem;
    int formIndex=-1;
    int mediaIndex=-1;
    String mediaUri;
    
    Item media;
    
    public Vector optionsList;
    
    
    /** Creates a new instance of XDataField */
    public XDataField(JabberDataBlock field) {

        type=field.getAttribute("type");
        
        name=field.getAttribute("var");
        
        label=field.getAttribute("label");
        if (label==null) label=name;
        
        value=field.getChildBlockText("value");
        
        required = field.getChildBlock("required")!=null;
        if (required) label = label+" *";
        
        if (type==null) {
            media=extractMedia(field);
            formItem=new TextField(label, value, 200,  TextField.ANY);
            return;
        }
        
        hidden = type.equals("hidden"); 
        
        if (type.equals("fixed")) {
            formItem=new StringItem(label, value);
        } else if (type.equals("boolean")) {
            ChoiceGroup ch=new ChoiceGroup(null,ChoiceGroup.MULTIPLE);
            formItem=ch;
            ch.append(label, null);
            boolean set=false;
            
            if (value.equals("1")) set=true;
            if (value.equals("true")) set=true;
                ch.setSelectedIndex(0, set);
        } else if (type.equals("list-single") || type.equals("list-multi")) {
            int choiceType = (type.equals("list-single"))
                    ? Choice.POPUP : ChoiceGroup.MULTIPLE;
            
            ChoiceGroup ch=new ChoiceGroup(label, choiceType);
            formItem=ch;
            
            optionsList=null;
            optionsList=new Vector();
            for (Enumeration e=field.getChildBlocks().elements(); e.hasMoreElements();) {
                JabberDataBlock option=(JabberDataBlock)e.nextElement();
                
                if (option.getTagName().equals("option")) {
                    
                    String opValue=option.getChildBlockText("value");
                    
                    String opLabel=option.getAttribute("label");
                    if (opLabel==null) opLabel=opValue;
                        
                        optionsList.addElement(opValue);
                        int index=ch.append(opLabel, null);
                        if (value.equals(opValue)) ch.setSelectedIndex(index, true);
                    }
                }
            } else { // text-single, text-private
            if (value.length()>=200) {
                value=value.substring(0,198);
            }
            int constrains=(type.equals("text-private"))? TextField.PASSWORD: TextField.ANY;
            formItem=new TextField(label, value, 200, constrains);
        }
    }

    private Item extractMedia(JabberDataBlock field) {
        try {
            JabberDataBlock m=field.findNamespace("media", "urn:xmpp:media-element");
        if (m==null) return null;

            for (Enumeration e=m.getChildBlocks().elements(); e.hasMoreElements(); ) {
                JabberDataBlock u=(JabberDataBlock) e.nextElement();
                if (u.getTagName().equals("uri")) {
                    if (!u.getTypeAttribute().startsWith("image")) continue;
                    mediaUri=u.getText();
                    return new StringItem(null, "[Loading Image]");
                }
            }
        
        } catch (Exception e) {}
        return null;
    }
    
    JabberDataBlock constructJabberDataBlock(){

        JabberDataBlock j=new JabberDataBlock("field", null, null);
        j.setAttribute("var", name);
        if (type!=null) j.setAttribute("type", type);
        
        if (formItem instanceof TextField) {
            String value=((TextField)formItem).getString();
            
            j.addChild("value", value);
        }
        
        if (formItem instanceof ChoiceGroup) {
            //only x:data
            if (type.equals("boolean")) {
                boolean set=((ChoiceGroup)formItem).isSelected(0);
                String result=String.valueOf(set);
                if (value.length()==1) result=set?"1":"0";
                
                j.addChild("value", result);
            } else if (type.equals("list-multi")) {
                ChoiceGroup ch=(ChoiceGroup) formItem;
                int count=ch.size();
                for (int i=0; i<count; i++) {
                    if (ch.isSelected(i))  
                        j.addChild("value", (String)optionsList.elementAt(i));                    
                }
            } else /* list-single */ {
                int index=((ChoiceGroup) formItem).getSelectedIndex();
                if (index>=0)  j.addChild("value", (String)optionsList.elementAt(index));
            }
        }
        return j;
    }
    
}
