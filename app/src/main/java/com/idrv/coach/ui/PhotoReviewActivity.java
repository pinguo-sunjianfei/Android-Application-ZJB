package com.idrv.coach.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.idrv.coach.R;
import com.idrv.coach.bean.Picture;
import com.idrv.coach.bean.event.EventConstant;
import com.idrv.coach.data.manager.RxBusManager;
import com.idrv.coach.ui.adapter.PhotoReviewPagerAdapter;
import com.idrv.coach.utils.helper.DialogHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * time:2016/4/25
 * description:照片浏览
 *
 * @author sunjianfei
 */
public class PhotoReviewActivity extends BaseActivity implements View.OnClickListener {
    private static final String KEY_PHOTOS = "photos";
    private static final String KEY_PAGE_NUM = "page_num";
    private static final String KEY_CAN_EDIT = "can_edit";

    @InjectView(R.id.photo_viewpager)
    ViewPager mViewPager;
    @InjectView(R.id.page_indicator)
    TextView mPageIndicatorTv;
    @InjectView(R.id.photo_delete)
    View mBtnDelete;

    PhotoReviewPagerAdapter mPagerAdapter;

    private int currentItemIndex;

    public static void launch(Context context, ArrayList<Picture> pictures, int pageNum, boolean canEdit) {
        Intent intent = new Intent(context, PhotoReviewActivity.class);
        intent.putParcelableArrayListExtra(KEY_PHOTOS, pictures);
        intent.putExtra(KEY_PAGE_NUM, pageNum);
        intent.putExtra(KEY_CAN_EDIT, canEdit);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_photo_review);
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

    @Override
    public int getStatusBarColor() {
        return getResources().getColor(R.color.black);
    }

    private void initView() {
        ArrayList<Picture> pictures = getIntent().getParcelableArrayListExtra(KEY_PHOTOS);
        boolean canEdit = getIntent().getBooleanExtra(KEY_CAN_EDIT, true);

        if (!canEdit) {
            mBtnDelete.setVisibility(View.GONE);
        }
        int pageNum = getIntent().getIntExtra(KEY_PAGE_NUM, 0);
        currentItemIndex = pageNum;
        mPagerAdapter = new PhotoReviewPagerAdapter(getSupportFragmentManager());
        mPagerAdapter.setData(pictures);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setCurrentItem(pageNum);

        mPageIndicatorTv.setText(pageNum + 1 + "/" + pictures.size());

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentItemIndex = position;
                mPageIndicatorTv.setText(position + 1 + "/" + mPagerAdapter.getCount());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @OnClick({R.id.photo_delete, R.id.back})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.photo_delete:
                photoDelete();
                break;
        }
    }

    private void photoDelete() {
        DialogHelper.create(DialogHelper.TYPE_NORMAL)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .title(getString(R.string.tip))
                .content(getString(R.string.sure_delete_photo))
                .leftButton(getString(R.string.cancel), getResources().getColor(R.color.themes_main))
                .rightButton(getString(R.string.confirm), getResources().getColor(R.color.black_54))
                .leftBtnClickListener((dialog, v) -> dialog.dismiss())
                .rightBtnClickListener((dialog, v) -> {
                    Picture picture = mPagerAdapter.getData().get(currentItemIndex);
                    if (!picture.isFake()) {
                        RxBusManager.post(EventConstant.KEY_PHOTO_DELETE, currentItemIndex);
                        if (mPagerAdapter.getCount() == 1) {
                            finish();
                        } else {
                            List<Picture> pictures = mPagerAdapter.getData();
                            pictures.remove(currentItemIndex);
                            mPagerAdapter = new PhotoReviewPagerAdapter(getSupportFragmentManager());
                            mPagerAdapter.setData(pictures);
                            mViewPager.setAdapter(mPagerAdapter);
                            if (mPagerAdapter.getCount() == 1 || currentItemIndex == 0) {
                                currentItemIndex = 0;
                                mPageIndicatorTv.setText(currentItemIndex + 1 + "/" + mPagerAdapter.getCount());
                            }
                            mViewPager.setCurrentItem(currentItemIndex);
                        }
                    }
                    dialog.dismiss();
                })
                .show();
    }
}
