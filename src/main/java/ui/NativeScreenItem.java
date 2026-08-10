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

    // Control type constants — following SawimNE's Forms.Control pattern
    public static final byte TYPE_TEXT = 0;       // simple text row
    public static final byte TYPE_INPUT = 1;      // text field
    public static final byte TYPE_CHECKBOX = 2;   // checkbox
    public static final byte TYPE_DROPDOWN = 3;   // dropdown selector
    public static final byte TYPE_SLIDER = 4;     // slider
    public static final byte TYPE_HEADER = 5;     // section header
    public static final byte TYPE_LINK = 6;       // clickable link
    public static final byte TYPE_MULTILINE = 7;  // multi-line text
    public static final byte TYPE_SPACER = 8;     // blank space
    public static final byte TYPE_IMAGE = 9;      // image display
    public static final byte TYPE_NUMBER = 10;    // numeric input
    public static final byte TYPE_PASSWORD = 11;  // password input

    /** Item key for identification in callbacks */
    public String key;

    /** Control type discriminator — defaults to TYPE_TEXT */
    public byte controlType = TYPE_TEXT;

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

    // Control state — checked for checkbox, value for slider, text for input
    public boolean checked;
    public String textValue = "";
    public int intValue;
    public float floatValue;

    // Dropdown options
    public String[] options;

    // Slider range
    public float sliderMin;
    public float sliderMax = 100f;

    // ── Convenience setters for building controls ──

    public void setAsCheckBox(String label, boolean checked) {
        this.controlType = TYPE_CHECKBOX;
        this.label = label;
        this.checked = checked;
        this.selectable = true;
    }

    public void setAsInput(String id, String caption, String value) {
        this.controlType = TYPE_INPUT;
        this.key = id;
        this.label = caption;
        this.textValue = value != null ? value : "";
        this.selectable = true;
    }

    public void setAsNumber(String id, String caption, int value) {
        this.controlType = TYPE_NUMBER;
        this.key = id;
        this.label = caption;
        this.intValue = value;
        this.selectable = true;
    }

    public void setAsPassword(String id, String caption) {
        this.controlType = TYPE_PASSWORD;
        this.key = id;
        this.label = caption;
        this.selectable = true;
    }

    public void setAsDropdown(String id, String caption, String[] options, int selected) {
        this.controlType = TYPE_DROPDOWN;
        this.key = id;
        this.label = caption;
        this.options = options;
        this.intValue = selected;
        this.selectable = true;
    }

    public void setAsSlider(String id, String caption, float value, float min, float max) {
        this.controlType = TYPE_SLIDER;
        this.key = id;
        this.label = caption;
        this.floatValue = value;
        this.sliderMin = min;
        this.sliderMax = max;
        this.selectable = true;
    }

    public void setAsHeader(String text) {
        this.controlType = TYPE_HEADER;
        this.label = text;
        this.selectable = false;
    }

    public void setAsLink(String text) {
        this.controlType = TYPE_LINK;
        this.label = text;
        this.selectable = true;
    }

    public void setAsMultiline(String text) {
        this.controlType = TYPE_MULTILINE;
        this.description = text;
        this.selectable = false;
    }

    public void setAsSpacer(int heightDp) {
        this.controlType = TYPE_SPACER;
        this.intValue = heightDp;
        this.selectable = false;
    }

    // Group arrow icon indices — override in platform-specific code
    public static int GROUP_DOWN_ICON = -1;
    public static int GROUP_RIGHT_ICON = -1;
}
