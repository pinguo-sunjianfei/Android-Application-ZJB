package com.idrv.coach.data.model;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;

import com.idrv.coach.ZjbApplication;
import com.idrv.coach.bean.AdvBean;
import com.idrv.coach.bean.HomePage;
import com.idrv.coach.bean.Message;
import com.idrv.coach.bean.parser.AdsParser;
import com.idrv.coach.data.cache.ACache;
import com.idrv.coach.data.constants.ApiConstant;
import com.idrv.coach.data.constants.SPConstant;
import com.idrv.coach.utils.PreferenceUtil;
import com.idrv.coach.utils.ValidateUtil;
import com.zjb.loader.ZjbImageLoader;
import com.zjb.loader.core.listener.ImageLoadingAdapterListener;
import com.zjb.volley.core.request.HttpGsonRequest;
import com.zjb.volley.core.request.Request;
import com.zjb.volley.core.request.RequestBuilder;
import com.zjb.volley.core.response.HttpResponse;
import com.zjb.volley.utils.GsonUtil;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.idrv.coach.data.pool.RequestPool.gRequestPool;

/**
 * time:2016/8/1
 * description:
 *
 * @author sunjianfei
 */
public class HomePageModel {
    private static final String KEY_ADV_CACHE = "adv_cache";
    private static final String KEY_HOME_PAGE_CACHE = "home_page_cache";
    ACache mACache;
    AdvBean mAdvBean;
    HomePage mHomePage;
    //分页参数
    String sp;

    public HomePageModel() {
        mACache = ACache.get(ZjbApplication.gContext);
    }

    public HomePage getHomePage() {
        return mHomePage;
    }

    public AdvBean getAdvBean() {
        return mAdvBean;
    }

    public void updateData() {
        if (null != mHomePage) {
            mHomePage.shareIncrease();
        }
    }

    public int getSelfShareCus() {
        if (null != mHomePage) {
            return mHomePage.getShareCus();
        }
        return 0;
    }

