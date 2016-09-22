package com.idrv.coach.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.idrv.coach.R;
import com.idrv.coach.bean.Rank;
import com.idrv.coach.ui.MyWebSiteActivity;
import com.idrv.coach.ui.view.RankHeaderLayout;
import com.idrv.coach.ui.view.RankItemLayout;
import com.idrv.coach.utils.ValidateUtil;
import com.idrv.coach.utils.helper.ViewUtils;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * time:2016/6/23
 * description:
 *
 * @author sunjianfei
 */
public class CoachRankAdapter extends AbsRecycleAdapter<Rank, RecyclerView.ViewHolder> {
    List<Rank> headerList = null;
    private static final int TYPE_HEADER = 0x000;
    private static final int TYPE_DEFAULT = 0x001;


    public void setHeaderList(List<Rank> headerList) {
        this.headerList = headerList;
    }

    @Override
    public int getItemCount() {
        return getCount();
    }

    @Override
    public int getItemViewType(int position) {
        if (ValidateUtil.isValidate(headerList)) {
            return position == 0 ? TYPE_HEADER : TYPE_DEFAULT;
        }
        return TYPE_DEFAULT;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == TYPE_HEADER) {
            view = new RankHeaderLayout(parent.getContext());
            return new HeaderViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vw_rank_item, parent, false);
            return new DefaultViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int type = getItemViewType(position);
        if (type == TYPE_HEADER) {
            HeaderViewHolder viewHolder = (HeaderViewHolder) holder;
            viewHolder.mRankHeaderLayout.setData(headerList);
        } else {
            Rank rank = mData.get(position - 1);
            DefaultViewHolder viewHolder = (DefaultViewHolder) holder;
            viewHolder.mRankItemLayout.setAvatar(rank.getHeadimgurl(), true);
            viewHolder.mRankItemLayout.setNickName(rank.getNickname());
            viewHolder.mRankItemLayout.setSchoolName(rank.getDrivingSchool());
            viewHolder.mRankItemLayout.setRanking(6 + position);
            viewHolder.mRankItemLayout.setTeachAge(rank.getCoachYears());
            viewHolder.mRankItemLayout.setOnClickListener(v -> {
                ViewUtils.setDelayedClickable(v,500);
                MyWebSiteActivity.launch(v.getContext(), rank);
            });
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {
        RankHeaderLayout mRankHeaderLayout;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            mRankHeaderLayout = (RankHeaderLayout) itemView;
        }
    }

    class DefaultViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.rank_item)
        RankItemLayout mRankItemLayout;

        public DefaultViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

    private int getCount() {
        if (ValidateUtil.isValidate(mData)) {
            return mData.size() + 1;
        } else {
            if (ValidateUtil.isValidate(headerList)) {
                return 1;
            }
        }
        return 0;
    }
}
