/*
 * XDataForm.java
 *
 * Created on 6 Май 2008 г., 0:28
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package xmpp.extensions;

import com.alsutton.jabber.JabberDataBlock;
import java.util.*;
import javax.microedition.lcdui.*;
import locale.SR;

/**
 *
 * @author root
 */
public class XDataForm implements CommandListener {
    
    public interface NotifyListener {
        void XDataFormSubmit(JabberDataBlock form);
    }
    
    private Display display;
    private Displayable parentView;
    private NotifyListener notifyListener;
    
    private Command cmdOk=new Command(SR.MS_SEND, Command.OK /*Command.SCREEN*/, 1);
    private Command cmdCancel=new Command(SR.MS_BACK, Command.BACK, 99);
    
    Vector items;
    /** Creates a new instance of XDataForm */
    public XDataForm(Display display, JabberDataBlock form, NotifyListener notifyListener) {
        this.display=display;
        this.parentView=display.getCurrent();
        this.notifyListener=notifyListener;

        String title=form.getChildBlockText("title");
        Form f=new Form(title);

        items=new Vector();
        
        for (Enumeration e=form.getChildBlocks().elements(); e.hasMoreElements(); ) {
            
            JabberDataBlock ch=(JabberDataBlock)e.nextElement();
            
            if (ch.getTagName().equals("instructions")) {
                f.append(ch.getText());
                f.append("\n");
                continue;
            };
            
            if (!ch.getTagName().equals("field")) continue;
            
            XDataField field=new XDataField(ch);
            
            items.addElement(field);
            
            if (field.hidden) continue;
            
            if (field.media!=null) f.append(field.media);
            f.append(field.formItem);
        }
        
        f.setCommandListener(this);
        f.addCommand(cmdOk);
        f.addCommand(cmdCancel);
        display.setCurrent(f);
    }

    public void commandAction(Command command, Displayable displayable) {
        if (command==cmdOk) {
            JabberDataBlock resultForm=new JabberDataBlock("x", null, null);
            resultForm.setNameSpace("jabber:x:data");
            resultForm.setTypeAttribute("submit");
            
            for (Enumeration e=items.elements(); e.hasMoreElements(); ) {
                JabberDataBlock ch=((XDataField)e.nextElement()).constructJabberDataBlock();
                if (ch!=null) resultForm.addChild(ch);
            }
            notifyListener.XDataFormSubmit(resultForm);
        }
        display.setCurrent(parentView);
    }
    
}
