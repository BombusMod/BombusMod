/*
 * VCardEdit.java
 *
 * Created on 30 Май 2008 г., 9:25
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package VCard;

import Client.StaticData;
//#if (FILE_IO)
//# import io.file.FileIO;
//# import io.file.browse.Browser;
//# import io.file.browse.BrowserListener;
//#endif
//#ifndef NOMMEDIA
import images.camera.*;
//#endif
import java.util.*;
import Menu.MenuCommand;
import javax.microedition.lcdui.Image;
import locale.SR;
import ui.VirtualList;

import util.StringUtils;

import ui.controls.form.ImageItem;
import ui.controls.form.DefForm;
import ui.controls.form.SimpleString;
import ui.controls.form.TextInput;
import ui.controls.form.LinkString;
import images.RosterIcons;

/**
 *
 * @author ad
 */
public class VCardEdit
        extends DefForm 
        implements Runnable
//#if (FILE_IO)
//#         , BrowserListener
//#endif
//#ifndef NOMMEDIA
        , CameraImageListener
//#endif
{
    
    MenuCommand cmdPublish=new MenuCommand(SR.MS_PUBLISH, MenuCommand.OK, 1, RosterIcons.ICON_ON);
    MenuCommand cmdRefresh=new MenuCommand(SR.MS_REFRESH, MenuCommand.SCREEN, 2, RosterIcons.ICON_FT);
//#if FILE_IO
//#     MenuCommand cmdLoadPhoto=new MenuCommand(SR.MS_LOAD_PHOTO, MenuCommand.SCREEN,3, RosterIcons.ICON_SENDPHOTO);
//#     MenuCommand cmdSavePhoto=new MenuCommand(SR.MS_SAVE_PHOTO, MenuCommand.SCREEN,4, RosterIcons.ICON_CHATARCHIVE);
//#endif
    MenuCommand cmdDelPhoto=new MenuCommand(SR.MS_CLEAR_PHOTO, MenuCommand.SCREEN,5, RosterIcons.ICON_CLEAN_MESSAGES);
//#ifndef NOMMEDIA
    MenuCommand cmdCamera=new MenuCommand(SR.MS_CAMERA, MenuCommand.SCREEN,6, RosterIcons.ICON_COMMAND);
//#endif

    private Vector items=new Vector();
    private VCard vcard;
    
    private ImageItem photoItem;

    private int st=-1;

    //private SimpleString endVCard=new SimpleString(SR.MS_END_OF_VCARD, false);
    private SimpleString noPhoto=new SimpleString(SR.MS_NO_PHOTO, false);
    private SimpleString badFormat=new SimpleString(SR.MS_UNSUPPORTED_FORMAT, false);
    private SimpleString photoTooLarge=new SimpleString(SR.MS_PHOTO_TOO_LARGE, false);
    
    private LinkString publish;

    /** Creates a new instance of vCardForm
     * @param vcard
     */
    public VCardEdit(VCard vcard) {
        super(SR.MS_VCARD+" "+StaticData.getInstance().account.getBareJid());
        this.vcard=vcard;

        for (int index=0; index<vcard.getCount(); index++) {
            String data=vcard.getVCardData(index);
            String name=(String)VCard.vCardLabels.elementAt(index);
            //truncating large string
            if (data!=null) {
                int len=data.length();
                if (data.length()>500)
                    data=data.substring(0, 494)+"<...>";
            } 
            itemsList.addElement(new TextInput(name, data, null));
        }

        publish=new LinkString(SR.MS_PUBLISH) { public void doAction() { publish(); } };
        
        setPhoto();             
    }
    
    public void publish() {
        for (int index=0; index<vcard.getCount(); index++) {
            try {
                String field=((TextInput)itemsList.elementAt(index)).getValue();
                if (field.length()==0) field=null;
                vcard.setVCardData(index, field);
             } catch (Exception ex) { }
        }
        //System.out.println(vcard.constructVCard().toString());
        new Thread(this).start();
        parentView = sd.roster;
        destroyView();
    }

    public void menuAction(MenuCommand c, VirtualList d) {        
        if (c==cmdRefresh) {
            VCard.request(vcard.getJid(), vcard.getId().substring(5));
            destroyView();
        }
        
//#if FILE_IO
//#         if (c==cmdLoadPhoto) {
//#             st=1;
//#             new Browser(null, this, false);
//#         }
//#         if (c==cmdSavePhoto) {
//#             st=2;
//#             new Browser(null, this, true);
//#         }
//#endif
//#ifndef NOMMEDIA
        if (c==cmdCamera)
            new CameraImage(this, this);
//#endif
        if (c==cmdDelPhoto) {
            vcard.dropPhoto();
            setPhoto();
        }
        if (c==cmdPublish)
            publish();

        super.menuAction(c, d);
    }

    
    public void run() {
        StaticData.getInstance().theStream.send(vcard.constructVCard());
        //System.out.println("VCard sent");
    }

//#if (FILE_IO)
//#     public void BrowserFilePathNotify(String pathSelected) {
//#         if (st>0) {
//#             if (st==1) {
//#                 try {
//#                     FileIO f=FileIO.createConnection(pathSelected);
//#                     vcard.photo=f.fileRead();
//#                     vcard.setPhotoType();
//#                     setPhoto();
//#                     redraw();
//#                 } catch (Exception e) {
//#ifdef DEBUG
//#                     System.out.println("error on load");
//#endif
//#                 }
//#             }
//#             if (st==2 & vcard.hasPhoto) {
//#                 //System.out.println(photoType+"->"+getFileType(photoType));
//#                 String filename = StringUtils.replaceBadChars(vcard.getNickDate());
//#                 FileIO file=FileIO.createConnection(pathSelected+filename+vcard.getFileType());
//#                 file.fileWrite(vcard.getPhoto());
//#             }
//#         }
//#     }
//#endif
    
    public void cameraImageNotify(byte[] capturedPhoto) {
        vcard.setPhoto(capturedPhoto);
        setPhoto();
    }

     private void setPhoto() {
        try {
            //itemsList.removeElement(endVCard);
            itemsList.removeElement(noPhoto);
            itemsList.removeElement(badFormat);
            itemsList.removeElement(photoItem);
            itemsList.removeElement(photoTooLarge);
            
            itemsList.removeElement(publish);
        } catch (Exception e) { }

         if (vcard.hasPhoto) {
            if (vcard.getPhoto().length==1) {
                vcard.setPhoto(null);
                itemsList.addElement(photoTooLarge);
            } else {
                try {
                    Image photoImg=Image.createImage(vcard.getPhoto(), 0,vcard.getPhoto().length);
                    photoItem=new ImageItem(photoImg, String.valueOf(vcard.getPhoto().length)+" bytes");
                    itemsList.addElement(photoItem);
                } catch (Exception e) {
                    itemsList.addElement(badFormat);
                }
            }
        } else {
            itemsList.addElement(noPhoto);
        }
        //itemsList.addElement(endVCard);
        
        itemsList.addElement(publish);
     }

    public final void commandState() {
        menuCommands.removeAllElements();
        addMenuCommand(cmdPublish);
        addMenuCommand(cmdRefresh);
//#if FILE_IO
//#         addMenuCommand(cmdLoadPhoto);
//#         addMenuCommand(cmdSavePhoto);
//#endif
//#ifndef NOMMEDIA
        String cameraAvailable=System.getProperty("supports.video.capture");
        if (cameraAvailable!=null) if (cameraAvailable.startsWith("true"))
            addMenuCommand(cmdCamera);
//#endif  
        addMenuCommand(cmdDelPhoto);        
    }    
 }
