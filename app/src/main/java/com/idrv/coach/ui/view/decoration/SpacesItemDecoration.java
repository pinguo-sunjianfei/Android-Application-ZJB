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
public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
    private int bottomSpace;
    private int leftSpace;
    private int rightSpace;
    private int topSpace;
    boolean special;

    public SpacesItemDecoration(int l, int t, int r, int b, boolean special) {
        this.leftSpace = l;
        this.topSpace = t;
        this.rightSpace = r;
        this.bottomSpace = b;
        this.special = special;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        outRect.left = leftSpace;
        outRect.right = rightSpace;
        outRect.top = topSpace;

        if (parent.getChildAdapterPosition(view) == 0 && special) {
            outRect.bottom = bottomSpace / 2;
        } else {
            outRect.bottom = bottomSpace;
        }
    }
}
