package com.idrv.coach.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.idrv.coach.R;
import com.idrv.coach.bean.SpreadTool;
import com.idrv.coach.bean.share.SharePictureProvider;
import com.idrv.coach.utils.helper.UIHelper;
import com.idrv.coach.wxapi.WXEntryActivity;
import com.zjb.loader.ZjbImageLoader;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import uk.co.senab.photoview.PhotoView;

/**
 * time:2016/7/19
 * description:
 *
 * @author sunjianfei
 */
public class PosterShareActivity extends BaseActivity {
    private static final String KEY_IMAGE_PATH = "image_path";
    private static final String KEY_PARAM_TOOL = "param_tool";

    @InjectView(R.id.image_view)
    PhotoView mImageView;

    String imagePath;
    SpreadTool mTool;


    public static void launch(Context context, String imagePath, SpreadTool tool) {
        Intent intent = new Intent(context, PosterShareActivity.class);
        intent.putExtra(KEY_IMAGE_PATH, imagePath);
        intent.putExtra(KEY_PARAM_TOOL, tool);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_poster_share);
        ButterKnife.inject(this);
        initView();
    }

    private void initView() {
        Intent intent = getIntent();
        imagePath = intent.getStringExtra(KEY_IMAGE_PATH);
        mTool = intent.getParcelableExtra(KEY_PARAM_TOOL);

        //设置标题
        mToolbarLayout.setTitle(mTool.getTitle());

        ZjbImageLoader.create("file:///" + imagePath)
                .setBitmapConfig(Bitmap.Config.RGB_565)
                .setDisplayType(ZjbImageLoader.DISPLAY_DEFAULT)
                .setDefaultDrawable(getResources().getDrawable(R.drawable.photo_review_default))
                .into(mImageView);
    }

    @OnClick({R.id.btn_re_make, R.id.btn_share})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_re_make:
                MakePosterActivity.launch(this, mTool, true);
                finish();
                break;
            case R.id.btn_share:
                onShare(imagePath);
                break;
        }
    }

    private void onShare(String imagePath) {
        File file = new File(imagePath);
        if (TextUtils.isEmpty(imagePath) || !file.exists()) {
            UIHelper.shortToast(R.string.share_loading_picture);
        } else {
            SharePictureProvider provider = new SharePictureProvider(imagePath, null);
            WXEntryActivity.launch(this, provider, R.string.share_str);
        }
    }
}
