/*
 * NativeScreenItem.java
 *
 * Pure data container for a single item in a VirtualList screen.
 * Zero framework dependencies — no J2ME, no Android imports.
 * Following SawimNE's VirtualListItem pattern.
 */
package ui;

/**
 * Data container for one row in a native screen's item list.
 * Holds display data; rendering is handled by the Compose/View layer.
 */
public class NativeScreenItem {

    /** Item key for identification in callbacks */
    public String key;

    /** Primary text label */
    public String label;

    /** Secondary description text (optional) */
    public String description;

    /** Image index in RosterIcons sprite sheet, or -1 for no icon */
    public int imageIndex = -1;

    /** Whether this item can be selected/focused */
    public boolean selectable = true;

    /** Font style: 0=plain, 1=bold. Maps to FontCache.FONT_STYLE_* */
    public int fontStyle;

    /** Theme color attribute for label text */
    public int labelColorAttr;

    /** Theme color attribute for description text */
    public int descColorAttr;

    /** Left margin in pixels for indentation */
    public int marginLeft;

    /** Group expansion listener — non-null if this item is a collapsible group header */
    public transient Object groupListener;
    public boolean isGroupOpened;

    public NativeScreenItem() {
    }

    public NativeScreenItem(boolean selectable) {
        this.selectable = selectable;
    }

    // --- Builder helpers, following SawimNE pattern ---

    public void addLabel(String text, int colorAttr, int fontStyle) {
        this.label = text;
        this.labelColorAttr = colorAttr;
        this.fontStyle = fontStyle;
    }

    public void addLabel(int marginLeft, String text, int colorAttr, int fontStyle) {
        this.marginLeft = marginLeft;
        addLabel(text, colorAttr, fontStyle);
    }

    public void addDescription(String text, int colorAttr, int fontStyle) {
        this.description = text;
        this.descColorAttr = colorAttr;
        this.fontStyle = fontStyle;
    }

    public void addDescription(int marginLeft, String text, int colorAttr, int fontStyle) {
        this.marginLeft = marginLeft;
        addDescription(text, colorAttr, fontStyle);
    }

    public void addImage(int imageIndex) {
        this.imageIndex = imageIndex;
    }

    public void addGroup(int marginLeft, String text, int colorAttr, int fontStyle, Object groupListener) {
        addImage(isGroupOpened ? GROUP_DOWN_ICON : GROUP_RIGHT_ICON);
        addDescription(marginLeft, text, colorAttr, fontStyle);
        this.groupListener = groupListener;
    }

    public boolean isItemSelectable() {
        return selectable;
    }

    // Group arrow icon indices — override in platform-specific code
    public static int GROUP_DOWN_ICON = -1;
    public static int GROUP_RIGHT_ICON = -1;
}
