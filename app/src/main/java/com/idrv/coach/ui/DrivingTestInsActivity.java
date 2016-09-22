package com.idrv.coach.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.idrv.coach.R;
import com.idrv.coach.bean.MainFragment;
import com.idrv.coach.bean.event.EventConstant;
import com.idrv.coach.data.manager.RxBusManager;
import com.idrv.coach.ui.adapter.MainViewPagerAdapter;
import com.idrv.coach.ui.adapter.TabSelectedAdapter;
import com.idrv.coach.ui.fragment.ApplyInsForDrivingTestFragment;
import com.idrv.coach.ui.fragment.DrivingTestInsDescFragment;
import com.idrv.coach.ui.fragment.DrivingTestInsListFragment;
import com.idrv.coach.utils.Logger;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.android.schedulers.AndroidSchedulers;

/**
 * time:2016/8/18
 * description:防爆险
 *
 * @author sunjianfei
 */
public class DrivingTestInsActivity extends BaseActivity {
    @InjectView(R.id.navigate_view_pager)
    ViewPager mViewPager;
    @InjectView(R.id.navigate_tab_layout)
    TabLayout mTabLayout;

    private TabLayout.OnTabSelectedListener mOnTabSelectedListener;
    private MainViewPagerAdapter mViewPagerAdapter;


    {
        this.mOnTabSelectedListener = new TabSelectedAdapter() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition(), true);
            }
        };
    }

    public static void launch(Context context) {
        Intent intent = new Intent(context, DrivingTestInsActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_car_ins);
        ButterKnife.inject(this);
        initView();
        registerEvent();
    }

    @Override
    protected boolean isToolbarEnable() {
        return false;
    }

    private void registerEvent() {
        RxBusManager.register(this, EventConstant.KEY_DRIVING_INS_COMMIT_SUCCESS, String.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> mViewPager.setCurrentItem(1), Logger::e);
    }

    private void initView() {
        //1.得到LayoutInflater
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        //2.得到要显示的数据
        MainFragment[] fragments = getFragments();
        //3.得到适配器
        mViewPagerAdapter = new MainViewPagerAdapter(getSupportFragmentManager());
        //4.遍历数据，进行显示
        for (int i = 0; i < fragments.length; i++) {
            TextView view = (TextView) inflater.inflate(R.layout.vw_tab_item, null, false);
            view.setText(fragments[i].getTitle());
            view.setCompoundDrawablesWithIntrinsicBounds(0, fragments[i].getIconResId(), 0, 0);
            mTabLayout.addTab(mTabLayout.newTab().setCustomView(view));
            mViewPagerAdapter.addFragment(fragments[i].getFragment());
        }
        //5.设置适配器
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.setOffscreenPageLimit(3);
        //6.viewpager与TabLayout联动
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.setOnTabSelectedListener(mOnTabSelectedListener);
    }

    private MainFragment[] getFragments() {
        MainFragment[] mainFragments = new MainFragment[3];
        mainFragments[0] = new MainFragment(R.string.ins_now, R.drawable.tab_item_ins_now, new ApplyInsForDrivingTestFragment());
        mainFragments[1] = new MainFragment(R.string.ins_history, R.drawable.tab_item_ins_history, new DrivingTestInsListFragment());
        mainFragments[2] = new MainFragment(R.string.ins_desc, R.drawable.tab_item_ins_detail, new DrivingTestInsDescFragment());
        return mainFragments;
    }
}
