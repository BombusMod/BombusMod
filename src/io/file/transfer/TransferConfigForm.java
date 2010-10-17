/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package io.file.transfer;

import io.file.browse.Browser;
import io.file.browse.BrowserListener;
import javax.microedition.lcdui.TextField;
import locale.SR;
import ui.VirtualList;
import ui.controls.form.DefForm;
import ui.controls.form.LinkString;
import ui.controls.form.TextInput;


/**
 *
 * @author Vitaly
 */
public class TransferConfigForm extends DefForm implements BrowserListener {
    
    private TextInput streamhost;
    private TextInput port;
    private TextInput transferFolder;
    private LinkString selectFolder;
    
    private TransferConfig ft = TransferConfig.getInstance();

    public TransferConfigForm(VirtualList parentView) {
        super ("File transfer");        
        transferFolder = new TextInput(sd.canvas, "Save files to", ft.ftFolder, null, TextField.ANY);
        itemsList.addElement(transferFolder);
        selectFolder=new LinkString(SR.MS_SELECT) { public void doAction() { selectFolder(); } };
        itemsList.addElement(selectFolder);
        streamhost = new TextInput(sd.canvas, "SOCKS5 proxy", ft.ftProxy, "ft_proxyjid", 0);
        port = new TextInput(sd.canvas, "SOCKS5 port", Integer.toString(ft.ftProxyPort), "ft_proxyport", TextField.NUMERIC);
        itemsList.addElement(streamhost);
        itemsList.addElement(port);
        
        
    }

    public void cmdOk() {
        ft.ftFolder = transferFolder.getValue();
        ft.ftProxy = streamhost.getValue();
        ft.ftProxyPort = Integer.parseInt(port.getValue());
        ft.saveToStorage();
        destroyView();
    }
    
     public void selectFolder() {
        new Browser(null, this, true);
    }

    public void BrowserFilePathNotify(String pathSelected) {
        transferFolder.setValue(pathSelected);
    }
    
    

}
