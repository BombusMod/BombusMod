/*
 * TextFieldEx.java
 *
 * Created on 12 Сентябрь 2007 г., 22:40
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ui.controls;

import Client.Config;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemCommandListener;
import javax.microedition.lcdui.TextField;
import locale.SR;
//#ifdef CLIPBOARD
//# import util.ClipBoard;
//#endif
/**
 *
 * @author ad
 */
public class TextFieldEx
    extends TextField
    implements ItemCommandListener
    {
//#ifdef CLIPBOARD
//#     private ClipBoard clipboard;
//# 
//#     protected Command cmdCopy = new Command(SR.MS_COPY, Command.SCREEN, 7);
//#     protected Command cmdCopyPlus = new Command("+ "+SR.MS_COPY, Command.SCREEN, 8);
//#     protected Command cmdPaste = new Command(SR.MS_PASTE, Command.SCREEN, 9);
//#endif
    /** Creates a new instance of TextFieldEx */
    public TextFieldEx(String itemName, String itemData, int itemSize, int constraints/*, Display display*/) {
        super(itemName, itemData, itemSize, constraints);
//#ifdef CLIPBOARD
//#         if (Config.getInstance().useClipBoard) {
//#             clipboard=ClipBoard.getInstance();
//#             addCommand(cmdCopy);
//# 
//#             if (!clipboard.isEmpty()) {
//#                 addCommand(cmdCopyPlus);
//#                 addCommand(cmdPaste);
//#             }
//#         }
//#endif
        setItemCommandListener(this);
    }

    public void commandAction(Command command, Item item) {
//#ifdef CLIPBOARD
//#         if (command == cmdCopy)
//#         {
//#             if (clipboard.isEmpty()) {
//#                 addCommand(cmdCopyPlus);
//#             }
//#             try {
//#                clipboard.setClipBoard(getString());
//#             } catch (Exception e) {/*no messages*/}
//#         }
//#         if (command==cmdCopyPlus) {
//#             try {
//#                 StringBuffer clipstr=new StringBuffer();
//#                 clipstr.append(clipboard.getClipBoard());
//#                 clipstr.append("\n\n");
//#                 clipstr.append(getString());
//#                 
//#                 clipboard.setClipBoard(clipstr.toString());
//#                 clipstr=null;
//#             } catch (Exception e) {/*no messages*/}
//#         }
//#         if (command==cmdPaste) { insertText(clipboard.getClipBoard(), getCaretPos()); return; }
//#endif
    } 
//#ifdef CLIPBOARD
//#     public int getCaretPos() {
//#         String body=getString();
//#         
//#         int caretPos=getCaretPosition();
//#         // +MOTOROLA STUB
//#         if (Config.getInstance().phoneManufacturer==Config.MOTO)
//#             caretPos=-1;
//#         
//#         if (caretPos<0) caretPos=body.length();
//#         
//#         return caretPos;
//#     }
//#     
//#     public void insertText(String s, int caretPos) {
//#         String src=getString();
//# 
//#         StringBuffer sb=new StringBuffer(s);
//#         
//#         if (caretPos>0) 
//#             if (src.charAt(caretPos-1)!=' ')   
//#                 sb.insert(0, ' ');
//#         
//#         if (caretPos<src.length())
//#             if (src.charAt(caretPos)!=' ')
//#                 sb.append(' ');
//#         
//#         if (caretPos==src.length()) sb.append(' ');
//#         
//#         try {
//#             int freeSz=getMaxSize()-size();
//#             if (freeSz<sb.length()) sb.delete(freeSz, sb.length());
//#         } catch (Exception e) {}
//#        
//#         insert(sb.toString(), caretPos);
//#         sb=null;
//#     }
//#endif
}
