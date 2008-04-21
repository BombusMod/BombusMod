/*
 * StringItemEx.java
 *
 * Created on 12 Сентябрь 2007 г., 23:16
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ui.controls;

import Client.Config;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemCommandListener;
import javax.microedition.lcdui.StringItem;
import locale.SR;
//#ifdef CLIPBOARD
//# import util.ClipBoard;
//#endif

/**
 *
 * @author ad
 */
public class StringItemEx
    extends StringItem
    implements ItemCommandListener
    {
//#ifdef CLIPBOARD
//#     private ClipBoard clipboard;
//# 
//#     protected Command cmdCopy = new Command(SR.MS_COPY, Command.SCREEN, 7);
//#     protected Command cmdCopyPlus = new Command("+ "+SR.MS_COPY, Command.SCREEN, 8);
//#endif
    /** Creates a new instance of StringItemEx */
    public StringItemEx(String itemName, String itemData) {
        super(itemName, itemData);
//#ifdef CLIPBOARD
//#         if (Config.getInstance().useClipBoard) {
//#             clipboard=ClipBoard.getInstance();
//#             addCommand(cmdCopy);
//# 
//#             if (!clipboard.isEmpty()) {
//#                 addCommand(cmdCopyPlus);
//#             }
//#         }
//#endif
        setItemCommandListener(this);
    }

    public void commandAction(Command command, Item item) {
//#ifdef CLIPBOARD
//#         if (command == cmdCopy) {
//#           try {
//#             clipboard.setClipBoard(getText());
//#           } catch (Exception e) {/*no messages*/}
//#         }
//#         if (command==cmdCopyPlus) {
//#             try {
//#                 StringBuffer clipstr=new StringBuffer();
//#                 clipstr.append(clipboard.getClipBoard());
//#                 clipstr.append("\n\n");
//#                 clipstr.append(getText());
//#                 
//#                 clipboard.setClipBoard(clipstr.toString());
//#                 clipstr=null;
//#             } catch (Exception e) {/*no messages*/}
//#         }
//#endif
    }
    
}
