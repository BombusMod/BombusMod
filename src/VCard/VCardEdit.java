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
//#ifdef DETRANSLIT
//# import util.DeTranslit;
//# import Client.Config;
//#endif
import io.file.FileIO;
import io.file.browse.Browser;
import io.file.browse.BrowserListener;
//#endif

import images.camera.*;

import java.util.*;
//#ifndef MENU_LISTENER
import javax.microedition.lcdui.Command;
//#else
//# import Menu.Command;
//#endif
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.TextField;
import locale.SR;

import ui.Time;
import util.StringUtils;

import ui.controls.form.ImageItem;
import ui.controls.form.DefForm;
import ui.controls.form.SimpleString;
import ui.controls.form.TextInput;

/**
 *
 * @author ad
 */
public class VCardEdit
        extends DefForm 
        implements Runnable
//#if (FILE_IO)
        , BrowserListener
//#endif
        , CameraImageListener
{
    
    private Display display;

    Command cmdCancel=new Command(SR.MS_CANCEL, Command.BACK, 99);
    Command cmdPublish=new Command(SR.MS_PUBLISH, Command.OK, 1);
    Command cmdRefresh=new Command(SR.MS_REFRESH, Command.SCREEN, 2);
//#if FILE_IO
    Command cmdLoadPhoto=new Command(SR.MS_LOAD_PHOTO, Command.SCREEN,3);
    Command cmdSavePhoto=new Command(SR.MS_SAVE_PHOTO, Command.SCREEN,4);
//#endif
    Command cmdDelPhoto=new Command(SR.MS_CLEAR_PHOTO, Command.SCREEN,5);
    Command cmdCamera=new Command(SR.MS_CAMERA, Command.SCREEN,6);

    private Vector items=new Vector();
    private VCard vcard;
    
    private ImageItem photoItem;

    private int st=-1;

    private SimpleString endVCard=new SimpleString(SR.MS_END_OF_VCARD, false);
    private SimpleString noPhoto=new SimpleString(SR.MS_NO_PHOTO, false);
    private SimpleString badFormat=new SimpleString(SR.MS_UNSUPPORTED_FORMAT, false);
    private SimpleString photoTooLarge=new SimpleString(SR.MS_PHOTO_TOO_LARGE, false);

    /** Creates a new instance of vCardForm */
    public VCardEdit(Display display, Displayable pView, VCard vcard) {
        super(display, pView, SR.MS_VCARD+" "+vcard.getNickName());
        this.display=display;
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
            itemsList.addElement(new TextInput(display, name, data, null, TextField.ANY));
        }
        setPhoto();
//#ifndef MENU_LISTENER
        commandState();
//#ifndef MENU
        super.removeCommand(cmdOk);
        addCommand(cmdPublish);
        addCommand(cmdRefresh);
//#if FILE_IO
        addCommand(cmdLoadPhoto);
        addCommand(cmdSavePhoto);
//#endif
        String cameraAvailable=System.getProperty("supports.video.capture");
        if (cameraAvailable!=null) if (cameraAvailable.startsWith("true"))
            addCommand(cmdCamera);
        addCommand(cmdDelPhoto);
//#endif
//#endif
        enableListWrapping(false);
        attachDisplay(display);
        this.parentView=pView;
    }
    
    public void cmdOk() {
        for (int index=0; index<vcard.getCount(); index++) {
            try {
                String field=((TextInput)itemsList.elementAt(index)).getValue();
                if (field.length()==0) field=null;
                vcard.setVCardData(index, field);
             } catch (Exception ex) { }
        }
        //System.out.println(vcard.constructVCard().toString());
        new Thread(this).start();
        destroyView();
    }
//#ifndef MENU
    public void commandAction(Command c, Displayable d) {
        if (c==cmdCancel) destroyView();
        if (c==cmdRefresh) {
            VCard.request(vcard.getJid(), vcard.getId().substring(5));
            destroyView();
        }
        
//#if FILE_IO
        if (c==cmdLoadPhoto) {
            st=1;
            new Browser(null, display, this, this, false);
        }
        if (c==cmdSavePhoto) {
            st=2;
            new Browser(null, display, this, this, true);
        }
//#endif

        if (c==cmdCamera)
            new CameraImage(display, this);

        if (c==cmdDelPhoto) {
            vcard.dropPhoto();
            setPhoto();
        }
        if (c==cmdPublish)
            cmdOk();

        super.commandAction(c, d);
    }
//#endif
    public void run() {
        StaticData.getInstance().roster.theStream.send(vcard.constructVCard());
        //System.out.println("VCard sent");
    }

//#if (FILE_IO)
    public void BrowserFilePathNotify(String pathSelected) {
        if (st>0) {
            if (st==1) {
                try {
                    FileIO f=FileIO.createConnection(pathSelected);
                    vcard.photo=f.fileRead();
                    vcard.setPhotoType();
                    setPhoto();
                } catch (Exception e) {
                    System.out.println("error on load");
                }
            }
            if (st==2 & vcard.hasPhoto) {
                //System.out.println(photoType+"->"+getFileType(photoType));
                String filename = StringUtils.replaceBadChars(getNickDate());
                FileIO file=FileIO.createConnection(pathSelected+filename+vcard.getFileType());
                file.fileWrite(vcard.getPhoto());
            }
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
        nickDate.append("_").append(Time.dayLocalString(Time.utcTimeMillis()).trim());
        return nickDate.toString();
    }
//#endif

    public void cameraImageNotify(byte[] capturedPhoto) {
        vcard.setPhoto(capturedPhoto);
        setPhoto();
    }

     private void setPhoto() {
        try {
            itemsList.removeElement(endVCard);
            itemsList.removeElement(noPhoto);
            itemsList.removeElement(badFormat);
            itemsList.removeElement(photoItem);
            itemsList.removeElement(photoTooLarge);
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
        itemsList.addElement(endVCard);
     }
     
//#ifdef MENU_LISTENER
//#     public void commandState() {
//#         super.commandState();
//#         
//#         removeCommand(cmdOk);
//#         addCommand(cmdPublish);
//#         addCommand(cmdRefresh);
//#if FILE_IO
//#         addCommand(cmdLoadPhoto);
//#         addCommand(cmdSavePhoto);
//#endif
//#         String cameraAvailable=System.getProperty("supports.video.capture");
//#         if (cameraAvailable!=null) if (cameraAvailable.startsWith("true"))
//#             addCommand(cmdCamera);
//#         addCommand(cmdDelPhoto);
//#     }
//#endif
}
