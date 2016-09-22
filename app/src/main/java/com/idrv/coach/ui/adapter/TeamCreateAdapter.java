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
import com.idrv.coach.utils.CollectionUtil;
import com.idrv.coach.utils.PixelUtil;
import com.idrv.coach.utils.helper.UIHelper;
import com.idrv.coach.utils.helper.ViewUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * time: 2016/3/23
 * description:
 * <p>
 * 邀请成员里面的网格布局
 * <p>
 * 实现了一个接口，每当点击的时候，返回已经选中了的数量
 *
 * @author bigflower
 */
public class TeamCreateAdapter extends AbsRecycleAdapter<TeamMember, TeamCreateAdapter.UserTaskHolder> {

    private OnChooseListener mInterface;

    private static final int DP_20 = (int) PixelUtil.dp2px(20);

    private Context context;
    private int width;
    private List<String> choosedIds = new ArrayList<>();

    public TeamCreateAdapter(Context context, OnChooseListener listener) {
        this.context = context;
        this.mInterface = listener;
        initLayout();
    }

    private void initLayout() {
        width = UIHelper.getScreenSize(context)[0];
        width = (width - DP_20 * 6) / 5;
    }

    @Override
    public UserTaskHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.vw_team_mumber, parent, false);
        return new UserTaskHolder(view);
    }

    @Override
    public void onBindViewHolder(UserTaskHolder holder, int position) {

        ViewGroup.LayoutParams lp = holder.imgLayout.getLayoutParams();
        lp.width = width;
        lp.height = width;
        holder.imgLayout.setLayoutParams(lp);
        TeamMember teamMember = mData.get(position);

        if (teamMember.isSelected()) {
            holder.choosedIv.setVisibility(View.VISIBLE);
            holder.mask.setVisibility(View.INVISIBLE);
        } else {
            holder.choosedIv.setVisibility(View.INVISIBLE);
            holder.mask.setVisibility(View.VISIBLE);
        }

        holder.avatarIv.setOnClickListener(v -> {
            if (teamMember.isSelected()) { // 取消选择
                // 界面的变化
                holder.choosedIv.setVisibility(View.INVISIBLE);
                holder.mask.setVisibility(View.VISIBLE);
                // 将选择结果从List中移除
                choosedIds.remove(teamMember.getId());
                if (mInterface != null) {
                    mInterface.onChoose(choosedIds.size());
                }
                teamMember.setIsSelected(false);
            } else {  // 选择
                // 界面的变化
                holder.choosedIv.setVisibility(View.VISIBLE);
                holder.mask.setVisibility(View.INVISIBLE);
                // 将选择结果添加到List中
                choosedIds.add(mData.get(position).getId());
                if (mInterface != null) {
                    mInterface.onChoose(choosedIds.size());
                }
                teamMember.setIsSelected(true);
            }
        });
        holder.nameTv.setText(mData.get(position).getNickname());
        ViewUtils.showAvatar(holder.avatarIv, mData.get(position).getHeadimgurl(), width);
    }

    public class UserTaskHolder extends RecyclerView.ViewHolder {
        FrameLayout imgLayout;
        ImageView avatarIv, choosedIv;
        TextView nameTv;
        View mask;


        public UserTaskHolder(View itemView) {
            super(itemView);
            imgLayout = (FrameLayout) itemView.findViewById(R.id.item_teamMember_imgLayout);
            avatarIv = (ImageView) itemView.findViewById(R.id.item_teamMember_avatarIV);
            choosedIv = (ImageView) itemView.findViewById(R.id.item_teamMember_choosedIV);
            nameTv = (TextView) itemView.findViewById(R.id.item_teamMember_nameTv);
            mask = itemView.findViewById(R.id.item_teamMember_mask);
        }
    }

    public String getChoosedIds() {
        String str = CollectionUtil.join(",", choosedIds);
        if (str == null) {
            return "";
        } else {
            return str;
        }
    }

    public int getChoosedSize() {
        return choosedIds.size();
    }

    public interface OnChooseListener {
        void onChoose(int choosedNumber);
    }
}
