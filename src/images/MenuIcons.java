/*
 * MenuIcons.java
 *
 * Created on 29 јпрель 2008 г., 18:31
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package images;

import javax.microedition.lcdui.Graphics;
import ui.ImageList;

/**
 *
 * @author ad
 */
public class MenuIcons extends ImageList{
    
    private static MenuIcons instance;

    public static MenuIcons getInstance() {
	if (instance==null)
            instance=new MenuIcons();
	return instance;
    }

    private final static int ICONS_IN_ROW=8;
    private final static int ICONS_IN_COL=4;

    /** Creates a new instance of RosterIcons */
    private MenuIcons() {
	super("/images/menu.png", ICONS_IN_COL, ICONS_IN_ROW);
    }

    public void drawImage(Graphics g, int index, int x, int y) {
        super.drawImage(g, index, x, y);
    }

    public static final int ICON_URL = 0x15;
    //public static Integer iconHasVcard=new Integer(ICON_SEARCH_INDEX);
}
