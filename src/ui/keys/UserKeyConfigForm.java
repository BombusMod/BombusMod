/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ui.keys;

//#if FILE_IO
import io.file.browse.Browser;
import io.file.browse.BrowserListener;
//#endif
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import ui.controls.form.DefForm;
import ui.controls.form.LinkString;
import ui.controls.form.DropChoiceBox;
import ui.controls.form.SpacerItem;
import locale.SR;
import java.util.Vector;
import util.StringLoader;

/**
 *
 * @author Totktonada
 */
public class UserKeyConfigForm extends DefForm
//#if FILE_IO
        implements BrowserListener
//#endif
    {

    private Vector[] files;
    private DropChoiceBox schemes;
    private LinkString useFromJar;
    private LinkString manualChange;
//#if FILE_IO
    private LinkString loadFromFile;
    private LinkString saveToFile;

    String filePath = "/userkeys/res.txt";
    private int loadType = 0;
//#endif

    public UserKeyConfigForm() {
        super(SR.MS_CUSTOM_KEYS);

        try {
            files = new StringLoader().stringLoader(filePath, 2);
            int filesCount = files[0].size();
            if (filesCount > 0) {
                schemes = new DropChoiceBox("Schemes");
                for (int i = 0; i < filesCount; i++)
                    schemes.add((String) files[1].elementAt(i));
                schemes.setSelectedIndex(0);
                itemsList.addElement(schemes);

                useFromJar = new LinkString(SR.MS_LOAD_SKIN) { public void doAction() { applyScheme(); } };
                itemsList.addElement(useFromJar);
                itemsList.addElement(new SpacerItem(10));
            }
        } catch (Exception e) { }

//#if FILE_IO
        loadFromFile = new LinkString(SR.MS_LOAD_FROM_FILE) { public void doAction() { initBrowser(1); } };
        itemsList.addElement(loadFromFile);
        saveToFile = new LinkString(SR.MS_SAVE_TO_FILE) { public void doAction() { initBrowser(0); } };
        itemsList.addElement(saveToFile);
        itemsList.addElement(new SpacerItem(10));
//#endif

        manualChange = new LinkString("ManualChange") { public void doAction() { new UserKeysList(); } };
        itemsList.addElement(manualChange);
    }

    private void applyScheme() {
        String fileName = (String) files[0].elementAt(schemes.getSelectedIndex());
        UserKeyExec.getInstance().loadFromInputStream(fileName, false);
    }

    public void cmdOk() {
        destroyView();
    }

//#if FILE_IO
    public void initBrowser(int type) {
        loadType = type;
        if (type == 0) {
            new Browser(null, this, true);
        } else if(type == 1) {
            new Browser(null, this, false);
        }
    }

    public void BrowserFilePathNotify(String pathSelected) {
        if (loadType == 0) {
            UserKeyExec.getInstance().writeToFile(pathSelected);
        } else {
            UserKeyExec.getInstance().loadFromInputStream(pathSelected, true);
        }
    }
//#endif
}
