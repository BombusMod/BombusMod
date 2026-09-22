package ui.controls;

import Client.Config;
import Colors.ColorTheme;
import ui.VirtualList;

public class ScrollBar {
    private static final int WIDTH_SCROLL_1 = 4;
    private Config cf = Config.getInstance();
    private final int WIDTH_SCROLL_2 = cf.widthScroll2 + 2;

    private int yTranslate;
    private int size;
    private int windowSize;
    private int position;
    private int scrollerX;
    private int drawHeight;
    private int point_y;
    private int scrollerSize;
    private int scrollerPos;
    private int minimumHeight = 3;
    private int scrollWidth = WIDTH_SCROLL_1;
    private int colorTop;
    private int colorBar;
    private int colorBorder;

    public ScrollBar() {
        point_y = -1;
        colorTop = ColorTheme.getColor(ColorTheme.SCROLL_BGND);
        colorBar = ColorTheme.getColor(ColorTheme.SCROLL_BAR);
        colorBorder = ColorTheme.getColor(ColorTheme.SCROLL_BRD);
    }

    public void setWindowSize(int windowSize) { this.windowSize = windowSize; }
    public void setSize(int size) { this.size = size; }
    public int getPostion() { return position; }
    public void setPostion(int postion) { this.position = postion; }
    public void setHasPointerEvents(boolean hasPointerEvents) { scrollWidth = (hasPointerEvents) ? WIDTH_SCROLL_2 : WIDTH_SCROLL_1; }
    public int getScrollWidth() { return scrollWidth; }

    public boolean pointerPressed(int x, int y, VirtualList v) {
        if (size == 0) return false;
        if (x < scrollerX) return false;
        y -= yTranslate;
        if (y < scrollerPos) { int pos = position - windowSize; if (pos < 0) pos = 0; v.win_top = pos; return true; }
        if (y > scrollerPos + scrollerSize) { int pos = position + windowSize; int listEnd = size - windowSize; v.win_top = (pos < listEnd) ? pos : listEnd; return true; }
        point_y = y - scrollerPos;
        return true;
    }

    public boolean pointerDragged(int x, int y, VirtualList v) {
        y -= yTranslate;
        if (point_y < 0) return false;
        int new_top = y - point_y;
        int new_pos = (new_top * size) / drawHeight;
        if ((position - new_pos) == 0) return true;
        if (new_pos < 0) new_pos = 0;
        if (new_pos + windowSize > size) new_pos = size - windowSize;
        v.win_top = new_pos;
        return true;
    }

    public boolean pointerReleased(int x, int y, VirtualList v) {
        if (point_y >= 0) { point_y = -1; return true; }
        if (size == 0) return false;
        if (x < scrollerX) return false;
        return true;
    }
}
