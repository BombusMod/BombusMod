/*
 * VCardEdit.java
 *
 * Created on 30 Май 2008 г., 9:25
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package vcard;

import Client.StaticData;
//#if (FILE_IO)
//#ifdef TRANSLIT
import Client.Config;
import util.Translit;
//#endif
import io.file.FileIO;
import io.file.browse.Browser;
import io.file.browse.BrowserListener;
//#endif

import images.camera.*;

import java.util.*;
import javax.microedition.lcdui.*;
import locale.SR;

import ui.Time;
import ui.controls.StringItemEx;
import ui.controls.TextFieldEx;
import util.StringUtils;

import ui.controls.form.ImageItem;
import ui.controls.form.DefForm;
import ui.controls.form.SimpleString;
import ui.controls.form.BoldString;
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
    private Displayable parentView;
    
    protected Command cmdCancel=new Command(SR.MS_CANCEL, Command.BACK, 99);
    protected Command cmdPublish=new Command(SR.MS_PUBLISH, Command.OK, 1);
    protected Command cmdRefresh=new Command(SR.MS_REFRESH, Command.SCREEN, 2);
//#if FILE_IO
    protected Command cmdLoadPhoto=new Command(SR.MS_LOAD_PHOTO, Command.SCREEN,3);
    protected Command cmdSavePhoto=new Command(SR.MS_SAVE_PHOTO, Command.SCREEN,4);
//#endif
    protected Command cmdDelPhoto=new Command(SR.MS_CLEAR_PHOTO, Command.SCREEN,5);
    protected Command cmdCamera=new Command(SR.MS_CAMERA, Command.SCREEN,6);

    private Vector items=new Vector();
    private VCard vcard;
    
    private ImageItem photoItem;
    private byte[] photo;
    
    private String photoType=null;
    private int st=-1;
    
    
    private SimpleString endVCard=new SimpleString("[end of vCard]");
    private SimpleString noPhoto=new SimpleString("[No photo available]");
    private SimpleString badFormat=new SimpleString("[Unsupported format]");

    /** Creates a new instance of vCardForm */
    public VCardEdit(Display display, VCard vcard) {
        super(display, SR.MS_VCARD+" "+vcard.getNickName());
        this.display=display;
        parentView=display.getCurrent();
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
            itemsList.addElement(new BoldString(name));
            itemsList.addElement(new TextInput(display, data, null, TextField.ANY));
        }
        
        photo=vcard.getPhoto();
        setPhoto();
        
        addCommand(cmdRefresh);
//#if FILE_IO
        addCommand(cmdLoadPhoto);
//#endif
        String cameraAvailable=System.getProperty("supports.video.capture");
        if (cameraAvailable!=null) if (cameraAvailable.startsWith("true"))
            addCommand(cmdCamera);
        addCommand(cmdDelPhoto);
        
        moveCursorTo(getNextSelectableRef(-1));
        attachDisplay(display);
    }
    
    public void cmdOk() {
        vcard.setPhoto(photo);
        vcard.setPhotoType(getPhotoMIMEType());
        int i=1;
        for (int index=0; index<vcard.getCount(); index++) {
            try {
                String field=((TextInput)itemsList.elementAt(i)).getValue();
                if (field.length()==0) field=null;
                vcard.setVCardData(index, field);
             } catch (Exception ex) { }
            i=i+2;
        }
        //System.out.println(vcard.constructVCard().toString());
        new Thread(this).start();
        destroyView();
    }
    
    public void commandAction(Command c, Displayable d) {
        if (c==cmdCancel) destroyView();
        if (c==cmdRefresh) {
            VCard.request(vcard.getJid(), vcard.getId().substring(5));
            destroyView();
        }
        
//#if FILE_IO
        if (c==cmdLoadPhoto) {
            st=1;
            new Browser(null, display, this, false);
        }
        if (c==cmdSavePhoto) {
            st=2;
            new Browser(null, display, this, true);
        }
//#endif

        if (c==cmdCamera)
            new CameraImage(display, this);

        if (c==cmdDelPhoto) {
            photo=null; 
            setPhoto();
        }
    }

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
                    photo=f.fileRead();
                    vcard.setPhotoType(getPhotoMIMEType());
                    setPhoto();
                } catch (Exception e) {
                    System.out.println("error on load");
                }
            }
            if (st==2 & photo!=null) {
                //System.out.println(photoType+"->"+getFileType(photoType));
                String filename = StringUtils.replaceBadChars(getNickDate());
                FileIO file=FileIO.createConnection(pathSelected+filename+getFileType(getPhotoMIMEType()));
                file.fileWrite(photo);
            }
        }
    }

    private String getNickDate() {
        StringBuffer nickDate=new StringBuffer();
        nickDate.append("photo_");
//#ifdef TRANSLIT
        String userName=(vcard.getNickName()!=null)?vcard.getNickName():vcard.getJid();
        if (Config.getInstance().transliterateFilenames) {
            nickDate.append(Translit.translit(userName));
        } else {
            nickDate.append(userName);
        }
//#else
//#          if (vcard.getNickName()!=null) {
//#              nickDate.append(vcard.getNickName());
//#          } else nickDate.append(vcard.getJid());
//#endif
        nickDate.append("_");
        nickDate.append(Time.dayLocalString(Time.utcTimeMillis()).trim());
        return nickDate.toString();
    }
//#endif
    private String getFileType(String MIMEtype) {
        if (MIMEtype!=null) {
            if (MIMEtype=="image/jpeg") return ".jpg";
            if (MIMEtype=="image/png") return ".png";
            if (MIMEtype=="image/gif") return ".gif";
            if (MIMEtype=="image/x-ms-bmp") return ".bmp";
        }
        return ".jpg";
    }

    public void cameraImageNotify(byte[] capturedPhoto) {
        photo=capturedPhoto;
        setPhoto();
    }

     private void setPhoto() {
        if (photo!=null) {
            try {
                Image photoImg=Image.createImage(photo, 0, photo.length);
                photoItem=new ImageItem(photoImg, String.valueOf(photo.length)+" bytes");
                itemsList.addElement(photoItem);
            } catch (Exception e) {
                itemsList.addElement(badFormat);
            }
        } else {
            itemsList.addElement(noPhoto);
        }
     }
	
    public String getPhotoMIMEType() {
        try {
             if (photo[0]==(byte)0xff &&
                photo[1]==(byte)0xd8 &&
                (photo[6]==(byte)'J' || photo[6]==(byte)'E' || photo[6]==(byte)'e') &&
                (photo[7]==(byte)'F' || photo[7]==(byte)'x' || photo[7]==(byte)'X') &&
                (photo[8]==(byte)'I' || photo[8]==(byte)'i') &&
                (photo[9]==(byte)'F' || photo[9]==(byte)'f')) {
                //System.out.println("image/jpeg");
                 return "image/jpeg";
             }
             
            if (photo[0]==(byte)0x89 &&
                photo[1]==(byte)'P' &&
                photo[2]==(byte)'N' &&
                photo[3]==(byte)'G') {
                //System.out.println("image/png");
                return "image/png";
            }
            
            if (photo[0]==(byte)'G' &&
                photo[1]==(byte)'I' &&
                photo[2]==(byte)'F') {
                //System.out.println("image/gif");
                 return "image/gif";
             }
             
            if (photo[0]==(byte)'B' &&
                photo[1]==(byte)'M') {
                //System.out.println("image/x-ms-bmp");
                 return "image/x-ms-bmp";
             }
            
        } catch (Exception e) {}
        //System.out.println("unknown MIME type");
        return null;
    }
}