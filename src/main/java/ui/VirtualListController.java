/*
 * VirtualListController.java
 *
 * Singleton navigation controller bridging business logic to native UI.
 * Replaces VirtualCanvas as the screen-show mechanism.
 *
 * Following SawimNE's VirtualList pattern:
 *   VirtualListController.getInstance().setCaption("Title");
 *   VirtualListController.getInstance().setModel(model);
 *   VirtualListController.getInstance().setClickListListener(this);
 *   VirtualListController.getInstance().show(activity);
 *
 * Named VirtualListController to avoid conflict with the existing
 * VirtualList J2ME class. Will be renamed to VirtualList after
 * the old class is removed (Phase 8 cleanup).
 */
package ui;

import java.util.ArrayList;
import java.util.List;

/**
 * Singleton controller that manages the current screen's model and
 * mediates between business logic and the native rendering layer.
 *
 * Business code calls setModel() and show(). The native UI layer
 * (ComposeHostActivity / ScreenHost) observes the model and
 * renders accordingly.
 */
public class VirtualListController {

    private static VirtualListController instance;

    private NativeScreenModel model;
    private String caption;
    private boolean isActive;

    // Listener interfaces, following SawimNE pattern
    private OnUpdateListener updateListener;
    private OnClickListListener clickListListener;
    private OnBuildOptionsMenu buildOptionsMenu;
    private OnBuildContextMenu buildContextMenu;

    // Callback to dismiss native UI and return to legacy screen
    private Runnable onDismiss;

    public void setOnDismiss(Runnable r) { onDismiss = r; }
    public Runnable getOnDismiss() { return onDismiss; }

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

    // --- Properties ---

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getCaption() {
        return caption;
    }

    public void setModel(NativeScreenModel model) {
        this.model = model;
    }

    public NativeScreenModel getModel() {
        return model;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    // --- Navigation ---

    /**
     * Show the current model. On Android with native UI active,
     * this triggers Compose recomposition. On legacy platforms,
     * delegates to VirtualCanvas.
     */
    public void show(Object activity) {
        if (!isActive) {
            // Legacy path — use old VirtualCanvas
            return;
        }
        // Push current model to back stack, notify UI
        notifyUpdate();
    }

    /**
     * Pop back to the previous screen.
     */
    public void back() {
        if (updateListener != null) {
            updateListener.back();
        }
    }

    /**
     * Push a screen onto the navigation back stack.
     * Called internally by the ComposeHostActivity.
     */
    public void pushStack(NativeScreenModel model, String caption,
                          OnClickListListener clickListener,
                          OnBuildOptionsMenu menuListener) {
        StackEntry entry = new StackEntry();
        entry.model = this.model;
        entry.caption = this.caption;
        entry.clickListener = this.clickListListener;
        entry.menuListener = this.buildOptionsMenu;
        backStack.add(entry);

        this.model = model;
        this.caption = caption;
        this.clickListListener = clickListener;
        this.buildOptionsMenu = menuListener;
    }

    /**
     * Pop from the back stack. Returns true if there was a previous screen.
     */
    public boolean popStack() {
        if (backStack.isEmpty()) return false;
        StackEntry entry = backStack.remove(backStack.size() - 1);
        this.model = entry.model;
        this.caption = entry.caption;
        this.clickListListener = entry.clickListener;
        this.buildOptionsMenu = entry.menuListener;
        return true;
    }

    public boolean canGoBack() {
        return !backStack.isEmpty();
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

    // --- Listener registration ---

    public void clearListeners() {
        updateListener = null;
        buildOptionsMenu = null;
        buildContextMenu = null;
        clickListListener = null;
    }

    public void clearAll() {
        clearListeners();
        if (model != null) {
            model.clear();
            model = null;
        }
        caption = null;
        backStack.clear();
    }

    public void setUpdateListener(OnUpdateListener l) {
        updateListener = l;
    }

    public void setClickListListener(OnClickListListener l) {
        clickListListener = l;
    }

    public OnClickListListener getClickListListener() {
        return clickListListener;
    }

    public void setBuildOptionsMenu(OnBuildOptionsMenu l) {
        buildOptionsMenu = l;
    }

    public OnBuildOptionsMenu getBuildOptionsMenu() {
        return buildOptionsMenu;
    }

    public void setBuildContextMenu(OnBuildContextMenu l) {
        buildContextMenu = l;
    }

    public OnBuildContextMenu getBuildContextMenu() {
        return buildContextMenu;
    }

    // --- Notify UI ---

    public void notifyUpdate() {
        if (updateListener != null)
            updateListener.update();
    }

    // --- Listener Interfaces (following SawimNE) ---

    /**
     * Called when the model data has changed and the UI should refresh.
     */
    public interface OnUpdateListener {
        void update();
        void back();
        int getCurrItem();
        void setCurrentItemIndex(int index, boolean isSelected);
    }

    /**
     * Called when an item in the list is clicked/selected.
     */
    public interface OnClickListListener {
        void itemSelected(Object activity, int position);
        boolean back();
    }

    /**
     * Called to build the options menu (ActionBar/Toolbar menu).
     */
    public interface OnBuildOptionsMenu {
        void onCreateOptionsMenu(Object menu);
        void onOptionsItemSelected(Object activity, Object menuItem);
    }

    /**
     * Called to build a context menu (long-press menu on list items).
     */
    public interface OnBuildContextMenu {
        void onCreateContextMenu(Object contextMenu, int listItem);
        void onContextItemSelected(Object activity, int listItem, int itemMenuId);
    }

    // --- Stack entry ---

    private static class StackEntry {
        NativeScreenModel model;
        String caption;
        OnClickListListener clickListener;
        OnBuildOptionsMenu menuListener;
    }
}
