package com.idrv.coach.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.idrv.coach.R;
import com.idrv.coach.data.constants.SPConstant;
import com.idrv.coach.ui.LoginActivity;
import com.idrv.coach.utils.PreferenceUtil;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * time:2016/3/8
 * description:
 *
 * @author sunjianfei
 */
public class GuideFragment extends BaseFragment {
    private static final String KEY_RES_ID = "key_res_id";
    private static final String KEY_LAST_PAGE = "key_last_page";
    @InjectView(R.id.guide_img)
    ImageView mImageView;
    @InjectView(R.id.guide_btn)
    Button mButton;

    int resId;
    boolean isLastPage;

    public GuideFragment() {

    }

    public static GuideFragment newInstance(int resId, boolean isLastPage) {
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_RES_ID, resId);
        bundle.putBoolean(KEY_LAST_PAGE, isLastPage);
        GuideFragment fragment = new GuideFragment();
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
        return inflater.inflate(R.layout.frag_guide, container, false);
    }

    @Override
    public void initView(View view) {
        ButterKnife.inject(this, view);
        mImageView.setImageResource(resId);
        mButton.setVisibility(isLastPage ? View.VISIBLE : View.GONE);
        mButton.setOnClickListener(v -> {
            //写入标志,标记已经非第一次使用
            PreferenceUtil.putBoolean(SPConstant.KEY_FIRST_USE, true);
            LoginActivity.launch(v.getContext());
            getActivity().finish();
        });
    }
}
