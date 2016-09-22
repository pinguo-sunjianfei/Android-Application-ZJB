package com.idrv.coach.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import com.idrv.coach.R;
import com.idrv.coach.bean.event.EventConstant;
import com.idrv.coach.data.manager.RxBusManager;
import com.idrv.coach.utils.helper.ResHelper;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * time:2016/3/8
 * description:
 *
 * @author sunjianfei
 */
public class TeachGuideFragment extends BaseFragment {
    private static final String KEY_RES_ID = "key_res_id";
    private static final String KEY_LAST_PAGE = "key_last_page";

    @InjectView(R.id.guide_img)
    ImageView mImageView;
    @InjectView(R.id.guide_btn)
    Button mButton;
    @InjectView(R.id.guide_layout)
    View mGuideLayout;

    int resId;
    boolean isLastPage;

    public TeachGuideFragment() {

    }

    public static TeachGuideFragment newInstance(int resId, boolean isLastPage) {
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_RES_ID, resId);
        bundle.putBoolean(KEY_LAST_PAGE, isLastPage);
        TeachGuideFragment fragment = new TeachGuideFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        resId = bundle.getInt(KEY_RES_ID);
        isLastPage = bundle.getBoolean(KEY_LAST_PAGE);
    }

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_teach_guide, container, false);
    }

    @Override
    public void initView(View view) {
        ButterKnife.inject(this, view);
        ViewGroup.LayoutParams params = mImageView.getLayoutParams();
        params.width = ResHelper.getScreenWidth() * 5 / 6;
        params.height = ResHelper.getScreenHeight() * 5 / 8;

        mImageView.setImageResource(resId);
        mGuideLayout.getBackground().setAlpha(130);
        mButton.setVisibility(isLastPage ? View.VISIBLE : View.GONE);

        Animation alphaAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.splash_alpha_anim);
        if (isLastPage) {
            mButton.startAnimation(alphaAnimation);
        }
        mButton.setOnClickListener(v -> {
            RxBusManager.post(EventConstant.KEY_TEACH_GUIDE_CLOSE, "");
        });
    }
}
