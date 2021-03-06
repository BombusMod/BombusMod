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
import Client.Contact;
import midlet.BombusMod;
//#if FILE_IO
import io.file.FileIO;
import io.file.browse.Browser;
import io.file.browse.BrowserListener;
import org.bombusmod.util.ClipBoardIO;
import util.StringUtils;
//#endif
import Menu.MenuCommand;
import javax.microedition.lcdui.Image;
import locale.SR;
import ui.VirtualList;
import ui.controls.form.DefForm;
import ui.controls.form.ImageItem;
import ui.controls.form.MultiLine;
import ui.controls.form.SimpleString;
import ui.controls.form.LinkString;
import ui.controls.form.SpacerItem;
import images.RosterIcons;

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
    
    private Contact c;
    
    private SimpleString endVCard=new SimpleString(SR.MS_END_OF_VCARD, false);
    private SimpleString noVCard=new SimpleString(SR.MS_NO_VCARD, true);
    private SimpleString noPhoto=new SimpleString(SR.MS_NO_PHOTO, false);
    private SimpleString badFormat=new SimpleString(SR.MS_UNSUPPORTED_FORMAT, false);
    private SimpleString photoTooLarge=new SimpleString(SR.MS_PHOTO_TOO_LARGE, false);

    private LinkString refresh;
    
    private LinkString copy;
    private LinkString save;
    
    private String url="";
        
//#ifdef CLIPBOARD
    StringBuffer VCardData = new StringBuffer();
    MenuCommand cmdCopy      = new MenuCommand(SR.MS_COPY, MenuCommand.OK, 1, RosterIcons.ICON_COPY);
    MenuCommand cmdCopyPlus  = new MenuCommand("+ "+SR.MS_COPY, MenuCommand.SCREEN, 2, RosterIcons.ICON_COPYPLUS);
//#endif    

    /** Creates a new instance of VCardView
     * @param contact
     */
    public VCardView(Contact contact) {
        super(contact.getNickJid(), false);
        this.vcard = contact.vcard;
        this.c = contact;
        
        refresh=new LinkString(SR.MS_REFRESH) { 
            public void doAction() { 
                VCard.request(vcard.getJid(), vcard.getId().substring(5)); 
                destroyView();
            } 
        };

        if (vcard == null || vcard.isEmpty()) {
            itemsList.addElement(noVCard);
            itemsList.addElement(refresh);
        } else {
            setPhoto();
            for (int index=0; index<vcard.getCount(); index++) {
                String data=vcard.getVCardData(index);
                String name=(String)VCard.vCardLabels.elementAt(index);
                if (data!=null && name!=null) {
                    if (!VCard.vCardFields.elementAt(index).equals("URL")) {
                        MultiLine nData=new MultiLine(name, data);
                        nData.selectable=true;
                        itemsList.addElement(nData);
//#ifdef CLIPBOARD
                        VCardData.append(name).append(":\n").append(data).append("\n");
//#endif                        
                    } else {
                        url=data;
                        LinkString nData = new LinkString(url) {
                            public void doAction() {
                                BombusMod.getInstance().platformRequest(url);
                            }
                        };
                        itemsList.addElement(nData);
                    }
                }
            }
            itemsList.addElement(endVCard);
            itemsList.addElement(new SpacerItem(10));
            itemsList.addElement(refresh);
//#ifdef CLIPBOARD
            copy = new LinkString(SR.MS_COPY + " " + SR.MS_VCARD) {

                public void doAction() {
                    ClipBoardIO.getInstance().setClipBoard(VCardData.toString());
                    destroyView();
                }
            };
            itemsList.addElement(copy);

//#endif           
//#ifdef FILE_IO
            if (vcard.hasPhoto) {
                save = new LinkString(SR.MS_SAVE_PHOTO) {

                    public void doAction() {
                        new Browser(null, VCardView.this, true);
                    }
                };
                itemsList.addElement(save);
            }
//#endif            
        }
        show();
    }    

    
    private void setPhoto() {
        try {
            itemsList.removeElement(noPhoto);
            itemsList.removeElement(badFormat);
            itemsList.removeElement(photoItem);
            itemsList.removeElement(photoTooLarge);
        } catch (Exception e) { }
        
         if (vcard.hasPhoto) {
            try {
                int length=vcard.getPhoto().length;
                if (length==1) {
                    vcard.setPhoto(null);
                    itemsList.addElement(photoTooLarge);
                } else {
                    Image photoImg=Image.createImage(vcard.getPhoto(), 0, length);
                    photoItem=new ImageItem(photoImg, "minimized, size: "+String.valueOf(length)+"b.");
                    if (length>10240)
                        photoItem.collapsed=true;
                    itemsList.insertElementAt(photoItem, 0);
                }
            } catch (OutOfMemoryError eom) {
            } catch (Exception e) {
                itemsList.addElement(badFormat);
            }
        } else {
            itemsList.addElement(noPhoto);
        }
     }

//#if FILE_IO
    public void BrowserFilePathNotify(String pathSelected) {
        if (vcard.hasPhoto) {
            //System.out.println(photoType+"->"+getFileType(photoType));
            String filename = StringUtils.replaceBadChars(vcard.getNickDate());
            FileIO file = FileIO.createConnection(pathSelected + filename + vcard.getFileType());
            file.writeFile(vcard.getPhoto());
        }
    }
//#endif
    
    public void commandState() {
        menuCommands.removeAllElements();
//#ifdef CLIPBOARD
        addMenuCommand(cmdCopy);
        if (!ClipBoardIO.getInstance().isEmpty()) {
            addMenuCommand(cmdCopyPlus);
        }
        //#endif
    }


//#ifdef CLIPBOARD
    public void menuAction(MenuCommand c, VirtualList d) {

        String value = null;

        if (getFocusedObject() instanceof MultiLine) {
            value = ((MultiLine) getFocusedObject()).toString();
        }
        if (value != null) {
            if (value.length() != 0) {

                if (c == cmdCopy) {
                    ClipBoardIO.getInstance().setClipBoard(value);
                }
                if (c == cmdCopyPlus) {
                    ClipBoardIO.getInstance().append(value);
                }
            }
        }

        super.menuAction(c, d);
    }   
    
//#endif    
}
