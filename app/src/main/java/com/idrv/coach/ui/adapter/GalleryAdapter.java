package com.idrv.coach.ui.adapter;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.idrv.coach.R;
import com.idrv.coach.bean.GalleryPhoto;
import com.idrv.coach.data.manager.LocalPhotoManager;
import com.idrv.coach.ui.widget.OnPressImageView;
import com.idrv.coach.utils.helper.UIHelper;
import com.zjb.loader.ZjbImageLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;

/**
 * time: 2015/9/23
 * description:
 *
 * @author sunjianfei
 */
public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder> {
    private Context mContext;
    private boolean openCamera;
    private LayoutInflater mInflater;
    private Map<Integer, String> mMapPhotos = new HashMap<>();
    private List<GalleryPhoto> mGalleryPhotos = new ArrayList<>();
    /*注意：mItemSize的值不能随意修改，在LocalPhotoManager当中会用到*/
    private int mItemSize;

    //选择的照片
    private ArrayList<String> mSelectPhotos = new ArrayList<>();

    private OnRecyclerItemClickListener mOnRecyclerItemClickListener;

    public GalleryAdapter(Context context, boolean needCamera) {
        this.mContext = context;
        this.mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mItemSize = (int) (mContext.getResources().getDisplayMetrics().widthPixels / 4.f + 0.5f - 3.f);
        this.openCamera = needCamera;
        initAdapter();
    }

    private void initAdapter() {
        this.mMapPhotos.putAll(LocalPhotoManager.getInstance().getLocalPhotos());
        this.mGalleryPhotos.clear();
        for (Map.Entry<Integer, String> entry : mMapPhotos.entrySet()) {
            GalleryPhoto galleryPhoto = new GalleryPhoto();
            galleryPhoto.setResId(entry.getKey());
            mGalleryPhotos.add(galleryPhoto);
        }
        Collections.sort(mGalleryPhotos, (lhs, rhs) -> rhs.getResId() - lhs.getResId());
    }

    public ArrayList<String> getSelectPhotos() {
        return mSelectPhotos;
    }

    public void refresh() {
        this.mMapPhotos = LocalPhotoManager.getInstance().getLocalPhotos();
        this.mItemSize = (int) (mContext.getResources().getDisplayMetrics().widthPixels / 4.f + 0.5f - 3.f);
        initAdapter();
    }

    public void refresh(TreeMap<Integer, String> photos) {
        //1.重置map
        mMapPhotos.clear();
        mMapPhotos.putAll(photos);
        //2.重置photos
        this.mGalleryPhotos.clear();
        for (Map.Entry<Integer, String> entry : mMapPhotos.entrySet()) {
            GalleryPhoto galleryPhoto = new GalleryPhoto();
            galleryPhoto.setResId(entry.getKey());
            mGalleryPhotos.add(galleryPhoto);
        }
    }

    public void setOnRecyclerItemClickListener(OnRecyclerItemClickListener listener) {
        this.mOnRecyclerItemClickListener = listener;
    }

    public String getUrl(int position) {
        GalleryPhoto galleryPhoto = mGalleryPhotos.get(position);
        Integer resourceId = galleryPhoto.getResId();
        String path = mMapPhotos.get(resourceId);
        String url;
        if (!TextUtils.isEmpty(path)) {
            url = "file://" + path;
        } else {
            url = "content://media/external/images/media/" + resourceId;
        }
        return url;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public GalleryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (0 == viewType && openCamera) {
            view = mInflater.inflate(R.layout.vw_gallery_camera_item, parent, false);
        } else {
            view = mInflater.inflate(R.layout.vw_gallery_item, parent, false);
        }
        view.setTag(viewType);
        return new GalleryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GalleryViewHolder holder, int position) {
        //1.设置宽高
        GridLayoutManager.LayoutParams params = new GridLayoutManager.LayoutParams(mItemSize, mItemSize);
        holder.view.setLayoutParams(params);

        //2.获取相应的资源进行显示
        if (0 == position && openCamera) {
            holder.view.setOnClickListener(v -> {
                if (mOnRecyclerItemClickListener != null) {
                    mOnRecyclerItemClickListener.onItemClick(position, null, holder.view);
                }
            });
        } else {
            int pos = openCamera ? position - 1 : position;
            String url = getUrl(pos);
            GalleryPhoto galleryPhoto = mGalleryPhotos.get(pos);

            holder.mImageView.setIsSelected(galleryPhoto.isSelected());
            ZjbImageLoader.create(url)
                    .setDisplayType(ZjbImageLoader.DISPLAY_FADE_IN)
                    .setFadeInTime(800)
                    .setQiniu(mItemSize, mItemSize)
                    .setDefaultDrawable(new ColorDrawable(0x00000000))
                    .into(holder.mImageView);

            holder.mImageView.setOnClickListener(v -> {
                Integer resourceId = galleryPhoto.getResId();
                String uri = "content://media/external/images/media/" + resourceId;
                if (galleryPhoto.isSelected()) {
                    mSelectPhotos.remove(uri);
                } else {
                    if (mSelectPhotos.size() >= 4) {
                        UIHelper.shortToast(R.string.the_large_num_is_four);
                        return;
                    }
                    mSelectPhotos.add(uri);
                }
                galleryPhoto.setIsSelected(!galleryPhoto.isSelected());
                holder.mImageView.setIsSelected(galleryPhoto.isSelected());
            });
        }
    }

    @Override
    public int getItemCount() {
        return mGalleryPhotos.size() + (openCamera ? 1 : 0);
    }


    public interface OnRecyclerItemClickListener {
        void onItemClick(int position, String url, View view);
    }

    public static class GalleryViewHolder extends RecyclerView.ViewHolder {
        @Optional
        @InjectView(R.id.iv)
        public OnPressImageView mImageView;

        public View view;

        public GalleryViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            ButterKnife.inject(this, itemView);
        }
    }
}
