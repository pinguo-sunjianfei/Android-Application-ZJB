package com.idrv.coach.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.idrv.coach.bean.Picture;
import com.idrv.coach.ui.fragment.PhotoReviewFragment;
import com.idrv.coach.utils.ValidateUtil;

/**
 * time:2016/4/25
 * description:图片查看适配器
 *
 * @author sunjianfei
 */
public class PhotoReviewPagerAdapter extends AbsFragmentPagerAdapter<Picture> {

    public PhotoReviewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return PhotoReviewFragment.newInstance(mData.get(position).getUrl());
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
