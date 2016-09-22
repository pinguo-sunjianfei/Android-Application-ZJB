package com.idrv.coach.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.idrv.coach.R;
import com.idrv.coach.ui.adapter.ViewPagerAdapter;
import com.idrv.coach.ui.fragment.GuideFragment;
import com.idrv.coach.ui.widget.CircleIndicator;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * time:2016/3/8
 * description:引导页
 *
 * @author sunjianfei
 */
public class GuideActivity extends BaseActivity {
    @InjectView(R.id.guide_viewpager)
    ViewPager mViewPager;
    @InjectView(R.id.guide_indicator)
    CircleIndicator mCircleIndicator;

    public static void launch(Context context) {
        Intent intent = new Intent(context, GuideActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_guide);
        ButterKnife.inject(this);
        initView();
    }

    @Override
    protected boolean isToolbarEnable() {
        return false;
    }

    @Override
    public boolean isSwipeBackEnabled() {
        return false;
    }

    private void initView() {
        Fragment fragment1 = GuideFragment.newInstance(R.drawable.img_guide1, false);
        Fragment fragment2 = GuideFragment.newInstance(R.drawable.img_guide2, false);
        Fragment fragment3 = GuideFragment.newInstance(R.drawable.img_guide3, true);

        List<Fragment> fragments = new ArrayList<>(3);
        fragments.add(fragment1);
        fragments.add(fragment2);
        fragments.add(fragment3);

        ViewPagerAdapter mAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mAdapter.setData(fragments);
        mViewPager.setAdapter(mAdapter);
        mCircleIndicator.setViewPager(mViewPager);
    }
}
