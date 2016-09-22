package com.idrv.coach.ui.view.decoration;

import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * time:2016/6/27
 * description:
 *
 * @author sunjianfei
 */
public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {
    private int space;
    private int spanCount;
    private int lastItemInFirstLane = -1;
    private int headerCount;

    public GridSpacingItemDecoration(int space) {
        this(space, 1, 0);
    }

    /**
     * @param space
     * @param spanCount spans count of one lane
     */
    public GridSpacingItemDecoration(int space, int spanCount, int headerCount) {
        this.space = space;
        this.spanCount = spanCount;
        this.headerCount = headerCount;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
        int position = params.getViewPosition();
        final int spanSize;
        final int index;

        if (position > headerCount - 1) {
            position -= headerCount;
            if (params instanceof GridLayoutManager.LayoutParams) {
                GridLayoutManager.LayoutParams gridParams = (GridLayoutManager.LayoutParams) params;
                spanSize = gridParams.getSpanSize();
                index = gridParams.getSpanIndex();
            } else {
                spanSize = 1;
                index = position % spanCount;
            }
            // invalid value
            if (spanSize < 1 || index < 0) return;

            if (spanSize == spanCount) { // full span
                outRect.left = space;
                outRect.right = space;
            } else {
                if (index == 0) {  // left one
                    outRect.left = space;
                }
                // spanCount >= 1
                if (index == spanCount - 1) { // right one
                    outRect.right = space;
                }
                if (outRect.left == 0) {
                    outRect.left = space / 2;
                }
                if (outRect.right == 0) {
                    outRect.right = space / 2;
                }
            }
            // set top to all in first lane
            if (position < spanCount && spanSize <= spanCount) {
                if (lastItemInFirstLane < 0) { // lay out at first time
                    lastItemInFirstLane = position + spanSize == spanCount ? position : lastItemInFirstLane;
                    outRect.top = space;
                } else if (position <= lastItemInFirstLane) { // scroll to first lane again
                    outRect.top = space;
                }
            }
            outRect.bottom = space;

        }
    }
}
