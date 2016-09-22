package com.idrv.coach.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import com.idrv.coach.R;
import com.idrv.coach.bean.AdvShareInfo;
import com.idrv.coach.bean.SpreadTool;
import com.idrv.coach.bean.WebParamBuilder;
import com.idrv.coach.bean.share.ShareWebProvider;
import com.idrv.coach.data.manager.UrlParserManager;
import com.idrv.coach.utils.BitmapUtil;
import com.idrv.coach.wxapi.WXEntryActivity;
import com.zjb.loader.ZjbImageLoader;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Subscription;

/**
 * time:2016/6/30
 * description:传播方案预览
 *
 * @author sunjianfei
 */
public class SpreadToolPreviewActivity extends AbsPayActivity {
    @InjectView(R.id.bottom_btn)
    TextView mBottomBtn;

    public static void launch(Context context, WebParamBuilder builder, SpreadTool tool) {
        Intent intent = new Intent(context, SpreadToolPreviewActivity.class);
        intent.putExtra(KEY_WEB_PARAM, builder);
        intent.putExtra(KEY_TOOL_PARAM, tool);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_spread_tool_preview);
        ButterKnife.inject(this);
        initView();
        loadShareIcon();
    }

    @Override
    public WebParamBuilder getParams() {
        return getIntent().getParcelableExtra(KEY_WEB_PARAM);
    }

    @Override
    protected void refreshUI() {
        setBottomViewStatus(mViewModel.getTool());
    }

    @Override
    protected void refresh() {
        Subscription subscription = mViewModel.getCommission()
                .subscribe(__ -> setBottomViewStatus(mViewModel.getTool()),
                        e -> showErrorView(), this::showContentView);
        addSubscription(subscription);
    }

    @Override
    protected int getToolType() {
        return TYPE_SPREAD_TOOL;
    }

    @Override
    protected void share() {
        SpreadTool tool = mViewModel.getTool();
        AdvShareInfo info = tool.getShareInfo();

        ShareWebProvider provider = new ShareWebProvider();
        String imagePath;
        String url = info.getShareImageUrl();

        if (!TextUtils.isEmpty(url) && url.startsWith("http://")) {
            imagePath = BitmapUtil.getImagePath(url);
        } else {
            imagePath = url;
        }
        provider.setTitle(info.getShareTitle());
        provider.setDesc(info.getShareContent());
        provider.setImagePath(imagePath);
        provider.setUrl(info.getShareUrl());
        WXEntryActivity.launch(this, provider, R.string.share_str, 0);
    }

    private void setBottomViewStatus(SpreadTool tool) {
        String payType = tool.getPayType();

        if (!TextUtils.isEmpty(payType)) {
            mBottomBtn.setText(R.string.share);
        } else {
            mBottomBtn.setText(R.string.immediately_use);
        }

        mBottomBtn.setOnClickListener(v -> {
            if (TextUtils.isEmpty(payType)) {
                showByDialog(tool);
            } else {
                share();
            }
        });
    }

    /**
     * 加载分享的icon
     */
    private void loadShareIcon() {
        SpreadTool tool = mViewModel.getTool();
        if (tool.isShare()) {
            AdvShareInfo shareInfo = tool.getShareInfo();
            String imageUrl = UrlParserManager.getInstance().parsePlaceholderUrl(shareInfo.getShareImageUrl());
            shareInfo.setShareImageUrl(imageUrl);
            if (null != shareInfo) {
                //加载分享的icon
                ZjbImageLoader.create(imageUrl)
                        .setDisplayType(ZjbImageLoader.DISPLAY_DEFAULT)
                        .load();
            }
        }
    }
}
