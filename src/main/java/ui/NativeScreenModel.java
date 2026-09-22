/*
 * NativeScreenModel.java
 *
 * Pure data list model for a screen's items and commands.
 * Zero framework dependencies — no J2ME, no Android imports.
 * Following SawimNE's VirtualListModel pattern.
 */
package ui;

import java.util.ArrayList;
import java.util.List;

/**
 * Data model for a VirtualList screen.
 * Holds the list of display items and available commands.
 * Helper methods mirror SawimNE's VirtualListModel.addParam/addItem/addHeader pattern.
 */
public class NativeScreenModel {

    /** Screen items (each row in the list) */
    public final List<NativeScreenItem> elements = new ArrayList<NativeScreenItem>();

    /** Available commands/menu items */
    public final List<NativeScreenCommand> commands = new ArrayList<NativeScreenCommand>();

    /** Pending header text — defer adding until next item is added */
    private String pendingHeader;

    // --- Item builders ---

    public NativeScreenItem createNewItem(boolean selectable) {
        return new NativeScreenItem(selectable);
    }

    public void addPar(NativeScreenItem item) {
        elements.add(item);
    }

    /** Add a simple selectable item with label */
    public void addItem(String text, boolean active) {
        int fontStyle = active ? 1 : 0; // bold if active
        NativeScreenItem item = createNewItem(true);
        item.addLabel(text, 0, fontStyle);
        addPar(item);
    }

    /** Add a labeled parameter row: "Label: Value" */
    public void addParam(String label, String value) {
        if (value == null || value.isEmpty()) return;
        flushHeader();
        NativeScreenItem item = createNewItem(true);
        item.addLabel(label + ": ", 0, 0);
        item.addDescription(value, 1, 0); // 1 = param_value color
        addPar(item);
    }

    /** Add a labeled parameter with image */
    public void addParamImage(String label, int imageIndex) {
        flushHeader();
        NativeScreenItem item = createNewItem(true);
        if (label != null && !label.isEmpty()) {
            item.addLabel(label + ": ", 0, 0);
        }
        item.addImage(imageIndex);
        addPar(item);
    }

    /** Set a header that will appear before the next added item */
    public void setHeader(String header) {
        this.pendingHeader = header;
    }

    /** Add a section header item */
    public void addHeader(String text) {
        NativeScreenItem item = createNewItem(false);
        item.addLabel(text, 0, 1); // bold
        addPar(item);
    }

    /** Add an informational message (non-selectable) */
    public void addInfoMessage(String text) {
        NativeScreenItem item = createNewItem(false);
        item.addDescription(text, 0, 0);
        addPar(item);
    }

    /** Add a command to the screen's menu */
    public void addCommand(String key, String label, int type, int imageIndex) {
        commands.add(new NativeScreenCommand(key, label, type, imageIndex));
    }

    public void addCommand(String key, String label, int type, int imageIndex, int priority) {
        commands.add(new NativeScreenCommand(key, label, type, imageIndex, priority));
    }

    // --- Lifecycle ---

    public void clear() {
        elements.clear();
        commands.clear();
        pendingHeader = null;
    }

    public int getSize() {
        return elements.size();
    }

    public boolean isItemSelectable(int index) {
        if (index < 0 || getSize() <= index) return false;
        return elements.get(index).isItemSelectable();
    }

    // --- Internal ---

    private void flushHeader() {
        if (pendingHeader != null) {
            addHeader(pendingHeader);
            pendingHeader = null;
        }
    }
}
