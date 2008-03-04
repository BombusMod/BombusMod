/*
 * ColorForm.java
 *
 * Copyright (c) 2006-2007, Daniel Apatin (ad), http://apatin.net.ru
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

package ui;

//#if (FILE_IO)
import io.file.FileIO;
import io.file.browse.Browser;
import io.file.browse.BrowserListener;
import java.io.IOException;
import java.io.OutputStream;
//#endif
import javax.microedition.lcdui.*;
import locale.SR;

public class ColorForm implements CommandListener
//#if (FILE_IO && COLORS)
//#         , BrowserListener
//#endif
{
    private Displayable currentChoice = null;
    private Display display;
    private Displayable parentView;
      
    private static ColorScheme cl=ColorScheme.getInstance();
      
    private final static int w=16;
    private final static int h=16;

    public static final String[] NAMES = {
            SR.MS_BALLOON_INK,
            SR.MS_BALLOON_BGND,
            SR.MS_LIST_BGND,
            SR.MS_LIST_BGND_EVEN,
            SR.MS_LIST_INK,

            SR.MS_MSG_SUBJ,
            SR.MS_MSG_HIGHLIGHT,
            SR.MS_DISCO_CMD,
            SR.MS_BAR_BGND,
            SR.MS_BAR_BGND+"2",
            SR.MS_BAR_INK,

            SR.MS_CONTACT_DEFAULT,
            SR.MS_CONTACT_CHAT,
            SR.MS_CONTACT_AWAY,
            SR.MS_CONTACT_XA,
            SR.MS_CONTACT_DND,

            SR.MS_GROUP_INK,
            SR.MS_BLK_INK,
            SR.MS_BLK_BGND,
            SR.MS_MESSAGE_IN,
            SR.MS_MESSAGE_OUT,
            SR.MS_MESSAGE_PRESENCE,
            
            SR.MS_MESSAGE_AUTH,
            SR.MS_MESSAGE_HISTORY,
            SR.MS_PGS_REMAINED,
            SR.MS_PGS_COMPLETE,

            SR.MS_PGS_BORDER,
            SR.MS_PGS_BGND,
            SR.MS_HEAP_TOTAL,
            SR.MS_HEAP_FREE,
            SR.MS_CURSOR_BGND,

            SR.MS_CURSOR_OUTLINE,
            SR.MS_SCROLL_BRD,
            SR.MS_SCROLL_BAR,
            SR.MS_SCROLL_BGND,
            
            SR.MS_CONTACT+"J2J"
//#if NICK_COLORS   
//#             ,SR.MS_MESSAGE_IN_S,
//#             SR.MS_MESSAGE_OUT_S,
//#             SR.MS_MESSAGE_PRESENCE_S
//#endif
        };
        
        public static int[] COLORS = {            
            cl.BALLOON_INK,
            cl.BALLOON_BGND,
            cl.LIST_BGND,
            cl.LIST_BGND_EVEN,
            cl.LIST_INK,
            
            cl.MSG_SUBJ,
            cl.MSG_HIGHLIGHT,
            cl.DISCO_CMD,
            cl.BAR_BGND,
            cl.BAR_BGND_BOTTOM,
            cl.BAR_INK,
            
            cl.CONTACT_DEFAULT,
            cl.CONTACT_CHAT,
            cl.CONTACT_AWAY,
            cl.CONTACT_XA,
            cl.CONTACT_DND,
            
            cl.GROUP_INK,
            cl.BLK_INK,
            cl.BLK_BGND,
            cl.MESSAGE_IN,
            cl.MESSAGE_OUT,
            cl.MESSAGE_PRESENCE,
            
            cl.MESSAGE_AUTH,
            cl.MESSAGE_HISTORY,
            cl.PGS_REMAINED,
            cl.PGS_COMPLETE,
            
            cl.PGS_BORDER,
            cl.PGS_BGND,
            cl.HEAP_TOTAL,
            cl.HEAP_FREE,
            cl.CURSOR_BGND,
            
            cl.CURSOR_OUTLINE,
            cl.SCROLL_BRD,
            cl.SCROLL_BAR,
            cl.SCROLL_BGND,
            
            cl.CONTACT_J2J
//#if NICK_COLORS
//#             ,cl.MESSAGE_IN_S,
//#             cl.MESSAGE_OUT_S,
//#             cl.MESSAGE_PRESENCE_S
//#endif
            
      };
        
      
    public static Image[] IMAGES= {
            imageData(cl.BALLOON_INK),
            imageData(cl.BALLOON_BGND),
            imageData(cl.LIST_BGND),
            imageData(cl.LIST_BGND_EVEN),
            imageData(cl.LIST_INK),

            imageData(cl.MSG_SUBJ),
            imageData(cl.MSG_HIGHLIGHT),
            imageData(cl.DISCO_CMD),
            imageData(cl.BAR_BGND),
            imageData(cl.BAR_BGND_BOTTOM),
            imageData(cl.BAR_INK),

            imageData(cl.CONTACT_DEFAULT),
            imageData(cl.CONTACT_CHAT),
            imageData(cl.CONTACT_AWAY),
            imageData(cl.CONTACT_XA),
            imageData(cl.CONTACT_DND),
                    
            imageData(cl.GROUP_INK),
            imageData(cl.BLK_INK),
            imageData(cl.BLK_BGND),
            imageData(cl.MESSAGE_IN),
            imageData(cl.MESSAGE_OUT),
            imageData(cl.MESSAGE_PRESENCE),
            
            imageData(cl.MESSAGE_AUTH),
            imageData(cl.MESSAGE_HISTORY),
            imageData(cl.PGS_REMAINED),
            imageData(cl.PGS_COMPLETE),

            imageData(cl.PGS_BORDER),
            imageData(cl.PGS_BGND),
            imageData(cl.HEAP_TOTAL),
            imageData(cl.HEAP_FREE),
            imageData(cl.CURSOR_BGND),

            imageData(cl.CURSOR_OUTLINE),
            imageData(cl.SCROLL_BRD),
            imageData(cl.SCROLL_BAR),
            imageData(cl.SCROLL_BGND),
            imageData(cl.CONTACT_J2J)
//#if NICK_COLORS
//#             ,imageData(cl.MESSAGE_IN_S),
//#             imageData(cl.MESSAGE_OUT_S),
//#             imageData(cl.MESSAGE_PRESENCE_S)
//#endif
            
      };


    private static List selectionList;

//#if (FILE_IO)
    Command cmdSaveSkin=new Command(SR.MS_SAVE, Command.ITEM,3); 
    int fileSize;
    private int filePos;
    String filePath;
    private FileIO file;
    private OutputStream os;
//#endif
    
    private Command cmdCancel=new Command(SR.MS_CLOSE, Command.BACK, 99);

    private Command selectCommand = new Command(SR.MS_EDIT, Command.ITEM, 1);
    
    public ColorForm(Display display) {
        super();
        this.display=display;
        parentView=display.getCurrent();

        selectionList = new List(SR.MS_COLOR_TUNE, List.IMPLICIT, NAMES, IMAGES);

        selectionList.setSelectCommand(selectCommand);

//#if (FILE_IO)
        selectionList.addCommand(cmdSaveSkin);
//#endif
        selectionList.addCommand(cmdCancel);
        display.setCurrent(selectionList);
        selectionList.setCommandListener(this);
    }
      
    public void commandAction(Command c, Displayable d) {
        int pos = selectionList.getSelectedIndex();
        
        if (c==cmdCancel) {
            destroyView();
            return;
        }
        
//#if (FILE_IO && COLORS)
//#         if (c==cmdSaveSkin) new Browser(null,display, this, true);
//#endif
        
        if (c==selectCommand) {
          try {
              if (!NAMES[pos].startsWith("(n/a)") && pos != NAMES.length) {
//#if (COLORS)
//#                 new ColorSelector(display, pos);
//#endif
              }
          } catch(Exception err) {}
       }
    }


//#if (FILE_IO && COLORS)
//#     void writeFile(byte b[]){
//#         try {
//#             os.write(b);
//#             filePos+=b.length;
//#         } catch (IOException ex) {
//#             //ex.printStackTrace();
//#         }
//#     }
//# 
//#     public void BrowserFilePathNotify(String pathSelected) {
//# 
//#         byte[] bodyMessage=ColorScheme.getSkin().getBytes();
//#                 
//#         file=FileIO.createConnection(pathSelected+"skin.txt");
//#         try {
//#             os=file.openOutputStream();
//#             writeFile(bodyMessage);
//#             os.close();
//#             file.close();
//#         } catch (IOException ex) {
//#             try {
//#                 file.close();
//#             } catch (IOException ex2) {
//#                 //ex2.printStackTrace();
//#             }
//#             //ex.printStackTrace();
//#         }
//#     }
//#endif 


    public void destroyView(){
        if (display!=null) display.setCurrent(parentView);
    }
    
    public static void updateItem(int item) {
        selectionList.set(item,NAMES[item],IMAGES[item]);
    }
    
    public static Image imageData(int color) {
        try {
            int len=w*h;
            int[] arrayInt=new int[len];
            
            for (int i=0; i<len;i++) {
                arrayInt[i]=color;
            }
            return Image.createRGBImage(arrayInt,w,h,false);
        } catch (Exception ex) { 
            //ex.printStackTrace(); 
        }
        
        return null;
    }
}