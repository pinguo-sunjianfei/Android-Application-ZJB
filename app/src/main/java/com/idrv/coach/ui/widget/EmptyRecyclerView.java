package com.idrv.coach.ui.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * time: 15/7/17
 * description:包含空view的recycler view
 *
 * @author sunjianfei
 */
public class EmptyRecyclerView extends RecyclerView {
    private View mEmptyView;
    private boolean mToggle;

    final private AdapterDataObserver observer = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            checkIfEmpty();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            checkIfEmpty();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            checkIfEmpty();
        }
    };

    public EmptyRecyclerView(Context context) {
        super(context);
    }

    public EmptyRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public EmptyRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    void checkIfEmpty() {
        if (mEmptyView != null && getAdapter() != null) {
            if (mToggle) {
                final boolean emptyViewVisible = getAdapter().getItemCount() == 1;
                mEmptyView.setVisibility(emptyViewVisible ? VISIBLE : GONE);
                setVisibility(VISIBLE);
            } else {
                final boolean emptyViewVisible = getAdapter().getItemCount() == 0;
                mEmptyView.setVisibility(emptyViewVisible ? VISIBLE : GONE);
                setVisibility(emptyViewVisible ? GONE : VISIBLE);
            }
        }
    }

    @Override
    public void setAdapter(Adapter adapter) {
        final Adapter oldAdapter = getAdapter();
        if (oldAdapter != null) {
            oldAdapter.unregisterAdapterDataObserver(observer);
        }
        super.setAdapter(adapter);
        if (adapter != null) {
            adapter.registerAdapterDataObserver(observer);
        }
        checkIfEmpty();
    }

    /**
     * 解决专辑主页有header view也要显示空view
     */
    public void toggleHeaderStatus(boolean status) {
        mToggle = status;
    }

    /**
     * 设置空view
     */
    public void setEmptyView(View mEmptyView) {
        this.mEmptyView = mEmptyView;
        checkIfEmpty();
    }
}
