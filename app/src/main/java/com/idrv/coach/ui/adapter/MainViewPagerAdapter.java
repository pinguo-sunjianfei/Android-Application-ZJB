package com.idrv.coach.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * time: 15/7/27
 * description: 主页对应的适配器
 *
 * @author sunjianfei
 */
public class MainViewPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> mFragmentNames = new ArrayList<>();


    public MainViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }


    public void addFragment(Fragment fragment) {
        if (!mFragmentNames.contains(fragment)) {
            mFragmentNames.add(fragment);
        }
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentNames.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentNames.size();
    }
}
