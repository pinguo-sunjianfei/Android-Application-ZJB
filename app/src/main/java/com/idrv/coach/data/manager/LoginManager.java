package com.idrv.coach.data.manager;

import android.text.TextUtils;

import com.google.gson.JsonSyntaxException;
import com.idrv.coach.ZjbApplication;
import com.idrv.coach.bean.Coach;
import com.idrv.coach.bean.User;
import com.idrv.coach.data.cache.ACache;
import com.idrv.coach.data.constants.SPConstant;
import com.idrv.coach.data.model.BaseModel;
import com.idrv.coach.utils.PreferenceUtil;
import com.zjb.volley.utils.GsonUtil;

/**
 * time: 15/7/20
 * description:封装用户登陆信息的管理器
 *
 * @author sunjianfei
 */
public class LoginManager extends BaseModel {

    private static final LoginManager gManager = new LoginManager();

    private User mLoginUser;
    private Coach mCoach;

    private LoginManager() {

    }

    public static LoginManager getInstance() {
        return gManager;
    }

    public void setLoginUser(User user) {
        mLoginUser = user;
    }

    public Coach getCoach() {
        if (null == mCoach) {
            mCoach = getCacheData();
        }
        return mCoach;
    }

    public void setCoach(Coach mCoach) {
        this.mCoach = mCoach;
    }

    public User getLoginUser() {
        if (mLoginUser == null) {
            isLoginValidate();
        }
        return mLoginUser;
    }

    public String getUid() {
        if (null != mLoginUser) {
            return mLoginUser.getUid();
        }
        return null;
    }

    public boolean isLoginValidate() {
        if (mLoginUser != null) {
            return true;
        }
        String user = PreferenceUtil.getString(SPConstant.KEY_USER);
        if (!TextUtils.isEmpty(user)) {
            try {
                User loginUser = GsonUtil.fromJson(user, User.class);
                if (null != loginUser) {
                    mLoginUser = loginUser;
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 是否不是首次使用
     *
     * @return
     */
    public boolean isNotFirstUse() {
        if (PreferenceUtil.getBoolean(SPConstant.KEY_FIRST_USE)) {
            return true;
        }
        return false;
    }

    /**
     * 判断user是否是当前用户
     */
    public boolean isOwner(User user) {
        if (user == null) {
            return false;
        }
        String userId = user.getUid();
        if (TextUtils.isEmpty(userId)) {
            return false;
        }
        User hostUser = mLoginUser;
        if (hostUser == null) {
            return false;
        }
        if (userId.equals(hostUser.getUid())) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取教练资料
     *
     * @return
     */
    private Coach getCacheData() {
        String json = PreferenceUtil.getString(SPConstant.KEY_COACH);
        Coach mCoach = null;
        try {
            if (!TextUtils.isEmpty(json)) {
                mCoach = GsonUtil.fromJson(json, Coach.class);
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return mCoach;
    }

    /**
     * 更新用户邀请码使用状态
     */
    public void updateInviteCode() {
        mLoginUser.setInviteCodeId(1);
        //重新保存数据
        PreferenceUtil.putString(SPConstant.KEY_USER, GsonUtil.toJson(mLoginUser));
    }

    public boolean isInvited() {
        return mLoginUser.getInviteCodeId() > 0;
    }

    public boolean isBindPhone() {
        return !TextUtils.isEmpty(mLoginUser.getPhone());
    }

    public void logout() {
        mLoginUser = null;
        mCoach = null;
        PreferenceUtil.remove(SPConstant.KEY_USER);
        PreferenceUtil.remove(SPConstant.KEY_COACH);
        ACache.get(ZjbApplication.gContext).clear();
    }
}
