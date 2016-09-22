package com.idrv.coach.bean;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;

import com.idrv.coach.R;
import com.idrv.coach.ui.DiscoverFragment;
import com.idrv.coach.ui.HomeFragment;
import com.idrv.coach.ui.fragment.PersonalInfoFragment;


/**
 * time: 15/7/27
 * description: 封装了MainActivity界面的3个可滑动fragment
 *
 * @author sunjianfei
 */
public class MainFragment {
    private int mTitle;
    private int mIconResId;
    private Fragment mFragment;

    public MainFragment(@StringRes int titleRes, @DrawableRes int icon, Fragment fragment) {
        this.mTitle = titleRes;
        this.mIconResId = icon;
        this.mFragment = fragment;
    }

    public static MainFragment[] values() {
        MainFragment[] mainFragments = new MainFragment[3];
        mainFragments[0] = new MainFragment(R.string.home_page, R.drawable.tab_item_feed, new HomeFragment());
        mainFragments[1] = new MainFragment(R.string.discover_page, R.drawable.tab_item_discover, new DiscoverFragment());
        mainFragments[2] = new MainFragment(R.string.person_center, R.drawable.tab_item_me, new PersonalInfoFragment());
        return mainFragments;
    }

    public int getTitle() {
        return mTitle;
    }

    public void setTitle(int title) {
        this.mTitle = title;
    }

    public int getIconResId() {
        return mIconResId;
    }

    public void setIconResId(int iconResId) {
        this.mIconResId = iconResId;
    }

    public Fragment getFragment() {
        return mFragment;
    }

    public void setFragment(Fragment fragment) {
        this.mFragment = fragment;
    }
}
