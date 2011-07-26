/*
 * ColorConfigForm.java
 *
 * Created on 25.05.2008, 14:05
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

package Colors;
//#if FILE_IO
import io.file.FileIO;
import io.file.browse.Browser;
import io.file.browse.BrowserListener;
//#endif
import Client.StaticData;
import java.util.Vector;
import locale.SR;
import ui.VirtualList;
import ui.controls.form.DropChoiceBox;
import ui.controls.form.DefForm;
import ui.controls.form.LinkString;
import ui.controls.form.SpacerItem;
import util.StringLoader;

/**
 *
 * @author ad
 */
public class ColorConfigForm 
        extends DefForm {

//#ifdef COLOR_TUNE
//#     private LinkString configureColors;
//#endif
    private LinkString invertColors;
    
    private Vector[] files;
    private DropChoiceBox skinFiles;
    private LinkString useFromJar;
    
    private LinkString reset;

    /** Creates a new instance of ColorConfigForm
     */
    public ColorConfigForm() {
        super(SR.MS_COLOR_TUNE);        
//#ifdef COLOR_TUNE
//#         configureColors=new LinkString(SR.MS_COLOR_TUNE) { public void doAction() { new ColorsList(); } };
//#         itemsList.addElement(configureColors);
//#endif
        invertColors=new LinkString(SR.MS_INVERT) { public void doAction() { ColorTheme.invertSkin(); } };
        itemsList.addElement(invertColors);
        reset=new LinkString(SR.MS_CLEAR) { public void doAction() { ColorTheme.init(); ColorTheme.saveToStorage(); } };
        itemsList.addElement(reset);

        itemsList.addElement(new SpacerItem(10));
        try {
            files=new StringLoader().stringLoader("/skins/res.txt",2);
            int j=files[0].size();
            if (j>0) {
                skinFiles=new DropChoiceBox(SR.MS_SELECT);
                for (int i=0; i<j; i++) {
                    skinFiles.add((String)files[1].elementAt(i));
                }
                skinFiles.setSelectedIndex(0);
                itemsList.addElement(skinFiles);
                useFromJar=new LinkString(SR.MS_LOAD_SKIN) { public void doAction() { userThemeFromJar(); } };
                itemsList.addElement(useFromJar);
            }
        } catch (Exception e) {}
        
        moveCursorTo(getNextSelectableRef(-1));
    }

    public void cmdOk() {
        destroyView();
    }

    public void userThemeFromJar() {
        try {
            if (skinFiles.getSelectedIndex()>-1) {
                ColorTheme.loadSkin((String)files[0].elementAt(skinFiles.getSelectedIndex()), 1);
            }
        } catch (Exception ex) {}
    }
}

