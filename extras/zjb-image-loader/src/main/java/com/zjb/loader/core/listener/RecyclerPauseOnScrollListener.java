package com.zjb.loader.core.listener;

import android.support.v7.widget.RecyclerView;

import com.zjb.loader.ZjbImageLoader;

/**
 * time: 15/8/1
 * description: RecyclerView滑动时候是否加载图片
 *
 * @author sunjianfei
 */
public class RecyclerPauseOnScrollListener extends RecyclerView.OnScrollListener {

    private final boolean pauseOnScroll;
    private final boolean pauseOnFling;

    public RecyclerPauseOnScrollListener(boolean pauseOnScroll, boolean pauseOnFling) {
        this.pauseOnScroll = pauseOnScroll;
        this.pauseOnFling = pauseOnFling;
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        switch (newState) {
            case RecyclerView.SCROLL_STATE_IDLE:
                ZjbImageLoader.resume();
                break;
            case RecyclerView.SCROLL_STATE_DRAGGING:
                if (this.pauseOnScroll) {
                    ZjbImageLoader.pause();
                }
                break;
            case RecyclerView.SCROLL_STATE_SETTLING:
                if (this.pauseOnFling) {
                    ZjbImageLoader.pause();
                }
        }
    }

}
