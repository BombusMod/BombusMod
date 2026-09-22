/*
 * NativeScreenCommand.java
 *
 * Pure data container for a menu/command item.
 * Zero framework dependencies — no J2ME, no Android imports.
 * Following SawimNE's MyMenuItem pattern.
 */
package ui;

/**
 * Data container for a single command in a screen's menu.
 * Mirrors Menu.MenuCommand without the J2ME IconTextElement inheritance.
 */
public class NativeScreenCommand {

    // Command type constants — mirror MenuCommand's types
    public static final int OK = 1;
    public static final int SCREEN = 2;
    public static final int BACK = 3;
    public static final int EXIT = 4;
    public static final int CANCEL = 5;
    public static final int ITEM = 6;

    /** Unique key for identifying this command in callbacks */
    public String key;

    /** Display label */
    public String label;

    /** Command type: OK, SCREEN, BACK, EXIT, CANCEL, or ITEM */
    public int type;

    /** Image index in RosterIcons sprite sheet, or -1 for no icon */
    public int imageIndex = -1;

    /** Priority for ordering (lower = higher priority) */
    public int priority;

    public NativeScreenCommand() {
    }

    public NativeScreenCommand(String key, String label, int type, int imageIndex) {
        this.key = key;
        this.label = label;
        this.type = type;
        this.imageIndex = imageIndex;
    }

    public NativeScreenCommand(String key, String label, int type, int imageIndex, int priority) {
        this(key, label, type, imageIndex);
        this.priority = priority;
    }
}
