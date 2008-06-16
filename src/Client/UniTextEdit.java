/*
 * UniTextEdit.java
 *
 * Created on 16 Июнь 2008 г., 9:48
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package Client;

//#ifdef ARCHIVE
import Archive.ArchiveList;
//#endif
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;
import locale.SR;
//#ifdef CLIPBOARD
//# import util.ClipBoard;
//#endif

/**
 *
 * @author ad
 */
public class UniTextEdit {

    private Display display;
    private Displayable parentView;
    
    public String body;
    private String subj;

    private Config cf;
    
    public TextBox t;
    
//#ifdef CLIPBOARD
//#     private ClipBoard clipboard;
//#endif
    
//#ifdef ARCHIVE
    private Command cmdPaste=new Command(SR.MS_ARCHIVE, Command.SCREEN, 6);    
//#endif
//#if TEMPLATES
    private Command cmdTemplate=new Command(SR.MS_TEMPLATE, Command.SCREEN, 7); 
//#endif  
//#ifdef CLIPBOARD
//#     private Command cmdPasteText=new Command(SR.MS_PASTE, Command.SCREEN, 8);  
//#endif
    
    /** Creates a new instance of UniTextEdit */
    public UniTextEdit(Display display, String body, String subj, int type) {
        this.display=display;
        parentView=display.getCurrent();

        cf=Config.getInstance();

        t=new TextBox(subj, "", 500, type);

        this.subj=subj;
		
        try {
            //expanding buffer as much as possible
            int maxSize=t.setMaxSize(4096); //must not trow

            if (body!=null) {
                if (body.length()>maxSize)
                    body=body.substring(0, maxSize-1);
                t.setString(body);
            }
         } catch (Exception e) {}


//#ifdef ARCHIVE
        t.addCommand(cmdPaste);
//#endif
//#ifdef CLIPBOARD
//#         if (cf.useClipBoard) {
//#             clipboard=ClipBoard.getInstance();
//#             if (!clipboard.isEmpty())
//#                 t.addCommand(cmdPasteText);
//#         }
//#endif
//#if TEMPLATES
        t.addCommand(cmdTemplate);
//#endif
        setInitialCaps(cf.capsState);
    }
    
    
    public void setParentView(Displayable parentView){
        this.parentView=parentView;
    }
    
    public void destroyView(){
        if (display!=null) display.setCurrent(parentView);
    }

    public int getCaretPos() {     
        int caretPos=t.getCaretPosition();
        // +MOTOROLA STUB
        if (cf.phoneManufacturer==Config.MOTO)
            caretPos=-1;
        if (caretPos<0)
            caretPos=t.getString().length();
        return caretPos;
    }
    
    public boolean executeCommand(Command c, Displayable displayable) {
        body=t.getString();
        
        int caretPos=getCaretPos();
		
        if (body.length()==0) body=null;

//#ifdef ARCHIVE
	if (c==cmdPaste) { new ArchiveList(display, caretPos, 1, t); return true; }
//#endif
//#ifdef CLIPBOARD
//#         if (c==cmdPasteText) { insertText(clipboard.getClipBoard(), getCaretPos()); return true; }
//#endif
//#if TEMPLATES
        if (c==cmdTemplate) { new ArchiveList(display, caretPos, 2, t); return true; }
//#endif

        return false;
    }
    
    
    public void insertText(String s, int caretPos) {
        String src=t.getString();

        StringBuffer sb=new StringBuffer(s);
        
        if (caretPos>0) 
            if (src.charAt(caretPos-1)!=' ')   
                sb.insert(0, ' ');
        
        if (caretPos<src.length())
            if (src.charAt(caretPos)!=' ')
                sb.append(' ');
        
        if (caretPos==src.length()) sb.append(' ');
        
        try {
            int freeSz=t.getMaxSize()-t.size();
            if (freeSz<sb.length()) sb.delete(freeSz, sb.length());
        } catch (Exception e) {}
       
        t.insert(sb.toString(), caretPos);
        sb=null;
    }
    
    private void setInitialCaps(boolean state) {
        t.setConstraints(state? TextField.INITIAL_CAPS_SENTENCE: TextField.ANY);
    }
}