    /**
     * 更新本地缓存
     *
     * @param message
     */
    public Observable<HomePage> updateMessageCache(Message message) {
        Observable<HomePage> observable = Observable.<HomePage>create(subscriber -> {
            try {
                HomePage homePage;
                String data = mACache.getAsString(KEY_HOME_PAGE_CACHE);
                if (TextUtils.isEmpty(data)) {
                    homePage = new HomePage();
                    List<Message> messages = new ArrayList<Message>();
                    messages.add(message);
                    homePage.setMessages(messages);
                } else {
                    homePage = GsonUtil.fromJson(data, HomePage.class);
                    List<Message> messages = homePage.getMessages();
                    if (!ValidateUtil.isValidate(messages)) {
                        messages = new ArrayList<>();
                    }
                    messages.add(messages.size(), message);
                    homePage.setMessages(messages);
                }
                this.mHomePage = homePage;
                //1.先删除原有的缓存
                mACache.remove(KEY_HOME_PAGE_CACHE);
                //2.重新存入新的数据
                mACache.put(KEY_HOME_PAGE_CACHE, GsonUtil.toJson(homePage));
                subscriber.onNext(mHomePage);
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        return observable;
    }

    /**
     * 获取广告缓存
     *
     * @return
     */
    public Observable<AdvBean> getAdvCache() {
        Observable<AdvBean> observable = Observable.<AdvBean>create(subscriber -> {
            try {
                String advStr = mACache.getAsString(KEY_ADV_CACHE);
                if (TextUtils.isEmpty(advStr)) {
                    subscriber.onError(new NullPointerException("no cache!"));
                } else {
                    AdvBean adv = GsonUtil.fromJson(advStr, AdvBean.class);
                    this.mAdvBean = adv;
                    subscriber.onNext(adv);
                    subscriber.onCompleted();
                }
            } catch (Exception e) {
                subscriber.onError(e);
            }
        }).subscribeOn(Schedulers.computation());
        return observable;
    }

    /**
     * 获取文件缓存的任务
     *
     * @return
     */
    public Observable<HomePage> getHomePageCache() {
        Observable<HomePage> observable = Observable.<HomePage>create(subscriber -> {
            try {
                String data = mACache.getAsString(KEY_HOME_PAGE_CACHE);
                if (TextUtils.isEmpty(data)) {
                    subscriber.onError(new NullPointerException("no cache!"));
                } else {
                    HomePage homePage = GsonUtil.fromJson(data, HomePage.class);
                    this.mHomePage = homePage;
                    List<Message> messages = mHomePage.getMessages();
//                    if (ValidateUtil.isValidate(messages)) {
//                        messages.addAll(0, getDefaultMessage());
//                    } else {
//                        mHomePage.setMessages(getDefaultMessage());
//                    }

                    subscriber.onNext(mHomePage);
                    subscriber.onCompleted();
                }
            } catch (Exception e) {
                subscriber.onError(e);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        return observable;
    }

    public Observable<HomePage> refresh() {
        return getHomePageData()
                .doOnNext(page -> {
                    //1.先删除原有的缓存
                    mACache.remove(KEY_HOME_PAGE_CACHE);
                    //2.重新存入新的数据
                    mACache.put(KEY_HOME_PAGE_CACHE, GsonUtil.toJson(page));
                });
//                .doOnNext(page -> {
//                    List<Message> messages = page.getMessages();
//                    if (ValidateUtil.isValidate(messages)) {
//                        messages.addAll(0, getDefaultMessage());
//                    } else {
//                        page.setMessages(getDefaultMessage());
//                    }
//                });
    }

    public Observable<HomePage> loadHistory() {
        return getHomePageData();
    }

    /**
     * 获取首页数据
     *
     * @return Observable<HomePage>
     */
    private Observable<HomePage> getHomePageData() {
        //1.创建Request
        HttpGsonRequest<HomePage> mRefreshRequest = RequestBuilder.create(HomePage.class)
                .requestMethod(Request.Method.POST)
                .url(ApiConstant.API_GET_HOME_PAGE_DATA)
                .put("sp", sp)
                .build();
        //2.进行数据的处理
        return gRequestPool.request(mRefreshRequest)
                .filter(resp -> null != resp && null != resp.data)
                .map(HttpResponse::getData)
                .doOnNext(page -> mHomePage = page)
                .doOnNext(data -> {
                    //1.记录分页SP
                    List<Message> messages = data.getMessages();
                    if (ValidateUtil.isValidate(messages)) {
                        sp = messages.get(0).getTime();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取弹窗广告数据
     *
     * @return Observable<HomePage>
     */
    public Observable<List<AdvBean>> getPopAdv() {
        //1.创建Request
        HttpGsonRequest<List<AdvBean>> mRefreshRequest = RequestBuilder.<List<AdvBean>>create()
                .requestMethod(Request.Method.POST)
                .parser(new AdsParser())
                .url(ApiConstant.API_POP_ADV)
                .put("type", "0")
                .build();
        //2.进行数据的处理
        return gRequestPool.request(mRefreshRequest)
                .filter(resp -> null != resp && null != resp.data)
                .map(HttpResponse::getData)
                .filter(ValidateUtil::isValidate)
                .doOnNext(list -> {
                    AdvBean advBean = list.get(0);
                    //存入缓存
                    mACache.put(KEY_ADV_CACHE, GsonUtil.toJson(advBean));
                    ZjbImageLoader.create(advBean.getImageUrl())
                            .setImageLoadinglistener(listener)
                            .load();
                });
    }

    ImageLoadingAdapterListener listener = new ImageLoadingAdapterListener() {
        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            PreferenceUtil.putString(SPConstant.KEY_ADV_PIC, imageUri);
        }
    };

//    private List<Message> getDefaultMessage() {
//        List<Message> messages = new ArrayList<>();
//
//
//        {
//            //消息1
//            Message message = new Message();
//            List<ChildView> childViews = new ArrayList<>();
//            message.setIcon("drawable://" + R.mipmap.ic_app);
//            message.setSource(ResHelper.getString(R.string.app_name));
//
//            ChildView childView1 = new ChildView();
//            childView1.setAreaId(2);
//            childView1.setColor("000000");
//            childView1.setAlign(0);
//            childView1.setText(ResHelper.getString(R.string.guide_text_title_1));
//
//            ChildView childView2 = new ChildView();
//            childView2.setAreaId(3);
//            childView2.setColor("D03B3B");
//            childView2.setAlign(1);
//            childView2.setText(ResHelper.getString(R.string.guide_text_title_2));
//
//            childViews.add(childView1);
//            childViews.add(childView2);
//            message.setComponents(childViews);
//            messages.add(message);
//        }
//
//        {
//            //消息2
//            Message message = new Message();
//            List<ChildView> childViews = new ArrayList<>();
//            message.setIcon("drawable://" + R.mipmap.ic_app);
//            message.setSource(ResHelper.getString(R.string.app_name));
//
//            ChildView childView1 = new ChildView();
//            childView1.setAreaId(2);
//            childView1.setColor("000000");
//            childView1.setAlign(1);
//            childView1.setText(ResHelper.getString(R.string.guide_text_title_3));
//
//            ChildView childView2 = new ChildView();
//            childView2.setAreaId(3);
//            childView2.setColor("D03B3B");
//            childView2.setAlign(1);
//            childView2.setText(ResHelper.getString(R.string.guide_text_title_4));
//
//            childViews.add(childView1);
//            childViews.add(childView2);
//            message.setComponents(childViews);
//            messages.add(message);
//        }
//        return messages;
//    }
}
