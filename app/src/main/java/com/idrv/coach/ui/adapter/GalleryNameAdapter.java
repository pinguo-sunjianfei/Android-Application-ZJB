package com.idrv.coach.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.idrv.coach.R;
import com.idrv.coach.bean.Gallery;
import com.idrv.coach.utils.helper.ResHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * time: 2015/10/23
 * description:
 *
 * @author sunjianfei
 */
public class GalleryNameAdapter extends RecyclerView.Adapter<GalleryNameAdapter.ViewHolder> {
    private List<Gallery> mGalleries = new ArrayList<>();
    private int mItemSize = (int) ResHelper.getDimen(R.dimen.gallery_pop_item_height);
    private Context mContext;
    private OnRecyclerItemClickListener mOnRecyclerItemClickListener;

    public GalleryNameAdapter(Context context, List<Gallery> galleryList) {
        this.mContext = context;
        mGalleries.clear();
        mGalleries.addAll(galleryList);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.vw_gallery_list_item, null));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, mItemSize);
        holder.itemView.setLayoutParams(params);
        Gallery gallery = mGalleries.get(position);
        holder.tv.setText(gallery.getGalleryName());
        holder.line.setVisibility(position == getItemCount() ? View.GONE : View.VISIBLE);
        holder.itemView.setOnClickListener(v -> {
            if (null != mOnRecyclerItemClickListener) {
                mOnRecyclerItemClickListener.onItemClick(position, holder.itemView);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mGalleries.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.list_item_text)
        public TextView tv;
        @InjectView(R.id.list_item_line)
        public View line;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

    public void setOnRecyclerItemClickListener(OnRecyclerItemClickListener listener) {
        this.mOnRecyclerItemClickListener = listener;
    }

    public interface OnRecyclerItemClickListener {
        void onItemClick(int position, View view);
    }
}
