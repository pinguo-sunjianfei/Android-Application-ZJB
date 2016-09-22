package com.idrv.coach.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.idrv.coach.R;
import com.idrv.coach.bean.share.SharePictureProvider;
import com.idrv.coach.utils.helper.UIHelper;
import com.idrv.coach.utils.helper.ViewUtils;
import com.idrv.coach.wxapi.WXEntryActivity;
import com.zjb.loader.ZjbImageLoader;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * time:2016/4/5
 * description:
 *
 * @author sunjianfei
 */
public class InsReViewsActivity extends BaseActivity {
    private static final String KEY_IMAGE_PATH = "path";
    private static final String KEY_INS_TYPE = "type";
    public static final int TYPE_CAR_INS = 0x000;
    public static final int TYPE_DRIVING_INS = 0x001;

    @InjectView(R.id.image)
    ImageView mImageView;
    @InjectView(R.id.ins_tips_tv)
    TextView mInsTipsTv;
    @InjectView(R.id.share_to_student)
    TextView mShareBtn;

    String imageUrl;

    public static void launch(Context context, String path, int type) {
        Intent intent = new Intent(context, InsReViewsActivity.class);
        intent.putExtra(KEY_IMAGE_PATH, path);
        intent.putExtra(KEY_INS_TYPE, type);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_ins_reviews);
        ButterKnife.inject(this);
        initView();
    }

    private void initToolBar() {
        mToolbarLayout.setTitleTxt(R.string.ins_reviews_title);
    }

    private void initView() {
        initToolBar();
        Intent intent = getIntent();
        String imagePath = intent.getStringExtra(KEY_IMAGE_PATH);
        int type = intent.getIntExtra(KEY_INS_TYPE, -1);
        mInsTipsTv.setVisibility(type == TYPE_CAR_INS ? View.VISIBLE : View.GONE);
        ViewUtils.showImage(mImageView, imagePath);
        imageUrl = imagePath;
        mImageView.setOnClickListener(v -> PhotoActivity.launch(this, imagePath));
        mShareBtn.setText(type == TYPE_CAR_INS ? R.string.share_quote_to_student : R.string.share_ins_to_student);
    }

    @OnClick(R.id.share_to_student)
    public void onShare() {
        String mPhotoPath = ZjbImageLoader.getQiniuDiskCachePath(imageUrl, 0, 0);
        File file = new File(mPhotoPath);
        if (TextUtils.isEmpty(mPhotoPath) || !file.exists()) {
            UIHelper.shortToast(R.string.share_loading_picture);
        } else {
            SharePictureProvider provider = new SharePictureProvider(mPhotoPath, imageUrl);
            WXEntryActivity.launch(this, provider, R.string.share_str);
        }
    }
}
