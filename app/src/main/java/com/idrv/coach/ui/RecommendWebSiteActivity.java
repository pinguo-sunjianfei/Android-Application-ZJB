package com.idrv.coach.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.idrv.coach.R;
import com.idrv.coach.bean.WebParamBuilder;
import com.idrv.coach.bean.WebSite;
import com.idrv.coach.bean.event.EventConstant;
import com.idrv.coach.data.manager.RxBusManager;
import com.idrv.coach.data.manager.UrlParserManager;
import com.idrv.coach.utils.PixelUtil;
import com.idrv.coach.utils.helper.UIHelper;
import com.idrv.coach.utils.helper.ViewUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * time:2016/6/13
 * description:
 *
 * @author sunjianfei
 */
public class RecommendWebSiteActivity extends ToolBoxWebActivity {
    private List<WebSite> webSites;
    private int mCurrentIndex = 0;

    public static void launch(Context context, List<WebSite> webSites) {
        Intent intent = new Intent(context, RecommendWebSiteActivity.class);
        intent.putParcelableArrayListExtra(KEY_WEB_PARAM, (ArrayList<? extends Parcelable>) webSites);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        webSites = getIntent().getParcelableArrayListExtra(KEY_WEB_PARAM);
        super.onCreate(savedInstanceState);
        View bottomView = LayoutInflater.from(this).inflate(R.layout.vw_recommend_website_bottom_layout, null, false);
        ViewGroup rootView = (ViewGroup) getWindow().getDecorView().findViewById(android.R.id.content);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) PixelUtil.dp2px(50));
        lp.gravity = Gravity.BOTTOM;
        rootView.addView(bottomView, lp);

        rootView.findViewById(R.id.btn_next).setOnClickListener(v -> {
            ViewUtils.setDelayedClickable(v, 500);
            mCurrentIndex++;
            if (mCurrentIndex > webSites.size() - 1) {
                UIHelper.shortToast(R.string.no_more);
            } else {
                mUrl = UrlParserManager.getInstance().parsePlaceholderUrl(buildParam().getUrl());
                mWebView.loadUrl(mUrl);
            }
        });

        rootView.findViewById(R.id.btn_reset).setOnClickListener(v -> {
            MyWebSiteActivity.launch(v.getContext());
            RxBusManager.post(EventConstant.KEY_CLOSE_SUCCESS_PAGE, "");
            this.finish();
        });
    }

    @Override
    public WebParamBuilder getParams() {
        return buildParam();
    }

    private WebParamBuilder buildParam() {
        WebSite webSite = webSites.get(mCurrentIndex);
        String url = MyWebSiteActivity.WEB_SITE_DEFAULT_URL;
        return WebParamBuilder
                .create()
                .setTitle(getString(R.string.title_recommend_website))
                .setUrl(url.replace("{uid}", webSite.getUid()));
    }
}
