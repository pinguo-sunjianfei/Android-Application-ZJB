package com.idrv.coach.data.model;

import com.idrv.coach.R;
import com.idrv.coach.ZjbApplication;
import com.idrv.coach.bean.PurseDetails;
import com.idrv.coach.bean.WalletPage;
import com.idrv.coach.data.constants.ApiConstant;
import com.idrv.coach.utils.TimeUtil;
import com.idrv.coach.utils.ValidateUtil;
import com.zjb.volley.core.request.HttpGsonRequest;
import com.zjb.volley.core.request.Request;
import com.zjb.volley.core.request.RequestBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;

import static com.idrv.coach.data.pool.RequestPool.gRequestPool;

/**
 * time:2016/5/20
 * description:
 *
 * @author sunjianfei
 */
public class WalletModel {
    String sp;
    String[] months = ZjbApplication.gContext.getResources().getStringArray(R.array.months);
    List<PurseDetails> dataSource = new ArrayList<>();

    private List<PurseDetails> groupByMonth(List<PurseDetails> list) {
        Map<String, List<PurseDetails>> map = new HashMap<>();
        if (ValidateUtil.isValidate(list)) {
            for (PurseDetails details : list) {
                String time = details.getCreated();
                String groupName = TimeUtil.getMonth(time, months);

                List<PurseDetails> detailsList = map.get(groupName);
                if (null == detailsList) {
                    detailsList = new ArrayList<>();
                    map.put(groupName, detailsList);
                }
                detailsList.add(details);
            }

            List<PurseDetails> resultList = new ArrayList<>();

            //讲map按分组顺序转换成list
            for (Map.Entry<String, List<PurseDetails>> entry : map.entrySet()) {
                String key = entry.getKey();
                List<PurseDetails> values = entry.getValue();

                PurseDetails fakeData = new PurseDetails();
                fakeData.setIsGroup(true);
                fakeData.setGroupName(key);
                fakeData.setCreated(values.get(0).getCreated());

                resultList.add(fakeData);
                resultList.addAll(values);
            }

            Collections.sort(resultList, new Comparator<PurseDetails>() {
                @Override
                public int compare(PurseDetails lhs, PurseDetails rhs) {
                    return TimeUtil.compareDate(rhs.getCreated(), lhs.getCreated());
                }
            });
            return resultList;
        }
        return null;
    }

    public Observable<WalletPage> refresh(Action0 clearAdapter) {
        sp = "";
        //刷新,先清除数据
        dataSource.clear();
        return request()
                .doOnNext(__ -> clearAdapter.call());
    }

    public Observable<WalletPage> loadMore() {
        return request();
    }

    private Observable<WalletPage> request() {
        //1.创建Request
        HttpGsonRequest<WalletPage> mRefreshRequest = RequestBuilder.create(WalletPage.class)
                .requestMethod(Request.Method.POST)
                .url(ApiConstant.API_WALLET_DETAILS)
                .put("category", 0)
                .put("sp", sp)
                .put("count", 30)
                .build();
        //2.进行数据的处理
        return gRequestPool.request(mRefreshRequest)
                .filter((resp) -> null != resp && null != resp.data)
                .map(resp -> {
                    WalletPage page = resp.getData();
                    List<PurseDetails> list = page.getInAccounts();
                    if (ValidateUtil.isValidate(list)) {
                        dataSource.addAll(list);
                        sp = list.get(list.size() - 1).getCreated();
                    }
                    page.setInAccounts(groupByMonth(dataSource));
                    return page;
                })
                .observeOn(AndroidSchedulers.mainThread());
    }
}
