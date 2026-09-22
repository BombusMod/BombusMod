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

    private Object font;
    private int fontHeight;

    private Object captionFont;
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
