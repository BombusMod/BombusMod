/*
 * DefForm.java
 *
 * Created on 21.05.2008, 9:40
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
package ui.controls.form;

//import Client.Config;
import Client.Config;
import java.util.Vector;
import Menu.MenuListener;
import Menu.MenuCommand;
import Menu.MyMenu;
import images.RosterIcons;
import locale.SR;
import ui.MainBar;
import ui.VirtualCanvas;
import ui.VirtualElement;
import ui.VirtualList;

/**
 *
 * @author ad
 */
public class DefForm
        extends VirtualList
        implements MenuListener {

    public final Vector itemsList = new Vector();
    public static MenuCommand cmdOk = new MenuCommand(SR.MS_OK, MenuCommand.OK, 1, RosterIcons.ICON_APPEARING_INDEX);
    public static MenuCommand cmdCancel = new MenuCommand(SR.MS_BACK, MenuCommand.BACK, 99, RosterIcons.ICON_RESUME);

    /**
     * Creates a new instance of DefForm
     * @param caption
     */
    public DefForm(String caption) {
        this(caption, true);
    }

    public DefForm(String caption, boolean show) {
        mainbar = new MainBar(caption);

        enableListWrapping(false);
        if (show) {
            show();
        }
    }

    protected int getItemCount() {
        return itemsList.size();
    }

    public VirtualElement getItemRef(int index) {
        if (index >= getItemCount()) {
            return null;
        }
        return (VirtualElement) itemsList.elementAt(index);
    }

    public void menuAction(MenuCommand command, VirtualList displayable) {
        if (command == cmdCancel) {
            cmdCancel();
        }
        if (command == cmdOk) {
            cmdOk();
        }
    }

    public final void loadItemsFrom(Vector items) {
        synchronized (itemsList) {
            if (items == null) {
                return;
            }
            int count = items.size();
            itemsList.removeAllElements();
            for (int i = 0; i < count; i++) {
                itemsList.addElement(items.elementAt(i));
            }
            redraw();
        }
    }

    public void cmdOk() {
        destroyView();
    }

    public final Vector menuCommands = new Vector();
    public String menuName = "";

    public void addMenuCommand(MenuCommand command) {
        if (menuCommands.indexOf(command) < 0)
            menuCommands.addElement(command);
    }

    public void removeMenuCommand(MenuCommand command) {
        menuCommands.removeElement(command);
    }

    public void commandState() {
        menuCommands.removeAllElements();
        addMenuCommand(cmdOk);
        // magic to invalidate menu
        VirtualCanvas.getInstance().setCommandListener(null);
        /*if (!cf.swapMenu)
            addMenuCommand(cmdCancel);*/
    }

    public void showMenu() {
        new MyMenu(this, menuName, RosterIcons.getInstance(), menuCommands);
    }

    MenuCommand findCommand(int type) {
	int size = menuCommands.size();
	for (int i = 0; i < size; i++) {
	    MenuCommand item = (MenuCommand) menuCommands.elementAt(i);
	    if (item != null) {
		if (item.map == type) {
		    return (MenuCommand) menuCommands.elementAt(i);
		}
	    }
	}
	return null;
    }

    public void captionPressed() {}
    
    public String selectCommand() {
        if (cf.swapMenu) {
            MenuCommand commandOk = findCommand(MenuCommand.OK);
            return commandOk == null ? SR.MS_OK : commandOk.name;
        } else {
            return canBack ? SR.MS_BACK : "";
        }
    }

    public String menuCommand() {
        int size = menuCommands.size();
        if (cf.swapMenu) {
            if (size == 2) {
                MenuCommand commandScreen = findCommand(MenuCommand.SCREEN);
                return commandScreen == null ? SR.MS_CANCEL : commandScreen.name;
            } else if (size > 1) {
                return SR.MS_MENU;
            } else {
                return "";
            }
        } else {
            if (size > 1) {
                return SR.MS_MENU;
            } else {
                MenuCommand commandOk = findCommand(MenuCommand.OK);
                return commandOk == null ? SR.MS_OK : commandOk.name;
            }
        }
    }

    public void menuPressed() {
        if (menuCommand().equals(SR.MS_MENU)) {
            showMenu();
        } else {
            if (!cf.swapMenu) {
                MenuCommand commandOk = findCommand(MenuCommand.OK);
                menuAction(commandOk == null ? cmdOk : commandOk, this);
            } else {
                if (menuCommands.size() == 2) {
                    MenuCommand commandScreen = findCommand(MenuCommand.SCREEN);
                    menuAction(commandScreen == null ? cmdOk : commandScreen, this);
                }
            }
        }
    }

    public void selectPressed() {
        if (cf.swapMenu) {
            MenuCommand commandOk = findCommand(MenuCommand.OK);
            menuAction(commandOk == null ? cmdOk : commandOk, this);
        } else {
            cmdCancel();
        }
    }
    public void touchLeftPressed() {
        if (cf.swapMenu) {
            selectPressed();
        } else {
            menuPressed();
        }
    }
    public void touchRightPressed() {
        if (cf.swapMenu) {
            menuPressed();
        } else {
            selectPressed();
        }
    }

    public String touchLeftCommand() {
        return cf.swapMenu ? selectCommand() : menuCommand();
    }
    public String touchRightCommand() {
        return cf.swapMenu ? menuCommand() : selectCommand();
    }
}
