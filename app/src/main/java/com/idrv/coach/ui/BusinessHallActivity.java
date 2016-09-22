package com.idrv.coach.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.idrv.coach.R;
import com.idrv.coach.bean.BusinessHallPage;
import com.idrv.coach.data.constants.SchemeConstant;
import com.idrv.coach.data.manager.PushCenterManager;
import com.idrv.coach.data.model.BusinessHallModel;
import com.idrv.coach.ui.adapter.BusinessHallAdapter;
import com.idrv.coach.ui.view.divider.ItemDivider;
import com.idrv.coach.ui.widget.RecyclerViewHeader;
import com.idrv.coach.utils.PixelUtil;
import com.idrv.coach.utils.ScrollUtils;
import com.idrv.coach.utils.helper.UIHelper;
import com.zjb.volley.utils.NetworkUtil;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import rx.Subscription;

/**
 * time:2016/3/14
 * description:
 *
 * @author sunjianfei
 */
public class BusinessHallActivity extends BaseActivity<BusinessHallModel> {
    public static final String KEY_FIRST_USE_BUSINESS = "first_use_business";

    @InjectView(R.id.business_recycler_view)
    RecyclerView mRecyclerView;
    @InjectView(R.id.title_bar_layout)
    View mTitleBarLayout;

    private TextView mSumNumTv;
    RecyclerViewHeader mRecyclerHeader;
    BusinessHallAdapter mAdapter;

    //列表滑动的距离
    private int mCurrentScroll;

    private float mHeaderViewHeight = PixelUtil.dp2px(225);

    public static void launch(Context context) {
        Intent intent = new Intent(context, BusinessHallActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_business_hall);
        ButterKnife.inject(this);
        initView();
        initViewModel();
    }

    @Override
    protected boolean isToolbarEnable() {
        return false;
    }

    @Override
    protected boolean hasBaseLayout() {
        return true;
    }

    @Override
    protected int getProgressBg() {
        return R.color.bg_main;
    }

    @Override
    public void onClickRetry() {
        if (NetworkUtil.isConnected(this)) {
            showProgressView();
            refresh();
        } else {
            UIHelper.shortToast(R.string.network_error);
        }
    }

    private void initView() {
        mAdapter = new BusinessHallAdapter();
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(null);
        mRecyclerView.addItemDecoration(new ItemDivider(this));
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerHeader = RecyclerViewHeader.fromXml(this, R.layout.vw_business_header);
        mRecyclerHeader.attachTo(mRecyclerView);
        mSumNumTv = (TextView) mRecyclerHeader.getHeaderView().findViewById(R.id.business_num);
        mRecyclerHeader.findViewById(R.id.header_layout).getBackground().setAlpha(120);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mCurrentScroll += dy;
                int baseColor = getResources().getColor(R.color.themes_main);
                float alpha = Math.min(1, (float) mCurrentScroll / mHeaderViewHeight);
                mTitleBarLayout.setBackgroundColor(ScrollUtils.getColorWithAlpha(alpha, baseColor));
            }
        });
        //清除小红点数据
        PushCenterManager.getInstance().updateRedPoint(SchemeConstant.TYPE_NEW_BUSINESS, true);
//        boolean isEntered = PreferenceUtil.getBoolean(KEY_FIRST_USE_BUSINESS);
//        if (!isEntered) {
//            showGuideView();
//        }
    }

    private void initViewModel() {
        mViewModel = new BusinessHallModel();
        refresh();
    }

    private void refresh() {
        Subscription subscription = mViewModel.getBusinessHall()
                .subscribe(this::onNext, this::onError, this::showContentView);
        addSubscription(subscription);
    }

    private void onNext(BusinessHallPage page) {
        mSumNumTv.setText(page.getTodayCount() + "");
        mAdapter.setData(page.getBoxes());
        mAdapter.notifyDataSetChanged();
    }

    private void onError(Throwable e) {
        showErrorView();
    }

//    private void showGuideView() {
//        View view = LayoutInflater.from(this).inflate(R.layout.vw_guide, null, false);
//        ViewPager mViewPager = (ViewPager) view.findViewById(R.id.guide_viewpager);
//        CircleIndicator mCircleIndicator = (CircleIndicator) view.findViewById(R.id.guide_indicator);
//
//
//        Fragment fragment1 = TeachGuideFragment.newInstance(R.drawable.business_1, false);
//        Fragment fragment2 = TeachGuideFragment.newInstance(R.drawable.business_2, false);
//        Fragment fragment3 = TeachGuideFragment.newInstance(R.drawable.business_3, false);
//        Fragment fragment4 = TeachGuideFragment.newInstance(R.drawable.business_4, true);
//
//        List<Fragment> fragments = new ArrayList<>(4);
//        fragments.add(fragment1);
//        fragments.add(fragment2);
//        fragments.add(fragment3);
//        fragments.add(fragment4);
//
//        ViewPagerAdapter mAdapter = new ViewPagerAdapter(getSupportFragmentManager());
//        mAdapter.setData(fragments);
//        mViewPager.setAdapter(mAdapter);
//        mCircleIndicator.setViewPager(mViewPager);
//        view.getBackground().setAlpha(130);
//
//        ViewGroup rootView = (ViewGroup) mRecyclerView.getRootView().findViewById(android.R.id.content);
//        RxBusManager.register(this, EventConstant.KEY_TEACH_GUIDE_CLOSE, String.class)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(__ -> {
//                    //写入标志,标记已经非第一次使用
//                    PreferenceUtil.putBoolean(KEY_FIRST_USE_BUSINESS, true);
//                    rootView.removeView(view);
//
//                }, Logger::e);
//        rootView.addView(view);
//    }

    @OnClick(R.id.back)
    public void onBackClick() {
        this.finish();
    }
}
