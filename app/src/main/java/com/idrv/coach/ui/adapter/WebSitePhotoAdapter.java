package com.idrv.coach.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.idrv.coach.R;
import com.idrv.coach.bean.Picture;
import com.idrv.coach.bean.WebSitePhoto;
import com.idrv.coach.ui.PhotoReviewActivity;
import com.idrv.coach.ui.PhotoWallActivity;
import com.idrv.coach.ui.view.ImageGridLayout;
import com.idrv.coach.utils.ValidateUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * time:2016/6/4
 * description:
 *
 * @author sunjianfei
 */
public class WebSitePhotoAdapter extends AbsListAdapter<WebSitePhoto> implements ImageGridLayout.OnGridItemClickListener {
    private static final int TYPE_UPLOAD = 0;
    private static final int TYPE_PHOTO = 1;
    private static final int TYPE_EMPTY = 2;

    List<Picture> resources;
    boolean isOwner;

    public void setResources(List<Picture> data) {
        this.resources = data;
    }

    public List<Picture> getResources() {
        return resources;
    }

    public void setOwner(boolean owner) {
        isOwner = owner;
    }

    @Override
    public int getCount() {
        return ValidateUtil.isValidate(mData) ? mData.size() + 1 : 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_UPLOAD;
        } else {
            WebSitePhoto webSitePhoto = mData.get(position - 1);
            return webSitePhoto.isFake() ? TYPE_EMPTY : TYPE_PHOTO;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        ViewHolder holder = null;
        EmptyHolder emptyHolder = null;

        if (null == convertView) {
            convertView = createViewByType(type, parent.getContext());
            if (type == TYPE_PHOTO) {
                holder = new ViewHolder();
                holder.mGridLayout = (ImageGridLayout) convertView.findViewById(R.id.grid_view);
                convertView.setTag(holder);
            } else if (type == TYPE_EMPTY) {
                emptyHolder = new EmptyHolder();

                emptyHolder.mEmptyView = (ImageView) convertView.findViewById(R.id.empty_iv);
                convertView.setTag(emptyHolder);
            }
        } else {
            if (type == TYPE_PHOTO) {
                holder = (ViewHolder) convertView.getTag();
            } else if (type == TYPE_EMPTY) {
                emptyHolder = (EmptyHolder) convertView.getTag();
            }
        }

        if (type == TYPE_PHOTO) {
            List<Picture> data = mData.get(position - 1).getPictures();
            holder.mGridLayout.setPosition(position - 1);
            holder.mGridLayout.setClickListener(this);
            holder.mGridLayout.setData(data);
        } else if (type == TYPE_UPLOAD) {
            convertView.findViewById(R.id.upload).setVisibility(isOwner ? View.VISIBLE : View.GONE);
            convertView.setVisibility(isOwner ? View.VISIBLE : View.GONE);
            convertView.setOnClickListener(v -> PhotoWallActivity.launch(v.getContext()));
        } else {
            emptyHolder.mEmptyView.setImageResource(R.drawable.photo_empty_bg);
        }
        return convertView;
    }

    private View createViewByType(int type, Context context) {
        View view;
        if (type == TYPE_UPLOAD) {
            view = LayoutInflater.from(context).inflate(R.layout.vw_upload_item, null, false);
        } else if (type == TYPE_PHOTO) {
            view = LayoutInflater.from(context).inflate(R.layout.vw_website_photo_item, null, false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.vw_website_empty_item, null, false);
        }
        return view;
    }

    @Override
    public void onImageClick(int position, View v) {
        PhotoReviewActivity.launch(v.getContext(), (ArrayList<Picture>) resources, position, isOwner);
    }

    class ViewHolder {
        ImageGridLayout mGridLayout;
    }

    class EmptyHolder {
        ImageView mEmptyView;
    }
}
