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

package Colors;

//#if (FILE_IO)
import io.file.FileIO;
import io.file.browse.Browser;
import io.file.browse.BrowserListener;
//#endif
import javax.microedition.lcdui.*;
import locale.SR;
import Colors.Colors;
import ui.*;

public class ColorForm implements CommandListener
//#if (FILE_IO && COLORS)
        , BrowserListener
//#endif
{
    private Displayable currentChoice = null;
    private Display display;
    private Displayable parentView;
      
    private static Colors cs=Colors.getInstance();
      
    private final static int w=6;
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

            SR.MS_HEAP_TOTAL,
            SR.MS_HEAP_FREE,
            SR.MS_CURSOR_BGND,

            SR.MS_CURSOR_OUTLINE,
            SR.MS_SCROLL_BRD,
            SR.MS_SCROLL_BAR,
            SR.MS_SCROLL_BGND,
            
            SR.MS_CONTACT+"J2J",
            
            SR.MS_MESSAGE_IN_S,
            SR.MS_MESSAGE_OUT_S,
            SR.MS_MESSAGE_PRESENCE_S

        };
        
        public static int[] COLORS = {            
            cs.BALLOON_INK,
            cs.BALLOON_BGND,
            cs.LIST_BGND,
            cs.LIST_BGND_EVEN,
            cs.LIST_INK,
            
            cs.MSG_SUBJ,
            cs.MSG_HIGHLIGHT,
            cs.DISCO_CMD,
            cs.BAR_BGND,
            cs.BAR_BGND_BOTTOM,
            cs.BAR_INK,
            
            cs.CONTACT_DEFAULT,
            cs.CONTACT_CHAT,
            cs.CONTACT_AWAY,
            cs.CONTACT_XA,
            cs.CONTACT_DND,
            
            cs.GROUP_INK,
            cs.BLK_INK,
            cs.BLK_BGND,
            cs.MESSAGE_IN,
            cs.MESSAGE_OUT,
            cs.MESSAGE_PRESENCE,
            
            cs.MESSAGE_AUTH,
            cs.MESSAGE_HISTORY,
            cs.PGS_REMAINED,
            cs.PGS_COMPLETE,

            cs.HEAP_TOTAL,
            cs.HEAP_FREE,
            cs.CURSOR_BGND,
            
            cs.CURSOR_OUTLINE,
            cs.SCROLL_BRD,
            cs.SCROLL_BAR,
            cs.SCROLL_BGND,
            
            cs.CONTACT_J2J
//#ifdef NICK_COLORS
            ,cs.MESSAGE_IN_S,
            cs.MESSAGE_OUT_S,
            cs.MESSAGE_PRESENCE_S
//#endif
      };
        
      
    public static Image[] IMAGES= {
            imageData(cs.BALLOON_INK),
            imageData(cs.BALLOON_BGND),
            imageData(cs.LIST_BGND),
            imageData(cs.LIST_BGND_EVEN),
            imageData(cs.LIST_INK),

            imageData(cs.MSG_SUBJ),
            imageData(cs.MSG_HIGHLIGHT),
            imageData(cs.DISCO_CMD),
            imageData(cs.BAR_BGND),
            imageData(cs.BAR_BGND_BOTTOM),
            imageData(cs.BAR_INK),

            imageData(cs.CONTACT_DEFAULT),
            imageData(cs.CONTACT_CHAT),
            imageData(cs.CONTACT_AWAY),
            imageData(cs.CONTACT_XA),
            imageData(cs.CONTACT_DND),
                    
            imageData(cs.GROUP_INK),
            imageData(cs.BLK_INK),
            imageData(cs.BLK_BGND),
            imageData(cs.MESSAGE_IN),
            imageData(cs.MESSAGE_OUT),
            imageData(cs.MESSAGE_PRESENCE),
            
            imageData(cs.MESSAGE_AUTH),
            imageData(cs.MESSAGE_HISTORY),
            imageData(cs.PGS_REMAINED),
            imageData(cs.PGS_COMPLETE),

            imageData(cs.HEAP_TOTAL),
            imageData(cs.HEAP_FREE),
            imageData(cs.CURSOR_BGND),

            imageData(cs.CURSOR_OUTLINE),
            imageData(cs.SCROLL_BRD),
            imageData(cs.SCROLL_BAR),
            imageData(cs.SCROLL_BGND),
            imageData(cs.CONTACT_J2J)
//#ifdef NICK_COLORS
            , imageData(cs.MESSAGE_IN_S),
            imageData(cs.MESSAGE_OUT_S),
            imageData(cs.MESSAGE_PRESENCE_S)
//#endif
      };


    private static List selectionList;

//#if (FILE_IO)
    Command cmdSaveSkin=new Command(SR.MS_SAVE, Command.ITEM,3); 
    Command cmdLoadSkinFS=new Command(SR.MS_LOAD_SKIN+"FS", Command.ITEM,4);
    String filePath;
//#endif
    
    private Command cmdCancel=new Command(SR.MS_CLOSE, Command.BACK, 99);
    private Command selectCommand = new Command(SR.MS_EDIT, Command.ITEM, 1);

    private int loadType=0;
    
    public ColorForm(Display display) {
        super();
        this.display=display;
        parentView=display.getCurrent();

        selectionList = new List(SR.MS_COLOR_TUNE, List.IMPLICIT, NAMES, IMAGES);

        selectionList.setSelectCommand(selectCommand);

//#if (FILE_IO)
        selectionList.addCommand(cmdSaveSkin);
        selectionList.addCommand(cmdLoadSkinFS);
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
        if (c==cmdSaveSkin) {
            loadType=0;
            new Browser(null,display, this, true);
        }
        if (c==cmdLoadSkinFS) {
            loadType=1;
            new Browser(null, display, this, false);
        }
//#endif
        
        if (c==selectCommand) {
          try {
              if (!NAMES[pos].startsWith("(n/a)") && pos != NAMES.length) {
//#if (COLORS)
                new ColorSelector(display, pos);
//#endif
              }
          } catch(Exception err) {}
       }
    }


//#if (FILE_IO && COLORS)
    public void BrowserFilePathNotify(String pathSelected) {
        if (loadType==0) {
            FileIO file=FileIO.createConnection(pathSelected+"skin.txt");
            file.fileWrite(ColorUtils.getSkin().getBytes());
        } else {
            ColorUtils.loadSkin(pathSelected, 0);
        }
    }
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
        } catch (Exception ex) { }
        
        return null;
    }
}