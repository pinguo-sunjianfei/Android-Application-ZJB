package com.idrv.coach.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.idrv.coach.utils.ValidateUtil;

/**
 * time:2016/3/8
 * description:
 *
 * @author sunjianfei
 */
public class ViewPagerAdapter extends AbsFragmentPagerAdapter<Fragment> {

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return mData.get(position);
    }

    @Override
    public int getCount() {
        return ValidateUtil.isValidate(mData) ? mData.size() : 0;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
