package com.idrv.coach.ui.view.decoration;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * time:2016/3/23
 * description:
 *
 * @author sunjianfei
 */
public class PhotoWallItemDecoration extends RecyclerView.ItemDecoration {
    int space;

    public PhotoWallItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        int childPos = parent.getChildAdapterPosition(view);
        if (childPos == 0 || childPos == 1) {
            outRect.right = 0;
            outRect.top = 0;
            outRect.bottom = 0;
            outRect.left = 0;
        } else {
            childPos = childPos - 1;
            outRect.right = 0;
            outRect.top = 0;
            outRect.bottom = space;
            if (childPos == 1 || childPos % 4 == 1) {
                outRect.left = space;
            } else {
                outRect.left = 0;
            }
        }
    }

}
