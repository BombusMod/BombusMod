/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

//#ifdef USER_KEYS

package ui.controls.form;

import ui.IconTextElement;
import images.RosterIcons;
import ui.keys.UserKey;
import ui.keys.UserKeyExec;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Font;
import Fonts.FontCache;
import Colors.ColorTheme;

/**
 *
 * @author Totktonada
 */
public class KeyInput extends IconTextElement {

    public boolean selected;
    public int keyCode;

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

        this.caption = (caption == null) ? "" : caption;
        font = FontCache.getFont(false, FontCache.roster);
        fontHeight = font.getHeight();

        captionFont = FontCache.getFont(true, FontCache.msg);
        captionFontHeight = captionFont.getHeight();
    }

    public KeyInput(int keyCode, String caption) {
        this(caption);
        this.keyCode = keyCode;
    }

    public void onSelect() {
        selected = true;
    }

    public void key(int keyCode) {
        this.keyCode = keyCode;
        selected = false;
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
        FontCache.drawString(g,caption, thisOfs, y, Graphics.TOP | Graphics.LEFT);
        y = captionFontHeight;

        g.setColor(colorBGnd);
        g.fillRect(0, y, width-1, height-1);

        g.setColor((sel)?colorBorder:colorItem);
        g.drawRect(0, y, width-1, height-1);

        g.setColor(oldColor);

        if (getTextLength()>0) {
            thisOfs=(getTextLength()>width)?-ofs+4:4;
            g.setFont(font);
            FontCache.drawString(g,toString(), thisOfs, y, Graphics.TOP|Graphics.LEFT);
        }
    }

    public int getVHeight() {
        return captionFontHeight+fontHeight;
    }

    public int getCaptionLength() {
        if (caption.length() == 0)
            return 0;
        return captionFont.stringWidth(caption);
    }

    public int getTextLength() {
        String text=toString();
        if (text.length() == 0)
            return 0;
        return font.stringWidth(text);
    }

    public String toString() {
        if (selected) {
            return "Key?";
        } else {
            return UserKey.getKeyName(keyCode, false);
        }
    }
}

//#endif
