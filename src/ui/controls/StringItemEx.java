/*
 * StringItemEx.java
 *
 * Created on 12 Сентябрь 2007 г., 23:16
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ui.controls;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemCommandListener;
import javax.microedition.lcdui.StringItem;
import locale.SR;
import util.ClipBoard;

/**
 *
 * @author ad
 */
public class StringItemEx
    extends StringItem
    implements ItemCommandListener
    {
    
    private ClipBoard clipboard=ClipBoard.getInstance();  // The clipboard class

    protected Command cmdCopy = new Command(SR.MS_COPY, Command.SCREEN, 7);
    protected Command cmdCopyPlus = new Command("+ "+SR.MS_COPY, Command.SCREEN, 8);
    
    /** Creates a new instance of StringItemEx */
    public StringItemEx(String itemName, String itemData) {
        super(itemName, itemData);
        
        addCommand(cmdCopy);
        
        if (!clipboard.isEmpty()) {
            addCommand(cmdCopyPlus);
        }
        
        setItemCommandListener(this);
    }

    public void commandAction(Command command, Item item) {
        if (command == cmdCopy)
        {
        if (clipboard.isEmpty()) {
            addCommand(cmdCopyPlus);
        }
          try {
            clipboard.setClipBoard(getText());
          } catch (Exception e) {/*no messages*/}
        }
        if (command==cmdCopyPlus) {
            try {
                StringBuffer clipstr=new StringBuffer();
                clipstr.append(clipboard.getClipBoard());
                clipstr.append("\n\n");
                clipstr.append(getText());
                
                clipboard.setClipBoard(clipstr.toString());
                clipstr=null;
            } catch (Exception e) {/*no messages*/}
        }
    }
    
}
