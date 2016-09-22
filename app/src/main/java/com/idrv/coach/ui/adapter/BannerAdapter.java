package com.idrv.coach.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.idrv.coach.bean.Banner;
import com.idrv.coach.ui.fragment.BannerFragment;
import com.idrv.coach.utils.ValidateUtil;

/**
 * time:2016/8/4
 * description:
 *
 * @author sunjianfei
 */
public class BannerAdapter extends AbsFragmentPagerAdapter<Banner> {

    public BannerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return BannerFragment.newInstance(mData.get(position % mData.size()));
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
