package com.idrv.coach.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.idrv.coach.R;
import com.idrv.coach.bean.Rank;
import com.idrv.coach.ui.MyWebSiteActivity;
import com.idrv.coach.utils.helper.ViewUtils;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * time:2016/6/24
 * description:
 *
 * @author sunjianfei
 */
public class RankHeaderLayout extends LinearLayout {
    @InjectView(R.id.rank_first)
    RankItemLayout mFirst;
    @InjectView(R.id.rank_second)
    RankItemLayout mSecond;
    @InjectView(R.id.rank_third)
    RankItemLayout mThird;
    @InjectView(R.id.rank_forth)
    RankItemLayout mForth;
    @InjectView(R.id.rank_fifth)
    RankItemLayout mFifth;
    @InjectView(R.id.rank_sixth)
    RankItemLayout mSixth;

    RankItemLayout[] mArrays;

    public RankHeaderLayout(Context context) {
        super(context);
        initView();
    }

    public RankHeaderLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public RankHeaderLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        inflate(getContext(), R.layout.vw_rank_header, this);
        ButterKnife.inject(this);
        mArrays = new RankItemLayout[6];
        mArrays[0] = mFirst;
        mArrays[1] = mSecond;
        mArrays[2] = mThird;
        mArrays[3] = mForth;
        mArrays[4] = mFifth;
        mArrays[5] = mSixth;
    }

    public void setData(List<Rank> ranks) {
        int size = ranks.size();
        int count = mArrays.length;
        //ranks的数量最大为6
        for (int i = 0; i < count; i++) {
            RankItemLayout layout = mArrays[i];
            if (size > i) {
                Rank rank = ranks.get(i);
                layout.setVisibility(VISIBLE);
                if (i >= 3) {
                    layout.setAvatar(rank.getHeadimgurl(), true);
                } else {
                    layout.setAvatar(rank.getHeadimgurl(), false);
                }
                layout.setRanking(i + 1);
                layout.setNickName(rank.getNickname());
                layout.setSchoolName(rank.getDrivingSchool());
                layout.setOnClickListener(v -> {
                    ViewUtils.setDelayedClickable(v, 500);
                    MyWebSiteActivity.launch(v.getContext(), rank);
                });
                layout.setTeachAge(rank.getCoachYears());
            } else {
                layout.setVisibility(GONE);
            }
        }
    }
}
