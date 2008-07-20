/*
 * VCardView.java
 *
 * Created on 25.05.2008, 21:27
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

package VCard;
//#if FILE_IO
import Client.Config;
import io.file.FileIO;
import io.file.browse.Browser;
import io.file.browse.BrowserListener;
import util.StringUtils;
import ui.Time;
//#endif
//#ifdef CLIPBOARD
//# import util.ClipBoard;
//#endif
//#ifdef DETRANSLIT
//# import util.DeTranslit;
//#endif
import javax.microedition.lcdui.Displayable;
//#ifndef MENU_LISTENER
import javax.microedition.lcdui.Command;
//#else
//# import Menu.Command;
//#endif
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Image;
import locale.SR;
import ui.controls.form.DefForm;
import ui.controls.form.ImageItem;
import ui.controls.form.MultiLine;
import ui.controls.form.SimpleString;

/**
 *
 * @author ad
 */
public class VCardView
    extends DefForm
//#if FILE_IO
        implements BrowserListener
//#endif
    {

    private VCard vcard;
    private ImageItem photoItem;
    
    private SimpleString endVCard=new SimpleString(SR.MS_END_OF_VCARD, false);
    private SimpleString noVCard=new SimpleString(SR.MS_NO_VCARD, true);
    private SimpleString noPhoto=new SimpleString(SR.MS_NO_PHOTO, false);
    private SimpleString badFormat=new SimpleString(SR.MS_UNSUPPORTED_FORMAT, false);


//#ifdef CLIPBOARD
//#     ClipBoard clipboard=ClipBoard.getInstance(); 
//#     Command cmdCopy      = new Command(SR.MS_COPY, Command.SCREEN, 1);
//#     Command cmdCopyPlus  = new Command("+ "+SR.MS_COPY, Command.SCREEN, 2);
//#endif
    Command cmdRefresh   = new Command(SR.MS_REFRESH, Command.SCREEN, 3);
//#if FILE_IO
    Command cmdSavePhoto = new Command(SR.MS_SAVE_PHOTO, Command.SCREEN,4);
//#endif
    Command cmdDelPhoto  = new Command(SR.MS_CLEAR_PHOTO, Command.SCREEN,5);

    /** Creates a new instance of VCardView */
    public VCardView(Display display, VCard vcard, String caption) {
        super(display, caption);
        this.display=display;
        parentView=display.getCurrent();
        
        this.vcard=vcard;

        if (vcard.isEmpty()) {
            itemsList.addElement(noVCard);
        } else {
            setPhoto();
            for (int index=0; index<vcard.getCount(); index++) {
                String data=vcard.getVCardData(index);
                String name=(String)VCard.vCardLabels.elementAt(index);
                if (data!=null && name!=null) {
                    MultiLine nData=new MultiLine(name, data, super.superWidth);
                    nData.selectable=true;
                    itemsList.addElement(nData);
                }
            }
            itemsList.addElement(endVCard);
        }

//#ifndef MENU_LISTENER
        commandState();
//#if FILE_IO
        if (vcard.hasPhoto)
            addCommand(cmdSavePhoto);
//#endif
        removeCommand(cmdOk);
        //removeCommand(cmdSelect);
        addCommand(cmdRefresh);
//#ifdef CLIPBOARD
//#         if (Config.getInstance().useClipBoard) {
//#             clipboard=ClipBoard.getInstance();
//#             addCommand(cmdCopy);
//#             addCommand(cmdCopyPlus);
//#         }
//#endif
        if (vcard.hasPhoto)
            addCommand(cmdDelPhoto);
//#endif

        enableListWrapping(false);
        attachDisplay(display);
    }
    
    
     private void setPhoto() {
        try {
            itemsList.removeElement(noPhoto);
            itemsList.removeElement(badFormat);
            itemsList.removeElement(photoItem);
        } catch (Exception e) { }
        
         if (vcard.hasPhoto) {
            try {
                int length=vcard.getPhoto().length;
                Image photoImg=Image.createImage(vcard.getPhoto(), 0, length);
                photoItem=new ImageItem(photoImg, String.valueOf(length)+" bytes");
                if (length>10240)
                    photoItem.collapsed=true;
                itemsList.insertElementAt(photoItem, 0);
            } catch (Exception e) {
                itemsList.addElement(badFormat);
            }
        } else {
            itemsList.addElement(noPhoto);
        }
     }
//#ifndef MENU
    public void commandAction(Command c, Displayable d) {
        if (c==cmdDelPhoto) {
            vcard.dropPhoto(); 
            setPhoto();
        }
        if (c==cmdRefresh) {
            VCard.request(vcard.getJid(), vcard.getId().substring(5));
            destroyView();
            return;
        }
//#if FILE_IO
        if (c==cmdSavePhoto) {
            new Browser(null, display, this, true);
        }
//#endif
//#ifdef CLIPBOARD
//#         if (c == cmdCopy) {
//#             try {
//#                 clipboard.setClipBoard((((MultiLine)getFocusedObject()).getValue()==null)?"":((MultiLine)getFocusedObject()).getValue()+"\n");
//#             } catch (Exception e) {/*no messages*/}
//#         }
//#         
//#         if (c==cmdCopyPlus) {
//#             try {
//#                 StringBuffer clipstr=new StringBuffer(clipboard.getClipBoard())
//#                 .append("\n\n")
//#                 .append((((MultiLine)getFocusedObject()).getValue()==null)?"":((MultiLine)getFocusedObject()).getValue()+"\n");
//#                 
//#                 clipboard.setClipBoard(clipstr.toString());
//#                 clipstr=null;
//#             } catch (Exception e) {/*no messages*/}
//#         }
//#endif
        super.commandAction(c, d);
    }
//#endif

//#if FILE_IO
    public void BrowserFilePathNotify(String pathSelected) {
        if (vcard.hasPhoto) {
            //System.out.println(photoType+"->"+getFileType(photoType));
            String filename = StringUtils.replaceBadChars(getNickDate());
            FileIO file=FileIO.createConnection(pathSelected+filename+vcard.getFileType());
            file.fileWrite(vcard.getPhoto());
        }
    }
    
    private String getNickDate() {
        StringBuffer nickDate=new StringBuffer();
        nickDate.append("photo_");
//#ifdef DETRANSLIT
//#         String userName=(vcard.getNickName()!=null)?vcard.getNickName():vcard.getJid();
//#         if (Config.getInstance().transliterateFilenames) {
//#             nickDate.append(DeTranslit.getInstance().translit(userName));
//#         } else {
//#             nickDate.append(userName);
//#         }
//#else
         if (vcard.getNickName()!=null) {
             nickDate.append(vcard.getNickName());
         } else nickDate.append(vcard.getJid());
//#endif
        nickDate.append('_').append(Time.dayLocalString(Time.utcTimeMillis()).trim());
        return nickDate.toString();
    }
//#endif
    
//#ifdef MENU_LISTENER
//#     public void commandState() {
//#         super.commandState();
//# 
//#         if (vcard!=null) {
//#             if (vcard.hasPhoto) {
//#if FILE_IO
//#                 addCommand(cmdSavePhoto);
//#endif
//#                 addCommand(cmdDelPhoto);
//#             }
//#ifdef CLIPBOARD
//#             if (Config.getInstance().useClipBoard) {
//#                 addCommand(cmdCopy);
//#                 addCommand(cmdCopyPlus);
//#             }
//#endif
//#         }
//#         addCommand(cmdRefresh);
//#     }
//#endif
}
