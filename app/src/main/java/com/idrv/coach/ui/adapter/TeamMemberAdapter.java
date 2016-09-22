package com.idrv.coach.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.idrv.coach.R;
import com.idrv.coach.bean.TeamMember;
import com.idrv.coach.utils.PixelUtil;
import com.idrv.coach.utils.helper.UIHelper;
import com.idrv.coach.utils.helper.ViewUtils;

/**
 * time: 2016/3/23
 * description:
 * <p>
 * 我的团队里面的网格布局
 *
 * @author bigflower
 */
public class TeamMemberAdapter extends AbsRecycleAdapter<TeamMember, TeamMemberAdapter.UserTaskHolder> {

    private OnAddClickListener mInterface;

    private static final int DP20 = (int) PixelUtil.dp2px(20);

    private Context context;
    private int width;
    private boolean invitable;

    public TeamMemberAdapter(Context context, OnAddClickListener listener) {
        this.context = context;
        this.mInterface = listener;
        initLayout();
    }

    private void initLayout() {
        width = UIHelper.getScreenSize(context)[0];
        width = (width - DP20 * 6) / 5;
    }

    @Override
    public UserTaskHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.vw_team_mumber, parent, false);
        return new UserTaskHolder(view);
    }

    @Override
    public void onBindViewHolder(UserTaskHolder holder, int position) {

        ViewGroup.LayoutParams lp = holder.imgLayout.getLayoutParams();
        TeamMember teamMember = mData.get(position);
        lp.width = width;
        lp.height = width;
        holder.imgLayout.setLayoutParams(lp);
        if (teamMember.getStatus() == 0) {
            holder.mJoinStatusTv.setVisibility(View.VISIBLE);
        } else {
            holder.mJoinStatusTv.setVisibility(View.GONE);
        }

        holder.mask.setVisibility(View.INVISIBLE);
        if (invitable && position == 0 && mInterface != null) {
            holder.mJoinStatusTv.setVisibility(View.GONE);
            holder.avatarIv.setOnClickListener(v -> {
                mInterface.addClick();
            });
        }
        holder.nameTv.setText(mData.get(position).getNickname());
        ViewUtils.showAvatar(holder.avatarIv, teamMember.getHeadimgurl(), width);
    }

    public class UserTaskHolder extends RecyclerView.ViewHolder {
        FrameLayout imgLayout;
        ImageView avatarIv, choosedIv;
        TextView nameTv;
        TextView mJoinStatusTv;
        View mask;

        public UserTaskHolder(View itemView) {
            super(itemView);
            imgLayout = (FrameLayout) itemView.findViewById(R.id.item_teamMember_imgLayout);
            avatarIv = (ImageView) itemView.findViewById(R.id.item_teamMember_avatarIV);
            choosedIv = (ImageView) itemView.findViewById(R.id.item_teamMember_choosedIV);
            nameTv = (TextView) itemView.findViewById(R.id.item_teamMember_nameTv);
            mask = itemView.findViewById(R.id.item_teamMember_mask);
            mJoinStatusTv = (TextView) itemView.findViewById(R.id.join_status_tv);
        }
    }

    public void setInvitable(boolean invitable) {
        this.invitable = invitable;
    }

    public interface OnAddClickListener {
        void addClick();
    }

}
