/*
 * VirtualListController.java
 *
 * Singleton navigation controller bridging VirtualList to Compose UI.
 * The current VirtualList is rendered by ScreenHost via instanceof checks
 * on VirtualElement items — no parallel data model needed.
 */
package ui;

import Menu.MenuCommand;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Singleton that holds the currently displayed VirtualList and
 * notifies the Compose UI layer of updates.
 *
 * Business code calls VirtualList.show() which sets itself
 * as the current list. The Compose ScreenHost observes the
 * current list and renders controls by instanceof.
 */
public class VirtualListController {

    private static VirtualListController instance;

    private VirtualList currentList;
    private boolean isActive;

    private OnUpdateListener updateListener;

    /** For explicit dismiss actions (e.g., from Compose UI) */
    private Runnable onDismiss;
    private Runnable onCancel;

    // Back stack for navigation
    private final List<StackEntry> backStack = new ArrayList<StackEntry>();

    // --- Singleton ---

    public static VirtualListController getInstance() {
        if (instance == null) {
            synchronized (VirtualListController.class) {
                if (instance == null) {
                    instance = new VirtualListController();
                }
            }
        }
        return instance;
    }

    private VirtualListController() {
    }

    // --- Current list ---

    public VirtualList getCurrentList() {
        return currentList;
    }

    public void setCurrentList(VirtualList list) {
        if (currentList != null && currentList != list) {
            // Push current to back stack
            StackEntry entry = new StackEntry();
            entry.list = currentList;
            entry.caption = currentList.getMainBarText();
            backStack.add(entry);
        }
        this.currentList = list;
    }

    // --- Properties ---

    public String getCaption() {
        if (currentList != null) {
            String s = currentList.getMainBarText();
            if (s != null && !s.isEmpty()) return s;
        }
        return "BombusMod";
    }

    /**
     * Get menu commands from the current list.
     * Handles DefForm and VirtualList subclasses.
     */
    @SuppressWarnings("unchecked")
    public java.util.Vector<MenuCommand> getMenuCommands() {
        if (currentList instanceof ui.controls.form.DefForm) {
            return ((ui.controls.form.DefForm) currentList).menuCommands;
        }
        return new java.util.Vector<MenuCommand>();
    }

    /**
     * Get left bottom bar command label.
     */
    public String getRightCommand() {
        if (currentList != null) {
            String cmd = currentList.touchRightCommand();
            if (cmd != null && !cmd.isEmpty()) return cmd;
        }
        return "OK";
    }

    public String getLeftCommand() {
        if (currentList != null) {
            String cmd = currentList.touchLeftCommand();
            if (cmd != null && !cmd.isEmpty()) return cmd;
        }
        return "";
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    // --- Dismiss callbacks ---

    public void setOnDismiss(Runnable r) { onDismiss = r; }
    public Runnable getOnDismiss() { return onDismiss; }
    public void setOnCancel(Runnable r) { onCancel = r; }
    public Runnable getOnCancel() { return onCancel; }

    // --- Navigation ---

    public void notifyUpdate() {
        if (updateListener != null)
            updateListener.update();
    }

    public boolean popStack() {
        if (backStack.isEmpty()) return false;
        StackEntry entry = backStack.remove(backStack.size() - 1);
        this.currentList = entry.list;
        notifyUpdate();
        return true;
    }

    public boolean canGoBack() {
        return !backStack.isEmpty();
    }

    // --- Listener registration ---

    public void clearListeners() {
        updateListener = null;
    }

    public void clearAll() {
        clearListeners();
        currentList = null;
        caption = null;
        onDismiss = null;
        onCancel = null;
        backStack.clear();
    }
    // Compatibility: keep caption field for existing code that sets it directly
    private String caption;

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public void setUpdateListener(OnUpdateListener l) {
        updateListener = l;
    }

    // --- Item helpers ---

    public int getCurrItem() {
        if (updateListener != null)
            return updateListener.getCurrItem();
        return 0;
    }

    public void setCurrentItemIndex(int index, boolean isSelected) {
        if (updateListener != null)
            updateListener.setCurrentItemIndex(index, isSelected);
    }

    // --- Listener interface ---

    public interface OnUpdateListener {
        void update();
        void back();
        int getCurrItem();
        void setCurrentItemIndex(int index, boolean isSelected);
    }

    // --- Stack entry ---

    private static class StackEntry {
        VirtualList list;
        String caption;
    }
}
