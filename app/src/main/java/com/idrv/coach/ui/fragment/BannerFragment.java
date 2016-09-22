package com.idrv.coach.ui.fragment;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.idrv.coach.R;
import com.idrv.coach.bean.Banner;
import com.idrv.coach.utils.PixelUtil;
import com.idrv.coach.utils.SchemeUtils;
import com.idrv.coach.utils.helper.ResHelper;
import com.idrv.coach.utils.helper.ViewUtils;
import com.zjb.loader.ZjbImageLoader;

/**
 * time:15-10-9
 * description:
 *
 * @author sunjianfei
 */
public class BannerFragment extends Fragment {
    private static final String RES_KEY = "res_key";
    Banner banner;

    public static BannerFragment newInstance(Banner banner) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(RES_KEY, banner);
        BannerFragment fragment = new BannerFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        banner = getArguments().getParcelable(RES_KEY);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.vw_banner, container, false);
        ImageView imageView = (ImageView) view;
        String url = banner.getImage();
        int height = (int) PixelUtil.dp2px(90);
        int width = ResHelper.getScreenWidth();
        ZjbImageLoader.create(url)
                .setDisplayType(ZjbImageLoader.DISPLAY_FADE_IN)
                .setQiniu(width, height)
                .setFadeInTime(1000)
                .setDefaultDrawable(new ColorDrawable(0xffe0dedc))
                .into(imageView);
        imageView.setOnClickListener(v -> {
            ViewUtils.setDelayedClickable(v, 500);
            SchemeUtils.schemeJump(v.getContext(), banner.getUrl());
        });
        return view;
    }

}
