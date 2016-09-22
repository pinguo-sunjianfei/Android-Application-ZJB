package com.idrv.coach.ui.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.idrv.coach.R;
import com.zjb.loader.ZjbImageLoader;

import butterknife.ButterKnife;
import butterknife.InjectView;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * time:2016/4/25
 * description:
 *
 * @author sunjianfei
 */
public class PhotoReviewFragment extends BaseFragment {
    private static final String KEY_IMAGE_URL = "url";

    @InjectView(R.id.image_view)
    PhotoView mImageView;

    public static PhotoReviewFragment newInstance(String url) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_IMAGE_URL, url);
        PhotoReviewFragment fragment = new PhotoReviewFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_photo_review, container, false);
    }

    @Override
    public void initView(View view) {
        ButterKnife.inject(this, view);
        Bundle bundle = getArguments();
        String url = bundle.getString(KEY_IMAGE_URL);

        mImageView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                getActivity().finish();
            }

            @Override
            public void onOutsidePhotoTap() {

            }
        });
        ZjbImageLoader.create(url)
                .setBitmapConfig(Bitmap.Config.RGB_565)
                .setDisplayType(ZjbImageLoader.DISPLAY_DEFAULT)
                .setDefaultDrawable(getResources().getDrawable(R.drawable.photo_review_default))
                .into(mImageView);
    }
}
