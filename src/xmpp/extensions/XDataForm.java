/*
 * XDataForm.java
 *
 * Created on 6 Май 2008 г., 0:28
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package xmpp.extensions;

import Menu.MenuCommand;
import com.alsutton.jabber.JabberDataBlock;
import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.lcdui.Image;
import locale.SR;
import ui.MainBar;
import ui.VirtualList;
import ui.controls.form.ComplexForm;
import ui.controls.form.ImageItem;
import ui.controls.form.MultiLine;
import util.Strconv;

/**
 *
 * @author root
 */

public class XDataForm extends ComplexForm {

    public final static String NS_XDATA = "jabber:x:data";

    public NotifyListener listener;

    private Vector xDataItems;

    MenuCommand cmdSend = new MenuCommand(SR.MS_SEND, MenuCommand.OK, 1);

    public XDataForm(JabberDataBlock form, NotifyListener listener) {
        super(null);
        this.listener = listener;
        xDataItems = new Vector();
        parse(form);
        for (Enumeration e = xDataItems.elements(); e.hasMoreElements();) {
            XDataField item = (XDataField)e.nextElement();
            if (!item.hidden)
                itemsList.addElement(item.formItem);
        }
    }
            
    final void parse(JabberDataBlock form) {

        for (Enumeration e = form.getChildBlocks().elements(); e.hasMoreElements();) {

            JabberDataBlock ch = (JabberDataBlock) e.nextElement();

            String tagName = ch.getTagName();

            if (tagName.equals("title"))
                setMainBarItem(new MainBar(ch.getText()));
            if (tagName.equals("instructions"))
                itemsList.addElement(new MultiLine("Instructions", ch.getText(), sd.roster.getListWidth()));

            if (!tagName.equals("field")) {
                continue;
            }

            XDataField field = new XDataField(ch);
            ch = null;

            xDataItems.addElement(field);

            if (field.hidden) {
                continue;
            }

            /*   if (field.media != null)
            field.mediaIndex = f.append(field.media);
            field.formIndex=f.append(field.formItem);*/
        }        
    }
    
    public void fetchMediaElements(Vector bobCache) {
        //TODO: fetch external http bobs and non-cached in-band bobs
        byte [] bytes = null;
        Image img = null;
        String cid = null;
        XDataField field = null;
        JabberDataBlock data = null;
        int formItems = xDataItems.size();
        for (int i=0; i<formItems; i++) {
            field=(XDataField)xDataItems.elementAt(i);
            if (field.mediaUri==null) continue;
            if (!(field.media instanceof ImageItem)) continue;

            if (field.mediaUri.startsWith("cid:")) {
                cid = field.mediaUri.substring(4);
                if (bobCache==null) continue; //TODO: in-band bob request

                for (int bob=0; bob<bobCache.size(); bob++) {
                    data = (JabberDataBlock) bobCache.elementAt(bob);
                    if (data.isJabberNameSpace("urn:xmpp:bob") && cid.equals(data.getAttribute("cid"))) {
                        bytes = Strconv.fromBase64(data.getText());
                        img = Image.createImage(bytes, 0, bytes.length);
                        if (field.media != null) {
                            ((ImageItem)field.media).img = img;
                            itemsList.addElement(field.media);
                        }                        
                    }
                }
            }
        }
    }

    public final JabberDataBlock construct(Vector items) {
        JabberDataBlock resultForm = new JabberDataBlock("x", null, null);
        resultForm.setNameSpace(NS_XDATA);
        resultForm.setTypeAttribute("submit");

        for (Enumeration e = items.elements(); e.hasMoreElements();) {
            JabberDataBlock ch = ((XDataField) e.nextElement()).constructJabberDataBlock();
            if (ch != null) {
                resultForm.addChild(ch);
            }
            ch = null;
        }
        return resultForm;
    }
    
    public interface NotifyListener {
        public void XDataFormSubmit(JabberDataBlock form);
    }
    
    public void cmdOk() {
        JabberDataBlock submitForm = construct(xDataItems);
        listener.XDataFormSubmit(submitForm);
        destroyView();
    }    

    // TODO: fix this shit
    public void commandState() {
        menuCommands.removeAllElements();
        addMenuCommand(cmdSend);
    }

    public void menuAction(MenuCommand c, VirtualList v) {
        if (c == cmdSend) {
            JabberDataBlock submitForm = construct(xDataItems);
            listener.XDataFormSubmit(submitForm);
            destroyView();
        } else {
            super.menuAction(c, v);
        }
    }
}
