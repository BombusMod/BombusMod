/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ui.controls.form;
import ui.IconTextElement;
import images.RosterIcons;
import ui.keys.UserKey;
import javax.microedition.lcdui.*;
import Fonts.FontCache;
import Colors.ColorTheme;

/**
 *
 * @author Totktonada
 */
public class KeyInput extends IconTextElement {
//#ifdef PLUGINS
//#     public static String plugin = new String("PLUGIN_USER_KEYS");
//#endif

    private boolean expected_first_key;

    public boolean two_keys;
    public boolean selected;
    public int previous_key;
    public int key;
    
    private int colorItem;
    private int colorBorder;
    private int colorBGnd;
    
    private String caption = "";
    
    private Font font;
    private int fontHeight;
    
    private Font captionFont;
    private int captionFontHeight;

    public KeyInput(String caption) {
        super(RosterIcons.getInstance());
        expected_first_key = true;
        
        this.caption = (caption == null) ? "" : caption;
        font = FontCache.getFont(false, FontCache.roster);
        fontHeight = font.getHeight();
        
        captionFont = FontCache.getFont(true, FontCache.msg);
        captionFontHeight = captionFont.getHeight();
    }
    
    public KeyInput(UserKey u, String caption) {
        this(caption);
        two_keys = u.two_keys;
        previous_key = u.previous_key;
        key = u.key;
    }

    public void onSelect() {
        selected = true;
    }

    public void keyPressed(int key_code) {
        if (two_keys) {
            if (expected_first_key) {
                previous_key = key_code;
                expected_first_key = false;
            } else {
                key = key_code;
                expected_first_key = true;
                selected = false;
            }
        } else {
            key = key_code;
            selected = false;
        }
    }

    public void drawItem(Graphics g, int ofs, boolean sel) {
        colorItem=ColorTheme.getColor(ColorTheme.CONTROL_ITEM);
        colorBorder=ColorTheme.getColor(ColorTheme.CURSOR_OUTLINE);
        colorBGnd=ColorTheme.getColor(ColorTheme.LIST_BGND);

        int width=g.getClipWidth();
        int height=fontHeight;

        int oldColor=g.getColor();

        int thisOfs=0;

        int y = 0;
        thisOfs = (getCaptionLength() > width) ? -ofs : 2;
        g.setFont(captionFont);
        g.drawString(caption, thisOfs, y, Graphics.TOP | Graphics.LEFT);
        y = captionFontHeight;

        g.setColor(colorBGnd);
        g.fillRect(0, y, width-1, height-1);

        g.setColor((sel)?colorBorder:colorItem);
        g.drawRect(0, y, width-1, height-1);

        g.setColor(oldColor);

        if (getTextLength()>0) {
            thisOfs=(getTextLength()>width)?-ofs+4:4;
            g.setFont(font);
            g.drawString(toString(), thisOfs, y, Graphics.TOP|Graphics.LEFT);
        }
    }
    
    public int getVHeight() {
        return captionFontHeight+fontHeight;
    }
    
    public int getCaptionLength() {
        if (caption.equals(""))
            return 0;
        return captionFont.stringWidth(caption);
    }
    
    public int getTextLength() {
        String text=toString();
        if (text.equals(""))
            return 0;
        return font.stringWidth(text);
    }

    public String toString() {
        if (two_keys) {
            if (selected) {
                if (expected_first_key) {
                    return "First key?";
                } else {
                    return UserKey.keyToString(previous_key) + " + ?";
                }
            } else {
                return UserKey.keyToString(previous_key) + " + " + UserKey.keyToString(key);
            }
        } else {
            if (selected) {
                return "Key?";
            } else {
                return UserKey.keyToString(key);
            }
        }
    }
}
