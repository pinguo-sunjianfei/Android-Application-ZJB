package com.idrv.coach.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.idrv.coach.R;
import com.idrv.coach.utils.helper.ViewUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * time:2016/3/21
 * description:
 *
 * @author sunjianfei
 */
public class PhotoActivity extends BaseActivity {
    private static final String KEY_IMAGE_PATH = "path";
    @InjectView(R.id.image_view)
    PhotoView mImageView;

    public static void launch(Activity activity, String path) {
        Intent intent = new Intent(activity, PhotoActivity.class);
        intent.putExtra(KEY_IMAGE_PATH, path);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.img_scale_in, R.anim.img_scale_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_photo_view);
        ButterKnife.inject(this);
        initView();
    }

    @Override
    protected boolean isToolbarEnable() {
        return false;
    }

    private void initView() {
        Intent intent = getIntent();
        String imagePath = intent.getStringExtra(KEY_IMAGE_PATH);
        mImageView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                PhotoActivity.this.finish();
            }

            @Override
            public void onOutsidePhotoTap() {

            }
        });
        ViewUtils.showImage(mImageView, imagePath);
    }

    @OnClick(R.id.image_view)
    public void onImageClick() {
        finish();
    }
}
