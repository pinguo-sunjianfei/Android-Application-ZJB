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
public class ServiceItemDecoration extends RecyclerView.ItemDecoration {
    int space;

    public ServiceItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        outRect.right = space;
        outRect.top = 0;
        outRect.bottom = space;
        outRect.left = 0;
    }
}
